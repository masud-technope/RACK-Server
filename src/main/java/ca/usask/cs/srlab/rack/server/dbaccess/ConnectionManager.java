package ca.usask.cs.srlab.rack.server.dbaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ca.usask.cs.srlab.rack.server.config.StaticData;

public class ConnectionManager {

    public static Connection conn = null;


    public static Connection getConnection() {
        try {
            if (conn == null) {
                conn = DriverManager.getConnection(StaticData.connectionString);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }
}
