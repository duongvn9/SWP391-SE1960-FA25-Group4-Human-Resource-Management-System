<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <!-- CSS riêng của trang -->
                <jsp:include page="../layout/head.jsp">
                    <jsp:param name="pageTitle" value="Request Detail - HRMS" />
                    <jsp:param name="pageCss" value="request-detail.css" />
                </jsp:include>
            </head>

            <body data-is-hrm="${isHRM}" data-is-hr="${isHR}">
                <!-- Sidebar -->
                <jsp:include page="../layout/sidebar.jsp">
                    <jsp:param name="currentPage" value="request-list" />
                </jsp:include>

                <!-- Main Content -->
                <div class="main-content" id="main-content">
                    <!-- Header -->
                    <jsp:include page="../layout/dashboard-header.jsp" />

                    <!-- Content Area -->
                    <div class="content-area" style="padding: 2rem;">
                        <!-- Breadcrumb Navigation -->
                        <nav aria-label="breadcrumb" class="mb-3">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/dashboard">
                                        <i class="fas fa-home"></i> Home
                                    </a>
                                </li>
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/requests">
                                        <i class="fas fa-clipboard-list"></i> Requests
                                    </a>
                                </li>
                                <li class="breadcrumb-item active" aria-current="page">Detail</li>
                            </ol>
                        </nav>

                        <!-- Page Header with Action Buttons -->
                        <div class="page-head d-flex justify-content-between align-items-center mb-4">
                            <div>
                                <h2 class="page-title">
                                    <i class="fas fa-file-alt me-2"></i>Request Detail #${requestDto.id}
                                </h2>
                                <p class="page-subtitle text-muted">View detailed information about this request</p>
                            </div>
                            <div class="d-flex gap-2">
                                <!-- Back to List Button -->
                                <a href="${pageContext.request.contextPath}/requests" class="btn btn-outline-secondary">
                                    <i class="fas fa-arrow-left me-1"></i> Back to List
                                </a>

                                <!-- Conditional Approve Button -->
                                <!-- Show for PENDING, APPROVED, or REJECTED status (allows manager override) -->
                                <c:if test="${canApprove && (requestDto.status == 'PENDING' || requestDto.status == 'APPROVED' || requestDto.status == 'REJECTED')}">
                                    <button onclick="openApprovalModal(${requestDto.id}, '${requestDto.title}', '${requestDto.status}')"
                                        class="btn btn-warning">
                                        <i class="fas fa-clipboard-check me-1"></i>
                                        <c:choose>
                                            <c:when test="${requestDto.status == 'APPROVED'}">Override Approval</c:when>
                                            <c:when test="${requestDto.status == 'REJECTED'}">Override Rejection</c:when>
                                            <c:otherwise>Approve Request</c:otherwise>
                                        </c:choose>
                                    </button>
                                </c:if>
                            </div>
                        </div>

                        <!-- Alerts converted to Toast notifications -->
                        <!-- Check for error in request scope first, then session scope -->
                        <c:if test="${not empty error}">
                            <input type="hidden" id="serverError" value="${error}">
                        </c:if>

                        <!-- Check for success in request scope first, then session scope -->
                        <c:if test="${not empty success}">
                            <input type="hidden" id="serverSuccess" value="${success}">
                        </c:if>

                        <!-- Request Information Card -->
                        <div class="card mb-4 shadow-sm border-0">
                            <div class="card-header bg-gradient-primary text-white">
                                <h5 class="mb-0"><i class="fas fa-info-circle me-2"></i>Request Information</h5>
                            </div>
                            <div class="card-body p-4">
                                <div class="row g-4">
                                    <!-- Left Column: Basic Request Info -->
                                    <div class="col-lg-6">
                                        <div class="info-section">
                                            <h6 class="section-title mb-3">
                                                <i class="fas fa-file-alt text-primary me-2"></i>Request Details
                                            </h6>

                                            <!-- Request ID & Title -->
                                            <div class="info-item mb-3">
                                                <div class="info-label">
                                                    <i class="fas fa-hashtag me-2"></i>Request ID
                                                </div>
                                                <div class="info-value">
                                                    <span class="badge bg-dark">#<c:out value="${requestDto.id}" /></span>
                                                </div>
                                            </div>

                                            <div class="info-item mb-3">
                                                <div class="info-label">
                                                    <i class="fas fa-heading me-2"></i>Title
                                                </div>
                                                <div class="info-value fw-semibold">
                                                    <c:out value="${requestDto.title}" />
                                                </div>
                                            </div>

                                            <!-- Request Type -->
                                            <div class="info-item mb-3">
                                                <div class="info-label">
                                                    <i class="fas fa-tag me-2"></i>Request Type
                                                </div>
                                                <div class="info-value">
                                                    <span class="badge bg-info">
                                                        <c:out value="${requestDto.requestTypeName}" />
                                                    </span>
                                                </div>
                                            </div>

                                            <!-- Status -->
                                            <div class="info-item">
                                                <div class="info-label">
                                                    <i class="fas fa-flag me-2"></i>Status
                                                </div>
                                                <div class="info-value">
                                                    <span class="badge ${requestDto.statusBadgeClass} px-3 py-2">
                                                        <c:choose>
                                                            <c:when test="${requestDto.status == 'PENDING'}">
                                                                <i class="fas fa-clock me-1"></i>
                                                            </c:when>
                                                            <c:when test="${requestDto.status == 'APPROVED'}">
                                                                <i class="fas fa-check-circle me-1"></i>
                                                            </c:when>
                                                            <c:when test="${requestDto.status == 'REJECTED'}">
                                                                <i class="fas fa-times-circle me-1"></i>
                                                            </c:when>
                                                            <c:when test="${requestDto.status == 'CANCELLED'}">
                                                                <i class="fas fa-ban me-1"></i>
                                                            </c:when>
                                                        </c:choose>
                                                        <c:out value="${requestDto.status}" />
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Right Column: Creator Info & Dates -->
                                    <div class="col-lg-6">
                                        <div class="info-section">
                                            <h6 class="section-title mb-3">
                                                <i class="fas fa-user-circle text-primary me-2"></i>Employee Information
                                            </h6>

                                            <!-- Creator Full Name -->
                                            <div class="info-item mb-3">
                                                <div class="info-label">
                                                    <i class="fas fa-user me-2"></i>Employee Name
                                                </div>
                                                <div class="info-value fw-semibold">
                                                    <c:out value="${requestDto.userFullName}" />
                                                </div>
                                            </div>

                                            <!-- Employee Code -->
                                            <div class="info-item mb-3">
                                                <div class="info-label">
                                                    <i class="fas fa-id-badge me-2"></i>Employee Code
                                                </div>
                                                <div class="info-value">
                                                    <span class="badge bg-secondary">
                                                        <c:out value="${requestDto.employeeCode}" />
                                                    </span>
                                                </div>
                                            </div>

                                            <!-- Department -->
                                            <div class="info-item mb-3">
                                                <div class="info-label">
                                                    <i class="fas fa-building me-2"></i>Department
                                                </div>
                                                <div class="info-value">
                                                    <c:choose>
                                                        <c:when test="${not empty requestDto.departmentName}">
                                                            <c:out value="${requestDto.departmentName}" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-muted fst-italic">N/A</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>

                                            <!-- Created Date -->
                                            <div class="info-item mb-3">
                                                <div class="info-label">
                                                    <i class="fas fa-calendar-plus me-2"></i>Created Date
                                                </div>
                                                <div class="info-value">
                                                    <fmt:formatDate value="${requestDto.createdAtAsDate}"
                                                        pattern="dd/MM/yyyy HH:mm" />
                                                </div>
                                            </div>

                                            <!-- Last Updated Date -->
                                            <div class="info-item">
                                                <div class="info-label">
                                                    <i class="fas fa-calendar-check me-2"></i>Last Updated
                                                </div>
                                                <div class="info-value">
                                                    <c:choose>
                                                        <c:when test="${not empty requestDto.updatedAtAsDate}">
                                                            <fmt:formatDate value="${requestDto.updatedAtAsDate}"
                                                                pattern="dd/MM/yyyy HH:mm" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-muted fst-italic">N/A</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Type-Specific Details Section -->
                        <c:choose>
                            <%-- Leave Request Details --%>
                                <c:when test="${requestDto.requestTypeCode.startsWith('LEAVE_')}">
                                    <jsp:include page="sections/leave-detail-section.jsp" />
                                </c:when>

                                <%-- OT Request Details --%>
                                    <c:when test="${requestDto.requestTypeCode == 'OVERTIME_REQUEST'}">
                                        <jsp:include page="sections/ot-detail-section.jsp" />
                                    </c:when>

                                    <%-- Appeal/Adjustment Request Details --%>
                                        <c:when test="${requestDto.requestTypeCode == 'ADJUSTMENT_REQUEST'}">
                                            <jsp:include page="sections/appeal-detail-section.jsp" />
                                        </c:when>

                                        <%-- Recruitment Request Details --%>
                                            <c:when test="${requestDto.requestTypeCode == 'RECRUITMENT_REQUEST'}">
                                                <jsp:include page="sections/recruitment-detail-section.jsp" />
                                            </c:when>

                                            <%-- Other Request Types --%>
                                                <c:otherwise>
                                                    <div class="card mb-4">
                                                        <div class="card-header bg-info text-white">
                                                            <h5 class="mb-0"><i class="fas fa-list-alt me-2"></i>Request Details
                                                            </h5>
                                                        </div>
                                                        <div class="card-body">
                                                            <div class="alert alert-info mb-0" role="alert">
                                                                <i class="fas fa-info-circle me-2"></i>
                                                                Detailed information not available for this request type.
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:otherwise>
                        </c:choose>

                        <!-- Attachments Section -->
                        <c:if test="${not empty attachments}">
                            <div class="card mb-4 shadow-sm border-0">
                                <div class="card-header bg-gradient-info text-white">
                                    <h5 class="mb-0">
                                        <i class="fas fa-paperclip me-2"></i>Attachments
                                        <span class="badge bg-white text-dark ms-2">${attachments.size()}</span>
                                    </h5>
                                </div>
                                <div class="card-body p-0">
                                    <div class="attachment-grid p-3">
                                        <c:forEach items="${attachments}" var="attachment" varStatus="status">
                                            <c:choose>
                                                <%-- External Link (Google Drive) --%>
                                                <c:when test="${attachment.attachmentType == 'LINK'}">
                                                    <div class="attachment-card link-card">
                                                        <div class="attachment-icon-wrapper bg-success bg-opacity-10">
                                                            <i class="fab fa-google-drive fa-2x text-success"></i>
                                                        </div>
                                                        <div class="attachment-info">
                                                            <h6 class="attachment-name mb-1">
                                                                <c:out value="${attachment.originalName}" />
                                                            </h6>
                                                            <p class="attachment-meta mb-2">
                                                                <span class="badge bg-success">
                                                                    <i class="fas fa-link me-1"></i>External Link
                                                                </span>
                                                            </p>
                                                        </div>
                                                        <div class="attachment-actions">
                                                            <a href="${attachment.externalUrl}"
                                                               target="_blank"
                                                               rel="noopener noreferrer"
                                                               class="btn btn-sm btn-success">
                                                                <i class="fas fa-external-link-alt me-1"></i>Open Link
                                                            </a>
                                                        </div>
                                                    </div>
                                                </c:when>

                                                <%-- File Upload --%>
                                                <c:otherwise>
                                                    <div class="attachment-card file-card">
                                                        <div class="attachment-icon-wrapper
                                                            <c:choose>
                                                                <c:when test="${attachment.contentType.startsWith('image/')}">bg-info bg-opacity-10</c:when>
                                                                <c:when test="${attachment.contentType == 'application/pdf'}">bg-danger bg-opacity-10</c:when>
                                                                <c:when test="${attachment.contentType.contains('word')}">bg-primary bg-opacity-10</c:when>
                                                                <c:otherwise>bg-secondary bg-opacity-10</c:otherwise>
                                                            </c:choose>">
                                                            <c:choose>
                                                                <c:when test="${attachment.contentType.startsWith('image/')}">
                                                                    <i class="fas fa-file-image fa-2x text-info"></i>
                                                                </c:when>
                                                                <c:when test="${attachment.contentType == 'application/pdf'}">
                                                                    <i class="fas fa-file-pdf fa-2x text-danger"></i>
                                                                </c:when>
                                                                <c:when test="${attachment.contentType.contains('word')}">
                                                                    <i class="fas fa-file-word fa-2x text-primary"></i>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <i class="fas fa-file fa-2x text-secondary"></i>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </div>
                                                        <div class="attachment-info">
                                                            <h6 class="attachment-name mb-1">
                                                                <c:out value="${attachment.originalName}" />
                                                            </h6>
                                                            <p class="attachment-meta mb-2">
                                                                <span class="badge bg-primary me-1">
                                                                    <i class="fas fa-hdd me-1"></i>File
                                                                </span>
                                                                <small class="text-muted">
                                                                    <c:choose>
                                                                        <c:when test="${attachment.sizeBytes < 1024}">
                                                                            ${attachment.sizeBytes} B
                                                                        </c:when>
                                                                        <c:when test="${attachment.sizeBytes < 1048576}">
                                                                            <fmt:formatNumber value="${attachment.sizeBytes / 1024}" maxFractionDigits="1" /> KB
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <fmt:formatNumber value="${attachment.sizeBytes / 1048576}" maxFractionDigits="2" /> MB
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </small>
                                                            </p>
                                                        </div>
                                                        <div class="attachment-actions">
                                                            <a href="${pageContext.request.contextPath}/attachments/${attachment.id}/download"
                                                               class="btn btn-sm btn-primary">
                                                                <i class="fas fa-download me-1"></i>Download
                                                            </a>
                                                        </div>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <!-- Approval History Section -->
                        <c:if test="${requestDto.status == 'APPROVED' || requestDto.status == 'REJECTED'}">
                            <div class="card mb-4 shadow-sm border-0">
                                <div class="card-header ${requestDto.status == 'APPROVED' ? 'bg-gradient-success' : 'bg-gradient-danger'} text-white">
                                    <h5 class="mb-0">
                                        <i class="fas fa-history me-2"></i>Approval History
                                    </h5>
                                </div>
                                <div class="card-body p-4">
                                    <div class="timeline-item">
                                        <div class="timeline-marker ${requestDto.status == 'APPROVED' ? 'bg-success' : 'bg-danger'}">
                                            <i class="fas ${requestDto.status == 'APPROVED' ? 'fa-check' : 'fa-times'} text-white"></i>
                                        </div>
                                        <div class="timeline-content">
                                            <div class="row g-4">
                                                <!-- Left Column: Approver Info -->
                                                <div class="col-lg-6">
                                                    <div class="approval-info-card">
                                                        <h6 class="text-muted mb-3">
                                                            <i class="fas fa-user-shield me-2"></i>Approver Information
                                                        </h6>

                                                        <div class="info-item mb-3">
                                                            <div class="info-label">
                                                                <i class="fas fa-user-check me-2"></i>
                                                                <c:choose>
                                                                    <c:when test="${requestDto.status == 'APPROVED'}">Approved By</c:when>
                                                                    <c:when test="${requestDto.status == 'REJECTED'}">Rejected By</c:when>
                                                                </c:choose>
                                                            </div>
                                                            <div class="info-value fw-semibold">
                                                                <c:choose>
                                                                    <c:when test="${not empty requestDto.approverName}">
                                                                        <c:out value="${requestDto.approverName}" />
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="text-muted fst-italic">N/A</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </div>

                                                        <div class="info-item">
                                                            <div class="info-label">
                                                                <i class="fas fa-calendar-check me-2"></i>
                                                                <c:choose>
                                                                    <c:when test="${requestDto.status == 'APPROVED'}">Approval Date</c:when>
                                                                    <c:when test="${requestDto.status == 'REJECTED'}">Rejection Date</c:when>
                                                                </c:choose>
                                                            </div>
                                                            <div class="info-value">
                                                                <c:choose>
                                                                    <c:when test="${not empty requestDto.approvedAtAsDate}">
                                                                        <fmt:formatDate value="${requestDto.approvedAtAsDate}"
                                                                            pattern="dd/MM/yyyy HH:mm" />
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="text-muted fst-italic">N/A</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <!-- Right Column: Reason & Notes -->
                                                <div class="col-lg-6">
                                                    <!-- Approval/Rejection Reason -->
                                                    <c:if test="${requestDto.status == 'APPROVED' || requestDto.status == 'REJECTED'}">
                                                        <div class="${requestDto.status == 'REJECTED' ? 'rejection-reason-card' : 'approval-reason-card'}">
                                                            <h6 class="${requestDto.status == 'REJECTED' ? 'text-danger' : 'text-success'} mb-3">
                                                                <i class="fas ${requestDto.status == 'REJECTED' ? 'fa-comment-slash' : 'fa-comment-check'} me-2"></i>
                                                                ${requestDto.status == 'REJECTED' ? 'Rejection Reason' : 'Approval Reason'}
                                                            </h6>
                                                            <div class="alert ${requestDto.status == 'REJECTED' ? 'alert-danger' : 'alert-success'} mb-0 border-0">
                                                                <i class="fas fa-info-circle me-2"></i>
                                                                <c:choose>
                                                                    <c:when test="${not empty requestDto.rejectReason}">
                                                                        <c:out value="${requestDto.rejectReason}" />
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="text-muted fst-italic">No reason provided</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </div>
                                                    </c:if>

                                                    <!-- Manager Notes (from detail JSON if available) -->
                                                    <c:if test="${not empty managerNotes}">
                                                        <div class="manager-notes-card ${requestDto.status == 'REJECTED' ? 'mt-3' : ''}">
                                                            <h6 class="text-info mb-3">
                                                                <i class="fas fa-sticky-note me-2"></i>Manager Notes
                                                            </h6>
                                                            <div class="alert alert-info mb-0 border-0">
                                                                <i class="fas fa-quote-left me-2"></i>
                                                                <c:out value="${managerNotes}" />
                                                            </div>
                                                        </div>
                                                    </c:if>

                                                    <!-- Approval Message (for APPROVED status without notes) -->
                                                    <c:if test="${requestDto.status == 'APPROVED' && empty managerNotes}">
                                                        <div class="approval-message-card">
                                                            <div class="alert alert-success mb-0 border-0">
                                                                <i class="fas fa-thumbs-up me-2"></i>
                                                                This request has been approved successfully.
                                                            </div>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>

                <!-- Approval Modal -->
                <jsp:include page="modals/approval-modal.jsp" />

                <!-- Attachment Modal -->
                <jsp:include page="modals/attachment-modal.jsp" />

                <!-- Bootstrap JS -->
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

                <!-- Toast Notification Container -->
                <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 11000;">
                    <div id="validationToast" class="toast border-0 shadow-lg fade hide" role="alert" aria-live="assertive" aria-atomic="true">
                        <div class="toast-header bg-danger text-white">
                            <i class="fas fa-exclamation-circle me-2" id="toastIcon"></i>
                            <strong class="me-auto" id="toastTitle">Notification</strong>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast" aria-label="Close"></button>
                        </div>
                        <div class="toast-body" id="toastMessage">Message here</div>
                    </div>
                </div>

                <style>
                    /* Toast notification styling */
                    #validationToast {
                        min-width: 350px;
                        max-width: 550px;
                    }

                    #validationToast .toast-header {
                        border-radius: 0.375rem 0.375rem 0 0;
                    }

                    #validationToast .toast-header.bg-danger {
                        background-color: #dc3545 !important;
                    }

                    #validationToast .toast-header.bg-success {
                        background-color: #198754 !important;
                    }

                    #validationToast .toast-body {
                        font-size: 0.95rem;
                        padding: 1rem;
                        line-height: 1.5;
                    }

                    /* Animation */
                    #validationToast.show {
                        animation: slideInRight 0.3s ease-out;
                    }

                    @keyframes slideInRight {
                        from {
                            transform: translateX(100%);
                            opacity: 0;
                        }
                        to {
                            transform: translateX(0);
                            opacity: 1;
                        }
                    }
                </style>

                <!-- Toast Auto-show Script -->
                <script>
                    document.addEventListener('DOMContentLoaded', function() {
                        // Check for server error
                        const serverError = document.getElementById('serverError');
                        if (serverError && serverError.value) {
                            showToast(serverError.value, 'danger', 'Error');
                        }

                        // Check for server success
                        const serverSuccess = document.getElementById('serverSuccess');
                        if (serverSuccess && serverSuccess.value) {
                            showToast(serverSuccess.value, 'success', 'Success');
                        }
                    });

                    /**
                     * Show toast notification
                     * @param {string} message - Message to display
                     * @param {string} type - Type: 'success', 'danger', 'warning', 'info'
                     * @param {string} title - Toast title
                     */
                    function showToast(message, type, title) {
                        const toast = document.getElementById('validationToast');
                        const toastHeader = toast.querySelector('.toast-header');
                        const toastIcon = document.getElementById('toastIcon');
                        const toastTitle = document.getElementById('toastTitle');
                        const toastMessage = document.getElementById('toastMessage');

                        // Set message and title
                        toastMessage.textContent = message;
                        toastTitle.textContent = title || 'Notification';

                        // Remove all bg classes
                        toastHeader.classList.remove('bg-danger', 'bg-success', 'bg-warning', 'bg-info');

                        // Set appropriate style based on type
                        if (type === 'success') {
                            toastHeader.classList.add('bg-success');
                            toastIcon.className = 'fas fa-check-circle me-2';
                        } else if (type === 'danger') {
                            toastHeader.classList.add('bg-danger');
                            toastIcon.className = 'fas fa-exclamation-circle me-2';
                        } else if (type === 'warning') {
                            toastHeader.classList.add('bg-warning');
                            toastIcon.className = 'fas fa-exclamation-triangle me-2';
                        } else {
                            toastHeader.classList.add('bg-info');
                            toastIcon.className = 'fas fa-info-circle me-2';
                        }

                        // Show toast
                        const bsToast = new bootstrap.Toast(toast, {
                            autohide: true,
                            delay: 5000
                        });
                        bsToast.show();
                    }
                </script>

                <!-- Page-specific JavaScript -->
                <script src="${pageContext.request.contextPath}/assets/js/approval-modal.js?v=1"></script>
                <script src="${pageContext.request.contextPath}/assets/js/request-detail.js?v=2"></script>
            </body>

            </html>