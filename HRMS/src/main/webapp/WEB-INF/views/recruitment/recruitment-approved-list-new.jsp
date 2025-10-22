<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp">
        <jsp:param name="pageTitle" value="Approved Recruitment Requests"/>
    </jsp:include>
    <link href="${pageContext.request.contextPath}/assets/css/dashboard.css" rel="stylesheet"/>
</head>
<body>
    <!-- Include sidebar -->
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp">
        <jsp:param name="currentPage" value="approved-recruitment-requests"/>
    </jsp:include>

    <div class="main-content">
        <!-- Include header -->
        <jsp:include page="/WEB-INF/views/layout/dashboard-header.jsp"/>

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
                        <i class="fas fa-briefcase me-2"></i>Job Postings
                    </a>
                </div>
            </div>

            <!-- Show any error messages -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i>${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <!-- Debug info -->
            <div class="card mb-3">
                <div class="card-body">
                    <h6 class="card-title">Debug Info</h6>
                    <p class="mb-0">
                        <strong>Result:</strong> ${not empty result}<br>
                        <strong>Has requests:</strong> ${not empty result.requests}<br>
                        <strong>Has departments:</strong> ${not empty result.requestsByDepartment}<br>
                        <strong>Filter scope:</strong> ${filter.scope}<br>
                        <strong>Filter status:</strong> ${filter.status}<br>
                        <strong>Filter type:</strong> ${filter.requestTypeId}
                    </p>
                </div>
            </div>

            <!-- Main content card -->
            <div class="card shadow-sm">
                <div class="card-body">
                    <c:if test="${empty result or (empty result.requests and empty result.requestsByDepartment)}">
                        <div class="text-center py-5">
                            <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                            <p class="text-muted mb-0">No approved recruitment requests found.</p>
                        </div>
                    </c:if>

                    <c:if test="${not empty result.requestsByDepartment}">
                        <c:forEach items="${result.requestsByDepartment}" var="dept">
                            <div class="mb-4">
                                <!-- Department Header -->
                                <div class="d-flex align-items-center mb-3">
                                    <h5 class="mb-0">
                                        <i class="fas fa-building text-primary me-2"></i>
                                        ${dept.key}
                                    </h5>
                                </div>
                                
                                <!-- Department Requests Table -->
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th>#</th>
                                                <th>Request Title</th>
                                                <th>Position</th>
                                                <th class="text-center">Vacancies</th>
                                                <th>Requested By</th>
                                                <th>Created At</th>
                                                <th class="text-end">Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="req" items="${dept.value}">
                                                <tr>
                                                    <td>${req.id}</td>
                                                    <td>
                                                        <a href="${pageContext.request.contextPath}/requests/detail?id=${req.id}" 
                                                           class="text-decoration-none">
                                                            <c:out value="${req.title}"/>
                                                        </a>
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
                                                    <td>
                                                        <fmt:parseDate value="${req.createdAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" />
                                                        <fmt:formatDate value="${parsedDate}" pattern="MMM dd, yyyy" />
                                                    </td>
                                                    <td class="text-end">
                                                        <a href="${pageContext.request.contextPath}/job-posting/create?requestId=${req.id}" 
                                                           class="btn btn-sm btn-success">
                                                            <i class="fas fa-plus-circle me-1"></i>Create Job Posting
                                                        </a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </c:forEach>
                    </c:if>
                    
                    <c:if test="${not empty result.requests}">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th>#</th>
                                        <th>Request Title</th>
                                        <th>Position</th>
                                        <th class="text-center">Vacancies</th>
                                        <th>Requested By</th>
                                        <th>Department</th>
                                        <th>Created At</th>
                                        <th class="text-end">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="req" items="${result.requests}">
                                        <tr>
                                            <td>${req.id}</td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/requests/detail?id=${req.id}" class="text-decoration-none">
                                                    <c:out value="${req.title}"/>
                                                </a>
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
                                                <fmt:parseDate value="${req.createdAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" />
                                                <fmt:formatDate value="${parsedDate}" pattern="MMM dd, yyyy" />
                                            </td>
                                            <td class="text-end">
                                                <a href="${pageContext.request.contextPath}/job-posting/create?requestId=${req.id}" 
                                                   class="btn btn-sm btn-success">
                                                    <i class="fas fa-plus-circle me-1"></i>Create Job Posting
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <!-- Include footer -->
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"/>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>