<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>

        <!-- ===== SIDEBAR (SELF-CONTAINED) ===== -->
        <style>
            /* Fallback biến dùng cho KHUNG (độc lập với dashboard.css) */
            :root {
                --sidebar-width: 260px;
                --primary-color: #4f46e5;
            }

            /* Khung layout tối thiểu: để mọi trang có include sidebar đều đứng vững */
            .main-content {
                margin-left: var(--sidebar-width, 260px);
                min-height: 100vh;
                display: flex;
                flex-direction: column;
                transition: margin-left .3s ease;
            }

            body.sidebar-collapsed .main-content {
                margin-left: 70px;
            }

            @media (max-width: 768px) {
                .main-content {
                    margin-left: 0;
                }
            }

            /* Sidebar core */
            .sidebar {
                position: fixed;
                top: 0;
                left: 0;
                height: 100vh;
                width: var(--sidebar-width, 260px);
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: #fff;
                z-index: 1040;
                overflow-y: auto;
                overflow-x: hidden;
                transition: width .3s ease, transform .3s ease;
            }

            .sidebar.collapsed {
                width: 70px;
            }

            /* Mobile off-canvas */
            @media (max-width:768px) {
                .sidebar {
                    transform: translateX(-100%);
                }

                .sidebar.show {
                    transform: translateX(0);
                }
            }

            /* Header trong sidebar (logo) */
            .sidebar-header {
                padding: 1rem 1.25rem;
                border-bottom: 1px solid rgba(255, 255, 255, .12);
            }

            .sidebar-logo-link {
                display: inline-flex;
                align-items: center;
                gap: .75rem;
                color: #fff;
                text-decoration: none;
            }

            .sidebar-brand-text h4 {
                margin: 0;
                font-weight: 700;
            }

            .sidebar-brand-text small {
                display: block;
                opacity: .95;
            }

            .sidebar.collapsed .sidebar-brand-text,
            body.sidebar-collapsed .sidebar-brand-text {
                display: none;
            }

            /* Nav trong sidebar */
            .sidebar-nav {
                padding: .75rem 0;
            }

            .sidebar .nav-item {
                margin: .15rem 0;
            }

            .sidebar .nav-link {
                color: rgba(255, 255, 255, .9);
                display: flex;
                align-items: center;
                gap: .6rem;
                padding: .75rem 1rem;
                text-decoration: none;
                transition: background-color .2s ease, transform .2s ease;
            }

            .sidebar .nav-link:hover,
            .sidebar .nav-link.active {
                background: rgba(255, 255, 255, .12);
                transform: translateX(4px);
            }

            .sidebar .nav-link i {
                width: 20px;
                text-align: center;
            }

            /* Submenu */
            .sidebar-submenu {
                list-style: none;
                margin: 0;
                padding: 0;
                max-height: 0;
                overflow: hidden;
                transition: max-height .25s ease;
                background: rgba(0, 0, 0, .08);
            }

            .sidebar-submenu.show {
                max-height: 320px;
            }

            .sidebar-submenu .nav-link {
                padding-left: 2.25rem;
                color: rgba(255, 255, 255, .85);
            }

            .dropdown-arrow {
                margin-left: auto;
                transition: transform .25s ease;
                font-size: .85rem;
            }

            .sidebar-dropdown-toggle[aria-expanded="true"] .dropdown-arrow {
                transform: rotate(90deg);
            }

            /* Tooltip khi thu gọn */
            .sidebar.collapsed .nav-link span {
                display: none;
            }

            .sidebar.collapsed .nav-link {
                justify-content: center;
                position: relative;
            }

            .sidebar.collapsed .nav-link:hover::after {
                content: attr(data-tooltip);
                position: absolute;
                left: 100%;
                top: 50%;
                transform: translateY(-50%);
                background: rgba(0, 0, 0, .85);
                color: #fff;
                padding: 6px 10px;
                border-radius: 6px;
                white-space: nowrap;
                margin-left: 10px;
                z-index: 1050;
            }

            .sidebar.collapsed .nav-link:hover::before {
                content: "";
                position: absolute;
                left: 100%;
                top: 50%;
                transform: translateY(-50%);
                border: 6px solid transparent;
                border-right-color: rgba(0, 0, 0, .85);
                margin-left: 4px;
            }

            /* Scrollbar nhẹ */
            .sidebar::-webkit-scrollbar {
                width: 4px;
            }

            .sidebar::-webkit-scrollbar-thumb {
                background: rgba(255, 255, 255, .35);
                border-radius: 2px;
            }
        </style>

        <nav class="sidebar" id="sidebar">
            <div class="sidebar-header">
                <a href="${pageContext.request.contextPath}/" class="sidebar-logo-link" data-tooltip="Home">
                    <i class="fas fa-users-cog fs-2"></i>
                    <div class="sidebar-brand-text">
                        <h4>HRMS</h4>
                        <small>Human Resource Management</small>
                    </div>
                </a>
            </div>

            <ul class="sidebar-nav nav flex-column">
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/dashboard"
                        class="nav-link ${param.currentPage == 'dashboard' ? 'active' : ''}" data-tooltip="Dashboard">
                        <i class="fas fa-tachometer-alt"></i><span>Dashboard</span>
                    </a>
                </li>

                <!-- Dropdown: Employee Management -->
                <li class="nav-item">
                    <a href="#" class="nav-link sidebar-dropdown-toggle" data-target="employee-submenu"
                        aria-expanded="false" data-tooltip="Employee Management">
                        <i class="fas fa-users"></i><span>Employee Management</span>
                        <i class="fas fa-chevron-right dropdown-arrow"></i>
                    </a>
                    <ul class="sidebar-submenu" id="employee-submenu">
                        <li>
                            <a href="${pageContext.request.contextPath}/employees/users"
                                class="nav-link ${param.currentPage == 'user-list' ? 'active' : ''}">
                                <i class="fas fa-user"></i><span>User List</span>
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/employees/accounts"
                                class="nav-link ${param.currentPage == 'account-list' ? 'active' : ''}">
                                <i class="fas fa-user-shield"></i><span>Account List</span>
                            </a>
                        </li>
                    </ul>
                </li>

                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/attendance"
                        class="nav-link ${param.currentPage == 'attendance' ? 'active' : ''}" data-tooltip="Attendance">
                        <i class="fas fa-clock"></i><span>Attendance</span>
                    </a>
                </li>

                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/payroll"
                        class="nav-link ${param.currentPage == 'payroll' ? 'active' : ''}" data-tooltip="Payroll">
                        <i class="fas fa-money-bill-wave"></i><span>Payroll</span>
                    </a>
                </li>

                <!-- Dropdown: Requests -->
                <li class="nav-item">
                    <a href="#" class="nav-link sidebar-dropdown-toggle" data-target="requests-submenu"
                        aria-expanded="false" data-tooltip="Requests">
                        <i class="fas fa-clipboard-list"></i><span>Requests</span>
                        <i class="fas fa-chevron-right dropdown-arrow"></i>
                    </a>
                    <ul class="sidebar-submenu" id="requests-submenu">
                        <li>
                            <a href="${pageContext.request.contextPath}/requests"
                                class="nav-link ${param.currentPage == 'request-list' ? 'active' : ''}">
                                <i class="fas fa-list"></i><span>Request List</span>
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/requests/leave/create"
                                class="nav-link ${param.currentPage == 'leave-request' ? 'active' : ''}">
                                <i class="fas fa-calendar-times"></i><span>Leave Request</span>
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/requests/ot/create"
                                class="nav-link ${param.currentPage == 'ot-request' ? 'active' : ''}">
                                <i class="fas fa-business-time"></i><span>OT Request</span>
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/requests/appeal/create"
                                class="nav-link ${param.currentPage == 'appeal-request' ? 'active' : ''}">
                                <i class="fas fa-exclamation-circle"></i><span>Attendance Appeal</span>
                            </a>
                        </li>
                    </ul>
                </li>

                <!-- Settings - Admin Only (position_id = 6) -->
                <c:if test="${sessionScope.user != null && sessionScope.user.positionId == 6}">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/settings"
                            class="nav-link ${param.currentPage == 'settings' ? 'active' : ''}"
                            data-tooltip="Settings">
                            <i class="fas fa-cog"></i><span>Settings</span>
                        </a>
                    </li>
                </c:if>

                <!-- Admin Only -->
                <c:if test="${sessionScope.userRole == 'ADMIN' || sessionScope.userRole == 'Admin'}">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/admin/accounts"
                            class="nav-link ${param.currentPage == 'accounts' ? 'active' : ''}"
                            data-tooltip="Account Management">
                            <i class="fas fa-user-shield"></i><span>Account Management</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/reports"
                            class="nav-link ${param.currentPage == 'reports' ? 'active' : ''}" data-tooltip="Reports">
                            <i class="fas fa-chart-bar"></i><span>Reports</span>
                        </a>
                    </li>
                </c:if>

                <!-- Profile & Logout -->
                <li class="nav-item mt-3">
                    <a href="${pageContext.request.contextPath}/profile"
                        class="nav-link ${param.currentPage == 'profile' ? 'active' : ''}" data-tooltip="Profile">
                        <i class="fas fa-user"></i><span>Profile</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/profile/change-password"
                        class="nav-link ${param.currentPage == 'change-password' ? 'active' : ''}"
                        data-tooltip="Change Password">
                        <i class="fas fa-key"></i><span>Change Password</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/" class="nav-link" data-tooltip="Back to Home">
                        <i class="fas fa-home"></i><span>Back to Home</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/logout" class="nav-link" data-tooltip="Logout">
                        <i class="fas fa-sign-out-alt"></i><span>Logout</span>
                    </a>
                </li>
            </ul>
        </nav>

        <script>
            (function () {
                const sidebar = document.getElementById('sidebar');
                const mq = window.matchMedia('(max-width: 768px)');

                function applyInitial() {
                    const collapsed = localStorage.getItem('sidebar-collapsed') === '1';
                    if (!mq.matches) { // desktop
                        document.body.classList.toggle('sidebar-collapsed', collapsed);
                        sidebar.classList.toggle('collapsed', collapsed);
                        sidebar.classList.remove('show');
                    } else {
                        document.body.classList.remove('sidebar-collapsed');
                        sidebar.classList.remove('collapsed');
                    }
                }

                // Gắn mọi thứ SAU khi toàn bộ DOM đã sẵn sàng (đảm bảo header đã có nút .toggle-sidebar)
                document.addEventListener('DOMContentLoaded', function () {
                    applyInitial();
                    mq.addEventListener('change', applyInitial);

                    // Lấy nút toggle ở header (giờ đã có trong DOM)
                    const toggles = document.querySelectorAll('.toggle-sidebar');

                    // Toggle: desktop -> collapse; mobile -> off-canvas
                    toggles.forEach(function (btn) {
                        btn.addEventListener('click', function (e) {
                            e.preventDefault();
                            if (mq.matches) {
                                sidebar.classList.toggle('show');
                            } else {
                                const isCollapsed = sidebar.classList.toggle('collapsed');
                                document.body.classList.toggle('sidebar-collapsed', isCollapsed);
                                localStorage.setItem('sidebar-collapsed', isCollapsed ? '1' : '0');
                            }
                        });
                    });

                    // Dropdown submenu
                    document.querySelectorAll('.sidebar-dropdown-toggle').forEach(function (tg) {
                        tg.addEventListener('click', function (e) {
                            e.preventDefault();
                            const id = tg.getAttribute('data-target');
                            const menu = document.getElementById(id);
                            const open = tg.getAttribute('aria-expanded') === 'true';
                            tg.setAttribute('aria-expanded', (!open).toString());
                            if (menu) menu.classList.toggle('show', !open);
                        });
                    });

                    // Tự đóng off-canvas khi click ra ngoài (mobile)
                    document.addEventListener('click', function (e) {
                        if (mq.matches && sidebar.classList.contains('show')) {
                            if (!sidebar.contains(e.target) && !e.target.closest('.toggle-sidebar')) {
                                sidebar.classList.remove('show');
                            }
                        }
                    });
                });
            })();
        </script>