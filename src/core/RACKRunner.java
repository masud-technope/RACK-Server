package core;

import java.util.ArrayList;
import java.util.HashMap;
import experiment.ExpDataCollector;
import experiment.ResultAnalyzer;

public class RACKRunner {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		HashMap<String, String> keyMap = new HashMap<>();

		for (int i = 0; i < args.length - 1; i += 2) {
			String key = args[i];
			String value = args[i + 1];
			keyMap.put(key, value);
		}

		if (keyMap.containsKey("-task")) {
			String task = keyMap.get("-task");

			String queryFile = "./sample-queries.txt";
			String query = "How to send email in Java?";
			String resultFile = "./sample-output.txt";
			String oracleFile = "./NL Queries & Oracle.txt";
			String sourceIndexFolder = "./IJDS-code-ext-index";

			int TOPK = 10;

			if (keyMap.containsKey("-K")) {
				TOPK = Integer.parseInt(keyMap.get("-K"));
			}

			switch (task) {
			case "suggestAPI":
				if (keyMap.containsKey("-query")) {
					query = keyMap.get("-query");
					System.out
							.println("Suggesting API classes. Please wait ...");
					CodeTokenProvider ctProvider = new CodeTokenProvider(query);
					ArrayList<String> apis = ctProvider
							.recommendRelevantAPIs("all");
					for (String api : apis) {
						System.out.println(api);
					}
				} else if (keyMap.containsKey("-queryFile")) {
					queryFile = keyMap.get("-queryFile");
					if (keyMap.containsKey("-resultFile")) {
						resultFile = keyMap.get("-resultFile");
						System.out
								.println("Suggesting API classes. Please wait ...");
						// now execute the query file
						ExpDataCollector expDC = new ExpDataCollector(
								queryFile, TOPK, resultFile);
						expDC.collectExpResults();
					} else {
						System.err.println("Please enter your result file.");
						return;
					}
				} else {
					System.err.println("Please enter your query file.");
					return;
				}
				break;
			case "evaluateAPISuggestion":
				if (keyMap.containsKey("-resultFile")) {
					resultFile = keyMap.get("-resultFile");
					ResultAnalyzer resultAnalyzer = new ResultAnalyzer(
							oracleFile, resultFile, TOPK, true);
					resultAnalyzer.analyzeResults();
				} else {
					System.err.println("Please enter the API result file.");
					return;
				}
				break;
			case "evaluateCodeSearch":
				if (keyMap.containsKey("-resultFile")) {
					resultFile = keyMap.get("-resultFile");
					
					
				} else {
					System.err.println("Please enter the API result file.");
					return;
				}
				break;
			case "evaluateQE":
				if (keyMap.containsKey("-resultFile")) {
					resultFile = keyMap.get("-resultFile");
				} else {
					System.err.println("Please enter the API result file.");
					return;
				}
				break;

			default:
				break;
			}
		}
	}
}
