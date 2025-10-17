<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <jsp:include page="../layout/head.jsp">
            <jsp:param name="pageTitle" value="Update Profile - HRMS" />
            <jsp:param name="pageCss" value="dashboard.css" />
        </jsp:include>
        <style>
            .main-content {
                margin-left: 260px;
                padding: 2rem;
            }
            .profile-container {
                max-width: 100%;
                background: white;
                padding: 30px 50px;
                border-radius: 8px;
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
            }
            @media (max-width: 768px) {
                .main-content {
                    margin-left: 0;
                }
            }
            .profile-header {
                text-align: center;
                margin-bottom: 20px;
                padding-bottom: 15px;
                border-bottom: 1px solid #dee2e6;
            }
            .profile-header h2 {
                color: #333;
                font-weight: 600;
                font-size: 1.5rem;
                margin: 0;
            }
            .profile-description {
                background-color: #f8f9fa;
                padding: 12px 15px;
                font-size: 0.9rem;
                color: #666;
                margin-bottom: 25px;
                font-style: italic;
            }
            .form-row {
                display: flex;
                align-items: center;
                margin-bottom: 15px;
                min-height: 40px;
            }
            .form-row.two-columns {
                display: flex;
                align-items: center;
                margin-bottom: 15px;
                min-height: 40px;
            }
            .form-row.two-columns .form-label {
                width: 200px;
            }
            .form-row.two-columns .form-control:first-of-type {
                flex: 0 0 calc(50% - 150px);
                margin-right: 15px;
            }
            .form-row.two-columns .label-inline {
                flex: 0 0 120px;
                margin-right: 15px;
            }
            .form-row.two-columns .form-control:last-of-type {
                flex: 1;
            }
            .form-label {
                width: 200px;
                font-weight: 500;
                color: #333;
                margin: 0;
                padding-right: 20px;
                text-align: left;
                flex-shrink: 0;
            }
            .form-input-wrapper {
                flex: 1;
                display: flex;
                gap: 15px;
                align-items: center;
            }
            .form-control, .form-select {
                flex: 1;
                padding: 8px 12px;
                border: 1px solid #ced4da;
                border-radius: 4px;
                font-size: 0.95rem;
            }
            .form-control:focus, .form-select:focus {
                border-color: #80bdff;
                outline: 0;
                box-shadow: 0 0 0 0.2rem rgba(0,123,255,.25);
            }
            .radio-group {
                display: flex;
                gap: 20px;
                align-items: center;
            }
            .form-check-inline {
                margin: 0;
            }
            .btn-save {
                padding: 10px 40px;
                font-size: 1rem;
                font-weight: 500;
                margin-top: 20px;
            }
            .back-link {
                display: inline-block;
                margin-bottom: 15px;
                color: #007bff;
                text-decoration: none;
                font-size: 0.95rem;
            }
            .back-link:hover {
                text-decoration: underline;
            }
            .text-danger {
                color: #dc3545;
            }
            .label-inline {
                text-align: right;
                font-weight: 500;
                color: #333;
                white-space: nowrap;
            }
        </style>
    </head>

    <body>
        <jsp:include page="../layout/sidebar.jsp">
            <jsp:param name="currentPage" value="profile" />
        </jsp:include>

        <div class="main-content" id="main-content">
            <!-- Header -->
            <jsp:include page="../layout/dashboard-header.jsp" />

            <!-- Content Area -->
            <div class="content-area">
                <div class="profile-container">
                    <!-- Back to View Profile Link -->
                <a href="${pageContext.request.contextPath}/user-profile" class="back-link">
                    <i class="fa-solid fa-arrow-left me-2"></i>Back to View Profile
                </a>

                <!-- Profile Header -->
            <div class="profile-header">
                <h2>Update Profile</h2>
            </div>

            <!-- Description -->
            <div class="profile-description">
                This information is important and confidential. It is used by the company for official records, 
                certificates, and administrative purposes. Please ensure all information is accurate and up-to-date.
            </div>

            <!-- Success/Error Messages -->
            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fa-solid fa-circle-check me-2"></i>${sessionScope.successMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <c:remove var="successMessage" scope="session"/>
            </c:if>

            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fa-solid fa-circle-exclamation me-2"></i>${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Profile Form (Editable fields only) -->
            <form method="post" action="${pageContext.request.contextPath}/user-profile/update">
                <!-- CSRF Token -->
                <input type="hidden" name="_csrf_token" value="${csrfToken}">
                
                <!-- Hidden field for Company Email (not editable by user) -->
                <input type="hidden" name="emailCompany" value="${profile.emailCompany}">

                <!-- Full Name -->
                <div class="form-row">
                    <label class="form-label">Full Name: </label>
                    <div class="form-input-wrapper">
                        <input type="text" name="fullName" class="form-control" value="${profile.fullName}" 
                               maxlength="100" pattern="^[a-zA-ZÀ-ỹ\s]+$"
                               title="Full name can only contain letters and spaces (no numbers or special characters)">
                    </div>
                </div>

                <!-- Phone -->
                <div class="form-row">
                    <label class="form-label">Phone Number: </label>
                    <div class="form-input-wrapper">
                        <input type="text" name="phone" class="form-control" value="${profile.phone}" 
                               pattern="^[0-9]{10,11}$" placeholder="10-11 digits"
                               title="Phone number must be 10-11 digits">
                    </div>
                </div>

                <!-- Date of Birth & Hometown -->
                <div class="form-row two-columns">
                    <label class="form-label">Date of Birth: </label>
                    <input type="date" name="dob" class="form-control" value="${profile.dob}" 
                           min="1900-01-01" max="${java.time.LocalDate.now()}">
                    <label class="label-inline">Hometown:</label>
                    <input type="text" name="hometown" class="form-control" value="${profile.hometown}" maxlength="50">
                </div>

                <!-- Gender -->
                <div class="form-row">
                    <label class="form-label">Gender: </label>
                    <div class="form-input-wrapper">
                        <div class="radio-group">
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="gender" id="genderMale" value="male" 
                                       ${fn:toLowerCase(profile.gender) == 'male' ? 'checked' : ''}>
                                <label class="form-check-label" for="genderMale">Male</label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="gender" id="genderFemale" value="female" 
                                       ${fn:toLowerCase(profile.gender) == 'female' ? 'checked' : ''}>
                                <label class="form-check-label" for="genderFemale">Female</label>
                            </div>
                         
                        </div>
                    </div>
                </div>

                <!-- Citizen ID (CCCD) -->
                <div class="form-row">
                    <label class="form-label">Citizen ID (CCCD): </label>
                    <div class="form-input-wrapper">
                        <input type="text" name="cccd" class="form-control" value="${profile.cccd}" 
                               pattern="^[0-9]{12}$" placeholder="12 digits"
                               title="CCCD must be exactly 12 digits">
                    </div>
                </div>

                <!-- CCCD Issued Date & Place -->
                <div class="form-row two-columns">
                    <label class="form-label">CCCD Issued Date: </label>
                    <input type="date" name="cccdIssuedDate" class="form-control" value="${profile.cccdIssuedDate}" 
                           max="${java.time.LocalDate.now()}">
                    <label class="label-inline">Issued Place: </label>
                    <input type="text" name="cccdIssuedPlace" class="form-control" value="${profile.cccdIssuedPlace}" 
                           maxlength="100" placeholder="e.g., Cuc canh sat">
                </div>

                <!-- Country -->
                <div class="form-row">
                    <label class="form-label">Country: </label>
                    <div class="form-input-wrapper">
                        <select name="country" class="form-select">
                            <option value="">Select Country</option>
                            <option value="Vietnam" ${profile.country == 'Vietnam' ? 'selected' : ''}>Vietnam</option>
                            <option value="United States" ${profile.country == 'United States' ? 'selected' : ''}>United States</option>
                            <option value="Japan" ${profile.country == 'Japan' ? 'selected' : ''}>Japan</option>
                            <option value="South Korea" ${profile.country == 'South Korea' ? 'selected' : ''}>South Korea</option>
                            <option value="Singapore" ${profile.country == 'Singapore' ? 'selected' : ''}>Singapore</option>
                            <option value="Other" ${profile.country == 'Other' ? 'selected' : ''}>Other</option>
                        </select>
                    </div>
                </div>

                <!-- Address Line 1 -->
                <div class="form-row">
                    <label class="form-label">Address Line 1: </label>
                    <div class="form-input-wrapper">
                        <input type="text" name="addressLine1" class="form-control" value="${profile.addressLine1}" 
                               maxlength="100" placeholder="e.g., Phu Thuong, Tay Ho, Ha Noi">
                    </div>
                </div>

                <!-- Address Line 2 -->
                <div class="form-row">
                    <label class="form-label">Address Line 2:</label>
                    <div class="form-input-wrapper">
                        <input type="text" name="addressLine2" class="form-control" value="${profile.addressLine2}" maxlength="100">
                    </div>
                </div>

                <!-- City & State -->
                <div class="form-row two-columns">
                    <label class="form-label">City: </label>
                    <input type="text" name="city" class="form-control" value="${profile.city}" maxlength="50">
                    <label class="label-inline">State:</label>
                    <input type="text" name="state" class="form-control" value="${profile.state}" maxlength="50">
                </div>

                <!-- Postal Code -->
                <div class="form-row">
                    <label class="form-label">Postal Code:</label>
                    <div class="form-input-wrapper">
                        <input type="text" name="postalCode" class="form-control" value="${profile.postalCode}" 
                               pattern="^[0-9]{5,10}$" placeholder="5-10 digits"
                               title="Postal code must be 5-10 digits">
                    </div>
                </div>

                <!-- Save and Cancel buttons centered -->
                <div class="text-center mt-4">
                    <button type="submit" class="btn btn-primary btn-save">
                        <i class="fa-solid fa-floppy-disk me-2"></i>Save
                    </button>
                    <button type="button" class="btn btn-secondary btn-save ms-3" onclick="confirmCancel()">
                        <i class="fa-solid fa-xmark me-2"></i>Cancel
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
            // Alt Flow 2: Cancel with confirmation
            function confirmCancel() {
                if (confirm('Are you sure you want to cancel changes?')) {
                    window.location.href = '${pageContext.request.contextPath}/user-profile';
                }
            }
            
            // Track form changes to warn user
            let formChanged = false;
            const form = document.querySelector('form');
            const inputs = form.querySelectorAll('input:not([type="hidden"]), textarea, select');
            
            inputs.forEach(input => {
                input.addEventListener('change', () => {
                    formChanged = true;
                });
            });
            
            // Warn before leaving page if form has changes
            window.addEventListener('beforeunload', (e) => {
                if (formChanged) {
                    e.preventDefault();
                    e.returnValue = '';
                }
            });
            
            // Don't warn when submitting form
            form.addEventListener('submit', (e) => {
                formChanged = false;
                
                // No client-side validation - all validation done on server side
                // Errors will be displayed in the form
                return true;
            });
            
            // Set max date for date inputs to today
            const today = new Date().toISOString().split('T')[0];
            document.querySelector('input[name="dob"]').setAttribute('max', today);
            document.querySelector('input[name="cccdIssuedDate"]').setAttribute('max', today);
        </script>
    </body>

</html>
