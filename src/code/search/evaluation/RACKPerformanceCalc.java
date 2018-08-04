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
	String indexFolder;
	String aqKey;
	String dsKey;
	int TOPK = 10;

	public RACKPerformanceCalc(String rackQueryFile, int TOPK) {
		this.queryFile = rackQueryFile;
		this.oracleFile = StaticData.ORACLE_FILE;
		this.indexFolder = StaticData.INDEX_FOLDER;
		this.rackQueryMap = new HashMap<>();
		this.baseQueryMap = new HashMap<>();
		this.goldAPIMap = new HashMap<>();
		this.loadBaselineQueryNGoldMap();
		this.rackQueryMap = getRACKQueryMap();
		this.TOPK = TOPK;
	}

	public RACKPerformanceCalc(String rackQueryFile) {
		this.queryFile = rackQueryFile;
		this.oracleFile = StaticData.ORACLE_FILE;
		this.indexFolder = StaticData.INDEX_FOLDER;
		this.rackQueryMap = new HashMap<>();
		this.baseQueryMap = new HashMap<>();
		this.goldAPIMap = new HashMap<>();
		this.loadBaselineQueryNGoldMap();
		this.rackQueryMap = getRACKQueryMap();
	}

	protected String extractNounVerbs(String squery) {
		return new QueryFormatter().tagSingleQuery(squery);
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
					temp += goldName + " ";
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

	protected String prepareNLQuery(String searchQuery) {
		//searchQuery = new TextNormalizer(searchQuery).normalizeTextLight();
		return extractNounVerbs(searchQuery).trim();
	}

	protected HashMap<Integer, String> getRACKQueryMap() {
		// collect RACK queries first.
		ArrayList<String> lines = ContentLoader
				.getAllLinesOptList(this.queryFile);
		// extracting the baseline queries
		for (int i = 0; i < lines.size(); i++) {
			if (i % 2 == 0) {
				int key = i / 2 + 1;
				if (!this.rackQueryMap.containsKey(key)) {
					String reformulated = new String();

					String nlQuery = this.baseQueryMap.get(key);
					reformulated = prepareNLQuery(nlQuery);

					//reformulated = lines.get(i).trim();

					String apiNames = lines.get(i + 1).trim();
					String goldAPIs = goldAPIMap.get(key);
					String matched = getMatchedAPIs(goldAPIs, apiNames);
					if (!matched.trim().isEmpty()) {
						// matchedAPIcase++;
					} else {
						matched = getPartiallyMatched(goldAPIs, apiNames);
					}

					String expanded = expandAPINames(matched);
					// reformulated += "/t/t" + matched;
					reformulated = reformulated + " " + expanded;

					this.rackQueryMap.put(key, reformulated);
				}
			}
		}
		return this.rackQueryMap;
	}

	public void getRACKPerformance() {
		// calculate the RACK code search performance
		// now perform code search
		RACKCodeSearcher codeSearcher = new RACKCodeSearcher(this.rackQueryMap,
				indexFolder, TOPK);
		double acc = codeSearcher.performRACKCodeSearch();
		double mrr = codeSearcher.performRACKCodeSearchForMRR();
		System.out.println("Top-" + TOPK + " Accuracy: " + acc);
		System.out.println("MRR@" + TOPK + ": " + mrr);
	}

	protected HashMap<Integer, Integer> getRACKFirstRanks() {
		// get the RACK queries first
		RACKCodeSearcher codeSearcher = new RACKCodeSearcher(rackQueryMap,
				indexFolder);
		return codeSearcher.getFirstGoldRankMap();
	}

	protected static void showMyRanks(HashMap<Integer, Integer> baseRanks,
			HashMap<Integer, Integer> toolRanks) {
		for (int key : baseRanks.keySet()) {
			System.out.println(key + "\t" + (baseRanks.get(key)+1) + "\t"
					+ (toolRanks.get(key)+1));
		}
	}
	
	public void getRACKQEPerformance() {
		HashMap<Integer, Integer> toolRanks = getRACKFirstRanks();
		HashMap<Integer, Integer> baseRanks = new BaselineCodeSearcher(
				oracleFile, indexFolder).getBaselineRanks();
		//showMyRanks(baseRanks, toolRanks);
		QueryRankComparer rqComparer = new QueryRankComparer(baseRanks,toolRanks);
		rqComparer.compareFirstGoldRanksNew();
	}

	public static void main(String[] args) {
		String EXP_HOME = "C:/MyWorks/Thesis Works/Crowdsource_Knowledge_Base/CodeTokenRec/RACK-Replication-Package/EMSE2018-Dataset";
		String resultFile = EXP_HOME + "/RACK-Suggested-API-Classes.txt";
		RACKPerformanceCalc pcalc = new RACKPerformanceCalc(resultFile);
		pcalc.getRACKQEPerformance();
	}
}
