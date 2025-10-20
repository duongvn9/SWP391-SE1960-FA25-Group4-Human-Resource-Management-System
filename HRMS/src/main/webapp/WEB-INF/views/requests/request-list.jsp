<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <!-- CSS riêng của trang -->
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="Request List - HRMS" />
        <jsp:param name="pageCss" value="request-list.css" />
    </jsp:include>
</head>

<body>
    <!-- Sidebar -->
    <jsp:include page="../layout/sidebar.jsp">
        <jsp:param name="currentPage" value="request-list" />
    </jsp:include>

    <!-- Main Content -->
    <div class="main-content" id="main-content">
        <!-- Header -->
        <jsp:include page="../layout/dashboard-header.jsp" />

        <!-- Content Area -->
        <div class="content-area">
            <!-- Breadcrumb Navigation -->
            <nav aria-label="breadcrumb" class="mb-3">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/dashboard">
                            <i class="fas fa-home"></i> Home
                        </a>
                    </li>
                    <li class="breadcrumb-item active" aria-current="page">
                        <i class="fas fa-clipboard-list"></i> Requests
                    </li>
                </ol>
            </nav>

            <!-- Page Title -->
            <div class="page-head d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="page-title"><i class="fas fa-clipboard-list me-2"></i>Request List</h2>
                    <p class="page-subtitle">View and manage your requests</p>
                </div>
                <div class="d-flex gap-2">
                    <!-- Export to Excel Button (HR only) -->
                    <c:if test="${canExport}">
                        <a href="${pageContext.request.contextPath}/requests/export?scope=${filter.scope}&type=${filter.requestTypeId}&status=${filter.status}&fromDate=${filter.fromDate}&toDate=${filter.toDate}&employeeId=${filter.employeeId}&search=${filter.searchKeyword}&format=excel" 
                           class="btn btn-success"
                           download>
                            <i class="fas fa-file-excel me-1"></i> Export Excel
                        </a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/requests/leave/create" class="btn btn-primary">
                        <i class="fas fa-plus me-1"></i> New Request
                    </a>
                </div>
            </div>

            <!-- Alerts -->
            <!-- Check for error in request scope first, then session scope -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i>
                    <c:out value="${error}" />
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            <c:if test="${empty error and not empty sessionScope.error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i>
                    <c:out value="${sessionScope.error}" />
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <c:remove var="error" scope="session" />
            </c:if>

            <!-- Check for success in request scope first, then session scope -->
            <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle me-2"></i>
                    <c:out value="${success}" />
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            <c:if test="${empty success and not empty sessionScope.success}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle me-2"></i>
                    <c:out value="${sessionScope.success}" />
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <c:remove var="success" scope="session" />
            </c:if>

            <!-- Statistics Cards (for managers and HR) -->
            <c:if test="${filter.scope != 'my'}">
                <div class="stats-container">
                    <div class="stat-card">
                        <div class="stat-card-header">
                            <div class="stat-icon primary">
                                <i class="fas fa-clipboard-list"></i>
                            </div>
                        </div>
                        <div class="stat-value">${result.pagination.totalItems}</div>
                        <div class="stat-label">Total Requests</div>
                    </div>

                    <div class="stat-card">
                        <div class="stat-card-header">
                            <div class="stat-icon warning">
                                <i class="fas fa-clock"></i>
                            </div>
                        </div>
                        <div class="stat-value">
                            <c:set var="pendingCount" value="0" />
                            <c:choose>
                                <c:when test="${result.groupedByDepartment}">
                                    <c:forEach items="${result.requestsByDepartment}" var="deptEntry">
                                        <c:forEach items="${deptEntry.value}" var="req">
                                            <c:if test="${req.status == 'PENDING'}">
                                                <c:set var="pendingCount" value="${pendingCount + 1}" />
                                            </c:if>
                                        </c:forEach>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${result.requests}" var="req">
                                        <c:if test="${req.status == 'PENDING'}">
                                            <c:set var="pendingCount" value="${pendingCount + 1}" />
                                        </c:if>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                            ${pendingCount}
                        </div>
                        <div class="stat-label">Pending</div>
                    </div>

                    <div class="stat-card">
                        <div class="stat-card-header">
                            <div class="stat-icon success">
                                <i class="fas fa-check-circle"></i>
                            </div>
                        </div>
                        <div class="stat-value">
                            <c:set var="approvedCount" value="0" />
                            <c:choose>
                                <c:when test="${result.groupedByDepartment}">
                                    <c:forEach items="${result.requestsByDepartment}" var="deptEntry">
                                        <c:forEach items="${deptEntry.value}" var="req">
                                            <c:if test="${req.status == 'APPROVED'}">
                                                <c:set var="approvedCount" value="${approvedCount + 1}" />
                                            </c:if>
                                        </c:forEach>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${result.requests}" var="req">
                                        <c:if test="${req.status == 'APPROVED'}">
                                            <c:set var="approvedCount" value="${approvedCount + 1}" />
                                        </c:if>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                            ${approvedCount}
                        </div>
                        <div class="stat-label">Approved</div>
                    </div>

                    <div class="stat-card">
                        <div class="stat-card-header">
                            <div class="stat-icon danger">
                                <i class="fas fa-times-circle"></i>
                            </div>
                        </div>
                        <div class="stat-value">
                            <c:set var="rejectedCount" value="0" />
                            <c:choose>
                                <c:when test="${result.groupedByDepartment}">
                                    <c:forEach items="${result.requestsByDepartment}" var="deptEntry">
                                        <c:forEach items="${deptEntry.value}" var="req">
                                            <c:if test="${req.status == 'REJECTED'}">
                                                <c:set var="rejectedCount" value="${rejectedCount + 1}" />
                                            </c:if>
                                        </c:forEach>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${result.requests}" var="req">
                                        <c:if test="${req.status == 'REJECTED'}">
                                            <c:set var="rejectedCount" value="${rejectedCount + 1}" />
                                        </c:if>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                            ${rejectedCount}
                        </div>
                        <div class="stat-label">Rejected</div>
                    </div>
                </div>
            </c:if>

            <!-- Filter Section -->
            <div class="card filter-section mb-4">
                <div class="card-header bg-light" data-bs-toggle="collapse" data-bs-target="#filterCollapse">
                    <h5 class="mb-0">
                        <i class="fas fa-filter me-2"></i>Filters
                        <c:set var="activeFilters" value="0" />
                        <c:if test="${filter.status != null && filter.status != 'all'}">
                            <c:set var="activeFilters" value="${activeFilters + 1}" />
                        </c:if>
                        <c:if test="${filter.requestTypeId != null && filter.requestTypeId > 0}">
                            <c:set var="activeFilters" value="${activeFilters + 1}" />
                        </c:if>
                        <c:if test="${filter.fromDate != null}">
                            <c:set var="activeFilters" value="${activeFilters + 1}" />
                        </c:if>
                        <c:if test="${filter.toDate != null}">
                            <c:set var="activeFilters" value="${activeFilters + 1}" />
                        </c:if>
                        <c:if test="${filter.employeeId != null}">
                            <c:set var="activeFilters" value="${activeFilters + 1}" />
                        </c:if>
                        <c:if test="${filter.searchKeyword != null && filter.searchKeyword != ''}">
                            <c:set var="activeFilters" value="${activeFilters + 1}" />
                        </c:if>
                        <c:if test="${activeFilters > 0}">
                            <span class="filter-badge">${activeFilters} active</span>
                        </c:if>
                    </h5>
                    <button class="filter-toggle" type="button">
                        <i class="fas fa-chevron-down"></i>
                    </button>
                </div>
                <div class="card-body collapse show" id="filterCollapse">
                    <form method="GET" action="${pageContext.request.contextPath}/requests" id="filterForm">
                        <div class="row g-3">
                            <!-- Scope Filter -->
                            <div class="col-md-3">
                                <label for="scope" class="form-label">
                                    <i class="fas fa-eye"></i> Scope
                                </label>
                                <select name="scope" id="scope" class="form-select">
                                    <c:forEach items="${availableScopes}" var="scopeOption">
                                        <option value="${scopeOption}" ${filter.scope == scopeOption ? 'selected' : ''}>
                                            <c:choose>
                                                <c:when test="${scopeOption == 'my'}">My Requests</c:when>
                                                <c:when test="${scopeOption == 'subordinate'}">Subordinate Requests</c:when>
                                                <c:when test="${scopeOption == 'all'}">All Requests</c:when>
                                            </c:choose>
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <!-- Type Filter -->
                            <div class="col-md-3">
                                <label for="type" class="form-label">
                                    <i class="fas fa-list"></i> Request Type
                                </label>
                                <select name="type" id="type" class="form-select">
                                    <option value="all" ${filter.requestTypeId == null || filter.requestTypeId == 0 ? 'selected' : ''}>All Types</option>
                                    <c:forEach items="${requestTypes}" var="type">
                                        <option value="${type.id}" ${filter.requestTypeId == type.id ? 'selected' : ''}>
                                            ${type.name}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <!-- Status Filter -->
                            <div class="col-md-3">
                                <label for="status" class="form-label">
                                    <i class="fas fa-info-circle"></i> Status
                                </label>
                                <select name="status" id="status" class="form-select">
                                    <option value="all" ${filter.status == 'all' || filter.status == null ? 'selected' : ''}>All</option>
                                    <option value="PENDING" ${filter.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                    <option value="APPROVED" ${filter.status == 'APPROVED' ? 'selected' : ''}>Approved</option>
                                    <option value="REJECTED" ${filter.status == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                                </select>
                            </div>

                            <!-- Employee Filter (Manager/HR only) -->
                            <c:if test="${not empty employees}">
                                <div class="col-md-3">
                                    <label for="employeeId" class="form-label">
                                        <i class="fas fa-user"></i> Employee
                                    </label>
                                    <select name="employeeId" id="employeeId" class="form-select">
                                        <option value="">All Employees</option>
                                        <c:forEach items="${employees}" var="emp">
                                            <option value="${emp.id}" ${filter.employeeId == emp.id ? 'selected' : ''}>
                                                ${emp.employeeCode} - ${emp.fullName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </c:if>

                            <!-- Search -->
                            <div class="col-md-3">
                                <label for="search" class="form-label">
                                    <i class="fas fa-search"></i> Search
                                </label>
                                <input type="text" name="search" id="search" class="form-control"
                                       placeholder="Search by title or reason"
                                       value="${filter.searchKeyword != null ? filter.searchKeyword : ''}">
                            </div>

                            <!-- Date Range -->
                            <div class="col-md-3">
                                <label for="fromDate" class="form-label">
                                    <i class="fas fa-calendar-alt"></i> From Date
                                </label>
                                <input type="date" name="fromDate" id="fromDate" class="form-control"
                                       value="${filter.fromDate != null ? filter.fromDate : ''}">
                            </div>

                            <div class="col-md-3">
                                <label for="toDate" class="form-label">
                                    <i class="fas fa-calendar-alt"></i> To Date
                                </label>
                                <input type="date" name="toDate" id="toDate" class="form-control"
                                       value="${filter.toDate != null ? filter.toDate : ''}">
                            </div>

                            <!-- Action Buttons -->
                            <div class="col-12">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-filter me-1"></i> Apply Filters
                                </button>
                                <a href="${pageContext.request.contextPath}/requests" class="btn btn-secondary">
                                    <i class="fas fa-times me-1"></i> Clear
                                </a>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Results Section -->
            <div class="results-section">
                <!-- Result Count -->
                <div class="result-info mb-3">
                    <p class="mb-0">
                        <i class="fas fa-list-ul me-2"></i>
                        Showing <strong>${result.pagination.totalItems}</strong> request(s)
                        <c:if test="${result.pagination.totalPages > 1}">
                            - Page <strong>${result.pagination.currentPage}</strong> of <strong>${result.pagination.totalPages}</strong>
                        </c:if>
                    </p>
                </div>

                <!-- Request List -->
                <c:choose>
                    <c:when test="${filter.scope == 'all' && not empty result.requestsByDepartment}">
                        <!-- Grouped by Department -->
                        <c:forEach items="${result.requestsByDepartment}" var="deptEntry">
                            <c:if test="${not empty deptEntry.value}">
                                <div class="department-section mb-4">
                                    <div class="department-header card-header bg-light border-start border-primary border-4">
                                        <h4 class="mb-0">
                                            <i class="fas fa-building me-2 text-primary"></i>
                                            <c:out value="${deptEntry.key}" />
                                            <span class="badge bg-secondary ms-2">${deptEntry.value.size()} request<c:if test="${deptEntry.value.size() > 1}">s</c:if></span>
                                        </h4>
                                    </div>
                                    <div class="card-body p-0">
                                        <c:set var="requests" value="${deptEntry.value}" scope="request" />
                                        <jsp:include page="request-list-table.jsp" />
                                    </div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <!-- Flat List -->
                        <div class="card">
                            <div class="card-body p-0">
                                <c:set var="requests" value="${result.requests}" scope="request" />
                                <jsp:include page="request-list-table.jsp" />
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>

                <!-- Pagination -->
                <c:if test="${result.pagination.totalPages > 1}">
                    <nav aria-label="Page navigation" class="mt-4">
                        <ul class="pagination justify-content-center">
                            <!-- Previous Button -->
                            <li class="page-item ${result.pagination.hasPrevious ? '' : 'disabled'}">
                                <a class="page-link"
                                   href="?scope=${filter.scope}&type=${filter.requestTypeId}&status=${filter.status}&showCancelled=${filter.showCancelled}&fromDate=${filter.fromDate}&toDate=${filter.toDate}&employeeId=${filter.employeeId}&search=${filter.searchKeyword}&page=${result.pagination.currentPage - 1}"
                                   aria-label="Previous">
                                    <span aria-hidden="true">&laquo;</span>
                                </a>
                            </li>

                            <!-- Page Numbers -->
                            <c:forEach begin="1" end="${result.pagination.totalPages}" var="pageNum">
                                <c:if test="${pageNum == 1 || pageNum == result.pagination.totalPages ||
                                              (pageNum >= result.pagination.currentPage - 2 && pageNum <= result.pagination.currentPage + 2)}">
                                    <li class="page-item ${pageNum == result.pagination.currentPage ? 'active' : ''}">
                                        <a class="page-link"
                                           href="?scope=${filter.scope}&type=${filter.requestTypeId}&status=${filter.status}&showCancelled=${filter.showCancelled}&fromDate=${filter.fromDate}&toDate=${filter.toDate}&employeeId=${filter.employeeId}&search=${filter.searchKeyword}&page=${pageNum}">
                                            ${pageNum}
                                        </a>
                                    </li>
                                </c:if>
                                <c:if test="${(pageNum == 2 && result.pagination.currentPage > 4) ||
                                              (pageNum == result.pagination.totalPages - 1 && result.pagination.currentPage < result.pagination.totalPages - 3)}">
                                    <li class="page-item disabled">
                                        <span class="page-link">...</span>
                                    </li>
                                </c:if>
                            </c:forEach>

                            <!-- Next Button -->
                            <li class="page-item ${result.pagination.hasNext ? '' : 'disabled'}">
                                <a class="page-link"
                                   href="?scope=${filter.scope}&type=${filter.requestTypeId}&status=${filter.status}&showCancelled=${filter.showCancelled}&fromDate=${filter.fromDate}&toDate=${filter.toDate}&employeeId=${filter.employeeId}&search=${filter.searchKeyword}&page=${result.pagination.currentPage + 1}"
                                   aria-label="Next">
                                    <span aria-hidden="true">&raquo;</span>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </div>
        </div>

        <!-- Footer -->
        <jsp:include page="../layout/dashboard-footer.jsp" />
    </div>

    <!-- Approval Modal -->
    <jsp:include page="modals/approval-modal.jsp" />

    <!-- Page specific JS -->
    <script src="${pageContext.request.contextPath}/assets/js/request-list.js?v=2"></script>
</body>
</html>
