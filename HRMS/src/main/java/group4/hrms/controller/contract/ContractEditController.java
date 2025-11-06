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
            // Get current user
            User currentUser = (User) request.getSession().getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            
            // Get contract ID
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/contracts?error=Missing contract ID");
                return;
            }
            
            Long id = Long.parseLong(idStr);
            Optional<EmploymentContract> contractOpt = contractDao.findById(id);
            
            if (!contractOpt.isPresent()) {
                response.sendRedirect(request.getContextPath() + "/contracts?error=Contract not found");
                return;
            }
            
            EmploymentContract contract = contractOpt.get();
            
            // Check if contract can be edited (use user ID)
            if (!contract.canBeEditedBy(currentUser.getId())) {
                response.sendRedirect(request.getContextPath() + 
                    "/contracts?error=" + java.net.URLEncoder.encode(
                        "You do not have permission to edit this contract. Approval Status: " + contract.getApprovalStatus(), "UTF-8"));
                return;
            }
             
            // Load reference data
            List<User> users = userDao.findAll();
            List<Department> departments = departmentDao.findAll();
            List<Position> positions = positionDao.findAll();
            
            request.setAttribute("contract", contract);
            request.setAttribute("users", users);
            request.setAttribute("departments", departments);
            request.setAttribute("positions", positions);
            request.setAttribute("isEdit", true);
            
            request.getRequestDispatcher("/WEB-INF/views/contracts/contract-form.jsp")
                    .forward(request, response);
                    
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/contracts?error=" + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get current user
            User currentUser = (User) request.getSession().getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            
            Long id = Long.parseLong(request.getParameter("id"));
            Optional<EmploymentContract> contractOpt = contractDao.findById(id);
            
            if (!contractOpt.isPresent()) {
                response.sendRedirect(request.getContextPath() + "/contracts?error=Contract not found");
                return;
            }
            
            EmploymentContract contract = contractOpt.get();
            
            // Check if contract can be edited (use user ID)
            if (!contract.canBeEditedBy(currentUser.getId())) {
                response.sendRedirect(request.getContextPath() + 
                    "/contracts?error=" + java.net.URLEncoder.encode(
                        "You do not have permission to edit this contract", "UTF-8"));
                return;
            }
            
            // Get form data
            String contractNo = request.getParameter("contractNo");
            String contractType = request.getParameter("contractType");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String baseSalaryStr = request.getParameter("baseSalary");
            String currency = request.getParameter("currency");
            String note = request.getParameter("note");
            
            // Parse data
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = (endDateStr != null && !endDateStr.isEmpty()) 
                    ? LocalDate.parse(endDateStr) : null;
            BigDecimal baseSalary = new BigDecimal(baseSalaryStr);
            
            String errorMessage = null;
            
            // Validation
            if (endDate != null && !startDate.isBefore(endDate)) {
                errorMessage = "Start date must be before end date";
            } else if ("indefinite".equalsIgnoreCase(contractType) && endDate != null) {
                errorMessage = "Indefinite contracts cannot have an end date";
            } else if (("fixed_term".equalsIgnoreCase(contractType) || "probation".equalsIgnoreCase(contractType)) && endDate == null) {
                errorMessage = "Fixed-term and probation contracts must have an end date";
            }
            
            if (errorMessage != null) {
                // Return to form with error
                List<User> users = userDao.findAll();
                List<Department> departments = departmentDao.findAll();
                List<Position> positions = positionDao.findAll();
                
                contract.setContractNo(contractNo);
                contract.setContractType(contractType);
                contract.setStartDate(startDate);
                contract.setEndDate(endDate);
                contract.setBaseSalary(baseSalary);
                contract.setCurrency(currency);
                contract.setNote(note);
                
                request.setAttribute("contract", contract);
                request.setAttribute("users", users);
                request.setAttribute("departments", departments);
                request.setAttribute("positions", positions);
                request.setAttribute("errorMessage", errorMessage);
                request.setAttribute("isEdit", true);
                
                request.getRequestDispatcher("/WEB-INF/views/contracts/contract-form.jsp")
                        .forward(request, response);
                return;
            }
            
            // Update contract fields
            contract.setContractNo(contractNo);
            contract.setContractType(contractType);
            contract.setStartDate(startDate);
            contract.setEndDate(endDate);
            contract.setBaseSalary(baseSalary);
            contract.setCurrency(currency);
            contract.setNote(note);
            
            // If contract was REJECTED, reset to PENDING for re-approval
            if ("rejected".equalsIgnoreCase(contract.getApprovalStatus())) {
                contract.setApprovalStatus("pending");
                contract.setRejectedReason(null);
                contract.setApprovedByAccountId(null);
                contract.setApprovedAt(null);
            }
            
            // Update in database
            contractDao.update(contract);
            
            String successMessage = "rejected".equalsIgnoreCase(contract.getApprovalStatus())
                ? "Contract updated and resubmitted for approval"
                : "Contract updated successfully";
            
            response.sendRedirect(request.getContextPath() + 
                "/contracts?success=" + java.net.URLEncoder.encode(successMessage, "UTF-8"));
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/contracts?error=" + e.getMessage());
        }
    }
}
        


