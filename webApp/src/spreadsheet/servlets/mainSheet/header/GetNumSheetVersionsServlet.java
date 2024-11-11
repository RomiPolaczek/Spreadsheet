package spreadsheet.servlets.mainSheet.header;

import api.Engine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spreadsheet.utils.ServletUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static spreadsheet.constants.Constants.GSON_INSTANCE;
import static spreadsheet.constants.Constants.SELECTED_SHEET_NAME;

@WebServlet(name = "GetNumSheetVersionsServlet", urlPatterns = "/mainSheet/getNumSheetVersions")
public class GetNumSheetVersionsServlet extends HttpServlet {

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String selectedSheetName = request.getParameter(SELECTED_SHEET_NAME);

        try {
            Engine engine = ServletUtils.getEngine(getServletContext());
            int numOfVersions = engine.getNumberOfVersions(selectedSheetName);
            List<String> versions = new ArrayList<>();
            for (int i = 1; i <= numOfVersions; i++) {
                versions.add(String.valueOf(i));
            }

            response.getWriter().write(GSON_INSTANCE.toJson(versions));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
        }
    }
}
