import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SubmitTicketServlet")
public class SubmitTicketServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Get form data
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String issueType = request.getParameter("issue-type");
        String message = request.getParameter("message");

        PrintWriter out = response.getWriter();

        // Database connection variables
        String jdbcURL = "jdbc:mysql://localhost:3306/gocoder"; // Change database name if needed
        String dbUser = "root"; // Change if needed
        String dbPassword = "root"; // Change your MySQL password

        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to database
            Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

            // Insert query
            String sql = "INSERT INTO tickets (name, email, issue_type, message) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, issueType);
            stmt.setString(4, message);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                out.println("<script>alert('Ticket Submitted Successfully!');window.location.href='support.html';</script>");
            } else {
                out.println("<script>alert('Error submitting ticket. Try again!');window.location.href='support.html';</script>");
            }

            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<script>alert('Database Error! Contact support.');window.location.href='support.html';</script>");
        }
    }
}
