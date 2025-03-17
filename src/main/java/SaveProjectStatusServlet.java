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
import org.json.JSONObject;

@WebServlet("/SaveProjectStatusServlet")
public class SaveProjectStatusServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("application/json");
    	response.setCharacterEncoding("UTF-8");

        
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            int projectId = Integer.parseInt(request.getParameter("project_id"));
            String projectName = request.getParameter("project_name");
            String projectStatus = request.getParameter("project_status");
            int statusSubmittedBy = Integer.parseInt(request.getParameter("status_submitted_by"));

            conn = JDBCApp.getConnection();

            
            String checkQuery = "SELECT project_id FROM project_status WHERE project_id = ?";
            pst = conn.prepareStatement(checkQuery);
            pst.setInt(1, projectId);
            rs = pst.executeQuery();
            
            if (rs.next()) {
                
                rs.close();
                pst.close();

          
                String updateQuery = "UPDATE project_status SET project_name = ?, project_status = ?, status_submitted_by = ? WHERE project_id = ?";
                pst = conn.prepareStatement(updateQuery);
                pst.setString(1, projectName);
                pst.setString(2, projectStatus);
                pst.setInt(3, statusSubmittedBy);
                pst.setInt(4, projectId);
                
                int rowsUpdated = pst.executeUpdate();
                if (rowsUpdated > 0) {
                    jsonResponse.put("status", "Updated Successfully");
                } else {
                    jsonResponse.put("error", "Failed to Update");
                }
            } else {
               
                pst.close();

                
                String insertQuery = "INSERT INTO project_status (project_id, project_name, project_status, status_submitted_by) VALUES (?, ?, ?, ?)";
                pst = conn.prepareStatement(insertQuery);
                pst.setInt(1, projectId);
                pst.setString(2, projectName);
                pst.setString(3, projectStatus);
                pst.setInt(4, statusSubmittedBy);
                
                int rowsInserted = pst.executeUpdate();
                if (rowsInserted > 0) {
                    jsonResponse.put("status", "Inserted Successfully");
                } else {
                    jsonResponse.put("error", "Failed to Insert");
                }
            }
        } catch (NumberFormatException e) {
            jsonResponse.put("error", "Invalid Input: Please enter valid numbers");
        } catch (Exception e) {
            jsonResponse.put("error", "Internal Server Error");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
}
