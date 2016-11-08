package core;

import java.util.ArrayList;
import snowballstemmer.EnglishStemmer;

public class TokenStemmer {

	public static ArrayList<String> performStemming(ArrayList<String> tokens) {
		// perform stemming on a list of tokens
		ArrayList<String> stemmedList = new ArrayList<>();
		EnglishStemmer stemmer = new EnglishStemmer();
		for (String token : tokens) {
			stemmer.setCurrent(token);
			if (stemmer.stem()) {
				stemmedList.add(stemmer.getCurrent());
			}
		}
		// returning the stemmed token list
		return stemmedList;
	}

	public static String performStemming(String token) {
		EnglishStemmer stemmer = new EnglishStemmer();
		stemmer.setCurrent(token);
		stemmer.stem();
		return stemmer.getCurrent();
	}
}
