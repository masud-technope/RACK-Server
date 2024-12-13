package ca.usask.cs.srlab.rack.server.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class CooccurrenceScoreProviderTest {

    ArrayList<String> queryTerms;

    @BeforeEach
    public void setup(){
        queryTerms=new ArrayList<>();
        /*queryTerms.add("How");
        queryTerms.add("parse");
        queryTerms.add("html");
        queryTerms.add("Java");*/

        queryTerms.add("copi");
        queryTerms.add("file");
        queryTerms.add("jdk");
    }

    @Test
    public void testGetCoocScores(){
        CoocurrenceScoreProvider provider=new CoocurrenceScoreProvider(queryTerms);
        System.out.println(provider.getCoocScores());
    }
}
