<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Create New Account</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
            <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
            <link
                href="https://cdn.jsdelivr.net/npm/select2-bootstrap-5-theme@1.3.0/dist/select2-bootstrap-5-theme.min.css"
                rel="stylesheet" />
            <style>
                /* Fix layout to prevent overlap with sidebar */
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
                    background-color: #f8f9fa;
                }

                /* Page Header */
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

                /* Form Card */
                .form-card {
                    background: #fff;
                    border-radius: 10px;
                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
                    overflow: hidden;
                }

                .form-card .card-header {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: #fff;
                    padding: 1rem 1.5rem;
                    border: none;
                }

                .form-card .card-body {
                    padding: 2rem;
                }

                /* Form styling */
                .form-label {
                    display: block;
                    margin-bottom: 0.5rem;
                    font-weight: 500;
                    color: #495057;
                }

                .form-label.required::after {
                    content: " *";
                    color: #dc3545;
                }

                .form-control {
                    width: 100%;
                    padding: 0.75rem;
                    border: 1px solid #ced4da;
                    border-radius: 6px;
                    font-size: 0.95rem;
                    transition: border-color 0.2s ease;
                }

                .form-control:focus {
                    outline: none;
                    border-color: #667eea;
                    box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
                }

                .text-muted {
                    font-size: 0.875rem;
                    color: #6c757d;
                }

                .select2-user-option {
                    padding: 0.25rem 0;
                }

                /* Alert styling */
                .alert {
                    border-radius: 6px;
                    margin-bottom: 1.5rem;
                }

                /* Button styling */
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

                /* Responsive Design */
                @media (max-width: 768px) {
                    .form-card .card-body {
                        padding: 1.5rem;
                    }

                    .page-header .d-flex {
                        flex-direction: column;
                        gap: 1rem;
                        align-items: stretch !important;
                    }

                    .btn {
                        width: 100%;
                    }
                }
            </style>
        </head>

        <body>
            <!-- Include Sidebar -->
            <jsp:include page="../layout/sidebar.jsp">
                <jsp:param name="currentPage" value="account-create" />
            </jsp:include>

            <!-- Main Content -->
            <div class="main-content" id="main-content">
                <!-- Include Header -->
                <jsp:include page="../layout/dashboard-header.jsp" />

                <!-- Page Content -->
                <div class="content-area">
                    <!-- Page Header -->
                    <div class="page-header">
                        <div class="d-flex justify-content-between align-items-center">
                            <h2 class="mb-0">
                                <i class="fas fa-user-plus me-2"></i>Create New Account
                            </h2>
                            <a href="${pageContext.request.contextPath}/employees/accounts" class="btn btn-secondary">
                                <i class="fas fa-arrow-left me-2"></i>Back to Account List
                            </a>
                        </div>
                    </div>

                    <!-- Error/Success Messages -->
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            ${errorMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <c:if test="${not empty sessionScope.errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            ${sessionScope.errorMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <c:remove var="errorMessage" scope="session" />
                    </c:if>

                    <c:if test="${not empty sessionScope.successMessage}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>
                            ${sessionScope.successMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <c:remove var="successMessage" scope="session" />
                    </c:if>

                    <!-- Create Account Form -->
                    <div class="form-card">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="fas fa-user-plus me-2"></i>Account Information</h5>
                        </div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/employees/accounts/create" method="post"
                                id="createAccountForm" autocomplete="off">

                                <!-- Fake fields to trick browser autocomplete -->
                                <input type="text" name="fake_username"
                                    style="position:absolute;top:-9999px;left:-9999px;" tabindex="-1"
                                    autocomplete="off">
                                <input type="password" name="fake_password"
                                    style="position:absolute;top:-9999px;left:-9999px;" tabindex="-1"
                                    autocomplete="new-password">

                                <!-- User Selection -->
                                <div class="mb-3">
                                    <label for="userId" class="form-label required">
                                        <i class="fas fa-user me-1"></i>Select User
                                    </label>
                                    <select class="form-select" id="userId" name="userId" required>
                                        <option value="">-- Search and select user --</option>
                                        <c:forEach var="user" items="${users}">
                                            <option value="${user.id}" data-employee-code="${user.employeeCode}"
                                                data-email="${user.emailCompany}" ${(preSelectedUserId !=null &&
                                                preSelectedUserId==user.id) || (selectedUserId !=null &&
                                                selectedUserId==user.id.toString()) ? 'selected' : '' }>
                                                ${user.fullName} (${user.employeeCode})
                                                <c:if test="${not empty user.emailCompany}">
                                                    - ${user.emailCompany}
                                                </c:if>
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <small class="text-muted">Type to search by name, employee code, or email</small>
                                </div>

                                <!-- Username -->
                                <div class="mb-3">
                                    <label for="username" class="form-label required">
                                        <i class="fas fa-user-circle me-1"></i>Username
                                    </label>
                                    <input type="text" class="form-control" id="username" name="username" required
                                        minlength="3" maxlength="100" pattern="[a-zA-Z0-9._-]+"
                                        placeholder="Enter username" value="${username != null ? username : ''}"
                                        autocomplete="off" data-lpignore="true" data-form-type="other" readonly
                                        onfocus="this.removeAttribute('readonly');">
                                    <small class="text-muted">3-100 characters</small>
                                </div>

                                <div class="row">
                                    <!-- Company Email -->
                                    <div class="col-md-6 mb-3">
                                        <label for="emailCompany" class="form-label">
                                            <i class="fas fa-building me-1"></i>Company Email
                                        </label>
                                        <input type="email" class="form-control" id="emailCompany" readonly
                                            placeholder="Auto-filled from user">
                                    </div>

                                    <!-- Email Login -->
                                    <div class="col-md-6 mb-3">
                                        <label for="emailLogin" class="form-label">
                                            <i class="fas fa-envelope me-1"></i>Email Login (Optional)
                                        </label>
                                        <input type="email" class="form-control" id="emailLogin" name="emailLogin"
                                            maxlength="255" placeholder="Leave empty to use company email"
                                            value="${emailLogin != null ? emailLogin : ''}">
                                        <small class="text-muted">If empty, company email will be used</small>
                                    </div>
                                </div>



                                <div class="row">
                                    <!-- Password -->
                                    <div class="col-md-6 mb-3">
                                        <label for="password" class="form-label required">
                                            <i class="fas fa-lock me-1"></i>Password
                                        </label>
                                        <div class="position-relative">
                                            <input type="password" class="form-control" id="password" name="password"
                                                required maxlength="100" placeholder="Enter password"
                                                style="padding-right: 40px;" autocomplete="new-password">
                                            <i class="fas fa-eye position-absolute" id="togglePassword"
                                                style="top: 50%; right: 10px; transform: translateY(-50%); cursor: pointer; color: #6c757d;"></i>
                                        </div>

                                        <!-- Password Strength Indicator -->
                                        <div id="passwordStrengthIndicator" class="mt-2" style="display: none;">
                                            <div class="small mb-1">Password Requirements:</div>
                                            <div class="password-requirements">
                                                <div id="lengthReq" class="requirement-item">
                                                    <i class="fas fa-times text-danger me-1"></i>
                                                    <span>At least 6 characters</span>
                                                </div>
                                                <div id="uppercaseReq" class="requirement-item">
                                                    <i class="fas fa-times text-danger me-1"></i>
                                                    <span>At least one uppercase letter</span>
                                                </div>
                                                <div id="specialCharReq" class="requirement-item">
                                                    <i class="fas fa-times text-danger me-1"></i>
                                                    <span>At least one special character</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Confirm Password -->
                                    <div class="col-md-6 mb-3">
                                        <label for="confirmPassword" class="form-label required">
                                            <i class="fas fa-lock me-1"></i>Confirm Password
                                        </label>
                                        <div class="position-relative">
                                            <input type="password" class="form-control" id="confirmPassword"
                                                name="confirmPassword" required maxlength="100"
                                                placeholder="Re-enter password" style="padding-right: 40px;"
                                                autocomplete="new-password">
                                            <i class="fas fa-eye position-absolute" id="toggleConfirmPassword"
                                                style="top: 50%; right: 10px; transform: translateY(-50%); cursor: pointer; color: #6c757d;"></i>
                                        </div>
                                        <div class="invalid-feedback">Passwords do not match</div>
                                    </div>
                                </div>

                                <!-- Form Actions -->
                                <div class="d-flex justify-content-end gap-2 mt-4 pt-3"
                                    style="border-top: 1px solid #dee2e6;">
                                    <button type="button" class="btn btn-secondary" id="btnClearForm">
                                        <i class="fas fa-eraser me-2"></i>Clear Form
                                    </button>
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-save me-2"></i>Create Account
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Scripts -->
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
            <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
            <script>
                $(document).ready(function () {
                    // Declare usernameField once at the top
                    const usernameField = document.getElementById('username');
                    const form = document.getElementById('createAccountForm');

                    // Clear username if browser autocompletes it with unwanted value
                    setTimeout(function () {
                        const selectedUserId = $('#userId').val();

                        // Clear username if it's autocompleted or no user is selected
                        if (!selectedUserId || usernameField.value === 'admin') {
                            usernameField.value = '';
                        }
                    }, 50);

                    // Initialize Select2 with search functionality
                    $('#userId').select2({
                        theme: 'bootstrap-5',
                        placeholder: '-- Search and select user --',
                        allowClear: true,
                        width: '100%',
                        templateResult: formatUser,
                        templateSelection: formatUserSelection,
                        matcher: customMatcher
                    });

                    // Auto-fill if user is pre-selected or form data exists
                    const selectedUserId = $('#userId').val();
                    const hasFormUsername = usernameField.value.trim() !== '';

                    if (selectedUserId) {
                        const selectedOption = $('#userId option:selected');
                        const employeeCode = selectedOption.data('employee-code');
                        const email = selectedOption.data('email');

                        // Only auto-fill username if not already filled from session
                        if (employeeCode && !hasFormUsername) {
                            usernameField.value = employeeCode.toLowerCase();
                        }
                        if (email) {
                            document.getElementById('emailCompany').value = email;
                        }
                    } else if (!hasFormUsername) {
                        // Clear username if no user selected and no form data
                        usernameField.value = '';
                    }

                    // Custom format for dropdown options
                    function formatUser(user) {
                        if (!user.id) {
                            return user.text;
                        }

                        const $user = $(user.element);
                        const employeeCode = $user.data('employee-code');
                        const email = $user.data('email');

                        let html = '<div class="select2-user-option">';
                        html += '<div class="fw-bold">' + user.text.split('(')[0].trim() + '</div>';
                        html += '<div class="small text-muted">';
                        if (employeeCode) {
                            html += '<i class="fas fa-id-badge me-1"></i>' + employeeCode;
                        }
                        if (email) {
                            html += ' <i class="fas fa-envelope ms-2 me-1"></i>' + email;
                        }
                        html += '</div>';
                        html += '</div>';

                        return $(html);
                    }

                    // Format for selected option
                    function formatUserSelection(user) {
                        if (!user.id) {
                            return user.text;
                        }
                        return user.text.split('(')[0].trim() + ' (' + $(user.element).data('employee-code') + ')';
                    }

                    // Custom matcher for better search
                    function customMatcher(params, data) {
                        if ($.trim(params.term) === '') {
                            return data;
                        }

                        if (typeof data.text === 'undefined') {
                            return null;
                        }

                        const $option = $(data.element);
                        const searchTerm = params.term.toLowerCase();
                        const text = data.text.toLowerCase();
                        const employeeCode = ($option.data('employee-code') || '').toString().toLowerCase();
                        const email = ($option.data('email') || '').toString().toLowerCase();

                        if (text.indexOf(searchTerm) > -1 ||
                            employeeCode.indexOf(searchTerm) > -1 ||
                            email.indexOf(searchTerm) > -1) {
                            return data;
                        }

                        return null;
                    }

                    // Form validation
                    form.addEventListener('submit', function (event) {
                        if (!form.checkValidity()) {
                            event.preventDefault();
                            event.stopPropagation();
                        }
                        form.classList.add('was-validated');
                    }, false);

                    // Auto-fill username and email from user selection
                    $('#userId').on('select2:select', function (e) {
                        const selectedOption = e.params.data.element;
                        const $option = $(selectedOption);

                        if (selectedOption.value) {
                            const employeeCode = $option.data('employee-code');
                            const email = $option.data('email');

                            if (employeeCode) {
                                usernameField.value = employeeCode.toLowerCase();
                                // Trigger change event to ensure value is set
                                usernameField.dispatchEvent(new Event('change'));
                            }

                            if (email) {
                                document.getElementById('emailCompany').value = email;
                            }
                        }
                    });

                    // Clear username when user selection is cleared
                    $('#userId').on('select2:clear', function () {
                        document.getElementById('username').value = '';
                        document.getElementById('emailCompany').value = '';
                    });

                    // Password strength validation function
                    function validatePasswordStrength(password) {
                        const requirements = {
                            minLength: password.length >= 6,
                            hasUpperCase: /[A-Z]/.test(password),
                            hasSpecialChar: /[!@#$%^&*(),.?":{}|<>]/.test(password)
                        };

                        return {
                            isValid: requirements.minLength && requirements.hasUpperCase && requirements.hasSpecialChar,
                            requirements: requirements
                        };
                    }

                    // Password validation
                    const passwordInput = document.getElementById('password');
                    const confirmPasswordInput = document.getElementById('confirmPassword');
                    const strengthIndicator = document.getElementById('passwordStrengthIndicator');
                    const lengthReq = document.getElementById('lengthReq');
                    const uppercaseReq = document.getElementById('uppercaseReq');
                    const specialCharReq = document.getElementById('specialCharReq');

                    // Update password strength indicator
                    function updatePasswordStrengthIndicator(password) {
                        if (password.length === 0) {
                            strengthIndicator.style.display = 'none';
                            return;
                        }

                        strengthIndicator.style.display = 'block';
                        const validation = validatePasswordStrength(password);

                        // Update length requirement
                        updateRequirementItem(lengthReq, validation.requirements.minLength);

                        // Update uppercase requirement
                        updateRequirementItem(uppercaseReq, validation.requirements.hasUpperCase);

                        // Update special character requirement
                        updateRequirementItem(specialCharReq, validation.requirements.hasSpecialChar);

                        // Set custom validity for form validation
                        if (!validation.isValid) {
                            const unmetRequirements = [];
                            if (!validation.requirements.minLength) unmetRequirements.push('at least 6 characters');
                            if (!validation.requirements.hasUpperCase) unmetRequirements.push('at least one uppercase letter');
                            if (!validation.requirements.hasSpecialChar) unmetRequirements.push('at least one special character');

                            passwordInput.setCustomValidity('Password must contain: ' + unmetRequirements.join(', '));
                        } else {
                            passwordInput.setCustomValidity('');
                        }
                    }

                    function updateRequirementItem(element, isMet) {
                        const icon = element.querySelector('i');
                        if (isMet) {
                            element.classList.remove('unmet');
                            element.classList.add('met');
                            icon.classList.remove('fa-times', 'text-danger');
                            icon.classList.add('fa-check', 'text-success');
                        } else {
                            element.classList.remove('met');
                            element.classList.add('unmet');
                            icon.classList.remove('fa-check', 'text-success');
                            icon.classList.add('fa-times', 'text-danger');
                        }
                    }

                    function validatePassword() {
                        if (passwordInput.value !== confirmPasswordInput.value) {
                            confirmPasswordInput.setCustomValidity('Passwords do not match');
                        } else {
                            confirmPasswordInput.setCustomValidity('');
                        }
                    }

                    // Real-time password validation
                    passwordInput.addEventListener('keyup', function () {
                        updatePasswordStrengthIndicator(this.value);
                    });

                    passwordInput.addEventListener('change', validatePassword);
                    confirmPasswordInput.addEventListener('keyup', validatePassword);

                    // Toggle password visibility
                    const togglePassword = document.getElementById('togglePassword');
                    const toggleConfirmPassword = document.getElementById('toggleConfirmPassword');

                    togglePassword.addEventListener('click', function () {
                        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
                        passwordInput.setAttribute('type', type);
                        this.classList.toggle('fa-eye');
                        this.classList.toggle('fa-eye-slash');
                    });

                    toggleConfirmPassword.addEventListener('click', function () {
                        const type = confirmPasswordInput.getAttribute('type') === 'password' ? 'text' : 'password';
                        confirmPasswordInput.setAttribute('type', type);
                        this.classList.toggle('fa-eye');
                        this.classList.toggle('fa-eye-slash');
                    });

                    // Prevent form submission if password is invalid
                    form.addEventListener('submit', function (event) {
                        const passwordValidation = validatePasswordStrength(passwordInput.value);
                        if (!passwordValidation.isValid) {
                            event.preventDefault();
                            event.stopPropagation();
                            passwordInput.focus();
                        }
                    });

                    // Clear form button functionality
                    document.getElementById('btnClearForm').addEventListener('click', function () {
                        // Reset the form
                        form.reset();

                        // Clear Select2
                        $('#userId').val(null).trigger('change');

                        // Clear other fields
                        document.getElementById('username').value = '';
                        document.getElementById('emailCompany').value = '';
                        document.getElementById('emailLogin').value = '';
                        document.getElementById('password').value = '';
                        document.getElementById('confirmPassword').value = '';

                        // Hide password strength indicator
                        strengthIndicator.style.display = 'none';

                        // Remove validation classes
                        form.classList.remove('was-validated');

                        // Reset custom validity
                        passwordInput.setCustomValidity('');
                        confirmPasswordInput.setCustomValidity('');

                        // Focus on first field
                        $('#userId').select2('open');
                    });
                });
            </script>
            <style>
                .select2-user-option {
                    padding: 5px 0;
                }

                .select2-container--bootstrap-5 .select2-selection {
                    min-height: 38px;
                }

                .select2-container--bootstrap-5 .select2-dropdown {
                    border-color: #dee2e6;
                }

                /* Password Strength Indicator Styles */
                .password-requirements {
                    background-color: #f8f9fa;
                    border: 1px solid #dee2e6;
                    border-radius: 4px;
                    padding: 0.75rem;
                }

                .requirement-item {
                    font-size: 0.875rem;
                    margin-bottom: 0.25rem;
                    transition: color 0.2s ease;
                }

                .requirement-item:last-child {
                    margin-bottom: 0;
                }

                .requirement-item.met {
                    color: #28a745;
                }

                .requirement-item.met i {
                    color: #28a745 !important;
                }

                .text-success {
                    color: #28a745 !important;
                }

                .requirement-item.unmet {
                    color: #dc3545;
                }

                .requirement-item.unmet i {
                    color: #dc3545;
                }
            </style>
        </body>

        </html>