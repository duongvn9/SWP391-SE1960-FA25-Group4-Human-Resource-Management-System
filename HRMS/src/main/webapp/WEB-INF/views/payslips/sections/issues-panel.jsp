<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!-- Issues Panel Section -->
<c:if test="${userRole == 'HRM' and (not empty param.periodStart and not empty param.periodEnd)}">
    <div class="card mt-4">
        <div class="card-header">
            <div class="d-flex justify-content-between align-items-center">
                <h5 class="mb-0">
                    <i class="fas fa-exclamation-triangle me-2 text-warning"></i>Issues Panel
                </h5>
                <div class="d-flex align-items-center gap-2">
                    <span class="badge bg-secondary">
                        <c:choose>
                            <c:when test="${not empty param.filterMonth and not empty param.filterYear}">
                                Period:
                                <c:choose>
                                    <c:when test="${param.filterMonth == '1'}">Jan</c:when>
                                    <c:when test="${param.filterMonth == '2'}">Feb</c:when>
                                    <c:when test="${param.filterMonth == '3'}">Mar</c:when>
                                    <c:when test="${param.filterMonth == '4'}">Apr</c:when>
                                    <c:when test="${param.filterMonth == '5'}">May</c:when>
                                    <c:when test="${param.filterMonth == '6'}">Jun</c:when>
                                    <c:when test="${param.filterMonth == '7'}">Jul</c:when>
                                    <c:when test="${param.filterMonth == '8'}">Aug</c:when>
                                    <c:when test="${param.filterMonth == '9'}">Sep</c:when>
                                    <c:when test="${param.filterMonth == '10'}">Oct</c:when>
                                    <c:when test="${param.filterMonth == '11'}">Nov</c:when>
                                    <c:when test="${param.filterMonth == '12'}">Dec</c:when>
                                </c:choose>
                                ${param.filterYear}
                            </c:when>
                            <c:when test="${not empty param.periodStart}">
                                Period: ${param.periodStart}
                            </c:when>
                            <c:otherwise>
                                No Period Selected
                            </c:otherwise>
                        </c:choose>
                    </span>
                    <button type="button" class="btn btn-sm btn-outline-secondary" onclick="refreshIssues()">
                        <i class="fas fa-refresh me-1"></i>Refresh
                    </button>
                </div>
            </div>
        </div>
        <div class="card-body">
            <div class="row">
                <!-- Missing Payslips Section -->
                <div class="col-md-6">
                    <div class="card border-danger h-100">
                        <div class="card-header bg-danger text-white">
                            <h6 class="mb-0">
                                <i class="fas fa-times-circle me-2"></i>Missing Payslips
                                <span class="badge bg-light text-danger ms-2">
                                    ${not empty missingPayslipIssues ? fn:length(missingPayslipIssues) : 0}
                                </span>
                            </h6>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty missingPayslipIssues}">
                                    <div class="list-group list-group-flush">
                                        <c:forEach var="issue" items="${missingPayslipIssues}" varStatus="status">
                                            <c:if test="${status.index < 10}"> <!-- Show only first 10 -->
                                                <div class="list-group-item d-flex justify-content-between align-items-center px-0">
                                                    <div>
                                                        <div class="fw-medium">${issue.userFullName}</div>
                                                        <small class="text-muted">
                                                            ${issue.userEmployeeId} - ${issue.departmentName}
                                                        </small>
                                                        <c:if test="${not empty issue.reason}">
                                                            <div class="small text-danger mt-1">
                                                                <i class="fas fa-info-circle me-1"></i>${issue.reason}
                                                            </div>
                                                        </c:if>
                                                    </div>
                                                    <button type="button"
                                                            class="btn btn-sm btn-outline-primary"
                                                            onclick="quickGenerate(${issue.userId}, '${param.periodStart}', '${param.periodEnd}')"
                                                            title="Quick Generate">
                                                        <i class="fas fa-plus"></i>
                                                    </button>
                                                </div>
                                            </c:if>
                                        </c:forEach>

                                        <!-- Show more link if there are more than 10 -->
                                        <c:if test="${fn:length(missingPayslipIssues) > 10}">
                                            <div class="list-group-item px-0 text-center">
                                                <button type="button" class="btn btn-sm btn-link" onclick="showAllMissingIssues()">
                                                    <i class="fas fa-ellipsis-h me-1"></i>
                                                    Show ${fn:length(missingPayslipIssues) - 10} more...
                                                </button>
                                            </div>
                                        </c:if>
                                    </div>

                                    <!-- Bulk Actions for Missing -->
                                    <div class="mt-3 d-grid gap-2">
                                        <button type="button" class="btn btn-danger btn-sm" onclick="generateAllMissing()">
                                            <i class="fas fa-play me-2"></i>Generate All Missing (${fn:length(missingPayslipIssues)})
                                        </button>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-center py-4">
                                        <i class="fas fa-check-circle fa-2x text-success mb-2"></i>
                                        <div class="text-muted">No missing payslips</div>
                                        <small class="text-muted">All employees have payslips for this period</small>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <!-- Dirty Payslips Section -->
                <div class="col-md-6">
                    <div class="card border-warning h-100">
                        <div class="card-header bg-warning text-dark">
                            <h6 class="mb-0">
                                <i class="fas fa-exclamation-triangle me-2"></i>Dirty Payslips
                                <span class="badge bg-light text-warning ms-2">
                                    ${not empty dirtyPayslipIssues ? fn:length(dirtyPayslipIssues) : 0}
                                </span>
                            </h6>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty dirtyPayslipIssues}">
                                    <div class="list-group list-group-flush">
                                        <c:forEach var="issue" items="${dirtyPayslipIssues}" varStatus="status">
                                            <c:if test="${status.index < 10}"> <!-- Show only first 10 -->
                                                <div class="list-group-item d-flex justify-content-between align-items-center px-0">
                                                    <div class="flex-grow-1">
                                                        <div class="fw-medium">${issue.userFullName}</div>
                                                        <small class="text-muted">
                                                            ${issue.userEmployeeId} - ${issue.departmentName}
                                                        </small>
                                                        <c:if test="${not empty issue.dirtyReason}">
                                                            <div class="small text-warning mt-1">
                                                                <i class="fas fa-info-circle me-1"></i>${issue.dirtyReason}
                                                            </div>
                                                        </c:if>
                                                        <c:if test="${not empty issue.lastChangedAt}">
                                                            <div class="small text-muted">
                                                                Changed: <fmt:formatDate value="${issue.lastChangedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                            </div>
                                                        </c:if>
                                                    </div>
                                                    <div class="btn-group" role="group">
                                                        <button type="button"
                                                                class="btn btn-sm btn-outline-warning"
                                                                onclick="quickRegenerate(${issue.payslipId})"
                                                                title="Quick Regenerate">
                                                            <i class="fas fa-sync"></i>
                                                        </button>
                                                        <a href="${pageContext.request.contextPath}/payslips/${issue.payslipId}"
                                                           class="btn btn-sm btn-outline-primary"
                                                           title="View Details">
                                                            <i class="fas fa-eye"></i>
                                                        </a>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </c:forEach>

                                        <!-- Show more link if there are more than 10 -->
                                        <c:if test="${fn:length(dirtyPayslipIssues) > 10}">
                                            <div class="list-group-item px-0 text-center">
                                                <button type="button" class="btn btn-sm btn-link" onclick="showAllDirtyIssues()">
                                                    <i class="fas fa-ellipsis-h me-1"></i>
                                                    Show ${fn:length(dirtyPayslipIssues) - 10} more...
                                                </button>
                                            </div>
                                        </c:if>
                                    </div>

                                    <!-- Bulk Actions for Dirty -->
                                    <div class="mt-3 d-grid gap-2">
                                        <button type="button" class="btn btn-warning btn-sm" onclick="regenerateAllDirty()">
                                            <i class="fas fa-sync me-2"></i>Regenerate All Dirty (${fn:length(dirtyPayslipIssues)})
                                        </button>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-center py-4">
                                        <i class="fas fa-check-circle fa-2x text-success mb-2"></i>
                                        <div class="text-muted">No dirty payslips</div>
                                        <small class="text-muted">All payslips are up to date</small>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Overall Issues Summary -->
            <c:if test="${not empty missingPayslipIssues or not empty dirtyPayslipIssues}">
                <div class="alert alert-info mt-4" role="alert">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <i class="fas fa-info-circle me-2"></i>
                            <strong>Issues Summary:</strong>
                            ${fn:length(missingPayslipIssues)} missing, ${fn:length(dirtyPayslipIssues)} dirty payslips found.
                        </div>
                        <div class="btn-group" role="group">
                            <button type="button" class="btn btn-sm btn-primary" onclick="resolveAllIssues()">
                                <i class="fas fa-magic me-1"></i>Resolve All Issues
                            </button>
                            <button type="button" class="btn btn-sm btn-outline-primary" onclick="exportIssuesReport()">
                                <i class="fas fa-file-export me-1"></i>Export Report
                            </button>
                        </div>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</c:if>

<!-- Issues Detail Modal -->
<div class="modal fade" id="issuesDetailModal" tabindex="-1" aria-labelledby="issuesDetailModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="issuesDetailModalLabel">
                    <i class="fas fa-list me-2"></i>All Issues
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div id="issuesDetailContent">
                    <!-- Content will be loaded dynamically -->
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<script>
function quickGenerate(userId, periodStart, periodEnd) {
    if (confirm('Generate payslip for this employee?')) {
        showProgressModal();

        fetch('${pageContext.request.contextPath}/payslips/quick-generate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                userId: userId,
                periodStart: periodStart,
                periodEnd: periodEnd
            })
        })
        .then(response => response.json())
        .then(result => {
            hideProgressModal();
            if (result.success) {
                alert('Payslip generated successfully!');
                location.reload();
            } else {
                alert('Error generating payslip: ' + result.message);
            }
        })
        .catch(error => {
            hideProgressModal();
            console.error('Error:', error);
            alert('Error generating payslip.');
        });
    }
}

function quickRegenerate(payslipId) {
    if (confirm('Regenerate this payslip?')) {
        showProgressModal();

        fetch(`${pageContext.request.contextPath}/payslips/${payslipId}/regenerate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then(response => response.json())
        .then(result => {
            hideProgressModal();
            if (result.success) {
                alert('Payslip regenerated successfully!');
                location.reload();
            } else {
                alert('Error regenerating payslip: ' + result.message);
            }
        })
        .catch(error => {
            hideProgressModal();
            console.error('Error:', error);
            alert('Error regenerating payslip.');
        });
    }
}

function generateAllMissing() {
    const count = ${fn:length(missingPayslipIssues)};
    if (count === 0) return;

    if (confirm(`Generate all ${count} missing payslips?`)) {
        showProgressModal();

        fetch('${pageContext.request.contextPath}/payslips/generate-missing', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                periodStart: '${param.periodStart}',
                periodEnd: '${param.periodEnd}'
            })
        })
        .then(response => response.json())
        .then(result => {
            hideProgressModal();
            alert(`Generation completed: ${result.createdCount} created, ${result.errorCount} errors`);
            location.reload();
        })
        .catch(error => {
            hideProgressModal();
            console.error('Error:', error);
            alert('Error generating missing payslips.');
        });
    }
}

function regenerateAllDirty() {
    const count = ${fn:length(dirtyPayslipIssues)};
    if (count === 0) return;

    if (confirm(`Regenerate all ${count} dirty payslips?`)) {
        showProgressModal();

        fetch('${pageContext.request.contextPath}/payslips/regenerate-dirty', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                periodStart: '${param.periodStart}',
                periodEnd: '${param.periodEnd}'
            })
        })
        .then(response => response.json())
        .then(result => {
            hideProgressModal();
            alert(`Regeneration completed: ${result.updatedCount} updated, ${result.errorCount} errors`);
            location.reload();
        })
        .catch(error => {
            hideProgressModal();
            console.error('Error:', error);
            alert('Error regenerating dirty payslips.');
        });
    }
}

function resolveAllIssues() {
    const missingCount = ${fn:length(missingPayslipIssues)};
    const dirtyCount = ${fn:length(dirtyPayslipIssues)};
    const totalCount = missingCount + dirtyCount;

    if (totalCount === 0) return;

    if (confirm(`Resolve all issues? This will generate ${missingCount} missing and regenerate ${dirtyCount} dirty payslips.`)) {
        showProgressModal();

        fetch('${pageContext.request.contextPath}/payslips/resolve-all-issues', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                periodStart: '${param.periodStart}',
                periodEnd: '${param.periodEnd}'
            })
        })
        .then(response => response.json())
        .then(result => {
            hideProgressModal();
            alert(`All issues resolved: ${result.createdCount} created, ${result.updatedCount} updated, ${result.errorCount} errors`);
            location.reload();
        })
        .catch(error => {
            hideProgressModal();
            console.error('Error:', error);
            alert('Error resolving issues.');
        });
    }
}

function showAllMissingIssues() {
    loadIssuesDetail('missing');
}

function showAllDirtyIssues() {
    loadIssuesDetail('dirty');
}

function loadIssuesDetail(type) {
    const modal = new bootstrap.Modal(document.getElementById('issuesDetailModal'));
    const content = document.getElementById('issuesDetailContent');

    content.innerHTML = '<div class="text-center"><i class="fas fa-spinner fa-spin"></i> Loading...</div>';
    modal.show();

    fetch(`${pageContext.request.contextPath}/payslips/issues-detail?type=${type}&periodStart=${param.periodStart}&periodEnd=${param.periodEnd}`)
        .then(response => response.text())
        .then(html => {
            content.innerHTML = html;
        })
        .catch(error => {
            console.error('Error:', error);
            content.innerHTML = '<div class="alert alert-danger">Error loading issues detail.</div>';
        });
}

function exportIssuesReport() {
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '${pageContext.request.contextPath}/payslips/export-issues-report';
    form.style.display = 'none';

    const periodStartInput = document.createElement('input');
    periodStartInput.type = 'hidden';
    periodStartInput.name = 'periodStart';
    periodStartInput.value = '${param.periodStart}';
    form.appendChild(periodStartInput);

    const periodEndInput = document.createElement('input');
    periodEndInput.type = 'hidden';
    periodEndInput.name = 'periodEnd';
    periodEndInput.value = '${param.periodEnd}';
    form.appendChild(periodEndInput);

    document.body.appendChild(form);
    form.submit();
    document.body.removeChild(form);
}

function refreshIssues() {
    location.reload();
}

// Note: showProgressModal() and hideProgressModal() are defined in bulk-actions.jsp and reused here
</script>

<style>
.list-group-item {
    border-left: none;
    border-right: none;
}

.list-group-item:first-child {
    border-top: none;
}

.list-group-item:last-child {
    border-bottom: none;
}

.card.border-danger .card-header {
    border-bottom: 1px solid rgba(220, 53, 69, 0.2);
}

.card.border-warning .card-header {
    border-bottom: 1px solid rgba(255, 193, 7, 0.2);
}

.btn-group .btn {
    border-radius: 0.25rem;
    margin-right: 2px;
}

.btn-group .btn:last-child {
    margin-right: 0;
}

@media (max-width: 768px) {
    .col-md-6 {
        margin-bottom: 1rem;
    }

    .btn-group {
        flex-direction: column;
    }

    .btn-group .btn {
        margin-right: 0;
        margin-bottom: 2px;
    }
}
</style>