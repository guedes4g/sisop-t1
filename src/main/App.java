package main;

import java.io.IOException;

public class App {
	public static void main(String[] args) throws IOException {
		Universe universe = Reader.ReadFile("Exemple.txt");
		Processor processor = new Processor(universe);
		processor.start();
		//System.out.println("\n-------\n| END |\n-------");
		//System.exit(0);
	}
}
