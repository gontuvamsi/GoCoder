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

        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
        out.println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>");
        out.println("<style>");
        out.println(".custom-modal .modal-content { border-radius: 8px; }");
        out.println(".btn { background-color: #6200ea; color: white; border: none; border-radius: 4px; }");
        out.println(".btn:hover { background-color: #7f39fb; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='modal fade' id='messageModal' tabindex='-1' aria-labelledby='messageModalLabel' aria-hidden='true'>");
        out.println("<div class='modal-dialog custom-modal modal-dialog-centered'>");
        out.println("<div class='modal-content'>");
        out.println("<div class='modal-header' style='padding: 10px;'>");
        out.println("<h5 class='modal-title' id='messageModalLabel' style='font-size: 18px; font-weight: 600; color: #6200ea; margin: 0;'>Registration Status</h5>");
        out.println("</div>");
        out.println("<div class='modal-body' style='padding: 10px;'>");
        out.println("<p style='font-size: 14px; margin: 0;' id='modalMessage'></p>");
        out.println("</div>");
        out.println("<div class='modal-footer' style='padding: 5px;'>");
        out.println("<button type='button' class='btn' id='modalButton' style='padding: 6px 16px; font-size: 14px; width: auto;'>OK</button>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);
            
            String sql1 = "SELECT email FROM users WHERE email = ?";
            PreparedStatement stmt1 = conn.prepareStatement(sql1);
            stmt1.setString(1, email);
            ResultSet rs = stmt1.executeQuery();
            
            out.println("<script>");
            out.println("document.addEventListener('DOMContentLoaded', function() {");
            out.println("var modal = new bootstrap.Modal(document.getElementById('messageModal'));");
            out.println("var modalMessage = document.getElementById('modalMessage');");
            out.println("var modalButton = document.getElementById('modalButton');");

            if (rs.next()) {
                out.println("modalMessage.textContent = 'Email Already Exists! Please Try Again.';");
                out.println("modalButton.onclick = function() { window.location='signup.html'; };");
            } else {
                String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.setString(4, role);
                
                int rowsInserted = stmt.executeUpdate();
                
                if (rowsInserted > 0) {
                    out.println("modalMessage.textContent = 'Registration Successful!';");
                    out.println("modalButton.onclick = function() { window.location='home.html'; };");
                } else {
                    out.println("modalMessage.textContent = 'Error in Registration! Please Try Again.';");
                    out.println("modalButton.onclick = function() { window.location='home.html'; };");
                }
                stmt.close();
            }
            
            out.println("modal.show();");
            out.println("});");
            out.println("</script>");

            rs.close();
            stmt1.close();
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<script>");
            out.println("document.addEventListener('DOMContentLoaded', function() {");
            out.println("var modal = new bootstrap.Modal(document.getElementById('messageModal'));");
            out.println("var modalMessage = document.getElementById('modalMessage');");
            out.println("var modalButton = document.getElementById('modalButton');");
            out.println("modalMessage.textContent = 'Database error: " + e.getMessage() + "';");
            out.println("modalButton.onclick = function() { window.location='home.html'; };");
            out.println("modal.show();");
            out.println("});");
            out.println("</script>");
        }
        
        out.println("</body>");
        out.println("</html>");
    }
}