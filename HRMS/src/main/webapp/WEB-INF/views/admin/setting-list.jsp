<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="Setting List - HRMS" />
        <jsp:param name="pageCss" value="dashboard.css" />
    </jsp:include>
    <style>
        .main-content { margin-left: 260px; padding: 2rem; }
        .card { border: none; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        .table th { background-color: #f8f9fa; font-weight: 600; }
        .btn-action { padding: 0.25rem 0.5rem; font-size: 0.875rem; }
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
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2><i class="fas fa-cog"></i> Setting List</h2>
                <a href="${pageContext.request.contextPath}/settings/new" class="btn btn-primary">
                    <i class="fas fa-plus"></i> New Setting
                </a>
            </div>

            <c:if test="${param.success == 'create'}">
                <div class="alert alert-success alert-dismissible fade show">
                    Setting created successfully!
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${param.success == 'update'}">
                <div class="alert alert-success alert-dismissible fade show">
                    Setting updated successfully!
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${param.success == 'delete'}">
                <div class="alert alert-success alert-dismissible fade show">
                    Setting deleted successfully!
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${param.error != null}">
                <div class="alert alert-danger alert-dismissible fade show">
                    Error: ${param.error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <div class="card">
                <div class="card-body">
                    <form method="get" action="${pageContext.request.contextPath}/settings" id="filterForm" class="row g-3 mb-4">
                        <div class="col-md-2">
                            <select name="type" class="form-select" onchange="document.getElementById('filterForm').submit()">
                                <option value="all" ${typeFilter == 'all' ? 'selected' : ''}>All Types</option>
                                <option value="Department" ${typeFilter == 'Department' ? 'selected' : ''}>Department</option>
                                <option value="Position" ${typeFilter == 'Position' ? 'selected' : ''}>Position</option>
                                <option value="Role" ${typeFilter == 'Role' ? 'selected' : ''}>Role</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <select name="sortBy" class="form-select" onchange="document.getElementById('filterForm').submit()">
                                <option value="">Sort By</option>
                                <option value="id" ${sortBy == 'id' ? 'selected' : ''}>Sort by ID</option>
                                <option value="name" ${sortBy == 'name' ? 'selected' : ''}>Sort by Name</option>
                            </select>
                        </div>
                        <div class="col-md-5">
                            <input type="text" name="search" class="form-control" 
                                   placeholder="Enter keyword(s) to search" value="${searchKeyword}">
                        </div>
                        <div class="col-md-3">
                            <button type="submit" class="btn btn-primary w-100">
                                <i class="fas fa-search"></i> Search
                            </button>
                        </div>
                    </form>
                    
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <div class="text-muted">
                            Showing ${(currentPage - 1) * 5 + 1} to ${(currentPage - 1) * 5 + settingList.size()} of ${totalItems} entries
                        </div>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Id</th>
                                    <th>Name</th>
                                    <th>Type</th>
                                    <th>Value</th>
                                    <th>Priority</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="setting" items="${settingList}">
                                    <tr>
                                        <td>${setting.id}</td>
                                        <td>${setting.name}</td>
                                        <td>${setting.type}</td>
                                        <td>${setting.value != null ? setting.value : ''}</td>
                                        <td>${setting.priority != null ? setting.priority : ''}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/settings/edit?id=${setting.id}&type=${setting.type}" 
                                               class="btn btn-sm btn-warning btn-action">
                                                <i class="fas fa-edit"></i> Edit
                                            </a>
                                            <button type="button" class="btn btn-sm btn-danger btn-action" 
                                                    data-id="${setting.id}" data-type="${setting.type}" data-name="${setting.name}"
                                                    onclick="confirmDelete(this)">
                                                <i class="fas fa-trash"></i> Delete
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty settingList}">
                                    <tr>
                                        <td colspan="6" class="text-center text-muted">No data available</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                    
                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Page navigation">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="?type=${typeFilter}&search=${searchKeyword}&sortBy=${sortBy}&page=${currentPage - 1}">Previous</a>
                                </li>
                                
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <c:choose>
                                        <c:when test="${currentPage == i}">
                                            <li class="page-item active"><span class="page-link">${i}</span></li>
                                        </c:when>
                                        <c:otherwise>
                                            <li class="page-item"><a class="page-link" href="?type=${typeFilter}&search=${searchKeyword}&sortBy=${sortBy}&page=${i}">${i}</a></li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="?type=${typeFilter}&search=${searchKeyword}&sortBy=${sortBy}&page=${currentPage + 1}">Next</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <jsp:include page="../layout/dashboard-footer.jsp" />
    </div>

    <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/settings/delete" style="display: none;">
        <input type="hidden" name="id" id="deleteId">
        <input type="hidden" name="type" id="deleteType">
    </form>

    <script>
        function confirmDelete(btn) {
            var id = btn.getAttribute('data-id');
            var type = btn.getAttribute('data-type');
            var name = btn.getAttribute('data-name');
            
            if (confirm('Are you sure you want to delete setting "' + name + '"?')) {
                document.getElementById('deleteId').value = id;
                document.getElementById('deleteType').value = type;
                document.getElementById('deleteForm').submit();
            }
        }
    </script>
</body>
</html>
