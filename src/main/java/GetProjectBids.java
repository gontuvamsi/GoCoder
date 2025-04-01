import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/GetProjectBids")
public class GetProjectBids extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String projectOwnerId = request.getParameter("user_id"); 
        if (projectOwnerId == null || projectOwnerId.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Missing required parameter: user_id\"}");
            return;
        }

        JSONArray jsonArray = new JSONArray();

        try (Connection conn = JDBCApp.getConnection()) {
            String query = "SELECT projects.id, projects.title, users.name AS Coder_Name, " +
                           "bids.user_id AS Coder_Id, bids.* " +
                           "FROM bids " +
                           "JOIN projects ON bids.project_id = projects.id " +
                           "JOIN users ON bids.user_id = users.id " +
                           "WHERE projects.user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, Integer.parseInt(projectOwnerId));
                try (ResultSet rs = ps.executeQuery()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    while (rs.next()) {
                        JSONObject jsonObject = new JSONObject();
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnLabel(i);
                            Object columnValue = rs.getObject(i);
                            jsonObject.put(columnName, columnValue);
                        }
                        jsonArray.put(jsonObject);
                    }
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Database error: " + e.getMessage() + "\"}");
            return;
        }

        out.print(jsonArray.toString());
    }
}
