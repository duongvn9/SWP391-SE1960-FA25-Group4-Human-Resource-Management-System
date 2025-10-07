<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="Dashboard - HRMS" />
        <jsp:param name="pageCss" value="dashboard.css" />
    </jsp:include>
</head>

<body>
    <!-- Sidebar -->
    <jsp:include page="../layout/sidebar.jsp">
        <jsp:param name="currentPage" value="dashboard" />
    </jsp:include>

    <!-- Main Content -->
    <div class="main-content" id="main-content">
        <!-- Header -->
        <jsp:include page="../layout/dashboard-header.jsp" />

        <!-- Content Area -->
        <div class="content-area">
            <!-- Welcome Section -->
            <div class="row mb-4">
                <div class="col-12">
                    <h2>Chào mừng trở lại, ${sessionScope.userFullName != null ? sessionScope.userFullName : 'Admin'}!</h2>
                    <p class="text-muted">Dashboard tổng quan hệ thống quản lý nhân sự</p>
                </div>
            </div>

            <!-- Statistics Cards -->
            <div class="row mb-4">
                <div class="col-lg-3 col-md-6 mb-3">
                    <div class="dashboard-card">
                        <div class="stat-card primary">
                            <div class="icon">
                                <i class="fas fa-users"></i>
                            </div>
                            <span class="stat-number counter">${totalEmployees != null ? totalEmployees : 156}</span>
                            <div class="stat-label">Tổng số nhân viên</div>
                        </div>
                    </div>
                </div>

                <div class="col-lg-3 col-md-6 mb-3">
                    <div class="dashboard-card">
                        <div class="stat-card success">
                            <div class="icon">
                                <i class="fas fa-user-check"></i>
                            </div>
                            <span class="stat-number counter">${presentToday != null ? presentToday : 142}</span>
                            <div class="stat-label">Có mặt hôm nay</div>
                        </div>
                    </div>
                </div>

                <div class="col-lg-3 col-md-6 mb-3">
                    <div class="dashboard-card">
                        <div class="stat-card warning">
                            <div class="icon">
                                <i class="fas fa-calendar-times"></i>
                            </div>
                            <span class="stat-number counter">${onLeaveToday != null ? onLeaveToday : 8}</span>
                            <div class="stat-label">Nghỉ phép hôm nay</div>
                        </div>
                    </div>
                </div>

                <div class="col-lg-3 col-md-6 mb-3">
                    <div class="dashboard-card">
                        <div class="stat-card danger">
                            <div class="icon">
                                <i class="fas fa-user-times"></i>
                            </div>
                            <span class="stat-number counter">${absentToday != null ? absentToday : 6}</span>
                            <div class="stat-label">Vắng mặt hôm nay</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Quick Actions for Admin -->
            <c:if test="${sessionScope.userRole == 'ADMIN' || sessionScope.userRole == 'Admin'}">
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="dashboard-card">
                            <h5 class="mb-3">
                                <i class="fas fa-bolt me-2 text-warning"></i>Thao tác quản trị nhanh
                            </h5>
                            <div class="row">
                                <div class="col-lg-3 col-md-6 mb-3">
                                    <a href="${pageContext.request.contextPath}/admin/accounts"
                                        class="btn btn-outline-primary w-100 py-3 text-decoration-none">
                                        <i class="fas fa-user-shield fs-2 d-block mb-2"></i>
                                        <strong>Quản lý tài khoản</strong>
                                        <br>
                                        <small class="text-muted">Tạo, sửa, xóa accounts</small>
                                    </a>
                                </div>
                                <div class="col-lg-3 col-md-6 mb-3">
                                    <a href="${pageContext.request.contextPath}/system/settings"
                                        class="btn btn-outline-secondary w-100 py-3 text-decoration-none">
                                        <i class="fas fa-cogs fs-2 d-block mb-2"></i>
                                        <strong>Cài đặt hệ thống</strong>
                                        <br>
                                        <small class="text-muted">Cấu hình hệ thống</small>
                                    </a>
                                </div>
                                <div class="col-lg-3 col-md-6 mb-3">
                                    <a href="${pageContext.request.contextPath}/reports"
                                        class="btn btn-outline-info w-100 py-3 text-decoration-none">
                                        <i class="fas fa-chart-bar fs-2 d-block mb-2"></i>
                                        <strong>Báo cáo tổng hợp</strong>
                                        <br>
                                        <small class="text-muted">Xem báo cáo chi tiết</small>
                                    </a>
                                </div>
                                <div class="col-lg-3 col-md-6 mb-3">
                                    <a href="${pageContext.request.contextPath}/identity/employees"
                                        class="btn btn-outline-success w-100 py-3 text-decoration-none">
                                        <i class="fas fa-users-cog fs-2 d-block mb-2"></i>
                                        <strong>Quản lý nhân viên</strong>
                                        <br>
                                        <small class="text-muted">Thêm, sửa thông tin</small>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>

            <!-- Charts and Recent Activities -->
            <div class="row">
                <!-- Attendance Chart -->
                <div class="col-lg-8 mb-4">
                    <div class="dashboard-card">
                        <h5 class="mb-3"><i class="fas fa-chart-line me-2"></i>Thống kê chấm công 7 ngày gần đây</h5>
                        <div class="chart-container">
                            <canvas id="attendanceChart"></canvas>
                        </div>
                    </div>
                </div>

                <!-- Recent Notifications -->
                <div class="col-lg-4 mb-4">
                    <div class="dashboard-card">
                        <h5 class="mb-3"><i class="fas fa-bell me-2"></i>Thông báo gần đây</h5>
                        <div class="notification-list">
                            <div class="notification-item">
                                <div class="notification-icon info">
                                    <i class="fas fa-info"></i>
                                </div>
                                <div class="flex-grow-1">
                                    <strong>Đơn xin nghỉ phép mới</strong>
                                    <p class="mb-1 small">Nguyễn Văn A xin nghỉ phép từ 15/10 - 17/10</p>
                                    <small class="text-muted">2 giờ trước</small>
                                </div>
                            </div>

                            <div class="notification-item">
                                <div class="notification-icon success">
                                    <i class="fas fa-check"></i>
                                </div>
                                <div class="flex-grow-1">
                                    <strong>Bảng lương đã được tạo</strong>
                                    <p class="mb-1 small">Bảng lương tháng 9 đã sẵn sàng</p>
                                    <small class="text-muted">1 ngày trước</small>
                                </div>
                            </div>

                            <div class="notification-item">
                                <div class="notification-icon warning">
                                    <i class="fas fa-exclamation"></i>
                                </div>
                                <div class="flex-grow-1">
                                    <strong>Nhân viên mới cần duyệt</strong>
                                    <p class="mb-1 small">3 hồ sơ tuyển dụng cần xét duyệt</p>
                                    <small class="text-muted">2 ngày trước</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Recent Activities Table -->
            <div class="row">
                <div class="col-12">
                    <div class="table-card">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="fas fa-history me-2"></i>Hoạt động gần đây</h5>
                        </div>
                        <div class="table-responsive">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>Thời gian</th>
                                        <th>Nhân viên</th>
                                        <th>Hoạt động</th>
                                        <th>Trạng thái</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>08:30 - Hôm nay</td>
                                        <td>Nguyễn Văn A</td>
                                        <td>Chấm công vào</td>
                                        <td><span class="badge badge-status bg-success">Thành công</span></td>
                                    </tr>
                                    <tr>
                                        <td>08:45 - Hôm nay</td>
                                        <td>Trần Thị B</td>
                                        <td>Gửi đơn xin nghỉ phép</td>
                                        <td><span class="badge badge-status bg-warning">Chờ duyệt</span></td>
                                    </tr>
                                    <tr>
                                        <td>09:00 - Hôm nay</td>
                                        <td>Lê Văn C</td>
                                        <td>Cập nhật thông tin cá nhân</td>
                                        <td><span class="badge badge-status bg-info">Hoàn thành</span></td>
                                    </tr>
                                    <tr>
                                        <td>17:30 - Hôm qua</td>
                                        <td>Phạm Thị D</td>
                                        <td>Chấm công ra</td>
                                        <td><span class="badge badge-status bg-success">Thành công</span></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <jsp:include page="../layout/dashboard-footer.jsp" />
    </div>
</body>

</html>