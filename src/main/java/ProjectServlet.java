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

        if (pathInfo == null || pathInfo.equals("/")) {
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Welcome to ProjectServlet main path");
        } else {
            String path = pathInfo.substring(1);
            try (Connection con = JDBCApp.getConnection()) {
                switch (path) {
                    case "getAllProject":
                        getAllProjects(con, jsonResponse);
                        break;
                    case "getProjectById":
                        getProjectById(request, con, jsonResponse);
                        break;
                    case "putProject":
                        putProject(request, con, jsonResponse);
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
            project.put("posted_by", rs.getString("posted_by"));
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
            project.put("posted_by", rs.getString("posted_by"));
            jsonResponse.put("status", "success");
            jsonResponse.put("project", project);
        } else {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Project not found");
        }
    }

    private void putProject(HttpServletRequest request, Connection con, JSONObject jsonResponse) throws Exception {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String technology = request.getParameter("technology");
        String category = request.getParameter("category");
        String deadline = request.getParameter("deadline");
        double min_budget = Double.parseDouble(request.getParameter("min_budget"));
        double max_budget = Double.parseDouble(request.getParameter("max_budget"));
        String visibility = request.getParameter("visibility");
        String posted_by = request.getParameter("posted_by");

        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO projects (title, description, technology, category, deadline, min_budget, max_budget, visibility, posted_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, technology);
        ps.setString(4, category);
        ps.setString(5, deadline);
        ps.setDouble(6, min_budget);
        ps.setDouble(7, max_budget);
        ps.setString(8, visibility);
        ps.setString(9, posted_by);

        int rows = ps.executeUpdate();
        jsonResponse.put("status", rows > 0 ? "success" : "error");
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
