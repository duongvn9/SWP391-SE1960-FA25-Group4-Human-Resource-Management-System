/**
 * Detail Payslip Page JavaScript - Clean Version
 * Handles responsive behavior and simple print functionality
 */

// Simple print function that doesn't modify screen
window.printPayslip = function() {
    console.log('Simple print function called');
    
    const printBtn = document.querySelector('.print-btn');
    
    // Show loading state
    if (printBtn) {
        printBtn.classList.add('loading');
        printBtn.disabled = true;
    }

    try {
        // Just print without any modifications
        setTimeout(() => {
            console.log('Triggering print dialog...');
            window.print();
            
            // Clean up loading state
            if (printBtn) {
                printBtn.classList.remove('loading');
                printBtn.disabled = false;
            }
            console.log('Print completed');
        }, 300);
        
    } catch (error) {
        console.error('Print error:', error);
        alert('Có lỗi xảy ra khi in payslip. Vui lòng thử lại.');
        
        if (printBtn) {
            printBtn.classList.remove('loading');
            printBtn.disabled = false;
        }
    }
};

// Debug log to confirm function is loaded
console.log('PrintPayslip function loaded and available globally:', typeof window.printPayslip);

document.addEventListener('DOMContentLoaded', function () {
    // Initialize page
    initializePayslipDetail();
    // Handle responsive behavior
    handleResponsiveBehavior();

    // Add smooth animations
    addSmoothAnimations();
    
    // Debug: Ensure printPayslip function is available
    console.log('Enhanced PrintPayslip function loaded:', typeof window.printPayslip === 'function');
});

/**
 * Initialize payslip detail page
 */
function initializePayslipDetail() {
    // Fix net salary display
    fixNetSalaryDisplay();

    // Add loading states to buttons
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(button => {
        button.addEventListener('click', function () {
            if (this.href || this.type === 'submit') {
                this.classList.add('loading');
                setTimeout(() => {
                    this.classList.remove('loading');
                }, 2000);
            }
        });
    });

    // Add hover effects to calculation rows (removed translateX to prevent horizontal scroll)
    const calculationRows = document.querySelectorAll('.calculation-row');
    calculationRows.forEach(row => {
        row.addEventListener('mouseenter', function () {
            this.style.backgroundColor = '#f9fafb';
        });

        row.addEventListener('mouseleave', function () {
            this.style.backgroundColor = '';
        });
    });
}

/**
 * Handle responsive behavior
 */
function handleResponsiveBehavior() {
    // Handle window resize
    window.addEventListener('resize', function () {
        adjustLayoutForScreenSize();
    });

    // Initial adjustment
    adjustLayoutForScreenSize();
}

/**
 * Adjust layout based on screen size
 */
function adjustLayoutForScreenSize() {
    const screenWidth = window.innerWidth;
    const payslipHeader = document.querySelector('.payslip-header');
    const infoCards = document.querySelectorAll('.info-card');

    if (screenWidth <= 768) {
        // Mobile adjustments
        if (payslipHeader) {
            payslipHeader.classList.add('mobile-header');
        }

        infoCards.forEach(card => {
            card.classList.add('mobile-card');
        });

        // Adjust calculation rows for mobile
        const calculationRows = document.querySelectorAll('.calculation-row .row');
        calculationRows.forEach(row => {
            const cols = row.querySelectorAll('[class*="col-"]');
            cols.forEach(col => {
                if (col.classList.contains('text-end')) {
                    col.style.textAlign = 'right';
                    col.style.fontWeight = '600';
                }
            });
        });
    } else {
        // Desktop adjustments
        if (payslipHeader) {
            payslipHeader.classList.remove('mobile-header');
        }

        infoCards.forEach(card => {
            card.classList.remove('mobile-card');
        });
    }
}

/**
 * Add smooth animations
 */
function addSmoothAnimations() {
    // Animate cards on scroll
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver(function (entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, observerOptions);

    // Observe all info cards
    const infoCards = document.querySelectorAll('.info-card');
    infoCards.forEach(card => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        card.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(card);
    });

    // Animate payslip header
    const payslipHeader = document.querySelector('.payslip-header');
    if (payslipHeader) {
        payslipHeader.style.opacity = '0';
        payslipHeader.style.transform = 'translateY(-20px)';
        payslipHeader.style.transition = 'opacity 0.4s ease, transform 0.4s ease';

        setTimeout(() => {
            payslipHeader.style.opacity = '1';
            payslipHeader.style.transform = 'translateY(0)';
        }, 100);
    }
}

/**
 * Fix net salary display to ensure correct calculation
 */
function fixNetSalaryDisplay() {
    // Get gross income and total deductions from the page
    let grossAmount = 0;
    let totalDeductions = 0;

    // Find gross income
    const grossElements = document.querySelectorAll('*');
    for (let element of grossElements) {
        if (element.textContent && element.textContent.includes('Gross Income')) {
            const parent = element.closest('.calculation-row, .row');
            if (parent) {
                const amountElement = parent.querySelector('.text-end');
                if (amountElement && amountElement.textContent.includes('VND')) {
                    const amountText = amountElement.textContent.replace(/[^\d.,]/g, '').replace(/,/g, '');
                    grossAmount = parseFloat(amountText) || 0;
                    break;
                }
            }
        }
    }

    // Find total deductions
    for (let element of grossElements) {
        if (element.textContent && element.textContent.includes('Total Deductions')) {
            const parent = element.closest('.calculation-row, .row');
            if (parent) {
                const amountElement = parent.querySelector('.text-end');
                if (amountElement && amountElement.textContent.includes('VND')) {
                    const amountText = amountElement.textContent.replace(/[^\d.,]/g, '').replace(/,/g, '');
                    totalDeductions = parseFloat(amountText) || 0;
                    break;
                }
            }
        }
    }

    // Calculate and update net salary if we have valid amounts
    if (grossAmount > 0) {
        const netAmount = grossAmount - totalDeductions;
        const netSalaryElement = document.getElementById('net-salary-amount');
        if (netSalaryElement && netAmount > 0) {
            netSalaryElement.innerHTML = formatCurrency(netAmount);
        }
    }
}

/**
 * Format currency values
 */
function formatCurrency(value) {
    return new Intl.NumberFormat('vi-VN', {
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
    }).format(value) + ' VND';
}

/**
 * Show loading state
 */
function showLoading(element) {
    element.classList.add('loading');
    element.disabled = true;
}

/**
 * Hide loading state
 */
function hideLoading(element) {
    element.classList.remove('loading');
    element.disabled = false;
}

/**
 * Show toast notification
 */
function showToast(message, type = 'info') {
    // Create toast element
    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type} border-0`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');

    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;

    // Add to page
    let toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
        document.body.appendChild(toastContainer);
    }

    toastContainer.appendChild(toast);

    // Show toast with fallback for bootstrap
    try {
        if (typeof bootstrap !== 'undefined' && bootstrap.Toast) {
            const bsToast = new bootstrap.Toast(toast);
            bsToast.show();
        } else {
            // Fallback: just show the toast
            toast.style.display = 'block';
            setTimeout(() => {
                toast.remove();
            }, 5000);
        }
    } catch (e) {
        console.warn('Bootstrap Toast not available, using fallback');
        toast.style.display = 'block';
        setTimeout(() => {
            toast.remove();
        }, 5000);
    }

    // Remove after hide
    toast.addEventListener('hidden.bs.toast', function () {
        toast.remove();
    });
}

/**
 * Handle errors gracefully
 */
window.addEventListener('error', function (event) {
    console.error('Detail Payslip Error:', event.error);
    // Could show user-friendly error message here
});

/**
 * Export functions for external use
 */
window.PayslipDetail = {
    formatCurrency,
    showLoading,
    hideLoading,
    showToast,
    printPayslip
};

/**
 * Handle sidebar toggle for proper layout
 */
function handleSidebarLayout() {
    // Check if sidebar toggle exists
    const sidebarToggle = document.querySelector('[data-bs-toggle="sidebar"]') ||
            document.querySelector('.sidebar-toggle') ||
            document.querySelector('#sidebarToggle');

    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function () {
            // Add a small delay to allow sidebar animation to complete
            setTimeout(() => {
                adjustMainContentLayout();
            }, 300);
        });
    }

    // Initial layout adjustment
    adjustMainContentLayout();
}

/**
 * Adjust main content layout based on sidebar state
 */
function adjustMainContentLayout() {
    const mainContent = document.querySelector('.main-content');
    const sidebar = document.querySelector('.sidebar') || document.querySelector('.main-sidebar');

    if (!mainContent)
        return;

    // Check if sidebar is collapsed
    const isCollapsed = document.body.classList.contains('sidebar-collapsed') ||
            document.body.classList.contains('sidebar-mini') ||
            (sidebar && sidebar.classList.contains('collapsed'));

    if (isCollapsed) {
        mainContent.style.marginLeft = '70px';
        mainContent.style.width = 'calc(100% - 70px)';
    } else {
        mainContent.style.marginLeft = '250px';
        mainContent.style.width = 'calc(100% - 250px)';
    }

    // On mobile, remove margins
    if (window.innerWidth <= 768) {
        mainContent.style.marginLeft = '0';
        mainContent.style.width = '100%';
    }
}

// Initialize sidebar layout handling
document.addEventListener('DOMContentLoaded', function () {
    handleSidebarLayout();

    // Handle window resize
    window.addEventListener('resize', function () {
        adjustMainContentLayout();
    });
});