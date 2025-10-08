<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="Thông tin cá nhân" />
        <jsp:param name="cssFiles" value="profile" />
    </jsp:include>
</head>
<body>
    <div class="dashboard-wrapper">
        <!-- Sidebar -->
        <jsp:include page="../layout/sidebar.jsp">
            <jsp:param name="currentPage" value="profile" />
        </jsp:include>

        <!-- Main Content -->
        <div class="main-content">
            <!-- Header -->
            <jsp:include page="../layout/dashboard-header.jsp">
                <jsp:param name="pageTitle" value="Thông tin cá nhân" />
            </jsp:include>

            <!-- Page Content -->
            <div class="container-fluid py-4">
                <div class="row">
                    <div class="col-lg-8 col-xl-6 mx-auto">
                        <!-- Profile Card -->
                        <div class="card">
                            <div class="card-header bg-primary text-white">
                                <h5 class="mb-0">
                                    <i class="fas fa-user me-2"></i>
                                    Thông tin cá nhân
                                </h5>
                            </div>
                            <div class="card-body">
                                <!-- User Info Display -->
                                <div class="row mb-3">
                                    <div class="col-sm-4"><strong>Họ và tên:</strong></div>
                                    <div class="col-sm-8">${sessionScope.userName != null ? sessionScope.userName : 'Chưa cập nhật'}</div>
                                </div>
                                <div class="row mb-3">
                                    <div class="col-sm-4"><strong>Email:</strong></div>
                                    <div class="col-sm-8">${sessionScope.userEmail != null ? sessionScope.userEmail : 'Chưa cập nhật'}</div>
                                </div>
                                <div class="row mb-3">
                                    <div class="col-sm-4"><strong>Vai trò:</strong></div>
                                    <div class="col-sm-8">
                                        <span class="badge bg-info">${sessionScope.userRole != null ? sessionScope.userRole : 'User'}</span>
                                    </div>
                                </div>
                                <hr>
                                <div class="text-end">
                                    <a href="${pageContext.request.contextPath}/profile/change-password" 
                                       class="btn btn-primary">
                                        <i class="fas fa-key me-1"></i>
                                        Đổi mật khẩu
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <jsp:include page="../layout/dashboard-footer.jsp" />
</body>
</html>