package experiment;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import config.StaticData;
import core.CodeTokenProvider;

public class ExpDataCollector {

	String oracleFile;
	ArrayList<String> queries;
	ArrayList<String> results;

	public ExpDataCollector() {
		// variable initialization
		this.oracleFile = StaticData.EVA_HOME + "/oracle-all-noun-verb.txt";
		this.queries = new ArrayList<>();
		this.results = new ArrayList<>();
	}

	protected void collectQueries() {
		// collect the code search queries
		try {
			Scanner scanner = new Scanner(new File(this.oracleFile));
			while (scanner.hasNext()) {
				String query = scanner.nextLine();
				this.queries.add(query);
				scanner.nextLine();
			}
			scanner.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	protected String convertAPI2String(ArrayList<String> result) {
		// convert APIs to String
		String line = new String();
		for (String api : result) {
			line += api + "\t";
		}
		return line;
	}

	protected void collectExpResults() {
		// collecting experiment results
		try {
			this.collectQueries();
			int count = 0;
			for (String query : this.queries) {
				try {
					CodeTokenProvider apiProvider = new CodeTokenProvider(query);
					ArrayList<String> result = apiProvider
							.recommendRelevantAPIs();
					String line = convertAPI2String(result);

					ArrayList<String> usedQueryTokens = apiProvider.stemmedQuery;

					String qline = convertAPI2String(usedQueryTokens);

					this.results.add(qline);
					this.results.add(line);
					System.out.println("Collected for: " + (++count));
				} catch (Exception exc) {

				}
			}
			// now save the results
			saveResults();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	protected void saveResults() {
		// save the collected results
		String outFile = StaticData.EVA_HOME+ "/result-all-noun-verb-5-5.txt";

		try {
			FileWriter fwriter = new FileWriter(new File(outFile));
			for (String line : this.results) {
				fwriter.write(line + "\n");
			}
			fwriter.close();
			System.out.println("Results saved successfully!");
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ExpDataCollector collector = new ExpDataCollector();
		collector.collectExpResults();
	}
}
