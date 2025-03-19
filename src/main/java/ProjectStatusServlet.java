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

@WebServlet("/ProjectStatusServlet")
public class ProjectStatusServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            Connection conn = JDBCApp.getConnection(); // Using JDBCApp.java for connection
            String query = "SELECT * FROM project_status";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            
            out.println("<table class='table table-bordered table-striped'>");
            out.println("<thead class='thead-dark'><tr>");
            //out.println("<th>Project ID</th><th>Project Name</th><th>Project Status</th><th>Submitted By</th>");
            out.println("</tr></thead><tbody>");
            
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("project_id") + "</td>");
                out.println("<td>" + rs.getString("project_name") + "</td>");
                out.println("<td>" + rs.getString("project_status") + "</td>");
                out.println("<td>" + rs.getInt("status_submitted_by") + "</td>");
                out.println("</tr>");
            }
            out.println("</tbody></table>");

            conn.close();
        } catch (SQLException e) {
            out.println("<p class='text-danger'>Error fetching data: " + e.getMessage() + "</p>");
        }
    }
}
