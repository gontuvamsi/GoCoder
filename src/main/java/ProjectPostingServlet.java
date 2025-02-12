import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ProjectPostingServlet")
public class ProjectPostingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String technology = request.getParameter("technology");
        String category = request.getParameter("category");
        String deadline = request.getParameter("deadline");
        String minBudget = request.getParameter("minBudget");
        String maxBudget = request.getParameter("maxBudget");
        String visibility = request.getParameter("visibility");
        String postedBy = request.getParameter("postedBy");

        Connection conn = null;
        PreparedStatement pstmt = null;
        String dbURL = "jdbc:mysql://localhost:3306/gocoder"; // Change database details
        String dbUser = "root"; // Change username
        String dbPassword = "root"; // Change password

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);

            String sql = "INSERT INTO projects (title, description, technology, category, deadline, min_budget, max_budget, visibility, posted_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, technology);
            pstmt.setString(4, category);
            pstmt.setString(5, deadline);
            pstmt.setString(6, minBudget);
            pstmt.setString(7, maxBudget);
            pstmt.setString(8, visibility);
            pstmt.setString(9, postedBy);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                response.getWriter().write("success");
            } else {
                response.getWriter().write("failure");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("error");
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
