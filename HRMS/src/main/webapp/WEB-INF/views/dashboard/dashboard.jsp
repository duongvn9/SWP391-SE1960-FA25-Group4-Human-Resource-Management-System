<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <jsp:include page="../layout/head.jsp">
                <jsp:param name="pageTitle" value="Home Page" />
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
                                        0}</span>
                                    <div class="stat-label">Total Employees</div>
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

                <!-- Footer -->
                <jsp:include page="../layout/dashboard-footer.jsp" />
            </div>
        </body>

        </html>