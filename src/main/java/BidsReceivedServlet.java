import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/BidsReceivedServlet")
public class BidsReceivedServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        JSONArray bidArray = new JSONArray();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = JDBCApp.getConnection();
            String query = "SELECT \r\n"
            		+ "    projects.id,\r\n"
            		+ "    projects.title,\r\n"
            		+ "    users.name AS Coder_Name,\r\n"
            		+ "    bids.user_id AS Coder_Id,\r\n"
            		+ "    bids.*\r\n"
            		+ "FROM\r\n"
            		+ "    bids\r\n"
            		+ "        JOIN\r\n"
            		+ "    projects ON bids.project_id = projects.id\r\n"
            		+ "        JOIN\r\n"
            		+ "    users ON bids.user_id = users.id\r\n"
            		+ "    where projects.user_id = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, "active");
            rs = ps.executeQuery();

            while (rs.next()) {
                JSONObject bid = new JSONObject();
                bid.put("project_id", rs.getInt("project_id"));
                bid.put("user_id", rs.getInt("user_id"));
                bid.put("bid_amt", rs.getDouble("bid_amt"));
                bid.put("completion_date", rs.getDate("completion_date"));
                bid.put("bid_desc", rs.getString("bid_desc"));
                bid.put("status", rs.getString("status"));
                bid.put("created_at", rs.getTimestamp("created_at"));
                

                bidArray.put(bid);
            }

            JSONObject result = new JSONObject();
            result.put("status", "success");
            result.put("bids", bidArray);
            out.print(result);

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", e.getMessage());
            out.print(error);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (Exception e) { e.printStackTrace(); }
        }

        out.flush();
    }
}