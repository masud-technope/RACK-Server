package config;

public class StaticData {
	//public static String EXP_HOME =
	//"C:/MyWorks/Thesis Works/Crowdsource_Knowledge_Base/CodeTokenRec/experiment";
	public static String EXP_HOME = "C:/My MSc/ThesisWorks/Crowdsource_Knowledge_Base/CodeTokenRec/experiment";
	public static String EVA_HOME = EXP_HOME + "/evaluation/175";

	public static String EXP_HOME_BDA = "C:/My MSc/ThesisWorks/BigData_Code_Search/CodeSearchBDA/experiment";
	//public static String EXP_HOME_BDA="F:/MyWorks/Thesis Works/Crowdsource_Knowledge_Base/CodeSearchBDA/experiment";

	//public static String FASTTEXT_DIR
	 //="F:/MyWorks/Thesis Works/Data_Mining_Works/W2Vec/fasttxt/fastText-0.1.0";
	public static String FASTTEXT_DIR = "C:/My MSc/ThesisWorks/BigData_Code_Search/w2vec/fastText";
	public static String BAT_FILE_PATH = "C:/tmp/w2vecRunner.bat";
	public static String BAT_FILE_PATH2 = "C:/tmp/w2wSim.bat";
	public static String BAT_FILE_PATH3 = "C:/tmp/printsentence.bat";

	public static String IJDATASET_HOME = "F:/MyWorks/MyBigData/IJDataset";
	static String Database_name = "CodeTokenRecEMSE2";

	public static String ORACLE_FILE = "oracle-175-ext.txt";

	//public static String connectionString = "jdbc:sqlserver://localhost:1433;databaseName="
	//		+ Database_name + ";integratedSecurity=true";
	//public static String Driver_name = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	public static int MAXAPI = 10;
	public static int DELTA1 = 10;
	public static int DELTA2 = 10;

	public static double alpha = 0.325;
	public static double beta = 0.575;
	public static double psi = 0.10;

	public static double gamma = 0;
	
	public static int GOLDSET_SIZE=10;
	
	public static String connectionString="jdbc:sqlite:"+System.getProperty("user.dir")+"/database/RACK-EMSE.db";

}
