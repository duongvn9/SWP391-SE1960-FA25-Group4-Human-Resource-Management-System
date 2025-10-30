<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
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

    <section class="container py-5">
        <div class="row">
            <div class="col-lg-9 mb-4">
                <h1 class="mb-4" style="font-size:2.2rem; font-weight:700; color:#2c5aa0;">${job.title}</h1>
                <div style="margin-bottom:1.5rem;">
                    <span class="badge bg-secondary me-2"><strong>Department:</strong> <c:out value="${department != null ? department.name : 'N/A'}" /></span>
                    <span class="badge bg-info text-dark me-2"><strong>Location:</strong> <c:out value="${empty job.workingLocation ? 'N/A' : job.workingLocation}" /></span>
                    <span class="badge bg-primary"><strong>Level:</strong> <c:out value="${job.level}" /></span>
                </div>
                <hr/>
                <h4 style="font-weight:600; color:#333;">About Company</h4>
                <div class="mb-4">
                    <p><b>FPT Smart Cloud (FCI)</b> – a member of FPT Corporation, pioneers AI &amp; Cloud solutions in Vietnam. FCI was founded with the mission to generate an immense leap in productivity and agility in business operations.</p>
                </div>
    
                <h4 style="font-weight:600; color:#333;" class="mt-4">Job Description</h4>
                <div class="mb-3 content-block"><c:out value="${job.description}" escapeXml="false" /></div>
    
                <h4 style="font-weight:600; color:#333;" class="mt-4">Key Responsibilities</h4>
                <div class="mb-3 content-block"><c:out value="${job.requirements}" escapeXml="false" /></div>
    
                <h4 style="font-weight:600; color:#333;" class="mt-4">Top Benefits</h4>
                <div class="mb-3 content-block"><c:out value="${job.benefits}" escapeXml="false" /></div>

                <h4 class="mt-4" style="font-weight:600; color:#333;">Working Environment</h4>
                <ul class="ps-3 mb-2">
                    <li><strong>Location:</strong> <c:out value="${empty job.workingLocation ? 'N/A' : job.workingLocation}" /></li>
                    <li><strong>Start date:</strong> <c:choose><c:when test="${not empty job.startDate}"><fmt:formatDate value="${job.startDate}" pattern="MMMM d, yyyy"/></c:when><c:otherwise>To be determined</c:otherwise></c:choose></li>
                    <li><strong>Working hours:</strong> <c:out value="${empty job.workingHours ? 'N/A' : job.workingHours}" /></li>
                    <li><strong>Total positions:</strong> <c:out value="${job.numberOfPositions}" /></li>
                </ul>
    
                <h4 class="mt-4" style="font-weight:600; color:#333;">Contact Person</h4>
                <div class="content-block mb-3">
                    <div><strong>Email:</strong> <c:out value="${empty job.contactEmail ? 'N/A' : job.contactEmail}" /></div>
                    <div><strong>Phone:</strong> <c:out value="${empty job.contactPhone ? 'N/A' : job.contactPhone}" /></div>
                </div>
    
                <button class="btn btn-hero mt-2" style="width:160px;font-size:1.1rem;">Send CV</button>
            </div>
            <div class="col-lg-3">
                <div class="mb-4">
                    <img src="${pageContext.request.contextPath}/assets/img/hero-illustration.svg" style="width:100%; border-radius:12px;" alt="Job illustration"/>
                </div>
                <div class="border rounded-3 p-3" style="background:#f8f9fa;">
                    <h5 class="mb-3" style="font-weight:600; color:#2c5aa0;">Other Info</h5>
                    <div><strong>Job type:</strong> <c:out value="${job.jobType}"/></div>
                    <div><strong>Application deadline:</strong> <c:choose><c:when test="${not empty job.applicationDeadline}"><fmt:formatDate value="${job.applicationDeadline}" pattern="MMMM d, yyyy"/></c:when><c:otherwise>Until filled</c:otherwise></c:choose></div>
                    <div><strong>Salary:</strong> <c:if test="${not empty job.minSalary}"><c:out value="${job.minSalary}" /></c:if><c:if test="${empty job.minSalary}">Negotiable</c:if></div>
                </div>
            </div>
        </div>
    </section>

    <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>
</html>
