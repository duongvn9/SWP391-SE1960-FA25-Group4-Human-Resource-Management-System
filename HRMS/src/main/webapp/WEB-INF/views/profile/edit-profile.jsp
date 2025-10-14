<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <title>Update Profile - HRMS</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            body {
                background-color: #f5f5f5;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                margin: 0;
                padding: 0;
            }
            .profile-container {
                max-width: 95%;
                margin: 20px auto;
                background: white;
                padding: 30px 50px;
                min-height: calc(100vh - 40px);
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

                <!-- Full Name -->
                <div class="form-row">
                    <label class="form-label">Full Name: <span class="text-danger">*</span></label>
                    <div class="form-input-wrapper">
                        <input type="text" name="fullName" class="form-control" value="${profile.fullName}" required>
                    </div>
                </div>

                <!-- Phone -->
                <div class="form-row">
                    <label class="form-label">Phone Number:</label>
                    <div class="form-input-wrapper">
                        <input type="text" name="phone" class="form-control" value="${profile.phone}" placeholder="10-11 digits">
                    </div>
                </div>

                <!-- Date of Birth & Hometown -->
                <div class="form-row two-columns">
                    <label class="form-label">Date of Birth:</label>
                    <input type="date" name="dob" class="form-control" value="${profile.dob}">
                    <label class="label-inline">Hometown:</label>
                    <input type="text" name="hometown" class="form-control" value="${profile.hometown}">
                </div>

                <!-- Gender -->
                <div class="form-row">
                    <label class="form-label">Gender:</label>
                    <div class="form-input-wrapper">
                        <div class="radio-group">
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="gender" id="genderMale" value="male" ${profile.gender == 'male' ? 'checked' : ''}>
                                <label class="form-check-label" for="genderMale">Male</label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="gender" id="genderFemale" value="female" ${profile.gender == 'female' ? 'checked' : ''}>
                                <label class="form-check-label" for="genderFemale">Female</label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="gender" id="genderFemale" value="female" ${profile.gender == 'female' ? 'checked' : ''}>
                                <label class="form-check-label" for="genderFemale">Others</label>
                            </div>    
                        </div>
                    </div>
                </div>

                <!-- Citizen ID (CCCD) -->
                <div class="form-row">
                    <label class="form-label">Citizen ID (CCCD):</label>
                    <div class="form-input-wrapper">
                        <input type="text" name="cccd" class="form-control" value="${profile.cccd}" placeholder="9 or 12 digits">
                    </div>
                </div>

                <!-- CCCD Issued Date & Place -->
                <div class="form-row two-columns">
                    <label class="form-label">CCCD Issued Date:</label>
                    <input type="date" name="cccdIssuedDate" class="form-control" value="${profile.cccdIssuedDate}">
                    <label class="label-inline">Issued Place:</label>
                    <input type="text" name="cccdIssuedPlace" class="form-control" value="${profile.cccdIssuedPlace}">
                </div>

                <!-- Country -->
                <div class="form-row">
                    <label class="form-label">Country:</label>
                    <div class="form-input-wrapper">
                        <input type="text" name="country" class="form-control" value="${profile.country}">
                    </div>
                </div>

                <!-- Address Line 1 -->
                <div class="form-row">
                    <label class="form-label">Address Line 1:</label>
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

                <!-- City & State -->
                <div class="form-row two-columns">
                    <label class="form-label">City:</label>
                    <input type="text" name="city" class="form-control" value="${profile.city}">
                    <label class="label-inline">State:</label>
                    <input type="text" name="state" class="form-control" value="${profile.state}">
                </div>

                <!-- Postal Code -->
                <div class="form-row">
                    <label class="form-label">Postal Code:</label>
                    <div class="form-input-wrapper">
                        <input type="text" name="postalCode" class="form-control" value="${profile.postalCode}">
                    </div>
                </div>

                <!-- Save button centered -->
                <div class="text-center mt-4">
                    <button type="submit" class="btn btn-primary btn-save">
                        <i class="fa-solid fa-floppy-disk me-2"></i>Save
                    </button>
                </div>
            </form>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>

</html>
