package spreadsheet.servlets.dashboard;

import api.Engine;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import permissions.PermissionStatus;
import permissions.PermissionType;
import spreadsheet.utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "HandlePermissionRequestServlet", urlPatterns = "/dashboard/handlePermissionRequest")
public class HandlePermissionRequestServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        String username = req.getParameter("username");
        String ownerName = req.getParameter("ownerName");
        String sheetName = req.getParameter("sheetName");
        String permissionStatusStr = req.getParameter("permissionStatus");
        String permissionTypeStr = req.getParameter("permissionType");

        // Convert the parameters to enum types
        PermissionStatus permissionStatus = PermissionStatus.valueOf(permissionStatusStr);
        PermissionType permissionType = PermissionType.valueOf(permissionTypeStr);

        // Retrieve the engine instance from the servlet context
        Engine engine = ServletUtils.getEngine(getServletContext());

        try {
            engine.handlePermissionRequest(username, permissionStatus, permissionType, sheetName);
            response.setStatus(HttpServletResponse.SC_OK);
            out.write(gson.toJson("Success"));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(gson.toJson(e.getMessage()));
        }
    }
}
