<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="${setting != null ? 'Edit' : 'New'} Setting - HRMS" />
        <jsp:param name="pageCss" value="dashboard.css" />
    </jsp:include>
    <style>
        .main-content { margin-left: 260px; padding: 2rem; }
        .card { border: none; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        @media (max-width: 768px) {
            .main-content { margin-left: 0; }
        }
    </style>
</head>
<body>
    <jsp:include page="../layout/sidebar.jsp">
        <jsp:param name="currentPage" value="settings" />
    </jsp:include>

    <div class="main-content" id="main-content">
        <!-- Header -->
        <jsp:include page="../layout/dashboard-header.jsp" />

        <!-- Content Area -->
        <div class="content-area">
            <div class="mb-4">
                <h2><i class="fas fa-cog"></i> ${setting != null ? 'Edit' : 'New'} Setting</h2>
            </div>

            <div class="card">
                <div class="card-body">
                    <c:if test="${errorMessage != null}">
                        <div class="alert alert-danger">${errorMessage}</div>
                    </c:if>

                    <form method="post" action="${setting != null ? pageContext.request.contextPath.concat('/settings/edit') : pageContext.request.contextPath.concat('/settings/new')}">
                        <c:if test="${setting != null}">
                            <input type="hidden" name="id" value="${setting.id}">
                            <input type="hidden" name="oldType" value="${setting.type}">
                        </c:if>

                        <div class="mb-3">
                            <label for="name" class="form-label">Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="name" name="name" 
                                   value="${setting != null ? setting.name : name}" required>
                        </div>

                        <div class="mb-3">
                            <label for="type" class="form-label">Type <span class="text-danger">*</span></label>
                            <c:choose>
                                <c:when test="${setting != null}">
                                    <!-- Read-only type field when editing -->
                                    <input type="text" class="form-control" id="type-display" value="${setting.type}" readonly disabled>
                                    <input type="hidden" name="type" value="${setting.type}">
                                    <small class="text-muted">Type cannot be changed after creation</small>
                                </c:when>
                                <c:otherwise>
                                    <!-- Editable type dropdown when creating new -->
                                    <select class="form-select" id="type" name="type" required>
                                        <option value="">-- Select Type --</option>
                                        <option value="Department" ${type == 'Department' ? 'selected' : ''}>Department</option>
                                        <option value="Position" ${type == 'Position' ? 'selected' : ''}>Position</option>
                                        <option value="Role" ${type == 'Role' ? 'selected' : ''}>Role</option>
                                    </select>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="mb-3">
                            <label for="value" class="form-label">Value (Code)</label>
                            <input type="text" class="form-control" id="value" name="value" 
                                   value="${setting != null ? setting.value : value}">
                            <small class="text-muted">Used for Position Code or Role Code</small>
                        </div>

                        <div class="mb-3">
                            <label for="priority" class="form-label">Priority</label>
                            <input type="number" class="form-control" id="priority" name="priority" 
                                   value="${setting != null ? setting.priority : priority}">
                            <small class="text-muted">Job Level for Position or Priority for Role</small>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> ${setting != null ? 'Update' : 'Create'}
                            </button>
                            <a href="${pageContext.request.contextPath}/settings" class="btn btn-secondary">
                                <i class="fas fa-times"></i> Cancel
                            </a>
                        </div>
                    </form>
                </div>
            </div>
            </div>
        </div>

        <!-- Footer -->
        <jsp:include page="../layout/dashboard-footer.jsp" />
    </div>
</body>
</html>
