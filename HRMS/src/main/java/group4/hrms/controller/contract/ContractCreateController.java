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
import java.util.ArrayList;
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
        try {
            // Get current user from session
            User currentUser = (User) request.getSession().getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            
            // Load all users
            List<User> allUsers = userDao.findAll();
            
            // Get user IDs with active contracts
            List<Long> userIdsWithActiveContract = contractDao.findUserIdsWithActiveContract();
            
            // Filter out users who already have active contracts and current user
            List<User> availableUsers = new ArrayList<>();
            for (User user : allUsers) {
                if (!user.getId().equals(currentUser.getId()) && 
                    !userIdsWithActiveContract.contains(user.getId())) {
                    availableUsers.add(user);
                }
            }
            
            // Generate contract number
            String generatedContractNo = contractDao.generateContractNo();
            
            List<Department> departments = departmentDao.findAll();
            List<Position> positions = positionDao.findAll();
            
            request.setAttribute("users", availableUsers);
            request.setAttribute("departments", departments);
            request.setAttribute("positions", positions);
            request.setAttribute("generatedContractNo", generatedContractNo);
            
            request.getRequestDispatcher("/WEB-INF/views/contracts/contract-form.jsp")
                    .forward(request, response);
                    
        } catch (SQLException e) {
            throw new ServletException("Error loading contract form", e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get current user from session
            User currentUser = (User) request.getSession().getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            
            // Get form data
            String userIdStr = request.getParameter("userId");
            String contractNo = request.getParameter("contractNo");
            String contractType = request.getParameter("contractType");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String baseSalaryStr = request.getParameter("baseSalary");
            String currency = request.getParameter("currency");
            String note = request.getParameter("note");
            
            // Parse data
            Long userId = Long.parseLong(userIdStr);
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = (endDateStr != null && !endDateStr.isEmpty()) 
                    ? LocalDate.parse(endDateStr) : null;
            BigDecimal baseSalary = new BigDecimal(baseSalaryStr);
            
            // Create temporary contract object for form data
            EmploymentContract formData = new EmploymentContract();
            formData.setUserId(userId);
            formData.setContractNo(contractNo);
            formData.setContractType(contractType);
            formData.setStartDate(startDate);
            formData.setEndDate(endDate);
            formData.setBaseSalary(baseSalary);
            formData.setCurrency(currency);
            formData.setNote(note);
            
            String errorMessage = null;
            
            // Validation 1: HR không thể tạo hợp đồng cho chính mình
            if (userId.equals(currentUser.getId())) {
                errorMessage = "You cannot create a contract for yourself";
            }
            // Validation 2: Kiểm tra nhân viên đã có hợp đồng active chưa
            else if (contractDao.findActiveContractByUser(userId).isPresent()) {
                errorMessage = "Employee already has an active contract";
            }
            // Validation 3: Ngày bắt đầu phải trước ngày kết thúc
            else if (endDate != null && !startDate.isBefore(endDate)) {
                errorMessage = "Start date must be before end date";
            }
            // Validation 4: Nếu loại hợp đồng là indefinite thì không được có end date
            else if ("indefinite".equalsIgnoreCase(contractType) && endDate != null) {
                errorMessage = "Indefinite contracts cannot have an end date";
            }
            // Validation 5: Nếu loại hợp đồng là fixed_term hoặc probation thì phải có end date
            else if (("fixed_term".equalsIgnoreCase(contractType) || "probation".equalsIgnoreCase(contractType)) && endDate == null) {
                errorMessage = "Fixed-term and probation contracts must have an end date";
            }
            
            // If validation failed, return to form with data
            if (errorMessage != null) {
                // Load form data
                List<User> allUsers = userDao.findAll();
                List<Long> userIdsWithActiveContract = contractDao.findUserIdsWithActiveContract();
                List<User> availableUsers = new ArrayList<>();
                for (User user : allUsers) {
                    if (!user.getId().equals(currentUser.getId()) && 
                        !userIdsWithActiveContract.contains(user.getId())) {
                        availableUsers.add(user);
                    }
                }
                
                List<Department> departments = departmentDao.findAll();
                List<Position> positions = positionDao.findAll();
                
                request.setAttribute("users", availableUsers);
                request.setAttribute("departments", departments);
                request.setAttribute("positions", positions);
                request.setAttribute("formData", formData);
                request.setAttribute("errorMessage", errorMessage);
                
                request.getRequestDispatcher("/WEB-INF/views/contracts/contract-form.jsp")
                        .forward(request, response);
                return;
            }
            
            // Create contract
            formData.setStatus("active"); // Mặc định là active
            formData.setCreatedByAccountId(currentUser.getId());
            
            // Save to database
            contractDao.save(formData);
            
            response.sendRedirect(request.getContextPath() + "/contracts?success=Contract created successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/contracts/create?error=" + e.getMessage());
        }
    }
}
