/**
 * Approval Modal - Shared functionality for request approval/rejection
 * Used by both request-list.jsp and request-detail.jsp
 */

/**
 * Get the application context path
 * @returns {string} The context path
 */
function getContextPath() {
    // Try to get from a data attribute or meta tag first
    const contextPathMeta = document.querySelector('meta[name="context-path"]');
    if (contextPathMeta) {
        return contextPathMeta.getAttribute('content');
    }

    // Fallback: extract from current path
    const path = window.location.pathname;
    const pathParts = path.split('/');

    // If path starts with /HRMS or similar context
    if (pathParts.length > 1 && pathParts[1]) {
        return '/' + pathParts[1];
    }

    return '';
}

/**
 * Open the approval modal with request information
 * @param {number} requestId - The ID of the request
 * @param {string} requestTitle - The title of the request
 * @param {string} requestStatus - The current status of the request (optional)
 */
function openApprovalModal(requestId, requestTitle, requestStatus) {
    // Set modal data
    const modalRequestIdEl = document.getElementById('modalRequestId');
    if (modalRequestIdEl) modalRequestIdEl.value = requestId;

    const modalRequestTitleEl = document.getElementById('modalRequestTitle');
    if (modalRequestTitleEl) modalRequestTitleEl.textContent = requestTitle;

    // Get employee name from the page
    // Try multiple selectors to find employee name (works for both list and detail pages)
    let employeeName = 'N/A';
    const employeeNameSelectors = [
        '.fw-semibold', // From detail page
        '.employee-name', // From list page (if exists)
        '[data-employee-name]' // Fallback
    ];

    for (const selector of employeeNameSelectors) {
        const element = document.querySelector(selector);
        if (element && element.textContent.trim()) {
            employeeName = element.textContent.trim();
            break;
        }
    }

    const modalEmployeeNameEl = document.getElementById('modalEmployeeName');
    if (modalEmployeeNameEl) modalEmployeeNameEl.textContent = employeeName;

    // Reset form fields
    const approvalReasonEl = document.getElementById('approvalReason');
    if (approvalReasonEl) {
        approvalReasonEl.value = '';
        approvalReasonEl.classList.remove('is-invalid');
    }

    // Reset validation error message
    const reasonErrorEl = document.getElementById('reasonError');
    if (reasonErrorEl) {
        reasonErrorEl.style.display = 'none';
    }

    // Get decision radio buttons and labels
    const acceptBtn = document.getElementById('decisionAccept');
    const acceptLabel = document.querySelector('label[for="decisionAccept"]');
    const rejectBtn = document.getElementById('decisionReject');
    const modalLabel = document.getElementById('approvalModalLabel');

    // Handle APPROVED status (manager override scenario)
    if (requestStatus === 'APPROVED') {
        // For APPROVED requests, only allow rejection (manager override)
        if (acceptBtn) acceptBtn.style.display = 'none';
        if (acceptLabel) acceptLabel.style.display = 'none';
        if (rejectBtn) {
            rejectBtn.checked = true;
            rejectBtn.style.display = 'block';
        }
        // Update modal title
        if (modalLabel) {
            modalLabel.innerHTML = '<i class="fas fa-exclamation-triangle me-2"></i>Override Request';
        }
    } else if (requestStatus === 'REJECTED') {
        // For REJECTED requests, only allow approval (HR override)
        if (acceptBtn) {
            acceptBtn.checked = true;
            acceptBtn.style.display = 'block';
        }
        if (acceptLabel) acceptLabel.style.display = 'block';
        if (rejectBtn) rejectBtn.style.display = 'none';
        const rejectLabel = document.querySelector('label[for="decisionReject"]');
        if (rejectLabel) rejectLabel.style.display = 'none';
        // Update modal title
        if (modalLabel) {
            modalLabel.innerHTML = '<i class="fas fa-redo me-2"></i>Override Rejection';
        }
    } else {
        // For PENDING requests, show both options
        if (acceptBtn) {
            acceptBtn.checked = true;
            acceptBtn.style.display = 'block';
        }
        if (acceptLabel) acceptLabel.style.display = 'block';
        if (rejectBtn) rejectBtn.style.display = 'block';
        const rejectLabel = document.querySelector('label[for="decisionReject"]');
        if (rejectLabel) rejectLabel.style.display = 'block';
        // Reset modal title
        if (modalLabel) {
            modalLabel.innerHTML = '<i class="fas fa-clipboard-check me-2"></i>Approve Request';
        }
    }

    // Add event listeners to clear error when decision changes
    const decisionRadios = document.querySelectorAll('input[name="decision"]');
    decisionRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            if (approvalReasonEl) {
                approvalReasonEl.classList.remove('is-invalid');
            }
            if (reasonErrorEl) {
                reasonErrorEl.style.display = 'none';
            }
        });
    });

    // Show the modal
    const modalElement = document.getElementById('approvalModal');
    if (modalElement) {
        const modal = new bootstrap.Modal(modalElement);
        modal.show();
    }
}

/**
 * Submit the approval decision
 */
function submitApproval() {
    const requestId = document.getElementById('modalRequestId').value;
    const decision = document.querySelector('input[name="decision"]:checked').value;
    const reason = document.getElementById('approvalReason').value.trim();

    // Validate: Rejection requires a reason
    if (decision === 'reject' && !reason) {
        const approvalReasonEl = document.getElementById('approvalReason');
        if (approvalReasonEl) {
            approvalReasonEl.classList.add('is-invalid');
        }
        const reasonErrorEl = document.getElementById('reasonError');
        if (reasonErrorEl) {
            reasonErrorEl.textContent = 'Rejection reason is required';
            reasonErrorEl.style.display = 'block';
        }
        return;
    }

    // Disable submit button to prevent double submission
    const submitBtn = document.getElementById('submitApprovalBtn');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Processing...';
    }

    // Prepare request data
    const action = decision === 'accept' ? 'approve' : 'reject';
    const formData = new URLSearchParams();
    formData.append('action', action);
    formData.append('requestId', requestId);
    if (reason) {
        formData.append('reason', reason);
    }

    // Get context path
    const contextPath = getContextPath();

    // Submit via AJAX
    fetch(`${contextPath}/requests/approve`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Close modal
            const modalElement = document.getElementById('approvalModal');
            if (modalElement) {
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
            }

            // Show success message
            showNotification('success', data.message || 'Request processed successfully');

            // Reload page after short delay
            setTimeout(() => {
                window.location.reload();
            }, 1500);
        } else {
            // Show error message
            showNotification('error', data.message || 'Failed to process request');

            // Re-enable submit button
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerHTML = '<i class="fas fa-check me-1"></i>Submit';
            }
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('error', 'An error occurred while processing the request');

        // Re-enable submit button
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="fas fa-check me-1"></i>Submit';
        }
    });
}

/**
 * Show notification message
 * @param {string} type - Type of notification ('success' or 'error')
 * @param {string} message - Message to display
 */
function showNotification(type, message) {
    // Try to use existing notification system if available
    if (typeof showToast === 'function') {
        showToast(type, message);
        return;
    }

    // Fallback to alert
    if (type === 'success') {
        alert('✓ ' + message);
    } else {
        alert('✗ ' + message);
    }
}

