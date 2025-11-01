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
            
            // Validation 1: Không được edit hợp đồng đã expired hoặc terminated
            if ("expired".equalsIgnoreCase(contract.getStatus()) || 
                "terminated".equalsIgnoreCase(contract.getStatus())) {
                // Load form data and show error
                List<User> users = userDao.findAll();
                List<Department> departments = departmentDao.findAll();
                List<Position> positions = positionDao.findAll();
                
                request.setAttribute("contract", contract);
                request.setAttribute("users", users);
                request.setAttribute("departments", departments);
                request.setAttribute("positions", positions);
                request.setAttribute("errorMessage", "Cannot edit expired or terminated contracts");
                
                request.getRequestDispatcher("/WEB-INF/views/contracts/contract-form.jsp")
                        .forward(request, response);
                return;
            }
            
            // Get new values from form
            String contractNo = request.getParameter("contractNo");
            String contractType = request.getParameter("contractType");
            String baseSalaryStr = request.getParameter("baseSalary");
            String currency = request.getParameter("currency");
            String newStatus = request.getParameter("status");
            String note = request.getParameter("note");
            
            String errorMessage = null;
            
            // Validation 2: Chỉ được phép thay đổi status sang terminated (không được chọn expired thủ công)
            if ("expired".equalsIgnoreCase(newStatus)) {
                errorMessage = "Cannot manually set status to Expired. Status will be automatically updated when contract expires";
            }
            
            // If validation failed, return to form with current data
            if (errorMessage != null) {
                // Update contract with form data for display
                contract.setContractNo(contractNo);
                contract.setContractType(contractType);
                contract.setBaseSalary(new BigDecimal(baseSalaryStr));
                contract.setCurrency(currency);
                contract.setNote(note);
                
                List<User> users = userDao.findAll();
                List<Department> departments = departmentDao.findAll();
                List<Position> positions = positionDao.findAll();
                
                request.setAttribute("contract", contract);
                request.setAttribute("users", users);
                request.setAttribute("departments", departments);
                request.setAttribute("positions", positions);
                request.setAttribute("errorMessage", errorMessage);
                
                request.getRequestDispatcher("/WEB-INF/views/contracts/contract-form.jsp")
                        .forward(request, response);
                return;
            }
            
            // Auto-update status to expired nếu hợp đồng đã hết hạn
            LocalDate today = LocalDate.now();
            LocalDate endDate = contract.getEndDate();
            if (endDate != null && !endDate.isAfter(today) && !"terminated".equalsIgnoreCase(newStatus)) {
                newStatus = "expired";
            }
            
            // Update fields (không cho phép thay đổi employee, startDate, endDate)
            contract.setContractNo(contractNo);
            contract.setContractType(contractType);
            contract.setBaseSalary(new BigDecimal(baseSalaryStr));
            contract.setCurrency(currency);
            contract.setStatus(newStatus);
            contract.setNote(note);
            
            // Update in database
            contractDao.update(contract);
            
            response.sendRedirect(request.getContextPath() + "/contracts?success=Contract updated successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/contracts?error=" + e.getMessage());
        }
    }
}
