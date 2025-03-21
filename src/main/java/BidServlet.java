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

@WebServlet("/GetBids")
public class BidServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String userId = request.getParameter("user_id");
        if (userId == null || userId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Missing user_id parameter\"}");
            return;
        }
        
        JSONArray jsonArray = new JSONArray();
        try (Connection con = JDBCApp.getConnection()) {
            String query = "SELECT bids.id , bids.project_id ,projects.title, users.name, bids.bid_amt, bids.completion_date, bids.bid_desc, bids.status "
                         + "FROM bids "
                         + "JOIN users ON bids.user_id = users.id "
                         + "JOIN projects ON bids.project_id = projects.id "
                         + "WHERE bids.user_id = ? and bids.status = 'pending'";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(userId));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                
                obj.put("bids_id", rs.getInt("id"));obj.put("project_id", rs.getInt("project_id"));
                obj.put("title", rs.getString("title"));
                obj.put("name", rs.getString("name"));
                obj.put("bid_amt", rs.getDouble("bid_amt"));
                obj.put("completion_date", rs.getDate("completion_date").toString());
                obj.put("bid_desc", rs.getString("bid_desc"));
                obj.put("status", rs.getString("status"));
                jsonArray.put(obj);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"Database error occurred\"}");
            return;
        }
        
        out.print(jsonArray.toString());
        out.flush();
    }
}
