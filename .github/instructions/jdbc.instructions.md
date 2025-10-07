# Hướng dẫn cho DAO/JDBC

## Kết nối

- Dùng **HikariCP**; cấu hình qua `application.properties` (url, user, pass, poolSize) hoặc JNDI `jdbc/HRMSDS` trong `webapp/META-INF/context.xml`.
- Tạo `DataSource` dùng chung (singleton) trong `DB.java` or `DataSourceFactory`.

## Quy tắc truy vấn

- **PreparedStatement bắt buộc**; đặt tham số theo thứ tự; đóng resource bằng try-with-resources.
- **Mapping** rõ ràng: ResultSet → DTO/Entity.
- Giao dịch (commit/rollback) do lớp Service quản lý.

## Mẫu DAO

```java
public class EmployeeDao {
  private final DataSource ds;
  public EmployeeDao(DataSource ds){ this.ds = ds; }

  public Optional<Employee> findById(long id) throws SQLException {
    String sql = "SELECT id, full_name, email, dept_id FROM employees WHERE id = ?";
    try (Connection c = ds.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Employee e = new Employee();
          e.setId(rs.getLong("id"));
          e.setFullName(rs.getString("full_name"));
          e.setEmail(rs.getString("email"));
          e.setDeptId(rs.getLong("dept_id"));
          return Optional.of(e);
        }
        return Optional.empty();
      }
    }
  }
}
```
