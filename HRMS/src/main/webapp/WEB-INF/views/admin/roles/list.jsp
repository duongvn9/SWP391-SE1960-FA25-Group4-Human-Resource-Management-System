<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Role List - HRMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .container-fluid {
            padding: 30px;
        }
        .page-header {
            background: white;
            padding: 20px 30px;
            border-radius: 8px;
            margin-bottom: 25px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .page-header h2 {
            margin: 0;
            color: #333;
            font-size: 1.75rem;
            font-weight: 600;
        }
        .search-section {
            background: white;
            padding: 20px 30px;
            border-radius: 8px;
            margin-bottom: 25px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .search-form {
            display: flex;
            gap: 15px;
            align-items: center;
        }
        .search-input {
            flex: 1;
            max-width: 400px;
        }
        .table-section {
            background: white;
            padding: 20px 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .table {
            margin-bottom: 0;
        }
        .table thead th {
            background-color: #f8f9fa;
            border-bottom: 2px solid #dee2e6;
            font-weight: 600;
            color: #495057;
        }
        .badge-system {
            background-color: #6c757d;
        }
        .badge-custom {
            background-color: #0d6efd;
        }
        .action-buttons {
            display: flex;
            gap: 8px;
        }
        .pagination {
            margin-top: 20px;
            margin-bottom: 0;
        }
        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            color: #007bff;
            text-decoration: none;
        }
        .back-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <!-- Include dashboard header -->
    <jsp:include page="../../layout/dashboard-header.jsp" />
    
    <!-- Include sidebar -->
    <jsp:include page="../../layout/sidebar.jsp">
        <jsp:param name="currentPage" value="roles" />
    </jsp:include>
    
    <div class="main-content">
        <div class="container-fluid">
            <!-- Back Link -->
            <a href="${pageContext.request.contextPath}/dashboard" class="back-link">
                <i class="fa-solid fa-arrow-left me-2"></i>Back to Dashboard
            </a>

        <!-- Page Header -->
        <div class="page-header d-flex justify-content-between align-items-center">
            <h2>Role List</h2>
            <a href="${pageContext.request.contextPath}/admin/roles/new" class="btn btn-primary">
                <i class="fa-solid fa-plus me-2"></i>New Role
            </a>
        </div>

        <!-- Success/Error Messages -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fa-solid fa-circle-check me-2"></i>${sessionScope.successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="successMessage" scope="session"/>
        </c:if>
        
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fa-solid fa-circle-exclamation me-2"></i>${sessionScope.errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>

        <!-- Search Section -->
        <div class="search-section">
            <form method="get" action="${pageContext.request.contextPath}/admin/roles" class="search-form">
                <div class="search-input">
                    <input type="text" name="keyword" class="form-control" 
                           placeholder="Search by ID, name, or code" 
                           value="${keyword}">
                </div>
                <button type="submit" class="btn btn-primary">
                    <i class="fa-solid fa-search me-2"></i>Search
                </button>
                <c:if test="${not empty keyword}">
                    <a href="${pageContext.request.contextPath}/admin/roles" class="btn btn-secondary">
                        <i class="fa-solid fa-times me-2"></i>Clear
                    </a>
                </c:if>
            </form>
        </div>

        <!-- Table Section -->
        <div class="table-section">
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th style="width: 80px;">ID</th>
                        <th>Name</th>
                        <th style="width: 200px;">Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty roles}">
                            <tr>
                                <td colspan="3" class="text-center text-muted py-4">
                                    <i class="fa-solid fa-inbox fa-2x mb-2"></i>
                                    <p class="mb-0">No roles found</p>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="role" items="${roles}">
                                <tr>
                                    <td>${role.id}</td>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <div>
                                                <strong>${role.name}</strong>
                                                <c:if test="${role.isSystem}">
                                                    <span class="badge badge-system ms-2">System</span>
                                                </c:if>
                                                <c:if test="${!role.isSystem}">
                                                    <span class="badge badge-custom ms-2">Custom</span>
                                                </c:if>
                                                <br>
                                                <small class="text-muted">Code: ${role.code} | Priority: ${role.priority}</small>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="action-buttons">
                                            <c:choose>
                                                <c:when test="${role.isSystem}">
                                                    <button class="btn btn-sm btn-secondary" disabled>
                                                        <i class="fa-solid fa-pen"></i> Edit
                                                    </button>
                                                    <button class="btn btn-sm btn-secondary" disabled>
                                                        <i class="fa-solid fa-trash"></i> Delete
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="${pageContext.request.contextPath}/admin/roles/edit?id=${role.id}" 
                                                       class="btn btn-sm btn-primary">
                                                        <i class="fa-solid fa-pen"></i> Edit
                                                    </a>
                                                    <button type="button" class="btn btn-sm btn-danger" 
                                                            onclick="confirmDelete(${role.id}, '${role.name}')">
                                                        <i class="fa-solid fa-trash"></i> Delete
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>

            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <nav aria-label="Page navigation">
                    <ul class="pagination justify-content-center">
                        <!-- Previous Button -->
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${currentPage - 1}${not empty keyword ? '&keyword='.concat(keyword) : ''}">
                                Previous
                            </a>
                        </li>
                        
                        <!-- Page Numbers -->
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                <a class="page-link" href="?page=${i}${not empty keyword ? '&keyword='.concat(keyword) : ''}">
                                    ${i}
                                </a>
                            </li>
                        </c:forEach>
                        
                        <!-- Next Button -->
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${currentPage + 1}${not empty keyword ? '&keyword='.concat(keyword) : ''}">
                                Next
                            </a>
                        </li>
                    </ul>
                </nav>
            </c:if>
        </div>
        </div>
    </div>

    <!-- Delete Form (Hidden) -->
    <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/admin/roles/delete" style="display: none;">
        <input type="hidden" name="_csrf_token" value="${csrfToken}">
        <input type="hidden" name="id" id="deleteId">
    </form>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function confirmDelete(id, name) {
            if (confirm('Are you sure you want to delete role "' + name + '"?')) {
                document.getElementById('deleteId').value = id;
                document.getElementById('deleteForm').submit();
            }
        }
    </script>
</body>
</html>
