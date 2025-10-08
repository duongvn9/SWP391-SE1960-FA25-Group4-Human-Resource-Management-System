# HRMS Website Translation Guide
# English Web Content Implementation

## Overview
This guide documents the translation of all HRMS web pages from Vietnamese to English while maintaining Vietnamese communication with developers.

## Completed Translations

### 1. Configuration & Layout Files ✅
- **copilot-instructions.md** - Updated AI instructions
- **head.jsp** - Meta tags and page titles
- **header.jsp** - Navigation menu
- **footer.jsp** - Footer links and content
- **sidebar.jsp** - Dashboard sidebar menu
- **dashboard-header.jsp** - User dropdown menu
- **dashboard-footer.jsp** - Footer copyright
- All HTML `lang` attributes changed from "vi" to "en"

## Files Requiring Translation

### Priority 1: Critical Pages

#### A. Landing Page (`home/landing.jsp`)
```
Line 21: Liên hệ với chúng tôi → Contact Us
Line 22: Chúng tôi luôn sẵn sàng → We are always ready
Line 33-63: Hero Section
Line 70-150: Features Section  
Line 160-185: Stats Section
Line 190-250: Services Section
Line 260-270: CTA Section
```

#### B. Login Page (`auth/login.jsp`)
```
Line 27: Về trang chủ → Back to Home
Line 34: Đăng nhập HRMS → Login to HRMS
Line 35: Hệ thống Quản lý Nhân sự → Human Resource Management System
Line 47: Tên đăng nhập → Username
Line 51: Nhập tên đăng nhập → Enter username
Line 56: Mật khẩu → Password
Line 60: Nhập mật khẩu → Enter password
Line 69: Đăng nhập → Login
Line 78: Đăng nhập với Google → Sign in with Google
```

#### C. Dashboard (`dashboard/dashboard.jsp`)
```
Line 19: Chào mừng trở lại → Welcome back
Line 20: Dashboard tổng quan → Dashboard Overview
Line 29: Tổng số nhân viên → Total Employees
Line 38: Có mặt hôm nay → Present Today
Line 47: Nghỉ phép hôm nay → On Leave Today
Line 56: Vắng mặt hôm nay → Absent Today
Line 66-110: Quick Actions Section
Line 120-140: Charts Section
Line 150-180: Notifications Section
Line 190-230: Recent Activities Table
```

### Priority 2: User Pages

#### D. Contact Page (`contact.jsp`)
```
Line 21: Liên hệ với chúng tôi → Contact Us
Line 51-85: Contact Information
Line 95-140: Contact Form
Line 190-210: Map Section
```

#### E. Profile Pages
**profile.jsp:**
```
Line 6: Hồ sơ cá nhân → Profile
Line 37-60: Left card content
Line 70-120: Profile form fields
```

**change-password.jsp:**
```
Line 7: Đổi mật khẩu → Change Password
Line 46-140: Password form
Line 150-180: Password requirements
Line 185-210: Security tips
```

### Priority 3: Attendance Pages (Partial Translation)

#### F. attendance-record-emp.jsp
```
Already mostly in English
Need to translate:
- Line comments
- Any remaining Vietnamese labels
```

#### G. attendance-record-HR.jsp
```
Already mostly in English
Need to translate:
- Vietnamese comments
- Department labels
```

## Translation Implementation Strategy

### Step 1: Replace Large Text Blocks
Use `replace_string_in_file` for sections with 5+ lines of context.

### Step 2: Replace Individual Labels
Focus on form labels, buttons, headings one by one.

### Step 3: Update Dynamic Content
Check for Vietnamese in:
- JavaScript alert() messages
- Console.log() statements
- Data attributes
- Placeholder text

### Step 4: Verify Consistency
- All buttons use consistent terminology
- All status values translated uniformly
- All error/success messages in English

## Common Patterns to Replace

### Hero Sections
```jsp
<h1>Quản lý Nhân sự<br>Thông minh & Hiệu quả</h1>
→
<h1>Smart & Efficient<br>Human Resource Management</h1>
```

### Form Labels
```jsp
<label>Họ và tên</label>
→
<label>Full Name</label>
```

### Buttons
```jsp
<button>Đăng nhập</button>
→
<button>Login</button>
```

### Alerts
```jsp
<div class="alert alert-success">
    <i class="fas fa-check-circle me-2"></i>Thành công
→
<div class="alert alert-success">
    <i class="fas fa-check-circle me-2"></i>Success
```

### Comments
```jsp
<!-- User đã đăng nhập -->
→
<!-- User logged in -->
```

## Quality Checklist

Before considering a file "complete":
- [ ] HTML lang attribute is "en"
- [ ] Page title in English
- [ ] All visible text in English
- [ ] All form labels in English
- [ ] All placeholders in English
- [ ] All buttons in English
- [ ] All alerts/messages in English
- [ ] All tooltips in English
- [ ] All dropdown options in English
- [ ] JavaScript strings in English
- [ ] Comments in English (or minimal)
- [ ] No mixed language content
- [ ] Consistent terminology throughout

## Testing Checklist

After translation:
- [ ] Page loads without errors
- [ ] All text displays correctly
- [ ] Forms submit properly
- [ ] JavaScript functions work
- [ ] No console errors
- [ ] Responsive design intact
- [ ] Accessibility maintained
- [ ] Navigation works
- [ ] Links are correct

## Next Actions

1. Translate landing.jsp (highest priority - main entry point)
2. Translate login.jsp (critical - auth flow)
3. Translate dashboard.jsp (critical - main interface)
4. Translate contact.jsp (high - user interaction)
5. Translate profile pages (medium - user settings)
6. Clean up attendance pages (low - mostly done)
7. Create centralized messages file for future (optional)

## Notes

- Keep backup before major changes
- Test after each file translation
- Commit frequently with descriptive messages
- Update this document as files are completed
- Maintain consistency in terminology across all pages
