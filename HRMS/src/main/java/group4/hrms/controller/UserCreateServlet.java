package group4.hrms.controller;

import group4.hrms.dao.AccountDao;
import group4.hrms.dao.AccountRoleDao;
import group4.hrms.dao.RoleDao;
import group4.hrms.dao.UserDao;
import group4.hrms.model.Account;
import group4.hrms.model.Role;
import group4.hrms.model.User;
import group4.hrms.util.DropdownCacheUtil;
import group4.hrms.util.SessionUtil;
import group4.hrms.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Servlet xử lý User Create page
 * URL: /employees/users/create
 */
@WebServlet("/employees/users/create")
public class UserCreateServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(UserCreateServlet.class);

    private final UserDao userDao = new UserDao();
    private final AccountDao accountDao = new AccountDao();
    private final RoleDao roleDao = new RoleDao();
    private final AccountRoleDao accountRoleDao = new AccountRoleDao();

    // Validation patterns
    private static final Pattern EMPLOYEE_CODE_PATTERN = Pattern.compile("^HE\\d{4}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Check authentication
            if (!SessionUtil.isUserLoggedIn(request)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Check authorization - Only ADMIN can create users
            if (!group4.hrms.util.PermissionUtil.canCreateUser(request)) {
                logger.warn("Unauthorized access attempt to create user page");
                request.getSession().setAttribute("errorMessage", "You don't have permission to create users");
                response.sendRedirect(request.getContextPath() + "/employees/users");
                return;
            }

            // Load departments and positions for dropdowns (using cache)
            request.setAttribute("departments", DropdownCacheUtil.getCachedDepartments(getServletContext()));
            request.setAttribute("positions", DropdownCacheUtil.getCachedPositions(getServletContext()));

            // Forward to create form
            request.getRequestDispatcher("/WEB-INF/views/employees/user-create.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading user create form", e);
            request.setAttribute("errorMessage", "An error occurred. Please try again.");
            response.sendRedirect(request.getContextPath() + "/employees/users");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Check authentication
            if (!SessionUtil.isUserLoggedIn(request)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Check authorization - Only ADMIN can create users
            if (!group4.hrms.util.PermissionUtil.canCreateUser(request)) {
                logger.warn("Unauthorized attempt to create user");
                request.getSession().setAttribute("errorMessage", "You don't have permission to create users");
                response.sendRedirect(request.getContextPath() + "/employees/users");
                return;
            }

            // Get form parameters
            String employeeCode = request.getParameter("employeeCode");
            String fullName = request.getParameter("fullName");
            String dateOfBirthStr = request.getParameter("dateOfBirth");
            String phone = request.getParameter("phone");
            String emailCompany = request.getParameter("emailCompany");
            String departmentIdStr = request.getParameter("departmentId");
            String positionIdStr = request.getParameter("positionId");
            String dateJoinedStr = request.getParameter("dateJoined");
            String startWorkDateStr = request.getParameter("startWorkDate");
            String gender = request.getParameter("gender");

            // Validate all fields and collect errors
            List<String> errors = new ArrayList<>();

            // Validate full name
            if (isNullOrEmpty(fullName)) {
                errors.add("Full name is required");
            }

            // Validate date of birth
            LocalDate dateOfBirth = null;
            if (isNullOrEmpty(dateOfBirthStr)) {
                errors.add("Date of birth is required");
            } else {
                try {
                    dateOfBirth = LocalDate.parse(dateOfBirthStr);
                    if (dateOfBirth.isAfter(LocalDate.now())) {
                        errors.add("Date of birth must be in the past");
                    } else if (!ValidationUtil.isAgeValid(dateOfBirth)) {
                        errors.add("Date of birth must indicate user is at least 18 years old");
                    }
                } catch (DateTimeParseException e) {
                    errors.add("Invalid date of birth format");
                }
            }

            // Validate phone
            if (isNullOrEmpty(phone)) {
                errors.add("Phone number is required");
            } else if (!ValidationUtil.isPhoneValid(phone.trim())) {
                errors.add("Phone number must be exactly 10 digits and start with 0");
            } else if (userDao.isPhoneExists(phone.trim())) {
                errors.add("Phone number already exists");
            }

            // Validate company email
            if (isNullOrEmpty(emailCompany)) {
                errors.add("Company email is required");
            } else if (!EMAIL_PATTERN.matcher(emailCompany.trim()).matches()) {
                errors.add("Invalid email format");
            } else if (userDao.isCompanyEmailExists(emailCompany.trim())) {
                errors.add("Company email already exists");
            }

            // Validate department
            Long departmentId = null;
            if (isNullOrEmpty(departmentIdStr)) {
                errors.add("Department is required");
            } else {
                try {
                    departmentId = Long.parseLong(departmentIdStr);
                } catch (NumberFormatException e) {
                    errors.add("Invalid department selection");
                }
            }

            // Validate position
            Long positionId = null;
            if (isNullOrEmpty(positionIdStr)) {
                errors.add("Position is required");
            } else {
                try {
                    positionId = Long.parseLong(positionIdStr);
                } catch (NumberFormatException e) {
                    errors.add("Invalid position selection");
                }
            }

            // Validate date joined
            LocalDate dateJoined = null;
            if (isNullOrEmpty(dateJoinedStr)) {
                errors.add("Date joined is required");
            } else {
                try {
                    dateJoined = LocalDate.parse(dateJoinedStr);
                } catch (DateTimeParseException e) {
                    errors.add("Invalid date joined format");
                }
            }

            // Validate start work date
            LocalDate startWorkDate = null;
            if (isNullOrEmpty(startWorkDateStr)) {
                errors.add("Start work date is required");
            } else {
                try {
                    startWorkDate = LocalDate.parse(startWorkDateStr);
                } catch (DateTimeParseException e) {
                    errors.add("Invalid start work date format");
                }
            }

            // Validate gender
            if (isNullOrEmpty(gender)) {
                errors.add("Gender is required");
            } else if (!ValidationUtil.isGenderValid(gender.trim())) {
                errors.add("Gender must be either 'male' or 'female'");
            }

            // Handle employee code (auto-generate if empty)
            if (isNullOrEmpty(employeeCode)) {
                employeeCode = generateEmployeeCode();
                logger.info("Auto-generated employee code: {}", employeeCode);
            } else {
                // Validate manual employee code
                employeeCode = employeeCode.trim();
                if (!EMPLOYEE_CODE_PATTERN.matcher(employeeCode).matches()) {
                    errors.add("Invalid employee code format. Must be HExxxx (e.g., HE0001)");
                } else if (userDao.isEmployeeCodeExists(employeeCode)) {
                    errors.add("Employee code already exists");
                }
            }

            // Check manager position uniqueness and auto-create account
            // Note: Account will be auto-created with role based on position and
            // department:
            // - HR Manager position → HR_MANAGER role
            // - Department Manager in Human Resource → HR_MANAGER role
            // - Department Manager in other departments → DEPARTMENT_MANAGER role
            boolean isManager = false;
            if (positionId != null) {
                final Long finalPositionId = positionId;
                var position = DropdownCacheUtil.getCachedPositions(getServletContext()).stream()
                        .filter(p -> p.getId().equals(finalPositionId))
                        .findFirst();

                if (position.isPresent()) {
                    String positionName = position.get().getName();

                    // Check if HR-specific positions are assigned to Human Resource department
                    final Long finalDepartmentId = departmentId;
                    if (("HR Manager".equalsIgnoreCase(positionName) || "HR Staff".equalsIgnoreCase(positionName))
                            && finalDepartmentId != null) {
                        var departments = DropdownCacheUtil.getCachedDepartments(getServletContext());
                        var department = departments.stream()
                                .filter(d -> d.getId().equals(finalDepartmentId))
                                .findFirst();

                        if (department.isPresent()
                                && !"Human Resource".equalsIgnoreCase(department.get().getName())) {
                            errors.add("Position '" + positionName
                                    + "' can only be assigned to employees in the Human Resource department.");
                        }
                    }

                    // Check for Department Manager uniqueness (one per department)
                    if ("Department Manager".equalsIgnoreCase(positionName)) {
                        isManager = true;
                        if (departmentId != null) {
                            Optional<User> existingManager = userDao.findDepartmentManager(departmentId);
                            if (existingManager.isPresent()) {
                                errors.add("This department already has a manager: " +
                                        existingManager.get().getFullName() +
                                        ". Please remove the current manager first.");
                            }
                        }
                    }

                    // Check for HR Manager uniqueness (only one in the company)
                    if ("HR Manager".equalsIgnoreCase(positionName)) {
                        isManager = true;
                        List<User> existingHRManagers = userDao.findByPositionId(finalPositionId);
                        if (!existingHRManagers.isEmpty()) {
                            errors.add("The company already has an HR Manager: " +
                                    existingHRManagers.get(0).getFullName() +
                                    ". Only one HR Manager is allowed.");
                        }
                    }
                }
            }

            // If there are validation errors, return to form
            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                loadFormData(request);
                preserveFormData(request, employeeCode, fullName, dateOfBirthStr, phone, emailCompany,
                        departmentIdStr, positionIdStr, dateJoinedStr, startWorkDateStr, gender);
                request.getRequestDispatcher("/WEB-INF/views/employees/user-create.jsp")
                        .forward(request, response);
                return;
            }

            // Create User object
            User user = new User();
            user.setEmployeeCode(employeeCode);
            user.setFullName(fullName.trim());
            user.setGender(gender.trim().toLowerCase());
            user.setEmailCompany(emailCompany.trim().toLowerCase());
            user.setPhone(phone.trim());
            user.setDepartmentId(departmentId);
            user.setPositionId(positionId);
            user.setDateJoined(dateJoined);
            user.setStartWorkDate(startWorkDate);
            user.setStatus("active");

            // Save user
            Optional<User> createdUserOpt = userDao.save(user);

            if (createdUserOpt.isPresent()) {
                User createdUser = createdUserOpt.get();
                logger.info("User created successfully: {} (ID: {})", createdUser.getEmployeeCode(),
                        createdUser.getId());

                // Create department manager account if position is Department Manager
                if (isManager) {
                    try {
                        createDepartmentManagerAccount(createdUser);
                        logger.info("Department manager account created for user: {}", createdUser.getEmployeeCode());
                    } catch (Exception e) {
                        logger.error("Error creating department manager account", e);
                        // Continue anyway - user is created, account creation failed
                    }
                }

                request.getSession().setAttribute("successMessage", "User created successfully");
                response.sendRedirect(request.getContextPath() + "/employees/users");
            } else {
                errors.add("Failed to create user. Please try again.");
                request.setAttribute("errors", errors);
                loadFormData(request);
                preserveFormData(request, employeeCode, fullName, dateOfBirthStr, phone, emailCompany,
                        departmentIdStr, positionIdStr, dateJoinedStr, startWorkDateStr, gender);
                request.getRequestDispatcher("/WEB-INF/views/employees/user-create.jsp")
                        .forward(request, response);
            }

        } catch (Exception e) {
            logger.error("Error creating user", e);
            List<String> errors = new ArrayList<>();

            // Provide more specific error message based on exception
            String errorMsg = "An error occurred while creating user. Please try again.";
            if (e.getMessage() != null) {
                if (e.getMessage().contains("Duplicate entry")) {
                    if (e.getMessage().contains("employee_code")) {
                        errorMsg = "Employee code already exists. Please use a different code.";
                    } else if (e.getMessage().contains("phone")) {
                        errorMsg = "Phone number already exists in the system.";
                    } else if (e.getMessage().contains("email_company")) {
                        errorMsg = "Company email already exists in the system.";
                    } else {
                        errorMsg = "Duplicate entry detected. Please check your input.";
                    }
                } else if (e.getMessage().contains("foreign key")) {
                    errorMsg = "Invalid department or position selection.";
                } else if (e.getMessage().contains("manager")) {
                    errorMsg = e.getMessage(); // Use the specific manager error message
                }
            }

            errors.add(errorMsg);
            request.setAttribute("errors", errors);
            loadFormData(request);
            // Preserve form data from request parameters
            request.setAttribute("employeeCode", request.getParameter("employeeCode"));
            request.setAttribute("fullName", request.getParameter("fullName"));
            request.setAttribute("dateOfBirth", request.getParameter("dateOfBirth"));
            request.setAttribute("phone", request.getParameter("phone"));
            request.setAttribute("emailCompany", request.getParameter("emailCompany"));
            request.setAttribute("selectedDepartment", request.getParameter("departmentId"));
            request.setAttribute("selectedPosition", request.getParameter("positionId"));
            request.setAttribute("dateJoined", request.getParameter("dateJoined"));
            request.setAttribute("startWorkDate", request.getParameter("startWorkDate"));
            request.setAttribute("selectedGender", request.getParameter("gender"));
            request.getRequestDispatcher("/WEB-INF/views/employees/user-create.jsp")
                    .forward(request, response);
        }
    }

    /**
     * Generate employee code in format HExxxx
     */
    private String generateEmployeeCode() {
        String maxCode = userDao.findMaxEmployeeCode();

        if (maxCode == null || maxCode.isEmpty()) {
            return "HE0001"; // First employee
        }

        // Extract numeric part and increment
        String numericPart = maxCode.substring(2); // Remove "HE" prefix
        int nextNumber = Integer.parseInt(numericPart) + 1;

        // Format with leading zeros (4 digits)
        return String.format("HE%04d", nextNumber);
    }

    /**
     * Create manager account with appropriate role based on position and department
     */
    private void createDepartmentManagerAccount(User user) {
        // Generate username from full name (simplified: firstname.lastname)
        String username = generateUsername(user.getFullName());

        // Create account
        Account account = new Account();
        account.setUserId(user.getId());
        account.setUsername(username);
        account.setEmailLogin(user.getEmailCompany());
        account.setStatus("active");
        account.setFailedAttempts(0);

        Account createdAccount = accountDao.save(account);

        // Determine role based on position and department
        String roleName = "DEPARTMENT_MANAGER"; // Default role

        // Get position and department info
        var positions = DropdownCacheUtil.getCachedPositions(getServletContext());
        var position = positions.stream()
                .filter(p -> p.getId().equals(user.getPositionId()))
                .findFirst();

        if (position.isPresent()) {
            String positionName = position.get().getName();

            // If position is HR Manager, assign HR_MANAGER role
            if ("HR Manager".equalsIgnoreCase(positionName)) {
                roleName = "HR_MANAGER";
            }
            // If Department Manager in Human Resource department, assign HR_MANAGER role
            else if ("Department Manager".equalsIgnoreCase(positionName) && user.getDepartmentId() != null) {
                var departments = DropdownCacheUtil.getCachedDepartments(getServletContext());
                var department = departments.stream()
                        .filter(d -> d.getId().equals(user.getDepartmentId()))
                        .findFirst();

                if (department.isPresent() && "Human Resource".equalsIgnoreCase(department.get().getName())) {
                    roleName = "HR_MANAGER";
                }
            }
        }

        // Assign role
        Optional<Role> managerRole = roleDao.findByName(roleName);
        if (managerRole.isPresent()) {
            accountRoleDao.assignRole(createdAccount.getId(), managerRole.get().getId());
            logger.info("Assigned {} role to account: {}", roleName, createdAccount.getUsername());
        } else {
            logger.warn("{} role not found in database", roleName);
        }
    }

    /**
     * Generate username from full name
     * Example: "Nguyen Van A" -> "nguyen.vana"
     */
    private String generateUsername(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "user" + System.currentTimeMillis();
        }

        String[] parts = fullName.trim().toLowerCase().split("\\s+");
        if (parts.length == 1) {
            return parts[0];
        }

        // Take first part and last part
        String firstName = parts[0];
        String lastName = parts[parts.length - 1];

        return firstName + "." + lastName;
    }

    private void loadFormData(HttpServletRequest request) {
        request.setAttribute("departments", DropdownCacheUtil.getCachedDepartments(getServletContext()));
        request.setAttribute("positions", DropdownCacheUtil.getCachedPositions(getServletContext()));
    }

    private void preserveFormData(HttpServletRequest request, String employeeCode, String fullName,
            String dateOfBirth, String phone, String emailCompany, String departmentId,
            String positionId, String dateJoined, String startWorkDate, String gender) {
        request.setAttribute("employeeCode", employeeCode);
        request.setAttribute("fullName", fullName);
        request.setAttribute("dateOfBirth", dateOfBirth);
        request.setAttribute("phone", phone);
        request.setAttribute("emailCompany", emailCompany);
        request.setAttribute("selectedDepartment", departmentId);
        request.setAttribute("selectedPosition", positionId);
        request.setAttribute("dateJoined", dateJoined);
        request.setAttribute("startWorkDate", startWorkDate);
        request.setAttribute("selectedGender", gender);
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
