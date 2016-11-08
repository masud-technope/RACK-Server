package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import config.StaticData;

public class RelevantAPICollector {
	
	
	ArrayList<String> queryTerms;
	
	public RelevantAPICollector(ArrayList<String> queryTerms)
	{
		this.queryTerms=queryTerms;
	}
	
	public HashMap<String, ArrayList<String>> collectAPIsforQuery() {
		// translate the query terms into API
		HashMap<String, ArrayList<String>> tokenmap = new HashMap<>();
		try {
			Class.forName(StaticData.Driver_name).newInstance();
			Connection conn = DriverManager
					.getConnection(StaticData.connectionString);
			if (conn != null) {
				Statement stmt = conn.createStatement();
				for (String texttoken : queryTerms) {
					String getCodeToken = "select top 5 ct.Token from CodeToken as ct, TextToken as tt "
							+ " where ct.EntryID=tt.EntryID and tt.Token='"
							+ texttoken
							+ "'"
							+ " group by ct.Token order by count(*) desc";
					ResultSet results = stmt.executeQuery(getCodeToken);

					ArrayList<String> apis = new ArrayList<>();
					while (results.next()) {
						String codetoken = results.getString("Token");
						apis.add(codetoken);
					}
					// storing results in the HashMap
					tokenmap.put(texttoken, apis);
				}
				conn.close();
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
