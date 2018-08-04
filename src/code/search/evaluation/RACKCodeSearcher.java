package code.search.evaluation;

import java.util.HashMap;

import lucenecheck.LuceneSearcher;

public class RACKCodeSearcher {

	HashMap<Integer, String> queryMap;
	String indexFolder;
	int TOPK;

	public RACKCodeSearcher(HashMap<Integer, String> queryMap,
			String indexFolder, int TOPK) {
		this.queryMap = queryMap;
		this.indexFolder = indexFolder;
		this.TOPK = TOPK;
	}

	public RACKCodeSearcher(HashMap<Integer, String> queryMap,
			String indexFolder) {
		this.queryMap = queryMap;
		this.indexFolder = indexFolder;
	}

	protected double performRACKCodeSearch() {
		// perform RACK code search
		double topKAcc = 0;
		int caseFound = 0;
		for (int key : this.queryMap.keySet()) {
			String query = queryMap.get(key);
			LuceneSearcher searcher = new LuceneSearcher(key, query,
					indexFolder);
			int goldRank = searcher.getFirstGoldRank(key, TOPK);
			if (goldRank >= 0 && goldRank < TOPK) {
				caseFound++;
			} else {
				// do nothing
			}
		}
		topKAcc = (double) caseFound / queryMap.size();
		return topKAcc;
	}

	protected double performRACKCodeSearchForMRR() {
		// perform RACK code search
		double rr = 0;
		for (int key : this.queryMap.keySet()) {
			String query = queryMap.get(key);
			LuceneSearcher searcher = new LuceneSearcher(key, query,
					indexFolder);
			int goldRank = searcher.getFirstGoldRank(key, TOPK);
			if (goldRank >= 0 && goldRank < TOPK) {
				rr += (1.0 / (goldRank + 1));
			} else {
				// do nothing
			}
		}
		return rr / queryMap.size();
	}

	public HashMap<Integer, Integer> getFirstGoldRankMap() {
		// collecting first gold ranks for the queries
		HashMap<Integer, Integer> rankMap = new HashMap<>();
		for (int key : this.queryMap.keySet()) {
			String query = queryMap.get(key);
			LuceneSearcher searcher = new LuceneSearcher(key, query,
					indexFolder);
			int goldRank = searcher.getFirstGoldRank(key);
			if (!rankMap.containsKey(key)) {
				rankMap.put(key, goldRank);
			}
		}
		return rankMap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
