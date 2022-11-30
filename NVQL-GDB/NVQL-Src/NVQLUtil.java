import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;

public final class NVQLUtil {

    private NVQLUtil() { // private constructor prvents instantiation of the class

    }

    public static void f() {
        g(); // g() can only be accessed by other methods inside the class only
    }

    private static void g() {

    }

    public static ResultSet execQuery(Connection pgSQLConn, String query) {
        ResultSet result = null;
        try {
            PreparedStatement stmt = pgSQLConn.prepareStatement(query);
            result = stmt.executeQuery();
        } catch (Exception e) {

        }
        return result;
    }

    public static int countRows(Connection pgSQLConn, String tableName) {
        // String query = "select count(*) from " + tableName;
        String query = "select distinct * from " + tableName;
        int count = 0;
        ResultSet result = null;
        try {
            PreparedStatement stmt = pgSQLConn.prepareStatement(query);
            result = stmt.executeQuery();
            // result.next(); System.out.println("@"+query);
            while (result.next()) {
                count++;
            }
            // count = result.getInt(1);
        } catch (Exception e) {

        }
        return count;
    }

    public static Connection getPGSQLConnection(String host, int port, String DBName, String uname, String passwd) {
        Connection pgSQLConn = null;

        String conString = "jdbc:postgresql://" + host + ":" + port + "/" + DBName;
        try {

            pgSQLConn = DriverManager.getConnection(conString, uname, passwd);

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }

        if (pgSQLConn != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }
        return pgSQLConn;
    }
}