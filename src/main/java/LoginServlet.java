import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            Connection con = JDBCApp.getConnection();
            
            // Check if email exists first
            String emailCheckQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement emailCheckPs = con.prepareStatement(emailCheckQuery);
            emailCheckPs.setString(1, email);
            ResultSet emailRs = emailCheckPs.executeQuery();
            
            if (emailRs.next() && emailRs.getInt(1) == 0) {
                // Email does not exist
                response.sendRedirect("login.html?error=Invalid email");
                emailCheckPs.close();
                emailRs.close();
                con.close();
                return; // Exit early
            }
            
            // Email exists, now check password
            String query = "SELECT id, name, role FROM users WHERE email = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Successful login
                int userId = rs.getInt("id");
                String name = rs.getString("name");
                String roleFromDB = rs.getString("role");

                HttpSession session = request.getSession();
                session.setAttribute("userId", userId);
                session.setAttribute("userEmail", email);
                session.setAttribute("userName", name);
                session.setAttribute("userRole", roleFromDB);

                switch (roleFromDB.toLowerCase()) {
                    case "coder":
                        response.sendRedirect("coderdashboard.html");
                        break;
                    case "customer":
                        response.sendRedirect("customerdashboard.html");
                        break;
                    case "admin":
                        response.sendRedirect("admindashboard.html");
                        break;
                    default:
                        response.sendRedirect("login.html?error=Unknown role. Contact admin.");
                }
            } else {
                // Email exists but password is wrong
                response.sendRedirect("login.html?error=Invalid password");
            }

            rs.close();
            ps.close();
            emailRs.close();
            emailCheckPs.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.html?error=Database error.");
        }
    }
}