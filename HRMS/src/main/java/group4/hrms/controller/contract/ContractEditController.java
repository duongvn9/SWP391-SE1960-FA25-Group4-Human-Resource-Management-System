package group4.hrms.controller.contract;

import group4.hrms.dao.DepartmentDao;
import group4.hrms.dao.EmploymentContractDao;
import group4.hrms.dao.PositionDao;
import group4.hrms.dao.UserDao;
import group4.hrms.model.Department;
import group4.hrms.model.EmploymentContract;
import group4.hrms.model.Position;
import group4.hrms.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@WebServlet("/contracts/edit")
public class ContractEditController extends HttpServlet {
    private EmploymentContractDao contractDao;
    private UserDao userDao;
    private DepartmentDao departmentDao;
    private PositionDao positionDao;
    
    @Override
    public void init() throws ServletException {
        contractDao = new EmploymentContractDao();
        userDao = new UserDao();
        departmentDao = new DepartmentDao();
        positionDao = new PositionDao();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            Optional<EmploymentContract> contractOpt = contractDao.findById(id);
            
            if (!contractOpt.isPresent()) {
                response.sendRedirect(request.getContextPath() + "/contracts?error=Contract not found");
                return;
            }
            
            // Load data for form
            List<User> users = userDao.findAll();
            List<Department> departments = departmentDao.findAll();
            List<Position> positions = positionDao.findAll();
            
            request.setAttribute("contract", contractOpt.get());
            request.setAttribute("users", users);
            request.setAttribute("departments", departments);
            request.setAttribute("positions", positions);
            
            request.getRequestDispatcher("/WEB-INF/views/contracts/contract-form.jsp")
                    .forward(request, response);
                    
        } catch (SQLException e) {
            throw new ServletException("Error loading contract", e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            Optional<EmploymentContract> contractOpt = contractDao.findById(id);
            
            if (!contractOpt.isPresent()) {
                response.sendRedirect(request.getContextPath() + "/contracts?error=Contract not found");
                return;
            }
            
            EmploymentContract contract = contractOpt.get();
            
            // Update fields
            contract.setUserId(Long.parseLong(request.getParameter("userId")));
            contract.setContractNo(request.getParameter("contractNo"));
            contract.setContractType(request.getParameter("contractType"));
            contract.setStartDate(LocalDate.parse(request.getParameter("startDate")));
            
            String endDateStr = request.getParameter("endDate");
            contract.setEndDate((endDateStr != null && !endDateStr.isEmpty()) 
                    ? LocalDate.parse(endDateStr) : null);
                    
            contract.setBaseSalary(new BigDecimal(request.getParameter("baseSalary")));
            contract.setCurrency(request.getParameter("currency"));
            contract.setStatus(request.getParameter("status"));
            contract.setNote(request.getParameter("note"));
            
            // Update in database
            contractDao.update(contract);
            
            response.sendRedirect(request.getContextPath() + "/contracts?success=Contract updated successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/contracts?error=" + e.getMessage());
        }
    }
}
