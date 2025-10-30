package group4.hrms.controller.profile;

import group4.hrms.dao.EmploymentContractDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.EmploymentContractDto;
import group4.hrms.model.EmploymentContract;
import group4.hrms.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;


@WebServlet("/my-contract")
public class ContractController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(ContractController.class);
    private final EmploymentContractDao contractDao;
    private final UserDao userDao;
    
    /**
     * Constructor with dependency injection
     */
    public ContractController() {
        this.contractDao = new EmploymentContractDao();
        this.userDao = new UserDao();
    }
    
    /**
     * Constructor for testing with mock DAOs
     */
    public ContractController(EmploymentContractDao contractDao, UserDao userDao) {
        this.contractDao = contractDao;
        this.userDao = userDao;
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Authentication check
        if (!SessionUtil.isUserLoggedIn(req)) {
            logger.warn("Unauthorized access attempt to /contracts");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        // 2. Get userId from session
        Long userId = SessionUtil.getCurrentUserId(req);
        if (userId == null) {
            logger.error("User ID not found in session");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        try {
            // 3. Call DAO to get active contract
            Optional<EmploymentContract> contractOpt = contractDao.findActiveContractByUser(userId);
            
            // 4. Handle result and convert to DTO
            if (contractOpt.isPresent()) {
                EmploymentContract contract = contractOpt.get();
                EmploymentContractDto dto = convertToDto(contract);
                
                // Set username and full name from session
                dto.setUsername(getUsernameFromSession(req));
                dto.setUserFullName(getFullNameFromSession(req));
                
                req.setAttribute("contract", dto);
                logger.info("Found active contract for user_id: {}", userId);
            } else {
                // No contract found
                req.setAttribute("message", "No active contract found");
                req.setAttribute("contract", null);
                logger.info("No active contract found for user_id: {}", userId);
            }
            
        } catch (SQLException e) {
            // Database error handling
            logger.error("Database error while fetching contract for user_id: {}", userId, e);
            req.setAttribute("error", "An error occurred while loading contract information. Please try again later.");
            req.setAttribute("contract", null);
        }
        
        // 5. Forward to JSP
        req.getRequestDispatcher("/WEB-INF/views/profile/contract.jsp").forward(req, resp);
    }
    
    /**
     * Convert EmploymentContract entity to DTO with formatted fields
     */
    private EmploymentContractDto convertToDto(EmploymentContract contract) {
        EmploymentContractDto dto = new EmploymentContractDto(contract);
        
        // Add formatted fields
        dto.setFormattedStartDate(formatDate(contract.getStartDate()));
        dto.setFormattedEndDate(formatDate(contract.getEndDate()));
        dto.setFormattedSalary(formatCurrency(contract.getBaseSalary(), contract.getCurrency()));
        dto.setContractTypeDisplay(formatContractType(contract.getContractType()));
        dto.setFormattedCreatedAt(formatDateTime(contract.getCreatedAt()));
        dto.setFormattedUpdatedAt(formatDateTime(contract.getUpdatedAt()));
        
        // Set status display and color
        String status = contract.getStatus();
        dto.setStatusDisplay(getStatusText(status));
        dto.setStatusColor(getStatusColor(status));
        
        return dto;
    }
    
    /**
     * Get username from session
     */
    private String getUsernameFromSession(HttpServletRequest req) {
        String username = SessionUtil.getCurrentUsername(req);
        return (username != null) ? username : "N/A";
    }
    
    /**
     * Get full name from session
     */
    private String getFullNameFromSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            String fullName = (String) session.getAttribute("userFullName");
            return (fullName != null) ? fullName : "N/A";
        }
        return "N/A";
    }
    
    /**
     * Format LocalDate to dd/MM/yyyy
     */
    private String formatDate(LocalDate date) {
        if (date == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
    
    /**
     * Format LocalDateTime to dd/MM/yyyy HH:mm:ss
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }
    
    /**
     * Format currency with thousand separator (without currency code)
     */
    private String formatCurrency(BigDecimal amount, String currency) {
        if (amount == null) {
            return "N/A";
        }
        
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        
        return formatter.format(amount);
    }
    
    /**
     * Format contract type to English
     */
    private String formatContractType(String type) {
        if (type == null) {
            return "N/A";
        }
        
        return switch (type.toLowerCase()) {
            case "indefinite" -> "Indefinite Contract";
            case "fixed_term" -> "Fixed-term Contract";
            case "probation" -> "Probation Contract";
            default -> type;
        };
    }
    
    /**
     * Get status display text in English
     */
    private String getStatusText(String status) {
        if (status == null) {
            return "N/A";
        }
        
        return switch (status.toLowerCase()) {
            case "active" -> "Active";
            case "expired" -> "Expired";
            case "terminated" -> "Terminated";
            default -> status;
        };
    }
    
    /**
     * Get status color class for Bootstrap badge
     */
    private String getStatusColor(String status) {
        if (status == null) {
            return "secondary";
        }
        
        return switch (status.toLowerCase()) {
            case "active" -> "success";
            case "expired" -> "secondary";
            case "terminated" -> "danger";
            default -> "secondary";
        };
    }
}