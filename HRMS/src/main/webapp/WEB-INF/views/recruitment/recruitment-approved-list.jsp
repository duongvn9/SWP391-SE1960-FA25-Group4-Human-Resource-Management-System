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

        <div class="container-fluid px-4 py-4">
            <!-- Page heading and actions -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="h4 mb-0">
                    <i class="fas fa-check-circle text-success me-2"></i>Approved Recruitment Requests
                </h2>
                <div>
                    <a href="${pageContext.request.contextPath}/requests" class="btn btn-outline-secondary">
                        <i class="fas fa-arrow-left me-2"></i>Back to Requests
                    </a>
                    <a href="${pageContext.request.contextPath}/job-postings" class="btn btn-primary ms-2">
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
            
           

            <!-- Main content card -->
            <div class="card shadow-sm">
                <div class="card-body">
                    <c:choose>
                        <%-- Flat list when result.requests is present (my / subordinate scopes) --%>
                        <c:when test="${not empty result and not empty result.requests}">
                            <c:choose>
                                <c:when test="${empty result.requests}">
                                    <div class="text-center py-5">
                                        <i class="fas fa-check-circle fa-3x text-success mb-3"></i>
                                        <p class="text-muted mb-0">All approved recruitment requests have been converted to job postings.</p>
                                        <p class="text-muted">Or there are no approved recruitment requests yet.</p>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="table-responsive">
                                        <table class="table table-hover align-middle">
                                            <thead class="table-light">
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
                                                            <span class="badge bg-info text-dark">
                                                                <c:out value="${req.recruitmentDetail.positionName}"/>
                                                            </span>
                                                        </td>
                                                        <td class="text-center">
                                                            <span class="badge bg-secondary">
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
                                                            <c:if test="${req.requestTypeCode == 'RECRUITMENT_REQUEST' && req.status == 'APPROVED'}">
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
                <div class="mb-4 mt-3">
                    <h5 class="mb-3 pb-2 border-bottom">
                        <i class="fas fa-building me-2 text-primary"></i>
                        <strong><c:out value="${entry.key}"/></strong>
                        <small class="text-muted ms-2">(${fn:length(entry.value)} request<c:if test="${fn:length(entry.value) > 1}">s</c:if>)</small>
                    </h5>
                                    <div class="table-responsive">
                                        <table class="table table-hover align-middle">
                                            <thead class="table-light">
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
                                                            <span class="badge bg-info text-dark">
                                                                <c:out value="${req.recruitmentDetail.positionName}"/>
                                                            </span>
                                                        </td>
                                                        <td class="text-center">
                                                            <span class="badge bg-secondary">${req.recruitmentDetail.quantity}</span>
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
                                                            <c:if test="${req.requestTypeCode == 'RECRUITMENT_REQUEST' && req.status == 'APPROVED'}">
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
            <div class="text-center py-5">
                <i class="fas fa-check-circle fa-3x text-success mb-3"></i>
                <p class="text-muted mb-2">All approved recruitment requests have been converted to job postings.</p>
                <p class="text-muted">Or there are no approved recruitment requests yet.</p>
                <a href="${pageContext.request.contextPath}/job-postings" class="btn btn-primary mt-3">
                    <i class="fas fa-briefcase me-2"></i>View Job Postings
                </a>
            </div>
        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            
            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <nav aria-label="Approved requests pagination" class="mt-4">
                    <ul class="pagination justify-content-center">
                        <!-- Previous button -->
                        <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${currentPage - 1}">
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
                                <a class="page-link" href="?page=1">
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
                                <a class="page-link" href="?page=${page}">
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
                                <a class="page-link" href="?page=${totalPages}">
                                    ${totalPages}
                                </a>
                            </li>
                        </c:if>
                        
                        <!-- Next button -->
                        <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${currentPage + 1}">
                                »
                            </a>
                        </li>
                    </ul>
                </nav>
            </c:if>
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
</body>
</html>
