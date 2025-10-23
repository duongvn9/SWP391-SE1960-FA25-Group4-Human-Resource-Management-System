<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <jsp:include page="../layout/head.jsp">
                <jsp:param name="pageTitle" value="User Management - HRMS" />
                <jsp:param name="pageCss" value="dashboard.css" />
            </jsp:include>
            <style>
                /* User List Specific Styles */
                .page-header {
                    background: #fff;
                    padding: 1.5rem;
                    border-radius: 10px;
                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
                    margin-bottom: 1.5rem;
                }

                .breadcrumb {
                    background: transparent;
                    padding: 0;
                    margin-bottom: 0.5rem;
                }

                .breadcrumb-item+.breadcrumb-item::before {
                    content: "›";
                    color: #6c757d;
                }

                .breadcrumb-item a {
                    color: #667eea;
                    text-decoration: none;
                }

                .breadcrumb-item.active {
                    color: #6c757d;
                }

                .filter-section {
                    background: #fff;
                    padding: 1.5rem;
                    border-radius: 10px;
                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
                    margin-bottom: 1.5rem;
                }

                .filter-row {
                    display: flex;
                    gap: 1rem;
                    flex-wrap: wrap;
                    align-items: end;
                }

                .filter-group {
                    flex: 1;
                    min-width: 200px;
                }

                .filter-group label {
                    display: block;
                    margin-bottom: 0.5rem;
                    font-weight: 500;
                    color: #495057;
                }

                .filter-group input,
                .filter-group select {
                    width: 100%;
                    padding: 0.5rem 0.75rem;
                    border: 1px solid #ced4da;
                    border-radius: 6px;
                    font-size: 0.95rem;
                }

                .filter-actions {
                    display: flex;
                    gap: 0.5rem;
                    align-items: end;
                }

                .table-card {
                    background: #fff;
                    border-radius: 10px;
                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
                    overflow: hidden;
                }

                .table-card .card-header {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: #fff;
                    padding: 1rem 1.5rem;
                    border: none;
                }

                .table-responsive {
                    overflow-x: auto;
                }

                .table {
                    margin-bottom: 0;
                }

                .table thead th {
                    background: #f8f9fa;
                    color: #495057;
                    font-weight: 600;
                    border-bottom: 2px solid #dee2e6;
                    padding: 1rem;
                    white-space: nowrap;
                }

                .table tbody td {
                    padding: 1rem;
                    vertical-align: middle;
                }

                .table tbody tr:hover {
                    background-color: #f8f9fa;
                }

                .status-badge {
                    padding: 0.35rem 0.75rem;
                    border-radius: 20px;
                    font-size: 0.85rem;
                    font-weight: 500;
                    display: inline-block;
                }

                .status-badge.active {
                    background-color: #d4edda;
                    color: #155724;
                }

                .status-badge.inactive {
                    background-color: #e2e3e5;
                    color: #383d41;
                }

                .status-badge.terminated {
                    background-color: #f8d7da;
                    color: #721c24;
                }

                .status-badge.locked {
                    background-color: #fff3cd;
                    color: #856404;
                }

                .status-badge.suspended {
                    background-color: #f8d7da;
                    color: #721c24;
                }

                .action-buttons {
                    display: flex;
                    gap: 0.5rem;
                }

                .btn-action {
                    padding: 0.4rem 0.75rem;
                    border-radius: 6px;
                    font-size: 0.875rem;
                    border: none;
                    cursor: pointer;
                    transition: all 0.2s ease;
                }

                .btn-action.btn-view {
                    background-color: #17a2b8;
                    color: #fff;
                }

                .btn-action.btn-view:hover {
                    background-color: #138496;
                }

                .btn-action.btn-edit {
                    background-color: #ffc107;
                    color: #212529;
                }

                .btn-action.btn-edit:hover {
                    background-color: #e0a800;
                }

                .btn-action:disabled {
                    opacity: 0.5;
                    cursor: not-allowed;
                }

                .pagination-wrapper {
                    padding: 1.5rem;
                    background: #fff;
                    border-top: 1px solid #dee2e6;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    flex-wrap: nowrap;
                    gap: 1rem;
                }

                .pagination-info {
                    color: #495057;
                    font-size: 0.95rem;
                    font-weight: 500;
                    flex: 0 0 auto;
                    white-space: nowrap;
                    order: 1;
                }

                .pagination {
                    margin: 0;
                    flex: 1 1 auto;
                    display: flex;
                    justify-content: center;
                    order: 2;
                }

                .pagination .page-link {
                    color: #495057;
                    background-color: #fff;
                    border: 1px solid #dee2e6;
                    padding: 0.5rem 0.75rem;
                    font-weight: 500;
                }

                .pagination .page-item.active .page-link {
                    background-color: #667eea;
                    border-color: #667eea;
                    color: #fff;
                }

                .pagination .page-item.disabled .page-link {
                    color: #6c757d;
                    background-color: #fff;
                }

                .pagination .page-link:hover:not(.active) {
                    background-color: #f8f9fa;
                    color: #667eea;
                }

                .records-per-page {
                    display: flex;
                    align-items: center;
                    gap: 0.5rem;
                    flex: 0 0 auto;
                    white-space: nowrap;
                    order: 3;
                }

                .records-per-page label {
                    color: #495057;
                    font-weight: 500;
                    margin: 0;
                }

                .records-per-page select {
                    padding: 0.375rem 0.75rem;
                    border: 1px solid #ced4da;
                    border-radius: 6px;
                    color: #495057;
                    font-weight: 500;
                }

                .empty-state,
                .loading-state {
                    text-align: center;
                    padding: 3rem 1rem;
                }

                .empty-state i,
                .loading-state i {
                    font-size: 3rem;
                    color: #6c757d;
                    margin-bottom: 1rem;
                }

                .empty-state p,
                .loading-state p {
                    color: #6c757d;
                    font-size: 1.1rem;
                }

                .spinner-border {
                    width: 3rem;
                    height: 3rem;
                }

                /* Accounts list in modal - scrollable */
                #view-accounts-list {
                    max-height: 250px;
                    overflow-y: auto;
                    overflow-x: hidden;
                }

                #view-accounts-list .list-group {
                    margin-bottom: 0;
                }

                #view-accounts-list .list-group-item {
                    border-left: none;
                    border-right: none;
                }

                #view-accounts-list .list-group-item:first-child {
                    border-top: none;
                }

                #view-accounts-list .list-group-item:last-child {
                    border-bottom: none;
                }

                /* Custom scrollbar for accounts list */
                #view-accounts-list::-webkit-scrollbar {
                    width: 8px;
                }

                #view-accounts-list::-webkit-scrollbar-track {
                    background: #f1f1f1;
                    border-radius: 4px;
                }

                #view-accounts-list::-webkit-scrollbar-thumb {
                    background: #888;
                    border-radius: 4px;
                }

                #view-accounts-list::-webkit-scrollbar-thumb:hover {
                    background: #555;
                }

                /* Responsive Design */
                @media (max-width: 1023px) {
                    .table .date-joined-col {
                        display: none;
                    }
                }

                @media (max-width: 768px) {
                    .filter-row {
                        flex-direction: column;
                    }

                    .filter-group {
                        width: 100%;
                    }

                    .filter-actions {
                        width: 100%;
                        justify-content: stretch;
                    }

                    .filter-actions button {
                        flex: 1;
                    }

                    .table-responsive {
                        overflow-x: scroll;
                    }

                    .pagination-wrapper {
                        flex-direction: column;
                        text-align: center;
                    }

                    .action-buttons {
                        flex-direction: column;
                    }
                }

                /* Modal Styles */
                .modal-content {
                    border: none;
                    border-radius: 12px;
                    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
                }

                .modal-header {
                    padding: 1.5rem 1.5rem 0.5rem;
                }

                .modal-title {
                    font-size: 1.25rem;
                    font-weight: 600;
                    display: flex;
                    align-items: center;
                }

                .modal-body {
                    padding: 1rem 1.5rem;
                    font-size: 1rem;
                    color: #495057;
                }

                .modal-footer {
                    padding: 0.75rem 1.5rem 1.5rem;
                }

                .modal-backdrop.show {
                    opacity: 0.6;
                }

                /* Success Alert Styles */
                .alert-success {
                    background-color: #d4edda;
                    border: 1px solid #c3e6cb;
                    color: #155724;
                    padding: 1rem 1.25rem;
                    border-radius: 6px;
                    margin-bottom: 1.5rem;
                }

                .alert-success .btn-close {
                    padding: 0.5rem;
                }
            </style>
        </head>

        <body>
            <!-- Sidebar -->
            <jsp:include page="../layout/sidebar.jsp">
                <jsp:param name="currentPage" value="user-list" />
            </jsp:include>

            <!-- Main Content -->
            <div class="main-content" id="main-content">
                <!-- Header -->
                <jsp:include page="../layout/dashboard-header.jsp" />

                <!-- Content Area -->
                <div class="content-area">
                    <!-- Success Message -->
                    <c:if test="${not empty sessionScope.successMessage}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>${sessionScope.successMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                        <c:remove var="successMessage" scope="session" />
                    </c:if>

                    <!-- Page Header -->
                    <div class="page-header">
                        <div class="d-flex justify-content-between align-items-center">
                            <h2 class="mb-0">User Management</h2>
                            <c:if test="${canCreateUser}">
                                <a href="${pageContext.request.contextPath}/employees/users/create"
                                    class="btn btn-primary">
                                    <i class="fas fa-plus me-2"></i>Add New User
                                </a>
                            </c:if>
                        </div>
                    </div>

                    <!-- Filter Section -->
                    <div class="filter-section">
                        <form method="get" action="${pageContext.request.contextPath}/employees/users" id="filterForm">
                            <div class="filter-row">
                                <div class="filter-group">
                                    <label for="search">Search</label>
                                    <input type="text" id="search" name="search" class="form-control"
                                        placeholder="Employee Code, Name, or Email" value="${param.search}">
                                </div>
                                <div class="filter-group">
                                    <label for="department">Department</label>
                                    <select id="department" name="department" class="form-select">
                                        <option value="">All Departments</option>
                                        <c:forEach var="dept" items="${departments}">
                                            <option value="${dept.id}" ${param.department==dept.id ? 'selected' : '' }>
                                                ${dept.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="filter-group">
                                    <label for="position">Position</label>
                                    <select id="position" name="position" class="form-select">
                                        <option value="">All Positions</option>
                                        <c:forEach var="pos" items="${positions}">
                                            <option value="${pos.id}" ${param.position==pos.id ? 'selected' : '' }>
                                                ${pos.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="filter-group">
                                    <label for="gender">Gender</label>
                                    <select id="gender" name="gender" class="form-select">
                                        <option value="">All</option>
                                        <option value="Male" ${param.gender=='Male' ? 'selected' : '' }>Male</option>
                                        <option value="Female" ${param.gender=='Female' ? 'selected' : '' }>Female
                                        </option>
                                    </select>
                                </div>
                                <div class="filter-group">
                                    <label for="status">Status</label>
                                    <select id="status" name="status" class="form-select">
                                        <option value="">All Status</option>
                                        <option value="active" ${param.status=='active' ? 'selected' : '' }>Active
                                        </option>
                                        <option value="inactive" ${param.status=='inactive' ? 'selected' : '' }>Inactive
                                        </option>
                                    </select>
                                </div>
                                <div class="filter-actions">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-search me-1"></i>Filter
                                    </button>
                                    <a href="${pageContext.request.contextPath}/employees/users"
                                        class="btn btn-secondary">
                                        <i class="fas fa-times me-1"></i>Clear
                                    </a>
                                </div>
                            </div>
                            <!-- Hidden fields for pagination -->
                            <input type="hidden" name="page" value="${currentPage}">
                            <input type="hidden" name="pageSize" value="${pageSize}">
                        </form>
                    </div>

                    <!-- User List Table -->
                    <div class="table-card">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="fas fa-users me-2"></i>User List</h5>
                        </div>

                        <!-- DEBUG INFO -->
                        <!-- Users attribute: ${users} -->
                        <!-- Users size: ${users != null ? users.size() : 'null'} -->
                        <!-- Total records: ${totalRecords} -->
                        <!-- Current page: ${currentPage} -->
                        <!-- Page size: ${pageSize} -->

                        <c:choose>
                            <c:when test="${loading}">
                                <!-- Loading State -->
                                <div class="loading-state">
                                    <div class="spinner-border text-primary" role="status">
                                        <span class="visually-hidden">Loading...</span>
                                    </div>
                                    <p class="mt-3">Loading data...</p>
                                </div>
                            </c:when>
                            <c:when test="${empty users}">
                                <!-- Empty State -->
                                <div class="empty-state">
                                    <i class="fas fa-inbox"></i>
                                    <p>No users found</p>
                                    <c:if
                                        test="${not empty param.search or not empty param.department or not empty param.position or not empty param.status}">
                                        <p class="text-muted">Try adjusting your filters</p>
                                    </c:if>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <!-- Table with Data -->
                                <div class="table-responsive">
                                    <table class="table">
                                        <thead>
                                            <tr>
                                                <th>Employee Code</th>
                                                <th>Full Name</th>
                                                <th>Email</th>
                                                <th>Gender</th>
                                                <th>Department</th>
                                                <th>Position</th>
                                                <th>Status</th>
                                                <th class="date-joined-col">Date Joined</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="user" items="${users}">
                                                <tr>
                                                    <td>${user.employeeCode}</td>
                                                    <td>${user.fullName}</td>
                                                    <td>${user.emailCompany}</td>
                                                    <td>${user.gender != null ?
                                                        user.gender.substring(0,1).toUpperCase().concat(user.gender.substring(1).toLowerCase())
                                                        : '-'}</td>
                                                    <td>${user.departmentName != null ? user.departmentName : '-'}</td>
                                                    <td>${user.positionName != null ? user.positionName : '-'}</td>
                                                    <td>
                                                        <span class="status-badge ${user.status}">
                                                            ${user.status}
                                                        </span>
                                                    </td>
                                                    <td class="date-joined-col">
                                                        ${user.dateJoined != null ? user.dateJoined : '-'}
                                                    </td>
                                                    <td>
                                                        <div class="action-buttons">
                                                            <button class="btn-action btn-view"
                                                                data-user-id="${user.id}" title="View Details">
                                                                <i class="fas fa-eye"></i>
                                                            </button>
                                                            <c:if test="${isAdmin}">
                                                                <button class="btn-action btn-edit"
                                                                    data-user-id="${user.id}" title="Edit User">
                                                                    <i class="fas fa-edit"></i>
                                                                </button>
                                                            </c:if>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>

                                <!-- Pagination -->
                                <div class="pagination-wrapper">
                                    <div class="pagination-info">
                                        Showing ${((currentPage - 1) * pageSize) + 1} to
                                        ${currentPage * pageSize > totalRecords ? totalRecords : currentPage * pageSize}
                                        of ${totalRecords} entries
                                    </div>

                                    <div class="records-per-page">
                                        <label for="pageSizeSelect">Records per page:</label>
                                        <select id="pageSizeSelect" onchange="changePageSize(this.value)">
                                            <option value="10" ${pageSize==10 ? 'selected' : '' }>10</option>
                                            <option value="20" ${pageSize==20 ? 'selected' : '' }>20</option>
                                            <option value="50" ${pageSize==50 ? 'selected' : '' }>50</option>
                                        </select>
                                    </div>

                                    <c:if test="${totalPages > 1}">
                                        <nav aria-label="Page navigation">
                                            <ul class="pagination mb-0">
                                                <!-- Previous Button -->
                                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                    <a class="page-link" href="#" data-page="${currentPage - 1}">
                                                        <i class="fas fa-chevron-left"></i>
                                                    </a>
                                                </li>

                                                <!-- Page Numbers -->
                                                <c:forEach begin="1" end="${totalPages}" var="pageNum">
                                                    <c:if
                                                        test="${pageNum == 1 || pageNum == totalPages || (pageNum >= currentPage - 2 && pageNum <= currentPage + 2)}">
                                                        <li class="page-item ${pageNum == currentPage ? 'active' : ''}">
                                                            <a class="page-link" href="#" data-page="${pageNum}">
                                                                ${pageNum}
                                                            </a>
                                                        </li>
                                                    </c:if>
                                                    <c:if
                                                        test="${pageNum == currentPage - 3 || pageNum == currentPage + 3}">
                                                        <li class="page-item disabled">
                                                            <span class="page-link">...</span>
                                                        </li>
                                                    </c:if>
                                                </c:forEach>

                                                <!-- Next Button -->
                                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                                    <a class="page-link" href="#" data-page="${currentPage + 1}">
                                                        <i class="fas fa-chevron-right"></i>
                                                    </a>
                                                </li>
                                            </ul>
                                        </nav>
                                    </c:if>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Footer -->
                <jsp:include page="../layout/dashboard-footer.jsp" />
            </div>

            <!-- View User Detail Modal -->
            <div class="modal fade" id="viewUserModal" tabindex="-1" aria-labelledby="viewUserModalLabel"
                aria-hidden="true">
                <div class="modal-dialog modal-lg modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="viewUserModalLabel">
                                <i class="fas fa-user me-2"></i>User Details
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Employee Code</label>
                                    <p class="form-control-plaintext" id="view-employeeCode">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Status</label>
                                    <p class="form-control-plaintext">
                                        <span class="status-badge" id="view-status">-</span>
                                    </p>
                                </div>
                                <div class="col-12">
                                    <label class="form-label fw-bold">Full Name</label>
                                    <p class="form-control-plaintext" id="view-fullName">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Phone</label>
                                    <p class="form-control-plaintext" id="view-phone">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Company Email</label>
                                    <p class="form-control-plaintext" id="view-emailCompany">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Gender</label>
                                    <p class="form-control-plaintext" id="view-gender">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Department</label>
                                    <p class="form-control-plaintext" id="view-department">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Position</label>
                                    <p class="form-control-plaintext" id="view-position">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Date Joined</label>
                                    <p class="form-control-plaintext" id="view-dateJoined">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Start Work Date</label>
                                    <p class="form-control-plaintext" id="view-startWorkDate">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Created At</label>
                                    <p class="form-control-plaintext" id="view-createdAt">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Updated At</label>
                                    <p class="form-control-plaintext" id="view-updatedAt">-</p>
                                </div>
                                <div class="col-12">
                                    <hr class="my-3">
                                    <label class="form-label fw-bold">
                                        <i class="fas fa-user-shield me-2"></i>Accounts
                                    </label>
                                    <div id="view-accounts-list">
                                        <p class="text-muted">Loading...</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                <i class="fas fa-times me-2"></i>Close
                            </button>
                            <c:if test="${isAdmin}">
                                <button type="button" class="btn btn-primary" id="btnEditFromView">
                                    <i class="fas fa-edit me-2"></i>Edit User
                                </button>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Edit User Modal -->
            <div class="modal fade" id="editUserModal" tabindex="-1" aria-labelledby="editUserModalLabel"
                aria-hidden="true">
                <div class="modal-dialog modal-lg modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="editUserModalLabel">
                                <i class="fas fa-edit me-2"></i>Edit User
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <form id="editUserForm" method="post">
                            <input type="hidden" id="edit-userId" name="userId">
                            <div class="modal-body">

                                <!-- Employee Code (Read-only) -->
                                <div class="mb-3">
                                    <label class="form-label fw-bold">Employee Code</label>
                                    <input type="text" class="form-control" id="edit-employeeCode" readonly>
                                </div>

                                <!-- Full Name -->
                                <div class="mb-3">
                                    <label for="edit-fullName" class="form-label">
                                        Full Name<span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control" id="edit-fullName" name="fullName" required
                                        maxlength="255">
                                </div>

                                <!-- Phone -->
                                <div class="mb-3">
                                    <label for="edit-phone" class="form-label">
                                        Phone<span class="text-danger">*</span>
                                    </label>
                                    <input type="tel" class="form-control" id="edit-phone" name="phone" required
                                        pattern="0[0-9]{9}"
                                        title="Phone number must be exactly 10 digits and start with 0">
                                </div>

                                <!-- Company Email -->
                                <div class="mb-3">
                                    <label for="edit-emailCompany" class="form-label">
                                        Company Email<span class="text-danger">*</span>
                                    </label>
                                    <input type="email" class="form-control" id="edit-emailCompany" name="emailCompany"
                                        required>
                                </div>

                                <!-- Gender -->
                                <div class="mb-3">
                                    <label for="edit-gender" class="form-label">
                                        Gender<span class="text-danger">*</span>
                                    </label>
                                    <select class="form-select" id="edit-gender" name="gender" required>
                                        <option value="">Select Gender</option>
                                        <option value="male">Male</option>
                                        <option value="female">Female</option>
                                    </select>
                                </div>

                                <div class="row">
                                    <!-- Department -->
                                    <div class="col-md-6 mb-3">
                                        <label for="edit-departmentId" class="form-label">
                                            Department<span class="text-danger">*</span>
                                        </label>
                                        <select class="form-select" id="edit-departmentId" name="departmentId" required>
                                            <option value="">Select Department</option>
                                            <c:forEach var="dept" items="${departments}">
                                                <option value="${dept.id}">${dept.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <!-- Position -->
                                    <div class="col-md-6 mb-3">
                                        <label for="edit-positionId" class="form-label">
                                            Position<span class="text-danger">*</span>
                                        </label>
                                        <select class="form-select" id="edit-positionId" name="positionId" required>
                                            <option value="">Select Position</option>
                                            <c:forEach var="pos" items="${positions}">
                                                <option value="${pos.id}">${pos.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>

                                <div class="row">
                                    <!-- Date Joined -->
                                    <div class="col-md-6 mb-3">
                                        <label for="edit-dateJoined" class="form-label">
                                            Date Joined<span class="text-danger">*</span>
                                        </label>
                                        <input type="date" class="form-control" id="edit-dateJoined" name="dateJoined"
                                            required>
                                    </div>

                                    <!-- Start Work Date -->
                                    <div class="col-md-6 mb-3">
                                        <label for="edit-startWorkDate" class="form-label">
                                            Start Work Date<span class="text-danger">*</span>
                                        </label>
                                        <input type="date" class="form-control" id="edit-startWorkDate"
                                            name="startWorkDate" required>
                                    </div>
                                </div>

                                <!-- Status -->
                                <div class="mb-3">
                                    <label for="edit-status" class="form-label">
                                        Status<span class="text-danger">*</span>
                                    </label>
                                    <select class="form-select" id="edit-status" name="status" required>
                                        <option value="active">Active</option>
                                        <option value="inactive">Inactive</option>
                                    </select>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                    <i class="fas fa-times me-2"></i>Cancel
                                </button>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save me-2"></i>Save Changes
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Notification Modal -->
            <div class="modal fade" id="notificationModal" tabindex="-1" aria-labelledby="notificationModalLabel"
                aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header" id="notificationModalHeader">
                            <h5 class="modal-title" id="notificationModalLabel">
                                <i class="fas fa-info-circle me-2"></i>Notification
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body" id="notificationModalBody">
                            <!-- Message will be inserted here -->
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- View Account Modal -->
            <div class="modal fade" id="viewAccountModal" tabindex="-1" aria-labelledby="viewAccountModalLabel"
                aria-hidden="true">
                <div class="modal-dialog modal-lg modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="viewAccountModalLabel">
                                <i class="fas fa-user-shield me-2"></i>Account Details
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Username</label>
                                    <p class="form-control-plaintext" id="view-account-username">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Status</label>
                                    <p class="form-control-plaintext">
                                        <span class="status-badge" id="view-account-status">-</span>
                                    </p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Email Login</label>
                                    <p class="form-control-plaintext" id="view-account-emailLogin">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">User (Full Name)</label>
                                    <p class="form-control-plaintext" id="view-account-userFullName">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Department</label>
                                    <p class="form-control-plaintext" id="view-account-department">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Position</label>
                                    <p class="form-control-plaintext" id="view-account-position">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Last Login</label>
                                    <p class="form-control-plaintext" id="view-account-lastLogin">-</p>
                                </div>
                                <div class="col-12">
                                    <hr class="my-3">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">
                                        <i class="fas fa-calendar-plus me-1"></i>Created At
                                    </label>
                                    <p class="form-control-plaintext" id="view-account-createdAt">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">
                                        <i class="fas fa-calendar-check me-1"></i>Updated At
                                    </label>
                                    <p class="form-control-plaintext" id="view-account-updatedAt">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">
                                        <i class="fas fa-key me-1"></i>Password Updated At
                                    </label>
                                    <p class="form-control-plaintext" id="view-account-passwordUpdatedAt">-</p>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                <i class="fas fa-times me-2"></i>Close
                            </button>
                            <a href="${pageContext.request.contextPath}/employees/accounts" class="btn btn-primary">
                                <i class="fas fa-list me-2"></i>Go to Account List
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <script>
                // Notification function
                function showNotification(message, type = 'info') {
                    const modal = new bootstrap.Modal(document.getElementById('notificationModal'));
                    const header = document.getElementById('notificationModalHeader');
                    const title = document.getElementById('notificationModalLabel');
                    const body = document.getElementById('notificationModalBody');

                    // Reset classes
                    header.className = 'modal-header';

                    // Set style based on type
                    if (type === 'success') {
                        header.classList.add('bg-success', 'text-white');
                        title.innerHTML = '<i class="fas fa-check-circle me-2"></i>Success';
                    } else if (type === 'error') {
                        header.classList.add('bg-danger', 'text-white');
                        title.innerHTML = '<i class="fas fa-exclamation-circle me-2"></i>Error';
                    } else if (type === 'warning') {
                        header.classList.add('bg-warning', 'text-dark');
                        title.innerHTML = '<i class="fas fa-exclamation-triangle me-2"></i>Warning';
                    } else {
                        header.classList.add('bg-info', 'text-white');
                        title.innerHTML = '<i class="fas fa-info-circle me-2"></i>Information';
                    }

                    body.textContent = message;
                    modal.show();
                }

                // Navigation functions
                function goToPage(page) {
                    const form = document.getElementById('filterForm');
                    const pageInput = form.querySelector('input[name="page"]');
                    pageInput.value = page;
                    form.submit();
                }

                function changePageSize(size) {
                    const form = document.getElementById('filterForm');
                    const pageSizeInput = form.querySelector('input[name="pageSize"]');
                    const pageInput = form.querySelector('input[name="page"]');
                    pageSizeInput.value = size;
                    pageInput.value = 1; // Reset to first page
                    form.submit();
                }

                // Action functions
                function viewUser(userId) {
                    // Fetch user details via AJAX
                    fetch('${pageContext.request.contextPath}/employees/users/details?id=' + userId)
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                const user = data.user;
                                // Populate view modal
                                document.getElementById('view-employeeCode').textContent = user.employeeCode || '-';
                                document.getElementById('view-fullName').textContent = user.fullName || '-';
                                document.getElementById('view-phone').textContent = user.phone || '-';
                                document.getElementById('view-emailCompany').textContent = user.emailCompany || '-';
                                document.getElementById('view-gender').textContent = user.gender ? user.gender.charAt(0).toUpperCase() + user.gender.slice(1) : '-';
                                document.getElementById('view-department').textContent = user.departmentName || '-';
                                document.getElementById('view-position').textContent = user.positionName || '-';
                                document.getElementById('view-dateJoined').textContent = user.dateJoined || '-';
                                document.getElementById('view-startWorkDate').textContent = user.startWorkDate || '-';
                                document.getElementById('view-createdAt').textContent = user.createdAt || '-';
                                document.getElementById('view-updatedAt').textContent = user.updatedAt || '-';

                                // Status badge
                                const statusBadge = document.getElementById('view-status');
                                statusBadge.textContent = user.status || '-';
                                statusBadge.className = 'status-badge ' + (user.status || '');

                                // Load accounts for this user
                                loadUserAccounts(userId);

                                // Store userId for edit button (only if button exists)
                                const editBtn = document.getElementById('btnEditFromView');
                                if (editBtn) {
                                    editBtn.setAttribute('data-user-id', userId);
                                }

                                // Show modal
                                const modal = new bootstrap.Modal(document.getElementById('viewUserModal'));
                                modal.show();
                            } else {
                                showNotification('Failed to load user details: ' + (data.message || 'Unknown error'), 'error');
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            showNotification('Failed to load user details', 'error');
                        });
                }

                function viewAccountFromUser(accountId) {
                    // Close user modal first
                    const userModal = bootstrap.Modal.getInstance(document.getElementById('viewUserModal'));
                    if (userModal) {
                        userModal.hide();
                    }

                    // Fetch account details and show in modal
                    setTimeout(() => {
                        fetch('${pageContext.request.contextPath}/employees/accounts/details?id=' + accountId)
                            .then(response => response.json())
                            .then(data => {
                                if (data.success) {
                                    const account = data.account;
                                    // Populate account view modal
                                    document.getElementById('view-account-username').textContent = account.username || '-';
                                    document.getElementById('view-account-emailLogin').textContent = account.emailLogin || '-';
                                    document.getElementById('view-account-userFullName').textContent = account.userFullName || '-';
                                    document.getElementById('view-account-department').textContent = account.departmentName || '-';
                                    document.getElementById('view-account-position').textContent = account.positionName || '-';
                                    document.getElementById('view-account-lastLogin').textContent = account.lastLoginAt || 'Never';
                                    document.getElementById('view-account-createdAt').textContent = account.createdAt || '-';
                                    document.getElementById('view-account-updatedAt').textContent = account.updatedAt || '-';
                                    document.getElementById('view-account-passwordUpdatedAt').textContent = account.passwordUpdatedAt || 'Never';

                                    // Status badge
                                    const statusBadge = document.getElementById('view-account-status');
                                    statusBadge.textContent = account.status || '-';
                                    statusBadge.className = 'status-badge ' + (account.status || '');

                                    // Show account modal
                                    const accountModal = new bootstrap.Modal(document.getElementById('viewAccountModal'));
                                    accountModal.show();
                                } else {
                                    showNotification('Failed to load account details: ' + (data.message || 'Unknown error'), 'error');
                                }
                            })
                            .catch(error => {
                                console.error('Error:', error);
                                showNotification('Failed to load account details', 'error');
                            });
                    }, 300);
                }

                function loadUserAccounts(userId) {
                    const accountsList = document.getElementById('view-accounts-list');
                    accountsList.innerHTML = '<p class="text-muted">Loading accounts...</p>';

                    // Fetch accounts for this user
                    fetch('${pageContext.request.contextPath}/employees/users/accounts?userId=' + userId)
                        .then(response => response.json())
                        .then(data => {
                            if (data.success && data.accounts && data.accounts.length > 0) {
                                let html = '<div class="list-group">';
                                data.accounts.forEach(account => {
                                    const statusClass = account.status === 'active' ? 'success' : 'secondary';
                                    html += '<a href="#" class="list-group-item list-group-item-action" onclick="viewAccountFromUser(' + account.id + '); return false;">';
                                    html += '<div class="d-flex w-100 justify-content-between align-items-center">';
                                    html += '<div>';
                                    html += '<h6 class="mb-1"><i class="fas fa-user-circle me-2"></i>' + account.username + '</h6>';
                                    html += '<small class="text-muted">' + (account.emailLogin || 'No email') + '</small>';
                                    html += '</div>';
                                    html += '<span class="badge bg-' + statusClass + '">' + account.status + '</span>';
                                    html += '</div>';
                                    html += '</a>';
                                });
                                html += '</div>';
                                accountsList.innerHTML = html;
                            } else {
                                accountsList.innerHTML = '<p class="text-muted"><i class="fas fa-info-circle me-2"></i>No accounts found for this user</p>';
                            }
                        })
                        .catch(error => {
                            console.error('Error loading accounts:', error);
                            accountsList.innerHTML = '<p class="text-danger"><i class="fas fa-exclamation-circle me-2"></i>Failed to load accounts</p>';
                        });
                }

                function editUser(userId) {
                    // Fetch user details via AJAX
                    fetch('${pageContext.request.contextPath}/employees/users/details?id=' + userId)
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                const user = data.user;
                                console.log('User data received:', user);
                                console.log('User ID:', user.id);

                                // Populate edit modal - need to convert formatted dates back to YYYY-MM-DD
                                document.getElementById('edit-userId').value = user.id;
                                document.getElementById('edit-employeeCode').value = user.employeeCode || '';
                                document.getElementById('edit-fullName').value = user.fullName || '';
                                document.getElementById('edit-phone').value = user.phone || '';
                                document.getElementById('edit-emailCompany').value = user.emailCompany || '';
                                // Set gender value (ensure lowercase to match select options)
                                document.getElementById('edit-gender').value = user.gender ? user.gender.toLowerCase() : '';
                                document.getElementById('edit-departmentId').value = user.departmentId || '';
                                document.getElementById('edit-positionId').value = user.positionId || '';

                                // Convert date format from dd/MM/yyyy to yyyy-MM-dd for input[type="date"]
                                if (user.dateJoined) {
                                    const djParts = user.dateJoined.split('/');
                                    if (djParts.length === 3) {
                                        document.getElementById('edit-dateJoined').value = djParts[2] + '-' + djParts[1] + '-' + djParts[0];
                                    }
                                }
                                if (user.startWorkDate) {
                                    const swdParts = user.startWorkDate.split('/');
                                    if (swdParts.length === 3) {
                                        document.getElementById('edit-startWorkDate').value = swdParts[2] + '-' + swdParts[1] + '-' + swdParts[0];
                                    }
                                }

                                document.getElementById('edit-status').value = user.status || 'active';

                                // Show modal
                                const modal = new bootstrap.Modal(document.getElementById('editUserModal'));
                                modal.show();
                            } else {
                                showNotification('Failed to load user details: ' + (data.message || 'Unknown error'), 'error');
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            showNotification('Failed to load user details', 'error');
                        });
                }

                // Function to view account detail from user modal
                function viewAccountFromUser(accountId) {
                    // Close user modal first
                    const userModal = bootstrap.Modal.getInstance(document.getElementById('viewUserModal'));
                    if (userModal) {
                        userModal.hide();
                    }

                    // Fetch account details and show in modal
                    setTimeout(() => {
                        fetch('${pageContext.request.contextPath}/employees/accounts/details?id=' + accountId)
                            .then(response => response.json())
                            .then(data => {
                                if (data.success) {
                                    const account = data.account;
                                    // Populate account view modal
                                    document.getElementById('view-account-username').textContent = account.username || '-';
                                    document.getElementById('view-account-emailLogin').textContent = account.emailLogin || '-';
                                    document.getElementById('view-account-userFullName').textContent = account.userFullName || '-';
                                    document.getElementById('view-account-department').textContent = account.departmentName || '-';
                                    document.getElementById('view-account-position').textContent = account.positionName || '-';
                                    document.getElementById('view-account-lastLogin').textContent = account.lastLoginAt || 'Never';
                                    document.getElementById('view-account-createdAt').textContent = account.createdAt || '-';
                                    document.getElementById('view-account-updatedAt').textContent = account.updatedAt || '-';
                                    document.getElementById('view-account-passwordUpdatedAt').textContent = account.passwordUpdatedAt || 'Never';

                                    // Status badge
                                    const statusBadge = document.getElementById('view-account-status');
                                    statusBadge.textContent = account.status || '-';
                                    statusBadge.className = 'status-badge ' + (account.status || '');

                                    // Show account modal
                                    const accountModal = new bootstrap.Modal(document.getElementById('viewAccountModal'));
                                    accountModal.show();
                                } else {
                                    showNotification('Failed to load account details: ' + (data.message || 'Unknown error'), 'error');
                                }
                            })
                            .catch(error => {
                                console.error('Error:', error);
                                showNotification('Failed to load account details', 'error');
                            });
                    }, 300);
                }

                // Auto-submit form on filter change (optional)
                document.addEventListener('DOMContentLoaded', function () {
                    const filterForm = document.getElementById('filterForm');
                    const searchInput = document.getElementById('search');

                    // Optional: Auto-submit on Enter key in search box
                    searchInput.addEventListener('keypress', function (e) {
                        if (e.key === 'Enter') {
                            e.preventDefault();
                            filterForm.submit();
                        }
                    });

                    // Event listeners for action buttons
                    document.querySelectorAll('.btn-view').forEach(btn => {
                        btn.addEventListener('click', function () {
                            const userId = this.getAttribute('data-user-id');
                            viewUser(userId);
                        });
                    });

                    document.querySelectorAll('.btn-edit').forEach(btn => {
                        btn.addEventListener('click', function () {
                            const userId = this.getAttribute('data-user-id');
                            editUser(userId);
                        });
                    });

                    // Event listeners for pagination links
                    document.querySelectorAll('.pagination .page-link[data-page]').forEach(link => {
                        link.addEventListener('click', function (e) {
                            e.preventDefault();
                            const page = this.getAttribute('data-page');
                            if (page && !this.parentElement.classList.contains('disabled')) {
                                goToPage(parseInt(page));
                            }
                        });
                    });

                    // Edit from view modal
                    document.getElementById('btnEditFromView')?.addEventListener('click', function () {
                        const userId = this.getAttribute('data-user-id');
                        // Close view modal
                        const viewModal = bootstrap.Modal.getInstance(document.getElementById('viewUserModal'));
                        viewModal.hide();
                        // Open edit modal
                        setTimeout(() => editUser(userId), 300);
                    });

                    // Edit form submission
                    document.getElementById('editUserForm').addEventListener('submit', function (e) {
                        e.preventDefault();
                        const formData = new FormData(this);

                        // Convert FormData to URLSearchParams for proper form encoding
                        const urlParams = new URLSearchParams();
                        for (let pair of formData.entries()) {
                            urlParams.append(pair[0], pair[1]);
                        }

                        // Debug: Log form data
                        console.log('Form data being sent:');
                        for (let pair of urlParams.entries()) {
                            console.log(pair[0] + ': ' + pair[1]);
                        }

                        fetch('${pageContext.request.contextPath}/employees/users/update', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            body: urlParams.toString()
                        })
                            .then(response => response.json())
                            .then(data => {
                                if (data.success) {
                                    // Close modal
                                    const modal = bootstrap.Modal.getInstance(document.getElementById('editUserModal'));
                                    modal.hide();
                                    // Show success message and reload
                                    showNotification('User updated successfully!', 'success');
                                    setTimeout(() => window.location.reload(), 1500);
                                } else {
                                    showNotification('Failed to update user: ' + (data.message || 'Unknown error'), 'error');
                                }
                            })
                            .catch(error => {
                                console.error('Error:', error);
                                showNotification('Failed to update user', 'error');
                            });
                    });

                });
            </script>
        </body>

        </html>