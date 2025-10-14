<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>View Profile - HRMS</title>
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
            display: flex;
            justify-content: space-between;
            align-items: center;
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
        .btn-update-profile {
            padding: 10px 30px;
            font-size: 1rem;
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
        .form-row.two-columns .form-label:first-child {
            width: 200px;
        }
        .form-row.two-columns .form-value:first-of-type {
            flex: 0 0 calc(50% - 150px);
            margin-right: 15px;
        }
        .form-row.two-columns .form-label:nth-child(3) {
            flex: 0 0 120px;
            text-align: right;
            padding-right: 15px;
        }
        .form-row.two-columns .form-value:last-of-type {
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
        .form-value {
            flex: 1;
            padding: 8px 12px;
            background-color: #f8f9fa;
            border: 1px solid #e9ecef;
            border-radius: 4px;
            font-size: 0.95rem;
            color: #495057;
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
    </style>
</head>

<body>
    <div class="profile-container">
        <!-- Back to Dashboard Link -->
        <a href="${pageContext.request.contextPath}/dashboard" class="back-link">
            <i class="fa-solid fa-arrow-left me-2"></i>Back to Dashboard
        </a>

        <!-- Profile Header with Update Button -->
        <div class="profile-header">
            <h2>View Profile</h2>
            <a href="${pageContext.request.contextPath}/user-profile/edit" class="btn btn-primary btn-update-profile">
                <i class="fa-solid fa-pen-to-square me-2"></i>Update Profile
            </a>
        </div>

        <!-- Description -->
        <div class="profile-description">
            This information is important and confidential. It is used by the company for official records, 
            certificates, and administrative purposes.
        </div>

        <!-- Profile Information (Read-only) -->
        
        <!-- Employee Code -->
        <div class="form-row">
            <label class="form-label">Employee Code:</label>
            <div class="form-value">${profile.employeeCode}</div>
        </div>

        <!-- Full Name -->
        <div class="form-row">
            <label class="form-label">Full Name:</label>
            <div class="form-value">${profile.fullName}</div>
        </div>

        <!-- Phone -->
        <div class="form-row">
            <label class="form-label">Phone Number:</label>
            <div class="form-value">${profile.phone}</div>
        </div>

        <!-- Date of Birth & Hometown -->
        <div class="form-row two-columns">
            <label class="form-label">Date of Birth:</label>
            <div class="form-value">${profile.dob}</div>
            <label class="form-label">Hometown:</label>
            <div class="form-value">${profile.hometown}</div>
        </div>

        <!-- Gender -->
        <div class="form-row">
            <label class="form-label">Gender:</label>
            <div class="form-value">
                <c:choose>
                    <c:when test="${profile.gender == 'male'}">Male</c:when>
                    <c:when test="${profile.gender == 'female'}">Female</c:when>
                    <c:when test="${profile.gender == 'others'}">Others</c:when>
                    <c:otherwise>-</c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Citizen ID (CCCD) -->
        <div class="form-row">
            <label class="form-label">Citizen ID (CCCD):</label>
            <div class="form-value">${profile.cccd}</div>
        </div>

        <!-- CCCD Issued Date & Place -->
        <div class="form-row two-columns">
            <label class="form-label">CCCD Issued Date:</label>
            <div class="form-value">${profile.cccdIssuedDate}</div>
            <label class="form-label">CCCD Issued Place:</label>
            <div class="form-value">${profile.cccdIssuedPlace}</div>
        </div>

        <!-- Country -->
        <div class="form-row">
            <label class="form-label">Country:</label>
            <div class="form-value">${profile.country}</div>
        </div>

        <!-- Email Company -->
        <div class="form-row">
            <label class="form-label">Company Email:</label>
            <div class="form-value">${profile.emailCompany}</div>
        </div>

        <!-- Department & Position -->
        <div class="form-row two-columns">
            <label class="form-label">Department:</label>
            <div class="form-value">${profile.departmentName}</div>
            <label class="form-label">Position:</label>
            <div class="form-value">${profile.positionName}</div>
        </div>

        <!-- Status -->
        <div class="form-row">
            <label class="form-label">Status:</label>
            <div class="form-value">${profile.status}</div>
        </div>

        <!-- Date Joined & Start Work Date -->
        <div class="form-row two-columns">
            <label class="form-label">Date Joined:</label>
            <div class="form-value">${profile.dateJoined}</div>
            <label class="form-label">Start Work Date:</label>
            <div class="form-value">${profile.startWorkDate}</div>
        </div>

        <!-- Address Line 1 -->
        <div class="form-row">
            <label class="form-label">Address Line 1:</label>
            <div class="form-value">${profile.addressLine1}</div>
        </div>

        <!-- Address Line 2 -->
        <div class="form-row">
            <label class="form-label">Address Line 2:</label>
            <div class="form-value">${profile.addressLine2}</div>
        </div>

        <!-- City & State -->
        <div class="form-row two-columns">
            <label class="form-label">City:</label>
            <div class="form-value">${profile.city}</div>
            <label class="form-label">State/Province:</label>
            <div class="form-value">${profile.state}</div>
        </div>

        <!-- Postal Code & Country -->
        <div class="form-row two-columns">
            <label class="form-label">Postal Code:</label>
            <div class="form-value">${profile.postalCode}</div>
        </div>

    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>
