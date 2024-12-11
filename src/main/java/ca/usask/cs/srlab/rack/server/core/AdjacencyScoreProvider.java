package ca.usask.cs.srlab.rack.server.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import ca.usask.cs.srlab.rack.server.similarity.CosineSimilarityMeasure;
import ca.usask.cs.srlab.rack.server.config.StaticData;
import ca.usask.cs.srlab.rack.server.dbaccess.ConnectionManager;

public class AdjacencyScoreProvider {

	ArrayList<String> queryTerms;
	HashMap<String, ArrayList<String>> adjacencymap;
	public ArrayList<String> keys;
	double[][] simscores;

	public AdjacencyScoreProvider(ArrayList<String> queryTerms) {
		// object initialization
		this.queryTerms = queryTerms;
		this.adjacencymap = new HashMap<>();
		this.keys = new ArrayList<>();
	}

	public void collectAdjacentTerms() {
		// collect adjacency terms
		try {
			/*
			 * Class.forName(StaticData.Driver_name).newInstance(); Connection
			 * conn = DriverManager .getConnection(StaticData.connectionString);
			 */
			Connection conn = ConnectionManager.getConnection();
			if (conn != null) {
				for (String key : queryTerms) {
					String getAdjacent = "select distinct Token from TextToken where EntryID in "
							+ "(select EntryID from TextToken where Token='"
							+ key + "') and Token!='" + key + "'";
					Statement stmt = conn.createStatement();
					ResultSet results = stmt.executeQuery(getAdjacent);
					ArrayList<String> adjacent = new ArrayList<>();
					while (results.next()) {
						String token = results.getString("Token");
						adjacent.add(token);
					}
					// now add the adjacent list to the query
					this.adjacencymap.put(key, adjacent);
				}
				// conn.close();
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public double[][] collectAdjacencyScores() {
		// collect adjacency scores
		int dimension = adjacencymap.keySet().size();
		// collecting the keys
		this.keys.addAll(adjacencymap.keySet());
		// matrix for storing similarity scores
		simscores = new double[dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			String first = keys.get(i);
			for (int j = i + 1; j < dimension; j++) {
				String second = keys.get(j);
				// adjacency similarity scores
				CosineSimilarityMeasure cos = new CosineSimilarityMeasure(
						adjacencymap.get(first), adjacencymap.get(second));
				double simscore = cos.getCosineSimilarityScore();
				simscores[i][j] = simscore;
				// System.out.println(first+" "+second+"="+simscore);
			}
		}
		return simscores;
	}

	public void getQueryTermAdjacencyScores() {
		// collecting query term adjacency scores
		this.collectAdjacentTerms();
		this.collectAdjacencyScores();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> queryTerms = new ArrayList<>();
		queryTerms.add("extract");
		queryTerms.add("method");
		queryTerms.add("class");
		AdjacencyScoreProvider provider = new AdjacencyScoreProvider(queryTerms);
		provider.getQueryTermAdjacencyScores();
	}
}
