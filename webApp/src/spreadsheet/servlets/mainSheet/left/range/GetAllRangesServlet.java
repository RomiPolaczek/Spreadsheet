package spreadsheet.servlets.mainSheet.left.range;

import jakarta.servlet.http.HttpServlet;
import api.Engine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.DTOsheet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.api.CoordinateDeserializer;
import spreadsheet.utils.ServletUtils;
import spreadsheet.utils.SessionUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import static spreadsheet.constants.Constants.SELECTED_SHEET_NAME;


@WebServlet(name = "GetAllRangesServlet", urlPatterns = "/mainSheet/getRanges")
public class GetAllRangesServlet extends HttpServlet {

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            Engine engine = ServletUtils.getEngine(getServletContext());
            String selectedSheetName = request.getParameter(SELECTED_SHEET_NAME);
            List<String> rangeList = engine.getExistingRangesBySheetName(selectedSheetName);
            String json = gson.toJson(rangeList);
            out.println(json);
            out.flush();
        }
    }
}

