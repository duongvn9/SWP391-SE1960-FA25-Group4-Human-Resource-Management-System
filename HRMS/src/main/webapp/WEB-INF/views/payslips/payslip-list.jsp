<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
            <%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <!-- CSS riêng của trang -->
                    <jsp:include page="../layout/head.jsp">
                        <jsp:param name="pageTitle" value="Payslip Management - HRMS" />
                        <jsp:param name="pageCss" value="payslip-list.css" />
                    </jsp:include>
                </head>

                <body class="payslip-page">
                    <!-- Sidebar -->
                    <jsp:include page="../layout/sidebar.jsp">
                        <jsp:param name="currentPage" value="payslip-list" />
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
                                        <i class="fas fa-file-invoice-dollar"></i> Payslips
                                    </li>
                                </ol>
                            </nav>

                            <!-- Hidden inputs for toast notifications -->
                            <input type="hidden" id="serverError"
                                value="${not empty error ? error : sessionScope.error}">
                            <input type="hidden" id="serverSuccess"
                                value="${not empty success ? success : sessionScope.success}">
                            <c:remove var="error" scope="session" />
                            <c:remove var="success" scope="session" />

                            <!-- Role-based conditional rendering -->
                            <c:choose>
                                <c:when test="${userRole == 'EMPLOYEE'}">
                                    <%-- Employee View: Simple interface --%>
                                        <div class="employee-payslip-view">
                                            <!-- Page Title -->
                                            <div
                                                class="page-head d-flex justify-content-between align-items-center mb-4">
                                                <div>
                                                    <h2 class="page-title"><i
                                                            class="fas fa-file-invoice-dollar me-2"></i>My Payslips</h2>
                                                    <p class="page-subtitle">View your monthly payslips and compensation
                                                        details</p>
                                                </div>
                                            </div>

                                            <!-- Employee Filters Section -->
                                            <div class="card mb-4">
                                                <div class="card-header">
                                                    <h5 class="mb-0">
                                                        <i class="fas fa-filter me-2"></i>Filter Payslips
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <form id="employeeFilterForm" method="GET"
                                                        action="${pageContext.request.contextPath}/payslips"
                                                        onsubmit="return convertMonthYearToDateRange()">
                                                        <input type="hidden" name="viewMode" value="personal">
                                                        <input type="hidden" name="periodStart" id="hiddenPeriodStart">
                                                        <input type="hidden" name="periodEnd" id="hiddenPeriodEnd">
                                                        <div class="row g-3">
                                                            <div class="col-md-6">
                                                                <label for="payslipMonth" class="form-label">
                                                                    Month <span class="text-danger">*</span>
                                                                </label>
                                                                <select class="form-select" id="payslipMonth"
                                                                    name="month" required>
                                                                    <option value="">Select Month</option>
                                                                    <option value="1" ${param.month=='1' ? 'selected'
                                                                        : '' }>January</option>
                                                                    <option value="2" ${param.month=='2' ? 'selected'
                                                                        : '' }>February</option>
                                                                    <option value="3" ${param.month=='3' ? 'selected'
                                                                        : '' }>March</option>
                                                                    <option value="4" ${param.month=='4' ? 'selected'
                                                                        : '' }>April</option>
                                                                    <option value="5" ${param.month=='5' ? 'selected'
                                                                        : '' }>May</option>
                                                                    <option value="6" ${param.month=='6' ? 'selected'
                                                                        : '' }>June</option>
                                                                    <option value="7" ${param.month=='7' ? 'selected'
                                                                        : '' }>July</option>
                                                                    <option value="8" ${param.month=='8' ? 'selected'
                                                                        : '' }>August</option>
                                                                    <option value="9" ${param.month=='9' ? 'selected'
                                                                        : '' }>September</option>
                                                                    <option value="10" ${param.month=='10' ? 'selected'
                                                                        : '' }>October</option>
                                                                    <option value="11" ${param.month=='11' ? 'selected'
                                                                        : '' }>November</option>
                                                                    <option value="12" ${param.month=='12' ? 'selected'
                                                                        : '' }>December</option>
                                                                </select>
                                                            </div>
                                                            <div class="col-md-6">
                                                                <label for="payslipYear" class="form-label">
                                                                    Year <span class="text-danger">*</span>
                                                                </label>
                                                                <select class="form-select" id="payslipYear" name="year"
                                                                    required>
                                                                    <option value="">Select Year</option>
                                                                    <c:forEach begin="2020" end="2030" var="y">
                                                                        <option value="${y}" ${param.year==y
                                                                            ? 'selected' : '' }>${y}</option>
                                                                    </c:forEach>
                                                                </select>
                                                            </div>
                                                            <div class="col-12">
                                                                <div class="d-flex gap-2">
                                                                    <button type="submit" class="btn btn-primary">
                                                                        <i class="fas fa-search me-1"></i>Search
                                                                    </button>
                                                                    <button type="button"
                                                                        class="btn btn-outline-secondary"
                                                                        onclick="document.getElementById('payslipMonth').value=''; document.getElementById('payslipYear').value='';">
                                                                        <i class="fas fa-times me-1"></i>Clear
                                                                    </button>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </form>
                                                    <script>
                                                        function convertMonthYearToDateRange() {
                                                            const month = document.getElementById('payslipMonth').value;
                                                            const year = document.getElementById('payslipYear').value;

                                                            if (!month || !year) {
                                                                alert('Please select both month and year');
                                                                return false;
                                                            }

                                                            // Calculate first day of month
                                                            const periodStart = year + '-' + month.padStart(2, '0') + '-01';

                                                            // Calculate last day of month
                                                            const lastDay = new Date(year, month, 0).getDate();
                                                            const periodEnd = year + '-' + month.padStart(2, '0') + '-' + lastDay.toString().padStart(2, '0');

                                                            // Set hidden fields
                                                            document.getElementById('hiddenPeriodStart').value = periodStart;
                                                            document.getElementById('hiddenPeriodEnd').value = periodEnd;

                                                            return true;
                                                        }
                                                    </script>
                                                </div>
                                            </div>

                                            <!-- Employee Payslip Table -->
                                            <div class="card">
                                                <div
                                                    class="card-header d-flex justify-content-between align-items-center">
                                                    <h5 class="mb-0">
                                                        <i class="fas fa-receipt me-2"></i>My Payslips
                                                    </h5>
                                                    <c:if test="${not empty payslips and payslips != null}">
                                                        <span class="badge bg-primary">${fn:length(payslips)}
                                                            payslip(s)</span>
                                                    </c:if>
                                                </div>
                                                <div class="card-body">
                                                    <c:choose>
                                                        <c:when test="${not empty payslips}">
                                                            <div class="table-responsive">
                                                                <table class="table table-striped table-hover">
                                                                    <thead class="table-dark">
                                                                        <tr>
                                                                            <th>Period</th>
                                                                            <th class="text-end">Gross Amount</th>
                                                                            <th class="text-end">Net Amount</th>
                                                                            <th class="text-center">Status</th>
                                                                            <th class="text-center">Generated</th>
                                                                            <th class="text-center">Actions</th>
                                                                        </tr>
                                                                    </thead>
                                                                    <tbody>
                                                                        <c:forEach var="payslip" items="${payslips}">
                                                                            <tr>
                                                                                <td>
                                                                                    ${payslip.periodStart} -
                                                                                    ${payslip.periodEnd}
                                                                                </td>
                                                                                <td class="text-end">
                                                                                    <fmt:formatNumber
                                                                                        value="${payslip.grossAmount}"
                                                                                        type="number"
                                                                                        groupingUsed="true"
                                                                                        maxFractionDigits="2" />
                                                                                    ${payslip.currency}
                                                                                </td>
                                                                                <td class="text-end">
                                                                                    <fmt:formatNumber
                                                                                        value="${payslip.netAmount}"
                                                                                        type="number"
                                                                                        groupingUsed="true"
                                                                                        maxFractionDigits="2" />
                                                                                    ${payslip.currency}
                                                                                    </span>
                                                                                </td>
                                                                                <td class="text-center">
                                                                                    <c:choose>
                                                                                        <c:when
                                                                                            test="${payslip.status == 'GENERATED'}">
                                                                                            <span
                                                                                                class="badge bg-success"><i
                                                                                                    class="fas fa-check me-1"></i>Generated</span>
                                                                                        </c:when>
                                                                                        <c:when
                                                                                            test="${payslip.status == 'DRAFT'}">
                                                                                            <span
                                                                                                class="badge bg-warning"><i
                                                                                                    class="fas fa-edit me-1"></i>Draft</span>
                                                                                        </c:when>
                                                                                        <c:otherwise>
                                                                                            <span
                                                                                                class="badge bg-secondary"><i
                                                                                                    class="fas fa-question me-1"></i>${payslip.status}</span>
                                                                                        </c:otherwise>
                                                                                    </c:choose>
                                                                                </td>
                                                                                <td class="text-center">
                                                                                    <c:choose>
                                                                                        <c:when
                                                                                            test="${not empty payslip.generatedAt}">
                                                                                            <div class="small">
                                                                                                ${payslip.generatedAt}
                                                                                            </div>
                                                                                        </c:when>
                                                                                        <c:otherwise>
                                                                                            <span
                                                                                                class="text-muted">-</span>
                                                                                        </c:otherwise>
                                                                                    </c:choose>
                                                                                </td>
                                                                                <td class="text-center">
                                                                                    <div class="btn-group" role="group">
                                                                                        <a href="${pageContext.request.contextPath}/payslips/detail?userId=${payslip.userId}&periodStart=${payslip.periodStart}&periodEnd=${payslip.periodEnd}"
                                                                                            class="btn btn-sm btn-outline-primary"
                                                                                            title="View Details">
                                                                                            <i class="fas fa-eye"></i>
                                                                                        </a>
                                                                                        <c:if
                                                                                            test="${not empty payslip.filePath}">
                                                                                            <a href="${pageContext.request.contextPath}/payslips/${payslip.id}/download"
                                                                                                class="btn btn-sm btn-outline-success"
                                                                                                title="Download PDF">
                                                                                                <i
                                                                                                    class="fas fa-download"></i>
                                                                                            </a>
                                                                                        </c:if>
                                                                                    </div>
                                                                                </td>
                                                                            </tr>
                                                                        </c:forEach>
                                                                    </tbody>
                                                                </table>
                                                            </div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="text-center py-5">
                                                                <i class="fas fa-receipt fa-3x text-muted mb-3"></i>
                                                                <h5 class="text-muted">No Payslips Found</h5>
                                                                <p class="text-muted">
                                                                    <c:choose>
                                                                        <c:when
                                                                            test="${not empty param.periodStart and not empty param.periodEnd}">
                                                                            No payslips found for the selected period.
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            Please select a period to view your
                                                                            payslips.
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </p>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>
                                </c:when>
                                <c:when test="${userRole == 'HRM'}">
                                    <%-- HRM View: Full administrative interface --%>
                                        <div class="hrm-payslip-view">
                                            <!-- Page Title with View Toggle -->
                                            <div
                                                class="page-head d-flex justify-content-between align-items-center mb-4">
                                                <div>
                                                    <h2 class="page-title">
                                                        <i class="fas fa-file-invoice-dollar me-2"></i>
                                                        <span id="pageTitle">Payslip Management</span>
                                                    </h2>
                                                    <p class="page-subtitle" id="pageSubtitle">Manage payslips for all
                                                        employees</p>
                                                </div>
                                                <div class="view-toggle-container">
                                                    <a href="${pageContext.request.contextPath}/payslips?viewMode=personal"
                                                        class="btn btn-primary">
                                                        <i class="fas fa-user me-1"></i>My Payslips
                                                    </a>
                                                </div>
                                            </div>

                                            <!-- Summary Counters -->
                                            <c:if test="${not empty summaryCounters}">
                                                <div class="summary-counters">
                                                    <!-- Generated Count -->
                                                    <div class="counter-card">
                                                        <div class="counter-card-header">
                                                            <div class="counter-icon success">
                                                                <i class="fas fa-check-circle"></i>
                                                            </div>
                                                        </div>
                                                        <div class="counter-value">${summaryCounters.generatedCount}
                                                        </div>
                                                        <div class="counter-label">Generated Payslips</div>
                                                    </div>

                                                    <!-- Dirty Count -->
                                                    <div class="counter-card">
                                                        <div class="counter-card-header">
                                                            <div class="counter-icon warning">
                                                                <i class="fas fa-exclamation-triangle"></i>
                                                            </div>
                                                        </div>
                                                        <div class="counter-value">${summaryCounters.dirtyCount}</div>
                                                        <div class="counter-label">Needs Regeneration</div>
                                                    </div>

                                                    <!-- Missing Count -->
                                                    <div class="counter-card">
                                                        <div class="counter-card-header">
                                                            <div class="counter-icon danger">
                                                                <i class="fas fa-times-circle"></i>
                                                            </div>
                                                        </div>
                                                        <div class="counter-value">${summaryCounters.missingCount}</div>
                                                        <div class="counter-label">Missing Payslips</div>
                                                    </div>

                                                    <!-- Total Count -->
                                                    <div class="counter-card">
                                                        <div class="counter-card-header">
                                                            <div class="counter-icon primary">
                                                                <i class="fas fa-users"></i>
                                                            </div>
                                                        </div>
                                                        <div class="counter-value">${summaryCounters.totalInScope}</div>
                                                        <div class="counter-label">Total Employees</div>
                                                    </div>
                                                </div>

                                                <!-- Employees Without Payslips Alert -->
                                                <c:if test="${summaryCounters.missingCount > 0}">
                                                    <div class="alert alert-warning" role="alert">
                                                        <div class="clickable-header" style="cursor: pointer;"
                                                            data-bs-toggle="collapse"
                                                            data-bs-target="#employeesWithoutPayslipCollapse"
                                                            aria-expanded="false"
                                                            aria-controls="employeesWithoutPayslipCollapse">
                                                            <div
                                                                class="d-flex justify-content-between align-items-center mb-2">
                                                                <h5 class="mb-0">
                                                                    <i class="fas fa-user-plus me-2"></i>Employees
                                                                    Without Payslips
                                                                    <span class="badge bg-warning text-dark"
                                                                        id="employeesWithoutPayslipCount">
                                                                        ${summaryCounters.missingCount}
                                                                    </span>
                                                                </h5>
                                                                <i class="fas fa-chevron-down toggle-icon"></i>
                                                            </div>
                                                            <p class="mb-0 small">
                                                                These employees don't have payslips for this period yet.
                                                                Click to view the list and generate payslips.
                                                            </p>
                                                        </div>
                                                        <div class="collapse mt-2" id="employeesWithoutPayslipCollapse">
                                                            <div id="employeesWithoutPayslipContent">
                                                                <!-- Loading state -->
                                                                <div class="text-center py-3"
                                                                    id="employeesWithoutPayslipLoading">
                                                                    <div class="spinner-border spinner-border-sm text-warning"
                                                                        role="status">
                                                                        <span class="visually-hidden">Loading...</span>
                                                                    </div>
                                                                    <p class="mt-2 mb-0 small text-muted">Loading
                                                                        employees...</p>
                                                                </div>
                                                                <!-- Content will be loaded here dynamically -->
                                                                <div class="row g-2" id="employeesWithoutPayslipList"
                                                                    style="display: none;">
                                                                    <!-- Will be populated by JavaScript -->
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:if>

                                                <!-- Employees with Attendance Changes Alert -->
                                                <c:choose>
                                                    <c:when test="${summaryCounters.dirtyCount > 0}">
                                                        <div class="alert alert-info" role="alert">
                                                            <div class="clickable-header" style="cursor: pointer;"
                                                                data-bs-toggle="collapse"
                                                                data-bs-target="#employeesWithAttendanceChangesCollapse"
                                                                aria-expanded="false"
                                                                aria-controls="employeesWithAttendanceChangesCollapse">
                                                                <div
                                                                    class="d-flex justify-content-between align-items-center mb-2">
                                                                    <h5 class="mb-0">
                                                                        <i class="fas fa-sync-alt me-2"></i>Attendance
                                                                        Changes Detected
                                                                        <span class="badge bg-info"
                                                                            id="employeesWithAttendanceChangesCount">
                                                                            ${summaryCounters.dirtyCount}
                                                                        </span>
                                                                    </h5>
                                                                    <i class="fas fa-chevron-down toggle-icon"></i>
                                                                </div>
                                                                <p class="mb-0 small">
                                                                    These payslips need regeneration due to attendance
                                                                    data changes.
                                                                    Click to view affected employees.
                                                                </p>
                                                            </div>
                                                            <div class="collapse mt-2"
                                                                id="employeesWithAttendanceChangesCollapse">
                                                                <div id="employeesWithAttendanceChangesContent">
                                                                    <!-- Loading state -->
                                                                    <div class="text-center py-3"
                                                                        id="employeesWithAttendanceChangesLoading">
                                                                        <div class="spinner-border spinner-border-sm text-info"
                                                                            role="status">
                                                                            <span
                                                                                class="visually-hidden">Loading...</span>
                                                                        </div>
                                                                        <p class="mt-2 mb-0 small text-muted">Loading
                                                                            employees...</p>
                                                                    </div>
                                                                    <!-- Content will be loaded here dynamically -->
                                                                    <div class="row g-2"
                                                                        id="employeesWithAttendanceChangesList"
                                                                        style="display: none;">
                                                                        <!-- Will be populated by JavaScript -->
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="alert alert-success" role="alert">
                                                            <div class="d-flex align-items-center">
                                                                <i class="fas fa-check-circle me-2"></i>
                                                                <div>
                                                                    <strong>All Up-to-Date:</strong>
                                                                    No attendance changes detected. All payslips are
                                                                    current.
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>

                                                <!-- Success message when all payslips are up to date -->
                                                <c:choose>
                                                    <c:when
                                                        test="${summaryCounters.generatedCount == summaryCounters.totalInScope and summaryCounters.totalInScope > 0}">
                                                        <div class="alert alert-success" role="alert">
                                                            <div class="d-flex align-items-center">
                                                                <i class="fas fa-check-circle me-2"></i>
                                                                <div>
                                                                    <strong>All Complete:</strong>
                                                                    All ${summaryCounters.totalInScope} payslips have
                                                                    been generated successfully.
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </c:when>
                                                </c:choose>
                                            </c:if>

                                            <!-- HRM Filters Section -->
                                            <div class="filter-section card">
                                                <div class="card-header" data-bs-toggle="collapse"
                                                    data-bs-target="#filterCollapse">
                                                    <h5>
                                                        <i class="fas fa-filter"></i>Advanced Filters
                                                        <c:set var="activeFilters" value="0" />
                                                        <c:if test="${not empty param.periodStart}">
                                                            <c:set var="activeFilters" value="${activeFilters + 1}" />
                                                        </c:if>
                                                        <c:if test="${not empty param.departmentId}">
                                                            <c:set var="activeFilters" value="${activeFilters + 1}" />
                                                        </c:if>
                                                        <c:if test="${not empty param.userId}">
                                                            <c:set var="activeFilters" value="${activeFilters + 1}" />
                                                        </c:if>
                                                        <c:if test="${not empty param.employeeSearch}">
                                                            <c:set var="activeFilters" value="${activeFilters + 1}" />
                                                        </c:if>
                                                        <c:if test="${not empty param.status}">
                                                            <c:set var="activeFilters" value="${activeFilters + 1}" />
                                                        </c:if>
                                                        <c:if test="${param.onlyDirty == 'true'}">
                                                            <c:set var="activeFilters" value="${activeFilters + 1}" />
                                                        </c:if>
                                                        <c:if test="${activeFilters > 0}">
                                                            <span class="badge bg-primary ms-2">${activeFilters}
                                                                active</span>
                                                        </c:if>
                                                    </h5>
                                                    <button class="filter-toggle" type="button">
                                                        <i class="fas fa-chevron-down"></i>
                                                    </button>
                                                </div>
                                                <div class="card-body collapse show" id="filterCollapse">
                                                    <div class="alert alert-info mb-3" role="alert">
                                                        <i class="fas fa-info-circle me-2"></i>
                                                        <strong>Payroll Period:</strong> Select month and year to view
                                                        payslips for that specific payroll period.
                                                        The system automatically calculates the full month range.
                                                    </div>
                                                    <form id="hrmFilterForm" method="GET"
                                                        action="${pageContext.request.contextPath}/payslips">
                                                        <div class="filter-row">
                                                            <!-- Payroll Period Filter -->
                                                            <div class="filter-group">
                                                                <label for="filterMonth">
                                                                    <i class="fas fa-calendar-alt"></i>Payroll Month
                                                                    <span class="text-danger">*</span>
                                                                </label>
                                                                <select class="form-select" id="filterMonth"
                                                                    name="filterMonth" required>
                                                                    <option value="">Select Month...</option>
                                                                    <option value="1" ${param.filterMonth=='1'
                                                                        ? 'selected' : '' }>January</option>
                                                                    <option value="2" ${param.filterMonth=='2'
                                                                        ? 'selected' : '' }>February</option>
                                                                    <option value="3" ${param.filterMonth=='3'
                                                                        ? 'selected' : '' }>March</option>
                                                                    <option value="4" ${param.filterMonth=='4'
                                                                        ? 'selected' : '' }>April</option>
                                                                    <option value="5" ${param.filterMonth=='5'
                                                                        ? 'selected' : '' }>May</option>
                                                                    <option value="6" ${param.filterMonth=='6'
                                                                        ? 'selected' : '' }>June</option>
                                                                    <option value="7" ${param.filterMonth=='7'
                                                                        ? 'selected' : '' }>July</option>
                                                                    <option value="8" ${param.filterMonth=='8'
                                                                        ? 'selected' : '' }>August</option>
                                                                    <option value="9" ${param.filterMonth=='9'
                                                                        ? 'selected' : '' }>September</option>
                                                                    <option value="10" ${param.filterMonth=='10'
                                                                        ? 'selected' : '' }>October</option>
                                                                    <option value="11" ${param.filterMonth=='11'
                                                                        ? 'selected' : '' }>November</option>
                                                                    <option value="12" ${param.filterMonth=='12'
                                                                        ? 'selected' : '' }>December</option>
                                                                </select>
                                                            </div>

                                                            <div class="filter-group">
                                                                <label for="filterYear">
                                                                    <i class="fas fa-calendar"></i>Payroll Year <span
                                                                        class="text-danger">*</span>
                                                                </label>
                                                                <select class="form-select" id="filterYear"
                                                                    name="filterYear" required>
                                                                    <option value="">Select Year...</option>
                                                                    <c:forEach begin="2020" end="2030" var="year">
                                                                        <option value="${year}" ${param.filterYear==year
                                                                            ? 'selected' : '' }>${year}</option>
                                                                    </c:forEach>
                                                                </select>
                                                            </div>

                                                            <!-- Hidden inputs for backward compatibility -->
                                                            <input type="hidden" id="periodStart" name="periodStart"
                                                                value="${param.periodStart}">
                                                            <input type="hidden" id="periodEnd" name="periodEnd"
                                                                value="${param.periodEnd}">

                                                            <!-- Department Filter -->
                                                            <div class="filter-group">
                                                                <label for="departmentId">
                                                                    <i class="fas fa-building"></i>Department
                                                                </label>
                                                                <select id="departmentId" name="departmentId">
                                                                    <option value="">All Departments</option>
                                                                    <c:forEach var="dept" items="${departments}">
                                                                        <option value="${dept.id}"
                                                                            ${param.departmentId==dept.id ? 'selected'
                                                                            : '' }>
                                                                            ${dept.name}
                                                                        </option>
                                                                    </c:forEach>
                                                                </select>
                                                            </div>

                                                            <!-- Status Filter -->
                                                            <div class="filter-group">
                                                                <label for="status">
                                                                    <i class="fas fa-info-circle"></i>Status
                                                                </label>
                                                                <select id="status" name="status">
                                                                    <option value="">All Status</option>
                                                                    <option value="GENERATED"
                                                                        ${param.status=='GENERATED' ? 'selected' : '' }>
                                                                        Generated</option>
                                                                    <option value="DIRTY" ${param.status=='DIRTY'
                                                                        ? 'selected' : '' }>Dirty</option>
                                                                    <option value="PENDING" ${param.status=='PENDING'
                                                                        ? 'selected' : '' }>Pending</option>
                                                                </select>
                                                            </div>

                                                            <!-- Employee Dropdown Filter -->
                                                            <div class="filter-group">
                                                                <label for="filterUserId">
                                                                    <i class="fas fa-user"></i>Select Employee
                                                                </label>
                                                                <select id="filterUserId" name="userId">
                                                                    <option value="">All Employees</option>
                                                                    <c:forEach var="emp" items="${employees}">
                                                                        <option value="${emp.id}" ${param.userId==emp.id
                                                                            ? 'selected' : '' }>
                                                                            [${emp.employeeCode}] ${emp.fullName}
                                                                        </option>
                                                                    </c:forEach>
                                                                </select>
                                                            </div>

                                                            <!-- Employee Search Filter -->
                                                            <div class="filter-group">
                                                                <label for="employeeSearch">
                                                                    <i class="fas fa-search"></i>Search Employee
                                                                </label>
                                                                <input type="text" id="employeeSearch"
                                                                    name="employeeSearch"
                                                                    placeholder="Type code or name"
                                                                    value="${param.employeeSearch}">
                                                            </div>
                                                        </div>

                                                        <!-- Filter Actions -->
                                                        <div class="filter-actions">
                                                            <button type="submit" class="btn btn-primary">
                                                                <i class="fas fa-search me-1"></i>Apply Filters
                                                            </button>
                                                            <button type="button" class="btn btn-secondary"
                                                                onclick="clearFilters()">
                                                                <i class="fas fa-times me-1"></i>Clear
                                                            </button>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>

                                            <!-- Bulk Actions -->
                                            <jsp:include page="sections/bulk-actions.jsp" />

                                            <!-- Issues Panel -->
                                            <jsp:include page="sections/issues-panel.jsp" />

                                            <!-- HRM Payslip Table -->
                                            <div class="table-card">
                                                <div class="card-header">
                                                    <h5 class="mb-0">
                                                        <i class="fas fa-table me-2"></i>Payslip Management
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <c:choose>
                                                        <c:when test="${not empty payslips}">
                                                            <div class="table-responsive">
                                                                <table class="table table-striped table-hover">
                                                                    <thead class="table-dark">
                                                                        <tr>
                                                                            <th>Employee Code</th>
                                                                            <th>Employee Name</th>
                                                                            <th>Department</th>
                                                                            <th>Period</th>
                                                                            <th class="text-end">Gross</th>
                                                                            <th class="text-end">Net</th>
                                                                            <th class="text-center">Status</th>
                                                                            <th class="text-center">Generated</th>
                                                                            <th class="text-center">Actions</th>
                                                                        </tr>
                                                                    </thead>
                                                                    <tbody>
                                                                        <c:forEach var="payslip" items="${payslips}">
                                                                            <tr>
                                                                                <td><strong>${payslip.userName}</strong>
                                                                                </td>
                                                                                <td>${payslip.userFullName}</td>
                                                                                <td>${payslip.departmentName}</td>
                                                                                <td>${payslip.periodStart} -
                                                                                    ${payslip.periodEnd}</td>
                                                                                <td class="text-end">
                                                                                    <fmt:formatNumber
                                                                                        value="${payslip.grossAmount}"
                                                                                        type="number"
                                                                                        groupingUsed="true"
                                                                                        maxFractionDigits="2" />
                                                                                    ${payslip.currency}
                                                                                </td>
                                                                                <td class="text-end">
                                                                                    <fmt:formatNumber
                                                                                        value="${payslip.netAmount}"
                                                                                        type="number"
                                                                                        groupingUsed="true"
                                                                                        maxFractionDigits="2" />
                                                                                    ${payslip.currency}
                                                                                </td>
                                                                                <td class="text-center">
                                                                                    <span
                                                                                        class="status-badge ${payslip.status.toLowerCase()}">
                                                                                        ${payslip.status}
                                                                                    </span>
                                                                                </td>
                                                                                <td class="text-center">
                                                                                    ${payslip.generatedAt}</td>
                                                                                <td class="text-center">
                                                                                    <div class="btn-group" role="group">
                                                                                        <a href="${pageContext.request.contextPath}/payslips/detail?userId=${payslip.userId}&periodStart=${payslip.periodStart}&periodEnd=${payslip.periodEnd}"
                                                                                            class="btn btn-sm btn-outline-primary"
                                                                                            title="View Details">
                                                                                            <i class="fas fa-eye"></i>
                                                                                        </a>
                                                                                        <button type="button"
                                                                                            class="btn btn-sm btn-outline-warning"
                                                                                            onclick="regeneratePayslip(${payslip.id}, '${fn:escapeXml(payslip.userFullName)}')"
                                                                                            title="Regenerate Payslip">
                                                                                            <i class="fas fa-sync"></i>
                                                                                        </button>
                                                                                    </div>
                                                                                </td>
                                                                            </tr>
                                                                        </c:forEach>
                                                                    </tbody>
                                                                </table>
                                                            </div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="text-center py-5">
                                                                <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                                                <h5 class="text-muted">No Payslips Found</h5>
                                                                <p class="text-muted">Please select a period and apply
                                                                    filters to view payslips.</p>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>
                                </c:when>
                                <c:otherwise>
                                    <%-- Access Denied --%>
                                        <div class="access-denied text-center py-5">
                                            <i class="fas fa-lock fa-3x text-muted mb-3"></i>
                                            <h3>Access Denied</h3>
                                            <p class="text-muted">You don't have permission to access this page.</p>
                                            <a href="${pageContext.request.contextPath}/dashboard"
                                                class="btn btn-primary">
                                                <i class="fas fa-home me-1"></i> Back to Dashboard
                                            </a>
                                        </div>
                                </c:otherwise>
                            </c:choose>

                            <!-- Pagination -->
                            <c:if test="${pagination != null && pagination.totalPages > 1}">
                                <nav aria-label="Page navigation" class="mt-4">
                                    <ul class="pagination justify-content-center">
                                        <!-- Previous Button -->
                                        <li class="page-item ${pagination.hasPrevious ? '' : 'disabled'}">
                                            <a class="page-link"
                                                href="?period=${filter.periodStart}&department=${filter.departmentId}&employee=${filter.userId}&onlyDirty=${filter.onlyDirty}&onlyNotGenerated=${filter.onlyNotGenerated}&page=${pagination.currentPage - 1}"
                                                aria-label="Previous">
                                                <span aria-hidden="true">&laquo;</span>
                                            </a>
                                        </li>

                                        <!-- Page Numbers -->
                                        <c:forEach begin="1" end="${pagination.totalPages}" var="pageNum">
                                            <c:if
                                                test="${pageNum == 1 || pageNum == pagination.totalPages ||
                                          (pageNum >= pagination.currentPage - 2 && pageNum <= pagination.currentPage + 2)}">
                                                <li
                                                    class="page-item ${pageNum == pagination.currentPage ? 'active' : ''}">
                                                    <a class="page-link"
                                                        href="?period=${filter.periodStart}&department=${filter.departmentId}&employee=${filter.userId}&onlyDirty=${filter.onlyDirty}&onlyNotGenerated=${filter.onlyNotGenerated}&page=${pageNum}">
                                                        ${pageNum}
                                                    </a>
                                                </li>
                                            </c:if>
                                            <c:if
                                                test="${(pageNum == 2 && pagination.currentPage > 4) ||
                                          (pageNum == pagination.totalPages - 1 && pagination.currentPage < pagination.totalPages - 3)}">
                                                <li class="page-item disabled">
                                                    <span class="page-link">...</span>
                                                </li>
                                            </c:if>
                                        </c:forEach>

                                        <!-- Next Button -->
                                        <li class="page-item ${pagination.hasNext ? '' : 'disabled'}">
                                            <a class="page-link"
                                                href="?period=${filter.periodStart}&department=${filter.departmentId}&employee=${filter.userId}&onlyDirty=${filter.onlyDirty}&onlyNotGenerated=${filter.onlyNotGenerated}&page=${pagination.currentPage + 1}"
                                                aria-label="Next">
                                                <span aria-hidden="true">&raquo;</span>
                                            </a>
                                        </li>
                                    </ul>
                                </nav>
                            </c:if>
                        </div>

                        <!-- Footer -->
                        <jsp:include page="../layout/dashboard-footer.jsp" />
                    </div>

                    <!-- Page specific JS -->
                    <script src="${pageContext.request.contextPath}/assets/js/payslip-actions.js"></script>

                    <!-- Toast Container -->
                    <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 11000;">
                        <div id="responseToast" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
                            <div class="toast-header" id="toastHeader">
                                <i class="fas fa-circle me-2" id="toastIcon"></i>
                                <strong class="me-auto" id="toastTitle">Notification</strong>
                                <button type="button" class="btn-close" data-bs-dismiss="toast"
                                    aria-label="Close"></button>
                            </div>
                            <div class="toast-body" id="toastBody">
                                <!-- Message will be inserted here -->
                            </div>
                        </div>
                    </div>

                    <!-- Toast JavaScript -->
                    <script>
                        function showToast(message, type = 'info', title = 'Notification') {
                            const toastElement = document.getElementById('responseToast');
                            const toastHeader = document.getElementById('toastHeader');
                            const toastIcon = document.getElementById('toastIcon');
                            const toastTitle = document.getElementById('toastTitle');
                            const toastBody = document.getElementById('toastBody');

                            // Reset classes
                            toastHeader.className = 'toast-header';
                            toastIcon.className = 'fas fa-circle me-2';

                            // Set type-specific styling
                            switch (type) {
                                case 'success':
                                    toastHeader.classList.add('bg-success');
                                    toastIcon.classList.add('fa-check-circle');
                                    toastTitle.textContent = title || 'Success';
                                    break;
                                case 'danger':
                                case 'error':
                                    toastHeader.classList.add('bg-danger');
                                    toastIcon.classList.add('fa-exclamation-circle');
                                    toastTitle.textContent = title || 'Error';
                                    break;
                                case 'warning':
                                    toastHeader.classList.add('bg-warning');
                                    toastIcon.classList.add('fa-exclamation-triangle');
                                    toastTitle.textContent = title || 'Warning';
                                    break;
                                case 'info':
                                default:
                                    toastHeader.classList.add('bg-info');
                                    toastIcon.classList.add('fa-info-circle');
                                    toastTitle.textContent = title || 'Information';
                                    break;
                            }

                            // Set message
                            toastBody.textContent = message;

                            // Show toast
                            const toast = new bootstrap.Toast(toastElement, {
                                autohide: true,
                                delay: 5000
                            });
                            toast.show();
                        }

                        // Global data for employees without payslips
                        const employeesWithoutPayslipData = [<c:forEach items="${employeesWithoutPayslip}" var="emp" varStatus="status">{id: ${emp.id}, employeeCode: '${fn:escapeXml(emp.employeeCode)}', fullName: '${fn:escapeXml(emp.fullName)}', departmentId: ${emp.departmentId != null ? emp.departmentId : 'null'}}<c:if test="${!status.last}">,</c:if></c:forEach>];
                        let employeesWithoutPayslipLoaded = false;
                        const itemsPerPageWithout = 12;

                        // Global function for pagination (must be in window scope for onclick)
                        window.renderEmployeesWithoutPayslip = function (page) {
                            const list = document.getElementById('employeesWithoutPayslipList');
                            if (!list) return;

                            list.innerHTML = '';
                            const startIdx = (page - 1) * itemsPerPageWithout;
                            const endIdx = startIdx + itemsPerPageWithout;
                            const displayEmployees = employeesWithoutPayslipData.slice(startIdx, endIdx);

                            displayEmployees.forEach(emp => {
                                const col = document.createElement('div');
                                col.className = 'col-md-6 col-lg-4';
                                col.innerHTML = `
                    <div class="card border-warning" style="cursor: pointer;"
                         onclick="showGenerateModalForEmployee(\${emp.id}, '\${emp.fullName}')">
                        <div class="card-body p-2">
                            <div class="d-flex align-items-center">
                                <div class="flex-shrink-0">
                                    <i class="fas fa-user-circle fa-2x text-warning"></i>
                                </div>
                                <div class="flex-grow-1 ms-2">
                                    <h6 class="mb-0">\${emp.fullName}</h6>
                                    <small class="text-muted">\${emp.employeeCode}</small>
                                    \${emp.departmentName ? '<br><small class="text-muted"><i class="fas fa-building me-1"></i>' + emp.departmentName + '</small>' : ''}
                                </div>
                                <div class="flex-shrink-0">
                                    <i class="fas fa-plus-circle text-success"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                                list.appendChild(col);
                            });

                            // Pagination info and controls
                            if (employeesWithoutPayslipData.length > itemsPerPageWithout) {
                                const totalPages = Math.ceil(employeesWithoutPayslipData.length / itemsPerPageWithout);
                                const paginationDiv = document.createElement('div');
                                paginationDiv.className = 'col-12 mt-3';
                                paginationDiv.innerHTML = `
                    <div class="d-flex justify-content-between align-items-center">
                        <small class="text-muted">Showing \${startIdx + 1}-\${Math.min(endIdx, employeesWithoutPayslipData.length)} of \${employeesWithoutPayslipData.length} employees</small>
                        <div class="btn-group btn-group-sm" role="group">
                            <button type="button" class="btn btn-outline-warning" \${page === 1 ? 'disabled' : ''}
                                    onclick="renderEmployeesWithoutPayslip(\${page - 1})">
                                <i class="fas fa-chevron-left"></i> Previous
                            </button>
                            <button type="button" class="btn btn-outline-warning" disabled>
                                Page \${page} of \${totalPages}
                            </button>
                            <button type="button" class="btn btn-outline-warning" \${page === totalPages ? 'disabled' : ''}
                                    onclick="renderEmployeesWithoutPayslip(\${page + 1})">
                                Next <i class="fas fa-chevron-right"></i>
                            </button>
                        </div>
                    </div>
                `;
                                list.appendChild(paginationDiv);
                            }
                        };

                        function loadEmployeesWithoutPayslip() {
                            if (employeesWithoutPayslipLoaded) return;
                            employeesWithoutPayslipLoaded = true;

                            const loading = document.getElementById('employeesWithoutPayslipLoading');
                            const list = document.getElementById('employeesWithoutPayslipList');

                            if (loading) loading.style.display = 'none';
                            if (list) {
                                list.style.display = '';
                                if (employeesWithoutPayslipData.length > 0) {
                                    renderEmployeesWithoutPayslip(1);
                                } else {
                                    list.innerHTML = '<div class="col-12 text-center text-muted">All employees have payslips for this period.</div>';
                                }
                            }
                        }

                        // Global data for employees with attendance changes
                        const employeesWithAttendanceChangesData = [<c:forEach items="${employeesWithAttendanceChanges}" var="emp" varStatus="status">{id: ${emp.id}, employeeCode: '${fn:escapeXml(emp.employeeCode)}', fullName: '${fn:escapeXml(emp.fullName)}', departmentId: ${emp.departmentId != null ? emp.departmentId : 'null'}, payslipId: ${emp.payslipId}, changeReason: '${fn:escapeXml(emp.changeReason)}'}<c:if test="${!status.last}">,</c:if></c:forEach>];
                        let employeesWithAttendanceChangesLoaded = false;
                        const itemsPerPageChanges = 12;

                        // Global function for pagination (must be in window scope for onclick)
                        window.renderEmployeesWithChanges = function (page) {
                            const list = document.getElementById('employeesWithAttendanceChangesList');
                            if (!list) return;

                            list.innerHTML = '';
                            const startIdx = (page - 1) * itemsPerPageChanges;
                            const endIdx = startIdx + itemsPerPageChanges;
                            const displayEmployees = employeesWithAttendanceChangesData.slice(startIdx, endIdx);

                            displayEmployees.forEach(emp => {
                                const col = document.createElement('div');
                                col.className = 'col-md-6 col-lg-4';
                                col.innerHTML = `
                    <div class="card border-info" style="cursor: pointer;"
                         onclick="showRegenerateModalForEmployee(\${emp.id}, '\${emp.fullName}')">
                        <div class="card-body p-2">
                            <div class="d-flex align-items-center">
                                <div class="flex-shrink-0">
                                    <i class="fas fa-user-circle fa-2x text-info"></i>
                                </div>
                                <div class="flex-grow-1 ms-2">
                                    <h6 class="mb-0">\${emp.fullName}</h6>
                                    <small class="text-muted">\${emp.employeeCode}</small>
                                    \${emp.departmentName ? '<br><small class="text-muted"><i class="fas fa-building me-1"></i>' + emp.departmentName + '</small>' : ''}
                                    \${emp.changeReason ? '<br><small class="text-info"><i class="fas fa-info-circle me-1"></i>' + emp.changeReason + '</small>' : ''}
                                </div>
                                <div class="flex-shrink-0">
                                    <i class="fas fa-sync-alt text-primary"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                                list.appendChild(col);
                            });

                            // Pagination info and controls
                            if (employeesWithAttendanceChangesData.length > itemsPerPageChanges) {
                                const totalPages = Math.ceil(employeesWithAttendanceChangesData.length / itemsPerPageChanges);
                                const paginationDiv = document.createElement('div');
                                paginationDiv.className = 'col-12 mt-3';
                                paginationDiv.innerHTML = `
                    <div class="d-flex justify-content-between align-items-center">
                        <small class="text-muted">Showing \${startIdx + 1}-\${Math.min(endIdx, employeesWithAttendanceChangesData.length)} of \${employeesWithAttendanceChangesData.length} employees</small>
                        <div class="btn-group btn-group-sm" role="group">
                            <button type="button" class="btn btn-outline-info" \${page === 1 ? 'disabled' : ''}
                                    onclick="renderEmployeesWithChanges(\${page - 1})">
                                <i class="fas fa-chevron-left"></i> Previous
                            </button>
                            <button type="button" class="btn btn-outline-info" disabled>
                                Page \${page} of \${totalPages}
                            </button>
                            <button type="button" class="btn btn-outline-info" \${page === totalPages ? 'disabled' : ''}
                                    onclick="renderEmployeesWithChanges(\${page + 1})">
                                Next <i class="fas fa-chevron-right"></i>
                            </button>
                        </div>
                    </div>
                `;
                                list.appendChild(paginationDiv);
                            }
                        };

                        function loadEmployeesWithAttendanceChanges() {
                            if (employeesWithAttendanceChangesLoaded) return;
                            employeesWithAttendanceChangesLoaded = true;

                            const loading = document.getElementById('employeesWithAttendanceChangesLoading');
                            const list = document.getElementById('employeesWithAttendanceChangesList');

                            if (loading) loading.style.display = 'none';
                            if (list) {
                                list.style.display = '';
                                if (employeesWithAttendanceChangesData.length > 0) {
                                    renderEmployeesWithChanges(1);
                                } else {
                                    list.innerHTML = '<div class="col-12 text-center text-muted">No attendance changes detected.</div>';
                                }
                            }
                        }

                        // Generate payslip for specific employee directly
                        function showGenerateModalForEmployee(userId, userName) {
                            // Get current period info
                            const currentDate = new Date();
                            let prevMonth = currentDate.getMonth(); // 0-based
                            let prevYear = currentDate.getFullYear();

                            if (prevMonth === 0) {
                                prevMonth = 12;
                                prevYear = prevYear - 1;
                            }

                            const monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
                                'July', 'August', 'September', 'October', 'November', 'December'];
                            const monthName = monthNames[prevMonth - 1];

                            // Show custom confirmation dialog
                            showCustomConfirm(
                                'Generate Payslip',
                                'Generate payslip for <strong>' + userName + '</strong> for <strong>' + monthName + ' ' + prevYear + '</strong>?',
                                'success',
                                function () {
                                    // Generate directly without opening modal
                                    generatePayslipForEmployee(userId, userName, prevMonth, prevYear, false);
                                }
                            );
                        }

                        // Generate payslip for specific employee via AJAX
                        function generatePayslipForEmployee(userId, userName, month, year, forceRegenerate) {
                            // Show loading toast
                            if (typeof showToast === 'function') {
                                showToast('Generating payslip for ' + userName + '...', 'info', 'Processing');
                            }

                            // Prepare form data using URLSearchParams for proper encoding
                            const params = new URLSearchParams();
                            params.append('action', 'generate');
                            params.append('scope', 'EMPLOYEE');
                            params.append('scopeId', userId);
                            params.append('payrollMonth', month);
                            params.append('payrollYear', year);
                            if (forceRegenerate) {
                                params.append('force', 'true');
                            }

                            console.log('[DEBUG] Generating payslip with params:', {
                                action: 'generate',
                                scope: 'EMPLOYEE',
                                scopeId: userId,
                                payrollMonth: month,
                                payrollYear: year,
                                force: forceRegenerate
                            });

                            // Make AJAX request
                            fetch('${pageContext.request.contextPath}/payslips', {
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/x-www-form-urlencoded'
                                },
                                body: params.toString()
                            })
                                .then(response => {
                                    if (!response.ok) {
                                        throw new Error('HTTP ' + response.status + ': ' + response.statusText);
                                    }
                                    return response.json();
                                })
                                .then(data => {
                                    if (data.success) {
                                        if (typeof showToast === 'function') {
                                            showToast('Payslip generated successfully for ' + userName + '!', 'success', 'Success');
                                        }
                                        // Reload page after 1.5 seconds
                                        setTimeout(() => {
                                            window.location.reload();
                                        }, 1500);
                                    } else {
                                        if (typeof showToast === 'function') {
                                            showToast(data.error || 'Generation failed', 'error', 'Error');
                                        }
                                    }
                                })
                                .catch(error => {
                                    console.error('Generation error:', error);
                                    if (typeof showToast === 'function') {
                                        showToast('Network error: ' + error.message, 'error', 'Error');
                                    }
                                });
                        }

                        // Regenerate payslip for specific employee directly
                        function showRegenerateModalForEmployee(userId, userName) {
                            // Get current period info
                            const currentDate = new Date();
                            let prevMonth = currentDate.getMonth(); // 0-based
                            let prevYear = currentDate.getFullYear();

                            if (prevMonth === 0) {
                                prevMonth = 12;
                                prevYear = prevYear - 1;
                            }

                            const monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
                                'July', 'August', 'September', 'October', 'November', 'December'];
                            const monthName = monthNames[prevMonth - 1];

                            // Show custom confirmation dialog
                            showCustomConfirm(
                                'Regenerate Payslip',
                                'Regenerate payslip for <strong>' + userName + '</strong> for <strong>' + monthName + ' ' + prevYear + '</strong>?<br><br><span class="text-warning"><i class="fas fa-exclamation-triangle me-1"></i>This will overwrite the existing payslip.</span>',
                                'warning',
                                function () {
                                    // Regenerate directly without opening modal (force = true)
                                    generatePayslipForEmployee(userId, userName, prevMonth, prevYear, true);
                                }
                            );
                        }

                        // Custom confirm dialog function
                        function showCustomConfirm(title, message, type, onConfirm) {
                            // Create modal HTML if not exists
                            let modal = document.getElementById('customConfirmModal');
                            if (!modal) {
                                const modalHTML = `
                    <div class="modal fade" id="customConfirmModal" tabindex="-1" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered">
                            <div class="modal-content">
                                <div class="modal-header" id="confirmModalHeader">
                                    <h5 class="modal-title" id="confirmModalTitle"></h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body" id="confirmModalBody"></div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                        <i class="fas fa-times me-1"></i>Cancel
                                    </button>
                                    <button type="button" class="btn" id="confirmModalBtn">
                                        <i class="fas fa-check me-1"></i>Confirm
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                                document.body.insertAdjacentHTML('beforeend', modalHTML);
                                modal = document.getElementById('customConfirmModal');
                            }

                            // Set content
                            const titleElement = document.getElementById('confirmModalTitle');
                            titleElement.innerHTML = '<i class="fas fa-question-circle me-2"></i>' + title;
                            document.getElementById('confirmModalBody').innerHTML = message;

                            // Set button style based on type
                            const btn = document.getElementById('confirmModalBtn');
                            const header = document.getElementById('confirmModalHeader');
                            const closeBtn = header.querySelector('.btn-close');

                            btn.className = 'btn';
                            header.className = 'modal-header';
                            titleElement.className = 'modal-title';

                            if (type === 'success') {
                                btn.classList.add('btn-success');
                                header.classList.add('bg-success', 'text-white');
                                titleElement.classList.add('text-white');
                                if (closeBtn) closeBtn.classList.add('btn-close-white');
                            } else if (type === 'warning') {
                                btn.classList.add('btn-warning');
                                header.classList.add('bg-warning', 'text-dark');
                                titleElement.classList.add('text-dark');
                                if (closeBtn) closeBtn.classList.remove('btn-close-white');
                            } else if (type === 'danger') {
                                btn.classList.add('btn-danger');
                                header.classList.add('bg-danger', 'text-white');
                                titleElement.classList.add('text-white');
                                if (closeBtn) closeBtn.classList.add('btn-close-white');
                            } else {
                                btn.classList.add('btn-primary');
                                header.classList.add('bg-primary', 'text-white');
                                titleElement.classList.add('text-white');
                                if (closeBtn) closeBtn.classList.add('btn-close-white');
                            }

                            // Set confirm handler
                            btn.onclick = function () {
                                bootstrap.Modal.getInstance(modal).hide();
                                if (onConfirm) onConfirm();
                            };

                            // Show modal
                            new bootstrap.Modal(modal).show();
                        }

                        // Toggle icon rotation on collapse - Without Payslips
                        document.addEventListener('DOMContentLoaded', function () {
                            const collapseElement1 = document.getElementById('employeesWithoutPayslipCollapse');
                            if (collapseElement1) {
                                collapseElement1.addEventListener('show.bs.collapse', function () {
                                    const icon = document.querySelector('[data-bs-target="#employeesWithoutPayslipCollapse"] .toggle-icon');
                                    if (icon) icon.classList.add('rotate-180');

                                    // Lazy load employees
                                    loadEmployeesWithoutPayslip();
                                });

                                collapseElement1.addEventListener('hide.bs.collapse', function () {
                                    const icon = document.querySelector('[data-bs-target="#employeesWithoutPayslipCollapse"] .toggle-icon');
                                    if (icon) icon.classList.remove('rotate-180');
                                });
                            }

                            // Toggle icon rotation on collapse - Attendance Changes
                            const collapseElement2 = document.getElementById('employeesWithAttendanceChangesCollapse');
                            if (collapseElement2) {
                                collapseElement2.addEventListener('show.bs.collapse', function () {
                                    const icon = document.querySelector('[data-bs-target="#employeesWithAttendanceChangesCollapse"] .toggle-icon');
                                    if (icon) icon.classList.add('rotate-180');

                                    // Lazy load employees
                                    loadEmployeesWithAttendanceChanges();
                                });

                                collapseElement2.addEventListener('hide.bs.collapse', function () {
                                    const icon = document.querySelector('[data-bs-target="#employeesWithAttendanceChangesCollapse"] .toggle-icon');
                                    if (icon) icon.classList.remove('rotate-180');
                                });
                            }
                        });

                        // Auto-show toast on page load if there's a server message
                        document.addEventListener('DOMContentLoaded', function () {
                            // Initialize event listeners from payslip-actions.js
                            if (typeof initializeEventListeners === 'function') {
                                initializeEventListeners();
                            }

                            const errorInput = document.getElementById('serverError');
                            const successInput = document.getElementById('serverSuccess');

                            if (errorInput && errorInput.value && errorInput.value.trim() !== '') {
                                showToast(errorInput.value, 'danger', 'Error');
                            } else if (successInput && successInput.value && successInput.value.trim() !== '') {
                                showToast(successInput.value, 'success', 'Success');
                            }

                            // Add form submission handler for generate form
                            const generateForm = document.getElementById('generateForm');
                            if (generateForm) {
                                generateForm.addEventListener('submit', function (e) {
                                    e.preventDefault();
                                    handleBulkGenerate();
                                });
                            }
                        });

                        // Clear HRM filters
                        function clearFilters() {
                            document.getElementById('filterMonth').value = '';
                            document.getElementById('filterYear').value = '';
                            document.getElementById('departmentId').value = '';
                            document.getElementById('status').value = '';
                            const filterUserId = document.getElementById('filterUserId');
                            if (filterUserId) filterUserId.value = '';
                            const employeeSearch = document.getElementById('employeeSearch');
                            if (employeeSearch) employeeSearch.value = '';
                            document.getElementById('periodStart').value = '';
                            document.getElementById('periodEnd').value = '';
                        }

                        // Employee Filter Functions
                        function clearEmployeeFilters() {
                            document.getElementById('periodStart').value = '';
                            document.getElementById('periodEnd').value = '';
                        }

                        // Auto-set period end when period start changes
                        const periodStartInput = document.getElementById('periodStart');
                        if (periodStartInput) {
                            periodStartInput.addEventListener('change', function () {
                                const startDate = new Date(this.value);
                                if (startDate) {
                                    // Set end date to last day of the same month
                                    const endDate = new Date(startDate.getFullYear(), startDate.getMonth() + 1, 0);
                                    document.getElementById('periodEnd').value = endDate.toISOString().split('T')[0];
                                }
                            });
                        }

                        // Validate period dates for employee form
                        const employeeForm = document.getElementById('employeeFilterForm');
                        if (employeeForm) {
                            employeeForm.addEventListener('submit', function (e) {
                                const startDate = document.getElementById('periodStart').value;
                                const endDate = document.getElementById('periodEnd').value;

                                if (!startDate || !endDate) {
                                    e.preventDefault();
                                    alert('Please select both period start and end dates.');
                                    return;
                                }

                                if (new Date(startDate) > new Date(endDate)) {
                                    e.preventDefault();
                                    alert('Period start date cannot be after period end date.');
                                    return;
                                }
                            });
                        }

                    </script>
                </body>

                </html>