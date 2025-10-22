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
    <div class="alert alert-info">DEBUG: loggedInUser.positionId = ${sessionScope.loggedInUser.positionId}</div>
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
                
                <!-- Only HR can create new job postings -->
                <c:if test="${sessionScope.userRole == 'HR'}">
                    <a href="${pageContext.request.contextPath}/job-posting/create" 
                       class="btn btn-primary">
                        <i class="fas fa-plus-circle me-1"></i> Create New Posting
                    </a>
                </c:if>
            </div>

        <!-- Success/Error Messages -->
        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${param.success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${param.error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
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
                    <div class="col-md-2">
                        <label class="form-label">Level</label>
                        <select name="jobLevel" class="form-select">
                            <option value="">All</option>
                            <option value="JUNIOR" ${param.jobLevel == 'JUNIOR' ? 'selected' : ''}>Junior</option>
                            <option value="MIDDLE" ${param.jobLevel == 'MIDDLE' ? 'selected' : ''}>Middle</option>
                            <option value="SENIOR" ${param.jobLevel == 'SENIOR' ? 'selected' : ''}>Senior</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Priority</label>
                        <select name="priority" class="form-select">
                            <option value="">All</option>
                            <option value="NORMAL" ${param.priority == 'NORMAL' ? 'selected' : ''}>Normal</option>
                            <option value="HIGH" ${param.priority == 'HIGH' ? 'selected' : ''}>High</option>
                            <option value="URGENT" ${param.priority == 'URGENT' ? 'selected' : ''}>Urgent</option>
                        </select>
                    </div>
                    <div class="col-md-2">
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
            <table class="table table-striped table-hover">
                <thead>
                    <tr>
                        <th><i class="fas fa-file-alt me-1"></i> Title</th>
                        <th><i class="fas fa-building me-1"></i> Department</th>
                        <th><i class="fas fa-briefcase me-1"></i> Type</th>
                        <th><i class="fas fa-layer-group me-1"></i> Level</th>
                        <th><i class="fas fa-users me-1"></i> Positions</th>
                        <th><i class="fas fa-flag me-1"></i> Priority</th>
                        <th><i class="fas fa-info-circle me-1"></i> Status</th>
                        <th><i class="fas fa-clock me-1"></i> Deadline</th>
                        <th><i class="fas fa-cogs me-1"></i> Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${jobPostings}" var="job">
                        <tr>
                            <td>
                                <a href="${pageContext.request.contextPath}/job-postings/view?id=${job.id}"
                                   class="text-decoration-none">
                                    ${job.title}
                                </a>
                                <br>
                                <small class="text-muted">${job.code}</small>
                            </td>
                            <td>
                                <c:forEach items="${departments}" var="dept">
                                    <c:if test="${dept.id == job.departmentId}">
                                        ${dept.name}
                                    </c:if>
                                </c:forEach>
                            </td>
                            <td>${job.jobType}</td>
                            <td>${job.level}</td>
                            <td class="text-center">${job.numberOfPositions}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${job.priority == 'URGENT'}">
                                        <span class="badge bg-danger">Urgent</span>
                                    </c:when>
                                    <c:when test="${job.priority == 'HIGH'}">
                                        <span class="badge bg-warning">High</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">Normal</span>
                                    </c:otherwise>
                                </c:choose>
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
                                <fmt:parseDate value="${job.applicationDeadline}" pattern="yyyy-MM-dd" var="deadline"/>
                                <fmt:formatDate value="${deadline}" pattern="MMM d, yyyy"/>
                            </td>
                            <td class="text-nowrap">
                                <!-- View Details button -->
                                <button type="button" 
                                        class="btn btn-sm btn-info" 
                                        data-bs-toggle="tooltip" 
                                        title="View Details"
                                        onclick="window.location.href='${pageContext.request.contextPath}/job-postings/view?id=${job.id}'">
                                    <i class="fas fa-eye"></i>
                                </button>
                                
                                <!-- Approve/Reject buttons for HR Manager (position_id = 7) when status is PENDING -->
                                <c:if test="${sessionScope.loggedInUser != null && sessionScope.loggedInUser.positionId == 7 && job.status == 'PENDING'}">
                                    <button type="button" class="btn btn-sm btn-success"
                                            onclick="approveJobPosting('${job.id}')" 
                                            data-bs-toggle="tooltip"
                                            title="Approve">
                                        <i class="fas fa-check-circle"></i>
                                    </button>
                                    <button type="button" class="btn btn-sm btn-danger"
                                            onclick="rejectJobPosting('${job.id}')"
                                            data-bs-toggle="tooltip" 
                                            title="Reject">
                                        <i class="fas fa-times-circle"></i>
                                    </button>
                                </c:if>
                                
                                <!-- Edit button for HR Staff (position_id = 8) (only if PENDING) -->
                                <c:if test="${sessionScope.loggedInUser.positionId == 8 && job.status == 'PENDING'}">
                                    <button type="button" 
                                            class="btn btn-sm btn-secondary"
                                            data-bs-toggle="tooltip"
                                            title="Edit"
                                            onclick="window.location.href='${pageContext.request.contextPath}/job-posting/edit?id=${job.id}'">
                                        <i class="fas fa-pencil"></i>
                                    </button>
                                </c:if>

                                <!-- Publish button for HR Manager (position_id = 7) when status is APPROVED -->
                                <c:if test="${sessionScope.loggedInUser.positionId == 7 && job.status == 'APPROVED'}">
                                    <button type="button" class="btn btn-sm btn-outline-primary"
                                            onclick="publishJobPosting('${job.id}')"
                                            data-bs-toggle="tooltip" 
                                            title="Publish">
                                        <i class="fas fa-globe"></i>
                                    </button>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    
                    <c:if test="${empty jobPostings}">
                        <tr>
                            <td colspan="8" class="text-center py-4">
                                No job postings found.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <!-- Pagination if needed -->
        <c:if test="${totalPages > 1}">
            <nav aria-label="Job postings pagination" class="mt-4">
                <ul class="pagination justify-content-center">
                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                        <a class="page-link" href="?page=${currentPage - 1}&status=${param.status}&departmentId=${param.departmentId}">
                            Previous
                        </a>
                    </li>
                    
                    <c:forEach begin="1" end="${totalPages}" var="page">
                        <li class="page-item ${currentPage == page ? 'active' : ''}">
                            <a class="page-link" href="?page=${page}&status=${param.status}&departmentId=${param.departmentId}">
                                ${page}
                            </a>
                        </li>
                    </c:forEach>
                    
                    <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                        <a class="page-link" href="?page=${currentPage + 1}&status=${param.status}&departmentId=${param.departmentId}">
                            Next
                        </a>
                    </li>
                </ul>
            </nav>
        </c:if>
    </div>

   

    <!-- Confirmation Modals -->
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

    <!-- Approve Modal -->
    <div class="modal fade" id="approveModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Approve Job Posting</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    Are you sure you want to approve this job posting?
                    <br><br>
                    <small class="text-muted">
                        After approval, the job posting can be published.
                    </small>
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