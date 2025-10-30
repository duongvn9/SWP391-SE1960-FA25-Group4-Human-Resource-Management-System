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

@WebServlet("/contracts/create")
public class ContractCreateController extends HttpServlet {
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
        // Load users, departments, positions for dropdowns
        List<User> users = userDao.findAll();
        List<Department> departments = departmentDao.findAll();
        List<Position> positions = positionDao.findAll();
        request.setAttribute("users", users);
        request.setAttribute("departments", departments);
        request.setAttribute("positions", positions);
        request.getRequestDispatcher("/WEB-INF/views/contracts/contract-form.jsp")
                .forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get form data
            Long userId = Long.parseLong(request.getParameter("userId"));
            String contractNo = request.getParameter("contractNo");
            String contractType = request.getParameter("contractType");
            LocalDate startDate = LocalDate.parse(request.getParameter("startDate"));
            String endDateStr = request.getParameter("endDate");
            LocalDate endDate = (endDateStr != null && !endDateStr.isEmpty()) 
                    ? LocalDate.parse(endDateStr) : null;
            BigDecimal baseSalary = new BigDecimal(request.getParameter("baseSalary"));
            String currency = request.getParameter("currency");
            String status = request.getParameter("status");
            String note = request.getParameter("note");
            
            // Create contract
            EmploymentContract contract = new EmploymentContract();
            contract.setUserId(userId);
            contract.setContractNo(contractNo);
            contract.setContractType(contractType);
            contract.setStartDate(startDate);
            contract.setEndDate(endDate);
            contract.setBaseSalary(baseSalary);
            contract.setCurrency(currency);
            contract.setStatus(status);
            contract.setNote(note);
            
            // Get current account ID from session
            User currentUser = (User) request.getSession().getAttribute("user");
            if (currentUser != null) {
                contract.setCreatedByAccountId(currentUser.getId());
            }
            
            // Save to database
            contractDao.save(contract);
            
            response.sendRedirect(request.getContextPath() + "/contracts?success=Contract created successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/contracts/create?error=" + e.getMessage());
        }
    }
}
