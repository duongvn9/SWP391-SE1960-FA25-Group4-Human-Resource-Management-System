<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
            <%
                // Retrieve form data from session (if exists from previous error)
                String savedLeaveType = (String) session.getAttribute("formData_leaveTypeCode");
                String savedStartDate = (String) session.getAttribute("formData_startDate");
                String savedEndDate = (String) session.getAttribute("formData_endDate");
                String savedReason = (String) session.getAttribute("formData_reason");
                Boolean savedIsHalfDay = (Boolean) session.getAttribute("formData_isHalfDay");
                String savedHalfDayPeriod = (String) session.getAttribute("formData_halfDayPeriod");

                // Clear form data from session after retrieving
                session.removeAttribute("formData_leaveTypeCode");
                session.removeAttribute("formData_startDate");
                session.removeAttribute("formData_endDate");
                session.removeAttribute("formData_reason");
                session.removeAttribute("formData_isHalfDay");
                session.removeAttribute("formData_halfDayPeriod");

                // Make available to JSTL
                pageContext.setAttribute("savedLeaveType", savedLeaveType);
                pageContext.setAttribute("savedStartDate", savedStartDate);
                pageContext.setAttribute("savedEndDate", savedEndDate);
                pageContext.setAttribute("savedReason", savedReason);
                pageContext.setAttribute("savedIsHalfDay", savedIsHalfDay != null && savedIsHalfDay);
                pageContext.setAttribute("savedHalfDayPeriod", savedHalfDayPeriod);
            %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <!-- CSS riêng của trang -->
                <jsp:include page="../layout/head.jsp">
                    <jsp:param name="pageTitle" value="Create Leave Request - HRMS" />
                    <jsp:param name="pageCss" value="leave-form.css" />
                </jsp:include>
            </head>

            <body>
                <script>console.log('BODY LOADED - JavaScript is working!');</script>

                <!-- Sidebar -->
                <jsp:include page="../layout/sidebar.jsp">
                    <jsp:param name="currentPage" value="leave-request" />
                </jsp:include>

                <!-- Main Content -->
                <div class="main-content" id="main-content">
                    <!-- Header -->
                    <jsp:include page="../layout/dashboard-header.jsp" />

                    <!-- Content Area -->
                    <div class="content-area">
                        <!-- Page Title -->
                        <div class="page-head d-flex justify-content-between align-items-center">
                            <div>
                                <h2 class="page-title"><i class="fas fa-calendar-alt me-2"></i>Create Leave Request</h2>
                                <p class="page-subtitle">Submit a new leave request for approval</p>
                            </div>
                            <a href="${pageContext.request.contextPath}/requests" class="btn btn-outline-secondary">
                                <i class="fas fa-list me-1"></i> View All Requests
                            </a>
                        </div>

                        <!-- Leave Balance Summary -->
                        <c:if test="${not empty leaveBalances}">
                            <div class="row mb-4">
                                <div class="col-12">
                                    <div class="card">
                                        <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center"
                                            style="cursor: pointer;" onclick="toggleLeaveBalance()">
                                            <h5 class="mb-0"><i class="fas fa-chart-pie me-2"></i>Your Leave Balance
                                                (${currentYear})</h5>
                                            <button class="btn btn-sm btn-light" type="button" id="leaveBalanceToggle">
                                                <i class="fas fa-eye"></i>
                                            </button>
                                        </div>
                                        <div class="card-body" id="leaveBalanceContent">
                                            <div class="row g-3">
                                                <c:forEach var="balance" items="${leaveBalances}">
                                                    <div class="col-md-6 col-lg-4">
                                                        <div class="leave-balance-card">
                                                            <div class="balance-header">
                                                                <h6 class="balance-title">${balance.leaveTypeName}</h6>
                                                                <span
                                                                    class="balance-code">${balance.leaveTypeCode}</span>
                                                            </div>
                                                            <c:choose>
                                                                <c:when
                                                                    test="${balance.leaveTypeCode == 'UNPAID_LEAVE' || balance.leaveTypeCode == 'UNPAID'}">
                                                                    <!-- Unpaid leave: Show only used and pending -->
                                                                    <div class="balance-stats">
                                                                        <div class="stat-item">
                                                                            <span class="stat-label">Used:</span>
                                                                            <span class="stat-value text-danger">
                                                                                <fmt:formatNumber
                                                                                    value="${balance.usedDays}"
                                                                                    minFractionDigits="0"
                                                                                    maxFractionDigits="1" /> days
                                                                            </span>
                                                                        </div>
                                                                        <div class="stat-item">
                                                                            <span class="stat-label">Pending:</span>
                                                                            <span class="stat-value text-warning">
                                                                                <fmt:formatNumber
                                                                                    value="${balance.pendingDays}"
                                                                                    minFractionDigits="0"
                                                                                    maxFractionDigits="1" /> days
                                                                            </span>
                                                                        </div>
                                                                    </div>
                                                                    <div class="balance-progress">
                                                                        <small class="text-muted">
                                                                            <i class="fas fa-info-circle"></i> Salary
                                                                            will be deducted for unpaid leave days
                                                                        </small>
                                                                    </div>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <!-- Paid leave: Show full balance info -->
                                                                    <div class="balance-stats">
                                                                        <div class="stat-item">
                                                                            <span class="stat-label">Total
                                                                                Allowed:</span>
                                                                            <span class="stat-value">
                                                                                <fmt:formatNumber
                                                                                    value="${balance.totalAllowed}"
                                                                                    minFractionDigits="0"
                                                                                    maxFractionDigits="1" /> days
                                                                            </span>
                                                                        </div>
                                                                        <div class="stat-item">
                                                                            <span class="stat-label">Used:</span>
                                                                            <span class="stat-value text-danger">
                                                                                <fmt:formatNumber
                                                                                    value="${balance.usedDays}"
                                                                                    minFractionDigits="0"
                                                                                    maxFractionDigits="1" /> days
                                                                            </span>
                                                                        </div>
                                                                        <div class="stat-item">
                                                                            <span class="stat-label">Pending:</span>
                                                                            <span class="stat-value text-warning">
                                                                                <fmt:formatNumber
                                                                                    value="${balance.pendingDays}"
                                                                                    minFractionDigits="0"
                                                                                    maxFractionDigits="1" /> days
                                                                            </span>
                                                                        </div>
                                                                        <div class="stat-item remaining">
                                                                            <span class="stat-label">Remaining:</span>
                                                                            <span class="stat-value text-info fw-bold">
                                                                                <fmt:formatNumber
                                                                                    value="${balance.remainingDays}"
                                                                                    minFractionDigits="0"
                                                                                    maxFractionDigits="1" /> days
                                                                            </span>
                                                                        </div>
                                                                        <div class="stat-item available">
                                                                            <span class="stat-label">Available:</span>
                                                                            <span
                                                                                class="stat-value text-primary fw-bold">
                                                                                <fmt:formatNumber
                                                                                    value="${balance.availableDays}"
                                                                                    minFractionDigits="0"
                                                                                    maxFractionDigits="1" /> days
                                                                                <c:if
                                                                                    test="${balance.availableDays < 3 && balance.availableDays > 0}">
                                                                                    <span
                                                                                        class="badge bg-warning text-dark ms-1">Low</span>
                                                                                </c:if>
                                                                                <c:if
                                                                                    test="${balance.availableDays <= 0}">
                                                                                    <span
                                                                                        class="badge bg-danger ms-1">None</span>
                                                                                </c:if>
                                                                            </span>
                                                                        </div>
                                                                    </div>
                                                                    <div class="balance-progress">
                                                                        <div class="progress" style="height: 8px;">
                                                                            <div class="progress-bar bg-success"
                                                                                role="progressbar"
                                                                                data-width="${balance.remainingPercentage}"
                                                                                aria-valuenow="${balance.remainingDays}"
                                                                                aria-valuemin="0"
                                                                                aria-valuemax="${balance.totalAllowed}">
                                                                            </div>
                                                                        </div>
                                                                        <small class="text-muted">
                                                                            ${balance.availableDays} available /
                                                                            ${balance.remainingDays} remaining /
                                                                            ${balance.totalAllowed} total
                                                                        </small>
                                                                    </div>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <!-- Form Card -->
                        <div class="card leave-request-card">
                            <div class="card-header">
                                <h4><i class="fas fa-calendar-plus me-2"></i> Leave Request Form</h4>
                            </div>

                            <div class="card-body">
                                <!-- Alerts -->
                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                        <div class="d-flex align-items-start">
                                            <div class="flex-shrink-0">
                                                <c:choose>
                                                    <c:when test="${errorType == 'OVERLAP'}">
                                                        <i class="fas fa-calendar-times fa-2x me-3"></i>
                                                    </c:when>
                                                    <c:when
                                                        test="${errorType == 'BALANCE_EXCEEDED' || errorType == 'INSUFFICIENT_BALANCE'}">
                                                        <i class="fas fa-exclamation-circle fa-2x me-3"></i>
                                                    </c:when>
                                                    <c:when test="${errorType == 'OT_CONFLICT'}">
                                                        <i class="fas fa-clock fa-2x me-3"></i>
                                                    </c:when>
                                                    <c:when test="${errorType == 'HALF_DAY_NON_WORKING_DAY'}">
                                                        <i class="fas fa-calendar-day fa-2x me-3"></i>
                                                    </c:when>
                                                    <c:when
                                                        test="${errorType == 'HALF_DAY_FULL_DAY_CONFLICT' || errorType == 'HALF_DAY_SAME_PERIOD_CONFLICT'}">
                                                        <i class="fas fa-calendar-times fa-2x me-3"></i>
                                                    </c:when>
                                                    <c:when test="${errorType == 'INVALID_HALF_DAY_PERIOD'}">
                                                        <i class="fas fa-clock fa-2x me-3"></i>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i class="fas fa-exclamation-triangle fa-2x me-3"></i>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="flex-grow-1">
                                                <c:choose>
                                                    <c:when test="${not empty errorTitle and not empty errorDetails}">
                                                        <h5 class="alert-heading mb-2">
                                                            <c:out value="${errorTitle}" />
                                                        </h5>
                                                        <div class="error-details" style="white-space: pre-line;">
                                                            <c:out value="${errorDetails}" />
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:out value="${error}" />
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                            aria-label="Close"></button>
                                    </div>
                                </c:if>

                                <c:if test="${not empty success}">
                                    <div class="alert alert-success" role="alert">
                                        <i class="fas fa-check-circle me-2"></i>
                                        <c:out value="${success}" />
                                    </div>
                                </c:if>

                                <!-- Form -->
                                <form method="post" action="${pageContext.request.contextPath}/requests/leave/create"
                                    id="leaveRequestForm" novalidate>

                                    <!-- Leave Type -->
                                    <div class="mb-3">
                                        <label for="leaveTypeCode" class="form-label">
                                            <i class="fas fa-list"></i> Leave Type
                                            <span class="text-danger">*</span>
                                        </label>
                                        <select class="form-select" id="leaveTypeCode" name="leaveTypeCode" required>
                                            <option value="">-- Select Leave Type --</option>
                                            <c:forEach var="entry" items="${leaveTypes}">
                                                <c:set var="shouldDisable" value="false" />
                                                <c:if test="${entry.key != 'UNPAID_LEAVE' && entry.key != 'UNPAID'}">
                                                    <c:forEach var="balance" items="${leaveBalances}">
                                                        <c:if
                                                            test="${balance.leaveTypeCode == entry.key && balance.availableDays <= 0}">
                                                            <c:set var="shouldDisable" value="true" />
                                                        </c:if>
                                                    </c:forEach>
                                                </c:if>
                                                <option value="${entry.key}" data-code="${entry.key}"
                                                    ${shouldDisable ? 'disabled' : '' }
                                                    ${savedLeaveType == entry.key ? 'selected' : ''}>${entry.value}</option>
                                            </c:forEach>
                                        </select>
                                        <div class="form-text">Choose the type of leave you want to request</div>
                                    </div>

                                    <!-- Leave Type Rules (auto show on change) -->
                                    <div id="leaveTypeRules" class="leave-type-rules d-none">
                                        <h6>Leave Type Information</h6>
                                        <div id="rulesContent" class="rules-content"></div>
                                    </div>

                                    <!-- Unpaid Leave Notice -->
                                    <div id="unpaidLeaveNotice" class="alert alert-warning d-none" role="alert">
                                        <i class="fas fa-exclamation-triangle me-2"></i>
                                        <strong>Notice:</strong> This is unpaid leave - salary will be deducted based on
                                        days taken.
                                        For half-day unpaid leave, 50% of daily salary will be deducted.
                                    </div>

                                    <!-- Duration Type & Half-Day Period (Same Row) -->
                                    <div class="row g-3 mb-3">
                                        <!-- Duration Type Selector -->
                                        <div class="col-md-6">
                                            <label class="form-label">
                                                <i class="fas fa-calendar-day"></i> Duration Type
                                                <span class="text-danger">*</span>
                                            </label>
                                            <div class="duration-type-selector">
                                                <div class="form-check form-check-inline">
                                                    <input class="form-check-input" type="radio" name="durationType"
                                                        id="durationFullDay" value="FULL_DAY" checked>
                                                    <label class="form-check-label" for="durationFullDay">
                                                        <i class="fas fa-calendar"></i> Full Day
                                                    </label>
                                                </div>
                                                <div class="form-check form-check-inline">
                                                    <input class="form-check-input" type="radio" name="durationType"
                                                        id="durationHalfDay" value="HALF_DAY">
                                                    <label class="form-check-label" for="durationHalfDay">
                                                        <i class="fas fa-calendar-minus"></i> Half Day
                                                    </label>
                                                </div>
                                            </div>
                                            <div class="form-text">Select whether you need full day or half day leave
                                            </div>
                                        </div>

                                        <!-- Half-Day Period Selector (shown only for half-day) -->
                                        <div class="col-md-6 d-none" id="halfDayPeriodContainer">
                                            <label class="form-label">
                                                <i class="fas fa-clock"></i> Half-Day Period
                                                <span class="text-danger">*</span>
                                            </label>
                                            <div class="half-day-period-selector">
                                                <div class="form-check">
                                                    <input class="form-check-input" type="radio" name="halfDayPeriod"
                                                        id="periodMorning" value="AM">
                                                    <label class="form-check-label" for="periodMorning">
                                                        <i class="fas fa-sun"></i> Morning (8:00 - 12:00)
                                                    </label>
                                                </div>
                                                <div class="form-check">
                                                    <input class="form-check-input" type="radio" name="halfDayPeriod"
                                                        id="periodAfternoon" value="PM">
                                                    <label class="form-check-label" for="periodAfternoon">
                                                        <i class="fas fa-cloud-sun"></i> Afternoon (13:00 - 17:00)
                                                    </label>
                                                </div>
                                            </div>
                                            <div class="form-text">Select which half of the day you need leave for</div>
                                        </div>
                                    </div>

                                    <!-- Dates (Full width for half-day, split for full-day) -->
                                    <div class="row g-3" id="dateFieldsContainer">
                                        <div class="col-md-6" id="startDateContainer">
                                            <label for="startDate" class="form-label">
                                                <i class="fas fa-calendar-alt"></i> <span id="startDateLabel">Start
                                                    Date</span>
                                                <span class="text-danger">*</span>
                                            </label>
                                            <input type="date" class="form-control" id="startDate" name="startDate"
                                                value="${not empty savedStartDate ? savedStartDate : ''}" required>
                                            <div class="form-text" id="startDateHelp">When your leave will start</div>
                                        </div>
                                        <div class="col-md-6" id="endDateContainer">
                                            <label for="endDate" class="form-label">
                                                <i class="fas fa-calendar-alt"></i> End Date
                                                <span class="text-danger">*</span>
                                            </label>
                                            <input type="date" class="form-control" id="endDate" name="endDate"
                                                value="${not empty savedEndDate ? savedEndDate : ''}"
                                                required>
                                            <div class="form-text">When your leave will end</div>
                                        </div>
                                    </div>

                                    <!-- Duration Info -->
                                    <div class="mt-3">
                                        <label class="form-label">
                                            <i class="fas fa-calendar-day"></i> Calculated Duration
                                        </label>
                                        <div class="duration-display" id="durationDisplay">
                                            <i class="fas fa-clock"></i>
                                            <span id="durationText">Select dates to calculate</span>
                                        </div>
                                        <div class="duration-warning d-none mt-2" id="durationWarning">
                                            <i class="fas fa-exclamation-triangle"></i>
                                            <span id="warningText"></span>
                                        </div>
                                    </div>

                                    <!-- Reason -->
                                    <div class="mt-3">
                                        <label for="reason" class="form-label">
                                            <i class="fas fa-comment"></i> Reason
                                            <span class="text-danger">*</span>
                                        </label>
                                        <textarea class="form-control" id="reason" name="reason" rows="5"
                                            maxlength="1000"
                                            placeholder="Please provide the reason for your leave request..."
                                            required>${not empty savedReason ? savedReason : ''}</textarea>
                                        <div class="char-counter">
                                            <span id="charCount">0</span>/1000 characters
                                        </div>
                                    </div>

                                    <!-- File Upload (Optional) -->
                                    <div class="mt-3">
                                        <label for="attachment" class="form-label">
                                            <i class="fas fa-paperclip"></i> Supporting Document
                                            <span class="text-muted">(Optional)</span>
                                        </label>
                                        <div class="file-upload-wrapper">
                                            <input type="file" class="form-control" id="attachment" name="attachment"
                                                accept=".pdf,.doc,.docx,.jpg,.jpeg,.png">
                                            <div class="form-text">
                                                <i class="fas fa-info-circle"></i>
                                                Accepted formats: PDF, DOC, DOCX, JPG, PNG (Max 5MB)
                                            </div>
                                            <div id="fileInfo" class="file-info d-none mt-2">
                                                <i class="fas fa-file"></i>
                                                <span id="fileName"></span>
                                                <button type="button" class="btn-remove-file" id="removeFile">
                                                    <i class="fas fa-times"></i>
                                                </button>
                                            </div>
                                            <div id="certificateRequired" class="certificate-notice d-none mt-2">
                                                <i class="fas fa-exclamation-circle"></i>
                                                <strong>Certificate Required:</strong> This leave type requires a
                                                medical certificate or supporting document.
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Hidden fields for half-day data -->
                                    <input type="hidden" name="isHalfDay" id="isHalfDay" value="false">

                                    <!-- Actions -->
                                    <div class="mt-4 d-grid gap-2 d-md-flex justify-content-md-end">
                                        <a href="${pageContext.request.contextPath}/requests"
                                            class="btn btn-leave-secondary">
                                            <i class="fas fa-times me-1"></i> Cancel
                                        </a>
                                        <button type="submit" class="btn btn-leave-primary" id="submitBtn">
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

                <!-- Leave Type Rules JSON (for JavaScript) -->
                <script type="application/json" id="leaveTypeRulesData">
{<c:if test="${not empty leaveTypeRules}"><c:forEach var="rules" items="${leaveTypeRules}" varStatus="status">
"${rules.code}": {
"name": "<c:out value='${rules.name}'/>",
"defaultDays": ${rules.defaultDays},
"maxDays": ${rules.maxDays},
"isPaid": ${rules.paid},
"requiresApproval": ${rules.requiresApproval},
"requiresCertificate": ${rules.requiresCertificate},
"minAdvanceNotice": ${rules.minAdvanceNotice}
}<c:if test="${not status.last}">,</c:if></c:forEach></c:if>
}
    </script>

                <!-- Leave Balance Data JSON (for JavaScript) -->
                <script type="application/json" id="leaveBalanceData">
{<c:if test="${not empty leaveBalances}"><c:forEach var="balance" items="${leaveBalances}" varStatus="status">
"${balance.leaveTypeCode}": {
"leaveTypeName": "<c:out value='${balance.leaveTypeName}'/>",
"totalAllowed": ${balance.totalAllowed},
"usedDays": ${balance.usedDays},
"pendingDays": ${balance.pendingDays},
"remainingDays": ${balance.remainingDays},
"availableDays": ${balance.availableDays}
}<c:if test="${not status.last}">,</c:if></c:forEach></c:if>
}
    </script>

                <!-- Form JavaScript -->
                <script>
                    console.log('Form script loaded');

                    document.addEventListener('DOMContentLoaded', function () {
                        console.log('DOM loaded, initializing form...');

                        // Set progress bar width from data attribute
                        document.querySelectorAll('.progress-bar[data-width]').forEach(function (bar) {
                            const width = bar.getAttribute('data-width');
                            bar.style.width = width + '%';
                        });

                        // Load leave type rules and balance data
                        const rulesData = JSON.parse(document.getElementById('leaveTypeRulesData').textContent);
                        const balanceData = JSON.parse(document.getElementById('leaveBalanceData').textContent);
                        console.log('Leave type rules loaded:', rulesData);
                        console.log('Leave balance data loaded:', balanceData);

                        const leaveTypeSelect = document.getElementById('leaveTypeCode');
                        const rulesContainer = document.getElementById('leaveTypeRules');
                        const rulesContent = document.getElementById('rulesContent');
                        const certificateRequired = document.getElementById('certificateRequired');
                        const unpaidLeaveNotice = document.getElementById('unpaidLeaveNotice');

                        const startDateInput = document.getElementById('startDate');
                        const endDateInput = document.getElementById('endDate');
                        const durationDisplay = document.getElementById('durationDisplay');
                        const durationText = document.getElementById('durationText');
                        const durationWarning = document.getElementById('durationWarning');
                        const warningText = document.getElementById('warningText');

                        const reasonTextarea = document.getElementById('reason');
                        const charCount = document.getElementById('charCount');

                        const attachmentInput = document.getElementById('attachment');
                        const fileInfo = document.getElementById('fileInfo');
                        const fileName = document.getElementById('fileName');
                        const removeFileBtn = document.getElementById('removeFile');

                        // Leave type change handler
                        leaveTypeSelect.addEventListener('change', function () {
                            const selectedCode = this.value;
                            console.log('Selected leave type:', selectedCode);

                            // Highlight selected leave type balance card
                            document.querySelectorAll('.leave-balance-card').forEach(function (card) {
                                card.classList.remove('selected');
                            });

                            if (selectedCode) {
                                // Find and highlight the matching card
                                document.querySelectorAll('.leave-balance-card').forEach(function (card) {
                                    const codeElement = card.querySelector('.balance-code');
                                    if (codeElement && codeElement.textContent.trim() === selectedCode) {
                                        card.classList.add('selected');
                                    }
                                });
                            }

                            if (selectedCode && rulesData[selectedCode]) {
                                const rules = rulesData[selectedCode];
                                const isUnpaid = !rules.isPaid || rules.defaultDays === 0;

                                // Show rules
                                let html = '';

                                // Only show Default Days for paid leave types
                                if (!isUnpaid) {
                                    html += '<div class="rule-item"><small>Default Days</small><br><strong>' + rules.defaultDays + ' days</strong></div>';
                                }

                                // Max Days with special handling for unpaid
                                if (isUnpaid) {
                                    html += '<div class="rule-item"><small>Max Days</small><br><strong>' + rules.maxDays + ' days<br><small class="text-muted">per request</small></strong></div>';
                                } else {
                                    html += '<div class="rule-item"><small>Max Days</small><br><strong>' + rules.maxDays + ' days</strong></div>';
                                }

                                html += '<div class="rule-item"><small>Type</small><br><strong>';
                                html += '<span class="leave-badge ' + (rules.isPaid ? 'success' : 'warning') + '">';
                                html += rules.isPaid ? 'Paid' : 'Unpaid';
                                html += '</span></strong></div>';

                                if (rules.minAdvanceNotice > 0) {
                                    html += '<div class="rule-item"><small>Advance Notice</small><br><strong>' + rules.minAdvanceNotice + ' days</strong></div>';
                                }

                                if (rules.requiresCertificate) {
                                    html += '<div class="rule-item"><small>Certificate</small><br><strong><span class="leave-badge warning">Required</span></strong></div>';
                                    certificateRequired.classList.remove('d-none');
                                } else {
                                    certificateRequired.classList.add('d-none');
                                }

                                // Show/hide unpaid leave notice
                                if (!rules.isPaid) {
                                    unpaidLeaveNotice.classList.remove('d-none');
                                } else {
                                    unpaidLeaveNotice.classList.add('d-none');
                                }

                                rulesContent.innerHTML = html;
                                rulesContainer.classList.remove('d-none');

                            } else {
                                rulesContainer.classList.add('d-none');
                                certificateRequired.classList.add('d-none');
                                unpaidLeaveNotice.classList.add('d-none');
                            }

                            updateDuration();
                        });

                        // Duration type change handler (Task 8.1)
                        const durationTypeRadios = document.querySelectorAll('input[name="durationType"]');
                        const halfDayPeriodContainer = document.getElementById('halfDayPeriodContainer');

                        durationTypeRadios.forEach(function (radio) {
                            radio.addEventListener('change', function () {
                                const isHalfDay = this.value === 'HALF_DAY';
                                const startDateContainer = document.getElementById('startDateContainer');
                                const endDateContainer = document.getElementById('endDateContainer');
                                const startDateLabel = document.getElementById('startDateLabel');
                                const startDateHelp = document.getElementById('startDateHelp');

                                if (isHalfDay) {
                                    // Show half-day period selector (same row as duration type)
                                    if (halfDayPeriodContainer) {
                                        halfDayPeriodContainer.classList.remove('d-none');
                                    }
                                    // Make start date full width
                                    startDateContainer.classList.remove('col-md-6');
                                    startDateContainer.classList.add('col-12');
                                    // Hide end date field
                                    endDateContainer.classList.add('d-none');
                                    // Disable end date field
                                    endDateInput.disabled = true;
                                    endDateInput.required = false;
                                    // Update start date label
                                    startDateLabel.textContent = 'Date';
                                    startDateHelp.textContent = 'Select the date for your half-day leave';
                                } else {
                                    // Hide half-day period selector
                                    if (halfDayPeriodContainer) {
                                        halfDayPeriodContainer.classList.add('d-none');
                                        // Clear half-day period selection
                                        document.querySelectorAll('input[name="halfDayPeriod"]').forEach(function (p) {
                                            p.checked = false;
                                        });
                                    }
                                    // Restore start date to half width
                                    startDateContainer.classList.remove('col-12');
                                    startDateContainer.classList.add('col-md-6');
                                    // Show end date field
                                    endDateContainer.classList.remove('d-none');
                                    // Enable end date field
                                    endDateInput.disabled = false;
                                    endDateInput.required = true;
                                    // Restore start date label
                                    startDateLabel.textContent = 'Start Date';
                                    startDateHelp.textContent = 'When your leave will start';
                                }

                                updateDuration();
                            });
                        });

                        // Half-day period change handler
                        const halfDayPeriodRadios = document.querySelectorAll('input[name="halfDayPeriod"]');
                        halfDayPeriodRadios.forEach(function (radio) {
                            radio.addEventListener('change', updateDuration);
                        });

                        // Date change handlers
                        startDateInput.addEventListener('change', updateDuration);
                        endDateInput.addEventListener('change', updateDuration);

                        // Task 8.3: Update duration display in real-time
                        function updateDuration() {
                            const startDate = startDateInput.value;
                            const endDate = endDateInput.value;
                            const selectedCode = leaveTypeSelect.value;
                            const durationType = document.querySelector('input[name="durationType"]:checked')?.value;
                            const isHalfDay = durationType === 'HALF_DAY';

                            if (!startDate) {
                                durationText.textContent = 'Select date to calculate';
                                durationDisplay.classList.remove('error');
                                durationWarning.classList.add('d-none');
                                return;
                            }

                            // Handle half-day leave
                            if (isHalfDay) {
                                const halfDayPeriod = document.querySelector('input[name="halfDayPeriod"]:checked')?.value;

                                // Use calculateDuration function
                                const duration = calculateDuration(durationType, startDate, endDate, halfDayPeriod);

                                if (halfDayPeriod === 'AM') {
                                    durationText.textContent = duration + ' days (Morning 8:00-12:00)';
                                } else if (halfDayPeriod === 'PM') {
                                    durationText.textContent = duration + ' days (Afternoon 13:00-17:00)';
                                } else {
                                    durationText.textContent = duration + ' days (Select period)';
                                }

                                durationDisplay.classList.remove('error');
                                durationWarning.classList.add('d-none');

                                // Check balance for half-day (skip for unpaid leave)
                                if (selectedCode && balanceData[selectedCode] && selectedCode !== 'UNPAID' && selectedCode !== 'UNPAID_LEAVE') {
                                    const balance = balanceData[selectedCode];
                                    if (balance.availableDays < 0.5 && balance.availableDays >= 0) {
                                        warningText.textContent = 'Insufficient balance for half-day leave';
                                        durationWarning.classList.remove('d-none');
                                    }
                                }

                                return;
                            }

                            // Handle full-day leave
                            if (!endDate) {
                                durationText.textContent = 'Select dates to calculate';
                                durationDisplay.classList.remove('error');
                                durationWarning.classList.add('d-none');
                                return;
                            }

                            const start = new Date(startDate);
                            const end = new Date(endDate);

                            if (end < start) {
                                durationText.textContent = 'Invalid date range';
                                durationDisplay.classList.add('error');
                                durationWarning.classList.add('d-none');
                                return;
                            }

                            // Use calculateDuration function for full-day
                            const workingDays = calculateDuration(durationType, startDate, endDate, null);

                            durationText.textContent = workingDays + ' working day' + (workingDays !== 1 ? 's' : '');
                            durationDisplay.classList.remove('error');

                            // Check against max days
                            if (selectedCode && rulesData[selectedCode]) {
                                const rules = rulesData[selectedCode];
                                if (workingDays > rules.maxDays) {
                                    warningText.textContent = 'Exceeds maximum allowed days (' + rules.maxDays + ' days)';
                                    durationWarning.classList.remove('d-none');
                                } else {
                                    durationWarning.classList.add('d-none');
                                }
                            }
                        }

                        // Character counter
                        reasonTextarea.addEventListener('input', function () {
                            const count = this.value.length;
                            charCount.textContent = count;

                            const counter = charCount.parentElement;
                            counter.classList.remove('danger', 'warning');

                            if (count > 900) {
                                counter.classList.add('danger');
                            } else if (count > 700) {
                                counter.classList.add('warning');
                            }
                        });

                        // File upload
                        attachmentInput.addEventListener('change', function () {
                            if (this.files && this.files[0]) {
                                const file = this.files[0];
                                const maxSize = 5 * 1024 * 1024; // 5MB

                                if (file.size > maxSize) {
                                    alert('File size exceeds 5MB limit');
                                    this.value = '';
                                    return;
                                }

                                fileName.textContent = file.name;
                                fileInfo.classList.remove('d-none');
                            }
                        });

                        removeFileBtn.addEventListener('click', function () {
                            attachmentInput.value = '';
                            fileInfo.classList.add('d-none');
                        });

                        // Task 8.3: Calculate duration function
                        function calculateDuration(durationType, startDate, endDate, period) {
                            if (durationType === 'HALF_DAY') {
                                return 0.5;
                            } else {
                                // Calculate working days for full-day requests
                                if (!startDate || !endDate) {
                                    return 0;
                                }

                                const start = new Date(startDate);
                                const end = new Date(endDate);

                                if (end < start) {
                                    return 0;
                                }

                                let workingDays = 0;
                                let current = new Date(start);

                                while (current <= end) {
                                    const dayOfWeek = current.getDay();
                                    if (dayOfWeek !== 0 && dayOfWeek !== 6) { // Not Sunday or Saturday
                                        workingDays++;
                                    }
                                    current.setDate(current.getDate() + 1);
                                }

                                return workingDays;
                            }
                        }

                        // Task 8.4: Check half-day conflict function
                        async function checkHalfDayConflict(date, period) {
                            try {
                                const response = await fetch(
                                    '${pageContext.request.contextPath}/requests/leave/create?' +
                                    'action=checkConflict&' +
                                    'date=' + encodeURIComponent(date) +
                                    '&period=' + encodeURIComponent(period),
                                    {
                                        method: 'GET',
                                        headers: {
                                            'Content-Type': 'application/json'
                                        }
                                    }
                                );

                                if (!response.ok) {
                                    throw new Error('Failed to check conflicts');
                                }

                                const result = await response.json();
                                return result;
                            } catch (error) {
                                console.error('Error checking half-day conflict:', error);
                                // Return no conflict if API fails (graceful degradation)
                                return {
                                    hasConflict: false,
                                    conflictType: null,
                                    message: null
                                };
                            }
                        }

                        // Task 8.2: Validate half-day request function
                        async function validateHalfDayRequest(date, period) {
                            const errors = [];

                            // Validate date is not empty
                            if (!date || date.trim() === '') {
                                errors.push('Date is required for half-day leave');
                                return { valid: false, errors: errors };
                            }

                            // Validate period is selected (AM/PM)
                            if (!period || (period !== 'AM' && period !== 'PM')) {
                                errors.push('Please select a half-day period (Morning or Afternoon)');
                                return { valid: false, errors: errors };
                            }

                            // Check if date is a working day (not weekend)
                            const selectedDate = new Date(date);
                            const dayOfWeek = selectedDate.getDay();
                            if (dayOfWeek === 0 || dayOfWeek === 6) {
                                errors.push('Half-day leave can only be requested for working days (Monday-Friday)');
                                return { valid: false, errors: errors };
                            }

                            // Call backend API to check working day (holidays)
                            // Note: This endpoint needs to be implemented in the backend (task 6.2)
                            try {
                                const conflictResult = await checkHalfDayConflict(date, period);

                                if (conflictResult.hasConflict) {
                                    errors.push(conflictResult.message || 'A conflict exists with this half-day request');
                                    return { valid: false, errors: errors, conflictType: conflictResult.conflictType };
                                }
                            } catch (error) {
                                console.warn('Could not validate against backend:', error);
                                // Continue with client-side validation only
                            }

                            return { valid: true, errors: [] };
                        }

                        // Display validation errors to user
                        function displayValidationErrors(errors) {
                            if (errors.length === 0) {
                                return;
                            }

                            // Remove existing error alerts
                            const existingAlerts = document.querySelectorAll('.validation-error-alert');
                            existingAlerts.forEach(alert => alert.remove());

                            // Create error alert
                            const alertDiv = document.createElement('div');
                            alertDiv.className = 'alert alert-danger alert-dismissible fade show validation-error-alert';
                            alertDiv.setAttribute('role', 'alert');

                            let errorHtml = '<div class="d-flex align-items-start">';
                            errorHtml += '<div class="flex-shrink-0"><i class="fas fa-exclamation-triangle fa-2x me-3"></i></div>';
                            errorHtml += '<div class="flex-grow-1">';
                            errorHtml += '<h5 class="alert-heading mb-2">Validation Error</h5>';
                            errorHtml += '<ul class="mb-0">';
                            errors.forEach(error => {
                                errorHtml += '<li>' + error + '</li>';
                            });
                            errorHtml += '</ul></div></div>';
                            errorHtml += '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>';

                            alertDiv.innerHTML = errorHtml;

                            // Insert at the top of the form
                            const formCard = document.querySelector('.leave-request-card .card-body');
                            formCard.insertBefore(alertDiv, formCard.firstChild);

                            // Scroll to the error
                            alertDiv.scrollIntoView({ behavior: 'smooth', block: 'center' });
                        }

                        // Form validation
                        document.getElementById('leaveRequestForm').addEventListener('submit', async function (e) {
                            e.preventDefault(); // Prevent default submission initially

                            const durationType = document.querySelector('input[name="durationType"]:checked')?.value;
                            const isHalfDay = durationType === 'HALF_DAY';

                            // Set hidden field for isHalfDay
                            document.getElementById('isHalfDay').value = isHalfDay ? 'true' : 'false';

                            // Validate half-day specific requirements
                            if (isHalfDay) {
                                const startDate = startDateInput.value;
                                const halfDayPeriodElement = document.querySelector('input[name="halfDayPeriod"]:checked');
                                const halfDayPeriod = halfDayPeriodElement ? halfDayPeriodElement.value : null;

                                // Perform async validation
                                const validationResult = await validateHalfDayRequest(startDate, halfDayPeriod);

                                if (!validationResult.valid) {
                                    displayValidationErrors(validationResult.errors);
                                    this.classList.add('was-validated');
                                    return false;
                                }

                                // Set end date same as start date for half-day
                                endDateInput.value = startDateInput.value;
                            }

                            // Check standard form validity
                            if (!this.checkValidity()) {
                                this.classList.add('was-validated');
                                return false;
                            }

                            this.classList.add('was-validated');
                            // Submit the form
                            this.submit();
                        });

                        console.log('Form initialized successfully');
                    });

                    // Toggle Leave Balance
                    function toggleLeaveBalance() {
                        const content = document.getElementById('leaveBalanceContent');
                        const toggle = document.getElementById('leaveBalanceToggle');
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
                </script>

            </body>

            </html>