<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
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

                    .btn-action.btn-lock {
                        background-color: #fd7e14;
                        color: #fff;
                    }

                    .btn-action.btn-lock:hover {
                        background-color: #e8590c;
                    }

                    .btn-action.btn-delete {
                        background-color: #dc3545;
                        color: #fff;
                    }

                    .btn-action.btn-delete:hover {
                        background-color: #c82333;
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
                        flex-wrap: wrap;
                        gap: 1rem;
                    }

                    .pagination-info {
                        color: #6c757d;
                        font-size: 0.95rem;
                    }

                    .pagination {
                        margin: 0;
                    }

                    .pagination .page-link {
                        color: #667eea;
                        border: 1px solid #dee2e6;
                        padding: 0.5rem 0.75rem;
                    }

                    .pagination .page-item.active .page-link {
                        background-color: #667eea;
                        border-color: #667eea;
                    }

                    .pagination .page-link:hover {
                        background-color: #f8f9fa;
                    }

                    .records-per-page {
                        display: flex;
                        align-items: center;
                        gap: 0.5rem;
                    }

                    .records-per-page select {
                        padding: 0.375rem 0.75rem;
                        border: 1px solid #ced4da;
                        border-radius: 6px;
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
                            <nav aria-label="breadcrumb">
                                <ol class="breadcrumb">
                                    <li class="breadcrumb-item"><a
                                            href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                                    <li class="breadcrumb-item">Employee Management</li>
                                    <li class="breadcrumb-item active" aria-current="page">Account List</li>
                                </ol>
                            </nav>
                            <div class="d-flex justify-content-between align-items-center">
                                <h2 class="mb-0">Account Management</h2>
                                <c:if test="${isAdmin}">
                                    <a href="${pageContext.request.contextPath}/employees/accounts/create"
                                        class="btn btn-primary">
                                        <i class="fas fa-plus me-2"></i>Add New Account
                                    </a>
                                </c:if>
                            </div>
                        </div>

                        <!-- Filter Section -->
                        <div class="filter-section">
                            <form method="get" action="${pageContext.request.contextPath}/employees/accounts"
                                id="filterForm">
                                <div class="filter-row">
                                    <div class="filter-group">
                                        <label for="search">Search</label>
                                        <input type="text" id="search" name="search" class="form-control"
                                            placeholder="Username, Email Login, or User Name" value="${param.search}">
                                    </div>
                                    <div class="filter-group">
                                        <label for="status">Status</label>
                                        <select id="status" name="status" class="form-select">
                                            <option value="">All Status</option>
                                            <option value="active" ${param.status=='active' ? 'selected' : '' }>Active
                                            </option>
                                            <option value="inactive" ${param.status=='inactive' ? 'selected' : '' }>
                                                Inactive
                                            </option>
                                            <option value="locked" ${param.status=='locked' ? 'selected' : '' }>Locked
                                            </option>
                                            <option value="suspended" ${param.status=='suspended' ? 'selected' : '' }>
                                                Suspended</option>
                                        </select>
                                    </div>
                                    <div class="filter-group">
                                        <label for="department">Department</label>
                                        <select id="department" name="department" class="form-select">
                                            <option value="">All Departments</option>
                                            <c:forEach var="dept" items="${departments}">
                                                <option value="${dept.id}" ${param.department==dept.id ? 'selected' : ''
                                                    }>
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
                                                        <td>${account.departmentName != null ? account.departmentName :
                                                            '-'}
                                                        </td>
                                                        <td>${account.positionName != null ? account.positionName : '-'}
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
                                                                    data-account-id="${account.id}" data-action="view"
                                                                    title="View Details">
                                                                    <i class="fas fa-eye"></i>
                                                                </button>
                                                                <c:if test="${isAdmin}">
                                                                    <button class="btn-action btn-edit"
                                                                        data-account-id="${account.id}"
                                                                        data-action="edit" title="Edit Account">
                                                                        <i class="fas fa-edit"></i>
                                                                    </button>
                                                                    <c:choose>
                                                                        <c:when test="${account.status == 'locked'}">
                                                                            <button class="btn-action btn-lock"
                                                                                data-account-id="${account.id}"
                                                                                data-username="${fn:escapeXml(account.username)}"
                                                                                data-action="unlock"
                                                                                title="Unlock Account">
                                                                                <i class="fas fa-unlock"></i>
                                                                            </button>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <button class="btn-action btn-lock"
                                                                                data-account-id="${account.id}"
                                                                                data-username="${fn:escapeXml(account.username)}"
                                                                                data-action="lock" title="Lock Account">
                                                                                <i class="fas fa-lock"></i>
                                                                            </button>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                    <button class="btn-action btn-delete"
                                                                        data-account-id="${account.id}"
                                                                        data-username="${fn:escapeXml(account.username)}"
                                                                        data-action="delete" title="Delete Account">
                                                                        <i class="fas fa-trash"></i>
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
                                        <a class="page-link" href="#" data-page="<c:out value='${currentPage - 1}'/>">
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
                                            <c:if test="${pageNum == currentPage - 3 || pageNum == currentPage + 3}">
                                                <li class="page-item disabled">
                                                    <span class="page-link">...</span>
                                                </li>
                                            </c:if>
                                        </c:forEach>

                                        <!-- Next Button -->
                                        <li class="page-item <c:if test=" ${currentPage==totalPages}">disabled</c:if>">
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

                <script>
                    const contextPath = '<c:out value="${pageContext.request.contextPath}"/>';

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

                                switch (action) {
                                    case 'view':
                                        window.location.href = contextPath + '/employees/accounts/' + accountId;
                                        break;
                                    case 'edit':
                                        window.location.href = contextPath + '/employees/accounts/' + accountId + '/edit';
                                        break;
                                    case 'lock':
                                        if (confirm('Are you sure you want to lock account "' + username + '"? The user will not be able to login.')) {
                                            submitAction(accountId, 'lock', 'POST');
                                        }
                                        break;
                                    case 'unlock':
                                        if (confirm('Are you sure you want to unlock account "' + username + '"? The user will be able to login again.')) {
                                            submitAction(accountId, 'unlock', 'POST');
                                        }
                                        break;
                                    case 'delete':
                                        if (confirm('Are you sure you want to delete account "' + username + '"? This action cannot be undone.')) {
                                            submitAction(accountId, 'delete', 'DELETE');
                                        }
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
                    });

                    function submitAction(accountId, action, method) {
                        const form = document.createElement('form');
                        form.method = 'POST';
                        form.action = contextPath + '/employees/accounts/' + accountId + '/' + action;

                        const methodInput = document.createElement('input');
                        methodInput.type = 'hidden';
                        methodInput.name = '_method';
                        methodInput.value = method;
                        form.appendChild(methodInput);

                        document.body.appendChild(form);
                        form.submit();
                    }
                </script>
            </body>

            </html>