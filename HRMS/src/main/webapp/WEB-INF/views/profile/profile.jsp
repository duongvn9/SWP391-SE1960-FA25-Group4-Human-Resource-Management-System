<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <title>Profile</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/user-profile.css">
    </head>

    <body>
        <div class="page-wrap">

            <div class="profile-shell">
                <!-- LEFT: User info card (keep unchanged) -->
                <div class="left-card">
                    <div class="name-row">
                        <h5 class="left-name">${user.fullName}</h5>
                        <span class="badge-admin"><i class="fa-solid fa-user-shield me-1"></i>Admin</span>
                    </div>
                    <div class="left-email">${user.email}</div>

                    <div class="left-actions mb-3">
                        <a class="text-primary" href="${pageContext.request.contextPath}/salary-history">
                            <i class="fa-solid fa-receipt"></i> View Salary History
                        </a>
                    </div>

                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-primary w-100">
                        <i class="fa-solid fa-arrow-left-long me-1"></i> Back to Dashboard
                    </a>
                </div>

                <!-- RIGHT: Profile form with additional fields -->
                <div class="profile-card">
                    <div class="hrms-logo">
                        <i class="fa-solid fa-users-gear hrms-logo-icon"></i>
                        <div class="hrms-logo-text">HRMS</div>
                    </div>
                    <div class="profile-header-title">Personal Profile</div>

                    <form method="post" action="${pageContext.request.contextPath}/user-profile">

                        <!-- Citizen ID (CCCD) -->
                        <div class="mb-3">
                            <label class="form-label">Citizen ID (CCCD)</label>
                            <input type="text" class="form-control" value="${user.cccd}" placeholder="Enter your Citizen ID" >
                        </div>
                        <!-- Full Name -->
                        <div class="mb-3">
                            <label class="form-label">Full Name</label>
                            <input type="text" class="form-control" value="${user.fullName}" >
                        </div>
                        <!-- Gender -->
                        <div class="mb-3">
                            <label class="form-label">Gender</label>
                            <select class="form-select" disabled>
                                <option value="">Select gender</option>
                                <option value="male" ${user.gender == 'male' ? 'selected' : ''}>Male</option>
                                <option value="female" ${user.gender == 'female' ? 'selected' : ''}>Female</option>
                                <option value="others" ${user.gender == 'others' ? 'selected' : ''}>Others</option>
                            </select>
                        </div>
                        <!-- Date Joined -->
                        <div class="mb-3">
                            <label class="form-label">Date Joined</label>
                            <input type="date" class="form-control" value="${user.dateJoined}" readonly>
                        </div>
                        <!-- Start Work Date -->
                        <div class="mb-3">
                            <label class="form-label">Start Work Date</label>
                            <input type="date" class="form-control" value="${user.startWorkDate}" readonly>
                        </div>
                        <!-- Email -->
                        <div class="mb-3">
                            <label class="form-label">Email</label>
                            <input type="email" class="form-control" value="${user.email}" >
                        </div>
                        <!-- Phone Number -->
                        <div class="mb-3">
                            <label class="form-label">Phone Number</label>
                            <input type="text" class="form-control" value="${user.phone}" >
                        </div>
                        <!-- Department -->
                        <div class="mb-3">
                            <label class="form-label">Department</label>
                            <input type="text" class="form-control" value="${user.department}" readonly>
                        </div>
                        <!-- Position -->
                        <div class="mb-3">
                            <label class="form-label">Position</label>
                            <input type="text" class="form-control" value="${user.position}" readonly>
                        </div>
                        <!-- Bank Information -->
                        <div class="mb-3">
                            <label class="form-label">Bank Information</label>
                            <input type="text" class="form-control" value="${user.bankInfo}">
                        </div>
                        <!-- Hometown -->
                        <div class="mb-3">
                            <label class="form-label">Hometown</label>
                            <input type="text" class="form-control" value="${user.hometown}" >
                        </div>
                        <!-- Address Line 1 -->
                        <div class="mb-3">
                            <label class="form-label">Address Line 1</label>
                            <input type="text" class="form-control" value="${user.addressLine1}" >
                        </div>
                        <!-- Address Line 2 -->
                        <div class="mb-3">
                            <label class="form-label">Address Line 2</label>
                            <input type="text" class="form-control" value="${user.addressLine2}" >
                        </div>
                        <!-- City -->
                        <div class="mb-3">
                            <label class="form-label">City</label>
                            <input type="text" class="form-control" value="${user.city}" >
                        </div>
                        <!-- Country -->
                        <div class="mb-3">
                            <label class="form-label">Country</label>
                            <input type="text" class="form-control" value="${user.country}">
                        </div>
                        <!-- Update button centered -->
                        <div class="text-center mt-4">
                            <button type="button" class="btn btn-success px-4">
                                <i class="fa-solid fa-pen-to-square me-1"></i> Update Profile
                            </button>
                        </div>
                    </form>
                </div>
            </div>

        </div>
    </body>

</html>