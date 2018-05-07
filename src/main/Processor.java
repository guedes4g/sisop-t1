package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 */
public class Processor {
	private List<Process> running = new LinkedList<>();
	private List<Process> inOut = new ArrayList<>();
	private List<Process> done = new LinkedList<>();
	private Queue<String> outputRoundRobin = new LinkedList<String>();

	private Universe universe;
	private Process current = null;

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
			if ( running.isEmpty() )
				outputRoundRobin.add("-");

			//Second validation: did the current process end his execution?
			else if( hasCurrentProcessDone() )
				//in case it did... end it and remove from the running jobs
				endCurrentProcess();

			//Third validation: check for time slice (sent in the input file)
			else if( shouldEndTimeSlice() ) {
				//reset the running time (since we will change context)
				current.resetRunningTime();

				//give another priority process the opportunity to run
				rotate();
			}

			//4th validation: should we interrupt?
			else if ( shouldInterrupt() )
				//give another priority process the opportunity to run
				rotate();

			//5th: check if we should stop to in/out to run
			else if( checkInOutRunningProcess() )
				outputRoundRobin.add("C");

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
		return !universe.hasProcesses() && running.isEmpty() && inOut.isEmpty();
	}

	private void runProcess() {
		//execute it
		current.run();

		//log
		outputRoundRobin.add(current.toString());
	}

	private void findProcessesToRun() {
		List<Process> plist, returnInOut;

		//get by time
		plist = universe.getByTime(currentTime);

		//get by IO interruption
		returnInOut = endedInOut();

		//add them into running list
		running.addAll(plist);
		running.addAll(returnInOut);

		//In case we just inserted a process, set it to current
		if (current == null && !running.isEmpty())
			current = running.get(0);

		//Re-order the processes by their priority
		orderRunningPriority();
	}

	private boolean shouldEndTimeSlice() {
		return current != null ? current.getRunningTime() >= universe.getFatiaDeTempo() : false;
	}

	private void printRoundRobin() {
		System.out.print("[");
		for (String c : outputRoundRobin)
			System.out.print(c);

		System.out.println("]");
	}
	
	private void printAverageStats() {
		int waitTime = 0;
		int answerTime = 0;

		for (Process p : done) {
			waitTime += p.getWaitTime();
			answerTime += p.getAnswerTime();
		}

		System.out.println("Média de resposta: " + (waitTime / done.size()));
		System.out.println("Média de espera: " + (answerTime / done.size()));

	}

	private List<Process> endedInOut(){
		List<Process> res = new ArrayList<>();

		for (int i = 0 ; i < inOut.size(); i++)
			if (inOut.get(i).getInOutTime() + universe.InOutTime == currentTime)
				res.add(inOut.remove(i));

		return res;
	}
	
	private boolean checkInOutRunningProcess() {
		boolean checked = false;

		if (current != null && current.shouldInOut()) {
			running.remove(0);

			current.setInOutTime(currentTime);
			current.resetRunningTime();

			inOut.add(current);

			checked = true;
		}

		return checked;
	}

	private boolean hasCurrentProcessDone() {
		return current != null && current.hasEnded();
	}

	private void endCurrentProcess() {
		//set the end time
		current.calculateAnswerTime(currentTime -1);

		//Add into the done list
		done.add(current);

		//remove from running list
		running.remove(0);

		//get a new current
		setNewCurrent();

		//and change the context
		if (!hasFinishedExecution())
			outputRoundRobin.add("C");
	}
	
	private void addWaitTime(boolean mustIncludeCurrent) {
		//Increment the wait time for all the running jobs
		for (Process p : running)
			//Check if the current process did not run
			if (p != current || mustIncludeCurrent == true)
				p.incrementWaitTime();

		//Increase wait time for the process waiting for IO
		for (Process p : inOut)
			p.incrementWaitTime();
	}

	private boolean shouldInterrupt() {
		if (running.isEmpty())
			return false;

		return current != running.get(0);
	}
	
	private void rotate() {
		int gap = 0;

		if (!running.isEmpty()) {
			gap = countProcessHavingMorePriority();

			//then means, we have more than one process to give the opportunity to run
			if (gap > 1) {
				//remove the current from the queue
			 	running.remove(current);

			 	//add into the new position (based on gap)
			 	running.add(gap -1, current);
			}

			//always refresh the current
			setNewCurrent();
		}

		//change context
		outputRoundRobin.add("C");
	}

	private int countProcessHavingMorePriority() {
		int res = 0;

		for (Process p : running)
			if (current.getPriority() >= p.getPriority())
				res++;

		return res;
	}

	private void setNewCurrent() {
		if (!running.isEmpty())
			current = running.get(0);
	}
	
	private void orderRunningPriority() {
		if(!running.isEmpty())
			Process.sortByPriority(running);
	}
	
}
