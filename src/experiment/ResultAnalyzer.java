package experiment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import config.StaticData;

public class ResultAnalyzer {

	String oracleFile;
	String resultFile;
	HashMap<Integer, ArrayList<String>> results;
	HashMap<Integer, ArrayList<String>> golddata;
	int K;

	public ResultAnalyzer(int K) {
		// variable initialization
		this.oracleFile = StaticData.EVA_HOME + "/oracle-all-noun-verb.txt";
		this.resultFile = StaticData.EVA_HOME
				+ "/result-all-noun-verb.txt";
		this.results = new HashMap<>();
		this.golddata = new HashMap<>();
		this.collectGoldAPIs();
		this.collectExpResult();
		this.K = K;
	}

	protected void collectGoldAPIs() {
		// collect gold APIs
		try {
			Scanner scanner = new Scanner(new File(this.oracleFile));
			int key = 0;
			while (scanner.hasNext()) {
				scanner.nextLine();
				String goldAPI = scanner.nextLine().trim();
				String[] apis = goldAPI.split("\\s+");
				ArrayList<String> apilist = new ArrayList<>(Arrays.asList(apis));
				this.golddata.put(++key, apilist);
			}
			scanner.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	protected void collectExpResult() {
		// collecting experimental results
		try {
			Scanner scanner = new Scanner(new File(this.resultFile));
			int key = 0;
			int qwordcount = 0;
			while (scanner.hasNext()) {
				String qwords = scanner.nextLine();
				qwordcount += qwords.split("\\s+").length;
				String resultAPI = scanner.nextLine().trim();
				if(resultAPI.isEmpty()){
					this.results.put(++key, new ArrayList<String>());
					continue;
				}
				String[] apis = resultAPI.split("\\s+");
				ArrayList<String> apilist = new ArrayList<>(Arrays.asList(apis));
				this.results.put(++key, apilist);
			}
			scanner.close();

			System.out.println("Avg. words per query:" + (double) qwordcount
					/ this.results.size());

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	protected int isApiFoundK(ArrayList<String> rapis, ArrayList<String> gapis,
			int K) {
		// check if correct API is found
		K = rapis.size() < K ? rapis.size() : K;
		int found = 0;
		outer: for (int i = 0; i < K; i++) {
			String api = rapis.get(i);
			for (String gapi : gapis) {
				// if (gapi.contains(api) || api.contains(gapi)) {
				if (gapi.endsWith(api) || api.endsWith(gapi)) {
				//if(gapi.equals(api)){
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
				//if(gapi.equals(api)){
				// if (gapi.contains(api) || api.contains(gapi)) {
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
			if (isApiFound(api, gapis)) {
				rrank = 1 / (i + 1);
				break;
			}
		}
		return rrank;
	}

	protected double getPrecisionK(ArrayList<String> rapis,
			ArrayList<String> gapis, int K) {
		// getting precision at K
		if(rapis.size()>0)
		K = rapis.size() < K ? rapis.size() : K;
		double found = 0;
		for (int index = 0; index < K; index++) {
			String api = rapis.get(index);
			if (isApiFound(api, gapis)) {
				found++;
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
			if (isApiFound(api, gapis)) {
				found++;
				linePrec += (found / (index + 1));
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
			if (isApiFound(api, gapis)) {
				found++;
			}
		}
		return found / gapis.size();
	}

	protected void analyzeResults() {
		// analyze two results and compare performance
		try {
			int correct_sum = 0;
			double rrank_sum = 0;
			double precision_sum = 0;
			double preck_sum = 0;
			double recall_sum = 0;
			double fmeasure_sum = 0;

			System.out.println("Results collected for:"+ this.results.keySet().size());

			int noresult=0;
			
			for (int key : this.golddata.keySet()) {
				try {
					
					ArrayList<String> rapis = this.results.get(key);
					ArrayList<String> gapis = this.golddata.get(key);

					if(rapis.isEmpty())noresult++;

					//System.out.println(key);
					
					correct_sum = correct_sum + isApiFoundK(rapis, gapis, K);
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

					if (preck + recall > 0)
						fmeasure_sum = fmeasure_sum
								+ ((2 * preck * recall) / (preck + recall));
				} catch (Exception exc) {
				}
			}

			
			System.out.println("No results:"+noresult);
			
			// now showing the average results
			System.out.println("Top-" + K + " accuracy: "
					+ ((double) correct_sum / this.results.keySet().size())
					* 100);

			System.out.println("Mean Receiprocal Rank: " + rrank_sum
					/ this.results.keySet().size());

			System.out.println("Mean Precision: " + 100 * precision_sum
					/ this.results.keySet().size());

			System.out.println("Mean Precision @" + K + ": " + 100 * preck_sum
					/ this.results.keySet().size());

			System.out.println("Mean Recall: " + 100 * recall_sum
					/ this.results.keySet().size());

			System.out.println("Mean F-measure: " + 100 * fmeasure_sum
					/ this.results.keySet().size());

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void main(String[] args) {
		int K = 10;
		ResultAnalyzer analyzer = new ResultAnalyzer(K);
		analyzer.analyzeResults();
	}
}
