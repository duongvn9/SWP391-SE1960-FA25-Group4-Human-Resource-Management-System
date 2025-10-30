<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
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

    <section class="features-section" id="jobs">
        <div class="container">
            <div class="section-title" data-aos="fade-up">
                <h2>Job Openings</h2>
                <p>Browse all published opportunities</p>
            </div>

            <c:choose>
                <c:when test="${not empty jobs}">
                    <div class="row">
                        <c:forEach items="${jobs}" var="job">
                            <div class="col-lg-4 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="100">
                                <div class="feature-card" style="height:100%">
                                    <img src="${pageContext.request.contextPath}/assets/img/hero-illustration.svg" alt="" style="width:100%; height:160px; object-fit:cover; border-radius:8px; margin-bottom:12px; opacity:0.9;"/>
                                    <h4>${job.title}</h4>
                                    <p style="margin-bottom:0.5rem;"><strong>Position:</strong> ${job.title}</p>
                                    <p style="margin-bottom:0.5rem;">
                                        <strong>Department:</strong>
                                        <c:set var="deptFound" value="false"/>
                                        <c:forEach items="${departments}" var="dept">
                                            <c:if test="${dept.id == job.departmentId}">
                                                ${dept.name}
                                                <c:set var="deptFound" value="true"/>
                                            </c:if>
                                        </c:forEach>
                                        <c:if test="${!deptFound}">N/A</c:if>
                                    </p>
                                    <p style="margin-bottom:1rem;"><strong>Location:</strong> ${empty job.workingLocation ? 'N/A' : job.workingLocation}</p>
                                    <a href="${pageContext.request.contextPath}/job-view-publish?id=${job.id}" class="btn btn-hero-outline">See Job Details</a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Jobs pagination" class="mt-4">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${currentPage - 1}">«</a>
                                </li>
                                <c:set var="startPage" value="${currentPage - 2}" />
                                <c:set var="endPage" value="${currentPage + 2}" />
                                <c:if test="${startPage < 1}"><c:set var="startPage" value="1" /></c:if>
                                <c:if test="${endPage > totalPages}"><c:set var="endPage" value="${totalPages}" /></c:if>

                                <c:if test="${startPage > 1}">
                                    <li class="page-item"><a class="page-link" href="?page=1">1</a></li>
                                    <c:if test="${startPage > 2}"><li class="page-item disabled"><span class="page-link">...</span></li></c:if>
                                </c:if>

                                <c:forEach begin="${startPage}" end="${endPage}" var="p">
                                    <li class="page-item ${currentPage == p ? 'active' : ''}">
                                        <a class="page-link" href="?page=${p}">${p}</a>
                                    </li>
                                </c:forEach>

                                <c:if test="${endPage < totalPages}">
                                    <c:if test="${endPage < totalPages - 1}"><li class="page-item disabled"><span class="page-link">...</span></li></c:if>
                                    <li class="page-item"><a class="page-link" href="?page=${totalPages}">${totalPages}</a></li>
                                </c:if>

                                <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${currentPage + 1}">»</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <div class="text-center text-muted" data-aos="fade-up">No published jobs at the moment.</div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>

    <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>
</html>

