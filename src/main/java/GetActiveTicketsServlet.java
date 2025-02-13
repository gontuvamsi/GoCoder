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

@WebServlet("/GetActiveTicketsServlet")
public class GetActiveTicketsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        
        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/gocoder", "root", "root");
            
            // Query to count only "open" tickets
            String sql = "SELECT COUNT(*) FROM tickets WHERE status = 'open'";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                out.print(count); // Send the count as response
            } else {
                out.print("0"); // If no open tickets found, return 0
            }
            
            // Close resources
            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            out.print("Error"); // In case of an error
            e.printStackTrace();
        }
    }
}
