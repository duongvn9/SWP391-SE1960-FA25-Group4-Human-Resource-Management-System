package group4.hrms.dto;

import group4.hrms.model.Position;

/**
 * DTO cho Position entity
 * Dùng để truyền dữ liệu chức vụ giữa các tầng
 */
public class PositionDto {
    private Long id;
    private String name;
    private String description;
    private Integer level;
    private Double baseSalary;
    private Boolean isActive;
    private Integer employeeCount;
    private String levelName;

    // Constructors
    public PositionDto() {}

    public PositionDto(String name) {
        this.name = name;
        this.isActive = true;
        this.level = 1;
    }

    public PositionDto(Long id, String name) {
        this.id = id;
        this.name = name;
        this.isActive = true;
    }

    // Static factory methods
    public static PositionDto createNew(String name) {
        return new PositionDto(name);
    }
    
    public static PositionDto fromPosition(Position position) {
        PositionDto dto = new PositionDto();
        dto.setId(position.getId());
        dto.setName(position.getName());
        dto.setDescription(position.getCode()); // Position không có description, dùng code
        dto.setLevel(position.getJobLevel());
        dto.setBaseSalary(null); // Position không có baseSalary
        dto.setIsActive(true); // Position không có isActive, mặc định true
        dto.setLevelName(getLevelNameByLevel(position.getJobLevel()));
        return dto;
    }

    public static PositionDto createWithId(Long id, String name) {
        return new PositionDto(id, name);
    }

    // Helper method
    private static String getLevelNameByLevel(Integer level) {
        if (level == null) return "Chưa xác định";
        switch (level) {
            case 1: return "Nhân viên";
            case 2: return "Nhân viên cao cấp";
            case 3: return "Trưởng nhóm";
            case 4: return "Quản lý";
            case 5: return "Giám đốc";
            default: return "Cấp " + level;
        }
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
        this.levelName = getLevelNameByLevel(level);
    }

    public Double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(Double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    // Business methods
    public boolean isActive() {
        return this.isActive != null && this.isActive;
    }

    public boolean isManagerLevel() {
        return this.level != null && this.level >= 3;
    }

    public boolean isExecutiveLevel() {
        return this.level != null && this.level >= 4;
    }

    public String getDisplayInfo() {
        StringBuilder info = new StringBuilder(this.name);
        if (this.levelName != null) {
            info.append(" (").append(this.levelName).append(")");
        }
        if (this.employeeCount != null) {
            info.append(" - ").append(this.employeeCount).append(" người");
        }
        return info.toString();
    }

    public String getStatusText() {
        return isActive() ? "Hoạt động" : "Ngừng tuyển";
    }

    public String getStatusBadgeClass() {
        return isActive() ? "badge bg-success" : "badge bg-secondary";
    }

    public String getSalaryText() {
        if (this.baseSalary == null) return "Chưa định";
        return String.format("%,.0f VNĐ", this.baseSalary);
    }

    @Override
    public String toString() {
        return "PositionDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", levelName='" + levelName + '\'' +
                ", baseSalary=" + baseSalary +
                ", isActive=" + isActive +
                ", employeeCount=" + employeeCount +
                '}';
    }
}