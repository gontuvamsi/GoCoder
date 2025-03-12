import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

@WebServlet("/session/*")
public class SessionManagement extends HttpServlet {
	private static final Logger LOGGER = Logger.getLogger(SessionManagement.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String path = Optional.ofNullable(req.getPathInfo()).orElse("/check");
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		JSONObject jsonResponse = new JSONObject();
		PrintWriter out = res.getWriter();
		try {
			switch (path) {
			case "/check" -> checkSession(jsonResponse, req, res);
			case "/getId" -> getUserDets(jsonResponse, req, res);
			case "/logout" -> logoutSession(jsonResponse, req, res);
			default -> {
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
				jsonResponse.put("status", "error");
				jsonResponse.put("message", "Invalid Request");
			}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error processing request", e);
			jsonResponse.put("status", "error");
			jsonResponse.put("message", "Internal Server Error");
		}
		out.print(jsonResponse.toString());
		out.flush();
	}

	private static void checkSession(JSONObject jsonResponse, HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		HttpSession session = req.getSession(false);
		if (session != null && session.getAttribute("userRole") != null) {
			String role = (String) session.getAttribute("userRole");
			jsonResponse.put("status", "success");
			jsonResponse.put("authenticated", true);
			jsonResponse.put("message", "Login Successful");
		} else {
			jsonResponse.put("status", "error");
			jsonResponse.put("message", "Not Authenticated");
		}
	}

	private static void getUserDets(JSONObject jsonResponse, HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		HttpSession session = req.getSession(false);
		if (session != null && session.getAttribute("userId") != null) {
			int user_id = (int) session.getAttribute("userId");
			String name = (String) session.getAttribute("userName");
			String role = (String) session.getAttribute("userRole");
			jsonResponse.put("status", "success");
			jsonResponse.put("user_id", user_id);
			jsonResponse.put("user_name", name);
			jsonResponse.put("user_role", role);
		} else {
			jsonResponse.put("status", "error");
			jsonResponse.put("message", "Not Authenticated");
		}
	}

	private static void logoutSession(JSONObject jsonResponse, HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		HttpSession session = req.getSession(false);
		if (session != null) {
			session.invalidate();
			jsonResponse.put("status", "success");
			jsonResponse.put("message", "Log Out Successfully");
		} else {
			 jsonResponse.put("status", "error");
  			jsonResponse.put("message", "Not Authenticated");
		}
	}
}
