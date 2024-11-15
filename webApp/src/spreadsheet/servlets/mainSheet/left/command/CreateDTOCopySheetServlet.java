package spreadsheet.servlets.mainSheet.left.command;

import api.Engine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.DTOsheet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.api.CoordinateDeserializer;
import spreadsheet.utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

import static spreadsheet.constants.Constants.SELECTED_SHEET_NAME;

@WebServlet(name = "CreateDTOCopySheetServlet", urlPatterns = "/mainSheet/dynamicAnalysis/getDTOCopySheet")
public class CreateDTOCopySheetServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        Engine engine = ServletUtils.getEngine(getServletContext());

        String sheetName = request.getParameter(SELECTED_SHEET_NAME);

        if (sheetName == null || sheetName.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Sheet name is missing or empty.");
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            DTOsheet dtoSheet = engine.createDTOCopySheet(sheetName);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Coordinate.class, new CoordinateDeserializer())
                    .create();

            String json = gson.toJson(dtoSheet);
            out.println(json);
            out.flush();
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            String errorResponse = String.format("Error creating DTO sheet: %s", e.getMessage());
            response.getWriter().write(errorResponse);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
