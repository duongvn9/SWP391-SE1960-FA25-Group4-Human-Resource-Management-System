<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="Request Details - HRMS" />
        <jsp:param name="pageCss" value="dashboard.css" />
    </jsp:include>
    <style>
        .detail-card {
            background: white;
            border-radius: 0.5rem;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            padding: 2rem;
            margin-bottom: 1.5rem;
        }
        .detail-row {
            display: flex;
            padding: 0.75rem 0;
            border-bottom: 1px solid #e5e7eb;
        }
        .detail-row:last-child {
            border-bottom: none;
        }
        .detail-label {
            font-weight: 600;
            color: #374151;
            min-width: 180px;
        }
        .detail-value {
            color: #6b7280;
            flex: 1;
        }
        .badge-duration {
            font-size: 1rem;
            padding: 0.5rem 1rem;
            border-radius: 0.375rem;
            font-weight: 600;
            display: inline-block;
        }
        .badge-full-day {
            background-color: #3b82f6;
            color: white;
        }
        .badge-half-day-am {
            background-color: #f59e0b;
            color: white;
        }
        .badge-half-day-pm {
            background-color: #8b5cf6;
            color: white;
        }
        .time-range-box {
            background: #f3f4f6;
            padding: 1rem;
            border-radius: 0.375rem;
            border-left: 4px solid #6366f1;
            margin-top: 0.5rem;
        }
        .time-range-box i {
            color: #6366f1;
        }
        .status-badge {
            font-size: 1rem;
            padding: 0.5rem 1rem;
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <jsp:include page="../layout/sidebar.jsp">
        <jsp:param name="currentPage" value="requests" />
    </jsp:include>

    <!-- Main Content -->
    <div class="main-content" id="main-content">
        <!-- Header -->
        <jsp:include page="../layout/dashboard-header.jsp" />

        <!-- Content Area -->
        <div class="content-area">
            <div class="row mb-4">
                <div class="col-12">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h2><i class="fas fa-file-alt me-2"></i>Request Details</h2>
                            <p class="text-muted">View detailed information about your request</p>
                        </div>
                        <a href="${pageContext.request.contextPath}/requests/list" class="btn btn-outline-secondary">
                            <i class="fas fa-arrow-left me-2"></i>Back to List
                        </a>
                    </div>
                </div>
            </div>

            <!-- Request Details -->
            <div class="row">
                <div class="col-12">
                    <div class="detail-card">
                        <h4 class="mb-4">${request.title}</h4>

                        <!-- Status -->
                        <div class="detail-row">
                            <div class="detail-label">Status:</div>
                            <div class="detail-value">
                                <c:choose>
                                    <c:when test="${request.status == 'PENDING'}">
                                        <span class="badge bg-warning status-badge">Pending</span>
                                    </c:when>
                                    <c:when test="${request.status == 'APPROVED'}">
                                        <span class="badge bg-success status-badge">Approved</span>
                                    </c:when>
                                    <c:when test="${request.status == 'REJECTED'}">
                                        <span class="badge bg-danger status-badge">Rejected</span>
                                    </c:when>
                                </c:choose>
                            </div>
                        </div>

                        <!-- Leave Request Details -->
                        <c:if test="${request.leaveDetail != null}">
                            <!-- Duration Type -->
                            <div class="detail-row">
                                <div class="detail-label">Duration Type:</div>
                                <div class="detail-value">
                                    <c:choose>
                                        <c:when test="${request.leaveDetail.isHalfDay}">
                                            <c:choose>
                                                <c:when test="${request.leaveDetail.halfDayPeriod == 'AM'}">
                                                    <span class="badge-duration badge-half-day-am">
                                                        <i class="fas fa-sun me-2"></i>Half Day - Morning (8:00-12:00)
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge-duration badge-half-day-pm">
                                                        <i class="fas fa-cloud-sun me-2"></i>Half Day - Afternoon (13:00-17:00)
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-duration badge-full-day">
                                                <i class="fas fa-calendar-day me-2"></i>Full Day
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <!-- Time Range for Half-Day -->
                            <c:if test="${request.leaveDetail.isHalfDay}">
                                <div class="detail-row">
                                    <div class="detail-label">Time Range:</div>
                                    <div class="detail-value">
                                        <div class="time-range-box">
                                            <i class="fas fa-clock me-2"></i>
                                            <strong>
                                                <c:choose>
                                                    <c:when test="${request.leaveDetail.halfDayPeriod == 'AM'}">
                                                        Morning: 8:00 AM - 12:00 PM
                                                    </c:when>
                                                    <c:otherwise>
                                                        Afternoon: 1:00 PM - 5:00 PM
                                                    </c:otherwise>
                                                </c:choose>
                                            </strong>
                                        </div>
                                    </div>
                                </div>
                            </c:if>

                            <!-- Duration -->
                            <div class="detail-row">
                                <div class="detail-label">Duration:</div>
                                <div class="detail-value">
                                    <c:choose>
                                        <c:when test="${request.leaveDetail.isHalfDay}">
                                            <strong>0.5 days</strong>
                                        </c:when>
                                        <c:otherwise>
                                            <strong>${request.leaveDetail.dayCount} day(s)</strong>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <!-- Leave Type -->
                            <div class="detail-row">
                                <div class="detail-label">Leave Type:</div>
                                <div class="detail-value">${request.leaveDetail.leaveTypeName}</div>
                            </div>

                            <!-- Date -->
                            <div class="detail-row">
                                <div class="detail-label">Date:</div>
                                <div class="detail-value">
                                    <i class="fas fa-calendar me-2"></i>
                                    ${request.leaveDetail.startDate}
                                    <c:if test="${!request.leaveDetail.isHalfDay && request.leaveDetail.startDate != request.leaveDetail.endDate}">
                                        to ${request.leaveDetail.endDate}
                                    </c:if>
                                </div>
                            </div>

                            <!-- Reason -->
                            <div class="detail-row">
                                <div class="detail-label">Reason:</div>
                                <div class="detail-value">${request.leaveDetail.reason}</div>
                            </div>

                            <!-- Certificate Required -->
                            <c:if test="${request.leaveDetail.certificateRequired != null}">
                                <div class="detail-row">
                                    <div class="detail-label">Certificate Required:</div>
                                    <div class="detail-value">
                                        <c:choose>
                                            <c:when test="${request.leaveDetail.certificateRequired}">
                                                <span class="badge bg-info">Yes</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">No</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </c:if>

                            <!-- Attachment -->
                            <c:if test="${request.leaveDetail.attachmentPath != null && !empty request.leaveDetail.attachmentPath}">
                                <div class="detail-row">
                                    <div class="detail-label">Attachment:</div>
                                    <div class="detail-value">
                                        <a href="${pageContext.request.contextPath}/uploads/${request.leaveDetail.attachmentPath}"
                                           target="_blank" class="btn btn-sm btn-outline-primary">
                                            <i class="fas fa-paperclip me-1"></i>View Attachment
                                        </a>
                                    </div>
                                </div>
                            </c:if>

                            <!-- Manager Notes -->
                            <c:if test="${request.leaveDetail.managerNotes != null && !empty request.leaveDetail.managerNotes}">
                                <div class="detail-row">
                                    <div class="detail-label">Manager Notes:</div>
                                    <div class="detail-value">
                                        <div class="alert alert-info mb-0">
                                            ${request.leaveDetail.managerNotes}
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                        </c:if>

                        <!-- OT Request Details -->
                        <c:if test="${request.otDetail != null}">
                            <div class="detail-row">
                                <div class="detail-label">OT Date:</div>
                                <div class="detail-value">${request.otDetail.otDate}</div>
                            </div>
                            <div class="detail-row">
                                <div class="detail-label">Start Time:</div>
                                <div class="detail-value">${request.otDetail.startTime}</div>
                            </div>
                            <div class="detail-row">
                                <div class="detail-label">End Time:</div>
                                <div class="detail-value">${request.otDetail.endTime}</div>
                            </div>
                            <div class="detail-row">
                                <div class="detail-label">Hours:</div>
                                <div class="detail-value">${request.otDetail.hours} hour(s)</div>
                            </div>
                            <div class="detail-row">
                                <div class="detail-label">Reason:</div>
                                <div class="detail-value">${request.otDetail.reason}</div>
                            </div>
                        </c:if>

                        <!-- Timestamps -->
                        <div class="detail-row">
                            <div class="detail-label">Created At:</div>
                            <div class="detail-value">
                                <i class="fas fa-clock me-2"></i>${request.createdAt}
                            </div>
                        </div>
                        <div class="detail-row">
                            <div class="detail-label">Last Updated:</div>
                            <div class="detail-value">
                                <i class="fas fa-clock me-2"></i>${request.updatedAt}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <jsp:include page="../layout/dashboard-footer.jsp" />
    </div>
</body>
</html>
