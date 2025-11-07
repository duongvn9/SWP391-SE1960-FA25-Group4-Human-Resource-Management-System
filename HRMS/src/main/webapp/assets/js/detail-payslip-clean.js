/**
 * Detail Payslip Page JavaScript - Clean Version
 * Handles responsive behavior and PDF export functionality
 */

// Export payslip as PDF directly without preview
window.exportPayslipPDF = function() {
    console.log('Export PDF function called');
    
    const printBtn = document.querySelector('.print-btn');
    
    // Show loading state
    if (printBtn) {
        printBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i> Exporting...';
        printBtn.disabled = true;
    }

    try {
        // Get the printable content
        const element = document.getElementById('printable-content');
        
        if (!element) {
            throw new Error('No payslip content found');
        }

        // Get employee info for filename
        const employeeName = document.querySelector('.info-card .col-sm-8')?.textContent.trim() || 'Employee';
        const periodText = document.querySelector('.payslip-header .opacity-75')?.textContent.trim() || '';
        const period = periodText.replace('Pay Period: ', '').replace(/\//g, '-').replace(/\s+/g, '_');
        
        // Generate filename
        const filename = `Payslip_${employeeName.replace(/\s+/g, '_')}_${period}.pdf`;

        // Create a complete HTML document with inline styles
        const pdfContainer = document.createElement('div');
        pdfContainer.style.cssText = `
            width: 210mm;
            min-height: 297mm;
            padding: 20mm;
            margin: 0 auto;
            background: white;
            font-family: Arial, sans-serif;
            font-size: 12px;
            color: #000;
            line-height: 1.6;
        `;

        // Add company header
        const header = `
            <div style="text-align: center; margin-bottom: 25px; padding-bottom: 15px; border-bottom: 3px solid #333;">
                <h1 style="margin: 0 0 8px 0; color: #000; font-size: 24px; font-weight: bold;">ABC COMPANY LIMITED</h1>
                <p style="margin: 3px 0; color: #555; font-size: 11px;">Address: 123 ABC Street, XYZ District, Ho Chi Minh City</p>
                <p style="margin: 3px 0; color: #555; font-size: 11px;">Phone: (028) 1234 5678 | Email: hr@company.com</p>
                <h2 style="margin: 15px 0 0 0; color: #2563eb; font-size: 20px; font-weight: bold;">PAYSLIP</h2>
            </div>
        `;

        let contentHTML = '';

        // Extract Employee and Pay Period Information (2 columns)
        const infoCards = element.querySelectorAll('.row > .col-md-6 > .info-card');
        if (infoCards.length >= 2) {
            contentHTML += '<div style="display: flex; gap: 15px; margin-bottom: 20px;">';
            
            // Employee Information (Left)
            const empCard = infoCards[0];
            const empRows = empCard.querySelectorAll('.row');
            contentHTML += '<div style="flex: 1; border: 1px solid #ddd; padding: 12px; background: #f9f9f9;">';
            contentHTML += '<h3 style="color: #000; font-size: 13px; font-weight: bold; margin: 0 0 10px 0; border-bottom: 2px solid #2563eb; padding-bottom: 5px;">Employee Information</h3>';
            empRows.forEach(row => {
                const cols = row.querySelectorAll('[class*="col-"]');
                if (cols.length >= 2) {
                    const label = cols[0].textContent.trim().replace(':', '');
                    const value = cols[1].textContent.trim();
                    if (label && value && !label.includes('Information')) {
                        contentHTML += `<div style="margin: 6px 0; font-size: 11px;"><strong style="color: #000;">${label}:</strong> <span style="color: #333;">${value}</span></div>`;
                    }
                }
            });
            contentHTML += '</div>';
            
            // Pay Period Information (Right)
            const periodCard = infoCards[1];
            const periodRows = periodCard.querySelectorAll('.row');
            contentHTML += '<div style="flex: 1; border: 1px solid #ddd; padding: 12px; background: #f9f9f9;">';
            contentHTML += '<h3 style="color: #000; font-size: 13px; font-weight: bold; margin: 0 0 10px 0; border-bottom: 2px solid #2563eb; padding-bottom: 5px;">Pay Period Information</h3>';
            periodRows.forEach(row => {
                const cols = row.querySelectorAll('[class*="col-"]');
                if (cols.length >= 2) {
                    const label = cols[0].textContent.trim().replace(':', '');
                    const value = cols[1].textContent.trim();
                    if (label && value && !label.includes('Information')) {
                        contentHTML += `<div style="margin: 6px 0; font-size: 11px;"><strong style="color: #000;">${label}:</strong> <span style="color: #333;">${value}</span></div>`;
                    }
                }
            });
            contentHTML += '</div>';
            contentHTML += '</div>';
        }

        // Basic Salary Information (3 columns layout)
        const basicSalarySection = Array.from(element.querySelectorAll('.info-card')).find(card => 
            card.querySelector('h5')?.textContent.includes('Basic Salary')
        );
        
        if (basicSalarySection) {
            contentHTML += '<div style="border: 1px solid #ddd; padding: 12px; margin-bottom: 15px; background: #f9f9f9;">';
            contentHTML += '<h3 style="color: #000; font-size: 14px; font-weight: bold; margin: 0 0 12px 0; border-bottom: 2px solid #2563eb; padding-bottom: 5px;">Basic Salary Information</h3>';
            
            const columns = basicSalarySection.querySelectorAll('.row > .col-md-4');
            contentHTML += '<div style="display: flex; gap: 15px;">';
            
            columns.forEach(col => {
                const colTitle = col.querySelector('h6');
                const calcRows = col.querySelectorAll('.calculation-row');
                
                contentHTML += '<div style="flex: 1;">';
                if (colTitle) {
                    contentHTML += `<h4 style="color: #2563eb; font-size: 12px; font-weight: bold; margin: 0 0 8px 0;">${colTitle.textContent.trim()}</h4>`;
                }
                
                calcRows.forEach(calcRow => {
                    const rowData = calcRow.querySelectorAll('[class*="col-"]');
                    if (rowData.length >= 2) {
                        const label = rowData[0].textContent.trim();
                        const value = rowData[1].textContent.trim();
                        const isTotal = calcRow.classList.contains('total-row');
                        
                        if (isTotal) {
                            contentHTML += `<div style="margin-top: 8px; padding-top: 8px; border-top: 2px solid #333; font-size: 11px;"><strong style="color: #000;">${label}:</strong> <strong style="color: #000;">${value}</strong></div>`;
                        } else {
                            contentHTML += `<div style="margin: 5px 0; font-size: 10px;"><span style="color: #555;">${label}:</span> <span style="color: #000;">${value}</span></div>`;
                        }
                    }
                });
                contentHTML += '</div>';
            });
            contentHTML += '</div></div>';
        }

        // Earnings and Deductions (2 columns)
        const earningsSection = Array.from(element.querySelectorAll('.info-card')).find(card => 
            card.querySelector('h5')?.textContent.includes('Earnings')
        );
        const deductionsSection = Array.from(element.querySelectorAll('.info-card')).find(card => 
            card.querySelector('h5')?.textContent.includes('Deductions')
        );
        
        if (earningsSection || deductionsSection) {
            contentHTML += '<div style="display: flex; gap: 15px; margin-bottom: 15px;">';
            
            // Earnings (Left)
            if (earningsSection) {
                contentHTML += '<div style="flex: 1; border: 1px solid #ddd; padding: 12px; background: #f0fdf4;">';
                contentHTML += '<h3 style="color: #198754; font-size: 13px; font-weight: bold; margin: 0 0 10px 0; border-bottom: 2px solid #198754; padding-bottom: 5px;">Earnings</h3>';
                
                const calcRows = earningsSection.querySelectorAll('.calculation-row');
                calcRows.forEach(calcRow => {
                    const rowData = calcRow.querySelectorAll('[class*="col-"]');
                    if (rowData.length >= 2) {
                        const label = rowData[0].textContent.trim();
                        const value = rowData[1].textContent.trim();
                        const isTotal = calcRow.classList.contains('total-row');
                        const isSubItem = calcRow.classList.contains('sub-item');
                        
                        if (isTotal) {
                            contentHTML += `<div style="margin-top: 10px; padding: 8px; background: #dcfce7; border-top: 2px solid #198754; font-size: 11px;"><strong style="color: #000;">${label}</strong> <strong style="float: right; color: #198754;">${value}</strong></div>`;
                        } else if (isSubItem) {
                            contentHTML += `<div style="margin: 4px 0 4px 15px; font-size: 10px; color: #555;"><span>${label}</span> <span style="float: right;">${value}</span></div>`;
                        } else {
                            contentHTML += `<div style="margin: 6px 0; font-size: 11px;"><span style="color: #000;">${label}</span> <span style="float: right; color: #000;">${value}</span></div>`;
                        }
                    }
                });
                contentHTML += '</div>';
            }
            
            // Deductions (Right)
            if (deductionsSection) {
                contentHTML += '<div style="flex: 1; border: 1px solid #ddd; padding: 12px; background: #fef2f2;">';
                contentHTML += '<h3 style="color: #dc3545; font-size: 13px; font-weight: bold; margin: 0 0 10px 0; border-bottom: 2px solid #dc3545; padding-bottom: 5px;">Deductions</h3>';
                
                const calcRows = deductionsSection.querySelectorAll('.calculation-row');
                const hasDeductions = calcRows.length > 0;
                
                if (hasDeductions) {
                    calcRows.forEach(calcRow => {
                        const rowData = calcRow.querySelectorAll('[class*="col-"]');
                        if (rowData.length >= 2) {
                            const label = rowData[0].textContent.trim();
                            const value = rowData[1].textContent.trim();
                            const isTotal = calcRow.classList.contains('total-row');
                            const isSubItem = calcRow.classList.contains('sub-item');
                            
                            if (isTotal) {
                                contentHTML += `<div style="margin-top: 10px; padding: 8px; background: #fee2e2; border-top: 2px solid #dc3545; font-size: 11px;"><strong style="color: #000;">${label}</strong> <strong style="float: right; color: #dc3545;">${value}</strong></div>`;
                            } else if (isSubItem) {
                                contentHTML += `<div style="margin: 4px 0 4px 15px; font-size: 10px; color: #555;"><span>${label}</span> <span style="float: right;">${value}</span></div>`;
                            } else {
                                contentHTML += `<div style="margin: 6px 0; font-size: 11px;"><span style="color: #000;">${label}</span> <span style="float: right; color: #dc3545;">${value}</span></div>`;
                            }
                        }
                    });
                } else {
                    contentHTML += '<div style="text-align: center; padding: 20px; color: #999; font-size: 11px;">No Deductions</div>';
                }
                contentHTML += '</div>';
            }
            contentHTML += '</div>';
        }

        // Net Salary (Full width)
        const netSalary = document.getElementById('net-salary-amount');
        if (netSalary) {
            contentHTML += `
                <div style="margin-top: 20px; padding: 18px; background: #198754; color: white; text-align: center; border-radius: 5px;">
                    <h3 style="margin: 0 0 8px 0; font-size: 16px; font-weight: bold;">Net Salary</h3>
                    <h2 style="margin: 0; font-size: 28px; font-weight: bold;">${netSalary.textContent.trim()}</h2>
                </div>
            `;
        }

        pdfContainer.innerHTML = header + contentHTML;

        // PDF options
        const opt = {
            margin: 0,
            filename: filename,
            image: { type: 'jpeg', quality: 0.95 },
            html2canvas: { 
                scale: 2,
                useCORS: true,
                letterRendering: true,
                backgroundColor: '#ffffff',
                logging: true
            },
            jsPDF: { 
                unit: 'mm', 
                format: 'a4', 
                orientation: 'portrait'
            }
        };

        // Generate PDF and trigger download
        html2pdf().set(opt).from(pdfContainer).save().then(() => {
            console.log('PDF exported successfully');
            
            // Reset button state
            if (printBtn) {
                printBtn.innerHTML = '<i class="fas fa-file-pdf me-1"></i> Export PDF';
                printBtn.disabled = false;
            }
            
            // Show success message
            showToast('PDF exported successfully!', 'success');
        }).catch(error => {
            throw error;
        });
        
    } catch (error) {
        console.error('Export PDF error:', error);
        alert('An error occurred while exporting PDF. Please try again.');
        
        if (printBtn) {
            printBtn.innerHTML = '<i class="fas fa-file-pdf me-1"></i> Export PDF';
            printBtn.disabled = false;
        }
    }
};

// Keep old function for backward compatibility
window.printPayslip = window.exportPayslipPDF;

/**
 * Load all content immediately when page loads
 */
function loadAllContent() {
    console.log('Loading all content...');
    
    // Force all content to be visible and loaded
    const allElements = document.querySelectorAll('*');
    allElements.forEach(element => {
        if (element.style.display === 'none' && 
            !element.classList.contains('sidebar') && 
            !element.classList.contains('btn') && 
            element.tagName.toLowerCase() !== 'button') {
            element.style.display = 'block';
        }
    });
    
    // Scroll to bottom to trigger any lazy loading, then back to top
    setTimeout(() => {
        const documentHeight = Math.max(
            document.body.scrollHeight,
            document.documentElement.scrollHeight
        );
        
        window.scrollTo({ top: documentHeight, behavior: 'instant' });
        
        // Wait a bit then scroll back to top
        setTimeout(() => {
            window.scrollTo({ top: 0, behavior: 'smooth' });
            console.log('All content loaded');
        }, 500);
    }, 100);
}

// Debug log to confirm function is loaded
console.log('ExportPayslipPDF function loaded and available globally:', typeof window.exportPayslipPDF);

document.addEventListener('DOMContentLoaded', function () {
    // Load all content immediately when page loads
    loadAllContent();
    
    // Initialize page
    initializePayslipDetail();
    // Handle responsive behavior
    handleResponsiveBehavior();

    // Add smooth animations
    addSmoothAnimations();
    
    // Debug: Ensure export function is available
    console.log('Export PDF function loaded:', typeof window.exportPayslipPDF === 'function');
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
                    const amountText = amountElement.textContent.replace(/[^\d.,]/g, '');
                    // Handle both comma and dot as thousand separators
                    const cleanAmount = amountText.replace(/[,.]/g, '');
                    grossAmount = parseFloat(cleanAmount) || 0;
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
                    const amountText = amountElement.textContent.replace(/[^\d.,]/g, '');
                    // Handle both comma and dot as thousand separators
                    const cleanAmount = amountText.replace(/[,.]/g, '');
                    totalDeductions = parseFloat(cleanAmount) || 0;
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
            console.log('Updating Net Salary:', {
                grossAmount: grossAmount,
                totalDeductions: totalDeductions,
                netAmount: netAmount,
                formatted: formatCurrency(netAmount)
            });
            netSalaryElement.innerHTML = formatCurrency(netAmount);
        }
    }
}

/**
 * Format currency values
 */
function formatCurrency(value) {
    return new Intl.NumberFormat('en-US', {
        style: 'decimal',
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
    exportPayslipPDF,
    printPayslip: window.exportPayslipPDF // Alias for backward compatibility
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