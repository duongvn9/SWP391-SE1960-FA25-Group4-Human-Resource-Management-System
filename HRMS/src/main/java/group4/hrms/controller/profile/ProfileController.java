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
            logger.debug("Update data: {}", dto);
            
            // Alt Flow 1: Check if there are any changes
            if (!hasChanges(currentProfile, dto)) {
                logger.info("No changes detected for user_id: {}", currentProfile.getUserId());
                req.getSession().setAttribute("successMessage", "No information has been changed.");
                resp.sendRedirect(req.getContextPath() + "/user-profile/edit");
                return;
            }
            
            // Validate DTO
            if (!dto.validate()) {
                logger.warn("Validation failed: {}", dto.getErrors());
                req.setAttribute("error", String.join(", ", dto.getErrors()));
                req.setAttribute("profile", currentProfile);
                req.setAttribute("csrfToken", generateCsrfToken());
                req.getRequestDispatcher("/WEB-INF/views/profile/edit-profile.jsp").forward(req, resp);
                return;
            }
            
            // Validate: Cannot clear existing data (cannot set to empty if already has value)
            String clearFieldError = validateNoClearingData(currentProfile, dto);
            if (clearFieldError != null) {
                logger.warn("Attempt to clear existing data: {}", clearFieldError);
                req.setAttribute("error", clearFieldError);
                req.setAttribute("profile", currentProfile);
                req.setAttribute("csrfToken", generateCsrfToken());
                req.getRequestDispatcher("/WEB-INF/views/profile/edit-profile.jsp").forward(req, resp);
                return;
            }
            
            // Check uniqueness constraints
            if (dto.getCccd() != null && !dto.getCccd().trim().isEmpty()) {
                if (userProfileDao.isCccdExistsForOtherUser(dto.getCccd(), currentProfile.getUserId())) {
                    logger.warn("CCCD already exists: {}", dto.getCccd());
                    req.setAttribute("error", "CCCD already exists");
                    req.setAttribute("profile", currentProfile);
                    req.setAttribute("csrfToken", generateCsrfToken());
                    req.getRequestDispatcher("/WEB-INF/views/profile/edit-profile.jsp").forward(req, resp);
                    return;
                }
            }
            
            if (userProfileDao.isEmailExistsForOtherUser(dto.getEmailCompany(), currentProfile.getUserId())) {
                logger.warn("Email already exists: {}", dto.getEmailCompany());
                req.setAttribute("error", "Email already exists");
                req.setAttribute("profile", currentProfile);
                req.setAttribute("csrfToken", generateCsrfToken());
                req.getRequestDispatcher("/WEB-INF/views/profile/edit-profile.jsp").forward(req, resp);
                return;
            }
            
            // Update profile
            UserProfile updatedProfile = new UserProfile();
            updatedProfile.setFullName(dto.getFullName());
            updatedProfile.setDob(dto.getDob());
            // Normalize gender to lowercase before saving
            updatedProfile.setGender(dto.getGender() != null ? dto.getGender().toLowerCase() : null);
            updatedProfile.setHometown(dto.getHometown());
            updatedProfile.setCccd(dto.getCccd());
            updatedProfile.setCccdIssuedDate(dto.getCccdIssuedDate());
            updatedProfile.setCccdIssuedPlace(dto.getCccdIssuedPlace());
            updatedProfile.setEmailCompany(dto.getEmailCompany());
            updatedProfile.setPhone(dto.getPhone());
            updatedProfile.setAddressLine1(dto.getAddressLine1());
            updatedProfile.setAddressLine2(dto.getAddressLine2());
            updatedProfile.setCity(dto.getCity());
            updatedProfile.setState(dto.getState());
            updatedProfile.setPostalCode(dto.getPostalCode());
            updatedProfile.setCountry(dto.getCountry());
            
            boolean success = userProfileDao.updateProfile(currentProfile.getUserId(), updatedProfile);
            
            if (success) {
                logger.info("Profile updated successfully for user_id: {}", currentProfile.getUserId());
                req.getSession().setAttribute("successMessage", "Profile updated successfully");
                resp.sendRedirect(req.getContextPath() + "/user-profile/edit"); // Stay on edit page
            } else {
                logger.error("Failed to update profile for user_id: {}", currentProfile.getUserId());
                req.setAttribute("error", "Failed to update profile");
                req.setAttribute("profile", currentProfile);
                req.setAttribute("csrfToken", generateCsrfToken());
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
        // Compare all editable fields
        if (!equals(current.getFullName(), dto.getFullName())) return true;
        if (!equals(current.getPhone(), dto.getPhone())) return true;
        if (!equals(current.getGender(), dto.getGender())) return true;
        if (!equals(current.getHometown(), dto.getHometown())) return true;
        if (!equals(current.getCccd(), dto.getCccd())) return true;
        if (!equals(current.getCccdIssuedPlace(), dto.getCccdIssuedPlace())) return true;
        if (!equals(current.getAddressLine1(), dto.getAddressLine1())) return true;
        if (!equals(current.getAddressLine2(), dto.getAddressLine2())) return true;
        if (!equals(current.getCity(), dto.getCity())) return true;
        if (!equals(current.getState(), dto.getState())) return true;
        if (!equals(current.getPostalCode(), dto.getPostalCode())) return true;
        if (!equals(current.getCountry(), dto.getCountry())) return true;
        
        // Compare dates
        if (!equals(current.getDob(), dto.getDob())) return true;
        if (!equals(current.getCccdIssuedDate(), dto.getCccdIssuedDate())) return true;
        
        return false;
    }
    
    /**
     * Helper method to compare two objects (handles null)
     */
    private boolean equals(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) return true;
        if (obj1 == null || obj2 == null) return false;
        
        // Trim strings before comparison
        if (obj1 instanceof String && obj2 instanceof String) {
            return ((String) obj1).trim().equals(((String) obj2).trim());
        }
        
        return obj1.equals(obj2);
    }
    
    /**
     * Validate that user is not clearing existing data
     * Returns error message if trying to clear data, null if OK
     */
    private String validateNoClearingData(UserProfile current, UserProfileDto dto) {
        // Check each field - if current has value, new value cannot be empty
        if (hasValue(current.getFullName()) && !hasValue(dto.getFullName())) {
            return "Cannot clear Full Name - this field cannot be empty";
        }
        if (hasValue(current.getPhone()) && !hasValue(dto.getPhone())) {
            return "Cannot clear Phone Number - this field cannot be empty";
        }
        if (current.getDob() != null && dto.getDob() == null) {
            return "Cannot clear Date of Birth - this field cannot be empty";
        }
        if (hasValue(current.getGender()) && !hasValue(dto.getGender())) {
            return "Cannot clear Gender - this field cannot be empty";
        }
        if (hasValue(current.getCccd()) && !hasValue(dto.getCccd())) {
            return "Cannot clear Citizen ID - this field cannot be empty";
        }
        if (current.getCccdIssuedDate() != null && dto.getCccdIssuedDate() == null) {
            return "Cannot clear CCCD Issued Date - this field cannot be empty";
        }
        if (hasValue(current.getCccdIssuedPlace()) && !hasValue(dto.getCccdIssuedPlace())) {
            return "Cannot clear CCCD Issued Place - this field cannot be empty";
        }
        if (hasValue(current.getCountry()) && !hasValue(dto.getCountry())) {
            return "Cannot clear Country - this field cannot be empty";
        }
        if (hasValue(current.getAddressLine1()) && !hasValue(dto.getAddressLine1())) {
            return "Cannot clear Address Line 1 - this field cannot be empty";
        }
        if (hasValue(current.getCity()) && !hasValue(dto.getCity())) {
            return "Cannot clear City - this field cannot be empty";
        }
        
        return null; // No errors
    }
    
    /**
     * Check if a string has value (not null and not empty after trim)
     */
    private boolean hasValue(String str) {
        return str != null && !str.trim().isEmpty();
    }
}