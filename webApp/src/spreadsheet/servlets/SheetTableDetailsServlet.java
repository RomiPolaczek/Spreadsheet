package spreadsheet.servlets;

import api.Engine;
import com.google.gson.Gson;
import dto.DTOsheetTableDetails;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spreadsheet.utils.ServletUtils;
import spreadsheet.utils.SessionUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


@WebServlet(name = "SheetTableDetailsServlet", urlPatterns = "/dashboard/getSheetDetails")

public class SheetTableDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("You must log in before loading a file.");
            return;
        }

        List<DTOsheetTableDetails> sheetsDetailsList = engine.getDTOsheetTableDetailsList();

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(sheetsDetailsList);
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
