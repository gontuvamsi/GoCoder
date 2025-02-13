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

@WebServlet("/AdminTicketServlet")
public class AdminTicketServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/gocoder", "root", "root");
            String query = "SELECT * FROM tickets";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            StringBuilder tableRows = new StringBuilder();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String issueType = rs.getString("issue_type");
                String message = rs.getString("message");
                String status = rs.getString("status");

                tableRows.append("<tr>");
                tableRows.append("<td>").append(id).append("</td>");
                tableRows.append("<td>").append(name).append("</td>");
                tableRows.append("<td>").append(email).append("</td>");
                tableRows.append("<td>").append(issueType).append("</td>");
                tableRows.append("<td>").append(message).append("</td>");
                tableRows.append("<td>").append(status.equals("open") ? "<span class='text-danger'>Open</span>" : "<span class='text-success'>Closed</span>").append("</td>");
                
                // Action Button
                if (status.equals("open")) {
                    tableRows.append("<td><button class='btn btn-sm btn-danger' onclick='updateTicketStatus(")
                        .append(id).append(")'>Close</button></td>");
                } else {
                    tableRows.append("<td><button class='btn btn-sm btn-secondary' disabled>Closed</button></td>");
                }

                tableRows.append("</tr>");
            }

            out.print(tableRows.toString());
            con.close();
        } catch (Exception e) {
            out.print("<tr><td colspan='7' class='text-center text-danger'>Error loading tickets</td></tr>");
            e.printStackTrace();
        }
    }
}
