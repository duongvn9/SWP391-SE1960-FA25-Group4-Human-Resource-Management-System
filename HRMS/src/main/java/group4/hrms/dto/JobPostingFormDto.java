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
    private String jobTitle; // posting title editable by HR
    private String jobLevel;
    private String jobType;
    private Integer numberOfPositions;
    private String code; // public job posting code
    private Integer minExperienceYears;
    private java.time.LocalDate startDate;
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
    private String priority;
    private String workingHours;

    // Minimum allowed salary (1,000,000 VND)
    private static final BigDecimal MIN_VALID_SALARY = new BigDecimal("1000000");

    public Map<String, String> validate() {
        Map<String, String> errors = new HashMap<>();

        // Required fields validation
        // positionCode/positionName could be prefilled from recruitment request; still validate jobTitle
        if (isBlank(jobTitle) && isBlank(positionName)) {
            errors.put("positionName", "Position name or Job title is required");
        }
        
        // Job title validation - enhanced
        if (!isBlank(jobTitle)) {
            if (jobTitle.length() < 3) {
                errors.put("jobTitle", "Job title must be at least 3 characters");
            }
            if (jobTitle.length() > 255) {
                errors.put("jobTitle", "Job title cannot exceed 255 characters");
            }
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
        } else if (numberOfPositions > 100) {
            errors.put("numberOfPositions", "Number of positions cannot exceed 100");
        }

        // Job code validation
        if (!isBlank(code) && code.length() > 128) {
            errors.put("code", "Code cannot exceed 128 characters");
        }
        
        // Min Experience Years validation
        if (minExperienceYears != null) {
            if (minExperienceYears < 0) {
                errors.put("minExperienceYears", "Experience years cannot be negative");
            }
            if (minExperienceYears > 50) {
                errors.put("minExperienceYears", "Experience years cannot exceed 50 years");
            }
        }
        
        // Start date validation
        if (startDate != null && startDate.isBefore(LocalDate.now())) {
            errors.put("startDate", "Start date cannot be in the past");
        }
        
        // Working hours validation
        if (!isBlank(workingHours) && workingHours.length() > 255) {
            errors.put("workingHours", "Working hours cannot exceed 255 characters");
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
        } else if (location.length() > 255) {
            errors.put("location", "Working location cannot exceed 255 characters");
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

        // Salary lower-bound validation
        if (minSalary != null && minSalary.compareTo(MIN_VALID_SALARY) < 0) {
            errors.put("minSalary", "Minimum salary must be at least 1,000,000 VND");
        }
        if (maxSalary != null && maxSalary.compareTo(MIN_VALID_SALARY) < 0) {
            errors.put("maxSalary", "Maximum salary must be at least 1,000,000 VND");
        }

        // Validate min < max
        if (minSalary != null && maxSalary != null && minSalary.compareTo(maxSalary) >= 0) {
            errors.put("maxSalary", "Maximum salary must be greater than minimum salary");
            errors.put("minSalary", "Minimum salary must be less than maximum salary");
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

    // Normalize input to canonical token used in DB/logic.
    // Accepts user-friendly variants (e.g. "Full-time", "Full Time", "FULL_TIME", "full time")
    private String normalizeToToken(String input) {
        if (input == null) return null;
        String s = input.trim();
        if (s.isEmpty()) return null;
        // Replace common separators with underscore and uppercase
        s = s.replaceAll("[\\s\\-]+", "_");
        s = s.replaceAll("[^A-Za-z0-9_]", "_");
        s = s.replaceAll("__+", "_");
        return s.toUpperCase();
    }

    private boolean isValidJobType(String type) {
        String tok = normalizeToToken(type);
        if (tok == null) return false;
        return tok.equals("FULL_TIME") || tok.equals("PART_TIME") || tok.equals("CONTRACT") || tok.equals("INTERN") || tok.equals("INTERNSHIP");
    }

    private boolean isValidSalaryType(String type) {
        String tok = normalizeToToken(type);
        if (tok == null) return false;
        return tok.equals("RANGE") || tok.equals("FROM") || tok.equals("NEGOTIABLE") || tok.equals("GROSS") || tok.equals("NET");
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

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getMinExperienceYears() {
        return minExperienceYears;
    }

    public void setMinExperienceYears(Integer minExperienceYears) {
        this.minExperienceYears = minExperienceYears;
    }

    public java.time.LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(java.time.LocalDate startDate) {
        this.startDate = startDate;
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

    /**
     * Return jobType normalized to canonical token used in DB/logic (e.g. FULL_TIME)
     */
    public String getNormalizedJobType() {
        String tok = normalizeToToken(this.jobType);
        if (tok == null) return null;
        if (tok.equals("INTERNSHIP")) return "INTERN"; // normalize alternative
        return tok;
    }

    /**
     * Return salaryType normalized to canonical token used in DB/logic (e.g. GROSS, NET, RANGE, FROM, NEGOTIABLE)
     */
    public String getNormalizedSalaryType() {
        String tok = normalizeToToken(this.salaryType);
        if (tok == null) return null;
        // Accept GROSS/NET directly; keep RANGE/FROM/NEGOTIABLE as-is
        return tok;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }
}