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
                box-shadow: 0 4px 10px rgba(0,0,0,0.1);
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
            .invalid-row input, .invalid-row select {
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
                position: relative; /* để dropdown có stacking context riêng */
                z-index: 1;
            }

            .manual-row.active-dropdown {
                z-index: 10; /* row đang mở dropdown luôn nổi trên các row khác */
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
                box-shadow: 0 2px 6px rgba(0,0,0,0.15);
                z-index: 9999; /* dropdown luôn cao hơn row */
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

                    <form class="upload-form" action="${pageContext.request.contextPath}/attendance/import" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="activeTab" value="upload" />
                        <div class="form-group">
                            <label class="form-label" for="fileInput">Select File to Import:</label>
                            <div class="file-upload">
                                <input type="file" id="fileInput" name="file" accept=".xlsx,.csv" class="form-input file-input" />
                                <p id="fileName" class="form-text">(Accepted formats: .xlsx, .csv)</p>
                            </div>
                        </div>

                        <div class="form-group btn-group">
                            <button type="submit" id="preview" name="action" value="Preview" class="form-button btn-primary">Preview</button>
                            <button type="submit" id="import" name="action" value="Import" class="form-button btn-secondary">Import</button>
                        </div>

                        <c:if test="${not empty error}">
                            <div class="form-message error-message">${error}</div>
                        </c:if>
                        <c:if test="${not empty success}">
                            <div class="form-message success-message">${success}</div>
                        </c:if>

                        <!-- Preview Table -->
                        <c:if test="${not empty previewLogs}">
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
                                    </tr>
                                </thead>
                                <tbody>
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
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>

                            <!-- Pagination -->
                            <c:if test="${totalPages > 1}">
                                <div class="pagination preview-pagination">
                                    <c:if test="${currentPage > 1}">
                                        <a href="?action=Preview&page=${currentPage - 1}" class="page-btn prev-btn">Previous</a>
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
                                        <a href="?action=Preview&page=${totalPages}" class="page-btn">${totalPages}</a>
                                    </c:if>

                                    <c:if test="${currentPage < totalPages}">
                                        <a href="?action=Preview&page=${currentPage + 1}" class="page-btn next-btn">Next</a>
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
                                                               value="${log.employeeName}"
                                                               style="background-color:#ffe6e6;"
                                                               onfocus="showEmployeeList(this)"
                                                               oninput="filterEmployeeList(this)">
                                                        <input type="hidden" name="employeeId" value="${log.userId}" class="employee-id-hidden">

                                                        <ul class="custom-dropdown">
                                                            <c:forEach var="emp" items="${uList}">
                                                                <li data-id="${emp.id}">${emp.employeeCode} - ${emp.fullName}</li>
                                                                </c:forEach>
                                                        </ul>
                                                    </div>
                                                </td>
                                                <td class="manual-cell">
                                                    <input type="date" class="form-control date-input" value="${log.date}" style="background-color:#ffe6e6;">
                                                </td>
                                                <td class="manual-cell">
                                                    <input type="time" class="form-control checkin-input" value="${log.checkIn}" style="background-color:#ffe6e6;">
                                                </td>
                                                <td class="manual-cell">
                                                    <input type="time" class="form-control checkout-input" value="${log.checkOut}" style="background-color:#ffe6e6;">
                                                </td>
                                                <td class="manual-cell">
                                                    <select class="form-control status-input" style="background-color:#ffe6e6;">
                                                        <option value="">Select status</option>
                                                        <option value="Late" ${log.status == 'Late' ? 'selected' : ''}>Late</option>
                                                        <option value="On Time" ${log.status == 'On Time' ? 'selected' : ''}>On Time</option>
                                                        <option value="Shift day" ${log.status == 'Shift day' ? 'selected' : ''}>Shift day</option>
                                                    </select>
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
                                                           placeholder="Select or type name"
                                                           onfocus="showEmployeeList(this)"
                                                           oninput="filterEmployeeList(this)">
                                                    <input type="hidden" name="employeeId" class="employee-id-hidden">
                                                    <ul class="custom-dropdown">
                                                        <c:forEach var="emp" items="${uList}">
                                                            <li data-id="${emp.id}">${emp.employeeCode} - ${emp.fullName}</li>
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
                                                </select>
                                            </td>
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
                                        <input type="text" name="employeeName"
                                               class="form-control employee-input"
                                               placeholder="Select or type name"
                                               onfocus="showEmployeeList(this)"
                                               oninput="filterEmployeeList(this)">
                                        <input type="hidden" name="employeeId" class="employee-id-hidden">
                                        <ul class="custom-dropdown">
                                            <c:forEach var="emp" items="${uList}">
                                                <li data-id="${emp.id}">${emp.employeeCode} - ${emp.fullName}</li>
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
                                    </select>
                                </td>
                            </tr>
                        </template>

                        <div class="manual-btn-wrapper d-flex gap-2 mt-3">                          
                            <button type="button" id="addRowBtn" class="btn btn-secondary">
                                <i class="fas fa-plus"></i> Add Row
                            </button>
                            <button type="button" id="deleteRowBtn" class="btn btn-danger">
                                <i class="fas fa-trash"></i> Delete Row
                            </button>
                            <form id="manualImportForm" action="${pageContext.request.contextPath}/attendance/import" method="post">
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

                // Chọn item
                document.addEventListener('click', e => {
                    const li = e.target.closest('.custom-dropdown li');
                    if (li) {
                        const wrapper = li.closest('.employee-select-wrapper');
                        const input = wrapper.querySelector('.employee-input');
                        const hidden = wrapper.querySelector('.employee-id-hidden');

                        input.value = li.textContent.trim();
                        hidden.value = li.dataset.id;

                        wrapper.querySelector('.custom-dropdown').style.display = 'none';
                        return;
                    }

                    // Click ra ngoài → ẩn tất cả dropdown
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
    </body>
</html>
