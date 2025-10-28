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
                background-color: rgba(0,0,0,0.5);
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
                box-shadow: 0 5px 15px rgba(0,0,0,0.3);
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

                                <!-- Selected records list -->
                                <div id="selectedRecordsList" class="mt-3 row g-3">
                                    <!-- Example of one selected record (template) -->
                                    <!--
                                    <div class="col-md-6">
                                        <div class="card p-2">
                                            <div class="mb-1"><strong>Date:</strong> 02/10/2025</div>
                                            <div class="d-flex gap-2 align-items-center">
                                                <label>Check-in:</label>
                                                <input type="time" class="form-control" name="records[0][newCheckIn]" value="08:00">
                                            </div>
                                            <div class="d-flex gap-2 align-items-center mt-1">
                                                <label>Check-out:</label>
                                                <input type="time" class="form-control" name="records[0][newCheckOut]" value="17:30">
                                            </div>
                                            <div class="mt-1">
                                                <textarea class="form-control" name="records[0][reason]" placeholder="Reason for change"></textarea>
                                            </div>
                                        </div>
                                    </div>
                                    -->
                                </div>

                                <input type="hidden" id="selectedLogsData" name="selected_logs_data" />
                                <div class="form-text">
                                    Click "Select record" to choose the attendance logs you want to dispute. Then fill in the corrected times.
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

            <!-- Attendance Record Selection Popup -->
            <div id="selectRecordPopup" class="d-none">
                <div class="popup-content">
                    <div class="popup-header d-flex justify-content-between align-items-center mb-2">
                        <h5>Select Attendance Records</h5>
                        <button type="button" id="closePopupBtn">X</button>
                    </div>

                    <!-- Filter: Period -->
                    <div class="mb-3">
                        <label for="periodFilter">Filter by Period:</label>
                        <select id="periodFilter" name="periodFilter" class="form-select">
                            <option value="">-- All Periods --</option>
                            <c:forEach var="p" items="${periodList}">
                                <option value="${p.id}">${p.name}</option>
                            </c:forEach>
                        </select>
                        <button type="button" id="filterBtn" class="btn btn-secondary btn-sm mt-1">Filter</button>
                    </div>

                    <!-- Attendance Table -->
                    <table class="table table-bordered table-hover">
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
                        <tbody>
                            <c:forEach var="log" items="${attendanceLogs}">
                                <tr>
                                    <td>
                                        <input type="checkbox" name="selectedLogs" value="${log.id}" />
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

                    <!-- Pagination -->
                    <div class="pagination-controls mt-2">
                        <c:if test="${totalPages > 1}">
                            <nav>
                                <ul class="pagination">
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <li class="page-item <c:if test='${i == currentPage}'>active</c:if>">
                                            <a class="page-link" href="?page=${i}&periodFilter=${selectedPeriod}">${i}</a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </nav>
                        </c:if>
                    </div>

                    <!-- Popup Actions -->
                    <div class="popup-actions mt-3 d-flex justify-content-end gap-2">
                        <button type="button" id="submitSelectedRecords" class="btn btn-primary">Submit</button>
                        <button type="button" id="closePopupBtn2" class="btn btn-secondary">X</button>
                    </div>
                </div>
            </div>

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
            </script>

            <jsp:include page="../layout/dashboard-footer.jsp" />
        </div>
        <script src="${pageContext.request.contextPath}/assets/js/appeal-request.js"></script>
    </body>
</html>
