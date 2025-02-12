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
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String selectedRole = request.getParameter("role");

        try {
            // Database Connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/gocoder", "root", "root");

            // Query to check user credentials
            String query = "SELECT name, role FROM users WHERE email = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String roleFromDB = rs.getString("role");

                if (roleFromDB.equalsIgnoreCase(selectedRole)) {
                    // Store user details in session
                    HttpSession session = request.getSession();
                    session.setAttribute("userEmail", email);
                    session.setAttribute("userName", name);
                    session.setAttribute("userRole", roleFromDB);

                    // Redirect to the appropriate dashboard
                    if ("coder".equalsIgnoreCase(roleFromDB)) {
                        response.sendRedirect("coderdashboard.html");
                    } else if ("customer".equalsIgnoreCase(roleFromDB)) {
                        response.sendRedirect("customerdashboard.html");
                    } else if ("admin".equalsIgnoreCase(roleFromDB)) {
                        response.sendRedirect("admindashboard.html");
                    }
                } else {
                    // Role mismatch
                    response.sendRedirect("login.html?error=Role mismatch. Please select the correct role.");
                }
            } else {
                // Invalid credentials
                response.sendRedirect("login.html?error=Invalid email or password.");
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.html?error=Database error.");
        }
    }
}
