package main;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Process implements Comparable<Object> {
	List<Integer> inOut;

	public int id;
	public int tempoDeChegada;
	private int tempoDeExecucao;
	public int prioridade;
	private int tempoRodando = 0; // tempo da fatia de tempo
	public int tempoRodandoTotal = 0;
	public int tempoDeInicioInOut;
	private int tempoDeEspera = 0;
	public int tempoDeResposta = -1;
	
	public Process(int tempoDeChegada, int tempoDeExecucao, int prioridade, int id, List<Integer> inOut) {
		this.id = id;
		this.tempoDeChegada = tempoDeChegada;
		this.tempoDeExecucao = tempoDeExecucao;
		this.prioridade = prioridade;
		this.inOut = inOut;

		if (inOut != null)
			Collections.sort(inOut);
	}
	
	public void run() {
		tempoDeExecucao--;
		tempoRodando++;
		tempoRodandoTotal++;
	}

	public void resetRunningTime() {
		tempoRodando = 0;
	}

	public int getRunningTime() {
		return tempoRodando;
	}

	public boolean hasEnded() {
		return tempoDeExecucao <= 0;
	}
	
	public void calculateAnswerTime(int currentTime) {
		this.tempoDeResposta = currentTime - this.tempoDeChegada ;
	}

	public void incrementWaitTime() {
		tempoDeEspera++;
	}

	public int getWaitTime() {
		return tempoDeEspera;
	}

	public int getAnswerTime() {
		return tempoDeResposta;
	}
	
	public boolean shouldInOut() {
		if(inOut == null)
			return false;

		for(int i = 0; i < inOut.size(); i++) {
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

	@Override
	public String toString() {
		return id + "";
	}

	public static void sortByPriority(List<Process> running) {
		Collections.sort(running, new Comparator<Process>() {
			@Override
			public int compare(Process p1, Process p2) {
				return p1.prioridade - p2.prioridade;
			}
		});
	}
}
