import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
@MultipartConfig()
@WebServlet("/GetMyProjects")
public class GetMyProjectsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database connection details (update these based on your setup)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gocoder";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set response content type to JSON
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get user_id from the request
        String userId = request.getParameter("user_id");
        if (userId == null || userId.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"user_id is required\"}");
            out.flush();
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish database connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare SQL query
            String sql = "SELECT project_id, project_name, project_status FROM project_status WHERE status_submitted_by = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(userId));

            // Execute query
            rs = stmt.executeQuery();

            // Build JSON response
            JSONArray projectsArray = new JSONArray();
            while (rs.next()) {
                JSONObject project = new JSONObject();
                project.put("project_id", rs.getInt("project_id"));
                project.put("project_name", rs.getString("project_name"));
                project.put("project_status", rs.getString("project_status"));
                projectsArray.put(project);
            }

            // Send JSON response
            out.print(projectsArray.toString());
            out.flush();

        } catch (ClassNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Database driver not found\"}");
            out.flush();
            e.printStackTrace();
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Database error occurred\"}");
            out.flush();
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"An unexpected error occurred\"}");
            out.flush();
            e.printStackTrace();
        } finally {
            // Clean up resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}