<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Attendance Management</title>
        <script>
            function selectAll(source) {
                const checkboxes = document.querySelectorAll('#attendanceTable tbody input[type="checkbox"]');
                checkboxes.forEach(cb => cb.checked = source.checked);
            }
        </script>
    </head>
    <body>
        <h2>Attendance Period Management</h2>

        <!-- ========== FILTER SECTION ========== -->
        <form id="filterForm" method="post" action="${pageContext.request.contextPath}/attendance/record/HR">
            <label>From:</label>
            <input type="date" name="startDate" value="${param.startDate}" />

            <label>To:</label>
            <input type="date" name="endDate" value="${param.endDate}" />

            <label>Department:</label>
            <select name="department">
                <option value="">--All--</option>
                <c:forEach var="dept" items="${departmentList}">
                    <option value="${dept.name}" 
                            <c:if test="${param.department eq dept.name}">selected</c:if>>
                        ${dept.name}
                    </option>
                </c:forEach>
            </select>

            <label>Employee:</label>
            <input type="text" name="employee" placeholder="Name or ID" value="${param.employee}" />

            <label>Status:</label>
            <select id="status" name="status">
                <option value="">All</option>
                <option value="On time" <c:if test="${param.status eq 'On time'}">selected</c:if>>On time</option>
                <option value="Late" <c:if test="${param.status eq 'Late'}">selected</c:if>>Late</option>
                </select>

                <label>Source:</label>
                <select id="source" name="source">
                    <option value="">All</option>
                    <option value="Google" <c:if test="${param.source eq 'Google'}">selected</c:if>>Google sheet</option>
                <option value="Manual" <c:if test="${param.source eq 'Manual'}">selected</c:if>>Manual</option>
                <option value="Import" <c:if test="${param.source eq 'Import'}">selected</c:if>>Import</option>
                </select>

                <label>Period:</label>
                <select id="periodSelect" name="periodId">
                    <option value="">-- All Periods --</option>
                <c:forEach var="p" items="${periodList}">
                    <option value="${p.id}" <c:if test="${param.periodId eq p.id.toString()}">selected</c:if>>
                        ${p.name}
                    </option>
                </c:forEach>
            </select>

            <button type="submit">Filter</button>
        </form>

        <!-- ========== MAIN TABLE ========== -->
        <table id="attendanceTable" border="1" cellspacing="0" cellpadding="6">
            <thead>
                <tr>
                    <th><input type="checkbox" onclick="selectAll(this)" /></th>
                    <th>Employee ID</th>
                    <th>Employee Name</th>
                    <th>Department</th>
                    <th>Date</th>
                    <th>Check-in</th>
                    <th>Check-out</th>
                    <th>Status</th>
                    <th>Source</th>
                    <th>Period</th>
                    <th class="edit-col" style="display:none;">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="att" items="${attendanceList}">
                    <tr>
                        <td><input type="checkbox" /></td>
                        <td>${att.employeeId}</td>
                        <td>${att.employeeName}</td>
                        <td>${att.department}</td>
                        <td><c:out value="${att.date}" /></td>
                        <td><c:if test="${att.checkIn != null}">${att.checkIn.toString().substring(0,5)}</c:if></td>
                        <td><c:if test="${att.checkOut != null}">${att.checkOut.toString().substring(0,5)}</c:if></td>
                        <td><c:out value="${att.status}" /></td>
                        <td><c:out value="${att.source}" /></td>
                        <td><c:out value="${att.period}" /></td>
                        <td class="edit-col" style="display:none;">
                            <button>Delete</button>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </body>
</html>
