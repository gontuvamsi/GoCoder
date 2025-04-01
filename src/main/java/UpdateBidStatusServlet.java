import java.io.BufferedReader;
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
import org.json.JSONObject;

@WebServlet("/UpdateBidStatusServlet")
public class UpdateBidStatusServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        System.out.println("Raw Request Body: " + sb.toString());

        JSONObject requestBody = new JSONObject(sb.toString());
        String bidIdParam = requestBody.optString("bid_id", null);
        String status = requestBody.optString("status", null);

        System.out.println("Received bid_id: " + bidIdParam);
        System.out.println("Received status: " + status);

        if (bidIdParam == null || bidIdParam.trim().isEmpty() || status == null || status.trim().isEmpty()) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Invalid input: bid_id or status is missing");
            out.print(jsonResponse.toString());
            out.flush();
            return;
        }

        int bidId;
        try {
            bidId = Integer.parseInt(bidIdParam);
        } catch (NumberFormatException e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Invalid bid_id format");
            out.print(jsonResponse.toString());
            out.flush();
            return;
        }

        if (!status.equalsIgnoreCase("Accepted") && !status.equalsIgnoreCase("Rejected")) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Invalid status value");
            out.print(jsonResponse.toString());
            out.flush();
            return;
        }

        try (Connection conn = JDBCApp.getConnection()) {
            
            String checkQuery = "SELECT COUNT(*) FROM bids WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, bidId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Bid ID not found in database");
                    out.print(jsonResponse.toString());
                    out.flush();
                    return;
                }
            }

            
            String sql = "UPDATE bids SET status = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, status);
                pstmt.setInt(2, bidId);
                
                int rowsUpdated = pstmt.executeUpdate();
                System.out.println("Rows updated: " + rowsUpdated);
                
                if (rowsUpdated > 0) {
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "Bid status updated successfully");
                } else {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "No matching bid found");
                }
            }
        } catch (SQLException e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
}
