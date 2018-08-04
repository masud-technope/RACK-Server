package code.search.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import text.normalizer.TextNormalizer;
import utility.ContentLoader;

public class BaselineCodeSearcher {

	String baselineFile;
	HashMap<Integer, String> baselineQueryMap;
	int TOPK = 10;
	String indexFolder;

	public BaselineCodeSearcher(String oracleFile, String indexFolder, int TOPK) {
		// default constructor
		this.baselineFile = oracleFile;
		this.baselineQueryMap = getBaselineQueryMap();
		this.indexFolder = indexFolder;
		this.TOPK = TOPK;
	}
	
	public BaselineCodeSearcher(String oracleFile, String indexFolder) {
		// default constructor
		this.baselineFile = oracleFile;
		this.baselineQueryMap = getBaselineQueryMap();
		this.indexFolder = indexFolder;
	}

	protected String normalizeBaseline(String myQuery) {
		TextNormalizer textNormalizer = new TextNormalizer(myQuery);
		return textNormalizer.normalizeText();
	}

	protected HashMap<Integer, String> getBaselineQueryMap() {
		ArrayList<String> lines = ContentLoader
				.getAllLinesOptList(this.baselineFile);
		HashMap<Integer, String> tempQueryMap = new HashMap<>();
		// extracting the baseline queries
		for (int i = 0; i < lines.size(); i++) {
			if (i % 2 == 0) {
				int key = i / 2 + 1;
				if (!tempQueryMap.containsKey(key)) {
					String normalized = normalizeBaseline(lines.get(i).trim());
					tempQueryMap.put(key, normalized);
				}
			}
		}
		return tempQueryMap;
	}

	protected void getBaselinePerformance() {
		// calculate the baseline code search performance
		RACKCodeSearcher codeSearcher = new RACKCodeSearcher(
				this.baselineQueryMap, indexFolder, TOPK);
		double acc = codeSearcher.performRACKCodeSearch();
		double mrr = codeSearcher.performRACKCodeSearchForMRR();
		System.out.println("Top-" + TOPK + " Accuracy: " + acc);
		System.out.println("MRR@" + TOPK + ": " + mrr);
	}

	public HashMap<Integer, Integer> getBaselineRanks() {
		// collect baseline ranks
		RACKCodeSearcher codeSearcher = new RACKCodeSearcher(baselineQueryMap,
				indexFolder);
		return codeSearcher.getFirstGoldRankMap();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
