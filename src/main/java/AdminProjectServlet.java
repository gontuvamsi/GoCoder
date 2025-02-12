import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AdminProjectServlet")
public class AdminProjectServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/gocoder", "root", "root");

            // SQL Query
            String query = "SELECT * FROM projects";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            // Loop through projects and generate HTML table rows
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getString("title") + "</td>");
                out.println("<td>" + rs.getString("description") + "</td>");
                out.println("<td>" + rs.getString("technology") + "</td>");
                out.println("<td>" + rs.getString("category") + "</td>");
                out.println("<td>" + rs.getDate("deadline") + "</td>");
                out.println("<td>$" + rs.getDouble("min_budget") + " - $" + rs.getDouble("max_budget") + "</td>");
                out.println("<td>" + rs.getString("visibility") + "</td>");
                out.println("<td>" + rs.getString("posted_by") + "</td>");
                out.println("<td><button class='btn btn-danger btn-sm' onclick='deleteProject(" + rs.getInt("id") + ")'>Delete</button></td>");
                out.println("</tr>");
            }

            // Close resources
            rs.close();
            pst.close();
            con.close();
        } catch (Exception e) {
            out.println("<tr><td colspan='10'>Error loading projects: " + e.getMessage() + "</td></tr>");
        }
    }
}
