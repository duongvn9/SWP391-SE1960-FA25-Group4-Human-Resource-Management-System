package group4.hrms.service.impl;

import group4.hrms.dao.JobPostingDao;
import group4.hrms.model.JobPosting;
import group4.hrms.service.JobPostingService;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of JobPostingService
 * ĐÃ ĐIỀU CHỈNH LOGIC để phù hợp với Flow HRM: Approve (PENDING -> PUBLISHED)
 */
public class JobPostingServiceImpl implements JobPostingService {
    
    private final JobPostingDao jobPostingDao;
    
    public JobPostingServiceImpl() {
        this.jobPostingDao = new JobPostingDao(); // Khởi tạo trực tiếp
    }
    @Override
    public long create(JobPosting jobPosting) {
        if (!"PENDING".equals(jobPosting.getStatus())) {
            throw new IllegalArgumentException("New job posting must have PENDING status");
        }
        try {
            JobPosting saved = jobPostingDao.save(jobPosting);
            return saved.getId();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create job posting", e);
        }
    }
    
    @Override
    public Optional<JobPosting> findById(long id) {
        try {
            return jobPostingDao.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public List<JobPosting> findJobPostings(Map<String, Object> criteria, int page, int pageSize) {
        try {
            // Prefer specialized DAO methods when possible
            if (criteria == null) {
                List<JobPosting> all = jobPostingDao.findAll();
                return paginate(all, page, pageSize);
            }

            // Search by requestId
            Object reqObj = criteria.get("requestId");
            if (reqObj instanceof Long) {
                Long requestId = (Long) reqObj;
                Optional<JobPosting> j = jobPostingDao.findByRequestId(requestId);
                return j.map(list -> List.of(list)).orElseGet(List::of);
            }

            // Search by department
            Object deptObj = criteria.get("departmentId");
            if (deptObj instanceof Long && criteria.size() == 1) {
                List<JobPosting> jobs = jobPostingDao.findByDepartmentId((Long) deptObj);
                return paginate(jobs, page, pageSize);
            }

            // Search by status (use dao method). Normalize to lowercase for DB values.
            Object statusObj = criteria.get("status");
            if (statusObj instanceof String && criteria.size() == 1) {
                String status = ((String) statusObj).trim();
                if (status.equalsIgnoreCase("PUBLISHED")) {
                    List<JobPosting> jobs = jobPostingDao.findPublishedJobs();
                    return paginate(jobs, page, pageSize);
                } else {
                    List<JobPosting> jobs = jobPostingDao.findByStatus(status.toLowerCase());
                    return paginate(jobs, page, pageSize);
                }
            }

            // Fallback: load all and filter in-memory by available criteria
            List<JobPosting> all = jobPostingDao.findAll();

            // Apply simple filters: status, departmentId, jobType, jobLevel, searchQuery
            String status = criteria.get("status") instanceof String ? ((String) criteria.get("status")).trim() : null;
            Long departmentId = criteria.get("departmentId") instanceof Long ? (Long) criteria.get("departmentId") : null;
            String jobType = criteria.get("jobType") instanceof String ? ((String) criteria.get("jobType")).trim() : null;
            String jobLevel = criteria.get("jobLevel") instanceof String ? ((String) criteria.get("jobLevel")).trim() : null;
            String searchQuery = criteria.get("searchQuery") instanceof String ? ((String) criteria.get("searchQuery")).trim().toLowerCase() : null;

            List<JobPosting> filtered = new java.util.ArrayList<>();
            for (JobPosting jp : all) {
                if (status != null && !status.isEmpty()) {
                    if (!status.equalsIgnoreCase(jp.getStatus())) continue;
                }
                if (departmentId != null) {
                    if (jp.getDepartmentId() == null || !departmentId.equals(jp.getDepartmentId())) continue;
                }
                if (jobType != null && !jobType.isEmpty()) {
                    if (jp.getJobType() == null || !jobType.equalsIgnoreCase(jp.getJobType())) continue;
                }
                if (jobLevel != null && !jobLevel.isEmpty()) {
                    if (jp.getLevel() == null || !jobLevel.equalsIgnoreCase(jp.getLevel())) continue;
                }
                if (searchQuery != null && !searchQuery.isEmpty()) {
                    String title = jp.getTitle() != null ? jp.getTitle().toLowerCase() : "";
                    String desc = jp.getDescription() != null ? jp.getDescription().toLowerCase() : "";
                    if (!title.contains(searchQuery) && !desc.contains(searchQuery)) continue;
                }
                filtered.add(jp);
            }

            return paginate(filtered, page, pageSize);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search job postings", e);
        }
    }
    
    @Override
    public int countJobPostings(Map<String, Object> criteria) {
        try {
            if (criteria == null || criteria.isEmpty()) {
                return (int) jobPostingDao.count();
            }

            // Try to use specialized DAO methods where available
            if (criteria.get("departmentId") instanceof Long && criteria.size() == 1) {
                return jobPostingDao.findByDepartmentId((Long) criteria.get("departmentId")).size();
            }

            if (criteria.get("status") instanceof String && criteria.size() == 1) {
                String status = ((String) criteria.get("status")).trim();
                if (status.equalsIgnoreCase("PUBLISHED")) {
                    return jobPostingDao.findPublishedJobs().size();
                }
                return jobPostingDao.findByStatus(status.toLowerCase()).size();
            }

            // Fallback: reuse in-memory filtering from findJobPostings
            List<JobPosting> list = findJobPostings(criteria, 1, Integer.MAX_VALUE);
            return list.size();
                
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count job postings", e);
        }
    }

    /**
     * Simple pagination helper (1-based page index)
     */
    private List<JobPosting> paginate(List<JobPosting> list, int page, int pageSize) {
        if (list == null || list.isEmpty()) return List.of();
        if (pageSize <= 0) pageSize = 10;
        if (page <= 0) page = 1;
        int from = (page - 1) * pageSize;
        if (from >= list.size()) return List.of();
        int to = Math.min(from + pageSize, list.size());
        return list.subList(from, to);
    }
    
    @Override
    public List<String> getAllJobTypes() {
        return Arrays.asList(
            "FULL_TIME",
            "PART_TIME", 
            "CONTRACT",
            "TEMPORARY",
            "INTERNSHIP"
        );
    }
    
    @Override
    public List<String> getAllJobLevels() {
        return Arrays.asList(
            "INTERN",
            "FRESHER",
            "JUNIOR",
            "MIDDLE",
            "SENIOR",
            "LEAD",
            "MANAGER"
        );
    }
    
    // HÀM APPROVE ĐÃ BỊ LOẠI BỎ THEO YÊU CẦU DUYỆT 1 BƯỚC

    /**
     * Hành động của HRM: Duyệt và Đăng tuyển (PENDING -> PUBLISHED)
     */
    @Override
    public void approve(long id, long approverId) {
        try {
            JobPosting jobPosting = jobPostingDao.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Job posting not found: " + id));
            if (!jobPosting.isPending()) {
                throw new IllegalStateException("Job posting must be in PENDING state to approve");
            }
            jobPosting.setStatus("APPROVED");
            jobPosting.setApprovedByAccountId(approverId);
            jobPosting.setApprovedAt(LocalDateTime.now());
            jobPosting.setUpdatedAt(LocalDateTime.now());
            jobPostingDao.update(jobPosting);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reject(long id, long approverId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Rejection reason is required");
        }
        
        try {
            JobPosting jobPosting = jobPostingDao.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Job posting not found: " + id));
                    
            if (!jobPosting.isPending()) {
                throw new IllegalStateException("Job posting must be in PENDING state to reject");
            }
            
            jobPosting.setStatus("REJECTED");
            jobPosting.setRejectedReason(reason.trim());
            jobPosting.setApprovedByAccountId(approverId);
            jobPosting.setApprovedAt(LocalDateTime.now());
            jobPosting.setUpdatedAt(LocalDateTime.now());
            
            jobPostingDao.update(jobPosting);
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reject job posting", e);
        }
    }

    @Override
    public void publish(long id, long publisherId, LocalDate publishDate) {
        try {
            JobPosting jobPosting = jobPostingDao.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Job posting not found: " + id));
                    
            if (!jobPosting.isApproved()) {
                throw new IllegalStateException("Job posting must be in APPROVED state to publish");
            }
            
            // Validate publish date
            if (publishDate != null && publishDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Publication date cannot be in the past");
            }
            
            jobPosting.setStatus("PUBLISHED");
            jobPosting.setPublishedByAccountId(publisherId);
            
            LocalDateTime publishDateTime = publishDate != null 
                ? publishDate.atStartOfDay()
                : LocalDateTime.now();
                
            jobPosting.setPublishedAt(publishDateTime);
            jobPosting.setUpdatedAt(LocalDateTime.now());
            
            jobPostingDao.update(jobPosting);
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to publish job posting", e);
        }
    }
}