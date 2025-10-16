package group4.hrms.controller.admin;

import group4.hrms.dao.RoleDao;
import group4.hrms.dto.RoleDto;
import group4.hrms.model.Role;
import group4.hrms.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controller for Role Management
 * Handles CRUD operations for roles
 */
@WebServlet(urlPatterns = {"/admin/roles", "/admin/roles/new", "/admin/roles/edit", "/admin/roles/delete"})
public class RoleController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    private final RoleDao roleDao;
    
    public RoleController() {
        this.roleDao = new RoleDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String path = req.getServletPath();
        logger.debug("GET {} - Loading role management page", path);
        
        // Check authentication
        if (!SessionUtil.isUserLoggedIn(req)) {
            logger.warn("User not logged in, redirecting to login page");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        try {
            switch (path) {
                case "/admin/roles/new":
                    showNewRoleForm(req, resp);
                    break;
                case "/admin/roles/edit":
                    showEditRoleForm(req, resp);
                    break;
                default:
                    showRoleList(req, resp);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error in role management", e);
            req.setAttribute("error", "An error occurred while processing your request");
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String path = req.getServletPath();
        logger.debug("POST {} - Processing role action", path);
        
        // Check authentication
        if (!SessionUtil.isUserLoggedIn(req)) {
            logger.warn("User not logged in, redirecting to login page");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        // Validate CSRF token
        if (!validateCsrfToken(req)) {
            logger.warn("Invalid CSRF token in role action request");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            req.setAttribute("error", "Invalid request. Please try again.");
            doGet(req, resp);
            return;
        }
        
        try {
            switch (path) {
                case "/admin/roles/new":
                    createRole(req, resp);
                    break;
                case "/admin/roles/edit":
                    updateRole(req, resp);
                    break;
                case "/admin/roles/delete":
                    deleteRole(req, resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/admin/roles");
                    break;
            }
        } catch (Exception e) {
            logger.error("Error processing role action", e);
            req.setAttribute("error", "An error occurred while processing your request");
            doGet(req, resp);
        }
    }
    
    /**
     * Show list of all roles with search and pagination
     */
    private void showRoleList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // Get search parameter
        String searchKeyword = req.getParameter("keyword");
        
        // Get all roles
        List<Role> roles = roleDao.findAll();
        
        // Filter by search keyword if provided
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            String keyword = searchKeyword.trim().toLowerCase();
            roles = roles.stream()
                    .filter(role -> role.getName().toLowerCase().contains(keyword) ||
                                  role.getCode().toLowerCase().contains(keyword) ||
                                  String.valueOf(role.getId()).contains(keyword))
                    .toList();
            req.setAttribute("keyword", searchKeyword);
        }
        
        // Pagination
        int page = 1;
        int pageSize = 3;
        
        String pageParam = req.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        int totalRoles = roles.size();
        int totalPages = (int) Math.ceil((double) totalRoles / pageSize);
        
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalRoles);
        
        List<Role> paginatedRoles = roles.subList(startIndex, endIndex);
        
        // Generate CSRF token
        String csrfToken = generateCsrfToken();
        req.getSession().setAttribute("_csrf_token", csrfToken);
        
        // Set attributes
        req.setAttribute("roles", paginatedRoles);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalRoles", totalRoles);
        req.setAttribute("csrfToken", csrfToken);
        
        logger.info("Displaying {} roles (page {}/{})", paginatedRoles.size(), page, totalPages);
        
        req.getRequestDispatcher("/WEB-INF/views/admin/roles/list.jsp").forward(req, resp);
    }
    
    /**
     * Show form to create new role
     */
    private void showNewRoleForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // Generate CSRF token
        String csrfToken = generateCsrfToken();
        req.getSession().setAttribute("_csrf_token", csrfToken);
        req.setAttribute("csrfToken", csrfToken);
        
        req.getRequestDispatcher("/WEB-INF/views/admin/roles/new.jsp").forward(req, resp);
    }
    
    /**
     * Show form to edit existing role
     */
    private void showEditRoleForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/roles");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            Optional<Role> roleOpt = roleDao.findById(id);
            
            if (!roleOpt.isPresent()) {
                req.getSession().setAttribute("errorMessage", "Role not found");
                resp.sendRedirect(req.getContextPath() + "/admin/roles");
                return;
            }
            
            Role role = roleOpt.get();
            
            // Generate CSRF token
            String csrfToken = generateCsrfToken();
            req.getSession().setAttribute("_csrf_token", csrfToken);
            
            req.setAttribute("role", role);
            req.setAttribute("csrfToken", csrfToken);
            
            req.getRequestDispatcher("/WEB-INF/views/admin/roles/edit.jsp").forward(req, resp);
            
        } catch (NumberFormatException e) {
            logger.error("Invalid role ID format: {}", idParam);
            resp.sendRedirect(req.getContextPath() + "/admin/roles");
        }
    }
    
    /**
     * Create new role
     */
    private void createRole(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // Build DTO from request
        RoleDto dto = buildDtoFromRequest(req);
        
        logger.info("Creating new role: {}", dto);
        
        // Validate DTO
        if (!dto.validate()) {
            logger.warn("Validation failed: {}", dto.getErrors());
            req.setAttribute("error", String.join(", ", dto.getErrors()));
            req.setAttribute("dto", dto);
            showNewRoleForm(req, resp);
            return;
        }
        
        // Check if code already exists
        if (roleDao.existsByCode(dto.getCode())) {
            logger.warn("Role code already exists: {}", dto.getCode());
            req.setAttribute("error", "Role code already exists");
            req.setAttribute("dto", dto);
            showNewRoleForm(req, resp);
            return;
        }
        
        // Create role
        Role role = new Role();
        role.setCode(dto.getCode().trim().toUpperCase());
        role.setName(dto.getName().trim());
        role.setPriority(dto.getPriority() != null ? dto.getPriority() : 0);
        role.setIsSystem(false); // User-created roles are never system roles
        
        Optional<Role> createdRole = roleDao.create(role);
        
        if (createdRole.isPresent()) {
            logger.info("Role created successfully: {}", createdRole.get().getCode());
            req.getSession().setAttribute("successMessage", "Role created successfully");
            resp.sendRedirect(req.getContextPath() + "/admin/roles");
        } else {
            logger.error("Failed to create role: {}", dto.getCode());
            req.setAttribute("error", "Failed to create role");
            req.setAttribute("dto", dto);
            showNewRoleForm(req, resp);
        }
    }
    
    /**
     * Update existing role
     */
    private void updateRole(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/roles");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            Optional<Role> roleOpt = roleDao.findById(id);
            
            if (!roleOpt.isPresent()) {
                req.getSession().setAttribute("errorMessage", "Role not found");
                resp.sendRedirect(req.getContextPath() + "/admin/roles");
                return;
            }
            
            Role existingRole = roleOpt.get();
            
            // Cannot edit system roles
            if (existingRole.isSystemRole()) {
                req.getSession().setAttribute("errorMessage", "Cannot edit system role");
                resp.sendRedirect(req.getContextPath() + "/admin/roles");
                return;
            }
            
            // Build DTO from request
            RoleDto dto = buildDtoFromRequest(req);
            
            logger.info("Updating role ID {}: {}", id, dto);
            
            // Validate DTO (only name and priority for update)
            if (dto.getName() == null || dto.getName().trim().isEmpty()) {
                req.setAttribute("error", "Role name is required");
                req.setAttribute("role", existingRole);
                showEditRoleForm(req, resp);
                return;
            }
            
            if (dto.getName().length() > 100) {
                req.setAttribute("error", "Role name must not exceed 100 characters");
                req.setAttribute("role", existingRole);
                showEditRoleForm(req, resp);
                return;
            }
            
            // Update role
            existingRole.setName(dto.getName().trim());
            existingRole.setPriority(dto.getPriority() != null ? dto.getPriority() : 0);
            
            boolean success = roleDao.update(existingRole);
            
            if (success) {
                logger.info("Role updated successfully: ID {}", id);
                req.getSession().setAttribute("successMessage", "Role updated successfully");
                resp.sendRedirect(req.getContextPath() + "/admin/roles");
            } else {
                logger.error("Failed to update role: ID {}", id);
                req.setAttribute("error", "Failed to update role");
                req.setAttribute("role", existingRole);
                showEditRoleForm(req, resp);
            }
            
        } catch (NumberFormatException e) {
            logger.error("Invalid role ID format: {}", idParam);
            resp.sendRedirect(req.getContextPath() + "/admin/roles");
        }
    }
    
    /**
     * Delete role
     */
    private void deleteRole(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/roles");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            
            // Check if role exists and is not system role
            Optional<Role> roleOpt = roleDao.findById(id);
            if (!roleOpt.isPresent()) {
                req.getSession().setAttribute("errorMessage", "Role not found");
                resp.sendRedirect(req.getContextPath() + "/admin/roles");
                return;
            }
            
            Role role = roleOpt.get();
            
            logger.info("Deleting role ID {}: {}", id, role.getCode());
            
            boolean success = roleDao.delete(id);
            
            if (success) {
                logger.info("Role deleted successfully: ID {}", id);
                req.getSession().setAttribute("successMessage", "Role deleted successfully");
            } else {
                logger.error("Failed to delete role: ID {}", id);
                req.getSession().setAttribute("errorMessage", "Failed to delete role. It may be in use.");
            }
            
            resp.sendRedirect(req.getContextPath() + "/admin/roles");
            
        } catch (NumberFormatException e) {
            logger.error("Invalid role ID format: {}", idParam);
            resp.sendRedirect(req.getContextPath() + "/admin/roles");
        }
    }
    
    /**
     * Generate CSRF token
     */
    private String generateCsrfToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    /**
     * Validate CSRF token
     */
    private boolean validateCsrfToken(HttpServletRequest request) {
        String sessionToken = (String) request.getSession().getAttribute("_csrf_token");
        String requestToken = request.getParameter("_csrf_token");
        
        return sessionToken != null && sessionToken.equals(requestToken);
    }
    
    /**
     * Build DTO from request parameters
     */
    private RoleDto buildDtoFromRequest(HttpServletRequest req) {
        RoleDto dto = new RoleDto();
        
        dto.setCode(req.getParameter("code"));
        dto.setName(req.getParameter("name"));
        
        String priorityStr = req.getParameter("priority");
        if (priorityStr != null && !priorityStr.trim().isEmpty()) {
            try {
                dto.setPriority(Integer.parseInt(priorityStr));
            } catch (NumberFormatException e) {
                logger.warn("Invalid priority format: {}", priorityStr);
                dto.setPriority(0);
            }
        }
        
        return dto;
    }
}
