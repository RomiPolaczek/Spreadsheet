package spreadsheet.servlets.dashboard;

import api.Engine;
import com.google.gson.Gson;
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
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "LoadFileServlet", urlPatterns = "/loadFile")
@MultipartConfig
public class LoadFileServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(createJsonResponse("You must log in before loading a file."));
            return;
        }

        Part filePart = request.getPart("file");
        if (filePart == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(createJsonResponse("File part is missing."));
            return;
        }

        String fileName = filePart.getSubmittedFileName();
        if (fileName == null || !fileName.toLowerCase().endsWith(".xml")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(createJsonResponse("Uploaded file must be an XML file."));
            return;
        }

        synchronized (this) {
            try {
                engine.LoadFile(filePart.getInputStream(), usernameFromSession);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(createJsonResponse("File " + fileName +" loaded successfully."));
            } catch (InvalidFileFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(createJsonResponse("Invalid file format: " + e.getMessage()));
            } catch (RuntimeException | IOException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(createJsonResponse("Error loading file: " + e.getMessage()));
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(createJsonResponse("Server error: " + e.getMessage()));
            }
        }
    }

    private String createJsonResponse(String message) {
        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("message", message);
        return gson.toJson(jsonResponse);
    }
}