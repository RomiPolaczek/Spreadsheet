package spreadsheet.servlets.dashboard;

import api.Engine;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spreadsheet.utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import static spreadsheet.constants.Constants.GSON_INSTANCE;

@WebServlet(name = "AvailableSheetsServlet", urlPatterns = "/dashboard/availableSheets")

public class AvailableSheetsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {;

            Engine engine = ServletUtils.getEngine(getServletContext());
            Set<String> sheetList = engine.getSheetNameToSheet().keySet();
            String json = GSON_INSTANCE.toJson(sheetList);
            out.println(json);
            out.flush();
        }
    }
}
