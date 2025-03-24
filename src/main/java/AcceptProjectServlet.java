import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AcceptProject")
public class AcceptProjectServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain"); 
        String projectId = request.getParameter("id");

        if (projectId == null || projectId.trim().isEmpty()) {
            response.getWriter().write("Invalid project ID");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            
            conn = JDBCApp.getConnection();

            String sql = "UPDATE projects SET visibility = 'Public' WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(projectId));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                response.getWriter().write("success");
            } else {
                response.getWriter().write("No project found with ID: " + projectId);
            }

        } catch (SQLException e) {
            response.getWriter().write("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            response.getWriter().write("Invalid ID format");
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