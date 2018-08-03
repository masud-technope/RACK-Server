package text.normalizer;

import java.util.ArrayList;
import java.util.Arrays;
import core.StopWordRemover;
import core.TokenStemmer;
import utility.MiscUtility;

public class TextNormalizer {

	String content;

	public TextNormalizer(String content) {
		this.content = content;
	}

	public String normalizeSimpleCodeDiscardSmall() {
		String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		// decomposing the camel cases
		ArrayList<String> codeItems = extractCodeItem(wordList);
		codeItems = decomposeCamelCase(codeItems);
		wordList.addAll(codeItems);
		// discarding non-important tokens
		wordList = discardSmallTokens(wordList);
		String modified = MiscUtility.list2Str(wordList);
		// discard stop words
		StopWordRemover stopManager = new StopWordRemover();
		this.content = stopManager.getRefinedSentence(modified);
		return this.content;
	}

	protected ArrayList<String> discardSmallTokens(ArrayList<String> items) {
		// discarding small tokens
		ArrayList<String> temp = new ArrayList<>();
		for (String item : items) {
			if (item.length() > 2) {
				temp.add(item);
			}
		}
		return temp;
	}

	public String normalizeText() {
		// normalize the content
		String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		// discard the small tokens
		wordList = discardSmallTokens(wordList);
		String modifiedContent = MiscUtility.list2Str(wordList);
		StopWordRemover stopManager = new StopWordRemover();
		this.content = stopManager.getRefinedSentence(modifiedContent);
		return this.content;
	}

	protected ArrayList<String> extractCodeItem(ArrayList<String> words) {
		// extracting camel-case letters
		ArrayList<String> codeTokens = new ArrayList<>();
		for (String token : words) {
			if (decomposeCamelCase(token).size() > 1) {
				codeTokens.add(token);
			}
		}
		return codeTokens;
	}

	protected ArrayList<String> decomposeCamelCase(String token) {
		// decomposing camel case tokens using regex
		ArrayList<String> refined = new ArrayList<>();
		String camRegex = "([a-z])([A-Z]+)";
		String replacement = "$1\t$2";
		String filtered = token.replaceAll(camRegex, replacement);
		String[] ftokens = filtered.split("\\s+");
		refined.addAll(Arrays.asList(ftokens));
		return refined;
	}

	protected ArrayList<String> decomposeCamelCase(ArrayList<String> tokens) {
		// decomposing camel case tokens using regex
		ArrayList<String> refined = new ArrayList<>();
		for (String token : tokens) {
			String camRegex = "([a-z])([A-Z]+)";
			String replacement = "$1\t$2";
			String filtered = token.replaceAll(camRegex, replacement);
			String[] ftokens = filtered.split("\\s+");
			refined.addAll(Arrays.asList(ftokens));
		}
		return refined;
	}
}
