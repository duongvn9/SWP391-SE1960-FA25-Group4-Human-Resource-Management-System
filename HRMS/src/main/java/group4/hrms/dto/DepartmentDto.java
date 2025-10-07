package group4.hrms.dto;

/**
 * DTO cho Department entity
 * Dùng để truyền dữ liệu phòng ban giữa các tầng
 */
public class DepartmentDto {
    private Long id;
    private String name;
    private String description;
    private Long headAccountId;
    private String headAccountName;
    private int employeeCount;
    private Integer totalEmployees;

    // Constructors
    public DepartmentDto() {}

    public DepartmentDto(String name) {
        this.name = name;
    }

    public DepartmentDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Static factory methods
    public static DepartmentDto createNew(String name) {
        return new DepartmentDto(name);
    }
    
    public static DepartmentDto fromDepartment(group4.hrms.model.Department department) {
        DepartmentDto dto = new DepartmentDto();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setHeadAccountId(department.getHeadAccountId());
        return dto;
    }

    public static DepartmentDto createWithId(Long id, String name) {
        return new DepartmentDto(id, name);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getHeadAccountId() {
        return headAccountId;
    }

    public void setHeadAccountId(Long headAccountId) {
        this.headAccountId = headAccountId;
    }

    public String getHeadAccountName() {
        return headAccountName;
    }

    public void setHeadAccountName(String headAccountName) {
        this.headAccountName = headAccountName;
    }

    public Integer getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(Integer totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(int employeeCount) {
        this.employeeCount = employeeCount;
    }

    // Business methods
    public boolean hasHead() {
        return this.headAccountId != null;
    }

    public String getDisplayInfo() {
        StringBuilder info = new StringBuilder(this.name);
        if (hasHead()) {
            info.append(" (Trưởng phòng: ").append(this.headAccountName).append(")");
        }
        if (this.totalEmployees != null) {
            info.append(" - ").append(this.totalEmployees).append(" nhân viên");
        }
        return info.toString();
    }

    @Override
    public String toString() {
        return "DepartmentDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", headAccountId=" + headAccountId +
                ", headAccountName='" + headAccountName + '\'' +
                ", totalEmployees=" + totalEmployees +
                '}';
    }
}