package ca.usask.cs.srlab.rack.server.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

public class LexicalSimilarityProviderTest {

    ArrayList<String> queryTerms;
    ArrayList<String> apiCandidates;
    @BeforeEach
    public void setup(){
        queryTerms=new ArrayList<>();
        queryTerms.add("How");
        queryTerms.add("parse");
        queryTerms.add("html");
        queryTerms.add("Java");

        apiCandidates=new ArrayList<>();
        apiCandidates.add("Document");
        apiCandidates.add("Jsoup");
        apiCandidates.add("Element");
        apiCandidates.add("Elements");
    }

    @Test
    public void testGetLexicalSimilarityScores(){
        LexicalSimilarityProvider provider=new LexicalSimilarityProvider(queryTerms, apiCandidates);
        System.out.println(provider.getLexicalSimilarityScores());
    }
}
