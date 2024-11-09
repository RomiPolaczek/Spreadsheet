package spreadsheet.servlets.mainSheet.header;

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

@WebServlet(name = "UpdateCellValueServlet", urlPatterns = "/mainSheet/updateCellValue")
public class UpdateCellValueServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        try {
            BufferedReader reader = request.getReader();
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            String jsonBody = jsonBuilder.toString();

            Map<String, String> cellData = null;
            try {
                cellData = gson.fromJson(jsonBody, new TypeToken<Map<String, String>>(){}.getType());
            } catch (JsonSyntaxException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(gson.toJson("Invalid JSON format."));
                return;
            }

            String sheetName = cellData.get("sheetName");
            String cellID = cellData.get("cellID");
            String newValue = cellData.get("newValue");

            if (sheetName == null || cellID == null || newValue == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(gson.toJson("Missing required fields: sheetName, cellID, or newValue"));
                return;
            }

            Engine engine = ServletUtils.getEngine(getServletContext());
            engine.EditCell(cellID, newValue, sheetName);

            response.setStatus(HttpServletResponse.SC_OK);
            out.write(gson.toJson("Cell updated successfully."));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write(gson.toJson("An error occurred while updating the cell: " + e.getMessage()));
        } finally {
            out.close(); // Ensure the writer is closed
        }
    }
}
