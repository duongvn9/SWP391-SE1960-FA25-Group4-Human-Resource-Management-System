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

/**
 * Controller để thay thế hợp đồng cũ bằng hợp đồng mới
 * Khi tạo hợp đồng mới thành công, hợp đồng cũ sẽ được terminate
 */
@WebServlet("/contracts/replace")
public class ContractReplaceController extends HttpServlet {
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

            // Get old contract ID from parameter
            String oldContractIdParam = request.getParameter("oldContractId");
            if (oldContractIdParam == null || oldContractIdParam.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/contracts?error=" + 
                    java.net.URLEncoder.encode("Old contract ID is required", "UTF-8"));
                return;
            }

            Long oldContractId = Long.parseLong(oldContractIdParam);
            
            // Get old contract
            Optional<EmploymentContract> oldContractOpt = contractDao.findById(oldContractId);
            if (!oldContractOpt.isPresent()) {
                response.sendRedirect(request.getContextPath() + "/contracts?error=" + 
                    java.net.URLEncoder.encode("Old contract not found", "UTF-8"));
                return;
            }

            EmploymentContract oldContract = oldContractOpt.get();
            
            // Validate old contract is active
            if (!"active".equalsIgnoreCase(oldContract.getStatus())) {
                response.sendRedirect(request.getContextPath() + "/contracts?error=" + 
                    java.net.URLEncoder.encode("Can only replace active contracts", "UTF-8"));
                return;
            }

            // Get user info
            Optional<User> userOpt = userDao.findById(oldContract.getUserId());
            if (!userOpt.isPresent()) {
                response.sendRedirect(request.getContextPath() + "/contracts?error=" + 
                    java.net.URLEncoder.encode("User not found", "UTF-8"));
                return;
            }

            User contractUser = userOpt.get();

            // Generate new contract number
            String generatedContractNo = contractDao.generateContractNo();

            List<Department> departments = departmentDao.findAll();
            List<Position> positions = positionDao.findAll();

            // Set attributes for form
            // Prepare data for contract-form.jsp (reuse existing form)
            request.setAttribute("oldContract", oldContract);
            request.setAttribute("contractUser", contractUser);
            request.setAttribute("generatedContractNo", generatedContractNo);
            request.setAttribute("departments", departments);
            request.setAttribute("positions", positions);
            request.setAttribute("isReplaceMode", true); // Flag to indicate replace mode
            
            // Create a list with single user for the form
            List<User> users = new java.util.ArrayList<>();
            users.add(contractUser);
            request.setAttribute("users", users);
            request.setAttribute("preSelectedUserId", contractUser.getId());

            request.getRequestDispatcher("/WEB-INF/views/contracts/contract-form.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Error loading replace contract form", e);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/contracts?error=" + 
                java.net.URLEncoder.encode("Invalid contract ID", "UTF-8"));
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
            String oldContractIdStr = request.getParameter("oldContractId");
            String contractNo = request.getParameter("contractNo");
            String contractType = request.getParameter("contractType");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String baseSalaryStr = request.getParameter("baseSalary");
            String currency = request.getParameter("currency");
            String note = request.getParameter("note");

            // Parse data
            Long oldContractId = Long.parseLong(oldContractIdStr);
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = (endDateStr != null && !endDateStr.isEmpty())
                    ? LocalDate.parse(endDateStr) : null;
            BigDecimal baseSalary = new BigDecimal(baseSalaryStr);

            // Get old contract
            Optional<EmploymentContract> oldContractOpt = contractDao.findById(oldContractId);
            if (!oldContractOpt.isPresent()) {
                response.sendRedirect(request.getContextPath() + "/contracts?error=" + 
                    java.net.URLEncoder.encode("Old contract not found", "UTF-8"));
                return;
            }

            EmploymentContract oldContract = oldContractOpt.get();
            
            // Validate old contract is still active
            if (!"active".equalsIgnoreCase(oldContract.getStatus())) {
                response.sendRedirect(request.getContextPath() + "/contracts?error=" + 
                    java.net.URLEncoder.encode("Can only replace active contracts", "UTF-8"));
                return;
            }

            // Validation
            String errorMessage = null;

            // Validation 1: Ngày bắt đầu phải trước ngày kết thúc
            if (endDate != null && !startDate.isBefore(endDate)) {
                errorMessage = "Start date must be before end date";
            }
            // Validation 2: Nếu loại hợp đồng là indefinite thì không được có end date
            else if ("indefinite".equalsIgnoreCase(contractType) && endDate != null) {
                errorMessage = "Indefinite contracts cannot have an end date";
            }
            // Validation 3: Nếu loại hợp đồng là fixed_term hoặc probation thì phải có end date
            else if (("fixed_term".equalsIgnoreCase(contractType) || "probation".equalsIgnoreCase(contractType)) && endDate == null) {
                errorMessage = "Fixed-term and probation contracts must have an end date";
            }

            // If validation failed, return to form with data
            if (errorMessage != null) {
                Optional<User> userOpt = userDao.findById(oldContract.getUserId());
                User contractUser = userOpt.get();
                
                List<Department> departments = departmentDao.findAll();
                List<Position> positions = positionDao.findAll();

                // Create temporary contract object for form data
                EmploymentContract formData = new EmploymentContract();
                formData.setUserId(oldContract.getUserId());
                formData.setContractNo(contractNo);
                formData.setContractType(contractType);
                formData.setStartDate(startDate);
                formData.setEndDate(endDate);
                formData.setBaseSalary(baseSalary);
                formData.setCurrency(currency);
                formData.setNote(note);

                request.setAttribute("oldContract", oldContract);
                request.setAttribute("contractUser", contractUser);
                request.setAttribute("formData", formData);
                request.setAttribute("errorMessage", errorMessage);
                request.setAttribute("departments", departments);
                request.setAttribute("positions", positions);
                request.setAttribute("isReplaceMode", true);
                
                // Create a list with single user for the form
                List<User> users = new java.util.ArrayList<>();
                users.add(contractUser);
                request.setAttribute("users", users);
                request.setAttribute("preSelectedUserId", contractUser.getId());

                request.getRequestDispatcher("/WEB-INF/views/contracts/contract-form.jsp")
                        .forward(request, response);
                return;
            }

            // Create new contract with draft status and pending approval
            EmploymentContract newContract = new EmploymentContract();
            newContract.setUserId(oldContract.getUserId());
            newContract.setContractNo(contractNo);
            newContract.setContractType(contractType);
            newContract.setStartDate(startDate);
            newContract.setEndDate(endDate);
            newContract.setBaseSalary(baseSalary);
            newContract.setCurrency(currency);
            newContract.setNote(note);
            newContract.setStatus("draft"); // Status is draft until approved
            newContract.setApprovalStatus("pending"); // Waiting for HRM approval
            newContract.setCreatedByAccountId(currentUser.getId());

            // Save new contract to database
            contractDao.save(newContract);

            // IMPORTANT: Only terminate old contract AFTER new contract is successfully created
            contractDao.updateStatus(oldContractId, "terminated");

            response.sendRedirect(request.getContextPath() +
                "/contracts?success=" + java.net.URLEncoder.encode(
                    "New contract created and old contract terminated successfully", "UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/contracts?error=" + 
                java.net.URLEncoder.encode("Error replacing contract: " + e.getMessage(), "UTF-8"));
        }
    }
}
