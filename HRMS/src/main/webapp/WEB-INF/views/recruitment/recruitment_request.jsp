<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>



        <!DOCTYPE html>
        <html lang="en">

        <head>
            <jsp:include page="../layout/head.jsp">
                <jsp:param name="pageTitle" value="Recruitment Request" />
            </jsp:include>

            <!-- External CSS -->
            <link href="${pageContext.request.contextPath}/assets/css/recruitment-request.css" rel="stylesheet">
        </head>

        <body>
            <div class="dashboard-wrapper">
                <jsp:include page="../layout/sidebar.jsp">
                    <jsp:param name="currentPage" value="recruitment-create" />
                </jsp:include>

                <div class="main-content">
                    <jsp:include page="../layout/dashboard-header.jsp">
                        <jsp:param name="pageTitle" value="Recruitment Request" />
                    </jsp:include>

                    <div class="container-fluid px-4 py-4">
                        <!-- Alert Messages -->
                        <!-- Success Alert -->
                        <c:if test="${not empty success}">
                            <div class="alert alert-success alert-dismissible fade show mb-4" role="alert">
                                <i class="bi bi-check-circle-fill me-2"></i>
                                <strong>Success!</strong>
                                <span>
                                    <c:out value="${success}" />
                                </span>
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>
                        <!-- Error Alert -->
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
                                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                                <strong>Error!</strong>
                                <span>
                                    <c:out value="${error}" />
                                </span>
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <div class="row g-4">
                            <!-- FORM SECTION -->
                            <div class="col-12">
                                <div class="card p-5">
                                    <div class="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom">
                                        <h5 class="card-title mb-0">
                                            <i class="bi bi-person-plus-fill"></i>
                                            Create Recruitment Request
                                        </h5>
                                        <button type="button" class="btn btn-outline-primary" id="toggleTipsBtn">
                                            <i class="bi bi-lightbulb-fill"></i> <span id="tipsBtnText">Show Tips</span>
                                        </button>
                                    </div>
                                    
                                    <!-- TIPS SECTION (Collapsible) -->
                                    <div id="tipsSection" class="tips-section mb-4" style="display: none;">
                                        <div class="alert">
                                            <h6 class="mb-3 d-flex align-items-center fw-bold">
                                                <i class="bi bi-lightbulb-fill me-2"></i>
                                                💡 Tips for Better Recruitment Request
                                            </h6>
                                            <ul class="tips-list mb-0">
                                                <li><i class="bi bi-check-circle-fill text-success me-2"></i> Clearly describe
                                                    the job responsibilities and expectations</li>
                                                <li><i class="bi bi-check-circle-fill text-success me-2"></i> Double-check
                                                    required skills and experience levels</li>
                                                <li><i class="bi bi-check-circle-fill text-success me-2"></i> Attach supporting
                                                    documents if available (job description, etc.)</li>
                                                <li><i class="bi bi-check-circle-fill text-success me-2"></i> Provide an
                                                    accurate and competitive salary range</li>
                                                <li><i class="bi bi-check-circle-fill text-success me-2"></i> Review all information carefully before submitting</li>
                                            </ul>
                                        </div>
                                    </div>

                                    <form id="recruitmentRequestForm"
                                        action="${pageContext.request.contextPath}/requests/recruitment/submit"
                                        method="post" novalidate>

                                        <input type="hidden" name="createdByAccountId"
                                            value="${sessionScope.userAccountId}" />
                                        <input type="hidden" name="createdByUserId" value="${sessionScope.userId}" />

                                        <!-- Basic Information Section -->
                                        <div class="section-header mt-3">
                                            <i class="bi bi-info-circle-fill"></i>
                                            <span>Basic Information</span>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-6 mb-3 mb-md-0">
                                                <label class="form-label fw-semibold">
                                                    <i class="bi bi-briefcase me-1"></i>Job Title <span class="text-danger">*</span>
                                                </label>
                                                <input type="text" name="jobTitle" id="jobTitle" 
                                                    class="form-control ${not empty errors.jobTitle ? 'is-invalid' : ''}"
                                                    maxlength="100" required 
                                                    value="<c:out value='${formData_jobTitle}'/>" 
                                                    placeholder="Enter job title" />
                                                <div class="invalid-feedback">
                                                    ${not empty errors.jobTitle ? errors.jobTitle : 'Job title is required (3-100 characters)'}
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label fw-semibold">
                                                    <i class="bi bi-diagram-3 me-1"></i>Position Name <span class="text-danger">*</span>
                                                </label>
                                                <input type="text" name="positionName" id="positionName"
                                                    class="form-control ${not empty errors.positionName ? 'is-invalid' : ''}" 
                                                    placeholder="e.g. Software Engineer"
                                                    maxlength="100" required 
                                                    value="<c:out value='${formData_positionName}'/>" />
                                                <div class="invalid-feedback">
                                                    ${not empty errors.positionName ? errors.positionName : 'Position name is required (3-100 characters)'}
                                                </div>
                                            </div>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold">
                                                <i class="bi bi-journal-text me-1"></i>Job Summary <span class="text-danger">*</span>
                                            </label>
                                            <textarea name="jobSummary" id="jobSummary" 
                                                class="form-control ${not empty errors.jobSummary ? 'is-invalid' : ''}" 
                                                rows="4"
                                                placeholder="Briefly describe what this job entails..." 
                                                maxlength="1000"
                                                required><c:out value="${formData_jobSummary}"/></textarea>
                                            <div class="invalid-feedback">
                                                ${not empty errors.jobSummary ? errors.jobSummary : 'Job summary is required (10-1000 characters)'}
                                            </div>
                                        </div>

                                        <!-- Position Details Section -->
                                        <div class="section-header mt-4">
                                            <i class="bi bi-sliders"></i>
                                            <span>Position Details</span>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-4 mb-3 mb-md-0">
                                                <label class="form-label fw-semibold">
                                                    <i class="bi bi-people me-1"></i>Quantity <span class="text-danger">*</span>
                                                </label>
                                                <input type="number" name="quantity" id="quantity" 
                                                    class="form-control ${not empty errors.quantity ? 'is-invalid' : ''}"
                                                    min="1" max="100" required 
                                                    placeholder="1-100"
                                                    value="<c:out value='${formData_quantity}'/>" />
                                                <div class="invalid-feedback">
                                                    ${not empty errors.quantity ? errors.quantity : 'Quantity must be between 1 and 100'}
                                                </div>
                                            </div>
                                            <div class="col-md-4 mb-3 mb-md-0">
                                                <label class="form-label fw-semibold">
                                                    <i class="bi bi-layers me-1"></i>Job Level <span class="text-danger">*</span>
                                                </label>
                                                <select name="jobLevel" id="jobLevel" 
                                                    class="form-select ${not empty errors.jobLevel ? 'is-invalid' : ''}" 
                                                    required>
                                                    <option value="">Select Level</option>
                                                    <option value="JUNIOR" ${formData_jobLevel == 'JUNIOR' ? 'selected' : ''}>Junior</option>
                                                    <option value="MIDDLE" ${formData_jobLevel == 'MIDDLE' ? 'selected' : ''}>Middle</option>
                                                    <option value="SENIOR" ${formData_jobLevel == 'SENIOR' ? 'selected' : ''}>Senior</option>
                                                </select>
                                                <div class="invalid-feedback">
                                                    ${not empty errors.jobLevel ? errors.jobLevel : 'Please select a job level'}
                                                </div>
                                            </div>
                                            <div class="col-md-4">
                                                <label class="form-label fw-semibold">
                                                    <i class="bi bi-clock me-1"></i>Job Type <span class="text-danger">*</span>
                                                </label>
                                                <select name="jobType" id="jobType" 
                                                    class="form-select ${not empty errors.jobType ? 'is-invalid' : ''}" 
                                                    required>
                                                    <option value="">Select Type</option>
                                                    <option value="Full-time" ${formData_jobType == 'Full-time' ? 'selected' : ''}>Full-time</option>
                                                    <option value="Part-time" ${formData_jobType == 'Part-time' ? 'selected' : ''}>Part-time</option>
                                                    <option value="Internship" ${formData_jobType == 'Internship' ? 'selected' : ''}>Internship</option>
                                                </select>
                                                <div class="invalid-feedback">
                                                    ${not empty errors.jobType ? errors.jobType : 'Please select a job type'}
                                                </div>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label class="form-label fw-semibold">
                                                <i class="bi bi-chat-text me-1"></i>Recruitment Reason <span class="text-muted">(Optional)</span>
                                            </label>
                                            <textarea name="recruitmentReason" id="recruitmentReason"
                                                class="form-control" 
                                                rows="3"
                                                placeholder="Enter recruitment reason (optional)"
                                                maxlength="500"><c:out value="${formData_recruitmentReason}"/></textarea>
                                            <div class="form-text text-muted">Max 500 characters</div>
                                        </div>

                                        <!-- Compensation Section -->
                                        <div class="section-header mt-4">
                                            <i class="bi bi-currency-dollar"></i>
                                            <span>Compensation & Benefits</span>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-4 mb-3 mb-md-0">
                                                <label class="form-label fw-semibold">
                                                    <i class="bi bi-cash-stack me-1"></i>Min Salary (VND) <span class="text-muted">(Optional)</span>
                                                </label>
                                                <input type="text" name="minSalary" id="minSalary"
                                                    class="form-control ${not empty errors.minSalary ? 'is-invalid' : ''}" 
                                                    placeholder="5,000,000" 
                                                    value="<c:out value='${formData_minSalary}'/>" />
                                                <div class="form-text text-muted small">Min: 1,000,000 VND</div>
                                                <div class="invalid-feedback">
                                                    ${not empty errors.minSalary ? errors.minSalary : 'Minimum salary must be at least 1,000,000 VND'}
                                                </div>
                                            </div>
                                            <div class="col-md-4 mb-3 mb-md-0">
                                                <label class="form-label fw-semibold">
                                                    <i class="bi bi-cash me-1"></i>Max Salary (VND) <span class="text-muted">(Optional)</span>
                                                </label>
                                                <input type="text" name="maxSalary" id="maxSalary"
                                                    class="form-control ${not empty errors.maxSalary ? 'is-invalid' : ''}" 
                                                    placeholder="15,000,000" 
                                                    value="<c:out value='${formData_maxSalary}'/>" />
                                                <div class="form-text text-muted small">Must be > min salary</div>
                                                <div class="invalid-feedback">
                                                    ${not empty errors.maxSalary ? errors.maxSalary : 'Maximum salary must be greater than minimum salary'}
                                                </div>
                                            </div>
                                            <div class="col-md-4">
                                                <label class="form-label fw-semibold">
                                                    <i class="bi bi-coin me-1"></i>Salary Type
                                                </label>
                                                <select name="salaryType" id="salaryType" 
                                                    class="form-select ${not empty errors.salaryType ? 'is-invalid' : ''}">
                                                    <option value="Gross" ${formData_salaryType == 'Gross' ? 'selected' : ''}>Gross</option>
                                                    <option value="Net" ${formData_salaryType == 'Net' ? 'selected' : ''}>Net</option>
                                                    <option value="Negotiable" ${formData_salaryType == 'Negotiable' ? 'selected' : ''}>Negotiable</option>
                                                </select>
                                                <div class="invalid-feedback">
                                                    ${not empty errors.salaryType ? errors.salaryType : 'Please select a salary type'}
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Additional Information Section -->
                                        <div class="section-header mt-4">
                                            <i class="bi bi-file-earmark-text"></i>
                                            <span>Additional Information</span>
                                        </div>

                                        <div class="mb-3">
                                            <label class="form-label fw-semibold">
                                                <i class="bi bi-geo-alt me-1"></i>Working Location <span class="text-danger">*</span>
                                            </label>
                                            <input type="text" name="workingLocation" id="workingLocation" 
                                                   class="form-control" value="Ha Noi" readonly 
                                                   style="background-color: #f8f9fa; cursor: not-allowed;" />
                                            <div class="form-text text-muted small">Default location</div>
                                        </div>

                                        <div class="mb-4">
                                            <label class="form-label fw-semibold">
                                                <i class="bi bi-link-45deg me-1"></i>Supporting Documents <span class="text-muted">(Optional)</span>
                                            </label>
                                            <input type="hidden" name="attachmentType" value="link">
                                            <input type="text" 
                                                class="form-control ${not empty errors.driveLink ? 'is-invalid' : ''}" 
                                                id="driveLink" name="driveLink"
                                                placeholder="https://drive.google.com/file/d/..."
                                                maxlength="500" 
                                                value="<c:out value='${formData_driveLink}'/>" />
                                            <div class="form-text text-muted small">
                                                <i class="bi bi-info-circle me-1"></i>Paste a shareable Google Drive link (optional)
                                            </div>
                                            <div class="invalid-feedback">
                                                ${not empty errors.driveLink ? errors.driveLink : 'Invalid Google Drive link format'}
                                            </div>
                                            <div id="driveLinkPreview" class="alert alert-info mt-2 d-none">
                                                <i class="bi bi-link-45deg me-2"></i>
                                                <strong>Link:</strong> <span id="driveLinkText"></span>
                                                <button type="button" class="btn-close float-end"
                                                    onclick="clearDriveLink()"></button>
                                            </div>
                                        </div>

                                        <div class="form-check mb-4">
                                            <input class="form-check-input" type="checkbox" id="confirmCheck" required>
                                            <label class="form-check-label" for="confirmCheck">
                                                <i class="bi bi-shield-check me-2"></i>
                                                I confirm that the above information is accurate and complete.
                                            </label>
                                            <div class="invalid-feedback">
                                                You must confirm that the information is accurate before submitting.
                                            </div>
                                        </div>

                                        <div class="d-flex justify-content-end gap-3 pt-3 border-top">
                                            <button type="button" class="btn btn-light"
                                                onclick="window.location='${pageContext.request.contextPath}/dashboard';">
                                                <i class="bi bi-x-circle me-2"></i> Cancel
                                            </button>
                                            <button type="submit" class="btn btn-primary">
                                                <i class="bi bi-send-fill me-2"></i> Submit Request
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>

                    <jsp:include page="../layout/dashboard-footer.jsp" />
                </div>
            </div>

            <script>
                document.addEventListener('DOMContentLoaded', function () {
                    const driveLink = document.getElementById('driveLink');
                    const driveLinkPreview = document.getElementById('driveLinkPreview');
                    const driveLinkText = document.getElementById('driveLinkText');
                    const minSalaryInput = document.getElementById('minSalary');
                    const maxSalaryInput = document.getElementById('maxSalary');
                    const form = document.getElementById('recruitmentRequestForm');
                    
                    // Form fields for validation
                    const jobTitleInput = document.getElementById('jobTitle');
                    const positionNameInput = document.getElementById('positionName');
                    const jobSummaryInput = document.getElementById('jobSummary');
                    const quantityInput = document.getElementById('quantity');
                    const jobLevelInput = document.getElementById('jobLevel');
                    const jobTypeInput = document.getElementById('jobType');
                    const recruitmentReasonInput = document.getElementById('recruitmentReason');
                    const salaryTypeInput = document.getElementById('salaryType');
                    
                    // Track which fields user has interacted with
                    const interactedFields = new Set();
                    
                    // Confirmation checkbox
                    const confirmCheck = document.getElementById('confirmCheck');
                    if (confirmCheck) {
                        confirmCheck.addEventListener('change', function() {
                            if (this.checked) {
                                this.classList.remove('is-invalid');
                            }
                        });
                    }

                    // Toggle Tips Section
                    const toggleTipsBtn = document.getElementById('toggleTipsBtn');
                    const tipsSection = document.getElementById('tipsSection');
                    const tipsBtnText = document.getElementById('tipsBtnText');
                    
                    toggleTipsBtn.addEventListener('click', function() {
                        if (tipsSection.style.display === 'none') {
                            tipsSection.style.display = 'block';
                            tipsBtnText.textContent = 'Hide Tips';
                        } else {
                            tipsSection.style.display = 'none';
                            tipsBtnText.textContent = 'Show Tips';
                        }
                    });

                    // ========== VALIDATION FUNCTIONS ==========
                    
                    // Number formatting functions
                    function formatNumber(num) {
                        if (!num) return '';
                        const intNum = Math.floor(parseFloat(num));
                        return intNum.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                    }
                    
                    function parseNumber(str) {
                        if (!str) return null;
                        const cleaned = str.replace(/,/g, '');
                        const parsed = parseInt(cleaned, 10);
                        return isNaN(parsed) ? null : parsed;
                    }
                    
                    // Validate text field
                    function validateTextField(element, minLength, maxLength, fieldName) {
                        if (!element) return true;
                        
                        const value = element.value.trim();
                        element.setCustomValidity('');
                        
                        if (element.hasAttribute('required') && value.length === 0) {
                            element.setCustomValidity(fieldName + ' is required');
                            return false;
                        }
                        
                        if (minLength > 0 && value.length > 0 && value.length < minLength) {
                            element.setCustomValidity(fieldName + ' must be at least ' + minLength + ' characters');
                            return false;
                        }
                        
                        if (maxLength > 0 && value.length > maxLength) {
                            element.setCustomValidity(fieldName + ' cannot exceed ' + maxLength + ' characters');
                            return false;
                        }
                        
                        return true;
                    }
                    
                    // Validate quantity
                    function validateQuantity() {
                        if (!quantityInput) return true;
                        
                        const quantity = parseInt(quantityInput.value);
                        quantityInput.setCustomValidity('');
                        
                        if (isNaN(quantity) || quantity < 1) {
                            quantityInput.setCustomValidity('Quantity must be at least 1');
                            return false;
                        }
                        
                        if (quantity > 100) {
                            quantityInput.setCustomValidity('Quantity cannot exceed 100');
                            return false;
                        }
                        
                        return true;
                    }
                    
                    // Validate dropdown
                    function validateDropdown(element, fieldName) {
                        if (!element) return true;
                        
                        const value = element.value.trim();
                        element.setCustomValidity('');
                        
                        if (element.hasAttribute('required') && value === '') {
                            element.setCustomValidity(fieldName + ' must be selected');
                            return false;
                        }
                        
                        return true;
                    }
                    
                    // Validate field on blur
                    function validateFieldOnBlur(field) {
                        if (!field || !interactedFields.has(field.id)) return;
                        
                        // Clear previous validation state
                        field.classList.remove('is-invalid', 'is-valid');
                        
                        let isValid = true;
                        
                        // Run custom validations based on field type
                        if (field.id === 'jobTitle') {
                            isValid = validateTextField(field, 3, 100, 'Job title');
                        } else if (field.id === 'positionName') {
                            isValid = validateTextField(field, 3, 100, 'Position name');
                        } else if (field.id === 'jobSummary') {
                            isValid = validateTextField(field, 10, 1000, 'Job summary');
                        } else if (field.id === 'recruitmentReason') {
                            isValid = validateTextField(field, 0, 500, 'Recruitment reason');
                        } else if (field.id === 'quantity') {
                            isValid = validateQuantity();
                        } else if (field.id === 'jobLevel' || field.id === 'jobType') {
                            isValid = validateDropdown(field, field.id === 'jobLevel' ? 'Job level' : 'Job type');
                        }
                        
                        // Check HTML5 validity after custom validation
                        if (isValid) {
                            isValid = field.checkValidity();
                        }
                        
                        // Only show invalid state if field is actually invalid
                        if (!isValid) {
                            field.classList.add('is-invalid');
                        }
                    }
                    
                    // Character counter removed
                    
                    // Add event listeners to track field interactions (exclude salary fields)
                    document.querySelectorAll('.form-control, .form-select').forEach(field => {
                        // Skip salary fields as they have special handling
                        if (field.id === 'minSalary' || field.id === 'maxSalary') {
                            return;
                        }
                        
                        field.addEventListener('focus', function() {
                            if (!interactedFields.has(this.id)) {
                                interactedFields.add(this.id);
                            }
                        });
                        
                        field.addEventListener('input', function() {
                            if (this.classList.contains('is-invalid')) {
                                this.classList.remove('is-invalid');
                            }
                        });
                        
                        field.addEventListener('blur', function() {
                            validateFieldOnBlur(this);
                        });
                        
                        field.addEventListener('change', function() {
                            if (this.tagName.toLowerCase() === 'select') {
                                validateFieldOnBlur(this);
                            }
                        });
                    });
                    
                    // Auto-resize textarea for recruitment reason
                    const autoResizeTextarea = document.getElementById('recruitmentReason');
                    
                    function resizeTextarea() {
                        autoResizeTextarea.style.height = 'auto';
                        autoResizeTextarea.style.height = autoResizeTextarea.scrollHeight + 'px';
                    }
                    
                    autoResizeTextarea.addEventListener('input', resizeTextarea);
                    resizeTextarea();

                    // Show preview if there's a saved drive link
                    const savedDriveLink = driveLink.value;
                    if (savedDriveLink && savedDriveLink.trim()) {
                        driveLinkText.textContent = savedDriveLink;
                        driveLinkPreview.classList.remove('d-none');
                        driveLinkPreview.classList.add('d-block');
                    }

                    // Salary validation
                    function validateSalary() {
                        const minVal = parseNumber(minSalaryInput.value);
                        const maxVal = parseNumber(maxSalaryInput.value);

                        // Reset custom validity
                        minSalaryInput.setCustomValidity('');
                        maxSalaryInput.setCustomValidity('');

                        // If both are empty, it's valid (optional fields)
                        if (!minVal && !maxVal) {
                            return true;
                        }

                        if (minVal && minVal < 1000000) {
                            minSalaryInput.setCustomValidity('Minimum salary must be at least 1,000,000 VND');
                            return false;
                        }

                        if (minVal && maxVal && maxVal <= minVal) {
                            maxSalaryInput.setCustomValidity('Maximum salary must be greater than minimum salary');
                            return false;
                        }

                        return true;
                    }

                    // Salary formatting and validation with special handling
                    if (minSalaryInput) {
                        // Track interaction
                        minSalaryInput.addEventListener('focus', function() {
                            if (!interactedFields.has(this.id)) {
                                interactedFields.add(this.id);
                            }
                        });
                        
                        // Only allow numbers and commas
                        minSalaryInput.addEventListener('input', function() {
                            // Remove invalid characters
                            this.value = this.value.replace(/[^0-9,]/g, '');
                            
                            // Clear invalid state while typing
                            if (this.classList.contains('is-invalid')) {
                                this.classList.remove('is-invalid');
                            }
                        });
                        
                        // Format and validate on blur
                        minSalaryInput.addEventListener('blur', function() {
                            const value = parseNumber(this.value);
                            if (value) {
                                this.value = formatNumber(value);
                            }
                            
                            // Validate if field has been interacted with
                            if (interactedFields.has(this.id)) {
                                const minVal = parseNumber(this.value);
                                const maxVal = parseNumber(maxSalaryInput.value);
                                
                                this.setCustomValidity('');
                                this.classList.remove('is-invalid');
                                
                                if (minVal && minVal < 1000000) {
                                    this.setCustomValidity('Minimum salary must be at least 1,000,000 VND');
                                    this.classList.add('is-invalid');
                                } else if (minVal && maxVal && minVal >= maxVal) {
                                    this.setCustomValidity('Minimum salary must be less than maximum salary');
                                    this.classList.add('is-invalid');
                                }
                            }
                        });
                    }
                    
                    if (maxSalaryInput) {
                        // Track interaction
                        maxSalaryInput.addEventListener('focus', function() {
                            if (!interactedFields.has(this.id)) {
                                interactedFields.add(this.id);
                            }
                        });
                        
                        // Only allow numbers and commas
                        maxSalaryInput.addEventListener('input', function() {
                            // Remove invalid characters
                            this.value = this.value.replace(/[^0-9,]/g, '');
                            
                            // Clear invalid state while typing
                            if (this.classList.contains('is-invalid')) {
                                this.classList.remove('is-invalid');
                            }
                        });
                        
                        // Format and validate on blur
                        maxSalaryInput.addEventListener('blur', function() {
                            const value = parseNumber(this.value);
                            if (value) {
                                this.value = formatNumber(value);
                            }
                            
                            // Validate if field has been interacted with
                            if (interactedFields.has(this.id)) {
                                const minVal = parseNumber(minSalaryInput.value);
                                const maxVal = parseNumber(this.value);
                                
                                this.setCustomValidity('');
                                this.classList.remove('is-invalid');
                                
                                if (maxVal && maxVal < 1000000) {
                                    this.setCustomValidity('Maximum salary must be at least 1,000,000 VND');
                                    this.classList.add('is-invalid');
                                } else if (minVal && maxVal && maxVal <= minVal) {
                                    this.setCustomValidity('Maximum salary must be greater than minimum salary');
                                    this.classList.add('is-invalid');
                                }
                            }
                        });
                    }

                    // Google Drive link validation
                    function validateGoogleDriveLink(link) {
                        if (!link || link.trim() === '') {
                            return true; // Empty is allowed
                        }
                        
                        try {
                            const url = new URL(link);
                            const host = url.hostname.toLowerCase();
                            
                            // Must be from Google Drive domains
                            if (host !== 'drive.google.com' && host !== 'docs.google.com') {
                                return false;
                            }
                            
                            // Check for common Google Drive URL patterns
                            const path = url.pathname;
                            return path.includes('/file/d/') || path.includes('/document/d/') || 
                                   path.includes('/spreadsheets/d/') || path.includes('/presentation/d/') ||
                                   path.includes('/folders/') || path.includes('/drive/folders/');
                                   
                        } catch (e) {
                            return false;
                        }
                    }

                    // Form validation before submit
                    form.addEventListener('submit', function (e) {
                        // Validate confirmation checkbox
                        const confirmCheck = document.getElementById('confirmCheck');
                        if (!confirmCheck.checked) {
                            e.preventDefault();
                            e.stopPropagation();
                            confirmCheck.classList.add('is-invalid');
                            // Scroll to checkbox
                            confirmCheck.scrollIntoView({ behavior: 'smooth', block: 'center' });
                            return false;
                        } else {
                            confirmCheck.classList.remove('is-invalid');
                        }
                        
                        // Validate salary
                        if (!validateSalary()) {
                            e.preventDefault();
                            return false;
                        }

                        // Validate Google Drive link format if provided
                        const linkValue = driveLink.value.trim();
                        if (linkValue && !validateGoogleDriveLink(linkValue)) {
                            e.preventDefault();
                            driveLink.classList.add('is-invalid');
                            driveLink.focus();
                            return false;
                        } else {
                            driveLink.classList.remove('is-invalid');
                        }
                    });

                    // Drive link preview and validation
                    driveLink.addEventListener('input', function () {
                        const v = this.value && this.value.trim();
                        if (v) {
                            // Show preview
                            driveLinkText.textContent = v;
                            driveLinkPreview.classList.remove('d-none');
                            driveLinkPreview.classList.add('d-block');
                            
                            // Validate format and show feedback
                            if (!validateGoogleDriveLink(v)) {
                                this.classList.add('is-invalid');
                                this.setCustomValidity('Invalid Google Drive link format');
                            } else {
                                this.classList.remove('is-invalid');
                                this.setCustomValidity('');
                            }
                        } else {
                            driveLinkPreview.classList.add('d-none');
                            this.classList.remove('is-invalid');
                            this.setCustomValidity('');
                        }
                    });

                    window.clearDriveLink = function () {
                        driveLink.value = '';
                        driveLinkPreview.classList.add('d-none');
                    };
                });
            </script>
        </body>

        </html>