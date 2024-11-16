package spreadsheet.servlets.mainSheet.range;

import api.Engine;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spreadsheet.utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static spreadsheet.constants.Constants.RANGE_NAME;
import static spreadsheet.constants.Constants.SELECTED_SHEET_NAME;


@WebServlet(name = "GetRangeCellsListServlet", urlPatterns = "/mainSheet/getRangeCellsList")
public class GetRangeCellsListServlet extends HttpServlet {

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            Engine engine = ServletUtils.getEngine(getServletContext());

            String selectedSheetName = request.getParameter(SELECTED_SHEET_NAME);
            String rangeName = request.getParameter(RANGE_NAME);

            List<String> rangeCellsList = engine.getRangeCellsList(rangeName, selectedSheetName);
            response.setStatus(HttpServletResponse.SC_OK);

            String json = gson.toJson(rangeCellsList);
            out.println(json);
            out.flush();

        } catch (Exception e) {
            // Handle any errors, respond with internal server error status and error message
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
        }

    }
}

