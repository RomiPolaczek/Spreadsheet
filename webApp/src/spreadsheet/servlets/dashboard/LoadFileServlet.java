package spreadsheet.servlets.dashboard;

import api.Engine;
import exception.InvalidFileFormatException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import spreadsheet.utils.ServletUtils;
import spreadsheet.utils.SessionUtils;

import java.io.IOException;

@WebServlet(name = "LoadFileServlet", urlPatterns = "/loadFile")
@MultipartConfig
public class LoadFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("You must log in before loading a file.");
            return;
        }

        Part filePart = request.getPart("file");

        if (filePart == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("File part is missing.");
            return;
        }

        String fileName = filePart.getSubmittedFileName();
        if (fileName == null || !fileName.toLowerCase().endsWith(".xml")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Uploaded file must be an XML file.");
            return;
        }

        synchronized (this) {
            try {
                engine.LoadFile(filePart.getInputStream(), usernameFromSession);

                String successResponse = String.format(fileName);
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
