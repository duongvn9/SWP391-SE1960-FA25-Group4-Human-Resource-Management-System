# Frontend RBAC Implementation Guide

> **⚠️ IMPORTANT:** Frontend RBAC chỉ để cải thiện UX, KHÔNG phải security layer!  
> Backend RBAC là security layer chính và BẮT BUỘC phải có.

---

## 📋 Table of Contents

1. [Frontend vs Backend RBAC](#frontend-vs-backend-rbac)
2. [Tại sao cần Frontend RBAC](#tại-sao-cần-frontend-rbac)
3. [Implementation Approaches](#implementation-approaches)
4. [JSP/JSTL Implementation](#jspjstl-implementation)
5. [JavaScript Implementation](#javascript-implementation)
6. [Menu & Navigation](#menu--navigation)
7. [Button & Action Controls](#button--action-controls)
8. [Best Practices](#best-practices)

---

## 🔄 Frontend vs Backend RBAC

### Backend RBAC (Security Layer) 🔒

**Mục đích:** BẢO MẬT - Ngăn chặn unauthorized access

**Đặc điểm:**
- ✅ **BẮT BUỘC** - Không thể bỏ qua
- ✅ **Trusted** - Chạy trên server, user không thể bypass
- ✅ **Authoritative** - Quyết định cuối cùng
- ✅ **Enforced** - Return 403 Forbidden nếu không có quyền

**Implementation:**
```java
// Authorization Filter
if (!permissionService.hasPermission(account, feature)) {
    response.sendError(403);
    return;
}
```

**Kết quả khi không có quyền:**
- HTTP 403 Forbidden
- Error page
- Audit log

---

### Frontend RBAC (UX Layer) 🎨

**Mục đích:** TRẢI NGHIỆM NGƯỜI DÙNG - Ẩn/hiện UI elements

**Đặc điểm:**
- ⚠️ **OPTIONAL** - Nhưng nên có để UX tốt hơn
- ⚠️ **Untrusted** - User có thể bypass (inspect element, disable JS)
- ⚠️ **Suggestive** - Chỉ gợi ý, không enforce
- ⚠️ **UX Enhancement** - Giúp user không thấy options họ không có quyền

**Implementation:**
```jsp
<!-- Ẩn button nếu không có quyền -->
<c:if test="${hasPermission['USER_DELETE']}">
    <button>Delete User</button>
</c:if>
```

**Kết quả khi không có quyền:**
- Button/menu item bị ẩn
- User không thấy option
- Tránh confusion và frustration

---

## 🎯 Tại sao cần Frontend RBAC?

### ❌ Không có Frontend RBAC:

```
User (EMPLOYEE) đăng nhập
→ Thấy menu "User Management" 
→ Click vào
→ Thấy button "Delete User"
→ Click Delete
→ Backend return 403 Forbidden
→ User confused: "Tại sao có button mà không cho dùng?"
```

**Vấn đề:**
- User frustration
- Nhiều failed requests
- Bad UX
- Tăng support tickets

### ✅ Có Frontend RBAC:

```
User (EMPLOYEE) đăng nhập
→ KHÔNG thấy menu "User Management"
→ Chỉ thấy menu "My Profile", "My Requests"
→ Smooth experience
→ Không có confusion
```

**Lợi ích:**
- Better UX
- Ít failed requests
- User biết rõ họ có quyền gì
- Giảm support tickets

---

## 🛠️ Implementation Approaches

### Approach 1: Server-Side Rendering (JSP/JSTL) ⭐ RECOMMENDED

**Ưu điểm:**
- ✅ Permissions được check trên server
- ✅ HTML đã được filter trước khi gửi về client
- ✅ Không cần JavaScript
- ✅ SEO friendly
- ✅ Works without JavaScript

**Nhược điểm:**
- ❌ Cần reload page để update permissions
- ❌ Ít dynamic hơn

**Use case:** Traditional JSP applications (như HRMS của bạn)

---

### Approach 2: Client-Side Rendering (JavaScript)

**Ưu điểm:**
- ✅ Dynamic - Có thể update UI without reload
- ✅ Rich interactions
- ✅ Modern UX

**Nhược điểm:**
- ❌ Permissions data phải gửi về client (security concern)
- ❌ User có thể inspect và thấy permissions
- ❌ Phụ thuộc JavaScript

**Use case:** SPA (React, Vue, Angular)

---

### Approach 3: Hybrid (JSP + JavaScript)

**Ưu điểm:**
- ✅ Kết hợp ưu điểm của cả 2
- ✅ Server-side cho initial render
- ✅ Client-side cho dynamic updates

**Use case:** Modern JSP applications với AJAX

---

## 📝 JSP/JSTL Implementation

### Step 1: Expose Permissions to JSP

**Option A: Session Attribute (Recommended)**

```java
// In LoginServlet or AuthorizationFilter
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpSession session = req.getSession();
    
    Account account = (Account) session.getAttribute("account");
    if (account != null) {
        // Load permissions once and cache in session
        Set<String> permissions = permissionService.getPermissions(account);
        session.setAttribute("userPermissions", permissions);
        
        // Also set role for convenience
        Role role = roleService.getHighestRole(account);
        session.setAttribute("userRole", role.getCode());
    }
    
    chain.doFilter(request, response);
}
```

**Option B: Request Attribute (Per-request)**

```java
// In each Servlet
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    Account account = (Account) request.getSession().getAttribute("account");
    Set<String> permissions = permissionService.getPermissions(account);
    request.setAttribute("userPermissions", permissions);
    
    // Forward to JSP
    request.getRequestDispatcher("/WEB-INF/views/users/list.jsp").forward(request, response);
}
```

---

### Step 2: Create Custom JSTL Functions

**File:** `src/main/java/group4/hrms/util/PermissionUtil.java`

```java
package group4.hrms.util;

import java.util.Set;

public class PermissionUtil {
    
    /**
     * Check if user has permission
     * Usage in JSP: ${hrms:hasPermission(userPermissions, 'USER_DELETE')}
     */
    public static boolean hasPermission(Set<String> permissions, String featureCode) {
        return permissions != null && permissions.contains(featureCode);
    }
    
    /**
     * Check if user has any of the permissions
     * Usage: ${hrms:hasAnyPermission(userPermissions, 'USER_EDIT,USER_DELETE')}
     */
    public static boolean hasAnyPermission(Set<String> permissions, String featureCodes) {
        if (permissions == null || featureCodes == null) {
            return false;
        }
        
        String[] codes = featureCodes.split(",");
        for (String code : codes) {
            if (permissions.contains(code.trim())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if user has all permissions
     */
    public static boolean hasAllPermissions(Set<String> permissions, String featureCodes) {
        if (permissions == null || featureCodes == null) {
            return false;
        }
        
        String[] codes = featureCodes.split(",");
        for (String code : codes) {
            if (!permissions.contains(code.trim())) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check if user has role
     * Usage: ${hrms:hasRole(userRole, 'ADMIN')}
     */
    public static boolean hasRole(String userRole, String role) {
        return userRole != null && userRole.equals(role);
    }
    
    /**
     * Check if user has any of the roles
     * Usage: ${hrms:hasAnyRole(userRole, 'ADMIN,HRM')}
     */
    public static boolean hasAnyRole(String userRole, String roles) {
        if (userRole == null || roles == null) {
            return false;
        }
        
        String[] roleArray = roles.split(",");
        for (String role : roleArray) {
            if (userRole.equals(role.trim())) {
                return true;
            }
        }
        return false;
    }
}
```

**File:** `src/main/webapp/WEB-INF/hrms.tld`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<taglib xmlns="http://java.sun.com/xml/ns/javaee"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
        http://java.sun.com/xml/ns/javaee/web-jsptaglibrary_2_1.xsd"
        version="2.1">
    
    <tlib-version>1.0</tlib-version>
    <short-name>hrms</short-name>
    <uri>http://hrms.group4.com/functions</uri>
    
    <function>
        <name>hasPermission</name>
        <function-class>group4.hrms.util.PermissionUtil</function-class>
        <function-signature>
            boolean hasPermission(java.util.Set, java.lang.String)
        </function-signature>
    </function>
    
    <function>
        <name>hasAnyPermission</name>
        <function-class>group4.hrms.util.PermissionUtil</function-class>
        <function-signature>
            boolean hasAnyPermission(java.util.Set, java.lang.String)
        </function-signature>
    </function>
    
    <function>
        <name>hasAllPermissions</name>
        <function-class>group4.hrms.util.PermissionUtil</function-class>
        <function-signature>
            boolean hasAllPermissions(java.util.Set, java.lang.String)
        </function-signature>
    </function>
    
    <function>
        <name>hasRole</name>
        <function-class>group4.hrms.util.PermissionUtil</function-class>
        <function-signature>
            boolean hasRole(java.lang.String, java.lang.String)
        </function-signature>
    </function>
    
    <function>
        <name>hasAnyRole</name>
        <function-class>group4.hrms.util.PermissionUtil</function-class>
        <function-signature>
            boolean hasAnyRole(java.lang.String, java.lang.String)
        </function-signature>
    </function>
</taglib>
```

---

### Step 3: Use in JSP

**Import taglib:**

```jsp
<%@ taglib prefix="hrms" uri="http://hrms.group4.com/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
```

**Example 1: Hide/Show Buttons**

```jsp
<!-- User List Page -->
<table class="table">
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach items="${users}" var="user">
            <tr>
                <td>${user.id}</td>
                <td>${user.fullName}</td>
                <td>${user.email}</td>
                <td>
                    <!-- View button - Everyone with USER_VIEW can see -->
                    <c:if test="${hrms:hasPermission(userPermissions, 'USER_VIEW')}">
                        <a href="/users/${user.id}" class="btn btn-sm btn-info">
                            <i class="fas fa-eye"></i> View
                        </a>
                    </c:if>
                    
                    <!-- Edit button - Only if has USER_EDIT -->
                    <c:if test="${hrms:hasPermission(userPermissions, 'USER_EDIT')}">
                        <a href="/users/${user.id}/edit" class="btn btn-sm btn-warning">
                            <i class="fas fa-edit"></i> Edit
                        </a>
                    </c:if>
                    
                    <!-- Delete button - Only ADMIN and HRM -->
                    <c:if test="${hrms:hasPermission(userPermissions, 'USER_DELETE')}">
                        <button onclick="deleteUser(${user.id})" class="btn btn-sm btn-danger">
                            <i class="fas fa-trash"></i> Delete
                        </button>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<!-- Create button - Only if has USER_CREATE -->
<c:if test="${hrms:hasPermission(userPermissions, 'USER_CREATE')}">
    <a href="/users/create" class="btn btn-primary">
        <i class="fas fa-plus"></i> Create New User
    </a>
</c:if>
```

**Example 2: Conditional Sections**

```jsp
<!-- Request Detail Page -->
<div class="card">
    <div class="card-header">
        <h3>Leave Request #${request.id}</h3>
    </div>
    <div class="card-body">
        <!-- Request details visible to everyone who can view -->
        <p><strong>Employee:</strong> ${request.employeeName}</p>
        <p><strong>Leave Type:</strong> ${request.leaveType}</p>
        <p><strong>From:</strong> ${request.startDate}</p>
        <p><strong>To:</strong> ${request.endDate}</p>
        <p><strong>Status:</strong> ${request.status}</p>
        
        <!-- Approval section - Only for users with approve permission -->
        <c:if test="${hrms:hasPermission(userPermissions, 'REQUEST_LEAVE_APPROVE')}">
            <hr>
            <div class="approval-section">
                <h4>Approval Actions</h4>
                <form action="/requests/leave/${request.id}/approve" method="POST">
                    <div class="form-group">
                        <label>Approval Note:</label>
                        <textarea name="note" class="form-control"></textarea>
                    </div>
                    <button type="submit" class="btn btn-success">
                        <i class="fas fa-check"></i> Approve
                    </button>
                </form>
                
                <form action="/requests/leave/${request.id}/reject" method="POST" class="mt-2">
                    <button type="submit" class="btn btn-danger">
                        <i class="fas fa-times"></i> Reject
                    </button>
                </form>
            </div>
        </c:if>
        
        <!-- Edit/Cancel - Only for owner and if status is DRAFT/PENDING -->
        <c:if test="${request.createdByUserId == sessionScope.account.userId}">
            <c:if test="${request.status == 'DRAFT'}">
                <c:if test="${hrms:hasPermission(userPermissions, 'REQUEST_LEAVE_EDIT')}">
                    <a href="/requests/leave/${request.id}/edit" class="btn btn-warning">
                        <i class="fas fa-edit"></i> Edit
                    </a>
                </c:if>
            </c:if>
            
            <c:if test="${request.status == 'PENDING'}">
                <c:if test="${hrms:hasPermission(userPermissions, 'REQUEST_LEAVE_CANCEL')}">
                    <button onclick="cancelRequest(${request.id})" class="btn btn-secondary">
                        <i class="fas fa-ban"></i> Cancel
                    </button>
                </c:if>
            </c:if>
        </c:if>
    </div>
</div>
```

---

## 🧭 Menu & Navigation

### Sidebar Menu with Permissions

**File:** `webapp/WEB-INF/views/layout/sidebar.jsp`

```jsp
<%@ taglib prefix="hrms" uri="http://hrms.group4.com/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<aside class="sidebar">
    <nav class="sidebar-nav">
        <ul class="nav">
            <!-- Dashboard - Everyone can see -->
            <li class="nav-item">
                <a href="/dashboard" class="nav-link">
                    <i class="fas fa-home"></i> Dashboard
                </a>
            </li>
            
            <!-- Profile - Everyone can see -->
            <li class="nav-item">
                <a href="/profile" class="nav-link">
                    <i class="fas fa-user"></i> My Profile
                </a>
            </li>
            
            <!-- Requests - Everyone can see own requests -->
            <li class="nav-item">
                <a href="/requests" class="nav-link">
                    <i class="fas fa-file-alt"></i> My Requests
                </a>
                <ul class="nav-submenu">
                    <li><a href="/requests">All My Requests</a></li>
                    
                    <c:if test="${hrms:hasPermission(userPermissions, 'REQUEST_LEAVE_CREATE')}">
                        <li><a href="/requests/leave/create">New Leave Request</a></li>
                    </c:if>
                    
                    <c:if test="${hrms:hasPermission(userPermissions, 'REQUEST_OT_CREATE')}">
                        <li><a href="/requests/ot/create">New OT Request</a></li>
                    </c:if>
                </ul>
            </li>
            
            <!-- Team Requests - Only MANAGER and above -->
            <c:if test="${hrms:hasAnyPermission(userPermissions, 'REQUEST_LIST_TEAM,REQUEST_LIST_ALL')}">
                <li class="nav-item">
                    <a href="/requests/team" class="nav-link">
                        <i class="fas fa-users"></i> Team Requests
                    </a>
                </li>
            </c:if>
            
            <!-- Attendance -->
            <li class="nav-item">
                <a href="/attendance" class="nav-link">
                    <i class="fas fa-clock"></i> Attendance
                </a>
                <ul class="nav-submenu">
                    <li><a href="/attendance">My Attendance</a></li>
                    
                    <c:if test="${hrms:hasPermission(userPermissions, 'ATT_VIEW_TEAM')}">
                        <li><a href="/attendance/team">Team Attendance</a></li>
                    </c:if>
                    
                    <c:if test="${hrms:hasPermission(userPermissions, 'ATT_VIEW_ALL')}">
                        <li><a href="/attendance/all">All Attendance</a></li>
                    </c:if>
                    
                    <c:if test="${hrms:hasPermission(userPermissions, 'ATT_IMPORT')}">
                        <li><a href="/attendance/import">Import Data</a></li>
                    </c:if>
                </ul>
            </li>
            
            <!-- Payslips -->
            <li class="nav-item">
                <a href="/payslips" class="nav-link">
                    <i class="fas fa-money-bill"></i> Payslips
                </a>
                <c:if test="${hrms:hasPermission(userPermissions, 'PAYSLIP_VIEW_ALL')}">
                    <ul class="nav-submenu">
                        <li><a href="/payslips">My Payslips</a></li>
                        <li><a href="/payslips/all">All Payslips</a></li>
                    </ul>
                </c:if>
            </li>
            
            <!-- HR Management Section - Only HR, HRM, ADMIN -->
            <c:if test="${hrms:hasAnyRole(userRole, 'ADMIN,HRM,HR')}">
                <li class="nav-divider">HR Management</li>
                
                <!-- User Management -->
                <c:if test="${hrms:hasPermission(userPermissions, 'USER_LIST')}">
                    <li class="nav-item">
                        <a href="/users" class="nav-link">
                            <i class="fas fa-users-cog"></i> Users
                        </a>
                    </li>
                </c:if>
                
                <!-- Department Management -->
                <c:if test="${hrms:hasPermission(userPermissions, 'DEPT_LIST')}">
                    <li class="nav-item">
                        <a href="/departments" class="nav-link">
                            <i class="fas fa-building"></i> Departments
                        </a>
                    </li>
                </c:if>
                
                <!-- Position Management -->
                <c:if test="${hrms:hasPermission(userPermissions, 'POS_LIST')}">
                    <li class="nav-item">
                        <a href="/positions" class="nav-link">
                            <i class="fas fa-briefcase"></i> Positions
                        </a>
                    </li>
                </c:if>
                
                <!-- Recruitment -->
                <c:if test="${hrms:hasPermission(userPermissions, 'REQUEST_RECRUITMENT_CREATE')}">
                    <li class="nav-item">
                        <a href="/requests/recruitment" class="nav-link">
                            <i class="fas fa-user-plus"></i> Recruitment
                        </a>
                    </li>
                </c:if>
                
                <!-- Applications -->
                <c:if test="${hrms:hasPermission(userPermissions, 'APP_LIST')}">
                    <li class="nav-item">
                        <a href="/applications" class="nav-link">
                            <i class="fas fa-file-invoice"></i> Applications
                        </a>
                    </li>
                </c:if>
                
                <!-- Contracts -->
                <c:if test="${hrms:hasPermission(userPermissions, 'CONTRACT_LIST')}">
                    <li class="nav-item">
                        <a href="/contracts" class="nav-link">
                            <i class="fas fa-file-contract"></i> Contracts
                        </a>
                    </li>
                </c:if>
            </c:if>
            
            <!-- Admin Section - Only ADMIN -->
            <c:if test="${hrms:hasRole(userRole, 'ADMIN')}">
                <li class="nav-divider">System Administration</li>
                
                <li class="nav-item">
                    <a href="/dashboard/admin" class="nav-link">
                        <i class="fas fa-tachometer-alt"></i> Admin Dashboard
                    </a>
                </li>
                
                <li class="nav-item">
                    <a href="/settings" class="nav-link">
                        <i class="fas fa-cog"></i> Settings
                    </a>
                    <ul class="nav-submenu">
                        <li><a href="/settings">General Settings</a></li>
                        <li><a href="/settings/roles">Roles</a></li>
                        <li><a href="/settings/permissions">Permissions</a></li>
                    </ul>
                </li>
            </c:if>
        </ul>
    </nav>
</aside>
```

---

## 🎮 Button & Action Controls

### Dynamic Button States

```jsp
<!-- Request List Page -->
<c:forEach items="${requests}" var="request">
    <tr>
        <td>${request.id}</td>
        <td>${request.title}</td>
        <td>${request.status}</td>
        <td>
            <!-- View - Everyone who can see the list can view -->
            <a href="/requests/leave/${request.id}" class="btn btn-sm btn-info">
                <i class="fas fa-eye"></i> View
            </a>
            
            <!-- Edit - Only owner and if DRAFT -->
            <c:if test="${request.createdByUserId == sessionScope.account.userId 
                         && request.status == 'DRAFT'
                         && hrms:hasPermission(userPermissions, 'REQUEST_LEAVE_EDIT')}">
                <a href="/requests/leave/${request.id}/edit" class="btn btn-sm btn-warning">
                    <i class="fas fa-edit"></i> Edit
                </a>
            </c:if>
            
            <!-- Cancel - Only owner and if PENDING -->
            <c:if test="${request.createdByUserId == sessionScope.account.userId 
                         && request.status == 'PENDING'
                         && hrms:hasPermission(userPermissions, 'REQUEST_LEAVE_CANCEL')}">
                <button onclick="cancelRequest(${request.id})" class="btn btn-sm btn-secondary">
                    <i class="fas fa-ban"></i> Cancel
                </button>
            </c:if>
            
            <!-- Approve/Reject - Only if has permission and status is PENDING -->
            <c:if test="${request.status == 'PENDING' 
                         && hrms:hasPermission(userPermissions, 'REQUEST_LEAVE_APPROVE')}">
                <!-- Additional check: MANAGER can only approve team requests -->
                <c:choose>
                    <c:when test="${hrms:hasAnyRole(userRole, 'ADMIN,HRM,HR')}">
                        <!-- Can approve any request -->
                        <button onclick="approveRequest(${request.id})" class="btn btn-sm btn-success">
                            <i class="fas fa-check"></i> Approve
                        </button>
                        <button onclick="rejectRequest(${request.id})" class="btn btn-sm btn-danger">
                            <i class="fas fa-times"></i> Reject
                        </button>
                    </c:when>
                    <c:when test="${hrms:hasRole(userRole, 'MANAGER') 
                                   && request.departmentId == sessionScope.account.departmentId}">
                        <!-- Manager can only approve team requests -->
                        <button onclick="approveRequest(${request.id})" class="btn btn-sm btn-success">
                            <i class="fas fa-check"></i> Approve
                        </button>
                        <button onclick="rejectRequest(${request.id})" class="btn btn-sm btn-danger">
                            <i class="fas fa-times"></i> Reject
                        </button>
                    </c:when>
                </c:choose>
            </c:if>
        </td>
    </tr>
</c:forEach>
```

### Disabled vs Hidden

**Option 1: Hide completely (Recommended)**
```jsp
<c:if test="${hrms:hasPermission(userPermissions, 'USER_DELETE')}">
    <button class="btn btn-danger">Delete</button>
</c:if>
```

**Option 2: Show but disabled (với tooltip)**
```jsp
<c:choose>
    <c:when test="${hrms:hasPermission(userPermissions, 'USER_DELETE')}">
        <button class="btn btn-danger">Delete</button>
    </c:when>
    <c:otherwise>
        <button class="btn btn-danger" disabled 
                title="You don't have permission to delete users">
            Delete
        </button>
    </c:otherwise>
</c:choose>
```

**Khi nào dùng cái nào?**
- **Hide:** Khi user hoàn toàn không cần biết feature này tồn tại
- **Disabled:** Khi muốn user biết feature tồn tại nhưng họ không có quyền (ví dụ: để họ biết cần request quyền)

---

## 💻 JavaScript Implementation

### For Dynamic Content

**File:** `webapp/static/js/permissions.js`

```javascript
/**
 * Permission helper for client-side
 * Note: This is for UX only, not security!
 */
const PermissionHelper = {
    // Permissions loaded from server
    userPermissions: new Set(),
    userRole: null,
    
    /**
     * Initialize permissions from server
     * Call this on page load
     */
    init: function(permissions, role) {
        this.userPermissions = new Set(permissions);
        this.userRole = role;
    },
    
    /**
     * Check if user has permission
     */
    hasPermission: function(featureCode) {
        return this.userPermissions.has(featureCode);
    },
    
    /**
     * Check if user has any of the permissions
     */
    hasAnyPermission: function(...featureCodes) {
        return featureCodes.some(code => this.userPermissions.has(code));
    },
    
    /**
     * Check if user has all permissions
     */
    hasAllPermissions: function(...featureCodes) {
        return featureCodes.every(code => this.userPermissions.has(code));
    },
    
    /**
     * Check if user has role
     */
    hasRole: function(role) {
        return this.userRole === role;
    },
    
    /**
     * Check if user has any of the roles
     */
    hasAnyRole: function(...roles) {
        return roles.includes(this.userRole);
    },
    
    /**
     * Show/hide element based on permission
     */
    toggleElement: function(selector, featureCode) {
        const elements = document.querySelectorAll(selector);
        const hasPermission = this.hasPermission(featureCode);
        
        elements.forEach(el => {
            if (hasPermission) {
                el.style.display = '';
            } else {
                el.style.display = 'none';
            }
        });
    },
    
    /**
     * Enable/disable element based on permission
     */
    toggleDisabled: function(selector, featureCode) {
        const elements = document.querySelectorAll(selector);
        const hasPermission = this.hasPermission(featureCode);
        
        elements.forEach(el => {
            el.disabled = !hasPermission;
            if (!hasPermission) {
                el.title = 'You don\'t have permission for this action';
            }
        });
    }
};
```

**Usage in JSP:**

```jsp
<!-- Load permissions into JavaScript -->
<script>
    // Initialize permissions on page load
    document.addEventListener('DOMContentLoaded', function() {
        const permissions = [
            <c:forEach items="${userPermissions}" var="perm" varStatus="status">
                '${perm}'<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];
        
        PermissionHelper.init(permissions, '${userRole}');
        
        // Apply permissions to dynamic elements
        PermissionHelper.toggleElement('.delete-btn', 'USER_DELETE');
        PermissionHelper.toggleElement('.approve-btn', 'REQUEST_LEAVE_APPROVE');
    });
</script>

<!-- Dynamic content -->
<div id="dynamic-content">
    <!-- These buttons will be shown/hidden by JavaScript -->
    <button class="btn btn-danger delete-btn" onclick="deleteUser()">
        Delete User
    </button>
    
    <button class="btn btn-success approve-btn" onclick="approveRequest()">
        Approve Request
    </button>
</div>
```

### AJAX Requests with Permission Check

```javascript
/**
 * Delete user with permission check
 */
function deleteUser(userId) {
    // Client-side check (UX only)
    if (!PermissionHelper.hasPermission('USER_DELETE')) {
        alert('You don\'t have permission to delete users');
        return;
    }
    
    if (!confirm('Are you sure you want to delete this user?')) {
        return;
    }
    
    // Send AJAX request
    fetch(`/users/${userId}/delete`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.status === 403) {
            // Backend denied (real security check)
            alert('Access denied. You don\'t have permission.');
        } else if (response.ok) {
            alert('User deleted successfully');
            location.reload();
        } else {
            alert('Error deleting user');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error deleting user');
    });
}

/**
 * Approve request with permission check
 */
function approveRequest(requestId) {
    // Client-side check
    if (!PermissionHelper.hasPermission('REQUEST_LEAVE_APPROVE')) {
        alert('You don\'t have permission to approve requests');
        return;
    }
    
    const note = prompt('Approval note (optional):');
    
    fetch(`/requests/leave/${requestId}/approve`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ note: note })
    })
    .then(response => {
        if (response.status === 403) {
            alert('Access denied');
        } else if (response.ok) {
            alert('Request approved successfully');
            location.reload();
        } else {
            alert('Error approving request');
        }
    });
}
```

---

## 🎨 Best Practices

### 1. ✅ Always Check Backend First

```java
// Backend - MUST HAVE
@WebServlet("/users/*/delete")
public class UserDeleteServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Account account = (Account) request.getSession().getAttribute("account");
        
        // SECURITY CHECK - Cannot be bypassed
        if (!permissionService.hasPermission(account, "USER_DELETE")) {
            response.sendError(403);
            return;
        }
        
        // Process delete...
    }
}
```

```jsp
<!-- Frontend - OPTIONAL but recommended -->
<c:if test="${hrms:hasPermission(userPermissions, 'USER_DELETE')}">
    <button onclick="deleteUser()">Delete</button>
</c:if>
```

### 2. ✅ Cache Permissions in Session

```java
// Load once after login
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
    HttpSession session = ((HttpServletRequest) request).getSession();
    Account account = (Account) session.getAttribute("account");
    
    if (account != null && session.getAttribute("userPermissions") == null) {
        // Load and cache
        Set<String> permissions = permissionService.getPermissions(account);
        session.setAttribute("userPermissions", permissions);
        
        Role role = roleService.getHighestRole(account);
        session.setAttribute("userRole", role.getCode());
    }
    
    chain.doFilter(request, response);
}
```

### 3. ✅ Invalidate Cache When Permissions Change

```java
// When admin changes user's role or permissions
public void updateUserRole(Long userId, Long newRoleId) {
    // Update database
    userDao.updateRole(userId, newRoleId);
    
    // Invalidate session cache
    SessionRegistry.invalidateUserSessions(userId);
    
    // Or force re-login
    SessionRegistry.logoutUser(userId);
}
```

### 4. ✅ Use Consistent Permission Codes

```java
// ❌ BAD - Inconsistent
if (role.equals("ADMIN")) { ... }
if (permissions.contains("delete_user")) { ... }
if (hasPermission("USER_DELETE")) { ... }

// ✅ GOOD - Consistent
if (permissionService.hasPermission(account, "USER_DELETE")) { ... }
```

### 5. ✅ Handle 403 Gracefully

```jsp
<!-- Error page: webapp/WEB-INF/views/error/403.jsp -->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Access Denied</title>
</head>
<body>
    <div class="error-page">
        <h1>403 - Access Denied</h1>
        <p>You don't have permission to access this resource.</p>
        <p>If you believe this is an error, please contact your administrator.</p>
        <a href="/dashboard" class="btn btn-primary">Go to Dashboard</a>
    </div>
</body>
</html>
```

```xml
<!-- web.xml -->
<error-page>
    <error-code>403</error-code>
    <location>/WEB-INF/views/error/403.jsp</location>
</error-page>
```

### 6. ✅ Log Permission Denials

```java
// In AuthorizationFilter
if (!permissionService.hasPermission(account, feature)) {
    // Log for audit
    auditService.logAccessDenied(
        account.getId(),
        feature.getCode(),
        request.getRequestURI(),
        request.getRemoteAddr()
    );
    
    response.sendError(403);
    return;
}
```

### 7. ❌ Don't Expose Sensitive Permission Data

```jsp
<!-- ❌ BAD - Exposes all permissions to client -->
<script>
    const allSystemPermissions = {
        <c:forEach items="${allFeatures}" var="feature">
            '${feature.code}': '${feature.description}',
        </c:forEach>
    };
</script>

<!-- ✅ GOOD - Only expose user's permissions -->
<script>
    const userPermissions = [
        <c:forEach items="${userPermissions}" var="perm">
            '${perm}',
        </c:forEach>
    ];
</script>
```

### 8. ✅ Test with Different Roles

```java
// Unit test
@Test
public void testUserListAccess() {
    // EMPLOYEE should not see user list
    Account employee = createAccount("EMPLOYEE");
    assertFalse(permissionService.hasPermission(employee, "USER_LIST"));
    
    // MANAGER should see user list (department scope)
    Account manager = createAccount("MANAGER");
    assertTrue(permissionService.hasPermission(manager, "USER_LIST"));
    
    // HRM should see user list (all scope)
    Account hrm = createAccount("HRM");
    assertTrue(permissionService.hasPermission(hrm, "USER_LIST"));
}
```

---

## 📊 Summary: Frontend vs Backend RBAC

| Aspect | Backend RBAC | Frontend RBAC |
|--------|--------------|---------------|
| **Purpose** | Security | UX Enhancement |
| **Required?** | ✅ YES - Mandatory | ⚠️ Optional but recommended |
| **Trusted?** | ✅ YES - Server-side | ❌ NO - Client can bypass |
| **Enforcement** | HTTP 403 Forbidden | Hide/disable UI elements |
| **Performance** | Checked on every request | Cached in session/client |
| **Implementation** | Authorization Filter | JSP conditions / JavaScript |
| **Can be bypassed?** | ❌ NO | ✅ YES (inspect element, disable JS) |
| **Primary benefit** | Prevents unauthorized access | Improves user experience |

**Golden Rule:** 
> Frontend RBAC là để user không thấy options họ không có quyền.  
> Backend RBAC là để ngăn chặn unauthorized access.  
> **LUÔN LUÔN có Backend RBAC. Frontend RBAC là bonus.**

---

## 🔗 Related Documents

- [RBAC Features & URLs](./rbac-features-urls.md) - Main permission matrix
- [RBAC README](./README-RBAC.md) - Implementation guide
- [RBAC Summary](./RBAC-SUMMARY.md) - Quick reference

---

**Last Updated:** 2025-10-14  
**Maintained by:** Team 4 - HRMS Project
