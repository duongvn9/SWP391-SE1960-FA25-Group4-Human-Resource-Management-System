<%-- 
    Document   : recruitment_request
    Created on : Oct 9, 2025, 3:15:34 AM
    Author     : ADMIN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <jsp:include page="../layout/head.jsp">
            <jsp:param name="pageTitle" value="Recruitment Request" />
        </jsp:include>
        <link href="${pageContext.request.contextPath}/assets/css/common.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/css/recruitment-request.css" rel="stylesheet">
    </head>
    <body>
        <div class="dashboard-wrapper">
            <jsp:include page="../layout/sidebar.jsp">
                <jsp:param name="currentPage" value="recruitment-create" />
            </jsp:include>

            <div class="main-content">

                <jsp:include page="../layout/dashboard-header.jsp">
                    <jsp:param name="pageTitle" value="Recruitment Request" />
                </jsp:include>


                <div class="container-fluid px-4 py-3">
                    <div class="card shadow-sm border-0 rounded-3">
                        <div class="card-body">
                            <h5 class="card-title mb-4">Create Recruitment Request</h5>

                            <form id="recruitmentRequestForm"
                                  action="${pageContext.request.contextPath}/requests/create"
                                  method="post" enctype="multipart/form-data">
                                <input type="hidden" name="creatorId" value="${sessionScope.user.id}" />
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <label class="form-label">Job Title <span class="text-danger">*</span></label>
                                        <input type="text" name="jobTitle" class="form-control" required />
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Department</label>
                                        <input type="text" name="department" class="form-control" />
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Number of Positions</label>
                                    <input type="number" name="numPositions" class="form-control" min="1" />
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Description</label>
                                    <textarea name="description" class="form-control" rows="4"></textarea>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Attachment (optional)</label>
                                    <input type="file" name="attachment" class="form-control" />
                                </div>

                                <div class="d-flex gap-2">
                                    <button type="button" id="saveDraftBtn" class="btn btn-secondary">Save as Draft</button>
                                    <button type="submit" class="btn btn-primary">Submit Request</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                <jsp:include page="../layout/dashboard-footer.jsp" />
            </div>
        </div> 


        <script src="${pageContext.request.contextPath}/assets/js/recruitment_request.js"></script>
    </body>
</html>
