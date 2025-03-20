import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UserProjectReport")
public class UserProjectReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Set response content type
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Database connection variables
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Get connection from JDBCApp
            conn = JDBCApp.getConnection();  // Assuming getConnection() is a static method in JDBCApp

            // SQL query
            String sql = "SELECT u.id, u.name, u.email, " +
                        "COUNT(ps.project_id) AS completed_projects " +
                        "FROM users u " +
                        "LEFT JOIN project_status ps ON u.id = ps.status_submitted_by " +
                        "AND ps.project_status = 'Completed' " +
                        "WHERE u.role = 'Coder' " +
                        "GROUP BY u.id, u.name, u.email " +
                        "ORDER BY u.id";

            // Prepare and execute statement
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            // HTML output
            out.println("<html><body>");
            out.println("<h2>User Project Report</h2>");
            out.println("<table border='1'>");
            out.println("<tr>");
            out.println("<th>ID</th>");
            out.println("<th>Name</th>");
            out.println("<th>Email</th>");
            out.println("<th>Completed Projects</th>");
            out.println("</tr>");

            // Process result set
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                int completedProjects = rs.getInt("completed_projects");

                out.println("<tr>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + name + "</td>");
                out.println("<td>" + email + "</td>");
                out.println("<td>" + completedProjects + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</body></html>");

        } catch (SQLException e) {
            out.println("<html><body>");
            out.println("<h2>Error retrieving user project report</h2>");
            out.println("<p>Error: " + e.getMessage() + "</p>");
            out.println("</body></html>");
            e.printStackTrace();
        } finally {
            // Clean up resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            out.close();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}