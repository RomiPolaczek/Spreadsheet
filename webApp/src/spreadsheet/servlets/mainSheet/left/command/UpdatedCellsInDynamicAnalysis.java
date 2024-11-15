package spreadsheet.servlets.mainSheet.left.command;

import api.Engine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sheet.api.EffectiveValue;
import spreadsheet.utils.ServletUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static spreadsheet.constants.Constants.*;

@WebServlet(name = "UpdatedCellsInDynamicAnalysis", urlPatterns = "/mainSheet/dynamicAnalysis/updatedCells")
public class UpdatedCellsInDynamicAnalysis extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            String jsonBody = jsonBuilder.toString();

            Map<String, String> cellData;
            try {
                cellData = GSON_INSTANCE.fromJson(jsonBody, new TypeToken<Map<String, String>>(){}.getType());
            } catch (JsonSyntaxException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(GSON_INSTANCE.toJson("Invalid JSON format."));
                return;
            }

            String sheetName = cellData.get(SELECTED_SHEET_NAME);
            String cellID = cellData.get(CELL_ID);
            String newValue = cellData.get(NEW_VALUE);

            if (sheetName == null || cellID == null || newValue == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(GSON_INSTANCE.toJson("Missing required fields: sheetName, cellID, or newValue"));
                return;
            }

            Engine engine = ServletUtils.getEngine(getServletContext());
            Map<String, String> cellsNewValues = engine.getCellsThatHaveChangedAfterUpdateCell(sheetName, cellID, newValue);

            Gson gson = new GsonBuilder().create();
            String jsonResponse = gson.toJson(cellsNewValues);
            out.println(jsonResponse);
            out.flush();
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write(GSON_INSTANCE.toJson("An error occurred while processing the request: " + e.getMessage()));
        } finally {
            out.close(); // Ensure the writer is closed
        }
    }
}
