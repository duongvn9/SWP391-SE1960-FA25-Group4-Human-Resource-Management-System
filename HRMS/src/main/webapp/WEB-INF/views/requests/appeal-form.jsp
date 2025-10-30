<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>

    <head>
        <title>Submit Attendance Dispute - HRMS</title>
        <jsp:include page="../layout/head.jsp">
            <jsp:param name="pageTitle" value="Submit Attendance Dispute - HRMS" />
        </jsp:include>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/appeal-form.css">
        <style>
            /* Overlay cho popup */
            #selectRecordPopup {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                display: flex;
                align-items: center;
                justify-content: center;
                background-color: rgba(0, 0, 0, 0.5);
                z-index: 1050;
            }

            /* Ẩn popup mặc định */
            #selectRecordPopup.d-none {
                display: none;
            }

            /* Nội dung popup */
            #selectRecordPopup .popup-content {
                background-color: #fff;
                padding: 20px;
                width: 90%;
                max-width: 900px;
                max-height: 80vh;
                overflow-y: auto;
                border-radius: 8px;
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
                position: relative;
            }

            /* Header popup */
            #selectRecordPopup .popup-header {
                border-bottom: 1px solid #ddd;
                padding-bottom: 10px;
                margin-bottom: 15px;
            }

            /* Các nút đóng */
            #selectRecordPopup button#closePopupBtn,
            #selectRecordPopup button#closePopupBtn2 {
                background: none;
                border: none;
                font-weight: bold;
                font-size: 1.2rem;
                cursor: pointer;
            }

            /* Table responsive */
            #selectRecordPopup table {
                width: 100%;
                border-collapse: collapse;
            }

            #selectRecordPopup table th,
            #selectRecordPopup table td {
                text-align: center;
                padding: 8px;
                border: 1px solid #dee2e6;
            }

            #selectRecordPopup table th {
                background-color: #f8f9fa;
            }

            /* Footer nút action */
            #selectRecordPopup .popup-actions {
                margin-top: 15px;
            }
        </style>
    </head>

    <body>
        <!-- Sidebar -->
        <jsp:include page="../layout/sidebar.jsp">
            <jsp:param name="currentPage" value="appeal-form" />
        </jsp:include>

        <!-- Main Content -->
        <div class="main-content" id="main-content">
            <!-- Header -->
            <jsp:include page="../layout/dashboard-header.jsp" />

            <!-- Content Area -->
            <div class="content-area">
                <!-- Page Head -->
                <div class="page-head d-flex justify-content-between align-items-center">
                    <div>
                        <h2 class="page-title">
                            <i class="fas fa-user-clock me-2"></i> Submit Attendance Dispute
                        </h2>
                        <p class="page-subtitle">File a dispute for incorrect or missing attendance logs</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/requests" class="btn btn-outline-secondary">
                        <i class="fas fa-list me-1"></i> View All Requests
                    </a>
                </div>

                <!-- Alert Messages -->
                <c:if test="${not empty message}">
                    <div class="alert alert-warning" role="alert">
                        <i class="fas fa-exclamation-circle me-2"></i>
                        ${message}
                    </div>
                </c:if>

                <c:if test="${not empty success}">
                    <div class="alert alert-success" role="alert">
                        <i class="fas fa-check-circle me-2"></i>
                        ${success}
                    </div>
                </c:if>

                <!-- Form Card -->
                <div class="card">
                    <div class="card-header">
                        <h4><i class="fas fa-clipboard-check me-2"></i> Attendance Dispute Form</h4>
                    </div>

                    <div class="card-body">
                        <form method="post" action="${pageContext.request.contextPath}/requests/appeal/create"
                              enctype="multipart/form-data" id="appealForm">

                            <!-- Hidden Request Type -->
                            <input type="hidden" name="request_type_id" value="${requestTypeId}" />

                            <!-- Select Attendance Record -->
                            <div class="mb-3">
                                <label class="form-label">
                                    <i class="fas fa-calendar-alt"></i> Attendance Record
                                    <span class="text-danger">*</span>
                                </label>
                                <div class="d-flex gap-2 align-items-center">
                                    <button type="button" id="selectRecordBtn" class="btn btn-primary">
                                        Select record
                                    </button>
                                </div>
                                <!-- Thông báo lỗi hiển thị tại đây -->
                                <div id="attendanceRecordError" class="text-danger mt-1" style="display:none;">
                                    Please select at least one attendance record.
                                </div>

                                <!-- Selected records list -->
                                <c:if test="${not empty records}">
                                    <div id="selectedRecordsList" class="mt-3 row g-3">
                                        <c:forEach var="rec" items="${records}">
                                            <div class="col-12 border p-3 rounded mb-2 position-relative">
                                                <!-- Nút xóa bản ghi -->
                                                <button type="button"
                                                        class="btn btn-sm btn-danger position-absolute top-0 end-0 m-2 remove-record-btn"
                                                        data-userid="${rec.userId}" title="Remove Record">X</button>

                                                <!-- Thông tin bản ghi đã chọn (không sửa) -->
                                                <div class="mb-2">
                                                    <strong>Selected Record:</strong>
                                                    <table class="table table-sm table-bordered mt-1">
                                                        <thead class="table-light">
                                                            <tr>
                                                                <th>Date</th>
                                                                <th>Check-in</th>
                                                                <th>Check-out</th>
                                                                <th>Status</th>
                                                                <th>Source</th>
                                                                <th>Period</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <tr>
                                                                <td>${rec.date}</td>
                                                                <td>${rec.checkIn}</td>
                                                                <td>${rec.checkOut}</td>
                                                                <td>${rec.status}</td>
                                                                <td>${rec.source}</td>
                                                                <td>${rec.period}</td>
                                                            </tr>
                                                        </tbody>
                                                    </table>
                                                </div>

                                                <!-- Vùng chỉnh sửa thông tin -->
                                                <div class="mb-2">
                                                    <strong>Edit Fields:</strong>
                                                    <div class="row g-2 mt-1">
                                                        <div class="col-md-3">
                                                            <label for="editDate" class="form-label">Date</label>
                                                            <input type="date" id="editDate" name="editDate"
                                                                   class="form-control" value="${rec.date}" readonly />
                                                        </div>
                                                        <div class="col-md-2">
                                                            <label for="editCheckIn"
                                                                   class="form-label">Check-in</label>
                                                            <input type="time" id="editCheckIn" name="editCheckIn"
                                                                   class="form-control validate-time"
                                                                   value="${rec.checkIn}" data-user-id="${rec.userId}"
                                                                   data-date="${rec.date}" data-type="checkIn" />
                                                            <div class="invalid-feedback"></div>
                                                        </div>
                                                        <div class="col-md-2">
                                                            <label for="editCheckOut"
                                                                   class="form-label">Check-out</label>
                                                            <input type="time" id="editCheckOut" name="editCheckOut"
                                                                   class="form-control validate-time"
                                                                   value="${rec.checkOut}" data-user-id="${rec.userId}"
                                                                   data-date="${rec.date}" data-type="checkOut" />
                                                            <div class="invalid-feedback"></div>
                                                        </div>
                                                        <div class="col-md-3">
                                                            <label for="editStatus"
                                                                   class="form-label">Status</label>
                                                            <select id="editStatus" name="editStatus"
                                                                    class="form-select">
                                                                <option value="Late" ${rec.status=='Late'
                                                                                       ? 'selected' : '' }>Late</option>
                                                                <option value="On Time" ${rec.status=='On Time'
                                                                                          ? 'selected' : '' }>On Time</option>
                                                                <option value="Shift day" ${rec.status=='Shift day'
                                                                                            ? 'selected' : '' }>Shift day</option>
                                                                <option value="leaving early"
                                                                        ${rec.status=='leaving early' ? 'selected' : ''
                                                                        }>leaving early</option>
                                                                <option value="Over Time" ${rec.status=='Over Time'
                                                                                            ? 'selected' : '' }>Over Time</option>
                                                            </select>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:if>
                                <input type="hidden" id="selected_logs_data" name="selected_logs_data" />

                                <div class="form-text">
                                    Click "Select record" to choose the attendance logs you want to dispute. Then
                                    fill in the corrected times.
                                </div>
                            </div>

                            <!-- Title -->
                            <div class="mb-3">
                                <label for="title" class="form-label">
                                    <i class="fas fa-heading"></i> Title
                                    <span class="text-danger">*</span>
                                </label>
                                <input type="text" class="form-control" id="title" name="title" required />
                                <div class="form-text">Provide a short title summarizing your dispute</div>
                            </div>

                            <!-- Detail -->
                            <div class="mb-3">
                                <label for="detail" class="form-label">
                                    <i class="fas fa-comment-dots"></i> Details
                                    <span class="text-danger">*</span>
                                </label>
                                <textarea class="form-control" id="detail" name="detail" rows="5" maxlength="1000"
                                          required></textarea>
                                <div class="form-text">Describe what happened and why you are submitting this
                                    dispute</div>
                            </div>

                            <!-- Attachment -->
                            <div class="mb-3">
                                <label for="attachment" class="form-label">
                                    <i class="fas fa-paperclip"></i> Attachment
                                    <span class="text-muted">(Optional)</span>
                                </label>
                                <input type="file" class="form-control" id="attachment" name="attachment"
                                       accept=".pdf,.doc,.docx,.jpg,.jpeg,.png" />
                                <div class="form-text">
                                    Accepted formats: PDF, DOC, DOCX, JPG, PNG (Max 5MB)
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="attachmentLink" class="form-label">
                                    <i class="fas fa-link"></i> Attachment Link
                                    <span class="text-muted">(Optional)</span>
                                </label>
                                <input type="url" class="form-control" id="attachmentLink" name="attachmentLink"
                                       placeholder="Enter a URL (e.g., https://example.com/file)" />
                                <div class="form-text">
                                    You can also provide a link instead of uploading a file.
                                </div>
                            </div>

                            <!-- Actions -->
                            <div class="mt-4 d-grid gap-2 d-md-flex justify-content-md-end">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-paper-plane me-1"></i> Submit Dispute
                                </button>
                            </div>

                        </form>
                    </div>
                </div>
            </div>

            <!-- Attendance Record Selection Popup -->
            <div id="selectRecordPopup" class="d-none">
                <div class="popup-content">
                    <div class="popup-header d-flex justify-content-between align-items-center mb-2">
                        <h5>Select Attendance Records</h5>
                        <button type="button" id="closePopupBtn">X</button>
                    </div>

                    <!-- Form để submit dữ liệu -->
                    <form id="attendanceForm" method="post"
                          action="${pageContext.request.contextPath}/requests/appeal/create">

                        <!-- Filter: Period -->
                        <div class="mb-3">
                            <label for="periodFilter">Filter by Period:</label>
                            <select id="periodFilter" name="periodFilter" class="form-select">
                                <c:forEach var="p" items="${periodList}">
                                    <option value="${p.id}" <c:if test="${p.id == currentPeriodId}">selected</c:if>>
                                        ${p.name}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- Attendance Table -->
                        <table class="table table-bordered table-hover" id="attendanceTable">
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
                            <tbody id="attendanceTableBody">
                                <c:forEach var="log" items="${attendanceList}">
                                    <tr data-period-name="${log.period}">
                                        <td>
                                            <input type="checkbox" class="form-check-input select-checkbox" />
                                        </td>
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

                        <!-- Frontend Pagination Controls -->
                        <div class="pagination-controls mt-2">
                            <ul class="pagination" id="paginationContainer"></ul>
                        </div>

                        <!-- Ẩn input để gửi dữ liệu selected records -->
                        <input type="hidden" name="action" value="submitSelectedRecords" />
                        <input type="hidden" name="records" id="recordsInput" />

                        <!-- Popup Actions -->
                        <div class="popup-actions mt-3 d-flex justify-content-end gap-2">
                            <button type="submit" id="submitSelectedRecords" class="btn btn-primary">Submit</button>
                            <button type="button" id="closePopupBtn2" class="btn btn-secondary">X</button>
                        </div>
                    </form>
                </div>
            </div>
            <jsp:include page="../layout/dashboard-footer.jsp" />
        </div>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const selectRecordBtn = document.getElementById("selectRecordBtn");
                const popup = document.getElementById("selectRecordPopup");
                const closeBtns = document.querySelectorAll("#closePopupBtn, #closePopupBtn2");
                const tableBody = document.getElementById("attendanceTableBody");
                const periodFilter = document.getElementById("periodFilter");
                const paginationContainer = document.getElementById("paginationContainer");
                const submitBtn = document.getElementById("submitSelectedRecords");
                const recordsInput = document.getElementById("recordsInput"); // hidden input in form
                const rowsPerPage = 5;
                let currentPage = 1;
                let selectedRecords = [];

                const allRows = Array.from(tableBody.querySelectorAll("tr"));

                function renderRows() {
                    const selectedPeriodName = periodFilter.options[periodFilter.selectedIndex]?.text;
                    const filtered = allRows.filter(row =>
                        !periodFilter.value || row.dataset.periodName === selectedPeriodName
                    );
                    const totalPages = Math.ceil(filtered.length / rowsPerPage) || 1;
                    if (currentPage > totalPages)
                        currentPage = totalPages;

                    allRows.forEach(r => (r.style.display = "none"));

                    const start = (currentPage - 1) * rowsPerPage;
                    const end = start + rowsPerPage;
                    const visible = filtered.slice(start, end);
                    visible.forEach(row => (row.style.display = ""));

                    // Render pagination
                    paginationContainer.innerHTML = "";
                    if (totalPages > 1) {
                        if (currentPage > 1) {
                            const prev = document.createElement("li");
                            prev.className = "page-item";
                            prev.innerHTML = `<a class="page-link" href="#">Prev</a>`;
                            prev.addEventListener("click", e => {
                                e.preventDefault();
                                currentPage--;
                                renderRows();
                            });
                            paginationContainer.appendChild(prev);
                        }

                        for (let i = 1; i <= totalPages; i++) {
                            const li = document.createElement("li");
                            li.className = "page-item" + (i === currentPage ? " active" : "");
                            li.innerHTML = `<a class="page-link" href="#">${i}</a>`;
                            li.addEventListener("click", e => {
                                e.preventDefault();
                                currentPage = i;
                                renderRows();
                            });
                            paginationContainer.appendChild(li);
                        }

                        if (currentPage < totalPages) {
                            const next = document.createElement("li");
                            next.className = "page-item";
                            next.innerHTML = `<a class="page-link" href="#">Next</a>`;
                            next.addEventListener("click", e => {
                                e.preventDefault();
                                currentPage++;
                                renderRows();
                            });
                            paginationContainer.appendChild(next);
                        }
                    }
                }

                // --- Popup events ---
                selectRecordBtn?.addEventListener("click", () => popup.classList.remove("d-none"));
                closeBtns.forEach(btn => btn.addEventListener("click", () => popup.classList.add("d-none")));
                popup.addEventListener("click", e => {
                    if (e.target === popup)
                        popup.classList.add("d-none");
                });
                periodFilter?.addEventListener("change", () => {
                    currentPage = 1;
                    renderRows();
                });

                // --- Checkbox selection ---
                tableBody.addEventListener("change", event => {
                    const checkbox = event.target.closest(".select-checkbox");
                    if (!checkbox)
                        return;

                    const row = checkbox.closest("tr");
                    const cells = row.querySelectorAll("td");
                    const record = {
                        date: cells[1].innerText.trim(),
                        checkIn: cells[2].innerText.trim(),
                        checkOut: cells[3].innerText.trim(),
                        status: cells[4].innerText.trim(),
                        source: cells[5].innerText.trim(),
                        period: cells[6].innerText.trim()
                    };

                    if (checkbox.checked) {
                        const exists = selectedRecords.some(r =>
                            r.date === record.date && r.checkIn === record.checkIn && r.checkOut === record.checkOut
                        );
                        if (!exists) {
                            selectedRecords.push(record);
                            row.classList.add("table-success");
                        }
                    } else {
                        selectedRecords = selectedRecords.filter(r =>
                            !(r.date === record.date && r.checkIn === record.checkIn && r.checkOut === record.checkOut)
                        );
                        row.classList.remove("table-success");
                    }
                });

                // --- Submit form ---
                submitBtn.closest("form").addEventListener("submit", function (e) {
                    if (selectedRecords.length === 0) {
                        e.preventDefault();
                        alert("⚠️ Please select at least one record before submitting!");
                        return;
                    }

                    // Set hidden input value trước khi submit
                    recordsInput.value = JSON.stringify(selectedRecords);
                });

                // --- Initial render ---
                renderRows();
            });
        </script>

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const selectRecordBtn = document.getElementById("selectRecordBtn");
                const popup = document.getElementById("selectRecordPopup");
                const closeBtns = document.querySelectorAll("#closePopupBtn, #closePopupBtn2");

                // Mở popup khi click Select record
                selectRecordBtn.addEventListener("click", function () {
                    popup.classList.remove("d-none");
                });

                // Đóng popup khi click nút X
                closeBtns.forEach(btn => {
                    btn.addEventListener("click", function () {
                        popup.classList.add("d-none");
                    });
                });

                // Optional: click bên ngoài popup-content cũng đóng popup
                popup.addEventListener("click", function (event) {
                    if (event.target === popup) {
                        popup.classList.add("d-none");
                    }
                });
            });

            document.querySelectorAll('.remove-record-btn').forEach(btn => {
                btn.addEventListener('click', e => {
                    const parentDiv = e.target.closest('.col-12');
                    if (parentDiv)
                        parentDiv.remove();
                });
            });
        </script>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const form = document.getElementById("appealForm");

                form.addEventListener("submit", function (e) {
                    const recordsDiv = document.getElementById("selectedRecordsList");
                    const records = [];

                    if (recordsDiv) {
                        const recordItems = recordsDiv.querySelectorAll(".col-12.border");

                        recordItems.forEach(div => {
                            const removeBtn = div.querySelector(".remove-record-btn");
                            if (!removeBtn)
                                return;

                            // --- Lấy dữ liệu cũ từ table ---
                            const tds = div.querySelectorAll("table tbody tr td");
                            const oldRecord = {
                                date: tds[0]?.innerText.trim() || "",
                                checkIn: tds[1]?.innerText.trim() || "",
                                checkOut: tds[2]?.innerText.trim() || "",
                                status: tds[3]?.innerText.trim() || "",
                                source: tds[4]?.innerText.trim() || "",
                                period: tds[5]?.innerText.trim() || ""
                            };

                            // --- Lấy dữ liệu mới từ input edit trong div ---
                            const editDateEl = div.querySelector("input[name='editDate']");
                            const editCheckInEl = div.querySelector("input[name='editCheckIn']");
                            const editCheckOutEl = div.querySelector("input[name='editCheckOut']");
                            const editStatusEl = div.querySelector("select[name='editStatus']");

                            const newRecord = {
                                date: editDateEl?.value || "",
                                checkIn: editCheckInEl?.value || "",
                                checkOut: editCheckOutEl?.value || "",
                                status: editStatusEl?.value || ""
                            };

                            records.push({
                                oldRecord: oldRecord,
                                newRecord: newRecord
                            });
                        });
                    }

                    // --- Gán JSON vào input hidden trong form ---
                    const hiddenInput = document.getElementById("selected_logs_data");
                    hiddenInput.value = JSON.stringify(records);
                });

                // --- Xử lý nút X để xóa bản ghi ---
                const removeBtns = document.querySelectorAll(".remove-record-btn");
                removeBtns.forEach(btn => {
                    btn.addEventListener("click", function () {
                        const div = btn.closest(".col-12.border");
                        if (div)
                            div.remove();
                    });
                });
            });
        </script>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const form = document.getElementById("appealForm");
                const selectRecordBtn = document.getElementById("selectRecordBtn");
                const recordsDiv = document.getElementById("selectedRecordsList");
                const errorDiv = document.getElementById("attendanceRecordError");

                form.addEventListener("submit", function (e) {
                    // Kiểm tra nếu chưa có bản ghi nào được chọn
                    if (!recordsDiv || recordsDiv.children.length === 0) {
                        e.preventDefault(); // chặn submit
                        errorDiv.style.display = "block"; // hiện thông báo
                        recordsDiv?.scrollIntoView({behavior: "smooth"}); // scroll đến chỗ cần điền
                    } else {
                        errorDiv.style.display = "none"; // ẩn thông báo nếu có bản ghi
                    }
                });

                // Nếu muốn, click vào selectRecordBtn có thể ẩn lỗi luôn
                selectRecordBtn.addEventListener("click", function () {
                    errorDiv.style.display = "none";
                });
            });
        </script>
        <script src="${pageContext.request.contextPath}/assets/js/appeal-request.js"></script>
    </body>F
</html>