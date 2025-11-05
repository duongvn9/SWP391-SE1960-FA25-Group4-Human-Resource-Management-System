<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<style>
#employeeSearch {
    border-bottom: 2px solid #0d6efd;
}

#employeeSearch:focus {
    box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

#genUserId {
    border: 1px solid #dee2e6;
    max-height: 250px;
}

#genUserId option {
    padding: 8px;
}

#genUserId option:hover {
    background-color: #e7f1ff;
}

.employee-search-wrapper {
    position: relative;
}

.employee-search-wrapper .search-icon {
    position: absolute;
    left: 10px;
    top: 50%;
    transform: translateY(-50%);
    color: #6c757d;
}

#employeeSearch {
    padding-left: 35px;
}

.employee-count-badge {
    display: inline-block;
    padding: 0.25rem 0.5rem;
    background-color: #e7f1ff;
    border-radius: 0.25rem;
    font-weight: 500;
}
</style>

<!-- Bulk Actions Section -->
<div class="card mb-4">
    <div class="card-body">
        <div class="d-flex justify-content-between align-items-center">
            <h5 class="mb-0">
                <i class="fas fa-cogs me-2"></i>Payslip Actions
            </h5>
            <div class="btn-group" role="group">
                <button type="button" class="btn btn-primary" onclick="showGenerateModal()">
                    <i class="fas fa-play me-2"></i>Generate Payslips
                </button>
                <button type="button" class="btn btn-success" onclick="exportPayslips('excel')">
                    <i class="fas fa-file-excel me-2"></i>Export to Excel
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Generate Modal -->
<div class="modal fade" id="generateModal" tabindex="-1" aria-labelledby="generateModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="generateModalLabel">
                    <i class="fas fa-play me-2"></i>Generate Payslips
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form id="generateForm" method="POST" action="${pageContext.request.contextPath}/payslips" enctype="application/x-www-form-urlencoded" onsubmit="return handleFormSubmit(event)">
                <div class="modal-body">
                    <div class="row g-3">
                        <!-- Payroll Period Selection -->
                        <div class="col-12">
                            <div class="alert alert-info" role="alert">
                                <i class="fas fa-info-circle me-2"></i>
                                <strong>Payroll Period:</strong> Payslips can only be generated for the previous month.
                                The system automatically sets the period based on the current date.
                            </div>
                        </div>

                        <div class="col-md-6">
                            <label for="payrollMonth" class="form-label">
                                <i class="fas fa-calendar-alt me-1"></i>Payroll Month
                            </label>
                            <select class="form-select" id="payrollMonth" name="payrollMonth" required disabled style="background-color: #e9ecef;">
                                <option value="">Select Month...</option>
                                <option value="1">January</option>
                                <option value="2">February</option>
                                <option value="3">March</option>
                                <option value="4">April</option>
                                <option value="5">May</option>
                                <option value="6">June</option>
                                <option value="7">July</option>
                                <option value="8">August</option>
                                <option value="9">September</option>
                                <option value="10">October</option>
                                <option value="11">November</option>
                                <option value="12">December</option>
                            </select>
                            <small class="text-muted">
                                <i class="fas fa-info-circle me-1"></i>Auto-set to previous month
                            </small>
                        </div>

                        <div class="col-md-6">
                            <label for="payrollYear" class="form-label">
                                <i class="fas fa-calendar me-1"></i>Payroll Year
                            </label>
                            <select class="form-select" id="payrollYear" name="payrollYear" required disabled style="background-color: #e9ecef;">
                                <option value="">Select Year...</option>
                                <c:forEach begin="2020" end="2030" var="year">
                                    <option value="${year}" ${year == 2024 ? 'selected' : ''}>${year}</option>
                                </c:forEach>
                            </select>
                            <small class="text-muted">
                                <i class="fas fa-info-circle me-1"></i>Auto-set based on current date
                            </small>
                        </div>

                        <!-- Auto-calculated Period Display -->
                        <div class="col-12">
                            <div class="card bg-light">
                                <div class="card-body py-2">
                                    <small class="text-muted">
                                        <strong>Calculated Period:</strong>
                                        <span id="calculatedPeriod" class="text-primary">Please select month and year</span>
                                    </small>
                                </div>
                            </div>
                        </div>

                        <!-- Hidden inputs for actual dates - REMOVED, let Java backend calculate from payrollMonth/payrollYear -->
                        <!-- <input type="hidden" id="genPeriodStart" name="periodStart">
                        <input type="hidden" id="genPeriodEnd" name="periodEnd"> -->

                        <!-- Scope -->
                        <div class="col-12">
                            <label class="form-label">Generation Scope</label>
                            <div class="row">
                                <div class="col-md-4">
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="scope" id="scopeAll" value="ALL" checked>
                                        <label class="form-check-label" for="scopeAll">
                                            <i class="fas fa-users me-1"></i>All Employees
                                        </label>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="scope" id="scopeDept" value="DEPARTMENT">
                                        <label class="form-check-label" for="scopeDept">
                                            <i class="fas fa-building me-1"></i>Specific Department
                                        </label>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="scope" id="scopeEmp" value="EMPLOYEE">
                                        <label class="form-check-label" for="scopeEmp">
                                            <i class="fas fa-user me-1"></i>Specific Employee
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Scope Selection -->
                        <div class="col-12" id="scopeSelection" style="display: none;">
                            <div id="deptSelection" style="display: none;">
                                <label for="genDepartmentId" class="form-label">Select Department</label>
                                <select class="form-select" id="genDepartmentId" name="scopeId">
                                    <option value="">Choose Department...</option>
                                    <c:forEach var="dept" items="${departments}">
                                        <option value="${dept.id}">${dept.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div id="empSelection" style="display: none;">
                                <label for="employeeSearch" class="form-label">
                                    <i class="fas fa-search me-1"></i>Search Employee
                                </label>
                                <div class="employee-search-wrapper">
                                    <i class="fas fa-search search-icon"></i>
                                    <input type="text"
                                           class="form-control mb-2"
                                           id="employeeSearch"
                                           placeholder="Type employee code or name to filter..."
                                           autocomplete="off">
                                </div>

                                <label for="genUserId" class="form-label mt-2">
                                    Select Employee
                                    <span class="employee-count-badge" id="employeeCountBadge">
                                        <span id="employeeCount">${fn:length(employees)}</span> found
                                    </span>
                                </label>
                                <select class="form-select" id="genUserId" name="scopeId" size="10">
                                    <option value="" disabled selected>-- Choose Employee --</option>
                                    <c:forEach var="user" items="${employees}">
                                        <option value="${user.id}"
                                                data-code="${fn:escapeXml(user.employeeCode)}"
                                                data-name="${fn:escapeXml(user.fullName)}">
                                            [${user.employeeCode}] ${user.fullName}
                                        </option>
                                    </c:forEach>
                                </select>
                                <small class="text-muted mt-1 d-block">
                                    <i class="fas fa-lightbulb me-1"></i>
                                    <strong>Tip:</strong> Type employee code or name to quickly filter the list. Press Enter to select the first match.
                                </small>
                            </div>
                        </div>

                        <!-- Options -->
                        <div class="col-12">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" id="onlyDirtyGen" name="onlyDirty" value="true">
                                        <label class="form-check-label" for="onlyDirtyGen">
                                            Only generate dirty payslips
                                        </label>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" id="forceGen" name="force" value="true">
                                        <label class="form-check-label" for="forceGen">
                                            Force regeneration of existing payslips
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Warning -->
                        <div class="col-12">
                            <div class="alert alert-warning" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                <strong>Warning:</strong> This operation may take several minutes for large datasets.
                                Please do not close this window during generation.
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-play me-1"></i>Start Generation
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Progress Modal -->
<div class="modal fade" id="progressModal" tabindex="-1" aria-labelledby="progressModalLabel" aria-hidden="true" data-bs-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="progressModalLabel">
                    <i class="fas fa-cog fa-spin me-2"></i>Processing...
                </h5>
            </div>
            <div class="modal-body text-center">
                <div class="progress mb-3">
                    <div class="progress-bar progress-bar-striped progress-bar-animated"
                         role="progressbar"
                         style="width: 0%"
                         id="progressBar">
                    </div>
                </div>
                <p id="progressMessage">Initializing...</p>
                <div id="progressDetails" class="small text-muted"></div>
            </div>
        </div>
    </div>
</div>

<script>
// Note: showGenerateModal(), showRegenerateModal(), exportPayslips()
// are defined in payslip-actions.js to avoid duplication

function quickGenerate(type) {
    const periodStart = document.getElementById('periodStart').value;
    const periodEnd = document.getElementById('periodEnd').value;

    if (!periodStart || !periodEnd) {
        alert('Please select a period first.');
        return;
    }

    let params = new URLSearchParams();
    params.append('periodStart', periodStart);
    params.append('periodEnd', periodEnd);

    switch(type) {
        case 'missing':
            params.append('scope', 'ALL');
            params.append('onlyMissing', 'true');
            break;
        case 'dirty':
            params.append('scope', 'ALL');
            params.append('onlyDirty', 'true');
            params.append('force', 'true');
            break;
        case 'current-dept':
            const deptId = document.getElementById('departmentId').value;
            if (!deptId) {
                alert('Please select a department first.');
                return;
            }
            params.append('scope', 'DEPARTMENT');
            params.append('scopeId', deptId);
            break;
    }

    // Show progress modal and start generation
    showProgressModal();
    startGeneration(params);
}

function refreshCounters() {
    location.reload();
}

function showProgressModal() {
    const progressModal = new bootstrap.Modal(document.getElementById('progressModal'));
    progressModal.show();

    // Reset progress
    document.getElementById('progressBar').style.width = '0%';
    document.getElementById('progressMessage').textContent = 'Initializing...';
    document.getElementById('progressDetails').textContent = '';
}

function startGeneration(params) {
    fetch('${pageContext.request.contextPath}/payslips/generate', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params.toString()
    })
    .then(response => response.json())
    .then(result => {
        updateProgress(100, 'Generation completed!',
                      `Created: ${result.createdCount}, Updated: ${result.updatedCount}, Errors: ${result.errorCount}`);

        setTimeout(() => {
            bootstrap.Modal.getInstance(document.getElementById('progressModal')).hide();
            location.reload(); // Refresh to show updated data
        }, 2000);
    })
    .catch(error => {
        console.error('Generation error:', error);
        updateProgress(0, 'Generation failed!', error.message);
    });
}

function updateProgress(percent, message, details) {
    document.getElementById('progressBar').style.width = percent + '%';
    document.getElementById('progressMessage').textContent = message;
    if (details) {
        document.getElementById('progressDetails').textContent = details;
    }
}

// Scope selection handlers
document.querySelectorAll('input[name="scope"]').forEach(radio => {
    radio.addEventListener('change', function() {
        const scopeSelection = document.getElementById('scopeSelection');
        const deptSelection = document.getElementById('deptSelection');
        const empSelection = document.getElementById('empSelection');

        if (this.value === 'ALL') {
            scopeSelection.style.display = 'none';
        } else {
            scopeSelection.style.display = 'block';

            if (this.value === 'DEPARTMENT') {
                deptSelection.style.display = 'block';
                empSelection.style.display = 'none';
            } else if (this.value === 'EMPLOYEE') {
                deptSelection.style.display = 'none';
                empSelection.style.display = 'block';
            }
        }
    });
});

// Function to update calculated period display (global scope)
window.updateCalculatedPeriod = function updateCalculatedPeriod() {
    const month = document.getElementById('payrollMonth').value;
    const year = document.getElementById('payrollYear').value;
    const calculatedPeriodSpan = document.getElementById('calculatedPeriod');

    console.log('[DEBUG updateCalculatedPeriod] Elements:', {
        month: month,
        year: year,
        calculatedPeriodSpan: calculatedPeriodSpan,
        spanExists: !!calculatedPeriodSpan
    });

    if (!calculatedPeriodSpan) {
        console.error('[ERROR] calculatedPeriod span not found!');
        return;
    }

    if (month && year) {
        // Calculate first and last day of the month
        const monthInt = parseInt(month);
        const yearInt = parseInt(year);

        // Calculate last day of the selected month
        const lastDayOfMonth = new Date(yearInt, monthInt, 0).getDate();

        // Format dates correctly for payroll period - ALWAYS use the selected month
        const startDate = yearInt + '-' + String(monthInt).padStart(2, '0') + '-01';
        const endDate = yearInt + '-' + String(monthInt).padStart(2, '0') + '-' + String(lastDayOfMonth).padStart(2, '0');

        console.log('[PAYSLIP PERIOD v2.0 - 21:59] Calculation:', {
            selectedMonth: month,
            selectedYear: year,
            monthInt: monthInt,
            yearInt: yearInt,
            lastDayOfMonth: lastDayOfMonth,
            calculatedStart: startDate,
            calculatedEnd: endDate,
            timestamp: new Date().toISOString()
        });

        // Update display
        const monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
                           'July', 'August', 'September', 'October', 'November', 'December'];
        const monthName = monthNames[month - 1];

        const displayText = '<strong>' + monthName + ' ' + year + '</strong> (' + startDate + ' to ' + endDate + ')';
        console.log('[DEBUG] Setting display text:', displayText);

        calculatedPeriodSpan.innerHTML = displayText;
        calculatedPeriodSpan.className = 'text-success fw-bold';
    } else {
        calculatedPeriodSpan.textContent = 'Please select month and year';
        calculatedPeriodSpan.className = 'text-muted';
    }
}

// Employee Search Filter
function filterEmployees() {
    const searchInput = document.getElementById('employeeSearch');
    const employeeSelect = document.getElementById('genUserId');
    const employeeCountSpan = document.getElementById('employeeCount');
    const countBadge = document.getElementById('employeeCountBadge');

    if (!searchInput || !employeeSelect) return;

    const searchTerm = searchInput.value.toLowerCase().trim();
    const options = employeeSelect.querySelectorAll('option');
    let visibleCount = 0;
    let firstVisibleOption = null;

    options.forEach(option => {
        if (option.disabled) {
            // Always show the disabled placeholder option
            option.style.display = '';
            return;
        }

        const code = (option.dataset.code || '').toLowerCase();
        const name = (option.dataset.name || '').toLowerCase();

        // Match against code or name
        const matches = searchTerm === '' ||
                       code.includes(searchTerm) ||
                       name.includes(searchTerm);

        if (matches) {
            option.style.display = '';
            visibleCount++;
            if (!firstVisibleOption) {
                firstVisibleOption = option;
            }
        } else {
            option.style.display = 'none';
        }
    });

    // Update count display
    if (employeeCountSpan) {
        employeeCountSpan.textContent = visibleCount;
    }

    // Update badge color based on results
    if (countBadge) {
        if (visibleCount === 0) {
            countBadge.style.backgroundColor = '#f8d7da';
            countBadge.style.color = '#721c24';
        } else if (visibleCount <= 5) {
            countBadge.style.backgroundColor = '#d1ecf1';
            countBadge.style.color = '#0c5460';
        } else {
            countBadge.style.backgroundColor = '#e7f1ff';
            countBadge.style.color = '#004085';
        }
    }

    // Show message if no results
    const noResultsMsg = document.getElementById('noEmployeeResults');
    if (visibleCount === 0 && searchTerm !== '') {
        if (!noResultsMsg) {
            const msg = document.createElement('small');
            msg.id = 'noEmployeeResults';
            msg.className = 'text-danger d-block mt-1';
            msg.innerHTML = '<i class="fas fa-exclamation-circle me-1"></i>No employees found matching "' + searchTerm + '"';
            employeeSelect.parentNode.appendChild(msg);
        }
    } else if (noResultsMsg) {
        noResultsMsg.remove();
    }

    // Auto-highlight first match
    if (firstVisibleOption && searchTerm !== '') {
        employeeSelect.value = firstVisibleOption.value;
    }
}

// Clear employee filter
function clearEmployeeFilter() {
    const searchInput = document.getElementById('employeeSearch');
    const employeeSelect = document.getElementById('genUserId');

    if (searchInput) {
        searchInput.value = '';
        filterEmployees();
    }

    if (employeeSelect) {
        employeeSelect.value = '';
    }
}

// Add event listeners for month/year changes
document.addEventListener('DOMContentLoaded', function() {
    const monthSelect = document.getElementById('payrollMonth');
    const yearSelect = document.getElementById('payrollYear');

    // Note: Month/Year selects are disabled since they're auto-set to previous month
    // They will be enabled before form submission to ensure values are sent

    // Enable disabled selects before form submission
    const generateForm = document.getElementById('generateForm');
    if (generateForm) {
        generateForm.addEventListener('submit', function(e) {
            // Enable month and year selects so their values are submitted
            if (monthSelect) monthSelect.disabled = false;
            if (yearSelect) yearSelect.disabled = false;
        });
    }

    // Employee search filter
    const employeeSearch = document.getElementById('employeeSearch');
    if (employeeSearch) {
        // Real-time filter as user types
        employeeSearch.addEventListener('input', filterEmployees);

        // Handle Enter key
        employeeSearch.addEventListener('keydown', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                const employeeSelect = document.getElementById('genUserId');
                if (employeeSelect && employeeSelect.value) {
                    // Blur search input and focus on select
                    employeeSelect.focus();
                }
            } else if (e.key === 'Escape') {
                // Clear search on Escape
                clearEmployeeFilter();
            }
        });

        // Focus search input when employee scope is selected
        document.getElementById('scopeEmp')?.addEventListener('change', function() {
            if (this.checked) {
                setTimeout(() => {
                    employeeSearch.focus();
                }, 100);
            }
        });

        // Clear search when switching away from employee scope
        document.querySelectorAll('input[name="scope"]').forEach(radio => {
            radio.addEventListener('change', function() {
                if (this.value !== 'EMPLOYEE' && employeeSearch.value) {
                    clearEmployeeFilter();
                }
            });
        });
    }
});

// Handle form submission
function handleFormSubmit(event) {
    event.preventDefault(); // Prevent default form submission

    // Validate form before submission
    const month = document.getElementById('payrollMonth').value;
    const year = document.getElementById('payrollYear').value;
    const periodStart = document.getElementById('genPeriodStart').value;
    const periodEnd = document.getElementById('genPeriodEnd').value;

    console.log('[DEBUG] Form validation:', {
        month: month,
        year: year,
        periodStart: periodStart,
        periodEnd: periodEnd
    });

    if (!month || !year) {
        alert('Please select both payroll month and year.');
        return false;
    }

    if (!periodStart || !periodEnd) {
        alert('Period dates are not calculated. Please select month and year again.');
        // Try to recalculate
        updateCalculatedPeriod();
        return false;
    }

    // Force recalculate if period looks wrong
    if (periodStart && periodStart.includes('09-30')) {
        console.log('[DEBUG] Period looks wrong, recalculating...');
        updateCalculatedPeriod();
        const newPeriodStart = document.getElementById('genPeriodStart').value;
        const newPeriodEnd = document.getElementById('genPeriodEnd').value;
        console.log('[DEBUG] After recalculation:', {
            newPeriodStart: newPeriodStart,
            newPeriodEnd: newPeriodEnd
        });
    }

    // Call the AJAX handler from payslip-actions.js
    if (typeof handleBulkGenerate === 'function') {
        handleBulkGenerate();
    } else {
        console.error('handleBulkGenerate function not found');
        alert('Error: Generation function not available. Please refresh the page.');
    }

    return false; // Prevent form submission
}
</script>