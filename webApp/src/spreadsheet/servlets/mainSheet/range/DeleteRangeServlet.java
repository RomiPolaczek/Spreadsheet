package spreadsheet.servlets.mainSheet.range;

import api.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spreadsheet.constants.Constants;
import spreadsheet.utils.ServletUtils;

import java.io.IOException;

@WebServlet(name = "DeleteRangeServlet", urlPatterns = "/mainSheet/deleteRange")
public class DeleteRangeServlet extends HttpServlet {

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        // Get the engine instance
        Engine engine = ServletUtils.getEngine(getServletContext());

        // Retrieve parameters from the request
        String sheetName = request.getParameter(Constants.SELECTED_SHEET_NAME);
        String rangeName = request.getParameter(Constants.RANGE_NAME);

        // Validate input parameters
        if (sheetName == null || rangeName == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid input parameters.");
            return;
        }

        try {
            // Call the engine method to remove the range
            engine.deleteRange(sheetName, rangeName);

            // Respond with success
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Range removed successfully.");
        } catch (Exception e) {
            // Handle any errors, respond with internal server error status and error message
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
        }
    }
}
