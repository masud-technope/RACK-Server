package config;

public class StaticData {
	//public static String EXP_HOME="C:/MyWorks/Thesis Works/Crowdsource_Knowledge_Base/CodeTokenRec/experiment";
	public static String EXP_HOME="C:/My MSc/ThesisWorks/Crowdsource_Knowledge_Base/CodeTokenRec/experiment";
	public static String EVA_HOME=EXP_HOME+"/evaluation/175";
	static String Database_name= "CodeTokenRec";//"stackoverflow2014p3";//  "vendasta";
	public static String connectionString="jdbc:sqlserver://localhost:1433;databaseName="+Database_name+";integratedSecurity=true";
	public static String Driver_name="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static int MAXAPI=10;
}
