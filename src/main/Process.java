package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Process implements Comparable<Object> {
	
	public int id;
	public int tempoDeChegada;
	public int tempoDeExecucao;
	public int prioridade;
	ArrayList<Integer> inOut;
	public int didExecute = 0;
	public int tempoRodando = 0; // tempo da fatia de tempo
	public int tempoRodandoTotal = 0;
	public int tempoDeInicioInOut;
	public int tempoDeEspera = 0;
	public int tempoDeResposta = -1;
	public Process(int tempoDeChegada , int tempoDeExecucao, int prioridade, int id) {
		this.id = id;
		this.tempoDeChegada = tempoDeChegada;
		this.tempoDeExecucao = tempoDeExecucao;
		this.prioridade = prioridade;
	}
	
	public Process(int tempoDeChegada , int tempoDeExecucao, int prioridade, int id, ArrayList<Integer> inOut) {
		this.id = id;
		this.tempoDeChegada = tempoDeChegada;
		this.tempoDeExecucao = tempoDeExecucao;
		this.prioridade = prioridade;
		this.inOut = inOut;
		Collections.sort(inOut);
	}
	
	public void run() {
		tempoDeExecucao--;
		tempoRodando++;
		tempoRodandoTotal++;
	}
	
	public void setTempoDeResposta(int currentTime) {
		this.tempoDeResposta = currentTime - this.tempoDeChegada ;
	}
	
	public boolean shouldInOut() {
		if(inOut == null) return false;
		for(int i  = 0; i <  inOut.size(); i++) {
			if(tempoRodandoTotal +1 == inOut.get(i)) {
				inOut.remove(i);
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(Object arg0) {
		int prioridade = ((Process) arg0).prioridade; 
		return this.prioridade - prioridade;
	}

	
	
}
