<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>

        <!-- Sidebar -->
        <nav class="sidebar" id="sidebar">
            <div class="sidebar-header">
                <i class="fas fa-users-cog fs-2 mb-2"></i>
                <h4>HRMS</h4>
                <small>Human Resource Management</small>
            </div>

            <ul class="sidebar-nav nav flex-column">
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/dashboard"
                        class="nav-link ${param.currentPage == 'dashboard' ? 'active' : ''}" data-tooltip="Dashboard">
                        <i class="fas fa-tachometer-alt"></i>
                        <span>Dashboard</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/identity/employees"
                        class="nav-link ${param.currentPage == 'employees' ? 'active' : ''}"
                        data-tooltip="Quản lý Nhân viên">
                        <i class="fas fa-users"></i>
                        <span>Quản lý Nhân viên</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/attendance"
                        class="nav-link ${param.currentPage == 'attendance' ? 'active' : ''}" data-tooltip="Chấm công">
                        <i class="fas fa-clock"></i>
                        <span>Chấm công</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/payroll"
                        class="nav-link ${param.currentPage == 'payroll' ? 'active' : ''}" data-tooltip="Bảng lương">
                        <i class="fas fa-money-bill-wave"></i>
                        <span>Bảng lương</span>
                    </a>
                </li>

                <!-- Dropdown: Đơn từ -->
                <li class="nav-item">
                    <a href="#" class="nav-link sidebar-dropdown-toggle" data-target="requests-submenu"
                        data-tooltip="Đơn từ">
                        <i class="fas fa-clipboard-list"></i>
                        <span>Đơn từ</span>
                        <i class="fas fa-chevron-right dropdown-arrow ms-auto"></i>
                    </a>
                    <ul class="sidebar-submenu" id="requests-submenu">
                        <li>
                            <a href="${pageContext.request.contextPath}/requests/leave/create"
                                class="nav-link submenu-link ${param.currentPage == 'leave-request' ? 'active' : ''}">
                                <i class="fas fa-calendar-times"></i>
                                <span>Xin nghỉ phép</span>
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/requests/overtime/create"
                                class="nav-link submenu-link ${param.currentPage == 'overtime-request' ? 'active' : ''}">
                                <i class="fas fa-clock"></i>
                                <span>Xin làm thêm</span>
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/requests/appeal"
                                class="nav-link submenu-link ${param.currentPage == 'appeal-request' ? 'active' : ''}">
                                <i class="fas fa-exclamation-circle"></i>
                                <span>Khiếu nại chấm công</span>
                            </a>
                        </li>
                    </ul>
                </li>

                <!-- Admin Only -->
                <c:if test="${sessionScope.userRole == 'ADMIN' || sessionScope.userRole == 'Admin'}">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/admin/accounts"
                            class="nav-link ${param.currentPage == 'accounts' ? 'active' : ''}"
                            data-tooltip="Quản lý tài khoản">
                            <i class="fas fa-user-shield"></i>
                            <span>Quản lý tài khoản</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/reports"
                            class="nav-link ${param.currentPage == 'reports' ? 'active' : ''}" data-tooltip="Báo cáo">
                            <i class="fas fa-chart-bar"></i>
                            <span>Báo cáo</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/system/settings"
                            class="nav-link ${param.currentPage == 'settings' ? 'active' : ''}"
                            data-tooltip="Cài đặt hệ thống">
                            <i class="fas fa-cogs"></i>
                            <span>Cài đặt hệ thống</span>
                        </a>
                    </li>
                </c:if>

                <!-- Profile & Logout -->
                <li class="nav-item mt-3">
                    <a href="${pageContext.request.contextPath}/profile"
                        class="nav-link ${param.currentPage == 'profile' ? 'active' : ''}"
                        data-tooltip="Thông tin cá nhân">
                        <i class="fas fa-user"></i>
                        <span>Thông tin cá nhân</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/profile/change-password"
                        class="nav-link ${param.currentPage == 'change-password' ? 'active' : ''}"
                        data-tooltip="Đổi mật khẩu">
                        <i class="fas fa-key"></i>
                        <span>Đổi mật khẩu</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/" class="nav-link" data-tooltip="Về trang chủ">
                        <i class="fas fa-home"></i>
                        <span>Về trang chủ</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/logout" class="nav-link" data-tooltip="Đăng xuất">
                        <i class="fas fa-sign-out-alt"></i>
                        <span>Đăng xuất</span>
                    </a>
                </li>
            </ul>
        </nav>