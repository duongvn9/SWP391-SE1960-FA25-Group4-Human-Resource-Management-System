# HRMS – Repository Custom Instructions

## Vai trò & Ngôn ngữ

- Đóng vai **Expert Software Engineer** chuyên: Java Core (Servlet), Maven, JDBC với MySQL, JSP + Bootstrap.
- **Luôn phản hồi và giao tiếp bằng tiếng Việt.**
- **Tất cả nội dung trang web (JSP, HTML, label, placeholder, message, button text, menu items, form fields) phải viết bằng tiếng Anh.**
- **Chú thích code (comment) trong Java/JSP nên bằng tiếng Anh; nếu cần giải thích phức tạp có thể dùng tiếng Việt.**

## Tài liệu ràng buộc

- Chỉ tạo/đổi mới API, bảng DB, JSP, use case… nếu **được mô tả hoặc đánh dấu READY/APPROVED** trong `docs/code-base-temp.md`. Nếu không thấy, **đừng suy đoán**.
  (Khi có `docs/detailed-design.md`, hãy tuân thủ nghiêm ngặt tài liệu đó.)

## Ngăn xếp & Phiên bản

- JDK hiện tại: **Java 17** (theo `HRMS/pom.xml`).
- Servlet API: **Jakarta Servlet 6 (Jakarta EE 10)** – Tomcat 10.1+.
- MySQL **8.x**, charset **utf8mb4**, **InnoDB**.
- Đóng gói **WAR** bằng Maven.

## Kiến trúc & Tổ chức mã

- Mô hình 3 lớp:
  - **Controller**: `…controller` (Servlet, chỉ điều phối).
  - **Service**: `…service` (nghiệp vụ, kiểm tra ràng buộc).
  - **DAO/Repository**: `…dao` (JDBC, PreparedStatement, mapping).
- **Model/Entity/DTO**: `…model` (POJO).
- **Filter**: `…filter` (auth, encoding, csrf).
- **Utils**: `…util` (DB, pagination, validators, hashing).
- **Views (JSP)**: `src/main/webapp/WEB-INF/views`.

## Quy ước code

- Tên package: `group4.hrms.*` (theo cấu trúc hiện tại; chỉ đổi khi có quyết định chính thức).
- Không dùng scriptlet trong JSP. **Chỉ EL + JSTL**.
- Java: tuân thủ chuẩn Google/Checkstyle (camelCase, SCREAMING_SNAKE_CASE cho hằng).
- **Null-safety**: kiểm tra null input/DAO trả về rỗng; không ném NPE.
- **Log** bằng SLF4J; không log dữ liệu nhạy cảm (mật khẩu, token, CCCD).

## JDBC & Kết nối CSDL

- Dùng **PreparedStatement** 100% (chống SQL Injection).
- Ưu tiên **connection pool (HikariCP)**, cấu hình ở `src/main/resources/application.properties` hoặc dùng **JNDI DataSource** trong `webapp/META-INF/context.xml` (ví dụ: `jdbc/HRMSDS`).
- Giao dịch (transaction) ở Service cho các thao tác nhiều bước.
- Mapping ResultSet → DTO rõ ràng; **không** trả ResultSet ra ngoài DAO.

## JSP + Bootstrap

- Giao diện theo **Bootstrap 5.3+**, responsive.
- Tổ chức layout: `WEB-INF/views/layout/header.jsp`, `footer.jsp`, `sidebar.jsp`; trang con include từ layout.
- **Tất cả text trong JSP (title, heading, label, button, menu, placeholder, error message) phải viết bằng tiếng Anh.**
- Thuộc tính `lang` trong HTML nên đặt `lang="en"` thay vì `lang="vi"`.
- Form: có **CSRF token** (ẩn), **server-side validation** là bắt buộc; client-side chỉ hỗ trợ UX.
- JSTL cho Jakarta EE 10: dùng URI `jakarta.tags.core` thay cho `http://java.sun.com/jsp/jstl/core`.

## Bảo mật

- **XSS**: dùng `<c:out>` hoặc escape đầu ra.
- **CSRF**: token per-session; kiểm tra ở Filter.
- **Auth**: đăng nhập lưu `userId`, `role` trong session; **AuthorizationFilter** dựa trên role (HRM/HR/Manager/Employee).
- **Password**: hash bằng **BCrypt**; **không bao giờ** lưu plain text.
- **Rate limiting** cho endpoint nhạy cảm (đăng nhập).

## Chuẩn DB

- Bảng snake_case; PK: `id BIGINT AUTO_INCREMENT`.
- Cột audit: `created_at DATETIME`, `updated_at DATETIME`, timezone `Asia/Ho_Chi_Minh`.
- Script schema ở `src/main/resources/db/schema.sql`, seed ở `seed.sql`.

## Test & Chất lượng

- Tối thiểu viết **unit test** cho Service, **integration test** nhẹ cho DAO (profile test).
- Bật **Checkstyle/SpotBugs**; build fail nếu vi phạm lỗi nghiêm trọng.

## I18N & UX

- Ngôn ngữ giao diện: **en-US** (tiếng Anh). Tất cả nội dung hiển thị trên trang web phải bằng tiếng Anh.
- File message/properties nếu có dùng ở `WEB-INF/i18n`, cũng cần viết bằng tiếng Anh.
- Viết label, placeholder, thông báo lỗi **ngắn gọn, dễ hiểu, bằng tiếng Anh**.

## Do / Don’t

- ✅ Sinh mã bám sát thiết kế, nhỏ gọn, dễ đọc, có log ở mức INFO/ERROR hợp lý.
- ✅ Ưu tiên cấu hình qua `.properties`.
- ❌ Không dùng framework ngoài phạm vi (không Spring/Hibernate) trừ khi tài liệu cho phép.
- ❌ Không hard-code chuỗi kết nối, mật khẩu; dùng biến môi trường hoặc `db.properties`.

## CI/CD

- CI GitHub Actions: build với Maven trong thư mục `HRMS/` dùng **JDK 17** (đồng bộ với `pom.xml`). Khi đổi phiên bản Java, hãy cập nhật cả `pom.xml` và workflow.
