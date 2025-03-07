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

        // Retrieve form parameters
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String technology = request.getParameter("technology");
        String category = request.getParameter("category");
        String deadlineStr = request.getParameter("deadline");
        String minBudgetStr = request.getParameter("min_budget");
        String maxBudgetStr = request.getParameter("max_budget");
        String visibility = request.getParameter("visibility");

        //retrieve the user from the session. Assuming you have logged in the user and stored their username in session
        HttpSession session = request.getSession();
        String postedBy = "Vamsi"; //or however you have stored the username in session

        

        // Validate parameters (server-side validation)
        if (title == null || title.isEmpty() || description == null || description.isEmpty()
                || technology == null || technology.isEmpty() || category == null || category.isEmpty()
                || deadlineStr == null || deadlineStr.isEmpty() || minBudgetStr == null || minBudgetStr.isEmpty()
                || maxBudgetStr == null || maxBudgetStr.isEmpty() || visibility == null || visibility.isEmpty()) {

            response.getWriter().println("All fields are required.");
            return; // Stop processing if validation fails
        }

        try {
            // Parse date and budget values
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date deadline = dateFormat.parse(deadlineStr);
            java.sql.Date sqlDeadline = new java.sql.Date(deadline.getTime());
            double minBudget = Double.parseDouble(minBudgetStr);
            double maxBudget = Double.parseDouble(maxBudgetStr);
System.out.println(sqlDeadline);
            // Database connection details
            String jdbcUrl = "jdbc:mysql://localhost:3306/gocoder"; // Replace with your database URL
            String dbUser = "root"; // Replace with your database user
            String dbPassword = "root"; // Replace with your database password

            // Establish database connection
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL driver
            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "INSERT INTO projects (title, description, technology, category, deadline, min_budget, max_budget, visibility, posted_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

                // Set parameters for the prepared statement
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, description);
                preparedStatement.setString(3, technology);
                preparedStatement.setString(4, category);
                preparedStatement.setDate(5, sqlDeadline);
                preparedStatement.setDouble(6, minBudget);
                preparedStatement.setDouble(7, maxBudget);
                preparedStatement.setString(8, visibility);
                preparedStatement.setString(9, postedBy);

                // Execute the query
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    // Project posted successfully
                    response.sendRedirect("customerdashboard.html"); // Redirect to dashboard or success page
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