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
		// decomposing camel case tokens using regex
		String camRegex = "([a-z])([A-Z]+)";
		String replacement = "$1\t$2";
		String filtered = token.replaceAll(camRegex, replacement);
		String[] ftokens = filtered.split("\\s+");
		return ftokens;
	}

	protected ArrayList<String> clearTheTokens(String[] tokenParts) {
		ArrayList<String> refined = StopWordRemover.removeStopWords(tokenParts);
		ArrayList<String> stemmed = TokenStemmer.performStemming(refined);
		return stemmed;
	}

	protected ArrayList<String> normalizeAPIToken(String apiToken) {
		// normalize the API token into granular tokens
		String[] decomposed = decomposeCamelCase(apiToken);
		ArrayList<String> normalized = clearTheTokens(decomposed);
		return normalized;
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
