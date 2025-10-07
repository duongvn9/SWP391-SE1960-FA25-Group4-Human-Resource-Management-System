# Hướng dẫn UI/Bootstrap

## Chuẩn giao diện

- Dùng **Bootstrap 5.3+**, theme sáng/tối theo biến CSS (nếu có).
- Kích thước container: `container` (mặc định), dùng `container-fluid` cho dashboard.
- Form: sử dụng `form-label`, `form-control`, `is-invalid`/`invalid-feedback`.
- Table: `table table-striped table-hover table-bordered`, có thead cố định nếu danh sách dài.

## Mẫu thành phần

- **Navbar**: sticky-top, có brand, menu theo role.
- **Sidebar** (tùy chọn): collapse trên mobile.
- **Modal**: xác nhận xóa, xem chi tiết nhanh.
- **Toast/Alert**: thông báo thêm/sửa/xóa.

## Khả năng truy cập

- Gắn `aria-*`, `alt` cho ảnh; contrast đạt AA; focus-state rõ ràng.
