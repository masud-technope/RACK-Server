package experiment;

import java.util.ArrayList;
import java.util.HashMap;
import utility.ContentLoader;

public class ResultAnalyzer {

	String oracleFile;
	String resultFile;
	HashMap<Integer, ArrayList<String>> results;
	HashMap<Integer, ArrayList<String>> golddata;
	int K;
	boolean strictMatching = false;
	public HashMap<Integer, Double> precisionMap;
	public HashMap<Integer, Double> recallMap;
	public HashMap<Integer, Double> rrankMap;
	public HashMap<Integer, Double> accMap;

	public ResultAnalyzer(String oracleFile, String resultFile, int K,
			boolean strictMatching) {

		// variable initialization
		this.strictMatching = strictMatching;
		this.oracleFile = oracleFile;
		this.resultFile = resultFile;
		this.results = collectExperimentalResults();
		this.golddata = collectGoldsetAPIs();
		this.K = K;

		// latest addition
		this.precisionMap = new HashMap<>();
		this.recallMap = new HashMap<>();
		this.rrankMap = new HashMap<>();
		this.accMap = new HashMap<>();
	}

	protected String[] decomposeCamelCase(String token) {
		// decomposing camel case tokens using regex
		String camRegex = "([a-z])([A-Z]+)";
		String replacement = "$1/t$2";
		String filtered = token.replaceAll(camRegex, replacement);
		String[] ftokens = filtered.split("//s+");
		return ftokens;
	}

	protected HashMap<Integer, ArrayList<String>> collectGoldsetAPIs() {
		// collect goldset API classes
		ArrayList<String> fileLines = ContentLoader
				.getAllLinesOptList(this.oracleFile);
		int key = 0;
		HashMap<Integer, ArrayList<String>> tempMap = new HashMap<>();
		for (int i = 0; i < fileLines.size(); i += 2) {
			String goldAPI = fileLines.get(i + 1).trim();
			String[] apis = goldAPI.split("\\s+");
			ArrayList<String> apilist = new ArrayList<>();
			for (String api : apis) {
				apilist.add(api);
			}
			tempMap.put(++key, apilist);
		}
		return tempMap;
	}

	protected HashMap<Integer, ArrayList<String>> collectExperimentalResults() {
		// collecting experimental results
		ArrayList<String> fileLines = ContentLoader
				.getAllLinesOptList(this.resultFile);
		int key = 0;
		HashMap<Integer, ArrayList<String>> tempMap = new HashMap<>();
		for (int i = 0; i < fileLines.size(); i += 2) {
			String resultAPI = fileLines.get(i + 1).trim();
			if (resultAPI.isEmpty()) {
				tempMap.put(++key, new ArrayList<>());
			} else {
				String[] apis = resultAPI.split("\\s+");
				ArrayList<String> apilist = new ArrayList<>();
				for (String api : apis) {
					apilist.add(api);
				}
				tempMap.put(++key, apilist);
			}
		}
		return tempMap;
	}

	protected int isApiFound_K(ArrayList<String> rapis,
			ArrayList<String> gapis, int K) {
		// check if correct API is found
		K = rapis.size() < K ? rapis.size() : K;
		int found = 0;
		outer: for (int i = 0; i < K; i++) {
			String api = rapis.get(i);
			for (String gapi : gapis) {
				if (gapi.endsWith(api) || api.endsWith(gapi)) {
					found = 1;
					break outer;
				}
			}
		}
		return found;
	}

	protected boolean isApiFound(String api, ArrayList<String> gapis) {
		// check if the API can be found
		for (String gapi : gapis) {
			if (gapi.endsWith(api) || api.endsWith(gapi)) {
				return true;
			}
		}
		return false;
	}

	protected double getRRank(ArrayList<String> rapis, ArrayList<String> gapis,
			int K) {
		K = rapis.size() < K ? rapis.size() : K;
		double rrank = 0;
		for (int i = 0; i < K; i++) {
			String api = rapis.get(i);
			if (strictMatching) {
				if (isApiFound(api, gapis)) {
					rrank = 1.0 / (i + 1);
					break;
				}
			}
		}
		return rrank;
	}

	protected double getPrecisionK(ArrayList<String> rapis,
			ArrayList<String> gapis, int K) {
		// getting precision at K
		if (rapis.size() > 0)
			K = rapis.size() < K ? rapis.size() : K;
		double found = 0;
		for (int index = 0; index < K; index++) {
			String api = rapis.get(index);
			if (strictMatching) {
				if (isApiFound(api, gapis)) {
					found++;
				}
			}
		}
		return found / K;
	}

	protected double getAvgPrecisionK(ArrayList<String> rapis,
			ArrayList<String> gapis, int K) {
		double linePrec = 0;
		K = rapis.size() < K ? rapis.size() : K;
		double found = 0;
		for (int index = 0; index < K; index++) {
			String api = rapis.get(index);
			if (strictMatching) {
				if (isApiFound(api, gapis)) {
					found++;
					linePrec += (found / (index + 1));
				}
			}
		}
		if (found == 0)
			return 0;

		return linePrec / found;
	}

	protected double getRecallK(ArrayList<String> rapis,
			ArrayList<String> gapis, int K) {
		// getting recall at K
		K = rapis.size() < K ? rapis.size() : K;
		double found = 0;
		for (int index = 0; index < K; index++) {
			String api = rapis.get(index);
			if (strictMatching) {
				if (isApiFound(api, gapis)) {
					found++;
				}
			}
		}
		return found / gapis.size();
	}

	public void analyzeResults() {
		// analyze two results and compare performance
		try {
			double correct_sum = 0;
			double rrank_sum = 0;
			double precision_sum = 0;
			double preck_sum = 0;
			double recall_sum = 0;

			for (int key : this.golddata.keySet()) {
				try {
					ArrayList<String> rapis = this.results.get(key);
					ArrayList<String> gapis = this.golddata.get(key);

					if (rapis.isEmpty()) {
						// System.err.println(key);
					}

					correct_sum = correct_sum + isApiFound_K(rapis, gapis, K);

					rrank_sum = rrank_sum + getRRank(rapis, gapis, K);

					double prec = 0;
					prec = getPrecisionK(rapis, gapis, K);
					precision_sum = precision_sum + prec;

					double preck = 0;
					preck = getAvgPrecisionK(rapis, gapis, K);
					preck_sum = preck_sum + preck;

					double recall = 0;
					recall = getRecallK(rapis, gapis, K);
					recall_sum = recall_sum + recall;

					// storing the results
					this.precisionMap.put(key, preck);
					this.recallMap.put(key, recall);
					this.rrankMap.put(key, getRRank(rapis, gapis, K));
					this.accMap
							.put(key, (double) isApiFound_K(rapis, gapis, K));

				} catch (Exception exc) {
					// handle the exception
				}
			}

			// now showing the results
			System.out.println("Top-" + K + " Accuracy: " + correct_sum
					/ golddata.size());
			System.out.println("MRR@" + K + ": " + rrank_sum / golddata.size());
			System.out.println("MAP@" + K + ": " + preck_sum / golddata.size());
			System.out.println("MR@" + K + ": " + recall_sum / golddata.size());

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void main(String[] args) {
		boolean strictMatching = true;
		int K = 10;
		String EXP_HOME = "C:/My MSc/ThesisWorks/Crowdsource_Knowledge_Base/CodeTokenRec/Replication-package/EMSE2018-Dataset";
		String oracleFile = EXP_HOME + "/NL Queries & Oracle.txt";
		String resultFile = EXP_HOME + "/RACK-Suggested-API-Classes.txt";

		ResultAnalyzer analyzer = new ResultAnalyzer(oracleFile, resultFile, K,
				strictMatching);
		analyzer.analyzeResults();
	}
}
