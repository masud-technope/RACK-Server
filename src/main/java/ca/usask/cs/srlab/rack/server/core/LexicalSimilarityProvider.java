package ca.usask.cs.srlab.rack.server.core;

import java.util.ArrayList;
import java.util.HashMap;

import ca.usask.cs.srlab.rack.server.similarity.CosineSimilarityMeasure;

public class LexicalSimilarityProvider {

    ArrayList<String> queryTerms;
    ArrayList<String> candidates;
    HashMap<String, Double> simScoreMap;

    public LexicalSimilarityProvider(ArrayList<String> queryTerms,
                                     ArrayList<String> candidates) {
        this.queryTerms = queryTerms;
        this.candidates = candidates;
        this.simScoreMap = new HashMap<>();
    }

    protected String[] decomposeCamelCase(String token) {
        String camRegex = "([a-z])([A-Z]+)";
        String replacement = "$1\t$2";
        String filtered = token.replaceAll(camRegex, replacement);
        return filtered.split("\\s+");
    }

    protected ArrayList<String> clearTheTokens(String[] tokenParts) {
        ArrayList<String> refined = StopWordRemover.removeStopWords(tokenParts);
        return TokenStemmer.performStemming(refined);
    }

    protected ArrayList<String> normalizeAPIToken(String apiToken) {
        String[] decomposed = decomposeCamelCase(apiToken);
        return clearTheTokens(decomposed);
    }

    public HashMap<String, Double> getLexicalSimilarityScores() {
        for (String apiName : this.candidates) {
            ArrayList<String> normalizedTokens = normalizeAPIToken(apiName);
            CosineSimilarityMeasure cosMeasure = new CosineSimilarityMeasure(
                    normalizedTokens, this.candidates);
            double simScore = cosMeasure.getCosineSimilarityScore();
            if (!this.simScoreMap.containsKey(apiName)) {
                this.simScoreMap.put(apiName, simScore);
            }
        }
        return this.simScoreMap;
    }
}
