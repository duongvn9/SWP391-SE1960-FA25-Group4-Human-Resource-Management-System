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
                                <p class="text-muted">HR Management System Dashboard - KPIs Overview</p>
                            </div>
                        </div>

                        <!-- Employee Statistics Cards -->
                        <div class="row mb-4">
                            <div class="col-lg-3 col-md-6 mb-3">
                                <div class="dashboard-card">
                                    <div class="stat-card primary">
                                        <div class="icon">
                                            <i class="fas fa-users"></i>
                                        </div>
                                        <span class="stat-number">${kpis.totalEmployees}</span>
                                        <div class="stat-label">Total Employees</div>
                                        <small class="text-muted">${kpis.activeEmployees} active</small>
                                    </div>
                                </div>
                            </div>

                            <div class="col-lg-3 col-md-6 mb-3">
                                <div class="dashboard-card">
                                    <div class="stat-card success">
                                        <div class="icon">
                                            <i class="fas fa-user-check"></i>
                                        </div>
                                        <span class="stat-number">${kpis.presentToday}</span>
                                        <div class="stat-label">Present Today</div>
                                        <small class="text-muted">
                                            <fmt:formatNumber value="${kpis.attendanceRate}" maxFractionDigits="1" />%
                                            attendance rate
                                        </small>
                                    </div>
                                </div>
                            </div>

                            <div class="col-lg-3 col-md-6 mb-3">
                                <div class="dashboard-card">
                                    <div class="stat-card warning">
                                        <div class="icon">
                                            <i class="fas fa-clock"></i>
                                        </div>
                                        <span class="stat-number">
                                            <fmt:formatNumber value="${kpis.totalOtHoursThisMonth}"
                                                maxFractionDigits="1" />
                                        </span>
                                        <div class="stat-label">OT Hours (This Month)</div>
                                        <small class="text-muted">${kpis.pendingOtRequests} pending requests</small>
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
                                        <small class="text-muted">${kpis.approvedLeavesToday} on leave today</small>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Charts Row 1 -->
                        <div class="row mb-4">
                            <!-- Department Distribution -->
                            <div class="col-lg-6 mb-3">
                                <div class="dashboard-card">
                                    <h5 class="mb-3">
                                        <i class="fas fa-building me-2"></i>Employees by Department
                                    </h5>
                                    <div class="chart-container">
                                        <canvas id="departmentChart"></canvas>
                                    </div>
                                </div>
                            </div>

                            <!-- Request Status -->
                            <div class="col-lg-6 mb-3">
                                <div class="dashboard-card">
                                    <h5 class="mb-3">
                                        <i class="fas fa-tasks me-2"></i>Requests Status (This Month)
                                    </h5>
                                    <div class="chart-container">
                                        <canvas id="requestStatusChart"></canvas>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Charts Row 2 -->
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

                        <!-- Payroll Summary -->
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
                                                <fmt:formatNumber value="${kpis.totalPayrollThisMonth}" type="currency"
                                                    currencySymbol="$" />
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
                    </div>

                    <!-- Confirmation Modal -->
                    <div class="modal fade" id="chartClickModal" tabindex="-1" aria-labelledby="chartClickModalLabel" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="chartClickModalLabel">View Details</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body" id="modalMessage">
                                    <!-- Dynamic message will be inserted here -->
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                    <button type="button" class="btn btn-primary" id="confirmViewBtn">View List</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Footer -->
                    <jsp:include page="../layout/dashboard-footer.jsp" />
                </div>

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
                                            padding: 8,
                                            font: { size: 11 }
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

                    // Request Status Chart
                    const reqCtx = document.getElementById('requestStatusChart');
                    let reqChart;
                    if (reqCtx) {
                        const reqLabels = [
                            <c:forEach items="${kpis.requestsByStatus}" var="entry" varStatus="status">
                                '${entry.key}'${!status.last ? ',' : ''}
                            </c:forEach>
                        ];
                        
                        // Map colors based on status name
                        const statusColors = {
                            'PENDING': '#ffc107',    // Yellow
                            'APPROVED': '#198754',   // Green
                            'REJECTED': '#dc3545',   // Red
                            'DRAFT': '#6c757d'       // Gray
                        };
                        
                        const reqColors = reqLabels.map(label => statusColors[label] || '#6c757d');
                        
                        const reqData = {
                            labels: reqLabels,
                            datasets: [{
                                data: [
                                    <c:forEach items="${kpis.requestsByStatus}" var="entry" varStatus="status">
                                        ${entry.value}${!status.last ? ',' : ''}
                                    </c:forEach>
                                ],
                                backgroundColor: reqColors
                            }]
                        };
                        reqChart = new Chart(reqCtx, {
                            type: 'pie',
                            data: reqData,
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: {
                                    legend: { 
                                        position: 'right',
                                        labels: {
                                            boxWidth: 15,
                                            padding: 8,
                                            font: { size: 11 }
                                        }
                                    }
                                },
                                onClick: (event, elements) => {
                                    if (elements.length > 0) {
                                        const index = elements[0].index;
                                        const statusName = reqData.labels[index];
                                        console.log('Clicked request status:', statusName);
                                        showChartModal(
                                            'View Request List',
                                            `Do you want to view all <strong>${statusName}</strong> requests?`,
                                            '${pageContext.request.contextPath}/requests/list?status=' + encodeURIComponent(statusName) + '&scope=all'
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

                    document.getElementById('confirmViewBtn').addEventListener('click', function() {
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
            </body>

            </html>