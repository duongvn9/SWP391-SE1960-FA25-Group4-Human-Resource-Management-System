
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Employment Contract</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/contract.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="contract-page-wrap">
        <div class="contract-card mx-auto mt-5 mb-5">
            <!-- Logo & Title Centered -->
            <div class="text-center mb-4">
                <i class="fa-solid fa-file-contract contract-logo-icon"></i>
                <div class="contract-header-title mt-2">Employment Contract</div>
            </div>
            <form>
                <div class="mb-3">
                    <label class="form-label">Contract ID</label>
                    <input type="text" class="form-control" value="1" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">User ID</label>
                    <input type="text" class="form-control" value="1001" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">Contract Number</label>
                    <input type="text" class="form-control" value="CT20250001" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">Contract Type</label>
                    <input type="text" class="form-control" value="Full-time" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">Start Date</label>
                    <input type="date" class="form-control" value="2024-11-24" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">End Date</label>
                    <input type="date" class="form-control" value="2025-11-24" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">Base Salary</label>
                    <input type="text" class="form-control" value="1200.00" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">Currency</label>
                    <input type="text" class="form-control" value="USD" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">Status</label>
                    <input type="text" class="form-control" value="Active" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">File Path</label>
                    <input type="text" class="form-control" value="/contracts/CT20250001.pdf" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">Note</label>
                    <textarea class="form-control" rows="2" readonly>Contract valid for 12 months.</textarea>
                </div>
                <div class="mb-3">
                    <label class="form-label">Created By (Account ID)</label>
                    <input type="text" class="form-control" value="2001" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">Created At</label>
                    <input type="text" class="form-control" value="2024-11-01 09:00:00" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">Updated At</label>
                    <input type="text" class="form-control" value="2024-11-01 09:00:00" readonly>
                </div>
            </form>
            <!-- Back to Dashboard button at bottom -->
            <div class="text-center mt-5">
                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-primary px-4">
                    <i class="fa-solid fa-arrow-left-long me-1"></i> Back to Dashboard
                </a>
            </div>
        </div>
    </div>
</body>
</html>