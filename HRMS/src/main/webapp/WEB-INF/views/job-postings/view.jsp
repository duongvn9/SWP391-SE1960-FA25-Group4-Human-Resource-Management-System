<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="View Job Posting - HRMS"/>
    </jsp:include>
    <style>
        .job-detail-section {
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            padding: 24px;
            margin-bottom: 20px;
        }
        
        .job-title {
            color: #2c3e50;
            margin-bottom: 16px;
        }
        
        .job-meta {
            color: #6c757d;
            font-size: 0.9rem;
            margin-bottom: 24px;
        }
        
        .job-meta i {
            width: 20px;
        }
        
        .job-description {
            white-space: pre-wrap;
            line-height: 1.6;
        }
        
        .status-badge {
            padding: 6px 12px;
            border-radius: 16px;
            font-weight: 500;
        }
        </style>
</head>
<body>
    <div class="alert alert-info">DEBUG: loggedInUser.positionId = ${sessionScope.loggedInUser.positionId}</div>
    <!-- Sidebar -->
    <jsp:include page="../layout/sidebar.jsp">
        <jsp:param name="currentPage" value="job-postings"/>
    </jsp:include>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Header -->
        <jsp:include page="../layout/dashboard-header.jsp"/>

        <!-- Content Area -->
        <div class="content-area p-4">
            <div class="container-fluid">
                <!-- Back button -->
                <div class="mb-4">
                    <a href="${pageContext.request.contextPath}/job-postings" class="btn btn-outline-secondary">
                        <i class="fas fa-arrow-left"></i> Back to Job Postings
                    </a>
                </div>

                <!-- Job Details -->
                <div class="job-detail-section">
                    <c:if test="${not empty debug_priority or not empty debug_workingHours}">
                        <div class="alert alert-secondary">
                            <strong>DEBUG:</strong> debug_priority = ${debug_priority}, debug_workingHours = ${debug_workingHours}
                        </div>
                    </c:if>
                    <div class="d-flex justify-content-between align-items-start mb-4">
                        <div>
                            <h2 class="job-title mb-2">${jobPosting.title}</h2>
                            <div class="job-meta">
                                <div class="mb-2">
                                    <i class="fas fa-code me-2"></i> Code: ${jobPosting.code}
                                </div>
                                <div class="mb-2">
                                    <i class="fas fa-building me-2"></i> Department: 
                                    <c:forEach items="${departments}" var="dept">
                                        <c:if test="${dept.id == jobPosting.departmentId}">
                                            ${dept.name}
                                        </c:if>
                                    </c:forEach>
                                </div>
                                <div class="mb-2">
                                    <i class="fas fa-users me-2"></i> Positions: ${jobPosting.numberOfPositions}
                                </div>
                                <div class="mb-2">
                                    <i class="fas fa-briefcase me-2"></i> Type: ${jobPosting.jobType}
                                </div>
                                <div class="mb-2">
                                    <i class="fas fa-layer-group me-2"></i> Level: ${jobPosting.level}
                                </div>
                                <div class="mb-2">
                                    <i class="fas fa-money-bill-wave me-2"></i> Salary Range: ${jobPosting.salaryRange}
                                </div>
                                <div class="mb-2">
                                    <i class="fas fa-location-dot me-2"></i> Working Location: ${jobPosting.workingLocation}
                                </div>
                                <div class="mb-2">
                                    <i class="fas fa-calendar-day me-2"></i> Expected Start Date: 
                                    <c:if test="${not empty jobPosting.startDate}">
                                        <fmt:parseDate value="${jobPosting.startDate}" pattern="yyyy-MM-dd" var="startDate"/>
                                        <fmt:formatDate value="${startDate}" pattern="MMMM d, yyyy"/>
                                    </c:if>
                                    <c:if test="${empty jobPosting.startDate}">TBD</c:if>
                                </div>
                                <div class="mb-2">
                                    <i class="fas fa-user-clock me-2"></i> Min Experience: <c:out value="${jobPosting.minExperienceYears}"/> years
                                </div>
                                <div class="mb-2">
                                    <i class="fas fa-clock me-2"></i> Working Hours: 
                                    <c:choose>
                                        <c:when test="${not empty jobPosting.workingHours}">
                                            <c:out value="${jobPosting.workingHours}"/>
                                        </c:when>
                                        <c:otherwise>Not provided</c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="mb-2">
                                    <i class="fas fa-envelope me-2"></i> Contact Email: 
                                    <c:choose>
                                        <c:when test="${not empty jobPosting.contactEmail}">
                                            <a href="mailto:${jobPosting.contactEmail}">${jobPosting.contactEmail}</a>
                                        </c:when>
                                        <c:otherwise>Not provided</c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="mb-2">
                                    <i class="fas fa-phone me-2"></i> Contact Phone: <c:out value="${jobPosting.contactPhone}"/>
                                </div>
                                <div class="mb-2">
                                    <i class="fas fa-calendar me-2"></i> Deadline: 
                                    <fmt:parseDate value="${jobPosting.applicationDeadline}" pattern="yyyy-MM-dd" var="deadline"/>
                                    <fmt:formatDate value="${deadline}" pattern="MMMM d, yyyy"/>
                                </div>
                                <div>
                                    <i class="fas fa-flag me-2"></i> Priority:
                                    <c:choose>
                                        <c:when test="${not empty jobPosting.priority}">
                                            <c:choose>
                                                <c:when test="${jobPosting.priority == 'URGENT'}"><c:set var="priorityClass" value="bg-danger"/></c:when>
                                                <c:when test="${jobPosting.priority == 'HIGH'}"><c:set var="priorityClass" value="bg-warning"/></c:when>
                                                <c:when test="${jobPosting.priority == 'MEDIUM'}"><c:set var="priorityClass" value="bg-info"/></c:when>
                                                <c:otherwise><c:set var="priorityClass" value="bg-secondary"/></c:otherwise>
                                            </c:choose>
                                            <span class="badge ${priorityClass}">
                                                <c:out value="${jobPosting.priority}"/>
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">Not set</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            
                            <div class="status mb-4">
                                <strong>Status:</strong>
                                <span class="status-badge ms-2 ${jobPosting.status == 'PENDING' ? 'bg-warning' :
                                                                jobPosting.status == 'APPROVED' ? 'bg-info' :
                                                                jobPosting.status == 'REJECTED' ? 'bg-danger' :
                                                                jobPosting.status == 'PUBLISHED' ? 'bg-success' : 'bg-secondary'}">
                                    ${jobPosting.status}
                                </span>
                            </div>
                        </div>
                        
                        <!-- Action buttons based on status and user role -->
                        <div>
                            <c:if test="${sessionScope.loggedInUser != null && sessionScope.loggedInUser.positionId == 7 && jobPosting.status == 'PENDING'}">
                                <button type="button" class="btn btn-success me-2" onclick="approveJobPosting('${jobPosting.id}')">
                                    <i class="fas fa-check-circle"></i> Approve
                                </button>
                                <button type="button" class="btn btn-danger" onclick="rejectJobPosting('${jobPosting.id}')">
                                    <i class="fas fa-times-circle"></i> Reject
                                </button>
                            </c:if>
                            
                            <c:if test="${sessionScope.loggedInUser != null && sessionScope.loggedInUser.positionId == 8 && jobPosting.status == 'PENDING'}">
                                <a href="${pageContext.request.contextPath}/job-posting/edit?id=${jobPosting.id}" 
                                   class="btn btn-primary">
                                    <i class="fas fa-edit"></i> Edit
                                </a>
                            </c:if>
                            
                            <c:if test="${sessionScope.loggedInUser != null && sessionScope.loggedInUser.positionId == 7 && jobPosting.status == 'APPROVED'}">
                                <button type="button" class="btn btn-primary" onclick="publishJobPosting('${jobPosting.id}')">
                                    <i class="fas fa-globe"></i> Publish
                                </button>
                            </c:if>
                        </div>
                    </div>

                    <!-- Job Description -->
                    <div class="job-description-section mb-4">
                        <h4>Job Description</h4>
                        <div class="job-description">
                            ${jobPosting.description}
                        </div>
                    </div>

                    <!-- Requirements -->
                    <div class="requirements-section mb-4">
                        <h4>Requirements</h4>
                        <div class="job-description">
                            ${jobPosting.requirements}
                        </div>
                    </div>

                    <!-- Benefits -->
                    <div class="benefits-section">
                        <h4>Benefits</h4>
                        <div class="job-description">
                            ${jobPosting.benefits}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Confirm Approve Modal -->
    <div class="modal fade" id="approveModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Approve Job Posting</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    Are you sure you want to approve this job posting?
                </div>
                <div class="modal-footer">
                    <form method="post" action="${pageContext.request.contextPath}/job-posting/approve">
                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                        <input type="hidden" name="id" id="approveJobId"/>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-success">Approve</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Reject Modal -->
    <div class="modal fade" id="rejectModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Reject Job Posting</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form method="post" action="${pageContext.request.contextPath}/job-posting/reject">
                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                        <input type="hidden" name="id" id="rejectJobId"/>
                        <div class="mb-3">
                            <label for="rejectReason" class="form-label">Reason for rejection</label>
                            <textarea class="form-control" id="rejectReason" name="reason" rows="3" required></textarea>
                        </div>
                        <div class="text-end">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-danger">Reject</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Publish Modal -->
    <div class="modal fade" id="publishModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Publish Job Posting</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    Are you sure you want to publish this job posting?
                    <br><br>
                    <small class="text-muted">
                        This will make the job posting visible to the public.
                    </small>
                </div>
                <div class="modal-footer">
                    <form method="post" action="${pageContext.request.contextPath}/job-posting/publish">
                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                        <input type="hidden" name="id" id="publishJobId"/>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Publish</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script>
        // Initialize modals
        const approveModal = new bootstrap.Modal(document.getElementById('approveModal'));
        const rejectModal = new bootstrap.Modal(document.getElementById('rejectModal'));
        const publishModal = new bootstrap.Modal(document.getElementById('publishModal'));
        
        function approveJobPosting(id) {
            document.getElementById('approveJobId').value = id;
            approveModal.show();
        }
        
        function rejectJobPosting(id) {
            document.getElementById('rejectJobId').value = id;
            rejectModal.show();
        }
        
        function publishJobPosting(id) {
            document.getElementById('publishJobId').value = id;
            publishModal.show();
        }
    </script>
</body>
</html>