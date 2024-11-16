package spreadsheet.servlets.mainSheet.filterAndSort;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import api.Engine;
import com.google.gson.Gson;
import spreadsheet.utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import static spreadsheet.constants.Constants.*;

@WebServlet(name = "GetValuesForFilterServlet", urlPatterns = "/mainSheet/getValuesForFilter")
public class GetValuesForFilterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            Engine engine = ServletUtils.getEngine(getServletContext());

            // Parse the JSON request body into a map
            Map<String, String> requestData = gson.fromJson(request.getReader(), Map.class);

            // Extract parameters from the request
            String selectedSheet = requestData.get(SELECTED_SHEET_NAME);
            String column = requestData.get(COLUMN);
            String rangeStr = requestData.get(RANGE_STR);

            // Fetch values for the filter from the engine
            List<String> columnsList = engine.createListOfValuesForFilter(selectedSheet, column, rangeStr);

            // Send a successful response with the values
            response.setStatus(HttpServletResponse.SC_OK);
            String json = gson.toJson(columnsList);
            out.println(json);
            out.flush();
        } catch (Exception e) {
            // Handle any errors and send an appropriate response
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
        }
    }
}
