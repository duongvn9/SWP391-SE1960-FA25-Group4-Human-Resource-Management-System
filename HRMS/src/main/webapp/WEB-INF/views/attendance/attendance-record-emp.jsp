<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>My Attendance</title>  
    </head>
    <body>
        <h2>My Attendance Records</h2>

        <form id="filterForm" method="post" action="${pageContext.request.contextPath}/attendance/record/emp">
            <label for="startDate">From:</label>
            <input type="date" id="startDate" name="startDate" value="${param.startDate}">

            <label for="endDate">To:</label>
            <input type="date" id="endDate" name="endDate" value="${param.endDate}">

            <label for="status">Status:</label>
            <select id="status" name="status">
                <option value="">All</option>
                <option value="On time" ${param.status == 'On time' ? 'selected' : ''}>On time</option>
                <option value="Late" ${param.status == 'Late' ? 'selected' : ''}>Late</option>
            </select>

            <label for="source">Source:</label>
            <select id="source" name="source">
                <option value="">All</option>
                <option value="Google" ${param.source == 'Google' ? 'selected' : ''}>Google sheet</option>
                <option value="Manual" ${param.source == 'Manual' ? 'selected' : ''}>Manual</option>
                <option value="Import" ${param.source == 'Import' ? 'selected' : ''}>Import</option>
            </select>

            <label for="periodSelect">Select Period:</label>
            <select id="periodSelect" name="periodSelect">
                <option value="">-- All Periods --</option>
                <c:forEach var="p" items="${periodList}">
                    <option value="${p.id}" ${param.periodSelect == p.id.toString() ? 'selected' : ''}>${p.name}</option>
                </c:forEach>
            </select>

            <button type="submit">Filter</button>
            <button type="submit" name="action" value="reset" id="resetBtn">Reset</button>
        </form>
        <br/><br/> 
        
        <form id="exportForm" action="${pageContext.request.contextPath}/attendance/record/emp" method="post">
            <input type="hidden" name="exportType" id="exportType">
        </form>

        <button type="button" id="exportXLSBtn">Export XLS</button>
        <button type="button" id="exportCSVBtn">Export CSV</button>
        <button type="button" id="exportPDFBtn">Export PDF</button>
        <br/><br/>

        <table id="attendanceTable" border="1" cellpadding="6">
            <thead>
                <tr>
                    <th>Select</th>
                    <th>Date</th>
                    <th>Check-in</th>
                    <th>Check-out</th>
                    <th>Status</th>
                    <th>Source</th>
                    <th>Period</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="record" items="${attendanceList}">
                    <tr>
                        <td><button type="button" class="selectAttendanceBtn">Select</button></td>
                        <td>${record.dateStr}</td>
                        <td>${record.checkInStr}</td>
                        <td>${record.checkOutStr}</td>
                        <td>${record.status}</td>
                        <td>${record.source}</td>
                        <td>${record.period}</td>
                    </tr>
                </c:forEach>
            </tbody>
            <script src="${pageContext.request.contextPath}/assets/js/attendance-record-emp.js"></script>
        </table>
    </body>
</html>