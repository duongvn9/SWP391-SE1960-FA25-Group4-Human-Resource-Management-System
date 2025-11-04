<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <jsp:include page="../layout/head.jsp">
            <jsp:param name="pageTitle" value="Payslip Details - HRMS" />
        </jsp:include>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/detail-payslip.css">
        <title>Payslip Details - HRMS</title>
    </head>

    <body class="payslip-page">
        <div class="page-wrapper">
            <jsp:include page="../layout/dashboard-header.jsp" />

            <div class="main-container">
                <jsp:include page="../layout/sidebar.jsp">
                    <jsp:param name="currentPage" value="payroll-detail" />
                </jsp:include>

                <main class="main-content">
                    <h2 class="page-title">
                        <i class="fas fa-file-invoice-dollar me-3"></i>Payslip Details
                    </h2>

                    <!-- Filter and Action Buttons -->
                    <div class="action-buttons no-print">
                        <a href="${pageContext.request.contextPath}/payroll/list" class="btn btn-secondary">
                            <i class="fas fa-arrow-left me-2"></i>Back to List
                        </a>

                        <!-- Pay Period Filter -->
                        <div class="filter-group">
                            <select id="payPeriodFilter" class="form-select" onchange="filterByPayPeriod()">
                                <option value="">Filter by Pay Period</option>
                                <c:forEach var="period" items="${period}">
                                    <option value="${period.id}" ${selectedPeriod==period.id ? 'selected' : ''}>
                                        ${period.name}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <button onclick="exportToPDF()" class="btn btn-success">
                            <i class="fas fa-file-pdf me-2"></i>Export PDF
                        </button>
                    </div>

                    <!-- Employee Information -->
                    <div class="card payslip-card">
                        <div class="card-header employee-info">
                            <h5 class="mb-0"><i class="fas fa-user me-2"></i>Employee Information</h5>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="row mb-2">
                                        <div class="col-5"><strong>Employee ID:</strong></div>
                                        <div class="col-7">${payslip.employee.employeeCode}</div>
                                    </div>
                                    <div class="row mb-2">
                                        <div class="col-5"><strong>Full Name:</strong></div>
                                        <div class="col-7">${payslip.employee.fullName}</div>
                                    </div>
                                    <div class="row mb-2">
                                        <div class="col-5"><strong>Position:</strong></div>
                                        <div class="col-7">${payslip.employee.position}</div>
                                    </div>
                                    <div class="row mb-2">
                                        <div class="col-5"><strong>Department:</strong></div>
                                        <div class="col-7">${payslip.employee.department}</div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="row mb-2">
                                        <div class="col-5"><strong>Pay Period:</strong></div>
                                        <div class="col-7">
                                            <fmt:formatDate value="${payslip.payPeriodStart}"
                                                            pattern="dd/MM/yyyy" /> -
                                            <fmt:formatDate value="${payslip.payPeriodEnd}"
                                                            pattern="dd/MM/yyyy" />
                                        </div>
                                    </div>
                                    <div class="row mb-2">
                                        <div class="col-5"><strong>Created Date:</strong></div>
                                        <div class="col-7">
                                            <fmt:formatDate value="${payslip.createdDate}"
                                                            pattern="dd/MM/yyyy HH:mm" />
                                        </div>
                                    </div>
                                    <div class="row mb-2">
                                        <div class="col-5"><strong>Status:</strong></div>
                                        <div class="col-7">
                                            <c:choose>
                                                <c:when test="${payslip.status == 'PAID'}">
                                                    <span class="badge bg-success status-badge">Paid</span>
                                                </c:when>
                                                <c:when test="${payslip.status == 'PENDING'}">
                                                    <span class="badge bg-warning status-badge">Pending</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary status-badge">Draft</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                    <div class="row mb-2">
                                        <div class="col-5"><strong>Bank Account:</strong></div>
                                        <div class="col-7">${payslip.employee.bankAccount}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Salary Details -->
                    <div class="row">
                        <!-- Earnings -->
                        <div class="col-md-6">
                            <div class="card payslip-card">
                                <div class="card-header bg-success text-white">
                                    <h5 class="mb-0"><i class="fas fa-plus-circle me-2"></i>Earnings</h5>
                                </div>
                                <div class="card-body p-0">
                                    <div class="salary-item px-3">
                                        <div class="d-flex justify-content-between">
                                            <span>Basic Salary:</span>
                                            <span>
                                                <fmt:formatNumber value="${payslip.basicSalary}" type="currency"
                                                                  currencySymbol="$" />
                                            </span>
                                        </div>
                                    </div>
                                    <div class="salary-item px-3">
                                        <div class="d-flex justify-content-between">
                                            <span>Overtime Pay:</span>
                                            <span>
                                                <fmt:formatNumber value="${payslip.overtimePay}" type="currency"
                                                                  currencySymbol="$" />
                                            </span>
                                        </div>
                                    </div>
                                    <div class="salary-item px-3">
                                        <div class="d-flex justify-content-between">
                                            <span>Bonus:</span>
                                            <span>
                                                <fmt:formatNumber value="${payslip.bonus}" type="currency"
                                                                  currencySymbol="$" />
                                            </span>
                                        </div>
                                    </div>
                                    <div class="salary-item px-3 total-row">
                                        <div class="d-flex justify-content-between">
                                            <span>Total Earnings:</span>
                                            <span>
                                                <fmt:formatNumber value="${payslip.totalEarnings}"
                                                                  type="currency" currencySymbol="$" />
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Deductions -->
                        <div class="col-md-6">
                            <div class="card payslip-card">
                                <div class="card-header bg-danger text-white">
                                    <h5 class="mb-0"><i class="fas fa-minus-circle me-2"></i>Deductions</h5>
                                </div>
                                <div class="card-body p-0">
                                    <div class="salary-item px-3">
                                        <div class="d-flex justify-content-between">
                                            <span>Social Insurance (8%):</span>
                                            <span>
                                                <fmt:formatNumber value="${payslip.socialInsurance}"
                                                                  type="currency" currencySymbol="$" />
                                            </span>
                                        </div>
                                    </div>
                                    <div class="salary-item px-3">
                                        <div class="d-flex justify-content-between">
                                            <span>Health Insurance (1.5%):</span>
                                            <span>
                                                <fmt:formatNumber value="${payslip.healthInsurance}"
                                                                  type="currency" currencySymbol="$" />
                                            </span>
                                        </div>
                                    </div>
                                    <div class="salary-item px-3">
                                        <div class="d-flex justify-content-between">
                                            <span>Unemployment Insurance (1%):</span>
                                            <span>
                                                <fmt:formatNumber value="${payslip.unemploymentInsurance}"
                                                                  type="currency" currencySymbol="$" />
                                            </span>
                                        </div>
                                    </div>
                                    <div class="salary-item px-3">
                                        <div class="d-flex justify-content-between">
                                            <span>Personal Income Tax:</span>
                                            <span>
                                                <fmt:formatNumber value="${payslip.personalIncomeTax}"
                                                                  type="currency" currencySymbol="$" />
                                            </span>
                                        </div>
                                    </div>
                                    <div class="salary-item px-3">
                                        <div class="d-flex justify-content-between">
                                            <span>Other Deductions:</span>
                                            <span>
                                                <fmt:formatNumber value="${payslip.otherDeductions}"
                                                                  type="currency" currencySymbol="$" />
                                            </span>
                                        </div>
                                    </div>
                                    <div class="salary-item px-3 total-row">
                                        <div class="d-flex justify-content-between">
                                            <span>Total Deductions:</span>
                                            <span>
                                                <fmt:formatNumber value="${payslip.totalDeductions}"
                                                                  type="currency" currencySymbol="$" />
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Net Salary -->
                    <div class="card payslip-card">
                        <div class="card-body net-salary text-center">
                            <h3 class="mb-0">
                                <i class="fas fa-money-bill-wave me-3"></i>
                                Net Salary:
                                <fmt:formatNumber value="${payslip.netSalary}" type="currency"
                                                  currencySymbol="$" />
                            </h3>
                        </div>
                    </div>

                    <!-- Work Summary -->
                    <div class="card payslip-card">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="fas fa-clock me-2"></i>Work Summary</h5>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-3">
                                    <div class="text-center">
                                        <div class="h4 text-primary">${payslip.workDays}</div>
                                        <small class="text-muted">Work Days</small>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="text-center">
                                        <div class="h4 text-warning">${payslip.overtimeHours}</div>
                                        <small class="text-muted">Overtime Hours</small>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="text-center">
                                        <div class="h4 text-info">${payslip.leaveDays}</div>
                                        <small class="text-muted">Leave Days</small>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="text-center">
                                        <div class="h4 text-danger">${payslip.absentDays}</div>
                                        <small class="text-muted">Absent Days</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
            </div>
            <!-- Notes -->
            <c:if test="${not empty payslip.notes}">
                <div class="card payslip-card">
                    <div class="card-header">
                        <h5 class="mb-0"><i class="fas fa-sticky-note me-2"></i>Notes</h5>
                    </div>
                    <div class="card-body">
                        <p class="mb-0">${payslip.notes}</p>
                    </div>
                </div>
            </c:if>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/assets/js/detail-payslip.js"></script>
</body>
</html>