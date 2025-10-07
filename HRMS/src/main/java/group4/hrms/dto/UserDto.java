package group4.hrms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO cho User entity
 * Dùng để truyền dữ liệu user giữa các tầng
 */
public class UserDto {
    private Long id;
    private String employeeCode;
    private String fullName;
    private String firstName;
    private String lastName;
    private String cccd;
    private String emailCompany;
    private String email;
    private String phone;
    private Long departmentId;
    private String departmentName;
    private Long positionId;
    private String positionName;
    private String status;
    private LocalDate dateJoined;
    private LocalDate startWorkDate;
    private LocalDate birthDate;
    private String address;
    private BigDecimal baseSalary;
    private String salaryCurrency;
    
    // Account related fields
    private Long accountId;
    private String username;

    // Constructors
    public UserDto() {}

    public UserDto(String fullName, String emailCompany) {
        this.fullName = fullName;
        this.emailCompany = emailCompany;
    }

    // Static factory methods
    public static UserDto createNew(String fullName, String emailCompany, Long departmentId, Long positionId) {
        UserDto dto = new UserDto();
        dto.setFullName(fullName);
        dto.setEmailCompany(emailCompany);
        dto.setDepartmentId(departmentId);
        dto.setPositionId(positionId);
        dto.setStatus("active");
        return dto;
    }
    
    public static UserDto fromUser(group4.hrms.model.User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmployeeCode(user.getEmployeeCode());
        dto.setFullName(user.getFullName());
        dto.setEmailCompany(user.getEmailCompany());
        dto.setPhone(user.getPhone());
        dto.setDepartmentId(user.getDepartmentId());
        dto.setPositionId(user.getPositionId());
        dto.setStatus(user.getStatus());
        dto.setDateJoined(user.getDateJoined());
        dto.setStartWorkDate(user.getStartWorkDate());
        dto.setBaseSalary(user.getBaseSalary());
        dto.setSalaryCurrency(user.getSalaryCurrency());
        return dto;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Business methods
    public boolean isActive() {
        return "active".equals(this.status);
    }

    public String getDisplayName() {
        return this.employeeCode != null ? 
               this.employeeCode + " - " + this.fullName : 
               this.fullName;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", employeeCode='" + employeeCode + '\'' +
                ", fullName='" + fullName + '\'' +
                ", emailCompany='" + emailCompany + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", positionName='" + positionName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}