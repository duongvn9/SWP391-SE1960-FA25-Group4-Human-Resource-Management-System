<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib uri="jakarta.tags.core" prefix="m" %>
<!DOCTYPE html> 
<html lang="vi"> 
    <head> 
        <meta charset="UTF-8"> 
        <jsp:include page="../layout/head.jsp">
            <jsp:param name="pageTitle" value="Submit Attendance Dispute - HRMS" />
        </jsp:include> 
        <title>Attendance Management</title> 
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/attendance-record-HR.css"> 
    </head> 
    <body> 
        <div class="page-wrapper">
            <jsp:include page="../layout/sidebar.jsp"> 
                <jsp:param name="currentPage" value="attendance-record-emp" /> 
            </jsp:include> 

            <div class="dashboard-wrapper">
                <jsp:include page="../layout/dashboard-header.jsp" />

                <main class="main-content"> 
                    <h2 class="page-title">Attendance Period Management</h2> 

                    <!-- ========== FILTER SECTION ========== --> 
                    <form id="filterForm" class="filter-form" method="post" action="${pageContext.request.contextPath}/attendance/record/HR"> 
                        <input type="hidden" name="page" id="pageInput" value="${currentPage}" />

                        <!-- From / To Date -->
                        <div class="filter-group">
                            <label class="filter-label">From:</label> 
                            <input type="date" class="filter-input" name="startDate" value="${startDate}" /> 
                        </div>
                        <div class="filter-group">
                            <label class="filter-label">To:</label> 
                            <input type="date" class="filter-input" name="endDate" value="${endDate}" /> 
                        </div>

                        <!-- Department -->
                        <div class="filter-group">
                            <label class="filter-label">Department:</label> 
                            <select name="department" class="filter-select"> 
                                <option value="">--All--</option> 
                                <c:forEach var="dept" items="${departmentList}"> 
                                    <option value="${dept.name}" <c:if test="${department eq dept.name}">selected</c:if>>${dept.name}</option> 
                                </c:forEach> 
                            </select> 
                        </div>

                        <!-- Employee -->
                        <div class="filter-group">
                            <label class="filter-label">Employee:</label> 
                            <input type="text" class="filter-input" name="employeeKeyword" value="${employeeKeyword}" placeholder="Name or ID" /> 
                        </div>

                        <!-- Status -->
                        <div class="filter-group">
                            <label class="filter-label">Status:</label> 
                            <select name="status" class="filter-select"> 
                                <option value="">All</option> 
                                <option value="On time" <c:if test="${status eq 'On time'}">selected</c:if>>On time</option> 
                                <option value="Late" <c:if test="${status eq 'Late'}">selected</c:if>>Late</option> 
                                </select> 
                            </div>

                            <!-- Source -->
                            <div class="filter-group">
                                <label class="filter-label">Source:</label> 
                                <select name="source" class="filter-select"> 
                                    <option value="">All</option> 
                                    <option value="Manual" <c:if test="${source eq 'Manual'}">selected</c:if>>Manual</option> 
                                <option value="Excel" <c:if test="${source eq 'Excel'}">selected</c:if>>Excel</option> 
                                </select> 
                            </div>

                            <!-- Period -->
                            <div class="filter-group">
                                <label class="filter-label">Period:</label> 
                                <select name="periodSelect" class="filter-select">
                                    <option value="">-- All Periods --</option> 
                                <c:forEach var="p" items="${periodList}"> 
                                    <option value="${p.id}" <c:if test="${selectedPeriod eq p.id}">selected</c:if>>${p.name}</option> 
                                </c:forEach> 
                            </select> 
                        </div>

                        <!-- Buttons -->
                        <div class="filter-buttons">
                            <button type="submit" name="action" value="filter" class="btn btn-primary">Filter</button> 
                            <button type="submit" name="action" value="reset" class="btn btn-secondary">Reset</button> 
                        </div>
                    </form>

                    <!-- ========== ACTION BUTTONS ========== -->
                    <div id="actions" class="action-buttons"> 
                        <button type="button" class="btn btn-upload" onclick="importAttendance()">Upload</button> 
                        <button type="button" id="exportXLSBtn" class="btn btn-export">Export XLS</button> 
                        <button type="button" id="exportCSVBtn" class="btn btn-export">Export CSV</button> 
                        <button type="button" id="exportPDFBtn" class="btn btn-export">Export PDF</button> 
                        <button id="editBtn" class="btn btn-edit" onclick="enableEdit()">Edit</button> 

                        <div class="switch-container">
                            <div class="toggle-switch">
                                <input type="checkbox" id="switchInput">
                                <label for="switchInput" class="slider"></label>
                            </div>
                            <span id="sliderStatus" class="status-text">Unlocked</span>
                        </div>
                    </div>

                    <form id="exportForm" class="export-form" action="${pageContext.request.contextPath}/attendance/record/HR" method="post"> 
                        <input type="hidden" name="exportType" id="exportType"> 
                    </form>

                    <c:if test="${not empty message or not empty error}">
                        <div id="actionMessage" class="action-message" style="margin-top:10px;
                             color: ${not empty message ? 'green' : 'red'};">
                            <c:out value="${not empty message ? message : error}" />
                        </div>
                    </c:if>

                    <!-- ========== MAIN TABLE ========== --> 
                    <div class="table-wrapper">
                        <table id="attendanceTable" class="attendance-table" border="1" cellspacing="0" cellpadding="6"> 
                            <thead> 
                                <tr> 
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
                                    <tr class="attendance-row"> 
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
                                            <form class="actionForm" method="post" action="${pageContext.request.contextPath}/attendance/record/HR">
                                                <input type="hidden" name="action" class="formAction">
                                                <input type="hidden" name="userId" value="${att.userId}">
                                                <input type="hidden" name="date" value="<c:out value='${att.date}'/>">
                                                <input type="hidden" name="checkIn" value="<c:if test='${att.checkIn != null}'>${att.checkIn.toString().substring(0,5)}</c:if>">
                                                <input type="hidden" name="checkOut" value="<c:if test='${att.checkOut != null}'>${att.checkOut.toString().substring(0,5)}</c:if>">

                                                    <button type="button" class="btn btn-update-row" onclick="submitAction(this, 'update')">Update</button>
                                                    <button type="button" class="btn btn-delete-row" onclick="submitAction(this, 'delete')">Delete</button>
                                                </form>
                                            </td>
                                        </tr> 
                                </c:forEach> 
                            </tbody> 
                        </table> 
                    </div>

                    <!-- ========== PAGINATION ========== --> 
                    <div class="pagination-wrapper" style="margin-top: 10px;">
                        <form id="paginationForm" method="post" action="${pageContext.request.contextPath}/attendance/record/HR">
                            <!-- Giữ tất cả filter hiện tại trong input ẩn -->
                            <input type="hidden" name="employeeKeyword" value="${employeeKeyword}" />
                            <input type="hidden" name="department" value="${department}" />
                            <input type="hidden" name="status" value="${status}" />
                            <input type="hidden" name="source" value="${source}" />
                            <input type="hidden" name="periodSelect" value="${selectedPeriod}" />
                            <input type="hidden" name="startDate" value="${startDate}" />
                            <input type="hidden" name="endDate" value="${endDate}" />

                            <c:if test="${currentPage > 1}">
                                <button type="submit" name="page" value="${currentPage - 1}" class="pagination-link">Previous</button>
                            </c:if>

                            <c:forEach var="i" begin="1" end="${totalPages}">
                                <c:choose>
                                    <c:when test="${i == currentPage}">
                                        <span class="pagination-current"><b>${i}</b></span>
                                            </c:when>
                                            <c:otherwise>
                                        <button type="submit" name="page" value="${i}" class="pagination-link">${i}</button>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                            <c:if test="${currentPage < totalPages}">
                                <button type="submit" name="page" value="${currentPage + 1}" class="pagination-link">Next</button>
                            </c:if>
                        </form>
                    </div>

                </main> 
            </div>
        </div>

        <div id="editModal" class="modal" style="display:none;">
            <div class="modal-content">
                <span class="close-btn" onclick="closeModal()">&times;</span>
                <h3>Edit Attendance</h3>
                <form id="editForm">
                    <label for="modalCheckIn">Employee ID: </label>
                    <input type="time" name="checkIn" id="modalCheckIn" required>
                    
                    <label for="modalCheckIn">Employee Name: </label>
                    <input type="time" name="checkIn" id="modalCheckIn" required>
                    
                    <label for="modalCheckIn">Department: </label>
                    <input type="time" name="checkIn" id="modalCheckIn" required>
                    
                    <label for="modalCheckIn">Date: </label>
                    <input type="time" name="checkIn" id="modalCheckIn" required>
                    
                    <label for="modalCheckIn">Check-in: </label>
                    <input type="time" name="checkIn" id="modalCheckIn" required>

                    <label for="modalCheckOut">Check-out: </label>
                    <input type="time" name="checkOut" id="modalCheckOut" required>

                    <label for="modalStatus">Status: </label>
                    <input type="text" name="status" id="modalStatus">

                    <label for="modalSource">Source: </label>
                    <input type="text" name="source" id="modalSource">
                    
                    <label for="modalSource">Period: </label>
                    <input type="text" name="source" id="modalSource">

                    <button type="button" onclick="submitEdit()">Save</button>
                </form>
            </div>
        </div>

        <script src="${pageContext.request.contextPath}/assets/js/attendance-record-HR.js"></script> 
    </body> 
</html>
