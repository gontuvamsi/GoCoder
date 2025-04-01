import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/GetProjectDetailsServlet")
public class GetProjectDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        String projectId = request.getParameter("project_id");

        if (projectId == null || projectId.trim().isEmpty()) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Project ID is required");
            out.print(jsonResponse.toString());
            return;
        }

        try {
            
            try (Connection conn = JDBCApp.getConnection()) {
                if (conn == null) {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Database connection failed");
                    out.print(jsonResponse.toString());
                    return;
                }

                String sql = "SELECT title, description, technology, category, deadline, min_budget, max_budget " +
                             "FROM projects WHERE id = ? AND status = 'active'";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(projectId));

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    jsonResponse.put("status", "success");
                    jsonResponse.put("title", rs.getString("title"));
                    jsonResponse.put("description", rs.getString("description"));
                    jsonResponse.put("technology", rs.getString("technology"));
                    jsonResponse.put("category", rs.getString("category"));
                    jsonResponse.put("deadline", rs.getString("deadline"));
                    jsonResponse.put("min_budget", rs.getString("min_budget"));
                    jsonResponse.put("max_budget", rs.getString("max_budget"));
                } else {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Project not found or inactive");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Server error: " + e.getMessage());
        }

        out.print(jsonResponse.toString());
        out.flush();
    }
}