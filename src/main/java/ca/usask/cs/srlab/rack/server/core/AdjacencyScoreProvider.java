package ca.usask.cs.srlab.rack.server.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import ca.usask.cs.srlab.rack.server.similarity.CosineSimilarityMeasure;
import ca.usask.cs.srlab.rack.server.dbaccess.ConnectionManager;

public class AdjacencyScoreProvider {

    ArrayList<String> queryTerms;
    HashMap<String, ArrayList<String>> adjacencyMap;
    public ArrayList<String> keys;
    double[][] simScores;

    public AdjacencyScoreProvider(ArrayList<String> queryTerms) {
        this.queryTerms = queryTerms;
        this.adjacencyMap = new HashMap<>();
        this.keys = new ArrayList<>();
    }

    public void collectAdjacentTerms() {
        try {
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
                    this.adjacencyMap.put(key, adjacent);
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public double[][] collectAdjacencyScores() {
        int dimension = adjacencyMap.keySet().size();
        this.keys.addAll(adjacencyMap.keySet());
        simScores = new double[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            String first = keys.get(i);
            for (int j = i + 1; j < dimension; j++) {
                String second = keys.get(j);
                CosineSimilarityMeasure cos = new CosineSimilarityMeasure(
                        adjacencyMap.get(first), adjacencyMap.get(second));
                double simScore = cos.getCosineSimilarityScore();
                simScores[i][j] = simScore;
                showScores(keys.get(i), keys.get(j), simScore);
            }
        }
        return simScores;
    }

    public double[][] getQueryTermAdjacencyScores() {
        this.collectAdjacentTerms();
        return this.collectAdjacencyScores();
    }

    protected void showScores(String firstTerm, String secondTerm, double score) {
        System.out.println(firstTerm + " -- " + secondTerm + " = " + score);
    }
}
