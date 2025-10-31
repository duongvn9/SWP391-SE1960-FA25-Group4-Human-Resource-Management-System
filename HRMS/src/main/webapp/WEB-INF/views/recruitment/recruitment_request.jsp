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
                            <div class="col-lg-8">
                                <div class="card p-4">
                                    <h5 class="card-title mb-4">
                                        <i class="bi bi-person-plus-fill"></i>
                                        Create Recruitment Request
                                    </h5>

                                    <form id="recruitmentRequestForm"
                                        action="${pageContext.request.contextPath}/requests/recruitment/submit"
                                        method="post" enctype="multipart/form-data">

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
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label"><i class="bi bi-diagram-3"></i><i
                                                        class="fas fa-sitemap"></i> Position Name <span
                                                        class="text-danger">*</span></label>
                                                <input type="text" name="positionName" id="positionName"
                                                    class="form-control" placeholder="e.g. Software Engineer"
                                                    maxlength="100" required />
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
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-4">
                                                <label class="form-label"><i class="bi bi-people"></i><i
                                                        class="fas fa-users"></i> Quantity <span
                                                        class="text-danger">*</span></label>
                                                <input type="number" name="quantity" id="quantity" class="form-control"
                                                    min="1" max="100" required />
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
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label class="form-label"><i class="bi bi-chat-text"></i><i
                                                        class="fas fa-comment"></i> Recruitment Reason</label>
                                                <textarea name="recruitmentReason" id="recruitmentReason"
                                                    class="form-control" rows="2" placeholder="Enter recruitment reason"
                                                    maxlength="500"></textarea>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="row">
                                                    <div class="col-md-4">
                                                        <label class="form-label"><i class="bi bi-cash-stack"></i><i
                                                                class="fas fa-money-bill-wave"></i> Min Salary</label>
                                                        <input type="number" name="minSalary" id="minSalary"
                                                            class="form-control" placeholder="5000000" min="1000000"
                                                            step="100000" />
                                                    </div>
                                                    <div class="col-md-4">
                                                        <label class="form-label"><i class="bi bi-cash"></i><i
                                                                class="fas fa-hand-holding-usd"></i> Max Salary</label>
                                                        <input type="number" name="maxSalary" id="maxSalary"
                                                            class="form-control" placeholder="8000000" min="1000000"
                                                            step="100000" />
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
                            <!-- TIPS SECTION -->
                            <div class="col-lg-4">
                                <div class="card p-4 tips-card">
                                    <h6 class="mb-3 d-flex align-items-center text-primary fw-bold">
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
                                        <li><i class="bi bi-check-circle-fill text-success me-2"></i> Confirm all
                                            information before submitting.</li>
                                        <li><i class="bi bi-check-circle-fill text-success me-2"></i> Explicitly State
                                            the Recruitment Purpose: Clarify whether this is a new position (due to
                                            growth/new projects) or a replacement position.</li>
                                    </ul>
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
                        const minVal = parseFloat(minSalaryInput.value);
                        const maxVal = parseFloat(maxSalaryInput.value);

                        // Reset custom validity
                        minSalaryInput.setCustomValidity('');
                        maxSalaryInput.setCustomValidity('');

                        if (minVal && minVal < 1000000) {
                            minSalaryInput.setCustomValidity('Minimum salary must be at least 1,000,000 VND');
                            return false;
                        }

                        if (minVal && maxVal && maxVal < minVal) {
                            maxSalaryInput.setCustomValidity('Maximum salary must be greater than minimum salary');
                            return false;
                        }

                        return true;
                    }

                    minSalaryInput.addEventListener('input', validateSalary);
                    maxSalaryInput.addEventListener('input', validateSalary);

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