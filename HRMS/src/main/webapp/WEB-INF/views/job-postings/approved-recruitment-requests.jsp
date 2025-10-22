<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Approved Recruitment Requests - HRMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/layout.css" rel="stylesheet">
</head>
<body>
    <!-- Include header -->
    <jsp:include page="../layout/header.jsp" />
    
    <!-- Include sidebar -->
    <jsp:include page="../layout/sidebar.jsp">
        <jsp:param name="currentPage" value="approved-recruitment-requests"/>
    </jsp:include>

    <main class="main-content">
        <div class="container-fluid px-4 py-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="mb-0">Approved Recruitment Requests</h2>
            </div>

            <div class="card">
                <div class="card-body">
                    <c:if test="${not empty requests}">
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>
                                        <th>Request ID</th>
                                        <th>Department</th>
                                        <th>Position</th>
                                        <th>Number of Vacancies</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${requests}" var="request">
                                        <tr>
                                            <td>${request.id}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty request.department}">${request.department.name}</c:when>
                                                    <c:when test="${not empty request.recruitmentDetail and not empty request.recruitmentDetail.departmentName}">${request.recruitmentDetail.departmentName}</c:when>
                                                    <c:otherwise>—</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty request.position}">${request.position.name}</c:when>
                                                    <c:when test="${not empty request.recruitmentDetail and not empty request.recruitmentDetail.positionName}">${request.recruitmentDetail.positionName}</c:when>
                                                    <c:otherwise>—</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty request.numberOfPositions}">${request.numberOfPositions}</c:when>
                                                    <c:when test="${not empty request.recruitmentDetail and not empty request.recruitmentDetail.quantity}">${request.recruitmentDetail.quantity}</c:when>
                                                    <c:otherwise>—</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <span class="badge bg-success">APPROVED</span>
                                            </td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/job-posting/create?requestId=${request.id}" 
                                                   class="btn btn-primary btn-sm">
                                                    Create Job Posting
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:if>
                    
                    <c:if test="${empty requests}">
                        <div class="alert alert-info" role="alert">
                            No approved recruitment requests found.
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/layout.js"></script>
</body>
</html>