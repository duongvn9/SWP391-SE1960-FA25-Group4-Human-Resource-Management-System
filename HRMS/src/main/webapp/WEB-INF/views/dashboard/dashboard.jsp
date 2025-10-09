<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

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
                            <h2>Welcome back, ${sessionScope.userFullName != null ? sessionScope.userFullName :
                                'Admin'}!</h2>
                            <p class="text-muted">HR Management System Dashboard Overview</p>
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
                                    <span class="stat-number counter">${totalEmployees != null ? totalEmployees :
                                        156}</span>
                                    <div class="stat-label">Total Employees</div>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-3 col-md-6 mb-3">
                            <div class="dashboard-card">
                                <div class="stat-card success">
                                    <div class="icon">
                                        <i class="fas fa-user-check"></i>
                                    </div>
                                    <span class="stat-number counter">${presentToday != null ? presentToday :
                                        142}</span>
                                    <div class="stat-label">Present Today</div>
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
                                    <div class="stat-label">On Leave Today</div>
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
                                    <div class="stat-label">Absent Today</div>
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
                                        <i class="fas fa-bolt me-2 text-warning"></i>Quick Admin Actions
                                    </h5>
                                    <div class="row">
                                        <div class="col-lg-3 col-md-6 mb-3">
                                            <a href="${pageContext.request.contextPath}/admin/accounts"
                                                class="btn btn-outline-primary w-100 py-3 text-decoration-none">
                                                <i class="fas fa-user-shield fs-2 d-block mb-2"></i>
                                                <strong>Account Management</strong>
                                                <br>
                                                <small class="text-muted">Create, edit, delete accounts</small>
                                            </a>
                                        </div>
                                        <div class="col-lg-3 col-md-6 mb-3">
                                            <a href="${pageContext.request.contextPath}/system/settings"
                                                class="btn btn-outline-secondary w-100 py-3 text-decoration-none">
                                                <i class="fas fa-cogs fs-2 d-block mb-2"></i>
                                                <strong>System Settings</strong>
                                                <br>
                                                <small class="text-muted">System configuration</small>
                                            </a>
                                        </div>
                                        <div class="col-lg-3 col-md-6 mb-3">
                                            <a href="${pageContext.request.contextPath}/reports"
                                                class="btn btn-outline-info w-100 py-3 text-decoration-none">
                                                <i class="fas fa-chart-bar fs-2 d-block mb-2"></i>
                                                <strong>Comprehensive Reports</strong>
                                                <br>
                                                <small class="text-muted">View detailed reports</small>
                                            </a>
                                        </div>
                                        <div class="col-lg-3 col-md-6 mb-3">
                                            <a href="${pageContext.request.contextPath}/identity/employees"
                                                class="btn btn-outline-success w-100 py-3 text-decoration-none">
                                                <i class="fas fa-users-cog fs-2 d-block mb-2"></i>
                                                <strong>Employee Management</strong>
                                                <br>
                                                <small class="text-muted">Add, edit information</small>
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
                                <h5 class="mb-3"><i class="fas fa-chart-line me-2"></i>Attendance Statistics - Last 7
                                    Days</h5>
                                <div class="chart-container">
                                    <canvas id="attendanceChart"></canvas>
                                </div>
                            </div>
                        </div>

                        <!-- Recent Notifications -->
                        <div class="col-lg-4 mb-4">
                            <div class="dashboard-card">
                                <h5 class="mb-3"><i class="fas fa-bell me-2"></i>Recent Notifications</h5>
                                <div class="notification-list">
                                    <div class="notification-item">
                                        <div class="notification-icon info">
                                            <i class="fas fa-info"></i>
                                        </div>
                                        <div class="flex-grow-1">
                                            <strong>New Leave Request</strong>
                                            <p class="mb-1 small">Nguyen Van A requested leave from Oct 15 - Oct 17</p>
                                            <small class="text-muted">2 hours ago</small>
                                        </div>
                                    </div>

                                    <div class="notification-item">
                                        <div class="notification-icon success">
                                            <i class="fas fa-check"></i>
                                        </div>
                                        <div class="flex-grow-1">
                                            <strong>Payroll Generated</strong>
                                            <p class="mb-1 small">September payroll is ready</p>
                                            <small class="text-muted">1 day ago</small>
                                        </div>
                                    </div>

                                    <div class="notification-item">
                                        <div class="notification-icon warning">
                                            <i class="fas fa-exclamation"></i>
                                        </div>
                                        <div class="flex-grow-1">
                                            <strong>New Employee Approval Needed</strong>
                                            <p class="mb-1 small">3 recruitment applications need review</p>
                                            <small class="text-muted">2 days ago</small>
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
                                    <h5 class="mb-0"><i class="fas fa-history me-2"></i>Recent Activities</h5>
                                </div>
                                <div class="table-responsive">
                                    <table class="table">
                                        <thead>
                                            <tr>
                                                <th>Time</th>
                                                <th>Employee</th>
                                                <th>Activity</th>
                                                <th>Status</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>08:30 - Today</td>
                                                <td>Nguyen Van A</td>
                                                <td>Checked in</td>
                                                <td><span class="badge badge-status bg-success">Success</span></td>
                                            </tr>
                                            <tr>
                                                <td>08:45 - Today</td>
                                                <td>Tran Thi B</td>
                                                <td>Submitted leave request</td>
                                                <td><span class="badge badge-status bg-warning">Pending</span></td>
                                            </tr>
                                            <tr>
                                                <td>09:00 - Today</td>
                                                <td>Le Van C</td>
                                                <td>Updated personal information</td>
                                                <td><span class="badge badge-status bg-info">Completed</span></td>
                                            </tr>
                                            <tr>
                                                <td>17:30 - Yesterday</td>
                                                <td>Pham Thi D</td>
                                                <td>Checked out</td>
                                                <td><span class="badge badge-status bg-success">Success</span></td>
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