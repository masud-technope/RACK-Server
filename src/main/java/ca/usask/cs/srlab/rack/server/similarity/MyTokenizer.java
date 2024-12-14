package ca.usask.cs.srlab.rack.server.similarity;

import java.util.ArrayList;
import java.util.StringTokenizer;


public class MyTokenizer {

    String itemToTokenize;

    public MyTokenizer(String item) {
        this.itemToTokenize = item;
    }

    public ArrayList<String> tokenizeTextItem() {
        StringTokenizer tokenizer = new StringTokenizer(this.itemToTokenize);
        ArrayList<String> tokens = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token.trim();

            if (!token.isEmpty()) {
                ArrayList<String> smalltokens = processTextItem(token);
                tokens.addAll(smalltokens);
            }
        }
        return tokens;
    }

    public ArrayList<String> tokenizeCodeItem() {
        StringTokenizer tokenizer = new StringTokenizer(this.itemToTokenize);
        ArrayList<String> tokens = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (!token.isEmpty()) {
                ArrayList<String> tokenParts = processSourceToken(token);
                tokens.addAll(tokenParts);
            }
        }
        return tokens;
    }

    public ArrayList<String> refineInsignificantTokens(
            ArrayList<String> codeTokens) {
        try {
            for (String token : codeTokens) {
                if (token.trim().length() == 1) {
                    codeTokens.remove(token);
                }
            }
        } catch (Exception exc) {

        }
        return codeTokens;
    }


    protected static String removeCodeComment(String codeFragment) {
        String modifiedCode = "";
        try {
            String pattern = "//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/";
            modifiedCode = codeFragment.replaceAll(pattern, "");
        } catch (Exception exc) {
        } catch (StackOverflowError err) {

        }
        return modifiedCode;
    }

    protected static ArrayList<String> processSourceToken(String token) {
        ArrayList<String> modified = new ArrayList<String>();
        String[] segments = token.split("\\.");
        for (String segment : segments) {
            String[] parts = org.apache.commons.lang.StringUtils
                    .splitByCharacterTypeCamelCase(segment);
            if (parts.length == 0) {
                modified.add(segment);
            } else {
                for (String part : parts) {
                    modified.add(part);
                }
            }
        }
        return modified;
    }

    protected static ArrayList<String> processTextItem(String bigToken) {
        ArrayList<String> modified = new ArrayList<>();
        try {
            String[] parts = org.apache.commons.lang.StringUtils
                    .splitByCharacterTypeCamelCase(bigToken);
            for (String part : parts) {
                String[] segments = part.split("\\.");
                if (segments.length == 0)
                    modified.add(part);
                else {
                    for (String segment : segments) {
                        if (!segment.isEmpty() && segment.length() >= 2)
                            modified.add(segment);
                    }
                }
            }
        } catch (Exception exc) {
        }
        return modified;
    }
}
