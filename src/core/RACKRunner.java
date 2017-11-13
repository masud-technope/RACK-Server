package core;

import java.io.File;
import experiment.ExpDataCollector;

public class RACKRunner {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String queryFile = "./sample-queries.txt";
		String outputFile = "./suggested-api-names.txt";
		int TOPK = 10;

		for (int i = 0; i < args.length - 1; i += 2) {
			try {
				switch (args[i]) {
				case "-queryfile":
					queryFile = args[i + 1];
					break;
				case "-outputfile":
					outputFile = args[i + 1];
					break;
				case "-K":
					TOPK = Integer.parseInt(args[i + 1]);
					break;
				default:
					break;
				}
			} catch (Exception exc) {
				System.err
						.println("Please try again. Failed to parse the argument: "
								+ args[i]);
			}
		}
		File qFile = new File(queryFile);
		if (qFile.isFile()) {
			ExpDataCollector expDC = new ExpDataCollector(queryFile, TOPK,
					outputFile);
			expDC.collectExpResults();
		} else {
			System.err
					.println("Please enter a file containing NL queries. Each query should be in one line.");
		}
	}
}
