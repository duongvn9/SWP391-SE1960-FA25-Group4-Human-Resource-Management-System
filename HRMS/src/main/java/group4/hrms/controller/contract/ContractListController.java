package group4.hrms.controller.contract;

import group4.hrms.dao.EmploymentContractDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dao.UserProfileDao;
import group4.hrms.dto.EmploymentContractDto;
import group4.hrms.model.EmploymentContract;
import group4.hrms.model.User;
import group4.hrms.model.UserProfile;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller để hiển thị danh sách contracts
 */
@WebServlet("/contracts")
public class ContractListController extends HttpServlet {
    private EmploymentContractDao contractDao;
    private UserDao userDao;
    private UserProfileDao userProfileDao;
    
    @Override
    public void init() throws ServletException {
        contractDao = new EmploymentContractDao();
        userDao = new UserDao();
        userProfileDao = new UserProfileDao();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get filter parameters
            String searchQuery = request.getParameter("search");
            String statusFilter = request.getParameter("status");
            String approvalStatusFilter = request.getParameter("approvalStatus");
            String typeFilter = request.getParameter("type");
            
            // Get pagination parameters
            int page = 1;
            int pageSize = 10;
            try {
                if (request.getParameter("page") != null) {
                    page = Integer.parseInt(request.getParameter("page"));
                }
                if (request.getParameter("pageSize") != null) {
                    pageSize = Integer.parseInt(request.getParameter("pageSize"));
                }
            } catch (NumberFormatException e) {
                // Use default values
            }
            
            // Get all contracts
            List<EmploymentContract> allContracts = contractDao.findAll();
            
            // Convert to DTOs with user info and apply filters
            List<EmploymentContractDto> contractDtos = new ArrayList<>();
            for (EmploymentContract contract : allContracts) {
                // Auto-update status to expired if contract has ended
                String oldStatus = contract.getStatus();
                contract.updateStatusIfExpired();
                
                // Save to database if status changed
                if (!oldStatus.equals(contract.getStatus())) {
                    contractDao.update(contract);
                }
                
                EmploymentContractDto dto = new EmploymentContractDto(contract);
                
                // Get user info
                Optional<User> userOpt = userDao.findById(contract.getUserId());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    dto.setUserFullName(user.getFullName());
                    dto.setUsername(user.getEmployeeCode());
                }
                
                // Get created by full name (directly from user_id)
                if (contract.getCreatedByAccountId() != null) {
                    Optional<User> createdByOpt = userDao.findById(contract.getCreatedByAccountId());
                    if (createdByOpt.isPresent()) {
                        dto.setCreatedByName(createdByOpt.get().getFullName());
                    }
                }
                
                // Get approved by full name (directly from user_id, same as created_by)
                if (contract.getApprovedByAccountId() != null) {
                    Optional<User> approvedByOpt = userDao.findById(contract.getApprovedByAccountId());
                    if (approvedByOpt.isPresent()) {
                        dto.setApprovedByName(approvedByOpt.get().getFullName());
                    }
                }
                
                // Apply filters
                boolean matchesSearch = true;
                boolean matchesStatus = true;
                boolean matchesApprovalStatus = true;
                boolean matchesType = true;
                
                if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                    String query = searchQuery.toLowerCase();
                    matchesSearch = dto.getContractNo().toLowerCase().contains(query) ||
                                  dto.getUserFullName().toLowerCase().contains(query) ||
                                  dto.getUsername().toLowerCase().contains(query);
                }
                
                if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("all")) {
                    matchesStatus = dto.getStatus().equalsIgnoreCase(statusFilter);
                }
                
                if (approvalStatusFilter != null && !approvalStatusFilter.isEmpty() && !approvalStatusFilter.equals("all")) {
                    matchesApprovalStatus = dto.getApprovalStatus().equalsIgnoreCase(approvalStatusFilter);
                }
                
                if (typeFilter != null && !typeFilter.isEmpty() && !typeFilter.equals("all")) {
                    matchesType = dto.getContractType().equalsIgnoreCase(typeFilter);
                }
                
                if (matchesSearch && matchesStatus && matchesApprovalStatus && matchesType) {
                    contractDtos.add(dto);
                }
            }
            
            // Calculate pagination
            int totalContracts = contractDtos.size();
            int totalPages = (int) Math.ceil((double) totalContracts / pageSize);
            
            // Ensure page is within bounds
            if (page < 1) page = 1;
            if (page > totalPages && totalPages > 0) page = totalPages;
            
            // Get contracts for current page
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalContracts);
            List<EmploymentContractDto> paginatedContracts = new ArrayList<>();
            if (startIndex < totalContracts) {
                paginatedContracts = contractDtos.subList(startIndex, endIndex);
            }
            
            // Get current user
            User currentUser = (User) request.getSession().getAttribute("user");
            
            // Load users without contract (for collapsible section)
            // Users without contract = users that don't have ANY contract record (regardless of status)
            List<UserProfile> usersWithoutContract = new ArrayList<>();
            if (currentUser != null) {
                List<User> allUsers = userDao.findAll();
                List<Long> userIdsWithAnyContract = contractDao.findUserIdsWithAnyContract();
                
                for (User user : allUsers) {
                    // Skip users with ANY contract (draft/active/expired), inactive users, and current user
                    if (userIdsWithAnyContract.contains(user.getId())) {
                        continue;
                    }
                    if (!"active".equalsIgnoreCase(user.getStatus())) {
                        continue;
                    }
                    if (user.getId().equals(currentUser.getId())) {
                        continue;
                    }
                    
                    // Get full profile for this user
                    UserProfile profile = userProfileDao.findByUserId(user.getId());
                    if (profile != null) {
                        usersWithoutContract.add(profile);
                    }
                }
            }
            
            // Set attributes
            request.setAttribute("contracts", paginatedContracts);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalContracts", totalContracts);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("statusFilter", statusFilter);
            request.setAttribute("approvalStatusFilter", approvalStatusFilter);
            request.setAttribute("typeFilter", typeFilter);
            request.setAttribute("usersWithoutContract", usersWithoutContract);
            request.setAttribute("usersWithoutContractTotal", usersWithoutContract.size());
            
            // Set CSRF token for approve/reject actions
            request.setAttribute("csrfToken", group4.hrms.util.SecurityUtil.generateCsrfToken(request.getSession()));
            
            request.getRequestDispatcher("/WEB-INF/views/contracts/contract-list.jsp")
                    .forward(request, response);
                    
        } catch (SQLException e) {
            throw new ServletException("Error loading contracts", e);
        }
    }
}
