package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author guedes
 */
public class Processor {
	private List<Process> running = new LinkedList<>();
	private List<Process> inOut = new ArrayList<>();
	private List<Process> done = new LinkedList<>();
	private Queue<String> outputRoundRobin = new LinkedList<String>();

	private Universe universe;
	private Process lastp = null;
	private int currentTime = 0;
	
	public Processor(Universe universe) {
		this.universe = universe;
	}
	
	public void run() {
		boolean didCurrentRan = false;
		List<Process> plist, returnInOut;
		boolean shouldInterrupt;

		while (true) {
			//reset current validation
			didCurrentRan = false;

			//check for processes being inserted
			plist = universe.getByTime(currentTime);
			returnInOut = endedInOut();

			//Validate if the process should be interreputed (either by "preempção" or by "IO"
			shouldInterrupt = addAndShouldInterrupt(plist);
			shouldInterrupt = addAndShouldInterrupt(returnInOut) || shouldInterrupt;

			//End the execution in case, there is no other process running
			if(!universe.hasProcesses() && running.isEmpty() && inOut.isEmpty())
				break;

			//Re-order the processes by their own priority
			orderRunningPriority();

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
				running.get(0).resetRunningTime();

				//give another priority process the opportunity to run
				rotate();
			}

			//4th validation: should we interrupt?
			else if (shouldInterrupt)
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

	private void runProcess() {
		//execute it
		running.get(0).run();

		//log
		outputRoundRobin.add(running.get(0).toString());
	}

	private boolean shouldEndTimeSlice() {
		return !running.isEmpty() ? running.get(0).getRunningTime() >= universe.getFatiaDeTempo() : false;
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
		Process p;

		if (!running.isEmpty() && running.get(0).shouldInOut()) {
			p = running.remove(0);
			p.setInOutTime(currentTime);
			p.resetRunningTime();

			inOut.add(p);

			checked = true;
		}

		return checked;
	}

	private boolean hasCurrentProcessDone() {
		return !running.isEmpty() && running.get(0).hasEnded();
	}

	private void endCurrentProcess() {
		//remove from running list
		lastp = running.remove(0);

		//set the end time
		lastp.calculateAnswerTime(currentTime -1);

		//Add into the done list
		done.add( lastp );

		//and change the context
		outputRoundRobin.add("C");
	}
	
	private void addWaitTime(boolean mustIncludeCurrent) {
		//Increment the wait time for all the running jobs
		for (int i = 0; i < running.size(); i++)
			//Check if the current process did not run
			if (i != 0 || mustIncludeCurrent == true)
				running.get(i).incrementWaitTime();

		//Increase wait time for the process waiting for IO
		for (Process p : inOut)
			p.incrementWaitTime();
	}

	private boolean addAndShouldInterrupt(List<Process> plist) {
		running.addAll(plist);

		if (running.isEmpty())
			return false;

		for (int i = 0; i < plist.size(); i++)
			if (running.get(0).getPriority() > plist.get(i).getPriority())
				return true;

		return false;
	}
	
	private void rotate() {
		lastp = null;

		if (!running.isEmpty()) {
			lastp = running.get(0);
			Process aux = running.remove(0);

			int i = 0;
			for (i = 0; i < running.size() ; i++) {
				if (aux.getPriority() >= running.get(i).getPriority())
					continue;
				else
					break;
			}

			running.add(i, aux);
		}

		//change context
		outputRoundRobin.add("C");
	}
	
	public void orderRunningPriority() {
		if(!running.isEmpty())
			Process.sortByPriority(running);
	}
	
}
