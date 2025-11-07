<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Import Attendance</title>
        <jsp:include page="../layout/head.jsp">
            <jsp:param name="pageTitle" value="Import Attendance - HRMS" />
            <jsp:param name="pageCss" value="import-attendance.css" />
        </jsp:include>
    </head>

    <body class="import-attendance-page">
        <c:set var="activeTab" value="${activeTab != null ? activeTab : 'upload'}" />
        <jsp:include page="../layout/sidebar.jsp">
            <jsp:param name="currentPage" value="attendance-record-emp" />
        </jsp:include>

        <!-- Main wrapper: header + content -->
        <div class="main-content" id="main-content">
            <jsp:include page="../layout/dashboard-header.jsp" />

            <div class="content-area">
                <h2 class="page-title">Import Attendance</h2>

                <div class="tab-header">
                    <button class="tab-btn upload-tab-btn" id="upload-btn">Upload File</button>
                    <button class="tab-btn manual-tab-btn" id="manual-btn">Manual Entry</button>
                </div>
                <hr class="tab-divider" />

                <!-- Upload File Tab -->
                <div id="upload" class="tab-content upload-tab">
                    <h3 class="section-title">Upload File (Excel)</h3>

                    <form class="upload-form" action="${pageContext.request.contextPath}/attendance/import"
                          method="post" enctype="multipart/form-data">
                        <input type="hidden" name="activeTab" value="upload" />
                        <div class="form-group">
                            <label class="form-label" for="fileInput">Select File to Import:</label>
                            <div class="file-upload">
                                <input type="file" id="fileInput" name="file" accept=".xlsx,.csv"
                                       class="form-input file-input" />
                                <p id="fileName" class="form-text">(Accepted formats: .xlsx, .csv)</p>
                            </div>
                        </div>

                        <div class="form-group btn-group">
                            <button type="submit" id="preview" name="action" value="Preview"
                                    class="form-button btn-primary">Preview</button>
                            <button type="submit" id="import" name="action" value="Import"
                                    class="form-button btn-secondary">Import</button>
                            <button type="submit" id="delete" name="action" value="Delete"
                                    class="form-button btn-danger">Delete</button>
                        </div>

                        <!-- Success/Error Messages (Hidden - will show as toast) -->
                        <c:if test="${not empty error}">
                            <div class="hidden-message" data-type="error" data-message="${error}"></div>
                        </c:if>
                        <c:if test="${not empty success}">
                            <div class="hidden-message" data-type="success" data-message="${success}"></div>
                        </c:if>
                        <c:if test="${not empty warning}">
                            <div class="hidden-message" data-type="warning" data-message="${warning}"></div>
                        </c:if>
                        <c:if test="${not empty message}">
                            <div class="hidden-message" data-type="error" data-message="${message}"></div>
                        </c:if>

                        <!-- Preview Table -->
                        <c:if test="${not empty previewLogs or not empty invalidLogsExcel}">
                            <h4 class="preview-title">Attendance Data (Preview)</h4>

                            <table class="preview-data-table">
                                <thead>
                                    <tr class="table-header">
                                        <th>Employee ID</th>
                                        <th>Employee Name</th>
                                        <th>Department</th>
                                        <th>Date</th>
                                        <th>Check-in</th>
                                        <th>Check-out</th>
                                        <th>Status</th>
                                        <th>Source</th>
                                        <th>Period</th>
                                            <c:if test="${not empty invalidLogsExcel}">
                                            <th>Error</th>
                                            </c:if>
                                    </tr>
                                </thead>
                                <tbody>
                                    <!-- Hiển thị bản ghi hợp lệ (valid) -->
                                    <c:forEach var="log" items="${previewLogs}">
                                        <tr class="data-row">
                                            <td>${log.userId}</td>
                                            <td>${log.employeeName}</td>
                                            <td>${log.department}</td>
                                            <td>${log.date}</td>
                                            <td>${log.checkIn}</td>
                                            <td>${log.checkOut}</td>
                                            <td>${log.status}</td>
                                            <td>${log.source}</td>
                                            <td>${log.period}</td>
                                            <td></td>
                                        </tr>
                                    </c:forEach>

                                    <c:forEach var="log" items="${invalidLogsExcel}">
                                        <tr class="data-row" style="background-color: #ffe6e6;">
                                            <td>${log.userId}</td>
                                            <td>${log.employeeName}</td>
                                            <td>${log.department}</td>
                                            <td>${log.date}</td>
                                            <td>${log.checkIn}</td>
                                            <td>${log.checkOut}</td>
                                            <td>${log.status}</td>
                                            <td>${log.source}</td>
                                            <td>${log.period}</td>
                                            <td>${log.error}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>

                            <!-- ✅ Pagination for Preview Records -->
                            <!-- Pagination -->
                            <c:if test="${totalPages > 1}">
                                <div class="pagination preview-pagination">
                                    <c:if test="${currentPage > 1}">
                                        <a href="?action=Preview&page=${currentPage - 1}"
                                           class="page-btn prev-btn">Previous</a>
                                    </c:if>
                                    <c:set var="startPage" value="${currentPage - 1}" />
                                    <c:set var="endPage" value="${currentPage + 1}" />

                                    <c:if test="${startPage < 1}">
                                        <c:set var="startPage" value="1" />
                                    </c:if>

                                    <c:if test="${endPage > totalPages}">
                                        <c:set var="endPage" value="${totalPages}" />
                                    </c:if>

                                    <!-- Hiển thị số 1 + dấu ... nếu cách từ startPage > 2 -->
                                    <c:if test="${startPage > 2}">
                                        <a href="?action=Preview&page=1" class="page-btn">1</a>
                                        <span>...</span>
                                    </c:if>
                                    <!-- Nếu startPage = 2 thì chỉ hiển thị 1 bình thường -->
                                    <c:if test="${startPage == 2}">
                                        <a href="?action=Preview&page=1" class="page-btn">1</a>
                                    </c:if>

                                    <!-- Các trang chính giữa -->
                                    <c:forEach var="i" begin="${startPage}" end="${endPage}">
                                        <c:choose>
                                            <c:when test="${i == currentPage}">
                                                <a href="javascript:void(0)" class="page-btn current-page">${i}</a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="?action=Preview&page=${i}" class="page-btn">${i}</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>

                                    <!-- Hiển thị dấu ... + trang cuối nếu khoảng cách > 1 -->
                                    <c:if test="${endPage < totalPages - 1}">
                                        <span>...</span>
                                        <a href="?action=Preview&page=${totalPages}"
                                           class="page-btn">${totalPages}</a>
                                    </c:if>
                                    <!-- Nếu endPage liền kề trang cuối thì chỉ hiển thị trang cuối -->
                                    <c:if test="${endPage == totalPages - 1}">
                                        <a href="?action=Preview&page=${totalPages}"
                                           class="page-btn">${totalPages}</a>
                                    </c:if>

                                    <c:if test="${currentPage < totalPages}">
                                        <a href="?action=Preview&page=${currentPage + 1}"
                                           class="page-btn next-btn">Next</a>
                                    </c:if>
                                </div>
                            </c:if>

                            <!-- ✅ Pagination for Invalid Logs (reuse preview CSS) -->
                            <c:if test="${not empty invalidLogsExcel and invalidTotalPages > 1}">
                                <div class="pagination preview-pagination">
                                    <c:if test="${invalidCurrentPage > 1}">
                                        <a href="?action=Import&invalidPage=${invalidCurrentPage - 1}"
                                           class="page-btn prev-btn">Previous</a>
                                    </c:if>

                                    <c:set var="startPage" value="${invalidCurrentPage - 1}" />
                                    <c:set var="endPage" value="${invalidCurrentPage + 1}" />

                                    <c:if test="${startPage < 1}">
                                        <c:set var="startPage" value="1" />
                                    </c:if>

                                    <c:if test="${endPage > invalidTotalPages}">
                                        <c:set var="endPage" value="${invalidTotalPages}" />
                                    </c:if>

                                    <!-- Hiển thị số 1 + dấu ... nếu cách từ startPage > 2 -->
                                    <c:if test="${startPage > 2}">
                                        <a href="?action=Import&invalidPage=1" class="page-btn">1</a>
                                        <span>...</span>
                                    </c:if>
                                    <!-- Nếu startPage = 2 thì chỉ hiển thị 1 bình thường -->
                                    <c:if test="${startPage == 2}">
                                        <a href="?action=Import&invalidPage=1" class="page-btn">1</a>
                                    </c:if>

                                    <!-- Các trang chính giữa -->
                                    <c:forEach var="i" begin="${startPage}" end="${endPage}">
                                        <c:choose>
                                            <c:when test="${i == invalidCurrentPage}">
                                                <a href="javascript:void(0)" class="page-btn current-page">${i}</a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="?action=Import&invalidPage=${i}" class="page-btn">${i}</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>

                                    <!-- Hiển thị dấu ... + trang cuối nếu khoảng cách > 1 -->
                                    <c:if test="${endPage < invalidTotalPages - 1}">
                                        <span>...</span>
                                        <a href="?action=Import&invalidPage=${invalidTotalPages}"
                                           class="page-btn">${invalidTotalPages}</a>
                                    </c:if>
                                    <!-- Nếu endPage liền kề trang cuối thì chỉ hiển thị trang cuối -->
                                    <c:if test="${endPage == invalidTotalPages - 1}">
                                        <a href="?action=Import&invalidPage=${invalidTotalPages}"
                                           class="page-btn">${invalidTotalPages}</a>
                                    </c:if>

                                    <c:if test="${invalidCurrentPage < invalidTotalPages}">
                                        <a href="?action=Import&invalidPage=${invalidCurrentPage + 1}"
                                           class="page-btn next-btn">Next</a>
                                    </c:if>
                                </div>
                            </c:if>
                        </c:if>
                    </form>
                </div>

                <!-- Manual Tab -->
                <div id="manual" class="tab-content manual-tab">
                    <div class="manual-inner">
                        <h3 class="section-title">Manual Entry</h3>

                        <div id="manualFeedback" class="feedback-message" style="display: none;"></div>

                        <!-- Manual Messages (Hidden - will show as toast) -->
                        <c:if test="${not empty manualError}">
                            <div class="hidden-message" data-type="error" data-message="${manualError}"></div>
                        </c:if>

                        <c:if test="${not empty manualSuccess}">
                            <div class="hidden-message" data-type="success" data-message="${manualSuccess}"></div>
                        </c:if>

                        <table id="manualTable" class="excel-table manual-table">
                            <thead>
                                <tr class="table-header">
                                    <th><input type="checkbox" id="selectAllRows"></th>
                                    <th>Employee</th>
                                    <th>Date</th>
                                    <th>Check-in</th>
                                    <th>Check-out</th>
                                        <c:if test="${not empty invalidLogs}">
                                        <th>Error</th>
                                        </c:if>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty invalidLogs}">
                                        <c:forEach var="log" items="${invalidLogs}">
                                            <tr class="manual-row invalid-row">
                                                <td class="manual-cell">
                                                    <input type="checkbox" class="row-select">
                                                </td>
                                                <td class="manual-cell">
                                                    <div class="employee-select-wrapper">
                                                        <input type="text" name="employeeName"
                                                               class="form-control employee-input"
                                                               placeholder="Select an employee"
                                                               value="${log.employeeName}"
                                                               style="background-color:#ffe6e6;"
                                                               onfocus="window.showEmployeeListJSP ? window.showEmployeeListJSP(this) : showEmployeeList(this)"
                                                               oninput="window.filterEmployeeListJSP ? window.filterEmployeeListJSP(this) : filterEmployeeList(this)">
                                                        <input type="hidden" name="employeeId" value="${log.userId}"
                                                               class="employee-id-hidden">

                                                        <ul class="custom-dropdown">
                                                            <c:forEach var="emp" items="${uList}">
                                                                <li data-id="${emp.id}"
                                                                    onclick="selectEmployee(this)">
                                                                    ${emp.employeeCode} - ${emp.fullName}</li>
                                                                </c:forEach>
                                                        </ul>
                                                    </div>
                                                </td>
                                                <td class="manual-cell">
                                                    <input type="date" class="form-control date-input"
                                                           value="${log.date}" style="background-color:#ffe6e6;">
                                                </td>
                                                <td class="manual-cell">
                                                    <input type="time" class="form-control checkin-input"
                                                           value="${log.checkIn}" style="background-color:#ffe6e6;">
                                                </td>
                                                <td class="manual-cell">
                                                    <input type="time" class="form-control checkout-input"
                                                           value="${log.checkOut}" style="background-color:#ffe6e6;">
                                                </td>
                                                <td class="manual-cell error-cell">
                                                    <span class="error-text">${log.error}</span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr class="manual-row">
                                            <td class="manual-cell"><input type="checkbox" class="row-select"></td>
                                            <td class="manual-cell">
                                                <div class="employee-select-wrapper">
                                                    <input type="text" name="employeeName"
                                                           class="form-control employee-input"
                                                           placeholder="Select an employee"
                                                           onfocus="window.showEmployeeListJSP ? window.showEmployeeListJSP(this) : showEmployeeList(this)"
                                                           oninput="window.filterEmployeeListJSP ? window.filterEmployeeListJSP(this) : filterEmployeeList(this)">
                                                    <input type="hidden" name="employeeId"
                                                           class="employee-id-hidden">
                                                    <ul class="custom-dropdown">
                                                        <c:forEach var="emp" items="${uList}">
                                                            <li data-id="${emp.id}" onclick="selectEmployee(this)">
                                                                ${emp.employeeCode} - ${emp.fullName}</li>
                                                            </c:forEach>
                                                    </ul>
                                                </div>
                                            </td>
                                            <td class="manual-cell"><input type="date"
                                                                           class="form-control date-input"></td>
                                            <td class="manual-cell">
                                                <input type="time" class="form-control checkin-input">
                                                <div class="error-message"></div>
                                            </td>
                                            <td class="manual-cell">
                                                <input type="time" class="form-control checkout-input">
                                                <div class="error-message"></div>
                                            </td>
                                            <c:if test="${not empty invalidLogs}">
                                                <td class="manual-cell error-cell">
                                                    <span class="error-text"></span>
                                                </td>
                                            </c:if>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>

                        <template id="manualRowTemplate">
                            <tr class="manual-row">
                                <td class="manual-cell"><input type="checkbox" class="row-select"></td>
                                <td class="manual-cell">
                                    <div class="employee-select-wrapper">
                                        <input type="text" name="employeeName" class="form-control employee-input"
                                               placeholder="Select an employee"
                                               onfocus="window.showEmployeeListJSP ? window.showEmployeeListJSP(this) : showEmployeeList(this)"
                                               oninput="window.filterEmployeeListJSP ? window.filterEmployeeListJSP(this) : filterEmployeeList(this)">
                                        <input type="hidden" name="employeeId" class="employee-id-hidden">
                                        <ul class="custom-dropdown">
                                            <c:forEach var="emp" items="${uList}">
                                                <li data-id="${emp.id}" onclick="selectEmployee(this)">
                                                    ${emp.employeeCode} - ${emp.fullName}</li>
                                                </c:forEach>
                                        </ul>
                                    </div>
                                </td>
                                <td class="manual-cell"><input type="date" class="form-control date-input"></td>
                                <td class="manual-cell"><input type="time" class="form-control checkin-input"></td>
                                <td class="manual-cell"><input type="time" class="form-control checkout-input"></td>
                            </tr>
                        </template>

                        <div class="manual-btn-wrapper d-flex gap-2 mt-3">
                            <button type="button" id="addRowBtn" class="btn btn-secondary">
                                <i class="fas fa-plus"></i> Add Row
                            </button>
                            <button type="button" id="deleteRowBtn" class="btn btn-danger">
                                <i class="fas fa-trash"></i> Delete Row
                            </button>
                            <form id="manualImportForm"
                                  action="${pageContext.request.contextPath}/attendance/import" method="post">
                                <input type="hidden" name="activeTab" value="manual" />
                                <input type="hidden" name="action" value="ManualImport" />
                                <input type="hidden" id="manualData" name="manualData" />
                                <button type="submit" class="btn btn-import">Import</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Delete Confirmation Modal -->
        <div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="deleteConfirmModalLabel">
                            <i class="fas fa-exclamation-triangle text-warning"></i> Confirm Delete
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p id="deleteMessage"></p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-danger" id="confirmDeleteBtn">Delete</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Toast Notification Container -->
        <div id="toastContainer" style="position: fixed; top: 80px; right: 20px; z-index: 99999 !important; min-width: 350px; pointer-events: none;"></div>

        <script>
            document.addEventListener("DOMContentLoaded", () => {
                function showTab(tabId) {
                    // Ẩn tất cả tab
                    document.querySelectorAll(".tab-content").forEach(tab => {
                        tab.classList.remove("active");
                        tab.style.display = "none";
                    });

                    // Hiện tab được chọn
                    const selectedTab = document.getElementById(tabId);
                    if (selectedTab) {
                        selectedTab.classList.add("active");
                        selectedTab.style.display = "block";
                    }

                    // Cập nhật nút
                    document.querySelectorAll(".tab-btn").forEach(btn => {
                        btn.classList.remove("active");
                        btn.setAttribute("aria-selected", "false");
                    });

                    const activeBtn = document.getElementById(tabId + "-btn");
                    if (activeBtn) {
                        activeBtn.classList.add("active");
                        activeBtn.setAttribute("aria-selected", "true");
                    }

                    // Cập nhật hidden input trong form để submit giữ tab
                    document.querySelectorAll('input[name="activeTab"]').forEach(input => input.value = tabId);
                }

                // Lấy giá trị từ JSTL
                const activeTab = "${activeTab}";
                showTab(activeTab);

                // Khi click nút tab
                document.querySelectorAll(".tab-btn").forEach(btn => {
                    btn.addEventListener("click", () => {
                        const tabId = btn.id.replace("-btn", "");
                        showTab(tabId);
                    });
                });
            });
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/import-attendance.js"></script>
    </body>
</html>