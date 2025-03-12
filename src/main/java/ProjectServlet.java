import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/ProjectServlet/*")
public class ProjectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String pathInfo = request.getPathInfo();
		JSONObject jsonResponse = new JSONObject();
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("userId") == null) {
			jsonResponse.put("status", "error");
			jsonResponse.put("message", "Session expired. Please log in again.");
			return;
		}
		if (pathInfo == null || pathInfo.equals("/")) {
			jsonResponse.put("status", "success");
			jsonResponse.put("message", "Welcome to ProjectServlet main path");
		} else {
			String path = pathInfo.substring(1);
			try (Connection con = JDBCApp.getConnection()) {
				switch (path) {
				case "getAllProject":
					getAllProjects(session, con, jsonResponse);
					break;
				case "getProject":
					
					if (!"Coder".equals(session.getAttribute("userRole"))) {
					    if (session != null) {
					        session.invalidate();
					    }
					    
					    jsonResponse.put("status", "error");
					    jsonResponse.put("message", "Session expired. Please log in again.");
				        return;
					} else {
					    getAllProjects(con, jsonResponse);
					}
					break;
				case "getProjectById":
					getProjectById(request, con, jsonResponse);
					break;
				case "ProjectPostingServlet":
					putProject(session, request, con, jsonResponse);
					break;
				case "editProjects":
					editProjects(request, con, jsonResponse);
					break;
				case "removeProjects":
					removeProjects(request, con, jsonResponse);
					break;
				default:
					jsonResponse.put("status", "error");
					jsonResponse.put("message", "Invalid path");
				}
			} catch (Exception e) {
				jsonResponse.put("status", "error");
				jsonResponse.put("message", e.getMessage());
			}
		}
		out.print(jsonResponse.toString());
		out.flush();
	}

	private void getAllProjects(HttpSession session, Connection con, JSONObject jsonResponse) throws Exception {
		int user_id = (int) session.getAttribute("userId");

		PreparedStatement ps = con.prepareStatement("SELECT * FROM projects where user_id = ?");
		ps.setInt(1, user_id);
		ResultSet rs = ps.executeQuery();

		JSONArray projectArray = new JSONArray();
		while (rs.next()) {
			JSONObject project = new JSONObject();
			project.put("id", rs.getInt("id"));
			project.put("title", rs.getString("title"));
			project.put("description", rs.getString("description"));
			project.put("technology", rs.getString("technology"));
			project.put("category", rs.getString("category"));
			project.put("deadline", rs.getDate("deadline"));
			project.put("min_budget", rs.getDouble("min_budget"));
			project.put("max_budget", rs.getDouble("max_budget"));
			project.put("visibility", rs.getString("visibility"));
			projectArray.put(project);
		}
		jsonResponse.put("status", "success");
		jsonResponse.put("projects", projectArray);
	}
	private void getAllProjects(Connection con, JSONObject jsonResponse) throws Exception {
		PreparedStatement ps = con.prepareStatement("SELECT * FROM projects");
		ResultSet rs = ps.executeQuery();

		JSONArray projectArray = new JSONArray();
		while (rs.next()) {
			JSONObject project = new JSONObject();
			project.put("id", rs.getInt("id"));
			project.put("title", rs.getString("title"));
			project.put("description", rs.getString("description"));
			project.put("technology", rs.getString("technology"));
			project.put("category", rs.getString("category"));
			project.put("deadline", rs.getDate("deadline"));
			project.put("min_budget", rs.getDouble("min_budget"));
			project.put("max_budget", rs.getDouble("max_budget"));
			project.put("visibility", rs.getString("visibility"));
			projectArray.put(project);
		}
		jsonResponse.put("status", "success");
		jsonResponse.put("projects", projectArray);
	}

	private void getProjectById(HttpServletRequest request, Connection con, JSONObject jsonResponse) throws Exception {
		int id = Integer.parseInt(request.getParameter("id"));
		PreparedStatement ps = con.prepareStatement("SELECT * FROM projects WHERE id=?");
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			JSONObject project = new JSONObject();
			project.put("id", rs.getInt("id"));
			project.put("title", rs.getString("title"));
			project.put("description", rs.getString("description"));
			project.put("technology", rs.getString("technology"));
			project.put("category", rs.getString("category"));
			project.put("deadline", rs.getDate("deadline"));
			project.put("min_budget", rs.getDouble("min_budget"));
			project.put("max_budget", rs.getDouble("max_budget"));
			project.put("visibility", rs.getString("visibility"));
			jsonResponse.put("status", "success");
			jsonResponse.put("project", project);
		} else {
			jsonResponse.put("status", "error");
			jsonResponse.put("message", "Project not found");
		}
	}

	private void putProject(HttpSession session, HttpServletRequest request, Connection con, JSONObject jsonResponse)
			throws Exception {

		int user_id = (int) session.getAttribute("userId");
		String title = request.getParameter("title");
		String description = request.getParameter("description");
		String technology = request.getParameter("technology");
		String category = request.getParameter("category");
		String deadlineStr = request.getParameter("deadline");
		String minBudgetStr = request.getParameter("min_budget");
		String maxBudgetStr = request.getParameter("max_budget");
		String visibility = request.getParameter("visibility");

		// Validate input fields
		if (title == null || title.isEmpty() || description == null || description.isEmpty() || technology == null
				|| technology.isEmpty() || category == null || category.isEmpty() || deadlineStr == null
				|| deadlineStr.isEmpty() || minBudgetStr == null || minBudgetStr.isEmpty() || maxBudgetStr == null
				|| maxBudgetStr.isEmpty() || visibility == null || visibility.isEmpty()) {

			jsonResponse.put("status", "error");
			jsonResponse.put("message", "All fields are required.");
			return;
		}

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date deadline = dateFormat.parse(deadlineStr);
			java.sql.Date sqlDeadline = new java.sql.Date(deadline.getTime());
			double minBudget = Double.parseDouble(minBudgetStr);
			double maxBudget = Double.parseDouble(maxBudgetStr);

			String sql = "INSERT INTO projects (title, description, technology, category, deadline, min_budget, max_budget, visibility, user_id) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
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
					jsonResponse.put("status", "success");
					jsonResponse.put("message", "Project posted successfully.");
				} else {
					jsonResponse.put("status", "error");
					jsonResponse.put("message", "Failed to post project.");
				}
			}
		} catch (ParseException e) {
			jsonResponse.put("status", "error");
			jsonResponse.put("message", "Invalid date format.");
		} catch (NumberFormatException e) {
			jsonResponse.put("status", "error");
			jsonResponse.put("message", "Invalid budget format.");
		} catch (SQLException e) {
			jsonResponse.put("status", "error");
			jsonResponse.put("message", "Database error: " + e.getMessage());
		}
	}

	private void editProjects(HttpServletRequest request, Connection con, JSONObject jsonResponse) throws Exception {
		int id = Integer.parseInt(request.getParameter("id"));
		String title = request.getParameter("title");
		String description = request.getParameter("description");
		PreparedStatement pst = con.prepareStatement("UPDATE projects SET title = ?, description = ? WHERE id = ?");
		pst.setString(1, title);
		pst.setString(2, description);
		pst.setInt(3, id);
		int result = pst.executeUpdate();
		jsonResponse.put("status", result > 0 ? "success" : "error");
	}

	private void removeProjects(HttpServletRequest request, Connection con, JSONObject jsonResponse) throws Exception {
		int id = Integer.parseInt(request.getParameter("id"));
		PreparedStatement pst = con.prepareStatement("DELETE FROM projects WHERE id = ?");
		pst.setInt(1, id);
		int result = pst.executeUpdate();
		jsonResponse.put("status", result > 0 ? "success" : "error");
	}
}
