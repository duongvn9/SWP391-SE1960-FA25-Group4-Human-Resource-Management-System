# Translation Status - Vietnamese to English

## ✅ Completed Files

### Layout Files
- [x] `layout/head.jsp` - Meta tags and title updated to English
- [x] `layout/header.jsp` - Navigation menu translated to English
- [x] `layout/footer.jsp` - Footer content translated to English
- [x] `layout/sidebar.jsp` - Sidebar menu translated to English
- [x] `layout/dashboard-header.jsp` - User dropdown menu translated
- [x] `layout/dashboard-footer.jsp` - Footer text translated
- [x] All files: Changed `lang="vi"` to `lang="en"`

### GitHub Instructions
- [x] `.github/copilot-instructions.md` - Updated to require English web content while keeping Vietnamese communication

## ⏳ Remaining Files to Translate

### Main Pages

#### 1. `home/landing.jsp` (Landing Page)
**Current:** Tiếng Việt
**Needs Translation:**
- Hero section: "Quản lý Nhân sự Thông minh & Hiệu quả" → "Smart & Efficient HR Management"
- Buttons: "Vào Dashboard", "Đăng nhập ngay", "Tìm hiểu thêm"
- Features section titles and descriptions
- Stats section labels
- Services section content
- CTA section

**Priority:** HIGH (Main entry point)

#### 2. `contact.jsp` (Contact Page)
**Current:** Tiếng Việt
**Needs Translation:**
- Hero: "Liên hệ với chúng tôi"
- Contact info labels
- Form fields and placeholders
- Subject dropdown options
- Button text

**Priority:** HIGH (User-facing)

#### 3. `auth/login.jsp` (Login Page)
**Current:** Tiếng Việt
**Needs Translation:**
- "Đăng nhập HRMS" → "Login to HRMS"
- "Hệ thống Quản lý Nhân sự" → "Human Resource Management System"
- Form labels: "Tên đăng nhập", "Mật khẩu"
- Placeholders
- "Đăng nhập với Google"
- "Về trang chủ"

**Priority:** CRITICAL (Authentication)

#### 4. `dashboard/dashboard.jsp` (Dashboard)
**Current:** Tiếng Việt
**Needs Translation:**
- Welcome message
- Stat cards labels
- Quick actions for admin
- Chart titles
- Notifications
- Activity table headers and content

**Priority:** CRITICAL (Main dashboard)

#### 5. `profile/profile.jsp` (Profile Page)
**Current:** Tiếng Việt
**Needs Translation:**
- "Hồ sơ cá nhân" → "Profile"
- Form labels
- Badge text
- Links and buttons
- "Quay về Dashboard" → "Back to Dashboard"

**Priority:** MEDIUM

#### 6. `profile/change-password.jsp` (Change Password)
**Current:** Tiếng Việt  
**Needs Translation:**
- "Đổi mật khẩu" → "Change Password"
- Form labels and placeholders
- Password requirements text
- Security tips section
- Button text

**Priority:** MEDIUM

#### 7. `attendance/attendance-record-emp.jsp` (Employee Attendance)
**Current:** Mixed (already has some English)
**Needs Translation:**
- Filter labels
- Table headers  
- Status values
- Popup labels
- Comments: "Quên chấm công buổi sáng, đã bổ sung"

**Priority:** LOW (Mostly English already)

#### 8. `attendance/attendance-record-HR.jsp` (HR Attendance)
**Current:** Mixed (already has some English)
**Needs Translation:**
- Filter section labels
- Action button texts
- Table headers
- Popup labels
- Vietnamese comments: "Nhân viên", "Phòng ban"

**Priority:** LOW (Mostly English already)

## 📝 Quick Translation Reference

### Common Terms
| Vietnamese | English |
|------------|---------|
| Trang chủ | Home |
| Đăng nhập | Login |
| Đăng xuất | Logout |
| Dashboard | Dashboard |
| Quản lý Nhân viên | Employee Management |
| Chấm công | Attendance |
| Bảng lương | Payroll |
| Đơn từ | Requests |
| Xin nghỉ phép | Leave Request |
| Xin làm thêm | Overtime Request |
| Khiếu nại chấm công | Attendance Appeal |
| Quản lý tài khoản | Account Management |
| Báo cáo | Reports |
| Cài đặt hệ thống | System Settings |
| Thông tin cá nhân | Profile |
| Đổi mật khẩu | Change Password |
| Về trang chủ | Back to Home |
| Liên hệ | Contact |
| Giới thiệu | About |
| Tính năng | Features |
| Hỗ trợ | Support |
| Thành công | Success |
| Lỗi | Error |
| Lưu | Save |
| Hủy | Cancel |
| Gửi | Submit/Send |
| Xác nhận | Confirm |
| Tìm kiếm | Search |
| Lọc | Filter |
| Xuất | Export |
| Nhập | Import |

### Form Fields
| Vietnamese | English |
|------------|---------|
| Họ và tên | Full Name |
| Email | Email |
| Số điện thoại | Phone Number |
| Mật khẩu | Password |
| Mật khẩu mới | New Password |
| Xác nhận mật khẩu | Confirm Password |
| Tên đăng nhập | Username |
| Địa chỉ | Address |
| Phòng ban | Department |
| Chức vụ | Position |
| Nội dung | Message/Content |
| Chủ đề | Subject |
| Ghi chú | Notes |
| Trạng thái | Status |

### Status Values
| Vietnamese | English |
|------------|---------|
| Đã duyệt | Approved |
| Chờ duyệt | Pending |
| Từ chối | Rejected |
| Hoàn thành | Completed |
| Đang xử lý | In Progress |
| Thành công | Success |
| Thất bại | Failed |
| Có mặt | Present |
| Vắng mặt | Absent |
| Muộn | Late |
| Nghỉ phép | On Leave |

## 🎯 Next Steps

1. **Translate landing.jsp** - Most visible page
2. **Translate login.jsp** - Critical for user access
3. **Translate dashboard.jsp** - Main interface
4. **Translate contact.jsp** - User interaction
5. **Translate profile pages** - User settings
6. **Clean up attendance pages** - Remove remaining Vietnamese

## 📌 Notes

- All **comments in code** should be in English or kept minimal
- All **user-facing text** must be in English
- **Communication with AI** remains in Vietnamese
- **Error messages** from backend should also be in English
- Consider creating `messages_en.properties` for centralized strings in future
