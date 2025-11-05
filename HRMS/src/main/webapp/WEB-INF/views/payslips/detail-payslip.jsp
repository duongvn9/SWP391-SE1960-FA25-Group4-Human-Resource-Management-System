<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">

    <head>
        <!-- CSS riêng của trang -->
        <jsp:include page="../layout/head.jsp">
            <jsp:param name="pageTitle" value="Payslip Details - HRMS" />
            <jsp:param name="pageCss" value="detail-payslip.css" />
        </jsp:include>
        <!-- Emergency fix CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/detail-payslip-fix.css">
    </head>

    <body class="payslip-page">
        <!-- Sidebar -->
        <jsp:include page="../layout/sidebar.jsp">
            <jsp:param name="currentPage" value="payslip-list" />
        </jsp:include>

        <!-- Main Content -->
        <div class="main-content" id="main-content">
            <!-- Header -->
            <jsp:include page="../layout/dashboard-header.jsp" />

            <!-- Content Area -->
            <div class="content-area">
                <!-- Breadcrumb Navigation -->
                <nav aria-label="breadcrumb" class="mb-3">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item">
                            <a href="${pageContext.request.contextPath}/dashboard">
                                <i class="fas fa-home"></i> Home
                            </a>
                        </li>
                        <li class="breadcrumb-item">
                            <a href="${pageContext.request.contextPath}/payslips">
                                <i class="fas fa-file-invoice-dollar"></i> Payslips
                            </a>
                        </li>
                        <li class="breadcrumb-item active" aria-current="page">
                            <i class="fas fa-eye"></i> Details
                        </li>
                    </ol>
                </nav>
                <!-- Header -->
                <div class="payslip-header">
                    <div class="row align-items-center">
                        <div class="col-md-8">
                            <h2 class="mb-1">
                                <i class="fas fa-file-invoice-dollar me-2"></i>
                                Payslip Details
                            </h2>
                            <c:if test="${not empty periodDisplay}">
                                <p class="mb-0 opacity-75">Pay Period: ${periodDisplay}</p>
                            </c:if>
                        </div>
                        <div class="col-md-4 text-end">
                            <a href="${pageContext.request.contextPath}/payslips" class="btn btn-light">
                                <i class="fas fa-arrow-left me-1"></i> Back
                            </a>
                        </div>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${noPayslipsFound}">
                        <!-- No payslips found -->
                        <div class="info-card no-payslip">
                            <i class="fas fa-inbox fa-3x mb-3"></i>
                            <h4>No Payslips Found</h4>
                            <p>No payslips have been generated for this employee yet.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- Employee Information -->
                        <div class="row">
                            <div class="col-md-6">
                                <div class="info-card">
                                    <h5 class="card-title mb-3">
                                        <i class="fas fa-user me-2"></i>Employee Information
                                    </h5>
                                    <div class="row">
                                        <div class="col-sm-4"><strong>Employee ID:</strong></div>
                                        <div class="col-sm-8">${targetUser.employeeCode}</div>
                                    </div>
                                    <div class="row mt-2">
                                        <div class="col-sm-4"><strong>Full Name:</strong></div>
                                        <div class="col-sm-8">${targetUser.fullName}</div>
                                    </div>
                                    <div class="row mt-2">
                                        <div class="col-sm-4"><strong>Department:</strong></div>
                                        <div class="col-sm-8">
                                            <c:choose>
                                                <c:when test="${not empty department}">
                                                    ${department.name}
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted">Not specified</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                    <div class="row mt-2">
                                        <div class="col-sm-4"><strong>Position:</strong></div>
                                        <div class="col-sm-8">
                                            <c:choose>
                                                <c:when test="${not empty position}">
                                                    ${position.name}
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted">Not specified</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-6">
                                <div class="info-card">
                                    <h5 class="card-title mb-3">
                                        <i class="fas fa-calendar me-2"></i>Pay Period Information
                                    </h5>
                                    <c:choose>
                                        <c:when test="${not empty payslip}">
                                            <div class="row">
                                                <div class="col-sm-4"><strong>From Date:</strong></div>
                                                <div class="col-sm-8">${periodStartFormatted}</div>
                                            </div>
                                            <div class="row mt-2">
                                                <div class="col-sm-4"><strong>To Date:</strong></div>
                                                <div class="col-sm-8">${periodEndFormatted}</div>
                                            </div>
                                            <div class="row mt-2">
                                                <div class="col-sm-4"><strong>Status:</strong></div>
                                                <div class="col-sm-8">
                                                    <c:choose>
                                                        <c:when test="${payslip.status == 'GENERATED'}">
                                                            <span
                                                                class="badge bg-success badge-status">Generated</span>
                                                        </c:when>
                                                        <c:when test="${payslip.status == 'DRAFT'}">
                                                            <span
                                                                class="badge bg-warning badge-status">Draft</span>
                                                        </c:when>
                                                        <c:when test="${payslip.status == 'APPROVED'}">
                                                            <span
                                                                class="badge bg-primary badge-status">Approved</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span
                                                                class="badge bg-secondary badge-status">${payslip.status}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <c:if test="${payslip.isDirty}">
                                                        <span
                                                            class="badge bg-danger badge-status ms-1">Needs
                                                            Update</span>
                                                        </c:if>
                                                </div>
                                            </div>
                                            <c:if test="${not empty generatedAtFormatted}">
                                                <div class="row mt-2">
                                                    <div class="col-sm-4"><strong>Generated
                                                            Date:</strong></div>
                                                    <div class="col-sm-8">${generatedAtFormatted}</div>
                                                </div>
                                            </c:if>
                                        </c:when>
                                        <c:when test="${not empty calculationResult}">
                                            <div class="row">
                                                <div class="col-sm-4"><strong>From Date:</strong></div>
                                                <div class="col-sm-8">${calcPeriodStartFormatted}</div>
                                            </div>
                                            <div class="row mt-2">
                                                <div class="col-sm-4"><strong>To Date:</strong></div>
                                                <div class="col-sm-8">${calcPeriodEndFormatted}</div>
                                            </div>
                                            <div class="row mt-2">
                                                <div class="col-sm-4"><strong>Status:</strong></div>
                                                <div class="col-sm-8">
                                                    <span class="badge bg-info badge-status">Temporary
                                                        Calculation</span>
                                                </div>
                                            </div>
                                        </c:when>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>

                <!-- Salary Calculation Details -->
                <c:if test="${not empty calculationResult}">
                    <!-- Basic Salary Information -->
                    <div class="row">
                        <div class="col-12">
                            <div class="info-card">
                                <div class="section-header">
                                    <h5 class="mb-0">
                                        <i class="fas fa-money-check-alt me-2"></i>Basic Salary
                                        Information
                                    </h5>
                                </div>

                                <div class="row">
                                    <div class="col-md-4">
                                        <h6 class="text-primary mb-3">Salary Rates</h6>
                                        <div class="calculation-row">
                                            <div class="row">
                                                <div class="col-7">Base Salary/Month</div>
                                                <div class="col-5 text-end">
                                                    <fmt:formatNumber
                                                        value="${calculationResult.baseSalary}"
                                                        type="currency" currencySymbol=""
                                                        maxFractionDigits="0" /> VND
                                                </div>
                                            </div>
                                        </div>
                                        <div class="calculation-row">
                                            <div class="row">
                                                <div class="col-7">Daily Rate</div>
                                                <div class="col-5 text-end">
                                                    <fmt:formatNumber value="${calculationResult.dailyRate}"
                                                                      type="currency" currencySymbol=""
                                                                      maxFractionDigits="0" /> VND
                                                </div>
                                            </div>
                                        </div>
                                        <div class="calculation-row">
                                            <div class="row">
                                                <div class="col-7">Hourly Rate</div>
                                                <div class="col-5 text-end">
                                                    <fmt:formatNumber
                                                        value="${calculationResult.hourlyRate}"
                                                        type="currency" currencySymbol=""
                                                        maxFractionDigits="0" /> VND
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-md-4">
                                        <h6 class="text-primary mb-3">Working Days</h6>
                                        <div class="calculation-row">
                                            <div class="row">
                                                <div class="col-7">Work Days</div>
                                                <div class="col-5 text-end">
                                                    ${calculationResult.workedDays}
                                                    days</div>
                                            </div>
                                        </div>
                                        <div class="calculation-row">
                                            <div class="row">
                                                <div class="col-7">Paid Leave Days</div>
                                                <div class="col-5 text-end">
                                                    ${calculationResult.paidLeaveDays} days</div>
                                            </div>
                                        </div>
                                        <div class="calculation-row total-row">
                                            <div class="row">
                                                <div class="col-7">Total Paid Days</div>
                                                <div class="col-5 text-end">
                                                    ${calculationResult.totalPaidDays} days</div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-md-4">
                                        <h6 class="text-primary mb-3">Working Hours</h6>
                                        <div class="calculation-row">
                                            <div class="row">
                                                <div class="col-7">Work Hours</div>
                                                <div class="col-5 text-end">
                                                    <fmt:formatNumber
                                                        value="${calculationResult.workedHours}"
                                                        maxFractionDigits="1" /> hrs
                                                </div>
                                            </div>
                                        </div>
                                        <div class="calculation-row">
                                            <div class="row">
                                                <div class="col-7">Paid Leave Hours</div>
                                                <div class="col-5 text-end">
                                                    <fmt:formatNumber
                                                        value="${calculationResult.paidLeaveHours}"
                                                        maxFractionDigits="1" /> hrs
                                                </div>
                                            </div>
                                        </div>
                                        <div class="calculation-row total-row">
                                            <div class="row">
                                                <div class="col-7">Total Paid Hours</div>
                                                <div class="col-5 text-end">
                                                    <fmt:formatNumber
                                                        value="${calculationResult.totalActualHours}"
                                                        maxFractionDigits="1" /> hrs
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- Salary Components -->
                    <div class="row">
                        <div class="col-md-6">
                            <div class="info-card">
                                <div class="section-header">
                                    <h5 class="mb-0">
                                        <i class="fas fa-plus-circle me-2 text-success"></i>Earnings
                                    </h5>
                                </div>

                                <div class="calculation-row">
                                    <div class="row">
                                        <div class="col-8">Base Salary (Prorated)</div>
                                        <div class="col-4 text-end">
                                            <fmt:formatNumber value="${calculationResult.baseProrated}"
                                                              type="currency" currencySymbol="" maxFractionDigits="0" />
                                            VND
                                        </div>
                                    </div>
                                </div>

                                <c:if test="${calculationResult.otAmount > 0}">
                                    <div class="calculation-row">
                                        <div class="row">
                                            <div class="col-8">Overtime Pay</div>
                                            <div class="col-4 text-end text-success">
                                                +
                                                <fmt:formatNumber value="${calculationResult.otAmount}"
                                                                  type="currency" currencySymbol=""
                                                                  maxFractionDigits="0" />
                                                VND
                                            </div>
                                        </div>
                                    </div>

                                    <!-- OT Details -->
                                    <c:if test="${calculationResult.weekdayOTHours > 0}">
                                        <div class="calculation-row sub-item">
                                            <div class="row">
                                                <div class="col-8">• Weekday OT</div>
                                                <div class="col-4 text-end">
                                                    <fmt:formatNumber
                                                        value="${calculationResult.weekdayOTHours}"
                                                        maxFractionDigits="1" /> hrs
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
                                    <c:if test="${calculationResult.weekendOTHours > 0}">
                                        <div class="calculation-row sub-item">
                                            <div class="row">
                                                <div class="col-8">• Weekend OT</div>
                                                <div class="col-4 text-end">
                                                    <fmt:formatNumber
                                                        value="${calculationResult.weekendOTHours}"
                                                        maxFractionDigits="1" /> hrs
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
                                    <c:if test="${calculationResult.holidayOTHours > 0}">
                                        <div class="calculation-row sub-item">
                                            <div class="row">
                                                <div class="col-8">• Holiday OT</div>
                                                <div class="col-4 text-end">
                                                    <fmt:formatNumber
                                                        value="${calculationResult.holidayOTHours}"
                                                        maxFractionDigits="1" /> hrs
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
                                    <c:if test="${calculationResult.compensatoryOTHours > 0}">
                                        <div class="calculation-row sub-item">
                                            <div class="row">
                                                <div class="col-8">• Compensatory OT</div>
                                                <div class="col-4 text-end">
                                                    <fmt:formatNumber
                                                        value="${calculationResult.compensatoryOTHours}"
                                                        maxFractionDigits="1" /> hrs
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>

                                    <c:if test="${calculationResult.otAmount == 0}">
                                        <div class="text-center text-muted py-3">
                                            <i class="fas fa-info-circle me-2"></i>
                                            No overtime hours
                                        </div>
                                    </c:if>

                                    <div class="calculation-row total-row">
                                        <div class="row">
                                            <div class="col-8">Gross Income</div>
                                            <div class="col-4 text-end text-success">
                                                <fmt:formatNumber value="${calculationResult.grossAmount}"
                                                                  type="currency" currencySymbol=""
                                                                  maxFractionDigits="0" />
                                                VND
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                        <!-- Deductions -->
                        <div class="col-md-6">
                            <div class="info-card">
                                <div class="section-header">
                                    <h5 class="mb-0">
                                        <i class="fas fa-minus-circle me-2 text-danger"></i>Deductions
                                    </h5>
                                </div>

                                <c:set var="hasDeductions"
                                       value="${calculationResult.latenessDeduction > 0 || calculationResult.underHoursDeduction > 0 || calculationResult.taxAmount > 0}" />

                                <c:choose>
                                    <c:when test="${hasDeductions}">
                                        <c:if test="${calculationResult.latenessDeduction > 0}">
                                            <div class="calculation-row">
                                                <div class="row">
                                                    <div class="col-8">Lateness Deduction</div>
                                                    <div class="col-4 text-end deduction-highlight">
                                                        -
                                                        <fmt:formatNumber
                                                            value="${calculationResult.latenessDeduction}"
                                                            type="currency" currencySymbol=""
                                                            maxFractionDigits="0" /> VND
                                                    </div>
                                                </div>
                                            </div>
                                            <c:if test="${calculationResult.totalLateMinutes > 0}">
                                                <div class="calculation-row sub-item">
                                                    <div class="row">
                                                        <div class="col-8">• Total late minutes</div>
                                                        <div class="col-4 text-end">
                                                            ${calculationResult.totalLateMinutes} mins
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </c:if>

                                        <c:if test="${calculationResult.underHoursDeduction > 0}">
                                            <div class="calculation-row">
                                                <div class="row">
                                                    <div class="col-8">Under Hours Deduction</div>
                                                    <div class="col-4 text-end deduction-highlight">
                                                        -
                                                        <fmt:formatNumber
                                                            value="${calculationResult.underHoursDeduction}"
                                                            type="currency" currencySymbol=""
                                                            maxFractionDigits="0" /> VND
                                                    </div>
                                                </div>
                                            </div>
                                            <c:if test="${calculationResult.underHours > 0}">
                                                <div class="calculation-row sub-item">
                                                    <div class="row">
                                                        <div class="col-8">• Hours short</div>
                                                        <div class="col-4 text-end">
                                                            <fmt:formatNumber
                                                                value="${calculationResult.underHours}"
                                                                maxFractionDigits="1" /> hrs
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="calculation-row sub-item">
                                                    <div class="row">
                                                        <div class="col-8">• Required hours</div>
                                                        <div class="col-4 text-end">
                                                            <fmt:formatNumber
                                                                value="${calculationResult.requiredHours}"
                                                                maxFractionDigits="1" /> hrs
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </c:if>

                                        <c:if test="${calculationResult.taxAmount > 0}">
                                            <div class="calculation-row">
                                                <div class="row">
                                                    <div class="col-8">Personal Income Tax</div>
                                                    <div class="col-4 text-end deduction-highlight">
                                                        -
                                                        <fmt:formatNumber
                                                            value="${calculationResult.taxAmount}"
                                                            type="currency" currencySymbol=""
                                                            maxFractionDigits="0" /> VND
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>

                                        <c:set var="totalDeductions"
                                               value="${calculationResult.latenessDeduction + calculationResult.underHoursDeduction + calculationResult.taxAmount}" />
                                        <div class="calculation-row total-row">
                                            <div class="row">
                                                <div class="col-8">Total Deductions</div>
                                                <div class="col-4 text-end deduction-highlight">
                                                    -
                                                    <fmt:formatNumber value="${totalDeductions}"
                                                                      type="currency" currencySymbol=""
                                                                      maxFractionDigits="0" /> VND
                                                </div>
                                            </div>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center text-muted py-5">
                                            <i class="fas fa-check-circle fa-2x mb-3"></i>
                                            <h6>No Deductions</h6>
                                            <p class="mb-0">Employee has no salary deductions this
                                                period
                                            </p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Final Amount -->
                <div class="row">
                    <div class="col-12">
                        <div class="info-card">
                            <div class="section-header">
                                <h5 class="mb-0">
                                    <i class="fas fa-money-bill-wave me-2"></i>Salary Summary
                                </h5>
                            </div>

                            <div class="row">
                                <div class="col-md-8">
                                    <div class="calculation-row">
                                        <div class="row">
                                            <div class="col-6">Gross Income</div>
                                            <div class="col-6 text-end">
                                                <fmt:formatNumber value="${calculationResult.grossAmount}"
                                                                  type="currency" currencySymbol="" maxFractionDigits="0" />
                                                VND
                                            </div>
                                        </div>
                                    </div>

                                    <c:set var="totalDeductions"
                                           value="${calculationResult.latenessDeduction + calculationResult.underHoursDeduction + calculationResult.taxAmount}" />
                                    <c:if test="${totalDeductions > 0}">
                                        <div class="calculation-row">
                                            <div class="row">
                                                <div class="col-6">Total Deductions</div>
                                                <div class="col-6 text-end deduction-highlight">
                                                    -
                                                    <fmt:formatNumber value="${totalDeductions}" type="currency"
                                                                      currencySymbol="" maxFractionDigits="0" /> VND
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
                                </div>

                                <div class="col-md-4">
                                    <div class="card bg-success text-white">
                                        <div class="card-body text-center">
                                            <h6 class="card-title mb-2">Net Salary</h6>
                                            <h3 class="mb-0">
                                                <fmt:formatNumber value="${calculationResult.netAmount}"
                                                                  type="currency" currencySymbol="" maxFractionDigits="0" />
                                                VND
                                            </h3>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/detail-payslip.js"></script>
    </body>
</html>