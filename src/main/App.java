package main;

import java.io.IOException;

public class App {
	public static void main(String[] args) throws IOException {
		Universe universe = Universe.readFile("Example.txt");
		Processor processor = new Processor(universe);

		processor.run();
	}
}
