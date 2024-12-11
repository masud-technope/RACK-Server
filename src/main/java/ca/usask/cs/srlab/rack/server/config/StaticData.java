package ca.usask.cs.srlab.rack.server.config;

public class StaticData {

	public static String HOME_DIR = "C:\\MyWorks\\MyResearch\\CodeSearch\\RACK\\RACK-Server\\src\\main\\resources";
	public static String STOPWORD_DIR = HOME_DIR + "/pp-data";
	public static String MAX_ENT_MODEL_DIR = HOME_DIR + "/models";
	
	public static int MAXAPI = 10;
	public static int DELTA1 = 10;
	public static int DELTA2 = 10;

	public static double alpha = 0.325;
	public static double beta = 0.575;
	public static double psi = 0.10;
	public static double gamma = 0;
	public static int GOLDSET_SIZE = 10;

	public static String connectionString = "jdbc:sqlite:"
			+ HOME_DIR + "/rack.database/RACK-EMSE.db";

}
