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
                                           value="${formData != null ? formData.jobTitle : (param.jobTitle != null ? param.jobTitle : requestDetails.positionName)}">
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
                                           value="${formData != null ? formData.code : (param.code != null ? param.code : requestDetails.positionCode)}">
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
                                    <c:set var="currentWorkingHours" value="${formData != null ? formData.workingHours : param.workingHours}" />
                                    <select class="form-select ${not empty errors.workingHours ? 'is-invalid' : ''}"
                                            id="workingHours" name="workingHours">
                                        <option value="">Select working hours...</option>
                                        <option value="Monday - Friday 8h00 - 12h00 & 13h00- 17h00" 
                                                ${currentWorkingHours == 'Monday - Friday 8h00 - 12h00 & 13h00- 17h00' ? 'selected' : ''}>
                                            Monday - Friday 8h00 - 12h00 & 13h00- 17h00
                                        </option>
                                        <option value="Monday - Friday 8h00 - 12h00" 
                                                ${currentWorkingHours == 'Monday - Friday 8h00 - 12h00' ? 'selected' : ''}>
                                            Monday - Friday 8h00 - 12h00
                                        </option>
                                        <option value="Monday - Friday 13h00- 17h00" 
                                                ${currentWorkingHours == 'Monday - Friday 13h00- 17h00' ? 'selected' : ''}>
                                            Monday - Friday 13h00- 17h00
                                        </option>
                                    </select>
                                    <div class="form-text">Select from predefined working hour options</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.workingHours ? errors.workingHours : 'Please select a valid working hours option'}
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
                                           value="${formData != null ? formData.minExperienceYears : param.minExperienceYears}">
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
                                           value="${formData != null ? formData.startDate : param.startDate}">
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
                                           value="${formData != null ? formData.salaryType : (param.salaryType != null ? param.salaryType : requestDetails.salaryType)}"
                                           placeholder="e.g. RANGE, FROM, NEGOTIABLE">
                                    <div class="form-text">RANGE, FROM, NEGOTIABLE, GROSS, NET</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.salaryType ? errors.salaryType : 'Salary type is required'}
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <label for="minSalary" class="form-label">Minimum Salary (VND)</label>
                                    <c:set var="currentMinSalary" value="${formData != null ? formData.minSalary : (param.minSalary != null ? param.minSalary : requestDetails.minSalary)}" />
                                    <input type="text" class="form-control ${not empty errors.minSalary ? 'is-invalid' : ''}" 
                                           id="minSalary" name="minSalary"
                                           pattern="[0-9,]+"
                                           value="${currentMinSalary}"
                                           data-original-value="${currentMinSalary}"
                                           placeholder="e.g. 12,000,000">
                                    <div class="form-text">Min: 1,000,000 VND (use commas for thousands)</div>
                                    <div class="invalid-feedback">
                                        ${not empty errors.minSalary ? errors.minSalary : 'Minimum salary must be at least 1,000,000 VND'}
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <label for="maxSalary" class="form-label">Maximum Salary (VND)</label>
                                    <c:set var="currentMaxSalary" value="${formData != null ? formData.maxSalary : (param.maxSalary != null ? param.maxSalary : requestDetails.maxSalary)}" />
                                    <input type="text" class="form-control ${not empty errors.maxSalary ? 'is-invalid' : ''}" 
                                           id="maxSalary" name="maxSalary"
                                           pattern="[0-9,]+"
                                           value="${currentMaxSalary}"
                                           data-original-value="${currentMaxSalary}"
                                           placeholder="e.g. 15,000,000">
                                    <div class="form-text">Must be greater than min salary (use commas for thousands)</div>
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
                                              placeholder="List required skills, qualifications, and experience..."><c:out value="${formData != null ? formData.requirements : param.requirements}"/></textarea>
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
                                              placeholder="List employee benefits, perks, and incentives..."><c:out value="${formData != null ? formData.benefits : param.benefits}"/></textarea>
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
                                           value="${formData != null ? formData.applicationDeadline : param.applicationDeadline}"
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
                                           value="${formData != null ? formData.contactEmail : param.contactEmail}" 
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
                                           value="${formData != null ? formData.contactPhone : param.contactPhone}"
                                           pattern="^[0-9+][0-9()\- ]{8,20}$"
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
                            counter.style.fontWeight = 'bold';
                        } else if (length > maxLength * 0.75) {
                            counter.style.color = '#ffc107'; // Yellow when 75%
                            counter.style.fontWeight = '500';
                        } else {
                            counter.style.color = '#6c757d'; // Gray default
                            counter.style.fontWeight = 'normal';
                        }
                        
                        // Add warning if over limit
                        if (length > maxLength) {
                            counter.textContent += ' (Over limit!)';
                            counter.style.color = '#dc3545';
                            counter.style.fontWeight = 'bold';
                        }
                    }
                    
                    element.parentNode.appendChild(counter);
                    element.addEventListener('input', updateCounter);
                    updateCounter(); // Initial count
                }
            }
            
            // Setup counters for all textareas and relevant inputs
            document.querySelectorAll('textarea, input[maxlength]').forEach(setupCharCounter);
            
            // Format salary inputs on page load
            function formatSalaryInputsOnLoad() {
                if (minSalaryInput && minSalaryInput.value) {
                    const value = parseNumber(minSalaryInput.value);
                    if (value && !isNaN(value)) {
                        minSalaryInput.value = formatNumber(value);
                    }
                }
                
                if (maxSalaryInput && maxSalaryInput.value) {
                    const value = parseNumber(maxSalaryInput.value);
                    if (value && !isNaN(value)) {
                        maxSalaryInput.value = formatNumber(value);
                    }
                }
            }
            
            // Call format function after DOM is ready
            formatSalaryInputsOnLoad();
            
            // Dropdown validation function
            function validateDropdown(element, fieldName) {
                if (!element) return true;
                
                const value = element.value.trim();
                
                // Clear custom validity
                element.setCustomValidity('');
                
                // For optional fields, empty is valid
                if (fieldName === 'Working hours' && value === '') {
                    return true;
                }
                
                // Check if a valid option is selected
                if (value === '') {
                    element.setCustomValidity(fieldName + ' must be selected');
                    return false;
                }
                
                return true;
            }
            
            // Text field validation function (if not already defined)
            function validateTextField(element, minLength, maxLength, fieldName) {
                if (!element) return true;
                
                const value = element.value.trim();
                
                // Clear custom validity
                element.setCustomValidity('');
                
                // Check length requirements
                if (minLength > 0 && value.length < minLength) {
                    element.setCustomValidity(fieldName + ' must be at least ' + minLength + ' characters');
                    return false;
                }
                
                if (maxLength > 0 && value.length > maxLength) {
                    element.setCustomValidity(fieldName + ' cannot exceed ' + maxLength + ' characters');
                    return false;
                }
                
                return true;
            }
            
            // Number formatting functions
            function formatNumber(num) {
                if (!num) return '';
                // Convert to integer to avoid decimal issues
                const intNum = Math.floor(parseFloat(num));
                return intNum.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
            }
            
            function parseNumber(str) {
                if (!str) return null;
                // Remove commas and parse as integer
                const cleaned = str.replace(/,/g, '');
                const parsed = parseInt(cleaned, 10);
                return isNaN(parsed) ? null : parsed;
            }
            
            // Salary validation
            function validateSalaries() {
                const minSalary = parseNumber(minSalaryInput.value);
                const maxSalary = parseNumber(maxSalaryInput.value);
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
                workingHoursInput.addEventListener('change', function() {
                    validateDropdown(this, 'Working hours');
                });
            }
            
            // Salary formatting on blur
            if (minSalaryInput) {
                minSalaryInput.addEventListener('blur', function() {
                    const value = parseNumber(this.value);
                    if (value) {
                        this.value = formatNumber(value);
                    }
                    validateSalaries();
                });
                
                // Allow only numbers and commas
                minSalaryInput.addEventListener('input', function() {
                    this.value = this.value.replace(/[^0-9,]/g, '');
                });
            }
            
            if (maxSalaryInput) {
                maxSalaryInput.addEventListener('blur', function() {
                    const value = parseNumber(this.value);
                    if (value) {
                        this.value = formatNumber(value);
                    }
                    validateSalaries();
                });
                
                // Allow only numbers and commas
                maxSalaryInput.addEventListener('input', function() {
                    this.value = this.value.replace(/[^0-9,]/g, '');
                });
            }

            // Form submission validation
            form.addEventListener('submit', function(event) {
                console.log('=== FORM SUBMIT EVENT TRIGGERED ===');
                console.log('Form action:', form.action);
                console.log('Form method:', form.method);
                
                // Show loading state
                const submitBtn = form.querySelector('button[type="submit"]');
                const originalText = submitBtn.innerHTML;
                
                // Run all validations
                const isSalaryValid = validateSalaries();
                const isDateValid = validateDates();
                const isExperienceValid = validateExperience();
                const isJobTitleValid = validateJobTitle();
                
                const isDescValid = validateTextField(descriptionInput, 1, 4000, 'Job description');
                const isReqValid = validateTextField(requirementsInput, 1, 4000, 'Requirements');
                const isBenValid = validateTextField(benefitsInput, 0, 2000, 'Benefits');
                const isLocValid = validateTextField(locationInput, 1, 255, 'Working location');
                const isWorkHoursValid = validateDropdown(workingHoursInput, 'Working hours');
                
                const allCustomValid = isSalaryValid && isDateValid && isExperienceValid && 
                                      isJobTitleValid && isDescValid && isReqValid && 
                                      isBenValid && isLocValid && isWorkHoursValid;
                
                // Convert formatted salary values back to numbers before submission
                if (allCustomValid && form.checkValidity()) {
                    if (minSalaryInput && minSalaryInput.value) {
                        minSalaryInput.value = parseNumber(minSalaryInput.value) || '';
                    }
                    if (maxSalaryInput && maxSalaryInput.value) {
                        maxSalaryInput.value = parseNumber(maxSalaryInput.value) || '';
                    }
                }
                
                if (!form.checkValidity() || !allCustomValid) {
                    console.log('=== FORM VALIDATION FAILED ===');
                    console.log('Form checkValidity:', form.checkValidity());
                    console.log('Custom validation:', allCustomValid);
                    
                    event.preventDefault();
                    event.stopPropagation();
                    
                    // Scroll to first invalid field
                    const firstInvalid = form.querySelector(':invalid');
                    if (firstInvalid) {
                        console.log('First invalid field:', firstInvalid.name, firstInvalid.validationMessage);
                        firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
                        firstInvalid.focus();
                    }
                    
                    // Show error message
                    showNotification('Please fix the errors in the form before submitting.', 'error');
                } else {
                    console.log('=== FORM VALIDATION PASSED ===');
                    console.log('Submitting form to:', form.action);
                    
                    // Show loading state (but don't disable inputs until after form submits)
                    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i> Creating Job Posting...';
                    
                    // Show success message
                    showNotification('Creating job posting...', 'info');
                    
                    // Disable submit button to prevent double submission
                    setTimeout(function() {
                        submitBtn.disabled = true;
                    }, 100);
                }
                
                form.classList.add('was-validated');
            }, false);
            
            // Notification system
            function showNotification(message, type = 'info') {
                // Remove existing notifications
                const existingNotifications = document.querySelectorAll('.custom-notification');
                existingNotifications.forEach(n => n.remove());
                
                const notification = document.createElement('div');
                let alertClass = 'info';
                if (type === 'error') alertClass = 'danger';
                else if (type === 'success') alertClass = 'success';
                
                notification.className = 'custom-notification alert alert-' + alertClass;
                notification.style.cssText = 
                    'position: fixed;' +
                    'top: 20px;' +
                    'right: 20px;' +
                    'z-index: 9999;' +
                    'min-width: 300px;' +
                    'animation: slideInRight 0.3s ease;';
                
                let icon = 'info-circle';
                if (type === 'error') icon = 'exclamation-triangle';
                else if (type === 'success') icon = 'check-circle';
                
                notification.innerHTML = 
                    '<i class="fas fa-' + icon + ' me-2"></i>' +
                    message +
                    '<button type="button" class="btn-close float-end" onclick="this.parentElement.remove()"></button>';
                
                document.body.appendChild(notification);
                
                // Auto remove after 5 seconds
                setTimeout(function() {
                    if (notification.parentElement) {
                        notification.style.animation = 'slideOutRight 0.3s ease';
                        setTimeout(function() { 
                            if (notification.parentElement) {
                                notification.remove(); 
                            }
                        }, 300);
                    }
                }, 5000);
            }
            
            // Add CSS for notification animations
            const style = document.createElement('style');
            style.textContent = 
                '@keyframes slideInRight {' +
                '    from { transform: translateX(100%); opacity: 0; }' +
                '    to { transform: translateX(0); opacity: 1; }' +
                '}' +
                '@keyframes slideOutRight {' +
                '    from { transform: translateX(0); opacity: 1; }' +
                '    to { transform: translateX(100%); opacity: 0; }' +
                '}';
            document.head.appendChild(style);
            
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
/* Enhanced styles for Job Posting form */
.content-area {
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    min-height: 100vh;
    padding: 2rem;
}

.page-head {
    background: rgba(255, 255, 255, 0.95);
    padding: 1.5rem;
    border-radius: 15px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    margin-bottom: 2rem;
}

.page-title {
    color: #2c3e50;
    font-weight: 600;
    margin-bottom: 0.5rem;
}

.page-subtitle {
    color: #6c757d;
    margin-bottom: 0;
}

.job-posting-card {
    background: rgba(255, 255, 255, 0.95);
    border: none;
    border-radius: 20px;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
    overflow: hidden;
}

.job-posting-card .card-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    padding: 1.5rem;
}

.job-posting-card .card-header h4 {
    color: white;
    margin-bottom: 0;
    font-weight: 600;
}

.card.mb-4 {
    background: rgba(255, 255, 255, 0.8);
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 15px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
    backdrop-filter: blur(10px);
    transition: all 0.3s ease;
}

.card.mb-4:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 35px rgba(0, 0, 0, 0.15);
}

.card.mb-4 .card-header {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: white;
    border: none;
    font-weight: 600;
    padding: 1rem 1.5rem;
    border-radius: 15px 15px 0 0;
}

.form-label {
    font-weight: 600;
    color: #2c3e50;
    margin-bottom: 0.75rem;
}

.form-label i {
    width: 20px;
    color: #667eea;
    margin-right: 0.5rem;
}

.form-control {
    border: 2px solid #e9ecef;
    border-radius: 10px;
    padding: 0.75rem 1rem;
    transition: all 0.3s ease;
    background: rgba(255, 255, 255, 0.9);
}

.form-control:focus {
    border-color: #667eea;
    box-shadow: 0 0 0 0.25rem rgba(102, 126, 234, 0.25);
    background: white;
}

.form-control[readonly] {
    background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
    border-color: #dee2e6;
    color: #6c757d;
}

.form-text {
    font-size: 0.825rem;
    color: #6c757d;
    margin-top: 0.5rem;
}

.char-counter {
    font-size: 0.75rem;
    color: #6c757d;
    text-align: right;
    margin-top: 0.25rem;
    font-weight: 500;
}

.btn {
    border-radius: 10px;
    padding: 0.75rem 2rem;
    font-weight: 600;
    transition: all 0.3s ease;
    border: none;
}

.btn-primary {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-primary:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
}

.btn-secondary {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: white;
    box-shadow: 0 4px 15px rgba(245, 87, 108, 0.4);
}

.btn-secondary:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(245, 87, 108, 0.6);
    color: white;
}

.btn-outline-secondary {
    border: 2px solid #6c757d;
    color: #6c757d;
    background: transparent;
}

.btn-outline-secondary:hover {
    background: #6c757d;
    color: white;
    transform: translateY(-2px);
}

.alert {
    border: none;
    border-radius: 15px;
    padding: 1rem 1.5rem;
    margin-bottom: 1.5rem;
}

.alert-danger {
    background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
    color: #721c24;
}

.alert-success {
    background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
    color: #155724;
}

/* Enhanced validation styles */
.was-validated .form-control:invalid,
.form-control.is-invalid {
    border-color: #dc3545;
    background: rgba(220, 53, 69, 0.05);
}

.was-validated .form-control:invalid:focus,
.form-control.is-invalid:focus {
    border-color: #dc3545;
    box-shadow: 0 0 0 0.25rem rgba(220, 53, 69, 0.25);
}

.was-validated .form-control:valid,
.form-control.is-valid {
    border-color: #198754;
    background: rgba(25, 135, 84, 0.05);
}

.was-validated .form-control:valid:focus,
.form-control.is-valid:focus {
    border-color: #198754;
    box-shadow: 0 0 0 0.25rem rgba(25, 135, 84, 0.25);
}

.invalid-feedback {
    font-weight: 500;
    font-size: 0.875rem;
}

/* Responsive adjustments */
@media (max-width: 768px) {
    .content-area {
        padding: 1rem;
    }
    
    .page-head {
        padding: 1rem;
        margin-bottom: 1rem;
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
    
    .card.mb-4 .card-header {
        padding: 0.75rem 1rem;
    }
}

/* Loading animation for form submission */
.btn-primary:disabled {
    background: linear-gradient(135deg, #6c757d 0%, #adb5bd 100%);
    cursor: not-allowed;
}

/* Smooth transitions */
* {
    transition: all 0.3s ease;
}

/* Custom scrollbar */
::-webkit-scrollbar {
    width: 8px;
}

::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 10px;
}

::-webkit-scrollbar-thumb {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
    background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

/* Custom notification styles */
.custom-notification {
    border: none;
    border-radius: 15px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    backdrop-filter: blur(10px);
}

.custom-notification .btn-close {
    background: none;
    border: none;
    font-size: 1.2rem;
    opacity: 0.7;
}

.custom-notification .btn-close:hover {
    opacity: 1;
}

/* Form loading state */
.form-loading {
    pointer-events: none;
    opacity: 0.7;
}

.form-loading .form-control {
    background-color: #f8f9fa !important;
}
</style>
</body>
</html>