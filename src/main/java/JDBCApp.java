
import java.sql.Connection;
import java.sql.DriverManager;

public class JDBCApp {

	public static Connection getConnection() {
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/gocoder", "root", "root");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

}
