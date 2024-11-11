package spreadsheet.servlets.dashboard;

import api.Engine;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import permissions.PermissionType;
import spreadsheet.constants.Constants;
import spreadsheet.utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "RequestPermissionServlet", urlPatterns = "/dashboard/requestPermission")
public class RequestPermissionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter(Constants.USER_NAME);
        String selectedSheet = request.getParameter(Constants.SELECTED_SHEET_NAME);
        String permissionType = request.getParameter(Constants.PERMISSION_TYPE);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        // Get the engine instace
        Engine engine = ServletUtils.getEngine(getServletContext());

        // Prepare GSON
        Map<String, String> jsonResponse = new HashMap<>();

        // Ask for permission
        try {
            engine.askForPermission(username, selectedSheet, PermissionType.valueOf(permissionType.toUpperCase()));
            response.setStatus(HttpServletResponse.SC_OK);
            out.write(gson.toJson("Success"));
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(gson.toJson(e.getMessage()));
        }

    }
}
