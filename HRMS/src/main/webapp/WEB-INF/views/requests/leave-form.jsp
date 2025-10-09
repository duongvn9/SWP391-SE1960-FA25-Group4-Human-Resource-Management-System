<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <!-- CSS riêng của trang -->
                <jsp:include page="../layout/head.jsp">
                    <jsp:param name="pageTitle" value="Create Leave Request - HRMS" />
                    <jsp:param name="pageCss" value="leave-form.css" />
                </jsp:include>
            </head>

            <body>
                <!-- Sidebar (tự lập, tạo khung) -->
                <jsp:include page="../layout/sidebar.jsp">
                    <jsp:param name="currentPage" value="leave-request" />
                </jsp:include>

                <!-- Khung nội dung -->
                <div class="main-content" id="main-content">
                    <!-- Header fixed (tự lập, có avatar dropdown) -->
                    <jsp:include page="../layout/dashboard-header.jsp" />

                    <!-- Content -->
                    <div class="content-area">
                        <!-- Title -->
                        <div class="page-head">
                            <h2 class="page-title"><i class="fas fa-calendar-alt me-2"></i>Create Leave Request</h2>
                            <p class="page-subtitle">Submit a new leave request for approval</p>
                        </div>

                        <!-- Form Card -->
                        <div class="card leave-request-card">
                            <div class="card-header">
                                <h4><i class="fas fa-calendar-plus me-2"></i> Leave Request Form</h4>
                            </div>

                            <div class="card-body">
                                <!-- Alerts -->
                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger" role="alert">
                                        <i class="fas fa-exclamation-triangle me-2"></i>
                                        <c:out value="${error}" />
                                    </div>
                                </c:if>
                                <c:if test="${not empty success}">
                                    <div class="alert alert-success" role="alert">
                                        <i class="fas fa-check-circle me-2"></i>
                                        <c:out value="${success}" />
                                    </div>
                                </c:if>

                                <!-- Form -->
                                <form method="post" action="${pageContext.request.contextPath}/request/leave"
                                    id="leaveRequestForm" novalidate>
                                    <input type="hidden" name="action" value="create" />

                                    <!-- Leave type -->
                                    <div class="mb-3">
                                        <label for="leaveTypeCode" class="form-label">
                                            <i class="fas fa-list"></i> Leave Type <span class="text-danger">*</span>
                                        </label>
                                        <select class="form-select" id="leaveTypeCode" name="leaveTypeCode" required>
                                            <option value="">-- Select Leave Type --</option>
                                            <c:forEach var="leaveType" items="${leaveTypes}">
                                                <option value="${leaveType.key}" data-code="${leaveType.key}">
                                                    <c:out value="${leaveType.value}" />
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <div class="form-text">Choose the type of leave you want to request</div>
                                    </div>

                                    <!-- Leave type rules (auto show on change) -->
                                    <div id="leaveTypeRules" class="leave-type-rules d-none">
                                        <h6>Leave Type Information</h6>
                                        <div id="rulesContent" class="rules-content"></div>
                                    </div>

                                    <!-- Dates -->
                                    <div class="row g-3">
                                        <div class="col-md-6">
                                            <label for="startDate" class="form-label">
                                                <i class="fas fa-calendar-alt"></i> Start Date <span
                                                    class="text-danger">*</span>
                                            </label>
                                            <input type="datetime-local" class="form-control" id="startDate"
                                                name="startDate" required>
                                            <div class="form-text">When your leave will start</div>
                                        </div>
                                        <div class="col-md-6">
                                            <label for="endDate" class="form-label">
                                                <i class="fas fa-calendar-alt"></i> End Date <span
                                                    class="text-danger">*</span>
                                            </label>
                                            <input type="datetime-local" class="form-control" id="endDate"
                                                name="endDate" required>
                                            <div class="form-text">When your leave will end</div>
                                        </div>
                                    </div>

                                    <!-- Duration -->
                                    <div class="mt-3">
                                        <div class="duration-info d-none" id="durationInfo" role="status">
                                            <i class="fas fa-info-circle"></i>
                                            <span id="durationText">Duration will be calculated automatically</span>
                                        </div>
                                    </div>

                                    <!-- Reason -->
                                    <div class="mt-3">
                                        <label for="reason" class="form-label">
                                            <i class="fas fa-comment"></i> Reason <span class="text-danger">*</span>
                                        </label>
                                        <textarea class="form-control" id="reason" name="reason" rows="5"
                                            maxlength="1000"
                                            placeholder="Please provide the reason for your leave request..."
                                            required></textarea>
                                        <div class="char-counter"><span id="charCount">0</span>/1000 characters</div>
                                    </div>

                                    <!-- Actions -->
                                    <div class="mt-4 d-grid gap-2 d-md-flex justify-content-md-end">
                                        <a href="${pageContext.request.contextPath}/request/leave?action=list"
                                            class="btn btn-leave-secondary">
                                            <i class="fas fa-times me-1"></i> Cancel
                                        </a>
                                        <button type="submit" class="btn btn-leave-primary" id="submitBtn">
                                            <i class="fas fa-paper-plane me-1"></i> Submit Request
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>

                        <!-- Reference (optional) -->
                        <c:if test="${not empty leaveTypeRules}">
                            <div class="leave-types-reference">
                                <div class="card">
                                    <div class="card-header">
                                        <h5><i class="fas fa-info-circle me-2"></i> Leave Types Reference</h5>
                                    </div>
                                    <div class="card-body">
                                        <div class="row">
                                            <c:forEach var="rules" items="${leaveTypeRules}">
                                                <div class="col-md-6 mb-3">
                                                    <div class="card leave-type-card h-100">
                                                        <div class="card-body">
                                                            <h6 class="card-title">
                                                                <c:out value="${rules.name}" />
                                                            </h6>
                                                            <ul class="list-unstyled mb-0">
                                                                <li><small><strong>Default Days:</strong>
                                                                        ${rules.defaultDays}</small></li>
                                                                <li><small><strong>Max Days:</strong>
                                                                        ${rules.maxDays}</small></li>
                                                                <li><small><strong>Paid:</strong>
                                                                        <span
                                                                            class="leave-badge ${rules.isPaid ? 'success' : 'warning'}">
                                                                            ${rules.isPaid ? 'Yes' : 'No'}
                                                                        </span></small>
                                                                </li>
                                                                <li><small><strong>Approval:</strong>
                                                                        <span
                                                                            class="leave-badge ${rules.requiresApproval ? 'warning' : 'success'}">
                                                                            ${rules.requiresApproval ? 'Required' : 'Not
                                                                            Required'}
                                                                        </span></small>
                                                                </li>
                                                                <c:if
                                                                    test="${rules.minAdvanceNotice != null && rules.minAdvanceNotice > 0}">
                                                                    <li><small><strong>Advance Notice:</strong>
                                                                            ${rules.minAdvanceNotice} days</small></li>
                                                                </c:if>
                                                            </ul>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>

                    <!-- Footer (bundle JS ở đây để dropdown avatar chạy ở MỌI trang) -->
                    <jsp:include page="../layout/dashboard-footer.jsp" />
                </div>

                <!-- Leave Type Rules JSON (cho JS đọc) -->
                <script type="application/json" id="leaveTypeRulesData">
  {
    <c:forEach var="rules" items="${leaveTypeRules}" varStatus="status">
      "${rules.code}": {
        "name": "<c:out value='${rules.name}'/>",
        "defaultDays": ${rules.defaultDays},
        "maxDays": ${rules.maxDays},
        "isPaid": ${rules.isPaid},
        "requiresApproval": ${rules.requiresApproval},
        "requiresCertificate": ${rules.requiresCertificate},
        "minAdvanceNotice": ${rules.minAdvanceNotice}
      }<c:if test="${not status.last}">,</c:if>
    </c:forEach>
  }
  </script>

                <!-- Page JS (đặt sau footer để đảm bảo bootstrap.bundle đã nạp) -->
            >
            </body>

            </html>