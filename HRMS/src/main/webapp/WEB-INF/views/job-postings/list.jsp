<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="Job Postings - HRMS" />
        <jsp:param name="pageCss" value="job-posting.css" />
    </jsp:include>
    <style>
        /* Custom styles for Job Postings list */
        .content-area {
            padding: 1.5rem;
        }

        .page-title {
            color: #2c3e50;
            font-size: 1.75rem;
            margin-bottom: 0.25rem;
        }

        .page-subtitle {
            color: #6c757d;
            font-size: 0.875rem;
        }

        .filter-card {
            background-color: #f8f9fa;
            border: none;
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
            margin-bottom: 1.5rem;
        }

        .filter-card .card-body {
            padding: 1.25rem;
        }

        .table {
            background-color: #ffffff;
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
        }

        .table th {
            background-color: #f8f9fa;
            border-bottom: 2px solid #dee2e6;
            color: #495057;
            font-weight: 600;
        }

        .badge {
            font-weight: 500;
            padding: 0.5em 0.75em;
        }

        /* Responsive adjustments */
        @media (max-width: 768px) {
            .content-area {
                padding: 1rem;
            }

            .page-head {
                flex-direction: column;
                gap: 1rem;
                text-align: center;
            }

            .filter-form {
                flex-direction: column;
            }

            .filter-form .col-md-2 {
                width: 100%;
                margin-bottom: 1rem;
            }

            .table-responsive {
                border: 0;
                margin-bottom: 0;
            }

            .pagination {
                flex-wrap: wrap;
                justify-content: center;
                gap: 0.5rem;
            }
        }

        /* Animation for status badges */
        .badge {
            transition: all 0.2s ease-in-out;
        }
        
        .badge:hover {
            transform: scale(1.1);
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <jsp:include page="../layout/sidebar.jsp">
        <jsp:param name="currentPage" value="job-postings" />
    </jsp:include>

    <!-- Main Content -->
    <div class="main-content" id="main-content">
        <!-- Header -->
        <jsp:include page="../layout/dashboard-header.jsp" />

        <!-- Content Area -->
        <div class="content-area">
           

            <!-- Page Title -->
            <div class="page-head d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="page-title"><i class="fas fa-briefcase me-2"></i>Job Postings</h2>
                    <p class="page-subtitle">
                        <i class="fas fa-info-circle me-1"></i>
                        Manage and track all job postings
                        <c:if test="${not empty jobPostings}">
                            <span class="badge bg-primary ms-2">${jobPostings.size()} Records</span>
                        </c:if>
                    </p>
                </div>
                
                <!-- Only HR Staff (8) can create new job postings. HR Manager (7) can only approve/reject/publish. -->
                <c:if test="${sessionScope.user != null && sessionScope.user.positionId == 8}">
                    <a href="${pageContext.request.contextPath}/recruitment/approved" 
                       class="btn btn-primary">
                        <i class="fas fa-plus-circle me-1"></i> Create New Posting
                    </a>
                </c:if>
            </div>

        <!-- Success/Error Messages -->
        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>${param.success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>${param.error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Quick Stats -->
        <c:if test="${not empty jobPostings}">
            <div class="row mb-4">
                <div class="col-md-3 col-sm-6 mb-3">
                    <div class="card border-0 shadow-sm">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-center">
                                <div>
                                    <h6 class="text-muted mb-1">Total Postings</h6>
                                    <h3 class="mb-0">${totalItems}</h3>
                                </div>
                                <div class="text-primary">
                                    <i class="fas fa-briefcase fa-2x"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 col-sm-6 mb-3">
                    <div class="card border-0 shadow-sm">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-center">
                                <div>
                                    <h6 class="text-muted mb-1">Current Page</h6>
                                    <h3 class="mb-0">${currentPage} / ${totalPages > 0 ? totalPages : 1}</h3>
                                </div>
                                <div class="text-info">
                                    <i class="fas fa-file-alt fa-2x"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 col-sm-6 mb-3">
                    <div class="card border-0 shadow-sm">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-center">
                                <div>
                                    <h6 class="text-muted mb-1">Page Size</h6>
                                    <h3 class="mb-0">${jobPostings.size()} / ${pageSize}</h3>
                                </div>
                                <div class="text-success">
                                    <i class="fas fa-list fa-2x"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 col-sm-6 mb-3">
                    <div class="card border-0 shadow-sm">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-center">
                                <div>
                                    <h6 class="text-muted mb-1">Active Filters</h6>
                                    <h3 class="mb-0">
                                        ${(not empty param.status ? 1 : 0) + 
                                          (not empty param.departmentId ? 1 : 0) + 
                                          (not empty param.jobType ? 1 : 0) + 
                                          (not empty param.jobLevel ? 1 : 0)}
                                    </h3>
                                </div>
                                <div class="text-warning">
                                    <i class="fas fa-filter fa-2x"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- Filters -->
        <div class="card filter-card">
            <div class="card-body">
                <form method="get" class="row g-3 filter-form">
                    <div class="col-md-2">
                        <label class="form-label">
                            <i class="fas fa-filter me-1"></i> Status</label>
                        <select name="status" class="form-select">
                            <option value="">All</option>
                            <option value="PENDING" ${param.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                            <option value="APPROVED" ${param.status == 'APPROVED' ? 'selected' : ''}>Approved</option>
                            <option value="REJECTED" ${param.status == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                            <option value="PUBLISHED" ${param.status == 'PUBLISHED' ? 'selected' : ''}>Published</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Department</label>
                        <select name="departmentId" class="form-select">
                            <option value="">All</option>
                            <c:forEach items="${departments}" var="dept">
                                <option value="${dept.id}" ${param.departmentId == dept.id ? 'selected' : ''}>
                                    ${dept.name}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Job Type</label>
                        <select name="jobType" class="form-select">
                            <option value="">All</option>
                            <option value="FULL_TIME" ${param.jobType == 'FULL_TIME' ? 'selected' : ''}>Full Time</option>
                            <option value="PART_TIME" ${param.jobType == 'PART_TIME' ? 'selected' : ''}>Part Time</option>
                            <option value="CONTRACT" ${param.jobType == 'CONTRACT' ? 'selected' : ''}>Contract</option>
                            <option value="INTERN" ${param.jobType == 'INTERN' ? 'selected' : ''}>Intern</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Level</label>
                        <select name="jobLevel" class="form-select">
                            <option value="">All</option>
                            <option value="JUNIOR" ${param.jobLevel == 'JUNIOR' ? 'selected' : ''}>Junior</option>
                            <option value="MIDDLE" ${param.jobLevel == 'MIDDLE' ? 'selected' : ''}>Middle</option>
                            <option value="SENIOR" ${param.jobLevel == 'SENIOR' ? 'selected' : ''}>Senior</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label d-block">&nbsp;</label>
                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary flex-grow-1">
                                <i class="fas fa-filter me-1"></i> Apply Filters
                            </button>
                            <a href="${pageContext.request.contextPath}/job-postings" 
                               class="btn btn-outline-secondary">
                                <i class="fas fa-times me-1"></i> Clear
                            </a>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Job Postings Table -->
        <div class="table-responsive">
            <table class="table table-striped table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th style="width: 25%;"><i class="fas fa-file-alt me-1"></i> Job Title & Code</th>
                        <th style="width: 15%;"><i class="fas fa-briefcase me-1"></i> Type / Level</th>
                        <th style="width: 10%;" class="text-center"><i class="fas fa-users me-1"></i>Quantity</th>
                        <th style="width: 12%;"><i class="fas fa-info-circle me-1"></i> Status</th>
                        <th style="width: 13%;"><i class="fas fa-clock me-1"></i> Deadline</th>
                        <th style="width: 13%;"><i class="fas fa-user me-1"></i> Created By</th>
                        <th style="width: 12%;" class="text-center"><i class="fas fa-cogs me-1"></i> Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${jobPostings}" var="job">
                        <tr>
                            <td>
                                <a href="${pageContext.request.contextPath}/job-postings/view?id=${job.id}"
                                   class="text-decoration-none fw-semibold">
                                    ${job.title}
                                </a>
                                <br>
                                <small class="text-muted">
                                    <i class="fas fa-hashtag"></i> ${job.code != null && !job.code.isEmpty() ? job.code : 'N/A'}
                                </small>
                            </td>
                            <td>
                                <div>${job.jobType != null ? job.jobType : 'N/A'}</div>
                                <small class="text-muted">${job.level != null ? job.level : 'N/A'}</small>
                            </td>
                            <td class="text-center">
                                <span class="badge bg-secondary">${job.numberOfPositions}</span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${job.status == 'PENDING'}">
                                        <span class="badge bg-warning">Pending</span>
                                    </c:when>
                                    <c:when test="${job.status == 'APPROVED'}">
                                        <span class="badge bg-info">Approved</span>
                                    </c:when>
                                    <c:when test="${job.status == 'REJECTED'}">
                                        <span class="badge bg-danger">Rejected</span>
                                    </c:when>
                                    <c:when test="${job.status == 'PUBLISHED'}">
                                        <span class="badge bg-success">Published</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">${job.status}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty job.applicationDeadline}">
                                        <fmt:parseDate value="${job.applicationDeadline}" pattern="yyyy-MM-dd" var="deadline"/>
                                        <div><fmt:formatDate value="${deadline}" pattern="MMM d, yyyy"/></div>
                                        <c:set var="now" value="<%= new java.util.Date() %>"/>
                                        <c:if test="${deadline.time < now.time}">
                                            <small class="text-danger"><i class="fas fa-exclamation-triangle"></i> Expired</small>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted">Not set</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty job.createdByAccountId}">
                                        <div class="small">
                                            <i class="fas fa-user-circle"></i> ID: ${job.createdByAccountId}
                                        </div>
                                        <c:if test="${not empty job.createdAt}">
                                            <small class="text-muted">
                                                <fmt:parseDate value="${job.createdAt}" pattern="yyyy-MM-dd'T'HH:mm" var="createdDate"/>
                                                <fmt:formatDate value="${createdDate}" pattern="MMM d, HH:mm"/>
                                            </small>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted">Unknown</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center text-nowrap">
                                <!-- View Details button - Everyone can view -->
                                <button type="button" 
                                        class="btn btn-sm btn-info" 
                                        data-bs-toggle="tooltip" 
                                        title="View Details"
                                        onclick="window.location.href='${pageContext.request.contextPath}/job-postings/view?id=${job.id}'">
                                    <i class="fas fa-eye"></i>
                                </button>
                                
                                <!-- Actions only visible for HR Manager (7) and HR Staff (8), NOT for Department Manager (9) -->
                                <c:if test="${sessionScope.user != null && sessionScope.user.positionId != 9}">
                                    <!-- Approve/Reject buttons for HR Manager (position_id = 7) when status is PENDING -->
                                    <c:if test="${sessionScope.user != null && sessionScope.user.positionId == 7 && job.status == 'PENDING'}">
                                        <button type="button" class="btn btn-sm btn-success"
                                                onclick="approveJobPosting('${job.id}')" 
                                                data-bs-toggle="tooltip"
                                                title="Approve Job Posting">
                                            <i class="fas fa-check-circle"></i>
                                        </button>
                                        <button type="button" class="btn btn-sm btn-danger"
                                                onclick="rejectJobPosting('${job.id}')"
                                                data-bs-toggle="tooltip" 
                                                title="Reject Job Posting">
                                            <i class="fas fa-times-circle"></i>
                                        </button>
                                    </c:if>
                                    
                                    <!-- Edit button for HR Staff (position_id = 8) when status is PENDING or REJECTED -->
                                    <c:if test="${sessionScope.user != null && sessionScope.user.positionId == 8 && (job.status == 'PENDING' || job.status == 'REJECTED')}">
                                        <button type="button" 
                                                class="btn btn-sm btn-warning"
                                                data-bs-toggle="tooltip"
                                                title="Edit Job Posting"
                                                onclick="window.location.href='${pageContext.request.contextPath}/job-posting/edit?id=${job.id}'">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                    </c:if>

                                    <!-- Publish button for HR Manager (position_id = 7) when status is APPROVED -->
                                    <c:if test="${sessionScope.user != null && sessionScope.user.positionId == 7 && job.status == 'APPROVED'}">
                                        <button type="button" class="btn btn-sm btn-primary"
                                                onclick="publishJobPosting('${job.id}')"
                                                data-bs-toggle="tooltip" 
                                                title="Publish to Public">
                                            <i class="fas fa-globe"></i> Publish
                                        </button>
                                    </c:if>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    
                    <c:if test="${empty jobPostings}">
                        <tr>
                            <td colspan="8" class="text-center py-5">
                                <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                <h5 class="text-muted">No Job Postings Found</h5>
                                <p class="text-muted">Try adjusting your filters or create a new job posting.</p>
                                <c:if test="${sessionScope.user != null && sessionScope.user.positionId == 8}">
                                    <a href="${pageContext.request.contextPath}/recruitment/approved" class="btn btn-primary mt-2">
                                        <i class="fas fa-plus-circle me-1"></i> Create New Posting
                                    </a>
                                </c:if>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <!-- Pagination -->
        <c:if test="${totalPages > 1}">
            <nav aria-label="Job postings pagination" class="mt-4">
                <ul class="pagination justify-content-center">
                    <!-- Previous button -->
                    <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                        <a class="page-link" href="?page=${currentPage - 1}&status=${param.status}&departmentId=${param.departmentId}&jobType=${param.jobType}&jobLevel=${param.jobLevel}">
                            «
                        </a>
                    </li>
                    
                    <!-- Smart page numbers with ellipsis -->
                    <c:set var="startPage" value="${currentPage - 2}" />
                    <c:set var="endPage" value="${currentPage + 2}" />
                    
                    <c:if test="${startPage < 1}">
                        <c:set var="startPage" value="1" />
                    </c:if>
                    
                    <c:if test="${endPage > totalPages}">
                        <c:set var="endPage" value="${totalPages}" />
                    </c:if>
                    
                    <!-- First page -->
                    <c:if test="${startPage > 1}">
                        <li class="page-item">
                            <a class="page-link" href="?page=1&status=${param.status}&departmentId=${param.departmentId}&jobType=${param.jobType}&jobLevel=${param.jobLevel}">
                                1
                            </a>
                        </li>
                        <c:if test="${startPage > 2}">
                            <li class="page-item disabled">
                                <span class="page-link">...</span>
                            </li>
                        </c:if>
                    </c:if>
                    
                    <!-- Page numbers around current page -->
                    <c:forEach begin="${startPage}" end="${endPage}" var="page">
                        <li class="page-item ${currentPage == page ? 'active' : ''}">
                            <a class="page-link" href="?page=${page}&status=${param.status}&departmentId=${param.departmentId}&jobType=${param.jobType}&jobLevel=${param.jobLevel}">
                                ${page}
                            </a>
                        </li>
                    </c:forEach>
                    
                    <!-- Last page -->
                    <c:if test="${endPage < totalPages}">
                        <c:if test="${endPage < totalPages - 1}">
                            <li class="page-item disabled">
                                <span class="page-link">...</span>
                            </li>
                        </c:if>
                        <li class="page-item">
                            <a class="page-link" href="?page=${totalPages}&status=${param.status}&departmentId=${param.departmentId}&jobType=${param.jobType}&jobLevel=${param.jobLevel}">
                                ${totalPages}
                            </a>
                        </li>
                    </c:if>
                    
                    <!-- Next button -->
                    <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                        <a class="page-link" href="?page=${currentPage + 1}&status=${param.status}&departmentId=${param.departmentId}&jobType=${param.jobType}&jobLevel=${param.jobLevel}">
                            »
                        </a>
                    </li>
                </ul>
            </nav>
        </c:if>
    </div>

   

    <!-- Confirmation Modals -->
    <!-- Approve Modal -->
    <div class="modal fade" id="approveModal" tabindex="-1" aria-labelledby="approveModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header bg-success text-white">
                    <h5 class="modal-title" id="approveModalLabel">
                        <i class="fas fa-check-circle me-2"></i>Approve Job Posting
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/job-posting/approve">
                    <div class="modal-body">
                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                        <input type="hidden" name="id" id="approveJobId"/>
                        <div class="text-center py-3">
                            <i class="fas fa-check-circle text-success fa-4x mb-3"></i>
                            <p class="mb-0">Are you sure you want to approve this job posting?</p>
                            <small class="text-muted d-block mt-2">
                                After approval, HR Manager can publish it to make it visible to the public.
                            </small>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times me-1"></i>Cancel
                        </button>
                        <button type="submit" class="btn btn-success">
                            <i class="fas fa-check me-1"></i>Approve
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Reject Modal -->
    <div class="modal fade" id="rejectModal" tabindex="-1" aria-labelledby="rejectModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header bg-danger text-white">
                    <h5 class="modal-title" id="rejectModalLabel">
                        <i class="fas fa-times-circle me-2"></i>Reject Job Posting
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/job-posting/reject">
                    <div class="modal-body">
                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                        <input type="hidden" name="id" id="rejectJobId"/>
                        <div class="mb-3">
                            <label for="rejectReason" class="form-label fw-bold">
                                <i class="fas fa-comment-alt me-1"></i>Reason for Rejection <span class="text-danger">*</span>
                            </label>
                            <textarea class="form-control" id="rejectReason" name="reason" rows="4" 
                                      required placeholder="Please provide a clear reason for rejection..."
                                      minlength="10" maxlength="500"></textarea>
                            <div class="form-text">Minimum 10 characters, maximum 500 characters</div>
                        </div>
                        <div class="alert alert-warning mb-0">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            <small>The HR staff will be notified about this rejection and the reason provided.</small>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times me-1"></i>Cancel
                        </button>
                        <button type="submit" class="btn btn-danger">
                            <i class="fas fa-ban me-1"></i>Reject
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Publish Modal -->
    <div class="modal fade" id="publishModal" tabindex="-1" aria-labelledby="publishModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title" id="publishModalLabel">
                        <i class="fas fa-globe me-2"></i>Publish Job Posting
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/job-posting/publish">
                    <div class="modal-body">
                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                        <input type="hidden" name="id" id="publishJobId"/>
                        <div class="text-center py-3">
                            <i class="fas fa-globe text-primary fa-4x mb-3"></i>
                            <p class="mb-0">Are you sure you want to publish this job posting?</p>
                            <small class="text-muted d-block mt-2">
                                This will make the job posting visible to the public on the careers page.
                            </small>
                        </div>
                        <div class="alert alert-info mb-0 mt-3">
                            <i class="fas fa-info-circle me-2"></i>
                            <small>Once published, candidates can start applying for this position.</small>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times me-1"></i>Cancel
                        </button>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-globe me-1"></i>Publish Now
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Dashboard Footer -->
    <jsp:include page="../layout/dashboard-footer.jsp"/>
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

    // Add hover effect to action buttons
    document.addEventListener('DOMContentLoaded', function() {
        const buttons = document.querySelectorAll('.btn-sm');
        buttons.forEach(btn => {
            btn.addEventListener('mouseover', function() {
                if (this.title) {
                    this.setAttribute('data-bs-toggle', 'tooltip');
                    new bootstrap.Tooltip(this).show();
                }
            });
        });

        // Initialize all tooltips
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl)
        });
    });
</script>
</body>
</html>