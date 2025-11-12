<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="Contract Form - HRMS" />
        <jsp:param name="pageCss" value="dashboard.css" />
    </jsp:include>
    <style>
        .main-content {
            margin-left: 260px;
            padding: 2rem 2rem 0 2rem;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        
        .content-area {
            flex: 1;
            margin-bottom: 2rem;
        }
        
        .dashboard-footer {
            margin-left: -2rem;
            margin-right: -2rem;
            margin-bottom: 0;
        }
        
        .form-card {
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
            padding: 2rem;
        }
        
        @media (max-width: 768px) {
            .main-content {
                margin-left: 0;
            }
        }
    </style>
    <script>
        // Client-side validation for dates and contract type
        document.addEventListener('DOMContentLoaded', function() {
            const form = document.querySelector('form');
            const startDateInput = document.getElementById('startDate');
            const endDateInput = document.getElementById('endDate');
            const contractTypeSelect = document.getElementById('contractType');
            const isEditMode = <c:out value="${not empty contract}" default="false"/>;
            
            // Handle contract type change (only for create mode)
            if (!isEditMode && contractTypeSelect) {
                contractTypeSelect.addEventListener('change', function() {
                    const selectedType = this.value;
                    
                    if (selectedType === 'indefinite') {
                        // Disable and clear end date for indefinite contracts
                        endDateInput.disabled = true;
                        endDateInput.value = '';
                        endDateInput.removeAttribute('required');
                    } else {
                        // Enable end date for fixed_term and probation
                        endDateInput.disabled = false;
                        endDateInput.setAttribute('required', 'required');
                    }
                });
                
                // Trigger change event on page load
                contractTypeSelect.dispatchEvent(new Event('change'));
            }
            
            // Form validation
            form.addEventListener('submit', function(e) {
                if (!isEditMode) {
                    const contractType = contractTypeSelect.value;
                    const startDate = startDateInput.value ? new Date(startDateInput.value) : null;
                    const endDate = endDateInput.value ? new Date(endDateInput.value) : null;
                    
                    // Validate dates
                    if (endDate && startDate && startDate >= endDate) {
                        e.preventDefault();
                        alert('Start date must be before end date');
                        return false;
                    }
                    
                    // Validate indefinite contract has no end date
                    if (contractType === 'indefinite' && endDate) {
                        e.preventDefault();
                        alert('Indefinite contracts cannot have an end date');
                        return false;
                    }
                    
                    // Validate fixed_term and probation have end date
                    if ((contractType === 'fixed_term' || contractType === 'probation') && !endDate) {
                        e.preventDefault();
                        alert('Fixed-term and probation contracts must have an end date');
                        return false;
                    }
                }
            });
        });
    </script>
</head>
<body>
    <jsp:include page="../layout/sidebar.jsp">
        <jsp:param name="currentPage" value="contracts" />
    </jsp:include>
    
    <div class="main-content" id="main-content">
        <jsp:include page="../layout/dashboard-header.jsp" />
        
        <div class="content-area">
            <c:if test="${not empty param.error}">
                <div class="alert alert-danger alert-dismissible fade show">
                    <i class="fas fa-exclamation-circle"></i> ${param.error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show">
                    <i class="fas fa-exclamation-circle"></i> ${errorMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            
            <c:set var="displayData" value="${not empty formData ? formData : contract}" />
            
            <div class="form-card">
                <h2 class="mb-4">
                    <i class="fas fa-file-contract"></i> 
                    <c:choose>
                        <c:when test="${isReplaceMode}">Replace Contract</c:when>
                        <c:when test="${empty contract}">Create New Contract</c:when>
                        <c:otherwise>Edit Contract</c:otherwise>
                    </c:choose>
                </h2>
                
                <c:if test="${isReplaceMode && not empty oldContract}">
                    <div class="alert alert-warning mb-4">
                        <i class="fas fa-exchange-alt"></i> 
                        <strong>Replacing Contract:</strong> ${oldContract.contractNo} 
                        <br><small>The old contract will be terminated after the new contract is successfully created.</small>
                    </div>
                </c:if>
                
                <form method="post" action="${pageContext.request.contextPath}/contracts/${isReplaceMode ? 'replace' : (empty contract ? 'create' : 'edit')}">
                    <c:if test="${not empty contract}">
                        <input type="hidden" name="id" value="${contract.id}">
                    </c:if>
                    <c:if test="${isReplaceMode && not empty oldContract}">
                        <input type="hidden" name="oldContractId" value="${oldContract.id}">
                    </c:if>
                    
                    <!-- Employee Selection -->
                    <div class="mb-3">
                        <label for="userId" class="form-label">Employee <span class="text-danger">*</span></label>
                        <select class="form-select" id="userId" name="userId" required ${not empty contract || isReplaceMode ? 'disabled' : ''}>
                            <option value="">Select Employee</option>
                            <c:forEach var="user" items="${users}">
                                <option value="${user.id}" 
                                        ${(not empty preSelectedUserId && preSelectedUserId == user.id) || 
                                          displayData.userId == user.id ? 'selected' : ''}>
                                    ${user.fullName} (${user.employeeCode})
                                </option>
                            </c:forEach>
                        </select>
                        <c:if test="${not empty contract || isReplaceMode}">
                            <input type="hidden" name="userId" value="${not empty contract ? contract.userId : preSelectedUserId}">
                            <small class="text-muted">Employee cannot be changed ${isReplaceMode ? 'when replacing contract' : 'after contract creation'}</small>
                        </c:if>
                        <c:if test="${empty contract && not empty preSelectedUserId && !isReplaceMode}">
                            <small class="text-muted">
                                <i class="fas fa-info-circle"></i> Employee pre-selected from users without contract list
                            </small>
                        </c:if>
                    </div>
                    
                    <!-- Contract Number -->
                    <div class="mb-3">
                        <label for="contractNo" class="form-label">Contract Number <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="contractNo" name="contractNo" 
                               value="${not empty displayData ? displayData.contractNo : generatedContractNo}" 
                               readonly required>
                        <c:if test="${empty contract}">
                            <small class="text-muted">Auto-generated contract number</small>
                        </c:if>
                        <c:if test="${not empty contract}">
                            <small class="text-muted">Contract number cannot be changed</small>
                        </c:if>
                    </div>
                    
                    <!-- Contract Type -->
                    <div class="mb-3">
                        <label for="contractType" class="form-label">Contract Type <span class="text-danger">*</span></label>
                        <select class="form-select" id="contractType" name="contractType" required>
                            <option value="">Select Type</option>
                            <option value="indefinite" ${displayData.contractType == 'indefinite' ? 'selected' : ''}>
                                Indefinite (Không xác định thời hạn)
                            </option>
                            <option value="fixed_term" ${displayData.contractType == 'fixed_term' ? 'selected' : ''}>
                                Fixed Term (Xác định thời hạn)
                            </option>
                            <option value="probation" ${displayData.contractType == 'probation' ? 'selected' : ''}>
                                Probation (Thử việc)
                            </option>
                        </select>
                    </div>
                    
                    <div class="row">
                        <!-- Start Date -->
                        <div class="col-md-6 mb-3">
                            <label for="startDate" class="form-label">Start Date <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" id="startDate" name="startDate" 
                                   value="${displayData.startDate}" required ${not empty contract ? 'readonly' : ''}>
                            <c:if test="${not empty contract}">
                                <small class="text-muted">Start date cannot be changed after contract creation</small>
                            </c:if>
                        </div>
                        
                        <!-- End Date -->
                        <div class="col-md-6 mb-3">
                            <label for="endDate" class="form-label">End Date</label>
                            <input type="date" class="form-control" id="endDate" name="endDate" 
                                   value="${displayData.endDate}" ${not empty contract ? 'readonly' : ''}>
                            <c:if test="${empty contract}">
                                <small class="text-muted">Leave empty for indefinite contracts</small>
                            </c:if>
                            <c:if test="${not empty contract}">
                                <small class="text-muted">End date cannot be changed after contract creation</small>
                            </c:if>
                        </div>
                    </div>
                    
                    <div class="row">
                        <!-- Base Salary -->
                        <div class="col-md-8 mb-3">
                            <label for="baseSalary" class="form-label">Base Salary <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="baseSalary" name="baseSalary" 
                                   value="${displayData.baseSalary}" step="0.01" min="0" required>
                        </div>
                        
                        <!-- Currency -->
                        <div class="col-md-4 mb-3">
                            <label for="currency" class="form-label">Currency</label>
                            <select class="form-select" id="currency" name="currency">
                                <option value="VND" ${empty displayData.currency || displayData.currency == 'VND' ? 'selected' : ''}>VND</option>
                                <option value="USD" ${displayData.currency == 'USD' ? 'selected' : ''}>USD</option>
                            </select>
                        </div>
                    </div>
                    
                    <!-- Status (Read-only) -->
                    <div class="mb-3">
                        <label for="status" class="form-label">Status</label>
                        <c:choose>
                            <c:when test="${not empty contract}">
                                <c:set var="statusDisplay" value="${contract.status == 'active' ? 'Active' : 
                                                                     contract.status == 'expired' ? 'Expired' : 
                                                                     contract.status == 'terminated' ? 'Terminated' : 'Draft'}" />
                                <c:set var="statusValue" value="${empty contract.status ? 'draft' : contract.status}" />
                                <input type="text" class="form-control" value="${statusDisplay}" readonly>
                                <input type="hidden" name="status" value="${statusValue}">
                                <small class="text-muted">Status is managed automatically by the system and cannot be changed manually</small>
                            </c:when>
                            <c:otherwise>
                                <input type="text" class="form-control" value="Draft" readonly>
                                <input type="hidden" name="status" value="draft">
                                <small class="text-muted">New contracts will be created with "Draft" status and require HRM approval</small>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    <!-- Note -->
                    <div class="mb-3">
                        <label for="note" class="form-label">Note</label>
                        <textarea class="form-control" id="note" name="note" rows="3">${displayData.note}</textarea>
                    </div>
                    
                    <!-- Buttons -->
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn ${isReplaceMode ? 'btn-warning' : 'btn-primary'}" 
                                ${contract.status == 'expired' || contract.status == 'terminated' ? 'disabled' : ''}>
                            <i class="fas ${isReplaceMode ? 'fa-exchange-alt' : 'fa-save'}"></i> 
                            ${isReplaceMode ? 'Replace Contract' : 'Save Contract'}
                        </button>
                        <a href="${pageContext.request.contextPath}/contracts" class="btn btn-secondary">
                            <i class="fas fa-times"></i> Cancel
                        </a>
                    </div>
                    <c:if test="${contract.status == 'expired' || contract.status == 'terminated'}">
                        <div class="alert alert-warning mt-3">
                            <i class="fas fa-exclamation-triangle"></i> 
                            This contract is ${contract.status} and cannot be edited.
                        </div>
                    </c:if>
                </form>
            </div>
        </div>
        
        <jsp:include page="../layout/dashboard-footer.jsp" />
    </div>
</body>
</html>
