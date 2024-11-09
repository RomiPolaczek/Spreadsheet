package spreadsheet.servlets.dashboard;

import api.Engine;
import com.google.gson.Gson;
import dto.DTOpermissionRequest;
import dto.DTOsheetTableDetails;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spreadsheet.constants.Constants;
import spreadsheet.utils.ServletUtils;
import spreadsheet.utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


@WebServlet(name = "PermissionTableDetailsServlet", urlPatterns = "/dashboard/getPermissionTableDetails")

public class PermissionTableDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(request);
        String selectedSheet = request.getParameter(Constants.SELECTED_SHEET_NAME);
        Engine engine = ServletUtils.getEngine(getServletContext());

//        if (usernameFromSession == null) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("You must log in before loading a file.");
//            return;
//        }

        List<DTOpermissionRequest> permissionsDetailsList = engine.getDTOpermissionTableDetailsList(selectedSheet);

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(permissionsDetailsList);
            out.println(json);
            out.flush();
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e){
            String errorResponse = String.format(e.getMessage());
            response.getWriter().write(errorResponse);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
