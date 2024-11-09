package spreadsheet.servlets.dashboard;

import api.Engine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.DTOsheet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.api.CoordinateDeserializer;
import spreadsheet.utils.ServletUtils;
import spreadsheet.utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

import static spreadsheet.constants.Constants.SELECTED_SHEET_NAME;

@WebServlet(name = "DisplaySheetServlet", urlPatterns = "/dashboard/displaySheet")

public class DisplaySheetServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("You must log in before loading a file.");
            return;
        }

        String selectedSheetName = request.getParameter(SELECTED_SHEET_NAME);

        if (selectedSheetName == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Sheet name is missing.");
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            DTOsheet dtoSheet = engine.createDTOSheet(selectedSheetName);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Coordinate.class, new CoordinateDeserializer())
                    .create();
            String json = gson.toJson(dtoSheet);;
            out.println(json);
            out.flush();
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e){
            String errorResponse = String.format(e.getMessage());
            response.getWriter().write(errorResponse);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
