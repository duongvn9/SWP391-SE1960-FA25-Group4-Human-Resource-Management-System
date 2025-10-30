<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!-- Recruitment Request Detail Section -->
<div class="card mb-4 shadow-sm border-0">
    <div class="card-header bg-gradient-success text-white">
        <h5 class="mb-0">
            <i class="fas fa-user-plus me-2"></i>Recruitment Request Details
        </h5>
    </div>
    <div class="card-body p-4">
        <c:choose>
            <c:when test="${not empty requestDto.recruitmentDetail}">
                <div class="row g-4">
                    <!-- Left Column: Position Information -->
                    <div class="col-lg-6">
                        <div class="detail-section">
                            <h6 class="section-title mb-3">
                                <i class="fas fa-briefcase text-success me-2"></i>Position Information
                            </h6>

                            <!-- Position Name -->
                            <div class="info-item mb-3">
                                <div class="info-label">
                                    <i class="fas fa-id-badge me-2"></i>Position Name
                                </div>
                                <div class="info-value">
                                    <strong><c:out value="${requestDto.recruitmentDetail.positionName}" /></strong>
                                </div>
                            </div>

                            <!-- Job Level -->
                            <c:if test="${not empty requestDto.recruitmentDetail.jobLevel}">
                                <div class="info-item mb-3">
                                    <div class="info-label">
                                        <i class="fas fa-layer-group me-2"></i>Job Level
                                    </div>
                                    <div class="info-value">
                                        <span class="badge bg-info">
                                            <c:out value="${requestDto.recruitmentDetail.jobLevel}" />
                                        </span>
                                    </div>
                                </div>
                            </c:if>

                            <!-- Quantity -->
                            <div class="info-item mb-3">
                                <div class="info-label">
                                    <i class="fas fa-users me-2"></i>Quantity Needed
                                </div>
                                <div class="info-value">
                                    <span class="badge bg-success text-white">
                                        <i class="fas fa-user-plus me-1"></i>
                                        ${requestDto.recruitmentDetail.quantity} positions
                                    </span>
                                </div>
                            </div>

                            <!-- Job Type (Full-time/Part-time/Contract) -->
                            <c:if test="${not empty requestDto.recruitmentDetail.jobType}">
                                <div class="info-item">
                                    <div class="info-label">
                                        <i class="fas fa-clock me-2"></i>Job Type
                                    </div>
                                    <div class="info-value">
                                        <c:choose>
                                            <c:when test="${requestDto.recruitmentDetail.jobType == 'FULL_TIME'}">
                                                <span class="badge bg-primary">
                                                    <i class="fas fa-briefcase me-1"></i>Full-time
                                                </span>
                                            </c:when>
                                            <c:when test="${requestDto.recruitmentDetail.jobType == 'PART_TIME'}">
                                                <span class="badge bg-warning text-dark">
                                                    <i class="fas fa-clock me-1"></i>Part-time
                                                </span>
                                            </c:when>
                                            <c:when test="${requestDto.recruitmentDetail.jobType == 'CONTRACT'}">
                                                <span class="badge bg-info">
                                                    <i class="fas fa-file-contract me-1"></i>Contract
                                                </span>
                                            </c:when>
                                            <c:when test="${requestDto.recruitmentDetail.jobType == 'INTERNSHIP'}">
                                                <span class="badge bg-secondary">
                                                    <i class="fas fa-graduation-cap me-1"></i>Internship
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">
                                                    <c:out value="${requestDto.recruitmentDetail.jobType}" />
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </div>

                    <!-- Right Column: Salary & Additional Information -->
                    <div class="col-lg-6">
                        <div class="detail-section">
                            <h6 class="section-title mb-3">
                                <i class="fas fa-dollar-sign text-success me-2"></i>Salary & Additional Info
                            </h6>

                            <!-- Salary Information -->
                            <c:if test="${not empty requestDto.recruitmentDetail.minSalary and not empty requestDto.recruitmentDetail.maxSalary}">
                                <div class="info-item mb-3">
                                    <div class="info-label">
                                        <i class="fas fa-money-bill-wave me-2"></i>Salary Range
                                    </div>
                                    <div class="info-value">
                                        <div class="alert alert-success mb-0">
                                            <i class="fas fa-coins me-2"></i>
                                            <strong>
                                                <fmt:formatNumber value="${requestDto.recruitmentDetail.minSalary}" type="number" groupingUsed="true" />
                                                -
                                                <fmt:formatNumber value="${requestDto.recruitmentDetail.maxSalary}" type="number" groupingUsed="true" />
                                                VND
                                            </strong>
                                            <c:if test="${not empty requestDto.recruitmentDetail.salaryType}">
                                                <span class="badge bg-success ms-2">
                                                    <c:out value="${requestDto.recruitmentDetail.salaryType}" />
                                                </span>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </c:if>

                            <!-- Working Location -->
                            <c:if test="${not empty requestDto.recruitmentDetail.workingLocation}">
                                <div class="info-item mb-3">
                                    <div class="info-label">
                                        <i class="fas fa-map-marker-alt me-2"></i>Working Location
                                    </div>
                                    <div class="info-value">
                                        <span class="badge bg-primary">
                                            <i class="fas fa-building me-1"></i>
                                            <c:out value="${requestDto.recruitmentDetail.workingLocation}" />
                                        </span>
                                    </div>
                                </div>
                            </c:if>

                            <!-- Recruitment Reason -->
                            <c:if test="${not empty requestDto.recruitmentDetail.recruitmentReason}">
                                <div class="info-item mb-3">
                                    <div class="info-label">
                                        <i class="fas fa-question-circle me-2"></i>Recruitment Reason
                                    </div>
                                    <div class="info-value">
                                        <div class="alert alert-light border mb-0">
                                            <c:out value="${requestDto.recruitmentDetail.recruitmentReason}" />
                                        </div>
                                    </div>
                                </div>
                            </c:if>

                            <!-- Attachments from JSON -->
                            <c:if test="${not empty requestDto.recruitmentDetail.attachments}">
                                <div class="info-item">
                                    <div class="info-label">
                                        <i class="fas fa-paperclip me-2"></i>Attachments
                                    </div>
                                    <div class="info-value">
                                        <c:forEach var="attachment" items="${requestDto.recruitmentDetail.attachments}" varStatus="status">
                                            <div class="mb-2">
                                                <a href="${attachment}" target="_blank" class="text-decoration-none">
                                                    <span class="badge bg-primary">
                                                        <i class="fab fa-google-drive me-1"></i>
                                                        Google Drive Link ${status.index + 1}
                                                    </span>
                                                </a>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>

                <!-- Job Summary Section -->
                <c:if test="${not empty requestDto.recruitmentDetail.jobSummary}">
                    <hr class="my-4">
                    <div class="job-summary-section">
                        <h6 class="section-title mb-3">
                            <i class="fas fa-file-alt text-primary me-2"></i>Job Description
                        </h6>
                        <div class="alert alert-info border-0">
                            <pre class="mb-0" style="white-space: pre-wrap; font-family: inherit;"><c:out value="${requestDto.recruitmentDetail.jobSummary}" /></pre>
                        </div>
                    </div>
                </c:if>
            </c:when>
            <c:otherwise>
                <!-- No Recruitment Detail Available -->
                <div class="alert alert-warning mb-0" role="alert">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    Recruitment request details are not available or could not be parsed.
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
