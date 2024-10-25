package spreadsheet.servlets;

import api.Engine;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spreadsheet.utils.ServletUtils;
import users.UserManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@WebServlet("/sheetList")
public class SheetListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            Engine engine = ServletUtils.getEngine(getServletContext());
            Set<String> sheetList = engine.getSheetNameToSheet().keySet();
            String json = gson.toJson(sheetList);
            out.println(json);
            out.flush();
        }
    }
}
