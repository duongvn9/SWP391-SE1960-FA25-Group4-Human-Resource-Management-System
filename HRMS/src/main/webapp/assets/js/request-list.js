/**
 * Request List Page JavaScript
 * Handles delete operations, export functionality, and filter interactions
 *
 * Note: openApprovalModal() and submitApproval() functions are now in approval-modal.js
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
