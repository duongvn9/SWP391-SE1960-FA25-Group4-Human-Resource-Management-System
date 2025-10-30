package group4.hrms.controller.contract;

import group4.hrms.dao.EmploymentContractDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/contracts/delete")
public class ContractDeleteController extends HttpServlet {
    private EmploymentContractDao contractDao;
    
    @Override
    public void init() throws ServletException {
        contractDao = new EmploymentContractDao();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            
            boolean deleted = contractDao.deleteById(id);
            
            if (deleted) {
                response.sendRedirect(request.getContextPath() + "/contracts?success=Contract deleted successfully");
            } else {
                response.sendRedirect(request.getContextPath() + "/contracts?error=Failed to delete contract");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/contracts?error=" + e.getMessage());
        }
    }
}
