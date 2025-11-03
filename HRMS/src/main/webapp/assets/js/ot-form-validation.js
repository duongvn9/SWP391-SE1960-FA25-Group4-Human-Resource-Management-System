/**
 * Enhanced OT Form Validation
 * Comprehensive frontend validation before allowing form submission
 */

class OTFormValidator {
    constructor() {
        this.form = document.getElementById('otRequestForm');
        this.errors = [];
        this.firstErrorField = null;

        // Initialize validation
        this.init();
    }

    init() {
        if (!this.form) {
            console.error('OT form not found');
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
            if (field.type === 'radio') {
                field.addEventListener('change', () => this.validateField(field));
            } else if (field.type === 'checkbox') {
                field.addEventListener('change', () => this.validateField(field));
            } else if (field.tagName === 'SELECT') {
                field.addEventListener('change', () => this.validateField(field));
                field.addEventListener('blur', () => this.validateField(field));
            } else {
                field.addEventListener('blur', () => this.validateField(field));
                field.addEventListener('input', () => this.clearFieldError(field));
            }
        });

        // Special handling for time dropdowns
        const timeSelects = ['startHour', 'startMinute', 'endHour', 'endMinute'];
        timeSelects.forEach(id => {
            const select = document.getElementById(id);
            if (select) {
                select.addEventListener('change', () => {
                    this.validateField(select);
                    this.validateTimeFields();
                });
            }
        });

        // Add validation for radio button groups
        const radioGroups = ['requestFor'];
        radioGroups.forEach(groupName => {
            const radios = this.form.querySelectorAll(`input[name="${groupName}"]`);
            radios.forEach(radio => {
                radio.addEventListener('change', () => this.validateField(radio));
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
        console.log('Starting OT form validation...');
        let isValid = true;

        // 1. Validate Request For (Required)
        console.log('Validating Request For...');
        if (!this.validateRequestFor()) {
            console.log('Request For validation failed');
            isValid = false;
        }

        // 2. Validate Employee Selection (if creating for subordinate)
        console.log('Validating Employee Selection...');
        if (!this.validateEmployeeSelection()) {
            console.log('Employee Selection validation failed');
            isValid = false;
        }

        // 3. Validate Request Title (Required)
        console.log('Validating Request Title...');
        if (!this.validateRequestTitle()) {
            console.log('Request Title validation failed');
            isValid = false;
        }

        // 4. Validate OT Date (Required)
        console.log('Validating OT Date...');
        if (!this.validateOTDate()) {
            console.log('OT Date validation failed');
            isValid = false;
        }

        // 5. Validate Time Fields (Required)
        console.log('Validating Time Fields...');
        if (!this.validateTimeFields()) {
            console.log('Time Fields validation failed');
            isValid = false;
        }

        // 6. Validate OT Hours and Limits
        console.log('Validating OT Hours...');
        if (!this.validateOTHours()) {
            console.log('OT Hours validation failed');
            isValid = false;
        }

        // 7. Validate Reason (Required)
        console.log('Validating Reason...');
        if (!this.validateReason()) {
            console.log('Reason validation failed');
            isValid = false;
        }

        // 8. Validate Employee Consent (if creating for self)
        console.log('Validating Employee Consent...');
        if (!this.validateEmployeeConsent()) {
            console.log('Employee Consent validation failed');
            isValid = false;
        }

        // 9. Validate File Size
        console.log('Validating File Size...');
        if (!this.validateFileSize()) {
            console.log('File Size validation failed');
            isValid = false;
        }

        console.log('OT form validation completed. Valid:', isValid, 'Errors:', this.errors.length);
        return isValid;
    }

    validateRequestFor() {
        const requestFor = document.querySelector('input[name="requestFor"]:checked');
        if (!requestFor) {
            this.addError('Please select who this request is for',
                         document.getElementById('requestForSelf'));
            return false;
        }
        return true;
    }

    validateEmployeeSelection() {
        const requestFor = document.querySelector('input[name="requestFor"]:checked')?.value;

        if (requestFor === 'employee') {
            const selectedEmployee = document.getElementById('selectedEmployeeId');
            if (!selectedEmployee || !selectedEmployee.value) {
                this.addError('Please select an employee', selectedEmployee);
                return false;
            }
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

    validateOTDate() {
        const otDateInput = document.getElementById('otDate');

        if (!otDateInput.value) {
            this.addError('OT date is required', otDateInput);
            return false;
        }

        // Validate date is not in the past (must be at least 1 day in advance)
        const otDate = new Date(otDateInput.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (otDate <= today) {
            this.addError('OT request must be submitted at least 1 day in advance', otDateInput);
            return false;
        }

        return true;
    }

    validateTimeFields() {
        const startHour = document.getElementById('startHour');
        const startMinute = document.getElementById('startMinute');
        const endHour = document.getElementById('endHour');
        const endMinute = document.getElementById('endMinute');

        // Check if all time fields are selected
        if (!startHour.value) {
            this.addError('Start hour is required', startHour);
            return false;
        }

        if (!startMinute.value) {
            this.addError('Start minute is required', startMinute);
            return false;
        }

        if (!endHour.value) {
            this.addError('End hour is required', endHour);
            return false;
        }

        if (!endMinute.value) {
            this.addError('End minute is required', endMinute);
            return false;
        }

        // Validate time range
        const startTime = startHour.value + ':' + startMinute.value;
        const endTime = endHour.value + ':' + endMinute.value;

        const start = new Date('2000-01-01 ' + startTime);
        const end = new Date('2000-01-01 ' + endTime);

        if (end <= start) {
            this.addError('End time must be after start time', endHour);
            return false;
        }

        // Validate time range (06:00-22:00)
        const startHourNum = parseInt(startHour.value);
        const endHourNum = parseInt(endHour.value);

        if (startHourNum < 6 || startHourNum > 22) {
            this.addError('Start time must be between 06:00 and 22:00', startHour);
            return false;
        }

        if (endHourNum < 6 || endHourNum > 22) {
            this.addError('End time must be between 06:00 and 22:00', endHour);
            return false;
        }

        // Update hidden fields
        document.getElementById('startTime').value = startTime;
        document.getElementById('endTime').value = endTime;

        return true;
    }

    validateOTHours() {
        const startTime = document.getElementById('startTime').value;
        const endTime = document.getElementById('endTime').value;

        if (!startTime || !endTime) {
            return false; // Already handled in validateTimeFields
        }

        // Calculate OT hours
        const start = new Date('2000-01-01 ' + startTime);
        const end = new Date('2000-01-01 ' + endTime);
        const otHours = (end - start) / (1000 * 60 * 60); // Convert to hours

        if (otHours <= 0) {
            this.addError('OT duration must be greater than 0 hours', document.getElementById('endHour'));
            return false;
        }

        // Check weekday OT restrictions
        const otDate = document.getElementById('otDate').value;
        if (otDate) {
            const otType = this.determineOTTypeSimple(otDate);

            if (otType === 'WEEKDAY') {
                // Weekday OT: 19:00-22:00, max 2 hours
                const startHour = parseInt(startTime.split(':')[0]);
                const endHour = parseInt(endTime.split(':')[0]);
                const endMinute = parseInt(endTime.split(':')[1]);

                if (startHour < 19) {
                    this.addError('Weekday OT can only start from 19:00 onwards', document.getElementById('startHour'));
                    return false;
                }

                if (endHour > 22 || (endHour === 22 && endMinute > 0)) {
                    this.addError('Weekday OT must end by 22:00', document.getElementById('endHour'));
                    return false;
                }

                if (otHours > 2) {
                    this.addError('Weekday OT is limited to 2 hours maximum', document.getElementById('endHour'));
                    return false;
                }
            } else {
                // Weekend/Holiday OT: max 10 hours
                if (otHours > 10) {
                    this.addError('Weekend/Holiday OT is limited to 10 hours maximum', document.getElementById('endHour'));
                    return false;
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

    validateEmployeeConsent() {
        const requestFor = document.querySelector('input[name="requestFor"]:checked')?.value;

        // Employee consent is only required when creating for self
        if (requestFor === 'self') {
            const consentCheckbox = document.getElementById('employeeConsent');
            if (!consentCheckbox || !consentCheckbox.checked) {
                this.addError('Employee consent is required for overtime work', consentCheckbox);
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

    determineOTTypeSimple(otDate) {
        // Use global function if available, otherwise simple determination
        if (typeof determineOTType === 'function') {
            const result = determineOTType(otDate);
            return result ? result.type : 'WEEKDAY';
        }

        // Fallback simple determination
        const date = new Date(otDate);
        const dayOfWeek = date.getDay();

        // Check if it's weekend
        if (dayOfWeek === 0 || dayOfWeek === 6) {
            return 'WEEKEND';
        }

        // Check if it's holiday (if holiday data is available)
        if (window.holidays && window.holidays.includes(otDate)) {
            return 'HOLIDAY';
        }

        // Check if it's compensatory day
        if (window.compensatoryDays && window.compensatoryDays.includes(otDate)) {
            return 'COMPENSATORY';
        }

        return 'WEEKDAY';
    }

    validateField(field) {
        // Individual field validation for real-time feedback
        this.clearFieldError(field);

        // Handle different field types
        if (field.type === 'radio') {
            // For radio buttons, check if any in the group is selected
            const radioGroup = document.querySelectorAll(`input[name="${field.name}"]`);
            const isSelected = Array.from(radioGroup).some(radio => radio.checked);
            if (!isSelected) {
                this.showFieldError(field, 'Please select an option');
                return false;
            }
        } else if (field.type === 'checkbox' && field.hasAttribute('required')) {
            // For required checkboxes
            if (!field.checked) {
                this.showFieldError(field, 'This field is required');
                return false;
            }
        } else if (field.tagName === 'SELECT') {
            // For select dropdowns
            if (field.hasAttribute('required') && (!field.value || field.value.trim() === '')) {
                this.showFieldError(field, this.getFieldLabel(field) + ' is required');
                return false;
            }
        } else {
            // For regular input fields
            if (field.hasAttribute('required') && !field.value.trim()) {
                this.showFieldError(field, this.getFieldLabel(field) + ' is required');
                return false;
            }
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

        if (field.id === 'otDate') {
            if (field.value) {
                const otDate = new Date(field.value);
                const today = new Date();
                today.setHours(0, 0, 0, 0);

                if (otDate <= today) {
                    this.showFieldError(field, 'OT request must be submitted at least 1 day in advance');
                    return false;
                }
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
        console.log('Displaying errors:', this.errors);
        if (this.errors.length === 0) {
            console.log('No errors to display');
            return;
        }

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
        const formCard = document.querySelector('.ot-request-card .card-body');
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

// Initialize validator when DOM is loaded (after ot-form.js)
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded, checking for OT form...');

    // Wait a bit to ensure ot-form.js is loaded first
    setTimeout(() => {
        const form = document.getElementById('otRequestForm');
        console.log('OT form found:', form);

        // Only initialize if we're on the OT form page
        if (form) {
            try {
                new OTFormValidator();
                console.log('OT form validator initialized successfully');
            } catch (error) {
                console.error('Error initializing OT form validator:', error);
            }
        } else {
            console.log('OT form not found on this page');
        }
    }, 100);
});