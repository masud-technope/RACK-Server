package ca.usask.cs.srlab.rack.server.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import ca.usask.cs.srlab.rack.server.config.StaticData;
import ca.usask.cs.srlab.rack.server.utility.ItemSorter;

public class CodeTokenProvider {

	String query;
	HashMap<String, Double> tokenScoreMap;
	final int MAXAPI = StaticData.MAXAPI;
	public ArrayList<String> stemmedQuery;
	public double gamma = StaticData.gamma;
	HashMap<String, Double> KACMap;
	HashMap<String, Double> KPACMap;
	HashMap<String, Double> KKCMap;

	public CodeTokenProvider(String query) {
		this.query = query;
		this.tokenScoreMap = new HashMap<>();
		this.stemmedQuery = new ArrayList<>();
		this.KACMap = new HashMap<>();
		this.KPACMap = new HashMap<>();
		this.KKCMap = new HashMap<>();
	}

	protected ArrayList<String> decomposeQueryTerms() {
		String tempQuery = this.query.toLowerCase();
		String regex = "\\p{Punct}+|\\s+";
		String numRegex = "\\d+";
		String[] tokens = tempQuery.split(regex);
		ArrayList<String> refined = StopWordRemover.removeStopWords(tokens);
		ArrayList<String> stemmed = TokenStemmer.performStemming(refined);
		ArrayList<String> stemmedQuery = new ArrayList<>();
		for (String token : stemmed) {
			// avoid numerical token
			if (token.matches(numRegex))
				continue;
			// at least greater than 2 //change 0 instantly
			else if (!token.isEmpty()) {
				if (!stemmedQuery.contains(token)) {
					stemmedQuery.add(token);
				}
			}
		}
		this.stemmedQuery = stemmedQuery;
		return stemmedQuery;
	}

	protected void collectTokenScores(ArrayList<String> queryTerms) {
		AdjacencyScoreProvider adjacent = new AdjacencyScoreProvider(queryTerms);
		adjacent.collectAdjacentTerms();
		double[][] simscores = adjacent.collectAdjacencyScores();
		ArrayList<String> keys = new ArrayList<>(adjacent.keys);

		RelevantAPICollector collector = new RelevantAPICollector(queryTerms);
		HashMap<String, ArrayList<String>> tokenmap = collector.collectAPIsforQuery();
		tokenScoreMap = new HashMap<>();

		// KAC scores
		this.addAssociationFrequencyScores(tokenmap);
		// KKC scores
		this.addTokenSimilarityScores(keys, simscores, tokenmap);
		// KPAC scores
		this.addDirectCoocScores();
		// add the textual similarity scores
		// this.addLexicalSimilarityScores(queryTerms, new ArrayList<>(
		// tokenScoreMap.keySet()));
		this.addExtraLayerScoreComputation();

	}

	public void collectTokenScoresKAC(ArrayList<String> queryTerms) {
		RelevantAPICollector collector = new RelevantAPICollector(queryTerms);
		HashMap<String, ArrayList<String>> tokenmap = collector.collectAPIsforQuery();
		tokenScoreMap = new HashMap<>();
		this.addAssociationFrequencyScores(tokenmap);
	}

	protected void collectTokenScoresKKC(ArrayList<String> queryTerms) {
		AdjacencyScoreProvider adjacent = new AdjacencyScoreProvider(queryTerms);
		adjacent.collectAdjacentTerms();
		double[][] simscores = adjacent.collectAdjacencyScores();
		ArrayList<String> keys = new ArrayList<>(adjacent.keys);
		RelevantAPICollector collector = new RelevantAPICollector(queryTerms);
		HashMap<String, ArrayList<String>> tokenmap = collector.collectAPIsforQuery();
		tokenScoreMap = new HashMap<>();
		this.addTokenSimilarityScores(keys, simscores, tokenmap);
	}

	protected void collectTokenScoresKPAC(ArrayList<String> queryTerms) {
		this.tokenScoreMap = new HashMap<>();
		this.addDirectCoocScores();
	}

	protected void addTokenSimilarityScores(ArrayList<String> keys, double[][] simscores,
			HashMap<String, ArrayList<String>> tokenmap) {
		for (int i = 0; i < keys.size(); i++) {
			String first = keys.get(i);
			ArrayList<String> firstapi = tokenmap.get(first);
			for (int j = i + 1; j < keys.size(); j++) {
				String second = keys.get(j);
				ArrayList<String> secondapi = tokenmap.get(second);
				HashSet<String> common = intersect(firstapi, secondapi);
				double simscore = simscores[i][j];

				if (simscore > gamma) {
					for (String token : common) {
						if (tokenScoreMap.containsKey(token)) {
							double newOldScore = tokenScoreMap.get(token) + simscore;
							tokenScoreMap.put(token, newOldScore);
						} else {
							tokenScoreMap.put(token, simscore);
						}

						if (KKCMap.containsKey(token)) {
							double newOldScore = KKCMap.get(token) + simscore;
							KKCMap.put(token, newOldScore);
						} else {
							KKCMap.put(token, simscore);
						}

					}
				}
			}
		}
	}

	protected void addAssociationFrequencyScores(HashMap<String, ArrayList<String>> tokenmap) {
		// association frequency score between text token and code token
		for (String key : tokenmap.keySet()) {
			ArrayList<String> apis = tokenmap.get(key);
			int length = apis.size();
			for (int i = 0; i < apis.size(); i++) {
				// now determine the score

				double score = 1 - (double) i / length;

				// add the weight
				// score = score * StaticData.alpha;

				String api = apis.get(i);
				// now check the score for each API
				// add the score to the map
				if (tokenScoreMap.containsKey(api)) {
					double newScore = tokenScoreMap.get(api) + score;
					tokenScoreMap.put(api, newScore);
				} else {
					tokenScoreMap.put(api, score);
				}

				// adding scores to the extra map
				if (KACMap.containsKey(api)) {
					double newScore = KACMap.get(api) + score;
					KACMap.put(api, newScore);
				} else {
					KACMap.put(api, score);
				}
			}
		}
	}

	protected void addDirectCoocScores() {
		// adding direct cooccurrence scores
		CoocurrenceScoreProvider coocProvider = new CoocurrenceScoreProvider(this.stemmedQuery);
		HashMap<String, Double> coocScoreMap = coocProvider.getCoocScores();
		for (String apiKey : coocScoreMap.keySet()) {
			double coocScore = coocScoreMap.get(apiKey);

			if (tokenScoreMap.containsKey(apiKey)) {
				double newScore = tokenScoreMap.get(apiKey) + coocScore;
				tokenScoreMap.put(apiKey, newScore);
			} else {
				tokenScoreMap.put(apiKey, coocScore);
			}

			if (KPACMap.containsKey(apiKey)) {
				double newScore = KPACMap.get(apiKey) + coocScore;
				KPACMap.put(apiKey, newScore);
			} else {
				KPACMap.put(apiKey, coocScore);
			}
		}
	}

	protected HashSet<String> intersect(ArrayList<String> s1, ArrayList<String> s2) {
		HashSet<String> common = new HashSet<>(s1);
		common.retainAll(s2);
		return common;
	}

	protected ArrayList<String> rankAPIElements() {
		List<Map.Entry<String, Double>> sorted = ItemSorter.sortHashMapDouble(tokenScoreMap);
		ArrayList<String> rankedAPIs = new ArrayList<>();
		for (Map.Entry<String, Double> entry : sorted) {
			rankedAPIs.add(entry.getKey());
		}
		return rankedAPIs;
	}

	protected ArrayList<String> rankAPIElements(HashMap<String, Double> scoreMap) {
		List<Map.Entry<String, Double>> sorted = ItemSorter.sortHashMapDouble(scoreMap);
		ArrayList<String> rankedAPIs = new ArrayList<>();
		for (Map.Entry<String, Double> entry : sorted) {
			rankedAPIs.add(entry.getKey());

		}
		// rankedAPIs = discardDuplicates(rankedAPIs);
		ArrayList<String> topRanked = new ArrayList<>();
		for (String api : rankedAPIs) {
			if (api.trim().isEmpty())
				continue;
			topRanked.add(api);
			if (topRanked.size() == StaticData.MAXAPI) {
				break;
			}
		}
		return topRanked;
	}

	protected void addExtraLayerScoreComputation() {
		ArrayList<String> kacs = rankAPIElements(this.KACMap);
		HashMap<String, Double> kacScoreMap = getNormScore(kacs);
		ArrayList<String> kpacs = rankAPIElements(this.KPACMap);
		HashMap<String, Double> kpacScoreMap = getNormScore(kpacs);
		ArrayList<String> kkcs = rankAPIElements(this.KKCMap);
		HashMap<String, Double> kkcScoreMap = getNormScore(kkcs);
		this.addCombinedRankingsV2(kacScoreMap, kpacScoreMap, kkcScoreMap, StaticData.alpha, StaticData.beta,
				StaticData.psi);

	}

	protected void addCombinedRankings(HashMap<String, Double> kacMap, HashMap<String, Double> kpacMap,
			HashMap<String, Double> kkcMap, double alpha1, double beta1, double psi1) {
		// get the combined rankings
		tokenScoreMap = new HashMap<>();
		// HashMap<String, Double> tokenScoreMap = new HashMap<>();
		for (String key : kacMap.keySet()) {
			Double score = kacMap.get(key);
			score = score * alpha1;
			if (tokenScoreMap.containsKey(key)) {
				double newScore = tokenScoreMap.get(key) + score;
				tokenScoreMap.put(key, newScore);
			} else {
				tokenScoreMap.put(key, score);
			}
		}
		for (String key : kpacMap.keySet()) {
			Double score = kpacMap.get(key);
			score = score * beta1;
			if (tokenScoreMap.containsKey(key)) {
				double newScore = tokenScoreMap.get(key) + score;
				tokenScoreMap.put(key, newScore);
			} else {
				tokenScoreMap.put(key, score);
			}
		}

		for (String key : kkcMap.keySet()) {
			Double score = kkcMap.get(key);
			score = score * psi1;
			if (tokenScoreMap.containsKey(key)) {
				double newScore = tokenScoreMap.get(key) + score;
				tokenScoreMap.put(key, newScore);
			} else {
				tokenScoreMap.put(key, score);
			}
		}
	}

	protected void addCombinedRankingsV2(HashMap<String, Double> kacMap, HashMap<String, Double> kpacMap,
			HashMap<String, Double> kkcMap, double alpha1, double beta1, double psi1) {
		// get the combined rankings
		tokenScoreMap = new HashMap<>();
		// HashMap<String, Double> tokenScoreMap = new HashMap<>();
		for (String key : kacMap.keySet()) {
			Double score = kacMap.get(key);
			score = score * alpha1;
			if (tokenScoreMap.containsKey(key)) {
				double newScore = Math.max(tokenScoreMap.get(key), score);
				tokenScoreMap.put(key, newScore);
			} else {
				tokenScoreMap.put(key, score);
			}
		}
		for (String key : kpacMap.keySet()) {
			Double score = kpacMap.get(key);
			score = score * beta1;
			if (tokenScoreMap.containsKey(key)) {
				double newScore = Math.max(tokenScoreMap.get(key), score);
				tokenScoreMap.put(key, newScore);
			} else {
				tokenScoreMap.put(key, score);
			}
		}

		for (String key : kkcMap.keySet()) {
			Double score = kkcMap.get(key);
			score = score * psi1;
			if (tokenScoreMap.containsKey(key)) {
				double newScore = Math.max(tokenScoreMap.get(key), score);
				tokenScoreMap.put(key, newScore);
			} else {
				tokenScoreMap.put(key, score);
			}
		}
		// return rankAPIElements(tokenScoreMap);
	}

	protected HashMap<String, Double> getNormScore(ArrayList<String> apis) {
		HashMap<String, Double> tempMap = new HashMap<>();
		int index = 0;
		for (String api : apis) {
			index++;
			double score = 1 - (double) index / apis.size();
			tempMap.put(api, score);
			// index++;
		}
		return tempMap;
	}

	public ArrayList<APIToken> recommendRelevantAPIs(String... key) {
		// recommend API names for a query

		ArrayList<String> queryTerms = decomposeQueryTerms();
		// collecting scores
		if (key.length > 0) {
			switch (key[0]) {
			case "KAC":
				this.collectTokenScoresKAC(queryTerms);
				break;
			case "KPAC":
				this.collectTokenScoresKPAC(queryTerms);
				break;
			case "KKC":
				this.collectTokenScoresKKC(queryTerms);
				break;
			case "all":
				this.collectTokenScores(queryTerms);
				break;
			default:
				this.collectTokenScores(queryTerms);
				break;
			}
		} else {
			this.collectTokenScores(queryTerms);
		}

		ArrayList<String> apis = rankAPIElements();

		// now refine the list
		// lets keep the duplicates
		// apis = discardDuplicates(apis);

		// now normalize the component scores
		this.KACMap = normalizeMapScores(this.KACMap);
		this.KPACMap = normalizeMapScores(this.KPACMap);
		this.KKCMap = normalizeMapScores(this.KKCMap);

		// now demonstrate the API
		ArrayList<String> resultAPIs = new ArrayList<>();
		ArrayList<APIToken> suggestedResults = new ArrayList<APIToken>();

		for (String api : apis) {
			// System.out.println(api);
			if (api.trim().isEmpty())
				continue;

			// adding the results with scores
			APIToken atoken = new APIToken();
			atoken.token = api;
			if (KACMap.containsKey(api)) {
				atoken.KACScore = KACMap.get(api);
			}
			if (KPACMap.containsKey(api)) {
				atoken.KPACScore = KPACMap.get(api);
			}
			if (KKCMap.containsKey(api)) {
				atoken.KKCScore = KKCMap.get(api);
			}
			if (tokenScoreMap.containsKey(api)) {
				atoken.totalScore = tokenScoreMap.get(api);
			}

			suggestedResults.add(atoken);
			resultAPIs.add(api);

			if (resultAPIs.size() == StaticData.MAXAPI)
				break;
		}
		// showAPIs(apis);

		return suggestedResults;
	}

	protected void showAPIs(ArrayList<String> apis) {
		// showing the ranked APIs
		for (String api : apis) {
			System.out.println(api);
		}
	}

	protected HashMap<String, Double> normalizeMapScores(HashMap<String, Double> tempScoreMap) {
		double maxScore = 0;
		for (String api : tempScoreMap.keySet()) {
			double score = tempScoreMap.get(api);
			if (score > maxScore) {
				maxScore = score;
			}
		}
		for (String api : tempScoreMap.keySet()) {
			double myscore = tempScoreMap.get(api);
			double normScore = myscore / maxScore;
			tempScoreMap.put(api, normScore);
		}
		return tempScoreMap;
	}

	protected ArrayList<String> discardDuplicates(ArrayList<String> results) {
		// discarding duplicated API list
		ArrayList<String> discardList = new ArrayList<>();
		for (int i = results.size() - 1; i > 0; i--) {
			String low = results.get(i);
			for (int j = i - 1; j >= 0; j--) {
				String high = results.get(j);
				// any overlap of APIs
				if (low.endsWith(high) || high.endsWith(low)) {
					discardList.add(low);
				}
			}
		}
		// now remove the discard items
		results.removeAll(discardList);
		// return the modified list
		return results;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String query = "How to parse HTML in Java?";
		CodeTokenProvider provider = new CodeTokenProvider(query);
		ArrayList<APIToken> results = provider.recommendRelevantAPIs("all");
		for (APIToken atoken : results) {
			System.out.println(atoken.token + " " + atoken.KACScore + " " + atoken.KPACScore + " " + atoken.KKCScore
					+ " " + atoken.totalScore);
		}
	}
}
