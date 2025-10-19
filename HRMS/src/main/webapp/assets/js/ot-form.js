/**
 * OT Request Form JavaScript
 * Requirements: 1, 2, 4, 6
 */

console.log('OT Form script loaded');

document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded, initializing OT form...');

    // Get form elements
    const otDateInput = document.getElementById('otDate');
    const startTimeInput = document.getElementById('startTime');
    const endTimeInput = document.getElementById('endTime');
    const reasonTextarea = document.getElementById('reason');
    const charCount = document.getElementById('charCount');
    const employeeConsentCheckbox = document.getElementById('employeeConsent');
    const submitBtn = document.getElementById('submitBtn');

    // Get display elements
    const otTypeDisplay = document.getElementById('otTypeDisplay');
    const otTypePlaceholder = document.getElementById('otTypePlaceholder');
    const otTypeBadge = document.getElementById('otTypeBadge');
    const otTypeMultiplier = document.getElementById('otTypeMultiplier');
    const otHoursDisplay = document.getElementById('otHoursDisplay');
    const otHoursText = document.getElementById('otHoursText');

    // Load holidays data from server
    let holidays = [];
    let compensatoryDays = [];

    try {
        const holidaysDataElement = document.getElementById('holidaysData');
        if (holidaysDataElement) {
            const holidaysData = JSON.parse(holidaysDataElement.textContent);
            holidays = holidaysData.holidays || [];
            compensatoryDays = holidaysData.compensatoryDays || [];
            console.log('Loaded holidays from server:', holidays.length, 'holidays,', compensatoryDays.length, 'compensatory days');
        } else {
            console.warn('Holidays data element not found, using empty arrays');
        }
    } catch (e) {
        console.error('Error loading holidays data:', e);
        // Fallback to empty arrays if loading fails
        holidays = [];
        compensatoryDays = [];
    }

    /**
     * Determine OT type based on date
     * Requirement 4: Auto-determine OT type
     */
    function determineOTType(dateStr) {
        if (!dateStr) return null;

        console.log('Determining OT type for:', dateStr);
        console.log('Holidays array:', holidays);
        console.log('Is holiday?', holidays.includes(dateStr));
        console.log('Compensatory days array:', compensatoryDays);
        console.log('Is compensatory?', compensatoryDays.includes(dateStr));

        const date = new Date(dateStr);
        const dayOfWeek = date.getDay(); // 0 = Sunday, 6 = Saturday

        // Check if compensatory day
        if (compensatoryDays.includes(dateStr)) {
            console.log('→ Matched as COMPENSATORY');
            return {
                type: 'COMPENSATORY',
                label: 'Compensatory Day',
                multiplier: 2.0,
                badgeClass: 'compensatory',
                description: 'Compensatory day for holiday falling on weekend'
            };
        }

        // Check if holiday
        if (holidays.includes(dateStr)) {
            console.log('→ Matched as HOLIDAY');
            return {
                type: 'HOLIDAY',
                label: 'Public Holiday',
                multiplier: 3.0,
                badgeClass: 'holiday',
                description: 'Public holiday - highest OT rate'
            };
        }

        // Check if weekend
        if (dayOfWeek === 0 || dayOfWeek === 6) {
            console.log('→ Matched as WEEKEND');
            return {
                type: 'WEEKEND',
                label: 'Weekend',
                multiplier: 2.0,
                badgeClass: 'weekend',
                description: 'Saturday or Sunday'
            };
        }

        // Weekday
        console.log('→ Matched as WEEKDAY');
        return {
            type: 'WEEKDAY',
            label: 'Weekday',
            multiplier: 1.5,
            badgeClass: 'weekday',
            description: 'Regular working day (Monday-Friday)'
        };
    }

    /**
     * Calculate OT hours from start and end time
     * Requirement 1: Auto-calculate OT hours
     */
    function calculateOTHours(startTime, endTime) {
        if (!startTime || !endTime) return null;

        const [startHour, startMin] = startTime.split(':').map(Number);
        const [endHour, endMin] = endTime.split(':').map(Number);

        const startMinutes = startHour * 60 + startMin;
        const endMinutes = endHour * 60 + endMin;

        if (endMinutes <= startMinutes) {
            return null; // Invalid time range
        }

        const totalMinutes = endMinutes - startMinutes;
        const hours = totalMinutes / 60;

        return Math.round(hours * 100) / 100; // Round to 2 decimal places
    }

    /**
     * Validate time range (06:00 - 22:00)
     * Requirement 2: Client-side validation for time range
     */
    function validateTimeRange(time) {
        if (!time) return true; // Empty is valid (will be caught by required)

        const [hour, min] = time.split(':').map(Number);
        const minutes = hour * 60 + min;

        const minTime = 6 * 60; // 06:00
        const maxTime = 22 * 60; // 22:00

        return minutes >= minTime && minutes <= maxTime;
    }

    /**
     * Update OT type display when date changes
     * Requirement 4: Auto-determine OT type when user selects date
     */
    otDateInput.addEventListener('change', function() {
        const dateStr = this.value;
        console.log('Date changed:', dateStr);

        if (!dateStr) {
            otTypeDisplay.classList.add('d-none');
            if (otTypePlaceholder) otTypePlaceholder.classList.remove('d-none');
            return;
        }

        // Check if date is in the past
        const selectedDate = new Date(dateStr);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (selectedDate < today) {
            otHoursDisplay.classList.add('error');
            otHoursText.textContent = 'Cannot select past date';
            otTypeDisplay.classList.add('d-none');
            if (otTypePlaceholder) otTypePlaceholder.classList.remove('d-none');
            return;
        }

        const otType = determineOTType(dateStr);
        if (otType) {
            otTypeBadge.textContent = otType.label;
            otTypeBadge.className = 'ot-type-badge ' + otType.badgeClass;
            otTypeBadge.title = otType.description; // Add tooltip
            otTypeMultiplier.textContent = otType.multiplier + 'x (' + (otType.multiplier * 100) + '%)';
            otTypeMultiplier.title = 'Pay rate: ' + (otType.multiplier * 100) + '% of regular hourly rate';
            otTypeDisplay.classList.remove('d-none');
            if (otTypePlaceholder) otTypePlaceholder.classList.add('d-none');
        }

        updateOTHours();
    });

    /**
     * Update OT hours when time changes
     * Requirement 1: Auto-calculate OT hours when user inputs time
     */
    function updateOTHours() {
        const startTime = startTimeInput.value;
        const endTime = endTimeInput.value;

        if (!startTime || !endTime) {
            otHoursDisplay.classList.remove('error');
            otHoursText.textContent = 'Enter start and end time';
            return;
        }

        // Validate time range (06:00 - 22:00)
        if (!validateTimeRange(startTime)) {
            otHoursDisplay.classList.add('error');
            otHoursText.textContent = 'Start time must be between 06:00 - 22:00';
            startTimeInput.setCustomValidity('Start time must be between 06:00 - 22:00');
            return;
        } else {
            startTimeInput.setCustomValidity('');
        }

        if (!validateTimeRange(endTime)) {
            otHoursDisplay.classList.add('error');
            otHoursText.textContent = 'End time must be between 06:00 - 22:00';
            endTimeInput.setCustomValidity('End time must be between 06:00 - 22:00');
            return;
        } else {
            endTimeInput.setCustomValidity('');
        }

        const hours = calculateOTHours(startTime, endTime);

        if (hours === null || hours <= 0) {
            otHoursDisplay.classList.add('error');
            otHoursText.textContent = 'End time must be after start time';
            return;
        }

        // Check daily limit based on OT type
        const dateStr = otDateInput.value;
        const otType = dateStr ? determineOTType(dateStr) : null;

        if (otType) {
            let maxHours;
            let limitMessage;

            if (otType.type === 'WEEKDAY') {
                // Weekday: max 2 hours OT (8h regular + 2h OT = 10h total)
                maxHours = 2;
                limitMessage = 'Weekday OT limit: 2 hours (8h regular + 2h OT = 10h total)';
            } else {
                // Weekend/Holiday: max 10 hours OT (no regular work)
                maxHours = 10;
                limitMessage = 'Weekend/Holiday OT limit: 10 hours';
            }

            console.log('updateOTHours validation:', {
                otType: otType.type,
                hours: hours,
                maxHours: maxHours,
                exceeds: hours > maxHours
            });

            if (hours > maxHours) {
                otHoursDisplay.classList.add('error');
                otHoursText.textContent = hours + ' hours (Exceeds ' + maxHours + 'h limit for ' + otType.label + ')';
                return;
            }
        }

        otHoursDisplay.classList.remove('error');
        otHoursText.textContent = hours + ' hours';
    }

    startTimeInput.addEventListener('change', updateOTHours);
    endTimeInput.addEventListener('change', updateOTHours);
    startTimeInput.addEventListener('input', updateOTHours);
    endTimeInput.addEventListener('input', updateOTHours);

    /**
     * Character counter for reason textarea
     * Requirement 1: Character counter for reason textarea
     */
    reasonTextarea.addEventListener('input', function() {
        const count = this.value.length;
        charCount.textContent = count;

        const counter = charCount.parentElement;
        counter.classList.remove('danger', 'warning');

        if (count > 900) {
            counter.classList.add('danger');
        } else if (count > 700) {
            counter.classList.add('warning');
        }
    });

    /**
     * Form validation on submit
     * Requirement 6: Client-side validation for consent checkbox
     */
    document.getElementById('otRequestForm').addEventListener('submit', function(e) {
        let isValid = true;

        // Check consent checkbox
        if (!employeeConsentCheckbox.checked) {
            e.preventDefault();
            alert('Please confirm your consent to work overtime');
            employeeConsentCheckbox.focus();
            isValid = false;
            return;
        }

        // Check time range
        const startTime = startTimeInput.value;
        const endTime = endTimeInput.value;

        if (startTime && !validateTimeRange(startTime)) {
            e.preventDefault();
            alert('Start time must be between 06:00 - 22:00');
            startTimeInput.focus();
            isValid = false;
            return;
        }

        if (endTime && !validateTimeRange(endTime)) {
            e.preventDefault();
            alert('End time must be between 06:00 - 22:00');
            endTimeInput.focus();
            isValid = false;
            return;
        }

        // Check OT hours
        const hours = calculateOTHours(startTime, endTime);
        if (hours === null || hours <= 0) {
            e.preventDefault();
            alert('End time must be after start time');
            endTimeInput.focus();
            isValid = false;
            return;
        }

        // Check daily limit based on OT type
        const dateStr = otDateInput.value;
        const otType = dateStr ? determineOTType(dateStr) : null;

        if (otType) {
            let maxHours;
            let limitMessage;

            if (otType.type === 'WEEKDAY') {
                // Weekday: max 2 hours OT
                maxHours = 2;
                limitMessage = 'Weekday OT cannot exceed 2 hours per day';
            } else {
                // Weekend/Holiday: max 10 hours OT
                maxHours = 10;
                limitMessage = 'Weekend/Holiday OT cannot exceed 10 hours per day';
            }

            if (hours > maxHours) {
                e.preventDefault();
                alert(limitMessage + '\nYou entered: ' + hours + ' hours');
                endTimeInput.focus();
                isValid = false;
                return;
            }
        }

        // Check date not in past
        if (dateStr) {
            const selectedDate = new Date(dateStr);
            const today = new Date();
            today.setHours(0, 0, 0, 0);

            if (selectedDate < today) {
                e.preventDefault();
                alert('Cannot select past date');
                otDateInput.focus();
                isValid = false;
                return;
            }
        }

        if (!this.checkValidity()) {
            e.preventDefault();
            e.stopPropagation();
            isValid = false;
        }

        this.classList.add('was-validated');

        if (isValid) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i> Submitting...';
        }
    });

    // Handle employee selection toggle
    const requestForSelfRadio = document.getElementById('requestForSelf');
    const requestForEmployeeRadio = document.getElementById('requestForEmployee');
    const employeeSelectionDiv = document.getElementById('employeeSelectionDiv');
    const selectedEmployeeIdSelect = document.getElementById('selectedEmployeeId');

    if (requestForSelfRadio && requestForEmployeeRadio) {
        requestForSelfRadio.addEventListener('change', function() {
            if (this.checked) {
                employeeSelectionDiv.classList.add('d-none');
                if (selectedEmployeeIdSelect) {
                    selectedEmployeeIdSelect.value = '';
                    selectedEmployeeIdSelect.removeAttribute('required');
                }
            }
        });

        requestForEmployeeRadio.addEventListener('change', function() {
            if (this.checked) {
                employeeSelectionDiv.classList.remove('d-none');
                if (selectedEmployeeIdSelect) {
                    selectedEmployeeIdSelect.setAttribute('required', 'required');
                }
            }
        });
    }

    // File upload preview functionality
    const attachmentsInput = document.getElementById('attachments');
    const filePreviewList = document.getElementById('filePreviewList');
    const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes

    if (attachmentsInput) {
        attachmentsInput.addEventListener('change', function(e) {
            filePreviewList.innerHTML = '';
            const files = Array.from(e.target.files);

            if (files.length === 0) {
                return;
            }

            files.forEach((file, index) => {
                const fileSize = file.size;
                const fileSizeKB = (fileSize / 1024).toFixed(1);
                const fileSizeMB = (fileSize / (1024 * 1024)).toFixed(2);
                const isOverSize = fileSize > MAX_FILE_SIZE;

                const fileItem = document.createElement('div');
                fileItem.className = 'alert py-2 px-3 mb-2 d-flex justify-content-between align-items-center ' +
                                    (isOverSize ? 'alert-danger' : 'alert-info');

                const fileInfo = document.createElement('div');
                fileInfo.innerHTML = '<i class="fas fa-file me-2"></i>' +
                                    '<strong>' + file.name + '</strong> ' +
                                    '<small class="text-muted">(' +
                                    (fileSizeKB < 1024 ? fileSizeKB + ' KB' : fileSizeMB + ' MB') +
                                    ')</small>';

                if (isOverSize) {
                    const errorMsg = document.createElement('small');
                    errorMsg.className = 'd-block text-danger mt-1';
                    errorMsg.innerHTML = '<i class="fas fa-exclamation-triangle"></i> File size exceeds 5MB limit';
                    fileInfo.appendChild(errorMsg);
                }

                fileItem.appendChild(fileInfo);
                filePreviewList.appendChild(fileItem);
            });

            // Check if any file exceeds size limit
            const hasOversizedFile = files.some(file => file.size > MAX_FILE_SIZE);
            if (hasOversizedFile) {
                // Disable submit button
                if (submitBtn) {
                    submitBtn.disabled = true;
                    submitBtn.title = 'Please remove files larger than 5MB';
                }
            } else {
                // Enable submit button (if not already disabled by form submission)
                if (submitBtn && !submitBtn.innerHTML.includes('Submitting')) {
                    submitBtn.disabled = false;
                    submitBtn.title = '';
                }
            }
        });
    }

    console.log('OT form initialized successfully');
});
