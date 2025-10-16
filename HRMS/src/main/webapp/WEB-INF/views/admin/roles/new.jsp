<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>New Role - HRMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .container {
            max-width: 800px;
            margin: 50px auto;
        }
        .form-card {
            background: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .form-header {
            text-align: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 2px solid #dee2e6;
        }
        .form-header h2 {
            color: #333;
            font-weight: 600;
            margin: 0;
        }
        .form-label {
            font-weight: 500;
            color: #333;
        }
        .text-danger {
            color: #dc3545;
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
        .form-text {
            font-size: 0.875rem;
            color: #6c757d;
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
        <div class="container">
            <!-- Back Link -->
        <a href="${pageContext.request.contextPath}/admin/roles" class="back-link">
            <i class="fa-solid fa-arrow-left me-2"></i>Back to Role List
        </a>

        <!-- Form Card -->
        <div class="form-card">
            <!-- Form Header -->
            <div class="form-header">
                <h2>Create New Role</h2>
            </div>

            <!-- Error Messages -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fa-solid fa-circle-exclamation me-2"></i>${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Form -->
            <form method="post" action="${pageContext.request.contextPath}/admin/roles/new">
                <!-- CSRF Token -->
                <input type="hidden" name="_csrf_token" value="${csrfToken}">

                <!-- Role Code -->
                <div class="mb-3">
                    <label for="code" class="form-label">
                        Role Code <span class="text-danger">*</span>
                    </label>
                    <input type="text" class="form-control" id="code" name="code" 
                           value="${dto.code}" required maxlength="50"
                           pattern="^[A-Z_]+$"
                           placeholder="e.g., DEPT_MANAGER">
                    <div class="form-text">
                        Uppercase letters and underscores only. Max 50 characters.
                    </div>
                </div>

                <!-- Role Name -->
                <div class="mb-3">
                    <label for="name" class="form-label">
                        Role Name <span class="text-danger">*</span>
                    </label>
                    <input type="text" class="form-control" id="name" name="name" 
                           value="${dto.name}" required maxlength="100"
                           placeholder="e.g., Department Manager">
                    <div class="form-text">
                        Display name for the role. Max 100 characters.
                    </div>
                </div>

                <!-- Priority -->
                <div class="mb-4">
                    <label for="priority" class="form-label">
                        Priority
                    </label>
                    <input type="number" class="form-control" id="priority" name="priority" 
                           value="${dto.priority != null ? dto.priority : 0}" 
                           min="0" max="100">
                    <div class="form-text">
                        Higher priority roles have more authority. Default is 0.
                    </div>
                </div>

                <!-- Buttons -->
                <div class="d-flex justify-content-center gap-3">
                    <button type="submit" class="btn btn-primary px-5">
                        <i class="fa-solid fa-save me-2"></i>Create Role
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/roles" class="btn btn-secondary px-5">
                        <i class="fa-solid fa-times me-2"></i>Cancel
                    </a>
                </div>
            </form>
        </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Auto-convert code to uppercase
        document.getElementById('code').addEventListener('input', function(e) {
            e.target.value = e.target.value.toUpperCase();
        });
    </script>
</body>
</html>
