<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!-- Employees Needing Payslips Section -->
<div class="row g-3 mb-4">
    <!-- Employees Without Payslip for Previous Month -->
    <div class="col-lg-6">
        <div class="alert alert-warning" role="alert">
            <div class="clickable-header" style="cursor: pointer;" data-bs-toggle="collapse"
                data-bs-target="#employeesWithoutPayslipCollapse" aria-expanded="false"
                aria-controls="employeesWithoutPayslipCollapse" onclick="loadEmployeesWithoutPayslip()">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <h6 class="mb-0">
                        <i class="fas fa-exclamation-triangle me-2"></i>Employees Without Payslip
                        <span class="badge bg-warning text-dark" id="employeesWithoutPayslipCount">
                            <c:choose>
                                <c:when test="${not empty employeesWithoutPayslip}">
                                    ${fn:length(employeesWithoutPayslip)}
                                </c:when>
                                <c:otherwise>...</c:otherwise>
                            </c:choose>
                        </span>
                    </h6>
                    <i class="fas fa-chevron-down toggle-icon"></i>
                </div>
                <p class="mb-0 small">Employees who need payslips generated for the previous month. Click to view details.</p>
            </div>
            <div class="collapse mt-2" id="employeesWithoutPayslipCollapse">
                <div id="employeesWithoutPayslipContent">
                    <!-- Loading state -->
                    <div class="text-center py-3" id="employeesWithoutPayslipLoading">
                        <div class="spinner-border spinner-border-sm text-warning" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                        <p class="mt-2 mb-0 small text-muted">Loading employees...</p>
                    </div>
                    <!-- Content will be loaded here -->
                    <div class="list-group" id="employeesWithoutPayslipList" style="display: none;">
                        <!-- Items will be dynamically loaded -->
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Employees With Attendance Changes -->
    <div class="col-lg-6">
        <div class="alert alert-primary" role="alert" style="background-color: #e3f2fd; border-color: #2196f3; color: #0d47a1;">
            <div class="clickable-header" style="cursor: pointer;" data-bs-toggle="collapse"
                data-bs-target="#employeesWithAttendanceChangesCollapse" aria-expanded="false"
                aria-controls="employeesWithAttendanceChangesCollapse" onclick="loadEmployeesWithAttendanceChanges()">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <h6 class="mb-0" style="color: #0d47a1;">
                        <i class="fas fa-user-clock me-2"></i>Employees With Attendance Changes
                        <span class="badge bg-primary" id="employeesWithAttendanceChangesCount">
                            <c:choose>
                                <c:when test="${not empty employeesWithAttendanceChanges}">
                                    ${fn:length(employeesWithAttendanceChanges)}
                                </c:when>
                                <c:otherwise>...</c:otherwise>
                            </c:choose>
                        </span>
                    </h6>
                    <i class="fas fa-chevron-down toggle-icon" style="color: #0d47a1;"></i>
                </div>
                <p class="mb-0 small" style="color: #1565c0;">Employees with attendance modifications in the current payroll period. These may need payslip regeneration.</p>
            </div>
            <div class="collapse mt-2" id="employeesWithAttendanceChangesCollapse">
                <div id="employeesWithAttendanceChangesContent">
                    <!-- Loading state -->
                    <div class="text-center py-3" id="employeesWithAttendanceChangesLoading">
                        <div class="spinner-border spinner-border-sm text-primary" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                        <p class="mt-2 mb-0 small" style="color: #1565c0;">Loading employees...</p>
                    </div>
                    <!-- Content will be loaded here -->
                    <div class="list-group" id="employeesWithAttendanceChangesList" style="display: none;">
                        <!-- Items will be dynamically loaded -->
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Payroll Period Lock Notice -->
<c:if test="${not empty payrollLockInfo && payrollLockInfo.isLocked}">
    <div class="alert alert-danger mb-4" role="alert">
        <div class="d-flex align-items-center">
            <i class="fas fa-lock fa-2x me-3"></i>
            <div>
                <h6 class="mb-1">
                    <strong>Payroll Period Locked</strong>
                </h6>
                <p class="mb-0 small">
                    The payroll period for <strong>${payrollLockInfo.lockedPeriod}</strong> has been locked since
                    <strong>${payrollLockInfo.lockDate}</strong>.
                    No new payslips can be generated or modified for this period.
                </p>
                <p class="mb-0 small mt-1">
                    <i class="fas fa-info-circle me-1"></i>
                    Payroll periods are automatically locked <strong>7 days</strong> after the start of a new month.
                </p>
            </div>
        </div>
    </div>
</c:if>

<style>
.clickable-header:hover {
    opacity: 0.8;
}

.clickable-header .toggle-icon {
    transition: transform 0.3s ease;
}

.clickable-header[aria-expanded="true"] .toggle-icon {
    transform: rotate(180deg);
}

.list-group-item:hover {
    background-color: #f8f9fa;
}

.employee-badge {
    font-size: 0.75rem;
    padding: 0.25rem 0.5rem;
}
</style>

<script>
let employeesWithoutPayslipLoaded = false;
let employeesWithAttendanceChangesLoaded = false;

// Load employees without payslip for previous month
function loadEmployeesWithoutPayslip() {
    if (employeesWithoutPayslipLoaded) return;

    const loadingDiv = document.getElementById('employeesWithoutPayslipLoading');
    const listDiv = document.getElementById('employeesWithoutPayslipList');

    // Show loading
    loadingDiv.style.display = 'block';
    listDiv.style.display = 'none';

    // Fetch data from server
    fetch('${pageContext.request.contextPath}/payslips/api/employees-without-payslip')
        .then(response => response.json())
        .then(data => {
            const countBadge = document.getElementById('employeesWithoutPayslipCount');
            countBadge.textContent = data.total || 0;

            if (data.employees && data.employees.length > 0) {
                let html = '';
                data.employees.forEach(emp => {
                    html += `
                        <div class="list-group-item list-group-item-action">
                            <div class="d-flex w-100 justify-content-between align-items-center">
                                <div>
                                    <h6 class="mb-1">\${emp.fullName}</h6>
                                    <small class="text-muted">
                                        <i class="fas fa-id-badge me-1"></i>\${emp.employeeCode}
                                        <span class="mx-2">|</span>
                                        <i class="fas fa-building me-1"></i>\${emp.departmentName}
                                    </small>
                                </div>
                                <div>
                                    <span class="badge bg-warning text-dark employee-badge">
                                        Missing: \${emp.missingPeriod}
                                    </span>
                                    <button class="btn btn-sm btn-primary ms-2"
                                            onclick="generatePayslipForEmployee(\${emp.userId}, '\${emp.missingPeriod}')">
                                        <i class="fas fa-plus me-1"></i>Generate
                                    </button>
                                </div>
                            </div>
                        </div>
                    `;
                });
                listDiv.innerHTML = html;
            } else {
                listDiv.innerHTML = `
                    <div class="text-center py-3">
                        <i class="fas fa-check-circle fa-2x text-success mb-2"></i>
                        <p class="mb-0 text-muted">All employees have payslips for the previous month.</p>
                    </div>
                `;
            }

            loadingDiv.style.display = 'none';
            listDiv.style.display = 'block';
            employeesWithoutPayslipLoaded = true;
        })
        .catch(error => {
            console.error('Error loading employees without payslip:', error);
            listDiv.innerHTML = `
                <div class="text-center py-3 text-danger">
                    <i class="fas fa-exclamation-circle me-1"></i>
                    Error loading data. Please try again.
                </div>
            `;
            loadingDiv.style.display = 'none';
            listDiv.style.display = 'block';
        });
}

// Load employees with attendance changes
function loadEmployeesWithAttendanceChanges() {
    if (employeesWithAttendanceChangesLoaded) return;

    const loadingDiv = document.getElementById('employeesWithAttendanceChangesLoading');
    const listDiv = document.getElementById('employeesWithAttendanceChangesList');

    // Show loading
    loadingDiv.style.display = 'block';
    listDiv.style.display = 'none';

    // Fetch data from server
    fetch('${pageContext.request.contextPath}/payslips/api/employees-with-attendance-changes')
        .then(response => response.json())
        .then(data => {
            const countBadge = document.getElementById('employeesWithAttendanceChangesCount');
            countBadge.textContent = data.total || 0;

            if (data.employees && data.employees.length > 0) {
                let html = '';
                data.employees.forEach(emp => {
                    html += `
                        <div class="list-group-item list-group-item-action">
                            <div class="d-flex w-100 justify-content-between align-items-center">
                                <div>
                                    <h6 class="mb-1">\${emp.fullName}</h6>
                                    <small class="text-muted">
                                        <i class="fas fa-id-badge me-1"></i>\${emp.employeeCode}
                                        <span class="mx-2">|</span>
                                        <i class="fas fa-building me-1"></i>\${emp.departmentName}
                                    </small>
                                    <br>
                                    <small style="color: #1976d2;">
                                        <i class="fas fa-clock me-1"></i>Last modified: \${emp.lastModified}
                                    </small>
                                </div>
                                <div>
                                    <span class="badge bg-primary employee-badge">
                                        \${emp.changesCount} change(s)
                                    </span>
                                    <button class="btn btn-sm btn-warning ms-2"
                                            onclick="regeneratePayslipForEmployee(\${emp.userId}, '\${emp.affectedPeriod}')">
                                        <i class="fas fa-sync me-1"></i>Regenerate
                                    </button>
                                </div>
                            </div>
                        </div>
                    `;
                });
                listDiv.innerHTML = html;
            } else {
                listDiv.innerHTML = `
                    <div class="text-center py-3">
                        <i class="fas fa-check-circle fa-2x text-success mb-2"></i>
                        <p class="mb-0 text-muted">No attendance changes detected for the current period.</p>
                    </div>
                `;
            }

            loadingDiv.style.display = 'none';
            listDiv.style.display = 'block';
            employeesWithAttendanceChangesLoaded = true;
        })
        .catch(error => {
            console.error('Error loading employees with attendance changes:', error);
            listDiv.innerHTML = `
                <div class="text-center py-3 text-danger">
                    <i class="fas fa-exclamation-circle me-1"></i>
                    Error loading data. Please try again.
                </div>
            `;
            loadingDiv.style.display = 'none';
            listDiv.style.display = 'block';
        });
}

// Generate payslip for specific employee
function generatePayslipForEmployee(userId, period) {
    // Use system time for checking
    const currentDate = new Date();
    const currentDay = currentDate.getDate();
    const withinCutoff = currentDay <= 7;

    let message = `Generate payslip for this employee for period ${period}?`;
    if (!withinCutoff) {
        message += '\n\nNote: After day 7, generating new payslips is still allowed.';
    }

    if (!confirm(message)) {
        return;
    }

    // Show generate modal with pre-filled data
    showGenerateModal();
    document.getElementById('scopeEmp').checked = true;
    document.getElementById('scopeEmp').dispatchEvent(new Event('change'));
    document.getElementById('genUserId').value = userId;

    // Parse and set period
    const [year, month] = period.split('-');
    document.getElementById('payrollMonth').value = parseInt(month);
    document.getElementById('payrollYear').value = parseInt(year);
    updateCalculatedPeriod();
}

// Regenerate payslip for specific employee
function regeneratePayslipForEmployee(userId, period) {
    // Use system time for checking
    const currentDate = new Date();
    const currentDay = currentDate.getDate();
    const withinCutoff = currentDay <= 7;

    let message = `Regenerate payslip for this employee for period ${period}?`;
    if (!withinCutoff) {
        message += '\n\nNote: After day 7, only dirty payslips should be regenerated. This will force regeneration.';
    }

    if (!confirm(message)) {
        return;
    }

    // Show generate modal with pre-filled data and force regenerate
    showGenerateModal();
    document.getElementById('scopeEmp').checked = true;
    document.getElementById('scopeEmp').dispatchEvent(new Event('change'));
    document.getElementById('genUserId').value = userId;
    document.getElementById('forceGen').checked = true;

    // Parse and set period
    const [year, month] = period.split('-');
    document.getElementById('payrollMonth').value = parseInt(month);
    document.getElementById('payrollYear').value = parseInt(year);
    updateCalculatedPeriod();
}
</script>
