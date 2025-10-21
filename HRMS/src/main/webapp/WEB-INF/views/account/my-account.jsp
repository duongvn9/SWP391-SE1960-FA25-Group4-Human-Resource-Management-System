<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="My Account - HRMS" />
        <jsp:param name="pageCss" value="dashboard.css" />
    </jsp:include>
    <style>
        .main-content { 
            margin-left: 260px; 
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        
        .content-area {
            flex: 1;
            padding: 2rem;
        }
        
        .account-card {
            background: white;
            border: none;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        .account-body {
            padding: 2rem;
        }
        
        .info-section {
            margin-bottom: 2rem;
        }
        
        .info-section h5 {
            color: #4f46e5;
            font-weight: 600;
            margin-bottom: 1rem;
            padding-bottom: 0.5rem;
            border-bottom: 2px solid #e5e7eb;
        }
        
        .info-row {
            display: flex;
            padding: 0.75rem 0;
            border-bottom: 1px solid #f3f4f6;
        }
        
        .info-row:last-child {
            border-bottom: none;
        }
        
        .info-label {
            font-weight: 600;
            color: #6b7280;
            width: 220px;
            flex-shrink: 0;
        }
        
        .info-value {
            color: #1f2937;
            flex: 1;
        }
        
        .status-badge {
            display: inline-block;
            padding: 0.25rem 0.75rem;
            border-radius: 20px;
            font-size: 0.875rem;
            font-weight: 600;
        }
        
        .status-active {
            background: #d1fae5;
            color: #065f46;
        }
        
        .status-inactive {
            background: #fee2e2;
            color: #991b1b;
        }
        
        @media (max-width: 768px) {
            .main-content { 
                margin-left: 0; 
            }
            
            .info-row {
                flex-direction: column;
            }
            
            .info-label {
                width: 100%;
                margin-bottom: 0.25rem;
            }
        }
    </style>
</head>
<body>
    <jsp:include page="../layout/sidebar.jsp">
        <jsp:param name="currentPage" value="my-account" />
    </jsp:include>

    <div class="main-content" id="main-content">
        <!-- Header -->
        <jsp:include page="../layout/dashboard-header.jsp" />

        <!-- Content Area -->
        <div class="content-area">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2><i class="fas fa-user-circle"></i> My Account</h2>
                <a href="${pageContext.request.contextPath}/change-password" class="btn btn-warning">
                    <i class="fas fa-key"></i> Change Password
                </a>
            </div>

            <!-- Success/Error Messages -->
            <c:if test="${not empty param.success}">
                <div class="alert alert-success alert-dismissible fade show">
                    <i class="fas fa-check-circle"></i> ${param.success}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            
            <c:if test="${not empty param.error}">
                <div class="alert alert-danger alert-dismissible fade show">
                    <i class="fas fa-exclamation-circle"></i> ${param.error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

        <div class="account-card">
            <div class="account-body">
                <!-- Account Information -->
                <div class="info-section">
                    <h5><i class="fas fa-user-shield me-2"></i>Account Information</h5>
                    <div class="info-row">
                        <div class="info-label">Account ID:</div>
                        <div class="info-value">${profile.accountId}</div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Username:</div>
                        <div class="info-value">${profile.username}</div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Email Login:</div>
                        <div class="info-value">${profile.emailLogin}</div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Full Name:</div>
                        <div class="info-value">${profile.fullName}</div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Position:</div>
                        <div class="info-value">${profile.positionName}</div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Department:</div>
                        <div class="info-value">${profile.departmentName}</div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Account Status:</div>
                        <div class="info-value">
                            <span class="status-badge ${profile.accountStatus == 'active' ? 'status-active' : 'status-inactive'}">
                                ${profile.accountStatus}
                            </span>
                        </div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Last Login:</div>
                        <div class="info-value">
                            <c:choose>
                                <c:when test="${not empty profile.lastLoginAt}">
                                    ${profile.lastLoginAt}
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted">Never</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Account Created At:</div>
                        <div class="info-value">
                            <c:choose>
                                <c:when test="${not empty profile.createdAt}">
                                    ${profile.createdAt}
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted">Not available</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Last Updated At:</div>
                        <div class="info-value">
                            <c:choose>
                                <c:when test="${not empty profile.updatedAt}">
                                    ${profile.updatedAt}
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted">Not available</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <!-- Action Buttons -->
                <div class="text-center mt-4">
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary">
                        <i class="fas fa-arrow-left me-2"></i>Back to Dashboard
                    </a>
                </div>
            </div>
        </div>
        </div>

        <!-- Footer -->
        <jsp:include page="../layout/dashboard-footer.jsp" />
    </div>

    <!-- Change Password Modal -->
    <div class="modal fade" id="changePasswordModal" tabindex="-1" aria-labelledby="changePasswordModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="changePasswordModalLabel">
                        <i class="fas fa-key me-2"></i>Change Password
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="changePasswordForm" method="post" action="${pageContext.request.contextPath}/change-password">
                        <div class="mb-3">
                            <label for="currentPassword" class="form-label">Current Password</label>
                            <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
                        </div>
                        <div class="mb-3">
                            <label for="newPassword" class="form-label">New Password</label>
                            <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                        </div>
                        <div class="mb-3">
                            <label for="confirmPassword" class="form-label">Confirm New Password</label>
                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" form="changePasswordForm" class="btn btn-primary">
                        <i class="fas fa-check me-2"></i>Change Password
                    </button>
                </div>
            </div>
        </div>
    </div>


</body>
</html>
