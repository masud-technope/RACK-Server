package ca.usask.cs.srlab.rack.server.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class CodeTokenProviderTest {

    @Test
    public void testProvideCodeTokens() {
        String searchQuery = "How to parse HTML in Java?";
        CodeTokenProvider provider = new CodeTokenProvider(searchQuery);
        ArrayList<APIToken> results = provider.recommendRelevantAPIs("all");
        System.out.println("========ALL=======");
        for (APIToken atoken : results) {
            System.out.println(atoken.token + " " + atoken.KACScore + " " + atoken.KPACScore + " " + atoken.KKCScore
                    + " " + atoken.totalScore);
        }
    }

    @Test
    public void testProvideCodeTokensKAC(){
        String searchQuery = "How to parse HTML in Java?";
        CodeTokenProvider provider = new CodeTokenProvider(searchQuery);
        ArrayList<APIToken> results = provider.recommendRelevantAPIs("KAC");
        System.out.println("========KAC=======");
        for (APIToken atoken : results) {
            System.out.println(atoken.token + " " + atoken.KACScore + " " + atoken.KPACScore + " " + atoken.KKCScore
                    + " " + atoken.totalScore);
        }
    }

    @Test
    public void testProvideCodeTokensKPAC(){
        String searchQuery = "How to parse HTML in Java?";
        CodeTokenProvider provider = new CodeTokenProvider(searchQuery);
        ArrayList<APIToken> results = provider.recommendRelevantAPIs("KPAC");
        System.out.println("========KPAC=======");
        for (APIToken atoken : results) {
            System.out.println(atoken.token + " " + atoken.KACScore + " " + atoken.KPACScore + " " + atoken.KKCScore
                    + " " + atoken.totalScore);
        }
    }

}
