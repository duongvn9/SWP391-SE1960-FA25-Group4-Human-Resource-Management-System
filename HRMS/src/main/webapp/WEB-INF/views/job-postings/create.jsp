<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Job Posting - HRMS</title>
    <jsp:include page="/WEB-INF/views/layout/links.jsp"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/job-posting.css"/>
</head>
<body>
    <jsp:include page="/WEB-INF/views/layout/navbar.jsp"/>
    
    <div class="container my-4">
        <div class="row">
            <div class="col">
                <h2 class="h4 mb-3">Create Job Posting</h2>
                
                <!-- Show error/success messages if any -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="alert alert-success">${success}</div>
                </c:if>
                
                <form method="post" action="${pageContext.request.contextPath}/job-posting/create" class="needs-validation" novalidate>
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>

                    <!-- Basic Information -->
                    <div class="card mb-4">
                        <div class="card-header">
                            Basic Information
                        </div>
                        <div class="card-body">
                            <!-- Position Code & Name (Read-only from Request) -->
                            <div class="row g-3 mb-3">
                                <div class="col-md-4">
                                    <label for="positionCode" class="form-label">Position Code</label>
                                    <input type="text" class="form-control" id="positionCode" 
                                           value="${requestDetails.positionCode}" readonly>
                                    <input type="hidden" name="positionCode" value="${requestDetails.positionCode}">
                                </div>
                                <div class="col-md-8">
                                    <label for="positionName" class="form-label">Position Name</label>
                                    <input type="text" class="form-control" id="positionName" 
                                           value="${requestDetails.positionName}" readonly>
                                    <input type="hidden" name="positionName" value="${requestDetails.positionName}">
                                </div>
                            </div>

                            <!-- Job Level & Type (Read-only from Request) -->
                            <div class="row g-3 mb-3">
                                <div class="col-md-4">
                                    <label for="jobLevel" class="form-label">Job Level</label>
                                    <input type="text" class="form-control" id="jobLevel"
                                           value="${requestDetails.jobLevel}" readonly>
                                    <input type="hidden" name="jobLevel" value="${requestDetails.jobLevel}">
                                </div>
                                <div class="col-md-4">
                                    <label for="jobType" class="form-label">Job Type</label>
                                    <input type="text" class="form-control" id="jobType"
                                           value="${requestDetails.jobType}" readonly>
                                    <input type="hidden" name="jobType" value="${requestDetails.jobType}">
                                </div>
                                <div class="col-md-4">
                                    <label for="numberOfPositions" class="form-label">Number of Positions</label>
                                    <input type="number" class="form-control" id="numberOfPositions"
                                           value="${requestDetails.quantity}" readonly>
                                    <input type="hidden" name="numberOfPositions" value="${requestDetails.quantity}">
                                </div>
                            </div>

                            <!-- Priority -->
                            <div class="row g-3 mb-3">
                                <div class="col-md-4">
                                    <label for="priority" class="form-label">Priority Level</label>
                                    <select class="form-select ${not empty errors.priority ? 'is-invalid' : ''}"
                                            id="priority" name="priority">
                                        <option value="NORMAL" ${param.priority == 'NORMAL' ? 'selected' : ''}>Normal</option>
                                        <option value="HIGH" ${param.priority == 'HIGH' ? 'selected' : ''}>High</option>
                                        <option value="URGENT" ${param.priority == 'URGENT' ? 'selected' : ''}>Urgent</option>
                                    </select>
                                    <div class="invalid-feedback">${errors.priority}</div>
                                </div>
                                <div class="col-md-8">
                                    <label for="workingHours" class="form-label">Working Hours</label>
                                    <input type="text" class="form-control ${not empty errors.workingHours ? 'is-invalid' : ''}"
                                           id="workingHours" name="workingHours" value="${param.workingHours}"
                                           placeholder="e.g. Monday-Friday 8:00-17:00">
                                    <div class="invalid-feedback">${errors.workingHours}</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Compensation Information -->
                    <div class="card mb-4">
                        <div class="card-header">
                            Compensation Details
                        </div>
                        <div class="card-body">
                            <!-- Salary Range (Read-only from Request) -->
                            <div class="row g-3 mb-3">
                                <div class="col-md-4">
                                    <label for="salaryType" class="form-label">Salary Type</label>
                                    <input type="text" class="form-control" id="salaryType"
                                           value="${requestDetails.salaryType}" readonly>
                                    <input type="hidden" name="salaryType" value="${requestDetails.salaryType}">
                                </div>
                                <div class="col-md-4">
                                    <label for="minSalary" class="form-label">Minimum Salary (VND)</label>
                                    <input type="number" class="form-control" id="minSalary"
                                           value="${requestDetails.minSalary}" readonly>
                                    <input type="hidden" name="minSalary" value="${requestDetails.minSalary}">
                                </div>
                                <div class="col-md-4">
                                    <label for="maxSalary" class="form-label">Maximum Salary (VND)</label>
                                    <input type="number" class="form-control" id="maxSalary"
                                           value="${requestDetails.maxSalary}" readonly>
                                    <input type="hidden" name="maxSalary" value="${requestDetails.maxSalary}">
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Job Details -->
                    <div class="card mb-4">
                        <div class="card-header">
                            Job Details
                        </div>
                        <div class="card-body">
                            <!-- Description & Requirements -->
                            <div class="row g-3 mb-3">
                                <div class="col-12">
                                    <label for="description" class="form-label required">Job Description</label>
                                    <textarea class="form-control ${not empty errors.description ? 'is-invalid' : ''}"
                                              id="description" name="description" rows="5" required>${param.description}</textarea>
                                    <div class="invalid-feedback">${errors.description}</div>
                                </div>
                                <div class="col-12">
                                    <label for="requirements" class="form-label required">Requirements</label>
                                    <textarea class="form-control ${not empty errors.requirements ? 'is-invalid' : ''}"
                                              id="requirements" name="requirements" rows="5" required>${param.requirements}</textarea>
                                    <div class="invalid-feedback">${errors.requirements}</div>
                                </div>
                                <div class="col-12">
                                    <label for="benefits" class="form-label">Benefits</label>
                                    <textarea class="form-control ${not empty errors.benefits ? 'is-invalid' : ''}"
                                              id="benefits" name="benefits" rows="3">${param.benefits}</textarea>
                                    <div class="invalid-feedback">${errors.benefits}</div>
                                </div>
                            </div>

                            <!-- Location & Deadline -->
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label for="location" class="form-label required">Working Location</label>
                                    <input type="text" class="form-control ${not empty errors.location ? 'is-invalid' : ''}"
                                           id="location" name="location" value="${param.location}" required>
                                    <div class="invalid-feedback">${errors.location}</div>
                                </div>
                                <div class="col-md-6">
                                    <label for="applicationDeadline" class="form-label required">Application Deadline</label>
                                    <input type="date" class="form-control ${not empty errors.applicationDeadline ? 'is-invalid' : ''}"
                                           id="applicationDeadline" name="applicationDeadline" 
                                           value="${param.applicationDeadline}"
                                           min="${LocalDate.now()}" required>
                                    <div class="invalid-feedback">${errors.applicationDeadline}</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Contact Information -->
                    <div class="card mb-4">
                        <div class="card-header">
                            Contact Information
                        </div>
                        <div class="card-body">
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label for="contactEmail" class="form-label required">Contact Email</label>
                                    <input type="email" class="form-control ${not empty errors.contactEmail ? 'is-invalid' : ''}"
                                           id="contactEmail" name="contactEmail" value="${param.contactEmail}" required>
                                    <div class="invalid-feedback">${errors.contactEmail}</div>
                                </div>
                                <div class="col-md-6">
                                    <label for="contactPhone" class="form-label">Contact Phone</label>
                                    <input type="tel" class="form-control ${not empty errors.contactPhone ? 'is-invalid' : ''}"
                                           id="contactPhone" name="contactPhone" value="${param.contactPhone}">
                                    <div class="invalid-feedback">${errors.contactPhone}</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Submit Buttons -->
                    <div class="text-center">
                        <button type="submit" class="btn btn-primary btn-lg">Submit for Approval</button>
                        <a href="${pageContext.request.contextPath}/job-postings" class="btn btn-outline-secondary btn-lg ms-2">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
    <script src="${pageContext.request.contextPath}/assets/js/job-posting.js"></script>
</body>
</html>