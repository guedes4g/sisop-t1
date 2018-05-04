package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Reader {
	
//LINHA 1: n�mero de processos, 
//LINHA 2: tamanho de fatia de tempo, 
//Para cada processo:
//1 = tempo de chegada, 
//2 = tempo de execu��o, 
//3 = prioridade (1 at� 9 - prioridade 1 � a melhor) 
//3? =  tempos de acesso a opera��es de E/S.	
	public static Universe ReadFile(String fileURL) throws IOException {
		Universe universe;
		try(BufferedReader br = new BufferedReader(new FileReader(fileURL))) {
		    String line;
			int numeroDeProcesso = Integer.parseInt(br.readLine());
		    int fatiaDeTempo =  Integer.parseInt(br.readLine());

		    universe = new Universe(numeroDeProcesso, fatiaDeTempo);
		    int id = 0;
		    while ((line = br.readLine()) != null) {
		    	id++;
		    	String[] lineParts = line.split(" ");
		    	int tempoDeChegada = Integer.parseInt(lineParts[0]);
		    	int tempoDeExecucao = Integer.parseInt(lineParts[1]);
		    	int prioridade = Integer.parseInt(lineParts[2]);
		    	Process process;
		    	if(lineParts.length > 3) {
		    		ArrayList<Integer> inOut = new ArrayList<Integer>();
		    		for(int i = 3; i < lineParts.length; i++) {
		    			inOut.add(Integer.parseInt(lineParts[i]));
		    		}
		    		process = new Process(tempoDeChegada, tempoDeExecucao, prioridade, id, inOut);
		    	} else 
		    		process = new Process(tempoDeChegada, tempoDeExecucao, prioridade, id);
		    	
		    	universe.addProcess(process);
		    }
		}
	    return universe;
	}
}
