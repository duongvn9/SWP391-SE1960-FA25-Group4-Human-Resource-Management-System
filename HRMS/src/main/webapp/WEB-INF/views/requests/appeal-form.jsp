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

            /* Dropdown styling fixes */
            .dropdown-menu {
                z-index: 1070 !important;
                border: 1px solid #dee2e6;
                box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
                min-width: 200px;
            }
            
            /* Ensure dropdown doesn't get clipped */
            .top-navbar .dropdown {
                position: static;
            }
            
            .dropdown-toggle::after {
                display: inline-block !important;
                margin-left: 0.255em;
                vertical-align: 0.255em;
                content: "";
                border-top: 0.3em solid;
                border-right: 0.3em solid transparent;
                border-bottom: 0;
                border-left: 0.3em solid transparent;
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
                                                        <div class="col-md-4">
                                                            <label for="editDate" class="form-label">Date</label>
                                                            <input type="date" id="editDate" name="editDate"
                                                                   class="form-control" value="${rec.date}" readonly />
                                                        </div>
                                                        <div class="col-md-4">
                                                            <label for="editCheckIn"
                                                                   class="form-label">Check-in</label>
                                                            <input type="time" id="editCheckIn" name="editCheckIn"
                                                                   class="form-control" value="${rec.checkIn}" />
                                                            <div class="error-message"></div>
                                                        </div>
                                                        <div class="col-md-4">
                                                            <label for="editCheckOut"
                                                                   class="form-label">Check-out</label>
                                                            <input type="time" id="editCheckOut" name="editCheckOut"
                                                                   class="form-control" value="${rec.checkOut}" />
                                                            <div class="error-message"></div>
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
                                    <c:if test="${not empty preservedNewRecords}">
                                        <c:forEach var="newRec" items="${preservedNewRecords}" varStatus="status">
                                            <div class="card border-primary mb-3 new-record-item" data-record-id="${status.index + 1}">
                                                <div class="card-header d-flex justify-content-between align-items-center py-2">
                                                    <small class="text-muted">Record #${status.index + 1}</small>
                                                    <button type="button" class="btn btn-sm btn-danger remove-record-btn">
                                                        <i class="fas fa-times"></i>
                                                    </button>
                                                </div>
                                                <div class="card-body">
                                                    <div class="row g-3">
                                                        <div class="col-md-4">
                                                            <label class="form-label">Date <span class="text-danger">*</span></label>
                                                            <input type="date" name="newRecordDate_${status.index + 1}" 
                                                                   class="form-control new-record-date" required value="${newRec.date}" />
                                                            <div class="error-message"></div>
                                                        </div>
                                                        <div class="col-md-4">
                                                            <label class="form-label">Check-in Time <span class="text-danger">*</span></label>
                                                            <input type="time" name="newRecordCheckIn_${status.index + 1}" 
                                                                   class="form-control new-record-checkin" required value="${newRec.checkIn}" />
                                                            <div class="error-message"></div>
                                                        </div>
                                                        <div class="col-md-4">
                                                            <label class="form-label">Check-out Time <span class="text-danger">*</span></label>
                                                            <input type="time" name="newRecordCheckOut_${status.index + 1}" 
                                                                   class="form-control new-record-checkout" required value="${newRec.checkOut}" />
                                                            <div class="error-message"></div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:if>
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
                                <textarea class="form-control" id="detail" name="detail" rows="5"
                                          required>${detail}</textarea>
                                <div class="d-flex justify-content-between align-items-center mt-1">
                                    <div class="form-text">Describe what happened and why you are submitting this
                                        dispute (max 200 words)</div>
                                    <small id="wordCount" class="text-muted">
                                        0/200 words
                                    </small>
                                </div>
                                <div id="wordLimitError" class="text-danger mt-1" style="display: none;">
                                    Please limit your description to 200 words or less.
                                </div>
                            </div>

                            <!-- Attachment -->
                            <div class="mb-3">
                                <label class="form-label">
                                    <i class="fas fa-paperclip"></i> Attachment
                                    <span class="text-danger">*</span>
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
            </div>
            <!-- Bootstrap Bundle (Required for dropdown functionality) -->
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>        
            <script src="${pageContext.request.contextPath}/assets/js/appeal-request.js"></script>
            <script>
                // Additional debug after appeal-request.js loads
                console.log('2. After appeal-request.js load');
                console.log('Bootstrap available after appeal-request.js:', typeof window.bootstrap !== 'undefined');
                
                // Bootstrap loaded successfully
                if (typeof window.bootstrap !== 'undefined') {
                    console.log('✅ Bootstrap loaded successfully!');
                    // Let appeal-request.js handle dropdown initialization
                } else {
                    console.error('❌ Bootstrap still not loaded!');
                }
                

                
                // Wait for everything to load
                setTimeout(function() {
                    console.log('3. After 1 second delay');
                    console.log('Bootstrap available after delay:', typeof window.bootstrap !== 'undefined');
                    
                    // Final dropdown check
                    const dropdowns = document.querySelectorAll('[data-bs-toggle="dropdown"]');
                    console.log('Final dropdown count:', dropdowns.length);
                    
                    if (dropdowns.length > 0 && typeof window.bootstrap !== 'undefined') {
                        console.log('Everything looks good - dropdown should work');
                    } else {
                        console.error('Problem detected:', {
                            dropdownsFound: dropdowns.length,
                            bootstrapLoaded: typeof window.bootstrap !== 'undefined'
                        });
                    }
                }, 1000);
            </script>
            
            <!-- Include Dashboard Footer for consistency -->
            <jsp:include page="../layout/dashboard-footer.jsp" />
    </body>
</html>