<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<%-- Function to format job type --%>
<c:set var="formattedJobType" value="${job.jobType}" />
<c:choose>
    <c:when test="${job.jobType == 'FULL_TIME'}">
        <c:set var="formattedJobType" value="Full Time" />
    </c:when>
    <c:when test="${job.jobType == 'PART_TIME'}">
        <c:set var="formattedJobType" value="Part Time" />
    </c:when>
    <c:when test="${job.jobType == 'CONTRACT'}">
        <c:set var="formattedJobType" value="Contract" />
    </c:when>
    <c:when test="${job.jobType == 'INTERN'}">
        <c:set var="formattedJobType" value="Intern" />
    </c:when>
    <c:when test="${job.jobType == 'TEMPORARY'}">
        <c:set var="formattedJobType" value="Temporary" />
    </c:when>
    <c:when test="${job.jobType == 'INTERNSHIP'}">
        <c:set var="formattedJobType" value="Internship" />
    </c:when>
    <c:otherwise>
        <c:set var="formattedJobType" value="${fn:toLowerCase(job.jobType)}" />
        <c:set var="formattedJobType" value="${fn:toUpperCase(fn:substring(formattedJobType, 0, 1))}${fn:substring(formattedJobType, 1, -1)}" />
    </c:otherwise>
</c:choose>

<%-- Function to format job level --%>
<c:set var="formattedLevel" value="${job.level}" />
<c:choose>
    <c:when test="${job.level == 'JUNIOR'}">
        <c:set var="formattedLevel" value="Junior" />
    </c:when>
    <c:when test="${job.level == 'MIDDLE'}">
        <c:set var="formattedLevel" value="Middle" />
    </c:when>
    <c:when test="${job.level == 'SENIOR'}">
        <c:set var="formattedLevel" value="Senior" />
    </c:when>
    <c:when test="${job.level == 'MANAGER'}">
        <c:set var="formattedLevel" value="Manager" />
    </c:when>
    <c:when test="${job.level == 'LEAD'}">
        <c:set var="formattedLevel" value="Lead" />
    </c:when>
    <c:when test="${job.level == 'FRESHER'}">
        <c:set var="formattedLevel" value="Fresher" />
    </c:when>
    <c:when test="${job.level == 'ENTRY'}">
        <c:set var="formattedLevel" value="Entry Level" />
    </c:when>
    <c:otherwise>
        <c:set var="formattedLevel" value="${fn:toLowerCase(job.level)}" />
        <c:set var="formattedLevel" value="${fn:toUpperCase(fn:substring(formattedLevel, 0, 1))}${fn:substring(formattedLevel, 1, -1)}" />
    </c:otherwise>
</c:choose>

<%-- Function to format salary --%>
<c:set var="formattedSalaryRange" value="" />
<c:choose>
    <c:when test="${not empty job.minSalary && not empty job.maxSalary}">
        <fmt:formatNumber value="${job.minSalary}" pattern="#,###" var="minSalaryFormatted" />
        <fmt:formatNumber value="${job.maxSalary}" pattern="#,###" var="maxSalaryFormatted" />
        <c:set var="formattedSalaryRange" value="${minSalaryFormatted} - ${maxSalaryFormatted} VND" />
    </c:when>
    <c:when test="${not empty job.minSalary}">
        <fmt:formatNumber value="${job.minSalary}" pattern="#,###" var="minSalaryFormatted" />
        <c:set var="formattedSalaryRange" value="From ${minSalaryFormatted} VND" />
    </c:when>
    <c:when test="${not empty job.maxSalary}">
        <fmt:formatNumber value="${job.maxSalary}" pattern="#,###" var="maxSalaryFormatted" />
        <c:set var="formattedSalaryRange" value="Up to ${maxSalaryFormatted} VND" />
    </c:when>
    <c:otherwise>
        <c:set var="formattedSalaryRange" value="Competitive" />
    </c:otherwise>
</c:choose>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp">
        <jsp:param name="pageTitle" value="${job.title}" />
        <jsp:param name="pageCss" value="landing.css" />
    </jsp:include>
</head>
<body>
    <jsp:include page="/WEB-INF/views/layout/header.jsp">
        <jsp:param name="currentPage" value="jobs" />
    </jsp:include>

    <!-- Job Header Hero Section -->
    <section class="job-hero-section">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-lg-8">
                    <div class="job-hero-content" data-aos="fade-right">
                        <div class="job-breadcrumb">
                            <a href="${pageContext.request.contextPath}/jobs" class="breadcrumb-link">
                                <i class="fas fa-arrow-left me-2"></i>Back to Jobs
                            </a>
                        </div>
                        
                        <h1 class="job-hero-title">${job.title}</h1>
                        
                        <div class="job-hero-meta">
                            <div class="job-meta-item">
                                <i class="fas fa-building"></i>
                                <span><c:out value="${department != null ? department.name : 'Department'}" /></span>
                            </div>
                            <div class="job-meta-item">
                                <i class="fas fa-map-marker-alt"></i>
                                <span><c:out value="${empty job.workingLocation ? 'Office Location' : job.workingLocation}" /></span>
                            </div>
                            <div class="job-meta-item">
                                <i class="fas fa-clock"></i>
                                <span><c:out value="${formattedJobType}" /></span>
                            </div>
                            <div class="job-meta-item">
                                <i class="fas fa-layer-group"></i>
                                <span><c:out value="${formattedLevel}" /></span>
                            </div>
                        </div>
                        
                        <div class="job-hero-badges">
                            <span class="job-hero-badge job-type">${formattedJobType}</span>
                            <span class="job-hero-badge job-level">${formattedLevel}</span>
                            <c:if test="${job.numberOfPositions > 1}">
                                <span class="job-hero-badge job-positions">${job.numberOfPositions} positions</span>
                            </c:if>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="job-hero-image" data-aos="fade-left">
                        <div class="job-hero-icon">
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
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Job Content Section -->
    <section class="job-content-section">
        <div class="container">
            <div class="row">
                <div class="col-lg-8 mb-4">
                    <div class="job-content-card" data-aos="fade-up">
                        <!-- About Company Section -->
                        <div class="content-section">
                            <div class="section-header">
                                <i class="fas fa-building section-icon"></i>
                                <h3>About Company</h3>
                            </div>
                            <div class="section-content">
                                <p><strong>FPT Smart Cloud (FCI)</strong> – a member of FPT Corporation, pioneers AI & Cloud solutions in Vietnam. FCI was founded with the mission to generate an immense leap in productivity and agility in business operations.</p>
                            </div>
                        </div>

                        <!-- Job Description Section -->
                        <div class="content-section">
                            <div class="section-header">
                                <i class="fas fa-file-alt section-icon"></i>
                                <h3>Job Description</h3>
                            </div>
                            <div class="section-content">
                                <c:out value="${job.description}" escapeXml="false" />
                            </div>
                        </div>

                        <!-- Key Responsibilities Section -->
                        <div class="content-section">
                            <div class="section-header">
                                <i class="fas fa-tasks section-icon"></i>
                                <h3>Key Responsibilities</h3>
                            </div>
                            <div class="section-content">
                                <c:out value="${job.requirements}" escapeXml="false" />
                            </div>
                        </div>

                        <!-- Benefits Section -->
                        <div class="content-section">
                            <div class="section-header">
                                <i class="fas fa-gift section-icon"></i>
                                <h3>Benefits & Perks</h3>
                            </div>
                            <div class="section-content">
                                <c:out value="${job.benefits}" escapeXml="false" />
                            </div>
                        </div>

                        <!-- Working Environment Section -->
                        <div class="content-section">
                            <div class="section-header">
                                <i class="fas fa-map-marker-alt section-icon"></i>
                                <h3>Working Environment</h3>
                            </div>
                            <div class="section-content">
                                <div class="work-details">
                                    <div class="work-detail-item">
                                        <i class="fas fa-map-marker-alt"></i>
                                        <div>
                                            <strong>Location</strong>
                                            <span><c:out value="${empty job.workingLocation ? 'Office Location' : job.workingLocation}" /></span>
                                        </div>
                                    </div>
                                    <div class="work-detail-item">
                                        <i class="fas fa-calendar-alt"></i>
                                        <div>
                                            <strong>Start Date</strong>
                                            <span>
                                                <c:choose>
                                                    <c:when test="${not empty job.startDate}">
                                                        <c:set var="startDateStr" value="${job.startDate.toString()}" />
                                                        <fmt:parseDate value="${startDateStr}" pattern="yyyy-MM-dd" var="parsedStartDate" />
                                                        <fmt:formatDate value="${parsedStartDate}" pattern="MMMM d, yyyy" />
                                                    </c:when>
                                                    <c:otherwise>To be determined</c:otherwise>
                                                </c:choose>
                                            </span>
                                        </div>
                                    </div>
                                    <div class="work-detail-item">
                                        <i class="fas fa-clock"></i>
                                        <div>
                                            <strong>Working Hours</strong>
                                            <span><c:out value="${empty job.workingHours ? 'Standard office hours' : job.workingHours}" /></span>
                                        </div>
                                    </div>
                                    <div class="work-detail-item">
                                        <i class="fas fa-users"></i>
                                        <div>
                                            <strong>Positions Available</strong>
                                            <span><c:out value="${job.numberOfPositions}" /> position<c:if test="${job.numberOfPositions > 1}">s</c:if></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Contact Section -->
                        <div class="content-section">
                            <div class="section-header">
                                <i class="fas fa-phone section-icon"></i>
                                <h3>Contact Information</h3>
                            </div>
                            <div class="section-content">
                                <div class="contact-info">
                                    <div class="contact-item">
                                        <i class="fas fa-envelope"></i>
                                        <div>
                                            <strong>Email</strong>
                                            <a href="mailto:${job.contactEmail}"><c:out value="${empty job.contactEmail ? 'hr@company.com' : job.contactEmail}" /></a>
                                        </div>
                                    </div>
                                    <div class="contact-item">
                                        <i class="fas fa-phone"></i>
                                        <div>
                                            <strong>Phone</strong>
                                            <a href="tel:${job.contactPhone}"><c:out value="${empty job.contactPhone ? '+84 123 456 789' : job.contactPhone}" /></a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Apply Button -->
                        <div class="apply-section">
                            <button class="btn btn-apply">
                                <i class="fas fa-paper-plane me-2"></i>Apply Now
                            </button>
                            <p class="apply-note">Ready to join our team? Send us your CV and let's start the conversation!</p>
                        </div>
                    </div>
                </div>
                
                <!-- Sidebar -->
                <div class="col-lg-4">
                    <div class="job-sidebar" data-aos="fade-up" data-aos-delay="200">
                        <!-- Quick Info Card -->
                        <div class="sidebar-card">
                            <h4 class="sidebar-title">
                                <i class="fas fa-info-circle me-2"></i>Quick Info
                            </h4>
                            <div class="quick-info">
                                <div class="info-item">
                                    <span class="info-label">Job Type</span>
                                    <span class="info-value"><c:out value="${formattedJobType}"/></span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">Experience Level</span>
                                    <span class="info-value"><c:out value="${formattedLevel}"/></span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">Application Deadline</span>
                                    <span class="info-value deadline">
                                        <c:choose>
                                            <c:when test="${not empty job.applicationDeadline}">
                                                <c:set var="deadlineStr" value="${job.applicationDeadline.toString()}" />
                                                <fmt:parseDate value="${deadlineStr}" pattern="yyyy-MM-dd" var="parsedDeadline" />
                                                <fmt:formatDate value="${parsedDeadline}" pattern="MMMM d, yyyy" />
                                            </c:when>
                                            <c:otherwise>Until filled</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">Salary Range</span>
                                    <span class="info-value salary">
                                        ${formattedSalaryRange}
                                    </span>
                                </div>
                            </div>
                        </div>

                        <!-- Share Card -->
                        <div class="sidebar-card">
                            <h4 class="sidebar-title">
                                <i class="fas fa-share-alt me-2"></i>Share This Job
                            </h4>
                            <div class="share-buttons">
                                <a href="#" class="share-btn facebook" title="Share on Facebook">
                                    <i class="fab fa-facebook-f"></i>
                                </a>
                                <a href="#" class="share-btn twitter" title="Share on Twitter">
                                    <i class="fab fa-twitter"></i>
                                </a>
                                <a href="#" class="share-btn linkedin" title="Share on LinkedIn">
                                    <i class="fab fa-linkedin-in"></i>
                                </a>
                                <a href="#" class="share-btn email" title="Share via Email">
                                    <i class="fas fa-envelope"></i>
                                </a>
                            </div>
                        </div>

                        <!-- Related Jobs Card -->
                        <div class="sidebar-card">
                            <h4 class="sidebar-title">
                                <i class="fas fa-briefcase me-2"></i>Other Opportunities
                            </h4>
                            <div class="related-jobs">
                                <a href="${pageContext.request.contextPath}/jobs" class="related-job-link">
                                    <i class="fas fa-arrow-right me-2"></i>View All Open Positions
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <style>
        /* Job Hero Section */
        .job-hero-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 80px 0 60px 0;
            color: white;
            position: relative;
            overflow: hidden;
        }

        .job-hero-section::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
            opacity: 0.1;
        }

        .job-breadcrumb {
            margin-bottom: 20px;
        }

        .breadcrumb-link {
            color: rgba(255, 255, 255, 0.8);
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s ease;
        }

        .breadcrumb-link:hover {
            color: white;
            transform: translateX(-5px);
        }

        .job-hero-title {
            font-size: 3rem;
            font-weight: 700;
            margin-bottom: 25px;
            line-height: 1.2;
        }

        .job-hero-meta {
            display: flex;
            flex-wrap: wrap;
            gap: 25px;
            margin-bottom: 25px;
        }

        .job-meta-item {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 1rem;
            color: rgba(255, 255, 255, 0.9);
        }

        .job-meta-item i {
            font-size: 1.1rem;
            color: rgba(255, 255, 255, 0.7);
        }

        .job-hero-badges {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
        }

        .job-hero-badge {
            padding: 8px 16px;
            border-radius: 25px;
            font-size: 0.85rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .job-hero-badge.job-type {
            background: rgba(255, 255, 255, 0.2);
            color: white;
            border: 1px solid rgba(255, 255, 255, 0.3);
        }

        .job-hero-badge.job-level {
            background: rgba(67, 233, 123, 0.2);
            color: #43e97b;
            border: 1px solid rgba(67, 233, 123, 0.3);
        }

        .job-hero-badge.job-positions {
            background: rgba(79, 172, 254, 0.2);
            color: #4facfe;
            border: 1px solid rgba(79, 172, 254, 0.3);
        }

        .job-hero-image {
            text-align: center;
        }

        .job-hero-icon {
            width: 120px;
            height: 120px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 30px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-size: 3rem;
            color: white;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }

        /* Job Content Section */
        .job-content-section {
            padding: 80px 0;
            background: #f8f9fa;
        }

        .job-content-card {
            background: white;
            border-radius: 20px;
            padding: 40px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
        }

        .content-section {
            margin-bottom: 40px;
            padding-bottom: 30px;
            border-bottom: 1px solid #e9ecef;
        }

        .content-section:last-child {
            border-bottom: none;
            margin-bottom: 0;
        }

        .section-header {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
        }

        .section-icon {
            width: 40px;
            height: 40px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 1.1rem;
            margin-right: 15px;
        }

        .section-header h3 {
            color: #2c3e50;
            font-weight: 600;
            margin: 0;
            font-size: 1.4rem;
        }

        .section-content {
            color: #6c757d;
            line-height: 1.7;
            font-size: 1rem;
        }

        .section-content p {
            margin-bottom: 15px;
        }

        .section-content strong {
            color: #2c3e50;
        }

        /* Work Details */
        .work-details {
            display: grid;
            gap: 20px;
        }

        .work-detail-item {
            display: flex;
            align-items: center;
            gap: 15px;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 12px;
            border-left: 4px solid #667eea;
        }

        .work-detail-item i {
            width: 20px;
            color: #667eea;
            font-size: 1.1rem;
        }

        .work-detail-item div {
            flex: 1;
        }

        .work-detail-item strong {
            display: block;
            color: #2c3e50;
            font-weight: 600;
            margin-bottom: 2px;
        }

        .work-detail-item span {
            color: #6c757d;
        }

        /* Contact Info */
        .contact-info {
            display: grid;
            gap: 15px;
        }

        .contact-item {
            display: flex;
            align-items: center;
            gap: 15px;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 12px;
        }

        .contact-item i {
            width: 20px;
            color: #667eea;
            font-size: 1.1rem;
        }

        .contact-item div {
            flex: 1;
        }

        .contact-item strong {
            display: block;
            color: #2c3e50;
            font-weight: 600;
            margin-bottom: 2px;
        }

        .contact-item a {
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
        }

        .contact-item a:hover {
            text-decoration: underline;
        }

        /* Apply Section */
        .apply-section {
            text-align: center;
            padding: 30px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 15px;
            color: white;
        }

        .btn-apply {
            background: white;
            color: #667eea;
            border: none;
            padding: 15px 40px;
            border-radius: 50px;
            font-weight: 600;
            font-size: 1.1rem;
            transition: all 0.3s ease;
            margin-bottom: 15px;
        }

        .btn-apply:hover {
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
            color: #667eea;
        }

        .apply-note {
            margin: 0;
            opacity: 0.9;
            font-size: 0.95rem;
        }

        /* Sidebar */
        .job-sidebar {
            position: sticky;
            top: 30px;
        }

        .sidebar-card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            margin-bottom: 25px;
            box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
            border: 1px solid rgba(102, 126, 234, 0.1);
        }

        .sidebar-title {
            color: #2c3e50;
            font-weight: 600;
            margin-bottom: 20px;
            font-size: 1.1rem;
            display: flex;
            align-items: center;
        }

        .sidebar-title i {
            color: #667eea;
        }

        /* Quick Info */
        .quick-info {
            display: grid;
            gap: 15px;
        }

        .info-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid #f1f3f4;
        }

        .info-item:last-child {
            border-bottom: none;
        }

        .info-label {
            color: #6c757d;
            font-weight: 500;
            font-size: 0.9rem;
        }

        .info-value {
            color: #2c3e50;
            font-weight: 600;
            text-align: right;
        }

        .info-value.deadline {
            color: #e74c3c;
        }

        .info-value.salary {
            color: #27ae60;
        }

        /* Share Buttons */
        .share-buttons {
            display: flex;
            gap: 10px;
            justify-content: center;
        }

        .share-btn {
            width: 45px;
            height: 45px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            text-decoration: none;
            transition: all 0.3s ease;
            font-size: 1.1rem;
        }

        .share-btn.facebook { background: #3b5998; }
        .share-btn.twitter { background: #1da1f2; }
        .share-btn.linkedin { background: #0077b5; }
        .share-btn.email { background: #34495e; }

        .share-btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
            color: white;
        }

        /* Related Jobs */
        .related-job-link {
            display: flex;
            align-items: center;
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
            padding: 12px 15px;
            border-radius: 8px;
            background: rgba(102, 126, 234, 0.05);
            transition: all 0.3s ease;
        }

        .related-job-link:hover {
            background: rgba(102, 126, 234, 0.1);
            color: #667eea;
            transform: translateX(5px);
        }

        /* Responsive */
        @media (max-width: 768px) {
            .job-hero-title {
                font-size: 2rem;
            }
            
            .job-hero-meta {
                flex-direction: column;
                gap: 15px;
            }
            
            .job-content-card {
                padding: 25px;
            }
            
            .work-details {
                grid-template-columns: 1fr;
            }
            
            .job-hero-icon {
                width: 80px;
                height: 80px;
                font-size: 2rem;
            }
            
            .share-buttons {
                flex-wrap: wrap;
            }
        }

        /* Animations */
        .job-content-card {
            animation: fadeInUp 0.6s ease-out;
        }

        .sidebar-card {
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

    <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>
</html>
