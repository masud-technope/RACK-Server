package rack.core;

import java.util.ArrayList;
import rack.config.StaticData;
import rack.utility.ContentLoader;

public class StopWordRemover {

	static ArrayList<String> stopwords = new ArrayList<>();
	
	static String stopDir = StaticData.STOPWORD_DIR
			+ "/stop-words-english-total.txt";

	protected static void loadStopWords() {
		// loading stop words
		if (stopwords.isEmpty()) {
			String[] lines = ContentLoader.getAllLines(stopDir);
			for (String line : lines) {
				stopwords.add(line.trim());
			}
		}
	}

	public String getRefinedSentence(String sentence) {
		// get refined sentence
		String refined = new String();
		String temp = removeSpecialChars(sentence);
		String[] tokens = temp.split("\\s+");
		for (String token : tokens) {
			if (!stopwords.contains(token.toLowerCase())) {
				refined += token + " ";
			}
		}
		return refined.trim();
	}

	protected String removeSpecialChars(String sentence) {
		// removing special characters
		String regex = "\\p{Punct}+|\\d+|\\s+";
		String[] parts = sentence.split(regex);
		String refined = new String();
		for (String str : parts) {
			refined += str.trim() + " ";
		}
		// if(modifiedWord.isEmpty())modifiedWord=word;
		return refined;
	}

	public static ArrayList<String> removeStopWords(String[] tokens) {
		// loading stop words
		loadStopWords();
		// now remove the stop words
		ArrayList<String> refined = new ArrayList<>();
		for (String token : tokens) {
			if (stopwords.contains(token.toLowerCase())) {
				continue;
			} else {
				refined.add(token);
			}
		}
		// returning refined token list
		return refined;
	}
}
