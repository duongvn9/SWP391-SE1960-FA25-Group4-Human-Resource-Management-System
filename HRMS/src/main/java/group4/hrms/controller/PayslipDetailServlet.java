package group4.hrms.controller;

import group4.hrms.dao.TimesheetPeriodDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/payslips/detail")
public class PayslipDetailServlet extends HttpServlet{
    
    TimesheetPeriodDao tDao = new TimesheetPeriodDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("period", tDao.findAll());
            req.getRequestDispatcher("/WEB-INF/views/payroll/detail-payslip.jsp").forward(req, resp);
        } catch (SQLException ex) {
            Logger.getLogger(PayslipDetailServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
