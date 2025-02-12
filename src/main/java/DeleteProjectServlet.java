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

@WebServlet("/DeleteProjectServlet")
public class DeleteProjectServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        int projectId = Integer.parseInt(request.getParameter("projectId"));

        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/gocoder", "root", "root");

            // Delete project query
            String query = "DELETE FROM projects WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, projectId);
            int result = pst.executeUpdate();

            if (result > 0) {
                out.print("Project deleted successfully.");
            } else {
                out.print("Failed to delete project.");
            }

            // Close resources
            pst.close();
            con.close();
        } catch (Exception e) {
            out.print("Error: " + e.getMessage());
        }
    }
}
