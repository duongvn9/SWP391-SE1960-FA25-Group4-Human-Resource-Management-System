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
    document.getElementById('approvalReason').classList.remove('is-invalid');
    document.getElementById('reasonRequired').style.display = 'none';

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

    // Validate: reason is required for rejection
    if (decision === 'reject' && !reason) {
        reasonField.classList.add('is-invalid');
        return;
    }

    reasonField.classList.remove('is-invalid');

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

/**
 * Delete request with confirmation dialog
 * Performs soft delete by changing status to CANCELLED via AJAX
 * @param {number} requestId - The ID of the request to delete
 */
function deleteRequest(requestId) {
    if (!confirm('Are you sure you want to delete this request? This will change its status to CANCELLED.')) {
        return;
    }

    // Show loading state
    const deleteButton = event.target.closest('button');
    if (deleteButton) {
        deleteButton.disabled = true;
        deleteButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
    }

    // Get context path from the page
    const contextPath = getContextPath();

    // Use form data instead of JSON for servlet compatibility
    const formData = new URLSearchParams();
    formData.append('action', 'delete');
    formData.append('requestId', requestId);

    fetch(contextPath + '/requests', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.status);
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            showMessage('Request deleted successfully', 'success');
            // Reload page after short delay to show message
            setTimeout(() => {
                location.reload();
            }, 1000);
        } else {
            showMessage('Error: ' + (data.message || 'Failed to delete request'), 'error');
            // Re-enable button on error
            if (deleteButton) {
                deleteButton.disabled = false;
                deleteButton.innerHTML = '<i class="fas fa-trash"></i>';
            }
        }
    })
    .catch(error => {
        console.error('Error deleting request:', error);
        showMessage('An error occurred while deleting the request. Please try again.', 'error');
        // Re-enable button on error
        if (deleteButton) {
            deleteButton.disabled = false;
            deleteButton.innerHTML = '<i class="fas fa-trash"></i>';
        }
    });
}

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
 * Show a message to the user
 * @param {string} message - The message to display
 * @param {string} type - The message type: 'success', 'error', 'info', 'warning'
 */
function showMessage(message, type) {
    // Check if there's an existing message container
    let messageContainer = document.getElementById('message-container');

    if (!messageContainer) {
        // Create message container if it doesn't exist
        messageContainer = document.createElement('div');
        messageContainer.id = 'message-container';
        messageContainer.style.cssText = 'position: fixed; top: 20px; right: 20px; z-index: 9999; max-width: 400px;';
        document.body.appendChild(messageContainer);
    }

    // Create message element
    const messageDiv = document.createElement('div');
    messageDiv.className = 'alert alert-' + getBootstrapAlertClass(type) + ' alert-dismissible fade show';
    messageDiv.setAttribute('role', 'alert');
    messageDiv.innerHTML = `
        ${message}
        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
    `;

    messageContainer.appendChild(messageDiv);

    // Auto-dismiss after 5 seconds
    setTimeout(() => {
        messageDiv.classList.remove('show');
        setTimeout(() => {
            messageDiv.remove();
        }, 150);
    }, 5000);
}

/**
 * Convert message type to Bootstrap alert class
 * @param {string} type - Message type
 * @returns {string} Bootstrap alert class
 */
function getBootstrapAlertClass(type) {
    const typeMap = {
        'success': 'success',
        'error': 'danger',
        'info': 'info',
        'warning': 'warning'
    };
    return typeMap[type] || 'info';
}

/**
 * Initialize page functionality when DOM is ready
 */
document.addEventListener('DOMContentLoaded', function() {
    // Handle decision change in approval modal
    const decisionInputs = document.querySelectorAll('input[name="decision"]');
    decisionInputs.forEach(input => {
        input.addEventListener('change', function() {
            const reasonRequired = document.getElementById('reasonRequired');
            const reasonField = document.getElementById('approvalReason');

            if (this.value === 'reject') {
                reasonRequired.style.display = 'inline';
                reasonField.placeholder = 'Enter rejection reason (required)';
            } else {
                reasonRequired.style.display = 'none';
                reasonField.placeholder = 'Enter approval reason (optional)';
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
