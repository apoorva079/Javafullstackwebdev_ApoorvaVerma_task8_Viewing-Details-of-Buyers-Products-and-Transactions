import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
  
    private static final String URL = "jdbc:mysql://localhost:3306/your_database_name";
    private static final String USER = "your_mysql_username";
    private static final String PASSWORD = "your_mysql_password";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Status: Connected to MySQL database!");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: JDBC Driver not found. Check 'lib' folder.");
        } catch (SQLException e) {
            System.err.println("Error: Connection Failed! Check MySQL Server Status & Credentials.");
        }
        return connection;
    }
}
