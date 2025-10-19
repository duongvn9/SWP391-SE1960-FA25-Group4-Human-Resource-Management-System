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
                            <a href="/downloads/fc33eb35-275e-4f61-9c15-cf8e18b50513.docx" download="Dispute.docx">Download file</a>
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
                                    <button type="button" id="addDateBtn" class="btn btn-primary">Add</button>
                                </div>
                                <div id="selectedDatesList" class="mt-2"></div>
                                <input type="hidden" id="selectedLogDates" name="selected_log_dates" />
                                <div class="form-text">Choose the date you wish to dispute or select from logs. Multiple dates allowed.</div>
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

                            <!-- Actions -->
                            <div class="mt-4 d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="${pageContext.request.contextPath}/requests"
                                   class="btn btn-secondary">
                                    <i class="fas fa-times me-1"></i> Cancel
                                </a>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-paper-plane me-1"></i> Submit Dispute
                                </button>
                            </div>

                        </form>
                    </div>
                </div>
            </div>

            <!-- Footer -->
            <jsp:include page="../layout/dashboard-footer.jsp" />
        </div>
        <script src="${pageContext.request.contextPath}/assets/js/appeal-request.js"></script>
    </body>
</html>
