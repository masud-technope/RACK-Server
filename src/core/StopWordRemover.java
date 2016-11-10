package core;

import java.util.ArrayList;
import utility.ContentLoader;

public class StopWordRemover {

	ArrayList<String> stopwords=new ArrayList<>();
	
	public StopWordRemover() {
		// default constructor
		this.stopwords=new ArrayList<>();
	}

	public StopWordRemover(ArrayList<String> stopwords) {
		this.stopwords=stopwords;
	}

	protected void loadStopWords() {
		// loading stop words
		if (this.stopwords.isEmpty()) {
			String[] lines = ContentLoader.getAllLines("./stopword/en.txt");
			for (String line : lines) {
				stopwords.add(line.trim());
			}
		}
	}

	public ArrayList<String> removeStopWords(String[] tokens) {
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
