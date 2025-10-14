# RBAC Features & URLs Specification

> **⚠️ IMPORTANT FOR AI AGENTS:**  
> This document MUST be read before implementing any authorization/permission-related code.  
> Always check the CHANGELOG section for latest updates.

---

## 📋 Document Information

- **Version:** 1.0.0
- **Last Updated:** 2025-10-14
- **Status:** DRAFT - Pending Team Review
- **Owner:** Team 4 - HRMS Project

---

## 📝 CHANGELOG

### [1.0.0] - 2025-10-14
- **CREATED**: Initial version with unified `/requests/*` module structure
- **ADDED**: 6 system roles (ADMIN, HRM, HR, MANAGER, EMPLOYEE, GUEST)
- **ADDED**: 70+ features organized by modules
- **ADDED**: Permission matrix for all roles
- **STRUCTURE**: Unified all request types under `/requests/*` path

### Instructions for Updates:
```markdown
### [Version] - YYYY-MM-DD
- **ADDED**: Description of what was added
- **MODIFIED**: Description of what was changed
- **REMOVED**: Description of what was removed
- **NOTE**: Any important notes or breaking changes
```

---

## 🎯 Overview

Hệ thống RBAC (Role-Based Access Control) cho HRMS sử dụng 6 system roles và 70+ features.
Tất cả features được map với URLs cụ thể và có permission matrix rõ ràng.

### Key Principles:
1. **Unified Request Module**: Tất cả requests (leave, OT, attendance appeal, recruitment) đều dùng `/requests/*`
2. **Hierarchical Permissions**: ADMIN > HRM > HR > MANAGER > EMPLOYEE > GUEST
3. **Resource-Level Control**: Employee chỉ xem được data của mình, Manager xem team, HRM xem tất cả
4. **Override Capability**: Có thể override permissions cho specific account hoặc department

---

## 👥 System Roles

| Code | Name | Priority | Description |
|------|------|----------|-------------|
| `ADMIN` | Administrator | 100 | Quản trị viên hệ thống, có toàn quyền |
| `HRM` | HR Manager | 90 | Quản lý nhân sự, quản lý toàn bộ HR operations |
| `HR` | HR Staff | 80 | Nhân viên phòng nhân sự, xử lý HR tasks |
| `MANAGER` | Department Manager | 70 | Quản lý phòng ban, duyệt requests của team |
| `EMPLOYEE` | Employee | 50 | Nhân viên thông thường |
| `GUEST` | Guest | 10 | Khách, chỉ xem thông tin công khai |

**Note:** Priority càng cao = quyền càng lớn. Khi user có nhiều roles, lấy role có priority cao nhất.

---

## 🔐 Features & URLs

### 1. Authentication & Profile

| Feature Code | Feature Name | URL | Description |
|--------------|--------------|-----|-------------|
| `AUTH_LOGIN` | Login | `/login` | Đăng nhập hệ thống |
| `AUTH_LOGOUT` | Logout | `/logout` | Đăng xuất |
| `PROFILE_VIEW` | View Profile | `/profile` | Xem profile cá nhân |
| `PROFILE_EDIT` | Edit Profile | `/profile/edit` | Chỉnh sửa profile |

### 2. Dashboard

| Feature Code | Feature Name | URL | Description |
|--------------|--------------|-----|-------------|
| `DASHBOARD_VIEW` | View Dashboard | `/dashboard` | Xem dashboard cá nhân |
| `DASHBOARD_ADMIN` | Admin Dashboard | `/dashboard/admin` | Dashboard admin với statistics |

### 3. User Management

| Feature Code | Feature Name | URL | Description |
|--------------|--------------|-----|-------------|
| `USER_LIST` | List Users | `/users` | Danh sách users |
| `USER_VIEW` | View User | `/users/{id}` | Xem chi tiết user |
| `USER_CREATE` | Create User | `/users/create` | Tạo user mới |
| `USER_EDIT` | Edit User | `/users/{id}/edit` | Chỉnh sửa user |
| `USER_DELETE` | Delete User | `/users/{id}/delete` | Xóa user |
| `USER_ACTIVATE` | Activate User | `/users/{id}/activate` | Kích hoạt user |
| `USER_DEACTIVATE` | Deactivate User | `/users/{id}/deactivate` | Vô hiệu hóa user |

### 4. Department Management

| Feature Code | Feature Name | URL | Description |
|--------------|--------------|-----|-------------|
| `DEPT_LIST` | List Departments | `/departments` | Danh sách phòng ban |
| `DEPT_VIEW` | View Department | `/departments/{id}` | Xem chi tiết phòng ban |
| `DEPT_CREATE` | Create Department | `/departments/create` | Tạo phòng ban |
| `DEPT_EDIT` | Edit Department | `/departments/{id}/edit` | Chỉnh sửa phòng ban |
| `DEPT_DELETE` | Delete Department | `/departments/{id}/delete` | Xóa phòng ban |

### 5. Position Management

| Feature Code | Feature Name | URL | Description |
|--------------|--------------|-----|-------------|
| `POS_LIST` | List Positions | `/positions` | Danh sách chức vụ |
| `POS_VIEW` | View Position | `/positions/{id}` | Xem chi tiết chức vụ |
| `POS_CREATE` | Create Position | `/positions/create` | Tạo chức vụ |
| `POS_EDIT` | Edit Position | `/positions/{id}/edit` | Chỉnh sửa chức vụ |
| `POS_DELETE` | Delete Position | `/positions/{id}/delete` | Xóa chức vụ |

### 6. Request Management (Unified Module)

#### 6.1 Request Dashboard & Lists

| Feature Code | Feature Name | URL | Description |
|--------------|--------------|-----|-------------|
| `REQUEST_LIST_OWN` | My Requests | `/requests` | Xem danh sách requests của mình |
| `REQUEST_LIST_TEAM` | Team Requests | `/requests/team` | Xem requests của team (Manager) |
| `REQUEST_LIST_DEPT` | Department Requests | `/requests/department` | Xem requests của phòng ban |
| `REQUEST_LIST_ALL` | All Requests | `/requests/all` | Xem tất cả requests (HRM/HR) |
| `REQUEST_PENDING` | Pending Requests | `/requests/pending` | Xem requests chờ duyệt |
| `REQUEST_HISTORY` | Request History | `/requests/history` | Xem lịch sử requests |

#### 6.2 Leave Requests

| Feature Code | Feature Name | URL | Description |
|--------------|--------------|-----|-------------|
| `REQUEST_LEAVE_CREATE` | Create Leave Request | `/requests/leave/create` | Tạo đơn nghỉ phép |
| `REQUEST_LEAVE_VIEW` | View Leave Request | `/requests/leave/{id}` | Xem chi tiết đơn nghỉ phép |
| `REQUEST_LEAVE_EDIT` | Edit Leave Request | `/requests/leave/{id}/edit` | Chỉnh sửa đơn (draft only) |
| `REQUEST_LEAVE_CANCEL` | Cancel Leave Request | `/requests/leave/{id}/cancel` | Hủy đơn nghỉ phép |
| `REQUEST_LEAVE_APPROVE` | Approve Leave Request | `/requests/leave/{id}/approve` | Duyệt đơn nghỉ phép |
| `REQUEST_LEAVE_REJECT` | Reject Leave Request | `/requests/leave/{id}/reject` | Từ chối đơn nghỉ phép |

#### 6.3 OT Requests

| Feature Code | Feature Name | URL | Description |
|--------------|--------------|-----|-------------|
| `REQUEST_OT_CREATE` | Create OT Request | `/requests/ot/create` | Tạo đơn OT |
| `REQUEST_OT_VIEW` | View OT Request | `/requests/ot/{id}` | Xem chi tiết đơn OT |
| `REQUEST_OT_EDIT` | Edit OT Request | `/requests/ot/{id}/edit` | Chỉnh sửa đơn OT (draft only) |
| `REQUEST_OT_CANCEL` | Cancel OT Request | `/requests/ot/{id}/cancel` | Hủy đơn OT |
| `REQUEST_OT_APPROVE` | Approve OT Request | `/requests/ot/{id}/approve` | Duyệt đơn OT |
| `REQUEST_OT_REJECT` | Reject OT Request | `/requests/ot/{id}/reject` | Từ chối đơn OT |

#### 6.4 Attendance Appeal Requests

| Feature Code | Feature Name | URL | Description |
|--------------|--------------|-----|-------------|
| `REQUEST_ATT_APPEAL_CREATE` | Create Attendance Appeal | `/requests/attendance-appeal/create` | Tạo khiếu nại chấm công |
| `REQUEST_ATT_APPEAL_VIEW` | View Attendance Appeal | `/requests/attendance-appeal/{id}` | Xem chi tiết khiếu nại |
| `REQUEST_ATT_APPEAL_EDIT` | Edit Attendance Appeal | `/requests/attendance-appeal/{id}/edit` | Chỉnh sửa khiếu nại (draft) |
| `REQUEST_ATT_APPEAL_CANCEL` | Cancel Attendance Appeal | `/requests/attendance-appeal/{id}/cancel` | Hủy khiếu nại |
| `REQUEST_ATT_APPEAL_APPROVE` | Approve Attendance Appeal | `/requests/attendance-appeal/{id}/approve` | Duyệt khiếu nại |
| `REQUEST_ATT_APPEAL_REJECT` | Reject Attendance Appeal | `/requests/attendance-appeal/{id}/reject` | Từ chối khiếu nại |

#### 6.5 Recruitment Requests

| Feature Code | Feature Name | URL | Description |
|--------------|--------------|-----|-------------|
| `REQUEST_RECRUITMENT_CREATE` | Create Recruitment Request | `/requests/recruitment/create` | Tạo yêu cầu tuyển dụng |
| `REQUEST_RECRUITMENT_VIEW` | View Recruitment Request | `/requests/recruitment/{id}` | Xem chi tiết yêu cầu |
| `REQUEST_RECRUITMENT_EDIT` | Edit Recruitment Request | `/requests/recruitment/{id}/edit` | Chỉnh sửa yêu cầu (draft) |
| `REQUEST_RECRUITMENT_SAVE_DRAFT` | Save Draft | `/requests/recruitment/{id}/save-draft` | Lưu draft |
| `REQUEST_RECRUITMENT_SUBMIT` | Submit Request | `/requests/recruitment/{id}/submit` | Submit để duyệt |
| `REQUEST_RECRUITMENT_APPROVE` | Approve Recruitment | `/requests/recruitment/{id}/approve` | Duyệt yêu cầu (level 1) |
| `REQUEST_RECRUITMENT_FINAL_APPROVE` | Final Approve | `/requests/recruitment/{id}/final-approve` | Duyệt cuối (level 2) |
| `REQUEST_RECRUITMENT_REJECT` | Reject Recruitment | `/requests/recruitment/{id}/reject` | Từ chối yêu cầu |

### 7. Attendance Management

| Feature Code | Feature Name | URL | Description |
|--------------|--------------|-----|-------------|
| `ATT_VIEW_OWN` | View My Attendance | `/attendance` | Xem chấm công của mình |
| `ATT_VIEW_TEAM` | View Team Attendance | `/attendance/team` | Xem chấm công team |
| `ATT_VIEW_ALL` | View All Attendance | `/attendance/all` | Xem tất cả chấm công |
| `ATT_IMPORT` | Import Attendance | `/attendance/import` | Import dữ liệu chấm công |
| `ATT_EXPORT` | Export Attendance | `/attendance/export` | Export dữ liệu chấm công |

### 8. Application Management (Recruitment)

| Feature Code | Feature Name | URL | Description |
|--------------|--------------|-----|-------------|
| `APP_LIST` | List Applications | `/applications` | Danh sách ứng viên |
| `APP_VIEW` | View Application | `/applications/{id}` | Xem hồ sơ ứng viên |
| `APP_REVIEW` | Review Application | `/applications/{id}/review` | Đánh giá ứng viên |

### 9. Contract Management

| Feature Code | Feature Name | URL | Description |
|--------------|--------------|-----|-------------|
| `CONTRACT_LIST` | List Contracts | `/contracts` | Danh sách hợp đồng |
| `CONTRACT_VIEW` | View Contract | `/contracts/{id}` | Xem hợp đồng |
| `CONTRACT_CREATE` | Create Contract | `/contracts/create` | Tạo hợp đồng |
| `CONTRACT_EDIT` | Edit Contract | `/contracts/{id}/edit` | Chỉnh sửa hợp đồng |

### 10. Payslip Management

| Feature Code | Feature Name | URL | Description |
|--------------|--------------|-----|-------------|
| `PAYSLIP_VIEW_OWN` | View My Payslips | `/payslips` | Xem phiếu lương của mình |
| `PAYSLIP_VIEW_ALL` | View All Payslips | `/payslips/all` | Xem tất cả phiếu lương |
| `PAYSLIP_CREATE` | Create Payslip | `/payslips/create` | Tạo phiếu lương |
| `PAYSLIP_EXPORT` | Export Payslips | `/payslips/export` | Export phiếu lương |

### 11. System Settings

| Feature Code | Feature Name | URL | Description |
|--------------|--------------|-----|-------------|
| `SETTINGS_VIEW` | View Settings | `/settings` | Xem cài đặt hệ thống |
| `SETTINGS_EDIT` | Edit Settings | `/settings/edit` | Chỉnh sửa cài đặt |
| `ROLE_MANAGE` | Manage Roles | `/settings/roles` | Quản lý roles |
| `PERMISSION_MANAGE` | Manage Permissions | `/settings/permissions` | Quản lý permissions |

### 12. Public Pages (No Authentication Required)

| Feature Code | Feature Name | URL | Description |
|--------------|--------------|-----|-------------|
| `PUBLIC_ABOUT` | About Page | `/about` | Trang giới thiệu |
| `PUBLIC_CONTACT` | Contact Page | `/contact` | Trang liên hệ |
| `PUBLIC_FAQS` | FAQs Page | `/faqs` | Trang câu hỏi thường gặp |

---

## 🔑 Permission Matrix

### ADMIN Role (Priority: 100)
- **ALL FEATURES**: Có toàn quyền truy cập tất cả features
- **No Restrictions**: Không bị giới hạn bởi bất kỳ rule nào

### HRM Role (Priority: 90)

| Module | Permissions |
|--------|-------------|
| **User Management** | ALL (list, view, create, edit, delete, activate, deactivate) |
| **Department Management** | ALL |
| **Position Management** | ALL |
| **Requests** | ALL (list all, view all types, approve/reject all types) |
| **Attendance** | ALL (view all, import, export) |
| **Applications** | ALL |
| **Contracts** | ALL |
| **Payslips** | ALL (view all, create, export) |
| **Settings** | VIEW only (không có EDIT, ROLE_MANAGE, PERMISSION_MANAGE) |

**Features:**
```
✅ USER_LIST, USER_VIEW, USER_CREATE, USER_EDIT, USER_DELETE, USER_ACTIVATE, USER_DEACTIVATE
✅ DEPT_LIST, DEPT_VIEW, DEPT_CREATE, DEPT_EDIT, DEPT_DELETE
✅ POS_LIST, POS_VIEW, POS_CREATE, POS_EDIT, POS_DELETE
✅ REQUEST_LIST_ALL, REQUEST_PENDING, REQUEST_HISTORY
✅ REQUEST_LEAVE_*, REQUEST_OT_*, REQUEST_ATT_APPEAL_*, REQUEST_RECRUITMENT_*
✅ ATT_VIEW_ALL, ATT_IMPORT, ATT_EXPORT
✅ APP_LIST, APP_VIEW, APP_REVIEW
✅ CONTRACT_LIST, CONTRACT_VIEW, CONTRACT_CREATE, CONTRACT_EDIT
✅ PAYSLIP_VIEW_ALL, PAYSLIP_CREATE, PAYSLIP_EXPORT
✅ SETTINGS_VIEW
❌ SETTINGS_EDIT, ROLE_MANAGE, PERMISSION_MANAGE
```

### HR Role (Priority: 80)

| Module | Permissions |
|--------|-------------|
| **User Management** | LIST, VIEW, CREATE, EDIT (không có DELETE) |
| **Department Management** | LIST, VIEW |
| **Position Management** | LIST, VIEW |
| **Requests** | LIST_ALL, VIEW, APPROVE, REJECT (tất cả loại) |
| **Attendance** | VIEW_ALL, IMPORT, EXPORT |
| **Applications** | LIST, VIEW, REVIEW |
| **Contracts** | LIST, VIEW, CREATE |
| **Payslips** | VIEW_ALL, EXPORT |

**Features:**
```
✅ USER_LIST, USER_VIEW, USER_CREATE, USER_EDIT
❌ USER_DELETE, USER_ACTIVATE, USER_DEACTIVATE
✅ DEPT_LIST, DEPT_VIEW
❌ DEPT_CREATE, DEPT_EDIT, DEPT_DELETE
✅ POS_LIST, POS_VIEW
❌ POS_CREATE, POS_EDIT, POS_DELETE
✅ REQUEST_LIST_ALL, REQUEST_PENDING, REQUEST_HISTORY
✅ REQUEST_*_VIEW, REQUEST_*_APPROVE, REQUEST_*_REJECT
❌ REQUEST_*_CREATE (chỉ approve/reject, không tạo)
✅ ATT_VIEW_ALL, ATT_IMPORT, ATT_EXPORT
✅ APP_LIST, APP_VIEW, APP_REVIEW
✅ CONTRACT_LIST, CONTRACT_VIEW, CONTRACT_CREATE
❌ CONTRACT_EDIT
✅ PAYSLIP_VIEW_ALL, PAYSLIP_EXPORT
❌ PAYSLIP_CREATE
```

### MANAGER Role (Priority: 70)

| Module | Permissions |
|--------|-------------|
| **User Management** | LIST, VIEW (chỉ trong department) |
| **Department Management** | VIEW (chỉ department của mình) |
| **Requests** | LIST_TEAM, LIST_DEPT, VIEW, APPROVE, REJECT (chỉ team/department) |
| **Attendance** | VIEW_TEAM, EXPORT (chỉ team) |
| **Profile** | VIEW, EDIT (của mình) |

**Features:**
```
✅ PROFILE_VIEW, PROFILE_EDIT
✅ DASHBOARD_VIEW
✅ USER_LIST, USER_VIEW (scope: department only)
✅ DEPT_VIEW (scope: own department only)
✅ REQUEST_LIST_OWN, REQUEST_LIST_TEAM, REQUEST_LIST_DEPT
✅ REQUEST_LEAVE_VIEW, REQUEST_LEAVE_APPROVE, REQUEST_LEAVE_REJECT (scope: team)
✅ REQUEST_OT_VIEW, REQUEST_OT_APPROVE, REQUEST_OT_REJECT (scope: team)
✅ REQUEST_ATT_APPEAL_VIEW, REQUEST_ATT_APPEAL_APPROVE, REQUEST_ATT_APPEAL_REJECT (scope: team)
✅ REQUEST_RECRUITMENT_CREATE, REQUEST_RECRUITMENT_APPROVE (level 1, for department)
✅ ATT_VIEW_OWN, ATT_VIEW_TEAM, ATT_EXPORT (scope: team)
❌ All other features
```

**Resource-Level Rules:**
- Chỉ xem/duyệt requests của nhân viên trong team/department
- Không xem được requests của departments khác
- Có thể tạo recruitment request cho department của mình

### EMPLOYEE Role (Priority: 50)

| Module | Permissions |
|--------|-------------|
| **Profile** | VIEW, EDIT (của mình) |
| **Dashboard** | VIEW |
| **Requests** | LIST_OWN, CREATE, VIEW, EDIT, CANCEL (chỉ của mình) |
| **Attendance** | VIEW_OWN (chỉ của mình) |
| **Payslips** | VIEW_OWN (chỉ của mình) |

**Features:**
```
✅ PROFILE_VIEW, PROFILE_EDIT
✅ DASHBOARD_VIEW
✅ REQUEST_LIST_OWN
✅ REQUEST_LEAVE_CREATE, REQUEST_LEAVE_VIEW, REQUEST_LEAVE_EDIT, REQUEST_LEAVE_CANCEL
✅ REQUEST_OT_CREATE, REQUEST_OT_VIEW, REQUEST_OT_EDIT, REQUEST_OT_CANCEL
✅ REQUEST_ATT_APPEAL_CREATE, REQUEST_ATT_APPEAL_VIEW, REQUEST_ATT_APPEAL_EDIT, REQUEST_ATT_APPEAL_CANCEL
✅ ATT_VIEW_OWN
✅ PAYSLIP_VIEW_OWN
❌ All other features
```

**Resource-Level Rules:**
- Chỉ xem/chỉnh sửa requests của chính mình
- Không xem được requests của người khác
- Chỉ có thể EDIT khi request ở trạng thái DRAFT
- Chỉ có thể CANCEL khi request ở trạng thái PENDING

### GUEST Role (Priority: 10)

| Module | Permissions |
|--------|-------------|
| **Public Pages** | VIEW only |

**Features:**
```
✅ PUBLIC_ABOUT, PUBLIC_CONTACT, PUBLIC_FAQS
❌ All other features
```

---

## 🗄️ Database Tables

### Tables Overview

```
roles ──────┐
            ├──▶ role_features ◀──── features
            │
            └──▶ position_roles ◀─── positions
            
accounts ───┐
            ├──▶ account_features ◀─ features
            │
departments ┴──▶ department_features ◀─ features
```

### 1. `roles` Table (Existing)
```sql
CREATE TABLE `roles` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `code` varchar(64) UNIQUE NOT NULL,
  `name` varchar(128) NOT NULL,
  `priority` int NOT NULL DEFAULT 0,
  `is_system` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL
);
```

### 2. `features` Table (Existing)
```sql
CREATE TABLE `features` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `code` varchar(64) UNIQUE NOT NULL,
  `name` varchar(128) NOT NULL,
  `description` varchar(255),
  `route` varchar(255) NOT NULL,
  `sort_order` int NOT NULL DEFAULT 0,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL
);
```

### 3. `role_features` Table (Existing)
```sql
CREATE TABLE `role_features` (
  `role_id` bigint NOT NULL,
  `feature_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`, `feature_id`),
  FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`),
  FOREIGN KEY (`feature_id`) REFERENCES `features`(`id`)
);
```
**Note:** Không có cột `effect`. Có record = GRANT, không có = DENY.

### 4. `position_roles` Table (Existing)
```sql
CREATE TABLE `position_roles` (
  `position_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`position_id`, `role_id`),
  FOREIGN KEY (`position_id`) REFERENCES `positions`(`id`),
  FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`)
);
```

### 5. `account_features` Table (Existing)
```sql
CREATE TABLE `account_features` (
  `account_id` bigint NOT NULL,
  `feature_id` bigint NOT NULL,
  `effect` enum('GRANT','DENY') NOT NULL,
  PRIMARY KEY (`account_id`, `feature_id`),
  FOREIGN KEY (`account_id`) REFERENCES `accounts`(`id`),
  FOREIGN KEY (`feature_id`) REFERENCES `features`(`id`)
);
```
**Note:** Có cột `effect` để override permissions cho specific account.

### 6. `department_features` Table (Existing)
```sql
CREATE TABLE `department_features` (
  `department_id` bigint NOT NULL,
  `feature_id` bigint NOT NULL,
  `effect` enum('GRANT','DENY') NOT NULL,
  PRIMARY KEY (`department_id`, `feature_id`),
  FOREIGN KEY (`department_id`) REFERENCES `departments`(`id`),
  FOREIGN KEY (`feature_id`) REFERENCES `features`(`id`)
);
```
**Note:** Có cột `effect` để override permissions cho cả department.

---

## ⚙️ Permission Resolution Logic

### Priority Order (Highest to Lowest)

```
1. account_features (DENY)     → Nếu có DENY, return false ngay
2. account_features (GRANT)    → Nếu có GRANT, return true
3. department_features (DENY)  → Nếu có DENY, return false
4. department_features (GRANT) → Nếu có GRANT, return true
5. role_features               → Check role của user
6. Default DENY                → Nếu không có gì, return false
```

### Pseudo Code

```java
boolean hasPermission(Account account, Feature feature) {
    // 1. Check account_features DENY
    if (accountFeaturesDao.hasDeny(account.getId(), feature.getId())) {
        return false;  // DENY luôn override tất cả
    }
    
    // 2. Check account_features GRANT
    if (accountFeaturesDao.hasGrant(account.getId(), feature.getId())) {
        return true;  // GRANT override role permissions
    }
    
    // 3. Check department_features DENY
    if (account.getDepartmentId() != null) {
        if (departmentFeaturesDao.hasDeny(account.getDepartmentId(), feature.getId())) {
            return false;
        }
    }
    
    // 4. Check department_features GRANT
    if (account.getDepartmentId() != null) {
        if (departmentFeaturesDao.hasGrant(account.getDepartmentId(), feature.getId())) {
            return true;
        }
    }
    
    // 5. Check role_features
    List<Role> roles = getRolesForAccount(account);
    Role highestRole = roles.stream()
        .max(Comparator.comparing(Role::getPriority))
        .orElse(null);
    
    if (highestRole != null) {
        if (roleFeaturesDao.hasPermission(highestRole.getId(), feature.getId())) {
            return true;
        }
    }
    
    // 6. Default DENY
    return false;
}
```

### Resource-Level Permission Check

Ngoài feature-level permission, cần check resource-level permission:

```java
boolean canAccessResource(Account account, Resource resource) {
    // 1. Check feature permission first
    if (!hasPermission(account, feature)) {
        return false;
    }
    
    // 2. Check resource ownership
    Role role = getHighestRole(account);
    
    switch (role.getCode()) {
        case "ADMIN":
        case "HRM":
            return true;  // Full access
            
        case "HR":
            return true;  // Full access for most resources
            
        case "MANAGER":
            // Check if resource belongs to manager's department/team
            return resource.getDepartmentId().equals(account.getDepartmentId());
            
        case "EMPLOYEE":
            // Check if resource belongs to employee
            return resource.getOwnerId().equals(account.getUserId());
            
        default:
            return false;
    }
}
```

---

## 🚀 Implementation Guidelines

### For AI Agents

**MUST READ THIS DOCUMENT** before implementing:
- Authorization filters
- Permission checks
- Request handlers
- UI components that depend on permissions

**Key Points:**
1. Always check both feature-level AND resource-level permissions
2. Use the permission resolution logic in correct order
3. Cache permissions in session for performance
4. Log all permission denials for audit

### URL Whitelist (No Authentication Required)

```java
private static final Set<String> PUBLIC_URLS = Set.of(
    "/login",
    "/logout", 
    "/google-login",
    "/google-oauth",
    "/about",
    "/contact",
    "/faqs"
);

private static final Set<String> STATIC_RESOURCES = Set.of(
    "/static/",
    "/css/",
    "/js/",
    "/images/",
    "/favicon.ico"
);
```

### Authorization Filter Pattern

```java
@WebFilter("/*")
public class AuthorizationFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        
        String path = req.getRequestURI();
        
        // 1. Check if public URL
        if (isPublicUrl(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 2. Check authentication
        Account account = (Account) req.getSession().getAttribute("account");
        if (account == null) {
            resp.sendRedirect("/login");
            return;
        }
        
        // 3. Check authorization
        Feature feature = featureService.getByRoute(path);
        if (feature == null || !permissionService.hasPermission(account, feature)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        // 4. Continue
        chain.doFilter(request, response);
    }
}
```

### Service Layer Pattern

```java
public class RequestService {
    
    public List<Request> getRequests(Account account, String scope) {
        Role role = roleService.getHighestRole(account);
        
        switch (scope) {
            case "own":
                return requestDao.findByUserId(account.getUserId());
                
            case "team":
                if (!role.getCode().equals("MANAGER")) {
                    throw new ForbiddenException("Only managers can view team requests");
                }
                return requestDao.findByDepartmentId(account.getDepartmentId());
                
            case "all":
                if (!role.getCode().matches("ADMIN|HRM|HR")) {
                    throw new ForbiddenException("Insufficient permissions");
                }
                return requestDao.findAll();
                
            default:
                throw new IllegalArgumentException("Invalid scope");
        }
    }
}
```

---

## 📊 Seed Data Scripts

### 1. Seed Roles

```sql
-- Insert system roles
INSERT INTO roles (code, name, priority, is_system, created_at, updated_at) VALUES
('ADMIN', 'Administrator', 100, 1, NOW(), NOW()),
('HRM', 'HR Manager', 90, 1, NOW(), NOW()),
('HR', 'HR Staff', 80, 1, NOW(), NOW()),
('MANAGER', 'Department Manager', 70, 1, NOW(), NOW()),
('EMPLOYEE', 'Employee', 50, 1, NOW(), NOW()),
('GUEST', 'Guest', 10, 1, NOW(), NOW());
```

### 2. Seed Features (Sample - Full list in separate SQL file)

```sql
-- Authentication & Profile
INSERT INTO features (code, name, description, route, sort_order, is_active) VALUES
('AUTH_LOGIN', 'Login', 'Đăng nhập hệ thống', '/login', 1, 1),
('AUTH_LOGOUT', 'Logout', 'Đăng xuất', '/logout', 2, 1),
('PROFILE_VIEW', 'View Profile', 'Xem profile cá nhân', '/profile', 3, 1),
('PROFILE_EDIT', 'Edit Profile', 'Chỉnh sửa profile', '/profile/edit', 4, 1);

-- Dashboard
INSERT INTO features (code, name, description, route, sort_order, is_active) VALUES
('DASHBOARD_VIEW', 'View Dashboard', 'Xem dashboard', '/dashboard', 10, 1),
('DASHBOARD_ADMIN', 'Admin Dashboard', 'Dashboard admin', '/dashboard/admin', 11, 1);

-- User Management
INSERT INTO features (code, name, description, route, sort_order, is_active) VALUES
('USER_LIST', 'List Users', 'Danh sách users', '/users', 20, 1),
('USER_VIEW', 'View User', 'Xem chi tiết user', '/users/{id}', 21, 1),
('USER_CREATE', 'Create User', 'Tạo user mới', '/users/create', 22, 1),
('USER_EDIT', 'Edit User', 'Chỉnh sửa user', '/users/{id}/edit', 23, 1),
('USER_DELETE', 'Delete User', 'Xóa user', '/users/{id}/delete', 24, 1);

-- Request Management
INSERT INTO features (code, name, description, route, sort_order, is_active) VALUES
('REQUEST_LIST_OWN', 'My Requests', 'Xem requests của mình', '/requests', 30, 1),
('REQUEST_LIST_TEAM', 'Team Requests', 'Xem requests của team', '/requests/team', 31, 1),
('REQUEST_LIST_ALL', 'All Requests', 'Xem tất cả requests', '/requests/all', 32, 1),
('REQUEST_LEAVE_CREATE', 'Create Leave', 'Tạo đơn nghỉ phép', '/requests/leave/create', 33, 1),
('REQUEST_LEAVE_APPROVE', 'Approve Leave', 'Duyệt đơn nghỉ phép', '/requests/leave/{id}/approve', 34, 1);

-- ... (Continue for all 70+ features)
```

### 3. Seed Role-Features Mapping

```sql
-- ADMIN: Grant all features
INSERT INTO role_features (role_id, feature_id)
SELECT 
    (SELECT id FROM roles WHERE code = 'ADMIN'),
    id
FROM features
WHERE is_active = 1;

-- EMPLOYEE: Grant basic features only
INSERT INTO role_features (role_id, feature_id)
SELECT 
    (SELECT id FROM roles WHERE code = 'EMPLOYEE'),
    id
FROM features
WHERE code IN (
    'PROFILE_VIEW', 'PROFILE_EDIT', 'DASHBOARD_VIEW',
    'REQUEST_LIST_OWN',
    'REQUEST_LEAVE_CREATE', 'REQUEST_LEAVE_VIEW', 'REQUEST_LEAVE_EDIT', 'REQUEST_LEAVE_CANCEL',
    'REQUEST_OT_CREATE', 'REQUEST_OT_VIEW', 'REQUEST_OT_EDIT', 'REQUEST_OT_CANCEL',
    'REQUEST_ATT_APPEAL_CREATE', 'REQUEST_ATT_APPEAL_VIEW', 'REQUEST_ATT_APPEAL_EDIT', 'REQUEST_ATT_APPEAL_CANCEL',
    'ATT_VIEW_OWN',
    'PAYSLIP_VIEW_OWN'
);

-- MANAGER: Grant team management features
INSERT INTO role_features (role_id, feature_id)
SELECT 
    (SELECT id FROM roles WHERE code = 'MANAGER'),
    id
FROM features
WHERE code IN (
    'PROFILE_VIEW', 'PROFILE_EDIT', 'DASHBOARD_VIEW',
    'USER_LIST', 'USER_VIEW',
    'DEPT_VIEW',
    'REQUEST_LIST_OWN', 'REQUEST_LIST_TEAM', 'REQUEST_LIST_DEPT',
    'REQUEST_LEAVE_VIEW', 'REQUEST_LEAVE_APPROVE', 'REQUEST_LEAVE_REJECT',
    'REQUEST_OT_VIEW', 'REQUEST_OT_APPROVE', 'REQUEST_OT_REJECT',
    'REQUEST_ATT_APPEAL_VIEW', 'REQUEST_ATT_APPEAL_APPROVE', 'REQUEST_ATT_APPEAL_REJECT',
    'REQUEST_RECRUITMENT_CREATE', 'REQUEST_RECRUITMENT_APPROVE',
    'ATT_VIEW_OWN', 'ATT_VIEW_TEAM', 'ATT_EXPORT'
);

-- HR: Grant HR operations features
INSERT INTO role_features (role_id, feature_id)
SELECT 
    (SELECT id FROM roles WHERE code = 'HR'),
    id
FROM features
WHERE code NOT IN (
    'USER_DELETE', 'USER_ACTIVATE', 'USER_DEACTIVATE',
    'DEPT_CREATE', 'DEPT_EDIT', 'DEPT_DELETE',
    'POS_CREATE', 'POS_EDIT', 'POS_DELETE',
    'CONTRACT_EDIT',
    'PAYSLIP_CREATE',
    'SETTINGS_EDIT', 'ROLE_MANAGE', 'PERMISSION_MANAGE'
);

-- HRM: Grant almost all features except system settings
INSERT INTO role_features (role_id, feature_id)
SELECT 
    (SELECT id FROM roles WHERE code = 'HRM'),
    id
FROM features
WHERE code NOT IN ('SETTINGS_EDIT', 'ROLE_MANAGE', 'PERMISSION_MANAGE');
```

### 4. Seed Position-Roles Mapping (Example)

```sql
-- Assuming positions exist:
-- 1: CEO
-- 2: HR Manager  
-- 3: Department Manager
-- 4: Staff
-- 5: Intern

INSERT INTO position_roles (position_id, role_id) VALUES
(1, (SELECT id FROM roles WHERE code = 'ADMIN')),
(2, (SELECT id FROM roles WHERE code = 'HRM')),
(3, (SELECT id FROM roles WHERE code = 'MANAGER')),
(4, (SELECT id FROM roles WHERE code = 'EMPLOYEE')),
(5, (SELECT id FROM roles WHERE code = 'EMPLOYEE'));
```

---

## 🧪 Testing Scenarios

### Test Case 1: EMPLOYEE Access
```
User: employee@company.com (Role: EMPLOYEE)

✅ SHOULD PASS:
- GET /profile → View own profile
- GET /requests → View own requests
- POST /requests/leave/create → Create leave request
- GET /requests/leave/123 → View own leave request (if owner)
- GET /attendance → View own attendance

❌ SHOULD FAIL (403 Forbidden):
- GET /requests/all → Cannot view all requests
- GET /requests/team → Cannot view team requests
- POST /requests/leave/123/approve → Cannot approve requests
- GET /users → Cannot list users
- GET /attendance/all → Cannot view all attendance
```

### Test Case 2: MANAGER Access
```
User: manager@company.com (Role: MANAGER, Department: IT)

✅ SHOULD PASS:
- GET /requests/team → View team requests
- GET /requests/department → View department requests
- POST /requests/leave/123/approve → Approve team member's leave (if in same dept)
- GET /users → List users in department
- GET /attendance/team → View team attendance

❌ SHOULD FAIL (403 Forbidden):
- GET /requests/all → Cannot view all requests (only HRM/HR)
- POST /requests/leave/456/approve → Cannot approve other department's request
- POST /users/create → Cannot create users
- DELETE /users/123 → Cannot delete users
```

### Test Case 3: HR Access
```
User: hr@company.com (Role: HR)

✅ SHOULD PASS:
- GET /requests/all → View all requests
- POST /requests/leave/123/approve → Approve any leave request
- GET /users → List all users
- POST /users/create → Create new user
- GET /attendance/all → View all attendance
- POST /attendance/import → Import attendance data

❌ SHOULD FAIL (403 Forbidden):
- DELETE /users/123 → Cannot delete users (only HRM)
- POST /departments/create → Cannot create departments
- POST /payslips/create → Cannot create payslips
```

### Test Case 4: HRM Access
```
User: hrm@company.com (Role: HRM)

✅ SHOULD PASS:
- All HR permissions
- DELETE /users/123 → Delete users
- POST /departments/create → Create departments
- POST /payslips/create → Create payslips
- GET /settings → View settings

❌ SHOULD FAIL (403 Forbidden):
- POST /settings/edit → Cannot edit system settings
- GET /settings/roles → Cannot manage roles
- GET /settings/permissions → Cannot manage permissions
```

### Test Case 5: ADMIN Access
```
User: admin@company.com (Role: ADMIN)

✅ SHOULD PASS:
- ALL features including:
- POST /settings/edit → Edit system settings
- GET /settings/roles → Manage roles
- GET /settings/permissions → Manage permissions
```

### Test Case 6: Resource-Level Permission
```
Scenario: Employee tries to view another employee's leave request

User: employee1@company.com (Role: EMPLOYEE)
Request: GET /requests/leave/123

Check:
1. Feature permission: REQUEST_LEAVE_VIEW → ✅ PASS (Employee has this feature)
2. Resource ownership: request.created_by_user_id == employee1.user_id
   - If YES → ✅ PASS (Can view own request)
   - If NO → ❌ FAIL (Cannot view other's request)
```

---

