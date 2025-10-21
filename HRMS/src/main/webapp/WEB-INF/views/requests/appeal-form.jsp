<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Submit Attendance Dispute - HRMS</title>
        <jsp:include page="../layout/head.jsp">
            <jsp:param name="pageTitle" value="Submit Attendance Dispute - HRMS" />
        </jsp:include>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/appeal-form.css"> 
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
                    <a href="${pageContext.request.contextPath}/requests"
                       class="btn btn-outline-secondary">
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
                        <form method="post"
                              action="${pageContext.request.contextPath}/requests/appeal/create"
                              enctype="multipart/form-data"
                              id="appealForm">

                            <!-- Hidden Request Type -->
                            <input type="hidden" name="request_type_id" value="${requestTypeId}" />

                            <!-- Attendance Date -->
                            <div class="mb-3">
                                <label for="attendanceDate" class="form-label">
                                    <i class="fas fa-calendar-alt"></i> Attendance Date
                                    <span class="text-danger">*</span>
                                </label>
                                <div class="d-flex gap-2 align-items-center">
                                    <input type="date" class="form-control" id="attendanceDate" />
                                    <button type="button" id="addDateBtn" class="btn btn-primary" disabled>Add</button>
                                </div>
                                <div id="attendanceDateError" class="invalid-feedback" style="display:none;"></div>

                                <div id="selectedDatesList" class="mt-2"></div>
                                <input type="hidden" id="selectedLogDates" name="selected_log_dates" />
                                <div class="form-text">
                                    Choose the date you wish to dispute or select from logs. Multiple dates allowed.
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
                                <textarea class="form-control" id="detail" name="detail"
                                          rows="5" maxlength="1000" required></textarea>
                                <div class="form-text">Describe what happened and why you are submitting this dispute</div>
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
            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    const dateInput = document.getElementById("attendanceDate");
                    const addBtn = document.getElementById("addDateBtn");
                    const selectedList = document.getElementById("selectedDatesList");
                    const hiddenInput = document.getElementById("selectedLogDates");
                    const errorDiv = document.getElementById("attendanceDateError");

                    // Chặn chọn ngày tương lai
                    const todayStr = new Date().toISOString().slice(0, 10);
                    dateInput.setAttribute("max", todayStr);

                    let selectedDates = [];

                    // --- Validation ---
                    dateInput.addEventListener("input", validateInput);
                    dateInput.addEventListener("change", validateInput);

                    function validateInput() {
                        clearError();
                        const val = dateInput.value;
                        if (!val) {
                            showInvalid("Please select a date.");
                            return false;
                        }

                        const sel = new Date(val);
                        sel.setHours(0, 0, 0, 0);
                        const today = new Date();
                        today.setHours(0, 0, 0, 0);

                        if (sel > today) {
                            showInvalid("Attendance date must be today or in the past.");
                            return false;
                        }

                        if (selectedDates.includes(val)) {
                            showInvalid("This date has already been added.");
                            return false;
                        }

                        addBtn.disabled = false;
                        return true;
                    }

                    // --- Add date ---
                    addBtn.addEventListener("click", function () {
                        if (!validateInput()) {
                            addBtn.disabled = true;
                            return;
                        }

                        const val = dateInput.value;
                        if (!val || selectedDates.includes(val))
                            return;

                        selectedDates.push(val);
                        renderSelectedDates();

                        dateInput.value = "";
                        addBtn.disabled = true;
                    });

                    // --- Render danh sách ---
                    function renderSelectedDates() {
                        selectedList.innerHTML = "";
                        selectedDates.forEach((date) => {
                            const span = document.createElement("span");
                            span.textContent = date;
                            span.className = "badge bg-light text-dark border me-2 mb-2 p-2 clickable-date";
                            span.style.cursor = "pointer";
                            span.title = "Click to remove this date";
                            selectedList.appendChild(span);
                        });
                        hiddenInput.value = selectedDates.join(",");
                    }

                    // --- Click để remove ---
                    selectedList.addEventListener("click", function (e) {
                        if (e.target.classList.contains("clickable-date")) {
                            const date = e.target.textContent;
                            selectedDates = selectedDates.filter((d) => d !== date);
                            renderSelectedDates();
                            validateInput();
                        }
                    });

                    // --- Error handling ---
                    function showInvalid(msg) {
                        errorDiv.textContent = msg;
                        errorDiv.style.display = "block";
                        dateInput.classList.add("is-invalid");
                        addBtn.disabled = true;
                    }

                    function clearError() {
                        errorDiv.textContent = "";
                        errorDiv.style.display = "none";
                        dateInput.classList.remove("is-invalid");
                    }
                });
            </script>
            <!-- Footer -->
            <jsp:include page="../layout/dashboard-footer.jsp" />
        </div>
        <script src="${pageContext.request.contextPath}/assets/js/appeal-request.js"></script>
    </body>
</html>
