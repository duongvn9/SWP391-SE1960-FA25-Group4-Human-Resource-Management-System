<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Job Postings - HRMS</title>
    <jsp:include page="/WEB-INF/views/layout/links.jsp"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/job-posting.css"/>
</head>
<body>
    <jsp:include page="/WEB-INF/views/layout/navbar.jsp"/>
    
    <div class="container my-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="h4 mb-0">Job Postings</h2>
            
            <!-- Only HR can create new job postings -->
            <c:if test="${sessionScope.userRole == 'HR'}">
                <a href="${pageContext.request.contextPath}/job-posting/create" 
                   class="btn btn-primary">
                    <i class="bi bi-plus-circle"></i> Create New
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
        <div class="card mb-4">
            <div class="card-body">
                <form method="get" class="row g-3">
                    <div class="col-md-2">
                        <label class="form-label">Status</label>
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
                    <div class="col-md-2 d-flex align-items-end">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-filter"></i> Filter
                        </button>
                        <a href="${pageContext.request.contextPath}/job-postings" 
                           class="btn btn-outline-secondary ms-2">Clear</a>
                    </div>
                </form>
            </div>
        </div>

        <!-- Job Postings Table -->
        <div class="table-responsive">
            <table class="table table-striped table-hover table-bordered">
                <thead class="table-light">
                    <tr>
                        <th>Title</th>
                        <th>Department</th>
                        <th>Type</th>
                        <th>Level</th>
                        <th>Positions</th>
                        <th>Priority</th>
                        <th>Status</th>
                        <th>Created</th>
                        <th>Deadline</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${jobPostings}" var="job">
                        <tr>
                            <td>
                                <a href="${pageContext.request.contextPath}/job-posting/view?id=${job.id}"
                                   class="text-decoration-none">
                                    ${job.title}
                                </a>
                                <br>
                                <small class="text-muted">${job.code}</small>
                            </td>
                            <td>${job.departmentName}</td>
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
                                <!-- View button for all -->
                                <a href="${pageContext.request.contextPath}/job-posting/view?id=${job.id}"
                                   class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-eye"></i>
                                </a>

                                <!-- Edit button for HR (only if PENDING) -->
                                <c:if test="${sessionScope.userRole == 'HR' && job.status == 'PENDING'}">
                                    <a href="${pageContext.request.contextPath}/job-posting/edit?id=${job.id}"
                                       class="btn btn-sm btn-outline-secondary">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                </c:if>

                                <!-- Approve/Reject buttons for HRM (only if PENDING) -->
                                <c:if test="${sessionScope.userRole == 'HRM' && job.status == 'PENDING'}">
                                    <button type="button" class="btn btn-sm btn-outline-success"
                                            onclick="approveJobPosting('${job.id}')">
                                        <i class="bi bi-check-circle"></i>
                                    </button>
                                    <button type="button" class="btn btn-sm btn-outline-danger"
                                            onclick="rejectJobPosting('${job.id}')">
                                        <i class="bi bi-x-circle"></i>
                                    </button>
                                </c:if>

                                <!-- Publish button for HRM (only if APPROVED) -->
                                <c:if test="${sessionScope.userRole == 'HRM' && job.status == 'APPROVED'}">
                                    <button type="button" class="btn btn-sm btn-outline-primary"
                                            onclick="publishJobPosting('${job.id}')"
                                        <i class="bi bi-globe"></i>
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

    <jsp:include page="/WEB-INF/views/layout/footer.jsp"/>

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