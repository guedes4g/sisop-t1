package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 */
public class Processor {
	private List<Process> ready = new LinkedList<>();
	private List<Process> inOut = new ArrayList<>();
	private List<Process> done = new LinkedList<>();
	private Queue<String> outputRoundRobin = new LinkedList<String>();

	private Universe universe;
	private Process oldCurrent;

	private int currentTime = 0;
	
	public Processor(Universe universe) {
		this.universe = universe;
	}
	
	public void run() {
		boolean didCurrentRan = false;

		while (!hasFinishedExecution()) {
			//reset current validation
			didCurrentRan = false;

			//check for processes being inserted
			findProcessesToRun();

			//First validation: "are there any process running?"
			if ( ready.isEmpty() )
				outputRoundRobin.add("-");

			//Second validation: did the current process end his execution?
			else if( hasCurrentProcessDone() )
				//in case it did... end it and remove from the running jobs
				endCurrentProcess();

			//Third validation: check for time slice (sent in the input file)
			else if( shouldEndTimeSlice() ) {
				//reset the running time (since we will change context)
				ready.get(0).resetRunningTime();

				//give another priority process the opportunity to run
				rotate();
			}

			//4th validation: should we interrupt?
			//5th: check if we should stop to in/out to run
			else if ( shouldInterrupt() )
				//give another priority process the opportunity to run
				rotate();

			else if ( shouldCheckInOutCurrent() )
				checkInOutCurrent();

			//Or then, just execute the process
			else {
				//execute it
				runProcess();

				didCurrentRan = true;
			}
			
			addWaitTime(!didCurrentRan);

			//increment the running time
			currentTime++;
		}
	}

	public void printStats() {
		printRoundRobin();
		printAverageStats();
	}

	private boolean hasFinishedExecution() {
		return !universe.hasProcesses() && ready.isEmpty() && inOut.isEmpty();
	}

	private void runProcess() {
		//execute it
		ready.get(0).run(currentTime);

		//log
		outputRoundRobin.add(ready.get(0).toString());
	}

	private void findProcessesToRun() {
		List<Process> plist, returnInOut;

		//get by time
		plist = universe.getByTime(currentTime);

		//get by IO interruption
		returnInOut = endedInOut();

		//add them into running list
		ready.addAll(plist);
		ready.addAll(returnInOut);

		//Re-order the processes by their priority
		orderRunningPriority();
	}

	private boolean shouldEndTimeSlice() {
		return !ready.isEmpty() ? ready.get(0).getRunningTime() >= universe.getFatiaDeTempo() : false;
	}

	private void printRoundRobin() {
		for (String c : outputRoundRobin)
			System.out.print(c);

		System.out.println("");
	}
	
	private void printAverageStats() {
		int waitTime = 0;
		int answerTime = 0;

		for (Process p : done) {
			waitTime += p.getWaitTime();
			answerTime += p.getAnswerTime();
		}

		System.out.println("Média de resposta: " +((double) answerTime / done.size()));
		System.out.println("Média de espera: " + ((double) waitTime / done.size()));

	}

	private List<Process> endedInOut(){
		List<Process> res = new ArrayList<>();

		for (Process p : inOut)
			if (p.getInOutTime() + universe.InOutTime == currentTime)
				res.add(p);

		for (Process p : res)
			inOut.remove(p);

		return res;
	}
	
	private boolean shouldCheckInOutCurrent() {
		return !ready.isEmpty() && ready.get(0).shouldInOut();
	}

	private void checkInOutCurrent() {
		ready.get(0).setInOutTime(currentTime);
		ready.get(0).resetRunningTime();

		inOut.add(ready.get(0));

		ready.remove(0);

		outputRoundRobin.add("C");
	}

	private boolean hasCurrentProcessDone() {
		return !ready.isEmpty() && ready.get(0).hasEnded();
	}

	private void endCurrentProcess() {
		//Add into the done list
		done.add(ready.get(0));

		//remove from running list
		ready.remove(0);

		//and change the context
		if (!hasFinishedExecution())
			outputRoundRobin.add("C");
	}
	
	private void addWaitTime(boolean mustIncludeCurrent) {
		Process current = null;

		if (!ready.isEmpty())
			current = ready.get(0);

		//Increment the wait time for all the running jobs
		for (Process p : ready)
			//Check if the current process did not run
			if (p != current || mustIncludeCurrent == true)
				p.incrementWaitTime();

		//Increase wait time for the process waiting for IO
		for (Process p : inOut)
			p.incrementWaitTime();
	}

	private boolean shouldInterrupt() {
		if (ready.isEmpty())
			return false;

		return oldCurrent != ready.get(0);
	}
	
	private void rotate() {
		int gap = 0;

		if (!ready.isEmpty()) {
			gap = countProcessHavingMorePriority();

			//then means, we have more than one process to give the opportunity to run
			if (gap > 0) {
				Process current = ready.get(0);

				//remove the current from the queue
			 	ready.remove(current);

			 	//add into the new position (based on gap)
			 	ready.add(gap -1, current);
			}
		}

		//change context
		outputRoundRobin.add("C");
	}

	private int countProcessHavingMorePriority() {
		int res = 0;

		for (Process p : ready)
			if (ready.get(0).getPriority() >= p.getPriority())
				res++;

		return res;
	}
	
	private void orderRunningPriority() {
		if(!ready.isEmpty()) {
			oldCurrent = ready.get(0);
			Process.sortByPriority(ready);
		}
	}
	
}
