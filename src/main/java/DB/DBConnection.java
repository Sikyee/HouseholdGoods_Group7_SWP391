package DB;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() throws Exception {
        String url = "jdbc:sqlserver://123TTT:1433;databaseName=SWP391_DB_Group7;encrypt=false";
        String user = "sa";
        String pass = "2004@Tri";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(url, user, pass);
    }
}

