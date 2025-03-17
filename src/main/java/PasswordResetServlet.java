import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/PasswordResetServlet")
public class PasswordResetServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gocoder";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String newPassword = request.getParameter("new-password");
        String confirmPassword = request.getParameter("confirm-password");

        if (!newPassword.equals(confirmPassword)) {
            response.getWriter().write("<script>alert('Passwords do not match!'); window.location='resetpassword.html';</script>");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Check if email exists
            PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Update password
                PreparedStatement updateStmt = conn.prepareStatement("UPDATE users SET password = ? WHERE email = ?");
                updateStmt.setString(1, newPassword); // Consider hashing for security
                updateStmt.setString(2, email);
                int updated = updateStmt.executeUpdate();

                if (updated > 0) {
                    response.getWriter().write("<script>alert('Password Reset! Login to Your Account'); window.location='login.html';</script>");
                }
            } else {
                response.getWriter().write("<script>alert('Please Register'); window.location='signup.html';</script>");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("<script>alert('An error occurred. Try again later.'); window.location='resetpassword.html';</script>");
        }
    }
}
