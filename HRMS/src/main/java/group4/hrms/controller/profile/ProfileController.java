package group4.hrms.controller.profile;

import group4.hrms.dao.UserProfileDao;
import group4.hrms.dto.UserProfileDto;
import group4.hrms.model.Account;
import group4.hrms.model.UserProfile;
import group4.hrms.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/user-profile", "/user-profile/edit", "/user-profile/update"})
public class ProfileController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private final UserProfileDao userProfileDao;
    
    public ProfileController() {
        this.userProfileDao = new UserProfileDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // E2: Check session timeout
        if (req.getSession(false) == null) {
            logger.warn("Session expired");
            resp.sendRedirect(req.getContextPath() + "/login?message=Session expired. Please login again");
            return;
        }
        
        String path = req.getServletPath();
        logger.debug("GET {} - Loading user profile page", path);
        
        // Kiểm tra authentication
        if (!SessionUtil.isUserLoggedIn(req)) {
            logger.warn("User not logged in, redirecting to login page");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        try {
            // Lấy account ID từ session
            Long accountId = SessionUtil.getCurrentAccountId(req);
            if (accountId == null) {
                logger.error("Account ID not found in session");
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            
            String username = SessionUtil.getCurrentUsername(req);
            logger.info("Loading profile for account_id: {}, username: {}", accountId, username);
            
            // Lấy user profile từ database
            Optional<UserProfile> profileOpt = userProfileDao.findByAccountId(accountId);
            
            if (!profileOpt.isPresent()) {
                logger.error("User profile not found for account_id: {}", accountId);
                req.setAttribute("error", "Profile not found");
                req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
                return;
            }
            
            UserProfile profile = profileOpt.get();
            logger.debug("Profile loaded successfully: {}", profile);
            
            // Generate CSRF token
            String csrfToken = generateCsrfToken();
            req.getSession().setAttribute("_csrf_token", csrfToken);
            
            // Set attributes for JSP
            req.setAttribute("profile", profile);
            req.setAttribute("csrfToken", csrfToken);
            
            // Forward to appropriate page based on path
            if (path.equals("/user-profile/edit")) {
                req.getRequestDispatcher("/WEB-INF/views/profile/edit-profile.jsp").forward(req, resp);
            } else {
                // Default: view profile
                req.getRequestDispatcher("/WEB-INF/views/profile/view-profile.jsp").forward(req, resp);
            }
            
        } catch (Exception e) {
            logger.error("Error loading user profile", e);
            req.setAttribute("error", "An error occurred while loading your profile");
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        logger.debug("POST /user-profile/update - Updating user profile");
        
        // Kiểm tra authentication
        if (!SessionUtil.isUserLoggedIn(req)) {
            logger.warn("User not logged in, redirecting to login page");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        try {
            // Validate CSRF token
            if (!validateCsrfToken(req)) {
                logger.warn("Invalid CSRF token in profile update request");
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                req.setAttribute("error", "Invalid request. Please try again.");
                doGet(req, resp);
                return;
            }
            
            // Lấy account ID từ session
            Long accountId = SessionUtil.getCurrentAccountId(req);
            if (accountId == null) {
                logger.error("Account ID not found in session");
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            
            String username = SessionUtil.getCurrentUsername(req);
            
            // Lấy current profile
            Optional<UserProfile> currentProfileOpt = userProfileDao.findByAccountId(accountId);
            if (!currentProfileOpt.isPresent()) {
                logger.error("User profile not found for account_id: {}", accountId);
                req.setAttribute("error", "Profile not found");
                doGet(req, resp);
                return;
            }
            
            UserProfile currentProfile = currentProfileOpt.get();
            
            // Build DTO from request parameters
            UserProfileDto dto = buildDtoFromRequest(req);
            
            logger.info("Updating profile for user_id: {}, username: {}", 
                currentProfile.getUserId(), username);
            logger.info("=== DEBUG INFO ===");
            logger.info("Gender from request: '{}'", req.getParameter("gender"));
            logger.info("Gender in DTO: '{}'", dto.getGender());
            logger.info("Gender in DB: '{}'", currentProfile.getGender());
            logger.info("Phone from request: '{}'", req.getParameter("phone"));
            logger.info("Phone in DTO: '{}'", dto.getPhone());
            logger.info("Phone in DB: '{}'", currentProfile.getPhone());
            logger.info("CCCD from request: '{}'", req.getParameter("cccd"));
            logger.info("CCCD in DTO: '{}'", dto.getCccd());
            logger.info("CCCD in DB: '{}'", currentProfile.getCccd());
            logger.info("==================");
            
            // Validate DTO first
            boolean isValid = dto.validate();
            logger.info("Validation result: {}", isValid);
            if (!isValid) {
                logger.warn("Validation failed: {}", dto.getErrors());
                req.setAttribute("error", String.join(", ", dto.getErrors()));
                // Set profile with user's input (not DB values) to show what they entered
                UserProfile profileWithInput = createProfileFromDto(currentProfile, dto);
                req.setAttribute("profile", profileWithInput);
                // Generate new CSRF token and save to session
                String newCsrfToken = generateCsrfToken();
                req.getSession().setAttribute("_csrf_token", newCsrfToken);
                req.setAttribute("csrfToken", newCsrfToken);
                logger.info("Forwarding back to edit page with errors");
                req.getRequestDispatcher("/WEB-INF/views/profile/edit-profile.jsp").forward(req, resp);
                return;
            }
            
            // E1: Validate age >= 18
            if (dto.getDob() != null) {
                LocalDate today = LocalDate.now();
                int age = today.getYear() - dto.getDob().getYear();
                
                // Adjust age if birthday hasn't occurred this year yet
                if (today.getMonthValue() < dto.getDob().getMonthValue() ||
                    (today.getMonthValue() == dto.getDob().getMonthValue() && today.getDayOfMonth() < dto.getDob().getDayOfMonth())) {
                    age--;
                }
                
                if (age < 18) {
                    logger.warn("Age validation failed: age={}, dob={}", age, dto.getDob());
                    req.setAttribute("error", "Age must be at least 18 years old");
                    UserProfile profileWithInput = createProfileFromDto(currentProfile, dto);
                    req.setAttribute("profile", profileWithInput);
                    // Generate new CSRF token and save to session
                    String newCsrfToken = generateCsrfToken();
                    req.getSession().setAttribute("_csrf_token", newCsrfToken);
                    req.setAttribute("csrfToken", newCsrfToken);
                    req.getRequestDispatcher("/WEB-INF/views/profile/edit-profile.jsp").forward(req, resp);
                    return;
                }
            }
            
            logger.info("Validation passed, continuing to update...");
            
            // Check uniqueness constraints
            if (dto.getCccd() != null && !dto.getCccd().trim().isEmpty()) {
                if (userProfileDao.isCccdExistsForOtherUser(dto.getCccd(), currentProfile.getUserId())) {
                    logger.warn("CCCD already exists: {}", dto.getCccd());
                    req.setAttribute("error", "CCCD already exists");
                    UserProfile profileWithInput = createProfileFromDto(currentProfile, dto);
                    req.setAttribute("profile", profileWithInput);
                    // Generate new CSRF token and save to session
                    String newCsrfToken = generateCsrfToken();
                    req.getSession().setAttribute("_csrf_token", newCsrfToken);
                    req.setAttribute("csrfToken", newCsrfToken);
                    req.getRequestDispatcher("/WEB-INF/views/profile/edit-profile.jsp").forward(req, resp);
                    return;
                }
            }
            
            if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
                if (userProfileDao.isPhoneExistsForOtherUser(dto.getPhone(), currentProfile.getUserId())) {
                    logger.warn("Phone number already exists: {}", dto.getPhone());
                    req.setAttribute("error", "Phone number already exists");
                    UserProfile profileWithInput = createProfileFromDto(currentProfile, dto);
                    req.setAttribute("profile", profileWithInput);
                    // Generate new CSRF token and save to session
                    String newCsrfToken = generateCsrfToken();
                    req.getSession().setAttribute("_csrf_token", newCsrfToken);
                    req.setAttribute("csrfToken", newCsrfToken);
                    req.getRequestDispatcher("/WEB-INF/views/profile/edit-profile.jsp").forward(req, resp);
                    return;
                }
            }
            
            
             if (!hasChanges(currentProfile, dto)) {
                 logger.info("No changes detected for user_id: {}", currentProfile.getUserId());
                 req.getSession().setAttribute("successMessage", "No information has been changed.");
                 resp.sendRedirect(req.getContextPath() + "/user-profile/edit");
                 return;
             }
            
            // Update profile
            UserProfile updatedProfile = new UserProfile();
            updatedProfile.setFullName(emptyToNull(dto.getFullName()));
            updatedProfile.setDob(dto.getDob());
            // Normalize gender to lowercase before saving
            updatedProfile.setGender(dto.getGender() != null && !dto.getGender().trim().isEmpty() 
                ? dto.getGender().toLowerCase() : null);
            updatedProfile.setHometown(emptyToNull(dto.getHometown()));
            updatedProfile.setCccd(emptyToNull(dto.getCccd()));
            updatedProfile.setCccdIssuedDate(dto.getCccdIssuedDate());
            updatedProfile.setCccdIssuedPlace(emptyToNull(dto.getCccdIssuedPlace()));
            updatedProfile.setEmailCompany(emptyToNull(dto.getEmailCompany()));
            updatedProfile.setPhone(emptyToNull(dto.getPhone()));
            updatedProfile.setAddressLine1(emptyToNull(dto.getAddressLine1()));
            updatedProfile.setAddressLine2(emptyToNull(dto.getAddressLine2()));
            updatedProfile.setCity(emptyToNull(dto.getCity()));
            updatedProfile.setState(emptyToNull(dto.getState()));
            updatedProfile.setPostalCode(emptyToNull(dto.getPostalCode()));
            updatedProfile.setCountry(emptyToNull(dto.getCountry()));
            
            boolean success = userProfileDao.updateProfile(currentProfile.getUserId(), updatedProfile);
            
            if (success) {
                logger.info("Profile updated successfully for user_id: {}", currentProfile.getUserId());
                req.getSession().setAttribute("successMessage", "Profile updated successfully");
                resp.sendRedirect(req.getContextPath() + "/user-profile/edit");
            } else {
                logger.error("Failed to update profile for user_id: {}", currentProfile.getUserId());
                req.setAttribute("error", "Failed to update profile");
                req.setAttribute("profile", currentProfile);
                // Generate new CSRF token and save to session
                String newCsrfToken = generateCsrfToken();
                req.getSession().setAttribute("_csrf_token", newCsrfToken);
                req.setAttribute("csrfToken", newCsrfToken);
                req.getRequestDispatcher("/WEB-INF/views/profile/edit-profile.jsp").forward(req, resp);
            }
            
        } catch (Exception e) {
            logger.error("Error updating user profile", e);
            req.setAttribute("error", "An error occurred while updating your profile");
            doGet(req, resp);
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
    private UserProfileDto buildDtoFromRequest(HttpServletRequest req) {
        UserProfileDto dto = new UserProfileDto();
        
        dto.setFullName(req.getParameter("fullName"));
        dto.setGender(req.getParameter("gender"));
        dto.setEmailCompany(req.getParameter("emailCompany"));
        dto.setPhone(req.getParameter("phone"));
        dto.setHometown(req.getParameter("hometown"));
        dto.setCccd(req.getParameter("cccd"));
        dto.setAddressLine1(req.getParameter("addressLine1"));
        dto.setAddressLine2(req.getParameter("addressLine2"));
        dto.setCity(req.getParameter("city"));
        dto.setState(req.getParameter("state"));
        dto.setPostalCode(req.getParameter("postalCode"));
        dto.setCountry(req.getParameter("country"));
        dto.setBankInfo(req.getParameter("bankInfo"));
        dto.setCccdIssuedPlace(req.getParameter("cccdIssuedPlace"));
        
        // Parse date of birth
        String dobStr = req.getParameter("dob");
        if (dobStr != null && !dobStr.trim().isEmpty()) {
            try {
                dto.setDob(LocalDate.parse(dobStr));
            } catch (Exception e) {
                logger.warn("Invalid date format for dob: {}", dobStr);
            }
        }
        
        // Parse CCCD issued date
        String cccdIssuedDateStr = req.getParameter("cccdIssuedDate");
        if (cccdIssuedDateStr != null && !cccdIssuedDateStr.trim().isEmpty()) {
            try {
                dto.setCccdIssuedDate(LocalDate.parse(cccdIssuedDateStr));
            } catch (Exception e) {
                logger.warn("Invalid date format for cccdIssuedDate: {}", cccdIssuedDateStr);
            }
        }
        
        return dto;
    }
    
    /**
     * Check if there are any changes between current profile and DTO
     * Alt Flow 1: Detect no changes
     */
    private boolean hasChanges(UserProfile current, UserProfileDto dto) {
        logger.info("=== CHECKING CHANGES ===");
        
        // Compare all editable fields
        if (!equals(current.getFullName(), dto.getFullName())) {
            logger.info("FullName changed: '{}' -> '{}'", current.getFullName(), dto.getFullName());
            return true;
        }
        if (!equals(current.getPhone(), dto.getPhone())) {
            logger.info("Phone changed: '{}' -> '{}'", current.getPhone(), dto.getPhone());
            return true;
        }
        // Normalize gender to lowercase before comparison
        String currentGender = current.getGender() != null ? current.getGender().toLowerCase() : null;
        String dtoGender = dto.getGender() != null ? dto.getGender().toLowerCase() : null;
        if (!equals(currentGender, dtoGender)) {
            logger.info("Gender changed: '{}' -> '{}'", currentGender, dtoGender);
            return true;
        }
        if (!equals(current.getHometown(), dto.getHometown())) {
            logger.info("Hometown changed: '{}' -> '{}'", current.getHometown(), dto.getHometown());
            return true;
        }
        if (!equals(current.getCccd(), dto.getCccd())) {
            logger.info("CCCD changed: '{}' -> '{}'", current.getCccd(), dto.getCccd());
            return true;
        }
        if (!equals(current.getCccdIssuedPlace(), dto.getCccdIssuedPlace())) {
            logger.info("CCCD Issued Place changed");
            return true;
        }
        if (!equals(current.getAddressLine1(), dto.getAddressLine1())) {
            logger.info("Address Line 1 changed");
            return true;
        }
        if (!equals(current.getAddressLine2(), dto.getAddressLine2())) {
            logger.info("Address Line 2 changed");
            return true;
        }
        if (!equals(current.getCity(), dto.getCity())) {
            logger.info("City changed");
            return true;
        }
        if (!equals(current.getState(), dto.getState())) {
            logger.info("State changed");
            return true;
        }
        if (!equals(current.getPostalCode(), dto.getPostalCode())) {
            logger.info("Postal Code changed");
            return true;
        }
        if (!equals(current.getCountry(), dto.getCountry())) {
            logger.info("Country changed: '{}' -> '{}'", current.getCountry(), dto.getCountry());
            return true;
        }
        
        // Compare dates
        if (!equals(current.getDob(), dto.getDob())) {
            logger.info("DOB changed");
            return true;
        }
        if (!equals(current.getCccdIssuedDate(), dto.getCccdIssuedDate())) {
            logger.info("CCCD Issued Date changed");
            return true;
        }
        
        logger.info("No changes detected");
        return false;
    }
    
    /**
     * Helper method to compare two objects (handles null and empty strings)
     */
    private boolean equals(Object obj1, Object obj2) {
        // Handle strings specially - treat null and empty as equivalent
        if (obj1 instanceof String || obj2 instanceof String) {
            String str1 = obj1 != null ? ((String) obj1).trim() : "";
            String str2 = obj2 != null ? ((String) obj2).trim() : "";
            return str1.equals(str2);
        }
        
        // For non-strings (like dates)
        if (obj1 == null && obj2 == null) return true;
        if (obj1 == null || obj2 == null) return false;
        
        return obj1.equals(obj2);
    }
    

    /**
     * Convert empty string to null (for database storage)
     */
    private String emptyToNull(String str) {
        return (str != null && !str.trim().isEmpty()) ? str.trim() : null;
    }
    
    /**
     * Create a UserProfile object from DTO (for displaying user input on error)
     * Keeps non-editable fields from current profile, updates editable fields from DTO
     */
    private UserProfile createProfileFromDto(UserProfile current, UserProfileDto dto) {
        UserProfile profile = new UserProfile();
        
        // Copy non-editable fields from current profile
        profile.setUserId(current.getUserId());
        profile.setEmployeeCode(current.getEmployeeCode());
        profile.setDepartmentId(current.getDepartmentId());
        profile.setDepartmentName(current.getDepartmentName());
        profile.setPositionId(current.getPositionId());
        profile.setPositionName(current.getPositionName());
        profile.setStatus(current.getStatus());
        profile.setDateJoined(current.getDateJoined());
        profile.setDateLeft(current.getDateLeft());
        profile.setStartWorkDate(current.getStartWorkDate());
        profile.setAccountId(current.getAccountId());
        profile.setUsername(current.getUsername());
        profile.setEmailLogin(current.getEmailLogin());
        profile.setAccountStatus(current.getAccountStatus());
        profile.setLastLoginAt(current.getLastLoginAt());
        profile.setCreatedAt(current.getCreatedAt());
        profile.setUpdatedAt(current.getUpdatedAt());
        
        // Set editable fields from DTO (user's input)
        profile.setFullName(dto.getFullName());
        profile.setDob(dto.getDob());
        profile.setGender(dto.getGender());
        profile.setHometown(dto.getHometown());
        profile.setCccd(dto.getCccd());
        profile.setCccdIssuedDate(dto.getCccdIssuedDate());
        profile.setCccdIssuedPlace(dto.getCccdIssuedPlace());
        profile.setEmailCompany(dto.getEmailCompany());
        profile.setPhone(dto.getPhone());
        profile.setAddressLine1(dto.getAddressLine1());
        profile.setAddressLine2(dto.getAddressLine2());
        profile.setCity(dto.getCity());
        profile.setState(dto.getState());
        profile.setPostalCode(dto.getPostalCode());
        profile.setCountry(dto.getCountry());
        
        return profile;
    }
}