/**
 * Show success message
 */
function showSuccess(message) {
    if (typeof showToast === 'function') {
        showToast(message, 'success');
    } else {
        alert('Success: ' + message);
    }
}

/**
 * Show error message
 */
function showError(message) {
    if (typeof showToast === 'function') {
        showToast(message, 'error');
    } else {
        alert('Error: ' + message);
    }
}

/**
 * Show warning message
 */
function showWarning(message) {
    if (typeof showToast === 'function') {
        showToast(message, 'warning');
    } else {
        alert('Warning: ' + message);
    }
}

/**
 * Show error details
 */
function showErrorDetails(errors) {
    if (errors && errors.length > 0) {
        const errorList = errors.join('\n');
        console.error('Generation errors:', errorList);
        showError('Generation completed with errors:\n' + errorList);
    }
}

/**
 * Initialize all event listeners for payslip actions
 */
function initializeEventListeners() {
    // Filter form event listeners
    const filterMonth = document.getElementById('filterMonth');
    const filterYear = document.getElementById('filterYear');

    if (filterMonth) {
        filterMonth.addEventListener('change', updateFilterPeriod);
    }

    if (filterYear) {
        filterYear.addEventListener('change', updateFilterPeriod);
    }

    // Initialize period calculation if month/year are already selected
    updateFilterPeriod();

    // Handle view mode toggle for HRM
    const viewModeRadios = document.querySelectorAll('input[name="viewMode"]');
    viewModeRadios.forEach(radio => {
        radio.addEventListener('change', handleViewModeChange);
    });

    // Set initial view mode based on URL parameter
    const urlParams = new URLSearchParams(window.location.search);
    const viewMode = urlParams.get('viewMode');
    if (viewMode === 'personal') {
        const personalViewRadio = document.getElementById('myPayslipsView');
        if (personalViewRadio) {
            personalViewRadio.checked = true;
            switchToPersonalView();
        }
    }

    // Modal event listeners
    const payrollMonth = document.getElementById('payrollMonth');
    const payrollYear = document.getElementById('payrollYear');

    if (payrollMonth) {
        payrollMonth.addEventListener('change', updateCalculatedPeriod);
    }

    if (payrollYear) {
        payrollYear.addEventListener('change', updateCalculatedPeriod);
    }

    // Scope selection handlers
    document.querySelectorAll('input[name="scope"]').forEach(radio => {
        radio.addEventListener('change', handleScopeChange);
    });

    // Individual regenerate buttons
    document.querySelectorAll('.regenerate-payslip-btn').forEach(btn => {
        btn.addEventListener('click', handleIndividualRegenerate);
    });

    // Quick action buttons from issues panel
    document.querySelectorAll('.quick-generate-btn').forEach(btn => {
        btn.addEventListener('click', handleQuickGenerate);
    });

    document.querySelectorAll('.quick-regenerate-btn').forEach(btn => {
        btn.addEventListener('click', handleQuickRegenerate);
    });
}

/**
 * Handle bulk payslip generation
 * Requirements: 3.1, 3.2, 9.1
 */
function handleBulkGenerate() {
    const form = document.getElementById('generateForm');
    if (!form) {
        showError('Generation form not found');
        return;
    }

    const formData = new FormData(form);

    // Validate business rules before submission
    if (!validateGenerationRules(formData)) {
        return;
    }

    // Convert FormData to URLSearchParams for proper encoding
    const params = new URLSearchParams();
    for (let [key, value] of formData.entries()) {
        params.append(key, value);
    }
    params.append('action', 'generate');

    // Debug: Log all parameters
    console.log('[DEBUG] Request parameters:');
    for (let [key, value] of params.entries()) {
        console.log(`  ${key}: ${value}`);
    }

    // Show loading state
    const btn = form.querySelector('button[type="submit"]');
    const originalText = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Generating...';

    // Make AJAX request
    fetch('/HRMS/payslips', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params.toString()
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            showSuccess(`Generation completed: ${data.createdCount} created, ${data.updatedCount} updated, ${data.errorCount} errors`);
            // Hide modal and refresh page
            const modal = bootstrap.Modal.getInstance(document.getElementById('generateModal'));
            if (modal) modal.hide();
            setTimeout(() => {
                window.location.reload();
            }, 2000);
        } else {
            showError(data.error || 'Generation failed');
            if (data.errors && data.errors.length > 0) {
                showErrorDetails(data.errors);
            }
        }
    })
    .catch(error => {
        console.error('Generation error:', error);
        showError('Network error occurred during generation: ' + error.message);

        // Close modal on error
        const modal = bootstrap.Modal.getInstance(document.getElementById('generateModal'));
        if (modal) modal.hide();
    })
    .finally(() => {
        // Restore button state
        btn.disabled = false;
        btn.innerHTML = originalText;
    });
}

/**
 * Get month name from month number (1-based)
 */
function getMonthName(month) {
    const monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
                       'July', 'August', 'September', 'October', 'November', 'December'];
    return monthNames[month - 1] || 'Unknown';
}

/**
 * Validate generation business rules
 */
function validateGenerationRules(formData) {
    const month = parseInt(formData.get('payrollMonth'));
    const year = parseInt(formData.get('payrollYear'));

    if (!month || !year) {
        showError('Please select both month and year');
        return false;
    }

    const currentDate = new Date();
    const currentMonth = currentDate.getMonth() + 1; // 1-based
    const currentYear = currentDate.getFullYear();
    const currentDay = currentDate.getDate();

    // Rule 1: Can only generate for previous months, not current or future
    if (year > currentYear || (year === currentYear && month >= currentMonth)) {
        showError('Payslips can only be generated for previous months, not current or future months.');
        return false;
    }

    // Rule 2: Can only generate within cutoff window (first 7 days of following month)
    // Example: October payslip can only be generated from Nov 1-7
    const payrollDate = new Date(year, month - 1, 1); // First day of payroll month
    const followingMonth = new Date(payrollDate);
    followingMonth.setMonth(followingMonth.getMonth() + 1); // First day of following month

    const isInFollowingMonth = (currentYear === followingMonth.getFullYear() &&
                               currentMonth === followingMonth.getMonth() + 1);

    if (!isInFollowingMonth) {
        showError(`Payslips for ${getMonthName(month)} ${year} can only be generated in ${getMonthName(followingMonth.getMonth() + 1)} ${followingMonth.getFullYear()}.`);
        return false;
    }

    if (currentDay > 7) {
        showError(`The generation window has closed. Payslips for ${getMonthName(month)} ${year} could only be generated within the first 7 days of ${getMonthName(currentMonth)} ${currentYear}.`);
        return false;
    }

    // Rule 3: Don't allow generating for too old periods (more than 12 months ago)
    const monthsAgo = (currentYear - year) * 12 + (currentMonth - month);
    if (monthsAgo > 12) {
        showError('Cannot generate payslips for periods older than 12 months.');
        return false;
    }

    return true;
}

/**
 * Handle bulk payslip regeneration
 * Requirements: 3.2, 9.1
 */
function handleBulkRegenerate() {
    showConfirmModal({
        title: 'Bulk Regenerate Payslips',
        message: 'Are you sure you want to regenerate all dirty payslips?<br><small class="text-muted">This will recalculate all amounts and may take several minutes.</small>',
        confirmText: 'Regenerate All',
        cancelText: 'Cancel',
        confirmClass: 'btn-warning',
        icon: 'fa-sync-alt',
        iconColor: 'text-warning',
        onConfirm: () => {
            performBulkRegenerate();
        }
    });
}

function performBulkRegenerate() {

    const params = new URLSearchParams();
    params.append('action', 'regenerate');

    // Get current filter parameters
    const filterForm = document.getElementById('hrmFilterForm');
    if (filterForm) {
        const filterData = new FormData(filterForm);
        for (let [key, value] of filterData.entries()) {
            params.append(key, value);
        }
    }

    // Show loading state
    const btn = document.getElementById('bulk-regenerate-btn');
    if (!btn) return;

    const originalText = btn.textContent;
    btn.disabled = true;
    btn.textContent = 'Regenerating...';

    // Make AJAX request
    fetch('/HRMS/payslips', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params.toString()
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            showSuccess(data.message);
            setTimeout(() => {
                window.location.reload();
            }, 2000);
        } else {
            showError(data.error || 'Regeneration failed');
            if (data.errors && data.errors.length > 0) {
                showErrorDetails(data.errors);
            }
        }
    })
    .catch(error => {
        console.error('Regeneration error:', error);
        showError('Network error occurred during regeneration');
    })
    .finally(() => {
        btn.disabled = false;
        btn.textContent = originalText;
    });
}

/**
 * Handle individual payslip regeneration
 * Requirements: 3.2, 9.1
 */
function handleIndividualRegenerate(event) {
    const btn = event.target.closest('button');
    const payslipId = btn.getAttribute('data-payslip-id');
    const employeeName = btn.getAttribute('data-employee-name') || '';

    if (!payslipId) {
        showError('Payslip ID not found');
        return;
    }

    showConfirmModal({
        title: 'Regenerate Payslip',
        message: employeeName
            ? `Are you sure you want to regenerate the payslip for <strong>${employeeName}</strong>?<br><small class="text-muted">This will recalculate all amounts based on current data.</small>`
            : 'Are you sure you want to regenerate this payslip?<br><small class="text-muted">This will recalculate all amounts based on current data.</small>',
        confirmText: 'Regenerate',
        cancelText: 'Cancel',
        confirmClass: 'btn-warning',
        icon: 'fa-sync-alt',
        iconColor: 'text-warning',
        onConfirm: () => {
            performIndividualRegenerate(btn, payslipId);
        }
    });
}

function performIndividualRegenerate(btn, payslipId) {

    const params = new URLSearchParams();
    params.append('action', 'regenerate');
    params.append('payslipId', payslipId);
    params.append('force', 'true');

    // Show loading state
    const originalText = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';

    fetch('/HRMS/payslips', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params.toString()
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            showSuccess('Payslip regenerated successfully');
            setTimeout(() => {
                window.location.reload();
            }, 1500);
        } else {
            showError(data.error || 'Regeneration failed');
        }
    })
    .catch(error => {
        console.error('Regeneration error:', error);
        showError('Network error occurred');
    })
    .finally(() => {
        btn.disabled = false;
        btn.innerHTML = originalText;
    });
}

/**
 * Handle data export
 * Requirements: 9.2, 9.3
 */
function handleExport(format) {
    const filterForm = document.getElementById('hrmFilterForm');
    const formData = new FormData();

    formData.append('action', 'export');
    formData.append('format', format);

    // Add current filter parameters
    if (filterForm) {
        const filterData = new FormData(filterForm);
        for (let [key, value] of filterData.entries()) {
            formData.append(key, value);
        }
    }

    // Show loading state
    const btn = document.getElementById(`export-${format}-btn`);
    if (btn) {
        const originalText = btn.textContent;
        btn.disabled = true;
        btn.textContent = 'Exporting...';

        // Restore button state after a delay
        setTimeout(() => {
            btn.disabled = false;
            btn.textContent = originalText;
        }, 2000);
    }

    // Create a temporary form for file download
    const downloadForm = document.createElement('form');
    downloadForm.method = 'POST';
    downloadForm.action = '/HRMS/payslips';
    downloadForm.style.display = 'none';

    // Add form data as hidden inputs
    for (let [key, value] of formData.entries()) {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = key;
        input.value = value;
        downloadForm.appendChild(input);
    }

    document.body.appendChild(downloadForm);
    downloadForm.submit();
    document.body.removeChild(downloadForm);

    showInfo(`${format.toUpperCase()} export started. Download will begin shortly.`);
}

/**
 * Handle quick generation from issues panel
 * Requirements: 4.4, 4.5, 4.6, 4.7
 */
function handleQuickGenerate(event) {
    const btn = event.target;
    const userIds = btn.getAttribute('data-user-ids');
    const periodStart = btn.getAttribute('data-period-start');
    const periodEnd = btn.getAttribute('data-period-end');

    if (!userIds || !periodStart || !periodEnd) {
        showError('Missing required data for quick generation');
        return;
    }

    const params = new URLSearchParams();
    params.append('action', 'quickgenerate');
    params.append('periodStart', periodStart);
    params.append('periodEnd', periodEnd);

    // Add user IDs (can be multiple)
    const userIdArray = userIds.split(',');
    userIdArray.forEach(userId => {
        params.append('userIds', userId.trim());
    });

    // Show loading state
    const originalText = btn.textContent;
    btn.disabled = true;
    btn.textContent = 'Generating...';

    fetch('/HRMS/payslips', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params.toString()
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            showSuccess(`Quick generation completed: ${data.successCount} success, ${data.failureCount} failures`);
            setTimeout(() => {
                window.location.reload();
            }, 2000);
        } else {
            showError(data.error || 'Quick generation failed');
            if (data.errors && data.errors.length > 0) {
                showErrorDetails(data.errors);
            }
        }
    })
    .catch(error => {
        console.error('Quick generation error:', error);
        showError('Network error occurred');
    })
    .finally(() => {
        btn.disabled = false;
        btn.textContent = originalText;
    });
}

/**
 * Handle quick regeneration from issues panel
 * Requirements: 4.6, 4.7
 */
function handleQuickRegenerate(event) {
    const btn = event.target;
    const payslipIds = btn.getAttribute('data-payslip-ids');

    if (!payslipIds) {
        showError('Missing payslip IDs for quick regeneration');
        return;
    }

    const payslipCount = payslipIds.split(',').length;

    showConfirmModal({
        title: 'Quick Regenerate',
        message: `Are you sure you want to regenerate <strong>${payslipCount}</strong> payslip(s)?<br><small class="text-muted">This will recalculate all amounts based on current data.</small>`,
        confirmText: 'Regenerate',
        cancelText: 'Cancel',
        confirmClass: 'btn-warning',
        icon: 'fa-sync-alt',
        iconColor: 'text-warning',
        onConfirm: () => {
            performQuickRegenerate(btn, payslipIds);
        }
    });
}

function performQuickRegenerate(btn, payslipIds) {

    const params = new URLSearchParams();
    params.append('action', 'quickregenerate');

    // Add payslip IDs (can be multiple)
    const payslipIdArray = payslipIds.split(',');
    payslipIdArray.forEach(payslipId => {
        params.append('payslipIds', payslipId.trim());
    });

    // Show loading state
    const originalText = btn.textContent;
    btn.disabled = true;
    btn.textContent = 'Regenerating...';

    fetch('/HRMS/payslips', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params.toString()
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            showSuccess(`Quick regeneration completed: ${data.successCount} success, ${data.failureCount} failures`);
            setTimeout(() => {
                window.location.reload();
            }, 2000);
        } else {
            showError(data.error || 'Quick regeneration failed');
            if (data.errors && data.errors.length > 0) {
                showErrorDetails(data.errors);
            }
        }
    })
    .catch(error => {
        console.error('Quick regeneration error:', error);
        showError('Network error occurred');
    })
    .finally(() => {
        btn.disabled = false;
        btn.textContent = originalText;
    });
}

/**
 * Show success message
 */
function showSuccess(message) {
    showNotification(message, 'success');
}

/**
 * Show error message
 */
function showError(message) {
    showNotification(message, 'error');
}

/**
 * Show info message
 */
function showInfo(message) {
    showNotification(message, 'info');
}

/**
 * Show warning message
 */
function showWarning(message) {
    showNotification(message, 'warning');
}

/**
 * Show notification with different types
 */
function showNotification(message, type = 'info') {
    // Try to use existing toast system if available
    if (typeof showToast === 'function') {
        showToast(message, type);
        return;
    }

    // Fallback to simple alert or console
    if (type === 'error') {
        alert('Error: ' + message);
        console.error(message);
    } else if (type === 'success') {
        alert('Success: ' + message);
        console.log(message);
    } else if (type === 'warning') {
        alert('Warning: ' + message);
        console.warn(message);
    } else {
        alert(message);
        console.info(message);
    }
}

/**
 * Show detailed error messages
 */
function showErrorDetails(errors) {
    if (!errors || errors.length === 0) return;

    let errorMessage = 'Detailed errors:\n';
    errors.forEach((error, index) => {
        errorMessage += `${index + 1}. ${error}\n`;
    });

    console.error('Detailed errors:', errors);

    // Show in a more user-friendly way if possible
    if (errors.length <= 5) {
        alert(errorMessage);
    } else {
        alert(`${errors.length} errors occurred. Check the console for details.`);
    }
}

/**
 * Utility function to get current filter parameters
 */
function getCurrentFilters() {
    const filterForm = document.getElementById('hrmFilterForm');
    const filters = {};

    if (filterForm) {
        const formData = new FormData(filterForm);
        for (let [key, value] of formData.entries()) {
            if (value && value.trim() !== '') {
                filters[key] = value;
            }
        }
    }

    return filters;
}

/**
 * Function to update period dates based on month/year selection
 */
function updateFilterPeriod() {
    const month = document.getElementById('filterMonth').value;
    const year = document.getElementById('filterYear').value;
    const periodStartInput = document.getElementById('periodStart');
    const periodEndInput = document.getElementById('periodEnd');

    if (month && year) {
        // Calculate first and last day of the month
        const firstDay = new Date(year, month - 1, 1);
        const lastDay = new Date(year, month, 0);

        // Format dates
        const startDate = firstDay.toISOString().split('T')[0];
        const endDate = lastDay.toISOString().split('T')[0];

        // Update hidden inputs
        if (periodStartInput) periodStartInput.value = startDate;
        if (periodEndInput) periodEndInput.value = endDate;
    } else {
        // Clear hidden inputs
        if (periodStartInput) periodStartInput.value = '';
        if (periodEndInput) periodEndInput.value = '';
    }
}

/**
 * Handle view mode change between Manage All and My Payslips
 */
function handleViewModeChange(event) {
    const viewMode = event.target.value;

    if (viewMode === 'personal') {
        switchToPersonalView();
        // Add personal view parameter and reload
        const currentUrl = new URL(window.location);
        currentUrl.searchParams.set('viewMode', 'personal');
        window.location.href = currentUrl.toString();
    } else {
        switchToManageView();
        // Remove personal view parameter and reload
        const currentUrl = new URL(window.location);
        currentUrl.searchParams.delete('viewMode');
        window.location.href = currentUrl.toString();
    }
}

/**
 * Switch to personal view mode
 */
function switchToPersonalView() {
    const pageTitle = document.getElementById('pageTitle');
    const pageSubtitle = document.getElementById('pageSubtitle');
    const summaryCounters = document.querySelector('.summary-counters');
    const bulkActions = document.querySelector('[id*="bulk"], .bulk-actions');
    const issuesPanel = document.querySelector('[class*="issues"], .issues-panel');

    if (pageTitle) pageTitle.textContent = 'My Payslips';
    if (pageSubtitle) pageSubtitle.textContent = 'View your personal payslip records';

    // Hide management features
    if (summaryCounters) summaryCounters.style.display = 'none';
    if (bulkActions) bulkActions.style.display = 'none';
    if (issuesPanel) issuesPanel.style.display = 'none';
}

/**
 * Switch to manage view mode
 */
function switchToManageView() {
    const pageTitle = document.getElementById('pageTitle');
    const pageSubtitle = document.getElementById('pageSubtitle');
    const summaryCounters = document.querySelector('.summary-counters');
    const bulkActions = document.querySelector('[id*="bulk"], .bulk-actions');
    const issuesPanel = document.querySelector('[class*="issues"], .issues-panel');

    if (pageTitle) pageTitle.textContent = 'Payslip Management';
    if (pageSubtitle) pageSubtitle.textContent = 'Manage payslips for all employees';

    // Show management features
    if (summaryCounters) summaryCounters.style.display = '';
    if (bulkActions) bulkActions.style.display = '';
    if (issuesPanel) issuesPanel.style.display = '';
}

/**
 * Handle scope selection change
 */
function handleScopeChange() {
    const scopeSelection = document.getElementById('scopeSelection');
    const deptSelection = document.getElementById('deptSelection');
    const empSelection = document.getElementById('empSelection');

    if (this.value === 'ALL') {
        if (scopeSelection) scopeSelection.style.display = 'none';
    } else {
        if (scopeSelection) scopeSelection.style.display = 'block';

        if (this.value === 'DEPARTMENT') {
            if (deptSelection) deptSelection.style.display = 'block';
            if (empSelection) empSelection.style.display = 'none';
        } else if (this.value === 'EMPLOYEE') {
            if (deptSelection) deptSelection.style.display = 'none';
            if (empSelection) empSelection.style.display = 'block';
        }
    }
}

/**
 * Handle view mode change between Manage All and My Payslips
 */
function handleViewModeChange(event) {
    const viewMode = event.target.value;

    if (viewMode === 'personal') {
        // Add personal view parameter and reload
        const currentUrl = new URL(window.location);
        currentUrl.searchParams.set('viewMode', 'personal');
        window.location.href = currentUrl.toString();
    } else {
        // Remove personal view parameter and reload
        const currentUrl = new URL(window.location);
        currentUrl.searchParams.delete('viewMode');
        window.location.href = currentUrl.toString();
    }
}

/**
 * Switch to personal view mode
 */
function switchToPersonalView() {
    const pageTitle = document.getElementById('pageTitle');
    const pageSubtitle = document.getElementById('pageSubtitle');
    const summaryCounters = document.querySelector('.summary-counters');
    const bulkActions = document.querySelector('.bulk-actions, [class*="bulk"]');
    const issuesPanel = document.querySelector('.issues-panel, [class*="issues"]');

    if (pageTitle) pageTitle.textContent = 'My Payslips';
    if (pageSubtitle) pageSubtitle.textContent = 'View your personal payslip records';

    // Hide management features
    if (summaryCounters) summaryCounters.style.display = 'none';
    if (bulkActions) bulkActions.style.display = 'none';
    if (issuesPanel) issuesPanel.style.display = 'none';
}

/**
 * Switch to manage view mode
 */
function switchToManageView() {
    const pageTitle = document.getElementById('pageTitle');
    const pageSubtitle = document.getElementById('pageSubtitle');
    const summaryCounters = document.querySelector('.summary-counters');
    const bulkActions = document.querySelector('.bulk-actions, [class*="bulk"]');
    const issuesPanel = document.querySelector('.issues-panel, [class*="issues"]');

    if (pageTitle) pageTitle.textContent = 'Payslip Management';
    if (pageSubtitle) pageSubtitle.textContent = 'Manage payslips for all employees';

    // Show management features
    if (summaryCounters) summaryCounters.style.display = '';
    if (bulkActions) bulkActions.style.display = '';
    if (issuesPanel) issuesPanel.style.display = '';
}

// Global functions for onclick handlers (to maintain compatibility with JSP)
function showGenerateModal() {
    const modal = new bootstrap.Modal(document.getElementById('generateModal'));

    // Set default to PREVIOUS month/year (business rule: generate for previous month)
    const currentDate = new Date();
    let defaultMonth = currentDate.getMonth(); // Current month (0-based)
    let defaultYear = currentDate.getFullYear();

    // Get previous month
    if (defaultMonth === 0) {
        // If current month is January (0), previous month is December (12) of previous year
        defaultMonth = 12; // December (1-based for form)
        defaultYear = defaultYear - 1;
    } else {
        // Otherwise, subtract 1 from current month (0-based) to get previous month (1-based)
        defaultMonth = defaultMonth; // e.g., November (10) -> October (10 in 1-based)
    }

    const monthSelect = document.getElementById('payrollMonth');
    const yearSelect = document.getElementById('payrollYear');

    if (monthSelect) monthSelect.value = defaultMonth;
    if (yearSelect) yearSelect.value = defaultYear;

    // Use setTimeout to ensure values are set before calculating
    setTimeout(() => {
        // Call the function from bulk-actions.jsp (defined as window.updateCalculatedPeriod)
        if (typeof window.updateCalculatedPeriod === 'function') {
            window.updateCalculatedPeriod();
        } else if (typeof updateCalculatedPeriod === 'function') {
            updateCalculatedPeriod();
        }
    }, 100);

    modal.show();
}

function showRegenerateModal() {
    showGenerateModal();
    // Pre-check force regeneration
    const forceCheckbox = document.getElementById('forceGen');
    if (forceCheckbox) forceCheckbox.checked = true;
}

function showBulkRegenerateModal() {
    showGenerateModal();
    // Pre-check force regeneration
    const forceCheckbox = document.getElementById('forceGen');
    if (forceCheckbox) forceCheckbox.checked = true;
}

function exportPayslips(format) {
    handleExport(format);
}

function quickGenerate(type) {
    const periodStart = document.getElementById('periodStart').value;
    const periodEnd = document.getElementById('periodEnd').value;

    if (!periodStart || !periodEnd) {
        alert('Please select a period first.');
        return;
    }

    alert(`Quick ${type} generation would start here. Feature coming soon!`);
}

function refreshCounters() {
    location.reload();
}

/**
 * Show custom confirm modal
 */
function showConfirmModal(options) {
    const {
        title = 'Confirm Action',
        message = 'Are you sure?',
        confirmText = 'Confirm',
        cancelText = 'Cancel',
        confirmClass = 'btn-primary',
        icon = 'fa-question-circle',
        iconColor = 'text-primary',
        onConfirm = () => {},
        onCancel = () => {}
    } = options;

    // Create modal HTML
    const modalId = 'customConfirmModal';
    let modal = document.getElementById(modalId);

    if (!modal) {
        modal = document.createElement('div');
        modal.id = modalId;
        modal.className = 'modal fade';
        modal.setAttribute('tabindex', '-1');
        modal.setAttribute('aria-hidden', 'true');
        document.body.appendChild(modal);
    }

    modal.innerHTML = `
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header border-0 pb-0 justify-content-end">
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body text-center pt-0 px-4" style="text-align: center;">
                    <div class="mb-3" style="text-align: center;">
                        <i class="fas ${icon} ${iconColor}" style="font-size: 3rem;"></i>
                    </div>
                    <h5 class="modal-title mb-3" style="text-align: center; width: 100%; display: block;">${title}</h5>
                    <p class="text-muted mb-0" style="text-align: center; width: 100%; display: block;">${message}</p>
                </div>
                <div class="modal-footer border-0 justify-content-center pt-3 pb-4">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times me-1"></i>${cancelText}
                    </button>
                    <button type="button" class="btn ${confirmClass}" id="confirmActionBtn">
                        <i class="fas fa-check me-1"></i>${confirmText}
                    </button>
                </div>
            </div>
        </div>
    `;

    const bsModal = new bootstrap.Modal(modal);

    // Handle confirm button
    const confirmBtn = modal.querySelector('#confirmActionBtn');
    confirmBtn.onclick = () => {
        bsModal.hide();
        onConfirm();
    };

    // Handle cancel
    modal.addEventListener('hidden.bs.modal', () => {
        if (!confirmBtn.dataset.confirmed) {
            onCancel();
        }
    }, { once: true });

    confirmBtn.addEventListener('click', () => {
        confirmBtn.dataset.confirmed = 'true';
    }, { once: true });

    bsModal.show();
}

/**
 * Regenerate a single payslip
 */
function regeneratePayslip(payslipId, employeeName = '') {
    showConfirmModal({
        title: 'Regenerate Payslip',
        message: employeeName
            ? `Are you sure you want to regenerate the payslip for <strong>${employeeName}</strong>?<br><small class="text-muted">This will recalculate all amounts based on current data.</small>`
            : 'Are you sure you want to regenerate this payslip?<br><small class="text-muted">This will recalculate all amounts based on current data.</small>',
        confirmText: 'Regenerate',
        cancelText: 'Cancel',
        confirmClass: 'btn-warning',
        icon: 'fa-sync-alt',
        iconColor: 'text-warning',
        onConfirm: () => {
            performRegenerate(payslipId);
        }
    });
}

/**
 * Perform the actual regeneration
 */
function performRegenerate(payslipId) {
    // Find the button that triggered this (if available)
    const btn = document.querySelector(`button[onclick*="regeneratePayslip(${payslipId}"]`);
    let originalText = '';

    if (btn) {
        originalText = btn.innerHTML;
        btn.disabled = true;
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
    }

    // Create URLSearchParams instead of FormData to avoid multipart/form-data
    const params = new URLSearchParams();
    params.append('action', 'regenerate');
    params.append('payslipId', payslipId);
    params.append('force', 'true');

    // Make AJAX request
    fetch('/HRMS/payslips', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params.toString()
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            showSuccess('Payslip regenerated successfully!');
            setTimeout(() => {
                location.reload();
            }, 1500);
        } else {
            showError(data.error || 'Regeneration failed');
            if (btn) {
                btn.disabled = false;
                btn.innerHTML = originalText;
            }
        }
    })
    .catch(error => {
        console.error('Regeneration error:', error);
        showError('Network error occurred: ' + error.message);
        if (btn) {
            btn.disabled = false;
            btn.innerHTML = originalText;
        }
    });
}

// NOTE: updateCalculatedPeriod() is now defined inline in bulk-actions.jsp
// to avoid conflicts and ensure proper scope

function clearFilters() {
    const filterMonth = document.getElementById('filterMonth');
    const filterYear = document.getElementById('filterYear');
    const departmentId = document.getElementById('departmentId');
    const status = document.getElementById('status');
    const periodStart = document.getElementById('periodStart');
    const periodEnd = document.getElementById('periodEnd');

    if (filterMonth) filterMonth.value = '';
    if (filterYear) filterYear.value = '';
    if (departmentId) departmentId.value = '';
    if (status) status.value = '';
    if (periodStart) periodStart.value = '';
    if (periodEnd) periodEnd.value = '';
}

// NOTE: showToast() function is defined in payslip-list.jsp to avoid conflicts
// NOTE: DOMContentLoaded handlers are also in payslip-list.jsp
// This file only contains action handlers and utility functions