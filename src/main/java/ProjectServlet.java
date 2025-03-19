import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            out.print(jsonResponse.toString());
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
                            out.print(jsonResponse.toString());
                            return;
                        } else {
                            getProjectsByTechnology(request, con, jsonResponse);
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

    private void putProject(HttpSession session, HttpServletRequest request, Connection con, JSONObject jsonResponse) {
		// TODO Auto-generated method stub
		
	}

	private void getAllProjects(HttpSession session, Connection con, JSONObject jsonResponse) throws Exception {
        int user_id = (int) session.getAttribute("userId");

        PreparedStatement ps = con.prepareStatement("SELECT * FROM projects WHERE user_id = ?");
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

    private void getProjectsByTechnology(HttpServletRequest request, Connection con, JSONObject jsonResponse) throws Exception {
        String technology = request.getParameter("technology");
        String sql = "SELECT * FROM projects WHERE status = 'active'";

        if (technology != null && !technology.isEmpty()) {
            sql += " AND technology = ?";
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (technology != null && !technology.isEmpty()) {
                ps.setString(1, technology);
            }

            ResultSet rs = ps.executeQuery();
            JSONArray projectArray = new JSONArray();

            while (rs.next()) {
                JSONObject project = new JSONObject();
                project.put("id", rs.getInt("id"));
                project.put("title", rs.getString("title"));
                project.put("description", rs.getString("description"));
                project.put("technology", rs.getString("technology"));
                project.put("deadline", rs.getDate("deadline").toString());
                project.put("min_budget", rs.getDouble("min_budget"));
                project.put("max_budget", rs.getDouble("max_budget"));
                projectArray.put(project);
            }

            jsonResponse.put("status", "success");
            jsonResponse.put("projects", projectArray);
        }
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
