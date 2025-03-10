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

@WebServlet("/AdminUserServlet")
public class AdminUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/gocoder", "root", "root");

            
            String query = "SELECT id, name, role, status FROM users";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            
            StringBuilder tableRows = new StringBuilder();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String role = rs.getString("role");
                String status = rs.getString("status");

                tableRows.append("<tr>");
                tableRows.append("<th scope='row'>" + id + "</th>");
                tableRows.append("<td>" + name + "</td>");
                tableRows.append("<td>" + role + "</td>");
                
                
                String statusClass = status.equals("Active") ? "text-success" : "text-danger";
                tableRows.append("<td class='" + statusClass + "'>" + status + "</td>");

                
                tableRows.append("<td>");
                if (status.equals("Active")) {
                    tableRows.append("<button class='btn btn-danger btn-sm' onclick='updateStatus(" + id + ", \"Inactive\")'><i class='fas fa-times'></i></button>");
                } else {
                    tableRows.append("<button class='btn btn-success btn-sm' onclick='updateStatus(" + id + ", \"Active\")'><i class='fas fa-check'></i></button>");
                }
                tableRows.append("</td>");
                tableRows.append("</tr>");
            }

            con.close();
            out.print(tableRows.toString());
        } catch (Exception e) {
            e.printStackTrace();
            out.print("<tr><td colspan='5'>Error loading users</td></tr>");
        }
    }
}
