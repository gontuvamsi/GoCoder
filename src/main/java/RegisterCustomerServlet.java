import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/registerCustomer")
public class RegisterCustomerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        
        String jdbcURL = "jdbc:mysql://localhost:3306/gocoder";
        String jdbcUser = "root"; 
        String jdbcPassword = "root"; 

        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");

            
            Connection conn = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);
            String sql1 = "SELECT email FROM users";
            PreparedStatement stmt1 = conn.prepareStatement(sql1);
            ResultSet rs = stmt1.executeQuery();
            while(rs.next()) {
            
            String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);
             if(!(rs.getString("email").equals(email))) {
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                out.println("<script>alert('Registration successful!'); window.location='login.html';</script>");
            } }else {
                out.println("<script>alert('User Already Exists ! Please Login'); window.location='signup.html';</script>");
            }

            
            stmt.close();
            conn.close();}

            
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<script>alert('Database error: " + e.getMessage() + "'); window.location='signup.html';</script>");
        }
    }
}
