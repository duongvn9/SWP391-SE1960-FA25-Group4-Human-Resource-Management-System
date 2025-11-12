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
 * @param {string} employeeName - The name of the employee who created the request (optional)
 */
function openApprovalModal(requestId, requestTitle, requestStatus, employeeName) {
    // Set modal data
    const modalRequestIdEl = document.getElementById('modalRequestId');
    if (modalRequestIdEl) modalRequestIdEl.value = requestId;

    const modalRequestTitleEl = document.getElementById('modalRequestTitle');
    if (modalRequestTitleEl) modalRequestTitleEl.textContent = requestTitle;

    // Set employee name (use provided parameter or fallback to finding it on the page)
    if (!employeeName || employeeName.trim() === '') {
        // Fallback: Try to find employee name from the page
        // Look for specific employee information section (more precise than before)
        const employeeInfoSelectors = [
            '.info-section .info-value.fw-semibold', // From detail page employee section
            'td .fw-semibold', // From list page table
            '[data-employee-name]' // Explicit data attribute
        ];

        employeeName = 'N/A';
        for (const selector of employeeInfoSelectors) {
            const element = document.querySelector(selector);
            if (element && element.textContent.trim()) {
                employeeName = element.textContent.trim();
                break;
            }
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

    // Get user role info from page
    const isHRM = document.body.getAttribute('data-is-hrm') === 'true';
    const isHR = document.body.getAttribute('data-is-hr') === 'true';

    // Handle APPROVED status (HRM override scenario for appeal requests)
    if (requestStatus === 'APPROVED') {
        // For APPROVED requests, only HRM can reject (override)
        if (acceptBtn) acceptBtn.style.display = 'none';
        if (acceptLabel) acceptLabel.style.display = 'none';
        if (rejectBtn) {
            rejectBtn.checked = true;
            rejectBtn.style.display = 'block';
        }
        // Update modal title
        if (modalLabel) {
            if (isHRM) {
                modalLabel.innerHTML = '<i class="fas fa-exclamation-triangle me-2"></i>HRM Override - Reject Approved Request';
            } else {
                modalLabel.innerHTML = '<i class="fas fa-exclamation-triangle me-2"></i>Override Request';
            }
        }
    } else if (requestStatus === 'REJECTED') {
        // For REJECTED requests, only HRM can approve (override)
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
            if (isHRM) {
                modalLabel.innerHTML = '<i class="fas fa-redo me-2"></i>HRM Override - Approve Rejected Request';
            } else {
                modalLabel.innerHTML = '<i class="fas fa-redo me-2"></i>Override Rejection';
            }
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

    // Add event listeners to clear error when decision changes or user types
    const decisionRadios = document.querySelectorAll('input[name="decision"]');
    decisionRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            clearValidationErrors();
        });
    });

    // Clear errors when user starts typing
    if (approvalReasonEl) {
        approvalReasonEl.addEventListener('input', function() {
            if (this.value.trim()) {
                clearValidationErrors();
            }
        });
    }

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

    // Clear previous errors
    const approvalReasonEl = document.getElementById('approvalReason');
    const reasonErrorEl = document.getElementById('reasonError');

    if (approvalReasonEl) {
        approvalReasonEl.classList.remove('is-invalid');
    }
    if (reasonErrorEl) {
        reasonErrorEl.style.display = 'none';
    }

    // Validate: Both approval and rejection require a reason
    if (!reason) {
        if (approvalReasonEl) {
            approvalReasonEl.classList.add('is-invalid');
        }
        if (reasonErrorEl) {
            const actionText = decision === 'accept' ? 'Approval' : 'Rejection';
            reasonErrorEl.textContent = actionText + ' reason is required';
            reasonErrorEl.style.display = 'block';
            reasonErrorEl.className = 'invalid-feedback d-block'; // Ensure it shows
        }

        // Focus on the reason field
        if (approvalReasonEl) {
            approvalReasonEl.focus();
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

            // Show success message with green color
            showNotification('success', data.message || 'Request processed successfully');

            // Reload page after short delay
            setTimeout(() => {
                window.location.reload();
            }, 2000);
        } else {
            // Show error message with red color
            showNotification('error', data.message || 'Failed to process request');

            // Re-enable submit button
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerHTML = '<i class="fas fa-paper-plane me-1"></i>Submit';
            }
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('error', 'Network error occurred while processing the request. Please try again.');

        // Re-enable submit button
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="fas fa-paper-plane me-1"></i>Submit';
        }
    });
}

/**
 * Clear validation errors
 */
function clearValidationErrors() {
    const approvalReasonEl = document.getElementById('approvalReason');
    const reasonErrorEl = document.getElementById('reasonError');

    if (approvalReasonEl) {
        approvalReasonEl.classList.remove('is-invalid');
    }
    if (reasonErrorEl) {
        reasonErrorEl.style.display = 'none';
    }
}

/**
 * Show notification message with appropriate colors
 * @param {string} type - Type of notification ('success' or 'error')
 * @param {string} message - Message to display
 */
function showNotification(type, message) {
    // Try to use existing notification system if available
    if (typeof showToast === 'function') {
        // Map type to appropriate toast type
        const toastType = type === 'success' ? 'success' : 'danger';
        showToast(message, toastType);
        return;
    }

    // Fallback: Create custom toast if showToast not available
    createCustomToast(type, message);
}

/**
 * Create custom toast notification
 * @param {string} type - Type of notification ('success' or 'error')
 * @param {string} message - Message to display
 */
function createCustomToast(type, message) {
    // Create toast container if not exists
    let toastContainer = document.getElementById('toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toast-container';
        toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
        toastContainer.style.zIndex = '9999';
        document.body.appendChild(toastContainer);
    }

    // Create toast element
    const toastId = 'toast-' + Date.now();
    const bgClass = type === 'success' ? 'bg-success' : 'bg-danger';
    const icon = type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle';
    const title = type === 'success' ? 'Success' : 'Error';

    const toastHtml = `
        <div id="${toastId}" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="toast-header ${bgClass} text-white">
                <i class="fas ${icon} me-2"></i>
                <strong class="me-auto">${title}</strong>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
            <div class="toast-body">
                ${message}
            </div>
        </div>
    `;

    toastContainer.insertAdjacentHTML('beforeend', toastHtml);

    // Show toast
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, {
        autohide: true,
        delay: type === 'success' ? 3000 : 5000
    });

    toast.show();

    // Remove toast element after it's hidden
    toastElement.addEventListener('hidden.bs.toast', function() {
        toastElement.remove();
    });
}



// ==================== Event Listeners ====================

/**
 * Initialize approval modal event listeners when DOM is ready
 */
document.addEventListener('DOMContentLoaded', function() {
    // Handle approve button clicks using event delegation
    document.addEventListener('click', function(e) {
        const approveBtn = e.target.closest('.btn-approve-request, .btn-approve-request-detail');
        if (approveBtn) {
            e.preventDefault();

            // Get data from button attributes
            const requestId = approveBtn.getAttribute('data-request-id');
            const requestTitle = approveBtn.getAttribute('data-request-title');
            const requestStatus = approveBtn.getAttribute('data-request-status') || 'PENDING';
            const employeeName = approveBtn.getAttribute('data-employee-name') || '';

            // Open modal with employee name
            openApprovalModal(requestId, requestTitle, requestStatus, employeeName);
        }
    });
});
