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
                            <!-- Department (Read-only from Request) -->
                            <c:if test="${not empty sourceDepartmentId}">
                            <div class="row g-3 mb-3">
                                <div class="col-md-12">
                                    <label for="departmentName" class="form-label">
                                        <i class="fas fa-building"></i> Department
                                    </label>
                                    <c:forEach items="${departments}" var="dept">
                                        <c:if test="${dept.id == sourceDepartmentId}">
                                            <input type="text" class="form-control" id="departmentName" 
                                                   value="${dept.name}" readonly>
                                            <div class="form-text">Auto-filled from recruitment request creator's department</div>
                                        </c:if>
                                    </c:forEach>
                                </div>
                            </div>
                            </c:if>
                            
                            <!-- Position Name (Read-only from Request) -->
                            <div class="row g-3 mb-3">
                                <div class="col-md-12">
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
                                           minlength="3" maxlength="255"
                                           value="${param.jobTitle != null ? param.jobTitle : requestDetails.positionName}">
                                    <div class="form-text">Title shown in job listing (3-255 characters)</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.jobTitle ? errors.jobTitle : 'Job title must be 3-255 characters'}
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <label for="code" class="form-label">
                                        <i class="fas fa-barcode"></i> Job Code
                                    </label>
                                    <input type="text" class="form-control ${not empty errors.code ? 'is-invalid' : ''}" 
                                           id="code" name="code" 
                                           maxlength="128"
                                           value="${param.code != null ? param.code : requestDetails.positionCode}">
                                    <div class="form-text">Public job code (max 128 characters)</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.code ? errors.code : 'Code cannot exceed 128 characters'}
                                    </div>
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

                            <!-- Working Hours -->
                            <div class="row g-3 mb-3">
                                <div class="col-md-12">
                                    <label for="workingHours" class="form-label">Working Hours</label>
                                    <input type="text" class="form-control ${not empty errors.workingHours ? 'is-invalid' : ''}"
                                           id="workingHours" name="workingHours" value="${param.workingHours}"
                                           maxlength="255"
                                           placeholder="e.g. Monday-Friday 8:00-17:00">
                                    <div class="form-text">Optional (max 255 characters)</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.workingHours ? errors.workingHours : 'Working hours cannot exceed 255 characters'}
                                    </div>
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
                                           min="0" max="50"
                                           value="${param.minExperienceYears}">
                                    <div class="form-text">Required years of experience (0-50)</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.minExperienceYears ? errors.minExperienceYears : 'Experience must be between 0 and 50 years'}
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <label for="startDate" class="form-label">
                                        <i class="fas fa-calendar-alt"></i> Expected Start Date
                                    </label>
                                    <input type="date" class="form-control ${not empty errors.startDate ? 'is-invalid' : ''}" 
                                           id="startDate" name="startDate" 
                                           min="<jsp:useBean id='today' class='java.util.Date'/><fmt:formatDate value='${today}' pattern='yyyy-MM-dd'/>"
                                           value="${param.startDate}">
                                    <div class="form-text">When the position starts (cannot be in the past)</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.startDate ? errors.startDate : 'Start date cannot be in the past'}
                                    </div>
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
                            <!-- Salary Range -->
                            <div class="row g-3 mb-3">
                                <div class="col-md-4">
                                    <label for="salaryType" class="form-label">
                                        Salary Type <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control ${not empty errors.salaryType ? 'is-invalid' : ''}" 
                                           id="salaryType" name="salaryType" required
                                           value="${param.salaryType != null ? param.salaryType : requestDetails.salaryType}"
                                           placeholder="e.g. RANGE, FROM, NEGOTIABLE">
                                    <div class="form-text">RANGE, FROM, NEGOTIABLE, GROSS, NET</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.salaryType ? errors.salaryType : 'Salary type is required'}
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <label for="minSalary" class="form-label">Minimum Salary (VND)</label>
                                    <input type="number" step="1000" class="form-control ${not empty errors.minSalary ? 'is-invalid' : ''}" 
                                           id="minSalary" name="minSalary"
                                           min="1000000"
                                           value="${param.minSalary != null ? param.minSalary : requestDetails.minSalary}">
                                    <div class="form-text">Min: 1,000,000 VND</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.minSalary ? errors.minSalary : 'Minimum salary must be at least 1,000,000 VND'}
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <label for="maxSalary" class="form-label">Maximum Salary (VND)</label>
                                    <input type="number" step="1000" class="form-control ${not empty errors.maxSalary ? 'is-invalid' : ''}" 
                                           id="maxSalary" name="maxSalary"
                                           min="1000000"
                                           value="${param.maxSalary != null ? param.maxSalary : requestDetails.maxSalary}">
                                    <div class="form-text">Must be greater than min salary</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.maxSalary ? errors.maxSalary : 'Maximum salary must be greater than minimum salary'}
                                    </div>
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
                                    <label for="description" class="form-label">
                                        Job Description <span class="text-danger">*</span>
                                    </label>
                                    <textarea class="form-control ${not empty errors.description ? 'is-invalid' : ''}"
                                              id="description" name="description" rows="5" required
                                              maxlength="4000"
                                              placeholder="Describe the job role, responsibilities, and expectations..."> 
                                        <c:out value="${param.description != null ? param.description : (formData != null ? formData.description : requestDetails.jobSummary)}"/>
                                    </textarea>
                                    <div class="form-text">Required (max 4000 characters)</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.description ? errors.description : 'Job description is required and cannot exceed 4000 characters'}
                                    </div>
                                </div>
                                <div class="col-12">
                                    <label for="requirements" class="form-label">
                                        Requirements <span class="text-danger">*</span>
                                    </label>
                                    <textarea class="form-control ${not empty errors.requirements ? 'is-invalid' : ''}"
                                              id="requirements" name="requirements" rows="5" required
                                              maxlength="4000"
                                              placeholder="List required skills, qualifications, and experience...">${param.requirements}</textarea>
                                    <div class="form-text">Required (max 4000 characters)</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.requirements ? errors.requirements : 'Requirements are required and cannot exceed 4000 characters'}
                                    </div>
                                </div>
                                <div class="col-12">
                                    <label for="benefits" class="form-label">Benefits</label>
                                    <textarea class="form-control ${not empty errors.benefits ? 'is-invalid' : ''}"
                                              id="benefits" name="benefits" rows="3"
                                              maxlength="2000"
                                              placeholder="List employee benefits, perks, and incentives...">${param.benefits}</textarea>
                                    <div class="form-text">Optional (max 2000 characters)</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.benefits ? errors.benefits : 'Benefits cannot exceed 2000 characters'}
                                    </div>
                                </div>
                            </div>

                            <!-- Location & Deadline -->
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label for="location" class="form-label">
                                        Working Location <span class="text-danger">*</span>
                                    </label>
                                    <c:choose>
                                        <c:when test="${not empty requestDetails.workingLocation}">
                                            <input type="text" class="form-control ${not empty errors.location ? 'is-invalid' : ''}"
                                                   id="location" name="location"
                                                   value="${param.location != null ? param.location : (formData != null ? formData.location : requestDetails.workingLocation)}"
                                                   required
                                                   maxlength="255"
                                                   readonly
                                                   placeholder="e.g. Hanoi, Ho Chi Minh City, Remote">
                                            <div class="form-text">Auto-filled from recruitment request (not editable)</div>
                                        </c:when>
                                        <c:otherwise>
                                            <input type="text" class="form-control ${not empty errors.location ? 'is-invalid' : ''}"
                                                   id="location" name="location" 
                                                   value="${param.location != null ? param.location : (formData != null ? formData.location : '')}" 
                                                   required
                                                   maxlength="255"
                                                   placeholder="e.g. Hanoi, Ho Chi Minh City, Remote">
                                            <div class="form-text">Required (max 255 characters)</div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="invalid-feedback">
                                        ${not empty errors.location ? errors.location : 'Working location is required and cannot exceed 255 characters'}
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <label for="applicationDeadline" class="form-label">
                                        Application Deadline <span class="text-danger">*</span>
                                    </label>
                                    <input type="date" class="form-control ${not empty errors.applicationDeadline ? 'is-invalid' : ''}"
                                           id="applicationDeadline" name="applicationDeadline" 
                                           value="${param.applicationDeadline}"
                                           min="<jsp:useBean id='deadline' class='java.util.Date'/><fmt:formatDate value='${deadline}' pattern='yyyy-MM-dd'/>" 
                                           required>
                                    <div class="form-text">Required (cannot be in the past)</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.applicationDeadline ? errors.applicationDeadline : 'Application deadline is required and cannot be in the past'}
                                    </div>
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
                                    <label for="contactEmail" class="form-label">
                                        Contact Email <span class="text-danger">*</span>
                                    </label>
                                    <input type="email" class="form-control ${not empty errors.contactEmail ? 'is-invalid' : ''}"
                                           id="contactEmail" name="contactEmail" 
                                           value="${param.contactEmail}" 
                                           required
                                           placeholder="hr@company.com">
                                    <div class="form-text">Required (valid email format)</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.contactEmail ? errors.contactEmail : 'Valid email is required'}
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <label for="contactPhone" class="form-label">Contact Phone</label>
                                    <input type="tel" class="form-control ${not empty errors.contactPhone ? 'is-invalid' : ''}"
                                           id="contactPhone" name="contactPhone" 
                                           value="${param.contactPhone}"
                                           pattern="^[0-9\+][0-9()\- ]{8,20}$"
                                           placeholder="+84 123 456 789">
                                    <div class="form-text">Optional (9-21 characters, numbers, +, -, ())</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.contactPhone ? errors.contactPhone : 'Invalid phone number format'}
                                    </div>
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
            const form = document.querySelector('.needs-validation');
            const minSalaryInput = document.getElementById('minSalary');
            const maxSalaryInput = document.getElementById('maxSalary');
            const salaryTypeInput = document.getElementById('salaryType');
            const startDateInput = document.getElementById('startDate');
            const applicationDeadlineInput = document.getElementById('applicationDeadline');
            const minExperienceInput = document.getElementById('minExperienceYears');
            const jobTitleInput = document.getElementById('jobTitle');
            const descriptionInput = document.getElementById('description');
            const requirementsInput = document.getElementById('requirements');
            const benefitsInput = document.getElementById('benefits');
            const locationInput = document.getElementById('location');
            const workingHoursInput = document.getElementById('workingHours');
            
            // Set today's date as minimum for date inputs
            const today = new Date().toISOString().split('T')[0];
            if (startDateInput) {
                startDateInput.setAttribute('min', today);
            }
            if (applicationDeadlineInput) {
                applicationDeadlineInput.setAttribute('min', today);
            }
            
            // Character counter for textareas and inputs with maxlength
            function setupCharCounter(element) {
                const maxLength = element.maxLength;
                if(maxLength && maxLength > 0) {
                    const counter = document.createElement('div');
                    counter.className = 'char-counter';
                    
                    function updateCounter() {
                        const length = element.value.length;
                        counter.textContent = length + '/' + maxLength + ' characters';
                        
                        // Change color based on usage
                        if (length > maxLength * 0.9) {
                            counter.style.color = '#dc3545'; // Red when near limit
                        } else if (length > maxLength * 0.75) {
                            counter.style.color = '#ffc107'; // Yellow when 75%
                        } else {
                            counter.style.color = '#6c757d'; // Gray default
                        }
                    }
                    
                    element.parentNode.appendChild(counter);
                    element.addEventListener('input', updateCounter);
                    updateCounter(); // Initial count
                }
            }
            
            // Setup counters for all textareas and relevant inputs
            document.querySelectorAll('textarea, input[maxlength]').forEach(setupCharCounter);
            
            // Salary validation
            function validateSalaries() {
                const minSalary = parseFloat(minSalaryInput.value);
                const maxSalary = parseFloat(maxSalaryInput.value);
                const salaryType = salaryTypeInput.value.toUpperCase();
                
                // Clear custom validity
                minSalaryInput.setCustomValidity('');
                maxSalaryInput.setCustomValidity('');
                
                // Check minimum salary requirement
                if (minSalary && minSalary < 1000000) {
                    minSalaryInput.setCustomValidity('Minimum salary must be at least 1,000,000 VND');
                    return false;
                }
                
                if (maxSalary && maxSalary < 1000000) {
                    maxSalaryInput.setCustomValidity('Maximum salary must be at least 1,000,000 VND');
                    return false;
                }
                
                // Check if max > min
                if (minSalary && maxSalary && minSalary >= maxSalary) {
                    maxSalaryInput.setCustomValidity('Maximum salary must be greater than minimum salary');
                    minSalaryInput.setCustomValidity('Minimum salary must be less than maximum salary');
                    return false;
                }
                
                // Check required fields based on salary type
                if (salaryType === 'RANGE') {
                    if (!minSalary || !maxSalary) {
                        if (!minSalary) minSalaryInput.setCustomValidity('Minimum salary is required for RANGE type');
                        if (!maxSalary) maxSalaryInput.setCustomValidity('Maximum salary is required for RANGE type');
                        return false;
                    }
                } else if (salaryType === 'FROM') {
                    if (!minSalary) {
                        minSalaryInput.setCustomValidity('Minimum salary is required for FROM type');
                        return false;
                    }
                }
                
                return true;
            }
            
            // Date validation
            function validateDates() {
                const startDate = startDateInput.value ? new Date(startDateInput.value) : null;
                const deadline = applicationDeadlineInput.value ? new Date(applicationDeadlineInput.value) : null;
                const todayDate = new Date();
                todayDate.setHours(0, 0, 0, 0);
                
                // Clear custom validity
                if (startDateInput) startDateInput.setCustomValidity('');
                if (applicationDeadlineInput) applicationDeadlineInput.setCustomValidity('');
                
                // Check if start date is in the past
                if (startDate && startDate < todayDate) {
                    startDateInput.setCustomValidity('Start date cannot be in the past');
                    return false;
                }
                
                // Check if deadline is in the past
                if (deadline && deadline < todayDate) {
                    applicationDeadlineInput.setCustomValidity('Application deadline cannot be in the past');
                    return false;
                }
                
                return true;
            }
            
            // Experience validation
            function validateExperience() {
                const experience = parseInt(minExperienceInput.value);
                
                minExperienceInput.setCustomValidity('');
                
                if (experience !== '' && !isNaN(experience)) {
                    if (experience < 0) {
                        minExperienceInput.setCustomValidity('Experience years cannot be negative');
                        return false;
                    }
                    if (experience > 50) {
                        minExperienceInput.setCustomValidity('Experience years cannot exceed 50 years');
                        return false;
                    }
                }
                
                return true;
            }
            
            // Job title validation
            function validateJobTitle() {
                const title = jobTitleInput.value.trim();
                
                jobTitleInput.setCustomValidity('');
                
                if (title.length > 0 && title.length < 3) {
                    jobTitleInput.setCustomValidity('Job title must be at least 3 characters');
                    return false;
                }
                
                if (title.length > 255) {
                    jobTitleInput.setCustomValidity('Job title cannot exceed 255 characters');
                    return false;
                }
                
                return true;
            }
            
            // Text field validation
            function validateTextField(input, minLength, maxLength, fieldName) {
                if (!input) return true;
                
                const value = input.value.trim();
                input.setCustomValidity('');
                
                if (input.hasAttribute('required') && value.length === 0) {
                    input.setCustomValidity(fieldName + ' is required');
                    return false;
                }
                
                if (value.length > maxLength) {
                    input.setCustomValidity(fieldName + ' cannot exceed ' + maxLength + ' characters');
                    return false;
                }
                
                return true;
            }
            
            // Add event listeners for real-time validation
            if (minSalaryInput) minSalaryInput.addEventListener('blur', validateSalaries);
            if (maxSalaryInput) maxSalaryInput.addEventListener('blur', validateSalaries);
            if (salaryTypeInput) salaryTypeInput.addEventListener('change', validateSalaries);
            
            if (startDateInput) startDateInput.addEventListener('change', validateDates);
            if (applicationDeadlineInput) applicationDeadlineInput.addEventListener('change', validateDates);
            
            if (minExperienceInput) minExperienceInput.addEventListener('blur', validateExperience);
            if (jobTitleInput) jobTitleInput.addEventListener('blur', validateJobTitle);
            
            // Validate text fields on blur
            if (descriptionInput) {
                descriptionInput.addEventListener('blur', function() {
                    validateTextField(this, 1, 4000, 'Job description');
                });
            }
            
            if (requirementsInput) {
                requirementsInput.addEventListener('blur', function() {
                    validateTextField(this, 1, 4000, 'Requirements');
                });
            }
            
            if (benefitsInput) {
                benefitsInput.addEventListener('blur', function() {
                    validateTextField(this, 0, 2000, 'Benefits');
                });
            }
            
            if (locationInput) {
                locationInput.addEventListener('blur', function() {
                    validateTextField(this, 1, 255, 'Working location');
                });
            }
            
            if (workingHoursInput) {
                workingHoursInput.addEventListener('blur', function() {
                    validateTextField(this, 0, 255, 'Working hours');
                });
            }

            // Form submission validation
            form.addEventListener('submit', function(event) {
                // Run all validations
                const isSalaryValid = validateSalaries();
                const isDateValid = validateDates();
                const isExperienceValid = validateExperience();
                const isJobTitleValid = validateJobTitle();
                
                const isDescValid = validateTextField(descriptionInput, 1, 4000, 'Job description');
                const isReqValid = validateTextField(requirementsInput, 1, 4000, 'Requirements');
                const isBenValid = validateTextField(benefitsInput, 0, 2000, 'Benefits');
                const isLocValid = validateTextField(locationInput, 1, 255, 'Working location');
                const isWorkHoursValid = validateTextField(workingHoursInput, 0, 255, 'Working hours');
                
                const allCustomValid = isSalaryValid && isDateValid && isExperienceValid && 
                                      isJobTitleValid && isDescValid && isReqValid && 
                                      isBenValid && isLocValid && isWorkHoursValid;
                
                if (!form.checkValidity() || !allCustomValid) {
                    event.preventDefault();
                    event.stopPropagation();
                    
                    // Scroll to first invalid field
                    const firstInvalid = form.querySelector(':invalid');
                    if (firstInvalid) {
                        firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
                        firstInvalid.focus();
                    }
                }
                
                form.classList.add('was-validated');
            }, false);
            
            // Prevent form submission on Enter key (except in submit button)
            form.addEventListener('keydown', function(event) {
                if (event.key === 'Enter' && event.target.type !== 'submit') {
                    event.preventDefault();
                }
            });
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