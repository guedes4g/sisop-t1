package main;

import java.io.IOException;

public class App {
	public static void main(String[] args) throws IOException {
		//create the universe
		Universe universe = Universe.readFile("Example.txt");

		//instance the processor
		Processor processor = new Processor(universe);

		//Run and print results
		processor.run();
		processor.printStats();
	}
}
