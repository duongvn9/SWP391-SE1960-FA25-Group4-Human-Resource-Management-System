package group4.hrms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * User entity - Nhân viên trong công ty
 * Tương ứng với bảng users trong database
 */
public class User {
    private Long id;
    private String employeeCode;
    private String fullName;
    private String cccd;
    private String emailCompany;
    private String phone;
    private String gender;
    private LocalDate dob;
    private Long departmentId;
    private Long positionId;
    private String status;
    private LocalDate dateJoined;
    private LocalDate dateLeft;
    private LocalDate startWorkDate;
    private BigDecimal baseSalary;
    private String salaryCurrency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enums for status
    public enum Status {
        ACTIVE("active"),
        INACTIVE("inactive"),
        TERMINATED("terminated");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Status fromValue(String value) {
            for (Status status : Status.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            return ACTIVE; // default
        }
    }

    // Constructors
    public User() {
    }

    public User(String fullName, String emailCompany) {
        this.fullName = fullName;
        this.emailCompany = emailCompany;
        this.status = Status.ACTIVE.getValue();
    }

    public User(String employeeCode, String fullName, String emailCompany, Long departmentId, Long positionId) {
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.emailCompany = emailCompany;
        this.departmentId = departmentId;
        this.positionId = positionId;
        this.status = Status.ACTIVE.getValue();
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

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getEmailCompany() {
        return emailCompany;
    }

    public void setEmailCompany(String emailCompany) {
        this.emailCompany = emailCompany;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
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

    public LocalDate getDateLeft() {
        return dateLeft;
    }

    public void setDateLeft(LocalDate dateLeft) {
        this.dateLeft = dateLeft;
    }

    public LocalDate getStartWorkDate() {
        return startWorkDate;
    }

    public void setStartWorkDate(LocalDate startWorkDate) {
        this.startWorkDate = startWorkDate;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public String getSalaryCurrency() {
        return salaryCurrency;
    }

    public void setSalaryCurrency(String salaryCurrency) {
        this.salaryCurrency = salaryCurrency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Business methods
    public boolean isActive() {
        return Status.ACTIVE.getValue().equals(this.status);
    }

    public boolean isInactive() {
        return Status.INACTIVE.getValue().equals(this.status);
    }

    public boolean isTerminated() {
        return Status.TERMINATED.getValue().equals(this.status);
    }

    public void activate() {
        this.status = Status.ACTIVE.getValue();
    }

    public void deactivate() {
        this.status = Status.INACTIVE.getValue();
    }

    public void terminate() {
        this.status = Status.TERMINATED.getValue();
        this.dateLeft = LocalDate.now();
    }

    public boolean hasValidSalary() {
        return this.baseSalary != null && this.baseSalary.compareTo(BigDecimal.ZERO) > 0;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(employeeCode, user.employeeCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, employeeCode);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", employeeCode='" + employeeCode + '\'' +
                ", fullName='" + fullName + '\'' +
                ", emailCompany='" + emailCompany + '\'' +
                ", gender='" + gender + '\'' +
                ", status='" + status + '\'' +
                ", departmentId=" + departmentId +
                ", positionId=" + positionId +
                '}';
    }
}