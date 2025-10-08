<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Hồ sơ cá nhân</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body{
            min-height:100vh;
            background:linear-gradient(160deg,#7b8dfb 0%,#7b5ffb 100%);
        }
        .page-wrap{ max-width:1100px; margin:48px auto; padding:0 16px; }
        .profile-shell{ display:flex; gap:28px; align-items:flex-start; }

        /* LEFT */
        .left-card{
            width:340px; background:#fff; border-radius:18px;
            box-shadow:0 4px 24px rgba(0,0,0,.07); padding:24px;
        }
        .name-row{ display:flex; align-items:center; gap:10px; flex-wrap:wrap; }
        .left-name{ font-size:1.1rem; font-weight:700; margin:0; }
        .badge-admin{
            background:#fdecec; color:#d12a2a; font-weight:600;
            border-radius:999px; padding:4px 10px; font-size:.8rem;
        }
        .left-email{ color:#6c757d; margin-top:4px; margin-bottom:12px; word-break:break-all; }
        .left-actions a{ display:inline-flex; align-items:center; gap:8px; text-decoration:none; }

        /* RIGHT (form) */
        .profile-card{
            flex:1; background:#fff; border-radius:18px;
            box-shadow:0 4px 24px rgba(0,0,0,.07); padding:32px;
        }
        .hrms-logo{ display:flex; flex-direction:column; align-items:center; margin-bottom:10px; }
        .hrms-logo-icon{ font-size:2.5rem; color:#7b8dfb; }
        .hrms-logo-text{ font-weight:700; font-size:1.1rem; color:#7b8dfb; letter-spacing:1px; }
        .profile-header-title{ text-align:center; font-size:1.25rem; font-weight:600; margin-bottom:18px; color:#222; }
        .form-control[readonly]{ background:#f6f7fa; border:none; color:#222; font-weight:500; }
        .btn{ border-radius:8px; font-weight:500; }
        @media (max-width: 992px){ .profile-shell{ flex-direction:column-reverse } .left-card{ width:100% } }
    </style>
</head>
<body>
<div class="page-wrap">

    <div class="profile-shell">
        <!-- LEFT: Tên + email + admin + link + nút Dashboard -->
        <div class="left-card">
            <div class="name-row">
                <h5 class="left-name">${user.fullName}</h5>
                <span class="badge-admin"><i class="fa-solid fa-user-shield me-1"></i>Admin</span>
            </div>
            <div class="left-email">${user.email}</div>

            <div class="left-actions mb-3">
                <a class="text-primary" href="${pageContext.request.contextPath}/salary/history">
                    <i class="fa-solid fa-receipt"></i> Xem lịch sử lương
                </a>
            </div>

            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-primary w-100">
                <i class="fa-solid fa-arrow-left-long me-1"></i> Quay về Dashboard
            </a>
        </div>

        <!-- RIGHT: giữ nguyên form gồm Họ tên + Email + các trường khác -->
        <div class="profile-card">
            <div class="hrms-logo">
                <i class="fa-solid fa-users-gear hrms-logo-icon"></i>
                <div class="hrms-logo-text">HRMS</div>
            </div>
            <div class="profile-header-title">Hồ sơ cá nhân</div>

            <form>
                <div class="mb-3">
                    <label class="form-label">Họ và tên</label>
                    <input type="text" class="form-control" value="${user.fullName}" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">Email</label>
                    <input type="email" class="form-control" value="${user.email}" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">Số điện thoại</label>
                    <input type="text" class="form-control" value="${user.phone}" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">Phòng ban</label>
                    <input type="text" class="form-control" value="${user.department}" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">Chức vụ</label>
                    <input type="text" class="form-control" value="${user.position}" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">Thông tin ngân hàng</label>
                    <input type="text" class="form-control" value="${user.bankInfo}" readonly>
                </div>

                <!-- nút Cập nhật căn giữa -->
                <div class="text-center mt-4">
                    <button type="button" class="btn btn-success px-4">
                        <i class="fa-solid fa-pen-to-square me-1"></i> Cập nhật
                    </button>
                </div>
            </form>
        </div>
    </div>

</div>
</body>
</html>
