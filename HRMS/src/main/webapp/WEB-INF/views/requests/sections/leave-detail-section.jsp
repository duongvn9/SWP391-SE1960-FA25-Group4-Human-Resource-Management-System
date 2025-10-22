<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

            <!-- Leave Request Details Card -->
            <div class="card mb-4 shadow-sm border-0">
                <div class="card-header bg-gradient-success text-white">
                    <h5 class="mb-0">Leave Request Details</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <!-- Leave Type -->
                        <div class="col-md-6 mb-3">
                            <label class="fw-bold text-muted">Leave Type:</label>
                            <p class="mb-0">
                                <c:out value="${leaveDetail.leaveTypeName}" />
                            </p>
                        </div>

                        <!-- Start Date -->
                        <div class="col-md-6 mb-3">
                            <label class="fw-bold text-muted">Start Date:</label>
                            <p class="mb-0">
                                <c:out value="${leaveDetail.startDate}" />
                            </p>
                        </div>

                        <!-- End Date -->
                        <div class="col-md-6 mb-3">
                            <label class="fw-bold text-muted">End Date:</label>
                            <p class="mb-0">
                                <c:out value="${leaveDetail.endDate}" />
                            </p>
                        </div>

                        <!-- Total Working Days -->
                        <div class="col-md-6 mb-3">
                            <label class="fw-bold text-muted">Total Working Days:</label>
                            <p class="mb-0">
                                <c:out value="${leaveDetail.dayCount}" />
                            </p>
                        </div>

                        <!-- Half-Day Indicator -->
                        <div class="col-md-6 mb-3">
                            <label class="fw-bold text-muted">Half-Day Leave:</label>
                            <p class="mb-0">
                                <c:choose>
                                    <c:when test="${leaveDetail.isHalfDay}">
                                        Yes (
                                        <c:out value="${leaveDetail.halfDayPeriod}" />)
                                    </c:when>
                                    <c:otherwise>
                                        No
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </div>

                        <!-- Duration in Days -->
                        <div class="col-md-6 mb-3">
                            <label class="fw-bold text-muted">Duration:</label>
                            <p class="mb-0">
                                <c:out value="${leaveDetail.durationDays}" /> day(s)
                            </p>
                        </div>

                        <!-- Certificate Required -->
                        <div class="col-md-6 mb-3">
                            <label class="fw-bold text-muted">Certificate Required:</label>
                            <p class="mb-0">
                                <c:choose>
                                    <c:when test="${leaveDetail.certificateRequired}">
                                        <span class="badge bg-warning text-dark">
                                            <i class="fas fa-file-medical"></i> Yes
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">No</span>
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </div>

                        <!-- Reason -->
                        <div class="col-12 mb-3">
                            <label class="fw-bold text-muted">Reason:</label>
                            <p class="mb-0">
                                <c:out value="${leaveDetail.reason}" />
                            </p>
                        </div>

                        <!-- Attachment -->
                        <c:if test="${not empty leaveDetail.attachmentPath}">
                            <div class="col-12 mb-3">
                                <label class="fw-bold text-muted">Attachment:</label>
                                <p class="mb-0">
                                    <a href="javascript:void(0);"
                                        onclick="openAttachmentModal('${pageContext.request.contextPath}${leaveDetail.attachmentPath}')"
                                        class="btn btn-sm btn-outline-primary">
                                        <i class="fas fa-paperclip"></i> View Attachment
                                    </a>
                                </p>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>