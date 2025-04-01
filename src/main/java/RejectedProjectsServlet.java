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

@WebServlet("/RejectedProjects")
public class RejectedProjectsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = JDBCApp.getConnection();
            String sql = "SELECT * FROM projects WHERE status = 'inactive'";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            out.println("<html><body>");
            out.println("<h2>Rejected Projects</h2>");
            out.println("<table border='1'>");
            out.println("<tr>" +
                        "<th>ID</th>" +
                        "<th>User ID</th>" +
                        "<th>Title</th>" +
                        "<th>Description</th>" +
                        "<th>Technology</th>" +
                        "<th>Category</th>" +
                        "<th>Deadline</th>" +
                        "<th>Min Budget</th>" +
                        "<th>Max Budget</th>" +
                        "<th>Visibility</th>" +
                        "<th>Status</th>" +
                        "<th>Created At</th>" +
                        "</tr>");

            while (rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("user_id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String technology = rs.getString("technology");
                String category = rs.getString("category");
                String deadline = rs.getString("deadline");
                double minBudget = rs.getDouble("min_budget");
                double maxBudget = rs.getDouble("max_budget");
                String visibility = rs.getString("visibility");
                String status = rs.getString("status");
                String createdAt = rs.getString("created_at");

                out.println("<tr>" +
                            "<td>" + id + "</td>" +
                            "<td>" + userId + "</td>" +
                            "<td>" + title + "</td>" +
                            "<td>" + description + "</td>" +
                            "<td>" + technology + "</td>" +
                            "<td>" + category + "</td>" +
                            "<td>" + deadline + "</td>" +
                            "<td>" + minBudget + "</td>" +
                            "<td>" + maxBudget + "</td>" +
                            "<td>" + visibility + "</td>" +
                            "<td>" + status + "</td>" +
                            "<td>" + createdAt + "</td>" +
                            "</tr>");
            }

            out.println("</table>");
            out.println("</body></html>");

        } catch (SQLException e) {
            out.println("<html><body><h3>Error: " + e.getMessage() + "</h3></body></html>");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                JDBCApp.closeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String projectId = request.getParameter("id");
        String action = request.getParameter("action");

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = JDBCApp.getConnection();
            
            if ("accept".equals(action)) {
                
                String sql = "UPDATE projects SET visibility = 'Public', status = 'active' WHERE id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(projectId));
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    out.write("success");
                } else {
                    out.write("Project not found");
                }
            } else {
                out.write("Invalid action");
            }
            
        } catch (SQLException e) {
            out.write("Database error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                JDBCApp.closeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}