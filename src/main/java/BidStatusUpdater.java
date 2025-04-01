import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/BidStatusUpdater")  
public class BidStatusUpdater extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        
        String bidIdStr = request.getParameter("bid_id");
        String status = request.getParameter("status");
        
        JSONObject jsonResponse = new JSONObject();
        
        try {
            if (bidIdStr == null || status == null || bidIdStr.trim().isEmpty() || status.trim().isEmpty()) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Invalid parameters");
                response.getWriter().write(jsonResponse.toString());
                return;
            }
            
            int bidId = Integer.parseInt(bidIdStr);
            
            String normalizedStatus = status.trim().toLowerCase();
            if (!normalizedStatus.equals("accepted") && !normalizedStatus.equals("rejected") && 
                !normalizedStatus.equals("pending")) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Invalid status value");
                response.getWriter().write(jsonResponse.toString());
                return;
            }
            
            try (Connection conn = JDBCApp.getConnection()) {
                String sql = "UPDATE bids SET status = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, status);
                    pstmt.setInt(2, bidId);
                    
                    int rowsAffected = pstmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        jsonResponse.put("status", "success");
                        jsonResponse.put("message", "Bid status updated successfully");
                    } else {
                        jsonResponse.put("status", "error");
                        jsonResponse.put("message", "Bid not found");
                    }
                }
            }
            
        } catch (NumberFormatException e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Invalid bid ID format");
        } catch (SQLException e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Database error: " + e.getMessage());
        } catch (Exception e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Unexpected error: " + e.getMessage());
        }
        
        response.getWriter().write(jsonResponse.toString());
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }
}