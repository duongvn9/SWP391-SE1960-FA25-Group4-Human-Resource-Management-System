<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!-- Appeal Request Detail Section -->
<div class="card mb-4 shadow-sm border-0">
    <div class="card-header bg-gradient-warning text-white">
        <h5 class="mb-0">
            <i class="fas fa-user-clock me-2"></i>Appeal Request Details
        </h5>
    </div>
    <div class="card-body p-4">
        <c:choose>
            <c:when test="${not empty requestDto.appealDetail}">
                <div class="row g-4">
                    <!-- Left Column: Appeal Information -->
                    <div class="col-lg-6">
                        <div class="detail-section">
                            <h6 class="section-title mb-3">
                                <i class="fas fa-calendar-alt text-warning me-2"></i>Attendance Dates
                            </h6>

                            <!-- Attendance Dates -->
                            <div class="info-item mb-3">
                                <div class="info-label">
                                    <i class="fas fa-calendar-day me-2"></i>Disputed Dates
                                </div>
                                <div class="info-value">
                                    <c:choose>
                                        <c:when test="${not empty requestDto.appealDetail.attendanceDates}">
                                            <div class="date-badges">
                                                <c:forEach items="${requestDto.appealDetail.attendanceDates}" var="date" varStatus="status">
                                                    <span class="badge bg-warning text-dark mb-2 me-2">
                                                        <i class="fas fa-calendar me-1"></i>
                                                        <c:out value="${date}" />
                                                    </span>
                                                    <c:if test="${not status.last}">
                                                        <br/>
                                                    </c:if>
                                                </c:forEach>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted fst-italic">No dates specified</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <!-- Reason -->
                            <div class="info-item mb-3">
                                <div class="info-label">
                                    <i class="fas fa-question-circle me-2"></i>Reason
                                </div>
                                <div class="info-value">
                                    <c:choose>
                                        <c:when test="${not empty requestDto.appealDetail.reason}">
                                            <div class="alert alert-warning mb-0">
                                                <c:out value="${requestDto.appealDetail.reason}" />
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted fst-italic">No reason provided</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <!-- Submitted Date -->
                            <c:if test="${not empty requestDto.appealDetail.submittedDate}">
                                <div class="info-item">
                                    <div class="info-label">
                                        <i class="fas fa-clock me-2"></i>Submitted Date
                                    </div>
                                    <div class="info-value">
                                        <c:out value="${requestDto.appealDetail.submittedDate}" />
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </div>

                    <!-- Right Column: Attachments & Additional Info -->
                    <div class="col-lg-6">
                        <div class="detail-section">
                            <h6 class="section-title mb-3">
                                <i class="fas fa-paperclip text-warning me-2"></i>Attachments & Additional Info
                            </h6>

                            <!-- Attachment Path (if any) -->
                            <c:choose>
                                <c:when test="${not empty requestDto.appealDetail.attachmentPath}">
                                    <div class="info-item">
                                        <div class="info-label">
                                            <i class="fas fa-paperclip me-2"></i>Attachment Reference
                                        </div>
                                        <div class="info-value">
                                            <span class="badge bg-secondary">
                                                <i class="fas fa-file me-1"></i>
                                                <c:out value="${requestDto.appealDetail.attachmentPath}" />
                                            </span>
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert alert-light mb-0">
                                        <i class="fas fa-info-circle me-2"></i>
                                        No attachment provided for this appeal request.
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <!-- HR/HRM Review Section (if exists) -->
                <c:if test="${not empty requestDto.appealDetail.hrNotes || not empty requestDto.appealDetail.hrmNotes || not empty requestDto.appealDetail.resolutionAction}">
                    <hr class="my-4">
                    <div class="review-section">
                        <h6 class="section-title mb-3">
                            <i class="fas fa-user-shield text-primary me-2"></i>Review & Resolution
                        </h6>

                        <div class="row g-3">
                            <!-- HR Notes -->
                            <c:if test="${not empty requestDto.appealDetail.hrNotes}">
                                <div class="col-12">
                                    <div class="alert alert-info border-0">
                                        <h6 class="alert-heading">
                                            <i class="fas fa-user-tie me-2"></i>HR Notes
                                        </h6>
                                        <p class="mb-0">
                                            <c:out value="${requestDto.appealDetail.hrNotes}" />
                                        </p>
                                    </div>
                                </div>
                            </c:if>

                            <!-- HRM Notes -->
                            <c:if test="${not empty requestDto.appealDetail.hrmNotes}">
                                <div class="col-12">
                                    <div class="alert alert-primary border-0">
                                        <h6 class="alert-heading">
                                            <i class="fas fa-user-shield me-2"></i>HRM Notes
                                        </h6>
                                        <p class="mb-0">
                                            <c:out value="${requestDto.appealDetail.hrmNotes}" />
                                        </p>
                                    </div>
                                </div>
                            </c:if>

                            <!-- Resolution Action -->
                            <c:if test="${not empty requestDto.appealDetail.resolutionAction}">
                                <div class="col-12">
                                    <div class="alert alert-success border-0">
                                        <h6 class="alert-heading">
                                            <i class="fas fa-check-circle me-2"></i>Resolution Action
                                        </h6>
                                        <p class="mb-0">
                                            <c:out value="${requestDto.appealDetail.resolutionAction}" />
                                        </p>
                                    </div>
                                </div>
                            </c:if>
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
