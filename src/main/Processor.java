package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author guedes
 */
public class Processor {
	
	public Universe universe;
	
	LinkedList<Process> running;
	ArrayList<Process> inOut;
	LinkedList<Process> done;
	Process lastp = null;

	int currentTime;
	
	public Processor(Universe universe) {
		//System.out.println("---1C222C222C444C222C444C22C444C444C444C11C333C111C333C111C333C1C333C333C555C555C55");
		this.universe = universe;
		running = new LinkedList<>();
		done = new LinkedList<>();
		inOut = new ArrayList<>();
		currentTime = 0;
	}
	
	public void start() {
		for(;;) {
			ArrayList<Process> plist = universe.getByTime(currentTime);
			ArrayList<Process> returnInOut = endedInOut();
			boolean didFinish = checkDoneCurrent();
			boolean shouldInterrupt = addAndShouldInterrupt(plist); // preempo
			shouldInterrupt =  addAndShouldInterrupt(returnInOut) || shouldInterrupt ; // retorno de IO
			
			if(universe.listOfProcess.isEmpty() && running.isEmpty() && inOut.isEmpty()) break;


			boolean endTimeSlice = running.isEmpty() ? false : running.get(0).tempoRodando >= universe.fatiaDeTempo;
			orderRunning();
			int tempoDeEsperaOpt = 0;
			
			if (running.isEmpty()) {
				System.out.print("-");				
			} else if( didFinish ) {
				System.out.print( "C" );
			} else if( endTimeSlice ) {
				running.get(0).tempoRodando = 0;
				rotate();
				System.out.print( "C" );
			} else if ( shouldInterrupt ) {
				rotate();
				System.out.print( "C" );
			} else if( checkInOutRunningProcess() ) {
				System.out.print( "C" );
			} else {
				System.out.print(  running.get(0).id );
				running.get(0).run();
				tempoDeEsperaOpt = 1;
			}
			
			addTempoDeEspera(tempoDeEsperaOpt);
			currentTime++;
		} 
		
		calculateStats();
		
	}
	
	private void calculateStats() {
		int totalEspera = 0;
		int totalResposta = 0;
		try {
		for(int i = 0; i < done.size(); i++ ) {
			totalEspera += done.get(i).tempoDeEspera;
			totalResposta += done.get(i).tempoDeResposta;
		}
		} catch(Exception e ) {
			e.printStackTrace();
		}
		int mediaEspera = totalEspera/done.size();
		int mediaResposta = totalResposta/done.size();
		System.out.println("\nMedia de resposta:" + mediaResposta);
		System.out.println("\nMedia de espera:" + mediaEspera);

	}

	private ArrayList<Process> endedInOut(){
		ArrayList<Process> res = new ArrayList<>();
		for(int i = 0 ; i < inOut.size(); i++) {
			if(inOut.get(i).tempoDeInicioInOut + universe.inOutTime == currentTime) {
				res.add(inOut.remove(i));
			}
		}
		return res;
	}
	
	private boolean checkInOutRunningProcess() {
		if(running.isEmpty()) return false;				
		if(running.get(0).shouldInOut()) {
			Process p = running.remove(0);
			p.tempoDeInicioInOut = currentTime;
			p.tempoRodando = 0;
			inOut.add(p);
			return true;
		}
		return false;
	}

	private boolean checkDoneCurrent() {
		if(running.isEmpty()) return false;
		if(running.get(0).tempoDeExecucao <= 0) {
			lastp = running.remove(0);
			lastp.setTempoDeResposta(currentTime-1);
			done.add( lastp );
			return true;
		}
		return false;
	}
	
	private void addTempoDeEspera(int i) {
		for(; i< running.size(); i++) {
			running.get(i).tempoDeEspera++;
		}
		for(Process p : inOut) {
			p.tempoDeEspera++;
		}
	}

	public boolean addAndShouldInterrupt(List<Process> plist) {
		running.addAll(plist);
		if(running.isEmpty() ) { 
			return false;
		} 
		boolean should = false;
		for (int i = 0; i< plist.size(); i++) {
			if(running.get(0).prioridade > plist.get(i).prioridade) {
				should = true; 
				break;
			}
		}
		return should;
	}
	
	public void rotate() {
		lastp = null;
		if(!running.isEmpty()) {
			lastp = running.get(0);
			Process aux = running.remove(0);
			int i;
			for(i = 0; i < running.size(); i++) {
				if(aux.prioridade >= running.get(i).prioridade) continue;
				else {
					break;
				}
			}
			running.add( i , aux);
		} 
	}
	
	public void orderRunning() {
		if(running.isEmpty()) return;
	    Collections.sort(running, new Comparator<Process>() {
	        @Override public int compare(Process p1, Process p2) {
	            return p1.prioridade- p2.prioridade;
	        }
	    });
	}
	
	private void print(List<Process> p) {
		System.out.print("\n[");
		for(int i = 0 ; i < p.size(); i++) {
			System.out.print(p.get(i).id + " " );
		}
		System.out.println("]");
	}
	
	
}
