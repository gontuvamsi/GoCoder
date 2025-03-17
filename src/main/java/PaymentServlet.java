import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/PaymentServlet")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Retrieve form data
        String projectId = request.getParameter("project_id");
        String projectName = request.getParameter("project_name");
        String amountPaid = request.getParameter("amount_paid");
        String transactionId = request.getParameter("transaction_id");

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            
            conn = JDBCApp.getConnection();

           
            String sql = "INSERT INTO payments (project_id, project_name, amount_paid, transaction_id) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

           
            pstmt.setInt(1, Integer.parseInt(projectId));
            pstmt.setString(2, projectName);
            pstmt.setBigDecimal(3, new java.math.BigDecimal(amountPaid));
            pstmt.setString(4, transactionId);

            int rowsInserted = pstmt.executeUpdate();

            
            if (rowsInserted > 0) {
                out.print("{\"status\":\"success\"}");
            } else {
                out.print("{\"status\":\"error\", \"message\":\"Failed to save payment.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
