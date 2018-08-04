package code.search.evaluation;

import java.util.HashMap;

public class QueryRankComparer {

	String rackQueryFile;

	HashMap<Integer, Integer> baseRanks;
	HashMap<Integer, Integer> toolRanks;

	public QueryRankComparer(HashMap<Integer, Integer> baseRanks,
			HashMap<Integer, Integer> toolRanks) {
		this.baseRanks = baseRanks;
		this.toolRanks = toolRanks;
	}

	public void compareFirstGoldRanksNew() {
		// now compare these two ranks
		int improved = 0;
		int worsened = 0;
		int preserved = 0;

		for (int key : baseRanks.keySet()) {
			int baseRank = baseRanks.get(key) + 1;
			int rackRank = toolRanks.get(key) + 1;

			if (rackRank > 0) {
				if (baseRank > 0) {
					if (rackRank < baseRank) {
						improved++;
					} else if (rackRank == baseRank) {
						preserved++;
					} else if (rackRank > baseRank) {
						worsened++;
					}
				} else {
					if (rackRank > 0) {
						improved++;
					}
				}
			} else {
				if (baseRank == rackRank) {
					preserved++;
				} else
					worsened++;
			}
		}

		System.out.println("Improved: " + (double) improved / baseRanks.size());
		System.out.println("Worsened: " + (double) worsened / baseRanks.size());
		System.out.println("Net Gain:"
				+ ((double) improved / baseRanks.size() - (double) worsened
						/ baseRanks.size()));
		System.out.println("Preserved: " + (double) preserved
				/ baseRanks.size());
	}

	public static void main(String[] args) {
		// empty main
	}
}
