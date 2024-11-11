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

        // Get the engine instace
        Engine engine = ServletUtils.getEngine(getServletContext());

        // Prepare GSON
        Gson gson = new Gson();
        Map<String, String> jsonResponse = new HashMap<>();

        // Ask for permission
        synchronized (this) {
            engine.askForPermission(username, selectedSheet, PermissionType.valueOf(permissionType.toUpperCase()));
            //jsonResponse.put("status", "PERMISSION_REQUESTED");
        }
        response.getWriter().write(gson.toJson(jsonResponse));
    }
}
