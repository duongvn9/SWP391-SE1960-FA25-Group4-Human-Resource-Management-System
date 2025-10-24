<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <jsp:include page="../layout/head.jsp">
                <jsp:param name="pageTitle" value="Create New User - HRMS" />
                <jsp:param name="pageCss" value="dashboard.css" />
            </jsp:include>
            <style>
                /* User Create Form Styles */
                /* Ensure main content doesn't overlap with sidebar */
                .main-content {
                    margin-left: 260px;
                    min-height: 100vh;
                    display: flex;
                    flex-direction: column;
                    transition: margin-left 0.3s ease;
                }

                body.sidebar-collapsed .main-content {
                    margin-left: 70px;
                }

                @media (max-width: 768px) {
                    .main-content {
                        margin-left: 0;
                    }
                }

                .content-area {
                    flex: 1 1 auto;
                    padding: 2rem;
                }

                .page-header {
                    background: #fff;
                    padding: 1.5rem;
                    border-radius: 10px;
                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
                    margin-bottom: 1.5rem;
                }

                .breadcrumb {
                    background: transparent;
                    padding: 0;
                    margin-bottom: 0.5rem;
                }

                .breadcrumb-item+.breadcrumb-item::before {
                    content: "›";
                    color: #6c757d;
                }

                .breadcrumb-item a {
                    color: #667eea;
                    text-decoration: none;
                }

                .breadcrumb-item.active {
                    color: #6c757d;
                }

                .form-card {
                    background: #fff;
                    border-radius: 10px;
                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
                    overflow: hidden;
                }

                .form-card .card-header {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: #fff;
                    padding: 1.5rem;
                    border: none;
                }

                .form-card .card-body {
                    padding: 2rem;
                }

                .form-group {
                    margin-bottom: 1.5rem;
                }

                .form-group label {
                    display: block;
                    margin-bottom: 0.5rem;
                    font-weight: 500;
                    color: #495057;
                }

                .form-group label .required {
                    color: #dc3545;
                    margin-left: 0.25rem;
                }

                .form-group input,
                .form-group select {
                    width: 100%;
                    padding: 0.75rem;
                    border: 1px solid #ced4da;
                    border-radius: 6px;
                    font-size: 0.95rem;
                    transition: border-color 0.2s ease;
                }

                .form-group input:focus,
                .form-group select:focus {
                    outline: none;
                    border-color: #667eea;
                    box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
                }

                .form-group .form-text {
                    display: block;
                    margin-top: 0.5rem;
                    font-size: 0.875rem;
                    color: #6c757d;
                }

                .form-group .invalid-feedback {
                    display: block;
                    margin-top: 0.5rem;
                    font-size: 0.875rem;
                    color: #dc3545;
                }

                .form-group input.is-invalid,
                .form-group select.is-invalid {
                    border-color: #dc3545;
                    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 12 12' width='12' height='12' fill='none' stroke='%23dc3545'%3e%3ccircle cx='6' cy='6' r='4.5'/%3e%3cpath d='m5.8 3.6h.4L6 6.5z'/%3e%3ccircle cx='6' cy='8.2' r='.6' fill='%23dc3545' stroke='none'/%3e%3c/svg%3e");
                    background-repeat: no-repeat;
                    background-position: right calc(0.375em + 0.1875rem) center;
                    background-size: calc(0.75em + 0.375rem) calc(0.75em + 0.375rem);
                    padding-right: calc(1.5em + 0.75rem);
                }

                .form-group input.is-valid,
                .form-group select.is-valid {
                    border-color: #28a745;
                    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 8 8'%3e%3cpath fill='%2328a745' d='m2.3 6.73.94-.94 1.38 1.38 3.02-3.02.94.94L2.97 8.84z'/%3e%3c/svg%3e");
                    background-repeat: no-repeat;
                    background-position: right calc(0.375em + 0.1875rem) center;
                    background-size: calc(0.75em + 0.375rem) calc(0.75em + 0.375rem);
                    padding-right: calc(1.5em + 0.75rem);
                }

                .form-group .valid-feedback {
                    display: block;
                    margin-top: 0.5rem;
                    font-size: 0.875rem;
                    color: #28a745;
                }

                .form-check {
                    margin-bottom: 1.5rem;
                }

                .form-check-input {
                    width: 1.25rem;
                    height: 1.25rem;
                    margin-top: 0.125rem;
                    cursor: pointer;
                }

                .form-check-label {
                    margin-left: 0.5rem;
                    font-weight: 500;
                    color: #495057;
                    cursor: pointer;
                }

                .form-actions {
                    display: flex;
                    gap: 1rem;
                    justify-content: flex-end;
                    padding-top: 1rem;
                    border-top: 1px solid #dee2e6;
                }

                .btn {
                    padding: 0.75rem 1.5rem;
                    border-radius: 6px;
                    font-size: 0.95rem;
                    font-weight: 500;
                    border: none;
                    cursor: pointer;
                    transition: all 0.2s ease;
                }

                .btn-primary {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: #fff;
                }

                .btn-primary:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
                }

                .btn-secondary {
                    background-color: #6c757d;
                    color: #fff;
                }

                .btn-secondary:hover {
                    background-color: #5a6268;
                }

                .alert {
                    padding: 1rem 1.25rem;
                    border-radius: 6px;
                    margin-bottom: 1.5rem;
                }

                .alert-danger {
                    background-color: #f8d7da;
                    border: 1px solid #f5c6cb;
                    color: #721c24;
                }

                .alert-danger .alert-heading {
                    font-weight: 600;
                    margin-bottom: 0.5rem;
                }

                .alert-danger ul {
                    margin: 0;
                    padding-left: 1.5rem;
                }

                .alert-danger li {
                    margin-bottom: 0.25rem;
                }

                .form-row {
                    display: grid;
                    grid-template-columns: repeat(2, 1fr);
                    gap: 1.5rem;
                }

                /* Responsive Design */
                @media (max-width: 768px) {
                    .form-row {
                        grid-template-columns: 1fr;
                    }

                    .form-card .card-body {
                        padding: 1.5rem;
                    }

                    .form-actions {
                        flex-direction: column;
                    }

                    .btn {
                        width: 100%;
                    }
                }

                /* Toast notification styles */
                .toast-container {
                    position: fixed;
                    top: 20px;
                    right: 20px;
                    z-index: 9999;
                }

                .toast {
                    background: #fff;
                    border-radius: 8px;
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
                    padding: 1rem 1.5rem;
                    margin-bottom: 1rem;
                    display: flex;
                    align-items: center;
                    gap: 1rem;
                    min-width: 300px;
                    animation: slideIn 0.3s ease;
                }

                .toast.success {
                    border-left: 4px solid #28a745;
                }

                .toast.error {
                    border-left: 4px solid #dc3545;
                }

                @keyframes slideIn {
                    from {
                        transform: translateX(100%);
                        opacity: 0;
                    }

                    to {
                        transform: translateX(0);
                        opacity: 1;
                    }
                }
            </style>
        </head>

        <body>
            <!-- Sidebar -->
            <jsp:include page="../layout/sidebar.jsp">
                <jsp:param name="currentPage" value="user-list" />
            </jsp:include>

            <!-- Main Content -->
            <div class="main-content" id="main-content">
                <!-- Header -->
                <jsp:include page="../layout/dashboard-header.jsp" />

                <!-- Content Area -->
                <div class="content-area">
                    <!-- Page Header -->
                    <div class="page-header">
                        <div class="d-flex justify-content-between align-items-center">
                            <h2 class="mb-0">Create New User</h2>
                            <a href="${pageContext.request.contextPath}/employees/users" class="btn btn-secondary">
                                <i class="fas fa-arrow-left me-2"></i>Back to User List
                            </a>
                        </div>
                    </div>

                    <!-- Error Summary -->
                    <c:if test="${not empty errors}">
                        <div class="alert alert-danger" role="alert">
                            <div class="alert-heading">
                                <i class="fas fa-exclamation-triangle me-2"></i>Please correct the following errors:
                            </div>
                            <ul>
                                <c:forEach var="error" items="${errors}">
                                    <li>${error}</li>
                                </c:forEach>
                            </ul>
                        </div>
                    </c:if>

                    <!-- User Create Form -->
                    <div class="form-card">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="fas fa-user-plus me-2"></i>User Information</h5>
                        </div>
                        <div class="card-body">
                            <form method="post" action="${pageContext.request.contextPath}/employees/users/create"
                                id="userCreateForm">

                                <!-- Employee Code (Optional) -->
                                <div class="form-group">
                                    <label for="employeeCode">Employee Code</label>
                                    <input type="text" id="employeeCode" name="employeeCode"
                                        class="form-control ${not empty errors and empty employeeCode ? 'is-invalid' : ''}"
                                        placeholder="Leave empty for auto-generation (HExxxx)" value="${employeeCode}"
                                        pattern="HE\d{4}" title="Format: HExxxx (e.g., HE0001)">
                                    <small class="form-text">Auto-generated if left empty (Format: HExxxx)</small>
                                </div>

                                <!-- Full Name (Required) -->
                                <div class="form-group">
                                    <label for="fullName">Full Name<span class="required">*</span></label>
                                    <input type="text" id="fullName" name="fullName"
                                        class="form-control ${not empty errors and empty fullName ? 'is-invalid' : ''}"
                                        placeholder="Enter full name" value="${fullName}" required maxlength="255">
                                </div>

                                <!-- Date of Birth and Gender (Row) -->
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="dateOfBirth">Date of Birth<span class="required">*</span></label>
                                        <input type="date" id="dateOfBirth" name="dateOfBirth"
                                            class="form-control ${not empty errors and empty dateOfBirth ? 'is-invalid' : ''}"
                                            value="${dateOfBirth}" required>
                                    </div>

                                    <div class="form-group">
                                        <label for="gender">Gender<span class="required">*</span></label>
                                        <select id="gender" name="gender"
                                            class="form-select ${not empty errors and empty gender ? 'is-invalid' : ''}"
                                            required>
                                            <option value="">Select Gender</option>
                                            <option value="male" ${gender=='male' ? 'selected' : '' }>Male</option>
                                            <option value="female" ${gender=='female' ? 'selected' : '' }>Female
                                            </option>
                                        </select>
                                    </div>
                                </div>

                                <!-- Phone (Full Width) -->
                                <div class="form-group">
                                    <label for="phone">Phone<span class="required">*</span></label>
                                    <input type="tel" id="phone" name="phone"
                                        class="form-control ${not empty errors and empty phone ? 'is-invalid' : ''}"
                                        placeholder="Enter phone number (10 digits starting with 0)" value="${phone}"
                                        required pattern="0[0-9]{9}"
                                        title="Phone number must be exactly 10 digits and start with 0">
                                </div>

                                <!-- Company Email (Required) -->
                                <div class="form-group">
                                    <label for="emailCompany">Company Email<span class="required">*</span></label>
                                    <input type="email" id="emailCompany" name="emailCompany"
                                        class="form-control ${not empty errors and empty emailCompany ? 'is-invalid' : ''}"
                                        placeholder="Enter company email" value="${emailCompany}" required>
                                </div>

                                <!-- Department and Position (Row) -->
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="departmentId">Department<span class="required">*</span></label>
                                        <select id="departmentId" name="departmentId"
                                            class="form-select ${not empty errors and empty selectedDepartment ? 'is-invalid' : ''}"
                                            required>
                                            <option value="">Select Department</option>
                                            <c:forEach var="dept" items="${departments}">
                                                <option value="${dept.id}" ${selectedDepartment==dept.id ? 'selected'
                                                    : '' }>
                                                    ${dept.name}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <div class="form-group">
                                        <label for="positionId">Position<span class="required">*</span></label>
                                        <select id="positionId" name="positionId"
                                            class="form-select ${not empty errors and empty selectedPosition ? 'is-invalid' : ''}"
                                            required>
                                            <option value="">Select Position</option>
                                            <c:forEach var="pos" items="${positions}">
                                                <option value="${pos.id}" ${selectedPosition==pos.id ? 'selected' : ''
                                                    }>
                                                    ${pos.name}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>

                                <!-- Date Joined and Start Work Date (Row) -->
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="dateJoined">Date Joined<span class="required">*</span></label>
                                        <input type="date" id="dateJoined" name="dateJoined"
                                            class="form-control ${not empty errors and empty dateJoined ? 'is-invalid' : ''}"
                                            value="${dateJoined}" required>
                                    </div>

                                    <div class="form-group">
                                        <label for="startWorkDate">Start Work Date<span
                                                class="required">*</span></label>
                                        <input type="date" id="startWorkDate" name="startWorkDate"
                                            class="form-control ${not empty errors and empty startWorkDate ? 'is-invalid' : ''}"
                                            value="${startWorkDate}" required>
                                    </div>
                                </div>

                                <!-- Form Actions -->
                                <div class="form-actions">
                                    <button type="button" class="btn btn-secondary" id="btnClearForm">
                                        <i class="fas fa-eraser me-2"></i>Clear Form
                                    </button>
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-save me-2"></i>Create User
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Footer -->
                <jsp:include page="../layout/dashboard-footer.jsp" />
            </div>

            <script>
                // Form validation
                document.addEventListener('DOMContentLoaded', function () {
                    const form = document.getElementById('userCreateForm');

                    // Set max date for Date of Birth to today
                    const dateOfBirthInput = document.getElementById('dateOfBirth');
                    const today = new Date().toISOString().split('T')[0];
                    dateOfBirthInput.setAttribute('max', today);

                    // Age validation function
                    function validateAge(dateOfBirth) {
                        if (!dateOfBirth) return false;

                        const today = new Date();
                        const birthDate = new Date(dateOfBirth);
                        let age = today.getFullYear() - birthDate.getFullYear();
                        const monthDiff = today.getMonth() - birthDate.getMonth();

                        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
                            age--;
                        }

                        return age >= 18;
                    }

                    // HTML5 validation is enabled by default with 'required' attributes
                    form.addEventListener('submit', function (e) {
                        // Age validation for Date of Birth
                        const dateOfBirth = dateOfBirthInput.value;
                        if (dateOfBirth) {
                            if (!validateAge(dateOfBirth)) {
                                e.preventDefault();
                                dateOfBirthInput.setCustomValidity('Date of birth must indicate user is at least 18 years old');
                                dateOfBirthInput.reportValidity();
                                return false;
                            } else {
                                dateOfBirthInput.setCustomValidity('');
                            }
                        }
                    });

                    // Real-time age validation on blur event
                    dateOfBirthInput.addEventListener('blur', function () {
                        const dateOfBirth = this.value;
                        if (dateOfBirth) {
                            const isValid = validateAge(dateOfBirth);
                            if (!isValid) {
                                this.setCustomValidity('Employee must be at least 18 years old');
                                this.reportValidity();
                            } else {
                                this.setCustomValidity('');
                            }
                            updateFieldValidation(this, isValid);
                        }
                    });

                    // Clear custom validity on input change
                    dateOfBirthInput.addEventListener('change', function () {
                        this.setCustomValidity('');
                        if (this.value) {
                            const isValid = validateAge(this.value);
                            updateFieldValidation(this, isValid);
                        }
                    });

                    // Phone validation function
                    function validatePhone(phone) {
                        if (!phone) return false;
                        const phoneRegex = /^0[0-9]{9}$/;
                        return phoneRegex.test(phone);
                    }

                    // Enhanced phone input handling
                    const phoneInput = document.getElementById('phone');
                    phoneInput.addEventListener('input', function (e) {
                        // Auto-remove non-numeric characters
                        this.value = this.value.replace(/[^0-9]/g, '');

                        // Real-time validation
                        const phone = this.value;
                        const isValid = phone === '' || validatePhone(phone);
                        if (phone && !isValid) {
                            this.setCustomValidity('Please enter a valid phone number (10 digits starting with 0)');
                        } else {
                            this.setCustomValidity('');
                        }
                        updateFieldValidation(this, isValid);
                    });

                    // Function to add visual validation indicators
                    function updateFieldValidation(field, isValid) {
                        field.classList.remove('is-valid', 'is-invalid');
                        if (field.value.trim() !== '') {
                            if (isValid) {
                                field.classList.add('is-valid');
                            } else {
                                field.classList.add('is-invalid');
                            }
                        }
                    }

                    // Custom validation messages for required fields
                    const fullNameInput = document.getElementById('fullName');
                    fullNameInput.addEventListener('invalid', function () {
                        if (this.validity.valueMissing) {
                            this.setCustomValidity('Please enter your full name');
                        }
                    });
                    fullNameInput.addEventListener('input', function () {
                        this.setCustomValidity('');
                        const isValid = this.value.trim().length > 0;
                        updateFieldValidation(this, isValid);
                    });

                    phoneInput.addEventListener('invalid', function () {
                        if (this.validity.valueMissing) {
                            this.setCustomValidity('Please enter your phone number');
                        } else if (this.validity.patternMismatch || !validatePhone(this.value)) {
                            this.setCustomValidity('Please enter a valid phone number (10 digits starting with 0)');
                        }
                    });

                    const emailInput = document.getElementById('emailCompany');
                    emailInput.addEventListener('invalid', function () {
                        if (this.validity.valueMissing) {
                            this.setCustomValidity('Please enter your company email');
                        } else if (this.validity.typeMismatch) {
                            this.setCustomValidity('Please enter a valid email address');
                        }
                    });
                    emailInput.addEventListener('input', function () {
                        this.setCustomValidity('');
                        const isValid = this.value === '' || this.validity.valid;
                        updateFieldValidation(this, isValid);
                    });

                    const departmentSelect = document.getElementById('departmentId');
                    departmentSelect.addEventListener('invalid', function () {
                        if (this.validity.valueMissing) {
                            this.setCustomValidity('Please select a department');
                        }
                    });
                    departmentSelect.addEventListener('change', function () {
                        this.setCustomValidity('');
                        const isValid = this.value !== '';
                        updateFieldValidation(this, isValid);
                    });

                    const positionSelect = document.getElementById('positionId');
                    positionSelect.addEventListener('invalid', function () {
                        if (this.validity.valueMissing) {
                            this.setCustomValidity('Please select a position');
                        }
                    });
                    positionSelect.addEventListener('change', function () {
                        this.setCustomValidity('');
                        const isValid = this.value !== '';
                        updateFieldValidation(this, isValid);
                    });

                    const dateJoinedInput = document.getElementById('dateJoined');
                    dateJoinedInput.addEventListener('invalid', function () {
                        if (this.validity.valueMissing) {
                            this.setCustomValidity('Please enter the date joined');
                        }
                    });
                    dateJoinedInput.addEventListener('input', function () {
                        this.setCustomValidity('');
                        const isValid = this.value !== '';
                        updateFieldValidation(this, isValid);
                    });

                    const startWorkDateInput = document.getElementById('startWorkDate');
                    startWorkDateInput.addEventListener('invalid', function () {
                        if (this.validity.valueMissing) {
                            this.setCustomValidity('Please enter the start work date');
                        }
                    });
                    startWorkDateInput.addEventListener('change', function () {
                        this.setCustomValidity('');
                        const isValid = this.value !== '';
                        updateFieldValidation(this, isValid);
                    });

                    const genderSelect = document.getElementById('gender');
                    genderSelect.addEventListener('invalid', function () {
                        if (this.validity.valueMissing) {
                            this.setCustomValidity('Please select a gender');
                        }
                    });
                    genderSelect.addEventListener('change', function () {
                        this.setCustomValidity('');
                        const isValid = this.value !== '';
                        updateFieldValidation(this, isValid);
                    });

                    // Clear form button functionality
                    document.getElementById('btnClearForm').addEventListener('click', function () {
                        // Reset the form
                        form.reset();

                        // Remove all validation classes
                        form.classList.remove('was-validated');
                        const inputs = form.querySelectorAll('.form-control, .form-select');
                        inputs.forEach(input => {
                            input.classList.remove('is-valid', 'is-invalid');
                            input.setCustomValidity('');
                        });

                        // Hide password strength indicator if exists
                        const strengthIndicator = document.getElementById('passwordStrengthIndicator');
                        if (strengthIndicator) {
                            strengthIndicator.style.display = 'none';
                        }

                        // Focus on first field
                        document.getElementById('fullName').focus();
                    });

                    startWorkDateInput.addEventListener('input', function () {
                        this.setCustomValidity('');
                        const isValid = this.value !== '';
                        updateFieldValidation(this, isValid);
                    });

                    const employeeCodeInput = document.getElementById('employeeCode');
                    employeeCodeInput.addEventListener('invalid', function () {
                        if (this.validity.patternMismatch) {
                            this.setCustomValidity('Please use the format HExxxx (e.g., HE0001)');
                        }
                    });
                    employeeCodeInput.addEventListener('input', function () {
                        this.setCustomValidity('');
                        const isValid = this.value === '' || this.validity.valid;
                        updateFieldValidation(this, isValid);
                    });
                });
            </script>
        </body>

        </html>