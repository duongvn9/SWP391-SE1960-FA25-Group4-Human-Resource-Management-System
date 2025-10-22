# Tóm tắt Luồng Hoạt động Hệ Thống Request

> **Tài liệu này mô tả chi tiết luồng hoạt động của các trang Leave Request, OT Request, Request List, Request Detail và Approve Request**
>
> **Ngày tạo:** 22/10/2025
> **Phiên bản:** 1.0

---

## 📚 Mục lục

1. [Leave Request (Đơn nghỉ phép)](#1-leave-request-đơn-nghỉ-phép)
2. [OT Request (Đơn tăng ca)](#2-ot-request-đơn-tăng-ca)
3. [Request List (Danh sách đơn)](#3-request-list-danh-sách-đơn)
4. [Request Detail (Chi tiết đơn)](#4-request-detail-chi-tiết-đơn)
5. [Approve Request (Duyệt đơn)](#5-approve-request-duyệt-đơn)
6. [Kiến trúc chung & Điểm quan trọng](#6-kiến-trúc-chung--điểm-quan-trọng)

---

## 1. Leave Request (Đơn nghỉ phép)

### 📍 Controller: `LeaveRequestController`
- **URL:** `/requests/leave/create`
- **Package:** `group4.hrms.controller`

### 🔵 A. GET - Hiển thị form tạo đơn nghỉ phép

#### Luồng xử lý chi tiết:

```
1. Kiểm tra Authentication
   └─ Session có account và user?
      ├─ Không → Redirect to /login
      └─ Có → Tiếp tục

2. Khởi tạo Service Layer
   └─ new LeaveRequestService(RequestDao, RequestTypeDao, LeaveTypeDao)

3. Load thông tin User Profile
   └─ UserProfileDao.findByUserId(userId)
      └─ Lấy gender (MALE/FEMALE)
      └─ Normalize: M/Male/MALE → "MALE", F/Female/FEMALE → "FEMALE"

4. Load danh sách loại nghỉ phép (Gender-filtered)
   └─ service.getAvailableLeaveTypes() → Map<String, String>
      └─ Filter logic:
         ├─ MATERNITY/MATERNITY_LEAVE → Chỉ FEMALE
         ├─ PATERNITY/PATERNITY_LEAVE → Chỉ MALE
         └─ Các loại khác → Tất cả

5. Load quy tắc nghỉ phép (Gender-filtered)
   └─ service.getAllLeaveTypeRules() → List<LeaveTypeRules>
      └─ Apply cùng logic filter như trên

6. Load số dư nghỉ phép (Gender-filtered)
   └─ service.getAllLeaveBalances(userId, currentYear)
      └─ Filter theo gender tương tự

7. Set Request Attributes
   ├─ leaveTypes
   ├─ leaveTypeRules
   ├─ leaveBalances
   ├─ currentYear
   └─ userGender

8. Forward to JSP
   └─ /WEB-INF/views/requests/leave-form.jsp
```

#### 🔌 API đặc biệt: Check Conflict (AJAX)

**URL:** `/requests/leave/create?action=checkConflict`

**Parameters:**
- `date` - Ngày nghỉ (YYYY-MM-DD)
- `period` - Buổi nghỉ (AM/PM)

**Response JSON:**
```json
{
  "hasConflict": true/false,
  "conflictType": "FULL_DAY" | "SAME_PERIOD" | null,
  "message": "Error message",
  "existingLeaveType": "ANNUAL_LEAVE",
  "existingPeriod": "AM",
  "existingStatus": "PENDING"
}
```

**Service Method:**
```java
service.checkHalfDayConflict(userId, date, period)
```

---

### 🟢 B. POST - Tạo đơn nghỉ phép

#### Form Parameters:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `leaveTypeCode` | String | ✅ | Mã loại nghỉ (ANNUAL_LEAVE, SICK_LEAVE, v.v.) |
| `startDate` | String | ✅ | Ngày bắt đầu (yyyy-MM-dd) |
| `endDate` | String | ⚠️ | Ngày kết thúc (bắt buộc với full-day, auto với half-day) |
| `reason` | String | ✅ | Lý do nghỉ |
| `isHalfDay` | Boolean | ❌ | Nghỉ nửa ngày (true/false) |
| `halfDayPeriod` | String | ⚠️ | Buổi nghỉ (AM/PM) - bắt buộc nếu isHalfDay=true |
| `attachmentType` | String | ❌ | Loại đính kèm: "file" hoặc "link" |
| `attachments` | File[] | ❌ | Danh sách file upload (max 5MB/file, 25MB total) |
| `driveLink` | String | ❌ | Link Google Drive |

#### Luồng xử lý chi tiết:

```
1. Authentication Check
   └─ Session có account và user?

2. Extract & Validate Parameters
   ├─ startDate: Bắt buộc
   ├─ endDate:
   │  ├─ Full-day → Bắt buộc
   │  └─ Half-day → Auto set = startDate nếu null
   └─ Half-day validation:
      ├─ isHalfDay=true → halfDayPeriod bắt buộc
      └─ halfDayPeriod ∈ {AM, PM}

3. Parse Dates
   └─ LocalDate.parse(dateStr)
      ├─ startDate → LocalDateTime (atStartOfDay)
      └─ endDate → LocalDateTime (atTime 23:59:59)

4. Create Leave Request
   └─ service.createLeaveRequest(
         accountId,
         userId,
         departmentId,
         leaveTypeCode,
         startDate,
         endDate,
         reason,
         isHalfDay,
         halfDayPeriod
      ) → requestId

5. Handle Attachments
   ├─ If attachmentType = "link":
   │  └─ AttachmentService.saveExternalLink(
   │        driveLink,
   │        requestId,
   │        "REQUEST",
   │        accountId,
   │        "Google Drive Link"
   │     )
   └─ Else (file upload):
      ├─ Filter parts với name="attachments" và size > 0
      ├─ Get uploadBasePath: /assets/img/Request/
      │  └─ Create directory if not exists
      └─ AttachmentService.saveFiles(
            fileParts,
            requestId,
            "REQUEST",
            accountId,
            uploadBasePath
         )

6. Error Handling & Response
   └─ [Chi tiết xem phần Error Handling]

7. Reload Form Data
   ├─ service.getAvailableLeaveTypes()
   ├─ service.getAllLeaveTypeRules()
   └─ service.getAllLeaveBalances(userId, currentYear)
   └─ Apply gender filtering

8. Forward back to form
   └─ /WEB-INF/views/requests/leave-form.jsp
      └─ With success/error message
```

#### ⚠️ Error Handling Hierarchy:

```java
try {
    // Create leave request
} catch (LeaveValidationException e) {
    // Structured business validation errors
    // errorType:
    // - HALF_DAY_FULL_DAY_CONFLICT → HTTP 409
    // - HALF_DAY_SAME_PERIOD_CONFLICT → HTTP 409
    // - INSUFFICIENT_BALANCE → HTTP 400
    // - HALF_DAY_NON_WORKING_DAY → HTTP 400
    // - INVALID_HALF_DAY_PERIOD → HTTP 400

    saveFormDataToSession();
    request.setAttribute("error", e.getMessage());
    request.setAttribute("errorType", e.getErrorType());
    request.setAttribute("errorTitle", e.getShortMessage());
    request.setAttribute("errorDetails", e.getDetailedMessage());

} catch (IllegalArgumentException e) {
    // Generic validation errors
    saveFormDataToSession();
    request.setAttribute("error", e.getMessage());

} catch (SQLException e) {
    // Database errors
    saveFormDataToSession();
    request.setAttribute("error", "Database error...");

} catch (Exception e) {
    // System errors
    saveFormDataToSession();
    request.setAttribute("error", "System error...");
}
```

#### 💾 Save Form Data to Session:

Khi có lỗi, form data được lưu vào session để preserve user input:

```java
session.setAttribute("formData_leaveTypeCode", leaveTypeCode);
session.setAttribute("formData_startDate", startDate);
session.setAttribute("formData_endDate", endDate);
session.setAttribute("formData_reason", reason);
session.setAttribute("formData_isHalfDay", isHalfDay);
session.setAttribute("formData_halfDayPeriod", halfDayPeriod);
```

---

## 2. OT Request (Đơn tăng ca)

### 📍 Controller: `OTRequestController`
- **URL:** `/requests/ot/create`
- **Package:** `group4.hrms.controller`

### 🔵 A. GET - Hiển thị form tạo đơn OT

#### Luồng xử lý chi tiết:

```
1. Authentication Check
   └─ Session có account và user?

2. Khởi tạo Service Layer
   └─ new OTRequestService(
         RequestDao,
         RequestTypeDao,
         HolidayDao,
         HolidayCalendarDao,
         UserDao
      )

3. Load OT Balance
   └─ service.getOTBalance(userId) → OTBalance
      └─ Chứa:
         ├─ currentWeekHours (giờ OT tuần này)
         ├─ monthlyHours (giờ OT tháng này)
         └─ annualHours (giờ OT năm nay)

4. Load Holidays & Compensatory Days
   └─ For years: currentYear đến currentYear+2
      ├─ service.getHolidaysForYear(year) → List<String>
      └─ service.getCompensatoryDaysForYear(year) → List<String>
   └─ Mục đích: Hỗ trợ JavaScript validation cho future dates

5. Load Subordinates (if Manager)
   └─ UserDao.getSubordinates(userId) → List<User>
      └─ Based on job_level hierarchy:
         ADMIN(1) > HR_MANAGER(2) > HR_STAFF(3) > DEPT_MANAGER(4) > STAFF(5)
      └─ Nếu có subordinates → Set attribute "departmentEmployees"

6. Set Request Attributes
   ├─ otBalance
   ├─ holidays
   ├─ compensatoryDays
   └─ departmentEmployees (optional)

7. Forward to JSP
   └─ /WEB-INF/views/requests/ot-form.jsp
```

---

### 🟢 B. POST - Tạo đơn OT

#### Form Parameters:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `requestFor` | String | ✅ | "self" hoặc "employee" |
| `selectedEmployeeId` | Long | ⚠️ | ID nhân viên (bắt buộc nếu requestFor="employee") |
| `otDate` | String | ✅ | Ngày tăng ca (yyyy-MM-dd) |
| `startTime` | String | ✅ | Giờ bắt đầu (HH:mm) |
| `endTime` | String | ✅ | Giờ kết thúc (HH:mm) |
| `reason` | String | ✅ | Lý do tăng ca |
| `employeeConsent` | Boolean | ❌ | Đồng ý của nhân viên ("on"/"true") |
| `attachmentType` | String | ❌ | "file" hoặc "link" |
| `attachments` | File[] | ❌ | Danh sách file upload |
| `driveLink` | String | ❌ | Link Google Drive |

#### Luồng xử lý chi tiết:

```
1. Authentication Check

2. Determine Target User
   ├─ If requestFor = "employee":
   │  ├─ Parse selectedEmployeeId
   │  ├─ Verify subordinate relationship:
   │  │  └─ UserDao.getSubordinates(currentUserId)
   │  │     └─ Check selectedUserId trong danh sách
   │  ├─ If not subordinate → throw IllegalArgumentException
   │  └─ Get target employee's departmentId
   └─ Else (requestFor = "self"):
      └─ targetUserId = currentUserId
         targetDepartmentId = currentUserDepartmentId

3. Select Service Method
   ├─ If creating for subordinate:
   │  └─ service.createOTRequestForEmployee(
   │        accountId,
   │        targetUserId,
   │        otDate,
   │        startTime,
   │        endTime,
   │        reason
   │     )
   │     └─ NOTE: Manager-created OT is AUTO-APPROVED
   │        └─ status = "APPROVED"
   │        └─ current_approver_account_id = manager's accountId
   │        └─ createdByManager = true (in detail JSON)
   │
   └─ Else (creating for self):
      └─ service.createOTRequest(
            accountId,
            targetUserId,
            targetDepartmentId,
            otDate,
            startTime,
            endTime,
            reason,
            employeeConsent
         )
         └─ status = "PENDING" (cần approval)

4. Handle Attachments
   └─ [Giống Leave Request - xem phần trên]

5. Error Handling
   ├─ IllegalArgumentException → Validation errors
   ├─ SQLException → Database errors
   └─ Exception → System errors

6. Reload Form Data
   ├─ service.getOTBalance(userId)
   └─ UserDao.getSubordinates(userId)

7. Forward back to form
   └─ /WEB-INF/views/requests/ot-form.jsp
```

#### 🎯 Điểm đặc biệt: Manager Creating OT for Employee

**Workflow:**
1. Manager chọn employee từ dropdown (subordinates only)
2. Điền thông tin OT (date, time, reason)
3. Submit → `createOTRequestForEmployee()`
4. **Auto-approved**: Status = "APPROVED" ngay lập tức
5. Lưu flag `createdByManager=true` trong detail JSON
6. Employee không cần approve, request hiệu lực ngay

**Security:**
- Verify subordinate relationship trước khi tạo
- Chỉ manager/HR có dropdown này
- Employee tạo cho mình luôn cần approval

---

## 3. Request List (Danh sách đơn)

### 📍 Controller: `RequestListController`
- **URLs:** `/requests`, `/requests/list`
- **Package:** `group4.hrms.controller`

### 🔵 A. GET - Hiển thị danh sách đơn

#### Request Parameters (All Optional):

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `scope` | String | Depends on role | "my" / "subordinate" / "all" |
| `type` | Long | null | Request type ID (filter) |
| `status` | String | null | "PENDING" / "APPROVED" / "REJECTED" |
| `showCancelled` | Boolean | false | Hiển thị đơn đã hủy |
| `fromDate` | String | null | Từ ngày (yyyy-MM-dd) |
| `toDate` | String | null | Đến ngày (yyyy-MM-dd) |
| `employeeId` | Long | null | Filter theo nhân viên |
| `search` | String | null | Keyword tìm kiếm |
| `page` | Integer | 1 | Trang hiện tại |
| `pageSize` | Integer | 8 | Số item mỗi trang |

#### Luồng xử lý chi tiết:

```
1. Authentication Check
   └─ Session có account và user?

2. Parse Filter Parameters
   └─ parseFilterParameters(request) → RequestListFilter
      ├─ Extract tất cả parameters
      ├─ Parse dates (LocalDate.parse)
      ├─ Parse numbers (Long.parseLong, Integer.parseInt)
      └─ Set defaults (page=1, pageSize=8)

3. Load User's Position
   └─ getUserPosition(user) → Position
      ├─ PositionDao.findById(user.getPositionId())
      └─ Return Position or null

4. Check Permissions & Available Scopes
   └─ RequestListPermissionHelper.getAvailableScopes(position)
      → Set<String>
      └─ Logic:
         ├─ ADMIN → {my, subordinate, all}
         ├─ HR_MANAGER, HR_STAFF → {my, subordinate, all}
         ├─ DEPT_MANAGER → {my, subordinate}
         └─ STAFF → {my}

5. Set Default Scope (if not specified)
   └─ RequestListPermissionHelper.getDefaultScope(position)
      └─ ADMIN/HR → "all"
      └─ Manager → "subordinate"
      └─ Staff → "my"

6. Initialize Service
   └─ new RequestListService(RequestDao, UserDao, DepartmentDao)

7. Get Request List
   └─ service.getRequestList(filter, user, position, accountId, contextPath)
      → RequestListResult
      └─ Contains:
         ├─ requests: List<RequestDto>
         ├─ totalRecords: int
         ├─ currentPage: int
         ├─ pageSize: int
         ├─ totalPages: int
         └─ hasNext, hasPrevious: boolean

8. Get Type Statistics
   └─ service.getRequestTypeStatistics(filter, user, position)
      → Map<Long, Integer>
      └─ Count requests by type (for stats cards)

9. Load Filter Reference Data
   ├─ RequestTypeDao.findAll() → List<RequestType>
   └─ If can view subordinate/all:
      └─ loadEmployeesForFilter(user, position) → List<User>
         ├─ If scope="all" → UserDao.findAll()
         └─ Else → UserDao.findSubordinateUserIds(userId)

10. Set Request Attributes
    ├─ result (RequestListResult)
    ├─ filter (RequestListFilter)
    ├─ availableScopes (Set<String>)
    ├─ requestTypes (List<RequestType>)
    ├─ employees (List<User>) - optional
    ├─ typeStatistics (Map<Long, Integer>)
    └─ canExport (Boolean)

11. Forward to JSP
    └─ /WEB-INF/views/requests/request-list.jsp
```

#### 🔍 Filter Logic trong Service Layer:

```java
// Build SQL WHERE clause based on filter
StringBuilder whereClause = new StringBuilder("WHERE 1=1");

// Scope filtering
if (scope.equals("my")) {
    whereClause.append(" AND r.user_id = ?");
} else if (scope.equals("subordinate")) {
    // Get subordinate IDs
    List<Long> subordinateIds = userDao.findSubordinateUserIds(userId);
    whereClause.append(" AND r.user_id IN (?)");
} else if (scope.equals("all")) {
    // No filter (HR can see all)
}

// Type filtering
if (filter.getRequestTypeId() != null) {
    whereClause.append(" AND r.request_type_id = ?");
}

// Status filtering
if (filter.getStatus() != null) {
    whereClause.append(" AND r.status = ?");
}

// Show cancelled toggle
if (!filter.isShowCancelled()) {
    whereClause.append(" AND r.status != 'CANCELLED'");
}

// Date range
if (filter.getFromDate() != null) {
    whereClause.append(" AND DATE(r.created_at) >= ?");
}
if (filter.getToDate() != null) {
    whereClause.append(" AND DATE(r.created_at) <= ?");
}

// Employee filter
if (filter.getEmployeeId() != null) {
    whereClause.append(" AND r.user_id = ?");
}

// Search keyword
if (filter.getSearchKeyword() != null) {
    whereClause.append(" AND (u.full_name LIKE ? OR r.reason LIKE ?)");
}

// Pagination
String sql = "SELECT ... FROM requests r ... " + whereClause
           + " ORDER BY r.created_at DESC"
           + " LIMIT ? OFFSET ?";
```

---

### 🟢 B. POST - Soft Delete Request

#### Action: `delete` (AJAX)

**Request Body (Form Data):**
- `action=delete`
- `requestId={id}`

**Response JSON:**
```json
{
  "success": true/false,
  "message": "Request deleted successfully" | "Error message"
}
```

#### Luồng xử lý:

```
1. Authentication Check
   └─ Return 401 if not authenticated

2. Validate Request ID
   ├─ Parse Long
   └─ Return 400 if invalid format

3. Call Service
   └─ service.softDeleteRequest(requestId, user) → boolean
      └─ Business Logic:
         ├─ Check ownership: request.userId == currentUser.id
         ├─ Check status: must be "PENDING"
         ├─ If valid:
         │  └─ UPDATE requests SET status='CANCELLED' WHERE id=?
         └─ Return true/false

4. Return JSON Response
   ├─ Success: HTTP 200, success=true
   ├─ Forbidden: HTTP 403, success=false
   └─ Error: HTTP 500, success=false
```

**Security Rules:**
- Chỉ owner mới có thể xóa
- Chỉ xóa được đơn PENDING
- Soft delete (set status = CANCELLED, không xóa khỏi DB)

---

## 4. Request Detail (Chi tiết đơn)

### 📍 Controller: `RequestDetailController`
- **URL:** `/requests/detail?id={requestId}`
- **Package:** `group4.hrms.controller`

### 🔵 GET - Hiển thị chi tiết đơn

#### Luồng xử lý chi tiết:

```
1. Authentication Check
   └─ Session có account và user?

2. Validate Request ID
   ├─ Extract parameter "id"
   ├─ Check null/empty → Redirect /requests with error
   └─ Parse Long → Redirect if NumberFormatException

3. Load User's Position
   └─ PositionDao.findById(user.getPositionId()) → Position
      └─ For permission checking

4. Load Request Entity (for permission check)
   └─ RequestDao.findById(requestId) → Optional<Request>
      ├─ If not present → Redirect with "Request not found"
      └─ requestEntity = result

5. Check View Permission
   └─ RequestListPermissionHelper.canViewRequest(user, requestEntity, position)
      → boolean
      └─ Logic:
         ├─ If user is owner → true
         ├─ If user is manager of owner → true
         ├─ If user is HR → true
         └─ Else → false
      └─ If false → Redirect with "No permission"

6. Load Request Details (with JOIN)
   └─ RequestDao.findByIdWithDetails(requestId) → Optional<RequestDto>
      └─ SQL JOIN:
         ├─ requests r
         ├─ users u (creator)
         ├─ request_types rt
         └─ departments d
      └─ Map to RequestDto with all related data

7. Calculate Status Badge Class
   └─ requestDto.calculateStatusBadgeClass()
      ├─ PENDING → "badge-warning"
      ├─ APPROVED → "badge-success"
      ├─ REJECTED → "badge-danger"
      └─ CANCELLED → "badge-secondary"

8. Parse Detail JSON by Request Type
   └─ Switch (requestTypeId):
      ├─ 6 (LEAVE_REQUEST):
      │  └─ requestEntity.getLeaveDetail() → LeaveRequestDetail
      │     └─ Extract: leaveTypeCode, startDate, endDate,
      │                isHalfDay, halfDayPeriod, managerNotes
      │
      ├─ 7 (OVERTIME_REQUEST):
      │  └─ requestEntity.getOtDetail() → OTRequestDetail
      │     └─ Extract: otDate, startTime, endTime,
      │                calculatedHours, createdByManager
      │
      ├─ 8 (ADJUSTMENT_REQUEST):
      │  └─ requestEntity.getAppealDetail() → AppealRequestDetail
      │     └─ Extract: attendanceRecordId, appealType,
      │                originalValue, proposedValue
      │
      └─ Recruitment:
         └─ requestEntity.getRecruitmentDetail() → RecruitmentDetailsDto

9. Check Approve Permission
   └─ RequestListPermissionHelper.canApproveRequest(
         user, requestEntity, position, accountId
      ) → boolean
      └─ Complex Logic:
         ├─ Cannot approve own request
         ├─ Can only approve PENDING requests
         ├─ Special case: OT created by manager
         │  └─ If otDetail.createdByManager == true → false
         │     (already auto-approved, no need to approve again)
         ├─ Manager can approve subordinates' requests
         ├─ HR can approve all requests
         └─ Return true/false

10. Load Attachments
    └─ AttachmentDao.findByOwner("REQUEST", requestId)
       → List<Attachment>
       └─ Each attachment:
          ├─ id
          ├─ file_name
          ├─ file_path (for file upload)
          ├─ external_link (for Google Drive)
          ├─ file_type
          ├─ file_size
          └─ uploaded_at

11. Set Request Attributes
    ├─ requestDto (main DTO)
    ├─ leaveDetail / otDetail / appealDetail / recruitmentDetail
    ├─ canApprove (boolean)
    ├─ attachments (List<Attachment>)
    └─ managerNotes (if available in leave detail)

12. Forward to JSP
    └─ /WEB-INF/views/requests/request-detail.jsp
```

#### 📊 RequestDto Structure:

```java
public class RequestDto {
    // Basic info
    private Long id;
    private Long requestTypeId;
    private String requestTypeCode;
    private String requestTypeName;

    // User info
    private Long userId;
    private String userName;
    private String userEmail;

    // Department info
    private Long departmentId;
    private String departmentName;

    // Request data
    private String status;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Approval info
    private Long currentApproverAccountId;
    private String currentApproverName;
    private String approveReason;

    // Detail JSON (parsed into specific objects)
    private String detailJson;

    // UI helpers
    private String statusBadgeClass;
    private String viewUrl;

    // Type-specific details (lazy-parsed)
    private LeaveRequestDetail leaveDetail;
    private OTRequestDetail otDetail;
    private AppealRequestDetail appealDetail;
    private RecruitmentDetailsDto recruitmentDetail;
}
```

#### 🔐 Permission Check Details:

**canViewRequest():**
```java
public static boolean canViewRequest(User user, Request request, Position position) {
    // Owner can always view
    if (request.getUserId().equals(user.getId())) {
        return true;
    }

    // HR can view all
    if (position != null && position.getJobLevel() <= 3) { // HR levels
        return true;
    }

    // Manager can view subordinates
    if (position != null && position.getJobLevel() == 4) { // DEPT_MANAGER
        UserDao userDao = new UserDao();
        List<Long> subordinateIds = userDao.findSubordinateUserIds(user.getId());
        return subordinateIds.contains(request.getUserId());
    }

    return false;
}
```

**canApproveRequest():**
```java
public static boolean canApproveRequest(User user, Request request,
                                       Position position, Long accountId) {
    // Cannot approve own request
    if (request.getUserId().equals(user.getId())) {
        return false;
    }

    // Can only approve PENDING requests
    if (!"PENDING".equals(request.getStatus())) {
        return false;
    }

    // Special: OT created by manager is already approved
    if (request.getRequestTypeId() == 7L) { // OVERTIME_REQUEST
        OTRequestDetail otDetail = request.getOtDetail();
        if (otDetail != null && otDetail.getCreatedByManager()) {
            return false; // Already approved, no need to approve again
        }
    }

    // HR can approve all
    if (position != null && position.getJobLevel() <= 3) {
        return true;
    }

    // Manager can approve subordinates
    if (position != null && position.getJobLevel() == 4) {
        UserDao userDao = new UserDao();
        List<Long> subordinateIds = userDao.findSubordinateUserIds(user.getId());
        return subordinateIds.contains(request.getUserId());
    }

    return false;
}
```

---

## 5. Approve Request (Duyệt đơn)

### 📍 Controller: `ApproveRequestController`
- **URL:** `/requests/approve` (POST only)
- **Package:** `group4.hrms.controller`

### 🟢 POST - Approve/Reject Request (AJAX)

#### Request Parameters:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `action` | String | ✅ | "approve" hoặc "reject" |
| `requestId` | Long | ✅ | ID của request |
| `reason` | String | ⚠️ | Lý do (bắt buộc với reject, optional với approve) |

#### Response JSON:

```json
{
  "success": true/false,
  "message": "Request approved successfully" | "Error message"
}
```

#### Luồng xử lý chi tiết:

```
1. Set Response Type
   └─ response.setContentType("application/json")
      response.setCharacterEncoding("UTF-8")

2. Authentication Check
   ├─ Session exists?
   ├─ User logged in?
   └─ Return {"success": false, "message": "Not authenticated"}

3. Validate Parameters
   ├─ action not null?
   ├─ requestId not null?
   └─ Return {"success": false, "message": "Missing parameters"}

4. Parse Request ID
   └─ Try Long.parseLong(requestIdStr)
      └─ Catch → Return "Invalid request ID"

5. Load Request Entity
   └─ RequestDao.findById(requestId) → Optional<Request>
      └─ If not present → Return "Request not found"

6. Parse Detail JSON (needed for permission check)
   └─ Based on requestTypeId:
      ├─ 6 → requestEntity.getLeaveDetail()
      ├─ 7 → requestEntity.getOtDetail()
      └─ 8 → requestEntity.getAppealDetail()
   └─ Catch parsing errors → Log warning, continue

7. Load User's Position
   └─ PositionDao.findById(user.getPositionId()) → Position

8. Check Approve Permission
   └─ RequestListPermissionHelper.canApproveRequest(
         user, requestEntity, position, accountId
      )
      └─ If false → Return "No permission"

9. Process Action
   ├─ IF action = "approve":
   │  ├─ Check status == "PENDING"
   │  │  └─ Else → Return "Can only approve PENDING requests"
   │  ├─ Update request:
   │  │  ├─ status = "APPROVED"
   │  │  ├─ current_approver_account_id = currentAccountId
   │  │  ├─ updated_at = now()
   │  │  └─ approve_reason = reason (if provided)
   │  └─ RequestDao.update(request)
   │
   └─ ELSE IF action = "reject":
      ├─ Validate reason not empty
      │  └─ Else → Return "Rejection reason is required"
      ├─ Check status ∈ {"PENDING", "APPROVED"}
      │  └─ Allows manager override of approved requests
      ├─ Update request:
      │  ├─ status = "REJECTED"
      │  ├─ current_approver_account_id = currentAccountId
      │  ├─ updated_at = now()
      │  └─ approve_reason = reason (required)
      └─ RequestDao.update(request)

10. Return Response
    ├─ If update successful:
    │  └─ Log info
    │  └─ Return {"success": true, "message": "..."}
    └─ Else:
       └─ Log warning
       └─ Return {"success": false, "message": "Failed to ..."}

11. Exception Handling
    └─ Catch all exceptions:
       └─ Log severe
       └─ Return {"success": false, "message": "An error occurred: ..."}
```

#### 🎯 Business Rules:

**Approve:**
- Chỉ approve được đơn **PENDING**
- Không approve được đơn của chính mình
- Manager chỉ approve đơn subordinates
- HR approve tất cả đơn
- OT created by manager không cần approve (already auto-approved)
- Reason optional (có thể để trống)

**Reject:**
- Reject được đơn **PENDING** hoặc **APPROVED** (manager override)
- Reason **bắt buộc**
- Không reject được đơn của chính mình
- Manager/HR có quyền reject

#### 📝 Database Update:

```sql
UPDATE requests
SET
    status = ?,                          -- 'APPROVED' or 'REJECTED'
    current_approver_account_id = ?,    -- ID người approve/reject
    approve_reason = ?,                  -- Lý do (nullable cho approve)
    updated_at = ?                       -- Timestamp hiện tại
WHERE id = ?
```

---

## 6. Kiến trúc chung & Điểm quan trọng

### 🏗️ A. Kiến trúc 3 lớp

```
┌─────────────────────────────────────────────────┐
│              PRESENTATION LAYER                 │
│  ┌──────────────────────────────────────────┐   │
│  │  JSP Views (WEB-INF/views/requests/)     │   │
│  │  - leave-form.jsp                        │   │
│  │  - ot-form.jsp                           │   │
│  │  - request-list.jsp                      │   │
│  │  - request-detail.jsp                    │   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
                      ↕
┌─────────────────────────────────────────────────┐
│              CONTROLLER LAYER                   │
│  ┌──────────────────────────────────────────┐   │
│  │  Servlets (group4.hrms.controller)       │   │
│  │  - LeaveRequestController                │   │
│  │  - OTRequestController                   │   │
│  │  - RequestListController                 │   │
│  │  - RequestDetailController               │   │
│  │  - ApproveRequestController              │   │
│  │                                           │   │
│  │  Responsibilities:                       │   │
│  │  • Xác thực session                      │   │
│  │  • Validate & parse parameters           │   │
│  │  • Điều phối Service calls                │   │
│  │  • Set attributes cho JSP                │   │
│  │  • Error handling & logging              │   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
                      ↕
┌─────────────────────────────────────────────────┐
│              SERVICE LAYER                      │
│  ┌──────────────────────────────────────────┐   │
│  │  Business Logic (group4.hrms.service)    │   │
│  │  - LeaveRequestService                   │   │
│  │  - OTRequestService                      │   │
│  │  - RequestListService                    │   │
│  │  - AttachmentService                     │   │
│  │                                           │   │
│  │  Responsibilities:                       │   │
│  │  • Business rules validation             │   │
│  │  • Transaction coordination              │   │
│  │  • Complex calculations                  │   │
│  │  • Data transformation                   │   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
                      ↕
┌─────────────────────────────────────────────────┐
│              DATA ACCESS LAYER                  │
│  ┌──────────────────────────────────────────┐   │
│  │  DAOs (group4.hrms.dao)                  │   │
│  │  - RequestDao                            │   │
│  │  - RequestTypeDao                        │   │
│  │  - LeaveTypeDao                          │   │
│  │  - UserDao                               │   │
│  │  - PositionDao                           │   │
│  │  - DepartmentDao                         │   │
│  │  - AttachmentDao                         │   │
│  │  - HolidayDao / HolidayCalendarDao       │   │
│  │                                           │   │
│  │  Responsibilities:                       │   │
│  │  • CRUD operations                       │   │
│  │  • SQL query execution                   │   │
│  │  • ResultSet mapping                     │   │
│  │  • Connection management                 │   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
                      ↕
              ┌───────────────┐
              │  MySQL 8.x DB │
              │  utf8mb4      │
              │  InnoDB       │
              └───────────────┘
```

---

### 🔐 B. Permission System

#### Job Level Hierarchy:

```
Level 1: ADMIN           (NO ACCESS to request system)
   ↓
Level 2: HR_MANAGER      (Quản lý HR - Full access)
   ↓
Level 3: HR_STAFF        (Nhân viên HR - Full access)
   ↓
Level 4: DEPT_MANAGER    (Quản lý phòng ban - Subordinate access)
   ↓
Level 5: STAFF           (Nhân viên - Own requests only)
```

#### Available Scopes by Role:

| Role | Job Level | Available Scopes | Default Scope |
|------|-----------|------------------|---------------|
| ADMIN | 1 | **NONE** | null (No access) |
| HR_MANAGER | 2 | my, subordinate, all | all |
| HR_STAFF | 3 | my, subordinate, all | subordinate |
| DEPT_MANAGER | 4 | my, subordinate | subordinate |
| STAFF | 5 | my | my |

#### Permission Helper: `RequestListPermissionHelper`

**Main Methods:**

```java
// Get available scopes for user
public static Set<String> getAvailableScopes(Position position)

// Get default scope
public static String getDefaultScope(Position position)

// Check if user can view request
public static boolean canViewRequest(User user, Request request, Position position)

// Check if user can approve request
public static boolean canApproveRequest(User user, Request request,
                                       Position position, Long accountId)

// Check if user can export
public static boolean canExport(Position position)
```

**Logic Matrix:**

| Action | Owner | Manager (Subordinate) | Manager (Non-subordinate) | HR | ADMIN | Other |
|--------|-------|----------------------|---------------------------|-----|-------|-------|
| **View** | ✅ | ✅ | ❌ | ✅ | ❌ | ❌ |
| **Approve** | ❌ | ✅ (if PENDING) | ❌ | ✅ (if PENDING) | ❌ | ❌ |
| **Delete** | ✅ (if PENDING) | ❌ | ❌ | ❌ | ❌ | ❌ |

**Special Cases:**
- **ADMIN has NO ACCESS**: Admin users cannot view, create, approve, or interact with the request system in any way
- **OT created by manager**: Auto-approved, `canApproveRequest()` returns `false`
- **Reject approved request**: Only HR/Manager can reject already-approved requests (override)

---

### 📎 C. Attachment System

#### Storage Strategy:

**Two types of attachments:**

1. **File Upload:**
   - Upload to: `webapp/assets/img/Request/`
   - Max file size: **5MB per file**
   - Max request size: **25MB total**
   - Saved fields:
     - `file_name` - Original filename
     - `file_path` - Relative path trong webapp
     - `file_type` - MIME type
     - `file_size` - Size in bytes

2. **External Link (Google Drive):**
   - Save URL to database
   - Saved fields:
     - `external_link` - Full URL
     - `file_name` - Description ("Google Drive Link")

#### AttachmentService Methods:

```java
// Save uploaded files
public List<Attachment> saveFiles(
    Collection<Part> fileParts,
    Long ownerId,
    String ownerType,  // "REQUEST"
    Long uploadedByAccountId,
    String uploadBasePath
) throws IOException

// Save external link
public Attachment saveExternalLink(
    String externalLink,
    Long ownerId,
    String ownerType,  // "REQUEST"
    Long uploadedByAccountId,
    String description
) throws SQLException
```

#### Database Schema (attachments table):

```sql
CREATE TABLE attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_type VARCHAR(50) NOT NULL,      -- 'REQUEST'
    owner_id BIGINT NOT NULL,             -- request.id
    file_name VARCHAR(255),
    file_path VARCHAR(500),               -- For uploads
    external_link VARCHAR(1000),          -- For Google Drive
    file_type VARCHAR(100),
    file_size BIGINT,
    uploaded_by_account_id BIGINT,
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_owner (owner_type, owner_id)
);
```

---

### ⚠️ D. Error Handling Strategy

#### Exception Hierarchy:

```
Exception
└─ SQLException
   └─ Database errors
└─ IllegalArgumentException
   └─ Generic validation errors
└─ LeaveValidationException (custom)
   └─ Structured business validation errors
      ├─ HALF_DAY_FULL_DAY_CONFLICT
      ├─ HALF_DAY_SAME_PERIOD_CONFLICT
      ├─ INSUFFICIENT_BALANCE
      ├─ HALF_DAY_NON_WORKING_DAY
      └─ INVALID_HALF_DAY_PERIOD
```

#### HTTP Status Codes:

| Exception Type | HTTP Status | Use Case |
|----------------|-------------|----------|
| Authentication failure | 401 Unauthorized | Session expired, not logged in |
| Permission denied | 403 Forbidden | No permission to view/approve |
| Validation error | 400 Bad Request | Invalid input, business rules violated |
| Conflict | 409 Conflict | Half-day conflicts |
| Not found | 404 Not Found | Request/Resource not found |
| Server error | 500 Internal Server Error | Database/System errors |

#### Error Message Pattern:

```java
// LeaveValidationException structure
public class LeaveValidationException extends Exception {
    private String errorType;        // Machine-readable code
    private String shortMessage;     // User-friendly title
    private String detailedMessage;  // Detailed explanation

    // Example usage:
    throw new LeaveValidationException(
        "HALF_DAY_FULL_DAY_CONFLICT",
        "Half-day request conflicts with full-day leave",
        "You already have a full-day ANNUAL_LEAVE on 2025-10-25. " +
        "Cannot create half-day request on the same date."
    );
}
```

#### Form Data Preservation:

Khi có lỗi, form data được lưu vào session:

```java
private void saveFormDataToSession(HttpSession session, ...) {
    session.setAttribute("formData_leaveTypeCode", leaveTypeCode);
    session.setAttribute("formData_startDate", startDate);
    session.setAttribute("formData_endDate", endDate);
    session.setAttribute("formData_reason", reason);
    session.setAttribute("formData_isHalfDay", isHalfDay);
    session.setAttribute("formData_halfDayPeriod", halfDayPeriod);
}
```

JSP sẽ lấy lại để fill form:

```jsp
<input type="text"
       name="reason"
       value="${sessionScope.formData_reason}" />
```

---

### 🎨 E. Gender-based Filtering (Leave Request)

#### Logic:

```java
// Normalize gender from database
String rawGender = userProfile.getGender().toUpperCase().trim();
String userGender;

if (rawGender.startsWith("F") || rawGender.equals("FEMALE")) {
    userGender = "FEMALE";
} else if (rawGender.startsWith("M") || rawGender.equals("MALE")) {
    userGender = "MALE";
} else {
    userGender = "UNKNOWN";
}

// Filter leave types
Map<String, String> leaveTypes = new LinkedHashMap<>();
for (Map.Entry<String, String> entry : allLeaveTypes.entrySet()) {
    String code = entry.getKey();

    if (code.equals("MATERNITY") || code.equals("MATERNITY_LEAVE")) {
        if ("FEMALE".equalsIgnoreCase(userGender)) {
            leaveTypes.put(code, entry.getValue());
        }
    } else if (code.equals("PATERNITY") || code.equals("PATERNITY_LEAVE")) {
        if ("MALE".equalsIgnoreCase(userGender)) {
            leaveTypes.put(code, entry.getValue());
        }
    } else {
        // All other types available for everyone
        leaveTypes.put(code, entry.getValue());
    }
}
```

#### Filtered Data:

- **Leave Types** dropdown
- **Leave Type Rules** (cho validation phía client)
- **Leave Balances** (số dư nghỉ phép)

---

### 🔄 F. Manager Creating OT for Employee

#### Special Workflow:

```
┌─────────────────────────────────────────────────┐
│  Manager creates OT for Employee                │
└─────────────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────────────┐
│  1. Manager selects employee from dropdown      │
│     (Only subordinates appear)                  │
└─────────────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────────────┐
│  2. Verify subordinate relationship             │
│     UserDao.getSubordinates(managerId)          │
│     → Check selectedEmployeeId in list          │
└─────────────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────────────┐
│  3. Call service method:                        │
│     createOTRequestForEmployee()                │
│     (Different from createOTRequest!)           │
└─────────────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────────────┐
│  4. Request created with:                       │
│     • status = "APPROVED" (auto-approved!)      │
│     • current_approver_account_id = manager's   │
│     • detail_json contains:                     │
│       "createdByManager": true                  │
└─────────────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────────────┐
│  5. Request Detail Page:                        │
│     • canApprove = false (already approved)     │
│     • No approve/reject buttons shown           │
└─────────────────────────────────────────────────┘
```

#### Key Differences:

| Aspect | Employee Self-creates | Manager Creates for Employee |
|--------|----------------------|------------------------------|
| Service Method | `createOTRequest()` | `createOTRequestForEmployee()` |
| Initial Status | PENDING | APPROVED |
| Employee Consent | Required (checkbox) | Not required |
| Needs Approval | Yes | No (auto-approved) |
| Detail JSON | `createdByManager: false` | `createdByManager: true` |
| Approver ID | null | manager's account_id |

---

### 📊 G. Data Flow Diagram

#### Create Request Flow:

```
┌──────────┐
│  User    │
│ (Browser)│
└────┬─────┘
     │ 1. GET /requests/leave/create
     ↓
┌────────────────┐
│  Controller    │
│  doGet()       │
└────┬───────────┘
     │ 2. Load form data
     ↓
┌────────────────┐       ┌──────────────┐
│  Service       │←─────→│  DAO         │
│  (Business)    │       │  (Database)  │
└────┬───────────┘       └──────────────┘
     │ 3. Return data
     ↓
┌────────────────┐
│  JSP View      │
│  (Form)        │
└────┬───────────┘
     │ 4. Render form
     ↓
┌──────────┐
│  User    │
│ fills form│
└────┬─────┘
     │ 5. POST /requests/leave/create
     ↓
┌────────────────┐
│  Controller    │
│  doPost()      │
└────┬───────────┘
     │ 6. Validate & create
     ↓
┌────────────────┐       ┌──────────────┐
│  Service       │←─────→│  DAO         │
│  validate()    │       │  insert()    │
│  create()      │       └──────────────┘
└────┬───────────┘              ↓
     │                   ┌──────────────┐
     │                   │   Database   │
     │                   │  (requests)  │
     │                   └──────────────┘
     │ 7. Handle attachments
     ↓
┌────────────────┐       ┌──────────────┐
│ AttachmentSvc  │←─────→│ Filesystem   │
│  saveFiles()   │       │ /assets/img/ │
└────┬───────────┘       └──────────────┘
     │                          ↓
     │                   ┌──────────────┐
     │                   │   Database   │
     │                   │(attachments) │
     │                   └──────────────┘
     │ 8. Return success/error
     ↓
┌────────────────┐
│  JSP View      │
│  (with msg)    │
└────┬───────────┘
     │ 9. Show result
     ↓
┌──────────┐
│  User    │
└──────────┘
```

#### Approve Request Flow (AJAX):

```
┌──────────┐
│  User    │
│ (Browser)│
└────┬─────┘
     │ 1. Click Approve/Reject button
     │    JavaScript AJAX call
     ↓
┌────────────────┐
│  JavaScript    │
│  fetch() API   │
└────┬───────────┘
     │ 2. POST /requests/approve
     │    {action, requestId, reason}
     ↓
┌────────────────┐
│  Controller    │
│  doPost()      │
└────┬───────────┘
     │ 3. Check permissions
     ↓
┌────────────────┐       ┌──────────────┐
│ PermissionHelp │←─────→│  DAO         │
│ canApprove()   │       │  (positions, │
└────┬───────────┘       │   users)     │
     │                   └──────────────┘
     │ 4. Update request
     ↓
┌────────────────┐       ┌──────────────┐
│  DAO           │──────→│   Database   │
│  update()      │       │  UPDATE      │
└────┬───────────┘       │  requests    │
     │                   │  SET status  │
     │                   └──────────────┘
     │ 5. Return JSON
     ↓
┌────────────────┐
│  Controller    │
│  {success:true}│
└────┬───────────┘
     │ 6. JSON response
     ↓
┌────────────────┐
│  JavaScript    │
│  response      │
└────┬───────────┘
     │ 7. Update UI
     │    - Change status badge
     │    - Show success message
     │    - Reload if needed
     ↓
┌──────────┐
│  User    │
│ (sees    │
│  result) │
└──────────┘
```

---

### 📝 H. Database Schema Summary

#### Core Tables:

**requests:**
```sql
CREATE TABLE requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    department_id BIGINT,
    request_type_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,  -- PENDING, APPROVED, REJECTED, CANCELLED
    reason TEXT,
    detail_json JSON,             -- Type-specific data
    current_approver_account_id BIGINT,
    approve_reason TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_created (created_at),
    INDEX idx_type (request_type_id)
);
```

**request_types:**
```sql
CREATE TABLE request_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,  -- LEAVE_REQUEST, OVERTIME_REQUEST, etc.
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE
);
```

**leave_types:**
```sql
CREATE TABLE leave_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,  -- ANNUAL_LEAVE, SICK_LEAVE, etc.
    name VARCHAR(100) NOT NULL,
    max_days_per_year INT,
    requires_approval BOOLEAN DEFAULT TRUE,
    deducts_from_balance BOOLEAN DEFAULT TRUE,
    gender_restricted VARCHAR(10)  -- 'MALE', 'FEMALE', NULL
);
```

---

### 🔍 I. Testing & Debugging Tips

#### Common Issues:

1. **Session Lost:**
   - Check filter chain: AuthenticationFilter
   - Verify session timeout in web.xml
   - Check browser cookies

2. **Permission Denied:**
   - Log position.getJobLevel()
   - Verify user.getPositionId() not null
   - Check subordinate relationship

3. **Attachment Upload Failed:**
   - Check directory permissions
   - Verify max file size in @MultipartConfig
   - Check Content-Type header

4. **Date Parsing Errors:**
   - Ensure frontend sends yyyy-MM-dd format
   - Check timezone consistency
   - Validate date range (start <= end)

#### Logging Strategy:

```java
// Start of operation
logger.info("=== OPERATION_NAME START ===");
logger.info("User: " + user.getId() + ", Request: " + requestId);

// Success
logger.info("Operation completed successfully: " + result);

// Warning (recoverable)
logger.warning("Non-critical issue: " + issue);

// Error (critical)
logger.severe("Critical error: " + error);
e.printStackTrace();
```

---

## 📚 Tài liệu tham khảo

### Source Files:

**Controllers:**
- `LeaveRequestController.java`
- `OTRequestController.java`
- `RequestListController.java`
- `RequestDetailController.java`
- `ApproveRequestController.java`

**Services:**
- `LeaveRequestService.java`
- `OTRequestService.java`
- `RequestListService.java`
- `AttachmentService.java`

**DAOs:**
- `RequestDao.java`
- `RequestTypeDao.java`
- `LeaveTypeDao.java`
- `UserDao.java`
- `PositionDao.java`
- `AttachmentDao.java`

**Helpers:**
- `RequestListPermissionHelper.java`

**Views:**
- `/WEB-INF/views/requests/leave-form.jsp`
- `/WEB-INF/views/requests/ot-form.jsp`
- `/WEB-INF/views/requests/request-list.jsp`
- `/WEB-INF/views/requests/request-detail.jsp`

---

## ✅ Checklist cho Developer

Khi làm việc với Request System:

- [ ] Hiểu rõ job level hierarchy và permission logic
- [ ] Biết sự khác biệt giữa manager-created OT và self-created OT
- [ ] Nắm được gender filtering cho leave types
- [ ] Hiểu attachment system (file vs link)
- [ ] Nắm được error handling strategy
- [ ] Biết cách preserve form data khi có lỗi
- [ ] Hiểu soft delete vs hard delete
- [ ] Nắm được transaction flow (create → attach → approve)
- [ ] Biết các status transitions hợp lệ
- [ ] Hiểu AJAX approval flow

---

**End of Document**
