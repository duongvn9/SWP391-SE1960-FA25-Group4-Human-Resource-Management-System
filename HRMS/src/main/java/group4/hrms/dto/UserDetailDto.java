package group4.hrms.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

/**
 * DTO cho User Detail display
 * Chứa đầy đủ thông tin user để hiển thị trong modal
 */
public class UserDetailDto {
    private Long id;
    private String employeeCode;
    private String fullName;
    private String phone;
    private String emailCompany;
    private Long departmentId;
    private String departmentName;
    private Long positionId;
    private String positionName;
    private String dateJoined;
    private String startWorkDate;
    private String gender;
    private String status;
    private String createdAt;
    private String updatedAt;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Constructors
    public UserDetailDto() {
    }

    /**
     * Constructor để map từ ResultSet
     * 
     * @param rs ResultSet từ query
     * @throws SQLException nếu có lỗi khi đọc ResultSet
     */
    public UserDetailDto(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.employeeCode = rs.getString("employee_code");
        this.fullName = rs.getString("full_name");
        this.phone = rs.getString("phone");
        this.emailCompany = rs.getString("email_company");

        long deptId = rs.getLong("department_id");
        if (!rs.wasNull()) {
            this.departmentId = deptId;
        }
        this.departmentName = rs.getString("department_name");

        long posId = rs.getLong("position_id");
        if (!rs.wasNull()) {
            this.positionId = posId;
        }
        this.positionName = rs.getString("position_name");

        this.gender = rs.getString("gender");
        this.status = rs.getString("status");

        // Format dates
        Date dateJoinedSql = rs.getDate("date_joined");
        if (dateJoinedSql != null) {
            this.dateJoined = dateJoinedSql.toLocalDate().format(DATE_FORMATTER);
        }

        Date startWorkDateSql = rs.getDate("start_work_date");
        if (startWorkDateSql != null) {
            this.startWorkDate = startWorkDateSql.toLocalDate().format(DATE_FORMATTER);
        }

        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            this.createdAt = createdAtTs.toLocalDateTime().format(DATETIME_FORMATTER);
        }

        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        if (updatedAtTs != null) {
            this.updatedAt = updatedAtTs.toLocalDateTime().format(DATETIME_FORMATTER);
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmailCompany() {
        return emailCompany;
    }

    public void setEmailCompany(String emailCompany) {
        this.emailCompany = emailCompany;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getStartWorkDate() {
        return startWorkDate;
    }

    public void setStartWorkDate(String startWorkDate) {
        this.startWorkDate = startWorkDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
