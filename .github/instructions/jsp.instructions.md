# Hướng dẫn cho tệp JSP

## Quy tắc vàng

- **Không dùng scriptlet** (`<% %>`). Chỉ dùng EL `${}` và JSTL (`c`, `fmt`, `fn`).
- Dùng **Bootstrap 5** cho layout, grid, form, table, modal.
- Thành phần lặp (menu/sidebar/header/footer) đặt trong `views/layout/*.jsp`, trang con include:
  `<jsp:include page="/WEB-INF/views/layout/header.jsp" />`.

## Cấu trúc trang mẫu

- Title ngắn gọn; breadcrumb nếu cần.
- Form có `csrfToken`, hiển thị thông báo lỗi dưới mỗi input.
- Bảng dữ liệu: có phân trang, sắp xếp, ô trống hiển thị “—”.

## Template mẫu

```jsp
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />
<div class="container my-4">
  <h1 class="h4 mb-3">${pageTitle}</h1>

  <c:if test="${not empty success}">
    <div class="alert alert-success">${success}</div>
  </c:if>
  <c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
  </c:if>

  <!-- Nội dung chính -->
  <form method="post" action="${pageContext.request.contextPath}/employee/create">
    <input type="hidden" name="csrfToken" value="${csrfToken}"/>
    <!-- ví dụ field -->
    <div class="mb-3">
      <label class="form-label">Full name</label>
      <input name="fullName" class="form-control" value="${param.fullName}" required />
      <c:if test="${not empty errors.fullName}">
        <div class="text-danger small">${errors.fullName}</div>
      </c:if>
    </div>
    <button class="btn btn-primary">Save</button>
  </form>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp" />

- Khi sử dụng jsp thì sẽ cần tách các phần code có thể tái sử dụng như header, footer và sidebar để có thể tiện cho việc tái sử dụng
- Khi sử dụng css thì cũng cần tách ra file riêng rồi import để tránh làm code trở nên quá dài (file css này chỉ chứa code cho trang jsp của nó thôi không đc chứa gộp của nhiều trang jsp)
'''
```
