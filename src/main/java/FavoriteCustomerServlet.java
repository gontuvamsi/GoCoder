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

@WebServlet("/favoriteCustomer/*") // Updated to handle sub-paths like /check
public class FavoriteCustomerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String ERROR_MISSING_PARAMS = "{\"status\":\"error\",\"message\":\"Missing user_id or customer_id\"}";
    private static final String ERROR_INVALID_FORMAT = "{\"status\":\"error\",\"message\":\"Invalid user_id or customer_id format\"}";
    private static final String ERROR_FAILED = "{\"status\":\"error\",\"message\":\"Operation failed\"}";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        String pathInfo = request.getPathInfo(); // Get the sub-path (e.g., /check)

        try (PrintWriter out = response.getWriter()) {
            if (pathInfo != null && pathInfo.equals("/check")) {
                // Handle the /favoriteCustomer/check endpoint
                handleCheckFavorite(request, response, out);
            } else {
                // Handle the original /favoriteCustomer endpoint
                handleToggleFavorite(request, response, out);
            }
        }
    }

    private void handleToggleFavorite(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws IOException {
        String userIdStr = request.getParameter("user_id");
        String customerIdStr = request.getParameter("customer_id");

        if (userIdStr == null || customerIdStr == null || userIdStr.trim().isEmpty() || customerIdStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(ERROR_MISSING_PARAMS);
            return;
        }

        int userId, customerId;
        try {
            userId = Integer.parseInt(userIdStr.trim());
            customerId = Integer.parseInt(customerIdStr.trim());
            if (userId <= 0 || customerId <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"message\":\"IDs must be positive\"}");
                return;
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(ERROR_INVALID_FORMAT);
            return;
        }

        try (Connection conn = JDBCApp.getConnection()) {
            if (isFavoriteExists(conn, userId, customerId)) {
                if (removeFavorite(conn, userId, customerId)) {
                    out.print("{\"status\":\"success\",\"message\":\"Customer removed from favorites\"}");
                } else {
                    out.print(ERROR_FAILED);
                }
            } else {
                if (addFavorite(conn, userId, customerId)) {
                    out.print("{\"status\":\"success\",\"message\":\"Customer added to favorites\"}");
                } else {
                    out.print(ERROR_FAILED);
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private void handleCheckFavorite(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws IOException {
        String userIdStr = request.getParameter("user_id");
        String customerIdStr = request.getParameter("customer_id");

        if (userIdStr == null || customerIdStr == null || userIdStr.trim().isEmpty() || customerIdStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(ERROR_MISSING_PARAMS);
            return;
        }

        int userId, customerId;
        try {
            userId = Integer.parseInt(userIdStr.trim());
            customerId = Integer.parseInt(customerIdStr.trim());
            if (userId <= 0 || customerId <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"message\":\"IDs must be positive\"}");
                return;
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(ERROR_INVALID_FORMAT);
            return;
        }

        try (Connection conn = JDBCApp.getConnection()) {
            boolean isFavorite = isFavoriteExists(conn, userId, customerId);
            out.print("{\"status\":\"success\",\"isFavorite\":" + isFavorite + "}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private boolean isFavoriteExists(Connection conn, int userId, int customerId) throws SQLException {
        String query = "SELECT COUNT(*) FROM fav_customer WHERE user_id = ? AND customer_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private boolean addFavorite(Connection conn, int userId, int customerId) throws SQLException {
        String query = "INSERT INTO fav_customer (user_id, customer_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, customerId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private boolean removeFavorite(Connection conn, int userId, int customerId) throws SQLException {
        String query = "DELETE FROM fav_customer WHERE user_id = ? AND customer_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, customerId);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        try (PrintWriter out = response.getWriter()) {
            out.print("{\"status\":\"error\",\"message\":\"Method not allowed, use POST\"}");
        }
    }
}