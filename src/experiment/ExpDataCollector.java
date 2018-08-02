package experiment;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import config.StaticData;
import core.CodeTokenProvider;

public class ExpDataCollector {

	String queryFile;
	String outputFile;
	ArrayList<String> queries;
	ArrayList<String> results;
	int TOPK;

	@Deprecated
	public ExpDataCollector() {
		// variable initialization
		this.queryFile = StaticData.EVA_HOME + "/oracle-all-noun-verb.txt";
		this.queries = new ArrayList<>();
		this.results = new ArrayList<>();
	}

	public ExpDataCollector(String queryFile, int TOPK, String outFile) {
		this.queryFile = queryFile;
		this.outputFile = outFile;
		this.TOPK = TOPK;
		StaticData.MAXAPI = this.TOPK;
		this.queries = new ArrayList<>();
		this.results = new ArrayList<>();
	}

	protected void collectQueries() {
		// collect the code search queries
		try {
			Scanner scanner = new Scanner(new File(this.queryFile));
			while (scanner.hasNext()) {
				String query = scanner.nextLine();
				this.queries.add(query);
				// scanner.nextLine();
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

	public void collectExpResults() {
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
					// ArrayList<String> usedQueryTokens =
					// apiProvider.stemmedQuery;
					// String qline = convertAPI2String(usedQueryTokens);
					this.results.add(query);
					this.results.add(line);
					this.results.add("");// blank line
					System.out.println("Done: Query #" + (++count));
				} catch (Exception exc) {
					System.err
							.println("Failed to collect API names for: Query #"
									+ query);
				}
			}
			// now save the results
			saveResults(this.outputFile);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	protected void saveResults(String outFile) {
		// save the collected results
		try {
			FileWriter fwriter = new FileWriter(new File(outFile));
			for (String line : this.results) {
				fwriter.write(line + "\n");
			}
			fwriter.close();
			System.out.println("Collected API names saved successfully!");
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// ExpDataCollector collector = new ExpDataCollector();
		// collector.collectExpResults();
		String query = "How to send email in Java?";
		int TOPK=5;
		CodeTokenProvider ctProvider = new CodeTokenProvider(query);
		System.out.println(ctProvider.recommendRelevantAPIs());

	}
}
