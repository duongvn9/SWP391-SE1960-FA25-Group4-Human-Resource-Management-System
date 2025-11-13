<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp">
        <jsp:param name="pageTitle" value="Approved Recruitment Requests"/>
    </jsp:include>
    <link href="${pageContext.request.contextPath}/assets/css/dashboard.css" rel="stylesheet"/>
</head>
<body>
<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp">
        <jsp:param name="currentPage" value="approved-recruitment-requests"/>
    </jsp:include>

    <div class="main-content">
        <jsp:include page="/WEB-INF/views/layout/dashboard-header.jsp">
            <jsp:param name="pageTitle" value="Approved Recruitment Requests"/>
        </jsp:include>

        <div class="content-area">
            <!-- Page heading and actions -->
            <div class="page-head d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="page-title">
                        <i class="fas fa-check-circle me-2"></i>Approved Recruitment Requests
                    </h2>
                    <p class="page-subtitle">Manage approved recruitment requests and create job postings</p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/requests" class="btn btn-outline-secondary">
                        <i class="fas fa-arrow-left me-2"></i>Back to Requests
                    </a>
                    <a href="${pageContext.request.contextPath}/job-postings" class="btn btn-primary">
                        <i class="fas fa-briefcase me-2"></i>View Job Postings
                    </a>
                </div>
            </div>

            <!-- Show success/error messages -->
            <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle me-2"></i>
                    <c:out value="${success}"/>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i>${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <!-- Filter Section -->
            <div class="card mb-3">
                <div class="card-body">
                    <form method="get" action="${pageContext.request.contextPath}/recruitment/approved" class="row g-3">
                        <div class="col-md-4">
                            <label for="departmentFilter" class="form-label">
                                <i class="fas fa-building me-1"></i>Filter by Department
                            </label>
                            <select class="form-select" id="departmentFilter" name="departmentId" onchange="this.form.submit()">
                                <option value="">All Departments</option>
                                <c:forEach var="dept" items="${departments}">
                                    <option value="${dept.id}" ${selectedDepartmentId == dept.id ? 'selected' : ''}>
                                        <c:out value="${dept.name}"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-8 d-flex align-items-end">
                            <button type="submit" class="btn btn-primary me-2">
                                <i class="fas fa-filter me-1"></i>Apply Filter
                            </button>
                            <a href="${pageContext.request.contextPath}/recruitment/approved" class="btn btn-outline-secondary">
                                <i class="fas fa-redo me-1"></i>Reset
                            </a>
                            <span class="ms-3 text-muted">
                                <i class="fas fa-info-circle me-1"></i>
                                Showing ${totalItems} request(s)
                            </span>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Main content card -->
            <div class="card recruitment-card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h4 class="mb-0"><i class="fas fa-list me-2"></i>Approved Requests</h4>
                    
                </div>
                <div class="card-body">
                    <c:choose>
                        <%-- Flat list when result.requests is present (my / subordinate scopes) --%>
                        <c:when test="${not empty result and not empty result.requests}">
                            <c:choose>
                                <c:when test="${empty result.requests}">
                                    <div class="empty-state">
                                        <div class="empty-icon">
                                            <i class="fas fa-check-circle"></i>
                                        </div>
                                        <h5 class="empty-title">All Set!</h5>
                                        <p class="empty-text">All approved recruitment requests have been converted to job postings.</p>
                                        <p class="empty-subtext">Or there are no approved recruitment requests yet.</p>
                                        <a href="${pageContext.request.contextPath}/job-postings" class="btn btn-primary mt-3">
                                            <i class="fas fa-briefcase me-2"></i>View Job Postings
                                        </a>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="table-responsive">
                                        <table class="table modern-table">
                                            <thead>
                                                <tr>
                                                    <th class="text-center">#</th>
                                                    <th>Request Title</th>
                                                    <th>Position</th>
                                                    <th class="text-center">Vacancies</th>
                                                    <th>Requested By</th>
                                                    <th>Department</th>
                                                    <th>Created</th>
                                                    <th class="text-center">Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="req" items="${result.requests}">
                                                    <tr>
                                                        <td class="text-center">${req.id}</td>
                                                        <td>
                                                            <strong><c:out value="${req.title}"/></strong>
                                                        </td>
                                                        <td>
                                                            <span class="position-badge">
                                                                <c:out value="${req.recruitmentDetail.positionName}"/>
                                                            </span>
                                                        </td>
                                                        <td class="text-center">
                                                            <span class="quantity-badge">
                                                                ${req.recruitmentDetail.quantity}
                                                            </span>
                                                        </td>
                                                        <td><c:out value="${req.userFullName}"/></td>
                                                        <td><c:out value="${req.departmentName}"/></td>
                                                        <td>
                                                            <fmt:parseDate value="${req.createdAt}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate" />
                                                            <fmt:formatDate value="${parsedDate}" pattern="MMM dd, yyyy" />
                                                        </td>
                                                        <td class="text-center">
                                                            <a href="${pageContext.request.contextPath}/requests/detail?id=${req.id}" 
                                                               class="btn btn-sm btn-outline-info me-2" 
                                                               data-bs-toggle="tooltip" 
                                                               title="View Details">
                                                                <i class="fas fa-eye"></i>
                                                            </a>
                                                            <c:if test="${req.requestTypeCode == 'RECRUITMENT_REQUEST' && req.status == 'APPROVED' && sessionScope.user != null && sessionScope.user.positionId == 8}">
                                                                <a href="${pageContext.request.contextPath}/job-posting/create?requestId=${req.id}" 
                                                                   class="btn btn-sm btn-success"
                                                                   data-bs-toggle="tooltip"
                                                                   title="Create Job Posting">
                                                                    <i class="fas fa-plus-circle me-1"></i>Create Job Posting
                                                                </a>
                                                            </c:if>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </c:when>

        <%-- Grouped result when result.requestsByDepartment is present (all scope) --%>
        <c:when test="${not empty result and not empty result.requestsByDepartment}">
            <c:forEach var="entry" items="${result.requestsByDepartment}">
                <div class="department-section">
                    <div class="department-header">
                        <i class="fas fa-building me-2"></i>
                        <strong><c:out value="${entry.key}"/></strong>
                        <span class="request-count">${fn:length(entry.value)} request<c:if test="${fn:length(entry.value) > 1}">s</c:if></span>
                    </div>
                                    <div class="table-responsive">
                                        <table class="table modern-table">
                                            <thead>
                                                <tr>
                                                    <th class="text-center">#</th>
                                                    <th>Request Title</th>
                                                    <th>Position</th>
                                                    <th class="text-center">Vacancies</th>
                                                    <th>Requested By</th>
                                                    <th>Created</th>
                                                    <th class="text-center">Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="req" items="${entry.value}">
                                                    <tr>
                                                        <td class="text-center">${req.id}</td>
                                                        <td><strong><c:out value="${req.title}"/></strong></td>
                                                        <td>
                                                            <span class="position-badge">
                                                                <c:out value="${req.recruitmentDetail.positionName}"/>
                                                            </span>
                                                        </td>
                                                        <td class="text-center">
                                                            <span class="quantity-badge">${req.recruitmentDetail.quantity}</span>
                                                        </td>
                                                        <td><c:out value="${req.userFullName}"/></td>
                                                        <td>
                                                            <fmt:parseDate value="${req.createdAt}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate2" />
                                                            <fmt:formatDate value="${parsedDate2}" pattern="MMM dd, yyyy" />
                                                        </td>
                                                        <td class="text-center">
                                                            <a href="${pageContext.request.contextPath}/requests/detail?id=${req.id}" 
                                                               class="btn btn-sm btn-outline-info me-2" 
                                                               data-bs-toggle="tooltip" 
                                                               title="View Details">
                                                                <i class="fas fa-eye"></i>
                                                            </a>
                                                            <c:if test="${req.requestTypeCode == 'RECRUITMENT_REQUEST' && req.status == 'APPROVED' && sessionScope.user != null && sessionScope.user.positionId == 8}">
                                                                <a href="${pageContext.request.contextPath}/job-posting/create?requestId=${req.id}" 
                                                                   class="btn btn-sm btn-success"
                                                                   data-bs-toggle="tooltip"
                                                                   title="Create Job Posting">
                                                                    <i class="fas fa-plus-circle me-1"></i>Create Job Posting
                                                                </a>
                                                            </c:if>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>

        <c:otherwise>
            <div class="empty-state">
                <div class="empty-icon">
                    <i class="fas fa-check-circle"></i>
                </div>
                <h5 class="empty-title">All Set!</h5>
                <p class="empty-text">All approved recruitment requests have been converted to job postings.</p>
                <p class="empty-subtext">Or there are no approved recruitment requests yet.</p>
                <a href="${pageContext.request.contextPath}/job-postings" class="btn btn-primary mt-3">
                    <i class="fas fa-briefcase me-2"></i>View Job Postings
                </a>
            </div>
        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/layout/dashboard-footer.jsp"/>
    </div>
</div>

    <script>
        // Initialize tooltips
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    </script>

<style>
/* Enhanced styles for Recruitment Approved List */
.content-area {
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    min-height: 100vh;
    padding: 2rem;
}

.page-head {
    background: rgba(255, 255, 255, 0.95);
    padding: 1.5rem;
    border-radius: 15px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    margin-bottom: 2rem;
}

.page-title {
    color: #2c3e50;
    font-weight: 600;
    margin-bottom: 0.5rem;
}

.page-subtitle {
    color: #6c757d;
    margin-bottom: 0;
}

.recruitment-card {
    background: rgba(255, 255, 255, 0.95);
    border: none;
    border-radius: 20px;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
    overflow: hidden;
}

.recruitment-card .card-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    padding: 1.5rem;
}

.recruitment-card .card-header h4 {
    color: white;
    margin-bottom: 0;
    font-weight: 600;
}

/* Empty State */
.empty-state {
    text-align: center;
    padding: 4rem 2rem;
}

.empty-icon {
    font-size: 4rem;
    color: #28a745;
    margin-bottom: 1.5rem;
    animation: pulse 2s infinite;
}

.empty-title {
    color: #2c3e50;
    font-weight: 600;
    margin-bottom: 1rem;
}

.empty-text {
    color: #6c757d;
    font-size: 1.1rem;
    margin-bottom: 0.5rem;
}

.empty-subtext {
    color: #adb5bd;
    margin-bottom: 2rem;
}

/* Modern Table */
.modern-table {
    background: white;
    border-radius: 15px;
    overflow: hidden;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.modern-table thead {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.modern-table thead th {
    color: white;
    font-weight: 600;
    border: none;
    padding: 1rem;
    text-transform: uppercase;
    font-size: 0.85rem;
    letter-spacing: 0.5px;
}

.modern-table tbody tr {
    transition: all 0.3s ease;
    border: none;
}

.modern-table tbody tr:hover {
    background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 50%);
    transform: translateY(-1px);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.modern-table tbody td {
    padding: 1rem;
    border: none;
    vertical-align: middle;
}

/* Badges */
.position-badge {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 0.4rem 0.8rem;
    border-radius: 20px;
    font-size: 0.85rem;
    font-weight: 500;
    display: inline-block;
}

.quantity-badge {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: white;
    padding: 0.3rem 0.6rem;
    border-radius: 15px;
    font-size: 0.9rem;
    font-weight: 600;
    display: inline-block;
    min-width: 30px;
    text-align: center;
}

/* Action Buttons */
.action-buttons {
    display: flex;
    gap: 0.5rem;
    justify-content: center;
    align-items: center;
}

.action-buttons .btn {
    border-radius: 8px;
    font-weight: 500;
    transition: all 0.3s ease;
}

.action-buttons .btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

/* Department Section */
.department-section {
    background: rgba(255, 255, 255, 0.8);
    border-radius: 15px;
    margin-bottom: 2rem;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
    backdrop-filter: blur(10px);
    overflow: hidden;
}

.department-header {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: white;
    padding: 1rem 1.5rem;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.request-count {
    background: rgba(255, 255, 255, 0.2);
    padding: 0.2rem 0.6rem;
    border-radius: 12px;
    font-size: 0.85rem;
    margin-left: auto;
}

/* Buttons */
.btn {
    border-radius: 10px;
    font-weight: 600;
    transition: all 0.3s ease;
    border: none;
}

.btn-primary {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-primary:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
}

.btn-outline-secondary {
    border: 2px solid #6c757d;
    color: #6c757d;
    background: transparent;
}

.btn-outline-secondary:hover {
    background: #6c757d;
    color: white;
    transform: translateY(-2px);
}

.btn-success {
    background: linear-gradient(135deg, #28a745 0%, #20c997 100%) !important;
    color: white !important;
    border: none !important;
    box-shadow: 0 4px 15px rgba(40, 167, 69, 0.4) !important;
    opacity: 1 !important;
}

.btn-success:hover {
    background: linear-gradient(135deg, #218838 0%, #1aa179 100%) !important;
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(40, 167, 69, 0.6) !important;
    opacity: 1 !important;
}

.btn-outline-info {
    border: 2px solid #17a2b8;
    color: #17a2b8;
    background: transparent;
}

.btn-outline-info:hover {
    background: #17a2b8;
    color: white;
    transform: translateY(-2px);
}

/* Alerts */
.alert {
    border: none;
    border-radius: 15px;
    padding: 1rem 1.5rem;
    margin-bottom: 1.5rem;
    backdrop-filter: blur(10px);
}

.alert-success {
    background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
    color: #155724;
}

.alert-danger {
    background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
    color: #721c24;
}

/* Pagination */
.pagination {
    margin-top: 2rem;
}

.page-link {
    border: none;
    border-radius: 8px;
    margin: 0 2px;
    color: #667eea;
    font-weight: 500;
    transition: all 0.3s ease;
}

.page-link:hover {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.page-item.active .page-link {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

/* Animations */
@keyframes pulse {
    0% {
        transform: scale(1);
    }
    50% {
        transform: scale(1.05);
    }
    100% {
        transform: scale(1);
    }
}

/* Responsive */
@media (max-width: 768px) {
    .content-area {
        padding: 1rem;
    }
    
    .page-head {
        padding: 1rem;
        margin-bottom: 1rem;
        flex-direction: column;
        gap: 1rem;
    }
    
    .action-buttons {
        flex-direction: column;
        gap: 0.25rem;
    }
    
    .action-buttons .btn {
        width: 100%;
        font-size: 0.85rem;
    }
    
    .department-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.5rem;
    }
    
    .request-count {
        margin-left: 0;
        align-self: flex-end;
    }
}

/* Smooth transitions - exclude buttons from global transition */
*:not(.btn) {
    transition: all 0.3s ease;
}

.btn {
    transition: transform 0.3s ease, box-shadow 0.3s ease !important;
}

/* Custom scrollbar */
::-webkit-scrollbar {
    width: 8px;
}

::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 10px;
}

::-webkit-scrollbar-thumb {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
    background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}
</style>
</body>
</html>
