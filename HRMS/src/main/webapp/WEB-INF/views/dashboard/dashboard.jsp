<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <jsp:include page="../layout/head.jsp">
                    <jsp:param name="pageTitle" value="HR Dashboard" />
                    <jsp:param name="pageCss" value="dashboard.css" />
                </jsp:include>
                <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
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
                                <c:if test="${canViewDashboardData}">
                                    <p class="text-muted">HR Management System Dashboard - KPIs Overview</p>
                                </c:if>
                            </div>
                        </div>

                        <!-- Hiển thị KPI và Charts cho HR, HRM và Admin -->
                        <c:if test="${canViewDashboardData || isAdmin}">
                            <!-- Employee Statistics Cards -->
                            <div class="row mb-4">
                                <div class="${isAdmin ? 'col-lg-6' : 'col-lg-3'} col-md-6 mb-3">
                                    <div class="dashboard-card">
                                        <div class="stat-card primary">
                                            <div class="icon">
                                                <i class="fas fa-users"></i>
                                            </div>
                                            <span class="stat-number">${kpis.totalEmployees}</span>
                                            <div class="stat-label">Total Employees</div>
                                            <small class="text-muted">${kpis.totalDepartments} departments</small>
                                        </div>
                                    </div>
                                </div>

                                <c:if test="${isAdmin}">
                                    <div class="col-lg-6 col-md-6 mb-3">
                                        <div class="dashboard-card">
                                            <div class="stat-card success">
                                                <div class="icon">
                                                    <i class="fas fa-user-shield"></i>
                                                </div>
                                                <span class="stat-number">${kpis.totalAccounts}</span>
                                                <div class="stat-label">Total Accounts</div>
                                                <small class="text-muted">${kpis.activeAccounts} active</small>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>

                                <c:if test="${!isAdmin}">
                                    <!-- Recruitment Card with success color -->
                                    <div class="col-lg-3 col-md-6 mb-3">
                                        <div class="dashboard-card">
                                            <div class="stat-card success">
                                                <div class="icon">
                                                    <i class="fas fa-user-plus"></i>
                                                </div>
                                                <span class="stat-number">${kpis.pendingRecruitmentRequests}</span>
                                                <div class="stat-label">Pending Recruitment Requests</div>
                                                <small class="text-muted">All pending requests</small>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-lg-3 col-md-6 mb-3">
                                        <div class="dashboard-card">
                                            <div class="stat-card warning">
                                                <div class="icon">
                                                    <i class="fas fa-clock"></i>
                                                </div>
                                                <span class="stat-number">${kpis.pendingOtRequests}</span>
                                                <div class="stat-label">Pending OT Requests</div>
                                                <small class="text-muted">All pending requests</small>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-lg-3 col-md-6 mb-3">
                                        <div class="dashboard-card">
                                            <div class="stat-card info">
                                                <div class="icon">
                                                    <i class="fas fa-calendar-alt"></i>
                                                </div>
                                                <span class="stat-number">${kpis.pendingLeaveRequests}</span>
                                                <div class="stat-label">Pending Leave Requests</div>
                                                <small class="text-muted">All pending requests</small>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </div>

                            <!-- Charts Row 1 -->
                            <div class="row mb-4">
                                <!-- Department Distribution -->
                                <div class="${isAdmin ? 'col-lg-6' : 'col-lg-3'} mb-3">
                                    <div class="dashboard-card">
                                        <h5 class="mb-3">
                                            <i class="fas fa-building me-2"></i>Employees by Department
                                        </h5>
                                        <div class="chart-container">
                                            <canvas id="departmentChart"></canvas>
                                        </div>
                                    </div>
                                </div>

                                <!-- Account Status - Only for Admin -->
                                <c:if test="${isAdmin}">
                                    <div class="col-lg-6 mb-3">
                                        <div class="dashboard-card">
                                            <h5 class="mb-3">
                                                <i class="fas fa-user-shield me-2"></i>Accounts by Status
                                            </h5>
                                            <div class="chart-container">
                                                <canvas id="accountStatusChart"></canvas>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>

                                <!-- Recruitment Request Status - Only for HR/HRM -->
                                <c:if test="${!isAdmin}">
                                    <div class="col-lg-3 mb-3">
                                        <div class="dashboard-card">
                                            <h5 class="mb-3">
                                                <i class="fas fa-user-plus me-2"></i>Recruitment Request Status
                                            </h5>
                                            <div class="chart-container">
                                                <canvas id="recruitmentRequestStatusChart"></canvas>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- OT Request Status - Only for HR/HRM -->
                                    <div class="col-lg-3 mb-3">
                                        <div class="dashboard-card">
                                            <h5 class="mb-3">
                                                <i class="fas fa-clock me-2"></i>OT Request Status
                                            </h5>
                                            <div class="chart-container">
                                                <canvas id="otRequestStatusChart"></canvas>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Leave Request Status - Only for HR/HRM -->
                                    <div class="col-lg-3 mb-3">
                                        <div class="dashboard-card">
                                            <h5 class="mb-3">
                                                <i class="fas fa-calendar-alt me-2"></i>Leave Request Status
                                            </h5>
                                            <div class="chart-container">
                                                <canvas id="leaveRequestStatusChart"></canvas>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </div>

                            <!-- Charts Row 2 - Only for HR/HRM -->
                            <c:if test="${!isAdmin}">
                                <div class="row mb-4">
                                    <!-- Attendance Trend -->
                                    <div class="col-lg-6 mb-3">
                                        <div class="dashboard-card">
                                            <h5 class="mb-3">
                                                <i class="fas fa-chart-line me-2"></i>Attendance Rate Trend (6 Months)
                                            </h5>
                                            <div class="chart-container chart-large">
                                                <canvas id="attendanceTrendChart"></canvas>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- OT Trend -->
                                    <div class="col-lg-6 mb-3">
                                        <div class="dashboard-card">
                                            <h5 class="mb-3">
                                                <i class="fas fa-chart-area me-2"></i>Overtime Hours Trend (6 Months)
                                            </h5>
                                            <div class="chart-container chart-large">
                                                <canvas id="otTrendChart"></canvas>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:if>

                            <!-- Payroll Summary - Only for HR/HRM -->
                            <c:if test="${!isAdmin}">
                                <div class="row mb-4">
                                    <div class="col-lg-4 col-md-6 mb-3">
                                        <div class="dashboard-card">
                                            <div class="stat-card-horizontal">
                                                <div class="icon-wrapper bg-success">
                                                    <i class="fas fa-money-bill-wave"></i>
                                                </div>
                                                <div class="stat-info">
                                                    <div class="stat-label">Total Payroll (This Month)</div>
                                                    <div class="stat-number">
                                                        <fmt:formatNumber value="${kpis.totalPayrollThisMonth}"
                                                            type="currency" currencySymbol="$" />
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-lg-4 col-md-6 mb-3">
                                        <div class="dashboard-card">
                                            <div class="stat-card-horizontal">
                                                <div class="icon-wrapper bg-info">
                                                    <i class="fas fa-file-invoice-dollar"></i>
                                                </div>
                                                <div class="stat-info">
                                                    <div class="stat-label">Payslips Generated</div>
                                                    <div class="stat-number">${kpis.payslipsGenerated}</div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-lg-4 col-md-6 mb-3">
                                        <div class="dashboard-card">
                                            <div class="stat-card-horizontal">
                                                <div class="icon-wrapper bg-primary">
                                                    <i class="fas fa-calculator"></i>
                                                </div>
                                                <div class="stat-info">
                                                    <div class="stat-label">Average Salary</div>
                                                    <div class="stat-number">
                                                        <fmt:formatNumber value="${kpis.averageSalary}" type="currency"
                                                            currencySymbol="$" />
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:if>

                            <!-- Quick Actions for Admin -->
                            <c:if test="${isAdmin}">
                                <div class="row mb-4">
                                    <div class="col-12">
                                        <div class="dashboard-card">
                                            <h5 class="mb-3">
                                                <i class="fas fa-bolt me-2 text-warning"></i>Quick Admin Actions
                                            </h5>
                                            <div class="row">
                                                <div class="col-lg-4 col-md-6 mb-3">
                                                    <a href="${pageContext.request.contextPath}/admin/accounts"
                                                        class="btn btn-outline-primary w-100 py-3 text-decoration-none">
                                                        <i class="fas fa-user-shield fs-2 d-block mb-2"></i>
                                                        <strong>Account Management</strong>
                                                        <br>
                                                        <small class="text-muted">Create, edit, delete accounts</small>
                                                    </a>
                                                </div>
                                                <div class="col-lg-4 col-md-6 mb-3">
                                                    <a href="${pageContext.request.contextPath}/settings"
                                                        class="btn btn-outline-secondary w-100 py-3 text-decoration-none">
                                                        <i class="fas fa-cogs fs-2 d-block mb-2"></i>
                                                        <strong>System Settings</strong>
                                                        <br>
                                                        <small class="text-muted">System configuration</small>
                                                    </a>
                                                </div>
                                                <div class="col-lg-4 col-md-6 mb-3">
                                                    <a href="${pageContext.request.contextPath}/employees/users"
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
                        </c:if>
                        <!-- Kết thúc phần chỉ dành cho HR và HRM -->
                    </div>

                    <!-- Confirmation Modal -->
                    <div class="modal fade" id="chartClickModal" tabindex="-1" aria-labelledby="chartClickModalLabel"
                        aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="chartClickModalLabel">View Details</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                                </div>
                                <div class="modal-body" id="modalMessage">
                                    <!-- Dynamic message will be inserted here -->
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary"
                                        data-bs-dismiss="modal">Cancel</button>
                                    <button type="button" class="btn btn-primary" id="confirmViewBtn">View List</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Footer -->
                    <jsp:include page="../layout/dashboard-footer.jsp" />
                </div>

                <!-- Load charts cho HR, HRM và Admin -->
                <c:if test="${canViewDashboardData || isAdmin}">
                    <script>
                        // Chart.js configuration
                        Chart.defaults.font.family = "'Inter', sans-serif";
                        Chart.defaults.color = '#6c757d';

                        // Department Distribution Chart
                        const deptCtx = document.getElementById('departmentChart');
                        let deptChart;
                        if (deptCtx) {
                            const deptData = {
                                labels: [
                                    <c:forEach items="${kpis.employeesByDepartment}" var="entry" varStatus="status">
                                        '${entry.key}'${!status.last ? ',' : ''}
                                    </c:forEach>
                                ],
                                datasets: [{
                                    data: [
                                        <c:forEach items="${kpis.employeesByDepartment}" var="entry" varStatus="status">
                                            ${entry.value}${!status.last ? ',' : ''}
                                        </c:forEach>
                                    ],
                                    backgroundColor: [
                                        '#0d6efd', '#6610f2', '#6f42c1', '#d63384', '#dc3545',
                                        '#fd7e14', '#ffc107', '#198754', '#20c997', '#0dcaf0'
                                    ]
                                }]
                            };
                            deptChart = new Chart(deptCtx, {
                                type: 'doughnut',
                                data: deptData,
                                options: {
                                    responsive: true,
                                    maintainAspectRatio: false,
                                    plugins: {
                                        legend: {
                                            position: 'right',
                                            labels: {
                                                boxWidth: 15,
                                                padding: 10,
                                                font: { size: 13 }
                                            }
                                        }
                                    },
                                    onClick: (event, elements) => {
                                        if (elements.length > 0) {
                                            const index = elements[0].index;
                                            const departmentName = deptData.labels[index];
                                            console.log('Clicked department:', departmentName);
                                            showChartModal(
                                                'View Employee List',
                                                `Do you want to view <strong>${departmentName}</strong> employee list?`,
                                                '${pageContext.request.contextPath}/employees/users?department=' + encodeURIComponent(departmentName)
                                            );
                                        }
                                    }
                                }
                            });
                        }

                        // Account Status Chart (Admin only) - Bỏ Locked
                        const accCtx = document.getElementById('accountStatusChart');
                        if (accCtx) {
                            const activeAccounts = <c:out value="${kpis.activeAccounts}" default="0" />;
                            const inactiveAccounts = <c:out value="${kpis.inactiveAccounts}" default="0" />;
                            const accData = {
                                labels: ['Active', 'Inactive'],
                                datasets: [{
                                    data: [activeAccounts, inactiveAccounts],
                                    backgroundColor: ['#198754', '#6c757d']
                                }]
                            };
                            new Chart(accCtx, {
                                type: 'pie',
                                data: accData,
                                options: {
                                    responsive: true,
                                    maintainAspectRatio: false,
                                    plugins: {
                                        legend: {
                                            position: 'right',
                                            labels: {
                                                boxWidth: 15,
                                                padding: 10,
                                                font: { size: 13 }
                                            }
                                        },
                                        tooltip: {
                                            callbacks: {
                                                label: function (context) {
                                                    const label = context.label || '';
                                                    const value = context.parsed || 0;
                                                    const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                                    const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
                                                    return label + ': ' + value + ' (' + percentage + '%)';
                                                }
                                            }
                                        }
                                    },
                                    onClick: (event, elements) => {
                                        if (elements.length > 0) {
                                            const index = elements[0].index;
                                            const statusName = accData.labels[index].toLowerCase();
                                            console.log('Clicked account status:', statusName);
                                            showChartModal(
                                                'View Account List',
                                                `Do you want to view <strong>${accData.labels[index]}</strong> accounts?`,
                                                '${pageContext.request.contextPath}/employees/accounts?status=' + statusName
                                            );
                                        }
                                    }
                                }
                            });
                        }

                        // Map colors based on status name (shared by OT and Leave charts)
                        const statusColors = {
                            'PENDING': '#ffc107',    // Yellow
                            'APPROVED': '#198754',   // Green
                            'REJECTED': '#dc3545',   // Red
                            'DRAFT': '#6c757d'       // Gray
                        };

                        // OT Request Status Chart
                        const otReqCtx = document.getElementById('otRequestStatusChart');
                        if (otReqCtx) {
                            const otReqLabels = [
                                <c:forEach items="${kpis.otRequestsByStatus}" var="entry" varStatus="status">
                                    '${entry.key}'${!status.last ? ',' : ''}
                                </c:forEach>
                            ];

                            const otReqColors = otReqLabels.map(label => statusColors[label] || '#6c757d');

                            const otReqData = {
                                labels: otReqLabels,
                                datasets: [{
                                    data: [
                                        <c:forEach items="${kpis.otRequestsByStatus}" var="entry" varStatus="status">
                                            ${entry.value}${!status.last ? ',' : ''}
                                        </c:forEach>
                                    ],
                                    backgroundColor: otReqColors
                                }]
                            };
                            new Chart(otReqCtx, {
                                type: 'pie',
                                data: otReqData,
                                options: {
                                    responsive: true,
                                    maintainAspectRatio: false,
                                    plugins: {
                                        legend: {
                                            position: 'right',
                                            labels: {
                                                boxWidth: 15,
                                                padding: 10,
                                                font: { size: 13 }
                                            }
                                        }
                                    },
                                    onClick: (event, elements) => {
                                        if (elements.length > 0) {
                                            const index = elements[0].index;
                                            const statusName = otReqData.labels[index];
                                            console.log('Clicked OT request status:', statusName);
                                            showChartModal(
                                                'View OT Request List',
                                                `Do you want to view all <strong>${statusName}</strong> OT requests?`,
                                                '${pageContext.request.contextPath}/requests/ot?status=' + encodeURIComponent(statusName)
                                            );
                                        }
                                    }
                                }
                            });
                        }

                        // Leave Request Status Chart
                        const leaveReqCtx = document.getElementById('leaveRequestStatusChart');
                        if (leaveReqCtx) {
                            const leaveReqLabels = [
                                <c:forEach items="${kpis.leaveRequestsByStatus}" var="entry" varStatus="status">
                                    '${entry.key}'${!status.last ? ',' : ''}
                                </c:forEach>
                            ];

                            const leaveReqColors = leaveReqLabels.map(label => statusColors[label] || '#6c757d');

                            const leaveReqData = {
                                labels: leaveReqLabels,
                                datasets: [{
                                    data: [
                                        <c:forEach items="${kpis.leaveRequestsByStatus}" var="entry" varStatus="status">
                                            ${entry.value}${!status.last ? ',' : ''}
                                        </c:forEach>
                                    ],
                                    backgroundColor: leaveReqColors
                                }]
                            };
                            new Chart(leaveReqCtx, {
                                type: 'pie',
                                data: leaveReqData,
                                options: {
                                    responsive: true,
                                    maintainAspectRatio: false,
                                    plugins: {
                                        legend: {
                                            position: 'right',
                                            labels: {
                                                boxWidth: 15,
                                                padding: 10,
                                                font: { size: 13 }
                                            }
                                        }
                                    },
                                    onClick: (event, elements) => {
                                        if (elements.length > 0) {
                                            const index = elements[0].index;
                                            const statusName = leaveReqData.labels[index];
                                            console.log('Clicked leave request status:', statusName);
                                            showChartModal(
                                                'View Leave Request List',
                                                `Do you want to view all <strong>${statusName}</strong> leave requests?`,
                                                '${pageContext.request.contextPath}/requests/leave?status=' + encodeURIComponent(statusName)
                                            );
                                        }
                                    }
                                }
                            });
                        }

                        // Recruitment Request Status Chart
                        const recruitReqCtx = document.getElementById('recruitmentRequestStatusChart');
                        if (recruitReqCtx) {
                            const recruitReqLabels = [
                                <c:forEach items="${kpis.recruitmentRequestsByStatus}" var="entry" varStatus="status">
                                    '${entry.key}'${!status.last ? ',' : ''}
                                </c:forEach>
                            ];

                            const recruitReqColors = recruitReqLabels.map(label => statusColors[label] || '#6c757d');

                            const recruitReqData = {
                                labels: recruitReqLabels,
                                datasets: [{
                                    data: [
                                        <c:forEach items="${kpis.recruitmentRequestsByStatus}" var="entry" varStatus="status">
                                            ${entry.value}${!status.last ? ',' : ''}
                                        </c:forEach>
                                    ],
                                    backgroundColor: recruitReqColors
                                }]
                            };
                            new Chart(recruitReqCtx, {
                                type: 'pie',
                                data: recruitReqData,
                                options: {
                                    responsive: true,
                                    maintainAspectRatio: false,
                                    plugins: {
                                        legend: {
                                            position: 'right',
                                            labels: {
                                                boxWidth: 15,
                                                padding: 10,
                                                font: { size: 13 }
                                            }
                                        }
                                    },
                                    onClick: (event, elements) => {
                                        if (elements.length > 0) {
                                            const index = elements[0].index;
                                            const statusName = recruitReqData.labels[index];
                                            console.log('Clicked recruitment request status:', statusName);
                                            showChartModal(
                                                'View Recruitment Request List',
                                                `Do you want to view all <strong>${statusName}</strong> recruitment requests?`,
                                                '${pageContext.request.contextPath}/requests?status=' + encodeURIComponent(statusName) + '&type=RECRUITMENT_REQUEST'
                                            );
                                        }
                                    }
                                }
                            });
                        }

                        // Attendance Trend Chart
                        const attCtx = document.getElementById('attendanceTrendChart');
                        if (attCtx) {
                            const attData = {
                                labels: [
                                    <c:forEach items="${kpis.attendanceTrend}" var="entry" varStatus="status">
                                        '${entry.key}'${!status.last ? ',' : ''}
                                    </c:forEach>
                                ],
                                datasets: [{
                                    label: 'Attendance Rate (%)',
                                    data: [
                                        <c:forEach items="${kpis.attendanceTrend}" var="entry" varStatus="status">
                                            ${entry.value}${!status.last ? ',' : ''}
                                        </c:forEach>
                                    ],
                                    borderColor: '#198754',
                                    backgroundColor: 'rgba(25, 135, 84, 0.1)',
                                    tension: 0.4,
                                    fill: true
                                }]
                            };
                            new Chart(attCtx, {
                                type: 'line',
                                data: attData,
                                options: {
                                    responsive: true,
                                    maintainAspectRatio: false,
                                    plugins: {
                                        legend: { display: false }
                                    },
                                    scales: {
                                        y: {
                                            beginAtZero: true,
                                            max: 100,
                                            ticks: {
                                                font: { size: 10 },
                                                callback: function (value) {
                                                    return value + '%';
                                                }
                                            }
                                        },
                                        x: {
                                            ticks: {
                                                font: { size: 10 }
                                            }
                                        }
                                    }
                                }
                            });
                        }

                        // OT Trend Chart
                        const otCtx = document.getElementById('otTrendChart');
                        if (otCtx) {
                            const otData = {
                                labels: [
                                    <c:forEach items="${kpis.otTrend}" var="entry" varStatus="status">
                                        '${entry.key}'${!status.last ? ',' : ''}
                                    </c:forEach>
                                ],
                                datasets: [{
                                    label: 'OT Hours',
                                    data: [
                                        <c:forEach items="${kpis.otTrend}" var="entry" varStatus="status">
                                            ${entry.value}${!status.last ? ',' : ''}
                                        </c:forEach>
                                    ],
                                    borderColor: '#ffc107',
                                    backgroundColor: 'rgba(255, 193, 7, 0.2)',
                                    tension: 0.4,
                                    fill: true
                                }]
                            };
                            new Chart(otCtx, {
                                type: 'line',
                                data: otData,
                                options: {
                                    responsive: true,
                                    maintainAspectRatio: false,
                                    plugins: {
                                        legend: { display: false }
                                    },
                                    scales: {
                                        y: {
                                            beginAtZero: true,
                                            ticks: {
                                                font: { size: 10 }
                                            }
                                        },
                                        x: {
                                            ticks: {
                                                font: { size: 10 }
                                            }
                                        }
                                    }
                                }
                            });
                        }

                        // Modal handling functions
                        let redirectUrl = '';
                        const modal = new bootstrap.Modal(document.getElementById('chartClickModal'));

                        function showChartModal(title, message, url) {
                            redirectUrl = url;
                            document.getElementById('chartClickModalLabel').textContent = title;
                            document.getElementById('modalMessage').innerHTML = message;
                            modal.show();
                        }

                        document.getElementById('confirmViewBtn').addEventListener('click', function () {
                            if (redirectUrl) {
                                window.location.href = redirectUrl;
                            }
                        });

                        // Add hover effect to charts
                        document.querySelectorAll('canvas').forEach(canvas => {
                            canvas.style.cursor = 'pointer';
                            canvas.title = 'Click to view details';
                        });
                    </script>
                </c:if>
            </body>

            </html>