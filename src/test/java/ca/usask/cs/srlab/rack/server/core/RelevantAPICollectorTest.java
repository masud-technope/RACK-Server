package ca.usask.cs.srlab.rack.server.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class RelevantAPICollectorTest {

    ArrayList<String> queryTerms;

    @BeforeEach
    public void setup() {
        queryTerms = new ArrayList<>();
        queryTerms.add("How");
        queryTerms.add("parse");
        queryTerms.add("html");
        queryTerms.add("Java");
    }

    @Test
    public void testCollectAPIForQuery(){
        RelevantAPICollector collector=new RelevantAPICollector(queryTerms);
        System.out.println(collector.collectAPIsforQuery());
    }
}
