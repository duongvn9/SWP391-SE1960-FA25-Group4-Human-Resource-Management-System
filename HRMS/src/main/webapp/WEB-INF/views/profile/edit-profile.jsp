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
                padding: 2rem 2rem 0 2rem;
                min-height: calc(100vh - 64px);
                display: flex;
                flex-direction: column;
            }
            
            .content-area {
                flex: 1;
            }
            
            .dashboard-footer {
                margin-left: -2rem;
                margin-right: -2rem;
                margin-bottom: 0;
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
            <form method="post" action="${pageContext.request.contextPath}/user-profile/update" autocomplete="off">
                <!-- CSRF Token -->
                <input type="hidden" name="_csrf_token" value="${csrfToken}">
                
                <!-- Hidden field for Company Email (not editable by user) -->
                <input type="hidden" name="emailCompany" value="${profile.emailCompany}">

                <!-- Full Name -->
                <div class="form-row">
                    <label class="form-label">Full Name: </label>
                    <div class="form-input-wrapper">
                        <input type="text" name="fullName" class="form-control" value="${profile.fullName}">
                    </div>
                </div>

                <!-- Phone -->
                <div class="form-row">
                    <label class="form-label">Phone Number: </label>
                    <div class="form-input-wrapper">
                        <input type="text" name="phone" class="form-control" value="${profile.phone}" autocomplete="off" maxlength="10" pattern="\d{10}" title="Phone number must be exactly 10 digits">
                    </div>
                </div>

                <!-- Date of Birth & Hometown -->
                <div class="form-row two-columns">
                    <label class="form-label">Date of Birth: </label>
                    <input type="date" name="dob" class="form-control" value="${profile.dob}">
                    <label class="label-inline">Hometown:</label>
                    <input type="text" name="hometown" class="form-control" value="${profile.hometown}">
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
                        <input type="text" name="cccd" class="form-control" value="${profile.cccd}" autocomplete="off">
                    </div>
                </div>

                <!-- CCCD Issued Date & Place -->
                <div class="form-row two-columns">
                    <label class="form-label">CCCD Issued Date: </label>
                    <input type="date" name="cccdIssuedDate" class="form-control" value="${profile.cccdIssuedDate}">
                    <label class="label-inline">Issued Place: </label>
                    <input type="text" name="cccdIssuedPlace" class="form-control" value="${profile.cccdIssuedPlace}">
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
                        <input type="text" name="addressLine1" class="form-control" value="${profile.addressLine1}">
                    </div>
                </div>

                <!-- Address Line 2 -->
                <div class="form-row">
                    <label class="form-label">Address Line 2:</label>
                    <div class="form-input-wrapper">
                        <input type="text" name="addressLine2" class="form-control" value="${profile.addressLine2}">
                    </div>
                </div>

                <!-- City -->
                <div class="form-row">
                    <label class="form-label">City: </label>
                    <div class="form-input-wrapper">
                        <input type="text" name="city" class="form-control" value="${profile.city}">
                    </div>
                </div>

                <!-- Postal Code -->
                <div class="form-row">
                    <label class="form-label">Postal Code:</label>
                    <div class="form-input-wrapper">
                        <input type="text" name="postalCode" class="form-control" value="${profile.postalCode}">
                    </div>
                </div>

                <!-- Save and Cancel buttons centered -->
                <div class="text-center mt-4">
                    <button type="submit" class="btn btn-primary btn-save">
                        <i class="fa-solid fa-floppy-disk me-2"></i>Save
                    </button>
                    <button type="button" class="btn btn-secondary btn-save ms-3" data-bs-toggle="modal" data-bs-target="#cancelModal">
                        <i class="fa-solid fa-xmark me-2"></i>Cancel
                    </button>
                </div>
                </form>
                </div>
            </div>
            <!-- Footer -->
        <jsp:include page="../layout/dashboard-footer.jsp" />                   
        </div>

        
    </div>

    <!-- Cancel Confirmation Modal -->
    <div class="modal fade" id="cancelModal" tabindex="-1" aria-labelledby="cancelModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="cancelModalLabel">Confirm Cancel</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    Are you sure you want to cancel changes? Any unsaved changes will be lost.
                </div>
                <div class="modal-footer">
                    <a href="${pageContext.request.contextPath}/user-profile" class="btn btn-primary">Yes, Cancel Changes</a>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">No, Continue Editing</button>
                </div>
            </div>
        </div>
    </div>

    <script>
            
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
                
                // Debug: Log form values before submit
                const phoneValue = document.querySelector('input[name="phone"]').value;
                const cccdValue = document.querySelector('input[name="cccd"]').value;
                console.log('=== FORM SUBMIT DEBUG ===');
                console.log('Phone value:', phoneValue);
                console.log('CCCD value:', cccdValue);
                console.log('========================');
                
                // No client-side validation - all validation done on server side
                // Errors will be displayed in the form
                return true;
            });
        </script>
    </body>

</html>
