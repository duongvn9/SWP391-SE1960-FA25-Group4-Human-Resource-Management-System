/**
 * Request Detail Page JavaScript
 * Handles attachment modal and other detail-specific functionality
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
 * Show success message using Bootstrap toast
 * @param {string} message - The success message to display
 */
function showSuccessMessage(message) {
    showMessage(message, 'success');
}

/**
 * Show error message using Bootstrap toast
 * @param {string} message - The error message to display
 */
function showErrorMessage(message) {
    showMessage(message, 'danger');
}

/**
 * Show a toast notification using Bootstrap Toast API
 * @param {string} message - The message to display
 * @param {string} type - The Bootstrap alert type: 'success', 'danger', 'info', 'warning'
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
document.addEventListener('DOMContentLoaded', function () {
    // Clear validation when user starts typing in reason field
    const reasonField = document.getElementById('approvalReason');
    if (reasonField) {
        reasonField.addEventListener('input', function () {
            if (this.value.trim()) {
                this.classList.remove('is-invalid');
                const reasonErrorEl = document.getElementById('reasonError');
                if (reasonErrorEl) reasonErrorEl.style.display = 'none';
            }
        });
    }

    // Handle attachment item clicks
    document.addEventListener('click', function (e) {
        const attachmentItem = e.target.closest('.attachment-item');
        if (attachmentItem) {
            e.preventDefault();
            const id = attachmentItem.getAttribute('data-attachment-id');
            const name = attachmentItem.getAttribute('data-attachment-name');
            const type = attachmentItem.getAttribute('data-attachment-type');
            viewAttachment(id, name, type);
        }
    });
});

/**
 * View attachment by ID - opens modal with preview
 * @param {number} id - The attachment ID
 * @param {string} filename - The original filename
 * @param {string} contentType - The MIME content type
 */
function viewAttachment(id, filename, contentType) {
    const contextPath = getContextPath();
    const modal = new bootstrap.Modal(document.getElementById('attachmentModal'));
    const content = document.getElementById('attachmentContent');
    const downloadBtn = document.getElementById('downloadBtn');

    // Set download link
    downloadBtn.href = `${contextPath}/attachments/${id}/download`;
    downloadBtn.setAttribute('download', filename);

    // Show loading
    content.innerHTML = '<div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div>';
    modal.show();

    // Load content based on type
    if (contentType.startsWith('image/')) {
        content.innerHTML = `<img src="${contextPath}/attachments/${id}/view" class="img-fluid" alt="${escapeHtml(filename)}" style="max-width: 100%; max-height: 70vh;">`;
    } else if (contentType === 'application/pdf') {
        content.innerHTML = `<embed src="${contextPath}/attachments/${id}/view" type="application/pdf" width="100%" height="600px">`;
    } else {
        content.innerHTML = `
            <div class="text-center py-4">
                <i class="fas fa-file fa-4x text-muted mb-3"></i>
                <p class="h5">${escapeHtml(filename)}</p>
                <p class="text-muted">Preview not available for this file type</p>
            </div>
        `;
    }
}

/**
 * Open attachment modal and display file based on type
 * @param {string} attachmentPath - The path to the attachment file (can be relative or absolute)
 */
function openAttachmentModal(attachmentPath) {
    if (!attachmentPath) {
        showErrorMessage('No attachment path provided');
        return;
    }

    // Get the modal element
    const modal = new bootstrap.Modal(document.getElementById('attachmentModal'));
    const attachmentContent = document.getElementById('attachmentContent');
    const downloadBtn = document.getElementById('downloadBtn');

    // Show loading spinner
    attachmentContent.innerHTML = `
        <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    `;

    // Show modal
    modal.show();

    // Use the path as-is (it should already include context path from JSP)
    const fullPath = attachmentPath;

    // Extract file name and extension
    const fileName = attachmentPath.split('/').pop();
    const fileExtension = fileName.split('.').pop().toLowerCase();

    // Set download button
    downloadBtn.href = fullPath;
    downloadBtn.setAttribute('download', fileName);

    // Detect file type and render accordingly
    if (isImageFile(fileExtension)) {
        // Display image
        renderImage(attachmentContent, fullPath, fileName);
    } else if (isPdfFile(fileExtension)) {
        // Display PDF
        renderPdf(attachmentContent, fullPath);
    } else {
        // Display file info for other document types
        renderFileInfo(attachmentContent, fileName, fileExtension);
    }
}

/**
 * Check if file extension is an image type
 * @param {string} extension - File extension
 * @returns {boolean} True if image file
 */
function isImageFile(extension) {
    const imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg'];
    return imageExtensions.includes(extension);
}

/**
 * Check if file extension is PDF
 * @param {string} extension - File extension
 * @returns {boolean} True if PDF file
 */
function isPdfFile(extension) {
    return extension === 'pdf';
}

/**
 * Render image in attachment modal
 * @param {HTMLElement} container - Container element
 * @param {string} imagePath - Path to image
 * @param {string} fileName - File name
 */
function renderImage(container, imagePath, fileName) {
    const img = document.createElement('img');
    img.src = imagePath;
    img.alt = fileName;
    img.className = 'img-fluid';
    img.style.maxWidth = '100%';
    img.style.maxHeight = '70vh';

    // Handle image load error
    img.onerror = function () {
        container.innerHTML = `
            <div class="alert alert-warning" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                Failed to load image. The file may not exist or is corrupted.
            </div>
        `;
    };

    // Clear loading spinner and show image
    container.innerHTML = '';
    container.appendChild(img);
}

/**
 * Render PDF in attachment modal
 * @param {HTMLElement} container - Container element
 * @param {string} pdfPath - Path to PDF
 */
function renderPdf(container, pdfPath) {
    // Use iframe for better compatibility
    const iframe = document.createElement('iframe');
    iframe.src = pdfPath;
    iframe.style.width = '100%';
    iframe.style.height = '70vh';
    iframe.style.border = 'none';

    // Handle iframe load error
    iframe.onerror = function () {
        container.innerHTML = `
            <div class="alert alert-warning" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                Failed to load PDF. Please use the download button to view the file.
            </div>
        `;
    };

    // Clear loading spinner and show PDF
    container.innerHTML = '';
    container.appendChild(iframe);
}

/**
 * Render file information for non-previewable files
 * @param {HTMLElement} container - Container element
 * @param {string} fileName - File name
 * @param {string} fileExtension - File extension
 */
function renderFileInfo(container, fileName, fileExtension) {
    // Get file type description
    const fileTypeDescription = getFileTypeDescription(fileExtension);

    // Create file info display
    container.innerHTML = `
        <div class="text-center py-4">
            <i class="fas fa-file fa-5x text-secondary mb-3"></i>
            <h5 class="mb-3">${escapeHtml(fileName)}</h5>
            <p class="text-muted mb-3">
                <strong>File Type:</strong> ${fileTypeDescription}
            </p>
            <div class="alert alert-info" role="alert">
                <i class="fas fa-info-circle me-2"></i>
                Preview not available for this file type. Please use the download button to view the file.
            </div>
        </div>
    `;
}

/**
 * Get human-readable file type description
 * @param {string} extension - File extension
 * @returns {string} File type description
 */
function getFileTypeDescription(extension) {
    const fileTypes = {
        'doc': 'Microsoft Word Document',
        'docx': 'Microsoft Word Document',
        'xls': 'Microsoft Excel Spreadsheet',
        'xlsx': 'Microsoft Excel Spreadsheet',
        'ppt': 'Microsoft PowerPoint Presentation',
        'pptx': 'Microsoft PowerPoint Presentation',
        'txt': 'Text Document',
        'csv': 'CSV File',
        'zip': 'Compressed Archive',
        'rar': 'Compressed Archive',
        '7z': 'Compressed Archive'
    };

    return fileTypes[extension] || extension.toUpperCase() + ' File';
}

/**
 * Escape HTML to prevent XSS
 * @param {string} text - Text to escape
 * @returns {string} Escaped text
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * Clear attachment content when modal is closed
 */
document.addEventListener('DOMContentLoaded', function () {
    const attachmentModal = document.getElementById('attachmentModal');
    if (attachmentModal) {
        attachmentModal.addEventListener('hidden.bs.modal', function () {
            // Clear attachment content when modal is closed
            const attachmentContent = document.getElementById('attachmentContent');
            if (attachmentContent) {
                attachmentContent.innerHTML = `
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                `;
            }

            // Reset download button
            const downloadBtn = document.getElementById('downloadBtn');
            if (downloadBtn) {
                downloadBtn.href = '#';
                downloadBtn.removeAttribute('download');
            }
        });
    }
});
