<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Attendance Management</title>  
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/attendance-record-HR.css">
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
            <button type="submit" name="action" value="reset" id="resetBtn">Reset</button>
        </form>

        <div id="actions">
            <button type="button" onclick="importAttendance()">Upload</button>
            <button type="button" id="exportXLSBtn">Export XLS</button>
            <button type="button" id="exportCSVBtn">Export CSV</button>
            <button type="button" id="exportPDFBtn">Export PDF</button>
            <button id="editBtn" onclick="enableEdit()">Edit</button>
            <button id="submitBtn" onclick="submitChanges()">Submit</button>
            <button id="deleteBtn" onclick="toggleDeleteMode()">Delete</button>
            <span id="sliderStatus">Unlocked</span>
        </div>

        <form id="exportForm" action="${pageContext.request.contextPath}/attendance/record/HR" method="post">
            <input type="hidden" name="exportType" id="exportType">
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
                        <td>${att.userId}</td>
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

        <div class="pagination" style="margin-top: 10px;">
            <c:if test="${currentPage > 1}">
                <a href="?page=${currentPage - 1}">Previous</a>
            </c:if>

            <c:forEach var="i" begin="1" end="${totalPages}">
                <c:choose>
                    <c:when test="${i == currentPage}">
                        <span><b>${i}</b></span>
                    </c:when>
                    <c:otherwise>
                        <a href="?page=${i}">${i}</a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <c:if test="${currentPage < totalPages}">
                <a href="?page=${currentPage + 1}">Next</a>
            </c:if>
        </div>

        <script src="${pageContext.request.contextPath}/assets/js/attendance-record-HR.js"></script>
    </body>
</html>
