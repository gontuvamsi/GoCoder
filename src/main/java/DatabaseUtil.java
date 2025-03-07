import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/gocoder"; // Replace todo_db with your actual database name
    private static final String JDBC_USER = "root"; // Replace with your MySQL username
    private static final String JDBC_PASSWORD = "root"; // Replace with your MySQL password

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load the MySQL driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL JDBC driver not found.", e); // Rethrow as SQLException
        }
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
}