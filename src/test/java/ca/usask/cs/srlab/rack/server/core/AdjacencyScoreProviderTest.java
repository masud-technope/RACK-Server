package ca.usask.cs.srlab.rack.server.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class AdjacencyScoreProviderTest {

    ArrayList<String> queryTerms;

    @BeforeEach
    public void setup(){
        queryTerms = new ArrayList<>();
        queryTerms.add("extract");
        queryTerms.add("method");
        queryTerms.add("class");
    }

    @Test
    public void testGetAdjacencyScores(){
        AdjacencyScoreProvider provider = new AdjacencyScoreProvider(queryTerms);
        provider.getQueryTermAdjacencyScores();
    }
}
