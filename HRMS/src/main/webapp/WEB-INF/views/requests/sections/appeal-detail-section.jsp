<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!-- Appeal Request Detail Section -->
<c:if test="${not empty appealDetail}">
    <div class="card mb-4">
        <div class="card-header bg-warning bg-opacity-10">
            <h5 class="card-title mb-0">
                <i class="fas fa-exclamation-triangle text-warning me-2"></i>
                Appeal Request Details
            </h5>
        </div>
        <div class="card-body">
            <!-- Set appeal status variable -->
            <c:set var="currentAppealStatus" value="${not empty appealDetail.appealStatus ? appealDetail.appealStatus : 'PENDING'}" />

            <!-- Basic Appeal Information -->
            <div class="row mb-4">
                <div class="col-md-4">
                    <h6 class="text-muted mb-2">Disputed Dates</h6>
                    <c:choose>
                        <c:when test="${not empty appealDetail.attendanceDates}">
                            <div class="d-flex flex-wrap gap-2">
                                <c:forEach var="date" items="${appealDetail.attendanceDates}">
                                    <span class="badge bg-warning text-dark">${date}</span>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <span class="text-muted">No dates specified</span>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="col-md-4">
                    <h6 class="text-muted mb-2">Submitted Date</h6>
                    <c:choose>
                        <c:when test="${not empty appealDetail.submittedDate}">
                            <span class="fw-medium">${appealDetail.submittedDate}</span>
                        </c:when>
                        <c:otherwise>
                            <span class="text-muted">Not specified</span>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="col-md-4">
                    <h6 class="text-muted mb-2">Appeal Reason</h6>
                    <c:choose>
                        <c:when test="${not empty appealDetail.reason}">
                            <div class="text-truncate" title="${appealDetail.reason}">
                                ${appealDetail.reason}
                            </div>
                        </c:when>
                        <c:otherwise>
                            <span class="text-muted">No reason provided</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Attendance Records Comparison -->
            <c:if test="${not empty attendanceRecords}">
                <div class="mb-4">
                    <h6 class="text-muted mb-3">Attendance Records Comparison</h6>
                    <c:forEach var="record" items="${attendanceRecords}" varStatus="status">
                        <div class="card mb-3">
                            <div class="card-header bg-light">
                                <h6 class="mb-0">
                                    <i class="fas fa-calendar-day me-2"></i>
                                    Date: ${record.date}
                                    <c:if test="${not empty record.period}">
                                        <small class="text-muted ms-2">(${record.period})</small>
                                    </c:if>
                                </h6>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <!-- Original Times -->
                                    <div class="col-md-6">
                                        <h6 class="text-danger mb-3">
                                            <i class="fas fa-clock me-1"></i>Original Times
                                        </h6>
                                        <div class="mb-2">
                                            <strong>Check-in:</strong>
                                            <span class="badge bg-danger-subtle text-danger ms-1">
                                                ${not empty record.oldCheckIn ? record.oldCheckIn : 'N/A'}
                                            </span>
                                        </div>
                                        <div class="mb-2">
                                            <strong>Check-out:</strong>
                                            <span class="badge bg-danger-subtle text-danger ms-1">
                                                ${not empty record.oldCheckOut ? record.oldCheckOut : 'N/A'}
                                            </span>
                                        </div>
                                        <div class="mb-2">
                                            <strong>Status:</strong>
                                            <c:choose>
                                                <c:when test="${record.oldStatus eq 'On Time'}">
                                                    <span class="badge bg-success ms-1">${record.oldStatus}</span>
                                                </c:when>
                                                <c:when test="${record.oldStatus eq 'Late'}">
                                                    <span class="badge bg-warning text-dark ms-1">${record.oldStatus}</span>
                                                </c:when>
                                                <c:when test="${record.oldStatus eq 'Early'}">
                                                    <span class="badge bg-info ms-1">${record.oldStatus}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary ms-1">
                                                        ${not empty record.oldStatus ? record.oldStatus : 'Unknown'}
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <c:if test="${not empty record.source}">
                                            <div class="mb-0">
                                                <small class="text-muted">Source: ${record.source}</small>
                                            </div>
                                        </c:if>
                                    </div>

                                    <!-- Arrow -->
                                    <div class="col-md-1 d-flex align-items-center justify-content-center">
                                        <i class="fas fa-arrow-right text-primary fa-2x"></i>
                                    </div>

                                    <!-- Requested Times -->
                                    <div class="col-md-5">
                                        <h6 class="text-success mb-3">
                                            <i class="fas fa-clock me-1"></i>Requested Times
                                        </h6>
                                        <div class="mb-2">
                                            <strong>Check-in:</strong>
                                            <span class="badge bg-success-subtle text-success ms-1">
                                                ${not empty record.newCheckIn ? record.newCheckIn : 'N/A'}
                                            </span>
                                        </div>
                                        <div class="mb-2">
                                            <strong>Check-out:</strong>
                                            <span class="badge bg-success-subtle text-success ms-1">
                                                ${not empty record.newCheckOut ? record.newCheckOut : 'N/A'}
                                            </span>
                                        </div>
                                        <div class="mb-2">
                                            <strong>Status:</strong>
                                            <c:choose>
                                                <c:when test="${record.newStatus eq 'On Time'}">
                                                    <span class="badge bg-success ms-1">${record.newStatus}</span>
                                </c:when>
                                                <c:when test="${record.newStatus eq 'Late'}">
                                                    <span class="badge bg-warning text-dark ms-1">${record.newStatus}</span>
                                                </c:when>
                                                <c:when test="${record.newStatus eq 'Early'}">
                                                    <span class="badge bg-info ms-1">${record.newStatus}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary ms-1">
                                                        ${not empty record.newStatus ? record.newStatus : 'Unknown'}
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>

                                <!-- Time Difference Analysis -->
                                <c:if test="${not empty record.oldCheckIn and not empty record.newCheckIn}">
                                    <hr class="my-3">
                                    <div class="row">
                                        <div class="col-12">
                                            <h6 class="text-muted mb-2">
                                                <i class="fas fa-chart-line me-1"></i>Time Changes
                                            </h6>
                                            <div class="d-flex flex-wrap gap-3">
                                                <div class="text-center">
                                                    <small class="text-muted d-block">Check-in Change</small>
                                                    <span class="fw-bold">
                                                        ${record.oldCheckIn} → ${record.newCheckIn}
                                                    </span>
                                                </div>
                                                <c:if test="${not empty record.oldCheckOut and not empty record.newCheckOut}">
                                                    <div class="text-center">
                                                        <small class="text-muted d-block">Check-out Change</small>
                                                        <span class="fw-bold">
                                                            ${record.oldCheckOut} → ${record.newCheckOut}
                                                        </span>
                                                    </div>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>



            <!-- Attachment Information -->
            <c:if test="${not empty appealDetail.attachmentPath}">
                <div class="mb-4">
                    <h6 class="text-muted mb-2">Supporting Document</h6>
                    <div class="d-flex align-items-center">
                        <i class="fas fa-paperclip text-primary me-2"></i>
                        <a href="${pageContext.request.contextPath}${appealDetail.attachmentPath}"
                           target="_blank" class="text-decoration-none">
                            View Attachment
                        </a>
                    </div>
                </div>
            </c:if>




        </div>
    </div>
</c:if>