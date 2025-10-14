<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />
<jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/recruitment_approval.css">

<div class="main-content">
    <h2>Recruitment Requests - Waiting for HR Approval</h2>

    <c:if test="${empty pendingRequests}">
        <p>No pending requests.</p>
    </c:if>

    <c:if test="${not empty pendingRequests}">
        <table class="table table-striped approval-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Description</th>
                    <th>Created At</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="req" items="${pendingRequests}">
                    <tr>
                        <td>${req.id}</td>
                        <td>${req.title}</td>
                        <td>${req.description}</td>
                        <td>${req.createdAt}</td>
                        <td>${req.status}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/recruitment/approve?id=${req.id}" 
                               class="btn btn-success btn-sm">Approve</a>
                            <a href="${pageContext.request.contextPath}/recruitment/reject?id=${req.id}" 
                               class="btn btn-danger btn-sm">Reject</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
