package bdd.connectionJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Stéfan on 08/11/2015.
 */
public class ConnectionBDD {
    private static String url = "jdbc:postgresql://localhost:5432/twitterMiage";

    private static String user = "postgres";

    private static String passwd = "twitter";

    private static Connection connect;

    /**
     * Returns database connection instance (and creates it if it doesn't exist)
     * 	 * @return Database connection link
     */
    public static Connection getInstance(){
        if(connect == null){
            try {
                connect = DriverManager.getConnection(url, user, passwd);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connect;
    }
}
