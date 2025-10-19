<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="jakarta.tags.core" prefix="m" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>My Attendance</title>  
        <jsp:include page="../layout/head.jsp">
            <jsp:param name="pageTitle" value="attendance-record-emp" />
        </jsp:include>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/attendance-record-emp.css">
    </head>
    <body class="attendance-page">
        <div class="page-wrapper">
            <jsp:include page="../layout/dashboard-header.jsp">
                <jsp:param name="pageTitle" value="attendance-record-emp" />
            </jsp:include>

            <div class="main-container">
                <jsp:include page="../layout/sidebar.jsp">
                    <jsp:param name="currentPage" value="attendance-record-emp" />
                </jsp:include>

                <main class="main-content">
                    <h2 class="page-title">My Attendance Records</h2>

                    <!-- Filter Form -->
                    <form id="filterForm" class="filter-form" method="post" action="${pageContext.request.contextPath}/attendance/record/emp">
                        <div class="filter-group">
                            <label for="startDate" class="filter-label">From:</label>
                            <input type="date" id="startDate" name="startDate" value="${startDate}" class="filter-input">
                        </div>

                        <div class="filter-group">
                            <label for="endDate" class="filter-label">To:</label>
                            <input type="date" id="endDate" name="endDate" value="${endDate}" class="filter-input">
                        </div>

                        <div class="filter-group">
                            <label for="status" class="filter-label">Status:</label>
                            <select id="status" name="status" class="filter-select">
                                <option value="">All</option>
                                <option value="On time" ${status == 'On time' ? 'selected' : ''}>On time</option>
                                <option value="Late" ${status == 'Late' ? 'selected' : ''}>Late</option>
                            </select>
                        </div>

                        <div class="filter-group">
                            <label for="source" class="filter-label">Source:</label>
                            <select id="source" name="source" class="filter-select">
                                <option value="">All</option>
                                <option value="Manual" ${source == 'Manual' ? 'selected' : ''}>Manual</option>
                                <option value="Import" ${source == 'Import' ? 'selected' : ''}>Import</option>
                            </select>
                        </div>

                        <div class="filter-group">
                            <label for="periodSelect" class="filter-label">Select Period:</label>
                            <select id="periodSelect" name="periodSelect" class="filter-select">
                                <option value="">-- All Periods --</option>
                                <c:forEach var="p" items="${periodList}">
                                    <option value="${p.id}" ${selectedPeriod == p.id ? 'selected' : ''}>${p.name}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="filter-actions">
                            <button type="submit" class="btn btn-filter">Filter</button>
                            <button type="submit" name="action" value="reset" class="btn btn-reset">Reset</button>
                        </div>

                        <!-- Hidden input lưu các record đã chọn -->
                        <input type="hidden" id="selectedLogDates" name="selected_log_dates" value="${selectedLogDates}">
                    </form>
                    <br/>

                    <!-- Export Form (ẩn, dùng JS để submit) -->
                    <form id="exportForm" class="export-form" action="${pageContext.request.contextPath}/attendance/record/emp" method="post">
                        <input type="hidden" name="exportType" id="exportType">
                        <input type="hidden" name="employeeKeyword" id="exportEmployeeKeyword" value="${employeeKeyword}">
                        <input type="hidden" name="department" id="exportDepartment" value="${department}">
                        <input type="hidden" name="startDate" id="exportStartDate" value="${startDate}">
                        <input type="hidden" name="endDate" id="exportEndDate" value="${endDate}">
                        <input type="hidden" name="status" id="exportStatus" value="${status}">
                        <input type="hidden" name="source" id="exportSource" value="${source}">
                        <input type="hidden" name="periodSelect" id="exportPeriodSelect" value="${selectedPeriod}">
                    </form>

                    <!-- Export / Submit Buttons -->
                    <div class="export-buttons">
                        <button type="button" id="exportXLSBtn" class="btn btn-export btn-xls">Export XLS</button>
                        <button type="button" id="exportCSVBtn" class="btn btn-export btn-csv">Export CSV</button>
                        <button type="button" id="exportPDFBtn" class="btn btn-export btn-pdf">Export PDF</button>
                    </div>
                    <br/>

                    <!-- Attendance Table -->
                    <table id="attendanceTable" class="attendance-table" border="1" cellpadding="6">
                        <thead>
                            <tr>
                                <th class="col-date">Date</th>
                                <th class="col-checkin">Check-in</th>
                                <th class="col-checkout">Check-out</th>
                                <th class="col-status">Status</th>
                                <th class="col-source">Source</th>
                                <th class="col-period">Period</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="record" items="${attendanceList}">
                                <tr class="attendance-row">
                                    <td class="cell-date">${record.dateStr}</td>
                                    <td class="cell-checkin">${record.checkInStr}</td>
                                    <td class="cell-checkout">${record.checkOutStr}</td>
                                    <td class="cell-status">${record.status}</td>
                                    <td class="cell-source">${record.source}</td>
                                    <td class="cell-period">${record.period}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <!-- Pagination POST -->
                    <div class="pagination" style="margin-top: 10px;">
                        <c:if test="${currentPage > 1}">
                            <form method="post" style="display:inline;">
                                <input type="hidden" name="startDate" value="${startDate}">
                                <input type="hidden" name="endDate" value="${endDate}">
                                <input type="hidden" name="status" value="${status}">
                                <input type="hidden" name="source" value="${source}">
                                <input type="hidden" name="periodSelect" value="${selectedPeriod}">
                                <input type="hidden" name="page" value="${currentPage - 1}">
                                <button type="submit" class="page-link">Previous</button>
                            </form>
                        </c:if>

                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <c:choose>
                                <c:when test="${i == currentPage}">
                                    <span class="page-current"><b>${i}</b></span>
                                        </c:when>
                                        <c:otherwise>
                                    <form method="post" style="display:inline;">
                                        <input type="hidden" name="startDate" value="${startDate}">
                                        <input type="hidden" name="endDate" value="${endDate}">
                                        <input type="hidden" name="status" value="${status}">
                                        <input type="hidden" name="source" value="${source}">
                                        <input type="hidden" name="periodSelect" value="${selectedPeriod}">
                                        <input type="hidden" name="page" value="${i}">
                                        <button type="submit" class="page-link">${i}</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <form method="post" style="display:inline;">
                                <input type="hidden" name="startDate" value="${startDate}">
                                <input type="hidden" name="endDate" value="${endDate}">
                                <input type="hidden" name="status" value="${status}">
                                <input type="hidden" name="source" value="${source}">
                                <input type="hidden" name="periodSelect" value="${selectedPeriod}">
                                <input type="hidden" name="page" value="${currentPage + 1}">
                                <button type="submit" class="page-link">Next</button>
                            </form>
                        </c:if>
                    </div>

                </main>
            </div>
        </div>