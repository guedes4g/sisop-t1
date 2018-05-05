package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//Simulates
public class Universe {
	public final int InOutTime = 4;

	private int fatiaDeTempo;
	private List<Process> listOfProcess = new ArrayList<>();
	
	public Universe(int fatiaDeTempo) {
		this.fatiaDeTempo = fatiaDeTempo;
	}
	
	public void addProcess(Process p) {
		this.listOfProcess.add(p);
	}

	public List<Process> getProcesses() {
		return listOfProcess;
	}

	public boolean hasProcesses() {
		return !listOfProcess.isEmpty();
	}

	public int getFatiaDeTempo() {
		return fatiaDeTempo;
	}
	
	public ArrayList<Process> getByTime(int t){
		ArrayList<Process> res = new ArrayList<Process>();

		for(int i = 0; i < listOfProcess.size(); i ++)
			if(listOfProcess.get(i).getArrivalTime() == t)
				res.add(listOfProcess.remove(i));

		return res;
	}

	public static Universe readFile(String fileURL) throws IOException {
		Universe universe;
		int id = 1;
		String line;
		List<Integer> inOut = null;

		try(BufferedReader br = new BufferedReader(new FileReader(fileURL))) {
			//Reseta lista de inOut
			inOut = null;

			//LINHA 1: número de processos (ignorar)
			int numeroDeProcesso = Integer.parseInt(br.readLine());

			//LINHA 2: tamanho de fatia de tempo,
			int fatiaDeTempo =  Integer.parseInt(br.readLine());

			//cria o universo a partir dos dados coletados do arquivo
			universe = new Universe(fatiaDeTempo);

			//lê as linhas restantes
			//Para cada processo:
			for (int j = 0; j < numeroDeProcesso; j++) {
				//read the line
				line = br.readLine();

				//split (similar to token)
				String[] lineParts = line.split(" ");

				//1 = tempo de chegada,
				int tempoDeChegada = Integer.parseInt(lineParts[0]);

				//2 = tempo de execução,
				int tempoDeExecucao = Integer.parseInt(lineParts[1]);

				//3 = prioridade (1 até 9 - prioridade 1 é o melhor)
				int prioridade = Integer.parseInt(lineParts[2]);

				//3? =  tempos de acesso a operações de E/S.
				if (lineParts.length > 3) {
					inOut = new ArrayList<Integer>();

					for (int i = 3; i < lineParts.length; i++)
						inOut.add(Integer.parseInt(lineParts[i]));
				}

				universe.addProcess(
					new Process(tempoDeChegada, tempoDeExecucao, prioridade, id++, inOut)
				);
			}
		}

		return universe;
	}
}
