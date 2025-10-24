package group4.hrms.service.impl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.dao.JobPostingDao;
import group4.hrms.model.JobPosting;
import group4.hrms.service.DepartmentService;
import group4.hrms.service.JobPostingService;
import group4.hrms.service.PositionService;

/**
 * Implementation of JobPostingService
 * ĐÃ ĐIỀU CHỈNH LOGIC để phù hợp với Flow HRM: Approve (PENDING -> PUBLISHED)
 */
public class JobPostingServiceImpl implements JobPostingService {
    private static final Logger logger = LoggerFactory.getLogger(JobPostingServiceImpl.class);

    private final JobPostingDao jobPostingDao;
    private final DepartmentService departmentService;
    private final PositionService positionService;
    
    public JobPostingServiceImpl(DepartmentService departmentService, PositionService positionService) {
        this.jobPostingDao = new JobPostingDao();
        this.departmentService = departmentService;
        this.positionService = positionService;
        logger.info("JobPostingServiceImpl initialized with dependencies");
    }

    @Override
    public void update(JobPosting jobPosting) {
        // Validate input
        if (jobPosting == null) {
            throw new IllegalArgumentException("Job posting cannot be null");
        }
        if (jobPosting.getId() == null) {
            throw new IllegalArgumentException("Job posting ID cannot be null");
        }

        logger.info("Attempting to update job posting: {}", jobPosting);

        try {
            // Get existing job posting
            JobPosting existing = jobPostingDao.findById(jobPosting.getId())
                .orElseThrow(() -> new IllegalArgumentException("Job posting not found with ID: " + jobPosting.getId()));

            // Check if job posting is in PENDING state
            if (!"PENDING".equals(existing.getStatus())) {
                throw new IllegalStateException("Can only update job postings in PENDING state");
            }

            // Validate the updated job posting
            validateJobPosting(jobPosting);

            // Set audit timestamps
            jobPosting.setCreatedAt(existing.getCreatedAt()); // Preserve creation time
            jobPosting.setUpdatedAt(LocalDateTime.now());

            // Save changes
            logger.info("Saving updated job posting to database: title='{}', id={}", 
                       jobPosting.getTitle(), jobPosting.getId());
            JobPosting saved = jobPostingDao.save(jobPosting);
            
            if (saved == null) {
                logger.error("JobPostingDao.save returned null during update");
                throw new RuntimeException("Failed to update job posting - null result from DAO");
            }

            logger.info("Successfully updated job posting with id={}", saved.getId());

        } catch (SQLException e) {
            logger.error("Database error while updating job posting: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update job posting due to database error", e);
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Re-throw business logic exceptions as-is
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while updating job posting: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error while updating job posting", e);
        }
    }

    @Override
    public long create(JobPosting jobPosting) {
        logger.info("Attempting to create job posting: {}", jobPosting);
        
        // Validate job posting data
        validateJobPosting(jobPosting);
        
        // Ensure status is PENDING
        if (!"PENDING".equals(jobPosting.getStatus())) {
            logger.error("Invalid status for new job posting: {}", jobPosting.getStatus());
            throw new IllegalArgumentException("New job posting must have PENDING status");
        }
        
        try {
            logger.info("Saving job posting to database: title='{}', requestId={}", 
                       jobPosting.getTitle(), jobPosting.getRequestId());
            
            // Set audit timestamps
            LocalDateTime now = LocalDateTime.now();
            jobPosting.setCreatedAt(now);
            jobPosting.setUpdatedAt(now);
            
            JobPosting saved = jobPostingDao.save(jobPosting);
            if (saved == null) {
                logger.error("JobPostingDao.save returned null");
                throw new RuntimeException("Failed to create job posting - null result from DAO");
            }
            
            Long savedId = saved.getId();
            if (savedId == null) {
                logger.error("Saved job posting has null ID");
                throw new RuntimeException("Failed to get ID for created job posting");
            }
            
            logger.info("Successfully created job posting with id={}", savedId);
            return savedId;
            
        } catch (SQLException e) {
            logger.error("Database error while creating job posting: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create job posting due to database error", e);
        } catch (Exception e) {
            logger.error("Unexpected error while creating job posting: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error while creating job posting", e);
        }
    }
    
    private void validateJobPosting(JobPosting jobPosting) {
        if (jobPosting == null) {
            throw new IllegalArgumentException("Job posting cannot be null");
        }
        
        List<String> errors = new ArrayList<>();
        
        // Required fields
        if (isEmpty(jobPosting.getTitle())) {
            errors.add("Title is required");
        }
        if (isEmpty(jobPosting.getDescription())) {
            errors.add("Description is required");
        }
        if (isEmpty(jobPosting.getRequirements())) {
            errors.add("Requirements are required");
        }
        if (isEmpty(jobPosting.getWorkingLocation())) {
            errors.add("Working location is required");
        }
        if (jobPosting.getApplicationDeadline() == null) {
            errors.add("Application deadline is required");
        }
        if (isEmpty(jobPosting.getContactEmail())) {
            errors.add("Contact email is required");
        }
        
        // Business rules
        if (jobPosting.getApplicationDeadline() != null && 
            jobPosting.getApplicationDeadline().isBefore(LocalDate.now())) {
            errors.add("Application deadline cannot be in the past");
        }
        
        if (!errors.isEmpty()) {
            String errorMessage = String.join("; ", errors);
            logger.error("Job posting validation failed: {}", errorMessage);
            throw new IllegalArgumentException("Invalid job posting: " + errorMessage);
        }
        
        logger.info("Job posting validation passed");
    }
    
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    @Override
    public Optional<JobPosting> findById(long id) {
        try {
            logger.debug("Finding job posting by id={}", id);
            return jobPostingDao.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public List<JobPosting> findJobPostings(Map<String, Object> criteria, int page, int pageSize) {
        try {
            // If no criteria, return all jobs
            if (criteria == null || criteria.isEmpty()) {
                List<JobPosting> allJobs = jobPostingDao.findAll();
                return paginate(allJobs, page, pageSize);
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
                    // Không chuyển status về lowercase vì trong DB lưu uppercase
                    List<JobPosting> jobs = jobPostingDao.findByStatus(status.toUpperCase());
                    return paginate(jobs, page, pageSize);
                }
            }

            // Fallback: load all and filter in-memory by available criteria
            List<JobPosting> all = jobPostingDao.findAll();

            // Apply simple filters: status, departmentId, jobType, jobLevel, priority, searchQuery
            String status = criteria.get("status") instanceof String ? ((String) criteria.get("status")).trim() : null;
            Long departmentId = criteria.get("departmentId") instanceof Long ? (Long) criteria.get("departmentId") : null;
            String jobType = criteria.get("jobType") instanceof String ? ((String) criteria.get("jobType")).trim() : null;
            String jobLevel = criteria.get("jobLevel") instanceof String ? ((String) criteria.get("jobLevel")).trim() : null;
            String priority = criteria.get("priority") instanceof String ? ((String) criteria.get("priority")).trim() : null;
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
                if (priority != null && !priority.isEmpty()) {
                    if (jp.getPriority() == null || !priority.equalsIgnoreCase(jp.getPriority())) continue;
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
                return jobPostingDao.findByStatus(status.toUpperCase()).size();
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