import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/UpdateBidStatusServlet")
public class UpdateBidStatusServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        Connection con = null;
        PreparedStatement ps = null;
        JSONObject jsonResponse = new JSONObject();

        try {
            int bidId = Integer.parseInt(request.getParameter("bid_id"));
            String status = request.getParameter("status"); // "Active" or "Inactive"

            // Establishing a database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = JDBCApp.getConnection();

            // SQL query to update the bid status
            String query = "UPDATE bids SET status = ? WHERE bid_id = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, status);
            ps.setInt(2, bidId);

            int rowsUpdated = ps.executeUpdate();
            
            if (rowsUpdated > 0) {
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Bid status updated successfully.");
            } else {
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "No bid found with the given ID.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", e.getMessage());
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (Exception e) { e.printStackTrace(); }
        }

        out.print(jsonResponse);
        out.flush();
    }
}
