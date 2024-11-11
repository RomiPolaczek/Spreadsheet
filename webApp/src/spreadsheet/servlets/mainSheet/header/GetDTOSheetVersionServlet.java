package spreadsheet.servlets.mainSheet.header;

import api.Engine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.DTOsheet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.api.CoordinateDeserializer;
import spreadsheet.utils.ServletUtils;

import java.io.IOException;

import static spreadsheet.constants.Constants.*;

@WebServlet(name = "GetDTOSheetVersion", urlPatterns = "/mainSheet/getDTOSheetVersion")
public class GetDTOSheetVersionServlet extends HttpServlet {

    @Override
    protected synchronized void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String selectedVersion = request.getParameter(SELECTED_VERSION);

            if (selectedVersion == null || selectedVersion.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid version selection");
                return;
            }

            String selectedSheetName = request.getParameter(SELECTED_SHEET_NAME);
            Engine engine = ServletUtils.getEngine(getServletContext());

            DTOsheet dtoSheet = engine.GetVersionForDisplay(selectedSheetName, selectedVersion);
            if (dtoSheet != null) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Coordinate.class, new CoordinateDeserializer())
                        .create();
                String json = gson.toJson(dtoSheet);
                response.getWriter().write(json);

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Version not found");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
            e.printStackTrace();
        }
    }
}
