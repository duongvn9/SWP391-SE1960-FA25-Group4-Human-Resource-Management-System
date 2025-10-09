<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="Change Password" />
    </jsp:include>
    <!-- Dashboard Layout CSS -->
    <link href="${pageContext.request.contextPath}/assets/css/common.css" rel="stylesheet">
    <!-- Profile Page CSS -->
    <link href="${pageContext.request.contextPath}/assets/css/profile.css" rel="stylesheet">
</head>
<body>
    <div class="dashboard-wrapper">
        <!-- Sidebar -->
        <jsp:include page="../layout/sidebar.jsp">
            <jsp:param name="currentPage" value="change-password" />
        </jsp:include>

        <!-- Main Content -->
        <div class="main-content">
            <!-- Header -->
            <jsp:include page="../layout/dashboard-header.jsp">
                <jsp:param name="pageTitle" value="Change Password" />
            </jsp:include>

            <!-- Page Content -->
            <div class="container-fluid py-4">
                <div class="row justify-content-center">
                    <div class="col-lg-8 col-xl-6">
                        <!-- Alert Messages -->
                        <c:if test="${not empty successMessage}">
                            <div class="alert alert-success fade-in" role="alert">
                                <i class="fas fa-check-circle me-2"></i>
                                ${successMessage}
                            </div>
                        </c:if>
                        
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger fade-in" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                ${errorMessage}
                            </div>
                        </c:if>

                        <!-- Change Password Card -->
                        <div class="card change-password-card fade-in">
                            <div class="card-header change-password-header">
                                <h4 class="mb-0">
                                    <i class="fas fa-key"></i>
                                    Change Password
                                </h4>
                            </div>
                            <div class="card-body password-form">
                                <form action="${pageContext.request.contextPath}/profile/change-password" 
                                      method="POST" id="changePasswordForm" novalidate>
                                    
                                    <!-- CSRF Token -->
                                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                                    
                                    <!-- Current Password -->
                                    <div class="form-floating">
                                        <input type="password" 
                                               class="form-control" 
                                               id="currentPassword" 
                                               name="currentPassword" 
                                               placeholder="Current Password"
                                               required>
                                        <label for="currentPassword">Current Password</label>
                                        <button type="button" class="password-toggle" onclick="togglePassword('currentPassword')">
                                            <i class="fas fa-eye" id="currentPasswordIcon"></i>
                                        </button>
                                        <div class="invalid-feedback"></div>
                                    </div>

                                    <!-- New Password -->
                                    <div class="form-floating">
                                        <input type="password" 
                                               class="form-control" 
                                               id="newPassword" 
                                               name="newPassword" 
                                               placeholder="New Password"
                                               required>
                                        <label for="newPassword">New Password</label>
                                        <button type="button" class="password-toggle" onclick="togglePassword('newPassword')">
                                            <i class="fas fa-eye" id="newPasswordIcon"></i>
                                        </button>
                                        <div class="invalid-feedback"></div>
                                    </div>

                                    <!-- Password Strength Indicator -->
                                    <div class="password-strength" id="passwordStrength" style="display: none;">
                                        <div class="strength-bar">
                                            <div class="strength-fill"></div>
                                        </div>
                                        <div class="strength-text"></div>
                                    </div>

                                    <!-- Password Requirements -->
                                    <div class="password-requirements">
                                        <h6><i class="fas fa-info-circle"></i> Password Requirements:</h6>
                                        <div class="requirement-item" id="lengthReq">
                                            <i class="fas fa-circle"></i>
                                            At least 8 characters
                                        </div>
                                        <div class="requirement-item" id="uppercaseReq">
                                            <i class="fas fa-circle"></i>
                                            At least 1 uppercase letter
                                        </div>
                                        <div class="requirement-item" id="lowercaseReq">
                                            <i class="fas fa-circle"></i>
                                            At least 1 lowercase letter
                                        </div>
                                        <div class="requirement-item" id="numberReq">
                                            <i class="fas fa-circle"></i>
                                            At least 1 number
                                        </div>
                                        <div class="requirement-item" id="specialReq">
                                            <i class="fas fa-circle"></i>
                                            At least 1 special character (!@#$%^&*)
                                        </div>
                                    </div>

                                    <!-- Confirm Password -->
                                    <div class="form-floating">
                                        <input type="password" 
                                               class="form-control" 
                                               id="confirmPassword" 
                                               name="confirmPassword" 
                                               placeholder="Confirm New Password"
                                               required>
                                        <label for="confirmPassword">Confirm New Password</label>
                                        <button type="button" class="password-toggle" onclick="togglePassword('confirmPassword')">
                                            <i class="fas fa-eye" id="confirmPasswordIcon"></i>
                                        </button>
                                        <div class="invalid-feedback"></div>
                                    </div>

                                    <!-- Form Buttons -->
                                    <div class="text-end mt-4">
                                        <a href="${pageContext.request.contextPath}/profile" 
                                           class="btn btn-cancel">
                                            <i class="fas fa-times me-1"></i>
                                            Cancel
                                        </a>
                                        <button type="submit" 
                                                class="btn btn-change-password" 
                                                id="submitBtn">
                                            <i class="fas fa-save me-1"></i>
                                            Change Password
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>

                        <!-- Security Tips -->
                        <div class="card mt-4">
                            <div class="card-body">
                                <h6 class="card-title">
                                    <i class="fas fa-shield-alt text-primary me-2"></i>
                                    Security Tips
                                </h6>
                                <ul class="mb-0 text-muted">
                                    <li>Use a strong and unique password for this account</li>
                                    <li>Never share your password with anyone</li>
                                    <li>Change your password regularly (every 3-6 months)</li>
                                    <li>Always log out after use, especially on shared computers</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Footer -->
            <jsp:include page="../layout/dashboard-footer.jsp" />
        </div>
    </div>

    <!-- Change Password JavaScript -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const form = document.getElementById('changePasswordForm');
            const newPasswordInput = document.getElementById('newPassword');
            const confirmPasswordInput = document.getElementById('confirmPassword');
            const currentPasswordInput = document.getElementById('currentPassword');
            const submitBtn = document.getElementById('submitBtn');
            const strengthIndicator = document.getElementById('passwordStrength');

            // Password validation requirements
            const requirements = {
                length: { element: document.getElementById('lengthReq'), regex: /.{8,}/, valid: false },
                uppercase: { element: document.getElementById('uppercaseReq'), regex: /[A-Z]/, valid: false },
                lowercase: { element: document.getElementById('lowercaseReq'), regex: /[a-z]/, valid: false },
                number: { element: document.getElementById('numberReq'), regex: /\d/, valid: false },
                special: { element: document.getElementById('specialReq'), regex: /[!@#$%^&*(),.?":{}|<>]/, valid: false }
            };

            // Real-time password validation
            newPasswordInput.addEventListener('input', function() {
                const password = this.value;
                validatePassword(password);
                updatePasswordStrength(password);
                validateConfirmPassword();
                updateSubmitButton();
            });

            confirmPasswordInput.addEventListener('input', function() {
                validateConfirmPassword();
                updateSubmitButton();
            });

            currentPasswordInput.addEventListener('input', function() {
                updateSubmitButton();
            });

            // Validate password requirements
            function validatePassword(password) {
                Object.keys(requirements).forEach(key => {
                    const req = requirements[key];
                    const isValid = req.regex.test(password);
                    req.valid = isValid;
                    
                    req.element.classList.toggle('valid', isValid);
                    req.element.classList.toggle('invalid', !isValid && password.length > 0);
                    
                    const icon = req.element.querySelector('i');
                    if (isValid) {
                        icon.className = 'fas fa-check';
                    } else if (password.length > 0) {
                        icon.className = 'fas fa-times';
                    } else {
                        icon.className = 'fas fa-circle';
                    }
                });
            }

            // Update password strength indicator
            function updatePasswordStrength(password) {
                if (password.length === 0) {
                    strengthIndicator.style.display = 'none';
                    return;
                }

                strengthIndicator.style.display = 'block';
                const validCount = Object.values(requirements).filter(req => req.valid).length;
                const strengthBar = strengthIndicator.querySelector('.strength-bar');
                const strengthText = strengthIndicator.querySelector('.strength-text');

                // Remove existing classes
                strengthBar.classList.remove('strength-weak', 'strength-fair', 'strength-good', 'strength-strong');

                if (validCount <= 2) {
                    strengthBar.classList.add('strength-weak');
                    strengthText.textContent = 'Weak';
                    strengthText.style.color = '#dc3545';
                } else if (validCount === 3) {
                    strengthBar.classList.add('strength-fair');
                    strengthText.textContent = 'Fair';
                    strengthText.style.color = '#fd7e14';
                } else if (validCount === 4) {
                    strengthBar.classList.add('strength-good');
                    strengthText.textContent = 'Good';
                    strengthText.style.color = '#ffc107';
                } else {
                    strengthBar.classList.add('strength-strong');
                    strengthText.textContent = 'Strong';
                    strengthText.style.color = '#28a745';
                }
            }

            // Validate confirm password
            function validateConfirmPassword() {
                const newPassword = newPasswordInput.value;
                const confirmPassword = confirmPasswordInput.value;
                const confirmField = confirmPasswordInput.closest('.form-floating');
                const feedback = confirmField.querySelector('.invalid-feedback');

                if (confirmPassword.length === 0) {
                    confirmPasswordInput.classList.remove('is-valid', 'is-invalid');
                    return;
                }

                if (newPassword === confirmPassword) {
                    confirmPasswordInput.classList.remove('is-invalid');
                    confirmPasswordInput.classList.add('is-valid');
                    feedback.textContent = '';
                } else {
                    confirmPasswordInput.classList.remove('is-valid');
                    confirmPasswordInput.classList.add('is-invalid');
                    feedback.textContent = 'Passwords do not match';
                }
            }

            // Update submit button state
            function updateSubmitButton() {
                const currentPassword = currentPasswordInput.value;
                const newPassword = newPasswordInput.value;
                const confirmPassword = confirmPasswordInput.value;
                const allRequirementsMet = Object.values(requirements).every(req => req.valid);
                const passwordsMatch = newPassword === confirmPassword && confirmPassword.length > 0;

                const isValid = currentPassword.length > 0 && allRequirementsMet && passwordsMatch;
                submitBtn.disabled = !isValid;
            }

            // Form submission
            form.addEventListener('submit', function(e) {
                e.preventDefault();
                
                if (submitBtn.disabled) return;

                // Show loading state
                submitBtn.classList.add('btn-loading');
                submitBtn.disabled = true;

                // Submit form
                setTimeout(() => {
                    this.submit();
                }, 500);
            });
        });

        // Toggle password visibility
        function togglePassword(inputId) {
            const input = document.getElementById(inputId);
            const icon = document.getElementById(inputId + 'Icon');
            
            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                input.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        }
    </script>
</body>
</html>