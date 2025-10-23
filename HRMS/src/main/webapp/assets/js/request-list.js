/**
 * Request List Page JavaScript
 * Handles delete operations, export functionality, and filter interactions
 */

/**
 * Open approval modal
 * @param {number} requestId - The ID of the request
 * @param {string} requestTitle - The title of the request
 */
function openApprovalModal(requestId, requestTitle) {
    // Set modal data
    document.getElementById('modalRequestId').value = requestId;
    document.getElementById('modalRequestTitle').textContent = requestTitle;
    document.getElementById('approvalReason').value = '';
    document.getElementById('decisionAccept').checked = true;

    // Reset validation
    const approvalReasonEl = document.getElementById('approvalReason');
    if (approvalReasonEl) {
        approvalReasonEl.classList.remove('is-invalid');
    }

    // The modal uses id="reasonError" for the validation message. Use defensive checks.
    const reasonErrorEl = document.getElementById('reasonError');
    if (reasonErrorEl) {
        reasonErrorEl.style.display = 'none';
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

    // Show modal
    const modal = new bootstrap.Modal(document.getElementById('approvalModal'));
    modal.show();
}

/**
 * Submit approval decision
 */
function submitApproval() {
    const requestId = document.getElementById('modalRequestId').value;
    const decision = document.querySelector('input[name="decision"]:checked').value;
    const reason = document.getElementById('approvalReason').value.trim();
    const reasonField = document.getElementById('approvalReason');

    // Validate: reason is required for both accept and reject
    const reasonError = document.getElementById('reasonError');
    if (!reason) {
        reasonField.classList.add('is-invalid');
        if (reasonError) {
            reasonError.style.display = 'block';
        }
        return;
    }

    reasonField.classList.remove('is-invalid');
    if (reasonError) {
        reasonError.style.display = 'none';
    }

    // Disable submit button
    const submitBtn = event.target;
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Processing...';

    const contextPath = getContextPath();
    const formData = new URLSearchParams();
    // Map 'accept' to 'approve' for backend
    const action = decision === 'accept' ? 'approve' : 'reject';
    formData.append('action', action);
    formData.append('requestId', requestId);
    if (reason) {
        formData.append('reason', reason);
    }

    fetch(contextPath + '/requests/approve', {
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
            const modal = bootstrap.Modal.getInstance(document.getElementById('approvalModal'));
            modal.hide();

            // Show success message
            const action = decision === 'accept' ? 'approved' : 'rejected';
            showMessage(`Request ${action} successfully`, 'success');

            // Reload page after short delay
            setTimeout(() => location.reload(), 1000);
        } else {
            showMessage('Error: ' + (data.message || 'Failed to process request'), 'error');
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="fas fa-paper-plane me-1"></i>Submit';
        }
    })
    .catch(error => {
        console.error('Error processing approval:', error);
        showMessage('An error occurred. Please try again.', 'error');
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-paper-plane me-1"></i>Submit';
    });
}
s
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
 * Show a toast notification using Bootstrap Toast API
 * @param {string} message - The message to display
 * @param {string} type - The message type: 'success', 'error', 'info', 'warning'
 */
function showMessage(message, type) {
    // Use the showToast function from the page if available
    if (typeof showToast === 'function') {
        showToast(message, type);
        return;
    }

    // Fallback: call showToast directly with proper parameters
    const toastElement = document.getElementById('responseToast');
    if (!toastElement) {
        console.error('Toast element not found');
        return;
    }

    const toastHeader = document.getElementById('toastHeader');
    const toastIcon = document.getElementById('toastIcon');
    const toastTitle = document.getElementById('toastTitle');
    const toastBody = document.getElementById('toastBody');

    // Reset classes
    toastHeader.className = 'toast-header';
    toastIcon.className = 'fas fa-circle me-2';

    // Set type-specific styling
    switch(type) {
        case 'success':
            toastHeader.classList.add('bg-success');
            toastIcon.classList.add('fa-check-circle');
            toastTitle.textContent = 'Success';
            break;
        case 'error':
        case 'danger':
            toastHeader.classList.add('bg-danger');
            toastIcon.classList.add('fa-exclamation-circle');
            toastTitle.textContent = 'Error';
            break;
        case 'warning':
            toastHeader.classList.add('bg-warning');
            toastIcon.classList.add('fa-exclamation-triangle');
            toastTitle.textContent = 'Warning';
            break;
        case 'info':
        default:
            toastHeader.classList.add('bg-info');
            toastIcon.classList.add('fa-info-circle');
            toastTitle.textContent = 'Information';
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

/**
 * Initialize page functionality when DOM is ready
 */
document.addEventListener('DOMContentLoaded', function() {
    // Handle decision change in approval modal - clear validation errors
    const decisionInputs = document.querySelectorAll('input[name="decision"]');
    decisionInputs.forEach(input => {
        input.addEventListener('change', function() {
            const reasonErrorEl = document.getElementById('reasonError');
            const reasonField = document.getElementById('approvalReason');

            // Clear validation errors when decision changes
            if (reasonErrorEl) reasonErrorEl.style.display = 'none';
            if (reasonField) {
                reasonField.classList.remove('is-invalid');
            }
        });
    });
    // Handle filter collapse toggle
    const filterHeader = document.querySelector('.filter-section .card-header');
    const filterToggle = document.querySelector('.filter-toggle i');
    const filterCollapse = document.getElementById('filterCollapse');

    if (filterHeader && filterToggle && filterCollapse) {
        filterCollapse.addEventListener('show.bs.collapse', function() {
            filterToggle.classList.remove('fa-chevron-down');
            filterToggle.classList.add('fa-chevron-up');
        });

        filterCollapse.addEventListener('hide.bs.collapse', function() {
            filterToggle.classList.remove('fa-chevron-up');
            filterToggle.classList.add('fa-chevron-down');
        });
    }
    // Form validation
    const filterForm = document.getElementById('filterForm');

    if (filterForm) {
        // Add form submit validation
        filterForm.addEventListener('submit', function(e) {
            const fromDate = document.getElementById('fromDate').value;
            const toDate = document.getElementById('toDate').value;

            // Validate date range
            if (fromDate && toDate) {
                const from = new Date(fromDate);
                const to = new Date(toDate);

                if (from > to) {
                    e.preventDefault();
                    showMessage('From Date must be less than or equal to To Date', 'error');
                    return false;
                }
            }

            return true;
        });

        // For search input, allow submit on Enter key
        const searchInput = filterForm.querySelector('input[name="search"]');
        if (searchInput) {
            searchInput.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    filterForm.submit();
                }
            });
        }
    }

    // Add confirmation for clear filters button
    const clearButton = document.querySelector('a[href*="/requests"]:not([href*="?"])');
    if (clearButton && clearButton.textContent.includes('Clear')) {
        clearButton.addEventListener('click', function(e) {
            // Only confirm if there are active filters
            const currentUrl = new URL(window.location.href);
            if (currentUrl.search && currentUrl.search.length > 1) {
                if (!confirm('Clear all filters?')) {
                    e.preventDefault();
                }
            }
        });
    }

    // Enhance table row clicks for better UX
    const tableRows = document.querySelectorAll('.table tbody tr');
    tableRows.forEach(row => {
        row.style.cursor = 'pointer';

        row.addEventListener('click', function(e) {
            // Don't trigger if clicking on a button or link
            if (e.target.closest('button') || e.target.closest('a')) {
                return;
            }

            // Find the view detail link and navigate to it
            const viewLink = this.querySelector('a[title="View Detail"]');
            if (viewLink) {
                window.location.href = viewLink.href;
            }
        });
    });
});
