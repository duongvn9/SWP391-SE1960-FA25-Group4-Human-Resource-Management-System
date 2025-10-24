<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="Edit Job Posting - HRMS" />
        <jsp:param name="pageCss" value="job-posting.css" />
    </jsp:include>
</head>
<body>
    <!-- Sidebar -->
    <jsp:include page="../layout/sidebar.jsp">
        <jsp:param name="currentPage" value="job-postings" />
    </jsp:include>

    <div class="main-content" id="main-content">
        <jsp:include page="../layout/dashboard-header.jsp" />

        <div class="content-area">
            <div class="page-head d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="page-title"><i class="fas fa-edit me-2"></i>Edit Job Posting</h2>
                    <p class="page-subtitle">Update an existing job posting</p>
                </div>
                <a href="${pageContext.request.contextPath}/job-postings" class="btn btn-outline-secondary">
                    <i class="fas fa-list me-1"></i> View All Job Postings
                </a>
            </div>

            <div class="card job-posting-card">
                <div class="card-header">
                    <h4><i class="fas fa-pencil-alt me-2"></i>Edit Job Posting</h4>
                </div>
                <div class="card-body">
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            <c:out value="${error}" />
                        </div>
                    </c:if>

                    <form method="post" action="${pageContext.request.contextPath}/job-posting/edit" class="needs-validation" novalidate>
                        <input type="hidden" name="csrfToken" value="${csrfToken}" />
                        <input type="hidden" name="id" value="${jobPosting.id}" />

                        <!-- Basic Information -->
                        <div class="card mb-4">
                            <div class="card-header">Basic Information</div>
                            <div class="card-body">
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label for="jobTitle" class="form-label">Job Title <span class="text-danger">*</span></label>
                                        <input type="text" id="jobTitle" name="jobTitle" class="form-control ${not empty errors.jobTitle ? 'is-invalid' : ''}"
                                               value="${not empty formData.jobTitle ? formData.jobTitle : jobPosting.title}" required />
                                        <div class="invalid-feedback">${not empty errors.jobTitle ? errors.jobTitle : 'Job title is required'}</div>
                                    </div>

                                    <div class="col-md-3">
                                        <label for="code" class="form-label">Code</label>
                                        <input type="text" id="code" name="code" class="form-control"
                                               value="${not empty formData.code ? formData.code : jobPosting.code}" />
                                    </div>

                                    <div class="col-md-3">
                                        <label for="numberOfPositions" class="form-label">Number of Positions</label>
                                        <input type="number" id="numberOfPositions" name="numberOfPositions" min="1" class="form-control"
                                               value="${not empty formData.numberOfPositions ? formData.numberOfPositions : jobPosting.numberOfPositions}" />
                                    </div>
                                </div>

                                <div class="row g-3 mt-3">
                                    <div class="col-md-6">
                                        <label for="departmentId" class="form-label">Department</label>
                                        <select id="departmentId" name="departmentId" class="form-select">
                                            <option value="">-- Select Department --</option>
                                            <c:forEach items="${departments}" var="dept">
                                                <c:set var="selectedDept" value="${not empty formData.departmentId ? formData.departmentId : jobPosting.departmentId}" />
                                                <option value="${dept.id}" <c:if test="${dept.id == selectedDept}">selected</c:if>>${dept.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <div class="col-md-6">
                                        <label for="positionId" class="form-label">Position</label>
                                        <select id="positionId" name="positionId" class="form-select">
                                            <option value="">-- Select Position --</option>
                                            <c:forEach items="${positions}" var="pos">
                                                <c:set var="selectedPos" value="${not empty formData.positionId ? formData.positionId : jobPosting.positionId}" />
                                                <option value="${pos.id}" <c:if test="${pos.id == selectedPos}">selected</c:if>>${pos.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Salary & Dates -->
                        <div class="card mb-4">
                            <div class="card-header">Salary & Dates</div>
                            <div class="card-body">
                                <div class="row g-3">
                                    <div class="col-md-3">
                                        <label for="salaryType" class="form-label">Salary Type</label>
                                        <select id="salaryType" name="salaryType" class="form-select">
                                            <option value="">-- Select --</option>
                                            <option value="GROSS" ${ (not empty formData.salaryType ? formData.salaryType : jobPosting.salaryType) == 'GROSS' ? 'selected' : '' }>GROSS</option>
                                            <option value="NET" ${ (not empty formData.salaryType ? formData.salaryType : jobPosting.salaryType) == 'NET' ? 'selected' : '' }>NET</option>
                                        </select>
                                    </div>
                                    <div class="col-md-3">
                                        <label for="minSalary" class="form-label">Min Salary</label>
                                        <input type="text" id="minSalary" name="minSalary" class="form-control"
                                               value="${not empty formData.minSalary ? formData.minSalary : jobPosting.minSalary}" />
                                    </div>
                                    <div class="col-md-3">
                                        <label for="maxSalary" class="form-label">Max Salary</label>
                                        <input type="text" id="maxSalary" name="maxSalary" class="form-control"
                                               value="${not empty formData.maxSalary ? formData.maxSalary : jobPosting.maxSalary}" />
                                    </div>
                                    <div class="col-md-3">
                                        <label for="applicationDeadline" class="form-label">Application Deadline</label>
                                        <input type="date" id="applicationDeadline" name="applicationDeadline" class="form-control"
                                               value="${not empty formData.applicationDeadline ? formData.applicationDeadline : jobPosting.applicationDeadline}" />
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Job Details -->
                        <div class="card mb-4">
                            <div class="card-header">Job Details</div>
                            <div class="card-body">
                                <div class="mb-3">
                                    <label for="description" class="form-label">Job Description <span class="text-danger">*</span></label>
                                    <textarea id="description" name="description" rows="5" class="form-control ${not empty errors.description ? 'is-invalid' : ''}" required>${not empty formData.description ? formData.description : jobPosting.description}</textarea>
                                    <div class="invalid-feedback">${not empty errors.description ? errors.description : 'Description is required'}</div>
                                </div>

                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label for="requirements" class="form-label">Requirements</label>
                                        <textarea id="requirements" name="requirements" rows="3" class="form-control">${not empty formData.requirements ? formData.requirements : jobPosting.requirements}</textarea>
                                    </div>
                                    <div class="col-md-6">
                                        <label for="benefits" class="form-label">Benefits</label>
                                        <textarea id="benefits" name="benefits" rows="3" class="form-control">${not empty formData.benefits ? formData.benefits : jobPosting.benefits}</textarea>
                                    </div>
                                </div>

                                <div class="row g-3 mt-3">
                                    <div class="col-md-6">
                                        <label for="contactEmail" class="form-label">Contact Email</label>
                                        <input type="email" id="contactEmail" name="contactEmail" class="form-control" value="${not empty formData.contactEmail ? formData.contactEmail : jobPosting.contactEmail}" />
                                    </div>
                                    <div class="col-md-6">
                                        <label for="contactPhone" class="form-label">Contact Phone</label>
                                        <input type="text" id="contactPhone" name="contactPhone" class="form-control" value="${not empty formData.contactPhone ? formData.contactPhone : jobPosting.contactPhone}" />
                                    </div>
                                </div>

                                <div class="mt-4 d-grid gap-2 d-md-flex justify-content-md-end">
                                    <a href="${pageContext.request.contextPath}/job-postings" class="btn btn-secondary">
                                        <i class="fas fa-times me-1"></i> Cancel
                                    </a>
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-save me-1"></i> Save Changes
                                    </button>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

        </div>

        <jsp:include page="../layout/dashboard-footer.jsp" />
    </div>

<script>
    // Basic client-side validation - mirrors create.jsp behavior
    (function() {
        'use strict';
        document.addEventListener('DOMContentLoaded', function() {
            const form = document.querySelector('.needs-validation');
            form.addEventListener('submit', function(event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                    const firstInvalid = form.querySelector(':invalid');
                    if (firstInvalid) {
                        firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
                        firstInvalid.focus();
                    }
                }
                form.classList.add('was-validated');
            }, false);
        });
    })();
</script>

<style>
.job-posting-card { box-shadow: 0 0.125rem 0.25rem rgba(0,0,0,0.075); }
.job-posting-card .card-header { background-color: #f8f9fa; border-bottom: 1px solid #e9ecef; }
</style>
</body>
</html>