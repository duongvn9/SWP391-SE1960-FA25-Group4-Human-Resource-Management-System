package group4.hrms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * DTO for job posting creation/editing validation
 */
public class JobPostingFormDto {
    private String positionCode;
    private String positionName;
    private String jobLevel;
    private String jobType;
    private Integer numberOfPositions;
    private String salaryType;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String description;
    private String requirements;
    private String benefits;
    private String location;
    private LocalDate applicationDeadline;
    private String contactEmail;
    private String contactPhone;

    public Map<String, String> validate() {
        Map<String, String> errors = new HashMap<>();

        // Required fields validation
        if (isBlank(positionCode)) {
            errors.put("positionCode", "Position code is required");
        }
        if (isBlank(positionName)) {
            errors.put("positionName", "Position name is required");
        }
        if (isBlank(jobLevel)) {
            errors.put("jobLevel", "Job level is required");
        }
        if (!isValidJobLevel(jobLevel)) {
            errors.put("jobLevel", "Invalid job level");
        }
        if (isBlank(jobType)) {
            errors.put("jobType", "Job type is required");
        }
        if (!isValidJobType(jobType)) {
            errors.put("jobType", "Invalid job type");
        }
        if (numberOfPositions == null || numberOfPositions < 1) {
            errors.put("numberOfPositions", "Number of positions must be at least 1");
        }

        // Salary validation
        if (isBlank(salaryType)) {
            errors.put("salaryType", "Salary type is required");
        } else if (!isValidSalaryType(salaryType)) {
            errors.put("salaryType", "Invalid salary type");
        } else {
            if ("RANGE".equals(salaryType)) {
                if (minSalary == null || maxSalary == null) {
                    errors.put("salaryType", "Both minimum and maximum salary are required for range");
                } else if (minSalary.compareTo(maxSalary) >= 0) {
                    errors.put("maxSalary", "Maximum salary must be greater than minimum salary");
                }
            } else if ("FROM".equals(salaryType) && minSalary == null) {
                errors.put("minSalary", "Minimum salary is required for 'From' type");
            }
        }

        // Job details validation
        if (isBlank(description)) {
            errors.put("description", "Job description is required");
        } else if (description.length() > 4000) {
            errors.put("description", "Description cannot exceed 4000 characters");
        }

        if (isBlank(requirements)) {
            errors.put("requirements", "Requirements are required");
        } else if (requirements.length() > 4000) {
            errors.put("requirements", "Requirements cannot exceed 4000 characters");
        }

        if (!isBlank(benefits) && benefits.length() > 2000) {
            errors.put("benefits", "Benefits cannot exceed 2000 characters");
        }

        if (isBlank(location)) {
            errors.put("location", "Working location is required");
        }

        // Deadline validation
        if (applicationDeadline == null) {
            errors.put("applicationDeadline", "Application deadline is required");
        } else if (applicationDeadline.isBefore(LocalDate.now())) {
            errors.put("applicationDeadline", "Application deadline cannot be in the past");
        }

        // Contact validation
        if (isBlank(contactEmail)) {
            errors.put("contactEmail", "Contact email is required");
        } else if (!isValidEmail(contactEmail)) {
            errors.put("contactEmail", "Invalid email format");
        }

        if (!isBlank(contactPhone) && !isValidPhone(contactPhone)) {
            errors.put("contactPhone", "Invalid phone number format");
        }

        return errors;
    }

    // Helper methods for validation
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isValidJobLevel(String level) {
        if (level == null) return false;
        return level.matches("JUNIOR|MIDDLE|SENIOR");
    }

    private boolean isValidJobType(String type) {
        if (type == null) return false;
        return type.matches("FULL_TIME|PART_TIME|CONTRACT|INTERN");
    }

    private boolean isValidSalaryType(String type) {
        if (type == null) return false;
        return type.matches("RANGE|FROM|NEGOTIABLE");
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return phone.matches("^[0-9+][0-9()-]{8,20}$");
    }

    // Getters and setters
    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(String jobLevel) {
        this.jobLevel = jobLevel;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public Integer getNumberOfPositions() {
        return numberOfPositions;
    }

    public void setNumberOfPositions(Integer numberOfPositions) {
        this.numberOfPositions = numberOfPositions;
    }

    public String getSalaryType() {
        return salaryType;
    }

    public void setSalaryType(String salaryType) {
        this.salaryType = salaryType;
    }

    public BigDecimal getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(BigDecimal minSalary) {
        this.minSalary = minSalary;
    }

    public BigDecimal getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(BigDecimal maxSalary) {
        this.maxSalary = maxSalary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDate applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}