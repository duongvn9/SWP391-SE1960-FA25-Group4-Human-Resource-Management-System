<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <jsp:include page="../layout/head.jsp">
                <jsp:param name="pageTitle" value="Login" />
                <jsp:param name="pageCss" value="login.css" />
            </jsp:include>

            <!-- Google Fonts for Login -->
            <link rel="preconnect" href="https://fonts.googleapis.com">
            <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
            <link
                href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Roboto:wght@300;400;500&display=swap"
                rel="stylesheet">
        </head>

        <body class="login-page">
            <a href="${pageContext.request.contextPath}/" class="back-home">
                <i class="fas fa-arrow-left"></i>
                Back to Home
            </a>

            <div class="login-container">
                <div class="login-card">
                    <div class="login-header">
                        <div class="logo">
                            <i class="fas fa-users-cog"></i>
                        </div>
                        <h1>Login to HRMS</h1>
                        <p>Human Resource Management System</p>
                    </div>

                    <!-- Display error if any -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">
                            <i class="fas fa-exclamation-circle me-2"></i>${error}
                        </div>
                    </c:if>

                    <!-- Display session expired message -->
                    <c:if test="${param.sessionExpired == 'true'}">
                        <div class="alert alert-warning">
                            <i class="fas fa-clock me-2"></i>Your session has expired. Please login again.
                        </div>
                    </c:if>

                    <form method="post" action="${pageContext.request.contextPath}/login">
                        <!-- CSRF Token -->
                        <input type="hidden" name="_csrf_token" value="${csrfToken}">

                        <div class="form-group">
                            <label class="form-label" for="username">Username</label>
                            <div class="input-group">
                                <i class="fas fa-user input-group-icon"></i>
                                <input type="text" id="username" name="username" class="form-control"
                                    placeholder="Enter username" value="${param.username}" required>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="password">Password</label>
                            <div class="input-group password-input-group">
                                <i class="fas fa-lock input-group-icon"></i>
                                <input type="password" id="password" name="password" class="form-control"
                                    placeholder="Enter password" required>
                                <button type="button" class="password-toggle-btn" onclick="togglePassword()">
                                    <i class="fas fa-eye" id="password-eye"></i>
                                </button>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-login">
                            <i class="fas fa-sign-in-alt me-2"></i>Login
                        </button>
                    </form>

                    <!-- Divider -->
                    <div class="divider">
                        <span>or</span>
                    </div>

                    <!-- Google Login Button -->
                    <a href="${googleAuthUrl}" class="btn-google">
                        <svg class="google-icon" viewBox="0 0 24 24">
                            <path fill="#4285F4"
                                d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" />
                            <path fill="#34A853"
                                d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" />
                            <path fill="#FBBC05"
                                d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" />
                            <path fill="#EA4335"
                                d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" />
                        </svg>
                        Sign in with Google
                    </a>
                </div>
            </div>

            <!-- Login Page Scripts -->
            <script>
                // Auto focus to username field
                document.addEventListener('DOMContentLoaded', function () {
                    const usernameField = document.querySelector('input[name="username"]');
                    if (usernameField) {
                        usernameField.focus();
                    }
                });

                // Handle form submit with loading state
                document.querySelector('form').addEventListener('submit', function () {
                    const submitBtn = document.querySelector('.btn-login');
                    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Processing...';
                    submitBtn.disabled = true;
                });

                // Toggle password visibility
                function togglePassword() {
                    const passwordField = document.getElementById('password');
                    const passwordEye = document.getElementById('password-eye');

                    if (passwordField.type === 'password') {
                        passwordField.type = 'text';
                        passwordEye.classList.remove('fa-eye');
                        passwordEye.classList.add('fa-eye-slash');
                    } else {
                        passwordField.type = 'password';
                        passwordEye.classList.remove('fa-eye-slash');
                        passwordEye.classList.add('fa-eye');
                    }
                }

                // Enter key support for password toggle
                document.addEventListener('keydown', function (event) {
                    if (event.target.classList.contains('password-toggle-btn') && event.key === 'Enter') {
                        togglePassword();
                    }
                });
            </script>
        </body>

        </html>