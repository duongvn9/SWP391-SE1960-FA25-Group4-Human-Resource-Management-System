<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
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

                    /* Make horizontal row borders extend full width */
                    .table tbody tr {
                        border-bottom: 1px solid #dee2e6 !important;
                    }

                    .table tbody tr:last-child {
                        border-bottom: none !important;
                    }

                    .table td {
                        border-top: none !important;
                        border-left: none !important;
                        border-right: none !important;
                    }

                    .status-badge {
                        padding: 0.35rem 0.75rem;
                        border-radius: 20px;
                        font-size: 0.85rem;
                        font-weight: 500;
                    }

                    .status-badge.pending {
                        background-color: #fff3cd;
                        color: #856404;
                    }

                    .status-badge.approved {
                        background-color: #d1ecf1;
                        color: #0c5460;
                    }

                    .status-badge.rejected {
                        background-color: #f8d7da;
                        color: #721c24;
                    }

                    .status-badge.draft {
                        background-color: #f8f9fa;
                        color: #6c757d;
                        border: 1px dashed #6c757d;
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

                    .btn-sm {
                        padding: 0.25rem 0.5rem;
                        font-size: 0.875rem;
                    }

                    /* Modal styling - center vertically and horizontally */
                    .modal-dialog {
                        display: flex;
                        align-items: center;
                        min-height: calc(100vh - 3.5rem);
                        margin: 1.75rem auto;
                    }

                    .modal-body label {
                        font-size: 0.875rem;
                        margin-bottom: 0.25rem;
                    }

                    .modal-body p {
                        font-size: 0.95rem;
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
                                <!-- Only HR (position_id = 8) can create contracts -->
                                <c:if test="${sessionScope.user.positionId == 8}">
                                    <a href="${pageContext.request.contextPath}/contracts/create" class="btn btn-primary">
                                        <i class="fas fa-plus"></i> Create New Contract
                                    </a>
                                </c:if>
                            </div>
                        </div>

                        <!-- Users Without Contract Section -->
                        <!-- Only show to HR (position_id = 8) since only HR can create contracts -->
                        <c:if test="${sessionScope.user.positionId == 8 && not empty usersWithoutContract}">
                            <div class="alert alert-info" role="alert">
                                <div class="clickable-header" style="cursor: pointer;" data-bs-toggle="collapse"
                                    data-bs-target="#usersWithoutContractCollapse" aria-expanded="false"
                                    aria-controls="usersWithoutContractCollapse">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <h5 class="mb-0">
                                            <i class="fas fa-user-clock me-2"></i>Users Without Contract
                                            <span class="badge bg-primary">${usersWithoutContractTotal}</span>
                                        </h5>
                                        <i class="fas fa-chevron-down toggle-icon"></i>
                                    </div>
                                    <p class="mb-0 small">The following users don't have contracts yet. Click on a user
                                        to create a contract.</p>
                                </div>
                                <div class="collapse mt-2" id="usersWithoutContractCollapse">
                                    <div class="row g-2">
                                        <c:forEach var="user" items="${usersWithoutContract}" varStatus="status">
                                            <c:if test="${status.index < 10}">
                                                <div class="col-md-6 col-lg-4">
                                                    <div class="card create-contract-card"
                                                        style="cursor: pointer; transition: all 0.3s ease; border: 2px solid #667eea;"
                                                        onclick="window.location.href='${pageContext.request.contextPath}/contracts/create?userId=${user.userId}'">
                                                        <div class="card-body p-2">
                                                            <div class="d-flex align-items-center">
                                                                <div class="flex-shrink-0">
                                                                    <i
                                                                        class="fas fa-user-circle fa-2x text-primary"></i>
                                                                </div>
                                                                <div class="flex-grow-1 ms-2">
                                                                    <h6 class="mb-0">${user.fullName}</h6>
                                                                    <small
                                                                        class="text-muted">${user.employeeCode}</small>
                                                                    <c:if test="${not empty user.departmentName}">
                                                                        <br><small
                                                                            class="text-muted">${user.departmentName} -
                                                                            ${user.positionName}</small>
                                                                    </c:if>
                                                                </div>
                                                                <div class="flex-shrink-0">
                                                                    <i class="fas fa-plus-circle text-success"></i>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </c:forEach>
                                    </div>
                                    <c:if test="${usersWithoutContractTotal > 10}">
                                        <div class="text-center mt-2">
                                            <small class="text-muted">Showing 10 of ${usersWithoutContractTotal} users
                                                without contract.</small>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                            <style>
                                .clickable-header {
                                    user-select: none;
                                    transition: background-color 0.2s ease;
                                    padding: 0.5rem;
                                    margin: -0.5rem;
                                    border-radius: 6px;
                                }

                                .clickable-header:hover {
                                    background-color: rgba(0, 0, 0, 0.05);
                                }

                                .clickable-header .toggle-icon {
                                    transition: transform 0.3s ease;
                                    font-size: 1.2rem;
                                    color: #667eea;
                                }

                                .clickable-header[aria-expanded="true"] .toggle-icon {
                                    transform: rotate(180deg);
                                }

                                .create-contract-card:hover {
                                    transform: translateY(-3px);
                                    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
                                    border-color: #764ba2 !important;
                                    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
                                }

                                .create-contract-card:active {
                                    transform: translateY(-1px);
                                    box-shadow: 0 3px 10px rgba(102, 126, 234, 0.3);
                                }
                            </style>
                        </c:if>

                        <!-- Filter Section -->
                        <div class="card mb-3">
                            <div class="card-body">
                                <form method="get" action="${pageContext.request.contextPath}/contracts"
                                    class="row g-3">
                                    <div class="col-md-4">
                                        <label for="search" class="form-label">Search</label>
                                        <input type="text" class="form-control" id="search" name="search"
                                            placeholder="Contract No, Employee Name..." value="${searchQuery}">
                                    </div>
                                    <div class="col-md-3">
                                        <label for="approvalStatus" class="form-label">Approval Status</label>
                                        <select class="form-select" id="approvalStatus" name="approvalStatus">
                                            <option value="all" ${empty approvalStatusFilter ||
                                                approvalStatusFilter=='all' ? 'selected' : '' }>All</option>
                                            <option value="pending" ${approvalStatusFilter=='pending' ? 'selected' : ''
                                                }>Pending Approval</option>
                                            <option value="approved" ${approvalStatusFilter=='approved' ? 'selected'
                                                : '' }>Approved</option>
                                            <option value="rejected" ${approvalStatusFilter=='rejected' ? 'selected'
                                                : '' }>Rejected</option>
                                        </select>
                                    </div>
                                    <div class="col-md-2">
                                        <label for="status" class="form-label">Contract Status</label>
                                        <select class="form-select" id="status" name="status">
                                            <option value="all" ${empty statusFilter || statusFilter=='all' ? 'selected'
                                                : '' }>All</option>
                                            <option value="draft" ${statusFilter=='draft' ? 'selected' : '' }>Draft
                                            </option>
                                            <option value="active" ${statusFilter=='active' ? 'selected' : '' }>Active
                                            </option>
                                            <option value="expired" ${statusFilter=='expired' ? 'selected' : '' }>
                                                Expired</option>
                                            <option value="terminated" ${statusFilter=='terminated' ? 'selected' : '' }>
                                                Terminated</option>
                                        </select>
                                    </div>
                                    <div class="col-md-3">
                                        <label for="type" class="form-label">Contract Type</label>
                                        <select class="form-select" id="type" name="type">
                                            <option value="all" ${empty typeFilter || typeFilter=='all' ? 'selected'
                                                : '' }>
                                                All Types</option>
                                            <option value="indefinite" ${typeFilter=='indefinite' ? 'selected' : '' }>
                                                Indefinite</option>
                                            <option value="fixed_term" ${typeFilter=='fixed_term' ? 'selected' : '' }>
                                                Fixed
                                                Term</option>
                                            <option value="probation" ${typeFilter=='probation' ? 'selected' : '' }>
                                                Probation</option>
                                        </select>
                                    </div>
                                    <div class="col-md-2 d-flex align-items-end gap-2">
                                        <button type="submit" class="btn btn-primary flex-fill">
                                            <i class="fas fa-filter"></i> Filter
                                        </button>
                                        <a href="${pageContext.request.contextPath}/contracts"
                                            class="btn btn-secondary flex-fill">
                                            <i class="fas fa-times"></i> Clear
                                        </a>
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
                                            <th>Approval</th>
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
                                                            <!-- Contract Status: draft, active, expired, terminated -->
                                                            <c:choose>
                                                                <c:when test="${contract.status == 'draft'}">
                                                                    <span class="status-badge draft">Draft</span>
                                                                </c:when>
                                                                <c:when test="${contract.status == 'expired'}">
                                                                    <span class="status-badge expired">Expired</span>
                                                                </c:when>
                                                                <c:when test="${contract.status == 'terminated'}">
                                                                    <span
                                                                        class="status-badge terminated">Terminated</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="status-badge active">Active</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <!-- Approval Status: pending, approved, rejected -->
                                                            <c:choose>
                                                                <c:when test="${contract.approvalStatus == 'pending'}">
                                                                    <span class="status-badge pending">Pending</span>
                                                                </c:when>
                                                                <c:when test="${contract.approvalStatus == 'rejected'}">
                                                                    <span class="status-badge rejected">Rejected</span>
                                                                    <c:if test="${not empty contract.rejectedReason}">
                                                                        <br><small class="text-danger"
                                                                            title="${contract.rejectedReason}">
                                                                            <i class="fas fa-info-circle"></i>
                                                                            ${contract.rejectedReason}
                                                                        </small>
                                                                    </c:if>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="status-badge approved">Approved</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <div class="btn-group" role="group">
                                                                <!-- View button for all contracts -->
                                                                <button type="button" class="btn btn-sm btn-info"
                                                                    title="View Details" class="view-contract-btn"
                                                                    data-contract-no="${contract.contractNo}"
                                                                    data-contract-type="${contract.contractTypeDisplay}"
                                                                    data-status="${contract.status}"
                                                                    data-approval-status="${contract.approvalStatus}"
                                                                    data-employee-name="${contract.userFullName}"
                                                                    data-employee-code="${contract.username}"
                                                                    data-start-date="${contract.formattedStartDate}"
                                                                    data-end-date="${contract.formattedEndDate}"
                                                                    data-salary="${contract.formattedSalary}"
                                                                    data-currency="${contract.currency}"
                                                                    data-note="${not empty contract.note ? contract.note : ''}"
                                                                    data-rejected-reason="${not empty contract.rejectedReason ? contract.rejectedReason : ''}"
                                                                    data-created-by="${not empty contract.createdByName ? contract.createdByName : 'N/A'}"
                                                                    data-created-at="${not empty contract.formattedCreatedAt ? contract.formattedCreatedAt : 'N/A'}"
                                                                    data-approved-by="${not empty contract.approvedByName ? contract.approvedByName : ''}"
                                                                    data-approved-at="${not empty contract.formattedApprovedAt ? contract.formattedApprovedAt : ''}"
                                                                    onclick="showContractDetailFromData(this)">
                                                                    <i class="fas fa-eye"></i>
                                                                </button>

                                                                <!-- HR Actions: Replace button for ACTIVE contracts -->
                                                                <c:if test="${sessionScope.user.positionId == 8 && contract.status == 'active'}">
                                                                    <button type="button" class="btn btn-sm btn-warning"
                                                                        title="Replace Contract"
                                                                        data-contract-id="${contract.id}"
                                                                        data-contract-no="${contract.contractNo}"
                                                                        data-employee-name="${contract.userFullName}"
                                                                        onclick="showReplaceModal(this.getAttribute('data-contract-id'), this.getAttribute('data-contract-no'), this.getAttribute('data-employee-name'))">
                                                                        <i class="fas fa-exchange-alt"></i>
                                                                    </button>
                                                                </c:if>

                                                                <!-- HRM Actions: Approve/Reject for PENDING approval_status -->
                                                                <c:if
                                                                    test="${sessionScope.user.positionId == 7 && contract.approvalStatus == 'pending'}">

                                                                    <button type="button" class="btn btn-sm btn-success"
                                                                        data-contract-id="${contract.id}"
                                                                        data-contract-no="${contract.contractNo}"
                                                                        onclick="showApproveModal(this.getAttribute('data-contract-id'), this.getAttribute('data-contract-no'))"
                                                                        title="Approve">
                                                                        <i class="fas fa-check"></i>
                                                                    </button>
                                                                    <button type="button" class="btn btn-sm btn-danger"
                                                                        data-contract-id="${contract.id}"
                                                                        data-contract-no="${contract.contractNo}"
                                                                        onclick="showRejectModal(this.getAttribute('data-contract-id'), this.getAttribute('data-contract-no'))"
                                                                        title="Reject">
                                                                        <i class="fas fa-times"></i>
                                                                    </button>
                                                                </c:if>

                                                                <!-- HR Actions: Edit for PENDING or REJECTED approval_status -->
                                                                <c:if
                                                                    test="${sessionScope.user.positionId == 8 && (contract.approvalStatus == 'pending' || contract.approvalStatus == 'rejected')}">
                                                                    <a href="${pageContext.request.contextPath}/contracts/edit?id=${contract.id}"
                                                                        class="btn btn-sm btn-primary" title="Edit">
                                                                        <i class="fas fa-edit"></i>
                                                                    </a>
                                                                </c:if>
                                                            </div>
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
                                            Showing ${(currentPage - 1) * pageSize + 1} to ${(currentPage - 1) *
                                            pageSize +
                                            contracts.size()} of ${totalContracts} entries
                                        </div>
                                        <nav>
                                            <ul class="pagination mb-0">
                                                <!-- Previous Button -->
                                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                    <a class="page-link"
                                                        href="?page=${currentPage - 1}&search=${searchQuery}&approvalStatus=${approvalStatusFilter}&status=${statusFilter}&type=${typeFilter}">
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
                                                        <c:when
                                                            test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                                            <li class="page-item">
                                                                <a class="page-link"
                                                                    href="?page=${i}&search=${searchQuery}&approvalStatus=${approvalStatusFilter}&status=${statusFilter}&type=${typeFilter}">${i}</a>
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
                                                    <a class="page-link"
                                                        href="?page=${currentPage + 1}&search=${searchQuery}&approvalStatus=${approvalStatusFilter}&status=${statusFilter}&type=${typeFilter}">
                                                        <i class="fas fa-chevron-right"></i>
                                                    </a>
                                                </li>
                                            </ul>
                                        </nav>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </div>

                    <!-- Footer -->
                    <jsp:include page="../layout/dashboard-footer.jsp" />
                </div>

                <!-- Contract Details Modal -->
                <div class="modal fade" id="contractDetailModal" tabindex="-1"
                    aria-labelledby="contractDetailModalLabel" aria-hidden="true">
                    <div class="modal-dialog modal-lg">
                        <div class="modal-content">
                            <div class="modal-header bg-primary text-white">
                                <h5 class="modal-title" id="contractDetailModalLabel">
                                    <i class="fas fa-file-contract me-2"></i>Contract Details
                                </h5>
                                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                                    aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <div class="row g-3">
                                    <!-- Contract Information -->
                                    <div class="col-12">
                                        <h6 class="text-primary border-bottom pb-2 mb-3">
                                            <i class="fas fa-info-circle me-2"></i>Contract Information
                                        </h6>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="text-muted small">Contract No</label>
                                        <p class="fw-bold mb-0" id="modalContractNo">-</p>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="text-muted small">Contract Type</label>
                                        <p class="fw-bold mb-0" id="modalContractType">-</p>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="text-muted small">Status</label>
                                        <p class="mb-0" id="modalStatus">-</p>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="text-muted small">Approval Status</label>
                                        <p class="mb-0" id="modalApprovalStatus">-</p>
                                    </div>

                                    <!-- Employee Information -->
                                    <div class="col-12 mt-4">
                                        <h6 class="text-primary border-bottom pb-2 mb-3">
                                            <i class="fas fa-user me-2"></i>Employee Information
                                        </h6>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="text-muted small">Employee Name</label>
                                        <p class="fw-bold mb-0" id="modalEmployeeName">-</p>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="text-muted small">Employee Code</label>
                                        <p class="fw-bold mb-0" id="modalEmployeeCode">-</p>
                                    </div>

                                    <!-- Contract Period -->
                                    <div class="col-12 mt-4">
                                        <h6 class="text-primary border-bottom pb-2 mb-3">
                                            <i class="fas fa-calendar-alt me-2"></i>Contract Period
                                        </h6>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="text-muted small">Start Date</label>
                                        <p class="fw-bold mb-0" id="modalStartDate">-</p>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="text-muted small">End Date</label>
                                        <p class="fw-bold mb-0" id="modalEndDate">-</p>
                                    </div>

                                    <!-- Salary Information -->
                                    <div class="col-12 mt-4">
                                        <h6 class="text-primary border-bottom pb-2 mb-3">
                                            <i class="fas fa-money-bill-wave me-2"></i>Salary Information
                                        </h6>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="text-muted small">Base Salary</label>
                                        <p class="fw-bold mb-0 text-success" id="modalSalary">-</p>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="text-muted small">Currency</label>
                                        <p class="fw-bold mb-0" id="modalCurrency">-</p>
                                    </div>

                                    <!-- Audit Information -->
                                    <div class="col-12 mt-4">
                                        <h6 class="text-primary border-bottom pb-2 mb-3">
                                            <i class="fas fa-history me-2"></i>Audit Information
                                        </h6>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="text-muted small">Created By</label>
                                        <p class="fw-bold mb-0" id="modalCreatedBy">-</p>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="text-muted small">Created At</label>
                                        <p class="fw-bold mb-0" id="modalCreatedAt">-</p>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="text-muted small">Approved By</label>
                                        <p class="fw-bold mb-0" id="modalApprovedBy">N/A</p>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="text-muted small">Approved At</label>
                                        <p class="fw-bold mb-0" id="modalApprovedAtValue">N/A</p>
                                    </div>

                                    <!-- Additional Information -->
                                    <div class="col-12 mt-4" id="modalNoteSection" style="display: none;">
                                        <h6 class="text-primary border-bottom pb-2 mb-3">
                                            <i class="fas fa-sticky-note me-2"></i>Notes
                                        </h6>
                                        <p class="mb-0" id="modalNote">-</p>
                                    </div>

                                    <div class="col-12 mt-4" id="modalRejectedReasonSection" style="display: none;">
                                        <h6 class="text-danger border-bottom pb-2 mb-3">
                                            <i class="fas fa-exclamation-triangle me-2"></i>Rejection Reason
                                        </h6>
                                        <p class="mb-0 text-danger" id="modalRejectedReason">-</p>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Approve Confirmation Modal -->
                <div class="modal fade" id="approveModal" tabindex="-1" aria-labelledby="approveModalLabel"
                    aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="approveModalLabel">Approve Contract</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"
                                    aria-label="Close"></button>
                            </div>
                            <form id="approveForm" method="post"
                                action="${pageContext.request.contextPath}/contracts/approve">
                                <div class="modal-body">
                                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                                    <input type="hidden" name="id" id="approveContractId">
                                    <p>Are you sure you want to approve contract <strong
                                            id="approveContractNo"></strong>?</p>
                                    <p class="text-success"><i class="fas fa-check-circle"></i> This contract will be
                                        approved and ready for activation.</p>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary"
                                        data-bs-dismiss="modal">Cancel</button>
                                    <button type="submit" class="btn btn-success">Approve Contract</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Reject Modal -->
                <div class="modal fade" id="rejectModal" tabindex="-1" aria-labelledby="rejectModalLabel"
                    aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="rejectModalLabel">Reject Contract</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"
                                    aria-label="Close"></button>
                            </div>
                            <form id="rejectForm" method="post"
                                action="${pageContext.request.contextPath}/contracts/reject">
                                <div class="modal-body">
                                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                                    <input type="hidden" name="id" id="rejectContractId">
                                    <p>Are you sure you want to reject contract <strong id="rejectContractNo"></strong>?
                                    </p>
                                    <div class="mb-3">
                                        <label for="reason" class="form-label">Rejection Reason <span
                                                class="text-danger">*</span></label>
                                        <textarea class="form-control" id="reason" name="reason" rows="3" required
                                            placeholder="Please provide a reason for rejection..."></textarea>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary"
                                        data-bs-dismiss="modal">Cancel</button>
                                    <button type="submit" class="btn btn-danger">Reject Contract</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Delete Confirmation Modal -->
                <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel"
                    aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="deleteModalLabel">Delete Contract</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"
                                    aria-label="Close"></button>
                            </div>
                            <form id="deleteForm" method="post"
                                action="${pageContext.request.contextPath}/contracts/delete">
                                <div class="modal-body">
                                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                                    <input type="hidden" name="id" id="deleteContractId">
                                    <p>Are you sure you want to delete contract <strong id="deleteContractNo"></strong>?
                                    </p>
                                    <p class="text-danger"><i class="fas fa-exclamation-triangle"></i> This action
                                        cannot be undone.</p>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary"
                                        data-bs-dismiss="modal">Cancel</button>
                                    <button type="submit" class="btn btn-danger">Delete Contract</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Replace Contract Confirmation Modal -->
                <div class="modal fade" id="replaceModal" tabindex="-1" aria-labelledby="replaceModalLabel"
                    aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header bg-warning">
                                <h5 class="modal-title" id="replaceModalLabel">
                                    <i class="fas fa-exchange-alt me-2"></i>Replace Contract
                                </h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"
                                    aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <p>Are you sure you want to replace contract <strong id="replaceContractNo"></strong> 
                                for employee <strong id="replaceEmployeeName"></strong>?</p>
                                <div class="alert alert-warning">
                                    <i class="fas fa-info-circle"></i> 
                                    <strong>Note:</strong>
                                    <ul class="mb-0 mt-2">
                                        <li>You will create a new contract for this employee</li>
                                        <li>The old contract will be <strong>terminated</strong> after the new contract is successfully created</li>
                                        <li>The old contract will remain in the contract list with "Terminated" status</li>
                                    </ul>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary"
                                    data-bs-dismiss="modal">Cancel</button>
                                <button type="button" class="btn btn-warning" id="confirmReplaceBtn">
                                    <i class="fas fa-exchange-alt"></i> Proceed to Replace
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <script>
                    // Show approve modal
                    function showApproveModal(contractId, contractNo) {
                        document.getElementById('approveContractId').value = contractId;
                        document.getElementById('approveContractNo').textContent = contractNo;
                        const modal = new bootstrap.Modal(document.getElementById('approveModal'));
                        modal.show();
                    }

                    // Show contract detail from data attributes
                    function showContractDetailFromData(button) {
                        const contractNo = button.getAttribute('data-contract-no');
                        const contractType = button.getAttribute('data-contract-type');
                        const status = button.getAttribute('data-status');
                        const approvalStatus = button.getAttribute('data-approval-status');
                        const employeeName = button.getAttribute('data-employee-name');
                        const employeeCode = button.getAttribute('data-employee-code');
                        const startDate = button.getAttribute('data-start-date');
                        const endDate = button.getAttribute('data-end-date');
                        const salary = button.getAttribute('data-salary');
                        const currency = button.getAttribute('data-currency');
                        const note = button.getAttribute('data-note');
                        const rejectedReason = button.getAttribute('data-rejected-reason');
                        const createdBy = button.getAttribute('data-created-by');
                        const createdAt = button.getAttribute('data-created-at');
                        const approvedBy = button.getAttribute('data-approved-by');
                        const approvedAt = button.getAttribute('data-approved-at');

                        showContractDetail(contractNo, contractType, status, approvalStatus, employeeName, employeeCode,
                            startDate, endDate, salary, currency, note, rejectedReason,
                            createdBy, createdAt, approvedBy, approvedAt);
                    }

                    // Show contract detail modal
                    function showContractDetail(contractNo, contractType, status, approvalStatus, employeeName, employeeCode,
                        startDate, endDate, salary, currency, note, rejectedReason,
                        createdBy, createdAt, approvedBy, approvedAt) {
                        // Contract Information
                        document.getElementById('modalContractNo').textContent = contractNo;
                        document.getElementById('modalContractType').textContent = contractType;

                        // Contract Status badges
                        let statusBadgeClass, statusText;
                        if (status === 'draft') {
                            statusBadgeClass = 'draft';
                            statusText = 'Draft';
                        } else if (status === 'expired') {
                            statusBadgeClass = 'expired';
                            statusText = 'Expired';
                        } else if (status === 'terminated') {
                            statusBadgeClass = 'terminated';
                            statusText = 'Terminated';
                        } else {
                            statusBadgeClass = 'active';
                            statusText = 'Active';
                        }

                        document.getElementById('modalStatus').innerHTML =
                            '<span class="status-badge ' + statusBadgeClass + '">' + statusText + '</span>';

                        // Approval Status badges
                        const approvalBadgeClass = approvalStatus === 'pending' ? 'pending' : approvalStatus === 'rejected' ? 'rejected' : 'approved';
                        document.getElementById('modalApprovalStatus').innerHTML =
                            '<span class="status-badge ' + approvalBadgeClass + '">' +
                            (approvalStatus === 'pending' ? 'Pending' : approvalStatus === 'rejected' ? 'Rejected' : 'Approved') +
                            '</span>';

                        // Employee Information
                        document.getElementById('modalEmployeeName').textContent = employeeName;
                        document.getElementById('modalEmployeeCode').textContent = employeeCode;

                        // Contract Period
                        document.getElementById('modalStartDate').textContent = startDate;
                        document.getElementById('modalEndDate').textContent = endDate || 'N/A';

                        // Salary Information
                        document.getElementById('modalSalary').textContent = salary;
                        document.getElementById('modalCurrency').textContent = currency;

                        // Audit Information
                        document.getElementById('modalCreatedBy').textContent = createdBy || 'N/A';
                        document.getElementById('modalCreatedAt').textContent = createdAt || 'N/A';
                        document.getElementById('modalApprovedBy').textContent = (approvedBy && approvedBy.trim() !== '') ? approvedBy : 'N/A';
                        document.getElementById('modalApprovedAtValue').textContent = (approvedAt && approvedAt.trim() !== '') ? approvedAt : 'N/A';

                        // Notes
                        if (note && note.trim() !== '') {
                            document.getElementById('modalNote').textContent = note;
                            document.getElementById('modalNoteSection').style.display = 'block';
                        } else {
                            document.getElementById('modalNoteSection').style.display = 'none';
                        }

                        // Rejected Reason
                        if (rejectedReason && rejectedReason.trim() !== '') {
                            document.getElementById('modalRejectedReason').textContent = rejectedReason;
                            document.getElementById('modalRejectedReasonSection').style.display = 'block';
                        } else {
                            document.getElementById('modalRejectedReasonSection').style.display = 'none';
                        }

                        // Show modal
                        const modal = new bootstrap.Modal(document.getElementById('contractDetailModal'));
                        modal.show();
                    }

                    // Show reject modal
                    function showRejectModal(contractId, contractNo) {
                        document.getElementById('rejectContractId').value = contractId;
                        document.getElementById('rejectContractNo').textContent = contractNo;
                        document.getElementById('reason').value = '';
                        const modal = new bootstrap.Modal(document.getElementById('rejectModal'));
                        modal.show();
                    }

                    // Show delete modal
                    function deleteContract(contractId, contractNo) {
                        document.getElementById('deleteContractId').value = contractId;
                        document.getElementById('deleteContractNo').textContent = contractNo;
                        const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
                        modal.show();
                    }

                    // Show replace modal
                    function showReplaceModal(contractId, contractNo, employeeName) {
                        document.getElementById('replaceContractNo').textContent = contractNo;
                        document.getElementById('replaceEmployeeName').textContent = employeeName;
                        
                        // Set up the confirm button to redirect to replace form
                        const confirmBtn = document.getElementById('confirmReplaceBtn');
                        confirmBtn.onclick = function() {
                            window.location.href = '${pageContext.request.contextPath}/contracts/replace?oldContractId=' + contractId;
                        };
                        
                        const modal = new bootstrap.Modal(document.getElementById('replaceModal'));
                        modal.show();
                    }
                </script>

            </body>

            </html>