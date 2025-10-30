<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Employment Contract - HRMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/contract.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            margin: 0;
            padding: 0;
            background-color: #f8f9fa;
        }
        
        .main-content {
            padding: 2rem 2rem 0 2rem;
            min-height: calc(100vh - 64px);
            display: flex;
            flex-direction: column;
        }
        
        .container-fluid {
            flex: 1;
        }
        
        .dashboard-footer {
            margin-left: -2rem;
            margin-right: -2rem;
            margin-bottom: 0;
        }
        
        .profile-container {
            max-width: 100%;
            background: white;
            padding: 30px 50px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        
        .profile-header {
            text-align: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 1px solid #dee2e6;
        }
        
        .profile-header h2 {
            color: #333;
            font-weight: 600;
            font-size: 1.5rem;
            margin: 0;
        }
        
        .profile-description {
            background-color: #f8f9fa;
            padding: 12px 15px;
            font-size: 0.9rem;
            color: #666;
            margin-bottom: 25px;
            font-style: italic;
        }
        
        .contract-content {
            /* Content styling */
        }
        
        .form-label {
            font-weight: 600;
            color: #495057;
            margin-bottom: 0.5rem;
        }
        
        .form-row {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
            min-height: 40px;
        }
        
        .form-row.two-columns {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
            min-height: 40px;
        }
        
        .form-row.two-columns .form-label:first-child {
            width: 150px;
        }
        
        .form-row.two-columns .form-value:first-of-type {
            flex: 0 0 calc(50% - 125px);
            margin-right: 15px;
        }
        
        .form-row.two-columns .form-label:nth-child(3) {
            flex: 0 0 150px;
            text-align: right;
            padding-right: 15px;
            white-space: nowrap;
        }
        
        .form-row.two-columns .form-value:last-of-type {
            flex: 1;
        }
        
        .form-label {
            width: 150px;
            font-weight: 500;
            color: #333;
            margin: 0;
            padding-right: 15px;
            text-align: left;
            flex-shrink: 0;
        }
        
        .form-value {
            flex: 1;
            padding: 8px 12px;
            background-color: #f8f9fa;
            border: 1px solid #e9ecef;
            border-radius: 4px;
            font-size: 0.95rem;
            color: #495057;
        }
        
        .alert-info {
            border-radius: 8px;
            border: none;
            background-color: #cfe2ff;
            color: #084298;
        }
        
        @media (max-width: 768px) {
            .main-content {
                padding: 1rem;
            }
            
            .contract-page-header {
                padding: 1rem;
            }
            
            .contract-page-header h2 {
                font-size: 1.15rem;
            }
            
            .contract-card {
                padding: 1.5rem;
            }
        }
    </style>
</head>
<body>
    <!-- Include dashboard header -->
    <jsp:include page="../layout/dashboard-header.jsp" />
    
    <!-- Include sidebar -->
    <jsp:include page="../layout/sidebar.jsp" />
    
    <div class="main-content">
        <div class="container-fluid">
            <c:choose>
                <c:when test="${not empty contract}">
                    <div class="profile-container">
                        <!-- Profile Header -->
                        <div class="profile-header">
                            <h2>Employment Contract</h2>
                            <p style="margin-top: 0.5rem; color: #666; font-size: 0.95rem;">Contract ID: <c:out value='${contract.id}'/></p>
                        </div>
                        
                        <div class="contract-content">
                            <!-- Row 1: Full Name (Full Width) -->
                            <div class="form-row">
                                <label class="form-label">Full Name:</label>
                                <div class="form-value"><c:out value='${contract.userFullName}'/></div>
                            </div>
                            
                            <!-- Row 2: Contract No & Contract Type -->
                            <div class="form-row two-columns">
                                <label class="form-label">Contract No:</label>
                                <div class="form-value"><c:out value='${contract.contractNo}'/></div>
                                <label class="form-label">Contract Type:</label>
                                <div class="form-value"><c:out value='${contract.contractTypeDisplay}'/></div>
                            </div>
                            
                            <!-- Row 3: Start Date & End Date -->
                            <div class="form-row two-columns">
                                <label class="form-label">Start Date:</label>
                                <div class="form-value"><c:out value='${contract.formattedStartDate}'/></div>
                                <label class="form-label">End Date:</label>
                                <div class="form-value"><c:out value='${contract.formattedEndDate}'/></div>
                            </div>
                            
                            <!-- Row 4: Base Salary & Currency -->
                            <div class="form-row two-columns">
                                <label class="form-label">Base Salary:</label>
                                <div class="form-value"><c:out value='${contract.formattedSalary}'/></div>
                                <label class="form-label">Currency:</label>
                                <div class="form-value"><c:out value='${contract.currency}'/></div>
                            </div>
                            
                            <!-- Row 5: Note (Full Width) -->
                            <div class="form-row">
                                <label class="form-label">Note:</label>
                                <div class="form-value"><c:out value="${contract.note}"/></div>
                            </div>
                            
                            <!-- Row 6: File Path (Link) -->
                            <div class="form-row">
                                <label class="form-label">Contract File:</label>
                                <div class="form-value">
                                    <c:choose>
                                        <c:when test="${not empty contract.filePath}">
                                            <a href="${pageContext.request.contextPath}/<c:out value='${contract.filePath}'/>" 
                                               target="_blank" 
                                               class="btn btn-sm btn-outline-primary">
                                                <i class="fas fa-file-pdf me-1"></i>
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">No file available</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="profile-container">
                        <div class="profile-header">
                            <h2>Employment Contract</h2>
                        </div>
                        <div class="contract-content">
                            <div class="alert alert-info text-center" role="alert">
                                <i class="fas fa-info-circle me-2"></i>
                                <c:out value="${message != null ? message : 'No contract information'}"/>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        
        <!-- Footer -->
        <jsp:include page="../layout/dashboard-footer.jsp" />
    </div>
</body>
</html>