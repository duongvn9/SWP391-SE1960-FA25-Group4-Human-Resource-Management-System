package group4.hrms.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import group4.hrms.dao.AttachmentDao;
import group4.hrms.dao.PositionDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dto.RequestDto;
import group4.hrms.model.Account;
import group4.hrms.model.Attachment;
import group4.hrms.model.Position;
import group4.hrms.model.Request;
import group4.hrms.model.User;
import group4.hrms.util.RequestListPermissionHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for displaying request details.
 * Shows detailed information about a specific request.
 *
 * @author HRMS Development Team
 * @version 1.0
 */
@WebServlet("/requests/detail")
public class RequestDetailController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(RequestDetailController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("RequestDetailController.doGet() called");

        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Account account = (Account) session.getAttribute("account");
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Get request ID from parameter
            String requestIdStr = request.getParameter("id");
            if (requestIdStr == null || requestIdStr.trim().isEmpty()) {
                logger.warning("Request ID parameter is missing");
                session.setAttribute("error", "Request ID is required");
                response.sendRedirect(request.getContextPath() + "/requests");
                return;
            }

            Long requestId;
            try {
                requestId = Long.parseLong(requestIdStr);
            } catch (NumberFormatException e) {
                logger.warning("Invalid request ID format: " + requestIdStr);
                session.setAttribute("error", "Invalid request ID format");
                response.sendRedirect(request.getContextPath() + "/requests");
                return;
            }

            RequestDao requestDao = new RequestDao();
            PositionDao positionDao = new PositionDao();

            // Load user's position for permission checks
            Position position = null;
            try {
                if (user.getPositionId() != null) {
                    Optional<Position> positionOpt = positionDao.findById(user.getPositionId());
                    if (positionOpt.isPresent()) {
                        position = positionOpt.get();
                        logger.info("Loaded position for user " + user.getId() + ": " + position.getName() + " (job level: " + position.getJobLevel() + ")");
                    } else {
                        logger.warning("Position not found for user " + user.getId() + " with position ID: " + user.getPositionId());
                    }
                } else {
                    logger.warning("User " + user.getId() + " has no position assigned");
                }
            } catch (Exception e) {
                logger.severe("Error loading position for user " + user.getId() + ": " + e.getMessage());
                // Continue without position - permission check will handle this
            }

            // Find the request entity for permission check
            Optional<Request> requestOpt;
            try {
                requestOpt = requestDao.findById(requestId);
            } catch (Exception e) {
                logger.severe("Database error while loading request " + requestId + ": " + e.getMessage());
                e.printStackTrace();
                session.setAttribute("error", "Error loading request details. Please try again later.");
                response.sendRedirect(request.getContextPath() + "/requests");
                return;
            }

            if (!requestOpt.isPresent()) {
                logger.warning("Request not found with ID: " + requestId);
                session.setAttribute("error", "Request not found");
                response.sendRedirect(request.getContextPath() + "/requests");
                return;
            }

            Request requestEntity = requestOpt.get();

            // Check view permission using RequestListPermissionHelper
            if (!RequestListPermissionHelper.canViewRequest(user, requestEntity, position)) {
                logger.warning("User " + user.getId() + " attempted to access request " + requestId + " without permission");
                session.setAttribute("error", "You don't have permission to view this request");
                response.sendRedirect(request.getContextPath() + "/requests");
                return;
            }

            // Find the request with details using the enhanced DAO method
            Optional<RequestDto> requestDtoOpt;
            try {
                requestDtoOpt = requestDao.findByIdWithDetails(requestId);
            } catch (Exception e) {
                logger.severe("Database error while loading request details for " + requestId + ": " + e.getMessage());
                e.printStackTrace();
                session.setAttribute("error", "Error loading request details. Please try again later.");
                response.sendRedirect(request.getContextPath() + "/requests");
                return;
            }

            if (!requestDtoOpt.isPresent()) {
                logger.warning("Request details not found with ID: " + requestId);
                session.setAttribute("error", "Request not found");
                response.sendRedirect(request.getContextPath() + "/requests");
                return;
            }

            RequestDto requestDto = requestDtoOpt.get();

            // Calculate status badge class for UI display
            requestDto.calculateStatusBadgeClass();

            // Ensure requestEntity has parsed detail objects for permission check
            // RequestDao.findById() may not parse JSON detail, so we manually trigger it
            if (requestEntity.getDetailJson() != null && !requestEntity.getDetailJson().trim().isEmpty()) {
                try {
                    // Trigger lazy parsing based on request_type_id
                    Long requestTypeId = requestEntity.getRequestTypeId();
                    if (requestTypeId != null) {
                        if (requestTypeId == 7L) {
                            // OVERTIME_REQUEST
                            requestEntity.getOtDetail();
                            logger.info("Parsed OT detail for request " + requestId + ". CreatedByManager="
                                + (requestEntity.getOtDetail() != null && requestEntity.getOtDetail().getCreatedByManager()));
                        } else if (requestTypeId == 6L) {
                            // LEAVE_REQUEST
                            requestEntity.getLeaveDetail();
                        } else if (requestTypeId == 8L) {
                            // ADJUSTMENT_REQUEST (Appeal)
                            requestEntity.getAppealDetail();
                        }
                    }
                } catch (Exception e) {
                    logger.warning("Error parsing detail JSON for request " + requestId + ": " + e.getMessage());
                }
            }

            // Check if user can approve this request
            boolean canApprove = RequestListPermissionHelper.canApproveRequest(user, requestEntity, position, account.getId());
            logger.info("User " + user.getId() + " can approve request " + requestId + ": " + canApprove);

            // Load attachments for this request
            AttachmentDao attachmentDao = new AttachmentDao();
            List<Attachment> attachments = null;
            try {
                attachments = attachmentDao.findByOwner("REQUEST", requestId);
                logger.info("Loaded " + attachments.size() + " attachment(s) for request " + requestId);
            } catch (Exception e) {
                logger.warning("Error loading attachments for request " + requestId + ": " + e.getMessage());
                // Continue without attachments - not critical for viewing request
                attachments = List.of(); // Empty list
            }

            // Parse and set type-specific detail objects
            try {
                if (requestDto.getRequestTypeCode() != null) {
                    if (requestDto.getRequestTypeCode().startsWith("LEAVE_")) {
                        // Parse leave request detail
                        group4.hrms.dto.LeaveRequestDetail leaveDetail = requestDto.getLeaveDetail();
                        if (leaveDetail != null) {
                            request.setAttribute("leaveDetail", leaveDetail);
                            logger.info("Successfully parsed leave detail for request " + requestId);

                            // Extract manager notes if available
                            if (leaveDetail.getManagerNotes() != null && !leaveDetail.getManagerNotes().trim().isEmpty()) {
                                request.setAttribute("managerNotes", leaveDetail.getManagerNotes());
                                logger.info("Manager notes found for request " + requestId);
                            }
                        } else {
                            logger.warning("Failed to parse leave detail for request " + requestId + " - detail is null");
                        }
                    } else if ("OVERTIME_REQUEST".equals(requestDto.getRequestTypeCode())) {
                        // Parse OT request detail
                        group4.hrms.dto.OTRequestDetail otDetail = requestDto.getOtDetail();
                        if (otDetail != null) {
                            request.setAttribute("otDetail", otDetail);
                            logger.info("Successfully parsed OT detail for request " + requestId);
                        } else {
                            logger.warning("Failed to parse OT detail for request " + requestId + " - detail is null");
                        }
                    } else if ("ADJUSTMENT_REQUEST".equals(requestDto.getRequestTypeCode())) {
                        // Parse Appeal/Adjustment request detail
                        group4.hrms.dto.AppealRequestDetail appealDetail = requestDto.getAppealDetail();
                        if (appealDetail != null) {
                            request.setAttribute("appealDetail", appealDetail);
                            logger.info("Successfully parsed appeal detail for request " + requestId);
                        } else {
                            logger.warning("Failed to parse appeal detail for request " + requestId + " - detail is null");
                        }
                    } else if ("RECRUITMENT_REQUEST".equals(requestDto.getRequestTypeCode())) {
                        // Parse Recruitment request detail
                        group4.hrms.dto.RecruitmentDetailsDto recruitmentDetail = requestDto.getRecruitmentDetail();
                        if (recruitmentDetail != null) {
                            request.setAttribute("recruitmentDetail", recruitmentDetail);
                            logger.info("Successfully parsed recruitment detail for request " + requestId);
                        } else {
                            logger.warning("Failed to parse recruitment detail for request " + requestId + " - detail is null");
                        }
                    }
                }
            } catch (Exception e) {
                // Log warning but continue with basic info
                logger.warning("Error parsing JSON detail for request " + requestId + ": " + e.getMessage());
                // Don't throw - continue to display basic request information
            }

            // Set attributes for JSP
            request.setAttribute("requestDto", requestDto);
            request.setAttribute("canApprove", canApprove);
            request.setAttribute("attachments", attachments);

            // Forward to detail view
            request.getRequestDispatcher("/WEB-INF/views/requests/request-detail.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            logger.severe("Unexpected error in RequestDetailController: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "An unexpected error occurred. Please try again later.");
            response.sendRedirect(request.getContextPath() + "/requests");
        }
    }
}
