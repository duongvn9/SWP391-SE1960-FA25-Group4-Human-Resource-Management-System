<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
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
                        <!-- Page Title -->
                        <div class="page-head">
                            <h2 class="page-title"><i class="fas fa-clock me-2"></i>Create OT Request</h2>
                            <p class="page-subtitle">Submit a new overtime request for approval</p>
                        </div>

                        <!-- OT Balance Summary -->
                        <c:if test="${not empty otBalance}">
                            <div class="row mb-4">
                                <div class="col-12">
                                    <div class="card">
                                        <div class="card-header bg-primary text-white">
                                            <h5 class="mb-0"><i class="fas fa-chart-bar me-2"></i>Your OT Balance</h5>
                                        </div>
                                        <div class="card-body">
                                            <div class="row g-3">
                                                <!-- Weekly Balance -->
                                                <div class="col-md-4">
                                                    <div class="ot-balance-card">
                                                        <div class="balance-header">
                                                            <h6 class="balance-title">This Week</h6>
                                                            <span class="balance-icon"><i
                                                                    class="fas fa-calendar-week"></i></span>
                                                        </div>
                                                        <div class="balance-stats">
                                                            <div class="stat-main">
                                                                <span
                                                                    class="stat-value">${otBalance.currentWeekHours}h</span>
                                                                <span class="stat-separator">/</span>
                                                                <span
                                                                    class="stat-limit">${otBalance.weeklyLimit}h</span>
                                                            </div>
                                                            <div class="stat-remaining">
                                                                <span class="stat-label">Remaining:</span>
                                                                <span
                                                                    class="stat-value-sm">${otBalance.weeklyRemaining}h</span>
                                                            </div>
                                                            <small class="text-muted d-block mt-1">
                                                                <i class="fas fa-info-circle"></i>
                                                                Total work hours (regular + OT) cannot exceed 48h/week
                                                            </small>
                                                        </div>
                                                        <div class="balance-progress">
                                                            <div class="progress" style="height: 10px;">
                                                                <c:set var="weeklyPct"
                                                                    value="${otBalance.weeklyPercentage}" />
                                                                <c:choose>
                                                                    <c:when test="${weeklyPct >= 95}">
                                                                        <c:set var="weeklyColor" value="bg-danger" />
                                                                    </c:when>
                                                                    <c:when test="${weeklyPct >= 80}">
                                                                        <c:set var="weeklyColor" value="bg-warning" />
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <c:set var="weeklyColor" value="bg-success" />
                                                                    </c:otherwise>
                                                                </c:choose>
                                                                <div class="progress-bar ${weeklyColor}"
                                                                    role="progressbar" style="width: ${weeklyPct}%"
                                                                    aria-valuenow="${otBalance.currentWeekHours}"
                                                                    aria-valuemin="0"
                                                                    aria-valuemax="${otBalance.weeklyLimit}">
                                                                </div>
                                                            </div>
                                                            <small class="text-muted">${weeklyPct}% used</small>
                                                        </div>
                                                    </div>
                                                </div>

                                                <!-- Monthly Balance -->
                                                <div class="col-md-4">
                                                    <div class="ot-balance-card">
                                                        <div class="balance-header">
                                                            <h6 class="balance-title">This Month</h6>
                                                            <span class="balance-icon"><i
                                                                    class="fas fa-calendar-alt"></i></span>
                                                        </div>
                                                        <div class="balance-stats">
                                                            <div class="stat-main">
                                                                <span
                                                                    class="stat-value">${otBalance.monthlyHours}h</span>
                                                                <span class="stat-separator">/</span>
                                                                <span
                                                                    class="stat-limit">${otBalance.monthlyLimit}h</span>
                                                            </div>
                                                            <div class="stat-remaining">
                                                                <span class="stat-label">Remaining:</span>
                                                                <span
                                                                    class="stat-value-sm">${otBalance.monthlyRemaining}h</span>
                                                            </div>
                                                            <small class="text-muted d-block mt-1">
                                                                <i class="fas fa-info-circle"></i>
                                                                Monthly OT limit (includes weekends & holidays)
                                                            </small>
                                                        </div>
                                                        <div class="balance-progress">
                                                            <div class="progress" style="height: 10px;">
                                                                <c:set var="monthlyPct"
                                                                    value="${otBalance.monthlyPercentage}" />
                                                                <c:choose>
                                                                    <c:when test="${monthlyPct >= 95}">
                                                                        <c:set var="monthlyColor" value="bg-danger" />
                                                                    </c:when>
                                                                    <c:when test="${monthlyPct >= 80}">
                                                                        <c:set var="monthlyColor" value="bg-warning" />
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <c:set var="monthlyColor" value="bg-success" />
                                                                    </c:otherwise>
                                                                </c:choose>
                                                                <div class="progress-bar ${monthlyColor}"
                                                                    role="progressbar" style="width: ${monthlyPct}%"
                                                                    aria-valuenow="${otBalance.monthlyHours}"
                                                                    aria-valuemin="0"
                                                                    aria-valuemax="${otBalance.monthlyLimit}">
                                                                </div>
                                                            </div>
                                                            <small class="text-muted">${monthlyPct}% used</small>
                                                        </div>
                                                    </div>
                                                </div>

                                                <!-- Annual Balance -->
                                                <div class="col-md-4">
                                                    <div class="ot-balance-card">
                                                        <div class="balance-header">
                                                            <h6 class="balance-title">This Year</h6>
                                                            <span class="balance-icon"><i
                                                                    class="fas fa-calendar"></i></span>
                                                        </div>
                                                        <div class="balance-stats">
                                                            <div class="stat-main">
                                                                <span
                                                                    class="stat-value">${otBalance.annualHours}h</span>
                                                                <span class="stat-separator">/</span>
                                                                <span
                                                                    class="stat-limit">${otBalance.annualLimit}h</span>
                                                            </div>
                                                            <div class="stat-remaining">
                                                                <span class="stat-label">Remaining:</span>
                                                                <span
                                                                    class="stat-value-sm">${otBalance.annualRemaining}h</span>
                                                            </div>
                                                            <small class="text-muted d-block mt-1">
                                                                <i class="fas fa-info-circle"></i>
                                                                Annual OT limit (300h standard, 200h if not eligible)
                                                            </small>
                                                        </div>
                                                        <div class="balance-progress">
                                                            <div class="progress" style="height: 10px;">
                                                                <c:set var="annualPct"
                                                                    value="${otBalance.annualPercentage}" />
                                                                <c:choose>
                                                                    <c:when test="${annualPct >= 95}">
                                                                        <c:set var="annualColor" value="bg-danger" />
                                                                    </c:when>
                                                                    <c:when test="${annualPct >= 80}">
                                                                        <c:set var="annualColor" value="bg-warning" />
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <c:set var="annualColor" value="bg-success" />
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
                                <!-- Alerts -->
                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger" role="alert">
                                        <i class="fas fa-exclamation-triangle me-2"></i>
                                        <c:out value="${error}" />
                                    </div>
                                </c:if>

                                <c:if test="${not empty success}">
                                    <div class="alert alert-success" role="alert">
                                        <i class="fas fa-check-circle me-2"></i>
                                        <c:out value="${success}" />
                                    </div>
                                </c:if>

                                <!-- Form -->
                                <form method="post" action="${pageContext.request.contextPath}/request/ot"
                                    id="otRequestForm" novalidate>
                                    <input type="hidden" name="action" value="create">

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
                                                    <span class="text-muted">(Not available - you are not a manager or
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
                                                    You can create OT requests for employees in your department
                                                </div>
                                            </div>
                                        </c:if>
                                    </div>

                                    <!-- OT Date and Type Display in one row -->
                                    <div class="row g-3 mb-3">
                                        <!-- OT Date -->
                                        <div class="col-md-4">
                                            <label for="otDate" class="form-label">
                                                <i class="fas fa-calendar-day"></i> OT Date
                                                <span class="text-danger">*</span>
                                            </label>
                                            <input type="date" class="form-control" id="otDate" name="otDate" required>
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
                                                                <div class="ot-type-multiplier" id="otTypeMultiplier">
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div id="otTypePlaceholder" class="ot-type-placeholder">
                                                <small class="text-muted">
                                                    <i class="fas fa-arrow-left"></i> Select a date to see day type and
                                                    pay rate
                                                </small>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Time Range -->
                                    <div class="row g-3">
                                        <div class="col-md-6">
                                            <label for="startTime" class="form-label">
                                                <i class="fas fa-clock"></i> Start Time
                                                <span class="text-danger">*</span>
                                            </label>
                                            <input type="time" class="form-control" id="startTime" name="startTime"
                                                required>
                                            <div class="form-text">
                                                <i class="fas fa-info-circle"></i>
                                                Time range: 06:00 - 22:00 (day shift only)
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <label for="endTime" class="form-label">
                                                <i class="fas fa-clock"></i> End Time
                                                <span class="text-danger">*</span>
                                            </label>
                                            <input type="time" class="form-control" id="endTime" name="endTime"
                                                required>
                                            <div class="form-text">
                                                <i class="fas fa-info-circle"></i>
                                                Time range: 06:00 - 22:00 (day shift only)
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
                                        <small class="text-muted">
                                            <i class="fas fa-exclamation-triangle"></i>
                                            Maximum 2 hours OT per day (total work hours including regular cannot exceed
                                            10h/day)
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
                                            required></textarea>
                                        <div class="char-counter">
                                            <span id="charCount">0</span>/1000 characters
                                        </div>
                                    </div>

                                    <!-- Employee Consent -->
                                    <div class="mt-3">
                                        <div class="form-check consent-checkbox">
                                            <input class="form-check-input" type="checkbox" id="employeeConsent"
                                                name="employeeConsent" required>
                                            <label class="form-check-label" for="employeeConsent">
                                                <strong>I voluntarily agree to work overtime as requested</strong>
                                                <span class="text-danger">*</span>
                                                <div class="consent-note">
                                                    <i class="fas fa-info-circle"></i>
                                                    Required by BR-AD-06: Employee consent is mandatory for all overtime
                                                    work
                                                </div>
                                            </label>
                                        </div>
                                    </div>

                                    <!-- Actions -->
                                    <div class="mt-4 d-grid gap-2 d-md-flex justify-content-md-end">
                                        <a href="${pageContext.request.contextPath}/request/ot?action=list"
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

                <!-- Form JavaScript -->
                <script src="${pageContext.request.contextPath}/assets/js/ot-form.js"></script>

            </body>

            </html>