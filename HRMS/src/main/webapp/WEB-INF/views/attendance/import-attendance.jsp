<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <html>

        <head>
            <title>Import Attendance</title>
            <jsp:include page="../layout/head.jsp">
                <jsp:param name="pageTitle" value="Import Attendance - HRMS" />
                <jsp:param name="pageCss" value="import-attendance.css" />
            </jsp:include>

            <style>
                .employee-select-wrapper {
                    position: relative;
                    width: 100%;
                }

                .employee-input {
                    width: 100%;
                    padding: 8px 12px;
                    border: 1px solid #ccc;
                    border-radius: 8px;
                    font-size: 14px;
                    background-color: #fafafa;
                }

                .custom-dropdown {
                    display: none;
                    position: absolute;
                    top: 105%;
                    left: 0;
                    right: 0;
                    background: white;
                    border: 1px solid #ddd;
                    border-radius: 6px;
                    max-height: 200px;
                    overflow-y: auto;
                    z-index: 1000;
                    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
                }

                .custom-dropdown li {
                    padding: 8px 12px;
                    cursor: pointer;
                }

                .custom-dropdown li:hover {
                    background-color: #007bff;
                    color: #fff;
                }

                /*            invalid row*/
                .invalid-row input,
                .invalid-row select {
                    border: 1px solid #dc3545;
                    background-color: #ffe6e6;
                }

                .alert {
                    padding: 10px 15px;
                    border-radius: 8px;
                    margin-bottom: 10px;
                }

                .alert-danger {
                    background-color: #f8d7da;
                    color: #842029;
                }

                .alert-success {
                    background-color: #d1e7dd;
                    color: #0f5132;
                }

                .employee-select-wrapper {
                    position: relative;
                }

                .manual-row {
                    position: relative;
                    /* để dropdown có stacking context riêng */
                    z-index: 1;
                }

                .manual-row.active-dropdown {
                    z-index: 10;
                    /* row đang mở dropdown luôn nổi trên các row khác */
                }

                .custom-dropdown {
                    position: absolute;
                    top: 100%;
                    left: 0;
                    width: 100%;
                    max-height: 200px;
                    overflow-y: auto;
                    display: none;
                    background: #fff;
                    border: 1px solid #ccc;
                    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
                    z-index: 9999;
                    /* dropdown luôn cao hơn row */
                }

                /* Dropdown vẫn nhận click */
                .manual-row.active-dropdown .employee-select-wrapper,
                .manual-row.active-dropdown .custom-dropdown {
                    pointer-events: auto;
                }
            </style>
            <style>
                .tab-header {
                    display: flex;
                    gap: 10px;
                    margin-bottom: 15px;
                    border-bottom: 2px solid #ddd;
                    padding-bottom: 8px;
                }

                .tab-btn {
                    background-color: #f5f5f5;
                    border: 1px solid #ccc;
                    border-radius: 8px;
                    padding: 8px 16px;
                    cursor: pointer;
                    font-size: 14px;
                    font-weight: 500;
                    transition: all 0.25s ease;
                    color: #333;
                }

                .tab-btn:hover {
                    background-color: #007bff;
                    color: #fff;
                    border-color: #007bff;
                    transform: translateY(-2px);
                    box-shadow: 0 2px 6px rgba(0, 123, 255, 0.3);
                }

                /* Trạng thái tab đang được chọn */
                .tab-btn.active {
                    background-color: #007bff;
                    color: #fff;
                    border-color: #007bff;
                    box-shadow: 0 2px 6px rgba(0, 123, 255, 0.3);
                }
            </style>
            <style>
                .error-message {
                    color: #dc3545;
                    font-size: 12px;
                    margin-top: 4px;
                    display: none;
                    font-weight: 500;
                    line-height: 1.3;
                }

                .error-message:not(:empty) {
                    display: block !important;
                }

                .error-cell {
                    max-width: 200px;
                    word-wrap: break-word;
                    padding: 8px 12px;
                    border: 1px solid #ddd;
                    background-color: #fff;
                    vertical-align: middle;
                }

                .error-text {
                    color: #dc3545;
                    font-size: 12px;
                    font-weight: 500;
                    display: block;
                    line-height: 1.4;
                    margin: 0;
                    padding: 0;
                }

                /* Chỉ áp dụng width cho cột error khi nó hiển thị */
                .manual-table th:last-child,
                .manual-table td:last-child {
                    width: 200px;
                    min-width: 150px;
                }

                /* Style cho manual-cell để đồng bộ */
                .manual-cell {
                    padding: 8px 12px;
                    border: 1px solid #ddd;
                    vertical-align: middle;
                }

                .manual-cell input,
                .manual-cell select {
                    width: 100%;
                    padding: 6px 8px;
                    border: 1px solid #ccc;
                    border-radius: 4px;
                    font-size: 14px;
                }
            </style>

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

                            <!-- Success/Error Messages -->
                            <c:if test="${not empty error}">
                                <div class="form-message error-message">${error}</div>
                            </c:if>
                            <c:if test="${not empty success}">
                                <div class="form-message success-message">${success}</div>
                            </c:if>
                            <c:if test="${not empty warning}">
                                <div class="form-message error-message">${warning}</div>
                            </c:if>
                            <c:if test="${not empty message}">
                                <div class="form-message error-message">${message}</div>
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

                                        <c:if test="${startPage > 1}">
                                            <a href="?action=Preview&page=1" class="page-btn">1</a>
                                            <span>...</span>
                                        </c:if>

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

                                        <c:if test="${endPage < totalPages}">
                                            <span>...</span>
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

                            <div id="manualFeedback" class="feedback-message"></div>

                            <c:if test="${not empty manualError}">
                                <div class="alert alert-danger">${manualError}</div>
                            </c:if>

                            <c:if test="${not empty manualSuccess}">
                                <div class="alert alert-success">${manualSuccess}</div>
                            </c:if>

                            <table id="manualTable" class="excel-table manual-table">
                                <thead>
                                    <tr class="table-header">
                                        <th><input type="checkbox" id="selectAllRows"></th>
                                        <th>Employee</th>
                                        <th>Date</th>
                                        <th>Check-in</th>
                                        <th>Check-out</th>
                                        <th>Status</th>
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
                                                    <td class="manual-cell">
                                                        <select class="form-control status-input"
                                                            style="background-color:#ffe6e6;">
                                                            <option value="">Select status</option>
                                                            <option value="Late" ${log.status=='Late' ? 'selected' : ''
                                                                }>Late</option>
                                                            <option value="On Time" ${log.status=='On Time' ? 'selected'
                                                                : '' }>On Time</option>
                                                            <option value="Shift day" ${log.status=='Shift day'
                                                                ? 'selected' : '' }>Shift day</option>
                                                            <option value="leaving early" ${log.status=='leaving early'
                                                                ? 'selected' : '' }>leaving early</option>
                                                            <option value="Over Time" ${log.status=='Over Time'
                                                                ? 'selected' : '' }>Over Time</option>
                                                        </select>
                                                    </td>
                                                    <c:if test="${not empty invalidLogs}">
                                                        <td class="manual-cell error-cell">
                                                            <span class="error-text">${log.error}</span>
                                                        </td>
                                                    </c:if>
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
                                                <td class="manual-cell">
                                                    <select class="form-control status-input">
                                                        <option value="">Select status</option>
                                                        <option value="Late">Late</option>
                                                        <option value="On Time">On Time</option>
                                                        <option value="Shift day">Shift day</option>
                                                        <option value="leaving early">leaving early</option>
                                                        <option value="Over Time">Over Time</option>
                                                    </select>
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
                                    <td class="manual-cell">
                                        <select class="form-control status-input">
                                            <option value="">Select status</option>
                                            <option value="Late">Late</option>
                                            <option value="On Time">On Time</option>
                                            <option value="Shift day">Shift day</option>
                                            <option value="leaving early">leaving early</option>
                                            <option value="Over Time">Over Time</option>
                                        </select>
                                    </td>
                                    <c:if test="${not empty invalidLogs}">
                                        <td class="manual-cell error-cell">
                                            <span class="error-text"></span>
                                        </td>
                                    </c:if>
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
            <script src="${pageContext.request.contextPath}/assets/js/import-attendance.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
            <script>
                document.getElementById("addRowBtn").addEventListener("click", () => {
                    const tbody = document.querySelector("#manualTable tbody");
                    const template = document.getElementById("manualRowTemplate");
                    const newRow = template.content.cloneNode(true);
                    tbody.appendChild(newRow);
                });
            </script>

            <script>
                document.addEventListener("DOMContentLoaded", () => {

                    function showEmployeeList(input) {
                        const row = input.closest('.manual-row');
                        const wrapper = input.closest('.employee-select-wrapper');
                        const dropdown = wrapper.querySelector('.custom-dropdown');

                        // Ẩn dropdown khác và remove class active
                        document.querySelectorAll('.manual-row').forEach(r => {
                            r.classList.remove('active-dropdown');
                            r.querySelectorAll('.custom-dropdown').forEach(dl => dl.style.display = 'none');
                        });

                        // Thêm class active cho row hiện tại
                        row.classList.add('active-dropdown');

                        // Hiển thị dropdown hiện tại
                        dropdown.style.display = 'block';
                    }

                    function filterEmployeeList(input) {
                        const dropdown = input.closest('.employee-select-wrapper').querySelector('.custom-dropdown');
                        const filter = input.value.toLowerCase();
                        let hasMatch = false;
                        dropdown.querySelectorAll('li').forEach(li => {
                            const match = li.textContent.toLowerCase().includes(filter);
                            li.style.display = match ? '' : 'none';
                            if (match)
                                hasMatch = true;
                        });
                        dropdown.style.display = hasMatch ? 'block' : 'none';
                    }

                    // Debug function to check employee data
                    function debugEmployeeData() {
                        const firstDropdown = document.querySelector('.custom-dropdown');
                        if (firstDropdown) {
                            console.log('Employee dropdown items:');
                            firstDropdown.querySelectorAll('li').forEach((li, index) => {
                                console.log(`${index}: ID=${li.dataset.id}, Text="${li.textContent.trim()}"`);
                            });
                        }
                    }

                    // Call debug function on page load
                    setTimeout(debugEmployeeData, 1000);

                    // Test function to verify JavaScript is working
                    console.log('Employee dropdown JavaScript loaded successfully');

                    // Simple and direct function to select employee
                    window.selectEmployee = function (liElement) {
                        console.log('selectEmployee called with:', liElement);

                        const wrapper = liElement.closest('.employee-select-wrapper');
                        const input = wrapper.querySelector('.employee-input');
                        const hidden = wrapper.querySelector('.employee-id-hidden');
                        const dropdown = wrapper.querySelector('.custom-dropdown');

                        const fullText = liElement.textContent.trim();
                        const employeeId = liElement.getAttribute('data-id');

                        console.log('Setting input value to:', fullText);
                        console.log('Setting hidden value to:', employeeId);

                        // Set values
                        input.value = fullText;
                        hidden.value = employeeId;

                        // Hide dropdown
                        dropdown.style.display = 'none';

                        console.log('Final input value:', input.value);
                        console.log('Final hidden value:', hidden.value);
                    };

                    // Make functions global for debugging
                    window.showEmployeeListJSP = showEmployeeList;
                    window.filterEmployeeListJSP = filterEmployeeList;

                    // Chọn item - Improved version (DISABLED - using onclick instead)
                    /*document.addEventListener('click', e => {
                        console.log('Click event triggered on:', e.target); // Debug log

                        // Try multiple ways to find the li element
                        let li = null;
                        if (e.target.tagName === 'LI' && e.target.closest('.custom-dropdown')) {
                            li = e.target;
                        } else {
                            li = e.target.closest('.custom-dropdown li');
                        }

                        if (li) {
                            console.log('Found li element:', li); // Debug log
                            console.log('Li innerHTML:', li.innerHTML); // Debug log
                            console.log('Li textContent:', li.textContent); // Debug log

                            const wrapper = li.closest('.employee-select-wrapper');
                            const input = wrapper ? wrapper.querySelector('.employee-input') : null;
                            const hidden = wrapper ? wrapper.querySelector('.employee-id-hidden') : null;

                            // Lấy text đầy đủ từ li và set vào input
                            const fullText = li.textContent.trim();
                            console.log('Selected employee fullText:', fullText); // Debug log
                            console.log('Employee ID:', li.dataset.id); // Debug log
                            console.log('Input element:', input); // Debug log
                            console.log('Wrapper element:', wrapper); // Debug log

                            if (input) {
                                // Force set the value
                                input.value = fullText;
                                input.setAttribute('value', fullText);
                                console.log('Input value set to:', input.value); // Debug log

                                // Trigger change event
                                input.dispatchEvent(new Event('change', { bubbles: true }));
                            }

                            if (hidden) {
                                hidden.value = li.dataset.id;
                                console.log('Hidden value set to:', hidden.value); // Debug log
                            }

                            // Hide dropdown
                            const dropdown = wrapper ? wrapper.querySelector('.custom-dropdown') : null;
                            if (dropdown) {
                                dropdown.style.display = 'none';
                            }

                            // Prevent further event propagation
                            e.preventDefault();
                            e.stopPropagation();
                            return false;
                        }

                        // Click ra ngoài → ẩn tất cả dropdown
                        if (!e.target.closest('.employee-select-wrapper')) {
                            document.querySelectorAll('.custom-dropdown').forEach(dl => dl.style.display = 'none');
                        }
                    });*/

                    // Simple click outside to hide dropdown
                    document.addEventListener('click', e => {
                        if (!e.target.closest('.employee-select-wrapper')) {
                            document.querySelectorAll('.custom-dropdown').forEach(dl => dl.style.display = 'none');
                        }
                    });

                    // Focus hoặc click vào input
                    document.addEventListener('focusin', e => {
                        if (e.target.matches('.employee-input')) {
                            showEmployeeList(e.target);
                        }
                    });
                    document.addEventListener('click', e => {
                        if (e.target.matches('.employee-input')) {
                            showEmployeeList(e.target);
                        }
                    });

                });
            </script>
            <script>
                document.addEventListener("DOMContentLoaded", () => {
                    const tbody = document.querySelector("#manualTable tbody");
                    const template = document.getElementById("manualRowTemplate");
                    const selectAll = document.getElementById("selectAllRows");

                    // Delete Row
                    document.getElementById("deleteRowBtn").addEventListener("click", () => {
                        const selectedRows = tbody.querySelectorAll(".row-select:checked");
                        if (selectedRows.length === 0) {
                            alert("Please select at least one row to delete!");
                            return;
                        }

                        if (confirm(`Are you sure you want to delete ${selectedRows.length} row(s)?`)) {
                            selectedRows.forEach(chk => chk.closest("tr").remove());
                        }

                        // Sau khi xóa xong: bỏ chọn tất cả checkbox
                        selectAll.checked = false;
                        tbody.querySelectorAll(".row-select").forEach(chk => chk.checked = false);
                    });

                    // Select / Deselect All
                    selectAll.addEventListener("change", () => {
                        tbody.querySelectorAll(".row-select").forEach(chk => chk.checked = selectAll.checked);
                    });
                });
            </script>
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
            <script>
                document.addEventListener("DOMContentLoaded", () => {
                    const today = new Date().toISOString().split("T")[0]; // YYYY-MM-DD

                    document.querySelectorAll(".date-input").forEach(input => {
                        input.setAttribute("max", today); // chỉ chọn ngày hiện tại trở về
                    });
                });
            </script>
            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    const minTime = "06:00";
                    const maxTime = "23:59";

                    // Frontend validation - hiển thị lỗi trên input
                    function showInputError(input, message) {
                        const errorDiv = input.parentElement.querySelector(".error-message");
                        if (errorDiv) {
                            errorDiv.textContent = message;
                            errorDiv.style.display = "block";
                            errorDiv.style.color = "#dc3545";
                            errorDiv.style.fontSize = "12px";
                            errorDiv.style.marginTop = "4px";
                        }
                        input.style.borderColor = "#dc3545";
                    }

                    function clearInputError(input) {
                        const errorDiv = input.parentElement.querySelector(".error-message");
                        if (errorDiv) {
                            errorDiv.textContent = "";
                            errorDiv.style.display = "none";
                        }
                        input.style.borderColor = "#ccc";
                    }

                    // Backend validation - hiển thị lỗi trong cột Error (chỉ khi có cột Error)
                    function showRowError(row, message) {
                        const errorSpan = row.querySelector(".error-text");
                        if (errorSpan) {
                            errorSpan.textContent = message;
                            errorSpan.style.display = "block";
                        }
                        row.classList.add("invalid-row");
                    }

                    function clearRowError(row) {
                        const errorSpan = row.querySelector(".error-text");
                        if (errorSpan) {
                            errorSpan.textContent = "";
                            errorSpan.style.display = "none";
                        }
                        row.classList.remove("invalid-row");
                    }

                    function isTimeInRange(time) {
                        return time >= minTime && time <= maxTime;
                    }

                    // Frontend validation cho time inputs
                    function validateTimeInput(input) {
                        clearInputError(input);

                        const time = input.value;
                        if (!time) return true; // Empty is OK for individual validation

                        if (!isTimeInRange(time)) {
                            showInputError(input, "Time must be between 06:00 and 23:59");
                            return false;
                        }

                        return true;
                    }

                    // Frontend validation cho check-in vs check-out
                    function validateTimeOrder(checkinInput, checkoutInput) {
                        const checkin = checkinInput.value;
                        const checkout = checkoutInput.value;

                        // Clear previous errors
                        clearInputError(checkinInput);
                        clearInputError(checkoutInput);

                        if (checkin && checkout && checkin >= checkout) {
                            showInputError(checkoutInput, "Check-out must be later than check-in");
                            return false;
                        }

                        return true;
                    }

                    // Backend validation - chỉ validate những thứ cần thiết cho import
                    function validateRowForImport(row) {
                        clearRowError(row);

                        const employeeInput = row.querySelector(".employee-input");
                        const dateInput = row.querySelector(".date-input");
                        const checkinInput = row.querySelector(".checkin-input");
                        const checkoutInput = row.querySelector(".checkout-input");
                        const statusInput = row.querySelector(".status-input");

                        const employee = employeeInput ? employeeInput.value.trim() : "";
                        const date = dateInput ? dateInput.value : "";
                        const checkin = checkinInput ? checkinInput.value : "";
                        const checkout = checkoutInput ? checkoutInput.value : "";
                        const status = statusInput ? statusInput.value : "";

                        // Check if row is completely empty
                        if (!employee && !date && !checkin && !checkout && !status) {
                            return true; // Empty row is OK
                        }

                        // Required fields validation (backend sẽ xử lý các lỗi khác như trùng lặp, employee không tồn tại)
                        if (!employee) {
                            showRowError(row, "Employee is required");
                            return false;
                        }

                        if (!date) {
                            showRowError(row, "Date is required");
                            return false;
                        }

                        if (!checkin) {
                            showRowError(row, "Check-in time is required");
                            return false;
                        }

                        if (!checkout) {
                            showRowError(row, "Check-out time is required");
                            return false;
                        }

                        if (!status) {
                            showRowError(row, "Status is required");
                            return false;
                        }

                        // Check future date
                        if (date) {
                            const selectedDate = new Date(date);
                            const today = new Date();
                            today.setHours(0, 0, 0, 0);
                            if (selectedDate > today) {
                                showRowError(row, "Date cannot be in the future");
                                return false;
                            }
                        }

                        return true;
                    }

                    // Add event listeners to existing rows
                    function addValidationToRow(row) {
                        const checkinInput = row.querySelector(".checkin-input");
                        const checkoutInput = row.querySelector(".checkout-input");

                        // Frontend validation cho time inputs
                        if (checkinInput) {
                            checkinInput.addEventListener("blur", () => {
                                validateTimeInput(checkinInput);
                                if (checkoutInput && checkoutInput.value) {
                                    validateTimeOrder(checkinInput, checkoutInput);
                                }
                            });
                            checkinInput.addEventListener("change", () => {
                                validateTimeInput(checkinInput);
                                if (checkoutInput && checkoutInput.value) {
                                    validateTimeOrder(checkinInput, checkoutInput);
                                }
                            });
                        }

                        if (checkoutInput) {
                            checkoutInput.addEventListener("blur", () => {
                                validateTimeInput(checkoutInput);
                                if (checkinInput && checkinInput.value) {
                                    validateTimeOrder(checkinInput, checkoutInput);
                                }
                            });
                            checkoutInput.addEventListener("change", () => {
                                validateTimeInput(checkoutInput);
                                if (checkinInput && checkinInput.value) {
                                    validateTimeOrder(checkinInput, checkoutInput);
                                }
                            });
                        }
                    }

                    // Validate all rows before form submission
                    function validateAllRowsForImport() {
                        let hasErrors = false;
                        const rows = document.querySelectorAll(".manual-row");

                        rows.forEach(row => {
                            // Validate for import (backend validation)
                            if (!validateRowForImport(row)) {
                                hasErrors = true;
                            }

                            // Also check frontend validation errors
                            const timeInputs = row.querySelectorAll(".checkin-input, .checkout-input");
                            timeInputs.forEach(input => {
                                const errorDiv = input.parentElement.querySelector(".error-message");
                                if (errorDiv && errorDiv.style.display === "block") {
                                    hasErrors = true;
                                }
                            });
                        });

                        return !hasErrors;
                    }



                    // Add form submission validation
                    const importForm = document.getElementById("manualImportForm");
                    if (importForm) {
                        importForm.addEventListener("submit", function (e) {
                            if (!validateAllRowsForImport()) {
                                e.preventDefault();

                                // Hiển thị thông báo lỗi thay vì alert
                                let feedbackDiv = document.getElementById("manualFeedback");
                                if (!feedbackDiv) {
                                    feedbackDiv = document.createElement("div");
                                    feedbackDiv.id = "manualFeedback";
                                    feedbackDiv.className = "alert alert-danger";
                                    feedbackDiv.style.marginBottom = "15px";
                                    const table = document.getElementById("manualTable");
                                    table.parentNode.insertBefore(feedbackDiv, table);
                                }

                                feedbackDiv.textContent = "Please fix all validation errors before importing.";
                                feedbackDiv.style.display = "block";

                                // Scroll to top để user thấy thông báo
                                feedbackDiv.scrollIntoView({ behavior: 'smooth', block: 'center' });

                                return false;
                            } else {
                                // Clear any previous error messages
                                const feedbackDiv = document.getElementById("manualFeedback");
                                if (feedbackDiv) {
                                    feedbackDiv.style.display = "none";
                                }
                            }
                        });
                    }

                    // Initialize validation for existing rows
                    document.querySelectorAll(".manual-row").forEach(addValidationToRow);

                    // Override the add row function to include validation
                    const originalAddRowBtn = document.getElementById("addRowBtn");
                    if (originalAddRowBtn) {
                        originalAddRowBtn.addEventListener("click", () => {
                            // Wait for the new row to be added
                            setTimeout(() => {
                                const newRows = document.querySelectorAll(".manual-row");
                                const lastRow = newRows[newRows.length - 1];
                                if (lastRow) {
                                    addValidationToRow(lastRow);
                                }
                            }, 100);
                        });
                    }
                });
            </script>
        </body>

        </html>