package ca.usask.cs.srlab.rack.server.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import ca.usask.cs.srlab.rack.server.config.StaticData;
import ca.usask.cs.srlab.rack.server.dbaccess.ConnectionManager;

public class RelevantAPICollector {

	ArrayList<String> queryTerms;
	final int TOP_K_API_THRESHOLD = StaticData.DELTA1;

	public RelevantAPICollector(ArrayList<String> queryTerms) {
		this.queryTerms = queryTerms;
	}

	public HashMap<String, ArrayList<String>> collectAPIsForQuery() {
		HashMap<String, ArrayList<String>> tokenMap = new HashMap<>();
		try {
			Connection conn = ConnectionManager.getConnection();
			if (conn != null) {
				Statement stmt = conn.createStatement();
				for (String textToken : queryTerms) {
					String getCodeToken = "select "
							+ " ct.Token from CodeToken as ct, TextToken as tt "
							+ " where ct.EntryID=tt.EntryID and tt.Token='"
							+ textToken
							+ "'"
							+ " group by ct.Token order by count(*) desc limit "
							+ TOP_K_API_THRESHOLD;
					ResultSet results = stmt.executeQuery(getCodeToken);

					ArrayList<String> apis = new ArrayList<>();
					while (results.next()) {
						String codeToken = results.getString("Token");
						apis.add(codeToken);
					}
					tokenMap.put(textToken, apis);
				}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return tokenMap;
	}
}
