package spreadsheet.servlets.mainSheet.left.range;

import api.Engine;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spreadsheet.utils.ServletUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static spreadsheet.constants.Constants.*;

@WebServlet(name = "AddRangeServlet", urlPatterns = "/mainSheet/addRange")
public class AddRangeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        try {
            // Read the JSON body
            BufferedReader reader = request.getReader();
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            String jsonBody = jsonBuilder.toString();

            // Parse the JSON body to extract rangeName and rangeStr
            String sheetName = null;
            String rangeName = null;
            String rangeStr = null;

            // Use Gson to parse the JSON directly
            try {
                // Deserialize the JSON object into a map
                Map<String, String> rangeData = gson.fromJson(jsonBody, new TypeToken<Map<String, String>>(){}.getType());
                sheetName = rangeData.get(SELECTED_SHEET_NAME);
                rangeName = rangeData.get(RANGE_NAME);
                rangeStr = rangeData.get(RANGE_STR);
            } catch (JsonSyntaxException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(gson.toJson("Invalid JSON format."));
                return;
            }

            // Retrieve engine from the context
            Engine engine = ServletUtils.getEngine(getServletContext());

            // Check for missing or empty parameters
            if (rangeName == null || rangeName.isEmpty() || rangeStr == null || rangeStr.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(gson.toJson("Range name and range string parameters are required and cannot be empty."));
                return;
            }

            // Attempt to add the range
            engine.addRange(sheetName, rangeName, rangeStr);

            // If successful, return a success response
            response.setStatus(HttpServletResponse.SC_OK);
            out.write(gson.toJson("Range added successfully."));

            //If not successful
        } catch (RuntimeException e) {
            // Handle custom exceptions, such as duplicate range names or invalid format for range
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(gson.toJson(e.getMessage()));
        } catch (Exception e) {
            // Handle any unexpected exceptions
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write(gson.toJson("An error occurred while adding the range: " + e.getMessage()));
        } finally {
            out.close(); // Ensure the writer is closed
        }
    }
}
