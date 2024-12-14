package ca.usask.cs.srlab.rack.server.text.normalizer;

import java.util.ArrayList;
import java.util.Arrays;

import ca.usask.cs.srlab.rack.server.core.StopWordRemover;
import ca.usask.cs.srlab.rack.server.core.TokenStemmer;
import ca.usask.cs.srlab.rack.server.stopwords.StopWordManager;
import ca.usask.cs.srlab.rack.server.utility.MiscUtility;

public class TextNormalizer {

    String content;

    public TextNormalizer(String content) {
        this.content = content;
    }

    public String normalizeSimple() {
        String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
        ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
        return MiscUtility.list2Str(wordList);
    }

    public String normalizeSimpleCodeDiscardSmall() {
        String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
        ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
        ArrayList<String> codeItems = extractCodeItem(wordList);
        codeItems = decomposeCamelCase(codeItems);
        wordList.addAll(codeItems);
        wordList = discardSmallTokens(wordList);
        String modified = MiscUtility.list2Str(wordList);
        StopWordRemover stopManager = new StopWordRemover();
        this.content = stopManager.getRefinedSentence(modified);
        return this.content;
    }

    public String normalizeSimpleCode() {
        String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
        ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
        wordList = extractCodeItem(wordList);
        String modifiedContent = MiscUtility.list2Str(wordList);
        StopWordManager stopManager = new StopWordManager();
        this.content = stopManager.getRefinedSentence(modifiedContent);
        return this.content;
    }

    public String normalizeSimpleNonCode() {
        String[] words = this.content.split("\\p{Punct}+|\\s+");
        ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
        ArrayList<String> codeOnly = extractCodeItem(wordList);
        wordList.removeAll(codeOnly);
        return MiscUtility.list2Str(wordList);
    }

    protected ArrayList<String> discardSmallTokens(ArrayList<String> items) {
        ArrayList<String> temp = new ArrayList<>();
        for (String item : items) {
            if (item.length() > 2) {
                temp.add(item);
            }
        }
        return temp;
    }

    public String normalizeText() {
        String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
        ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
        wordList = discardSmallTokens(wordList);
        String modifiedContent = MiscUtility.list2Str(wordList);
        StopWordManager stopManager = new StopWordManager();
        this.content = stopManager.getRefinedSentence(modifiedContent);
        return this.content;
    }

    public String normalizeText(ArrayList<String> wordList) {
        wordList = discardSmallTokens(wordList);
        StopWordManager stopManager = new StopWordManager();
        ArrayList<String> refined = stopManager.getRefinedList(wordList);
        ArrayList<String> temp = TokenStemmer.performStemming(refined);
        return MiscUtility.list2Str(temp);
    }

    public String normalizeTextLight() {
        String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
        ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
        wordList = discardSmallTokens(wordList);
        return MiscUtility.list2Str(wordList);
    }

    protected ArrayList<String> extractCodeItem(ArrayList<String> words) {
        ArrayList<String> codeTokens = new ArrayList<>();
        for (String token : words) {
            if (decomposeCamelCase(token).size() > 1) {
                codeTokens.add(token);
            }
        }
        return codeTokens;
    }

    protected ArrayList<String> decomposeCamelCase(String token) {
        ArrayList<String> refined = new ArrayList<>();
        String camRegex = "([a-z])([A-Z]+)";
        String replacement = "$1\t$2";
        String filtered = token.replaceAll(camRegex, replacement);
        String[] filteredTokens = filtered.split("\\s+");
        refined.addAll(Arrays.asList(filteredTokens));
        return refined;
    }

    protected ArrayList<String> decomposeCamelCase(ArrayList<String> tokens) {
        ArrayList<String> refined = new ArrayList<>();
        for (String token : tokens) {
            String camRegex = "([a-z])([A-Z]+)";
            String replacement = "$1\t$2";
            String filtered = token.replaceAll(camRegex, replacement);
            String[] filteredTokens = filtered.split("\\s+");
            refined.addAll(Arrays.asList(filteredTokens));
        }
        return refined;
    }
}
