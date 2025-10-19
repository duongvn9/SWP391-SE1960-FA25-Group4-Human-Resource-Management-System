/**
 * Attachment Toggle Handler
 * Manages switching between file upload and Google Drive link input
 * Used in: leave-form.jsp, ot-form.jsp
 *
 * @author HRMS Development Team
 * @version 1.0
 */

document.addEventListener('DOMContentLoaded', function() {
    console.log('Attachment toggle script loaded');

    // Get DOM elements
    const fileRadio = document.getElementById('attachmentTypeFile');
    const linkRadio = document.getElementById('attachmentTypeLink');
    const fileUploadSection = document.getElementById('fileUploadSection');
    const driveLinkSection = document.getElementById('driveLinkSection');
    const attachmentsInput = document.getElementById('attachments');
    const driveLinkInput = document.getElementById('driveLink');
    const driveLinkPreview = document.getElementById('driveLinkPreview');
    const driveLinkText = document.getElementById('driveLinkText');

    // Check if elements exist (might not exist on all pages)
    if (!fileRadio || !linkRadio || !fileUploadSection || !driveLinkSection) {
        console.log('Attachment toggle elements not found on this page');
        return;
    }

    console.log('Attachment toggle elements found, initializing...');

    /**
     * Toggle between file upload and Drive link sections
     */
    function toggleAttachmentType() {
        if (linkRadio.checked) {
            // Show Drive link section, hide file upload
            fileUploadSection.classList.add('d-none');
            driveLinkSection.classList.remove('d-none');

            // Clear file input
            if (attachmentsInput) {
                attachmentsInput.value = '';
                // Clear file preview if exists
                const filePreviewList = document.getElementById('filePreviewList');
                if (filePreviewList) {
                    filePreviewList.innerHTML = '';
                }
            }

            console.log('Switched to Drive Link mode');
        } else {
            // Show file upload section, hide Drive link
            fileUploadSection.classList.remove('d-none');
            driveLinkSection.classList.add('d-none');

            // Clear Drive link input
            if (driveLinkInput) {
                driveLinkInput.value = '';
                if (driveLinkPreview) {
                    driveLinkPreview.classList.add('d-none');
                }
            }

            console.log('Switched to File Upload mode');
        }
    }

    /**
     * Validate and show preview for Google Drive link
     */
    function handleDriveLinkInput() {
        const url = driveLinkInput.value.trim();

        if (url === '') {
            driveLinkPreview.classList.add('d-none');
            return;
        }

        // Basic validation for Google Drive URLs
        const isValidGoogleDriveUrl = url.includes('drive.google.com') || url.includes('docs.google.com');

        if (isValidGoogleDriveUrl) {
            // Show preview
            driveLinkText.textContent = url.length > 60 ? url.substring(0, 60) + '...' : url;
            driveLinkPreview.classList.remove('d-none');
            driveLinkPreview.classList.remove('alert-danger');
            driveLinkPreview.classList.add('alert-info');
            driveLinkPreview.querySelector('strong').textContent = 'Drive Link:';
        } else {
            // Show error
            driveLinkText.textContent = 'Invalid Google Drive URL. Please paste a valid link.';
            driveLinkPreview.classList.remove('d-none');
            driveLinkPreview.classList.remove('alert-info');
            driveLinkPreview.classList.add('alert-danger');
            driveLinkPreview.querySelector('strong').textContent = 'Error:';
        }
    }

    // Add event listeners
    fileRadio.addEventListener('change', toggleAttachmentType);
    linkRadio.addEventListener('change', toggleAttachmentType);

    if (driveLinkInput) {
        driveLinkInput.addEventListener('input', handleDriveLinkInput);
        driveLinkInput.addEventListener('blur', handleDriveLinkInput);
    }

    // Initialize: show file upload section by default
    toggleAttachmentType();

    console.log('Attachment toggle initialized successfully');
});

/**
 * Clear Drive link input and hide preview
 * Called from inline onclick event
 */
window.clearDriveLink = function() {
    const driveLinkInput = document.getElementById('driveLink');
    const driveLinkPreview = document.getElementById('driveLinkPreview');

    if (driveLinkInput) {
        driveLinkInput.value = '';
    }

    if (driveLinkPreview) {
        driveLinkPreview.classList.add('d-none');
    }

    console.log('Drive link cleared');
};
