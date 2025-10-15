<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
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
                                                            <div class="balance-stats">
                                                                <div class="stat-item">
                                                                    <span class="stat-label">Total Allowed:</span>
                                                                    <span
                                                                        class="stat-value">${balance.totalAllowed}</span>
                                                                </div>
                                                                <div class="stat-item">
                                                                    <span class="stat-label">Used:</span>
                                                                    <span
                                                                        class="stat-value text-danger">${balance.usedDays}</span>
                                                                </div>
                                                                <div class="stat-item">
                                                                    <span class="stat-label">Pending:</span>
                                                                    <span
                                                                        class="stat-value text-warning">${balance.pendingDays}</span>
                                                                </div>
                                                                <div class="stat-item remaining">
                                                                    <span class="stat-label">Remaining:</span>
                                                                    <span
                                                                        class="stat-value text-info fw-bold">${balance.remainingDays}</span>
                                                                </div>
                                                                <div class="stat-item available">
                                                                    <span class="stat-label">Available:</span>
                                                                    <span class="stat-value text-primary fw-bold">
                                                                        ${balance.availableDays}
                                                                        <c:if
                                                                            test="${balance.availableDays < 3 && balance.availableDays > 0}">
                                                                            <span
                                                                                class="badge bg-warning text-dark ms-1">Low</span>
                                                                        </c:if>
                                                                        <c:if test="${balance.availableDays <= 0}">
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
                                                    <c:when test="${errorType == 'BALANCE_EXCEEDED'}">
                                                        <i class="fas fa-exclamation-circle fa-2x me-3"></i>
                                                    </c:when>
                                                    <c:when test="${errorType == 'OT_CONFLICT'}">
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
                                                <option value="${entry.key}" data-code="${entry.key}" <c:forEach
                                                    var="balance" items="${leaveBalances}">
                                                    <c:if
                                                        test="${balance.leaveTypeCode == entry.key && balance.availableDays <= 0}">
                                                        disabled
                                                    </c:if>
                                            </c:forEach>
                                            >${entry.value}</option>
                                            </c:forEach>
                                        </select>
                                        <div class="form-text">Choose the type of leave you want to request</div>
                                    </div>

                                    <!-- Leave Type Rules (auto show on change) -->
                                    <div id="leaveTypeRules" class="leave-type-rules d-none">
                                        <h6>Leave Type Information</h6>
                                        <div id="rulesContent" class="rules-content"></div>
                                    </div>

                                    <!-- Dates -->
                                    <div class="row g-3">
                                        <div class="col-md-6">
                                            <label for="startDate" class="form-label">
                                                <i class="fas fa-calendar-alt"></i> Start Date
                                                <span class="text-danger">*</span>
                                            </label>
                                            <input type="datetime-local" class="form-control" id="startDate"
                                                name="startDate" value="" required>
                                            <div class="form-text">When your leave will start</div>
                                        </div>
                                        <div class="col-md-6">
                                            <label for="endDate" class="form-label">
                                                <i class="fas fa-calendar-alt"></i> End Date
                                                <span class="text-danger">*</span>
                                            </label>
                                            <input type="datetime-local" class="form-control" id="endDate"
                                                name="endDate" value="" required>
                                            <div class="form-text">When your leave will end</div>
                                        </div>
                                    </div>

                                    <!-- Duration & Days Info -->
                                    <div class="mt-3">
                                        <div class="row g-3">
                                            <div class="col-md-6">
                                                <label class="form-label">
                                                    <i class="fas fa-calendar-day"></i> Calculated Duration
                                                </label>
                                                <div class="duration-display" id="durationDisplay">
                                                    <i class="fas fa-clock"></i>
                                                    <span id="durationText">Select dates to calculate</span>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label">
                                                    <i class="fas fa-info-circle"></i> Leave Type Allowance
                                                </label>
                                                <div class="allowance-display" id="allowanceDisplay">
                                                    <i class="fas fa-calendar-check"></i>
                                                    <span id="allowanceText">Select leave type first</span>
                                                </div>
                                            </div>
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
                                            required></textarea>
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
                        const allowanceDisplay = document.getElementById('allowanceDisplay');
                        const allowanceText = document.getElementById('allowanceText');
                        const certificateRequired = document.getElementById('certificateRequired');

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

                                // Show rules
                                let html = '';
                                html += '<div class="rule-item"><small>Default Days</small><br><strong>' + rules.defaultDays + ' days</strong></div>';
                                html += '<div class="rule-item"><small>Max Days</small><br><strong>' + rules.maxDays + ' days</strong></div>';
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

                                rulesContent.innerHTML = html;
                                rulesContainer.classList.remove('d-none');

                                // Update allowance - show total only
                                console.log('Updating allowance for:', selectedCode);
                                console.log('Balance data:', balanceData);
                                console.log('Selected balance:', balanceData[selectedCode]);

                                if (balanceData && balanceData[selectedCode]) {
                                    const balance = balanceData[selectedCode];
                                    console.log('Using balance data:', balance.totalAllowed);
                                    allowanceText.textContent = balance.totalAllowed + ' days total';
                                } else if (rules && rules.defaultDays) {
                                    console.log('Using rules data:', rules.defaultDays);
                                    allowanceText.textContent = rules.defaultDays + ' days total';
                                } else {
                                    console.log('No data found, using fallback');
                                    allowanceText.textContent = 'No data available';
                                }

                            } else {
                                rulesContainer.classList.add('d-none');
                                allowanceText.textContent = 'Select leave type first';
                                certificateRequired.classList.add('d-none');
                            }

                            updateDuration();
                        });

                        // Date change handlers
                        startDateInput.addEventListener('change', updateDuration);
                        endDateInput.addEventListener('change', updateDuration);

                        function updateDuration() {
                            const startDate = startDateInput.value;
                            const endDate = endDateInput.value;
                            const selectedCode = leaveTypeSelect.value;

                            if (!startDate || !endDate) {
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

                            // Calculate working days (excluding weekends)
                            let workingDays = 0;
                            let current = new Date(start);

                            while (current <= end) {
                                const dayOfWeek = current.getDay();
                                if (dayOfWeek !== 0 && dayOfWeek !== 6) { // Not Sunday or Saturday
                                    workingDays++;
                                }
                                current.setDate(current.getDate() + 1);
                            }

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

                        // Form validation
                        document.getElementById('leaveRequestForm').addEventListener('submit', function (e) {
                            if (!this.checkValidity()) {
                                e.preventDefault();
                                e.stopPropagation();
                            }
                            this.classList.add('was-validated');
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