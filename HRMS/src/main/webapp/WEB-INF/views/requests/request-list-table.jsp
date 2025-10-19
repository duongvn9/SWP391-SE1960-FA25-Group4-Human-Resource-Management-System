<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

            <!-- Reusable Request List Table Component -->
            <div class="table-responsive">
                <c:choose>
                    <c:when test="${not empty requests}">
                        <!-- Desktop Table View -->
                        <table class="table table-hover align-middle">
                            <thead class="table-light">
                                <tr>
                                    <th style="width: 70px;">ID</th>
                                    <th style="width: 130px;">Type</th>
                                    <th>Title</th>
                                    <th style="width: 180px;">Employee</th>
                                    <th style="width: 150px;">Department</th>
                                    <th style="width: 110px;">Status</th>
                                    <th style="width: 80px;" class="text-center">Files</th>
                                    <th style="width: 130px;">Created</th>
                                    <th style="width: 150px;" class="text-center">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${requests}" var="req">
                                    <tr>
                                        <!-- ID Column -->
                                        <td>
                                            <span class="text-muted">#${req.id}</span>
                                        </td>

                                        <!-- Type Column -->
                                        <td>
                                            <span class="badge bg-info text-dark">
                                                <i class="fas fa-tag me-1"></i>
                                                ${req.requestTypeName}
                                            </span>
                                        </td>

                                        <!-- Title Column -->
                                        <td>
                                            <strong>${req.title}</strong>
                                            <c:if test="${not empty req.description}">
                                                <br>
                                                <small class="text-muted">
                                                    <c:choose>
                                                        <c:when test="${req.description.length() > 50}">
                                                            ${req.description.substring(0, 50)}...
                                                        </c:when>
                                                        <c:otherwise>
                                                            ${req.description}
                                                        </c:otherwise>
                                                    </c:choose>
                                                </small>
                                            </c:if>
                                        </td>

                                        <!-- Employee Column -->
                                        <td>
                                            <div class="d-flex align-items-center">
                                                <div class="employee-avatar">
                                                    <c:choose>
                                                        <c:when test="${not empty req.userFullName}">
                                                            ${req.userFullName.substring(0, 1).toUpperCase()}
                                                        </c:when>
                                                        <c:otherwise>?</c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div>
                                                    <div class="fw-semibold">${req.userFullName}</div>
                                                    <small class="text-muted">${req.employeeCode}</small>
                                                </div>
                                            </div>
                                        </td>

                                        <!-- Department Column -->
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty req.departmentName}">
                                                    <i class="fas fa-building me-1 text-muted"></i>
                                                    <span class="text-muted">${req.departmentName}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted fst-italic">N/A</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <!-- Status Column -->
                                        <td>
                                            <span class="badge ${req.statusBadgeClass}">
                                                <c:choose>
                                                    <c:when test="${req.status == 'PENDING'}">
                                                        <i class="fas fa-clock me-1"></i>
                                                    </c:when>
                                                    <c:when test="${req.status == 'APPROVED'}">
                                                        <i class="fas fa-check-circle me-1"></i>
                                                    </c:when>
                                                    <c:when test="${req.status == 'REJECTED'}">
                                                        <i class="fas fa-times-circle me-1"></i>
                                                    </c:when>
                                                    <c:when test="${req.status == 'CANCELLED'}">
                                                        <i class="fas fa-ban me-1"></i>
                                                    </c:when>
                                                </c:choose>
                                                ${req.status}
                                            </span>
                                        </td>

                                        <!-- Attachment Indicator Column -->
                                        <td class="text-center">
                                            <c:if test="${req.attachmentCount > 0}">
                                                <span class="text-muted"
                                                      data-bs-toggle="tooltip"
                                                      data-bs-placement="top"
                                                      title="${req.attachmentCount} attachment(s)">
                                                    <i class="fas fa-paperclip"></i>
                                                    <small>${req.attachmentCount}</small>
                                                </span>
                                            </c:if>
                                        </td>

                                        <!-- Created Date Column -->
                                        <td>
                                            <i class="fas fa-calendar-alt me-1 text-muted"></i>
                                            <fmt:formatDate value="${req.createdAtAsDate}" pattern="dd/MM/yyyy" />
                                            <br>
                                            <small class="text-muted">
                                                <i class="fas fa-clock me-1"></i>
                                                <fmt:formatDate value="${req.createdAtAsDate}" pattern="HH:mm" />
                                            </small>
                                        </td>

                                        <!-- Actions Column -->
                                        <td class="text-center">
                                            <div class="btn-group btn-group-sm" role="group">
                                                <!-- View Detail Button (Always visible) -->
                                                <a href="${pageContext.request.contextPath}/requests/detail?id=${req.id}"
                                                    class="btn btn-outline-info" title="View Detail"
                                                    data-bs-toggle="tooltip" data-bs-placement="top">
                                                    <i class="fas fa-eye"></i>
                                                </a>

                                                <!-- Approve/Review Button (has approval permission) -->
                                                <c:if test="${req.canApprove}">
                                                    <c:choose>
                                                        <c:when test="${req.status == 'APPROVED'}">
                                                            <button onclick="openApprovalModal(${req.id}, '${req.title}')"
                                                                class="btn btn-warning"
                                                                title="Review Request"
                                                                data-bs-toggle="tooltip" data-bs-placement="top">
                                                                <i class="fas fa-clipboard-check"></i>
                                                            </button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <button onclick="openApprovalModal(${req.id}, '${req.title}')"
                                                                class="btn btn-warning"
                                                                title="Approve Request"
                                                                data-bs-toggle="tooltip" data-bs-placement="top">
                                                                <i class="fas fa-clipboard-check"></i>
                                                            </button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>

                        <!-- Mobile Card View -->
                        <div class="mobile-card-view" style="display: none;">
                            <c:forEach items="${requests}" var="req">
                                <div class="request-card">
                                    <div class="request-card-header">
                                        <div>
                                            <div class="request-card-id">#${req.id}</div>
                                            <div class="request-card-title">${req.title}</div>
                                        </div>
                                        <span class="badge ${req.statusBadgeClass}">
                                            <c:choose>
                                                <c:when test="${req.status == 'PENDING'}">
                                                    <i class="fas fa-clock me-1"></i>
                                                </c:when>
                                                <c:when test="${req.status == 'APPROVED'}">
                                                    <i class="fas fa-check-circle me-1"></i>
                                                </c:when>
                                                <c:when test="${req.status == 'REJECTED'}">
                                                    <i class="fas fa-times-circle me-1"></i>
                                                </c:when>
                                                <c:when test="${req.status == 'CANCELLED'}">
                                                    <i class="fas fa-ban me-1"></i>
                                                </c:when>
                                            </c:choose>
                                            ${req.status}
                                        </span>
                                    </div>

                                    <div class="request-card-body">
                                        <div class="request-card-info">
                                            <i class="fas fa-tag"></i>
                                            <span>${req.requestTypeName}</span>
                                        </div>
                                        <div class="request-card-info">
                                            <i class="fas fa-user"></i>
                                            <span>${req.userFullName} (${req.employeeCode})</span>
                                        </div>
                                        <c:if test="${not empty req.departmentName}">
                                            <div class="request-card-info">
                                                <i class="fas fa-building"></i>
                                                <span>${req.departmentName}</span>
                                            </div>
                                        </c:if>
                                        <div class="request-card-info">
                                            <i class="fas fa-calendar-alt"></i>
                                            <span>
                                                <fmt:formatDate value="${req.createdAtAsDate}"
                                                    pattern="dd/MM/yyyy HH:mm" />
                                            </span>
                                        </div>
                                        <c:if test="${req.attachmentCount > 0}">
                                            <div class="request-card-info">
                                                <i class="fas fa-paperclip"></i>
                                                <span>${req.attachmentCount} attachment(s)</span>
                                            </div>
                                        </c:if>
                                        <c:if test="${not empty req.description}">
                                            <div class="request-card-info">
                                                <i class="fas fa-align-left"></i>
                                                <span class="text-muted">
                                                    <c:choose>
                                                        <c:when test="${req.description.length() > 80}">
                                                            ${req.description.substring(0, 80)}...
                                                        </c:when>
                                                        <c:otherwise>
                                                            ${req.description}
                                                        </c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </div>
                                        </c:if>
                                    </div>

                                    <div class="request-card-footer">
                                        <div class="request-card-actions">
                                            <a href="${pageContext.request.contextPath}/requests/detail?id=${req.id}"
                                                class="btn btn-sm btn-outline-info">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <c:if test="${req.canApprove}">
                                                <button onclick="openApprovalModal(${req.id}, '${req.title}')"
                                                    class="btn btn-sm btn-warning">
                                                    <i class="fas fa-clipboard-check"></i>
                                                </button>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- Empty State -->
                        <div class="empty-state text-center py-5">
                            <div class="mb-4">
                                <i class="fas fa-inbox"></i>
                            </div>
                            <h4 class="mb-3">No requests found</h4>
                            <p class="text-muted mb-4">
                                <c:choose>
                                    <c:when
                                        test="${not empty filter.searchKeyword || not empty filter.status || not empty filter.requestTypeId}">
                                        No requests match your current filters. Try adjusting your search criteria.
                                    </c:when>
                                    <c:when test="${filter.scope == 'subordinate'}">
                                        No requests from subordinates found.
                                    </c:when>
                                    <c:when test="${filter.scope == 'all'}">
                                        No requests found in the system.
                                    </c:when>
                                    <c:otherwise>
                                        You haven't created any requests yet. Get started by creating your first
                                        request.
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <div class="d-flex gap-2 justify-content-center flex-wrap">
                                <!-- Only show create buttons for "my" scope -->
                                <c:if test="${filter.scope == 'my'}">
                                    <a href="${pageContext.request.contextPath}/requests/leave/create"
                                        class="btn btn-sm btn-primary">
                                        <i class="fas fa-plus me-1"></i>Create Leave Request
                                    </a>
                                    <a href="${pageContext.request.contextPath}/requests/ot/create"
                                        class="btn btn-sm btn-outline-primary">
                                        <i class="fas fa-plus me-1"></i>Create OT Request
                                    </a>
                                </c:if>
                                <!-- Show clear filters button if filters are active -->
                                <c:if
                                    test="${not empty filter.searchKeyword || not empty filter.status || not empty filter.requestTypeId || not empty filter.fromDate || not empty filter.toDate}">
                                    <a href="${pageContext.request.contextPath}/requests?scope=${filter.scope}"
                                        class="btn btn-sm btn-outline-secondary">
                                        <i class="fas fa-times me-1"></i>Clear Filters
                                    </a>
                                </c:if>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Initialize Bootstrap tooltips -->
            <script>
                document.addEventListener('DOMContentLoaded', function () {
                    // Initialize tooltips for action buttons
                    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
                    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
                        return new bootstrap.Tooltip(tooltipTriggerEl);
                    });
                });
            </script>