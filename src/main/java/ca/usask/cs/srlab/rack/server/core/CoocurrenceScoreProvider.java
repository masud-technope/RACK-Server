package ca.usask.cs.srlab.rack.server.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import ca.usask.cs.srlab.rack.server.config.StaticData;
import ca.usask.cs.srlab.rack.server.dbaccess.ConnectionManager;

public class CoocurrenceScoreProvider {

	ArrayList<String> queryTerms;
	ArrayList<String> keys;
	HashMap<String, ArrayList<String>> coocAPIMap;
	final int TOP_K_API_THRESHOLD = StaticData.DELTA1;
	HashMap<String, Double> coocScoreMap;

	public CoocurrenceScoreProvider(ArrayList<String> queryTerms) {
		this.queryTerms = queryTerms;
		this.keys = new ArrayList<>(new HashSet<String>(this.queryTerms));
		this.coocAPIMap = new HashMap<>();
		this.coocScoreMap = new HashMap<>();
	}

	protected ArrayList<String> getKeyPairs() {
		ArrayList<String> temp = new ArrayList<>();
		for (int i = 0; i < keys.size(); i++) {
			String first = keys.get(i);
			for (int j = i + 1; j < keys.size(); j++) {
				String second = keys.get(j);
				String keypair = first + "-" + second;
				temp.add(keypair);
			}
		}
		return temp;
	}

	protected void collectCoocAPIs(ArrayList<String> keyPairs) {
		try {
			Connection conn = ConnectionManager.conn;
			if (conn == null) {
				conn = ConnectionManager.getConnection();
			}
			if (conn != null) {
				for (String keypair : keyPairs) {
					String[] parts = keypair.split("-");
					String first = parts[0];
					String second = parts[1];
					String getCocc = "select "
							+ " Token from CodeToken where EntryID in("
							+ "select EntryID from TextToken where Token='"
							+ first + "' " + " intersect "
							+ " select EntryID from TextToken where Token='"
							+ second + "')"
							+ "group by Token order by count(*) desc limit "
							+ TOP_K_API_THRESHOLD;
					Statement stmt = conn.createStatement();
					ResultSet results = stmt.executeQuery(getCocc);
					ArrayList<String> temp = new ArrayList<>();
					while (results.next()) {
						temp.add(results.getString("Token"));
					}

					this.coocAPIMap.put(keypair, temp);
				}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	protected void generateCoocScores() {
		for (String keypair : this.coocAPIMap.keySet()) {
			ArrayList<String> apis = this.coocAPIMap.get(keypair);
			int length = apis.size();
			for (int i = 0; i < apis.size(); i++) {
				double score = 1 - (double) i / length;
				String api = apis.get(i);
				if (coocScoreMap.containsKey(api)) {
					double newScore = coocScoreMap.get(api) + score;
					coocScoreMap.put(api, newScore);
				} else {
					coocScoreMap.put(api, score);
				}
			}
		}
	}

	protected void normalizeScores() {
		double maxScore = 0;
		for (String api : coocScoreMap.keySet()) {
			double score = coocScoreMap.get(api);
			if (score > maxScore) {
				maxScore = score;
			}
		}
		for (String api : coocScoreMap.keySet()) {
			double nScore = coocScoreMap.get(api) / maxScore;
			this.coocScoreMap.put(api, nScore);
		}
	}

	public HashMap<String, Double> getCoocScores() {
		ArrayList<String> keyPairs = getKeyPairs();
		this.collectCoocAPIs(keyPairs);
		this.generateCoocScores();
		this.normalizeScores();
		return this.coocScoreMap;
	}
}
