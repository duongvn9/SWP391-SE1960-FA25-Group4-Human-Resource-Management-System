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
            max-width: 800px;
            margin: 0 auto;
        }
        
        @media (max-width: 768px) {
            .main-content {
                margin-left: 0;
            }
        }
    </style>
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
            
            <div class="form-card">
                <h2 class="mb-4">
                    <i class="fas fa-file-contract"></i> 
                    ${empty contract ? 'Create New Contract' : 'Edit Contract'}
                </h2>
                
                <form method="post" action="${pageContext.request.contextPath}/contracts/${empty contract ? 'create' : 'edit'}">
                    <c:if test="${not empty contract}">
                        <input type="hidden" name="id" value="${contract.id}">
                    </c:if>
                    
                    <!-- Employee Selection -->
                    <div class="mb-3">
                        <label for="userId" class="form-label">Employee <span class="text-danger">*</span></label>
                        <select class="form-select" id="userId" name="userId" required>
                            <option value="">Select Employee</option>
                            <c:forEach var="user" items="${users}">
                                <option value="${user.id}" ${contract.userId == user.id ? 'selected' : ''}>
                                    ${user.fullName} (${user.employeeCode})
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <!-- Contract Number -->
                    <div class="mb-3">
                        <label for="contractNo" class="form-label">Contract Number <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="contractNo" name="contractNo" 
                               value="${contract.contractNo}" required>
                    </div>
                    
                    <!-- Contract Type -->
                    <div class="mb-3">
                        <label for="contractType" class="form-label">Contract Type <span class="text-danger">*</span></label>
                        <select class="form-select" id="contractType" name="contractType" required>
                            <option value="">Select Type</option>
                            <option value="indefinite" ${contract.contractType == 'indefinite' ? 'selected' : ''}>
                                Indefinite (Không xác định thời hạn)
                            </option>
                            <option value="fixed_term" ${contract.contractType == 'fixed_term' ? 'selected' : ''}>
                                Fixed Term (Xác định thời hạn)
                            </option>
                            <option value="probation" ${contract.contractType == 'probation' ? 'selected' : ''}>
                                Probation (Thử việc)
                            </option>
                        </select>
                    </div>
                    
                    <div class="row">
                        <!-- Start Date -->
                        <div class="col-md-6 mb-3">
                            <label for="startDate" class="form-label">Start Date <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" id="startDate" name="startDate" 
                                   value="${contract.startDate}" required>
                        </div>
                        
                        <!-- End Date -->
                        <div class="col-md-6 mb-3">
                            <label for="endDate" class="form-label">End Date</label>
                            <input type="date" class="form-control" id="endDate" name="endDate" 
                                   value="${contract.endDate}">
                            <small class="text-muted">Leave empty for indefinite contracts</small>
                        </div>
                    </div>
                    
                    <div class="row">
                        <!-- Base Salary -->
                        <div class="col-md-8 mb-3">
                            <label for="baseSalary" class="form-label">Base Salary <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="baseSalary" name="baseSalary" 
                                   value="${contract.baseSalary}" step="0.01" required>
                        </div>
                        
                        <!-- Currency -->
                        <div class="col-md-4 mb-3">
                            <label for="currency" class="form-label">Currency</label>
                            <select class="form-select" id="currency" name="currency">
                                <option value="VND" ${empty contract.currency || contract.currency == 'VND' ? 'selected' : ''}>VND</option>
                                <option value="USD" ${contract.currency == 'USD' ? 'selected' : ''}>USD</option>
                            </select>
                        </div>
                    </div>
                    
                    <!-- Status -->
                    <div class="mb-3">
                        <label for="status" class="form-label">Status <span class="text-danger">*</span></label>
                        <select class="form-select" id="status" name="status" required>
                            <option value="active" ${empty contract.status || contract.status == 'active' ? 'selected' : ''}>Active</option>
                            <option value="expired" ${contract.status == 'expired' ? 'selected' : ''}>Expired</option>
                            <option value="terminated" ${contract.status == 'terminated' ? 'selected' : ''}>Terminated</option>
                        </select>
                    </div>
                    
                    <!-- Note -->
                    <div class="mb-3">
                        <label for="note" class="form-label">Note</label>
                        <textarea class="form-control" id="note" name="note" rows="3">${contract.note}</textarea>
                    </div>
                    
                    <!-- Buttons -->
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Save Contract
                        </button>
                        <a href="${pageContext.request.contextPath}/contracts" class="btn btn-secondary">
                            <i class="fas fa-times"></i> Cancel
                        </a>
                    </div>
                </form>
            </div>
        </div>
        
        <jsp:include page="../layout/dashboard-footer.jsp" />
    </div>
</body>
</html>
