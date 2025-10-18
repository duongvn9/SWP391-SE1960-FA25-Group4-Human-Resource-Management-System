<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <jsp:include page="../layout/head.jsp">
                <jsp:param name="pageTitle" value="Create User" />
                <jsp:param name="pageCss" value="dashboard.css" />
            </jsp:include>
            <style>
                .form-card {
                    background: #fff;
                    border-radius: 10px;
                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
                    padding: 2rem;
                    margin-bottom: 2rem;
                }

                .form-section {
                    margin-bottom: 2rem;
                }

                .form-section-title {
                    font-size: 1.1rem;
                    font-weight: 600;
                    color: #495057;
                    margin-bottom: 1rem;
                    padding-bottom: 0.5rem;
                    border-bottom: 2px solid #e9ecef;
                }

                .form-row {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                    gap: 1.5rem;
                    margin-bottom: 1.5rem;
                }

                .form-group {
                    display: flex;
                    flex-direction: column;
                }

                .form-group label {
                    font-weight: 500;
                    color: #495057;
                    margin-bottom: 0.5rem;
                }

                .form-group label .required {
                    color: #dc3545;
                }

                .form-control {
                    padding: 0.75rem;
                    border: 1px solid #ced4da;
                    border-radius: 6px;
                    font-size: 0.95rem;
                }

                .form-control:focus {
                    outline: none;
                    border-color: #667eea;
                    box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
                }

                .form-actions {
                    display: flex;
                    gap: 1rem;
                    justify-content: flex-end;
                    margin-top: 2rem;
                    padding-top: 2rem;
                    border-top: 1px solid #e9ecef;
                }

                .btn {
                    padding: 0.75rem 2rem;
                    border-radius: 6px;
                    font-weight: 500;
                    border: none;
                    cursor: pointer;
                    transition: all 0.2s ease;
                }

                .btn-primary {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: #fff;
                }

                .btn-primary:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
                }

                .btn-secondary {
                    background: #6c757d;
                    color: #fff;
                }

                .btn-secondary:hover {
                    background: #5a6268;
                }

                .alert {
                    padding: 1rem 1.5rem;
                    border-radius: 6px;
                    margin-bottom: 1.5rem;
                }

                .alert-danger {
                    background-color: #f8d7da;
                    color: #721c24;
                    border: 1px solid #f5c6cb;
                }

                .page-header {
                    background: #fff;
                    padding: 1.5rem;
                    border-radius: 10px;
                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
                    margin-bottom: 1.5rem;
                }

                .breadcrumb {
                    background: transparent;
                    padding: 0;
                    margin-bottom: 0.5rem;
                }

                .breadcrumb-item+.breadcrumb-item::before {
                    content: "›";
                    color: #6c757d;
                }

                .breadcrumb-item a {
                    color: #667eea;
                    text-decoration: none;
                }
            </style>
        </head>

        <body>
            <!-- Sidebar -->
            <jsp:include page="../layout/sidebar.jsp">
                <jsp:param name="currentPage" value="user-list" />
            </jsp:include>

            <!-- Main Content -->
            <div class="main-content" id="main-content">
                <!-- Header -->
                <jsp:include page="../layout/dashboard-header.jsp" />

                <!-- Content Area -->
                <div class="content-area">
                    <!-- Page Header -->
                    <div class="page-header">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a
                                        href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                                <li class="breadcrumb-item"><a
                                        href="${pageContext.request.contextPath}/employees/users">User List</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Create User</li>
                            </ol>
                        </nav>
                        <h2 class="mb-0">Create New User</h2>
                    </div>

                    <!-- Error Message -->
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger">
                            <i class="fas fa-exclamation-circle me-2"></i>${errorMessage}
                        </div>
                    </c:if>

                    <!-- Form -->
                    <form method="post" action="${pageContext.request.contextPath}/employees/users/create"
                        id="createUserForm">
                        <div class="form-card">
                            <!-- Basic Information -->
                            <div class="form-section">
                                <h3 class="form-section-title">Basic Information</h3>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="employeeCode">Employee Code <span class="required">*</span></label>
                                        <input type="text" id="employeeCode" name="employeeCode" class="form-control"
                                            value="${employeeCode}" required maxlength="50">
                                    </div>
                                    <div class="form-group">
                                        <label for="fullName">Full Name <span class="required">*</span></label>
                                        <input type="text" id="fullName" name="fullName" class="form-control"
                                            value="${fullName}" required maxlength="255">
                                    </div>
                                </div>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="cccd">CCCD/ID Number</label>
                                        <input type="text" id="cccd" name="cccd" class="form-control" value="${cccd}"
                                            maxlength="32">
                                    </div>
                                    <div class="form-group">
                                        <label for="phone">Phone Number</label>
                                        <input type="tel" id="phone" name="phone" class="form-control" value="${phone}"
                                            maxlength="32">
                                    </div>
                                </div>
                            </div>

                            <!-- Contact & Position -->
                            <div class="form-section">
                                <h3 class="form-section-title">Contact & Position</h3>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="emailCompany">Company Email <span class="required">*</span></label>
                                        <input type="email" id="emailCompany" name="emailCompany" class="form-control"
                                            value="${emailCompany}" required maxlength="255">
                                    </div>
                                    <div class="form-group">
                                        <label for="departmentId">Department</label>
                                        <select id="departmentId" name="departmentId" class="form-control">
                                            <option value="">Select Department</option>
                                            <c:forEach var="dept" items="${departments}">
                                                <option value="${dept.id}" ${selectedDepartment==dept.id ? 'selected'
                                                    : '' }>
                                                    ${dept.name}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="positionId">Position</label>
                                        <select id="positionId" name="positionId" class="form-control">
                                            <option value="">Select Position</option>
                                            <c:forEach var="pos" items="${positions}">
                                                <option value="${pos.id}" ${selectedPosition==pos.id ? 'selected' : ''
                                                    }>
                                                    ${pos.name}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="status">Status</label>
                                        <select id="status" name="status" class="form-control">
                                            <option value="active" ${selectedStatus=='active' ? 'selected' : '' }>Active
                                            </option>
                                            <option value="inactive" ${selectedStatus=='inactive' ? 'selected' : '' }>
                                                Inactive</option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <!-- Employment Details -->
                            <div class="form-section">
                                <h3 class="form-section-title">Employment Details</h3>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="dateJoined">Date Joined</label>
                                        <input type="date" id="dateJoined" name="dateJoined" class="form-control"
                                            value="${dateJoined}">
                                    </div>
                                    <div class="form-group">
                                        <label for="startWorkDate">Start Work Date</label>
                                        <input type="date" id="startWorkDate" name="startWorkDate" class="form-control"
                                            value="${startWorkDate}">
                                    </div>
                                </div>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="baseSalary">Base Salary</label>
                                        <input type="number" id="baseSalary" name="baseSalary" class="form-control"
                                            value="${baseSalary}" min="0" step="0.01">
                                    </div>
                                    <div class="form-group">
                                        <label for="salaryCurrency">Currency</label>
                                        <select id="salaryCurrency" name="salaryCurrency" class="form-control">
                                            <option value="VND" ${selectedCurrency=='VND' ? 'selected' : '' }>VND
                                            </option>
                                            <option value="USD" ${selectedCurrency=='USD' ? 'selected' : '' }>USD
                                            </option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <!-- Form Actions -->
                            <div class="form-actions">
                                <a href="${pageContext.request.contextPath}/employees/users" class="btn btn-secondary">
                                    <i class="fas fa-times me-2"></i>Cancel
                                </a>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save me-2"></i>Create User
                                </button>
                            </div>
                        </div>
                    </form>
                </div>

                <!-- Footer -->
                <jsp:include page="../layout/dashboard-footer.jsp" />
            </div>

            <script>
                // Form validation
                document.getElementById('createUserForm').addEventListener('submit', function (e) {
                    const employeeCode = document.getElementById('employeeCode').value.trim();
                    const fullName = document.getElementById('fullName').value.trim();
                    const emailCompany = document.getElementById('emailCompany').value.trim();

                    if (!employeeCode || !fullName || !emailCompany) {
                        e.preventDefault();
                        alert('Please fill in all required fields');
                        return false;
                    }

                    // Email validation
                    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    if (!emailPattern.test(emailCompany)) {
                        e.preventDefault();
                        alert('Please enter a valid email address');
                        return false;
                    }
                });
            </script>
        </body>

        </html>