package ca.usask.cs.srlab.rack.server.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import ca.usask.cs.srlab.rack.server.config.StaticData;
import ca.usask.cs.srlab.rack.server.dbaccess.ConnectionManager;

public class RelevantAPICollector {

	ArrayList<String> queryTerms;
	final int TOPK_API_THRESHOLD = StaticData.DELTA1;

	public RelevantAPICollector(ArrayList<String> queryTerms) {
		this.queryTerms = queryTerms;
	}

	public HashMap<String, ArrayList<String>> collectAPIsforQuery() {
		// translate the query terms into API
		HashMap<String, ArrayList<String>> tokenmap = new HashMap<>();
		try {
			/*
			 * Class.forName(StaticData.Driver_name).newInstance(); Connection
			 * conn = DriverManager .getConnection(StaticData.connectionString);
			 */
			Connection conn = ConnectionManager.getConnection();
			if (conn != null) {
				Statement stmt = conn.createStatement();
				for (String texttoken : queryTerms) {
					String getCodeToken = "select "
							+ " ct.Token from CodeToken as ct, TextToken as tt "
							+ " where ct.EntryID=tt.EntryID and tt.Token='"
							+ texttoken
							+ "'"
							+ " group by ct.Token order by count(*) desc limit "
							+ TOPK_API_THRESHOLD;
					ResultSet results = stmt.executeQuery(getCodeToken);

					ArrayList<String> apis = new ArrayList<>();
					while (results.next()) {
						String codetoken = results.getString("Token");
						apis.add(codetoken);
					}
					// storing results in the HashMap
					tokenmap.put(texttoken, apis);
				}
				// conn.close();
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return tokenmap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
