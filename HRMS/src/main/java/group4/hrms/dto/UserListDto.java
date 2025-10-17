package group4.hrms.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;

/**
 * DTO cho User List display
 * Chứa thông tin user với department và position names
 */
public class UserListDto {
    private Long id;
    private String employeeCode;
    private String fullName;
    private String emailCompany;
    private String departmentName;
    private String positionName;
    private String status;
    private LocalDate dateJoined;
    private boolean hasActiveAccounts;

    // Constructors
    public UserListDto() {
    }

    /**
     * Constructor để map từ ResultSet
     * 
     * @param rs ResultSet từ query
     * @throws SQLException nếu có lỗi khi đọc ResultSet
     */
    public UserListDto(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.employeeCode = rs.getString("employee_code");
        this.fullName = rs.getString("full_name");
        this.emailCompany = rs.getString("email_company");
        this.departmentName = rs.getString("department_name");
        this.positionName = rs.getString("position_name");
        this.status = rs.getString("status");

        Date dateJoinedSql = rs.getDate("date_joined");
        if (dateJoinedSql != null) {
            this.dateJoined = dateJoinedSql.toLocalDate();
        }

        // Check if has_active_accounts column exists in result set
        try {
            int activeAccountCount = rs.getInt("active_account_count");
            this.hasActiveAccounts = !rs.wasNull() && activeAccountCount > 0;
        } catch (SQLException e) {
            // Column doesn't exist, default to false
            this.hasActiveAccounts = false;
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

    public String getEmailCompany() {
        return emailCompany;
    }

    public void setEmailCompany(String emailCompany) {
        this.emailCompany = emailCompany;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(LocalDate dateJoined) {
        this.dateJoined = dateJoined;
    }

    public boolean isHasActiveAccounts() {
        return hasActiveAccounts;
    }

    public void setHasActiveAccounts(boolean hasActiveAccounts) {
        this.hasActiveAccounts = hasActiveAccounts;
    }

    @Override
    public String toString() {
        return "UserListDto{" +
                "id=" + id +
                ", employeeCode='" + employeeCode + '\'' +
                ", fullName='" + fullName + '\'' +
                ", emailCompany='" + emailCompany + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", positionName='" + positionName + '\'' +
                ", status='" + status + '\'' +
                ", dateJoined=" + dateJoined +
                ", hasActiveAccounts=" + hasActiveAccounts +
                '}';
    }
}
