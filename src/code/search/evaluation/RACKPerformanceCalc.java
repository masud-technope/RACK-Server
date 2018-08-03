package code.search.evaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import text.normalizer.TextNormalizer;
import utility.ContentLoader;
import utility.MiscUtility;
import config.StaticData;
import experiment.QueryFormatter;

public class RACKPerformanceCalc {

	String queryFile;
	String oracleFile;
	HashMap<Integer, String> rackQueryMap;
	HashMap<Integer, String> baseQueryMap;
	HashMap<Integer, String> goldAPIMap;
	String aqKey;
	String dsKey;

	public RACKPerformanceCalc(String rackQueryFile) {
		this.queryFile = StaticData.EVA_HOME + "/" + rackQueryFile;
		this.oracleFile = StaticData.EVA_HOME + "/oracle-175-ext.txt";
		this.rackQueryMap = new HashMap<>();
		this.baseQueryMap = new HashMap<>();
		this.goldAPIMap = new HashMap<>();
		this.loadBaselineQueryNGoldMap();
	}

	public RACKPerformanceCalc(String rackQueryFile, String aqKey) {
		this.aqKey = aqKey;
		this.queryFile = StaticData.EVA_HOME + "/" + rackQueryFile;
		this.oracleFile = StaticData.EVA_HOME + "/oracle-175-ext.txt";
		this.rackQueryMap = new HashMap<>();
		this.baseQueryMap = new HashMap<>();
		this.goldAPIMap = new HashMap<>();
		this.loadBaselineQueryNGoldMap();
	}

	protected String expandAPINames(String apiNames) {
		// expand API names
		return new TextNormalizer(apiNames).normalizeSimpleCodeDiscardSmall();
	}

	protected String getMatchedAPIs(String goldAPIs, String recommended) {
		ArrayList<String> golds = new ArrayList<String>(Arrays.asList(goldAPIs
				.split("\\s+")));
		ArrayList<String> recs = new ArrayList<String>(
				Arrays.asList(recommended.split("\\s+")));
		recs.retainAll(golds);
		return MiscUtility.list2Str(recs);
	}

	protected String getPartiallyMatched(String goldAPIs, String recommended) {
		// collecting partially matched API names
		ArrayList<String> golds = new ArrayList<String>(Arrays.asList(goldAPIs
				.split("\\s+")));
		ArrayList<String> recs = new ArrayList<String>(
				Arrays.asList(recommended.split("\\s+")));
		String temp = new String();
		for (String apiName : recs) {
			for (String goldName : golds) {
				if (apiName.contains(goldName) || goldName.contains(apiName)) {
					temp += goldName + "\t";
				}
			}
		}
		return temp;
	}

	protected void loadBaselineQueryNGoldMap() {
		// loading baseline queries
		ArrayList<String> lines = ContentLoader
				.getAllLinesOptList(this.oracleFile);
		// extracting the baseline queries
		for (int i = 0; i < lines.size(); i++) {
			if (i % 2 == 0) {
				int key = i / 2 + 1;
				if (!this.baseQueryMap.containsKey(key)) {
					this.baseQueryMap.put(key, lines.get(i).trim());
				}
				if (!this.goldAPIMap.containsKey(key)) {
					this.goldAPIMap.put(key, lines.get(i + 1).trim());
				}
			}
		}
	}

	protected void getRACKQueryMap() {
		// collect RACK queries first.
		ArrayList<String> lines = ContentLoader
				.getAllLinesOptList(this.queryFile);
		// extracting the baseline queries
		int matchedAPIcase = 0;
		for (int i = 0; i < lines.size(); i++) {
			if (i % 2 == 0) {
				int key = i / 2 + 1;
				if (!this.rackQueryMap.containsKey(key)) {
					String reformulated = new String();
					if (baseQueryMap.containsKey(key)) {
						String baseQuery = baseQueryMap.get(key);
						String nounverbs = extractNounVerbs(baseQuery).trim();
						if (!nounverbs.isEmpty()) {
							reformulated = nounverbs;
						} else {
							reformulated = baseQuery;
						}
					}
					String apiNames = lines.get(i + 1).trim();
					String goldAPIs = goldAPIMap.get(key);
					String matched = getMatchedAPIs(goldAPIs, apiNames);
					if (!matched.trim().isEmpty()) {
						matchedAPIcase++;
					} else {
						matched = getPartiallyMatched(goldAPIs, apiNames);
					}
					String expanded = expandAPINames(matched);
					// reformulated += "\t\t" + matched;
					// reformulated=expanded;

					switch (aqKey) {
					case "A":
						reformulated = expanded;
						// reformulated=apiNames;
						break;
					case "A+Q":
						reformulated += "\t" + expanded;
						// reformulated+="\t"+apiNames;
						break;
					case "QECK":
						reformulated = apiNames;
						break;
					}
					// reformulated += "\t\t" + expanded;
					// reformulated+="\t"+apiNames;
					// System.out.println(reformulated);
					this.rackQueryMap.put(key, reformulated);
				}
			}
		}
		System.out.println("Matched:" + matchedAPIcase);
	}

	protected void getRACKPerformance() {
		// calculate the RACK code search performance
		// now perform code search
		this.getRACKQueryMap();

		int[] indices = { 1, 3, 5, 10 };
		for (int K : indices) {
			// for (int K = 1; K <= 10; K++) {
			RACKCodeSearcher codeSearcher = null;
			switch (dsKey) {
			case "S":
				codeSearcher = new RACKCodeSearcher(rackQueryMap, K);
				break;
			case "L":
				codeSearcher = new RACKCodeSearcher(rackQueryMap, K, " ");
				break;
			case "XL":
				codeSearcher = new RACKCodeSearcher(rackQueryMap, K, true);
				break;
			default:
				break;
			}
			double acc = codeSearcher.performRACKCodeSearch();
			double mrr = codeSearcher.performRACKCodeSearchForMRR();
			System.out.println(K + "\t" + acc + "\t" + mrr);
		}
	}

	public HashMap<Integer, Integer> getRACKFirstRanks() {
		// get the RACK queries first
		this.getRACKQueryMap();
		int TOPK = 10;

		RACKCodeSearcher codeSearcher = null;

		switch (dsKey) {
		case "S":
			codeSearcher = new RACKCodeSearcher(rackQueryMap, TOPK);
			break;
		case "L":
			codeSearcher = new RACKCodeSearcher(rackQueryMap, TOPK, "");
			break;
		case "XL":
			codeSearcher = new RACKCodeSearcher(rackQueryMap, TOPK, true);
			break;
		default:
			break;
		}

		return codeSearcher.getFirstGoldRankMap();
	}

	protected void getRACKQEPerformance() {
		HashMap<Integer, Integer> toolRanks = getRACKFirstRanks();
		HashMap<Integer, Integer> baseRanks = new BaselineCodeSearcher(dsKey)
				.getBaselineRanks();

		// show the ranks
		showMyRanks(baseRanks, toolRanks);

		QueryRankComparer rqComparer = new QueryRankComparer(baseRanks,
				toolRanks);
		rqComparer.compareFirstGoldRanksNew();
	}

	protected static void showMyRanks(HashMap<Integer, Integer> baseRanks,
			HashMap<Integer, Integer> toolRanks) {
		for (int key : baseRanks.keySet()) {
			System.out.println(key + "\t" + (baseRanks.get(key) + 1) + "\t"
					+ (toolRanks.get(key) + 1));
		}
	}

	public static void main(String[] args) {
		
		String rackQueryFile = "GenQR-result.txt";
		// String rackQueryFile="result-iman-tse-June9-top10.txt";
		new RACKPerformanceCalc(rackQueryFile, "A+Q", "XL")
				.getRACKPerformance();
		// new RACKPerformanceCalc(rackQueryFile, "A+Q", "XL")
		// .getRACKQEPerformance();
		// showMyRanks(ranksMap);
	}
}
