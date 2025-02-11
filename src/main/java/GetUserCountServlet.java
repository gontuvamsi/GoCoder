import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GetUserCountServlet")
public class GetUserCountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/gocoder", "root", "root");

            // Query to count total users
            String query = "SELECT COUNT(*) AS userCount FROM users";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            // Fetch user count from the result
            int userCount = 0;
            if (rs.next()) {
                userCount = rs.getInt("userCount");
            }

            con.close();

            // Send the result back as plain text
            response.getWriter().print(userCount);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("Error fetching user count.");
        }
    }
}
