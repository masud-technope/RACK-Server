package dbaccess;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import config.StaticData;

public class TokenMapSaver {

	String itemset;

	public TokenMapSaver(String itemset) {
		// default constructor
		this.itemset = StaticData.EXP_HOME + "/dbextension/" + itemset;
	}

	protected void addTokens(int entryID, ArrayList<String> textTokens,
			ArrayList<String> codeTokens) {
		// adding text tokens and code tokens
		Connection conn = null;
		try {
			Class.forName(StaticData.Driver_name).newInstance();
			conn = DriverManager.getConnection(StaticData.connectionString);
			if (conn != null) {
				conn.setAutoCommit(false);
				// queries
				String addTextToken = "insert into TextToken(EntryID,Token) values(?,?)";
				String addCodeToken = "insert into CodeToken(EntryID,Token) values(?,?)";

				PreparedStatement addTextTokenStmt = conn
						.prepareStatement(addTextToken);
				PreparedStatement addCodeTokenStmt = conn
						.prepareStatement(addCodeToken);

				for (String token : textTokens) {
					addTextTokenStmt.setInt(1, entryID);
					addTextTokenStmt.setString(2, token.trim());
					addTextTokenStmt.executeUpdate();
				}

				for (String token : codeTokens) {
					addCodeTokenStmt.setInt(1, entryID);
					addCodeTokenStmt.setString(2, token.trim());
					addCodeTokenStmt.executeUpdate();
				}
				conn.commit();
				conn.close();
				System.out.println("Done: "+entryID);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected void collectTokens()
	{
		//collecting tokens from the file
		try{
			Scanner scanner=new Scanner(new File(this.itemset));
			int entry=0;
			while(scanner.hasNextLine()){
				String line=scanner.nextLine();
				String[] parties=line.split(":");
				String[] texts=parties[0].split("\\s+");
				ArrayList<String> textTokens=new ArrayList<>();
				for(String token:texts){
					if(token.length()>2){
						textTokens.add(token);
					}
				}
				String[] codes=parties[1].split("\\s+");
				ArrayList<String> codeTokens=new ArrayList<>();
				for(String token:codes){
					if(token.length()>2){
						codeTokens.add(token);
					}
				}
				
				//entry increments
				entry++;
				
				//now add them to database
				addTokens(entry, textTokens, codeTokens);
				
				//break;	
			}
			scanner.close();
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}
	
	protected void testDBAcces() {
		ArrayList<String> texts = new ArrayList<>();
		ArrayList<String> codes = new ArrayList<>();
		texts.add("map");
		texts.add("list");
		codes.add("HashMap");
		codes.add("ArrayList");
		addTokens(1, texts, codes);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long start=System.currentTimeMillis();
		TokenMapSaver saver=new TokenMapSaver("itemset-all-ext.txt");
		//saver.testDBAcces();
		saver.collectTokens();
		long end=System.currentTimeMillis();
		System.out.println( "Time elapsed:" +(end-start)/1000 +"s");
	}
}
