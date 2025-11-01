<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<fmt:setLocale value="en" />

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
                            <button class="btn btn-apply" onclick="showApplicationModal('${job.id}', '${fn:escapeXml(job.title)}')">
                                <i class="fas fa-paper-plane me-2"></i>Apply Now
                            </button>
                            <p class="apply-note">Ready to join our team? Send us your CV and let's start the conversation!</p>
                        </div>
                    </div>
                </div>
                
                <!-- Sidebar -->
                <div class="col-lg-4">
                    <div class="job-sidebar" data-aos="fade-up" data-aos-delay="200">
                        <!-- Apply Now (sidebar) -->
                        <div class="sidebar-card sidebar-apply-top">
                            <div class="apply-top">
                                <button class="btn btn-apply w-100" onclick="showApplicationModal('${job.id}', '${fn:escapeXml(job.title)}')">
                                    <i class="fas fa-paper-plane me-2"></i>Apply Now
                                </button>
                            </div>
                        </div>
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

    <!-- Application Modal -->
    <div class="modal fade" id="applicationModal" tabindex="-1" aria-labelledby="applicationModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-scrollable">
            <div class="modal-content">
                <div class="modal-header" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
                    <h5 class="modal-title" id="applicationModalLabel">
                        <i class="fas fa-paper-plane me-2"></i>Apply for Position
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="applicationForm" enctype="multipart/form-data">
                        <input type="hidden" id="jobId" name="jobId" />
                        
                        <!-- CCCD Information Section - Required -->
                        <div class="form-section">
                            <h6 class="section-title">
                                <i class="fas fa-id-card me-2"></i>Citizen ID (CCCD) Information <span class="text-danger">*</span>
                            </h6>
                            <div class="mb-3">
                                <label for="cccdFrontUpload" class="form-label">Upload CCCD Front Image <span class="text-danger">*</span></label>
                                <input type="file" class="form-control" id="cccdFrontUpload" name="cccdFrontUpload" 
                                       accept="image/jpeg,image/jpg,image/png" required>
                                <div class="form-text">
                                    <strong>Please upload the front side of your Vietnamese Citizen ID Card (CCCD) first, then click "Extract Information" to automatically fill the form fields below.</strong><br>
                                    Supported formats: JPG, PNG (Max size: 10MB)
                                </div>
                                <button type="button" class="btn btn-primary btn-sm mt-2" id="processOCRBtn" disabled>
                                    <i class="fas fa-magic me-1"></i>Extract Information
                                </button>
                                <div id="ocrStatus" class="mt-2" style="display: none;">
                                    <div class="alert alert-info">
                                        <i class="fas fa-spinner fa-spin me-2"></i>Processing CCCD image...
                                    </div>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="cccd" class="form-label">CCCD Number <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="cccd" name="cccd" required>
                            </div>
                            <div class="mb-3">
                                <label for="cccdName" class="form-label">Full Name from CCCD <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="cccdName" name="cccdName" required>
                            </div>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="cccdDob" class="form-label">Date of Birth <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="cccdDob" name="cccdDob" 
                                           placeholder="DD/MM/YYYY" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="cccdGender" class="form-label">Gender <span class="text-danger">*</span></label>
                                    <select class="form-select" id="cccdGender" name="cccdGender" required>
                                        <option value="">Select Gender</option>
                                        <option value="NAM">Male</option>
                                        <option value="NU">Female</option>
                                    </select>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="cccdHometown" class="form-label">Hometown/Origin <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="cccdHometown" name="cccdHometown" required>
                            </div>
                        </div>

                        <!-- Contact Information Section -->
                        <div class="form-section">
                            <h6 class="section-title">
                                <i class="fas fa-envelope me-2"></i>Contact Information
                            </h6>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="email" class="form-label">Email Address <span class="text-danger">*</span></label>
                                    <input type="email" class="form-control" id="email" name="email" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="phone" class="form-label">Phone Number <span class="text-danger">*</span></label>
                                    <input type="tel" class="form-control" id="phone" name="phone" required>
                                </div>
                            </div>
                        </div>

                        <!-- Address Information Section -->
                        <div class="form-section">
                            <h6 class="section-title">
                                <i class="fas fa-map-marker-alt me-2"></i>Address Information
                            </h6>
                            <div class="mb-3">
                                <label for="addressLine1" class="form-label">Address Line 1</label>
                                <input type="text" class="form-control" id="addressLine1" name="addressLine1" placeholder="Street address">
                            </div>
                            <div class="mb-3">
                                <label for="addressLine2" class="form-label">Address Line 2</label>
                                <input type="text" class="form-control" id="addressLine2" name="addressLine2" placeholder="Apartment, suite, etc. (optional)">
                            </div>
                            <div class="row">
                                <div class="col-md-4 mb-3">
                                    <label for="city" class="form-label">City</label>
                                    <input type="text" class="form-control" id="city" name="city">
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="state" class="form-label">State/Province</label>
                                    <input type="text" class="form-control" id="state" name="state">
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="postalCode" class="form-label">Postal Code</label>
                                    <input type="text" class="form-control" id="postalCode" name="postalCode">
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="country" class="form-label">Country</label>
                                <input type="text" class="form-control" id="country" name="country" value="Vietnam">
                            </div>
                        </div>

                        <!-- Resume/CV Section -->
                        <div class="form-section">
                            <h6 class="section-title">
                                <i class="fas fa-file-alt me-2"></i>Resume/CV <span class="text-danger">*</span>
                            </h6>
                            <div class="mb-3">
                                <label for="resumeUrl" class="form-label">Resume/CV Link <span class="text-danger">*</span></label>
                                <input type="url" class="form-control" id="resumeUrl" name="resumeUrl" 
                                       placeholder="https://drive.google.com/... or https://linkedin.com/..." required>
                                <div class="form-text">Provide a link to your online resume (Google Drive, LinkedIn, etc.)</div>
                            </div>
                        </div>



                        <!-- Terms and Conditions -->
                        <div class="form-section">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="agreeTerms" name="agreeTerms" required>
                                <label class="form-check-label" for="agreeTerms">
                                    I confirm that all information provided is accurate and complete <span class="text-danger">*</span>
                                </label>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times me-1"></i>Cancel
                    </button>
                    <button type="button" class="btn btn-primary" onclick="submitApplication()">
                        <i class="fas fa-paper-plane me-1"></i>Submit Application
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Success Modal -->
    <div class="modal fade" id="successModal" tabindex="-1" aria-labelledby="successModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-body text-center py-4">
                    <div class="success-icon mb-3">
                        <i class="fas fa-check-circle"></i>
                    </div>
                    <h5 class="mb-3">Application Submitted Successfully!</h5>
                    <p class="text-muted mb-4">Thank you for your interest in this position. We have received your application with CCCD information and will review it shortly. You will hear from us within 5-7 business days.</p>
                    <button type="button" class="btn btn-primary" data-bs-dismiss="modal">
                        <i class="fas fa-check me-1"></i>Got it
                    </button>
                </div>
            </div>
        </div>
    </div>

    <script>
        function showApplicationModal(jobId, jobTitle) {
            // Set job information
            document.getElementById('jobId').value = jobId;
            document.getElementById('applicationModalLabel').innerHTML = 
                '<i class="fas fa-paper-plane me-2"></i>Apply for: ' + jobTitle;
            
            // Reset form
            document.getElementById('applicationForm').reset();
            
            // Show modal
            var modal = new bootstrap.Modal(document.getElementById('applicationModal'));
            modal.show();
        }

        function submitApplication() {
            var form = document.getElementById('applicationForm');
            if (!form) {
                alert('Form not found!');
                return;
            }
            
            // Get submit button
            var submitBtn = document.querySelector('button[onclick="submitApplication()"]');
            if (!submitBtn) {
                alert('Submit button not found!');
                return;
            }
            
            try {
                // Custom validation for resume
                var resumeUrl = document.getElementById('resumeUrl') ? document.getElementById('resumeUrl').value : '';
                
                if (!resumeUrl || resumeUrl.trim() === '') {
                    alert('Please provide a link to your resume');
                    return;
                }
                
                // CCCD validation (required)
                var cccdNumber = document.getElementById('cccd').value;
                if (!cccdNumber || cccdNumber.trim() === '') {
                    alert('CCCD number is required. Please upload CCCD image and extract information.');
                    return;
                }
                
                // Basic validation
                if (!form.checkValidity()) {
                    form.reportValidity();
                    return;
                }

                // No file size check needed for URL
                
                var cccdFront = document.getElementById('cccdFront') ? document.getElementById('cccdFront').files[0] : null;
                var cccdBack = document.getElementById('cccdBack') ? document.getElementById('cccdBack').files[0] : null;
                
                if (cccdFront && cccdFront.size > 5 * 1024 * 1024) {
                    alert('CCCD front image size must be less than 5MB');
                    return;
                }
                
                if (cccdBack && cccdBack.size > 5 * 1024 * 1024) {
                    alert('CCCD back image size must be less than 5MB');
                    return;
                }

                // Show loading state
                var originalText = submitBtn.innerHTML;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Submitting...';
                submitBtn.disabled = true;

                // Create FormData for form submission
                var formData = new FormData(form);

                // Submit to server
                fetch('${pageContext.request.contextPath}/submit-application', {
                    method: 'POST',
                    body: formData
                })
                .then(function(response) {
                    if (!response.ok) {
                        // Try to get detailed error message from response
                        return response.text().then(function(errorText) {
                            try {
                                var errorData = JSON.parse(errorText);
                                throw new Error('Server Error (' + response.status + '): ' + (errorData.message || 'Unknown error'));
                            } catch (e) {
                                throw new Error('Server Error (' + response.status + '): ' + errorText);
                            }
                        });
                    }
                    return response.json();
                })
                .then(function(data) {
                    // Reset button
                    submitBtn.innerHTML = originalText;
                    submitBtn.disabled = false;
                    
                    if (data.success) {
                        // Hide application modal
                        var applicationModal = bootstrap.Modal.getInstance(document.getElementById('applicationModal'));
                        if (applicationModal) {
                            applicationModal.hide();
                        }
                        
                        // Show success modal
                        var successModal = new bootstrap.Modal(document.getElementById('successModal'));
                        if (successModal) {
                            successModal.show();
                        }
                    } else {
                        alert('Error: ' + data.message);
                    }
                })
                .catch(function(error) {
                    console.error('Error submitting application:', error);
                    
                    // Reset button
                    submitBtn.innerHTML = originalText;
                    submitBtn.disabled = false;
                    
                    alert('An error occurred while submitting your application. Please try again.\nError: ' + error.message);
                });
                
            } catch (error) {
                alert('JavaScript error: ' + error.message);
            }
        }

        // No resume type switching needed anymore

        // File input validation (only for images now)
        function validateFile(input, type) {
            var file = input.files[0];
            if (!file) return;

            var maxSize = 5 * 1024 * 1024; // 5MB
            var allowedTypes;
            
            if (type === 'image') {
                allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
            }

            if (!allowedTypes.includes(file.type)) {
                alert('Please select a valid image format (JPG, PNG, GIF)');
                input.value = '';
                return;
            }

            if (file.size > maxSize) {
                alert('File size must be less than 5MB');
                input.value = '';
                return;
            }
        }

        // OCR Processing Function
        function processOCR() {
            var fileInput = document.getElementById('cccdFrontUpload');
            var file = fileInput.files[0];
            
            if (!file) {
                alert('Please select a CCCD image first');
                return;
            }
            
            // Show loading state
            var btn = document.getElementById('processOCRBtn');
            var originalText = btn.innerHTML;
            btn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Processing...';
            btn.disabled = true;
            
            document.getElementById('ocrStatus').style.display = 'block';
            
            // Create FormData
            var formData = new FormData();
            formData.append('file', file);
            
            // Call OCR API
            fetch('${pageContext.request.contextPath}/api/ocr', {
                method: 'POST',
                body: formData
            })
            .then(function(response) {
                return response.json();
            })
            .then(function(data) {
                // Reset button
                btn.innerHTML = originalText;
                btn.disabled = false;
                document.getElementById('ocrStatus').style.display = 'none';
                
                if (data.error) {
                    alert('OCR Error: ' + data.message);
                    return;
                }
                
                // Fill form fields with OCR results
                if (data.SO_CAN_CUOC_CONG_DAN) {
                    document.getElementById('cccd').value = data.SO_CAN_CUOC_CONG_DAN;
                }
                if (data.TEN) {
                    document.getElementById('cccdName').value = data.TEN;
                }
                if (data.NGAY_SINH) {
                    document.getElementById('cccdDob').value = data.NGAY_SINH;
                }
                if (data.GIOI_TINH) {
                    document.getElementById('cccdGender').value = data.GIOI_TINH;
                }
                if (data.QUE_QUAN) {
                    document.getElementById('cccdHometown').value = data.QUE_QUAN;
                }
                // Note: NGAY_HET_HAN is only used for validation in OCRServlet, not stored
                
                // Show success message
                var successAlert = document.createElement('div');
                successAlert.className = 'alert alert-success mt-2';
                successAlert.innerHTML = '<i class="fas fa-check-circle me-2"></i>CCCD information extracted successfully! Please review and edit if needed.';
                document.getElementById('ocrStatus').innerHTML = '';
                document.getElementById('ocrStatus').appendChild(successAlert);
                document.getElementById('ocrStatus').style.display = 'block';
                
                // Hide success message after 5 seconds
                setTimeout(function() {
                    document.getElementById('ocrStatus').style.display = 'none';
                }, 5000);
            })
            .catch(function(error) {
                // Reset button
                btn.innerHTML = originalText;
                btn.disabled = false;
                document.getElementById('ocrStatus').style.display = 'none';
                
                console.error('OCR Error:', error);
                alert('An error occurred while processing the image. Please try again.');
            });
        }

        // Add event listeners for OCR
        document.addEventListener('DOMContentLoaded', function() {

            // CCCD Front Upload handler
            document.getElementById('cccdFrontUpload').addEventListener('change', function(e) {
                validateFile(e.target, 'image');
                // Enable OCR button when file is selected
                var processBtn = document.getElementById('processOCRBtn');
                if (e.target.files[0]) {
                    processBtn.disabled = false;
                } else {
                    processBtn.disabled = true;
                }
            });
            
            // OCR button click handler
            document.getElementById('processOCRBtn').addEventListener('click', processOCR);
        });
    </script>

    <style>
        /* Application Modal Styles */
        .form-section {
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 1px solid #e9ecef;
        }

        .form-section:last-child {
            border-bottom: none;
            margin-bottom: 0;
        }

        .section-title {
            color: #667eea;
            font-weight: 600;
            margin-bottom: 15px;
            font-size: 1rem;
        }

        .form-label {
            font-weight: 500;
            color: #2c3e50;
            margin-bottom: 5px;
        }

        .form-control, .form-select {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 10px 12px;
            transition: all 0.3s ease;
        }

        .form-control:focus, .form-select:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }

        .form-text {
            font-size: 0.8rem;
            color: #6c757d;
            margin-top: 5px;
        }

        .form-check-label {
            font-size: 0.9rem;
            color: #6c757d;
        }

        .form-check-label a {
            color: #667eea;
            text-decoration: none;
        }

        .form-check-label a:hover {
            text-decoration: underline;
        }

        /* Success Modal Styles */
        .success-icon {
            font-size: 4rem;
            color: #28a745;
        }

        .success-icon i {
            animation: successPulse 0.6s ease-out;
        }

        @keyframes successPulse {
            0% {
                transform: scale(0);
                opacity: 0;
            }
            50% {
                transform: scale(1.1);
                opacity: 1;
            }
            100% {
                transform: scale(1);
                opacity: 1;
            }
        }

        /* Modal Enhancements */
        .modal-content {
            border: none;
            border-radius: 15px;
            overflow: hidden;
        }

        .modal-header {
            border: none;
            padding: 20px 30px;
        }

        .modal-body {
            padding: 30px;
            max-height: 70vh;
            overflow-y: auto;
        }

        .modal-footer {
            border: none;
            padding: 20px 30px;
            background-color: #f8f9fa;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .modal-body {
                padding: 20px;
            }
            
            .modal-header, .modal-footer {
                padding: 15px 20px;
            }
        }
    </style>

    <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>
</html>
