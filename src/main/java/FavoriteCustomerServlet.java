import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/favoriteCustomer")
public class FavoriteCustomerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get parameters from the request
        String userIdStr = request.getParameter("user_id");
        String customerIdStr = request.getParameter("customer_id");

        try {
            // Validate input
            if (userIdStr == null || customerIdStr == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"message\":\"Missing user_id or customer_id\"}");
                return;
            }

            int userId = Integer.parseInt(userIdStr);
            int customerId = Integer.parseInt(customerIdStr);

            // Use JDBCApp for database connection
            try (Connection conn = JDBCApp.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO fav_customer (user_id, customer_id) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE user_id = user_id")) { // Prevents duplicates
                pstmt.setInt(1, userId);
                pstmt.setInt(2, customerId);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    out.print("{\"status\":\"success\"}");
                } else {
                    out.print("{\"status\":\"error\",\"message\":\"No rows affected\"}");
                }
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                if (e.getSQLState().equals("23000")) { // Duplicate entry
                    out.print("{\"status\":\"error\",\"message\":\"Customer already marked as favorite\"}");
                } else {
                    out.print("{\"status\":\"error\",\"message\":\"Database error: " + e.getMessage() + "\"}");
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"message\":\"Invalid user_id or customer_id format\"}");
        } finally {
            out.flush();
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.getWriter().print("{\"status\":\"error\",\"message\":\"GET method not supported\"}");
    }
}