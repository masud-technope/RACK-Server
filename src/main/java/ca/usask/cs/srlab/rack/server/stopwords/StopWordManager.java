package ca.usask.cs.srlab.rack.server.stopwords;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import ca.usask.cs.srlab.rack.server.config.StaticData;
import ca.usask.cs.srlab.rack.server.utility.ContentLoader;

public class StopWordManager {

    public ArrayList<String> stopList;
    String stopDir = StaticData.STOPWORD_DIR
            + "/stop-words-english-total.txt";
    String javaKeywordFile = StaticData.STOPWORD_DIR
            + "/java-keywords.txt";
    ;
    String CppKeywordFile = StaticData.STOPWORD_DIR + "/cpp-keywords.txt";

    public StopWordManager() {
        this.stopList = new ArrayList<>();
        this.loadStopWords();
    }

    protected void loadStopWords() {
        try {
            Scanner scanner = new Scanner(new File(this.stopDir));
            while (scanner.hasNext()) {
                String word = scanner.nextLine().trim();
                this.stopList.add(word);
            }
            scanner.close();

            ArrayList<String> keywords = ContentLoader
                    .getAllLinesOptList(javaKeywordFile);
            this.stopList.addAll(keywords);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public String removeTinyTerms(String sentence) {
        String regex = "\\p{Punct}+|\\d+|\\s+";
        String[] parts = sentence.split(regex);
        String refined = new String();
        for (String str : parts) {
            if (str.length() >= 3)
                refined += str.trim() + " ";
        }
        return refined;
    }

    public String getRefinedSentence(String sentence) {
        String refined = new String();
        String temp = removeSpecialChars(sentence);
        String[] tokens = temp.split("\\s+");
        for (String token : tokens) {
            if (!this.stopList.contains(token.toLowerCase())) {
                refined += token + " ";
            }
        }
        return refined.trim();
    }

    public ArrayList<String> getRefinedList(String[] words) {
        ArrayList<String> refined = new ArrayList<>();
        for (String word : words) {
            if (!this.stopList.contains(word.toLowerCase())) {
                refined.add(word);
            }
        }
        return refined;
    }

    public ArrayList<String> getRefinedList(ArrayList<String> words) {
        ArrayList<String> refined = new ArrayList<>();
        for (String word : words) {
            if (!this.stopList.contains(word.toLowerCase())) {
                refined.add(word);
            }
        }
        return refined;
    }

    public HashSet<String> getRefinedList(HashSet<String> words) {
        HashSet<String> refined = new HashSet<>();
        for (String word : words) {
            if (!this.stopList.contains(word.toLowerCase())) {
                refined.add(word);
            }
        }
        return refined;
    }
}
