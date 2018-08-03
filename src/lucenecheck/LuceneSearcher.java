package lucenecheck;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import config.StaticData;

public class LuceneSearcher {

	int bugID;
	String repository;
	String indexFolder;
	String field = "contents";
	String queries = null;
	int repeat = 0;
	boolean raw = true;
	String queryString = null;
	int hitsPerPage = 10;
	String searchQuery;
	int MAX_RESULTS = 10000;
	ArrayList<String> results;
	ArrayList<String> goldset;
	int caseNo;

	public double precision = 0;
	public double recall = 0;
	public double precatk = 0;

	public double maxScore = 0;
	public double minScore = 100000;

	public LuceneSearcher(String searchQuery, String repository) {
		// initialization
		// this.bugID = bugID;
		this.repository = repository;
		this.indexFolder = StaticData.EVA_HOME + "/lucene/index/" + repository;
		this.searchQuery = searchQuery;
		this.results = new ArrayList<>();
		// this.goldset = new ArrayList<>();
	}

	public LuceneSearcher(int caseNo, String searchQuery, String indexFolder) {
		this.searchQuery = searchQuery;
		this.indexFolder = indexFolder;
		this.results = new ArrayList<>();
		this.caseNo = caseNo;
	}

	public ArrayList<String> performVSMSearch(int TOPK) {
		// performing Lucene search
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory
					.open(new File(indexFolder)));
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
			QueryParser parser = new QueryParser(Version.LUCENE_44, field,
					analyzer);
			if (!searchQuery.isEmpty()) {
				Query myquery = parser.parse(searchQuery);
				TopDocs results = searcher.search(myquery, TOPK);
				ScoreDoc[] hits = results.scoreDocs;

				for (int i = 0; i < TOPK; i++) {
					ScoreDoc item = hits[i];
					Document doc = searcher.doc(item.doc);
					// int docID=item.doc;
					String fileURL = doc.get("path");
					fileURL = fileURL.replace('\\', '/');
					// System.out.println("Doc ID:"+docID);
					// Terms terms=reader.getTermVector(docID, "contents");
					// System.out.println(doc.get("contents"));

					String APIName = new File(fileURL).getName().split("\\.")[0];
					this.results.add(APIName);
				}
				// showing gold set
				// showGoldSet();
				// getGoldSet();
			}
		} catch (Exception e) {
			// handle the exception
		}
		return this.results;
	}

	public ArrayList<String> performVSMSearch() {
		// performing Lucene search
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory
					.open(new File(indexFolder)));
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
			QueryParser parser = new QueryParser(Version.LUCENE_44, field,
					analyzer);
			if (!searchQuery.isEmpty()) {
				Query myquery = parser.parse(searchQuery);
				TopDocs results = searcher.search(myquery, MAX_RESULTS);
				ScoreDoc[] hits = results.scoreDocs;

				for (int i = 0; i < hits.length; i++) {
					ScoreDoc item = hits[i];
					Document doc = searcher.doc(item.doc);
					// int docID=item.doc;
					String fileURL = doc.get("path");
					fileURL = fileURL.replace('\\', '/');
					// System.out.println("Doc ID:"+docID);
					// Terms terms=reader.getTermVector(docID, "contents");
					// System.out.println(doc.get("contents"));

					String APIName = new File(fileURL).getName().split("\\.")[0];
					this.results.add(APIName);
				}
				// showing gold set
				// showGoldSet();
				// getGoldSet();
			}
		} catch (Exception e) {
			// handle the exception
		}
		return this.results;
	}

	public HashMap<String, Double> performVSMSearchWithScore(int TOPK) {
		HashMap<String, Double> resultMap = new HashMap<>();
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory
					.open(new File(indexFolder)));
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
			QueryParser parser = new QueryParser(Version.LUCENE_44, field,
					analyzer);
			if (!searchQuery.isEmpty()) {
				Query myquery = parser.parse(searchQuery);
				TopDocs results = searcher.search(myquery, TOPK);
				ScoreDoc[] hits = results.scoreDocs;

				for (int i = 0; i < hits.length; i++) {
					ScoreDoc item = hits[i];
					Document doc = searcher.doc(item.doc);
					double score = item.score;
					String fileURL = doc.get("path");
					fileURL = fileURL.replace('\\', '/');
					String resultFileName = new File(fileURL).getName().split(
							"\\.")[0].trim();
					resultMap.put(resultFileName, score);

					// determine max-min scores
					if (score > maxScore) {
						maxScore = score;
					}
					if (score < minScore) {
						minScore = score;
					}
				}
			}
		} catch (Exception e) {
			// handle the exception
		}
		return resultMap;
	}

	public int getFirstGoldRank(int key, int TOPK) {
		// getting the first gold rank
		ArrayList<String> results = performVSMSearch(TOPK);
		int rank = -1;
		for (String fileURL : results) {
			// rank++;
			int fileID = Integer.parseInt(fileURL.trim());
			if (fileID == key) {
				// rank++;
				rank = results.indexOf(fileURL);
				return rank;
			}
		}
		return rank;
	}

	public int getFirstGoldRank(int key) {
		// getting the first gold rank
		ArrayList<String> results = performVSMSearch();
		int rank = -1;
		for (String fileURL : results) {
			// rank++;
			int fileID = Integer.parseInt(fileURL.trim());
			if (fileID == key) {
				// rank++;
				rank = results.indexOf(fileURL);
				return rank;
			}
		}
		return rank;
	}

	public static void main(String[] args) {
		// int bugID = 41186;
		String repository = "chcomment";
		String searchQuery = "verify signature signed data";
		LuceneSearcher searcher = new LuceneSearcher(searchQuery, repository);
		int TOPK = 10;
		ArrayList<String> results = searcher.performVSMSearch(TOPK);
		System.out.println(results);
	}
}
