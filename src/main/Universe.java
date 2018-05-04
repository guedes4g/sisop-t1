package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

//Simulates
public class Universe {
	
	public int numeroDeProcesso;
	public int fatiaDeTempo;
	public ArrayList<Process> listOfProcess;
	
	public final int inOutTime = 4;
	
	public Universe(int numeroDeProcesso, int fatiaDeTempo) {
		this.numeroDeProcesso = numeroDeProcesso;
		this.fatiaDeTempo = fatiaDeTempo;
		this.listOfProcess = new ArrayList<>();
	}
	
	public void addProcess(Process p) {
		this.listOfProcess.add(p);
	}
	
	public void sortByArrival() {
	    Collections.sort(listOfProcess, new Comparator<Process>() {
	        @Override public int compare(Process p1, Process p2) {
	            return p1.tempoDeChegada- p2.tempoDeChegada;
	        }
	    });
	}
	
	public ArrayList<Process> getByTime(int t){
		ArrayList<Process> res = new ArrayList<Process>();
		for(int i = 0; i < listOfProcess.size(); i ++) {
			if(listOfProcess.get(i).tempoDeChegada == t) {
				res.add(listOfProcess.remove(i));
			}
		}
		return res;
	}
	
}
