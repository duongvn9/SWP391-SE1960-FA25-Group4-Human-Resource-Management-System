<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
            <%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <jsp:include page="/WEB-INF/views/layout/head.jsp">
                        <jsp:param name="pageTitle" value="Application Management" />
                    </jsp:include>
                    <link href="${pageContext.request.contextPath}/assets/css/dashboard.css" rel="stylesheet" />
                </head>

                <body>
                    <div class="dashboard-wrapper">
                        <jsp:include page="/WEB-INF/views/layout/sidebar.jsp">
                            <jsp:param name="currentPage" value="applications" />
                        </jsp:include>

                        <div class="main-content">
                            <jsp:include page="/WEB-INF/views/layout/dashboard-header.jsp">
                                <jsp:param name="pageTitle" value="Application Management" />
                            </jsp:include>

                            <div class="content-area">
                                <!-- Page heading -->
                                <div class="page-head d-flex justify-content-between align-items-center mb-4">
                                    <div>
                                        <h2 class="page-title">
                                            <i class="fas fa-file-alt me-2"></i>Application Management
                                        </h2>
                                        <p class="page-subtitle">Review and approve job applications</p>
                                    </div>
                                    <div class="d-flex gap-2">
                                        <a href="${pageContext.request.contextPath}/job-postings"
                                            class="btn btn-outline-secondary">
                                            <i class="fas fa-briefcase me-2"></i>Job Postings
                                        </a>
                                    </div>
                                </div>

                                <!-- Show success/error messages -->
                                <c:if test="${not empty success}">
                                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                                        <i class="fas fa-check-circle me-2"></i>
                                        <c:out value="${success}" />
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                    </div>
                                </c:if>

                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                        <i class="fas fa-exclamation-circle me-2"></i>
                                        <c:out value="${error}" />
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                    </div>
                                </c:if>

                                <!-- Filter and Search -->
                                <div class="card mb-4">
                                    <div class="card-body">
                                        <form method="GET" action="${pageContext.request.contextPath}/applications"
                                            class="row g-3">
                                            <div class="col-md-3">
                                                <label for="status" class="form-label">Status</label>
                                                <select name="status" id="status" class="form-select">
                                                    <option value="">All</option>
                                                    <option value="new" ${statusFilter=='new' ? 'selected' : '' }>New
                                                    </option>
                                                    <option value="reviewing" ${statusFilter=='reviewing' ? 'selected'
                                                        : '' }>Reviewing</option>
                                                    <option value="interviewed" ${statusFilter=='interviewed'
                                                        ? 'selected' : '' }>Interviewed</option>
                                                    <option value="approved" ${statusFilter=='approved' ? 'selected'
                                                        : '' }>Approved</option>
                                                    <option value="rejected" ${statusFilter=='rejected' ? 'selected'
                                                        : '' }>Rejected</option>
                                                </select>
                                            </div>
                                            <div class="col-md-6">
                                                <label for="search" class="form-label">Search</label>
                                                <input type="text" name="search" id="search" class="form-control"
                                                    placeholder="Candidate name, email, phone..."
                                                    value="${fn:escapeXml(searchTerm)}">
                                            </div>
                                            <div class="col-md-3 d-flex align-items-end">
                                                <button type="submit" class="btn btn-primary me-2">
                                                    <i class="fas fa-search me-1"></i>Search
                                                </button>
                                                <a href="${pageContext.request.contextPath}/applications"
                                                    class="btn btn-outline-secondary">
                                                    <i class="fas fa-times me-1"></i>Clear Filter
                                                </a>
                                            </div>
                                        </form>
                                    </div>
                                </div>

                                <!-- Applications Table -->
                                <div class="card">
                                    <div class="card-header d-flex justify-content-between align-items-center">
                                        <h5 class="mb-0">
                                            <i class="fas fa-list me-2"></i>Application List
                                            <span class="badge bg-secondary ms-2">${totalApplications}</span>
                                        </h5>
                                    </div>
                                    <div class="card-body p-0">
                                        <c:choose>
                                            <c:when test="${empty applications}">
                                                <div class="text-center py-5">
                                                    <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                                    <h5 class="text-muted">No Applications Found</h5>
                                                    <p class="text-muted">No candidates have submitted applications yet.
                                                    </p>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="table-responsive">
                                                    <table class="table table-hover mb-0">
                                                        <thead class="table-light">
                                                            <tr>
                                                                <th>ID</th>
                                                                <th>Candidate</th>
                                                                <th>Position Applied</th>
                                                                <th>Status</th>
                                                                <th>Applied Date</th>
                                                                <th>Actions</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:forEach var="application" items="${applications}">
                                                                <tr>
                                                                    <td>
                                                                        <span class="fw-bold">#${application.id}</span>
                                                                    </td>
                                                                    <td>
                                                                        <div class="d-flex align-items-center">
                                                                            <div class="avatar-circle me-3">
                                                                                <i class="fas fa-user"></i>
                                                                            </div>
                                                                            <div>
                                                                                <div class="fw-bold">
                                                                                    ${fn:escapeXml(application.fullName)}
                                                                                </div>
                                                                                <small class="text-muted">
                                                                                    <i
                                                                                        class="fas fa-envelope me-1"></i>${fn:escapeXml(application.email)}
                                                                                </small>
                                                                                <c:if
                                                                                    test="${not empty application.phone}">
                                                                                    <br><small class="text-muted">
                                                                                        <i
                                                                                            class="fas fa-phone me-1"></i>${fn:escapeXml(application.phone)}
                                                                                    </small>
                                                                                </c:if>
                                                                            </div>
                                                                        </div>
                                                                    </td>
                                                                    <td>
                                                                        <c:set var="jobPosting"
                                                                            value="${jobMap[application.jobId]}" />
                                                                        <c:choose>
                                                                            <c:when test="${not empty jobPosting}">
                                                                                <div class="fw-bold">
                                                                                    ${fn:escapeXml(jobPosting.title)}
                                                                                </div>
                                                                                <small class="text-muted">
                                                                                    <c:set var="department"
                                                                                        value="${departmentMap[jobPosting.departmentId]}" />
                                                                                    ${not empty department ?
                                                                                    department.name : 'Unknown
                                                                                    Department'}
                                                                                    <!-- Debug: DeptID=${jobPosting.departmentId}, Found=${not empty department} -->
                                                                                </small>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span class="text-muted">Unknown</span>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </td>
                                                                    <td>
                                                                        <c:choose>
                                                                            <c:when
                                                                                test="${application.status == 'new'}">
                                                                                <span class="badge bg-info">New</span>
                                                                            </c:when>
                                                                            <c:when
                                                                                test="${application.status == 'reviewing'}">
                                                                                <span
                                                                                    class="badge bg-warning">Reviewing</span>
                                                                            </c:when>
                                                                            <c:when
                                                                                test="${application.status == 'interviewed'}">
                                                                                <span
                                                                                    class="badge bg-primary">Interviewed</span>
                                                                            </c:when>
                                                                            <c:when
                                                                                test="${application.status == 'approved'}">
                                                                                <span
                                                                                    class="badge bg-success">Approved</span>
                                                                            </c:when>
                                                                            <c:when
                                                                                test="${application.status == 'rejected'}">
                                                                                <span
                                                                                    class="badge bg-danger">Rejected</span>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span
                                                                                    class="badge bg-secondary">${fn:escapeXml(application.status)}</span>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </td>
                                                                    <td>
                                                                        ${formattedDates[application.id]}
                                                                    </td>
                                                                    <td>
                                                                        <div class="btn-group" role="group">
                                                                            <a href="${pageContext.request.contextPath}/applications/detail?id=${application.id}"
                                                                                class="btn btn-sm btn-outline-primary"
                                                                                title="View Details">
                                                                                <i class="fas fa-eye"></i>
                                                                            </a>

                                                                            <!-- Approval buttons based on user position and application status -->
                                                                            <!-- Check permissions for current user and application -->
                                                                            <c:set var="userPositionId"
                                                                                value="${currentUser.positionId}" />
                                                                            <c:set var="appStatus"
                                                                                value="${application.status}" />

                                                                            <% Long userPositionId=(Long)
                                                                                pageContext.getAttribute("userPositionId");
                                                                                String appStatus=(String)
                                                                                pageContext.getAttribute("appStatus");
                                                                                boolean
                                                                                canApprove=group4.hrms.util.ApplicationPermissionHelper.canApproveApplication(userPositionId,
                                                                                appStatus); boolean
                                                                                canReject=group4.hrms.util.ApplicationPermissionHelper.canRejectApplication(userPositionId,
                                                                                appStatus); String
                                                                                approverRole=group4.hrms.util.ApplicationPermissionHelper.getApprovalRole(userPositionId);
                                                                                pageContext.setAttribute("canApprove",
                                                                                canApprove);
                                                                                pageContext.setAttribute("canReject",
                                                                                canReject);
                                                                                pageContext.setAttribute("approverRole",
                                                                                approverRole); %>
                                                                                <!-- Debug: UserPos=${userPositionId}, AppStatus=${appStatus}, CanApprove=${canApprove}, CanReject=${canReject}, Role=${approverRole} -->

                                                                                <c:if test="${canApprove}">
                                                                                    <c:choose>
                                                                                        <c:when
                                                                                            test="${empty approverRole}">
                                                                                            <c:set var="approvalTitle"
                                                                                                value="Approval" />
                                                                                            <c:set
                                                                                                var="approvalButtonTitle"
                                                                                                value="Final Approval" />
                                                                                            <c:set var="approvalIcon"
                                                                                                value="fa-check-double" />
                                                                                        </c:when>
                                                                                        <c:otherwise>
                                                                                            <c:set var="approvalTitle"
                                                                                                value="${approverRole} Approval" />
                                                                                            <c:choose>
                                                                                                <c:when
                                                                                                    test="${approverRole == 'HR'}">
                                                                                                    <c:set
                                                                                                        var="approvalButtonTitle"
                                                                                                        value="Move to Review" />
                                                                                                    <c:set
                                                                                                        var="approvalIcon"
                                                                                                        value="fa-check" />
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                    <c:set
                                                                                                        var="approvalButtonTitle"
                                                                                                        value="Final Approval" />
                                                                                                    <c:set
                                                                                                        var="approvalIcon"
                                                                                                        value="fa-check-double" />
                                                                                                </c:otherwise>
                                                                                            </c:choose>
                                                                                        </c:otherwise>
                                                                                    </c:choose>
                                                                                    <button type="button"
                                                                                        class="btn btn-sm btn-success approval-btn"
                                                                                        data-app-id="${application.id}"
                                                                                        data-action="approved"
                                                                                        data-title="${approvalTitle}"
                                                                                        title="${approvalButtonTitle}">
                                                                                        <i
                                                                                            class="fas ${approvalIcon}"></i>
                                                                                    </button>
                                                                                </c:if>

                                                                                <c:if test="${canReject}">
                                                                                    <c:choose>
                                                                                        <c:when
                                                                                            test="${empty approverRole}">
                                                                                            <c:set var="rejectTitle"
                                                                                                value="Reject" />
                                                                                            <c:set var="rejectIcon"
                                                                                                value="fa-ban" />
                                                                                        </c:when>
                                                                                        <c:otherwise>
                                                                                            <c:set var="rejectTitle"
                                                                                                value="${approverRole} Reject" />
                                                                                            <c:choose>
                                                                                                <c:when
                                                                                                    test="${approverRole == 'HR'}">
                                                                                                    <c:set
                                                                                                        var="rejectIcon"
                                                                                                        value="fa-times" />
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                    <c:set
                                                                                                        var="rejectIcon"
                                                                                                        value="fa-ban" />
                                                                                                </c:otherwise>
                                                                                            </c:choose>
                                                                                        </c:otherwise>
                                                                                    </c:choose>
                                                                                    <button type="button"
                                                                                        class="btn btn-sm btn-danger rejection-btn"
                                                                                        data-app-id="${application.id}"
                                                                                        data-action="rejected"
                                                                                        data-title="${rejectTitle}"
                                                                                        title="Reject">
                                                                                        <i
                                                                                            class="fas ${rejectIcon}"></i>
                                                                                    </button>
                                                                                </c:if>""
                                                                        </div>
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <!-- Pagination -->
                                    <c:if test="${totalPages > 1}">
                                        <div class="card-footer">
                                            <nav aria-label="Application pagination">
                                                <ul class="pagination justify-content-center mb-0">
                                                    <!-- Previous page -->
                                                    <c:if test="${currentPage > 1}">
                                                        <li class="page-item">
                                                            <a class="page-link"
                                                                href="?page=${currentPage - 1}&status=${statusFilter}&search=${fn:escapeXml(searchTerm)}">
                                                                <i class="fas fa-chevron-left"></i>
                                                            </a>
                                                        </li>
                                                    </c:if>

                                                    <!-- Page numbers -->
                                                    <c:forEach begin="1" end="${totalPages}" var="pageNum">
                                                        <c:if
                                                            test="${pageNum <= 5 || pageNum > totalPages - 5 || (pageNum >= currentPage - 2 && pageNum <= currentPage + 2)}">
                                                            <li
                                                                class="page-item ${pageNum == currentPage ? 'active' : ''}">
                                                                <a class="page-link"
                                                                    href="?page=${pageNum}&status=${statusFilter}&search=${fn:escapeXml(searchTerm)}">
                                                                    ${pageNum}
                                                                </a>
                                                            </li>
                                                        </c:if>
                                                        <c:if
                                                            test="${pageNum == 6 && totalPages > 10 && currentPage < totalPages - 5}">
                                                            <li class="page-item disabled">
                                                                <span class="page-link">...</span>
                                                            </li>
                                                        </c:if>
                                                    </c:forEach>

                                                    <!-- Next page -->
                                                    <c:if test="${currentPage < totalPages}">
                                                        <li class="page-item">
                                                            <a class="page-link"
                                                                href="?page=${currentPage + 1}&status=${statusFilter}&search=${fn:escapeXml(searchTerm)}">
                                                                <i class="fas fa-chevron-right"></i>
                                                            </a>
                                                        </li>
                                                    </c:if>
                                                </ul>
                                            </nav>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Approval Modal -->
                    <div class="modal fade" id="approvalModal" tabindex="-1" aria-labelledby="approvalModalLabel"
                        aria-hidden="true" style="margin-top: 5rem;">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="approvalModalLabel">Confirm Application Review</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                                </div>
                                <form id="approvalForm" method="POST"
                                    action="${pageContext.request.contextPath}/applications">
                                    <div class="modal-body">
                                        <input type="hidden" name="action" value="approve">
                                        <input type="hidden" name="applicationId" id="modalApplicationId">
                                        <input type="hidden" name="status" id="modalStatus">

                                        <div class="mb-3">
                                            <label for="modalNote" class="form-label">Notes</label>
                                            <textarea name="note" id="modalNote" class="form-control" rows="3"
                                                placeholder="Enter notes about this decision..."></textarea>
                                        </div>

                                        <div class="alert alert-info">
                                            <i class="fas fa-info-circle me-2"></i>
                                            <span id="modalMessage"></span>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary"
                                            data-bs-dismiss="modal">Cancel</button>
                                        <button type="submit" class="btn" id="modalSubmitBtn">Confirm</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <script>
                        // Event listeners for approval/rejection buttons
                        document.addEventListener('DOMContentLoaded', function () {
                            // Handle approval buttons
                            document.querySelectorAll('.approval-btn').forEach(function (btn) {
                                btn.addEventListener('click', function () {
                                    const appId = this.getAttribute('data-app-id');
                                    const action = this.getAttribute('data-action');
                                    const title = this.getAttribute('data-title');
                                    showApprovalModal(appId, action, title);
                                });
                            });

                            // Handle rejection buttons
                            document.querySelectorAll('.rejection-btn').forEach(function (btn) {
                                btn.addEventListener('click', function () {
                                    const appId = this.getAttribute('data-app-id');
                                    const action = this.getAttribute('data-action');
                                    const title = this.getAttribute('data-title');
                                    showApprovalModal(appId, action, title);
                                });
                            });
                        });

                        function showApprovalModal(applicationId, status, actionType) {
                            console.log('showApprovalModal called:', applicationId, status, actionType);

                            document.getElementById('modalApplicationId').value = applicationId;
                            document.getElementById('modalStatus').value = status;

                            const modal = document.getElementById('approvalModal');
                            const submitBtn = document.getElementById('modalSubmitBtn');
                            const message = document.getElementById('modalMessage');

                            if (status === 'approved') {
                                submitBtn.className = 'btn btn-success';
                                submitBtn.innerHTML = '<i class="fas fa-check me-2"></i>Approve';
                                if (actionType && actionType.includes('HR')) {
                                    message.textContent = 'Application will be moved to "Reviewing" status and await HRM final approval.';
                                } else {
                                    message.textContent = 'Application will be approved and candidate may be contacted for interview.';
                                }
                            } else {
                                submitBtn.className = 'btn btn-danger';
                                submitBtn.innerHTML = '<i class="fas fa-times me-2"></i>Reject';
                                message.textContent = 'Application will be rejected and candidate will be notified.';
                            }

                            console.log('Opening modal...');
                            new bootstrap.Modal(modal).show();
                        }
                    </script>

                    <style>
                        .avatar-circle {
                            width: 40px;
                            height: 40px;
                            border-radius: 50%;
                            background-color: #e9ecef;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            color: #6c757d;
                        }

                        .table th {
                            border-top: none;
                            font-weight: 600;
                            color: #495057;
                        }

                        .badge {
                            font-size: 0.75em;
                        }

                        .btn-group .btn {
                            border-radius: 0.25rem;
                            margin-right: 2px;
                        }

                        .btn-group .btn:last-child {
                            margin-right: 0;
                        }
                    </style>

                    <!-- Bootstrap JS (required for modal) -->
                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

                </body>

                </html>