<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
            <%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <jsp:include page="../layout/head.jsp">
                        <jsp:param name="pageTitle" value="Account Management" />
                        <jsp:param name="pageCss" value="dashboard.css" />
                    </jsp:include>
                    <style>
                        /* Account List Specific Styles */
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

                        .btn-action.btn-reset-password {
                            background-color: #6c757d;
                            color: #fff;
                        }

                        .btn-action.btn-reset-password:hover {
                            background-color: #5a6268;
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
                            order: 2;
                        }

                        .pagination {
                            margin: 0;
                            flex: 1 1 auto;
                            display: flex;
                            justify-content: center;
                            order: 1;
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

                        /* User card hover effect */
                        .create-account-card {
                            cursor: pointer;
                            transition: all 0.3s ease;
                            border: 2px solid #667eea !important;
                            background: #fff;
                        }

                        .create-account-card:hover {
                            transform: translateY(-3px);
                            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
                            border-color: #764ba2 !important;
                            background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
                        }

                        .create-account-card:active {
                            transform: translateY(-1px);
                            box-shadow: 0 3px 10px rgba(102, 126, 234, 0.3);
                        }

                        .create-account-card .card-body {
                            pointer-events: none;
                            /* Prevent child elements from interfering with card click */
                        }

                        .alert-info {
                            background-color: #e7f3ff;
                            border-color: #b3d9ff;
                            color: #004085;
                        }

                        .clickable-header {
                            user-select: none;
                            transition: background-color 0.2s ease;
                            padding: 0.5rem;
                            margin: -0.5rem;
                            border-radius: 6px;
                        }

                        .clickable-header:hover {
                            background-color: rgba(0, 0, 0, 0.05);
                        }

                        .clickable-header .toggle-icon {
                            transition: transform 0.3s ease;
                            font-size: 1.2rem;
                            color: #667eea;
                        }

                        .clickable-header[aria-expanded="true"] .toggle-icon {
                            transform: rotate(180deg);
                        }

                        /* Responsive Design */
                        @media (max-width: 1023px) {
                            .table .last-login-col {
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
                    </style>
                </head>

                <body>
                    <!-- Sidebar -->
                    <jsp:include page="../layout/sidebar.jsp">
                        <jsp:param name="currentPage" value="account-list" />
                    </jsp:include>

                    <!-- Main Content -->
                    <div class="main-content" id="main-content">
                        <!-- Header -->
                        <jsp:include page="../layout/dashboard-header.jsp" />

                        <!-- Content Area -->
                        <div class="content-area">
                            <!-- Page Header -->
                            <div class="page-header">

                                <div class="d-flex justify-content-between align-items-center">
                                    <h2 class="mb-0">Account Management</h2>
                                    <c:if test="${canCreateAccount}">
                                        <a href="${pageContext.request.contextPath}/employees/accounts/create"
                                            class="btn btn-primary">
                                            <i class="fas fa-plus me-2"></i>Add New Account
                                        </a>
                                    </c:if>
                                </div>
                            </div>

                            <!-- Success/Error Messages -->
                            <c:if test="${not empty sessionScope.successMessage}">
                                <div class="alert alert-success alert-dismissible fade show" role="alert">
                                    <i class="fas fa-check-circle me-2"></i>${sessionScope.successMessage}
                                    <button type="button" class="btn-close" data-bs-dismiss="alert"
                                        aria-label="Close"></button>
                                </div>
                                <c:remove var="successMessage" scope="session" />
                            </c:if>

                            <c:if test="${not empty sessionScope.errorMessage}">
                                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                    <i class="fas fa-exclamation-circle me-2"></i>${sessionScope.errorMessage}
                                    <button type="button" class="btn-close" data-bs-dismiss="alert"
                                        aria-label="Close"></button>
                                </div>
                                <c:remove var="errorMessage" scope="session" />
                            </c:if>

                            <!-- Users Without Account Card -->
                            <c:if test="${canCreateAccount && not empty usersWithoutAccount}">
                                <div class="alert alert-info" role="alert">
                                    <div class="clickable-header" style="cursor: pointer;" data-bs-toggle="collapse"
                                        data-bs-target="#usersWithoutAccountCollapse" aria-expanded="false"
                                        aria-controls="usersWithoutAccountCollapse">
                                        <div class="d-flex justify-content-between align-items-center mb-2">
                                            <h5 class="mb-0">
                                                <i class="fas fa-user-plus me-2"></i>Users Without Account
                                                <span class="badge bg-primary">${fn:length(usersWithoutAccount)}</span>
                                            </h5>
                                            <i class="fas fa-chevron-down toggle-icon"></i>
                                        </div>
                                        <p class="mb-0 small">The following users don't have accounts yet. Click on a
                                            user
                                            to create an account.</p>
                                    </div>
                                    <div class="collapse mt-2" id="usersWithoutAccountCollapse">
                                        <div class="row g-2">
                                            <c:forEach var="user" items="${usersWithoutAccount}" varStatus="status">
                                                <c:if test="${status.index < 10}">
                                                    <div class="col-md-6 col-lg-4">
                                                        <div class="card create-account-card" data-user-id="${user.id}"
                                                            data-user-name="${fn:escapeXml(user.fullName)}"
                                                            data-employee-code="${fn:escapeXml(user.employeeCode)}"
                                                            data-email-company="${fn:escapeXml(empty user.emailCompany ? '' : user.emailCompany)}">
                                                            <div class="card-body p-2">
                                                                <div class="d-flex align-items-center">
                                                                    <div class="flex-shrink-0">
                                                                        <i
                                                                            class="fas fa-user-circle fa-2x text-primary"></i>
                                                                    </div>
                                                                    <div class="flex-grow-1 ms-2">
                                                                        <h6 class="mb-0">${user.fullName}</h6>
                                                                        <small
                                                                            class="text-muted">${user.employeeCode}</small>
                                                                        <c:if test="${not empty user.emailCompany}">
                                                                            <br><small
                                                                                class="text-muted">${user.emailCompany}</small>
                                                                        </c:if>
                                                                    </div>
                                                                    <div class="flex-shrink-0">
                                                                        <i class="fas fa-plus-circle text-success"></i>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:if>
                                            </c:forEach>
                                        </div>
                                        <c:if test="${fn:length(usersWithoutAccount) > 10}">
                                            <div class="text-center mt-2">
                                                <small class="text-muted">Showing 10 of
                                                    ${fn:length(usersWithoutAccount)} users.
                                                    <a
                                                        href="${pageContext.request.contextPath}/employees/accounts/create">View
                                                        all</a>
                                                </small>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </c:if>

                            <!-- Filter Section -->
                            <div class="filter-section">
                                <form method="get" action="${pageContext.request.contextPath}/employees/accounts"
                                    id="filterForm">
                                    <div class="filter-row">
                                        <div class="filter-group">
                                            <label for="search">Search</label>
                                            <input type="text" id="search" name="search" class="form-control"
                                                placeholder="Username, Email Login, or User Name"
                                                value="${param.search}">
                                        </div>
                                        <div class="filter-group">
                                            <label for="status">Status</label>
                                            <select id="status" name="status" class="form-select">
                                                <option value="">All Status</option>
                                                <option value="active" ${param.status=='active' ? 'selected' : '' }>
                                                    Active
                                                </option>
                                                <option value="inactive" ${param.status=='inactive' ? 'selected' : '' }>
                                                    Inactive
                                                </option>
                                                <option value="locked" ${param.status=='locked' ? 'selected' : '' }>
                                                    Locked
                                                </option>
                                                <option value="suspended" ${param.status=='suspended' ? 'selected' : ''
                                                    }>
                                                    Suspended</option>
                                            </select>
                                        </div>
                                        <div class="filter-group">
                                            <label for="department">Department</label>
                                            <select id="department" name="department" class="form-select">
                                                <option value="">All Departments</option>
                                                <c:forEach var="dept" items="${departments}">
                                                    <option value="${dept.id}" ${param.department==dept.id ? 'selected'
                                                        : '' }>
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
                                                    <option value="${pos.id}" ${param.position==pos.id ? 'selected' : ''
                                                        }>
                                                        ${pos.name}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="filter-actions">
                                            <button type="submit" class="btn btn-primary">
                                                <i class="fas fa-search me-1"></i>Filter
                                            </button>
                                            <a href="${pageContext.request.contextPath}/employees/accounts"
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

                            <!-- Account List Table -->
                            <div class="table-card">
                                <div class="card-header">
                                    <h5 class="mb-0"><i class="fas fa-user-shield me-2"></i>Account List</h5>
                                </div>

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
                                    <c:when test="${empty accounts}">
                                        <!-- Empty State -->
                                        <div class="empty-state">
                                            <i class="fas fa-inbox"></i>
                                            <p>No accounts found</p>
                                            <c:if
                                                test="${not empty param.search or not empty param.status or not empty param.department or not empty param.position}">
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
                                                        <th>Username</th>
                                                        <th>Email Login</th>
                                                        <th>User (Full Name)</th>
                                                        <th>Department</th>
                                                        <th>Position</th>
                                                        <th>Status</th>
                                                        <th class="last-login-col">Last Login</th>
                                                        <th>Actions</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="account" items="${accounts}">
                                                        <tr>
                                                            <td>${account.username}</td>
                                                            <td>${account.emailLogin != null ? account.emailLogin : '-'}
                                                            </td>
                                                            <td>${account.userFullName}</td>
                                                            <td>${account.departmentName != null ?
                                                                account.departmentName :
                                                                '-'}
                                                            </td>
                                                            <td>${account.positionName != null ? account.positionName :
                                                                '-'}
                                                            </td>
                                                            <td>
                                                                <span class="status-badge ${account.status}">
                                                                    ${account.status}
                                                                </span>
                                                            </td>
                                                            <td class="last-login-col">
                                                                <c:choose>
                                                                    <c:when test="${account.lastLoginAt != null}">
                                                                        ${account.lastLoginAt}
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        Never
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>
                                                                <div class="action-buttons">
                                                                    <button class="btn-action btn-view"
                                                                        data-account-id="${account.id}"
                                                                        data-action="view" title="View Details">
                                                                        <i class="fas fa-eye"></i>
                                                                    </button>
                                                                    <c:if test="${canCreateAccount}">
                                                                        <button class="btn-action btn-edit"
                                                                            data-account-id="${account.id}"
                                                                            data-action="edit" title="Edit Account">
                                                                            <i class="fas fa-edit"></i>
                                                                        </button>
                                                                    </c:if>
                                                                    <c:if test="${canResetPassword}">
                                                                        <button class="btn-action btn-reset-password"
                                                                            data-account-id="${account.id}"
                                                                            data-username="${fn:escapeXml(account.username)}"
                                                                            data-action="reset-password"
                                                                            title="Reset Password"
                                                                            style="background-color: #6c757d; color: #fff;">
                                                                            <i class="fas fa-key"></i>
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
                                                ${currentPage * pageSize > totalRecords ? totalRecords : currentPage *
                                                pageSize}
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
                                                        <li class="page-item <c:if test=" ${currentPage==1}">disabled
                                            </c:if>">
                                            <a class="page-link" href="#"
                                                data-page="<c:out value='${currentPage - 1}'/>">
                                                <i class="fas fa-chevron-left"></i>
                                            </a>
                                            </li>

                                            <!-- Page Numbers -->
                                            <c:forEach begin="1" end="${totalPages}" var="pageNum">
                                                <c:if
                                                    test="${pageNum == 1 || pageNum == totalPages || (pageNum >= currentPage - 2 && pageNum <= currentPage + 2)}">
                                                    <li class="page-item <c:if test=" ${pageNum==currentPage}">active
                                                </c:if>">
                                                <a class="page-link" href="#" data-page="<c:out value='${pageNum}'/>">
                                                    <c:out value="${pageNum}" />
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
                                            <li class="page-item <c:if test=" ${currentPage==totalPages}">disabled
                                                </c:if>">
                                                <a class="page-link" href="#"
                                                    data-page="<c:out value='${currentPage + 1}'/>">
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

                    <!-- View Account Modal -->
                    <div class="modal fade" id="viewAccountModal" tabindex="-1" aria-labelledby="viewAccountModalLabel"
                        aria-hidden="true">
                        <div class="modal-dialog modal-lg modal-dialog-centered">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="viewAccountModalLabel">
                                        <i class="fas fa-user-shield me-2"></i>Account Details
                                    </h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="row g-3">
                                        <div class="col-md-6">
                                            <label class="form-label fw-bold">Username</label>
                                            <p class="form-control-plaintext" id="view-username">-</p>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label fw-bold">Status</label>
                                            <p class="form-control-plaintext">
                                                <span class="status-badge" id="view-status">-</span>
                                            </p>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label fw-bold">Email Login</label>
                                            <p class="form-control-plaintext" id="view-emailLogin">-</p>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label fw-bold">User (Full Name)</label>
                                            <p class="form-control-plaintext" id="view-userFullName">-</p>
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
                                            <label class="form-label fw-bold">Last Login</label>
                                            <p class="form-control-plaintext" id="view-lastLogin">-</p>
                                        </div>
                                        <div class="col-12">
                                            <hr class="my-3">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label fw-bold">
                                                <i class="fas fa-calendar-plus me-1"></i>Created At
                                            </label>
                                            <p class="form-control-plaintext" id="view-createdAt">-</p>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label fw-bold">
                                                <i class="fas fa-calendar-check me-1"></i>Updated At
                                            </label>
                                            <p class="form-control-plaintext" id="view-updatedAt">-</p>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label fw-bold">
                                                <i class="fas fa-key me-1"></i>Password Updated At
                                            </label>
                                            <p class="form-control-plaintext" id="view-passwordUpdatedAt">-</p>
                                        </div>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                        <i class="fas fa-times me-2"></i>Close
                                    </button>
                                    <c:if test="${isAdmin}">
                                        <button type="button" class="btn btn-primary" id="btnEditFromView">
                                            <i class="fas fa-edit me-2"></i>Edit Account
                                        </button>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Edit Account Modal -->
                    <div class="modal fade" id="editAccountModal" tabindex="-1" aria-labelledby="editAccountModalLabel"
                        aria-hidden="true">
                        <div class="modal-dialog modal-lg modal-dialog-centered">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="editAccountModalLabel">
                                        <i class="fas fa-edit me-2"></i>Edit Account
                                    </h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                                </div>
                                <form id="editAccountForm" method="post">
                                    <input type="hidden" id="edit-accountId" name="accountId">
                                    <div class="modal-body">
                                        <!-- Username (Read-only) -->
                                        <div class="mb-3">
                                            <label class="form-label fw-bold">Username</label>
                                            <input type="text" class="form-control" id="edit-username" readonly>
                                        </div>

                                        <!-- Email Login -->
                                        <div class="mb-3">
                                            <label for="edit-emailLogin" class="form-label">
                                                Email Login<span class="text-danger">*</span>
                                            </label>
                                            <input type="email" class="form-control" id="edit-emailLogin"
                                                name="emailLogin" required>
                                        </div>

                                        <!-- Note about permissions -->
                                        <div class="mb-3">
                                            <div class="alert alert-info">
                                                <i class="fas fa-info-circle me-2"></i>
                                                <strong>Note:</strong> Account permissions are determined by the user's
                                                position.
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
                                                <option value="locked">Locked</option>
                                                <option value="suspended">Suspended</option>
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

                    <!-- Reset Password Modal -->
                    <div class="modal fade" id="resetPasswordModal" tabindex="-1"
                        aria-labelledby="resetPasswordModalLabel" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="resetPasswordModalLabel">
                                        <i class="fas fa-key me-2"></i>Reset Password
                                    </h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                                </div>
                                <form id="resetPasswordForm">
                                    <input type="hidden" id="reset-accountId" name="accountId">
                                    <div class="modal-body">
                                        <div class="alert alert-warning">
                                            <i class="fas fa-exclamation-triangle me-2"></i>
                                            You are about to reset the password for account: <strong
                                                id="reset-username"></strong>
                                        </div>

                                        <!-- New Password -->
                                        <div class="mb-3">
                                            <label for="reset-newPassword" class="form-label">
                                                New Password<span class="text-danger">*</span>
                                            </label>
                                            <input type="password" class="form-control" id="reset-newPassword"
                                                name="newPassword" required placeholder="Enter new password">
                                        </div>

                                        <!-- Confirm Password -->
                                        <div class="mb-3">
                                            <label for="reset-confirmPassword" class="form-label">
                                                Confirm Password<span class="text-danger">*</span>
                                            </label>
                                            <input type="password" class="form-control" id="reset-confirmPassword"
                                                required placeholder="Confirm new password">
                                            <div class="invalid-feedback">
                                                Passwords do not match
                                            </div>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                            <i class="fas fa-times me-2"></i>Cancel
                                        </button>
                                        <button type="submit" class="btn btn-primary">
                                            <i class="fas fa-key me-2"></i>Reset Password
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <!-- Success Modal -->
                    <div class="modal fade" id="successModal" tabindex="-1" aria-labelledby="successModalLabel"
                        aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered">
                            <div class="modal-content">
                                <div class="modal-header border-0 pb-0">
                                    <h5 class="modal-title text-success" id="successModalLabel">
                                        <i class="fas fa-check-circle me-2"></i>Success
                                    </h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                                </div>
                                <div class="modal-body pt-2" id="successModalBody">
                                    Operation completed successfully!
                                </div>
                                <div class="modal-footer border-0">
                                    <button type="button" class="btn btn-success" data-bs-dismiss="modal">
                                        <i class="fas fa-check me-1"></i>OK
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Error Modal -->
                    <div class="modal fade" id="errorModal" tabindex="-1" aria-labelledby="errorModalLabel"
                        aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered">
                            <div class="modal-content">
                                <div class="modal-header border-0 pb-0">
                                    <h5 class="modal-title text-danger" id="errorModalLabel">
                                        <i class="fas fa-exclamation-circle me-2"></i>Error
                                    </h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                                </div>
                                <div class="modal-body pt-2" id="errorModalBody">
                                    An error occurred. Please try again.
                                </div>
                                <div class="modal-footer border-0">
                                    <button type="button" class="btn btn-danger" data-bs-dismiss="modal">
                                        <i class="fas fa-times me-1"></i>Close
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <script>
                        const contextPath = '<c:out value="${pageContext.request.contextPath}"/>';

                        // Helper function to show success modal
                        function showSuccessModal(message, reloadOnClose = true) {
                            document.getElementById('successModalBody').textContent = message;
                            const successModal = new bootstrap.Modal(document.getElementById('successModal'));
                            successModal.show();

                            if (reloadOnClose) {
                                document.getElementById('successModal').addEventListener('hidden.bs.modal', function () {
                                    window.location.reload();
                                }, { once: true });
                            }
                        }

                        // Helper function to show error modal
                        function showErrorModal(message) {
                            document.getElementById('errorModalBody').textContent = message;
                            const errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
                            errorModal.show();
                        }

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
                            pageInput.value = 1;
                            form.submit();
                        }

                        function createAccountForUser(userId, fullName, employeeCode, emailCompany) {
                            // Redirect to create account page with pre-filled user
                            window.location.href = contextPath + '/employees/accounts/create?userId=' + userId;
                        }

                        // Handle click on create account cards
                        document.addEventListener('click', function (e) {
                            const card = e.target.closest('.create-account-card');
                            if (card) {
                                const userId = card.dataset.userId;
                                const userName = card.dataset.userName;
                                const employeeCode = card.dataset.employeeCode;
                                const emailCompany = card.dataset.emailCompany;
                                createAccountForUser(userId, userName, employeeCode, emailCompany);
                            }
                        });

                        function viewAccount(accountId) {
                            // Fetch account details via AJAX
                            fetch(contextPath + '/employees/accounts/details?id=' + accountId)
                                .then(response => response.json())
                                .then(data => {
                                    if (data.success) {
                                        const account = data.account;
                                        // Populate view modal
                                        document.getElementById('view-username').textContent = account.username || '-';
                                        document.getElementById('view-emailLogin').textContent = account.emailLogin || '-';
                                        document.getElementById('view-userFullName').textContent = account.userFullName || '-';
                                        document.getElementById('view-department').textContent = account.departmentName || '-';
                                        document.getElementById('view-position').textContent = account.positionName || '-';
                                        document.getElementById('view-lastLogin').textContent = account.lastLoginAt || 'Never';

                                        // Timestamps
                                        document.getElementById('view-createdAt').textContent = account.createdAt || '-';
                                        document.getElementById('view-updatedAt').textContent = account.updatedAt || '-';
                                        document.getElementById('view-passwordUpdatedAt').textContent = account.passwordUpdatedAt || 'Never';

                                        // Status badge
                                        const statusBadge = document.getElementById('view-status');
                                        statusBadge.textContent = account.status || '-';
                                        statusBadge.className = 'status-badge ' + (account.status || '');

                                        // Store accountId for edit button
                                        document.getElementById('btnEditFromView').setAttribute('data-account-id', accountId);

                                        // Show modal
                                        const modal = new bootstrap.Modal(document.getElementById('viewAccountModal'));
                                        modal.show();
                                    } else {
                                        showErrorModal('Failed to load account details: ' + (data.message || 'Unknown error'));
                                    }
                                })
                                .catch(error => {
                                    console.error('Error:', error);
                                    showErrorModal('Failed to load account details');
                                });
                        }

                        function resetPassword(accountId, username) {
                            // Populate reset password modal
                            document.getElementById('reset-accountId').value = accountId;
                            document.getElementById('reset-username').textContent = username;
                            document.getElementById('reset-newPassword').value = '';
                            document.getElementById('reset-confirmPassword').value = '';
                            document.getElementById('reset-confirmPassword').classList.remove('is-invalid');

                            // Show modal
                            const modal = new bootstrap.Modal(document.getElementById('resetPasswordModal'));
                            modal.show();
                        }

                        function editAccount(accountId) {
                            // Fetch account details via AJAX
                            fetch(contextPath + '/employees/accounts/details?id=' + accountId)
                                .then(response => response.json())
                                .then(data => {
                                    if (data.success) {
                                        const account = data.account;
                                        // Populate edit modal
                                        document.getElementById('edit-accountId').value = account.id;
                                        document.getElementById('edit-username').value = account.username || '';
                                        document.getElementById('edit-emailLogin').value = account.emailLogin || '';
                                        document.getElementById('edit-status').value = account.status || 'active';

                                        // Show modal
                                        const modal = new bootstrap.Modal(document.getElementById('editAccountModal'));
                                        modal.show();
                                    } else {
                                        showErrorModal('Failed to load account details: ' + (data.message || 'Unknown error'));
                                    }
                                })
                                .catch(error => {
                                    console.error('Error:', error);
                                    showErrorModal('Failed to load account details');
                                });
                        }

                        document.addEventListener('DOMContentLoaded', function () {
                            const filterForm = document.getElementById('filterForm');
                            const searchInput = document.getElementById('search');

                            if (searchInput) {
                                searchInput.addEventListener('keypress', function (e) {
                                    if (e.key === 'Enter') {
                                        e.preventDefault();
                                        filterForm.submit();
                                    }
                                });
                            }

                            document.addEventListener('click', function (e) {
                                const button = e.target.closest('.btn-action');
                                if (button) {
                                    const accountId = button.dataset.accountId;
                                    const action = button.dataset.action;
                                    const username = button.dataset.username;
                                    const currentStatus = button.dataset.currentStatus;

                                    switch (action) {
                                        case 'view':
                                            viewAccount(accountId);
                                            break;
                                        case 'edit':
                                            editAccount(accountId);
                                            break;
                                        case 'reset-password':
                                            resetPassword(accountId, username);
                                            break;
                                    }
                                    return;
                                }

                                const pageLink = e.target.closest('.page-link');
                                if (pageLink && pageLink.dataset.page) {
                                    e.preventDefault();
                                    const page = parseInt(pageLink.dataset.page);
                                    if (!isNaN(page) && page > 0) {
                                        goToPage(page);
                                    }
                                }
                            });

                            // Edit from view modal
                            document.getElementById('btnEditFromView')?.addEventListener('click', function () {
                                const accountId = this.getAttribute('data-account-id');
                                // Close view modal
                                const viewModal = bootstrap.Modal.getInstance(document.getElementById('viewAccountModal'));
                                viewModal.hide();
                                // Open edit modal
                                setTimeout(() => editAccount(accountId), 300);
                            });

                            // Edit form submission
                            document.getElementById('editAccountForm').addEventListener('submit', function (e) {
                                e.preventDefault();
                                const formData = new FormData(this);

                                // Convert FormData to URLSearchParams
                                const urlParams = new URLSearchParams();
                                for (let pair of formData.entries()) {
                                    urlParams.append(pair[0], pair[1]);
                                }

                                fetch(contextPath + '/employees/accounts/update', {
                                    method: 'POST',
                                    headers: {
                                        'Content-Type': 'application/x-www-form-urlencoded'
                                    },
                                    body: urlParams.toString()
                                })
                                    .then(response => response.json())
                                    .then(data => {
                                        if (data.success) {
                                            // Close edit modal
                                            const modal = bootstrap.Modal.getInstance(document.getElementById('editAccountModal'));
                                            modal.hide();
                                            // Show success modal and reload
                                            showSuccessModal('Account updated successfully!');
                                        } else {
                                            // Close edit modal first
                                            const modal = bootstrap.Modal.getInstance(document.getElementById('editAccountModal'));
                                            modal.hide();
                                            // Show error modal
                                            setTimeout(() => {
                                                showErrorModal(data.message || 'Failed to update account');
                                            }, 300);
                                        }
                                    })
                                    .catch(error => {
                                        console.error('Error:', error);
                                        // Close edit modal first
                                        const modal = bootstrap.Modal.getInstance(document.getElementById('editAccountModal'));
                                        modal.hide();
                                        // Show error modal
                                        setTimeout(() => {
                                            showErrorModal('Failed to update account. Please try again.');
                                        }, 300);
                                    });
                            });

                            // Reset password form submission
                            document.getElementById('resetPasswordForm').addEventListener('submit', function (e) {
                                e.preventDefault();

                                const newPassword = document.getElementById('reset-newPassword').value;
                                const confirmPassword = document.getElementById('reset-confirmPassword').value;
                                const confirmInput = document.getElementById('reset-confirmPassword');

                                // Validate passwords match
                                if (newPassword !== confirmPassword) {
                                    confirmInput.classList.add('is-invalid');
                                    return;
                                }
                                confirmInput.classList.remove('is-invalid');

                                const formData = new FormData(this);
                                const urlParams = new URLSearchParams();
                                for (let pair of formData.entries()) {
                                    urlParams.append(pair[0], pair[1]);
                                }

                                fetch(contextPath + '/employees/accounts/reset-password', {
                                    method: 'POST',
                                    headers: {
                                        'Content-Type': 'application/x-www-form-urlencoded'
                                    },
                                    body: urlParams.toString()
                                })
                                    .then(response => response.json())
                                    .then(data => {
                                        if (data.success) {
                                            // Close reset password modal
                                            const resetModal = bootstrap.Modal.getInstance(document.getElementById('resetPasswordModal'));
                                            resetModal.hide();
                                            // Show success modal and reload
                                            showSuccessModal('Password reset successfully!');
                                        } else {
                                            // Close reset password modal first
                                            const resetModal = bootstrap.Modal.getInstance(document.getElementById('resetPasswordModal'));
                                            resetModal.hide();
                                            // Show error modal
                                            setTimeout(() => {
                                                showErrorModal(data.message || 'Failed to reset password');
                                            }, 300);
                                        }
                                    })
                                    .catch(error => {
                                        console.error('Error:', error);
                                        // Close reset password modal first
                                        const resetModal = bootstrap.Modal.getInstance(document.getElementById('resetPasswordModal'));
                                        resetModal.hide();
                                        // Show error modal
                                        setTimeout(() => {
                                            showErrorModal('Failed to reset password. Please try again.');
                                        }, 300);
                                    });
                            });
                        });
                    </script>
                </body>

                </html>