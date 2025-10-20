<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>



<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../layout/head.jsp"> 
        <jsp:param name="pageTitle" value="Recruitment Request" />
    </jsp:include>

    <!-- External CSS -->
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
            <!-- Alert Messages -->
            <div id="alertSuccess" class="alert alert-success alert-dismissible fade show mb-3" role="alert" style="display:none;">
                <i class="bi bi-check-circle me-2"></i>
                <span id="successMessage"></span>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <div id="alertError" class="alert alert-danger alert-dismissible fade show mb-3" role="alert" style="display:none;">
                <i class="bi bi-exclamation-circle me-2"></i>
                <span id="errorMessage"></span>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>

            <div class="row g-4">
                <!-- FORM SECTION -->
                <div class="col-lg-8">
                    <div class="card p-4">
                        <h5 class="card-title mb-4">
                            <i class="bi bi-person-plus-fill"></i>
                            Create Recruitment Request
                        </h5>

                        <form id="recruitmentRequestForm"
                              action="${pageContext.request.contextPath}/requests/recruitment/submit"
                              method="post" enctype="multipart/form-data">

                            <input type="hidden" name="createdByAccountId" value="${sessionScope.userAccountId}" />
                            <input type="hidden" name="createdByUserId" value="${sessionScope.userId}" />

                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label class="form-label"><i class="bi bi-briefcase"></i>Job Title <span class="text-danger">*</span></label>
                                    <input type="text" name="jobTitle" id="jobTitle" class="form-control" required />
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label"><i class="bi bi-diagram-3"></i>Position Name <span class="text-danger">*</span></label>
                                    <input type="text" name="positionName" id="positionName" class="form-control" placeholder="e.g. Software Engineer" required />
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">
                                    <i class="bi bi-journal-text"></i> Job Summary <span class="text-danger">*</span>
                                </label>
                                <textarea name="jobSummary" id="jobSummary" class="form-control" rows="3"
                                        placeholder="Briefly describe what this job entails..." required></textarea>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-4">
                                    <label class="form-label"><i class="bi bi-people"></i>Quantity <span class="text-danger">*</span></label>
                                    <input type="number" name="quantity" id="quantity" class="form-control" min="1" required />
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label"><i class="bi bi-layers"></i>Job Level <span class="text-danger">*</span></label>
                                    <select name="jobLevel" id="jobLevel" class="form-select" required>
                                        <option value="">Select Level</option>
                                        <option>Junior</option>
                                        <option>Middle</option>
                                        <option>Senior</option>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label"><i class="bi bi-clock"></i>Job Type <span class="text-danger">*</span></label>
                                    <select name="jobType" id="jobType" class="form-select" required>
                                        <option>Full-time</option>
                                        <option>Part-time</option>
                                        <option>Internship</option>
                                    </select>
                                </div>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label class="form-label"><i class="bi bi-chat-text"></i>Recruitment Reason</label>
                                    <textarea name="recruitmentReason" id="recruitmentReason" class="form-control" rows="2" placeholder="Enter recruitment reason"></textarea>
                                </div>
                                <div class="col-md-6">
                                    <div class="row">
                                        <div class="col-md-4">
                                            <label class="form-label"><i class="bi bi-cash-stack"></i>Min Salary</label>
                                            <input type="number" name="minSalary" id="minSalary" class="form-control" placeholder="5000000" />
                                        </div>
                                        <div class="col-md-4">
                                            <label class="form-label"><i class="bi bi-cash"></i>Max Salary</label>
                                            <input type="number" name="maxSalary" id="maxSalary" class="form-control" placeholder="8000000" />
                                        </div>
                                        <div class="col-md-4">
                                            <label class="form-label"><i class="bi bi-coin"></i>Salary Type</label>
                                            <select name="salaryType" id="salaryType" class="form-select">
                                                <option>Gross</option>
                                                <option>Net</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label"><i class="bi bi-geo-alt"></i>Working Location <span class="text-danger">*</span></label>
                                <input type="text" name="workingLocation" id="workingLocation" class="form-control" placeholder="e.g. Ho Chi Minh" required />
                            </div>

                            <div class="mb-3">
                                <label class="form-label"><i class="bi bi-paperclip"></i>Attachment (optional)</label>
                                <input type="file" name="attachment" class="form-control file-input" />
                            </div>

                            <div class="form-check mb-4">
                                <input class="form-check-input" type="checkbox" id="confirmCheck" required>
                                <label class="form-check-label" for="confirmCheck">
                                    I confirm that the above information is accurate.
                                </label>
                            </div>

                            <div class="d-flex justify-content-end gap-2">
                                <button type="button" class="btn btn-light" onclick="window.location='${pageContext.request.contextPath}/dashboard';">
                                    <i class="bi bi-x-lg"></i> Cancel
                                </button>
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-send-fill"></i> Submit Request
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                
               
            </div>
        </div>

        <jsp:include page="../layout/dashboard-footer.jsp" />
    </div>
</div>

</body>
</html>
