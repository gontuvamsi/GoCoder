import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/SubmitBidServlet")
public class SubmitBidServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

		
		String projectId = request.getParameter("project_id"); 
		String bidAmt = request.getParameter("bid_amt");
		String completionDate = request.getParameter("completion_date");
		String bidDesc = request.getParameter("bid_desc");
		String userId = request.getParameter("user_id");

		
		System.out.println("Project ID: " + projectId);
		System.out.println("Bid Amount: " + bidAmt);
		System.out.println("Completion Date: " + completionDate);
		System.out.println("Bid Description: " + bidDesc);
		System.out.println("User ID: " + userId);

		
		String jdbcURL = "jdbc:mysql://localhost:3306/gocoder";
		String dbUser = "root";
		String dbPassword = "root";
		JSONObject jsonResponse = new JSONObject();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

			
			String sql = "INSERT INTO bids (project_id, user_id, bid_amt, completion_date, bid_desc) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);

			
			stmt.setInt(1, Integer.parseInt(projectId)); 
			stmt.setInt(2, Integer.parseInt(userId));
			stmt.setDouble(3, Double.parseDouble(bidAmt));
			stmt.setString(4, completionDate);
			stmt.setString(5, bidDesc);

			
			int rowsInserted = stmt.executeUpdate();

			
			if (rowsInserted > 0) {
				jsonResponse.put("status", "success");
				jsonResponse.put("message", "Bid submitted successfully.");
			} else {
				jsonResponse.put("status", "failure");
				jsonResponse.put("message", "Failed to submit bid.");
			}
			out.print(jsonResponse.toString());
			out.flush();
			stmt.close();
			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
			out.write("failure");
		}
	}
}
