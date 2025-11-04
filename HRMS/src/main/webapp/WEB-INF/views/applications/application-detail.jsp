<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
            <%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <jsp:include page="/WEB-INF/views/layout/head.jsp">
                        <jsp:param name="pageTitle" value="Application Detail" />
                    </jsp:include>
                    <link href="${pageContext.request.contextPath}/assets/css/dashboard.css" rel="stylesheet" />
                    <style>
                        .timeline {
                            position: relative;
                            padding-left: 30px;
                        }

                        .timeline::before {
                            content: '';
                            position: absolute;
                            left: 15px;
                            top: 0;
                            bottom: 0;
                            width: 2px;
                            background: #dee2e6;
                        }

                        .timeline-item {
                            position: relative;
                            margin-bottom: 30px;
                        }

                        .timeline-marker {
                            position: absolute;
                            left: -22px;
                            top: 5px;
                            width: 12px;
                            height: 12px;
                            border-radius: 50%;
                            border: 2px solid #fff;
                            box-shadow: 0 0 0 2px #dee2e6;
                        }

                        .timeline-content {
                            background: #f8f9fa;
                            padding: 15px;
                            border-radius: 8px;
                            border-left: 3px solid #007bff;
                        }

                        .timeline-content h6 {
                            color: #495057;
                            margin-bottom: 8px;
                        }
                    </style>
                </head>

                <body>
                    <div class="dashboard-wrapper">
                        <jsp:include page="/WEB-INF/views/layout/sidebar.jsp">
                            <jsp:param name="currentPage" value="applications" />
                        </jsp:include>

                        <div class="main-content">
                            <jsp:include page="/WEB-INF/views/layout/dashboard-header.jsp">
                                <jsp:param name="pageTitle" value="Application Detail" />
                            </jsp:include>

                            <div class="content-area">
                                <!-- Back button -->
                                <div class="mb-3">
                                    <a href="${pageContext.request.contextPath}/applications"
                                        class="btn btn-outline-secondary">
                                        <i class="fas fa-arrow-left me-2"></i>Back to Applications
                                    </a>
                                </div>

                                <!-- Application Info -->
                                <div class="card">
                                    <div class="card-header">
                                        <h5 class="mb-0">
                                            <i class="fas fa-file-alt me-2"></i>Application #${application.id}
                                        </h5>
                                    </div>
                                    <div class="card-body">
                                        <div class="row">
                                            <!-- Personal Information -->
                                            <div class="col-md-6">
                                                <h6 class="text-primary mb-3">
                                                    <i class="fas fa-user me-2"></i>Personal Information
                                                </h6>
                                                <div class="mb-2">
                                                    <strong>Full Name:</strong> ${fn:escapeXml(application.fullName)}
                                                </div>
                                                <div class="mb-2">
                                                    <strong>Email:</strong>
                                                    <a
                                                        href="mailto:${application.email}">${fn:escapeXml(application.email)}</a>
                                                </div>
                                                <div class="mb-2">
                                                    <strong>Phone:</strong>
                                                    <a
                                                        href="tel:${application.phone}">${fn:escapeXml(application.phone)}</a>
                                                </div>
                                                <c:if test="${not empty application.dob}">
                                                    <div class="mb-2">
                                                        <strong>Date of Birth:</strong> ${application.dob}
                                                    </div>
                                                </c:if>
                                                <c:if test="${not empty application.gender}">
                                                    <div class="mb-2">
                                                        <strong>Gender:</strong> ${fn:escapeXml(application.gender)}
                                                    </div>
                                                </c:if>
                                                <c:if test="${not empty application.hometown}">
                                                    <div class="mb-2">
                                                        <strong>Hometown:</strong> ${fn:escapeXml(application.hometown)}
                                                    </div>
                                                </c:if>
                                                <div class="mb-2">
                                                    <strong>Status:</strong>
                                                    <c:choose>
                                                        <c:when test="${application.status == 'new'}">
                                                            <span class="badge bg-info">New</span>
                                                        </c:when>
                                                        <c:when test="${application.status == 'reviewing'}">
                                                            <span class="badge bg-warning">Reviewing</span>
                                                        </c:when>
                                                        <c:when test="${application.status == 'approved'}">
                                                            <span class="badge bg-success">Approved</span>
                                                        </c:when>
                                                        <c:when test="${application.status == 'rejected'}">
                                                            <span class="badge bg-danger">Rejected</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span
                                                                class="badge bg-secondary">${fn:escapeXml(application.status)}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>

                                            <!-- Job Information -->
                                            <div class="col-md-6">
                                                <h6 class="text-primary mb-3">
                                                    <i class="fas fa-briefcase me-2"></i>Job Information
                                                </h6>
                                                <c:if test="${not empty jobPosting}">
                                                    <div class="mb-2">
                                                        <strong>Position:</strong> ${fn:escapeXml(jobPosting.title)}
                                                    </div>
                                                    <div class="mb-2">
                                                        <strong>Job ID:</strong> ${jobPosting.id}
                                                    </div>
                                                </c:if>
                                                <div class="mb-2">
                                                    <strong>Applied Date:</strong> ${application.createdAt}
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Address Information -->
                                        <c:if
                                            test="${not empty application.addressLine1 || not empty application.city}">
                                            <hr class="my-4">
                                            <div class="row">
                                                <div class="col-12">
                                                    <h6 class="text-primary mb-3">
                                                        <i class="fas fa-map-marker-alt me-2"></i>Address Information
                                                    </h6>
                                                    <div class="mb-2">
                                                        <strong>Address:</strong>
                                                        <c:if test="${not empty application.addressLine1}">
                                                            ${fn:escapeXml(application.addressLine1)}
                                                        </c:if>
                                                        <c:if test="${not empty application.addressLine2}">
                                                            <br>${fn:escapeXml(application.addressLine2)}
                                                        </c:if>
                                                        <c:if test="${not empty application.city}">
                                                            <br>${fn:escapeXml(application.city)}
                                                            <c:if test="${not empty application.state}">
                                                                , ${fn:escapeXml(application.state)}
                                                            </c:if>
                                                            <c:if test="${not empty application.postalCode}">
                                                                ${fn:escapeXml(application.postalCode)}
                                                            </c:if>
                                                        </c:if>
                                                        <c:if test="${not empty application.country}">
                                                            <br>${fn:escapeXml(application.country)}
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>

                                        <!-- CCCD Information -->
                                        <c:if test="${not empty application.cccd}">
                                            <hr class="my-4">
                                            <div class="row">
                                                <div class="col-12">
                                                    <h6 class="text-primary mb-3">
                                                        <i class="fas fa-id-card me-2"></i>Citizen ID Information
                                                    </h6>
                                                    <div class="mb-2">
                                                        <strong>CCCD Number:</strong> ${fn:escapeXml(application.cccd)}
                                                    </div>
                                                    <c:if test="${not empty application.cccdIssuedDate}">
                                                        <div class="mb-2">
                                                            <strong>Issued Date:</strong> ${application.cccdIssuedDate}
                                                        </div>
                                                    </c:if>
                                                    <c:if test="${not empty application.cccdIssuedPlace}">
                                                        <div class="mb-2">
                                                            <strong>Issued Place:</strong>
                                                            ${fn:escapeXml(application.cccdIssuedPlace)}
                                                        </div>
                                                    </c:if>

                                                    <!-- CCCD Images -->
                                                    <c:if
                                                        test="${not empty application.cccdFrontPath || not empty application.cccdBackPath}">
                                                        <div class="mt-3">
                                                            <strong>CCCD Images:</strong>
                                                            <div class="row mt-2">
                                                                <c:if test="${not empty application.cccdFrontPath}">
                                                                    <div class="col-md-6">
                                                                        <div class="card">
                                                                            <div class="card-header">
                                                                                <small>Front Side</small>
                                                                            </div>
                                                                            <div class="card-body text-center">
                                                                                <i
                                                                                    class="fas fa-image fa-3x text-muted mb-3"></i>
                                                                                <br>
                                                                                <div class="mb-2">
                                                                                    <small class="text-muted">Image
                                                                                        URL:</small>
                                                                                    <br>
                                                                                    <code
                                                                                        class="small">${fn:escapeXml(application.cccdFrontPath)}</code>
                                                                                </div>
                                                                                <a href="${application.cccdFrontPath}"
                                                                                    target="_blank"
                                                                                    class="btn btn-sm btn-outline-primary">
                                                                                    <i
                                                                                        class="fas fa-external-link-alt me-1"></i>View
                                                                                    Image
                                                                                </a>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </c:if>
                                                                <c:if test="${not empty application.cccdBackPath}">
                                                                    <div class="col-md-6">
                                                                        <div class="card">
                                                                            <div class="card-header">
                                                                                <small>Back Side</small>
                                                                            </div>
                                                                            <div class="card-body text-center">
                                                                                <i
                                                                                    class="fas fa-image fa-3x text-muted mb-3"></i>
                                                                                <br>
                                                                                <div class="mb-2">
                                                                                    <small class="text-muted">Image
                                                                                        URL:</small>
                                                                                    <br>
                                                                                    <code
                                                                                        class="small">${fn:escapeXml(application.cccdBackPath)}</code>
                                                                                </div>
                                                                                <a href="${application.cccdBackPath}"
                                                                                    target="_blank"
                                                                                    class="btn btn-sm btn-outline-primary">
                                                                                    <i
                                                                                        class="fas fa-external-link-alt me-1"></i>View
                                                                                    Image
                                                                                </a>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </c:if>
                                                            </div>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </c:if>

                                        <!-- Resume -->
                                        <c:if test="${not empty application.resumePath}">
                                            <hr class="my-4">
                                            <div class="row">
                                                <div class="col-12">
                                                    <h6 class="text-primary mb-3">
                                                        <i class="fas fa-file-pdf me-2"></i>Resume/CV
                                                    </h6>
                                                    <div class="d-flex align-items-center">
                                                        <i class="fas fa-link fa-2x text-primary me-3"></i>
                                                        <div>
                                                            <div><strong>Resume URL</strong></div>
                                                            <div class="mb-2">
                                                                <code
                                                                    class="small">${fn:escapeXml(application.resumePath)}</code>
                                                            </div>
                                                            <div class="mt-2">
                                                                <a href="${application.resumePath}" target="_blank"
                                                                    class="btn btn-primary">
                                                                    <i class="fas fa-external-link-alt me-2"></i>Open
                                                                    Resume
                                                                </a>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>

                                        <!-- Notes -->
                                        <c:if test="${not empty application.note}">
                                            <hr class="my-4">
                                            <div class="row">
                                                <div class="col-12">
                                                    <h6 class="text-primary mb-3">
                                                        <i class="fas fa-sticky-note me-2"></i>Notes
                                                    </h6>
                                                    <div class="alert alert-info">
                                                        ${fn:escapeXml(application.note)}
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>

                                        <!-- Approval History -->
                                        <hr class="my-4">
                                        <div class="row">
                                            <div class="col-12">
                                                <h6 class="text-primary mb-3">
                                                    <i class="fas fa-history me-2"></i>Approval History
                                                </h6>
                                                <div class="timeline">
                                                    <!-- HR Approval -->
                                                    <c:if test="${not empty application.hrApprovalStatus}">
                                                        <div class="timeline-item">
                                                            <div class="timeline-marker bg-warning"></div>
                                                            <div class="timeline-content">
                                                                <h6 class="mb-1">HR Review</h6>
                                                                <p class="mb-1">
                                                                    <strong>${fn:escapeXml(application.hrApproverName)}</strong>
                                                                    <span
                                                                        class="badge ${application.hrApprovalStatus == 'approved' ? 'bg-success' : 'bg-danger'}">
                                                                        ${fn:escapeXml(application.hrApprovalStatus)}
                                                                    </span>
                                                                </p>
                                                                <c:if test="${not empty application.hrApprovalNote}">
                                                                    <p class="text-muted mb-1">
                                                                        ${fn:escapeXml(application.hrApprovalNote)}</p>
                                                                </c:if>
                                                                <small
                                                                    class="text-muted">${application.hrApprovalDate}</small>
                                                            </div>
                                                        </div>
                                                    </c:if>

                                                    <!-- HRM Approval -->
                                                    <c:if test="${not empty application.hrmApprovalStatus}">
                                                        <div class="timeline-item">
                                                            <div class="timeline-marker bg-primary"></div>
                                                            <div class="timeline-content">
                                                                <h6 class="mb-1">HRM Final Decision</h6>
                                                                <p class="mb-1">
                                                                    <strong>${fn:escapeXml(application.hrmApproverName)}</strong>
                                                                    <span
                                                                        class="badge ${application.hrmApprovalStatus == 'approved' ? 'bg-success' : 'bg-danger'}">
                                                                        ${fn:escapeXml(application.hrmApprovalStatus)}
                                                                    </span>
                                                                </p>
                                                                <c:if test="${not empty application.hrmApprovalNote}">
                                                                    <p class="text-muted mb-1">
                                                                        ${fn:escapeXml(application.hrmApprovalNote)}</p>
                                                                </c:if>
                                                                <small
                                                                    class="text-muted">${application.hrmApprovalDate}</small>
                                                            </div>
                                                        </div>
                                                    </c:if>

                                                    <!-- Application Submitted -->
                                                    <div class="timeline-item">
                                                        <div class="timeline-marker bg-info"></div>
                                                        <div class="timeline-content">
                                                            <h6 class="mb-1">Application Submitted</h6>
                                                            <p class="mb-1">Application was submitted by candidate</p>
                                                            <small class="text-muted">${application.createdAt}</small>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </body>

                </html>