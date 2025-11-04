<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%
    // Retrieve form data from session (if exists from previous error)
    String savedRequestTitle = (String) session.getAttribute("formData_requestTitle");
    String savedOtDate = (String) session.getAttribute("formData_otDate");
    String savedStartTime = (String) session.getAttribute("formData_startTime");
    String savedEndTime = (String) session.getAttribute("formData_endTime");
    String savedReason = (String) session.getAttribute("formData_reason");

    // Clear form data from session after retrieving
    session.removeAttribute("formData_requestTitle");
    session.removeAttribute("formData_otDate");
    session.removeAttribute("formData_startTime");
    session.removeAttribute("formData_endTime");
    session.removeAttribute("formData_reason");

    // Make available to JSTL
    pageContext.setAttribute("savedRequestTitle", savedRequestTitle);
    pageContext.setAttribute("savedOtDate", savedOtDate);
    pageContext.setAttribute("savedStartTime", savedStartTime);
    pageContext.setAttribute("savedEndTime", savedEndTime);
    pageContext.setAttribute("savedReason", savedReason);
%>
<!DOCTYPE html>
<html lang="en">

    <head>
        <!-- CSS riêng của trang -->
        <jsp:include page="../layout/head.jsp">
            <jsp:param name="pageTitle" value="Create OT Request - HRMS" />
            <jsp:param name="pageCss" value="ot-form.css" />
        </jsp:include>
    </head>

    <body>
        <script>console.log('BODY LOADED - JavaScript is working!');</script>

        <!-- Sidebar -->
        <jsp:include page="../layout/sidebar.jsp">
            <jsp:param name="currentPage" value="ot-request" />
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
                        <li class="breadcrumb-item">
                            <a href="${pageContext.request.contextPath}/requests">
                                <i class="fas fa-clipboard-list"></i> Requests
                            </a>
                        </li>
                        <li class="breadcrumb-item active" aria-current="page">Create OT Request</li>
                    </ol>
                </nav>

                <!-- Page Title -->
                <div class="page-head d-flex justify-content-between align-items-center">
                    <div>
                        <h2 class="page-title"><i class="fas fa-clock me-2"></i>Create OT Request</h2>
                        <p class="page-subtitle">Submit a new overtime request for approval</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/requests" class="btn btn-outline-secondary">
                        <i class="fas fa-list me-1"></i> View All Requests
                    </a>
                </div>

                <!-- OT Balance Summary -->
                <c:if test="${not empty otBalance}">
                    <div class="row mb-4">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                                    <h5 class="mb-0"><i class="fas fa-chart-bar me-2"></i>Your OT Balance
                                    </h5>
                                    <div class="d-flex gap-2 align-items-center">
                                        <button class="btn btn-sm btn-light" type="button" id="otBalanceToggle" onclick="toggleOTBalance()">
                                            <i class="fas fa-eye"></i>
                                        </button>
                                    </div>
                                </div>
                                <div class="card-body" id="otBalanceContent">
                                    <div class="row g-3">
                                        <!-- Weekly Balance -->
                                        <div class="col-md-4">
                                            <div class="ot-balance-card">
                                                <div class="balance-header d-flex justify-content-between align-items-center">
                                                    <button type="button" class="btn btn-sm btn-outline-primary" onclick="changeWeek(-1)" title="Previous Week">
                                                        <i class="fas fa-chevron-left"></i>
                                                    </button>
                                                    <div class="text-center flex-grow-1">
                                                        <h6 class="balance-title mb-0">
                                                            <c:choose>
                                                                <c:when test="${empty param.weekOffset or param.weekOffset == '0'}">This Week</c:when>
                                                                <c:when test="${param.weekOffset > 0}">Week +${param.weekOffset}</c:when>
                                                                <c:otherwise>Week ${param.weekOffset}</c:otherwise>
                                                            </c:choose>
                                                        </h6>
                                                        <small class="text-muted d-block" id="weekDateRange">
                                                            <i class="fas fa-calendar"></i>
                                                            <c:out value="${otBalance.weekStartDate}"/> - <c:out value="${otBalance.weekEndDate}"/>
                                                        </small>
                                                    </div>
                                                    <button type="button" class="btn btn-sm btn-outline-primary" onclick="changeWeek(1)" title="Next Week">
                                                        <i class="fas fa-chevron-right"></i>
                                                    </button>
                                                </div>

                                                <div class="balance-stats mt-3">
                                                    <div class="stat-main">
                                                            <div>
                                                                <span class="stat-value" id="weeklyHours">
                                                                    <fmt:formatNumber value="${otBalance.currentWeekHours}" minFractionDigits="1" maxFractionDigits="1"/>h
                                                                    / <fmt:formatNumber value="${otBalance.weeklyLimit - otBalance.regularHoursThisWeek}" minFractionDigits="1" maxFractionDigits="1"/>h
                                                                </span>
                                                            </div>
                                                        </div>
                                                    <!-- Expose regular scheduled hours this week for client-side preview -->
                                                    <span id="regularHoursThisWeek" style="display:none">${otBalance.regularHoursThisWeek}</span>
                                                    <div class="stat-remaining">
                                                        <span class="stat-label">Remaining:</span>
                                                        <span class="stat-value-sm" id="weeklyRemaining"><fmt:formatNumber value="${otBalance.weeklyRemaining}" minFractionDigits="1" maxFractionDigits="1"/>h</span>
                                                    </div>
                                                        <div class="stat-extra text-muted mt-1">
                                                            <small>Approved requests: <span id="weeklyApprovedCount"><c:out value="${otBalance.weeklyApprovedCount}"/></span></small>
                                                        </div>
                                                    <small class="text-muted d-block mt-1">
                                                        <i class="fas fa-info-circle"></i>
                                                        Total work hours (regular + OT) cannot exceed
                                                        48h/week
                                                    </small>
                                                </div>
                                                <div class="balance-progress">
                                                    <div class="progress" style="height: 10px;">
                                                        <c:set var="weeklyPct"
                                                               value="${otBalance.weeklyPercentage}" />
                                                        <c:choose>
                                                            <c:when test="${weeklyPct >= 95}">
                                                                <c:set var="weeklyColor"
                                                                       value="bg-danger" />
                                                            </c:when>
                                                            <c:when test="${weeklyPct >= 80}">
                                                                <c:set var="weeklyColor"
                                                                       value="bg-warning" />
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="weeklyColor"
                                                                       value="bg-success" />
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <div class="progress-bar ${weeklyColor}" id="weeklyProgressBar"
                                                             role="progressbar" style="width: ${weeklyPct}%"
                                                             aria-valuenow="${otBalance.currentWeekHours}"
                                                             aria-valuemin="0"
                                                             aria-valuemax="${otBalance.weeklyLimit}">
                                                        </div>
                                                    </div>
                                                    <small class="text-muted"><span id="weeklyPercentage">${weeklyPct}</span>% used</small>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Monthly Balance -->
                                        <div class="col-md-4">
                                            <div class="ot-balance-card">
                                                <div class="balance-header d-flex justify-content-between align-items-center">
                                                    <button type="button" class="btn btn-sm btn-outline-primary" onclick="changeMonth(-1)" title="Previous Month">
                                                        <i class="fas fa-chevron-left"></i>
                                                    </button>
                                                    <div class="text-center flex-grow-1">
                                                        <h6 class="balance-title mb-0">
                                                            <c:choose>
                                                                <c:when test="${empty param.monthOffset or param.monthOffset == '0'}">This Month</c:when>
                                                                <c:when test="${param.monthOffset > 0}">Month +${param.monthOffset}</c:when>
                                                                <c:otherwise>Month ${param.monthOffset}</c:otherwise>
                                                            </c:choose>
                                                        </h6>
                                                        <small class="text-muted d-block" id="monthNameDisplay">
                                                            <i class="fas fa-calendar"></i>
                                                            <c:out value="${otBalance.monthName}"/>
                                                        </small>
                                                    </div>
                                                    <button type="button" class="btn btn-sm btn-outline-primary" onclick="changeMonth(1)" title="Next Month">
                                                        <i class="fas fa-chevron-right"></i>
                                                    </button>
                                                </div>

                                                <div class="balance-stats mt-3">
                                                    <div class="stat-main">
                                                                    <span class="stat-value" id="monthlyHours">
                                                                        <fmt:formatNumber value="${otBalance.monthlyHours}" minFractionDigits="1" maxFractionDigits="1"/>h
                                                                        / <fmt:formatNumber value="${otBalance.monthlyLimit}" minFractionDigits="1" maxFractionDigits="1"/>h
                                                                    </span>
                                                    </div>
                                                    <div class="stat-remaining">
                                                        <span class="stat-label">Remaining:</span>
                                                        <span class="stat-value-sm" id="monthlyRemaining"><fmt:formatNumber value="${otBalance.monthlyRemaining}" minFractionDigits="1" maxFractionDigits="1"/>h</span>
                                                    </div>
                                                                <div class="stat-extra text-muted mt-1">
                                                                    <small>Approved requests: <span id="monthlyApprovedCount"><c:out value="${otBalance.monthlyApprovedCount}"/></span></small>
                                                                </div>
                                                                <small class="text-muted d-block mt-1">
                                                                    <i class="fas fa-info-circle"></i>
                                                                    Monthly OT limit
                                                                </small>
                                                </div>
                                                <div class="balance-progress">
                                                    <div class="progress" style="height: 10px;">
                                                        <c:set var="monthlyPct"
                                                               value="${otBalance.monthlyPercentage}" />
                                                        <c:choose>
                                                            <c:when test="${monthlyPct >= 95}">
                                                                <c:set var="monthlyColor"
                                                                       value="bg-danger" />
                                                            </c:when>
                                                            <c:when test="${monthlyPct >= 80}">
                                                                <c:set var="monthlyColor"
                                                                       value="bg-warning" />
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="monthlyColor"
                                                                       value="bg-success" />
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <div class="progress-bar ${monthlyColor}" id="monthlyProgressBar"
                                                             role="progressbar" style="width: ${monthlyPct}%"
                                                             aria-valuenow="${otBalance.monthlyHours}"
                                                             aria-valuemin="0"
                                                             aria-valuemax="${otBalance.monthlyLimit}">

                                                        </div>
                                                    </div>
                                                    <small class="text-muted"><span id="monthlyPercentage">${monthlyPct}</span>% used</small>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Annual Balance -->
                                        <div class="col-md-4">
                                            <div class="ot-balance-card">
                                                <div class="balance-header d-flex justify-content-between align-items-center">
                                                    <button type="button" class="btn btn-sm btn-outline-primary" onclick="changeYear(-1)" title="Previous Year">
                                                        <i class="fas fa-chevron-left"></i>
                                                    </button>
                                                    <div class="text-center">
                                                        <h6 class="balance-title mb-0">
                                                            <c:choose>
                                                                <c:when test="${empty param.yearOffset or param.yearOffset == '0'}">This Year</c:when>
                                                                <c:when test="${param.yearOffset > 0}">Year +${param.yearOffset}</c:when>
                                                                <c:otherwise>Year ${param.yearOffset}</c:otherwise>
                                                            </c:choose>
                                                        </h6>
                                                        <small class="text-muted" id="yearLabel">
                                                            <i class="fas fa-calendar"></i> ${empty param.yearOffset ? '2025' : (2025 + param.yearOffset)}
                                                        </small>
                                                    </div>
                                                    <button type="button" class="btn btn-sm btn-outline-primary" onclick="changeYear(1)" title="Next Year">
                                                        <i class="fas fa-chevron-right"></i>
                                                    </button>
                                                </div>
                                                <div class="balance-stats">
                                                    <div class="stat-main">
                                                        <span class="stat-value">
                                                            <fmt:formatNumber value="${otBalance.annualHours}" minFractionDigits="1" maxFractionDigits="1"/>h
                                                            / <fmt:formatNumber value="${otBalance.annualLimit}" minFractionDigits="1" maxFractionDigits="1"/>h
                                                        </span>
                                                    </div>
                                                    <div class="stat-remaining">
                                                        <span class="stat-label">Remaining:</span>
                                                        <span
                                                            class="stat-value-sm"><fmt:formatNumber value="${otBalance.annualRemaining}" minFractionDigits="1" maxFractionDigits="1"/>h</span>
                                                    </div>
                                                    <div class="stat-extra text-muted mt-1">
                                                        <small>Approved requests: <c:out value="${otBalance.annualApprovedCount}"/></small>
                                                    </div>
                                                    <small class="text-muted d-block mt-1">
                                                        <i class="fas fa-info-circle"></i>
                                                        Annual OT limit
                                                    </small>
                                                </div>
                                                <div class="balance-progress">
                                                    <div class="progress" style="height: 10px;">
                                                        <c:set var="annualPct"
                                                               value="${otBalance.annualPercentage}" />
                                                        <c:choose>
                                                            <c:when test="${annualPct >= 95}">
                                                                <c:set var="annualColor"
                                                                       value="bg-danger" />
                                                            </c:when>
                                                            <c:when test="${annualPct >= 80}">
                                                                <c:set var="annualColor"
                                                                       value="bg-warning" />
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="annualColor"
                                                                       value="bg-success" />
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <div class="progress-bar ${annualColor}"
                                                             role="progressbar" style="width: ${annualPct}%"
                                                             aria-valuenow="${otBalance.annualHours}"
                                                             aria-valuemin="0"
                                                             aria-valuemax="${otBalance.annualLimit}">
                                                        </div>
                                                    </div>
                                                    <small class="text-muted">${annualPct}% used</small>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

                <!-- Form Card -->
                <div class="card ot-request-card">
                    <div class="card-header">
                        <h4><i class="fas fa-clock me-2"></i> OT Request Form</h4>
                    </div>

                    <div class="card-body">
                        <!-- Server messages will be shown as Toast notifications -->
                        <c:if test="${not empty error}">
                            <input type="hidden" id="serverError" value="<c:out value='${error}'/>" />
                        </c:if>

                        <c:if test="${not empty success}">
                            <input type="hidden" id="serverSuccess" value="<c:out value='${success}'/>" />
                        </c:if>

                        <!-- Holiday Data for JavaScript -->
                        <script type="application/json" id="holidaysData">
                            {
                                "holidays": [
                                    <c:forEach var="holiday" items="${holidays}" varStatus="status">
                                        "${holiday}"<c:if test="${!status.last}">,</c:if>
                                    </c:forEach>
                                ],
                                "compensatoryDays": [
                                    <c:forEach var="compensatoryDay" items="${compensatoryDays}" varStatus="status">
                                        "${compensatoryDay}"<c:if test="${!status.last}">,</c:if>
                                    </c:forEach>
                                ]
                            }
                        </script>

                        <!-- Form -->
                        <form method="post" action="${pageContext.request.contextPath}/requests/ot/create"
                              id="otRequestForm" enctype="multipart/form-data" novalidate>

                            <!-- Employee Selection -->
                            <div class="mb-4">
                                <label class="form-label">
                                    <i class="fas fa-user-check"></i> Create Request For
                                    <span class="text-danger">*</span>
                                </label>
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" name="requestFor"
                                           id="requestForSelf" value="self" checked>
                                    <label class="form-check-label" for="requestForSelf">
                                        <strong>Create for myself</strong>
                                    </label>
                                </div>
                                <div class="form-check mt-2">
                                    <input class="form-check-input" type="radio" name="requestFor"
                                           id="requestForEmployee" value="employee" <c:if
                                               test="${empty departmentEmployees}">disabled</c:if>>
                                           <label class="form-check-label" for="requestForEmployee">
                                               <strong>Create for subordinate</strong>
                                           <c:if test="${empty departmentEmployees}">
                                               <span class="text-muted">(Not available - you are not a manager
                                                   or
                                                   have no subordinates)</span>
                                               </c:if>
                                    </label>
                                </div>
                                <c:if test="${not empty departmentEmployees}">
                                    <div id="employeeSelectionDiv" class="mt-3 d-none">
                                        <label for="selectedEmployeeId" class="form-label">
                                            <i class="fas fa-users"></i> Select Employee
                                        </label>
                                        <select class="form-select" id="selectedEmployeeId"
                                                name="selectedEmployeeId">
                                            <option value="">-- Select an employee --</option>
                                            <c:forEach var="employee" items="${departmentEmployees}">
                                                <option value="${employee.id}">${employee.fullName} -
                                                    ${employee.employeeCode}</option>
                                                </c:forEach>
                                        </select>
                                        <div class="form-text">
                                            <i class="fas fa-info-circle"></i>
                                            You can create OT requests for your subordinates (employees with
                                            lower job level)
                                        </div>
                                    </div>
                                </c:if>
                            </div>

                            <!-- Request Title -->
                            <div class="mb-4">
                                <label for="requestTitle" class="form-label">
                                    <i class="fas fa-heading"></i> Request Title
                                    <span class="text-danger">*</span>
                                </label>
                                <input type="text" class="form-control" id="requestTitle" name="requestTitle"
                                       placeholder="Enter a brief title for your OT request (e.g., 'Urgent project deadline')"
                                       maxlength="200" required value="${not empty savedRequestTitle ? savedRequestTitle : ''}">
                                <div class="form-text">
                                    <i class="fas fa-info-circle"></i>
                                    Provide a clear title that summarizes your OT request (max 200 characters)
                                </div>
                            </div>

                            <!-- OT Date and Type Display in one row -->
                            <div class="row g-3 mb-3">
                                <!-- OT Date -->
                                <div class="col-md-4">
                                    <label for="otDate" class="form-label">
                                        <i class="fas fa-calendar-day"></i> OT Date
                                        <span class="text-danger">*</span>
                                    </label>
                                    <input type="date" class="form-control" id="otDate" name="otDate"
                                           value="${not empty savedOtDate ? savedOtDate : ''}" required>
                                    <div class="form-text">Select the date you want to work overtime</div>
                                </div>

                                <!-- OT Type Display (auto-determined) -->
                                <div class="col-md-8">
                                    <label class="form-label">
                                        <i class="fas fa-info-circle"></i> Day Type & Pay Rate
                                    </label>
                                    <div id="otTypeDisplay" class="ot-type-display d-none">
                                        <div class="alert alert-info mb-0">
                                            <div class="row align-items-center">
                                                <div class="col-md-6">
                                                    <div class="ot-type-info">
                                                        <div class="ot-type-label">
                                                            <i class="fas fa-tag"></i> Day Type:
                                                        </div>
                                                        <div class="ot-type-badge" id="otTypeBadge"></div>
                                                    </div>
                                                </div>
                                                <div class="col-md-6">
                                                    <div class="ot-type-info">
                                                        <div class="ot-type-label">
                                                            <i class="fas fa-money-bill-wave"></i> Pay Rate:
                                                        </div>
                                                        <div class="ot-type-multiplier"
                                                             id="otTypeMultiplier">
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div id="otTypePlaceholder" class="ot-type-placeholder">
                                        <small class="text-muted">
                                            <i class="fas fa-arrow-left"></i> Select a date to see day type
                                            and
                                            pay rate
                                        </small>
                                    </div>
                                </div>
                            </div>

                            <!-- Time Range -->
                            <div class="row g-3">
                                <!-- Start Time -->
                                <div class="col-md-6">
                                    <label class="form-label">
                                        <i class="fas fa-clock"></i> Start Time
                                        <span class="text-danger">*</span>
                                    </label>
                                    <div class="row g-2">
                                        <div class="col-6">
                                            <select class="form-select" id="startHour" required>
                                                <option value="">Hour</option>
                                                <option value="06">06</option>
                                                <option value="07">07</option>
                                                <option value="08">08</option>
                                                <option value="09">09</option>
                                                <option value="10">10</option>
                                                <option value="11">11</option>
                                                <option value="12">12</option>
                                                <option value="13">13</option>
                                                <option value="14">14</option>
                                                <option value="15">15</option>
                                                <option value="16">16</option>
                                                <option value="17">17</option>
                                                <option value="18">18</option>
                                                <option value="19">19</option>
                                                <option value="20">20</option>
                                                <option value="21">21</option>
                                                <option value="22">22</option>
                                            </select>
                                        </div>
                                        <div class="col-6">
                                            <select class="form-select" id="startMinute" required>
                                                <option value="">Minute</option>
                                                <option value="00">00</option>
                                                <option value="15">15</option>
                                                <option value="30">30</option>
                                                <option value="45">45</option>
                                            </select>
                                        </div>
                                    </div>
                                    <input type="hidden" id="startTime" name="startTime" required>
                                    <div class="form-text">
                                        <i class="fas fa-info-circle"></i>
                                        Time: 06:00 - 22:00. <strong>Weekday OT: 19:00-22:00 (max
                                            2h)</strong>
                                    </div>
                                </div>

                                <!-- End Time -->
                                <div class="col-md-6">
                                    <label class="form-label">
                                        <i class="fas fa-clock"></i> End Time
                                        <span class="text-danger">*</span>
                                    </label>
                                    <div class="row g-2">
                                        <div class="col-6">
                                            <select class="form-select" id="endHour" required>
                                                <option value="">Hour</option>
                                                <option value="06">06</option>
                                                <option value="07">07</option>
                                                <option value="08">08</option>
                                                <option value="09">09</option>
                                                <option value="10">10</option>
                                                <option value="11">11</option>
                                                <option value="12">12</option>
                                                <option value="13">13</option>
                                                <option value="14">14</option>
                                                <option value="15">15</option>
                                                <option value="16">16</option>
                                                <option value="17">17</option>
                                                <option value="18">18</option>
                                                <option value="19">19</option>
                                                <option value="20">20</option>
                                                <option value="21">21</option>
                                                <option value="22">22</option>
                                            </select>
                                        </div>
                                        <div class="col-6">
                                            <select class="form-select" id="endMinute" required>
                                                <option value="">Minute</option>
                                                <option value="00">00</option>
                                                <option value="15">15</option>
                                                <option value="30">30</option>
                                                <option value="45">45</option>
                                            </select>
                                        </div>
                                    </div>
                                    <input type="hidden" id="endTime" name="endTime" required>
                                    <div class="form-text">
                                        <i class="fas fa-info-circle"></i>
                                        Time: 06:00 - 22:00. <strong>Weekday OT: 19:00-22:00 (max
                                            2h)</strong>
                                    </div>
                                </div>
                            </div>

                            <!-- OT Hours Display (auto-calculated) -->
                            <div class="mt-3">
                                <label class="form-label">
                                    <i class="fas fa-hourglass-half"></i> OT Hours (Calculated)
                                </label>
                                <div class="ot-hours-display" id="otHoursDisplay">
                                    <i class="fas fa-calculator"></i>
                                    <span id="otHoursText">Enter start and end time</span>
                                </div>
                                <small class="text-muted" id="otLimitMessage">
                                    <i class="fas fa-info-circle"></i>
                                    <strong>Weekday:</strong> Max 2 hours OT (8h regular + 2h OT = 10h
                                    total) |
                                    <strong>Weekend/Holiday:</strong> Max 10 hours OT
                                </small>
                            </div>

                            <!-- Reason -->
                            <div class="mt-3">
                                <label for="reason" class="form-label">
                                    <i class="fas fa-comment"></i> Reason
                                    <span class="text-danger">*</span>
                                </label>
                                <textarea class="form-control" id="reason" name="reason" rows="5"
                                          maxlength="1000"
                                          placeholder="Please provide the reason for overtime work..."
                                          required>${not empty savedReason ? savedReason : ''}</textarea>
                                <div class="char-counter">
                                    <span id="charCount">0</span>/1000 characters
                                </div>
                            </div>

                            <!-- Supporting Documents (Optional) - Hybrid: File Upload or Link -->
                            <div class="mt-3">
                                <label class="form-label">
                                    <i class="fas fa-paperclip"></i> Supporting Documents
                                    <span class="text-muted">(Optional)</span>
                                </label>

                                <!-- Attachment Type Selection -->
                                <div class="btn-group w-100 mb-3" role="group" aria-label="Attachment Type">
                                    <input type="radio" class="btn-check" name="attachmentType" id="attachmentTypeFile"
                                           value="file" checked autocomplete="off">
                                    <label class="btn btn-outline-primary" for="attachmentTypeFile">
                                        <i class="fas fa-upload me-1"></i> Upload File
                                    </label>

                                    <input type="radio" class="btn-check" name="attachmentType" id="attachmentTypeLink"
                                           value="link" autocomplete="off">
                                    <label class="btn btn-outline-primary" for="attachmentTypeLink">
                                        <i class="fab fa-google-drive me-1"></i> Google Drive Link
                                    </label>
                                </div>

                                <!-- File Upload Section -->
                                <div id="fileUploadSection" class="file-upload-wrapper">
                                    <input type="file" class="form-control" id="attachments" name="attachments"
                                           accept=".pdf,.jpg,.jpeg,.png,.doc,.docx" multiple>
                                    <div class="form-text">
                                        <i class="fas fa-info-circle"></i>
                                        Accepted formats: PDF, JPG, PNG, DOC, DOCX (Max 5MB each)
                                    </div>
                                    <div id="filePreviewList" class="file-preview-list mt-2"></div>
                                </div>

                                <!-- Google Drive Link Section -->
                                <div id="driveLinkSection" class="drive-link-wrapper d-none">
                                    <input type="url" class="form-control" id="driveLink" name="driveLink"
                                           placeholder="Paste Google Drive link here (e.g., https://drive.google.com/file/d/...)">
                                    <div class="form-text">
                                        <i class="fas fa-info-circle"></i>
                                        Paste a shareable Google Drive link to your supporting document
                                    </div>
                                    <div id="driveLinkPreview" class="alert alert-info mt-2 d-none">
                                        <i class="fab fa-google-drive me-2"></i>
                                        <strong>Drive Link:</strong> <span id="driveLinkText"></span>
                                        <button type="button" class="btn-close float-end" onclick="clearDriveLink()"></button>
                                    </div>
                                </div>
                            </div>

                            <!-- Employee Consent (only shown when creating for self) -->
                            <div class="mt-3" id="employeeConsentDiv">
                                <div class="form-check consent-checkbox">
                                    <input class="form-check-input" type="checkbox" id="employeeConsent"
                                           name="employeeConsent">
                                    <label class="form-check-label" for="employeeConsent">
                                        <strong>I voluntarily agree to work overtime as requested</strong>
                                        <span class="text-danger">*</span>
                                        <div class="consent-note">
                                            <i class="fas fa-info-circle"></i>
                                            Required by BR-AD-06: Employee consent is mandatory for all
                                            overtime
                                            work
                                        </div>
                                    </label>
                                </div>
                            </div>

                            <!-- Actions -->
                            <div class="mt-4 d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="${pageContext.request.contextPath}/requests"
                                   class="btn btn-ot-secondary">
                                    <i class="fas fa-times me-1"></i> Cancel
                                </a>
                                <button type="submit" class="btn btn-ot-primary" id="submitBtn">
                                    <i class="fas fa-paper-plane me-1"></i> Submit Request
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Footer -->
            <jsp:include page="../layout/dashboard-footer.jsp" />
        </div>

        <!-- Holidays Data from Server -->
        <script type="application/json" id="holidaysData">
            {
            "holidays": [
            <c:if test="${not empty holidays}">
                <c:forEach var="holiday" items="${holidays}" varStatus="status">
                    "<c:out value='${holiday}'/>"<c:if test="${not status.last}">,</c:if>
                </c:forEach>
            </c:if>
            ],
            "compensatoryDays": [
            <c:if test="${not empty compensatoryDays}">
                <c:forEach var="compDay" items="${compensatoryDays}" varStatus="status">
                    "<c:out value='${compDay}'/>"<c:if test="${not status.last}">,</c:if>
                </c:forEach>
            </c:if>
            ]
            }
        </script>

        <script>
            // Time dropdown handler
            document.addEventListener('DOMContentLoaded', function () {
                const startHour = document.getElementById('startHour');
                const startMinute = document.getElementById('startMinute');
                const startTime = document.getElementById('startTime');
                const endHour = document.getElementById('endHour');
                const endMinute = document.getElementById('endMinute');
                const endTime = document.getElementById('endTime');

                // Function to update hidden time input
                function updateStartTime() {
                    if (startHour.value && startMinute.value) {
                        startTime.value = startHour.value + ':' + startMinute.value;
                        // Trigger change event for OT hours calculation
                        startTime.dispatchEvent(new Event('change'));
                    } else {
                        startTime.value = '';
                    }
                }

                function updateEndTime() {
                    if (endHour.value && endMinute.value) {
                        endTime.value = endHour.value + ':' + endMinute.value;
                        // Trigger change event for OT hours calculation
                        endTime.dispatchEvent(new Event('change'));
                    } else {
                        endTime.value = '';
                    }
                }

                // Add event listeners
                startHour.addEventListener('change', updateStartTime);
                startMinute.addEventListener('change', updateStartTime);
                endHour.addEventListener('change', updateEndTime);
                endMinute.addEventListener('change', updateEndTime);

                // Restore saved values if exists
                const savedStartTime = '${savedStartTime}';
                const savedEndTime = '${savedEndTime}';

                if (savedStartTime) {
                    const parts = savedStartTime.split(':');
                    if (parts.length >= 2) {
                        startHour.value = parts[0];
                        startMinute.value = parts[1];
                        updateStartTime();
                    }
                }

                if (savedEndTime) {
                    const parts = savedEndTime.split(':');
                    if (parts.length >= 2) {
                        endHour.value = parts[0];
                        endMinute.value = parts[1];
                        updateEndTime();
                    }
                }

                // Employee selection toggle
                const requestForSelf = document.getElementById('requestForSelf');
                const requestForEmployee = document.getElementById('requestForEmployee');
                const employeeSelectionDiv = document.getElementById('employeeSelectionDiv');
                const employeeConsentDiv = document.getElementById('employeeConsentDiv');
                const employeeConsentCheckbox = document.getElementById('employeeConsent');

                // Initialize: consent is required by default (creating for self)
                if (employeeConsentCheckbox) {
                    employeeConsentCheckbox.required = true;
                }

                if (requestForSelf && requestForEmployee && employeeSelectionDiv) {
                    requestForSelf.addEventListener('change', function () {
                        if (this.checked) {
                            // Creating for self
                            employeeSelectionDiv.classList.add('d-none');
                            document.getElementById('selectedEmployeeId').value = '';

                            // Show consent and make it required
                            if (employeeConsentDiv) {
                                employeeConsentDiv.classList.remove('d-none');
                            }
                            if (employeeConsentCheckbox) {
                                employeeConsentCheckbox.required = true;
                                employeeConsentCheckbox.checked = false; // Uncheck so user must manually agree
                            }
                        }
                    });

                    requestForEmployee.addEventListener('change', function () {
                        if (this.checked) {
                            // Creating for subordinate
                            employeeSelectionDiv.classList.remove('d-none');

                            // Hide consent and auto-check it (consent not required from manager)
                            if (employeeConsentDiv) {
                                employeeConsentDiv.classList.add('d-none');
                            }
                            if (employeeConsentCheckbox) {
                                employeeConsentCheckbox.required = false;
                                employeeConsentCheckbox.checked = true; // Auto-check when creating for subordinate
                            }
                        }
                    });
                }
            });
        </script>

        <!-- Toast Notification Container -->
        <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 11000;">
            <div id="validationToast" class="toast border-0 shadow-lg" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="toast-header">
                    <i class="fas fa-exclamation-circle text-danger me-2" id="toastIcon"></i>
                    <strong class="me-auto" id="toastTitle">Validation Error</strong>
                    <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
                <div class="toast-body" id="toastMessage">
                    Please check your input.
                </div>
            </div>
        </div>

        <style>
            /* Toast notification styling */
            #validationToast {
                min-width: 350px;
                max-width: 550px;
            }

            #validationToast .toast-header {
                border-radius: 0.375rem 0.375rem 0 0;
            }

            #validationToast .toast-header.bg-danger {
                background-color: #dc3545 !important;
            }

            #validationToast .toast-header.bg-warning {
                background-color: #ffc107 !important;
                color: #000 !important;
            }

            #validationToast .toast-header.bg-success {
                background-color: #198754 !important;
            }

            #validationToast .toast-header.bg-info {
                background-color: #0dcaf0 !important;
            }

            #validationToast .toast-body {
                font-size: 0.95rem;
                padding: 1rem;
                line-height: 1.5;
            }

            /* Animation */
            #validationToast.show {
                animation: slideInRight 0.3s ease-out;
            }

            @keyframes slideInRight {
                from {
                    transform: translateX(100%);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
        </style>

        <!-- Form JavaScript -->
        <script src="${pageContext.request.contextPath}/assets/js/ot-form.js"></script>

    </body>

</html>

<!-- Toggle OT Balance -->
<script>
            function toggleOTBalance() {
                const content = document.getElementById('otBalanceContent');
                const toggle = document.getElementById('otBalanceToggle');
                const icon = toggle.querySelector('i');

                if (content.style.display === 'none') {
                    content.style.display = 'block';
                    icon.classList.remove('fa-eye-slash');
                    icon.classList.add('fa-eye');
                } else {
                    content.style.display = 'none';
                    icon.classList.remove('fa-eye');
                    icon.classList.add('fa-eye-slash');
                }
            }

            // Week/Month Navigation for OT Balance
            function changeWeek(offset) {
                // Get current URL parameters
                const urlParams = new URLSearchParams(window.location.search);
                const currentWeekOffset = parseInt(urlParams.get('weekOffset') || '0');
                const currentMonthOffset = parseInt(urlParams.get('monthOffset') || '0');
                const currentYearOffset = parseInt(urlParams.get('yearOffset') || '0');
                const newWeekOffset = currentWeekOffset + offset;

                // Reload page with ALL offsets preserved
                const contextPath = '${pageContext.request.contextPath}';
                window.location.href = contextPath + '/requests/ot/create?weekOffset=' + newWeekOffset + '&monthOffset=' + currentMonthOffset + '&yearOffset=' + currentYearOffset;
            }

            function changeMonth(offset) {
                // Get current URL parameters
                const urlParams = new URLSearchParams(window.location.search);
                const currentWeekOffset = parseInt(urlParams.get('weekOffset') || '0');
                const currentMonthOffset = parseInt(urlParams.get('monthOffset') || '0');
                const currentYearOffset = parseInt(urlParams.get('yearOffset') || '0');
                const newMonthOffset = currentMonthOffset + offset;

                // Reload page with ALL offsets preserved
                const contextPath = '${pageContext.request.contextPath}';
                window.location.href = contextPath + '/requests/ot/create?weekOffset=' + currentWeekOffset + '&monthOffset=' + newMonthOffset + '&yearOffset=' + currentYearOffset;
            }

            function changeYear(offset) {
                // Get current URL parameters
                const urlParams = new URLSearchParams(window.location.search);
                const currentWeekOffset = parseInt(urlParams.get('weekOffset') || '0');
                const currentMonthOffset = parseInt(urlParams.get('monthOffset') || '0');
                const currentYearOffset = parseInt(urlParams.get('yearOffset') || '0');
                const newYearOffset = currentYearOffset + offset;

                // Reload page with ALL offsets preserved
                const contextPath = '${pageContext.request.contextPath}';
                window.location.href = contextPath + '/requests/ot/create?weekOffset=' + currentWeekOffset + '&monthOffset=' + currentMonthOffset + '&yearOffset=' + newYearOffset;
            }

            // File upload handler
            const attachmentInput = document.getElementById('attachment');
            const fileInfo = document.getElementById('fileInfo');
            const fileName = document.getElementById('fileName');
            const removeFileBtn = document.getElementById('removeFile');

            if (attachmentInput) {
                attachmentInput.addEventListener('change', function (e) {
                    const file = e.target.files[0];
                    if (file) {
                        // Check file size (5MB max)
                        if (file.size > 5 * 1024 * 1024) {
                            alert('File size must not exceed 5MB');
                            this.value = '';
                            return;
                        }

                        // Show file info
                        fileName.textContent = file.name;
                        fileInfo.classList.remove('d-none');
                    }
                });
            }

            if (removeFileBtn) {
                removeFileBtn.addEventListener('click', function () {
                    attachmentInput.value = '';
                    fileInfo.classList.add('d-none');
                    fileName.textContent = '';
                });
            }

            // Character counter for reason
            const reasonTextarea = document.getElementById('reason');
            const charCount = document.getElementById('charCount');

            if (reasonTextarea && charCount) {
                reasonTextarea.addEventListener('input', function () {
                    charCount.textContent = this.value.length;
                });
            }
</script>

<!-- Attachment Toggle Script -->
<script src="${pageContext.request.contextPath}/assets/js/attachment-toggle.js"></script>

    <!-- Make data available globally for validation -->
    <script>
        // Global variables for validation
        window.holidays = [];
        window.compensatoryDays = [];

        try {
            const holidaysData = JSON.parse(document.getElementById('holidaysData').textContent);
            window.holidays = holidaysData.holidays || [];
            window.compensatoryDays = holidaysData.compensatoryDays || [];
        } catch (e) {
            console.warn('Could not load holidays data:', e);
        }
    </script>

    <!-- OT Form Logic -->
    <script src="${pageContext.request.contextPath}/assets/js/ot-form.js"></script>

    <!-- Enhanced Form Validation -->
    <script src="${pageContext.request.contextPath}/assets/js/ot-form-validation.js"></script>

    </body>

</html>