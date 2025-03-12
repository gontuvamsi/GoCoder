import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ProjectPostingServlet")
public class ProjectPostingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	HttpSession session = request.getSession(false);
    	if(session == null ||  session.getAttribute("userId")==null) {
    		response.getWriter().println("alert('Session Expired Login Again!')");
    		response.sendRedirect("login.html");
    		return;
    	}
    	int user_id = (int) session.getAttribute("userId");
    	String title = request.getParameter("title");
        String description = request.getParameter("description");
        String technology = request.getParameter("technology");
        String category = request.getParameter("category");
        String deadlineStr = request.getParameter("deadline");
        String minBudgetStr = request.getParameter("min_budget");
        String maxBudgetStr = request.getParameter("max_budget");
        String visibility = request.getParameter("visibility");

        
       
        

        

        
        if (title == null || title.isEmpty() || description == null || description.isEmpty()
                || technology == null || technology.isEmpty() || category == null || category.isEmpty()
                || deadlineStr == null || deadlineStr.isEmpty() || minBudgetStr == null || minBudgetStr.isEmpty()
                || maxBudgetStr == null || maxBudgetStr.isEmpty() || visibility == null || visibility.isEmpty()) {

            response.getWriter().println("All fields are required.");
            return; 
        }

        try {
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date deadline = dateFormat.parse(deadlineStr);
            java.sql.Date sqlDeadline = new java.sql.Date(deadline.getTime());
            double minBudget = Double.parseDouble(minBudgetStr);
            double maxBudget = Double.parseDouble(maxBudgetStr);
System.out.println(sqlDeadline);
            
            String jdbcUrl = "jdbc:mysql://localhost:3306/gocoder"; 
            String dbUser = "root"; 
            String dbPassword = "root"; 

            
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "INSERT INTO projects (title, description, technology, category, deadline, min_budget, max_budget, visibility, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

                
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, description);
                preparedStatement.setString(3, technology);
                preparedStatement.setString(4, category);
                preparedStatement.setDate(5, sqlDeadline);
                preparedStatement.setDouble(6, minBudget);
                preparedStatement.setDouble(7, maxBudget);
                preparedStatement.setString(8, visibility);
                preparedStatement.setInt(9, user_id);

                
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    
                    response.sendRedirect("customerdashboard.html"); 
                } else {
                    response.getWriter().println("Failed to post project.");
                }

            }

        } catch (ParseException e) {
            response.getWriter().println("Invalid date format.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            response.getWriter().println("Invalid budget format.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            response.getWriter().println("Database driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            response.getWriter().println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}