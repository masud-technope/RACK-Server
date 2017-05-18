package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import config.StaticData;
import utility.ItemSorter;

public class CodeTokenProvider {

	String query;
	HashMap<String, Double> tokenScoreMap;
	HashMap<String, APIToken> tokenMap;
	final int MAXAPI = StaticData.MAXAPI;
	public ArrayList<String> stemmedQuery;
	public double gamma = 0;
	ArrayList<String> stopwords;

	public CodeTokenProvider(String query) {
		this.query = query;
		this.tokenScoreMap = new HashMap<>();
		this.stemmedQuery = new ArrayList<>();
		this.tokenMap = new HashMap<>();
		this.stopwords = new ArrayList<>();
	}

	public CodeTokenProvider(String query, ArrayList<String> stopwords) {
		this.query = query;
		this.tokenScoreMap = new HashMap<>();
		this.stemmedQuery = new ArrayList<>();
		this.tokenMap = new HashMap<>();
		this.stopwords = stopwords;
	}

	protected ArrayList<String> decomposeQueryTerms() {
		// decompose the query terms
		String tempQuery = this.query.toLowerCase();
		String regex = "\\p{Punct}+|\\s+";
		String numRegex = "\\d+";
		String[] tokens = tempQuery.split(regex);
		// performing NLP
		ArrayList<String> refined = new StopWordRemover(this.stopwords)
				.removeStopWords(tokens);
		// ArrayList<String> refined=new
		// ArrayList<String>(Arrays.asList(tokens));
		ArrayList<String> stemmed = TokenStemmer.performStemming(refined);
		ArrayList<String> stemmedQuery = new ArrayList<>();
		for (String token : stemmed) {
			if (token.matches(numRegex))
				continue; // avoid numerical token
			else if (token.length() > 0) { // at least greater than 2 //change 0
											// instantly
				if (!stemmedQuery.contains(token)) {
					stemmedQuery.add(token);
				}
			}
		}
		// store the stemmed query
		this.stemmedQuery = stemmedQuery;
		// System.out.println("Stemmed Query:" + stemmedQuery);
		// returning the processed query
		return stemmedQuery;
	}

	protected void collectTokenScoresOld(ArrayList<String> queryTerms) {
		// collecting token scores
		AdjacencyScoreProvider adjacent = new AdjacencyScoreProvider(queryTerms);
		// adjacency scores
		adjacent.collectAdjacentTerms();
		double[][] simscores = adjacent.collectAdjacencyScores();
		// keys
		ArrayList<String> keys = new ArrayList<>(adjacent.keys);

		RelevantAPICollector collector = new RelevantAPICollector(queryTerms);
		HashMap<String, ArrayList<String>> tokenmap = collector
				.collectAPIsforQuery();
		// System.out.println(tokenmap);
		tokenScoreMap = new HashMap<>();

		// now add the scores
		this.addAssociationFrequencyScores(tokenmap);
		this.addTokenSimilarityScores(keys, simscores, tokenmap);
	}

	protected void collectTokenScores(ArrayList<String> queryTerms) {
		// collecting token scores
		AdjacencyScoreProvider adjacent = new AdjacencyScoreProvider(queryTerms);
		// adjacency scores
		adjacent.collectAdjacentTerms();
		double[][] simscores = adjacent.collectAdjacencyScores();
		// keys
		ArrayList<String> keys = new ArrayList<>(adjacent.keys);

		RelevantAPICollector collector = new RelevantAPICollector(queryTerms);
		HashMap<String, ArrayList<String>> tokenmap = collector
				.collectAPIsforQuery();
		// System.out.println(tokenmap);
		// tokenScoreMap = new HashMap<>();
		// now add the scores
		// KAC scores
		this.addAssociationFrequencyScores(tokenmap);
		// KKC scores
		this.addTokenSimilarityScores(keys, simscores, tokenmap);
		// direct cooc-score
		this.addDirectCoocScores();
		// add the textual similarity scores
		// this.addLexicalSimilarityScores(queryTerms, new ArrayList<>(
		// tokenScoreMap.keySet()));
	}

	protected void addDirectCoocScores() {
		// adding direct co-occurrence scores
		CoocurrenceScoreProvider coocProvider = new CoocurrenceScoreProvider(
				this.stemmedQuery);
		HashMap<String, Double> coocScoreMap = coocProvider.getCoocScores();
		for (String apiKey : coocScoreMap.keySet()) {
			double coocScore = coocScoreMap.get(apiKey);
			if (tokenScoreMap.containsKey(apiKey)) {
				double newScore = tokenScoreMap.get(apiKey) + coocScore;
				tokenScoreMap.put(apiKey, newScore);
			} else {
				tokenScoreMap.put(apiKey, coocScore);
			}

			// adding tokens to token map
			// storing the KKC scores
			if (this.tokenMap.containsKey(apiKey)) {
				APIToken atoken = this.tokenMap.get(apiKey);
				atoken.CoocScore += coocScore;
				this.tokenMap.put(apiKey, atoken);
			} else {
				APIToken atoken = new APIToken();
				atoken.token = apiKey;
				atoken.CoocScore = coocScore;
				this.tokenMap.put(apiKey, atoken);
			}
		}
	}

	@Deprecated
	protected void collectTokenScoresKAC(ArrayList<String> queryTerms) {
		// collecting token scores based on KAC
		RelevantAPICollector collector = new RelevantAPICollector(queryTerms);
		HashMap<String, ArrayList<String>> tokenmap = collector
				.collectAPIsforQuery();
		tokenScoreMap = new HashMap<>();
		// now add the scores
		this.addAssociationFrequencyScores(tokenmap);
	}

	@Deprecated
	protected void collectTokenScoresAAC(ArrayList<String> queryTerms) {
		// collecting scores based on AAC
		AdjacencyScoreProvider adjacent = new AdjacencyScoreProvider(queryTerms);
		// adjacency scores
		adjacent.collectAdjacentTerms();
		double[][] simscores = adjacent.collectAdjacencyScores();
		// keys from queries
		ArrayList<String> keys = new ArrayList<>(adjacent.keys);
		RelevantAPICollector collector = new RelevantAPICollector(queryTerms);
		HashMap<String, ArrayList<String>> tokenmap = collector
				.collectAPIsforQuery();

		tokenScoreMap = new HashMap<>();
		// now add the scores
		this.addTokenSimilarityScores(keys, simscores, tokenmap);
	}

	protected void addTokenSimilarityScores(ArrayList<String> keys,
			double[][] simscores, HashMap<String, ArrayList<String>> tokenmap) {
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
							double newScore = tokenScoreMap.get(token)
									+ simscore;
							tokenScoreMap.put(token, newScore);
						} else {
							tokenScoreMap.put(token, simscore);
						}

						// storing the KKC scores
						if (this.tokenMap.containsKey(token)) {
							APIToken atoken = this.tokenMap.get(token);
							atoken.KKCScore += simscore;
							this.tokenMap.put(token, atoken);
						} else {
							APIToken atoken = new APIToken();
							atoken.token = token;
							atoken.KKCScore = simscore;
							this.tokenMap.put(token, atoken);
						}
					}
				}
			}
		}

		// now show the KAC score of the APIs
		// System.out.println("KKC Score============");
		// for (String key : tokenScoreMap.keySet()) {
		// System.out.println(key + " " + tokenScoreMap.get(key));
		// }
		// System.out.println("KKC Score============ended");
	}

	protected void addAssociationFrequencyScores(
			HashMap<String, ArrayList<String>> tokenmap) {
		// association frequency score between text token and code token
		for (String key : tokenmap.keySet()) {
			ArrayList<String> apis = tokenmap.get(key);
			int length = apis.size();
			for (int i = 0; i < apis.size(); i++) {
				// now determine the score
				double score = 1 - (double) i / length;
				String api = apis.get(i);
				// now check the score for each API
				// add the score to the map
				if (tokenScoreMap.containsKey(api)) {
					double newScore = tokenScoreMap.get(api) + score;
					tokenScoreMap.put(api, newScore);
				} else {
					tokenScoreMap.put(api, score);
				}

				// storing KAC scores
				if (this.tokenMap.containsKey(api)) {
					APIToken atoken = this.tokenMap.get(api);
					atoken.KACScore += score;
					this.tokenMap.put(api, atoken);
				} else {
					APIToken atoken = new APIToken();
					atoken.token = api;
					atoken.KACScore = score;
					this.tokenMap.put(api, atoken);
				}
			}
		}

		// now show the KAC score of the APIs
		// System.out.println("KAC Score============");
		// for (String key : tokenScoreMap.keySet()) {
		// System.out.println(key + " " + tokenScoreMap.get(key));
		// }
		// System.out.println("KAC Score============ended");

	}

	protected HashSet<String> intersect(ArrayList<String> s1,
			ArrayList<String> s2) {
		// intersecting the two sets / list of items
		HashSet<String> common = new HashSet<>(s1);
		common.retainAll(s2);
		return common;
	}

	protected ArrayList<String> rankAPIElements() {
		// rank the API names
		List<Map.Entry<String, Double>> sorted = ItemSorter
				.sortHashMapDouble(tokenScoreMap);
		ArrayList<String> rankedAPIs = new ArrayList<>();
		for (Map.Entry<String, Double> entry : sorted) {
			rankedAPIs.add(entry.getKey());
			// System.out.println(entry.getKey() + " " + entry.getValue());
		}
		// returning the ranked APIs
		return rankedAPIs;
	}

	protected void normalizeNSumScores() {
		// normalize the scores
		double maxKAC = 0;
		double maxKKC = 0;
		double maxTS = 0;

		for (String key : this.tokenMap.keySet()) {
			APIToken atoken = this.tokenMap.get(key);
			if (atoken.KACScore > maxKAC) {
				maxKAC = atoken.KACScore;
			}
			if (atoken.KKCScore > maxKKC) {
				maxKKC = atoken.KKCScore;
			}
		}
		// normalize and summate the scores
		for (String key : this.tokenMap.keySet()) {
			APIToken atoken = this.tokenMap.get(key);
			atoken.KACScore = atoken.KACScore / maxKAC;
			atoken.KKCScore = atoken.KKCScore / maxKKC;
			atoken.totalScore = atoken.KACScore + atoken.KKCScore;
			if (atoken.totalScore > maxTS) {
				maxTS = atoken.totalScore;
			}
			this.tokenMap.put(key, atoken);
		}

		for (String key : this.tokenMap.keySet()) {
			APIToken atoken = this.tokenMap.get(key);
			atoken.totalScore = atoken.totalScore / maxTS;
			this.tokenMap.put(key, atoken);

			this.tokenScoreMap.put(key, atoken.totalScore);

		}
	}

	public ArrayList<String> recommendRelevantAPIs() {
		// recommend API names for a query
		ArrayList<String> queryTerms = decomposeQueryTerms();

		// collecting scores
		this.collectTokenScores(queryTerms);
		// this.collectTokenScoresKAC(queryTerms);
		// this.collectTokenScoresAAC(queryTerms);

		ArrayList<String> apis = rankAPIElements();

		// now refine the list
		apis = discardDuplicates(apis);

		// now demonstrate the API
		int count = 0;
		ArrayList<String> result = new ArrayList<>();

		for (String api : apis) {
			// System.out.println(api);
			result.add(api);
			count++;
			if (count == MAXAPI)
				break;
		}
		// showAPIs(apis);

		return result;
	}

	public ArrayList<APIToken> recommendRelevantAPIs(boolean webVersion) {
		// recommend API names for a query
		ArrayList<String> queryTerms = decomposeQueryTerms();
		// collecting scores
		this.collectTokenScores(queryTerms);
		// this.collectTokenScoresKAC(queryTerms);
		// this.collectTokenScoresAAC(queryTerms);

		// normalize and summate scores
		this.normalizeNSumScores();

		ArrayList<String> apis = rankAPIElements();

		// now refine the list
		apis = discardDuplicates(apis);

		// now demonstrate the API
		int count = 0;
		ArrayList<APIToken> result = new ArrayList<>();

		for (String api : apis) {
			APIToken atoken = this.tokenMap.get(api);
			atoken.totalScore = tokenScoreMap.get(api);
			result.add(atoken);
			count++;
			if (count == MAXAPI)
				break;
		}
		return result;
	}

	protected void showAPIs(ArrayList<String> apis) {
		// showing the ranked APIs
		for (String api : apis) {
			System.out.println(api);
		}
	}

	protected void showAPITokens(ArrayList<APIToken> apis) {
		// showing API ranks
		for (APIToken atoken : apis) {
			System.out.println(atoken.token + "\t" + atoken.KACScore + "\t"
					+ atoken.KKCScore + "\t" + atoken.totalScore);
		}
	}

	protected void showAPITokens(ArrayList<APIToken> apis, boolean extended) {
		// showing API ranks
		for (APIToken atoken : apis) {
			System.out.println(atoken.token + "\t" + atoken.KACScore + "\t"
					+ atoken.KKCScore + "\t" + atoken.CoocScore + "\t"
					+ atoken.totalScore);
		}
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
		String query = "sending email in Java";
		CodeTokenProvider provider = new CodeTokenProvider(query);
		ArrayList<APIToken> relevantAPIs = provider.recommendRelevantAPIs(true);
		// System.out.println(relevantAPIs);
		provider.showAPITokens(relevantAPIs, true);
	}
}
