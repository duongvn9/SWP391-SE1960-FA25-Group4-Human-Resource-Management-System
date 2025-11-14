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

                    <div class="container-fluid px-4 py-3">
                        <!-- Alert Messages -->
                        <!-- Success Alert -->
                        <c:if test="${not empty success}">
                            <div class="alert alert-success alert-dismissible fade show mb-3" role="alert">
                                <i class="bi bi-check-circle me-2"></i>
                                <span>
                                    <c:out value="${success}" />
                                </span>
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>
                        <!-- Error Alert -->
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show mb-3" role="alert">
                                <i class="bi bi-exclamation-circle me-2"></i>
                                <span>
                                    <c:out value="${error}" />
                                </span>
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <div class="row g-4">
                            <!-- FORM SECTION -->
                            <div class="col-12">
                                <div class="card p-4">
                                    <div class="d-flex justify-content-between align-items-center mb-3">
                                        <h5 class="card-title mb-0">
                                            <i class="bi bi-person-plus-fill"></i>
                                            Create Recruitment Request
                                        </h5>
                                        <button type="button" class="btn btn-outline-primary btn-sm" id="toggleTipsBtn">
                                            <i class="bi bi-lightbulb"></i> <span id="tipsBtnText">Show Tips</span>
                                        </button>
                                    </div>
                                    
                                    <!-- TIPS SECTION (Collapsible) -->
                                    <div id="tipsSection" class="tips-section mb-4" style="display: none;">
                                        <div class="alert alert-info">
                                            <h6 class="mb-3 d-flex align-items-center fw-bold">
                                                <i class="bi bi-lightbulb-fill me-2"></i>
                                                Tips for Better Recruitment Request
                                            </h6>
                                            <ul class="tips-list mb-0">
                                                <li><i class="bi bi-check-circle-fill text-success me-2"></i> Clearly describe
                                                    the job responsibilities.</li>
                                                <li><i class="bi bi-check-circle-fill text-success me-2"></i> Double-check
                                                    required skills and experience.</li>
                                                <li><i class="bi bi-check-circle-fill text-success me-2"></i> Attach supporting
                                                    documents if available.</li>
                                                <li><i class="bi bi-check-circle-fill text-success me-2"></i> Provide an
                                                    accurate salary range.</li>
                                                <li><i class="bi bi-check-circle-fill text-success me-2"></i> Review all information before submitting.</li>
                                            </ul>
                                        </div>
                                    </div>

                                    <form id="recruitmentRequestForm"
                                        action="${pageContext.request.contextPath}/requests/recruitment/submit"
                                        method="post" enctype="multipart/form-data" novalidate>

                                        <input type="hidden" name="createdByAccountId"
                                            value="${sessionScope.userAccountId}" />
                                        <input type="hidden" name="createdByUserId" value="${sessionScope.userId}" />

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label class="form-label"><i class="bi bi-briefcase"></i><i
                                                        class="fas fa-briefcase"></i> Job Title <span
                                                        class="text-danger">*</span></label>
                                                <input type="text" name="jobTitle" id="jobTitle" class="form-control"
                                                    maxlength="100" required />
                                                <div class="invalid-feedback">
                                                    Job title is required (3-100 characters)
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label"><i class="bi bi-diagram-3"></i><i
                                                        class="fas fa-sitemap"></i> Position Name <span
                                                        class="text-danger">*</span></label>
                                                <input type="text" name="positionName" id="positionName"
                                                    class="form-control" placeholder="e.g. Software Engineer"
                                                    maxlength="100" required />
                                                <div class="invalid-feedback">
                                                    Position name is required (3-100 characters)
                                                </div>
                                            </div>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">
                                                <i class="bi bi-journal-text"></i><i class="fas fa-file-alt"></i> Job
                                                Summary <span class="text-danger">*</span>
                                            </label>
                                            <textarea name="jobSummary" id="jobSummary" class="form-control" rows="3"
                                                placeholder="Briefly describe what this job entails..." maxlength="1000"
                                                required></textarea>
                                            <div class="invalid-feedback">
                                                Job summary is required (10-1000 characters)
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-4">
                                                <label class="form-label"><i class="bi bi-people"></i><i
                                                        class="fas fa-users"></i> Quantity <span
                                                        class="text-danger">*</span></label>
                                                <input type="number" name="quantity" id="quantity" class="form-control"
                                                    min="1" max="100" required />
                                                <div class="invalid-feedback">
                                                    Quantity must be between 1 and 100
                                                </div>
                                            </div>
                                            <div class="col-md-4">
                                                <label class="form-label"><i class="bi bi-layers"></i><i
                                                        class="fas fa-layer-group"></i> Job Level <span
                                                        class="text-danger">*</span></label>
                                                <select name="jobLevel" id="jobLevel" class="form-select" required>
                                                    <option value="">Select Level</option>
                                                    <option value="JUNIOR">JUNIOR</option>
                                                    <option value="MIDDLE">MIDDLE</option>
                                                    <option value="SENIOR">SENIOR</option>
                                                </select>
                                                <div class="invalid-feedback">
                                                    Please select a job level
                                                </div>
                                            </div>
                                            <div class="col-md-4">
                                                <label class="form-label"><i class="bi bi-clock"></i><i
                                                        class="fas fa-clock"></i> Job Type <span
                                                        class="text-danger">*</span></label>
                                                <select name="jobType" id="jobType" class="form-select" required>
                                                    <option value="">Select Type</option>
                                                    <option value="Full-time">Full-time</option>
                                                    <option value="Part-time">Part-time</option>
                                                    <option value="Internship">Internship</option>
                                                </select>
                                                <div class="invalid-feedback">
                                                    Please select a job type
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Recruitment Reason - Full Width -->
                                        <div class="mb-3">
                                            <label class="form-label"><i class="bi bi-chat-text"></i><i
                                                    class="fas fa-comment"></i> Recruitment Reason</label>
                                            <textarea name="recruitmentReason" id="recruitmentReason"
                                                class="form-control auto-resize" placeholder="Enter recruitment reason"
                                                maxlength="500"></textarea>
                                            <div class="invalid-feedback">
                                                Recruitment reason cannot exceed 500 characters
                                            </div>
                                        </div>

                                        <!-- Salary Information Row -->
                                        <div class="row mb-3">
                                            <div class="col-md-4">
                                                <label class="form-label"><i class="bi bi-cash-stack"></i><i
                                                        class="fas fa-money-bill-wave"></i> Min Salary (VND)</label>
                                                <input type="text" name="minSalary" id="minSalary"
                                                    class="form-control" placeholder="5,000,000" />
                                                <div class="form-text">Min: 1,000,000 VND (use commas)</div>
                                                <div class="invalid-feedback">
                                                    Minimum salary must be at least 1,000,000 VND
                                                </div>
                                            </div>
                                            <div class="col-md-4">
                                                <label class="form-label"><i class="bi bi-cash"></i><i
                                                        class="fas fa-hand-holding-usd"></i> Max Salary (VND)</label>
                                                <input type="text" name="maxSalary" id="maxSalary"
                                                    class="form-control" placeholder="8,000,000" />
                                                <div class="form-text">Must be greater than min salary</div>
                                                <div class="invalid-feedback">
                                                    Maximum salary must be greater than minimum salary
                                                </div>
                                            </div>
                                            <div class="col-md-4">
                                                <label class="form-label"><i class="bi bi-coin"></i><i
                                                        class="fas fa-coins"></i> Salary Type</label>
                                                <select name="salaryType" id="salaryType" class="form-select">
                                                    <option value="Gross">Gross</option>
                                                    <option value="Net">Net</option>
                                                    <option value="Negotiable">Negotiable</option>
                                                </select>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label class="form-label"><i class="bi bi-geo-alt"></i><i
                                                    class="fas fa-map-marker-alt"></i> Working Location <span
                                                    class="text-danger">*</span></label>
                                            <input type="text" name="workingLocation" id="workingLocation" 
                                                   class="form-control" value="Ha Noi" readonly 
                                                   style="background-color: #f8f9fa; cursor: not-allowed;" />
                                        </div>

                                        <div class="mb-3">
                                            <label class="form-label">
                                                <i class="bi bi-paperclip"></i><i class="fas fa-paperclip"></i>
                                                Supporting Documents
                                                <span class="text-muted">(Optional)</span>
                                            </label>
                                            <div class="btn-group w-100 mb-3" role="group" aria-label="Attachment Type">
                                                <input type="radio" class="btn-check" name="attachmentType"
                                                    id="attachmentTypeFile" value="file" checked autocomplete="off">
                                                <label class="btn btn-outline-primary" for="attachmentTypeFile">
                                                    <i class="bi bi-upload me-1"></i><i class="fas fa-upload me-1"></i>
                                                    Upload File
                                                </label>

                                                <input type="radio" class="btn-check" name="attachmentType"
                                                    id="attachmentTypeLink" value="link" autocomplete="off">
                                                <label class="btn btn-outline-primary" for="attachmentTypeLink">
                                                    <i class="bi bi-link-45deg me-1"></i><i
                                                        class="fas fa-link me-1"></i> Google Drive Link
                                                </label>
                                            </div>

                                            <!-- File Upload Section -->
                                            <div id="fileUploadSection" class="file-upload-wrapper">
                                                <input type="file" class="form-control" id="attachments"
                                                    name="attachments" accept=".pdf,.jpg,.jpeg,.png,.doc,.docx,.txt,.xls,.xlsx,.ppt,.pptx" multiple>
                                                <div class="form-text">
                                                    <i class="bi bi-info-circle"></i>
                                                    Upload supporting documents: PDF, JPG, PNG, DOC, DOCX, TXT, XLS, XLSX, PPT, PPTX (Max 5MB each)
                                                </div>
                                                <div id="filePreviewList" class="file-preview-list mt-2"></div>
                                            </div>

                                            <!-- Google Drive Link Section -->
                                            <div id="driveLinkSection" class="drive-link-wrapper d-none">
                                                <input type="text" class="form-control" id="driveLink" name="driveLink"
                                                    placeholder="Paste Google Drive link here (e.g., https://drive.google.com/file/d/...) - Optional"
                                                    maxlength="500" />
                                                <div class="form-text">
                                                    <i class="bi bi-info-circle"></i>
                                                    Paste a shareable Google Drive link to your supporting document
                                                    (Optional - can be left empty)
                                                </div>
                                                <div id="driveLinkPreview" class="alert alert-info mt-2 d-none">
                                                    <i class="bi bi-link-45deg me-2"></i>
                                                    <strong>Drive Link:</strong> <span id="driveLinkText"></span>
                                                    <button type="button" class="btn-close float-end"
                                                        onclick="clearDriveLink()"></button>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-check mb-4">
                                            <input class="form-check-input" type="checkbox" id="confirmCheck" required>
                                            <label class="form-check-label" for="confirmCheck">
                                                I confirm that the above information is accurate.
                                            </label>
                                        </div>

                                        <div class="d-flex justify-content-end gap-2">
                                            <button type="button" class="btn btn-light"
                                                onclick="window.location='${pageContext.request.contextPath}/dashboard';">
                                                <i class="bi bi-x-lg"></i> Cancel
                                            </button>
                                            <button type="submit" class="btn btn-primary">
                                                <i class="bi bi-send-fill"></i> Submit Request
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
                    const attachmentTypeFile = document.getElementById('attachmentTypeFile');
                    const attachmentTypeLink = document.getElementById('attachmentTypeLink');
                    const fileUploadSection = document.getElementById('fileUploadSection');
                    const driveLinkSection = document.getElementById('driveLinkSection');
                    const attachmentsInput = document.getElementById('attachments');
                    const filePreviewList = document.getElementById('filePreviewList');
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
                    
                    // Character counter for textareas
                    function setupCharCounter(element) {
                        const maxLength = element.maxLength;
                        if (maxLength && maxLength > 0) {
                            const counter = document.createElement('div');
                            counter.className = 'char-counter';
                            counter.style.fontSize = '0.875rem';
                            counter.style.marginTop = '0.25rem';
                            counter.style.textAlign = 'right';
                            
                            function updateCounter() {
                                const length = element.value.length;
                                counter.textContent = length + '/' + maxLength + ' characters';
                                
                                if (length > maxLength * 0.9) {
                                    counter.style.color = '#dc3545';
                                    counter.style.fontWeight = 'bold';
                                } else if (length > maxLength * 0.75) {
                                    counter.style.color = '#ffc107';
                                    counter.style.fontWeight = '500';
                                } else {
                                    counter.style.color = '#6c757d';
                                    counter.style.fontWeight = 'normal';
                                }
                                
                                if (length > maxLength) {
                                    counter.textContent += ' (Over limit!)';
                                    counter.style.color = '#dc3545';
                                    counter.style.fontWeight = 'bold';
                                }
                            }
                            
                            element.parentNode.appendChild(counter);
                            element.addEventListener('input', updateCounter);
                            updateCounter();
                        }
                    }
                    
                    // Setup character counters
                    if (jobTitleInput) setupCharCounter(jobTitleInput);
                    if (positionNameInput) setupCharCounter(positionNameInput);
                    if (jobSummaryInput) setupCharCounter(jobSummaryInput);
                    if (recruitmentReasonInput) setupCharCounter(recruitmentReasonInput);
                    
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

                    function showFileSection() {
                        fileUploadSection.classList.remove('d-none');
                        driveLinkSection.classList.add('d-none');
                        // enable file input, disable link
                        attachmentsInput.disabled = false;
                        driveLink.disabled = true;
                    }

                    function showLinkSection() {
                        fileUploadSection.classList.add('d-none');
                        driveLinkSection.classList.remove('d-none');
                        attachmentsInput.disabled = true;
                        driveLink.disabled = false;
                        // Clear any validation messages
                        driveLink.setCustomValidity('');
                    }

                    // initial state
                    if (attachmentTypeLink.checked) {
                        showLinkSection();
                    } else {
                        showFileSection();
                    }

                    attachmentTypeFile.addEventListener('change', showFileSection);
                    attachmentTypeLink.addEventListener('change', showLinkSection);

                    // File preview with size validation
                    attachmentsInput.addEventListener('change', function (e) {
                        filePreviewList.innerHTML = '';
                        const files = Array.from(e.target.files || []);
                        const maxSize = 5 * 1024 * 1024; // 5MB in bytes
                        let hasInvalidFile = false;

                        files.forEach((file, idx) => {
                            const li = document.createElement('div');
                            const isOverSize = file.size > maxSize;

                            if (isOverSize) {
                                hasInvalidFile = true;
                                const fileSizeMB = (file.size / 1024 / 1024).toFixed(2);
                                li.className = 'd-flex align-items-center mb-1 p-2 border rounded bg-danger bg-opacity-10 border-danger';
                                li.innerHTML = '<div class="me-2"><i class="bi bi-file-earmark-fill fs-4 text-danger"></i></div>' +
                                    '<div class="flex-grow-1">' +
                                    file.name + ' ' +
                                    '<small class="text-danger">(' + fileSizeMB + ' MB - Exceeds 5MB limit!)</small>' +
                                    '</div>' +
                                    '<div><button type="button" class="btn btn-sm btn-outline-danger" data-idx="' + idx + '">Remove</button></div>';
                            } else {
                                const fileSizeKB = Math.round(file.size / 1024);
                                li.className = 'd-flex align-items-center mb-1 p-2 border rounded bg-light';
                                li.innerHTML = '<div class="me-2"><i class="bi bi-file-earmark-fill fs-4"></i></div>' +
                                    '<div class="flex-grow-1">' + file.name + ' <small class="text-muted">(' + fileSizeKB + ' KB)</small></div>' +
                                    '<div><button type="button" class="btn btn-sm btn-outline-danger" data-idx="' + idx + '">Remove</button></div>';
                            }
                            filePreviewList.appendChild(li);
                        });

                        if (hasInvalidFile) {
                            const warning = document.createElement('div');
                            warning.className = 'alert alert-danger alert-sm mt-2';
                            warning.innerHTML = '<i class="bi bi-exclamation-triangle me-2"></i>Some files exceed the 5MB size limit. Please remove them before submitting.';
                            filePreviewList.appendChild(warning);
                        }

                        // Remove handler
                        filePreviewList.querySelectorAll('button[data-idx]').forEach(btn => {
                            btn.addEventListener('click', function () {
                                const index = parseInt(this.getAttribute('data-idx'));
                                const dt = new DataTransfer();
                                const currentFiles = Array.from(attachmentsInput.files);
                                currentFiles.forEach((f, i) => { if (i !== index) dt.items.add(f); });
                                attachmentsInput.files = dt.files;
                                attachmentsInput.dispatchEvent(new Event('change'));
                            });
                        });
                    });

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
                        // Validate salary
                        if (!validateSalary()) {
                            e.preventDefault();
                            return false;
                        }

                        // Validate file sizes only if file upload is selected
                        if (attachmentTypeFile.checked) {
                            const files = Array.from(attachmentsInput.files || []);
                            const maxSize = 5 * 1024 * 1024; // 5MB
                            const oversizedFiles = files.filter(f => f.size > maxSize);

                            if (oversizedFiles.length > 0) {
                                e.preventDefault();
                                alert('Please remove files that exceed the 5MB size limit before submitting.');
                                return false;
                            }
                        }

                        // Validate Google Drive link format if provided
                        if (attachmentTypeLink.checked) {
                            const linkValue = driveLink.value.trim();
                            if (linkValue && !validateGoogleDriveLink(linkValue)) {
                                e.preventDefault();
                                alert('Invalid Google Drive link format. Please provide a valid shareable Google Drive link.');
                                driveLink.focus();
                                return false;
                            }
                            driveLink.setCustomValidity('');
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