# Leave Request Validation Rules

## Overview

This document describes the comprehensive validation rules implemented for the Leave Request feature in the HRMS system. These validations ensure data integrity, prevent conflicts, and provide a better user experience.

## Validation Rules

### 1. Overlap Detection (Requirement 1)

**Purpose**: Prevent employees from creating overlapping leave requests.

**Rule**: The system checks if there are any existing leave requests (PENDING or APPROVED status) that overlap with the requested date range.

**Validation Logic**:
- Checks both PENDING and APPROVED requests
- Detects full overlap (exact same dates)
- Detects partial overlap (any date range intersection)
- Excludes the current request when updating (to avoid self-conflict)

**Error Message Format**:
```
Đơn nghỉ phép trùng với đơn khác: [Leave Type] ([Status]) từ [Start Date] đến [End Date]
```

**Example Scenarios**:

| Scenario | Existing Request | New Request | Result |
|----------|-----------------|-------------|---------|
| Full Overlap | Jan 10-15 (APPROVED) | Jan 10-15 | ❌ Rejected |
| Partial Overlap (Start) | Jan 10-15 (PENDING) | Jan 8-12 | ❌ Rejected |
| Partial Overlap (End) | Jan 10-15 (APPROVED) | Jan 13-18 | ❌ Rejected |
| No Overlap | Jan 10-15 (APPROVED) | Jan 20-25 | ✅ Allowed |

**Implementation**:
- Method: `checkLeaveOverlap()` in `LeaveRequestService`
- DAO Method: `findByUserIdAndDateRange()` in `RequestDao`

---

### 2. Leave Balance Validation (Requirement 2)

**Purpose**: Ensure employees don't exceed their allocated leave days.

**Rule**: The system calculates remaining leave days and rejects requests that exceed the available balance.

**Calculation Formula**:
```
Remaining Days = (Default Days + Seniority Bonus) - Used Days
```

Where:
- **Default Days**: Base allocation from leave type configuration
- **Seniority Bonus**: Additional days based on years of service (future enhancement)
- **Used Days**: Sum of APPROVED leave requests in the current year

**Validation Logic**:
- Only counts APPROVED requests (PENDING requests don't reduce balance)
- Filters by leave type and year
- Skips validation for unlimited leave types (default_days = null or 0)

**Error Message Format**:
```
Không đủ số ngày nghỉ phép. Còn lại: [X] ngày, đã dùng: [Y] ngày, xin nghỉ: [Z] ngày
```

**Example Scenarios**:

| Leave Type | Default Days | Used Days | Requested Days | Result |
|------------|--------------|-----------|----------------|---------|
| Annual Leave | 12 | 5 | 3 | ✅ Allowed (7 remaining) |
| Annual Leave | 12 | 10 | 5 | ❌ Rejected (only 2 remaining) |
| Sick Leave | 10 | 10 | 1 | ❌ Rejected (0 remaining) |
| Unpaid Leave | Unlimited | N/A | 10 | ✅ Allowed (no limit) |

**Implementation**:
- Method: `validateLeaveBalance()` in `LeaveRequestService`
- Method: `calculateUsedDays()` in `LeaveRequestService`

---

### 3. Pending Request Check (Requirement 3)

**Purpose**: Warn employees about existing pending requests in the same date range.

**Rule**: The system checks for PENDING requests and logs a warning, but still allows the new request to be created.

**Validation Logic**:
- Queries only PENDING requests in the date range
- Logs warning message to system log
- Does NOT block the request creation
- Useful for preventing accidental duplicate submissions

**Warning Message Format**:
```
Warning: User [ID] already has a pending leave request: [Leave Type] (PENDING) from [Start Date] to [End Date]. Creating new request anyway.
```

**Behavior**:
- ⚠️ Warning logged (not shown to user)
- ✅ Request creation proceeds normally
- 📝 Helps identify potential duplicate submissions in logs

**Implementation**:
- Method: `findPendingLeaveInRange()` in `LeaveRequestService`
- Called before saving the request

---

### 4. OT Conflict Detection (Requirement 4)

**Purpose**: Prevent leave requests on days with approved overtime requests.

**Rule**: The system rejects leave requests if there's an APPROVED OT request on any day within the leave period.

**Validation Logic**:
- Queries APPROVED OT requests in the date range
- Parses OT date from JSON detail
- Checks for any date intersection
- Only considers APPROVED OT requests (PENDING OT doesn't block)

**Error Message Format**:
```
Không thể xin nghỉ phép trong ngày đã có đơn OT được duyệt: [OT Date] ([Hours] giờ, [Start Time]-[End Time])
```

**Example Scenarios**:

| Leave Request | OT Request | Result |
|---------------|------------|---------|
| Jan 10-15 | Jan 12 (APPROVED, 2h) | ❌ Rejected |
| Jan 10-15 | Jan 12 (PENDING, 2h) | ✅ Allowed |
| Jan 10-15 | Jan 20 (APPROVED, 3h) | ✅ Allowed |

**Rationale**: An employee cannot be on leave and work overtime on the same day.

**Implementation**:
- Method: `checkConflictWithOT()` in `LeaveRequestService`
- DAO Method: `findOTRequestsByUserIdAndDateRange()` in `RequestDao`

---

### 5. Weekend and Holiday Validation (Requirement 5)

**Purpose**: Accurately calculate working days by excluding weekends and holidays.

**Rule**: The system automatically excludes Saturdays, Sundays, and public holidays when calculating leave days.

**Calculation Logic**:
```java
for each day in (startDate to endDate):
    if day is Monday-Friday AND day is not a holiday:
        workingDays++
```

**Display Fields**:
- **Total Days**: Calendar days including weekends (e.g., 7 days)
- **Working Days**: Business days only (e.g., 5 days)

**Validation**:
- Max days limit is compared against working days, not total days
- Balance deduction uses working days

**Example**:
```
Request: Jan 10 (Mon) to Jan 16 (Sun)
Total Days: 7 days
Working Days: 5 days (Mon-Fri)
Balance Deduction: 5 days
```

**Implementation**:
- Method: `calculateWorkingDays()` in `LeaveRequestService`
- Uses `HolidayDao` to check public holidays

---

### 6. Informative Error Messages (Requirement 6)

**Purpose**: Provide clear, actionable error messages to users.

**Rule**: All validation errors include specific details to help users understand and resolve the issue.

**Error Message Components**:

1. **Overlap Error**:
   - Conflicting leave type
   - Status of conflicting request
   - Date range of conflict

2. **Balance Error**:
   - Days remaining
   - Days already used
   - Days requested
   - Total allowed days

3. **OT Conflict Error**:
   - OT date
   - OT hours
   - OT time range

**Implementation**:
- Class: `ValidationErrorMessage` (utility class)
- Exception: `LeaveValidationException`
- All error messages in Vietnamese for user-friendliness

---

### 7. Balance Display Enhancement (Requirement 7)

**Purpose**: Show employees their leave balance before submitting requests.

**Rule**: The leave form displays a comprehensive balance summary for all leave types.

**Display Components**:

1. **Balance Summary Card**:
   - Leave type name
   - Total allowed days
   - Used days (APPROVED)
   - Pending days (PENDING)
   - Available days (remaining - pending)

2. **Visual Indicators**:
   - 🟢 Green: Balance > 3 days
   - 🟡 Yellow: Balance 1-3 days (warning badge)
   - 🔴 Red: Balance = 0 (disabled, cannot select)

3. **Progress Bar**:
   - Shows usage percentage
   - Color-coded based on remaining balance

**Balance Calculation**:
```
Total Allowed = Default Days + Seniority Bonus
Used Days = Sum of APPROVED requests in current year
Pending Days = Sum of PENDING requests in current year
Remaining Days = Total Allowed - Used Days
Available Days = Remaining Days - Pending Days
```

**UI Behavior**:
- Balance updates when leave type is selected
- Warning badge appears when balance < 3 days
- Leave type is disabled in dropdown when balance = 0
- Tooltip shows detailed breakdown on hover

**Implementation**:
- DTO: `LeaveBalance` with enhanced fields
- Method: `getLeaveBalance()` in `LeaveRequestService`
- JSP: `leave-form.jsp` with balance display section

---

## Validation Flow

The validations are executed in the following order:

```
1. Basic Field Validation
   ↓
2. Date Logic Validation
   ↓
3. Advance Notice Validation
   ↓
4. Max Days Validation
   ↓
5. Balance Validation (if leave type has limit)
   ↓
6. Overlap Detection
   ↓
7. OT Conflict Check
   ↓
8. Pending Request Warning (non-blocking)
   ↓
9. Save Request
```

**Fail-Fast Approach**: The validation stops at the first error and throws an exception immediately.

---

## Testing

### Test Coverage

The validation rules are covered by comprehensive unit tests in `LeaveRequestServiceTest.java`:

**Overlap Detection Tests**:
- ✅ Full overlap with same dates
- ✅ Partial overlap (start date)
- ✅ Partial overlap (end date)
- ✅ No overlap
- ✅ Overlap with PENDING request
- ✅ Overlap with APPROVED request

**Balance Validation Tests**:
- ✅ Sufficient balance
- ✅ Insufficient balance
- ✅ Unlimited leave type (skip validation)
- ✅ Exact limit (boundary test)

**OT Conflict Tests**:
- ✅ Conflict detected
- ✅ No conflict
- ✅ Conflict on middle day of range

### Running Tests

```bash
cd HRMS
mvn test -Dtest=LeaveRequestServiceTest
```

---

## Database Schema

### Required Tables

1. **requests** - Stores all leave and OT requests
2. **leave_types** - Leave type configurations
3. **holiday_calendar** - Holiday calendar by year
4. **holidays** - Public holiday definitions

### Key Indexes

For optimal performance, ensure these indexes exist:

```sql
-- Index for overlap detection
CREATE INDEX idx_requests_user_dates ON requests(created_by_user_id, created_at, status);

-- Index for OT conflict check
CREATE INDEX idx_requests_type_status ON requests(request_type_id, status);
```

---

## Configuration

### Leave Type Configuration

Each leave type in the `leave_types` table has these validation-related fields:

| Field | Description | Example |
|-------|-------------|---------|
| `default_days` | Annual allocation (null = unlimited) | 12 |
| `max_days` | Maximum consecutive days | 5 |
| `min_advance_notice` | Days of advance notice required | 3 |
| `requires_approval` | Needs manager approval | true |
| `requires_certificate` | Needs medical certificate | false |

---

## Error Handling

### Exception Hierarchy

```
Exception
  └── RuntimeException
        └── IllegalArgumentException
              ├── LeaveValidationException (custom)
              └── Standard validation errors
```

### Controller Error Handling

```java
try {
    leaveRequestService.createLeaveRequest(...);
} catch (LeaveValidationException e) {
    // Display user-friendly error message
    request.setAttribute("error", e.getMessage());
    return "leave-form";
} catch (Exception e) {
    // Log and display generic error
    logger.error("Unexpected error", e);
    request.setAttribute("error", "Đã xảy ra lỗi. Vui lòng thử lại.");
    return "error";
}
```

---

## Future Enhancements

### Planned Improvements

1. **Seniority Bonus Calculation**
   - Automatically calculate based on user's join date
   - Configurable bonus rules (e.g., +1 day per 2 years)

2. **Carry Forward Support**
   - Allow unused days to carry to next year
   - Configurable carry forward limits

3. **Delegation Support**
   - Allow managers to create leave requests for employees
   - Track who created the request

4. **Notification System**
   - Email notifications for validation errors
   - Reminders for pending requests

5. **Advanced Reporting**
   - Leave balance reports
   - Usage analytics
   - Trend analysis

---

## Troubleshooting

### Common Issues

**Issue**: Overlap detection not working
- **Check**: Ensure `findByUserIdAndDateRange()` includes both PENDING and APPROVED statuses
- **Check**: Verify date range query logic in DAO

**Issue**: Balance calculation incorrect
- **Check**: Ensure only APPROVED requests are counted
- **Check**: Verify year filtering in `calculateUsedDays()`

**Issue**: OT conflict not detected
- **Check**: Ensure `findOTRequestsByUserIdAndDateRange()` parses JSON correctly
- **Check**: Verify OT request type ID is correct

---

## References

- Requirements Document: `.kiro/specs/enhance-leave-validation/requirements.md`
- Design Document: `.kiro/specs/enhance-leave-validation/design.md`
- Test Suite: `HRMS/src/test/java/group4/hrms/service/LeaveRequestServiceTest.java`
- Service Implementation: `HRMS/src/main/java/group4/hrms/service/LeaveRequestService.java`

---

**Document Version**: 1.0
**Last Updated**: October 13, 2025
**Author**: HRMS Development Team
