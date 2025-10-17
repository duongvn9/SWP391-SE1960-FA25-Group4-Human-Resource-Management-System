<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../layout/head.jsp">
        <jsp:param name="pageTitle" value="My Requests - HRMS" />
        <jsp:param name="pageCss" value="dashboard.css" />
    </jsp:include>
    <style>
        .badge-duration {
            font-size: 0.85rem;
            padding: 0.35rem 0.65rem;
            border-radius: 0.25rem;
            font-weight: 600;
        }
        .badge-full-day {
            background-color: #3b82f6;
            color: white;
        }
        .badge-half-day-am {
            background-color: #f59e0b;
            color: white;
        }
        .badge-half-day-pm {
            background-color: #8b5cf6;
            color: white;
        }
        .badge-status {
            font-size: 0.85rem;
            padding: 0.35rem 0.65rem;
        }
        .filter-section {
            background: #f8f9fa;
            padding: 1rem;
            border-radius: 0.5rem;
            margin-bottom: 1.5rem;
        }
        .request-card {
            border: 1px solid #e5e7eb;
            border-radius: 0.5rem;
            padding: 1rem;
            margin-bottom: 1rem;
            transition: box-shadow 0.2s;
        }
        .request-card:hover {
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .duration-info {
            color: #6b7280;
            font-size: 0.9rem;
        }
        .time-range {
            color: #9ca3af;
            font-size: 0.85rem;
        }
    </style>
    <script>
        // Auto-submit form when filters change
        document.addEventListener('DOMContentLoaded', function() {
            const filterForm = document.querySelector('form[action*="requests/list"]');
            const filterSelects = filterForm.querySelectorAll('select');

            filterSelects.forEach(select => {
                select.addEventListener('change', function() {
                    // Auto-submit on filter change
                    // filterForm.submit();
                });
            });

            // Enable/disable period filter based on duration filter
            const durationFilter = document.getElementById('durationFilter');
            const periodFilter = document.getElementById('periodFilter');

            function updatePeriodFilter() {
                if (durationFilter.value === 'full-day') {
                    periodFilter.disabled = true;
                    periodFilter.value = '';
                } else {
                    periodFilter.disabled = false;
                }
            }

            durationFilter.addEventListener('change', updatePeriodFilter);
            updatePeriodFilter(); // Initialize on page load
        });
    </script>
</head>
<body>
    <!-- Sidebar -->
    <jsp:include page="../layout/sidebar.jsp">
        <jsp:param name="currentPage" value="requests" />
    </jsp:include>

    <!-- Main Content -->
    <div class="main-content" id="main-content">
        <!-- Header -->
        <jsp:include page="../layout/dashboard-header.jsp" />

        <!-- Content Area -->
        <div class="content-area">
            <div class="row mb-4">
                <div class="col-12">
                    <h2><i class="fas fa-clipboard-list me-2"></i>My Requests</h2>
                    <p class="text-muted">View and manage your leave and OT requests</p>
                </div>
            </div>

            <!-- Filter Section -->
            <div class="filter-section">
                <form method="get" action="${pageContext.request.contextPath}/requests/list" class="row g-3">
                    <div class="col-md-3">
                        <label for="statusFilter" class="form-label">Status</label>
                        <select class="form-select" id="statusFilter" name="status">
           <option value="">All Statuses</option>
                            <option value="PENDING" ${statusFilter == 'PENDING' ? 'selected' : ''}>Pending</option>
                            <option value="APPROVED" ${statusFilter == 'APPROVED' ? 'selected' : ''}>Approved</option>
                            <option value="REJECTED" ${statusFilter == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label for="typeFilter" class="form-label">Request Type</label>
                        <select class="form-select" id="typeFilter" name="type">
                            <option value="">All Types</option>
                            <option value="LEAVE_REQUEST" ${typeFilter == 'LEAVE_REQUEST' ? 'selected' : ''}>Leave Request</option>
                            <option value="OVERTIME_REQUEST" ${typeFilter == 'OVERTIME_REQUEST' ? 'selected' : ''}>OT Request</option>
                            <option value="ATTENDANCE_APPEAL" ${typeFilter == 'ATTENDANCE_APPEAL' ? 'selected' : ''}>Attendance Appeal</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label for="durationFilter" class="form-label">Duration</label>
                        <select class="form-select" id="durationFilter" name="duration">
                            <option value="">All Durations</option>
                            <option value="full-day" ${durationFilter == 'full-day' ? 'selected' : ''}>Full Day</option>
                            <option value="half-day" ${durationFilter == 'half-day' ? 'selected' : ''}>Half Day</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label for="periodFilter" class="form-label">Period</label>
                        <select class="form-select" id="periodFilter" name="period">
                            <option value="">All Periods</option>
                            <option value="AM" ${periodFilter == 'AM' ? 'selected' : ''}>Morning (AM)</option>
                            <option value="PM" ${periodFilter == 'PM' ? 'selected' : ''}>Afternoon (PM)</option>
                        </select>
                    </div>
                    <div class="col-12">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-filter me-2"></i>Apply Filters
                        </button>
                        <a href="${pageContext.request.contextPath}/requests/list" class="btn btn-secondary">
                            <i class="fas fa-times me-2"></i>Clear Filters
                        </a>
                    </div>
                </form>
            </div>

            <!-- Requests List -->
            <div class="row">
                <div class="col-12">
                    <c:choose>
                        <c:when test="${empty requests}">
                            <div class="alert alert-info">
                                <i class="fas fa-info-circle me-2"></i>
                                No requests found. <a href="${pageContext.request.contextPath}/requests/leave/create">Create a new request</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="req" items="${requests}">
                                <div class="request-card">
                                    <div class="row align-items-center">
                                        <div class="col-md-8">
                                            <div class="d-flex align-items-center mb-2">
                                                <h5 class="mb-0 me-3">${req.title}</h5>

                                                <!-- Duration Badge -->
                                                <c:choose>
                                                    <c:when test="${req.request.leaveDetail != null}">
                                                        <c:choose>
                                                            <c:when test="${req.request.leaveDetail.isHalfDay}">
                                                                <c:choose>
                                                                    <c:when test="${req.request.leaveDetail.halfDayPeriod == 'AM'}">
                                                                        <span class="badge badge-duration badge-half-day-am">
                                                                            Half Day (Morning)
                                                                        </span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="badge badge-duration badge-half-day-pm">
                                                                            Half Day (Afternoon)
                                                                        </span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge badge-duration badge-full-day">
                                                                    Full Day
                                                                </span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                </c:choose>

                                                <!-- Status Badge -->
                                                <c:choose>
                                                    <c:when test="${req.status == 'PENDING'}">
                                                        <span class="badge bg-warning ms-2">Pending</span>
                                                    </c:when>
                                                    <c:when test="${req.status == 'APPROVED'}">
                                                        <span class="badge bg-success ms-2">Approved</span>
                                                    </c:when>
                                                    <c:when test="${req.status == 'REJECTED'}">
                                                        <span class="badge bg-danger ms-2">Rejected</span>
                                                    </c:when>
                                                </c:choose>
                                            </div>

                                            <p class="mb-1">
                                                <strong>Type:</strong> ${req.requestTypeName}
                                            </p>

                                            <!-- Duration and Period Info -->
                                            <c:if test="${req.request.leaveDetail != null}">
                                                <p class="duration-info mb-1">
                                                    <i class="fas fa-calendar-day me-1"></i>
                                                    <strong>Duration:</strong>
                                                    <c:choose>
                                                        <c:when test="${req.request.leaveDetail.isHalfDay}">
                                                            0.5 days
                                                            <c:choose>
                                                                <c:when test="${req.request.leaveDetail.halfDayPeriod == 'AM'}">
                                                                    (Morning)
                                                                </c:when>
                                                                <c:otherwise>
                                                                    (Afternoon)
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:otherwise>
                                                            ${req.request.leaveDetail.dayCount} day(s)
                                                        </c:otherwise>
                                                    </c:choose>
                                                </p>

                                                <!-- Time Range for Half-Day -->
                                                <c:if test="${req.request.leaveDetail.isHalfDay}">
                                                    <p class="time-range mb-1">
                                                        <i class="fas fa-clock me-1"></i>
                                                        <c:choose>
                                                            <c:when test="${req.request.leaveDetail.halfDayPeriod == 'AM'}">
                                                                8:00 - 12:00
                                                            </c:when>
                                                            <c:otherwise>
                                                                13:00 - 17:00
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </p>
                                                </c:if>

                                                <p class="mb-1">
                                                    <i class="fas fa-calendar me-1"></i>
                                                    <strong>Date:</strong> ${req.request.leaveDetail.startDate}
                                                    <c:if test="${!req.request.leaveDetail.isHalfDay && req.request.leaveDetail.startDate != req.request.leaveDetail.endDate}">
                                                        to ${req.request.leaveDetail.endDate}
                                                    </c:if>
                                                </p>
                                            </c:if>

                                            <p class="text-muted mb-0">
                                                <small>
                                                    <i class="fas fa-clock me-1"></i>
                                                    Created: ${req.createdAt}
                                                </small>
                                            </p>
                                        </div>
                                        <div class="col-md-4 text-end">
                                            <a href="${pageContext.request.contextPath}/requests/detail?id=${req.id}"
                                               class="btn btn-outline-primary btn-sm">
                                                <i class="fas fa-eye me-1"></i>View Details
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <jsp:include page="../layout/dashboard-footer.jsp" />
    </div>
</body>
</html>
