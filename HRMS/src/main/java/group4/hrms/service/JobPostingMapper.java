package group4.hrms.service;

import group4.hrms.dto.RecruitmentDetailsDto;
import group4.hrms.model.JobPosting;


/**
 * Utility class to map RecruitmentDetailsDto to JobPosting entity.
 * Chú thích: Chỉ dùng cho luồng tạo JobPosting từ RecruitmentRequest (HR tạo, HRM duyệt).
 */
public class JobPostingMapper {
    /**
     * Map RecruitmentDetailsDto to JobPosting (chỉ các trường liên quan)
     * @param dto RecruitmentDetailsDto (đã validate)
     * @param jobPosting JobPosting (có thể là entity mới hoặc đã có sẵn)
     * @return JobPosting đã được gán dữ liệu
     */
    public static JobPosting mapFromDto(RecruitmentDetailsDto dto, JobPosting jobPosting) {
    if (dto == null || jobPosting == null) return jobPosting;
    jobPosting.setTitle(dto.getPositionName());
    jobPosting.setCode(dto.getPositionCode());
    jobPosting.setLevel(dto.getJobLevel());
    jobPosting.setNumberOfPositions(dto.getQuantity());
    jobPosting.setJobType(normalizeJobType(dto.getJobType()));
    jobPosting.setDescription(dto.getJobSummary());
    jobPosting.setWorkingLocation(dto.getWorkingLocation());
    // Salary: lấy trực tiếp từ DTO mới
    jobPosting.setMinSalary(dto.getMinSalary() != null ? java.math.BigDecimal.valueOf(dto.getMinSalary()) : null);
    jobPosting.setMaxSalary(dto.getMaxSalary() != null ? java.math.BigDecimal.valueOf(dto.getMaxSalary()) : null);
    jobPosting.setSalaryType(dto.getSalaryType());
    // Requirements, benefits, contact: để HRM bổ sung sau
    return jobPosting;
}

    /**
     * Chuẩn hóa jobType từ text sang code (Full-time → FULL_TIME, Part-time → PART_TIME, ...)
     */
    private static String normalizeJobType(String type) {
        if (type == null) return null;
        String t = type.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        switch (t) {
            case "FULL_TIME":
                return "FULL_TIME";
            case "PART_TIME":
                return "PART_TIME";
            case "CONTRACT":
                return "CONTRACT";
            case "INTERN":
                return "INTERN";
            default:
                return t;
        }
    }

    /**
     * Parse budgetSalaryRange ("min-max") và set vào jobPosting
     */
    private static void parseAndSetSalary(String salaryRange, JobPosting jobPosting) {
        if (salaryRange == null || salaryRange.isBlank()) {
            jobPosting.setMinSalary(null);
            jobPosting.setMaxSalary(null);
            jobPosting.setSalaryType("NEGOTIABLE");
            return;
        }
        String s = salaryRange.replaceAll("[^0-9-]", "");
        String[] parts = s.split("-");
        try {
            if (parts.length == 2) {
                Double min = Double.valueOf(parts[0]);
                Double max = Double.valueOf(parts[1]);
                jobPosting.setMinSalary(java.math.BigDecimal.valueOf(min));
                jobPosting.setMaxSalary(java.math.BigDecimal.valueOf(max));
                jobPosting.setSalaryType("RANGE");
            } else if (parts.length == 1 && !parts[0].isBlank()) {
                Double min = Double.valueOf(parts[0]);
                jobPosting.setMinSalary(java.math.BigDecimal.valueOf(min));
                jobPosting.setMaxSalary(null);
                jobPosting.setSalaryType("FROM");
            } else {
                jobPosting.setMinSalary(null);
                jobPosting.setMaxSalary(null);
                jobPosting.setSalaryType("NEGOTIABLE");
            }
        } catch (Exception e) {
            jobPosting.setMinSalary(null);
            jobPosting.setMaxSalary(null);
            jobPosting.setSalaryType("NEGOTIABLE");
        }
    }
}
