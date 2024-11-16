package spreadsheet.servlets.login;

import spreadsheet.utils.ServletUtils;
import spreadsheet.utils.SessionUtils;
import users.UserManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        if (usernameFromSession != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            System.out.println("Clearing session for " + usernameFromSession);
            userManager.removeUser(usernameFromSession);
            SessionUtils.clearSession(request);
            out.write("Logged out successfully");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println("Username is null");
            out.write("Something went wrong...\ncouldn't identify a user to logout");

        }
    }

}