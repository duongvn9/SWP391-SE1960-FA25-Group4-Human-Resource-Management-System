<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!-- Appeal Request Detail Section -->
<div class="card mb-4 shadow-sm border-0">
    <div class="card-header bg-gradient-warning text-white">
        <h5 class="mb-0">
            <i class="fas fa-exclamation-triangle me-2"></i>Attendance Appeal Details
        </h5>
    </div>
    <div class="card-body p-4">
        <c:choose>
            <c:when test="${not empty appealDetail}">
                <!-- Basic Appeal Information -->
                <div class="row g-4 mb-4">
                    <div class="col-lg-4">
                        <div class="info-item">
                            <div class="info-label">
                                <i class="fas fa-calendar-alt me-2"></i>Disputed Dates
                            </div>
                            <div class="info-value">
                                <c:choose>
                                    <c:when test="${not empty appealDetail.attendanceDates}">
                                        <div class="d-flex flex-wrap gap-2">
                                            <c:forEach var="date" items="${appealDetail.attendanceDates}">
                                                <span class="badge bg-warning text-dark">
                                                    <i class="fas fa-calendar-day me-1"></i>${date}
                                                </span>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted fst-italic">No dates specified</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-4">
                        <div class="info-item">
                            <div class="info-label">
                                <i class="fas fa-calendar-plus me-2"></i>Submitted Date
                            </div>
                            <div class="info-value">
                                <c:choose>
                                    <c:when test="${not empty appealDetail.submittedDate}">
                                        <span class="fw-medium">${appealDetail.submittedDate}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:formatDate value="${requestDto.createdAtAsDate}" pattern="yyyy-MM-dd" />
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-4">
                        <div class="info-item">
                            <div class="info-label">
                                <i class="fas fa-flag me-2"></i>Appeal Status
                            </div>
                            <div class="info-value">
                                <c:set var="currentAppealStatus" value="${not empty appealDetail.appealStatus ? appealDetail.appealStatus : requestDto.status}" />
                                <span class="badge ${requestDto.statusBadgeClass} px-3 py-2">
                                    <c:choose>
                                        <c:when test="${currentAppealStatus == 'PENDING'}">
                                            <i class="fas fa-clock me-1"></i>
                                        </c:when>
                                        <c:when test="${currentAppealStatus == 'APPROVED'}">
                                            <i class="fas fa-check-circle me-1"></i>
                                        </c:when>
                                        <c:when test="${currentAppealStatus == 'REJECTED'}">
                                            <i class="fas fa-times-circle me-1"></i>
                                        </c:when>
                                    </c:choose>
                                    ${currentAppealStatus}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Appeal Reason Section -->
                <c:if test="${not empty appealDetail.reason}">
                    <div class="mb-4">
                        <h6 class="section-title mb-3">
                            <i class="fas fa-comment-alt text-primary me-2"></i>Appeal Reason
                        </h6>
                        <div class="alert alert-light border mb-0">
                            <i class="fas fa-quote-left me-2 text-muted"></i>
                            <c:out value="${appealDetail.reason}" />
                        </div>
                    </div>
                </c:if>

                <!-- Attendance Records Comparison -->
                <c:if test="${not empty attendanceRecords}">
                    <div class="mb-4">
                        <h6 class="section-title mb-3">
                            <i class="fas fa-exchange-alt text-primary me-2"></i>Attendance Records Comparison
                        </h6>
                        <c:forEach var="record" items="${attendanceRecords}" varStatus="status">
                            <div class="card mb-3 border-0 shadow-sm">
                                <div class="card-header bg-gradient-light">
                                    <h6 class="mb-0 d-flex align-items-center justify-content-between">
                                        <div class="d-flex align-items-center">
                                            <i class="fas fa-calendar-day me-2 text-primary"></i>
                                            <strong>Date: ${record.date}</strong>
                                            <c:if test="${not empty record.period}">
                                                <span class="badge bg-info ms-2">${record.period}</span>
                                            </c:if>
                                        </div>
                                        <!-- Record Type Badge -->
                                        <c:choose>
                                            <c:when test="${record.recordType == 'NEW'}">
                                                <span class="badge bg-success">
                                                    <i class="fas fa-plus me-1"></i>New Record
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-warning text-dark">
                                                    <i class="fas fa-edit me-1"></i>Modified Record
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </h6>
                                </div>
                                <div class="card-body p-4">
                                    <!-- Check if this is a new record or edited record -->
                                    <c:choose>
                                        <c:when test="${record.recordType == 'NEW'}">
                                            <!-- New Record Display -->
                                            <div class="new-record-display">
                                                <div class="alert alert-success border-0 mb-3">
                                                    <h6 class="alert-heading mb-2">
                                                        <i class="fas fa-plus-circle me-2"></i>New Attendance Record
                                                    </h6>
                                                    <p class="mb-0">This is a completely new attendance record being added for this date.</p>
                                                </div>

                                                <div class="row g-4">
                                                    <div class="col-lg-12">
                                                        <div class="comparison-section requested-section">
                                                            <h6 class="text-success mb-3 d-flex align-items-center">
                                                                <i class="fas fa-clock me-2"></i>New Attendance Record
                                                            </h6>

                                                            <div class="time-info-grid">
                                                                <div class="time-info-item">
                                                                    <div class="time-label">
                                                                        <i class="fas fa-sign-in-alt me-1"></i>Check-in Time
                                                                    </div>
                                                                    <div class="time-value">
                                                                        <span class="badge bg-success bg-opacity-10 text-success px-3 py-2">
                                                                            ${not empty record.newCheckIn ? record.newCheckIn : 'N/A'}
                                                                        </span>
                                                                    </div>
                                                                </div>

                                                                <div class="time-info-item">
                                                                    <div class="time-label">
                                                                        <i class="fas fa-sign-out-alt me-1"></i>Check-out Time
                                                                    </div>
                                                                    <div class="time-value">
                                                                        <span class="badge bg-success bg-opacity-10 text-success px-3 py-2">
                                                                            ${not empty record.newCheckOut ? record.newCheckOut : 'N/A'}
                                                                        </span>
                                                                    </div>
                                                                </div>

                                                                <div class="time-info-item">
                                                                    <div class="time-label">
                                                                        <i class="fas fa-flag me-1"></i>Calculated Status
                                                                    </div>
                                                                    <div class="time-value">
                                                                        <c:choose>
                                                                            <c:when test="${record.newStatus eq 'Present' or record.newStatus eq 'On Time'}">
                                                                                <span class="badge bg-success px-3 py-2">
                                                                                    <i class="fas fa-check me-1"></i>${record.newStatus}
                                                                                </span>
                                                                            </c:when>
                                                                            <c:when test="${record.newStatus eq 'Late'}">
                                                                                <span class="badge bg-warning text-dark px-3 py-2">
                                                                                    <i class="fas fa-clock me-1"></i>${record.newStatus}
                                                                                </span>
                                                                            </c:when>
                                                                            <c:when test="${record.newStatus eq 'Early'}">
                                                                                <span class="badge bg-info px-3 py-2">
                                                                                    <i class="fas fa-fast-forward me-1"></i>${record.newStatus}
                                                                                </span>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span class="badge bg-secondary px-3 py-2">
                                                                                    <i class="fas fa-question me-1"></i>
                                                                                    ${not empty record.newStatus ? record.newStatus : 'Unknown'}
                                                                                </span>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <!-- Edited Record Display (Original vs Modified) -->
                                            <div class="row g-4">
                                                <!-- Original Times -->
                                                <div class="col-lg-5">
                                                    <div class="comparison-section original-section">
                                                        <h6 class="text-danger mb-3 d-flex align-items-center">
                                                            <i class="fas fa-history me-2"></i>Original Record
                                                        </h6>

                                                        <div class="time-info-grid">
                                                            <div class="time-info-item">
                                                                <div class="time-label">
                                                                    <i class="fas fa-sign-in-alt me-1"></i>Check-in
                                                                </div>
                                                                <div class="time-value">
                                                                    <span class="badge bg-danger bg-opacity-10 text-danger px-3 py-2">
                                                                        ${not empty record.oldCheckIn ? record.oldCheckIn : 'N/A'}
                                                                    </span>
                                                                </div>
                                                            </div>

                                                            <div class="time-info-item">
                                                                <div class="time-label">
                                                                    <i class="fas fa-sign-out-alt me-1"></i>Check-out
                                                                </div>
                                                                <div class="time-value">
                                                                    <span class="badge bg-danger bg-opacity-10 text-danger px-3 py-2">
                                                                        ${not empty record.oldCheckOut ? record.oldCheckOut : 'N/A'}
                                                                    </span>
                                                                </div>
                                                            </div>

                                                            <div class="time-info-item">
                                                                <div class="time-label">
                                                                    <i class="fas fa-flag me-1"></i>Status
                                                                </div>
                                                                <div class="time-value">
                                                                    <c:choose>
                                                                        <c:when test="${record.oldStatus eq 'Present' or record.oldStatus eq 'On Time'}">
                                                                            <span class="badge bg-success px-3 py-2">
                                                                                <i class="fas fa-check me-1"></i>${record.oldStatus}
                                                                            </span>
                                                                        </c:when>
                                                                        <c:when test="${record.oldStatus eq 'Late'}">
                                                                            <span class="badge bg-warning text-dark px-3 py-2">
                                                                                <i class="fas fa-clock me-1"></i>${record.oldStatus}
                                                                            </span>
                                                                        </c:when>
                                                                        <c:when test="${record.oldStatus eq 'Early'}">
                                                                            <span class="badge bg-info px-3 py-2">
                                                                                <i class="fas fa-fast-forward me-1"></i>${record.oldStatus}
                                                                            </span>
                                                                        </c:when>
                                                                        <c:when test="${record.oldStatus eq 'Absent'}">
                                                                            <span class="badge bg-danger px-3 py-2">
                                                                                <i class="fas fa-times me-1"></i>${record.oldStatus}
                                                                            </span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="badge bg-secondary px-3 py-2">
                                                                                <i class="fas fa-question me-1"></i>
                                                                                ${not empty record.oldStatus ? record.oldStatus : 'Unknown'}
                                                                            </span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <c:if test="${not empty record.source}">
                                                            <div class="mt-3">
                                                                <small class="text-muted">
                                                                    <i class="fas fa-source me-1"></i>Source: 
                                                                    <span class="badge bg-light text-dark">${record.source}</span>
                                                                </small>
                                                            </div>
                                                        </c:if>
                                                    </div>
                                                </div>

                                                <!-- Arrow -->
                                                <div class="col-lg-2 d-flex align-items-center justify-content-center">
                                                    <div class="comparison-arrow">
                                                        <i class="fas fa-arrow-right text-primary fa-2x"></i>
                                                        <div class="text-center mt-2">
                                                            <small class="text-muted fw-medium">Requested Change</small>
                                                        </div>
                                                    </div>
                                                </div>

                                                <!-- Requested Times -->
                                                <div class="col-lg-5">
                                                    <div class="comparison-section requested-section">
                                                        <h6 class="text-success mb-3 d-flex align-items-center">
                                                            <i class="fas fa-edit me-2"></i>Requested Record
                                                        </h6>

                                                        <div class="time-info-grid">
                                                            <div class="time-info-item">
                                                                <div class="time-label">
                                                                    <i class="fas fa-sign-in-alt me-1"></i>Check-in
                                                                </div>
                                                                <div class="time-value">
                                                                    <span class="badge bg-success bg-opacity-10 text-success px-3 py-2">
                                                                        ${not empty record.newCheckIn ? record.newCheckIn : 'N/A'}
                                                                    </span>
                                                                </div>
                                                            </div>

                                                            <div class="time-info-item">
                                                                <div class="time-label">
                                                                    <i class="fas fa-sign-out-alt me-1"></i>Check-out
                                                                </div>
                                                                <div class="time-value">
                                                                    <span class="badge bg-success bg-opacity-10 text-success px-3 py-2">
                                                                        ${not empty record.newCheckOut ? record.newCheckOut : 'N/A'}
                                                                    </span>
                                                                </div>
                                                            </div>

                                                            <div class="time-info-item">
                                                                <div class="time-label">
                                                                    <i class="fas fa-flag me-1"></i>Status
                                                                </div>
                                                                <div class="time-value">
                                                                    <c:choose>
                                                                        <c:when test="${record.newStatus eq 'Present' or record.newStatus eq 'On Time'}">
                                                                            <span class="badge bg-success px-3 py-2">
                                                                                <i class="fas fa-check me-1"></i>${record.newStatus}
                                                                            </span>
                                                                        </c:when>
                                                                        <c:when test="${record.newStatus eq 'Late'}">
                                                                            <span class="badge bg-warning text-dark px-3 py-2">
                                                                                <i class="fas fa-clock me-1"></i>${record.newStatus}
                                                                            </span>
                                                                        </c:when>
                                                                        <c:when test="${record.newStatus eq 'Early'}">
                                                                            <span class="badge bg-info px-3 py-2">
                                                                                <i class="fas fa-fast-forward me-1"></i>${record.newStatus}
                                                                            </span>
                                                                        </c:when>
                                                                        <c:when test="${record.newStatus eq 'Absent'}">
                                                                            <span class="badge bg-danger px-3 py-2">
                                                                                <i class="fas fa-times me-1"></i>${record.newStatus}
                                                                            </span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="badge bg-secondary px-3 py-2">
                                                                                <i class="fas fa-question me-1"></i>
                                                                                ${not empty record.newStatus ? record.newStatus : 'Unknown'}
                                                                            </span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <!-- Time Difference Analysis -->
                                            <c:if test="${not empty record.oldCheckIn and not empty record.newCheckIn}">
                                                <hr class="my-4">
                                                <div class="change-summary">
                                                    <h6 class="text-muted mb-3 d-flex align-items-center">
                                                        <i class="fas fa-chart-line me-2"></i>Change Summary
                                                    </h6>
                                                    <div class="row g-3">
                                                        <div class="col-md-6">
                                                            <div class="change-item">
                                                                <div class="change-label">Check-in Time</div>
                                                                <div class="change-value">
                                                                    <span class="fw-bold text-primary">
                                                                        ${record.oldCheckIn} → ${record.newCheckIn}
                                                                    </span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <c:if test="${not empty record.oldCheckOut and not empty record.newCheckOut}">
                                                            <div class="col-md-6">
                                                                <div class="change-item">
                                                                    <div class="change-label">Check-out Time</div>
                                                                    <div class="change-value">
                                                                        <span class="fw-bold text-primary">
                                                                            ${record.oldCheckOut} → ${record.newCheckOut}
                                                                        </span>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>

                <!-- HR/HRM Review Notes -->
                <c:if test="${requestDto.status == 'APPROVED' or requestDto.status == 'REJECTED'}">
                    <div class="mb-4">
                        <h6 class="section-title mb-3">
                            <i class="fas fa-user-shield text-primary me-2"></i>Review Notes
                        </h6>

                        <!-- HR Notes -->
                        <c:if test="${not empty appealDetail.hrNotes}">
                            <div class="alert alert-info border-0 mb-3">
                                <h6 class="alert-heading mb-2">
                                    <i class="fas fa-user-tie me-2"></i>HR Staff Notes
                                </h6>
                                <c:out value="${appealDetail.hrNotes}" />
                            </div>
                        </c:if>

                        <!-- HRM Notes -->
                        <c:if test="${not empty appealDetail.hrmNotes}">
                            <div class="alert alert-primary border-0 mb-3">
                                <h6 class="alert-heading mb-2">
                                    <i class="fas fa-user-crown me-2"></i>HR Manager Notes
                                </h6>
                                <c:out value="${appealDetail.hrmNotes}" />
                            </div>
                        </c:if>

                        <!-- Resolution Action -->
                        <c:if test="${not empty appealDetail.resolutionAction}">
                            <div class="alert alert-success border-0 mb-0">
                                <h6 class="alert-heading mb-2">
                                    <i class="fas fa-check-circle me-2"></i>Resolution Action
                                </h6>
                                <c:out value="${appealDetail.resolutionAction}" />
                            </div>
                        </c:if>
                    </div>
                </c:if>

                <!-- Supporting Documents -->
                <c:if test="${not empty appealDetail.attachmentPath}">
                    <div class="mb-4">
                        <h6 class="section-title mb-3">
                            <i class="fas fa-paperclip text-primary me-2"></i>Supporting Documents
                        </h6>
                        <div class="attachment-preview">
                            <div class="d-flex align-items-center p-3 bg-light rounded">
                                <i class="fas fa-file-alt text-primary me-3 fa-2x"></i>
                                <div class="flex-grow-1">
                                    <h6 class="mb-1">Supporting Document</h6>
                                    <small class="text-muted">Click to view attachment</small>
                                </div>
                                <a href="${pageContext.request.contextPath}${appealDetail.attachmentPath}"
                                   target="_blank" 
                                   class="btn btn-outline-primary btn-sm">
                                    <i class="fas fa-external-link-alt me-1"></i>View
                                </a>
                            </div>
                        </div>
                    </div>
                </c:if>
            </c:when>

            <c:otherwise>
                <!-- No Appeal Detail Available -->
                <div class="alert alert-warning mb-0" role="alert">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    Appeal request details are not available or could not be parsed.
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<!-- Cus
tom CSS for Appeal Detail Section -->
<style>
    .info-item {
        margin-bottom: 1.5rem;
    }

    .info-label {
        font-weight: 600;
        color: #6c757d;
        margin-bottom: 0.5rem;
        font-size: 0.9rem;
    }

    .info-value {
        font-size: 1rem;
    }

    .section-title {
        font-weight: 600;
        color: #495057;
        border-bottom: 2px solid #e9ecef;
        padding-bottom: 0.5rem;
    }

    .comparison-section {
        background: #f8f9fa;
        border-radius: 0.5rem;
        padding: 1.5rem;
        height: 100%;
    }

    .original-section {
        border-left: 4px solid #dc3545;
    }

    .requested-section {
        border-left: 4px solid #198754;
    }

    .time-info-grid {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .time-info-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.75rem;
        background: white;
        border-radius: 0.375rem;
        border: 1px solid #e9ecef;
    }

    .time-label {
        font-weight: 500;
        color: #495057;
        font-size: 0.9rem;
    }

    .time-value {
        font-weight: 600;
    }

    .comparison-arrow {
        text-align: center;
        padding: 1rem;
    }

    .change-summary {
        background: #f8f9fa;
        border-radius: 0.5rem;
        padding: 1.5rem;
        border: 1px solid #e9ecef;
    }

    .change-item {
        text-align: center;
        padding: 1rem;
        background: white;
        border-radius: 0.375rem;
        border: 1px solid #e9ecef;
    }

    .change-label {
        font-size: 0.85rem;
        color: #6c757d;
        font-weight: 500;
        margin-bottom: 0.5rem;
    }

    .change-value {
        font-size: 1rem;
    }

    .attachment-preview {
        border: 1px solid #e9ecef;
        border-radius: 0.5rem;
        overflow: hidden;
    }

    .new-record-display {
        background: #f8fff8;
        border-radius: 0.5rem;
        padding: 1rem;
        border: 1px solid #d4edda;
    }

    @media (max-width: 768px) {
        .comparison-arrow {
            transform: rotate(90deg);
            margin: 1rem 0;
        }

        .time-info-item {
            flex-direction: column;
            text-align: center;
            gap: 0.5rem;
        }

        .change-item {
            margin-bottom: 1rem;
        }
    }
</style>