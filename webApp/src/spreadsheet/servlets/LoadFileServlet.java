package spreadsheet.servlets;

import api.Engine;
import exception.InvalidFileFormatException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spreadsheet.utils.ServletUtils;
import spreadsheet.utils.SessionUtils;
import users.UserManager;

import java.io.FileNotFoundException;
import java.io.IOException;

@WebServlet(name = "LoadFileServlet", urlPatterns = "/loadFile")
public class LoadFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("You must log in before loading a file.");
            return;
        }

        String filePathFromParameter = request.getParameter("filePath");
        if (filePathFromParameter == null || filePathFromParameter.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("File path is missing or empty.");
            return;
        }

        filePathFromParameter = filePathFromParameter.trim();

        synchronized (this) {
            try {
                engine.LoadFile(filePathFromParameter);

                String successResponse = String.format(filePathFromParameter);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(successResponse);

            } catch (RuntimeException | IOException | InvalidFileFormatException e) {
                String errorResponse = String.format(e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(errorResponse);
            } catch (Exception e) {
                String errorResponse = String.format(e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(errorResponse);
            }
        }
    }

}
