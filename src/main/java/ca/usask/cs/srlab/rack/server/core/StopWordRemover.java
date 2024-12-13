package ca.usask.cs.srlab.rack.server.core;

import java.util.ArrayList;
import ca.usask.cs.srlab.rack.server.config.StaticData;
import ca.usask.cs.srlab.rack.server.utility.ContentLoader;

public class StopWordRemover {

	static ArrayList<String> stopwords = new ArrayList<>();
	
	static String stopDir = StaticData.STOPWORD_DIR
			+ "/stop-words-english-total.txt";

	protected static void loadStopWords() {
		if (stopwords.isEmpty()) {
			String[] lines = ContentLoader.getAllLines(stopDir);
			for (String line : lines) {
				stopwords.add(line.trim());
			}
		}
	}

	public String getRefinedSentence(String sentence) {
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
		String regex = "\\p{Punct}+|\\d+|\\s+";
		String[] parts = sentence.split(regex);
		String refined = new String();
		for (String str : parts) {
			refined += str.trim() + " ";
		}
		return refined;
	}

	public static ArrayList<String> removeStopWords(String[] tokens) {
		loadStopWords();
		ArrayList<String> refined = new ArrayList<>();
		for (String token : tokens) {
			if (stopwords.contains(token.toLowerCase())) {
				continue;
			} else {
				refined.add(token);
			}
		}
		return refined;
	}
}
