package spreadsheet.servlets.mainSheet.filterAndSort;

import api.Engine;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.DTOsheet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spreadsheet.utils.ServletUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static spreadsheet.constants.Constants.*;

@WebServlet(name = "CreateDTOSheetSortServlet", urlPatterns = "/mainSheet/createDTOSheetSort")
public class CreateDTOSheetSortServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Engine engine = ServletUtils.getEngine(getServletContext());

        StringBuilder jsonBuilder = new StringBuilder();

        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        } catch (IOException e) {
            // Handle IO exception when reading the request
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(e.getMessage());
            return;
        }

        // Step 2: Parse the JSON string
        String jsonString = jsonBuilder.toString();
        Gson gson = new Gson();

        try {
                // Define the structure of the JSON payload
            Type payloadType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> payload = gson.fromJson(jsonString, payloadType);

            // Step 3: Extract data from the parsed payload
            String sheetName = (String) payload.get(SELECTED_SHEET_NAME);
            String rangeStr = (String) payload.get(RANGE_STR);

            // Extract selectedColumns as a List<String>
            Type selectedColumnsType = new TypeToken<List<String>>() {}.getType();
            List<String> selectedColumns = gson.fromJson(gson.toJson(payload.get(SELECTED_COLUMNS)), selectedColumnsType);

            // Extract oldCoordToNewCoord as a Map<String, String>
            Type oldCoordToNewCoordType = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> oldCoordToNewCoord = gson.fromJson(gson.toJson(payload.get(OLD_COORD_TO_NEW_COORD)), oldCoordToNewCoordType);

            // Call the engine method and handle the response
            DTOsheet resultSheet = engine.sortColumnBasedOnSelection(sheetName, rangeStr, selectedColumns, oldCoordToNewCoord);
            response.setStatus(HttpServletResponse.SC_OK);

            // Write the result back as a JSON response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(resultSheet));

        } catch (Exception e) {
            // Catch JSON parsing errors or any other exception that occurs in the try block
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(e.getMessage());
        }
    }

}
