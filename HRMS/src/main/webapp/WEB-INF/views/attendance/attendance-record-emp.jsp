<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>My Attendance</title>  
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/attendance-record-emp.css">
    </head>
    <body class="attendance-page">
        <h2 class="page-title">My Attendance Records</h2>

        <form id="filterForm" class="filter-form" method="post" action="${pageContext.request.contextPath}/attendance/record/emp">
            <div class="filter-group">
                <label for="startDate" class="filter-label">From:</label>
                <input type="date" id="startDate" name="startDate" value="${param.startDate}" class="filter-input">
            </div>

            <div class="filter-group">
                <label for="endDate" class="filter-label">To:</label>
                <input type="date" id="endDate" name="endDate" value="${param.endDate}" class="filter-input">
            </div>

            <div class="filter-group">
                <label for="status" class="filter-label">Status:</label>
                <select id="status" name="status" class="filter-select">
                    <option value="">All</option>
                    <option value="On time" ${param.status == 'On time' ? 'selected' : ''}>On time</option>
                    <option value="Late" ${param.status == 'Late' ? 'selected' : ''}>Late</option>
                </select>
            </div>

            <div class="filter-group">
                <label for="source" class="filter-label">Source:</label>
                <select id="source" name="source" class="filter-select">
                    <option value="">All</option>
                    <option value="Manual" ${param.source == 'Manual' ? 'selected' : ''}>Manual</option>
                    <option value="Import" ${param.source == 'Import' ? 'selected' : ''}>Import</option>
                </select>
            </div>

            <div class="filter-group">
                <label for="periodSelect" class="filter-label">Select Period:</label>
                <select id="periodSelect" name="periodSelect" class="filter-select">
                    <option value="">-- All Periods --</option>
                    <c:forEach var="p" items="${periodList}">
                        <option value="${p.id}" ${param.periodSelect == p.id.toString() ? 'selected' : ''}>${p.name}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="filter-actions">
                <button type="submit" class="btn btn-filter">Filter</button>
                <button type="submit" name="action" value="reset" id="resetBtn" class="btn btn-reset">Reset</button>
            </div>
        </form>
        <br/><br/> 

        <form id="exportForm" class="export-form" action="${pageContext.request.contextPath}/attendance/record/emp" method="post">
            <input type="hidden" name="exportType" id="exportType">
        </form>

        <div class="export-buttons">
            <button type="button" id="exportXLSBtn" class="btn btn-export btn-xls">Export XLS</button>
            <button type="button" id="exportCSVBtn" class="btn btn-export btn-csv">Export CSV</button>
            <button type="button" id="exportPDFBtn" class="btn btn-export btn-pdf">Export PDF</button>
        </div>
        <br/><br/>

        <table id="attendanceTable" class="attendance-table" border="1" cellpadding="6">
            <thead>
                <tr>
                    <th class="col-select">Select</th>
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
                        <td><button type="button" class="selectAttendanceBtn btn btn-select">Select</button></td>
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

        <div class="pagination" style="margin-top: 10px;">
            <c:if test="${currentPage > 1}">
                <a href="?page=${currentPage - 1}" class="page-link">Previous</a>
            </c:if>

            <c:forEach var="i" begin="1" end="${totalPages}">
                <c:choose>
                    <c:when test="${i == currentPage}">
                        <span class="page-current"><b>${i}</b></span>
                            </c:when>
                            <c:otherwise>
                        <a href="?page=${i}" class="page-link">${i}</a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <c:if test="${currentPage < totalPages}">
                <a href="?page=${currentPage + 1}" class="page-link">Next</a>
            </c:if>
        </div>

        <script src="${pageContext.request.contextPath}/assets/js/attendance-record-emp.js"></script>
    </body>
</html>
