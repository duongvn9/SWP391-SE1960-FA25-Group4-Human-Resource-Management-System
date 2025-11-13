<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="View Job Posting - HRMS"/>
    </jsp:include>
    <style>
        :root {
            --primary-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            --success-gradient: linear-gradient(135deg, #0cebeb 0%, #20e3b2 100%);
            --warning-gradient: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }
        
        .job-hero {
            background: var(--primary-gradient);
            color: white;
            padding: 3rem 2rem;
            border-radius: 16px;
            margin-bottom: 2rem;
            box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
            position: relative;
            overflow: hidden;
        }
        
        .job-hero::before {
            content: '';
            position: absolute;
            top: -50%;
            right: -10%;
            width: 300px;
            height: 300px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 50%;
        }
        
        .job-hero::after {
            content: '';
            position: absolute;
            bottom: -30%;
            left: -5%;
            width: 200px;
            height: 200px;
            background: rgba(255, 255, 255, 0.08);
            border-radius: 50%;
        }
        
        .job-hero-content {
            position: relative;
            z-index: 1;
        }
        
        .job-title {
            font-size: 2.5rem;
            font-weight: 700;
            margin-bottom: 1rem;
            text-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .job-code {
            font-size: 0.9rem;
            background: rgba(255, 255, 255, 0.2);
            padding: 0.5rem 1rem;
            border-radius: 20px;
            display: inline-block;
            backdrop-filter: blur(10px);
        }
        
        .info-card {
            background: #fff;
            border-radius: 12px;
            padding: 1.5rem;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
            transition: transform 0.3s, box-shadow 0.3s;
            height: 100%;
            border: 1px solid #f0f0f0;
        }
        
        .info-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.12);
        }
        
        .info-card-icon {
            width: 50px;
            height: 50px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            margin-bottom: 1rem;
        }
        
        .info-card-icon.purple {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .info-card-icon.green {
            background: linear-gradient(135deg, #0cebeb 0%, #20e3b2 100%);
            color: white;
        }
        
        .info-card-icon.orange {
            background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
            color: white;
        }
        
        .info-card-icon.blue {
            background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
            color: white;
        }
        
        .info-card-title {
            font-size: 0.85rem;
            color: #6c757d;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 0.5rem;
            font-weight: 600;
        }
        
        .info-card-value {
            font-size: 1.25rem;
            font-weight: 700;
            color: #2c3e50;
        }
        
        .detail-section {
            background: #fff;
            border-radius: 12px;
            padding: 2rem;
            margin-bottom: 1.5rem;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
            border: 1px solid #f0f0f0;
        }
        
        .section-title {
            font-size: 1.5rem;
            font-weight: 700;
            color: #2c3e50;
            margin-bottom: 1.5rem;
            padding-bottom: 0.75rem;
            border-bottom: 3px solid #667eea;
            display: inline-block;
        }
        
        .meta-item {
            padding: 0.75rem 0;
            border-bottom: 1px solid #f0f0f0;
            display: flex;
            align-items: center;
        }
        
        .meta-item:last-child {
            border-bottom: none;
        }
        
        .meta-item i {
            width: 35px;
            height: 35px;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 1rem;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            font-size: 0.9rem;
        }
        
        .meta-label {
            font-weight: 600;
            color: #495057;
            min-width: 180px;
            margin-right: 1rem;
        }
        
        .meta-value {
            color: #2c3e50;
            flex: 1;
        }
        
        .content-block {
            background: #f8f9fa;
            padding: 1.5rem;
            border-radius: 8px;
            line-height: 1.8;
            white-space: pre-wrap;
            border-left: 4px solid #667eea;
        }
        
        .status-badge {
            padding: 0.5rem 1.5rem;
            border-radius: 25px;
            font-weight: 600;
            font-size: 0.9rem;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .status-badge i {
            font-size: 1rem;
        }
        
        .action-buttons {
            display: flex;
            gap: 1rem;
            flex-wrap: wrap;
        }
        
        .btn-action {
            padding: 0.75rem 1.5rem;
            border-radius: 8px;
            font-weight: 600;
            transition: all 0.3s;
            border: none;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .btn-action:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }
            </style>
</head>
<body>
<div class="dashboard-wrapper">
    <!-- Sidebar -->
    <jsp:include page="../layout/sidebar.jsp">
        <jsp:param name="currentPage" value="job-postings"/>
    </jsp:include>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Header -->
        <jsp:include page="../layout/dashboard-header.jsp">
            <jsp:param name="pageTitle" value="Job Posting Details"/>
        </jsp:include>

        <!-- Content Area -->
        <div class="container-fluid px-4 py-4">
            <!-- Back button -->
            <div class="mb-4">
                <a href="${pageContext.request.contextPath}/job-postings" class="btn btn-outline-secondary">
                    <i class="fas fa-arrow-left me-2"></i>Back to Job Postings
                </a>
            </div>

            <!-- Hero Section -->
            <div class="job-hero">
                <div class="job-hero-content">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <h1 class="job-title">${jobPosting.title}</h1>
                            <div class="job-code">
                                <i class="fas fa-hashtag me-2"></i>${jobPosting.code}
                            </div>
                        </div>
                        <div>
                            <c:choose>
                                <c:when test="${jobPosting.status == 'PENDING'}">
                                    <span class="status-badge bg-warning text-dark">
                                        <i class="fas fa-clock"></i> ${jobPosting.status}
                                    </span>
                                </c:when>
                                <c:when test="${jobPosting.status == 'APPROVED'}">
                                    <span class="status-badge bg-info text-white">
                                        <i class="fas fa-check"></i> ${jobPosting.status}
                                    </span>
                                </c:when>
                                <c:when test="${jobPosting.status == 'REJECTED'}">
                                    <span class="status-badge bg-danger text-white">
                                        <i class="fas fa-times"></i> ${jobPosting.status}
                                    </span>
                                </c:when>
                                <c:when test="${jobPosting.status == 'PUBLISHED'}">
                                    <span class="status-badge bg-success text-white">
                                        <i class="fas fa-globe"></i> ${jobPosting.status}
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge bg-secondary text-white">
                                        <i class="fas fa-question"></i> ${jobPosting.status}
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Debug Info (Remove in production) -->
            <c:if test="${param.debug == 'true'}">
            <div class="alert alert-info">
                <strong>Debug Info:</strong><br/>
                jobPosting.departmentId = ${jobPosting.departmentId}<br/>
                departments size = ${not empty departments ? departments.size() : '0'}<br/>
                <c:forEach items="${departments}" var="dept" varStatus="status">
                    dept[${status.index}]: id=${dept.id}, name=${dept.name}<br/>
                </c:forEach>
            </div>
            </c:if>

            <!-- Quick Info Cards -->
            <div class="row g-3 mb-4">
                <div class="col-md-3">
                    <div class="info-card">
                        <div class="info-card-icon purple">
                            <i class="fas fa-building"></i>
                        </div>
                        <div class="info-card-title">Department</div>
                        <div class="info-card-value">
                            <c:set var="deptFound" value="false"/>
                            <c:forEach items="${departments}" var="dept">
                                <c:if test="${dept.id == jobPosting.departmentId}">
                                    <c:out value="${dept.name}"/>
                                    <c:set var="deptFound" value="true"/>
                                </c:if>
                            </c:forEach>
                            <c:if test="${!deptFound}">
                                <span class="text-muted">Not specified</span>
                            </c:if>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="info-card">
                        <div class="info-card-icon green">
                            <i class="fas fa-users"></i>
                        </div>
                        <div class="info-card-title">Positions</div>
                        <div class="info-card-value">${jobPosting.numberOfPositions} Openings</div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="info-card">
                        <div class="info-card-icon orange">
                            <i class="fas fa-layer-group"></i>
                        </div>
                        <div class="info-card-title">Level</div>
                        <div class="info-card-value">${jobPosting.level}</div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="info-card">
                        <div class="info-card-icon blue">
                            <i class="fas fa-briefcase"></i>
                        </div>
                        <div class="info-card-title">Job Type</div>
                        <div class="info-card-value">${jobPosting.jobType}</div>
                    </div>
                </div>
            </div>

            <!-- Approval History (for Rejected status) -->
            <c:if test="${jobPosting.status == 'REJECTED'}">
                <div class="detail-section mb-4" style="background: linear-gradient(135deg, #fee 0%, #fdd 100%); border-left: 4px solid #dc3545;">
                    <h3 class="section-title" style="border-bottom-color: #dc3545;">
                        <i class="fas fa-history me-2" style="color: #dc3545;"></i>Approval History
                    </h3>
                    
                    <div class="row">
                        <div class="col-md-6">
                            <h5 style="color: #495057; margin-bottom: 1rem;">
                                <i class="fas fa-user-shield me-2"></i>Approver Information
                            </h5>
                            <div class="meta-item">
                                <i class="fas fa-user" style="background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);"></i>
                                <span class="meta-label">Rejected By</span>
                                <span class="meta-value">
                                    <c:choose>
                                        <c:when test="${not empty approverUser}">
                                            <strong>${approverUser.fullName}</strong>
                                            <br><small class="text-muted">(${approverAccount.username})</small>
                                        </c:when>
                                        <c:when test="${not empty approverAccount}">
                                            ${approverAccount.username}
                                        </c:when>
                                        <c:otherwise>N/A</c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                            <div class="meta-item">
                                <i class="fas fa-calendar-times" style="background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);"></i>
                                <span class="meta-label">Rejection Date</span>
                                <span class="meta-value">
                                    <c:choose>
                                        <c:when test="${not empty jobPosting.approvedAt}">
                                            ${rejectionDateFormatted}
                                        </c:when>
                                        <c:otherwise>N/A</c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <h5 style="color: #dc3545; margin-bottom: 1rem;">
                                <i class="fas fa-ban me-2"></i>Rejection Reason
                            </h5>
                            <div class="alert alert-danger" role="alert">
                                <i class="fas fa-exclamation-circle me-2"></i>
                                <c:choose>
                                    <c:when test="${not empty jobPosting.rejectedReason}">
                                        ${jobPosting.rejectedReason}
                                    </c:when>
                                    <c:otherwise>No reason provided</c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>

            <!-- Approval Info (for Approved status) -->
            <c:if test="${jobPosting.status == 'APPROVED'}">
                <div class="detail-section mb-4" style="background: linear-gradient(135deg, #e6fff4 0%, #d1fae5 100%); border-left: 4px solid #20e3b2;">
                    <h3 class="section-title" style="border-bottom-color: #20e3b2;">
                        <i class="fas fa-history me-2" style="color: #20e3b2;"></i>Approval Info
                    </h3>
                    <div class="row">
                        <div class="col-md-6">
                            <h5 style="color: #0f766e; margin-bottom: 1rem;">
                                <i class="fas fa-user-shield me-2"></i>Approver Information
                            </h5>
                            <div class="meta-item">
                                <i class="fas fa-user" style="background: linear-gradient(135deg, #0cebeb 0%, #20e3b2 100%);"></i>
                                <span class="meta-label">Approved By</span>
                                <span class="meta-value">
                                    <c:choose>
                                        <c:when test="${not empty approverUser}">
                                            <strong>${approverUser.fullName}</strong>
                                            <br><small class="text-muted">(${approverAccount.username})</small>
                                        </c:when>
                                        <c:when test="${not empty approverAccount}">
                                            ${approverAccount.username}
                                        </c:when>
                                        <c:otherwise>N/A</c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <h5 style="color: #0f766e; margin-bottom: 1rem;">
                                <i class="fas fa-calendar-check me-2"></i>Approval Date
                            </h5>
                            <div class="meta-item">
                                <i class="fas fa-calendar" style="background: linear-gradient(135deg, #0cebeb 0%, #20e3b2 100%);"></i>
                                <span class="meta-label">Approved At</span>
                                <span class="meta-value">
                                    <c:choose>
                                        <c:when test="${not empty jobPosting.approvedAt}">
                                            ${rejectionDateFormatted}
                                        </c:when>
                                        <c:otherwise>N/A</c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>

            <!-- Action Buttons -->
            <c:if test="${sessionScope.loggedInUser != null}">
                <c:set var="showButtons" value="false"/>
                <c:set var="canEdit" value="false"/>
                
                <%-- Department Manager (position 9) cannot see any action buttons --%>
                <c:if test="${sessionScope.loggedInUser.positionId != 9}">
                    <%-- Both HRM (position 7) and HR (position 8) can approve/reject PENDING, publish APPROVED --%>
                    <c:if test="${(sessionScope.loggedInUser.positionId == 7 || sessionScope.loggedInUser.positionId == 8) && 
                                 (jobPosting.status == 'PENDING' || jobPosting.status == 'APPROVED')}">
                        <c:set var="showButtons" value="true"/>
                    </c:if>
                    
                    <%-- Both HRM and HR can edit PENDING --%>
                    <c:if test="${(sessionScope.loggedInUser.positionId == 7 || sessionScope.loggedInUser.positionId == 8) && 
                                 jobPosting.status == 'PENDING'}">
                        <c:set var="canEdit" value="true"/>
                    </c:if>
                    
                    <%-- Creator can edit REJECTED --%>
                    <c:if test="${jobPosting.status == 'REJECTED' && 
                                 sessionScope.accountId == jobPosting.createdByAccountId}">
                        <c:set var="showButtons" value="true"/>
                        <c:set var="canEdit" value="true"/>
                    </c:if>
                </c:if>
                
                <c:if test="${showButtons}">
                    <div class="detail-section mb-4">
                        <div class="action-buttons">
                            <%-- Both HRM and HR approve/reject buttons --%>
                            <c:if test="${(sessionScope.loggedInUser.positionId == 7 || sessionScope.loggedInUser.positionId == 8) && 
                                         jobPosting.status == 'PENDING'}">
                                <button type="button" class="btn btn-success btn-action" onclick="approveJobPosting('${jobPosting.id}')">
                                    <i class="fas fa-check-circle"></i> Approve
                                </button>
                                <button type="button" class="btn btn-danger btn-action" onclick="rejectJobPosting('${jobPosting.id}')">
                                    <i class="fas fa-times-circle"></i> Reject
                                </button>
                            </c:if>
                            
                            <%-- Edit button (for PENDING by HR, or REJECTED by creator) --%>
                            <c:if test="${canEdit}">
                                <a href="${pageContext.request.contextPath}/job-posting/edit?id=${jobPosting.id}" 
                                   class="btn btn-primary btn-action">
                                    <i class="fas fa-edit"></i> Edit
                                </a>
                            </c:if>
                            
                            <%-- Both HRM and HR publish button --%>
                            <c:if test="${(sessionScope.loggedInUser.positionId == 7 || sessionScope.loggedInUser.positionId == 8) && 
                                         jobPosting.status == 'APPROVED'}">
                                <button type="button" class="btn btn-primary btn-action" onclick="publishJobPosting('${jobPosting.id}')">
                                    <i class="fas fa-globe"></i> Publish
                                </button>
                            </c:if>
                        </div>
                    </div>
                </c:if>
            </c:if>

            <!-- Job Details -->
            <div class="detail-section">
                <h3 class="section-title">
                    <i class="fas fa-info-circle me-2"></i>Job Information
                </h3>
                
                <div class="row">
                    <div class="col-md-6">
                        <div class="meta-item">
                            <i class="fas fa-money-bill-wave"></i>
                            <span class="meta-label">Salary Range</span>
                            <span class="meta-value"><strong>${jobPosting.salaryRange}</strong></span>
                        </div>
                        <div class="meta-item">
                            <i class="fas fa-map-marker-alt"></i>
                            <span class="meta-label">Location</span>
                            <span class="meta-value">${jobPosting.workingLocation}</span>
                        </div>
                        <div class="meta-item">
                            <i class="fas fa-calendar-alt"></i>
                            <span class="meta-label">Start Date</span>
                            <span class="meta-value">
                                <c:if test="${not empty jobPosting.startDate}">
                                    <fmt:parseDate value="${jobPosting.startDate}" pattern="yyyy-MM-dd" var="startDate"/>
                                    <fmt:formatDate value="${startDate}" pattern="MMMM d, yyyy"/>
                                </c:if>
                                <c:if test="${empty jobPosting.startDate}">To Be Determined</c:if>
                            </span>
                        </div>
                        <div class="meta-item">
                            <i class="fas fa-hourglass-end"></i>
                            <span class="meta-label">Application Deadline</span>
                            <span class="meta-value">
                                <fmt:parseDate value="${jobPosting.applicationDeadline}" pattern="yyyy-MM-dd" var="deadline"/>
                                <fmt:formatDate value="${deadline}" pattern="MMMM d, yyyy"/>
                            </span>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="meta-item">
                            <i class="fas fa-user-clock"></i>
                            <span class="meta-label">Experience Required</span>
                            <span class="meta-value"><c:out value="${jobPosting.minExperienceYears}"/> years</span>
                        </div>
                        <div class="meta-item">
                            <i class="fas fa-clock"></i>
                            <span class="meta-label">Working Hours</span>
                            <span class="meta-value">
                                <c:choose>
                                    <c:when test="${not empty jobPosting.workingHours}">
                                        <c:out value="${jobPosting.workingHours}"/>
                                    </c:when>
                                    <c:otherwise><span class="text-muted">Not specified</span></c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="meta-item">
                            <i class="fas fa-envelope"></i>
                            <span class="meta-label">Contact Email</span>
                            <span class="meta-value">
                                <c:choose>
                                    <c:when test="${not empty jobPosting.contactEmail}">
                                        <a href="mailto:${jobPosting.contactEmail}">${jobPosting.contactEmail}</a>
                                    </c:when>
                                    <c:otherwise><span class="text-muted">Not provided</span></c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="meta-item">
                            <i class="fas fa-phone"></i>
                            <span class="meta-label">Contact Phone</span>
                            <span class="meta-value">
                                <c:choose>
                                    <c:when test="${not empty jobPosting.contactPhone}">
                                        <c:out value="${jobPosting.contactPhone}"/>
                                    </c:when>
                                    <c:otherwise><span class="text-muted">Not provided</span></c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Job Description -->
            <div class="detail-section">
                <h3 class="section-title">
                    <i class="fas fa-file-alt me-2"></i>Job Description
                </h3>
                <div class="content-block">${jobPosting.description}</div>
            </div>

            <!-- Requirements -->
            <div class="detail-section">
                <h3 class="section-title">
                    <i class="fas fa-list-check me-2"></i>Requirements
                </h3>
                <div class="content-block">${jobPosting.requirements}</div>
            </div>

            <!-- Benefits -->
            <div class="detail-section">
                <h3 class="section-title">
                    <i class="fas fa-gift me-2"></i>Benefits
                </h3>
                <div class="content-block">${jobPosting.benefits}</div>
            </div>
        </div>

        <jsp:include page="../layout/dashboard-footer.jsp"/>
    </div>
</div>

    <!-- Confirm Approve Modal -->
    <div class="modal fade" id="approveModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Approve Job Posting</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    Are you sure you want to approve this job posting?
                </div>
                <div class="modal-footer">
                    <form method="post" action="${pageContext.request.contextPath}/job-posting/approve">
                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                        <input type="hidden" name="id" id="approveJobId"/>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-success">Approve</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Reject Modal -->
    <div class="modal fade" id="rejectModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Reject Job Posting</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form method="post" action="${pageContext.request.contextPath}/job-posting/reject">
                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                        <input type="hidden" name="id" id="rejectJobId"/>
                        <div class="mb-3">
                            <label for="rejectReason" class="form-label">Reason for rejection</label>
                            <textarea class="form-control" id="rejectReason" name="reason" rows="3" required></textarea>
                        </div>
                        <div class="text-end">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-danger">Reject</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Publish Modal -->
    <div class="modal fade" id="publishModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Publish Job Posting</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    Are you sure you want to publish this job posting?
                    <br><br>
                    <small class="text-muted">
                        This will make the job posting visible to the public.
                    </small>
                </div>
                <div class="modal-footer">
                    <form method="post" action="${pageContext.request.contextPath}/job-posting/publish">
                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                        <input type="hidden" name="id" id="publishJobId"/>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Publish</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script>
        // Initialize modals
        const approveModal = new bootstrap.Modal(document.getElementById('approveModal'));
        const rejectModal = new bootstrap.Modal(document.getElementById('rejectModal'));
        const publishModal = new bootstrap.Modal(document.getElementById('publishModal'));
        
        function approveJobPosting(id) {
            document.getElementById('approveJobId').value = id;
            approveModal.show();
        }
        
        function rejectJobPosting(id) {
            document.getElementById('rejectJobId').value = id;
            rejectModal.show();
        }
        
        function publishJobPosting(id) {
            document.getElementById('publishJobId').value = id;
            publishModal.show();
        }
    </script>
</body>
</html>