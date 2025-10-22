package group4.hrms.controller;

import java.io.IOException;
import java.util.Optional;


import group4.hrms.dao.DepartmentDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.RequestListFilter;
import group4.hrms.dto.RequestListResult;
import group4.hrms.model.Account;
import group4.hrms.model.Position;
import group4.hrms.model.RequestType;
import group4.hrms.model.User;
import group4.hrms.service.RequestListService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet to display a dedicated page listing only APPROVED recruitment requests
 * This returns RequestListResult (RequestDto) so the view can render UI fields.
 */
@WebServlet("/recruitment/approved")
public class RecruitmentApprovedListServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(RecruitmentApprovedListServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Authorization: accept if any of these true:
        // - session user.positionId == 7 or 8
        // - session attribute "userRole" equals HR or HRM
        // - SessionUtil.hasAnyRole(request, "HR", "HRM")
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session == null) {
            // Not logged in -> redirect to login
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        boolean allowed = false;
        // 1) positionId check (HR=8, HRM=7)
        Object userObj = session.getAttribute("user");
        if (userObj instanceof group4.hrms.model.User) {
            group4.hrms.model.User user = (group4.hrms.model.User) userObj;
            if (user.getPositionId() != null) {
                Long posId = user.getPositionId();
                if (posId == 7L || posId == 8L) {
                    allowed = true;
                }
            }
        }

        // 2) userRole string in session (older pages set sessionScope.userRole)
        if (!allowed) {
            Object roleAttr = session.getAttribute("userRole");
            if (roleAttr != null) {
                String roleStr = roleAttr.toString();
                if ("HR".equalsIgnoreCase(roleStr) || "HRM".equalsIgnoreCase(roleStr)) {
                    allowed = true;
                }
            }
        }

        // 3) fallback to SessionUtil (checks roles string and admin flag)
        if (!allowed) {
            try {
                if (group4.hrms.util.SessionUtil.hasAnyRole(request, "HR", "HRM")) {
                    allowed = true;
                }
            } catch (Exception ignored) {
                // safe fallback
            }
        }

        if (!allowed) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Get success message from redirect if any
        String successMessage = request.getParameter("success");
        if (successMessage != null && !successMessage.isEmpty()) {
            request.setAttribute("success", successMessage);
        }

        // Prepare filter: recruitment requests with APPROVED
        RequestListFilter filter = new RequestListFilter();

        // Resolve request type id by code to avoid magic number
        RequestTypeDao rtDao = new RequestTypeDao();
        RequestType rt = rtDao.findByCode("RECRUITMENT_REQUEST");
        if (rt == null) {
            logger.error("Could not find RECRUITMENT_REQUEST type");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not find recruitment request type");
            return;
        }
        
        // Set up filter for all approved recruitment requests
        filter.setRequestTypeId(rt.getId());
        filter.setStatus("APPROVED");  // Only approved requests
        filter.setScope("all");        // Show all departments
        filter.setPage(1);             // First page
        filter.setPageSize(1000);      // Show all on one page
        
        logger.info("Set up filter: typeId={}, status={}, scope={}", 
                   filter.getRequestTypeId(), filter.getStatus(), filter.getScope());

        // Load current user/account and position
        Account account = (Account) request.getSession(false).getAttribute("account");
        User user = (User) request.getSession(false).getAttribute("user");
        Position position = null;
        try {
            if (user != null && user.getPositionId() != null) {
                group4.hrms.dao.PositionDao posDao = new group4.hrms.dao.PositionDao();
                Optional<Position> posOpt = posDao.findById(user.getPositionId());
                position = posOpt.orElse(null);
            }
        } catch (Exception ignore) {
            // ignore position lookup errors; permission helper will fallback
        }

        // Build service
        RequestListService service = new RequestListService(new RequestDao(), new UserDao(), new DepartmentDao());
        try {
            logger.info("Calling service with filter - type: {}, status: {}, scope: {}", 
                    filter.getRequestTypeId(), filter.getStatus(), filter.getScope());

            RequestListResult result = service.getRequestList(filter, user, position, 
                    account != null ? account.getId() : null, request.getContextPath());

            logger.info("Service call completed. Result is {}", (result != null ? "present" : "null"));
            if (result != null) {
                logger.info("Result contains {} requests, {} departments", 
                        (result.getRequests() != null ? result.getRequests().size() : 0),
                        (result.getRequestsByDepartment() != null ? result.getRequestsByDepartment().size() : 0));
                if (result.getRequestsByDepartment() != null) {
                    result.getRequestsByDepartment().forEach((dept, requests) -> 
                            logger.info("Department '{}' has {} requests", dept, requests.size())
                    );
                }
            }

            request.setAttribute("result", result);
            request.setAttribute("filter", filter);

            String jspPath = "/WEB-INF/views/recruitment/recruitment-approved-list.jsp";
            logger.info("Forwarding to JSP: {}", jspPath);
            request.getRequestDispatcher(jspPath).forward(request, response);

        } catch (ServletException | IOException e) {
            logger.error("Error forwarding to JSP: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error processing request: {}", e.getMessage(), e);
            request.setAttribute("error", "An unexpected error occurred. Please try again later.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment-approved-list.jsp").forward(request, response);
        }
    }

}
