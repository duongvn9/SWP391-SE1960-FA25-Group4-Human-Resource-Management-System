<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
                    <jsp:include page="../layout/dashboard-header.jsp" />

                    <div class="main-container">
                        <jsp:include page="../layout/sidebar.jsp">
                            <jsp:param name="currentPage" value="attendance-record-emp" />
                        </jsp:include>

                        <main class="main-content">
                            <h2 class="page-title">My Attendance Records</h2>

                            <!-- Filter Form -->
                            <form id="filterForm" class="filter-form" method="post"
                                action="${pageContext.request.contextPath}/attendance/record/emp">
                                <div class="filter-group">
                                    <label for="startDate" class="filter-label">From:</label>
                                    <input type="date" id="startDate" name="startDate" value="${startDate}"
                                        class="filter-input">
                                </div>

                                <div class="filter-group">
                                    <label for="endDate" class="filter-label">To:</label>
                                    <input type="date" id="endDate" name="endDate" value="${endDate}"
                                        class="filter-input">
                                </div>

                                <div class="filter-group">
                                    <label for="status" class="filter-label">Status:</label>
                                    <select id="status" name="status" class="filter-select">
                                        <option value="">All</option>
                                        <option value="On time" ${status=='On time' ? 'selected' : '' }>On time</option>
                                        <option value="Late" ${status=='Late' ? 'selected' : '' }>Late</option>
                                        <option value="Early Leave" ${status=='Early Leave' ? 'selected' : '' }>Early
                                            Leave</option>
                                        <option value="Late & Early Leave" ${status=='Late & Early Leave' ? 'selected'
                                            : '' }>Late & Early Leave</option>
                                        <option value="Over Time" ${status=='Over Time' ? 'selected' : '' }>Over Time
                                        </option>
                                        <option value="Invalid" ${status=='Invalid' ? 'selected' : '' }>Invalid</option>
                                    </select>
                                </div>

                                <div class="filter-group">
                                    <label for="source" class="filter-label">Source:</label>
                                    <select id="source" name="source" class="filter-select">
                                        <option value="">All</option>
                                        <option value="Manual" ${source=='Manual' ? 'selected' : '' }>Manual</option>
                                        <option value="excel" ${source=='Import' ? 'selected' : '' }>Excel</option>
                                    </select>
                                </div>

                                <div class="filter-group">
                                    <label for="periodSelect" class="filter-label">Select Period:</label>
                                    <select id="periodSelect" name="periodSelect" class="filter-select">
                                        <option value="">-- All Periods --</option>
                                        <c:forEach var="p" items="${periodList}">
                                            <option value="${p.id}" ${selectedPeriodId==p.id ? 'selected' : '' }>
                                                ${p.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="filter-actions">
                                    <button type="submit" class="btn btn-filter">Filter</button>
                                    <button type="submit" name="action" value="reset"
                                        class="btn btn-reset">Reset</button>
                                </div>

                                <!-- Hidden input lưu các record đã chọn -->
                                <input type="hidden" id="selectedLogDates" name="selected_log_dates"
                                    value="${selectedLogDates}">
                            </form>
                            <br />

                            <!-- Export Form (ẩn, dùng JS để submit) -->
                            <form id="exportForm" class="export-form"
                                action="${pageContext.request.contextPath}/attendance/record/emp" method="post">
                                <input type="hidden" name="exportType" id="exportType">
                                <input type="hidden" name="employeeKeyword" id="exportEmployeeKeyword"
                                    value="${employeeKeyword}">
                                <input type="hidden" name="department" id="exportDepartment" value="${department}">
                                <input type="hidden" name="startDate" id="exportStartDate" value="${startDate}">
                                <input type="hidden" name="endDate" id="exportEndDate" value="${endDate}">
                                <input type="hidden" name="status" id="exportStatus" value="${status}">
                                <input type="hidden" name="source" id="exportSource" value="${source}">
                                <input type="hidden" name="periodSelect" id="exportPeriodSelect"
                                    value="${selectedPeriodId}">
                            </form>

                            <!-- ========== ACTION BUTTONS ========== -->
                            <div class="export-buttons">
                                <button type="button" id="exportXLSBtn" class="btn btn-export btn-xls">Export
                                    XLS</button>
                                <button type="button" id="exportCSVBtn" class="btn btn-export btn-csv">Export
                                    CSV</button>
                                <button type="button" id="exportPDFBtn" class="btn btn-export btn-pdf">Export
                                    PDF</button>
                                <c:if test="${showSummaryButton}">
                                    <button type="button" id="viewSummaryBtn" class="btn btn-summary">View
                                        Summary</button>
                                </c:if>
                            </div>

                            <!-- ========== MAIN TABLE ========== -->
                            <div class="table-wrapper">
                                <table id="attendanceTable" class="attendance-table" border="1" cellspacing="0"
                                    cellpadding="6">
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
                            </div>

                            <!-- ========== PAGINATION ========== -->
                            <div class="pagination" style="margin-top: 10px;">
                                <!-- Previous -->
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

                                <!-- Tính toán pagination logic -->
                                <c:set var="maxVisiblePages" value="5" />
                                <c:set var="halfVisible" value="2" />

                                <!-- Tính startPage và endPage -->
                                <c:choose>
                                    <c:when test="${totalPages <= maxVisiblePages}">
                                        <!-- Nếu tổng số trang <= 5, hiển thị tất cả -->
                                        <c:set var="startPage" value="1" />
                                        <c:set var="endPage" value="${totalPages}" />
                                    </c:when>
                                    <c:when test="${currentPage <= halfVisible + 1}">
                                        <!-- Nếu ở đầu, hiển thị từ 1 đến maxVisiblePages -->
                                        <c:set var="startPage" value="1" />
                                        <c:set var="endPage" value="${maxVisiblePages}" />
                                    </c:when>
                                    <c:when test="${currentPage >= totalPages - halfVisible}">
                                        <!-- Nếu ở cuối, hiển thị maxVisiblePages trang cuối -->
                                        <c:set var="startPage" value="${totalPages - maxVisiblePages + 1}" />
                                        <c:set var="endPage" value="${totalPages}" />
                                    </c:when>
                                    <c:otherwise>
                                        <!-- Ở giữa, hiển thị currentPage ± halfVisible -->
                                        <c:set var="startPage" value="${currentPage - halfVisible}" />
                                        <c:set var="endPage" value="${currentPage + halfVisible}" />
                                    </c:otherwise>
                                </c:choose>

                                <!-- Hiển thị trang đầu và dấu ... nếu cần -->
                                <c:if test="${startPage > 1}">
                                    <form method="post" style="display:inline;">
                                        <input type="hidden" name="startDate" value="${startDate}">
                                        <input type="hidden" name="endDate" value="${endDate}">
                                        <input type="hidden" name="status" value="${status}">
                                        <input type="hidden" name="source" value="${source}">
                                        <input type="hidden" name="periodSelect" value="${selectedPeriod}">
                                        <input type="hidden" name="page" value="1">
                                        <button type="submit" class="page-link">1</button>
                                    </form>
                                    <c:if test="${startPage > 2}">
                                        <span class="pagination-dots">...</span>
                                    </c:if>
                                </c:if>

                                <!-- Hiển thị các trang trong khoảng startPage đến endPage -->
                                <c:forEach var="i" begin="${startPage}" end="${endPage}">
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

                                <!-- Hiển thị dấu ... và trang cuối nếu cần -->
                                <c:if test="${endPage < totalPages}">
                                    <c:if test="${endPage < totalPages - 1}">
                                        <span class="pagination-dots">...</span>
                                    </c:if>
                                    <form method="post" style="display:inline;">
                                        <input type="hidden" name="startDate" value="${startDate}">
                                        <input type="hidden" name="endDate" value="${endDate}">
                                        <input type="hidden" name="status" value="${status}">
                                        <input type="hidden" name="source" value="${source}">
                                        <input type="hidden" name="periodSelect" value="${selectedPeriod}">
                                        <input type="hidden" name="page" value="${totalPages}">
                                        <button type="submit" class="page-link">${totalPages}</button>
                                    </form>
                                </c:if>

                                <!-- Next -->
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
                    </main>
                </div>

                <!-- Attendance Summary Popup -->
                <div id="summaryModal" class="modal-overlay">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3>Attendance Summary</h3>
                            <span class="close-btn" id="closeSummaryModal">&times;</span>
                        </div>
                        <div class="modal-body">
                            <div class="summary-grid">
                                <div class="summary-item">
                                    <label>Total Working Days:</label>
                                    <span id="totalWorkingDays">0</span>
                                </div>
                                <div class="summary-item">
                                    <label>Days On Time:</label>
                                    <span id="daysOnTime">0</span>
                                </div>
                                <div class="summary-item">
                                    <label>Days Late:</label>
                                    <span id="daysLate">0</span>
                                </div>
                                <div class="summary-item">
                                    <label>Days Early Leaving:</label>
                                    <span id="daysEarlyLeaving">0</span>
                                </div>
                                <div class="summary-item">
                                    <label>Days Late & Early Leaving:</label>
                                    <span id="daysLateAndEarlyLeaving">0</span>
                                </div>
                                <div class="summary-item">
                                    <label>Total Hours Worked:</label>
                                    <span id="totalHoursWorked">0</span>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" id="closeSummaryBtn">Close</button>
                        </div>
                    </div>
                </div>

                <!-- Hidden element to store attendance summary data from backend -->
                <script type="application/json" id="attendanceSummaryData">
        <c:if test="${not empty attendanceSummary}">
            {
                "totalWorkingDays": ${attendanceSummary.totalWorkingDays != null ? attendanceSummary.totalWorkingDays : 0},
                "daysOnTime": ${attendanceSummary.daysOnTime != null ? attendanceSummary.daysOnTime : 0},
                "daysLate": ${attendanceSummary.daysLate != null ? attendanceSummary.daysLate : 0},
                "daysEarlyLeaving": ${attendanceSummary.daysEarlyLeaving != null ? attendanceSummary.daysEarlyLeaving : 0},
                "daysLateAndEarlyLeaving": ${attendanceSummary.daysLateAndEarlyLeaving != null ? attendanceSummary.daysLateAndEarlyLeaving : 0},
                "daysAbsent": ${attendanceSummary.daysAbsent != null ? attendanceSummary.daysAbsent : 0},
                "totalHoursWorked": ${attendanceSummary.totalHoursWorked != null ? attendanceSummary.totalHoursWorked : 0},
                "overtimeHours": ${attendanceSummary.overtimeHours != null ? attendanceSummary.overtimeHours : 0}
            }
        </c:if>
        <c:if test="${empty attendanceSummary}">
            {
                "totalWorkingDays": 0,
                "daysOnTime": 0,
                "daysLate": 0,
                "daysEarlyLeaving": 0,
                "daysLateAndEarlyLeaving": 0,
                "daysAbsent": 0,
                "totalHoursWorked": 0,
                "overtimeHours": 0
            }
        </c:if>
    </script>

                <script src="${pageContext.request.contextPath}/assets/js/attendance-record-emp.js"></script>
            </body>

            </html>