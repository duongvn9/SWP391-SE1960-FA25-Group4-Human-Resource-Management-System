<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

            <!-- Overtime Request Details Card -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">Overtime Request Details</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <!-- OT Date -->
                        <div class="col-md-6 mb-3">
                            <label class="fw-bold text-muted">OT Date:</label>
                            <p class="mb-0">
                                <c:out value="${otDetail.otDate}" />
                            </p>
                        </div>

                        <!-- OT Type -->
                        <div class="col-md-6 mb-3">
                            <label class="fw-bold text-muted">OT Type:</label>
                            <p class="mb-0">
                                <c:choose>
                                    <c:when test="${otDetail.otType == 'WEEKDAY'}">
                                        <span class="badge bg-primary">Weekday</span>
                                    </c:when>
                                    <c:when test="${otDetail.otType == 'WEEKEND'}">
                                        <span class="badge bg-info">Weekend</span>
                                    </c:when>
                                    <c:when test="${otDetail.otType == 'HOLIDAY'}">
                                        <span class="badge bg-danger">Holiday</span>
                                    </c:when>
                                    <c:when test="${otDetail.otType == 'COMPENSATORY'}">
                                        <span class="badge bg-secondary">Compensatory</span>
                                    </c:when>
                                    <c:otherwise>
                                        <c:out value="${otDetail.otType}" />
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </div>

                        <!-- Start Time -->
                        <div class="col-md-6 mb-3">
                            <label class="fw-bold text-muted">Start Time:</label>
                            <p class="mb-0">
                                <c:out value="${otDetail.startTime}" />
                            </p>
                        </div>

                        <!-- End Time -->
                        <div class="col-md-6 mb-3">
                            <label class="fw-bold text-muted">End Time:</label>
                            <p class="mb-0">
                                <c:out value="${otDetail.endTime}" />
                            </p>
                        </div>

                        <!-- Total OT Hours -->
                        <div class="col-md-6 mb-3">
                            <label class="fw-bold text-muted">Total OT Hours:</label>
                            <p class="mb-0">
                                <c:out value="${otDetail.otHours}" /> hour(s)
                            </p>
                        </div>

                        <!-- Pay Multiplier -->
                        <div class="col-md-6 mb-3">
                            <label class="fw-bold text-muted">Pay Multiplier:</label>
                            <p class="mb-0">
                                <c:out value="${otDetail.payMultiplier}" />x
                            </p>
                        </div>

                        <!-- Reason -->
                        <div class="col-12 mb-3">
                            <label class="fw-bold text-muted">Reason:</label>
                            <p class="mb-0">
                                <c:out value="${otDetail.reason}" />
                            </p>
                        </div>

                        <!-- Manager Notes (if available) -->
                        <c:if test="${not empty otDetail.managerNotes}">
                            <div class="col-12 mb-3">
                                <label class="fw-bold text-muted">Manager Notes:</label>
                                <p class="mb-0">
                                    <c:out value="${otDetail.managerNotes}" />
                                </p>
                            </div>
                        </c:if>

                        <!-- Employee Consent -->
                        <div class="col-md-6 mb-3">
                            <label class="fw-bold text-muted">Employee Consent:</label>
                            <p class="mb-0">
                                <c:choose>
                                    <c:when test="${otDetail.employeeConsent}">
                                        <span class="badge bg-success">
                                            <i class="fas fa-check-circle"></i> Confirmed
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-warning text-dark">
                                            <i class="fas fa-exclamation-circle"></i> Pending
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </div>

                        <!-- Consent Timestamp -->
                        <c:if test="${not empty otDetail.consentTimestamp}">
                            <div class="col-md-6 mb-3">
                                <label class="fw-bold text-muted">Consent Given At:</label>
                                <p class="mb-0">
                                    <c:out value="${otDetail.consentTimestamp}" />
                                </p>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>