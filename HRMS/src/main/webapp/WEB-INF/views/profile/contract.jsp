<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Employment Contract - HRMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/contract.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
        }
        .status-badge {
            display: inline-block;
            padding: 0.35em 0.65em;
            font-size: 0.95rem;
            font-weight: 500;
            border-radius: 0.25rem;
        }
        .status-active {
            background-color: #d1e7dd;
            color: #0f5132;
        }
        .status-expired {
            background-color: #e2e3e5;
            color: #41464b;
        }
        .status-terminated {
            background-color: #f8d7da;
            color: #842029;
        }
    </style>
</head>
<body>
    <!-- Include dashboard header -->
    <jsp:include page="../layout/dashboard-header.jsp" />
    
    <!-- Include sidebar -->
    <jsp:include page="../layout/sidebar.jsp" />
    
    <div class="main-content">
        <div class="contract-page-wrap">
            <c:choose>
                <c:when test="${not empty contract}">
                    <div class="contract-card mx-auto mt-5 mb-5">
                        <!-- Logo & Title Centered -->
                        <div class="text-center mb-4">
                            <i class="fa-solid fa-file-contract contract-logo-icon"></i>
                            <div class="contract-header-title mt-2">Employment Contract</div>
                        </div>
                        <form>
                            <div class="mb-3">
                                <label class="form-label">Contract ID</label>
                                <input type="text" class="form-control" value="<c:out value='${contract.id}'/>" readonly>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">User ID</label>
                                <input type="text" class="form-control" value="<c:out value='${contract.userId}'/>" readonly>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Contract Number</label>
                                <input type="text" class="form-control" value="<c:out value='${contract.contractNo}'/>" readonly>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Contract Type</label>
                                <input type="text" class="form-control" value="<c:out value='${contract.contractTypeDisplay}'/>" readonly>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Start Date</label>
                                <input type="text" class="form-control" value="<c:out value='${contract.formattedStartDate}'/>" readonly>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">End Date</label>
                                <input type="text" class="form-control" value="<c:out value='${contract.formattedEndDate}'/>" readonly>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Base Salary</label>
                                <input type="text" class="form-control" value="<c:out value='${contract.formattedSalary}'/>" readonly>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Currency</label>
                                <input type="text" class="form-control" value="<c:out value='${contract.currency}'/>" readonly>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Status</label>
                                <div class="mt-2">
                                    <span class="status-badge status-${contract.statusColor}">
                                        <c:out value="${contract.statusDisplay}"/>
                                    </span>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">File Path</label>
                                <input type="text" class="form-control" value="<c:out value='${contract.filePath}'/>" readonly>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Note</label>
                                <textarea class="form-control" rows="2" readonly><c:out value="${contract.note}"/></textarea>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Created By (Account ID)</label>
                                <input type="text" class="form-control" value="<c:out value='${contract.createdByAccountId}'/>" readonly>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Created At</label>
                                <input type="text" class="form-control" value="<c:out value='${contract.formattedCreatedAt}'/>" readonly>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Updated At</label>
                                <input type="text" class="form-control" value="<c:out value='${contract.formattedUpdatedAt}'/>" readonly>
                            </div>
                        </form>
                        <!-- Back to Dashboard button at bottom -->
                        <div class="text-center mt-5">
                            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-primary px-4">
                                <i class="fa-solid fa-arrow-left-long me-1"></i> Back to Dashboard
                            </a>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="container mt-5">
                        <div class="alert alert-info text-center" role="alert">
                            <i class="fas fa-info-circle me-2"></i>
                            <c:out value="${message != null ? message : 'Không tìm thấy hợp đồng hiện tại'}"/>
                        </div>
                        <div class="text-center mt-4">
                            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-primary px-4">
                                <i class="fa-solid fa-arrow-left-long me-1"></i> Back to Dashboard
                            </a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>