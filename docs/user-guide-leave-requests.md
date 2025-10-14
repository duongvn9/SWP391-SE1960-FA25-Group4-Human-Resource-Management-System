# User Guide: Leave Request Management

## Table of Contents
1. [Overview](#overview)
2. [Creating a Leave Request](#creating-a-leave-request)
3. [Understanding Leave Balance](#understanding-leave-balance)
4. [Common Validation Errors](#common-validation-errors)
5. [Best Practices](#best-practices)
6. [FAQ](#faq)

---

## Overview

The Leave Request Management system allows employees to submit, track, and manage their leave requests. The system includes comprehensive validation to ensure data integrity and prevent conflicts.

### Leave Types

The system supports multiple leave types:

| Leave Type | Default Days/Year | Max Consecutive Days | Advance Notice | Paid |
|------------|-------------------|---------------------|----------------|------|
| Annual Leave | 12 | 5 | 3 days | Yes |
| Sick Leave | 10 | 3 | 0 days | Yes |
| Unpaid Leave | Unlimited | N/A | 7 days | No |
| Maternity Leave | 180 | 180 | 30 days | Yes |
| Paternity Leave | 5 | 5 | 7 days | Yes |

*Note: Actual values may vary based on company policy and your employment contract.*

---

## Creating a Leave Request

### Step-by-Step Guide

1. **Navigate to Leave Request Form**
   - Click on "Requests" in the main menu
   - Select "Create Leave Request"

2. **Select Leave Type**
   - Choose the appropriate leave type from the dropdown
   - Your current balance will be displayed automatically
   - Leave types with 0 balance will be disabled

3. **Choose Dates**
   - **Start Date**: First day of your leave
   - **End Date**: Last day of your leave
   - The system automatically calculates working days (excluding weekends and holidays)

4. **Enter Reason**
   - Provide a clear reason for your leave request
   - Maximum 1000 characters
   - Be specific and professional

5. **Review Balance**
   - Check the balance summary card
   - Ensure you have sufficient days available
   - Note any pending requests that may affect your balance

6. **Submit Request**
   - Click "Submit" to create your request
   - You'll receive confirmation if successful
   - Any validation errors will be displayed clearly

### Form Fields

| Field | Required | Description | Example---|----------|-------------|---------|
| Leave Type | Yes | Type of leave you're requesting | Annual Leave |
| Start Date | Yes | First day of leave | 2025-01-10 |
| End Date | Yes | Last day of leave | 2025-01-15 |
| Reason | Yes | Explanation for leave | Family vacation |

---

## Understanding Leave Balance

### Balance Components

Your leave balance consists of several components:

```
┌─────────────────────────────────────┐
│ Leave Balance Summary               │
├─────────────────────────────────────┤
│ Total Allowed:    12 days           │
│ Used Days:        5 days            │
│ Pending Days:     2 days            │
│ Remaining Days:   7 days            │
│ Available Days:   5 days            │
└─────────────────────────────────────┘
```

**Definitions**:
- **Total Allowed**: Your annual allocation (default + seniority bonus)
- **Used Days**: Days from APPROVED requests this year
- **Pending Days**: Days from PENDING requests (not yet approved)
- **Remaining Days**: Total Allowed - Used Days
- **Available Days**: Remaining Days - Pending Days (what you can actually request)

### Balance Indicators

The system uses color-coded indicators to show your balance status:

- 🟢 **Green** (> 3 days): Healthy balance
- 🟡 **Yellow** (1-3 days): Low balance warning
- 🔴 **Red** (0 days): No balance available (cannot request)

### Viewing Your Balance

1. **On Leave Request Form**:
   - Balance summary appears when you select a leave type
   - Shows real-time calculation

2. **On Dashboard**:
   - View all leave types and their balances
   - See usage trends and history

---

## Common Validation Errors

### 1. Overlapping Leave Requests

**Error Message**:
```
Đơn nghỉ phép trùng với đơn khác: Annual Leave (APPROVED) từ 2025-01-10 đến 2025-01-15
```

**What it means**: You already have a leave request (pending or approved) that overlaps with the dates you're trying to request.

**How to fix**:
- Check your existing requests
- Choose different dates
- Cancel or modify the conflicting request first

**Example**:
```
Existing: Jan 10-15 (APPROVED)
New Request: Jan 12-18 ❌ (overlaps on Jan 12-15)
Alternative: Jan 20-25 ✅ (no overlap)
```

---

### 2. Insufficient Leave Balance

**Error Message**:
```
Không đủ số ngày nghỉ phép. Còn lại: 2 ngày, đã dùng: 10 ngày, xin nghỉ: 5 ngày
```

**What it means**: You don't have enough leave days remaining for your request.

**How to fix**:
- Request fewer days
- Wait until next year when balance resets
- Consider using a different leave type (e.g., Unpaid Leave)

**Example**:
```
Total Allowed: 12 days
Used: 10 days
Remaining: 2 days
Requested: 5 days ❌ (exceeds remaining)
Alternative: 2 days ✅ (within limit)
```

---

### 3. OT Conflict

**Error Message**:
```
Không thể xin nghỉ phép trong ngày đã có đơn OT được duyệt: 2025-01-12 (2.0 giờ, 18:00-20:00)
```

**What it means**: You have an approved overtime request on one of the days you're trying to take leave.

**How to fix**:
- Cancel the OT request first
- Choose different leave dates that don't include the OT day
- Contact your manager to resolve the conflict

**Example**:
```
OT Request: Jan 12 (APPROVED, 2 hours)
Leave Request: Jan 10-15 ❌ (includes Jan 12)
Alternative: Jan 13-18 ✅ (excludes Jan 12)
```

---

### 4. Advance Notice Not Met

**Error Message**:
```
Annual Leave requires at least 3 days advance notice (current notice: 1 days)
```

**What it means**: You're trying to request leave too close to the start date.

**How to fix**:
- Choose a start date further in the future
- Use a leave type with shorter notice requirements (e.g., Sick Leave)
- Contact your manager for emergency situations

**Example**:
```
Today: Jan 10
Requested Start: Jan 12 ❌ (only 2 days notice, need 3)
Alternative Start: Jan 14 ✅ (4 days notice)
```

---

### 5. Exceeds Maximum Days

**Error Message**:
```
Cannot request more than 5 days for Annual Leave (requested: 7 days)
```

**What it means**: Your request exceeds the maximum consecutive days allowed for this leave type.

**How to fix**:
- Split your request into multiple shorter requests
- Use a different leave type with higher limits
- Contact HR for special circumstances

**Example**:
```
Max Consecutive: 5 days
Requested: 7 days ❌
Alternative: Two requests of 3 and 4 days ✅
```

---

### 6. Past Date Request

**Error Message**:
```
Start date cannot be in the past
```

**What it means**: You're trying to request leave for dates that have already passed.

**How to fix**:
- Choose future dates
- For retroactive leave, contact HR directly

---

## Best Practices

### Planning Your Leave

1. **Check Balance First**
   - Review your balance before planning
   - Consider pending requests
   - Account for upcoming needs

2. **Submit Early**
   - Submit requests as early as possible
   - Respect advance notice requirements
   - Give your team time to plan

3. **Avoid Peak Periods**
   - Check team calendar
   - Coordinate with colleagues
   - Consider business needs

4. **Keep Documentation**
   - Save confirmation emails
   - Keep medical certificates (if required)
   - Track your leave history

### Managing Your Balance

1. **Monitor Regularly**
   - Check balance monthly
   - Track usage trends
   - Plan for year-end

2. **Use Strategically**
   - Prioritize important events
   - Balance work and personal needs
   - Consider carry-forward rules

3. **Communicate Clearly**
   - Inform your manager early
   - Provide adequate context
   - Be professional in requests

### Avoiding Conflicts

1. **Check Existing Requests**
   - Review your pending requests
   - Check approved leave
   - Verify OT schedule

2. **Coordinate with Team**
   - Discuss with colleagues
   - Avoid overlapping absences
   - Ensure coverage

3. **Update Promptly**
   - Cancel if plans change
   - Modify dates if needed
   - Keep information current

---

## FAQ

### General Questions

**Q: How often does my leave balance reset?**
A: Leave balance typically resets annually on January 1st. Some leave types may have different reset schedules.

**Q: Can I carry forward unused leave days?**
A: This depends on the leave type and company policy. Check with HR for specific rules.

**Q: What happens to pending requests when balance resets?**
A: Pending requests are evaluated against the balance at the time of approval, not submission.

### Balance Questions

**Q: Why is my available balance different from my remaining balance?**
A: Available balance excludes pending requests. Remaining balance only excludes approved requests.

**Q: Do weekends count towards my leave days?**
A: No, the system automatically excludes weekends and public holidays from the day count.

**Q: Can I request more days than my current balance?**
A: No, the system will reject requests that exceed your available balance.

### Request Questions

**Q: Can I modify a request after submission?**
A: You can cancel and resubmit. Contact your manager if the request is already approved.

**Q: How long does approval take?**
A: This varies by manager and leave type. Check with your manager for typical timelines.

**Q: Can I request leave for past dates?**
A: No, the system only accepts future dates. Contact HR for retroactive leave.

### Conflict Questions

**Q: What if I have an emergency and can't meet advance notice?**
A: Use Sick Leave (0 days notice) or contact your manager directly for emergency situations.

**Q: Can I have overlapping leave and OT requests?**
A: No, you cannot be on leave and work overtime on the same day.

**Q: What if my request is rejected?**
A: Review the rejection reason, make necessary changes, and resubmit. Contact your manager if unclear.

---

## Support

### Need Help?

- **Technical Issues**: Contact IT Support at [it-support@company.com]
- **Policy Questions**: Contact HR at [hr@company.com]
- **Manager Approval**: Contact your direct manager
- **System Bugs**: Report to [dev-team@company.com]

### Additional Resources

- [Leave Policy Document](link-to-policy)
- [HR Portal](link-to-hr-portal)
- [Company Calendar](link-to-calendar)
- [Team Schedule](link-to-schedule)

---

**Document Version**: 1.0
**Last Updated**: October 13, 2025
**For**: HRMS Users
