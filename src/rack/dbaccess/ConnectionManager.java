package rack.dbaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import rack.config.StaticData;

public class ConnectionManager {

	public static Connection conn = null;

	public static Connection getConnection() {
		try {
			if (conn == null) {
				/*********** SQL Server connection **************/
				// Class.forName(StaticData.Driver_name).newInstance();
				// conn =
				// DriverManager.getConnection(StaticData.connectionString);

				/*********** SQLite connection ******************/
				conn = DriverManager.getConnection(StaticData.connectionString);
			}
		} catch (Exception exc) {
			// handle the exception
			exc.printStackTrace();
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return conn;
	}
}
