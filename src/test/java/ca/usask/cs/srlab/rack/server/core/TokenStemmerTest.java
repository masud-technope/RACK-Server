package ca.usask.cs.srlab.rack.server.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TokenStemmerTest {

    String[] tokens;

    @BeforeEach
    public void setup(){
        tokens=new String[]{"revised", "version", "satisfactorily", "addresses"};
    }

    @Test
    public void testPerformStemmingToken(){
        System.out.println(TokenStemmer.performStemming("satisfactorily"));
    }

    @Test
    public void testPerformStemmingTokens(){
        System.out.println(TokenStemmer.performStemming(tokens));
    }
}
