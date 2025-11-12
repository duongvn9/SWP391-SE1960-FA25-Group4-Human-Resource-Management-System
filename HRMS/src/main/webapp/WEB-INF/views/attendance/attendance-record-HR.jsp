<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
    <body class="attendance-page">
        <div class="page-wrapper">
            <jsp:include page="../layout/dashboard-header.jsp" />

            <div class="main-container">
                <jsp:include page="../layout/sidebar.jsp">
                    <jsp:param name="currentPage" value="attendance-record-HR" />
                </jsp:include>

                <main class="main-content">
                    <h2 class="page-title">Attendance Period Management</h2>

                    <!-- ========== FILTER SECTION ========== -->
                    <form id="filterForm" class="filter-form" method="post"
                          action="${pageContext.request.contextPath}/attendance/record/HR">

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
                                    <option value="${dept.name}" <c:if test="${department eq dept.name}">
                                            selected</c:if>>${dept.name}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- Employee -->
                        <div class="filter-group">
                            <label class="filter-label">Employee:</label>
                            <select name="employeeId" class="filter-select">
                                <option value="">-- Select Employee --</option>
                                <c:forEach var="emp" items="${uList}">
                                    <option value="${emp.id}" <c:if test="${employeeId == emp.id}">
                                            selected
                                        </c:if>>
                                        ${emp.employeeCode} - ${emp.fullName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- Status -->
                        <div class="filter-group">
                            <label class="filter-label">Status:</label>
                            <select name="status" class="filter-select">
                                <option value="">All</option>
                                <option value="On time" <c:if test="${status eq 'On time'}">selected</c:if>>On
                                        time</option>
                                    <option value="Late" <c:if test="${status eq 'Late'}">selected</c:if>>Late
                                    </option>
                                    <option value="Early Leave" <c:if test="${status eq 'Early Leave'}">selected
                                        </c:if>>Early Leave</option>
                                <option value="Late & Early Leave" <c:if
                                            test="${status eq 'Late & Early Leave'}">selected</c:if>>Late & Early Leave
                                        </option>
                                        <option value="Over Time" <c:if test="${status eq 'Over Time'}">selected</c:if>
                                                >Over Time</option>
                                        <option value="Outside Working Hours" <c:if test="${status eq 'Outside Working Hours'}">selected</c:if>
                                                >Outside Working Hours</option>
                                        <option value="Invalid" <c:if test="${status eq 'Invalid'}">selected</c:if>
                                                >Invalid</option>
                                </select>
                            </div>

                            <!-- Source -->
                            <div class="filter-group">
                                <label class="filter-label">Source:</label>
                                <select name="source" class="filter-select">
                                    <option value="">All</option>
                                    <option value="Manual" <c:if test="${source eq 'Manual'}">selected</c:if>>Manual
                                    </option>
                                    <option value="Excel" <c:if test="${source eq 'Excel'}">selected</c:if>>Excel
                                    </option>
                                </select>
                            </div>

                            <!-- Period -->
                            <div class="filter-group">
                                <label class="filter-label">Period:</label>
                                <select name="periodSelect" class="filter-select">
                                    <option value="">-- All Periods --</option>
                                <c:forEach var="p" items="${periodList}">
                                    <option value="${p.id}" <c:if
                                                test="${selectedPeriod != null and selectedPeriod.id eq p.id}">
                                                selected
                                            </c:if>>
                                        ${p.name}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- Buttons -->
                        <div class="filter-buttons">
                            <button type="submit" name="action" value="filter"
                                    class="btn btn-primary">Filter</button>
                            <button type="submit" name="action" value="reset"
                                    class="btn btn-secondary">Reset</button>
                        </div>
                    </form>

                    <!-- ========== ACTION BUTTONS ========== -->
                    <div id="actions" class="action-buttons">
                        <button type="button" class="btn btn-upload"
                                onclick="importAttendance()">Upload</button>
                        <button type="button" id="exportXLSBtn" class="btn btn-export">Export XLS</button>
                        <button type="button" id="exportCSVBtn" class="btn btn-export">Export CSV</button>
                        <button type="button" id="exportPDFBtn" class="btn btn-export">Export PDF</button>
                        <button id="editBtn" class="btn btn-edit" onclick="enableEdit()">Edit</button>

                        <c:if test="${sessionScope.user != null && sessionScope.user.positionId == 7}">
                            <c:if test="${selectedPeriod != null}">
                                <div class="switch-container">
                                    <div class="toggle-switch">
                                        <input type="checkbox" id="switchInput"
                                               data-period-id="${selectedPeriod.id}" ${selectedPeriod.isLocked
                                                                 ? "checked" : "" } ${!canToggleLock ? "disabled" : "" } />
                                        <label for="switchInput"
                                               class="slider ${!canToggleLock ? 'disabled' : ''}"></label>
                                    </div>
                                    <span id="sliderStatus">
                                        <c:choose>
                                            <c:when test="${isPermanentlyLocked}">
                                                Permanently Locked
                                            </c:when>
                                            <c:when test="${selectedPeriod.isLocked}">
                                                Locked
                                            </c:when>
                                            <c:otherwise>
                                                Unlocked
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </c:if>
                        </c:if>
                    </div>

                    <form id="exportForm" class="export-form"
                          action="${pageContext.request.contextPath}/attendance/record/HR" method="post">
                        <input type="hidden" name="exportType" id="exportType">
                        <input type="hidden" name="employeeId" id="exportEmployeeId"
                               value="${employeeId != null ? employeeId : ''}">
                        <input type="hidden" name="department" id="exportDepartment" value="${department}">
                        <input type="hidden" name="startDate" id="exportStartDate" value="${startDate}">
                        <input type="hidden" name="endDate" id="exportEndDate" value="${endDate}">
                        <input type="hidden" name="status" id="exportStatus" value="${status}">
                        <input type="hidden" name="source" id="exportSource" value="${source}">
                        <input type="hidden" name="periodSelect" id="exportPeriodSelect"
                               value="${selectedPeriod != null ? selectedPeriod.id : ''}">
                    </form>

                    <!-- Toast Container -->
                    <div id="toastContainer" class="toast-container"></div>

                    <!-- Hidden messages for toast -->
                    <c:if test="${not empty error}">
                        <div class="hidden-message" data-type="error" data-message="${error}" style="display: none;"></div>
                    </c:if>
                    <c:if test="${not empty message}">
                        <div class="hidden-message" data-type="success" data-message="${message}" style="display: none;"></div>
                    </c:if>

                    <!-- ========== MAIN TABLE ========== -->
                    <div class="table-wrapper">
                        <table id="attendanceTable" class="attendance-table" border="1" cellspacing="0"
                               cellpadding="6">
                            <thead>
                                <tr>
                                    <th>Employee Code</th>
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
                                        <td>${att.employeeCode}</td>
                                        <td>${att.employeeName}</td>
                                        <td>${att.department}</td>
                                        <td>
                                            <c:out value="${att.date}" />
                                        </td>
                                        <td>
                                            <c:if test="${att.checkIn != null}">
                                                ${att.checkIn.toString().substring(0,5)}</c:if>
                                            </td>
                                            <td>
                                            <c:if test="${att.checkOut != null}">
                                                ${att.checkOut.toString().substring(0,5)}</c:if>
                                            </td>
                                            <td>
                                            <c:out value="${att.status}" />
                                        </td>
                                        <td>
                                            <c:out value="${att.source}" />
                                        </td>
                                        <td>
                                            <c:out value="${att.period}" />
                                        </td>
                                        <td class="edit-col" style="display:none;">
                                            <c:if test="${!att.isLocked}">
                                                <form class="actionForm" method="post"
                                                      action="${pageContext.request.contextPath}/attendance/record/HR">
                                                    <input type="hidden" name="userIdEdit"
                                                           value="${att.userId}">
                                                    <input type="hidden" name="employeeNameEdit"
                                                           value="${att.employeeName}">
                                                    <input type="hidden" name="departmentEdit"
                                                           value="${att.department}">
                                                    <input type="hidden" name="dateEdit" value="${att.date}">
                                                    <input type="hidden" name="checkInEdit"
                                                           value="${att.checkIn}">
                                                    <input type="hidden" name="checkOutEdit"
                                                           value="${att.checkOut}">
                                                    <input type="hidden" name="statusEdit"
                                                           value="${att.status}">
                                                    <input type="hidden" name="sourceEdit"
                                                           value="${att.source}">
                                                    <input type="hidden" name="periodEdit"
                                                           value="${att.period}">

                                                    <input type="hidden" name="employeeId"
                                                           value="${employeeId != null ? employeeId : ''}">
                                                    <input type="hidden" name="department"
                                                           value="${department}">
                                                    <input type="hidden" name="startDate" value="${startDate}">
                                                    <input type="hidden" name="endDate" value="${endDate}">
                                                    <input type="hidden" name="status" value="${status}">
                                                    <input type="hidden" name="source" value="${source}">
                                                    <input type="hidden" name="periodSelect"
                                                           value="${selectedPeriod != null ? selectedPeriod.id : ''}">

                                                    <input type="hidden" class="formAction" name="action"
                                                           value="">
                                                    <button type="button" class="btn btn-update-row"
                                                            onclick="submitAction(this, 'update')">Update</button>
                                                    <button type="button" class="btn btn-delete-row"
                                                            onclick="submitAction(this, 'delete')">Delete</button>
                                                </form>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <!-- ========== PAGINATION ========== -->
                    <div class="pagination-wrapper" style="margin-top: 10px;">
                        <form id="paginationForm" method="get"
                              action="${pageContext.request.contextPath}/attendance/record/HR">
                            <!-- Giữ tất cả filter hiện tại -->
                            <input type="hidden" name="employeeId" value="${employeeId != null ? employeeId : ''}" />
                            <input type="hidden" name="department" value="${department}" />
                            <input type="hidden" name="status" value="${status}" />
                            <input type="hidden" name="source" value="${source}" />
                            <input type="hidden" name="startDate" value="${startDate}" />
                            <input type="hidden" name="endDate" value="${endDate}" />
                            <input type="hidden" name="periodSelect"
                                   value="${selectedPeriod != null ? selectedPeriod.id : ''}" />

                            <!-- Previous -->
                            <c:if test="${currentPage > 1}">
                                <button type="submit" name="page" value="${currentPage - 1}"
                                        class="pagination-link">Previous</button>
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
                                <button type="submit" name="page" value="1" class="pagination-link">1</button>
                                <c:if test="${startPage > 2}">
                                    <span class="pagination-dots">...</span>
                                </c:if>
                            </c:if>

                            <!-- Hiển thị các trang trong khoảng startPage đến endPage -->
                            <c:forEach var="i" begin="${startPage}" end="${endPage}">
                                <c:choose>
                                    <c:when test="${i == currentPage}">
                                        <span class="pagination-current">${i}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="submit" name="page" value="${i}"
                                                class="pagination-link">${i}</button>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                            <!-- Hiển thị dấu ... và trang cuối nếu cần -->
                            <c:if test="${endPage < totalPages}">
                                <c:if test="${endPage < totalPages - 1}">
                                    <span class="pagination-dots">...</span>
                                </c:if>
                                <button type="submit" name="page" value="${totalPages}"
                                        class="pagination-link">${totalPages}</button>
                            </c:if>

                            <!-- Next -->
                            <c:if test="${currentPage < totalPages}">
                                <button type="submit" name="page" value="${currentPage + 1}"
                                        class="pagination-link">Next</button>
                            </c:if>
                        </form>
                    </div>
                    <!-- Edit Modal -->
                    <div id="editModal" class="modal" style="display:none;">
                        <div class="modal-content">
                            <span class="close-btn" onclick="closeModal()">&times;</span>
                            <h3>Edit Attendance</h3>

                            <form id="editForm" method="post">
                                <input type="hidden" name="action" value="update">

                                <input type="hidden" name="checkInOld" id="checkInOld">
                                <input type="hidden" name="checkOutOld" id="checkOutOld">

                                <!-- Thêm filter -->
                                <input type="hidden" name="employeeId" value="${employeeId != null ? employeeId : ''}">
                                <input type="hidden" name="department" value="${department}">
                                <input type="hidden" name="startDate" value="${startDate}">
                                <input type="hidden" name="endDate" value="${endDate}">
                                <input type="hidden" name="status" value="${status}">
                                <input type="hidden" name="source" value="${source}">
                                <input type="hidden" name="periodSelect"
                                       value="${selectedPeriod != null ? selectedPeriod.id : ''}">

                                <label for="modalEmpId">Employee ID:</label>
                                <input type="text" name="userIdUpdate" id="modalEmpId" readonly>

                                <label for="modalEmpName">Employee Name:</label>
                                <input type="text" name="employeeNameUpdate" id="modalEmpName" readonly>

                                <label for="modalDepartment">Department:</label>
                                <input type="text" name="departmentUpdate" id="modalDepartment" readonly>

                                <label for="modalDate">Date:</label>
                                <input type="date" name="dateUpdate" id="modalDate" readonly>

                                <label for="modalCheckIn">Check-in:</label>
                                <input type="time" name="checkInUpdate" id="modalCheckIn">

                                <label for="modalCheckOut">Check-out:</label>
                                <input type="time" name="checkOutUpdate" id="modalCheckOut">

                                <label for="modalStatus">Status:</label>
                                <input type="text" name="statusUpdate" id="modalStatus" readonly
                                       style="background-color: #f8f9fa; color: #6c757d;"
                                       placeholder="Will be calculated automatically">

                                <label for="modalSource">Source:</label>
                                <input type="text" name="sourceUpdate" id="modalSource" readonly>

                                <label for="modalPeriod">Period:</label>
                                <input type="text" name="periodUpdate" id="modalPeriod" readonly>

                                <button type="button" onclick="submitEdit()">Save</button>
                            </form>
                        </div>
                    </div>

                    <!-- Delete Confirmation Modal -->
                    <div id="deleteConfirmModal" class="modal delete-modal" style="display:none;">
                        <div class="modal-content delete-modal-content">
                            <div class="delete-modal-header">
                                <div class="delete-icon">
                                    <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                        <path d="M12 9V13M12 17H12.01M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="#dc3545" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                                    </svg>
                                </div>
                                <h3>Confirm Delete</h3>
                            </div>
                            
                            <div class="delete-modal-body">
                                <p>Are you sure you want to delete this attendance record?</p>
                                <div class="delete-details">
                                    <div class="detail-grid">
                                        <div class="detail-column">
                                            <div class="detail-item">
                                                <strong>Employee:</strong> 
                                                <span id="deleteEmployeeName"></span>
                                            </div>
                                            <div class="detail-item">
                                                <strong>Date:</strong> 
                                                <span id="deleteDate"></span>
                                            </div>
                                            <div class="detail-item">
                                                <strong>Check-in:</strong> 
                                                <span id="deleteCheckIn"></span>
                                            </div>
                                            <div class="detail-item">
                                                <strong>Status:</strong> 
                                                <span id="deleteStatus"></span>
                                            </div>
                                        </div>
                                        <div class="detail-column">
                                            <div class="detail-item">
                                                <strong>ID:</strong> 
                                                <span id="deleteEmployeeId"></span>
                                            </div>
                                            <div class="detail-item">
                                                <strong>Department:</strong> 
                                                <span id="deleteDepartment"></span>
                                            </div>
                                            <div class="detail-item">
                                                <strong>Check-out:</strong> 
                                                <span id="deleteCheckOut"></span>
                                            </div>
                                            <div class="detail-item">
                                                <strong>Source:</strong> 
                                                <span id="deleteSource"></span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <p class="delete-warning">This action cannot be undone.</p>
                            </div>
                            
                            <div class="delete-modal-footer">
                                <button type="button" class="btn btn-cancel" onclick="closeDeleteModal()">Cancel</button>
                                <button type="button" class="btn btn-delete-confirm" onclick="confirmDelete()">Delete</button>
                            </div>
                        </div>
                    </div>
                </main>
            </div>
        </div>
        <script>
            // Header dropdown functionality
            document.addEventListener('DOMContentLoaded', function () {
                const dropdowns = document.querySelectorAll('.dropdown');

                dropdowns.forEach(dropdown => {
                    const toggle = dropdown.querySelector('.dropdown-toggle');
                    const menu = dropdown.querySelector('.dropdown-menu');

                    if (!toggle || !menu)
                        return;

                    toggle.addEventListener('click', function (e) {
                        e.preventDefault();
                        e.stopPropagation();

                        // Close other dropdowns
                        document.querySelectorAll('.dropdown-menu').forEach(otherMenu => {
                            if (otherMenu !== menu && otherMenu.classList.contains('show')) {
                                otherMenu.classList.remove('show');
                            }
                        });

                        // Toggle current dropdown
                        menu.classList.toggle('show');
                    });
                });

                // Close dropdowns when clicking outside
                document.addEventListener('click', function (e) {
                    if (!e.target.closest('.dropdown')) {
                        document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
                            menu.classList.remove('show');
                        });
                    }
                });
            });
        </script>
        <script src="${pageContext.request.contextPath}/assets/js/attendance-record-HR.js"></script>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/toast-notification.css">
        <script src="${pageContext.request.contextPath}/assets/js/toast-notification.js"></script>
    </body>
</html>