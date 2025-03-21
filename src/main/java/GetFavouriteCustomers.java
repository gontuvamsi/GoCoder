import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/GetFavouriteCustomers")
public class GetFavouriteCustomers extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String userId = request.getParameter("user_id");

        if (userId == null || userId.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"User ID is required\"}");
            out.flush();
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<JSONObject> customers = new ArrayList<>();

        try {
            // Assuming JDBCApp.java has a static method getConnection() to get DB connection
            conn = JDBCApp.getConnection();

            String sql = "SELECT users.id, users.name, users.email " +
                         "FROM users " +
                         "INNER JOIN fav_customer ON users.id = fav_customer.customer_id " +
                         "WHERE fav_customer.user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                JSONObject customer = new JSONObject();
                customer.put("id", rs.getString("id"));
                customer.put("name", rs.getString("name"));
                customer.put("email", rs.getString("email"));
                customers.add(customer);
            }

            JSONArray jsonArray = new JSONArray(customers);
            out.print(jsonArray.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Database error occurred\"}");
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        out.flush();
    }
}