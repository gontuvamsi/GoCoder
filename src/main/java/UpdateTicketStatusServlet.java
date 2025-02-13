import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UpdateTicketStatusServlet")
public class UpdateTicketStatusServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int ticketId = Integer.parseInt(request.getParameter("ticketId"));

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/gocoder", "root", "root");

            String query = "UPDATE tickets SET status = 'closed' WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, ticketId);

            int updatedRows = ps.executeUpdate();
            if (updatedRows > 0) {
                response.getWriter().write("Ticket closed successfully");
            } else {
                response.getWriter().write("Error closing ticket");
            }

            con.close();
        } catch (Exception e) {
            response.getWriter().write("Error closing ticket");
            e.printStackTrace();
        }
    }
}
