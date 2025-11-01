<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp">
        <jsp:param name="pageTitle" value="Jobs" />
        <jsp:param name="pageCss" value="landing.css" />
    </jsp:include>
</head>

<body>
    <jsp:include page="/WEB-INF/views/layout/header.jsp">
        <jsp:param name="currentPage" value="jobs" />
    </jsp:include>

    <!-- Hero Section for Jobs -->
    <section class="hero-section"
        style="padding: 80px 0 60px 0; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-lg-8 mx-auto text-center">
                    <div class="hero-content" data-aos="fade-up">
                        <h1 style="color: white; margin-bottom: 20px;">Career Opportunities</h1>
                        <p style="color: rgba(255,255,255,0.9); font-size: 1.2rem; margin-bottom: 30px;">
                            Join our team and build your career with exciting opportunities
                        </p>
                        <div class="stats-row"
                            style="display: flex; justify-content: center; gap: 40px; flex-wrap: wrap;">
                            <div class="stat-item" style="text-align: center; color: white;">
                                <div style="font-size: 2rem; font-weight: bold;">${totalJobs > 0 ? totalJobs : jobs.size()}</div>
                                <div style="opacity: 0.8;">Open Positions</div>
                            </div>
                            <div class="stat-item" style="text-align: center; color: white;">
                                <div style="font-size: 2rem; font-weight: bold;">${departments.size()}</div>
                                <div style="opacity: 0.8;">Departments</div>
                            </div>
                            <div class="stat-item" style="text-align: center; color: white;">
                                <div style="font-size: 2rem; font-weight: bold;">100+</div>
                                <div style="opacity: 0.8;">Employees</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Jobs Section -->
    <section class="features-section" id="jobs" style="padding: 80px 0;">
        <div class="container">
            <div class="section-title" data-aos="fade-up">
                <h2>Available Positions</h2>
                <p>Discover exciting career opportunities and join our growing team</p>
            </div>

            <!-- Filters: search by title, department, job type -->
            <div class="jobs-filter my-4" data-aos="fade-up">
                <form method="get" action="" class="row g-2">
                    <div class="col-md-5">
                        <input type="text" name="title" class="form-control" placeholder="Search by job title"
                               value="${fn:escapeXml(param.title)}" />
                    </div>

                    <div class="col-md-3">
                        <select name="departmentId" class="form-select">
                            <option value="">All Departments</option>
                            <c:forEach items="${departments}" var="dept">
                                <option value="${dept.id}" <c:if test="${param.departmentId == dept.id}">selected</c:if>>
                                    <c:out value="${dept.name}" />
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-2">
                        <select name="jobType" class="form-select">
                            <option value="">All Types</option>
                            <option value="FULL_TIME" <c:if test="${param.jobType == 'FULL_TIME'}">selected</c:if>>Full Time</option>
                            <option value="PART_TIME" <c:if test="${param.jobType == 'PART_TIME'}">selected</c:if>>Part Time</option>
                            <option value="CONTRACT" <c:if test="${param.jobType == 'CONTRACT'}">selected</c:if>>Contract</option>
                            <option value="INTERN" <c:if test="${param.jobType == 'INTERN'}">selected</c:if>>Intern</option>
                            <option value="TEMPORARY" <c:if test="${param.jobType == 'TEMPORARY'}">selected</c:if>>Temporary</option>
                            <option value="INTERNSHIP" <c:if test="${param.jobType == 'INTERNSHIP'}">selected</c:if>>Internship</option>
                        </select>
                    </div>

                    <div class="col-md-2 d-grid">
                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary w-100">Filter</button>
                            <a href="${pageContext.request.contextPath}/jobs" class="btn btn-outline-secondary">Clear</a>
                        </div>
                    </div>
                </form>
            </div>

            <c:choose>
                <c:when test="${not empty jobs}">
                    <div class="row">
                        <c:forEach items="${jobs}" var="job" varStatus="status">
                            <%-- Format job type for current job --%>
                            <c:set var="currentJobType" value="${job.jobType}" />
                            <c:choose>
                                <c:when test="${job.jobType == 'FULL_TIME'}">
                                    <c:set var="currentJobType" value="Full Time" />
                                </c:when>
                                <c:when test="${job.jobType == 'PART_TIME'}">
                                    <c:set var="currentJobType" value="Part Time" />
                                </c:when>
                                <c:when test="${job.jobType == 'CONTRACT'}">
                                    <c:set var="currentJobType" value="Contract" />
                                </c:when>
                                <c:when test="${job.jobType == 'INTERN'}">
                                    <c:set var="currentJobType" value="Intern" />
                                </c:when>
                                <c:when test="${job.jobType == 'TEMPORARY'}">
                                    <c:set var="currentJobType" value="Temporary" />
                                </c:when>
                                <c:when test="${job.jobType == 'INTERNSHIP'}">
                                    <c:set var="currentJobType" value="Internship" />
                                </c:when>
                            </c:choose>

                            <%-- Format job level for current job --%>
                            <c:set var="currentJobLevel" value="${job.level}" />
                            <c:choose>
                                <c:when test="${job.level == 'JUNIOR'}">
                                    <c:set var="currentJobLevel" value="Junior" />
                                </c:when>
                                <c:when test="${job.level == 'MIDDLE'}">
                                    <c:set var="currentJobLevel" value="Middle" />
                                </c:when>
                                <c:when test="${job.level == 'SENIOR'}">
                                    <c:set var="currentJobLevel" value="Senior" />
                                </c:when>
                                <c:when test="${job.level == 'MANAGER'}">
                                    <c:set var="currentJobLevel" value="Manager" />
                                </c:when>
                                <c:when test="${job.level == 'LEAD'}">
                                    <c:set var="currentJobLevel" value="Lead" />
                                </c:when>
                                <c:when test="${job.level == 'FRESHER'}">
                                    <c:set var="currentJobLevel" value="Fresher" />
                                </c:when>
                                <c:when test="${job.level == 'ENTRY'}">
                                    <c:set var="currentJobLevel" value="Entry Level" />
                                </c:when>
                            </c:choose>

                            <%-- Format salary for current job --%>
                            <c:set var="currentSalaryRange" value="" />
                            <c:choose>
                                <c:when test="${not empty job.minSalary && not empty job.maxSalary}">
                                    <fmt:formatNumber value="${job.minSalary}" pattern="#,###" var="currentMinSalary" />
                                    <fmt:formatNumber value="${job.maxSalary}" pattern="#,###" var="currentMaxSalary" />
                                    <c:set var="currentSalaryRange" value="${currentMinSalary} - ${currentMaxSalary} VND" />
                                </c:when>
                                <c:when test="${not empty job.minSalary}">
                                    <fmt:formatNumber value="${job.minSalary}" pattern="#,###" var="currentMinSalary" />
                                    <c:set var="currentSalaryRange" value="From ${currentMinSalary} VND" />
                                </c:when>
                                <c:when test="${not empty job.maxSalary}">
                                    <fmt:formatNumber value="${job.maxSalary}" pattern="#,###" var="currentMaxSalary" />
                                    <c:set var="currentSalaryRange" value="Up to ${currentMaxSalary} VND" />
                                </c:when>
                                <c:otherwise>
                                    <c:set var="currentSalaryRange" value="Competitive" />
                                </c:otherwise>
                            </c:choose>

                            <div class="col-lg-4 col-md-6 mb-4" data-aos="fade-up"
                                data-aos-delay="${(status.index % 3) * 100 + 100}">
                          <div class="job-card clickable-card" onclick="showJobDetail('${job.id}')" 
                                     title="Click to view job details">
                                    <div class="job-header">
                                        <div class="job-icon">
                                            <c:choose>
                                                <c:when test="${job.level == 'SENIOR' || job.level == 'MANAGER'}">
                                                    <i class="fas fa-user-tie"></i>
                                                </c:when>
                                                <c:when test="${job.level == 'JUNIOR' || job.level == 'FRESHER'}">
                                                    <i class="fas fa-user-graduate"></i>
                                                </c:when>
                                                <c:when test="${job.jobType == 'INTERN'}">
                                                    <i class="fas fa-user-plus"></i>
                                                </c:when>
                                                <c:otherwise>
                                                    <i class="fas fa-briefcase"></i>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="job-badges">
                                            <c:if test="${not empty job.jobType}">
                                                <span class="job-badge job-type">${currentJobType}</span>
                                            </c:if>
                                            <c:if test="${not empty job.level}">
                                                <span class="job-badge job-level">${currentJobLevel}</span>
                                            </c:if>
                                        </div>
                                    </div>

                                    <div class="job-content">
                                        <h4 class="job-title">${job.title}</h4>

                                        <div class="job-details">
                                            <div class="job-detail-item">
                                                <i class="fas fa-building"></i>
                                                <span>
                                                    <c:set var="deptFound" value="false" />
                                                    <c:forEach items="${departments}" var="dept">
                                                        <c:if test="${dept.id == job.departmentId}">
                                                            ${dept.name}
                                                            <c:set var="deptFound" value="true" />
                                                        </c:if>
                                                    </c:forEach>
                                                    <c:if test="${!deptFound}">Department</c:if>
                                                </span>
                                            </div>

                                            <div class="job-detail-item">
                                                <i class="fas fa-map-marker-alt"></i>
                                                <span>${empty job.workingLocation ? 'Office' : job.workingLocation}</span>
                                            </div>

                                            <c:if test="${not empty job.numberOfPositions && job.numberOfPositions > 1}">
                                                <div class="job-detail-item">
                                                    <i class="fas fa-users"></i>
                                                    <span>${job.numberOfPositions} positions</span>
                                                </div>
                                            </c:if>

                                            <c:if test="${not empty job.minSalary || not empty job.maxSalary}">
                                                <div class="job-detail-item">
                                                    <i class="fas fa-dollar-sign"></i>
                                                    <span>${currentSalaryRange}</span>
                                                </div>
                                            </c:if>
                                        </div>

                                        <c:if test="${not empty job.description}">
                                            <p class="job-description">
                                                <c:choose>
                                                    <c:when test="${job.description.length() > 120}">
                                                        ${job.description.substring(0, 120)}...
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${job.description}
                                                    </c:otherwise>
                                                </c:choose>
                                            </p>
                                        </c:if>
                                    </div>

                                    <div class="job-footer">
                    <button type="button" class="btn btn-hero-outline job-detail-btn"
                        onclick="event.stopPropagation(); showJobDetail('${job.id}')">
                                            <i class="fas fa-eye me-2"></i>View Details
                                        </button>
                                        <a href="${pageContext.request.contextPath}/job-view-publish?id=${job.id}"
                                           class="btn btn-outline-secondary mt-2" style="font-size: 0.85rem;">
                                            <i class="fas fa-external-link-alt me-1"></i>Full Page
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Jobs pagination" class="mt-4">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${currentPage - 1}${queryString}">«</a>
                                </li>
                                <c:set var="startPage" value="${currentPage - 2}" />
                                <c:set var="endPage" value="${currentPage + 2}" />
                                <c:if test="${startPage < 1}">
                                    <c:set var="startPage" value="1" />
                                </c:if>
                                <c:if test="${endPage > totalPages}">
                                    <c:set var="endPage" value="${totalPages}" />
                                </c:if>

                                <c:if test="${startPage > 1}">
                                    <li class="page-item"><a class="page-link" href="?page=1${queryString}">1</a></li>
                                    <c:if test="${startPage > 2}">
                                        <li class="page-item disabled"><span class="page-link">...</span></li>
                                    </c:if>
                                </c:if>

                                <c:forEach begin="${startPage}" end="${endPage}" var="p">
                                    <li class="page-item ${currentPage == p ? 'active' : ''}">
                                        <a class="page-link" href="?page=${p}${queryString}">${p}</a>
                                    </li>
                                </c:forEach>

                                <c:if test="${endPage < totalPages}">
                                    <c:if test="${endPage < totalPages - 1}">
                                        <li class="page-item disabled"><span class="page-link">...</span></li>
                                    </c:if>
                                    <li class="page-item"><a class="page-link" href="?page=${totalPages}${queryString}">${totalPages}</a></li>
                                </c:if>

                                <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${currentPage + 1}${queryString}">»</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <div class="empty-jobs-state" data-aos="fade-up">
                        <div class="empty-icon">
                            <i class="fas fa-briefcase"></i>
                        </div>
                        <h4>No Open Positions</h4>
                        <p>We don't have any job openings at the moment, but we're always looking for talented
                            individuals. Check back soon!</p>
                        <a href="${pageContext.request.contextPath}/contact" class="btn btn-hero-outline">
                            <i class="fas fa-envelope me-2"></i>Contact HR
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>

    <!-- Job Detail Modal -->
    <div class="modal fade" id="jobDetailModal" tabindex="-1" aria-labelledby="jobDetailModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-scrollable">
            <div class="modal-content">
                <div class="modal-header" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
                    <h5 class="modal-title" id="jobDetailModalLabel">
                        <i class="fas fa-briefcase me-2"></i>Job Details
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" id="jobDetailContent">
                    <div class="text-center py-4">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                        <p class="mt-2">Loading job details...</p>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times me-1"></i>Close
                    </button>
                    <a id="viewFullPageBtn" href="#" class="btn btn-primary">
                        <i class="fas fa-external-link-alt me-1"></i>View Full Page
                    </a>
                </div>
            </div>
        </div>
    </div>

    <style>
        /* Job Cards Styling */
        .job-card {
            background: white;
            border-radius: 20px;
            padding: 30px;
            height: 100%;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
            border: 1px solid rgba(102, 126, 234, 0.1);
            display: flex;
            flex-direction: column;
            position: relative;
            overflow: hidden;
        }

        .job-card:hover {
            transform: translateY(-15px) scale(1.02);
            box-shadow: 0 25px 50px rgba(102, 126, 234, 0.25);
            border-color: rgba(102, 126, 234, 0.4);
        }

        .job-card::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
            opacity: 0;
            transition: opacity 0.3s ease;
            pointer-events: none;
        }

        .job-card:hover::after {
            opacity: 1;
        }

        .job-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 20px;
        }

        .job-icon {
            width: 60px;
            height: 60px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 24px;
            flex-shrink: 0;
        }

        .job-badges {
            display: flex;
            flex-direction: column;
            gap: 5px;
            align-items: flex-end;
        }

        .job-badge {
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.75rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .job-badge.job-type {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
        }

        .job-badge.job-level {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
            color: #2d3436;
        }

        .job-content {
            flex-grow: 1;
            margin-bottom: 20px;
        }

        .job-title {
            color: #2c3e50;
            font-size: 1.4rem;
            font-weight: 700;
            margin-bottom: 15px;
            line-height: 1.3;
        }

        .job-details {
            margin-bottom: 15px;
        }

        .job-detail-item {
            display: flex;
            align-items: center;
            margin-bottom: 8px;
            color: #6c757d;
            font-size: 0.9rem;
        }

        .job-detail-item i {
            width: 16px;
            margin-right: 10px;
            color: #667eea;
            font-size: 0.85rem;
        }

        .job-description {
            color: #6c757d;
            line-height: 1.6;
            font-size: 0.95rem;
            margin-bottom: 0;
        }

        .job-footer {
            margin-top: auto;
        }

        .job-footer .btn {
            width: 100%;
            padding: 12px;
            font-weight: 600;
            border-radius: 12px;
            transition: all 0.3s ease;
        }

        .job-footer .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
        }

        /* Clickable card styling */
        .clickable-card {
            cursor: pointer;
        }

        .clickable-card::before {
            content: '👁️ Click to view details';
            position: absolute;
            top: 15px;
            right: 15px;
            background: rgba(102, 126, 234, 0.9);
            color: white;
            padding: 5px 10px;
            border-radius: 15px;
            font-size: 0.75rem;
            font-weight: 500;
            opacity: 0;
            transform: translateY(-10px);
            transition: all 0.3s ease;
            z-index: 10;
            pointer-events: none;
        }

        .clickable-card:hover::before {
            opacity: 1;
            transform: translateY(0);
        }

        /* Enhanced button styling */
        .job-detail-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
            border: none !important;
            color: white !important;
            font-weight: 600 !important;
            padding: 12px 20px !important;
            border-radius: 12px !important;
            transition: all 0.3s ease !important;
        }

        .job-detail-btn:hover {
            transform: translateY(-2px) !important;
            box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4) !important;
            background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%) !important;
        }

        /* Empty State */
        .empty-jobs-state {
            text-align: center;
            padding: 60px 20px;
            max-width: 500px;
            margin: 0 auto;
        }

        .empty-icon {
            font-size: 4rem;
            color: #667eea;
            margin-bottom: 20px;
            opacity: 0.7;
        }

        .empty-jobs-state h4 {
            color: #2c3e50;
            font-weight: 600;
            margin-bottom: 15px;
        }

        .empty-jobs-state p {
            color: #6c757d;
            line-height: 1.6;
            margin-bottom: 25px;
        }

        /* Modal Styling */
        .modal-content {
            border: none;
            border-radius: 20px;
            overflow: hidden;
        }

        .modal-header {
            border: none;
            padding: 20px 30px;
        }

        .modal-body {
            padding: 30px;
        }

        .modal-footer {
            border: none;
            padding: 20px 30px;
            background-color: #f8f9fa;
        }

        /* Hero Section Stats */
        .stats-row {
            margin-top: 30px;
        }

        .stat-item {
            min-width: 120px;
        }

        .stat-item div:first-child {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .job-card {
                padding: 20px;
            }

            .job-header {
                flex-direction: column;
                align-items: center;
                text-align: center;
                gap: 15px;
            }

            .job-badges {
                flex-direction: row;
                align-items: center;
            }

            .stats-row {
                gap: 20px !important;
            }

            .stat-item {
                min-width: 80px;
            }
        }

        /* Animation for job cards */
        .job-card {
            animation: fadeInUp 0.6s ease-out;
        }

        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }

            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
    </style>

    <script>
        function showJobDetail(jobId) {
            // Show loading state
            document.getElementById('jobDetailModalLabel').innerHTML = 
                '<i class="fas fa-briefcase me-2"></i>Loading...';
            document.getElementById('jobDetailContent').innerHTML = 
                '<div class="text-center py-4"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div><p class="mt-2">Loading job details...</p></div>';
            
            // Update full page link
            document.getElementById('viewFullPageBtn').href = 
                '${pageContext.request.contextPath}/job-view-publish?id=' + jobId;
            
            // Show modal immediately
            var modal = new bootstrap.Modal(document.getElementById('jobDetailModal'));
            modal.show();
            
            // Redirect to full page after a short delay
            setTimeout(function() {
                window.location.href = '${pageContext.request.contextPath}/job-view-publish?id=' + jobId;
            }, 1000);
        }

        // Add click effect for job cards
        document.addEventListener('DOMContentLoaded', function() {
            var jobCards = document.querySelectorAll('.job-card');
            jobCards.forEach(function(card) {
                card.addEventListener('mouseenter', function() {
                    this.style.transform = 'translateY(-10px) scale(1.02)';
                });
                
                card.addEventListener('mouseleave', function() {
                    this.style.transform = 'translateY(0) scale(1)';
                });
            });
        });
    </script>

    <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>

</html>