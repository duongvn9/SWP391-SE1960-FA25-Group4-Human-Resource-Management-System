<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="Create Job Posting - HRMS" />
        <jsp:param name="pageCss" value="job-posting.css" />
    </jsp:include>
</head>
<body>
    <!-- Sidebar -->
    <jsp:include page="../layout/sidebar.jsp">
        <jsp:param name="currentPage" value="job-postings" />
    </jsp:include>

    <!-- Main Content -->
    <div class="main-content" id="main-content">
        <!-- Header -->
        <jsp:include page="../layout/dashboard-header.jsp" />

        <!-- Content Area -->
        <div class="content-area">
        

            <!-- Page Title -->
            <div class="page-head d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="page-title"><i class="fas fa-plus-circle me-2"></i>Create Job Posting</h2>
                    <p class="page-subtitle">Create a new job posting from recruitment request</p>
                </div>
                <a href="${pageContext.request.contextPath}/job-postings" class="btn btn-outline-secondary">
                    <i class="fas fa-list me-1"></i> View All Job Postings 
                </a>
            </div>
                
            <!-- Main Form Card -->
            <div class="card job-posting-card">
                <div class="card-header">
                    <h4><i class="fas fa-edit me-2"></i>Job Posting Form</h4>
                </div>

                <div class="card-body">
                    <!-- Alerts -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            <c:out value="${error}" />
                        </div>
                    </c:if>
                    <c:if test="${not empty success}">
                        <div class="alert alert-success" role="alert">
                            <i class="fas fa-check-circle me-2"></i>
                            <c:out value="${success}" />
                        </div>
                    </c:if>

                    <form method="post" action="${pageContext.request.contextPath}/job-postings/create" 
                          class="needs-validation" novalidate>
                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                        <c:if test="${not empty sourceRequestId}">
                            <input type="hidden" name="sourceRequestId" value="${sourceRequestId}" />
                        </c:if>

                    <!-- Basic Information -->
                    <div class="card mb-4">
                        <div class="card-header">
                            Basic Information
                        </div>
                        <div class="card-body">
                            <!-- Position Code & Name (Read-only from Request) -->
                            <div class="row g-3 mb-3">
                                <div class="col-md-4">
                                    <label for="positionCode" class="form-label">
                                        <i class="fas fa-hashtag"></i> Position Code
                                    </label>
                                    <input type="text" class="form-control" id="positionCode" 
                                           value="${requestDetails.positionCode}" readonly>
                                    <input type="hidden" name="positionCode" value="${requestDetails.positionCode}">
                                    <div class="form-text">Auto-filled from recruitment request</div>
                                </div>
                                <div class="col-md-8">
                                    <label for="positionName" class="form-label">
                                        <i class="fas fa-id-badge"></i> Position Name
                                    </label>
                                    <input type="text" class="form-control" id="positionName" 
                                           value="${requestDetails.positionName}" readonly>
                                    <input type="hidden" name="positionName" value="${requestDetails.positionName}">
                                    <div class="form-text">Auto-filled from recruitment request</div>
                                </div>
                            </div>

                            <!-- Job Title & Code editable by HR -->
                            <div class="row g-3 mb-3">
                                <div class="col-md-6">
                                    <label for="jobTitle" class="form-label">
                                        <i class="fas fa-heading"></i> Job Title
                                        <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control ${not empty errors.jobTitle ? 'is-invalid' : ''}" 
                                           id="jobTitle" name="jobTitle" required
                                           value="${param.jobTitle != null ? param.jobTitle : requestDetails.positionName}">
                                    <div class="form-text">Title shown in job listing (can be customized from position name)</div>
                                    <div class="invalid-feedback">${errors.jobTitle}</div>
                                </div>
                                <div class="col-md-6">
                                    <label for="code" class="form-label">
                                        <i class="fas fa-barcode"></i> Job Code
                                    </label>
                                    <input type="text" class="form-control ${not empty errors.code ? 'is-invalid' : ''}" 
                                           id="code" name="code" 
                                           value="${param.code != null ? param.code : requestDetails.positionCode}">
                                    <div class="form-text">Public job code (defaults to position code if empty)</div>
                                    <div class="invalid-feedback">${errors.code}</div>
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
                            <!-- Min Experience & Start Date -->
                            <div class="row g-3 mb-3">
                                <div class="col-md-4">
                                    <label for="minExperienceYears" class="form-label">
                                        <i class="fas fa-briefcase"></i> Min Experience Years
                                    </label>
                                    <input type="number" class="form-control ${not empty errors.minExperienceYears ? 'is-invalid' : ''}" 
                                           id="minExperienceYears" name="minExperienceYears" 
                                           min="0" max="20"
                                           value="${param.minExperienceYears}">
                                    <div class="form-text">Required years of experience (0-20)</div>
                                    <div class="invalid-feedback">${errors.minExperienceYears}</div>
                                </div>
                                <div class="col-md-4">
                                    <label for="startDate" class="form-label">
                                        <i class="fas fa-calendar-alt"></i> Expected Start Date
                                    </label>
                                    <input type="date" class="form-control ${not empty errors.startDate ? 'is-invalid' : ''}" 
                                           id="startDate" name="startDate" 
                                           min="${LocalDate.now()}"
                                           value="${param.startDate}">
                                    <div class="form-text">When the position starts</div>
                                    <div class="invalid-feedback">${errors.startDate}</div>
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
                                    <input type="text" class="form-control" id="salaryType" name="salaryType"
                                           value="${param.salaryType != null ? param.salaryType : requestDetails.salaryType}">
                                </div>
                                <div class="col-md-4">
                                    <label for="minSalary" class="form-label">Minimum Salary (VND)</label>
                                    <input type="number" step="0.01" class="form-control" id="minSalary" name="minSalary"
                                           value="${param.minSalary != null ? param.minSalary : requestDetails.minSalary}">
                                </div>
                                <div class="col-md-4">
                                    <label for="maxSalary" class="form-label">Maximum Salary (VND)</label>
                                    <input type="number" step="0.01" class="form-control" id="maxSalary" name="maxSalary"
                                           value="${param.maxSalary != null ? param.maxSalary : requestDetails.maxSalary}">
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

                    <!-- Actions -->
                    <div class="mt-4 d-grid gap-2 d-md-flex justify-content-md-end">
                        <a href="${pageContext.request.contextPath}/job-postings" 
                           class="btn btn-secondary">
                            <i class="fas fa-times me-1"></i> Cancel
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-paper-plane me-1"></i> Submit for Approval
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <jsp:include page="../layout/dashboard-footer.jsp" />
</div>

<script>
    // Form validation
    (function() {
        'use strict';
        
        document.addEventListener('DOMContentLoaded', function() {
            // Character counter for textareas
            document.querySelectorAll('textarea').forEach(textarea => {
                const maxLength = textarea.maxLength;
                if(maxLength && maxLength > 0) {
                    const counter = document.createElement('div');
                    counter.className = 'char-counter';
                    textarea.parentNode.appendChild(counter);
                    
                    function updateCounter() {
                        const remaining = maxLength - textarea.value.length;
                        counter.textContent = textarea.value.length + '/' + maxLength + ' characters';
                    }
                    
                    textarea.addEventListener('input', updateCounter);
                    updateCounter(); // Initial count
                }
            });

            // Form validation
            const form = document.querySelector('.needs-validation');
            form.addEventListener('submit', function(event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    })();
</script>

<style>
/* Custom styles for Job Posting form */
.job-posting-card {
    box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

.job-posting-card .card-header {
    background-color: #f8f9fa;
    border-bottom: 1px solid #e9ecef;
}

.job-posting-card .card-header h4 {
    color: #2c3e50;
    margin-bottom: 0;
}

.form-label {
    font-weight: 500;
    color: #495057;
}

.form-label i {
    width: 20px;
    color: #6c757d;
}

.form-text {
    font-size: 0.825rem;
    color: #6c757d;
}

.char-counter {
    font-size: 0.75rem;
    color: #6c757d;
    text-align: right;
    margin-top: 0.25rem;
}

/* Responsive adjustments */
@media (max-width: 768px) {
    .content-area {
        padding: 1rem;
    }
    
    .job-posting-card {
        margin-bottom: 1rem;
    }
    
    .btn {
        width: 100%;
        margin-bottom: 0.5rem;
    }
    
    .d-md-flex.justify-content-md-end {
        flex-direction: column-reverse;
    }
}

/* Form validation styles */
.was-validated .form-control:invalid:focus,
.form-control.is-invalid:focus {
    border-color: #dc3545;
    box-shadow: 0 0 0 0.25rem rgba(220, 53, 69, 0.25);
}

.was-validated .form-control:valid:focus,
.form-control.is-valid:focus {
    border-color: #198754;
    box-shadow: 0 0 0 0.25rem rgba(25, 135, 84, 0.25);
}
</style>
</body>
</html>