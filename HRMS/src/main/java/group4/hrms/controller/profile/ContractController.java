package group4.hrms.controller.profile;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Controller for Employment Contract view (read-only)
 */
@WebServlet("/contracts")
public class ContractController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Forward to contract.jsp 
        req.getRequestDispatcher("/WEB-INF/views/profile/contract.jsp").forward(req, resp);
    }
}