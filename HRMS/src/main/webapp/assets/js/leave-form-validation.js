/**
 * Enhanced Leave Form Validation
 * Comprehensive frontend validation before allowing form submission
 */

class LeaveFormValidator {
    constructor() {
        this.form = document.getElementById('leaveRequestForm');
        this.errors = [];
        this.firstErrorField = null;

        // Initialize validation
        this.init();
    }

    init() {
        if (!this.form) {
            console.log('Leave form not found');
            return;
        }

        // Add submit event listener
        this.form.addEventListener('submit', (e) => this.handleSubmit(e));

        // Add real-time validation for required fields
        this.addRealTimeValidation();
    }

    addRealTimeValidation() {
        // Add blur validation for required fields
        const requiredFields = this.form.querySelectorAll('[required]');
        requiredFields.forEach(field => {
            field.addEventListener('blur', () => this.validateField(field));
            field.addEventListener('input', () => this.clearFieldError(field));
        });

        // Add special handling for half-day period radio buttons
        const halfDayRadios = this.form.querySelectorAll('input[name="halfDayPeriod"]');
        halfDayRadios.forEach(radio => {
            radio.addEventListener('change', () => {
                // Clear error from half-day container when user selects an option
                const halfDayContainer = document.getElementById('halfDayPeriodContainer');
                if (halfDayContainer) {
                    this.clearFieldError(halfDayContainer);
                }
            });
        });
    }

    async handleSubmit(e) {
        e.preventDefault(); // Always prevent default first

        // Clear previous errors
        this.clearAllErrors();
        this.errors = [];
        this.firstErrorField = null;

        // Run comprehensive validation
        const isValid = await this.validateForm();

        if (!isValid) {
            // Show errors and focus first error field
            this.displayErrors();
            if (this.firstErrorField) {
                this.firstErrorField.focus();
                this.firstErrorField.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
            return false;
        }

        // If validation passes, submit the form
        this.submitForm();
    }

    async validateForm() {
        let isValid = true;

        // 1. Validate Leave Type (Required)
        if (!this.validateLeaveType()) isValid = false;

        // 2. Validate Request Title (Required)
        if (!this.validateRequestTitle()) isValid = false;

        // 3. Validate Duration Type (Required)
        if (!this.validateDurationType()) isValid = false;

        // 4. Validate Dates (Required)
        if (!this.validateDates()) isValid = false;

        // 5. Validate Half-day specific fields
        if (!await this.validateHalfDayFields()) isValid = false;

        // 6. Validate Reason (Required)
        if (!this.validateReason()) isValid = false;

        // 7. Validate Certificate Requirements
        if (!this.validateCertificateRequirement()) isValid = false;

        // 8. Validate File Size
        if (!this.validateFileSize()) isValid = false;

        return isValid;
    }

    validateLeaveType() {
        const leaveTypeSelect = document.getElementById('leaveTypeCode');
        if (!leaveTypeSelect.value || leaveTypeSelect.value.trim() === '') {
            this.addError('Please select a leave type', leaveTypeSelect);
            return false;
        }
        return true;
    }

    validateRequestTitle() {
        const titleInput = document.getElementById('requestTitle');
        const title = titleInput.value.trim();

        if (!title) {
            this.addError('Request title is required', titleInput);
            return false;
        }

        if (title.length < 5) {
            this.addError('Request title must be at least 5 characters long', titleInput);
            return false;
        }

        if (title.length > 200) {
            this.addError('Request title cannot exceed 200 characters', titleInput);
            return false;
        }

        return true;
    }

    validateDurationType() {
        const durationType = document.querySelector('input[name="durationType"]:checked');
        if (!durationType) {
            this.addError('Please select duration type (Full Day or Half Day)',
                         document.getElementById('durationFullDay'));
            return false;
        }
        return true;
    }

    validateDates() {
        const startDateInput = document.getElementById('startDate');
        const endDateInput = document.getElementById('endDate');
        const durationType = document.querySelector('input[name="durationType"]:checked')?.value;

        // Start date is always required
        if (!startDateInput.value) {
            if (durationType === 'HALF_DAY') {
                this.addError('Date is required', startDateInput);
            } else {
                this.addError('Start date is required', startDateInput);
            }
            return false;
        }

        // End date is required for full-day leave
        if (durationType !== 'HALF_DAY' && !endDateInput.value) {
            this.addError('End date is required', endDateInput);
            return false;
        }

        // Validate date range for full-day
        if (durationType !== 'HALF_DAY' && startDateInput.value && endDateInput.value) {
            const startDate = new Date(startDateInput.value);
            const endDate = new Date(endDateInput.value);

            if (endDate < startDate) {
                this.addError('End date cannot be before start date', endDateInput);
                return false;
            }
        }

        // Validate date is not in the past
        const startDate = new Date(startDateInput.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (startDate < today) {
            this.addError('Leave date cannot be in the past', startDateInput);
            return false;
        }

        return true;
    }

    async validateHalfDayFields() {
        const durationType = document.querySelector('input[name="durationType"]:checked')?.value;

        if (durationType === 'HALF_DAY') {
            // Half-day period is required
            const halfDayPeriod = document.querySelector('input[name="halfDayPeriod"]:checked');
            if (!halfDayPeriod) {
                // Find the half-day period container to show error after the form-text
                const halfDayContainer = document.getElementById('halfDayPeriodContainer');
                this.addError('Half-day period is required - please select Morning (8:00-12:00) or Afternoon (13:00-17:00)',
                             halfDayContainer);
                return false;
            }

            // Validate half-day date (async validation)
            const startDate = document.getElementById('startDate').value;
            if (startDate && halfDayPeriod) {
                try {
                    // Check if validateHalfDayRequest function exists (from leave-form.jsp)
                    if (typeof validateHalfDayRequest === 'function') {
                        const validationResult = await validateHalfDayRequest(startDate, halfDayPeriod.value);
                        if (!validationResult.valid) {
                            validationResult.errors.forEach(error => {
                                this.addError(error, document.getElementById('startDate'));
                            });
                            return false;
                        }
                    }
                } catch (error) {
                    console.warn('Half-day validation failed:', error);
                    // Continue with client-side validation only
                }
            }
        }

        return true;
    }

    validateReason() {
        const reasonTextarea = document.getElementById('reason');
        const reason = reasonTextarea.value.trim();

        if (!reason) {
            this.addError('Reason is required', reasonTextarea);
            return false;
        }

        if (reason.length > 1000) {
            this.addError('Reason cannot exceed 1000 characters', reasonTextarea);
            return false;
        }

        return true;
    }

    validateCertificateRequirement() {
        const selectedLeaveType = document.getElementById('leaveTypeCode').value;

        // Check if leave type rules are available
        if (!window.leaveTypeRules || !window.leaveTypeRules[selectedLeaveType]) {
            return true; // Skip validation if rules not available
        }

        const rules = window.leaveTypeRules[selectedLeaveType];

        if (rules.requiresCertificate) {
            const attachmentType = document.querySelector('input[name="attachmentType"]:checked')?.value;
            const hasFile = document.getElementById('attachments')?.files?.length > 0;
            const hasLink = document.getElementById('driveLink')?.value?.trim() !== '';

            if (!hasFile && !hasLink) {
                this.addError(
                    `This leave type (${rules.name}) requires supporting documents. Please upload a file or provide a Google Drive link.`,
                    document.getElementById('attachments')
                );
                return false;
            }
        }

        return true;
    }

    validateFileSize() {
        const attachmentsInput = document.getElementById('attachments');
        if (attachmentsInput && attachmentsInput.files.length > 0) {
            const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

            for (let file of attachmentsInput.files) {
                if (file.size > MAX_FILE_SIZE) {
                    this.addError(
                        `File "${file.name}" exceeds 5MB limit. Please choose a smaller file.`,
                        attachmentsInput
                    );
                    return false;
                }
            }
        }

        return true;
    }

    validateField(field) {
        // Individual field validation for real-time feedback
        this.clearFieldError(field);

        if (field.hasAttribute('required') && !field.value.trim()) {
            this.showFieldError(field, this.getFieldLabel(field) + ' is required');
            return false;
        }

        // Specific field validations
        if (field.id === 'requestTitle') {
            const title = field.value.trim();
            if (title && title.length < 5) {
                this.showFieldError(field, 'Request title must be at least 5 characters long');
                return false;
            }
        }

        if (field.id === 'reason') {
            const reason = field.value.trim();
            if (reason && reason.length > 1000) {
                this.showFieldError(field, 'Reason cannot exceed 1000 characters');
                return false;
            }
        }

        return true;
    }

    addError(message, field = null) {
        this.errors.push(message);

        // Track first error field for focusing
        if (field && !this.firstErrorField) {
            this.firstErrorField = field;
        }

        // Show field-specific error
        if (field) {
            this.showFieldError(field, message);
        }
    }

    showFieldError(field, message) {
        // Handle container divs (like halfDayPeriodContainer) differently
        if (field.tagName === 'DIV' && field.id === 'halfDayPeriodContainer') {
            // For half-day container, show error after the form-text
            let errorDiv = field.querySelector('.invalid-feedback');
            if (!errorDiv) {
                errorDiv = document.createElement('div');
                errorDiv.className = 'invalid-feedback';
                // Insert after the form-text element
                const formText = field.querySelector('.form-text');
                if (formText) {
                    formText.parentNode.insertBefore(errorDiv, formText.nextSibling);
                } else {
                    field.appendChild(errorDiv);
                }
            }
            errorDiv.textContent = message;
            errorDiv.style.cssText = 'display: block !important; color: #b91c1c !important; font-weight: 700 !important; text-shadow: none !important; margin-top: 0.25rem !important;';
            return;
        }

        // Only add error class to input fields, not radio/checkbox
        if (field.type !== 'radio' && field.type !== 'checkbox') {
            field.classList.add('is-invalid');
        }

        // Create or update error message
        let errorDiv = field.parentNode.querySelector('.invalid-feedback');
        if (!errorDiv) {
            errorDiv = document.createElement('div');
            errorDiv.className = 'invalid-feedback';
            field.parentNode.appendChild(errorDiv);
        }
        errorDiv.textContent = message;
        errorDiv.style.cssText = 'display: block !important; color: #b91c1c !important; font-weight: 700 !important; text-shadow: none !important;';
    }

    clearFieldError(field) {
        // Handle container divs (like halfDayPeriodContainer) differently
        if (field.tagName === 'DIV' && field.id === 'halfDayPeriodContainer') {
            const errorDiv = field.querySelector('.invalid-feedback');
            if (errorDiv) {
                errorDiv.style.display = 'none';
            }
            return;
        }

        // Only remove class from non-radio/checkbox fields
        if (field.type !== 'radio' && field.type !== 'checkbox') {
            field.classList.remove('is-invalid');
        }

        const errorDiv = field.parentNode.querySelector('.invalid-feedback');
        if (errorDiv) {
            errorDiv.style.display = 'none';
        }
    }

    clearAllErrors() {
        // Clear all field errors
        const invalidFields = this.form.querySelectorAll('.is-invalid');
        invalidFields.forEach(field => this.clearFieldError(field));

        // Clear error alerts
        const errorAlerts = document.querySelectorAll('.validation-error-alert');
        errorAlerts.forEach(alert => alert.remove());
    }

    displayErrors() {
        if (this.errors.length === 0) return;

        // Create error alert
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-danger alert-dismissible fade show validation-error-alert';
        alertDiv.setAttribute('role', 'alert');
        alertDiv.style.cssText = 'color: #b91c1c !important; font-weight: 600 !important; background-color: #fef2f2 !important;';

        let errorHtml = '<div class="d-flex align-items-start">';
        errorHtml += '<div class="flex-shrink-0"><i class="fas fa-exclamation-triangle fa-2x me-3" style="color: #dc3545 !important;"></i></div>';
        errorHtml += '<div class="flex-grow-1">';
        errorHtml += '<h5 class="alert-heading mb-2" style="color: #b91c1c !important; font-weight: 800 !important;">Please fix the following errors:</h5>';
        errorHtml += '<ul class="mb-0" style="color: #b91c1c !important; font-weight: 600 !important;">';

        this.errors.forEach(error => {
            errorHtml += '<li>' + this.escapeHtml(error) + '</li>';
        });

        errorHtml += '</ul></div></div>';
        errorHtml += '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>';

        alertDiv.innerHTML = errorHtml;

        // Insert at the top of the form
        const formCard = document.querySelector('.leave-request-card .card-body');
        formCard.insertBefore(alertDiv, formCard.firstChild);

        // Scroll to the error
        alertDiv.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }

    getFieldLabel(field) {
        const label = this.form.querySelector(`label[for="${field.id}"]`);
        if (label) {
            return label.textContent.replace('*', '').trim();
        }
        return field.name || field.id || 'Field';
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    submitForm() {
        // Set hidden field for half-day
        const durationType = document.querySelector('input[name="durationType"]:checked')?.value;
        const isHalfDay = durationType === 'HALF_DAY';
        document.getElementById('isHalfDay').value = isHalfDay ? 'true' : 'false';

        // Auto-fill end date for half-day
        if (isHalfDay) {
            const startDate = document.getElementById('startDate').value;
            document.getElementById('endDate').value = startDate;
        }

        // Disable submit button to prevent double submission
        const submitBtn = document.getElementById('submitBtn');
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i> Submitting...';
        }

        // Add was-validated class for Bootstrap styling
        this.form.classList.add('was-validated');

        // Submit the form
        this.form.submit();
    }
}

// Initialize validator when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Only initialize if we're on the leave form page
    if (document.getElementById('leaveRequestForm')) {
        new LeaveFormValidator();
        console.log('Leave form validator initialized');
    }
});