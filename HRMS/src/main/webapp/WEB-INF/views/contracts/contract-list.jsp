<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="Contract Management - HRMS" />
        <jsp:param name="pageCss" value="dashboard.css" />
    </jsp:include>
    <style>
        .main-content {
            margin-left: 260px;
            padding: 2rem 2rem 0 2rem;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        
        .content-area {
            flex: 1;
            margin-bottom: 2rem;
        }
        
        .dashboard-footer {
            margin-left: -2rem;
            margin-right: -2rem;
            margin-bottom: 0;
        }
        
        .page-header {
            background: #fff;
            padding: 1.5rem;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
            margin-bottom: 1.5rem;
        }
        
        .table-card {
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
            overflow: hidden;
        }
        
        .table-card .card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: #fff;
            padding: 1rem 1.5rem;
        }
        
        .table thead th {
            background: #f8f9fa;
            font-weight: 600;
            border-bottom: 2px solid #dee2e6;
        }
        
        .status-badge {
            padding: 0.35rem 0.75rem;
            border-radius: 20px;
            font-size: 0.85rem;
            font-weight: 500;
        }
        
        .status-badge.active {
            background-color: #d4edda;
            color: #155724;
        }
        
        .status-badge.expired {
            background-color: #e2e3e5;
            color: #383d41;
        }
        
        .status-badge.terminated {
            background-color: #f8d7da;
            color: #721c24;
        }
        
        @media (max-width: 768px) {
            .main-content {
                margin-left: 0;
            }
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <jsp:include page="../layout/sidebar.jsp">
        <jsp:param name="currentPage" value="contracts" />
    </jsp:include>
    
    <!-- Main Content -->
    <div class="main-content" id="main-content">
        <!-- Header -->
        <jsp:include page="../layout/dashboard-header.jsp" />
        
        <!-- Content Area -->
        <div class="content-area">
            <!-- Success Message -->
            <c:if test="${not empty param.success}">
                <div class="alert alert-success alert-dismissible fade show">
                    <i class="fas fa-check-circle"></i> ${param.success}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            
            <c:if test="${not empty param.error}">
                <div class="alert alert-danger alert-dismissible fade show">
                    <i class="fas fa-exclamation-circle"></i> ${param.error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            
            <!-- Page Header -->
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center">
                    <h2 class="mb-0"><i class="fas fa-file-contract"></i> Contract Management</h2>
                    <a href="${pageContext.request.contextPath}/contracts/create" class="btn btn-primary">
                        <i class="fas fa-plus"></i> Create New Contract
                    </a>
                </div>
            </div>
            
            <!-- Filter Section -->
            <div class="card mb-3">
                <div class="card-body">
                    <form method="get" action="${pageContext.request.contextPath}/contracts" class="row g-3">
                        <div class="col-md-4">
                            <label for="search" class="form-label">Search</label>
                            <input type="text" class="form-control" id="search" name="search" 
                                   placeholder="Contract No, Employee Name..." value="${searchQuery}">
                        </div>
                        <div class="col-md-3">
                            <label for="status" class="form-label">Status</label>
                            <select class="form-select" id="status" name="status">
                                <option value="all" ${empty statusFilter || statusFilter == 'all' ? 'selected' : ''}>All Status</option>
                                <option value="active" ${statusFilter == 'active' ? 'selected' : ''}>Active</option>
                                <option value="expired" ${statusFilter == 'expired' ? 'selected' : ''}>Expired</option>
                                <option value="terminated" ${statusFilter == 'terminated' ? 'selected' : ''}>Terminated</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label for="type" class="form-label">Contract Type</label>
                            <select class="form-select" id="type" name="type">
                                <option value="all" ${empty typeFilter || typeFilter == 'all' ? 'selected' : ''}>All Types</option>
                                <option value="indefinite" ${typeFilter == 'indefinite' ? 'selected' : ''}>Indefinite</option>
                                <option value="fixed_term" ${typeFilter == 'fixed_term' ? 'selected' : ''}>Fixed Term</option>
                                <option value="probation" ${typeFilter == 'probation' ? 'selected' : ''}>Probation</option>
                            </select>
                        </div>
                        <div class="col-md-2 d-flex align-items-end">
                            <button type="submit" class="btn btn-primary w-100">
                                <i class="fas fa-filter"></i> Filter
                            </button>
                        </div>
                    </form>
                </div>
            </div>
            
            <!-- Contract List Table -->
            <div class="table-card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="fas fa-list"></i> Contract List</h5>
                    <span class="badge bg-light text-dark">Total: ${totalContracts} contracts</span>
                </div>
                
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead>
                            <tr>
                                <th>Contract No</th>
                                <th>Employee</th>
                                <th>Type</th>
                                <th>Start Date</th>
                                <th>End Date</th>
                                <th>Salary</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty contracts}">
                                    <tr>
                                        <td colspan="8" class="text-center py-4">
                                            <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                            <p class="text-muted">No contracts found</p>
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="contract" items="${contracts}">
                                        <tr>
                                            <td>${contract.contractNo}</td>
                                            <td>
                                                <strong>${contract.userFullName}</strong><br>
                                                <small class="text-muted">${contract.username}</small>
                                            </td>
                                            <td>${contract.contractTypeDisplay}</td>
                                            <td>${contract.formattedStartDate}</td>
                                            <td>${contract.formattedEndDate}</td>
                                            <td>${contract.formattedSalary} ${contract.currency}</td>
                                            <td>
                                                <span class="status-badge ${contract.status}">
                                                    ${contract.statusDisplay}
                                                </span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${contract.status == 'expired' || contract.status == 'terminated'}">
                                                        <button class="btn btn-sm btn-secondary" disabled title="Cannot edit expired or terminated contracts">
                                                            <i class="fas fa-lock"></i>
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="${pageContext.request.contextPath}/contracts/edit?id=${contract.id}" 
                                                           class="btn btn-sm btn-warning" title="Edit">
                                                            <i class="fas fa-edit"></i>
                                                        </a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
                
                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <div class="card-footer">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                Showing ${(currentPage - 1) * pageSize + 1} to ${(currentPage - 1) * pageSize + contracts.size()} of ${totalContracts} entries
                            </div>
                            <nav>
                                <ul class="pagination mb-0">
                                    <!-- Previous Button -->
                                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="?page=${currentPage - 1}&pageSize=${pageSize}&search=${searchQuery}&status=${statusFilter}&type=${typeFilter}">
                                            <i class="fas fa-chevron-left"></i>
                                        </a>
                                    </li>
                                    
                                    <!-- Page Numbers -->
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <c:choose>
                                            <c:when test="${i == currentPage}">
                                                <li class="page-item active">
                                                    <span class="page-link">${i}</span>
                                                </li>
                                            </c:when>
                                            <c:when test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                                <li class="page-item">
                                                    <a class="page-link" href="?page=${i}&pageSize=${pageSize}&search=${searchQuery}&status=${statusFilter}&type=${typeFilter}">${i}</a>
                                                </li>
                                            </c:when>
                                            <c:when test="${i == currentPage - 3 || i == currentPage + 3}">
                                                <li class="page-item disabled">
                                                    <span class="page-link">...</span>
                                                </li>
                                            </c:when>
                                        </c:choose>
                                    </c:forEach>
                                    
                                    <!-- Next Button -->
                                    <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="?page=${currentPage + 1}&pageSize=${pageSize}&search=${searchQuery}&status=${statusFilter}&type=${typeFilter}">
                                            <i class="fas fa-chevron-right"></i>
                                        </a>
                                    </li>
                                </ul>
                            </nav>
                            
                            <!-- Page Size Selector -->
                            <div>
                                <select class="form-select form-select-sm" onchange="window.location.href='?page=1&pageSize=' + this.value + '&search=${searchQuery}&status=${statusFilter}&type=${typeFilter}'">
                                    <option value="10" ${pageSize == 10 ? 'selected' : ''}>10 per page</option>
                                    <option value="25" ${pageSize == 25 ? 'selected' : ''}>25 per page</option>
                                    <option value="50" ${pageSize == 50 ? 'selected' : ''}>50 per page</option>
                                    <option value="100" ${pageSize == 100 ? 'selected' : ''}>100 per page</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
        
        <!-- Footer -->
        <jsp:include page="../layout/dashboard-footer.jsp" />
    </div>
    

</body>
</html>
