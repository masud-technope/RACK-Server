package experiment;

import java.io.File;
import java.io.FileWriter;
import utility.ContentLoader;
import config.StaticData;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class QueryFormatter {

	String oracleFile;
	String formattedQueryFile;
	static MaxentTagger tagger = null;

	public QueryFormatter() {
		// default constructor
		if (tagger == null) {
			tagger = new MaxentTagger("./models/english-left3words-distsim.tagger");
		}
	}

	public QueryFormatter(String oracleFile) {
		// customized constructor
		tagger = new MaxentTagger("./models/english-left3words-distsim.tagger");
		this.oracleFile = StaticData.EVA_HOME + "/" + oracleFile;
		this.formattedQueryFile = StaticData.EVA_HOME
				+ "/oracle-all-noun-verb.txt";
	}

	public String tagSingleQuery(String query) {
		// tag a single query
		String tagged = tagger.tagString(query);
		String[] words = tagged.split("\\s+");
		String temp = new String();
		for (String word : words) {
			String[] parts = word.split("_");
			if (parts[1].contains("NN") || parts[1].contains("VB")) {
				// if (parts[1].contains("VB")) {
				// if (parts[1].contains("NN")) {
				// System.out.println(parts[0]);
				temp += parts[0] + " ";
			}
		}
		// String tagged=tagger.tagTokenizedString(query);
		// System.out.println(tagged);
		return temp.trim();
	}

	protected void saveTagFilteredQuery(String query, String apiList) {
		// saving the tag filtered query
		try {
			FileWriter fwriter = new FileWriter(new File(
					this.formattedQueryFile), true);
			fwriter.write(query + "\n");
			fwriter.write(apiList + "\n");
			fwriter.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	protected void reformatQuery() {
		// formatting the queries
		String[] lines = ContentLoader.getAllLines(this.oracleFile);
		for (int i = 0; i < lines.length; i = i + 2) {
			if (i % 2 == 0) {
				String tagFiltered = tagSingleQuery(lines[i]);
				String apiList = lines[i + 1];
				saveTagFilteredQuery(tagFiltered, apiList);
				// System.out.println(tagFiltered);
				// System.out.println(apiList);
				// if(i==50)break;
			}
		}
		System.out.println("Original query reformulated.");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String oracle = "oracle-175.txt";
		QueryFormatter formatter = new QueryFormatter(oracle);
		formatter.reformatQuery();
	}
}
