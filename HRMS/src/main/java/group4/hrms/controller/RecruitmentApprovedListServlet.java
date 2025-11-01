package group4.hrms.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.dao.DepartmentDao;
import group4.hrms.dao.JobPostingDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.RequestDto;
import group4.hrms.dto.RequestListFilter;
import group4.hrms.dto.RequestListResult;
import group4.hrms.model.Account;
import group4.hrms.model.Position;
import group4.hrms.model.RequestType;
import group4.hrms.model.User;
import group4.hrms.service.RequestListService;
import group4.hrms.util.RecruitmentPermissionHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet to display a dedicated page listing only APPROVED recruitment requests
 * This returns RequestListResult (RequestDto) so the view can render UI fields.
 */
@WebServlet("/recruitment/approved")
public class RecruitmentApprovedListServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(RecruitmentApprovedListServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Kiểm tra session đăng nhập
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Kiểm tra quyền xem recruitment requests (HR, HRM, Department Manager)
        Long positionId = (Long) session.getAttribute("userPositionId");
        
        // Nếu vẫn null, thử lấy từ user object
        if (positionId == null) {
            Object userObj = session.getAttribute("user");
            if (userObj instanceof group4.hrms.model.User) {
                group4.hrms.model.User user = (group4.hrms.model.User) userObj;
                positionId = user.getPositionId();
            }
        }
        
        if (!RecruitmentPermissionHelper.canViewRecruitmentRequest(positionId)) {
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
        
        // Parse pagination parameters
        int page = 1;
        int pageSize = 6; // 6 items per page
        String pageStr = request.getParameter("page");
        if (pageStr != null) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) page = 1;
            } catch (NumberFormatException ignored) {}
        }
        
        // Set up filter for all approved recruitment requests
        filter.setRequestTypeId(rt.getId());
        filter.setStatus("APPROVED");  // Only approved requests
        filter.setScope("all");        // Show all departments
        filter.setPage(page);          // Current page
        filter.setPageSize(pageSize);  // 6 items per page
        
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

            // Filter out requests that already have job postings
            if (result != null) {
                JobPostingDao jobPostingDao = new JobPostingDao();
                List<Long> requestIdsWithJobPostings = jobPostingDao.findRequestIdsWithJobPostings();
                logger.info("Found {} request IDs that already have job postings: {}", 
                        requestIdsWithJobPostings.size(), requestIdsWithJobPostings);

                // Filter flat list
                if (result.getRequests() != null && !result.getRequests().isEmpty()) {
                    List<RequestDto> filteredRequests = result.getRequests().stream()
                            .filter(req -> !requestIdsWithJobPostings.contains(req.getId()))
                            .collect(Collectors.toList());
                    logger.info("Filtered requests from {} to {} (removed requests with job postings)", 
                            result.getRequests().size(), filteredRequests.size());
                    result.setRequests(filteredRequests);
                }

                // Filter grouped by department
                if (result.getRequestsByDepartment() != null && !result.getRequestsByDepartment().isEmpty()) {
                    Map<String, List<RequestDto>> filteredByDept = result.getRequestsByDepartment().entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue().stream()
                                            .filter(req -> !requestIdsWithJobPostings.contains(req.getId()))
                                            .collect(Collectors.toList())
                            ))
                            .entrySet().stream()
                            .filter(entry -> !entry.getValue().isEmpty()) // Remove empty departments
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    logger.info("Filtered departments from {} to {} (removed empty departments)", 
                            result.getRequestsByDepartment().size(), filteredByDept.size());
                    result.setRequestsByDepartment(filteredByDept);
                }
            }

            // Calculate total items and pages based on filtered results
            int totalItems = 0;
            if (result != null && result.getRequests() != null) {
                totalItems = result.getRequests().size();
            } else if (result != null && result.getRequestsByDepartment() != null) {
                totalItems = result.getRequestsByDepartment().values().stream()
                        .mapToInt(List::size)
                        .sum();
            }
            
            int totalPages = (int) Math.ceil((double) totalItems / pageSize);
            if (totalPages < 1) totalPages = 1;
            
            // Set pagination attributes
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalItems", totalItems);
            
            request.setAttribute("result", result);
            request.setAttribute("filter", filter);

            String jspPath = "/WEB-INF/views/recruitment/recruitment-approved-list.jsp";
            logger.info("Forwarding to JSP: {} (page {}/{}, {} items)", jspPath, page, totalPages, totalItems);
            request.getRequestDispatcher(jspPath).forward(request, response);

        } catch (ServletException | IOException e) {
            logger.error("Error forwarding to JSP: {}", e.getMessage(), e);
            throw e;
        } catch (java.sql.SQLException e) {
            logger.error("Database error processing request: {}", e.getMessage(), e);
            request.setAttribute("error", "Database error occurred. Please try again later.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment-approved-list.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Unexpected error processing request: {}", e.getMessage(), e);
            request.setAttribute("error", "An unexpected error occurred. Please try again later.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment-approved-list.jsp").forward(request, response);
        }
    }

}
