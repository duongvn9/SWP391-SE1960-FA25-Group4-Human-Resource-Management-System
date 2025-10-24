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
    <!-- Quick visible debug banner - remove after troubleshooting -->
    <div style="background:#fff3cd;color:#856404;padding:10px;border:1px solid #ffeeba;margin:10px;">
        <strong>DEBUG:</strong>
        Result present: <c:out value="${not empty result}"/>
        &nbsp;|&nbsp; Total requests: <c:out value="${result.totalRequestCount}"/>
        &nbsp;|&nbsp; Departments: <c:out value="${result.departmentCount}"/>
        &nbsp;|&nbsp; Grouped: <c:out value="${result.groupedByDepartment}"/>
        &nbsp;|&nbsp; Scope: <c:out value="${filter.scope}"/>
    </div>
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
                    <i class="fas fa-check-circle text-success me-2"></i>Create Job Posting
                </h2>
                <div>
                    <a href="${pageContext.request.contextPath}/requests" class="btn btn-outline-secondary">
                        <i class="fas fa-arrow-left me-2"></i>Back to Requests
                    </a>
                    <a href="${pageContext.request.contextPath}/job-postings" class="btn btn-primary ms-2">
                        <i class="fas fa-briefcase me-2"></i>Job Postings
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
                                    <div class="alert alert-info">
                                        <i class="fas fa-info-circle me-2"></i>
                                        No approved recruitment requests available for creating job postings.
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
                                <div class="mb-2 mt-3">
                                    <h5 class="mb-1">Department: <strong><c:out value="${entry.key}"/></strong>
                                        <small class="text-muted">(${fn:length(entry.value)} requests)</small>
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
                                <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                <p class="text-muted mb-0">No approved recruitment requests found.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/layout/dashboard-footer.jsp"/>

    <script>
        // Initialize tooltips
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    </script>
</body>
</html>
