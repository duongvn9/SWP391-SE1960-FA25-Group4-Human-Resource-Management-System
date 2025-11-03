# Application Approval System

## Tổng quan

Hệ thống duyệt đơn ứng tuyển được thiết kế với quy trình 2 cấp:

1. **HR Review**: HR xem xét và chuyển đơn sang trạng thái "reviewing" hoặc từ chối
2. **HRM Final Approval**: HRM duyệt cuối cùng (approved/rejected) cho các đơn đã được HR review

## Cấu trúc Database

### Bảng `applications` - Các trường approval mới:

```sql
-- HR Approval fields
hr_approver_id BIGINT NULL           -- ID của HR đã duyệt
hr_approver_name VARCHAR(255) NULL   -- Tên HR đã duyệt  
hr_approval_status VARCHAR(24) NULL  -- approved, rejected
hr_approval_note TEXT NULL           -- Ghi chú của HR
hr_approval_date DATETIME NULL       -- Ngày HR duyệt

-- HRM Approval fields
hrm_approver_id BIGINT NULL          -- ID của HRM đã duyệt cuối cùng
hrm_approver_name VARCHAR(255) NULL  -- Tên HRM đã duyệt
hrm_approval_status VARCHAR(24) NULL -- approved, rejected  
hrm_approval_note TEXT NULL          -- Ghi chú của HRM
hrm_approval_date DATETIME NULL      -- Ngày HRM duyệt
```

### Lưu trữ thông tin duyệt:

Thông tin duyệt được lưu trực tiếp trong bảng `applications` thông qua các trường HR/HRM approval. Không cần bảng riêng để lưu lịch sử.

## Quy trình Approval

### Trạng thái đơn ứng tuyển:

- **new**: Đơn mới, chưa được xem xét
- **reviewing**: Đã được HR duyệt, chờ HRM duyệt cuối cùng  
- **approved**: Đã được HRM duyệt cuối cùng
- **rejected**: Bị từ chối (có thể ở bất kỳ giai đoạn nào)

### Quyền hạn theo role:

#### HR (Human Resources):
- Xem tất cả đơn ứng tuyển
- Duyệt đơn từ "new" → "reviewing" 
- Từ chối đơn từ "new" → "rejected"

#### HRM (Human Resources Manager):
- Xem tất cả đơn ứng tuyển
- Duyệt cuối cùng từ "reviewing" → "approved"
- Từ chối từ "reviewing" → "rejected"
- Có thể duyệt trực tiếp từ "new" → "approved" (bypass HR)

## Cách chạy Migration

1. **Thêm các trường approval vào bảng applications:**
   ```bash
   mysql -u username -p hrms < db-script/add-application-approval-columns.sql
   ```

2. **Không cần tạo bảng riêng** - Thông tin approval được lưu trong bảng `applications`

## API Methods mới trong ApplicationDao

### Cập nhật Approval:
- `updateHrApproval()` - Cập nhật HR approval
- `updateHrmApproval()` - Cập nhật HRM approval  
- `updateStatusWithApproval()` - Cập nhật cả status và approval trong transaction

### Truy vấn:
- `findPendingHrApproval()` - Đơn chờ HR duyệt
- `findPendingHrmApproval()` - Đơn chờ HRM duyệt cuối cùng
- `findByHrApprover()` - Đơn đã được HR cụ thể duyệt
- `findByHrmApprover()` - Đơn đã được HRM cụ thể duyệt
- `countByApprovalStatus()` - Đếm theo trạng thái approval

## Giao diện Web

Trang `/applications` hiển thị:
- Danh sách tất cả đơn ứng tuyển với phân trang
- Bộ lọc theo trạng thái và tìm kiếm
- Nút duyệt/từ chối tùy theo quyền hạn của user
- Modal xác nhận với trường ghi chú

## Lưu ý

- Thông tin approval được lưu trực tiếp trong bảng `applications`
- Sử dụng transaction để đảm bảo tính nhất quán dữ liệu
- Các trường approval có thể NULL để tương thích với dữ liệu cũ
- Hệ thống hỗ trợ cả quy trình tuần tự (HR → HRM) và bypass (HRM trực tiếp)
- Đơn giản hóa cấu trúc database bằng cách không sử dụng bảng lịch sử riêng