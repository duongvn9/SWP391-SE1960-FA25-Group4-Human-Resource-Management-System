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

            .error-message {
                color: #dc3545;
                font-size: 13px;
                margin-top: 4px;
                display: none;
            }

            /* Word counter styling */
            #wordCount {
                font-size: 12px;
                font-weight: 500;
                transition: color 0.3s ease;
            }

            #wordCount.text-danger {
                font-weight: 600;
            }

            #detail.is-invalid {
                border-color: #dc3545;
                box-shadow: 0 0 0 0.2rem rgba(220, 53, 69, 0.25);
            }

            #wordLimitError {
                font-size: 12px;
                font-weight: 500;
            }

            /* Fix submit button spacing */
            .justify-content-md-end {
                padding-right: 1rem !important;
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

                            <!-- Request Type Selection -->
                            <div class="mb-3">
                                <label class="form-label">
                                    <i class="fas fa-clipboard-list"></i> Request Type
                                    <span class="text-danger">*</span>
                                </label>
                                <div class="mb-3">
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="requestType"
                                               id="editExistingRecord" value="edit" checked>
                                        <label class="form-check-label" for="editExistingRecord">
                                            <i class="fas fa-edit me-1"></i> Edit Existing Attendance Record
                                        </label>
                                    </div>
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="requestType"
                                               id="addNewRecord" value="add">
                                        <label class="form-check-label" for="addNewRecord">
                                            <i class="fas fa-plus me-1"></i> Add New Attendance Record
                                        </label>
                                    </div>
                                </div>
                                <!-- Thông báo lỗi hiển thị tại đây -->
                                <div id="requestTypeError" class="text-danger mt-1" style="display:none;">
                                    <i class="fas fa-exclamation-circle me-1"></i>
                                    Please select at least one attendance record to edit or add at least one new
                                    record.
                                </div>
                            </div>

                            <!-- Edit Existing Record Section -->
                            <div class="mb-3" id="editRecordSection">
                                <label class="form-label">
                                    <i class="fas fa-calendar-alt"></i> Select Attendance Record to Edit
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
                                                                   class="form-control" value="${rec.checkIn}" />
                                                            <div class="error-message"></div>
                                                        </div>
                                                        <div class="col-md-2">
                                                            <label for="editCheckOut"
                                                                   class="form-label">Check-out</label>
                                                            <input type="time" id="editCheckOut" name="editCheckOut"
                                                                   class="form-control" value="${rec.checkOut}" />
                                                            <div class="error-message"></div>
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

                            <!-- Add New Record Section -->
                            <div class="mb-3" id="addRecordSection" style="display: none;">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <label class="form-label mb-0">
                                        <i class="fas fa-plus-circle"></i> New Attendance Records
                                        <span class="text-danger">*</span>
                                    </label>
                                    <button type="button" id="addNewRecordBtn" class="btn btn-sm btn-success">
                                        <i class="fas fa-plus me-1"></i> Add Record
                                    </button>
                                </div>

                                <!-- Container for multiple new records -->
                                <div id="newRecordsContainer">
                                    <!-- Records will be added here dynamically -->
                                </div>

                                <div class="form-text mt-2">
                                    <i class="fas fa-info-circle me-1"></i>
                                    Click "Add Record" to add missing attendance records. You can add multiple
                                    records.
                                </div>

                                <!-- Thông báo lỗi cho new records -->
                                <div id="newRecordError" class="text-danger mt-1" style="display:none;">
                                    Please add at least one new attendance record with all required fields.
                                </div>

                                <!-- Hidden input to store new records data -->
                                <input type="hidden" id="new_records_data" name="new_records_data" />
                            </div>

                            <!-- Title -->
                            <div class="mb-3">
                                <label for="title" class="form-label">
                                    <i class="fas fa-heading"></i> Title
                                    <span class="text-danger">*</span>
                                </label>
                                <input type="text" class="form-control" id="title" name="title" required
                                       value="${title}" />
                                <div class="form-text">Provide a short title summarizing your dispute</div>
                            </div>

                            <!-- Detail -->
                            <div class="mb-3">
                                <label for="detail" class="form-label"></label>
                                <i class="fas fa-comment-dots"></i> Details
                                <span class="text-danger">*</span>
                                </label>
                                <textarea class="form-control" id="detail" name="detail" rows="5" required
                                          oninput="updateWordCountInline(this)" onkeyup="updateWordCountInline(this)"
                                          onpaste="setTimeout(function(){updateWordCountInline(document.getElementById('detail'))}, 50)">${detail}</textarea>
                                <div class="d-flex justify-content-between align-items-center mt-1">
                                    <div class="form-text">Describe what happened and why you are submitting this
                                        dispute (max 200 words)</div>
                                    <small id="wordCount" class="text-muted">
                                        <script>document.write('0/200 words');</script>
                                    </small>
                                </div>
                                <div id="wordLimitError" class="text-danger mt-1" style="display: none;">
                                    Please limit your description to 200 words or less.
                                </div>
                            </div>

                            <!-- Attachment -->
                            <div class="mb-3"></div>
                            <label class="form-label"></label>
                            <i class="fas fa-paperclip"></i> Attachment
                            <span class="text-muted">(Optional)</span>
                            </label>

                            <!-- Attachment Type Selection -->
                            <div class="mb-2">
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="attachmentType"
                                           id="attachmentTypeFile" value="file" ${empty attachmentType ||
                                                                                  attachmentType=='file' ? 'checked' : '' }>
                                    <label class="form-check-label" for="attachmentTypeFile">
                                        <i class="fas fa-upload me-1"></i> Upload Files
                                    </label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="attachmentType"
                                           id="attachmentTypeLink" value="link" ${attachmentType=='link' ? 'checked'
                                                                                  : '' }>
                                    <label class="form-check-label" for="attachmentTypeLink">
                                        <i class="fas fa-link me-1"></i> Google Drive Link
                                    </label>
                                </div>
                            </div>

                            <!-- File Upload Section -->
                            <div id="fileUploadSection">
                                <input type="file" class="form-control" id="attachments" name="attachments"
                                       accept=".pdf,.doc,.docx,.jpg,.jpeg,.png" multiple />
                                <div class="form-text">
                                    <i class="fas fa-info-circle me-1"></i>
                                    Accepted formats: PDF, DOC, DOCX, JPG, PNG (Max 5MB per file, multiple files
                                    allowed)
                                </div>
                            </div>

                            <!-- Google Drive Link Section -->
                            <div id="driveLinkSection" style="display: none;">
                                <input type="url" class="form-control" id="driveLink" name="driveLink"
                                       placeholder="https://drive.google.com/..." value="${driveLink}" />
                                <div class="form-text">
                                    <i class="fas fa-info-circle me-1"></i>
                                    Provide a Google Drive link to your supporting documents. Make sure the link
                                    is publicly accessible or shared with HR.
                                </div>
                            </div>

                            <!-- Attachment Error Message -->
                            <div id="attachmentError" class="text-danger mt-2" style="display: none;">
                                <i class="fas fa-exclamation-circle me-1"></i>
                                Please provide either file attachments or a Google Drive link as evidence.
                            </div>
                    </div>

                    <!-- Actions -->
                    <div class="mt-4 d-grid gap-2 d-md-flex justify-content-md-end"
                         style="margin-bottom: 2rem !important; margin-right: 2rem !important;">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-paper-plane me-1"></i> Submit Dispute
                        </button>
                    </div>

                    </form>
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
                                <option value="">-- All Periods --</option>
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
                    const selectedPeriodValue = periodFilter.value;
                    const selectedPeriodName = periodFilter.options[periodFilter.selectedIndex]?.text;

                    const filtered = allRows.filter(row => {
                        const rowPeriodName = row.dataset.periodName;

                        // Nếu không chọn period nào (value rỗng) thì hiển thị tất cả
                        if (!selectedPeriodValue || selectedPeriodValue === "") {
                            return true;
                        }

                        // So sánh tên period (trim để loại bỏ khoảng trắng thừa)
                        const match = rowPeriodName && rowPeriodName.trim() === selectedPeriodName.trim();
                        return match;
                    });
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
                // Xử lý nút X để xóa bản ghi
                document.querySelectorAll('.remove-record-btn').forEach(btn => {
                    btn.addEventListener('click', e => {
                        const parentDiv = e.target.closest('.col-12');
                        if (parentDiv)
                            parentDiv.remove();
                    });
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
                const editRadio = document.getElementById("editExistingRecord");

                // Hide error message when user interacts with the form (but don't validate yet)
                function hideAttendanceRecordError() {
                    if (errorDiv) {
                        errorDiv.style.display = "none";
                    }
                }

                // Hide error when user clicks select record button
                if (selectRecordBtn) {
                    selectRecordBtn.addEventListener("click", hideAttendanceRecordError);
                }

                // Hide error when user switches to add new record mode
                const addRadio = document.getElementById("addNewRecord");
                if (addRadio) {
                    addRadio.addEventListener("change", hideAttendanceRecordError);
                }

                // Note: The actual validation logic is now handled in the main attachment validation script
                // This ensures all validations happen only on form submit
            });
        </script>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const checkInInput = document.getElementById("editCheckIn");
                const checkOutInput = document.getElementById("editCheckOut");

                // Chỉ thực hiện nếu các elements tồn tại
                if (!checkInInput || !checkOutInput)
                    return;

                const minTime = "06:00";
                const maxTime = "23:59";

                function showError(input, message) {
                    const errorDiv = input.parentElement.querySelector(".error-message");
                    if (errorDiv) {
                        errorDiv.textContent = message;
                        errorDiv.style.display = "block";
                    }
                }

                function clearError(input) {
                    const errorDiv = input.parentElement.querySelector(".error-message");
                    if (errorDiv) {
                        errorDiv.textContent = "";
                        errorDiv.style.display = "none";
                    }
                }

                function isTimeInRange(time) {
                    return time >= minTime && time <= maxTime;
                }

                function validateEditTimes() {
                    clearError(checkInInput);
                    clearError(checkOutInput);

                    const checkIn = checkInInput.value;
                    const checkOut = checkOutInput.value;

                    if (!checkIn && !checkOut)
                        return;

                    if (checkIn && !isTimeInRange(checkIn)) {
                        showError(checkInInput, "Check-in time must be between 06:00 and 23:59.");
                        checkInInput.value = "";
                        return;
                    }

                    if (checkOut && !isTimeInRange(checkOut)) {
                        showError(checkOutInput, "Check-out time must be between 06:00 and 23:59.");
                        checkOutInput.value = "";
                        return;
                    }

                    if (checkIn && checkOut && checkIn >= checkOut) {
                        showError(checkOutInput, "Check-in time must be earlier than check-out time.");
                        checkOutInput.value = "";
                    }
                }

                checkInInput.addEventListener("change", validateEditTimes);
                checkOutInput.addEventListener("change", validateEditTimes);
            });
        </script>
        <script>
            // Simple inline word count function
            function updateWordCountInline(textarea) {
                const text = textarea.value;
                const words = text.trim() === "" ? 0 : text.trim().split(/\s+/).length;
                const maxWords = 200;

                const display = document.getElementById("wordCount");
                const error = document.getElementById("wordLimitError");

                if (display) {
                    display.textContent = words + "/" + maxWords + " words";

                    if (words > maxWords) {
                        display.classList.add("text-danger");
                        display.classList.remove("text-muted");
                        if (error)
                            error.style.display = "block";
                        textarea.classList.add("is-invalid");
                    } else {
                        display.classList.remove("text-danger");
                        display.classList.add("text-muted");
                        if (error)
                            error.style.display = "none";
                        textarea.classList.remove("is-invalid");
                    }
                }
            }

            // Initialize word count
            document.addEventListener("DOMContentLoaded", function () {
                const textarea = document.getElementById("detail");
                if (textarea) {
                    updateWordCountInline(textarea);
                }
            });
        </script>
        <script>
            // Word count validation for detail textarea
            document.addEventListener("DOMContentLoaded", function () {
                const detailTextarea = document.getElementById("detail");
                const wordCountDisplay = document.getElementById("wordCount");
                const wordLimitError = document.getElementById("wordLimitError");
                const form = document.getElementById("appealForm");
                const maxWords = 200;

                if (!detailTextarea || !wordCountDisplay) {
                    return;
                }

                function countWords(text) {
                    const words = text.trim().split(/\s+/);
                    return text.trim() === "" ? 0 : words.length;
                }

                function updateWordCount() {
                    const text = detailTextarea.value;
                    const wordCount = countWords(text);

                    wordCountDisplay.textContent = `${wordCount}/${maxWords} words`;

                    if (wordCount > maxWords) {
                        wordCountDisplay.classList.add("text-danger");
                        wordCountDisplay.classList.remove("text-muted");
                        if (wordLimitError)
                            wordLimitError.style.display = "block";
                        detailTextarea.classList.add("is-invalid");
                    } else {
                        wordCountDisplay.classList.remove("text-danger");
                        wordCountDisplay.classList.add("text-muted");
                        if (wordLimitError)
                            wordLimitError.style.display = "none";
                        detailTextarea.classList.remove("is-invalid");
                    }
                }

                // Add event listeners
                detailTextarea.addEventListener("input", updateWordCount);
                detailTextarea.addEventListener("keyup", updateWordCount);
                detailTextarea.addEventListener("paste", function () {
                    setTimeout(updateWordCount, 50);
                });

                // Form validation
                if (form) {
                    form.addEventListener("submit", function (e) {
                        const wordCount = countWords(detailTextarea.value);
                        if (wordCount > maxWords) {
                            e.preventDefault();
                            if (wordLimitError)
                                wordLimitError.style.display = "block";
                            detailTextarea.focus();
                            detailTextarea.scrollIntoView({behavior: 'smooth', block: 'center'});
                        }
                    });
                }

                // Initial count
                setTimeout(updateWordCount, 100);
            });
        </script>
        <script>
            // Handle attachment type switching
            document.addEventListener("DOMContentLoaded", function () {
                const fileRadio = document.getElementById("attachmentTypeFile");
                const linkRadio = document.getElementById("attachmentTypeLink");
                const fileSection = document.getElementById("fileUploadSection");
                const linkSection = document.getElementById("driveLinkSection");
                const fileInput = document.getElementById("attachments");
                const linkInput = document.getElementById("driveLink");

                // Preserve attachment type from server (if validation error occurred)
                const serverAttachmentType = "${attachmentType}";
                if (serverAttachmentType === "link") {
                    linkRadio.checked = true;
                    fileRadio.checked = false;
                } else {
                    fileRadio.checked = true;
                    linkRadio.checked = false;
                }

                function toggleAttachmentType() {
                    if (fileRadio.checked) {
                        fileSection.style.display = "block";
                        linkSection.style.display = "none";
                        // Only clear link input when switching from link to file (not on page load)
                        if (serverAttachmentType !== "link") {
                            linkInput.value = "";
                        }
                    } else if (linkRadio.checked) {
                        fileSection.style.display = "none";
                        linkSection.style.display = "block";
                        // Clear file input when switching to link
                        fileInput.value = "";
                    }
                }

                // Add event listeners
                fileRadio.addEventListener("change", toggleAttachmentType);
                linkRadio.addEventListener("change", toggleAttachmentType);

                // Initial state
                toggleAttachmentType();
            });
        </script>
        <script>
            // Handle request type switching
            document.addEventListener("DOMContentLoaded", function () {
                const editRadio = document.getElementById("editExistingRecord");
                const addRadio = document.getElementById("addNewRecord");
                const editSection = document.getElementById("editRecordSection");
                const addSection = document.getElementById("addRecordSection");

                function toggleRequestType() {
                    if (editRadio.checked) {
                        editSection.style.display = "block";
                        addSection.style.display = "none";

                        // Disable required for add section inputs
                        const addInputs = addSection.querySelectorAll('input[required]');
                        addInputs.forEach(input => input.removeAttribute('required'));

                    } else if (addRadio.checked) {
                        editSection.style.display = "none";
                        addSection.style.display = "block";

                        // Enable required for add section inputs that are visible
                        const addInputs = addSection.querySelectorAll('.new-record-date, .new-record-checkin, .new-record-checkout');
                        addInputs.forEach(input => {
                            const recordItem = input.closest('.new-record-item');
                            if (recordItem && recordItem.style.display !== 'none') {
                                input.setAttribute('required', 'required');
                            }
                        });
                    }
                }

                // Add event listeners
                editRadio.addEventListener("change", toggleRequestType);
                addRadio.addEventListener("change", toggleRequestType);

                // Initial state
                toggleRequestType();
            });
        </script>
        <script>
            // Handle multiple new records
            document.addEventListener("DOMContentLoaded", function () {
                const addNewRecordBtn = document.getElementById("addNewRecordBtn");
                const newRecordsContainer = document.getElementById("newRecordsContainer");
                let recordCounter = 0;

                // Function to create a new record form
                function createNewRecordForm() {
                    recordCounter++;
                    const recordDiv = document.createElement("div");
                    recordDiv.className = "card border-primary mb-3 new-record-item";
                    recordDiv.setAttribute("data-record-id", recordCounter);

                    recordDiv.innerHTML = `
                        <div class="card-header d-flex justify-content-between align-items-center py-2">
                            <small class="text-muted">Record #${recordCounter}</small>
                            <button type="button" class="btn btn-sm btn-danger remove-record-btn">
                                <i class="fas fa-times"></i>
                            </button>
                        </div>
                        <div class="card-body">
                            <div class="row g-3">
                                <div class="col-md-4">
                                    <label class="form-label">Date <span class="text-danger">*</span></label>
                                    <input type="date" name="newRecordDate_${recordCounter}" 
                                           class="form-control new-record-date" required />
                                    <div class="error-message"></div>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label">Check-in Time <span class="text-danger">*</span></label>
                                    <input type="time" name="newRecordCheckIn_${recordCounter}" 
                                           class="form-control new-record-checkin" required />
                                    <div class="error-message"></div>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label">Check-out Time <span class="text-danger">*</span></label>
                                    <input type="time" name="newRecordCheckOut_${recordCounter}" 
                                           class="form-control new-record-checkout" required />
                                    <div class="error-message"></div>
                                </div>
                            </div>
                        </div>
                    `;

                    return recordDiv;
                }

                // Add new record when button clicked
                addNewRecordBtn.addEventListener("click", function () {
                    const newRecord = createNewRecordForm();
                    newRecordsContainer.appendChild(newRecord);

                    // Add event listener for remove button
                    const removeBtn = newRecord.querySelector(".remove-record-btn");
                    removeBtn.addEventListener("click", function () {
                        newRecord.remove();
                        updateRecordNumbers();
                    });

                    // Add validation for time inputs
                    addTimeValidation(newRecord);
                });

                // Function to update record numbers after removal
                function updateRecordNumbers() {
                    const records = newRecordsContainer.querySelectorAll(".new-record-item");
                    records.forEach((record, index) => {
                        const header = record.querySelector(".card-header small");
                        header.textContent = `Record #${index + 1}`;
                    });
                }

                // Function to add time validation
                function addTimeValidation(recordDiv) {
                    const checkInInput = recordDiv.querySelector(".new-record-checkin");
                    const checkOutInput = recordDiv.querySelector(".new-record-checkout");
                    const minTime = "06:00";
                    const maxTime = "23:59";

                    function showError(input, message) {
                        const errorDiv = input.parentElement.querySelector(".error-message");
                        if (errorDiv) {
                            errorDiv.textContent = message;
                            errorDiv.style.display = "block";
                        }
                    }

                    function clearError(input) {
                        const errorDiv = input.parentElement.querySelector(".error-message");
                        if (errorDiv) {
                            errorDiv.textContent = "";
                            errorDiv.style.display = "none";
                        }
                    }

                    function isTimeInRange(time) {
                        return time >= minTime && time <= maxTime;
                    }

                    function validateTimes() {
                        clearError(checkInInput);
                        clearError(checkOutInput);

                        const checkIn = checkInInput.value;
                        const checkOut = checkOutInput.value;

                        if (!checkIn && !checkOut)
                            return;

                        if (checkIn && !isTimeInRange(checkIn)) {
                            showError(checkInInput, "Check-in time must be between 06:00 and 23:59.");
                            checkInInput.value = "";
                            return;
                        }

                        if (checkOut && !isTimeInRange(checkOut)) {
                            showError(checkOutInput, "Check-out time must be between 06:00 and 23:59.");
                            checkOutInput.value = "";
                            return;
                        }

                        if (checkIn && checkOut && checkIn >= checkOut) {
                            showError(checkOutInput, "Check-in time must be earlier than check-out time.");
                            checkOutInput.value = "";
                        }
                    }

                    checkInInput.addEventListener("change", validateTimes);
                    checkOutInput.addEventListener("change", validateTimes);
                }

                // Collect new records data when form is submitted
                const appealForm = document.getElementById("appealForm");
                if (appealForm) {
                    appealForm.addEventListener("submit", function (e) {
                        const addRadio = document.getElementById("addNewRecord");
                        if (addRadio && addRadio.checked) {
                            const newRecords = [];
                            const recordItems = newRecordsContainer.querySelectorAll(".new-record-item");

                            recordItems.forEach(item => {
                                const date = item.querySelector(".new-record-date").value;
                                const checkIn = item.querySelector(".new-record-checkin").value;
                                const checkOut = item.querySelector(".new-record-checkout").value;

                                if (date && checkIn && checkOut) {
                                    newRecords.push({
                                        date: date,
                                        checkIn: checkIn,
                                        checkOut: checkOut
                                    });
                                }
                            });

                            // Store in hidden input
                            const hiddenInput = document.getElementById("new_records_data");
                            if (hiddenInput) {
                                hiddenInput.value = JSON.stringify(newRecords);
                            }
                        }
                    });
                }

                // Add first record automatically when "Add New Record" is selected
                const addRadio = document.getElementById("addNewRecord");
                if (addRadio) {
                    addRadio.addEventListener("change", function () {
                        if (this.checked && newRecordsContainer.children.length === 0) {
                            addNewRecordBtn.click();
                        }
                    });
                }
            });
        </script>
        <script>
            // Attachment validation - require either file or link
            document.addEventListener("DOMContentLoaded", function () {
                const appealForm = document.getElementById("appealForm");
                const fileInput = document.getElementById("attachments");
                const linkInput = document.getElementById("driveLink");
                const fileRadio = document.getElementById("attachmentTypeFile");
                const linkRadio = document.getElementById("attachmentTypeLink");
                const attachmentError = document.getElementById("attachmentError");

                function validateAttachment() {
                    let hasFile = false;
                    let hasLink = false;

                    // Check if file is selected
                    if (fileRadio && fileRadio.checked && fileInput && fileInput.files && fileInput.files.length > 0) {
                        hasFile = true;
                    }

                    // Check if link is provided
                    if (linkRadio && linkRadio.checked && linkInput && linkInput.value && linkInput.value.trim() !== "") {
                        hasLink = true;
                    }

                    return hasFile || hasLink;
                }

                // Hide error message when user makes changes (but don't validate yet)
                function hideAttachmentError() {
                    if (attachmentError) {
                        attachmentError.style.display = "none";
                    }
                    // Clear custom validity messages
                    if (fileInput)
                        fileInput.setCustomValidity("");
                    if (linkInput)
                        linkInput.setCustomValidity("");
                }

                // Add validation only on form submit
                if (appealForm) {
                    appealForm.addEventListener("submit", function (e) {
                        let isValid = true;

                        // Validate request type selection
                        if (!validateRequestType()) {
                            isValid = false;
                        }

                        // Validate attachment - only show error on submit
                        if (!validateAttachment()) {
                            isValid = false;
                            if (attachmentError) {
                                attachmentError.style.display = "block";
                                if (fileRadio && fileRadio.checked && fileInput) {
                                    fileInput.setCustomValidity("Please select at least one file or switch to Google Drive link.");
                                } else if (linkInput) {
                                    linkInput.setCustomValidity("Please provide a Google Drive link or switch to file upload.");
                                }
                            }
                        } else {
                            // Hide error if validation passes
                            if (attachmentError) {
                                attachmentError.style.display = "none";
                            }
                            if (fileInput)
                                fileInput.setCustomValidity("");
                            if (linkInput)
                                linkInput.setCustomValidity("");
                        }

                        if (!isValid) {
                            e.preventDefault();
                            e.stopPropagation();

                            // Scroll to first error
                            const firstError = document.querySelector('#requestTypeError[style*="block"], #attachmentError[style*="block"]');
                            if (firstError) {
                                firstError.scrollIntoView({behavior: 'smooth', block: 'center'});
                            }
                            return false;
                        }
                    });
                }

                // Hide error messages when user makes changes (but don't validate)
                if (fileInput) {
                    fileInput.addEventListener("change", hideAttachmentError);
                }

                if (linkInput) {
                    linkInput.addEventListener("input", hideAttachmentError);
                }

                // Hide error when switching attachment types
                if (fileRadio) {
                    fileRadio.addEventListener("change", hideAttachmentError);
                }

                if (linkRadio) {
                    linkRadio.addEventListener("change", hideAttachmentError);
                }

                // Request type validation function
                function validateRequestType() {
                    const editRadio = document.getElementById("editExistingRecord");
                    const addRadio = document.getElementById("addNewRecord");
                    const requestTypeError = document.getElementById("requestTypeError");
                    const selectedRecordsList = document.getElementById("selectedRecordsList");
                    const newRecordsContainer = document.getElementById("newRecordsContainer");

                    let hasValidSelection = false;

                    if (editRadio && editRadio.checked) {
                        // Check if user has selected records to edit
                        if (selectedRecordsList && selectedRecordsList.children.length > 0) {
                            hasValidSelection = true;
                        }
                    } else if (addRadio && addRadio.checked) {
                        // Check if user has added new records
                        if (newRecordsContainer && newRecordsContainer.children.length > 0) {
                            // Also check if the records have required fields filled
                            const recordItems = newRecordsContainer.querySelectorAll(".new-record-item");
                            let hasCompleteRecord = false;

                            recordItems.forEach(item => {
                                const date = item.querySelector(".new-record-date").value;
                                const checkIn = item.querySelector(".new-record-checkin").value;
                                const checkOut = item.querySelector(".new-record-checkout").value;

                                if (date && checkIn && checkOut) {
                                    hasCompleteRecord = true;
                                }
                            });

                            if (hasCompleteRecord) {
                                hasValidSelection = true;
                            }
                        }
                    }

                    if (requestTypeError) {
                        if (hasValidSelection) {
                            requestTypeError.style.display = "none";
                        } else {
                            requestTypeError.style.display = "block";
                        }
                    }

                    return hasValidSelection;
                }
            });
        </script>
    </body>
</html>