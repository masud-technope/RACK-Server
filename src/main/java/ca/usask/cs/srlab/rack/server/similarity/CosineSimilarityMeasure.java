package ca.usask.cs.srlab.rack.server.similarity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

public class CosineSimilarityMeasure {


    String title1 = "";
    String title2 = "";
    double cosine_measure = 0;


    Set<String> set1;
    Set<String> set2;


    ArrayList<String> list1;
    ArrayList<String> list2;


    HashMap<String, Integer> map1;
    HashMap<String, Integer> map2;

    public CosineSimilarityMeasure(String title1, String title2) {
        this.title1 = title1;
        this.title2 = title2;

        set1 = new HashSet<String>();
        set2 = new HashSet<String>();

        map1 = new HashMap<String, Integer>();
        map2 = new HashMap<String, Integer>();
    }

    public CosineSimilarityMeasure(ArrayList<String> list1,
                                   ArrayList<String> list2) {

        this.list1 = list1;
        this.list2 = list2;

        set1 = new HashSet<String>();
        set2 = new HashSet<String>();

        map1 = new HashMap<String, Integer>();
        map2 = new HashMap<String, Integer>();
    }

    @Deprecated
    protected ArrayList<String> getTokenizedTextContent(String content) {
        ArrayList<String> tokens = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(content);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!token.isEmpty()) {
                tokens.add(token);
            }
        }

        return tokens;
    }

    protected ArrayList<String> getTokenizedTextContentGranularized(
            String content) {
        MyTokenizer myTokenizer = new MyTokenizer(content);
        ArrayList<String> tokens = myTokenizer.tokenizeCodeItem();
        return myTokenizer.refineInsignificantTokens(tokens);
    }


    public double getCosineSimilarityScore() {
        try {
            for (String str : this.list1) {

                if (!str.isEmpty()) {
                    set1.add(str);
                    if (!map1.containsKey(str))
                        map1.put(str, 1);
                    else {
                        int count = Integer.parseInt(map1.get(str).toString());
                        count++;
                        map1.put(str, count);
                    }
                }
            }

            for (String str : this.list2) {

                if (!str.isEmpty()) {
                    set2.add(str);
                    if (!map2.containsKey(str))
                        map2.put(str, 1);
                    else {
                        int count = Integer.parseInt(map2.get(str).toString());
                        count++;
                        map2.put(str, count);
                    }
                }
            }

            HashSet<String> hset1 = new HashSet<String>(set1);
            HashSet<String> hset2 = new HashSet<String>(set2);

            double sqr1 = 0;
            for (int i = 0; i < hset1.size(); i++) {
                int val = Integer
                        .parseInt(map1.get(hset1.toArray()[i]) != null ? map1
                                .get(hset1.toArray()[i]).toString() : "0");
                sqr1 += val * val;
            }

            double sqr2 = 0;
            for (int i = 0; i < hset2.size(); i++) {
                int val = Integer
                        .parseInt(map2.get(hset2.toArray()[i]) != null ? map2
                                .get(hset2.toArray()[i]).toString() : "0");
                sqr2 += val * val;
            }

            // now calculate the similarity
            double top_part = 0;
            for (int i = 0; i < hset1.size(); i++) {
                String key = (String) set1.toArray()[i];
                double val1 = Double.parseDouble(map1.get(key).toString());
                double val2 = Double.parseDouble(map2.get(key) != null ? map2
                        .get(key).toString() : "0");
                top_part += val1 * val2;
            }

            double cosine_ratio = 0;
            try {
                cosine_ratio = top_part / (Math.sqrt(sqr1) * Math.sqrt(sqr2));
            } catch (Exception exc) {
                cosine_ratio = 0;
            }

            this.cosine_measure = cosine_ratio;

        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return cosine_measure;

    }


    public double getCosineSimilarityScore(boolean granularized) {
        try {
            if (title1.isEmpty() || title1 == null)
                return 0;
            if (title2.isEmpty() || title2 == null)
                return 0;

            ArrayList<String> parts1 = granularized == true ? this
                    .getTokenizedTextContentGranularized(title1) : this
                    .getTokenizedTextContent(title1);
            ArrayList<String> parts2 = granularized == true ? this
                    .getTokenizedTextContentGranularized(title2) : this
                    .getTokenizedTextContent(title2);


            for (String str : parts1) {


                if (!str.isEmpty()) {
                    set1.add(str);
                    if (!map1.containsKey(str))
                        map1.put(str, 1);
                    else {
                        int count = Integer.parseInt(map1.get(str).toString());
                        count++;
                        map1.put(str, count);
                    }
                }
            }

            for (String str : parts2) {
                if (!str.isEmpty()) {
                    set2.add(str);
                    if (!map2.containsKey(str))
                        map2.put(str, 1);
                    else {
                        int count = Integer.parseInt(map2.get(str).toString());
                        count++;
                        map2.put(str, count);
                    }
                }
            }

            HashSet<String> hashSet1 = new HashSet<String>(set1);
            HashSet<String> hashSet2 = new HashSet<String>(set2);

            double sqr1 = 0;
            for (int i = 0; i < hashSet1.size(); i++) {
                int val = Integer
                        .parseInt(map1.get(hashSet1.toArray()[i]) != null ? map1
                                .get(hashSet1.toArray()[i]).toString() : "0");
                sqr1 += val * val;
            }

            double sqr2 = 0;
            for (int i = 0; i < hashSet2.size(); i++) {
                int val = Integer
                        .parseInt(map2.get(hashSet2.toArray()[i]) != null ? map2
                                .get(hashSet2.toArray()[i]).toString() : "0");
                sqr2 += val * val;
            }

            double top_part = 0;
            for (int i = 0; i < hashSet1.size(); i++) {
                String key = (String) set1.toArray()[i];
                double val1 = Double.parseDouble(map1.get(key).toString());
                double val2 = Double.parseDouble(map2.get(key) != null ? map2
                        .get(key).toString() : "0");
                top_part += val1 * val2;
            }

            double cosine_ratio = 0;
            try {
                cosine_ratio = top_part / (Math.sqrt(sqr1) * Math.sqrt(sqr2));
            } catch (Exception exc) {
                cosine_ratio = 0;
            }

            this.cosine_measure = cosine_ratio;

        } catch (Exception exc) {
            // exc.printStackTrace();
        }

        return cosine_measure;
    }

    protected void show_extracted_tokens(Set s) {
        for (int i = 0; i < s.size(); i++) {
            System.out.print(s.toArray()[i] + "\t");
        }
    }

    protected static String loadTextContent(String fileName) {
        String content = "";
        try {
            Scanner scanner = new Scanner(new File("./testdata/" + fileName));
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                content += line;
            }
            scanner.close();
        } catch (Exception exc) {
        }
        return content;
    }

}
