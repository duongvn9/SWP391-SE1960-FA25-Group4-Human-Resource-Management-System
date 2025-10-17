package group4.hrms.controller;

import group4.hrms.dao.UserDao;
import group4.hrms.model.User;
import group4.hrms.util.DropdownCacheUtil;
import group4.hrms.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Servlet xử lý User Create page
 * URL: /employees/users/create
 */
@WebServlet("/employees/users/create")
public class UserCreateServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(UserCreateServlet.class);

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Check authentication
            if (!SessionUtil.isUserLoggedIn(request)) {
                response.sendRedirect(request.getContextPath() + "/login");
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

            // Get form parameters
            String employeeCode = request.getParameter("employeeCode");
            String fullName = request.getParameter("fullName");
            String cccd = request.getParameter("cccd");
            String emailCompany = request.getParameter("emailCompany");
            String phone = request.getParameter("phone");
            String departmentIdStr = request.getParameter("departmentId");
            String positionIdStr = request.getParameter("positionId");
            String status = request.getParameter("status");
            String dateJoinedStr = request.getParameter("dateJoined");
            String startWorkDateStr = request.getParameter("startWorkDate");
            String baseSalaryStr = request.getParameter("baseSalary");
            String salaryCurrency = request.getParameter("salaryCurrency");

            // Validate required fields
            if (isNullOrEmpty(employeeCode) || isNullOrEmpty(fullName) || isNullOrEmpty(emailCompany)) {
                request.setAttribute("errorMessage", "Employee Code, Full Name, and Email are required");
                loadFormData(request);
                request.getRequestDispatcher("/WEB-INF/views/employees/user-create.jsp")
                        .forward(request, response);
                return;
            }

            // Check if employee code already exists
            if (userDao.findByEmployeeCode(employeeCode.trim()).isPresent()) {
                request.setAttribute("errorMessage", "Employee Code already exists");
                loadFormData(request);
                preserveFormData(request, employeeCode, fullName, cccd, emailCompany, phone,
                        departmentIdStr, positionIdStr, status, dateJoinedStr, startWorkDateStr,
                        baseSalaryStr, salaryCurrency);
                request.getRequestDispatcher("/WEB-INF/views/employees/user-create.jsp")
                        .forward(request, response);
                return;
            }

            // Check if email already exists
            if (userDao.findByEmail(emailCompany.trim()).isPresent()) {
                request.setAttribute("errorMessage", "Email already exists");
                loadFormData(request);
                preserveFormData(request, employeeCode, fullName, cccd, emailCompany, phone,
                        departmentIdStr, positionIdStr, status, dateJoinedStr, startWorkDateStr,
                        baseSalaryStr, salaryCurrency);
                request.getRequestDispatcher("/WEB-INF/views/employees/user-create.jsp")
                        .forward(request, response);
                return;
            }

            // Create User object
            User user = new User();
            user.setEmployeeCode(employeeCode.trim());
            user.setFullName(fullName.trim());
            user.setCccd(isNullOrEmpty(cccd) ? null : cccd.trim());
            user.setEmailCompany(emailCompany.trim());
            user.setPhone(isNullOrEmpty(phone) ? null : phone.trim());
            user.setStatus(isNullOrEmpty(status) ? "active" : status);
            user.setSalaryCurrency(isNullOrEmpty(salaryCurrency) ? "VND" : salaryCurrency);

            // Parse department ID
            if (!isNullOrEmpty(departmentIdStr)) {
                try {
                    user.setDepartmentId(Long.parseLong(departmentIdStr));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid department ID: {}", departmentIdStr);
                }
            }

            // Parse position ID
            if (!isNullOrEmpty(positionIdStr)) {
                try {
                    user.setPositionId(Long.parseLong(positionIdStr));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid position ID: {}", positionIdStr);
                }
            }

            // Parse dates
            if (!isNullOrEmpty(dateJoinedStr)) {
                try {
                    user.setDateJoined(LocalDate.parse(dateJoinedStr));
                } catch (DateTimeParseException e) {
                    logger.warn("Invalid date joined: {}", dateJoinedStr);
                }
            }

            if (!isNullOrEmpty(startWorkDateStr)) {
                try {
                    user.setStartWorkDate(LocalDate.parse(startWorkDateStr));
                } catch (DateTimeParseException e) {
                    logger.warn("Invalid start work date: {}", startWorkDateStr);
                }
            }

            // Parse salary
            if (!isNullOrEmpty(baseSalaryStr)) {
                try {
                    user.setBaseSalary(new BigDecimal(baseSalaryStr));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid base salary: {}", baseSalaryStr);
                }
            }

            // Save user
            var createdUserOpt = userDao.create(user);

            if (createdUserOpt.isPresent()) {
                User createdUser = createdUserOpt.get();
                logger.info("User created successfully: {}", createdUser.getEmployeeCode());
                request.getSession().setAttribute("successMessage", "User created successfully");
                response.sendRedirect(request.getContextPath() + "/employees/users");
            } else {
                request.setAttribute("errorMessage", "Failed to create user. Please try again.");
                loadFormData(request);
                preserveFormData(request, employeeCode, fullName, cccd, emailCompany, phone,
                        departmentIdStr, positionIdStr, status, dateJoinedStr, startWorkDateStr,
                        baseSalaryStr, salaryCurrency);
                request.getRequestDispatcher("/WEB-INF/views/employees/user-create.jsp")
                        .forward(request, response);
            }

        } catch (Exception e) {
            logger.error("Error creating user", e);
            request.setAttribute("errorMessage", "An error occurred while creating user. Please try again.");
            loadFormData(request);
            request.getRequestDispatcher("/WEB-INF/views/employees/user-create.jsp")
                    .forward(request, response);
        }
    }

    private void loadFormData(HttpServletRequest request) {
        request.setAttribute("departments", DropdownCacheUtil.getCachedDepartments(getServletContext()));
        request.setAttribute("positions", DropdownCacheUtil.getCachedPositions(getServletContext()));
    }

    private void preserveFormData(HttpServletRequest request, String employeeCode, String fullName,
            String cccd, String emailCompany, String phone, String departmentId,
            String positionId, String status, String dateJoined,
            String startWorkDate, String baseSalary, String salaryCurrency) {
        request.setAttribute("employeeCode", employeeCode);
        request.setAttribute("fullName", fullName);
        request.setAttribute("cccd", cccd);
        request.setAttribute("emailCompany", emailCompany);
        request.setAttribute("phone", phone);
        request.setAttribute("selectedDepartment", departmentId);
        request.setAttribute("selectedPosition", positionId);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("dateJoined", dateJoined);
        request.setAttribute("startWorkDate", startWorkDate);
        request.setAttribute("baseSalary", baseSalary);
        request.setAttribute("selectedCurrency", salaryCurrency);
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
