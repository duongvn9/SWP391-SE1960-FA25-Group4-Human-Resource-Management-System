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
        <!-- Print CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/payslip-print.css">

        <!-- Custom CSS for Earnings/Deductions Layout -->
        <style>
            /* Prevent horizontal scroll globally */
            html, body {
                overflow-x: hidden !important;
                max-width: 100% !important;
            }

            .main-content {
                overflow-x: hidden !important;
                max-width: 100% !important;
            }

            .content-area {
                overflow-x: hidden !important;
                max-width: 100% !important;
            }

            /* Fix container widths */
            .container, .container-fluid {
                max-width: 100% !important;
                overflow-x: hidden !important;
            }

            /* Fix for earnings and deductions side by side */
            .earnings-deductions {
                display: flex !important;
                gap: 15px !important;
                margin-left: 0 !important;
                margin-right: 0 !important;
                width: 100% !important;
                max-width: 100% !important;
                box-sizing: border-box !important;
            }

            .earnings-deductions .col-md-6 {
                flex: 1 !important;
                max-width: calc(50% - 7.5px) !important;
                width: calc(50% - 7.5px) !important;
                padding-left: 0 !important;
                padding-right: 0 !important;
                box-sizing: border-box !important;
            }

            /* Ensure info cards have proper height and content visibility */
            .earnings-deductions .info-card {
                min-height: 300px !important;
                height: auto !important;
                overflow: visible !important;
                word-wrap: break-word !important;
            }

            /* Fix calculation rows to prevent overflow */
            .calculation-row {
                margin-bottom: 8px !important;
                overflow: visible !important;
                word-wrap: break-word !important;
            }

            .calculation-row .row {
                margin: 0 !important;
                overflow: visible !important;
            }

            .calculation-row .col-8,
            .calculation-row .col-4 {
                padding: 2px 5px !important;
                overflow: visible !important;
                word-wrap: break-word !important;
            }

            /* Fix info cards */
            .info-card {
                width: 100% !important;
                max-width: 100% !important;
                box-sizing: border-box !important;
                overflow: hidden !important;
            }

            /* Fix all rows */
            .row {
                margin-left: 0 !important;
                margin-right: 0 !important;
                max-width: 100% !important;
            }

            /* Fix columns */
            [class*="col-"] {
                padding-left: 7.5px !important;
                padding-right: 7.5px !important;
                box-sizing: border-box !important;
            }

            /* Ensure deductions section shows all content */
            .earnings-deductions .info-card .section-header {
                margin-bottom: 15px !important;
            }

            .deduction-highlight {
                color: #dc3545 !important;
                font-weight: 500 !important;
            }

            .sub-item {
                padding-left: 15px !important;
                font-size: 0.9em !important;
                color: #666 !important;
            }

            .total-row {
                border-top: 2px solid #dee2e6 !important;
                padding-top: 8px !important;
                margin-top: 10px !important;
                font-weight: bold !important;
            }

            /* Responsive adjustments */
            @media (max-width: 768px) {
                .earnings-deductions {
                    flex-direction: column !important;
                    gap: 10px !important;
                }

                .earnings-deductions .col-md-6 {
                    max-width: 100% !important;
                    width: 100% !important;
                }

                .earnings-deductions .info-card {
                    min-height: auto !important;
                }
            }

            /* Fix specific elements that might cause overflow */
            .payslip-header, .breadcrumb, .section-header {
                max-width: 100% !important;
                overflow: hidden !important;
            }

            /* Hide any default browser print buttons */
            button[onclick*="print"]:not(.print-btn),
            button[onclick*="window.print"]:not(.print-btn),
            input[onclick*="print"]:not(.print-btn) {
                display: none !important;
            }

            /* Hide any floating print buttons */
            .print-button, .floating-print, .print-widget, .print-icon,
            [class*="print-"]:not(.print-btn):not(.print-css) {
                display: none !important;
            }

            /* Hide buttons with print text that are not our main button */
            button:not(.print-btn):contains("Print"),
            a:not(.print-btn):contains("Print") {
                display: none !important;
            }

            /* Ensure only our print button is visible */
            .payslip-header .print-btn {
                display: inline-block !important;
            }
        </style>
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
                            <<<<<<< HEAD
                            <button onclick="printPayslip()" class="btn btn-primary me-2 print-btn">
                                <i class="fas fa-print me-1"></i> Print Payslip
                            </button>
                            =======
                            >>>>>>> 9b2a1a2f081093d4ad887872b4d7301bd80ed0c7
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
                        <<<<<<< HEAD
                        <!-- Printable Content Start -->
                        <div id="printable-content">
                            =======
                            >>>>>>> 9b2a1a2f081093d4ad887872b4d7301bd80ed0c7
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
                                                    <div class="col-7">Standard Working Days</div>
                                                    <div class="col-5 text-end">
                                                        <c:choose>
                                                            <c:when test="${not empty standardWorkingDays}">
                                                                ${standardWorkingDays} days
                                                            </c:when>
                                                            <c:otherwise>
                                                                22 days
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="calculation-row">
                                                <div class="row">
                                                    <div class="col-7">Actual Work Days</div>
                                                    <div class="col-5 text-end">
                                                        <c:choose>
                                                            <c:when test="${not empty actualWorkingDays}">
                                                                <span class="${actualWorkingDays > standardWorkingDays ? 'text-success fw-bold' : actualWorkingDays < standardWorkingDays ? 'text-warning' : ''}">
                                                                    ${actualWorkingDays} days
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="${calculationResult.workedDays > 22 ? 'text-success fw-bold' : calculationResult.workedDays < 22 ? 'text-warning' : ''}">
                                                                    ${calculationResult.workedDays} days
                                                                </span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="calculation-row">
                                                <div class="row">
                                                    <div class="col-7">Paid Leave Days</div>
                                                    <div class="col-5 text-end">
                                                        ${calculationResult.paidLeaveDays} days</div>
                                                </div>
                                            </div>
                                            <div class="calculation-row">
                                                <div class="row">
                                                    <div class="col-7">Days Attendance Rate</div>
                                                    <div class="col-5 text-end">
                                                        <c:choose>
                                                            <c:when test="${not empty workingDaysRatio}">
                                                                <span class="badge ${actualWorkingDays >= standardWorkingDays * 0.9 ? 'bg-success' : actualWorkingDays >= standardWorkingDays * 0.8 ? 'bg-warning' : 'bg-danger'}">
                                                                    ${workingDaysRatio}
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-secondary">N/A</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
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
                                            <div class="calculation-row">
                                                <div class="row">
                                                    <div class="col-7">Hours Completion Rate</div>
                                                    <div class="col-5 text-end">
                                                        <c:choose>
                                                            <c:when test="${not empty workingHoursRatio}">
                                                                <span class="badge ${workingHoursRatio >= '95.0%' ? 'bg-success' : workingHoursRatio >= '85.0%' ? 'bg-warning' : 'bg-danger'}">
                                                                    ${workingHoursRatio}
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-secondary">N/A</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </div>
                                            <c:if test="${not empty avgHoursPerDay}">
                                                <div class="calculation-row">
                                                    <div class="row">
                                                        <div class="col-7">Avg Hours/Day</div>
                                                        <div class="col-5 text-end">
                                                            <span class="${avgHoursPerDay < '7.0' ? 'text-warning' : avgHoursPerDay >= '8.0' ? 'text-success' : ''}">
                                                                ${avgHoursPerDay} hrs
                                                            </span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:if>
                                            <c:if test="${actualWorkingDays > standardWorkingDays && calculationResult.underHours > 0}">
                                                <div class="calculation-row">
                                                    <div class="row">
                                                        <div class="col-12">
                                                            <small class="text-info">
                                                                <i class="fas fa-info-circle"></i>
                                                                More days worked but fewer hours per day (${avgHoursPerDay}h/day avg)
                                                            </small>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:if>
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
                        <div class="row earnings-deductions">
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
                    <div class="row salary-summary">
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
                                                <h3 class="mb-0" id="net-salary-amount">
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
                    </div>
                    <!-- Printable Content End -->
                </div>
            </c:if>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/detail-payslip.js"></script>

    <!-- Fix Net Salary Display -->
    <script>
                                document.addEventListener('DOMContentLoaded', function () {
                                    // Debug: Log the net amount value
                                    console.log('Net Amount from server:', '${calculationResult.netAmount}');

                                    // Only fix if the display is clearly wrong (like showing just "38 VND")
                                    const netSalaryElement = document.getElementById('net-salary-amount');
                                    if (netSalaryElement) {
                                        const currentText = netSalaryElement.textContent.trim();
                                        console.log('Current net salary display:', currentText);

                                        // Check if it's showing an obviously wrong value (less than 1000 VND)
                                        const currentValue = parseFloat(currentText.replace(/[^\d]/g, ''));
                                        if (currentValue < 1000) {
                                            const netAmount = '${calculationResult.netAmount}';
                                            if (netAmount && netAmount !== '' && netAmount !== 'null' && !isNaN(netAmount)) {
                                                const formattedAmount = parseFloat(netAmount).toLocaleString('vi-VN');
                                                netSalaryElement.innerHTML = formattedAmount + ' VND';
                                                console.log('Fixed net salary to:', formattedAmount + ' VND');
                                            }
                                        }
                                    }
                                });
    </script>


    <!-- Print JavaScript -->
    <script>
        function printPayslip() {
            // Get employee name and period for header
            const employeeName = '${targetUser.fullName}' || 'Employee';
            const periodDisplay = '${periodDisplay}' || 'N/A';
            const employeeCode = '${targetUser.employeeCode}' || 'N/A';

            // Get the printable content (KHÔNG thay đổi nội dung gốc)
            const printContent = document.getElementById('printable-content');
            const contentToPrint = printContent.innerHTML;

            // Tạo cửa sổ mới để in
            const printWindow = window.open('', '_blank', 'width=800,height=600');

            // Tạo HTML hoàn chỉnh cho cửa sổ in
            const printHTML = `
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Payslip - ` + employeeName + ` - ` + periodDisplay + `</title>
                    <meta charset="UTF-8">
                    <style>
                        /* Print styles */
                        body {
                            font-family: Arial, sans-serif;
                            margin: 0;
                            padding: 20px;
                            color: black;
                            background: white;
                            min-height: 100vh;
                            overflow: visible;
                        }
                            
                        /* Ensure all content is visible */
                        html, body {
                            height: auto !important;
                            min-height: auto !important;
                            max-height: none !important;
                        }
                            
                        .info-card {
                            border: 1px solid #333;
                            margin-bottom: 15px;
                            padding: 15px;
                            page-break-inside: avoid;
                        }
                            
                        .section-header h5 {
                            font-size: 14px;
                            font-weight: bold;
                            margin-bottom: 10px;
                            border-bottom: 1px solid #333;
                            padding-bottom: 5px;
                        }
                            
                        .calculation-row {
                            margin-bottom: 5px;
                            font-size: 12px;
                        }
                            
                        .total-row {
                            border-top: 1px solid #333;
                            padding-top: 5px;
                            font-weight: bold;
                        }
                            
                        .row {
                            display: flex;
                            margin: 0;
                        }
                            
                        .col-md-6 {
                            width: 48%;
                            float: left;
                            margin-right: 2%;
                            box-sizing: border-box;
                        }
                            
                        /* Earnings and Deductions side by side */
                        .earnings-deductions {
                            display: block !important;
                            width: 100% !important;
                            clear: both !important;
                            margin-top: 20px !important;
                            page-break-inside: avoid !important;
                        }
                            
                        /* Clean side-by-side layout for Earnings and Deductions */
                        .earnings-deductions {
                            display: table !important;
                            width: 100% !important;
                            table-layout: fixed !important;
                            border-collapse: separate !important;
                            border-spacing: 15px 0 !important;
                            margin-top: 20px !important;
                        }
                            
                        .earnings-deductions .col-md-6 {
                            display: table-cell !important;
                            vertical-align: top !important;
                            width: 50% !important;
                            padding: 0 !important;
                            margin: 0 !important;
                            box-sizing: border-box !important;
                        }
                            
                        /* Equal width for both columns */
                        .earnings-deductions .col-md-6:first-child {
                            width: 40% !important;
                        }
                            
                        .earnings-deductions .col-md-6:last-child {
                            width: 60% !important;
                        }
                            
                        /* Cards with proper styling */
                        .earnings-deductions .info-card {
                            height: auto !important;
                            min-height: 120px !important;
                            width: 100% !important;
                            padding: 15px !important;
                            border: 1px solid #333 !important;
                            box-sizing: border-box !important;
                            overflow: visible !important;
                            background: white !important;
                        }
                            
                        /* Section headers */
                        .earnings-deductions .section-header h5 {
                            font-size: 14px !important;
                            font-weight: bold !important;
                            margin-bottom: 10px !important;
                            border-bottom: 1px solid #333 !important;
                            padding-bottom: 5px !important;
                        }
                            
                        .earnings-deductions::after {
                            content: "";
                            display: table;
                            clear: both;
                        }
                            
                        /* Ensure all sections are visible */
                        .earnings-deductions .info-card {
                            display: block !important;
                            visibility: visible !important;
                            opacity: 1 !important;
                        }
                            
                        .col-md-4 {
                            width: 32%;
                            float: left;
                            margin-right: 2%;
                            box-sizing: border-box;
                            display: inline-block;
                            vertical-align: top;
                        }
                            
                        /* Ensure 3-column layout for Basic Salary Information */
                        .row .col-md-4 {
                            width: 32% !important;
                            float: left !important;
                            margin-right: 2% !important;
                            display: block !important;
                        }
                            
                        .row .col-md-4:last-child {
                            margin-right: 0 !important;
                        }
                            
                        .col-md-8 {
                            width: 65%;
                            float: left;
                            margin-right: 2%;
                        }
                            
                        .col-6 {
                            width: 48%;
                            float: left;
                        }
                            
                        .col-7 {
                            width: 58%;
                            float: left;
                        }
                            
                        .col-5 {
                            width: 40%;
                            float: right;
                        }
                            
                        .col-8 {
                            width: 65%;
                            float: left;
                        }
                            
                        .col-4 {
                            width: 33%;
                            float: right;
                        }
                            
                        .text-end {
                            text-align: right;
                        }
                            
                        .clearfix::after {
                            content: "";
                            display: table;
                            clear: both;
                        }
                            
                        .card {
                            border: 1px solid #333;
                            background-color: white;
                            padding: 10px;
                        }
                            
                        .badge {
                            border: 1px solid #333;
                            background-color: white;
                            color: black;
                            padding: 2px 6px;
                            font-size: 10px;
                        }
                            
                        .fas, .fa {
                            display: none;
                        }
                            
                        h2 { font-size: 18px; }
                        h3 { font-size: 16px; }
                        h4 { font-size: 14px; }
                        h5 { font-size: 13px; }
                        h6 { font-size: 12px; }
                            
                        .deduction-highlight {
                            color: black;
                        }
                            
                        .text-success, .text-warning, .text-danger, .text-info {
                            color: black !important;
                        }
                            
                        /* Salary Summary specific styling - FORCE VISIBILITY */
                        .salary-summary {
                            display: block !important;
                            visibility: visible !important;
                            opacity: 1 !important;
                            width: 100% !important;
                            clear: both !important;
                            margin-top: 20px !important;
                            page-break-inside: avoid !important;
                            border: 1px solid #333 !important;
                            padding: 15px !important;
                        }
                            
                        .salary-summary .col-md-8 {
                            width: 65% !important;
                            float: left !important;
                            margin-right: 2% !important;
                        }
                            
                        .salary-summary .col-md-4 {
                            width: 33% !important;
                            float: right !important;
                            margin-right: 0 !important;
                        }
                            
                        /* Net salary card - make it prominent */
                        .salary-summary .card {
                            border: 2px solid #333 !important;
                            background-color: #f8f9fa !important;
                            color: black !important;
                            padding: 15px !important;
                            text-align: center !important;
                        }
                            
                        .salary-summary .card-body {
                            padding: 10px !important;
                        }
                            
                        .salary-summary .card-title {
                            font-size: 14px !important;
                            font-weight: bold !important;
                            margin-bottom: 10px !important;
                            color: black !important;
                        }
                            
                        .salary-summary h3 {
                            font-size: 20px !important;
                            font-weight: bold !important;
                            margin: 0 !important;
                            color: black !important;
                        }
                            
                        /* Ensure salary summary is visible */
                        .salary-summary .info-card {
                            display: block !important;
                            visibility: visible !important;
                            opacity: 1 !important;
                            border: 1px solid #333 !important;
                            margin-bottom: 0 !important;
                        }
                            
                        /* Force all major sections to be visible */
                        .earnings-deductions, .salary-summary, .info-card {
                            display: block !important;
                            visibility: visible !important;
                            opacity: 1 !important;
                            height: auto !important;
                            max-height: none !important;
                            overflow: visible !important;
                        }
                            
                        /* Ensure proper spacing and no content cutoff */
                        .calculation-row {
                            margin-bottom: 8px !important;
                            line-height: 1.4 !important;
                            overflow: visible !important;
                        }
                            
                        /* Fix text wrapping issues */
                        .col-8, .col-7, .col-6 {
                            word-wrap: break-word !important;
                            overflow-wrap: break-word !important;
                        }
                            
                        /* Ensure deduction details are fully visible */
                        .sub-item {
                            font-size: 11px !important;
                            margin-left: 10px !important;
                            margin-bottom: 5px !important;
                        }
                            
                        /* Force all important sections to be visible */
                        .row, .col-md-4, .col-md-6, .col-md-8, .col-md-12 {
                            display: block !important;
                            visibility: visible !important;
                            opacity: 1 !important;
                        }
                            
                        /* Ensure no content is hidden */
                        * {
                            max-height: none !important;
                            overflow: visible !important;
                        }
                            
                        /* Page breaks for better printing */
                        .earnings-deductions {
                            page-break-before: auto !important;
                            page-break-after: auto !important;
                            page-break-inside: avoid !important;
                        }
                            
                        @media print {
                            body { 
                                margin: 0; 
                                font-size: 12px;
                            }
                            .info-card { 
                                page-break-inside: avoid; 
                                margin-bottom: 15px !important;
                            }
                            .salary-summary { 
                                page-break-inside: avoid !important; 
                                page-break-before: auto !important;
                            }
                            .earnings-deductions {
                                page-break-inside: avoid !important;
                                page-break-before: auto !important;
                            }
                                
                            /* Ensure all content fits on page */
                            .row {
                                page-break-inside: avoid;
                            }
                        }
                    </style>
                </head>
                <body>
                    <!-- Company Header -->
                    <div style="text-align: center; margin-bottom: 30px; border-bottom: 2px solid #333; padding-bottom: 15px;">
                        <h2 style="margin: 0; font-size: 24px; font-weight: bold;">HRMS COMPANY</h2>
                        <p style="margin: 5px 0; font-size: 12px;">Address: 123 Business Street, Ho Chi Minh City, Vietnam</p>
                        <p style="margin: 5px 0; font-size: 12px;">Phone: (84) 28-1234-5678 | Email: hr@hrms-company.com</p>
                        <h3 style="margin: 20px 0 10px 0; font-size: 18px; font-weight: bold;">SALARY SLIP</h3>
                        <div style="display: flex; justify-content: space-between; margin: 10px 0; font-size: 12px;">
                            <span><strong>Employee:</strong> ` + employeeName + ` (` + employeeCode + `)</span>
                            <span><strong>Period:</strong> ` + periodDisplay + `</span>
                        </div>
                        <div style="font-size: 11px; color: #666; margin-top: 10px;">
                            Generated on: ` + new Date().toLocaleDateString('en-GB') + `
                        </div>
                    </div>
                        
                    <!-- Payslip Content -->
                    <div class="clearfix" style="width: 100%; min-height: 500px;">
                        ` + contentToPrint + `
                    </div>
                        

                        
                    <!-- Debug: Ensure all sections are included -->
                    <div style="margin-top: 20px; font-size: 10px; color: #666; text-align: center;">
                        Complete payslip generated on ` + new Date().toLocaleDateString('en-GB') + `
                    </div>
                        
                    <!-- Footer -->
                    <div style="margin-top: 40px; padding-top: 20px; border-top: 1px solid #333; font-size: 11px; text-align: center;">
                        <p style="margin: 5px 0;">This is a computer-generated payslip. No signature required.</p>
                        <p style="margin: 5px 0;">For any queries, please contact HR Department.</p>
                    </div>
                </body>
                </html>
            `;

            // Ghi nội dung vào cửa sổ mới
            printWindow.document.write(printHTML);
            printWindow.document.close();

            // Đợi load xong rồi in
            printWindow.onload = function () {
                // Ensure Salary Summary is visible and has correct values
                const salarySection = printWindow.document.querySelector('.salary-summary');
                if (salarySection) {
                    salarySection.style.display = 'block';
                    salarySection.style.visibility = 'visible';
                    salarySection.style.pageBreakInside = 'avoid';
                    salarySection.style.marginTop = '20px';

                    // Update Salary Summary with extracted values
                    const allSpans = salarySection.querySelectorAll('*');
                    let grossIncomeRow = null;
                    let deductionsRow = null;

                    for (let span of allSpans) {
                        if (span.textContent.includes('Gross Income')) {
                            grossIncomeRow = span;
                        }
                        if (span.textContent.includes('Total Deductions')) {
                            deductionsRow = span;
                        }
                    }

                    const netSalaryCard = salarySection.querySelector('.card');

                    // Update values if found
                    if (grossIncomeRow && grossAmount !== 'N/A') {
                        const amountSpan = grossIncomeRow.parentElement.querySelector('.text-end, .col-6:last-child');
                        if (amountSpan)
                            amountSpan.textContent = grossAmount;
                    }

                    if (deductionsRow && totalDeductions !== 'N/A') {
                        const amountSpan = deductionsRow.parentElement.querySelector('.text-end, .col-6:last-child');
                        if (amountSpan)
                            amountSpan.textContent = totalDeductions;
                    }

                    if (netSalaryCard && netAmount !== 'N/A') {
                        const h3 = netSalaryCard.querySelector('h3');
                        if (h3)
                            h3.textContent = netAmount;

                        netSalaryCard.style.border = '2px solid #333';
                        netSalaryCard.style.backgroundColor = '#f8f9fa';
                        netSalaryCard.style.color = 'black';
                    }
                }

                // Get salary values from page content with better regex
                let grossAmount = 'N/A';
                let deductionsAmount = 'N/A';
                let netAmount = 'N/A';

                // Extract from page text content
                const pageText = printWindow.document.body.textContent;

                // Find Base Salary (Prorated) - look for full number including dots/commas
                const grossMatch = pageText.match(/Base Salary \(Prorated\)[\s\S]*?([\d]{1,3}(?:[,.][\d]{3})*(?:\.[\d]+)?)\s*VND/);
                if (grossMatch) {
                    grossAmount = grossMatch[1] + ' VND';
                }

                // Find Total Deductions - look for full number
                const deductMatch = pageText.match(/Total Deductions[\s\S]*?-?\s*([\d]{1,3}(?:[,.][\d]{3})*(?:\.[\d]+)?)\s*VND/);
                if (deductMatch) {
                    deductionsAmount = deductMatch[1] + ' VND';
                }

                // Try alternative patterns if not found
                if (grossAmount === 'N/A') {
                    // Look for any large VND amount (likely gross)
                    const allAmounts = pageText.match(/([\d]{2,3}(?:[,.][\d]{3})*(?:\.[\d]+)?)\s*VND/g);
                    if (allAmounts && allAmounts.length > 0) {
                        // Take the largest amount as gross
                        let maxAmount = 0;
                        let maxAmountStr = '';
                        allAmounts.forEach(amount => {
                            const num = parseFloat(amount.replace(/[^\d.]/g, ''));
                            if (num > maxAmount) {
                                maxAmount = num;
                                maxAmountStr = amount;
                            }
                        });
                        if (maxAmountStr)
                            grossAmount = maxAmountStr;
                    }
                }

                // Calculate Net Salary
                if (grossAmount !== 'N/A' && deductionsAmount !== 'N/A') {
                    try {
                        // Remove all non-digit characters except dots and commas, then parse
                        const grossStr = grossAmount.replace(/[^\d.,]/g, '').replace(/,/g, '');
                        const deductStr = deductionsAmount.replace(/[^\d.,]/g, '').replace(/,/g, '');

                        const gross = parseFloat(grossStr);
                        const deduct = parseFloat(deductStr);

                        if (!isNaN(gross) && !isNaN(deduct)) {
                            const net = gross - deduct;
                            netAmount = Math.round(net).toLocaleString('vi-VN') + ' VND';
                        } else {
                            netAmount = 'Calculation Error';
                        }
                    } catch (e) {
                        console.error('Net salary calculation error:', e);
                        netAmount = 'Calculation Error';
                    }
                }

                // Update summary immediately
                updateSummarySection();

                function updateSummarySection() {
                    // Try to update existing summary first
                    let summarySection = printWindow.document.querySelector('.salary-summary');

                    // If not found, try backup summary
                    if (!summarySection) {
                        summarySection = printWindow.document.querySelector('.salary-summary-backup');
                    }

                    if (summarySection) {
                        // Update Gross Income
                        const grossRow = Array.from(summarySection.querySelectorAll('*')).find(el =>
                            el.textContent.includes('Gross Income'));
                        if (grossRow) {
                            const parent = grossRow.closest('.calculation-row, .row');
                            if (parent) {
                                const amountCell = parent.querySelector('.text-end, strong');
                                if (amountCell)
                                    amountCell.textContent = grossAmount;
                            }
                        }

                        // Update Total Deductions
                        const deductRow = Array.from(summarySection.querySelectorAll('*')).find(el =>
                            el.textContent.includes('Total Deductions'));
                        if (deductRow) {
                            const parent = deductRow.closest('.calculation-row, .row');
                            if (parent) {
                                const amountCell = parent.querySelector('.text-end, strong');
                                if (amountCell)
                                    amountCell.textContent = '-' + deductionsAmount;
                            }
                        }

                        // Update Net Salary card
                        const netCard = summarySection.querySelector('h3');
                        if (netCard)
                            netCard.textContent = netAmount;
                    }
                }

                // Add Salary Summary if missing with JSP values
                if (!salarySection) {
                    const content = printWindow.document.querySelector('.clearfix');
                    if (content) {
                        const summaryHTML = '<div class="salary-summary-backup" style="margin-top: 30px; border: 2px solid #333; padding: 15px; page-break-inside: avoid;">' +
                                '<h4 style="text-align: center; margin-bottom: 15px; border-bottom: 1px solid #333; padding-bottom: 10px;">SALARY SUMMARY</h4>' +
                                '<div style="display: flex; justify-content: space-between; align-items: center;">' +
                                '<div style="width: 65%;">' +
                                '<div class="calculation-row" style="margin-bottom: 10px; display: flex; justify-content: space-between;">' +
                                '<span>Gross Income:</span>' +
                                '<span class="text-end"><strong>' + grossAmount + '</strong></span>' +
                                '</div>' +
                                '<div class="calculation-row" style="margin-bottom: 10px; display: flex; justify-content: space-between;">' +
                                '<span>Total Deductions:</span>' +
                                '<span class="text-end"><strong>-' + deductionsAmount + '</strong></span>' +
                                '</div>' +
                                '</div>' +
                                '<div style="width: 30%; text-align: center; border: 2px solid #333; padding: 15px; background-color: #f8f9fa;">' +
                                '<div style="font-size: 14px; font-weight: bold; margin-bottom: 5px;">Net Salary</div>' +
                                '<h3 style="font-size: 18px; font-weight: bold; margin: 0;">' + netAmount + '</h3>' +
                                '</div>' +
                                '</div>' +
                                '</div>';
                        content.insertAdjacentHTML('beforeend', summaryHTML);
                    }
                }

                printWindow.focus();
                printWindow.print();
                // Đóng cửa sổ sau khi in (hoặc cancel)
                printWindow.close();
            };
        }

        // Add print shortcut (Ctrl+P)
        document.addEventListener('keydown', function (e) {
            if (e.ctrlKey && e.key === 'p') {
                e.preventDefault();
                printPayslip();
            }
        });

        // Remove any unwanted print buttons that might be added by other scripts
        document.addEventListener('DOMContentLoaded', function () {
            // Wait a bit for other scripts to load
            setTimeout(function () {
                // Find and remove unwanted print buttons
                const unwantedPrintButtons = document.querySelectorAll(
                        'button:not(.print-btn)[onclick*="print"], ' +
                        'button:not(.print-btn)[onclick*="window.print"], ' +
                        'a:not(.print-btn)[onclick*="print"], ' +
                        '.print-button:not(.print-btn), ' +
                        '.floating-print:not(.print-btn)'
                        );

                unwantedPrintButtons.forEach(function (button) {
                    // Check if it's not our main print button
                    if (!button.classList.contains('print-btn')) {
                        button.style.display = 'none';
                        button.remove();
                    }
                });

                // Also check for buttons with "Print" text that are not our button
                const allButtons = document.querySelectorAll('button, a');
                allButtons.forEach(function (button) {
                    if (button.textContent.includes('Print') &&
                            !button.classList.contains('print-btn') &&
                            !button.textContent.includes('Print Payslip')) {
                        button.style.display = 'none';
                        button.remove();
                    }
                });
            }, 1000);
        });
    </script>
</body>
</html>