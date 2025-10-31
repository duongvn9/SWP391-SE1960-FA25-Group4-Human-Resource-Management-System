<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="View Profile - HRMS" />
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
            width: 150px;
        }
        .form-row.two-columns .form-value:first-of-type {
            flex: 0 0 calc(50% - 125px);
            margin-right: 15px;
        }
        .form-row.two-columns .form-label:nth-child(3) {
            flex: 0 0 150px;
            text-align: right;
            padding-right: 15px;
            white-space: nowrap;
        }
        .form-row.two-columns .form-value:last-of-type {
            flex: 1;
        }
        .form-label {
            width: 150px;
            font-weight: 500;
            color: #333;
            margin: 0;
            padding-right: 15px;
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
        
        <!-- Employee Code & Full Name -->
        <div class="form-row two-columns">
            <label class="form-label">Employee Code:</label>
            <div class="form-value">${profile.employeeCode}</div>
            <label class="form-label">Full Name:</label>
            <div class="form-value">${profile.fullName}</div>
        </div>

        <!-- Phone Number & Gender -->
        <div class="form-row two-columns">
            <label class="form-label">Phone Number:</label>
            <div class="form-value">${profile.phone}</div>
            <label class="form-label">Gender:</label>
            <div class="form-value">
                <c:choose>
                    <c:when test="${not empty profile.gender}">
                        ${fn:substring(fn:toUpperCase(profile.gender), 0, 1)}${fn:toLowerCase(fn:substring(profile.gender, 1, fn:length(profile.gender)))}
                    </c:when>
                    <c:otherwise>-</c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Date of Birth & Hometown -->
        <div class="form-row two-columns">
            <label class="form-label">Date of Birth:</label>
            <div class="form-value">${profile.dob}</div>
            <label class="form-label">Hometown:</label>
            <div class="form-value">${profile.hometown}</div>
        </div>

        <!-- Citizen ID (CCCD) & Issued Place -->
        <div class="form-row two-columns">
            <label class="form-label">Citizen ID (CCCD):</label>
            <div class="form-value">${profile.cccd}</div>
            <label class="form-label">Issued Place:</label>
            <div class="form-value">${profile.cccdIssuedPlace}</div>
        </div>

        <!-- CCCD Issued Date & Expire Date -->
        <div class="form-row two-columns">
            <label class="form-label">CCCD Issued Date:</label>
            <div class="form-value">${profile.cccdIssuedDate}</div>
            <label class="form-label">CCCD Expire Date:</label>
            <div class="form-value">${profile.cccdExpireDate}</div>
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

        <!-- Status & Postal Code -->
        <div class="form-row two-columns">
            <label class="form-label">Status:</label>
            <div class="form-value">${profile.status}</div>
            <label class="form-label">Postal Code:</label>
            <div class="form-value">${profile.postalCode}</div>
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

        <!-- City -->
        <div class="form-row">
            <label class="form-label">City:</label>
            <div class="form-value">${profile.city}</div>

            </div>
            </div>

        </div>

      <!-- Footer -->
        <jsp:include page="../layout/dashboard-footer.jsp" />  
    </div>
    
</body>
</html>
