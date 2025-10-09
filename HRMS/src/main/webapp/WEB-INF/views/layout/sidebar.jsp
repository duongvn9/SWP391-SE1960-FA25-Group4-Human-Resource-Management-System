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
                        data-tooltip="Employee Management">
                        <i class="fas fa-users"></i>
                        <span>Employee Management</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/attendance"
                        class="nav-link ${param.currentPage == 'attendance' ? 'active' : ''}" data-tooltip="Attendance">
                        <i class="fas fa-clock"></i>
                        <span>Attendance</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/payroll"
                        class="nav-link ${param.currentPage == 'payroll' ? 'active' : ''}" data-tooltip="Payroll">
                        <i class="fas fa-money-bill-wave"></i>
                        <span>Payroll</span>
                    </a>
                </li>

                <!-- Dropdown: Requests -->
                <li class="nav-item">
                    <a href="#" class="nav-link sidebar-dropdown-toggle" data-target="requests-submenu"
                        data-tooltip="Requests">
                        <i class="fas fa-clipboard-list"></i>
                        <span>Requests</span>
                        <i class="fas fa-chevron-right dropdown-arrow ms-auto"></i>
                    </a>
                    <ul class="sidebar-submenu" id="requests-submenu">
                        <li>
                            <a href="${pageContext.request.contextPath}/requests/leave/create"
                                class="nav-link submenu-link ${param.currentPage == 'leave-request' ? 'active' : ''}">
                                <i class="fas fa-calendar-times"></i>
                                <span>Leave Request</span>
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/requests/overtime/create"
                                class="nav-link submenu-link ${param.currentPage == 'overtime-request' ? 'active' : ''}">
                                <i class="fas fa-clock"></i>
                                <span>Overtime Request</span>
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/requests/appeal"
                                class="nav-link submenu-link ${param.currentPage == 'appeal-request' ? 'active' : ''}">
                                <i class="fas fa-exclamation-circle"></i>
                                <span>Attendance Appeal</span>
                            </a>
                        </li>
                    </ul>
                </li>

                <!-- Admin Only -->
                <c:if test="${sessionScope.userRole == 'ADMIN' || sessionScope.userRole == 'Admin'}">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/admin/accounts"
                            class="nav-link ${param.currentPage == 'accounts' ? 'active' : ''}"
                            data-tooltip="Account Management">
                            <i class="fas fa-user-shield"></i>
                            <span>Account Management</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/reports"
                            class="nav-link ${param.currentPage == 'reports' ? 'active' : ''}" data-tooltip="Reports">
                            <i class="fas fa-chart-bar"></i>
                            <span>Reports</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/system/settings"
                            class="nav-link ${param.currentPage == 'settings' ? 'active' : ''}"
                            data-tooltip="System Settings">
                            <i class="fas fa-cogs"></i>
                            <span>System Settings</span>
                        </a>
                    </li>
                </c:if>

                <!-- Profile & Logout -->
                <li class="nav-item mt-3">
                    <a href="${pageContext.request.contextPath}/profile"
                        class="nav-link ${param.currentPage == 'profile' ? 'active' : ''}"
                        data-tooltip="Profile">
                        <i class="fas fa-user"></i>
                        <span>Profile</span>
                    </a>
                </li>
                <li class="nav-item">
<<<<<<< HEAD
                    <a href="${pageContext.request.contextPath}/" class="nav-link" data-tooltip="Về trang chủ">
=======
                    <a href="${pageContext.request.contextPath}/profile/change-password"
                        class="nav-link ${param.currentPage == 'change-password' ? 'active' : ''}"
                        data-tooltip="Change Password">
                        <i class="fas fa-key"></i>
                        <span>Change Password</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/" class="nav-link" data-tooltip="Back to Home">
>>>>>>> 2432be8d7905b5243e2e8d233ef76e3bc47a8b80
                        <i class="fas fa-home"></i>
                        <span>Back to Home</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/logout" class="nav-link" data-tooltip="Logout">
                        <i class="fas fa-sign-out-alt"></i>
                        <span>Logout</span>
                    </a>
                </li>
            </ul>
        </nav>