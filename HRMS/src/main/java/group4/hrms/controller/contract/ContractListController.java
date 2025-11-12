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
            int pageSize = 20; // Increased from 10 for better performance
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

            // OPTIMIZATION: Batch update expired contracts ONCE before loading
            // This is much faster than updating contracts one by one in a loop
            contractDao.batchUpdateExpiredContracts();

            // Calculate offset for pagination
            int offset = (page - 1) * pageSize;

            // Get current user
            User currentUser = (User) request.getSession().getAttribute("user");

            // OPTIMIZATION: Get contracts with filters and pagination in ONE optimized query
            // This replaces the old approach of:
            // 1. findAll() - 1 query
            // 2. findById() for each contract's user - N queries
            // 3. findById() for each contract's creator - N queries
            // 4. findById() for each contract's approver - N queries
            // Total: 1 + 3N queries
            // New approach: Just 2 queries (1 for data, 1 for count)
            
            // Check if current user is HRM (position_id = 7) to prioritize pending contracts
            boolean isHRM = currentUser != null && currentUser.getPositionId() != null && currentUser.getPositionId() == 7;
            
            List<EmploymentContractDto> contracts = contractDao.findWithFilters(
                searchQuery, statusFilter, approvalStatusFilter, typeFilter, offset, pageSize, isHRM
            );

            // OPTIMIZATION: Get total count with same filters (ONE query)
            int totalContracts = contractDao.countWithFilters(
                searchQuery, statusFilter, approvalStatusFilter, typeFilter
            );

            // Calculate pagination
            int totalPages = (int) Math.ceil((double) totalContracts / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages && totalPages > 0) page = totalPages;

            // Load users without contract (for collapsible section)
            List<UserProfile> usersWithoutContract = loadUsersWithoutContract(currentUser);

            // Set attributes
            request.setAttribute("contracts", contracts);
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

    /**
     * Load users without contract (extracted to separate method for clarity)
     * Users without contract = users that don't have ANY contract record (regardless of status)
     */
    private List<UserProfile> loadUsersWithoutContract(User currentUser) throws SQLException {
        List<UserProfile> usersWithoutContract = new ArrayList<>();
        
        if (currentUser == null) {
            return usersWithoutContract;
        }

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

        return usersWithoutContract;
    }
}
