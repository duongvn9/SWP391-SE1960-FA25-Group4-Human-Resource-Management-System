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

            // Check if job posting can be updated
            // Allow update for PENDING (normal edit) and REJECTED (resubmit after rejection)
            if (!"PENDING".equals(existing.getStatus()) && !"REJECTED".equals(existing.getStatus())) {
                throw new IllegalStateException("Can only update job postings in PENDING or REJECTED state");
            }
            
            logger.info("Updating job posting from status: {} to: {}", existing.getStatus(), jobPosting.getStatus());

            // Validate the updated job posting
            if (jobPosting == null) {
                throw new IllegalArgumentException("Job posting cannot be null");
            }
            if (jobPosting.getTitle() == null || jobPosting.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Job title is required");
            }

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
        // Debug logs removed
        
        logger.info("Attempting to create job posting: {}", jobPosting);
        
        // Validate job posting data
        if (jobPosting == null) {
            throw new IllegalArgumentException("Job posting cannot be null");
        }
        if (jobPosting.getTitle() == null || jobPosting.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Job title is required");
        }
        
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
                    // Filter out expired jobs for PUBLISHED status
                    List<JobPosting> activeJobs = filterActiveJobs(jobs);
                    return paginate(activeJobs, page, pageSize);
                } else {
                    // Không chuyển status về lowercase vì trong DB lưu uppercase
                    List<JobPosting> jobs = jobPostingDao.findByStatus(status.toUpperCase());
                    return paginate(jobs, page, pageSize);
                }
            }

            // Check if we need to handle PUBLISHED status with other criteria
            String statusFilter = criteria.get("status") instanceof String ? ((String) criteria.get("status")).trim() : null;
            if (statusFilter != null && statusFilter.equalsIgnoreCase("PUBLISHED")) {
                // For PUBLISHED status with additional criteria, start with published jobs and filter
                List<JobPosting> publishedJobs = jobPostingDao.findPublishedJobs();
                // First filter out expired jobs
                List<JobPosting> activeJobs = filterActiveJobs(publishedJobs);
                // Then apply other criteria filters
                return applyAdditionalFilters(activeJobs, criteria, page, pageSize);
            }

            // Fallback: load all and filter in-memory by available criteria
            List<JobPosting> all = jobPostingDao.findAll();

            // Apply simple filters: status, departmentId, jobType, jobLevel, priority, searchQuery
            String statusCriteria = criteria.get("status") instanceof String ? ((String) criteria.get("status")).trim() : null;
            Long departmentId = criteria.get("departmentId") instanceof Long ? (Long) criteria.get("departmentId") : null;
            String jobType = criteria.get("jobType") instanceof String ? ((String) criteria.get("jobType")).trim() : null;
            String jobLevel = criteria.get("jobLevel") instanceof String ? ((String) criteria.get("jobLevel")).trim() : null;
            String priority = criteria.get("priority") instanceof String ? ((String) criteria.get("priority")).trim() : null;
            String searchQuery = criteria.get("searchQuery") instanceof String ? ((String) criteria.get("searchQuery")).trim().toLowerCase() : null;

            List<JobPosting> filtered = new java.util.ArrayList<>();
            for (JobPosting jp : all) {
                if (statusCriteria != null && !statusCriteria.isEmpty()) {
                    if (!statusCriteria.equalsIgnoreCase(jp.getStatus())) continue;
                    // If filtering by PUBLISHED status, also check if job is not expired
                    if (statusCriteria.equalsIgnoreCase("PUBLISHED") && jp.isExpired()) continue;
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
                    List<JobPosting> jobs = jobPostingDao.findPublishedJobs();
                    // Filter out expired jobs for PUBLISHED status count
                    List<JobPosting> activeJobs = filterActiveJobs(jobs);
                    return activeJobs.size();
                }
                return jobPostingDao.findByStatus(status.toUpperCase()).size();
            }

            // Check if we need to handle PUBLISHED status with other criteria
            String statusFilter = criteria.get("status") instanceof String ? ((String) criteria.get("status")).trim() : null;
            if (statusFilter != null && statusFilter.equalsIgnoreCase("PUBLISHED")) {
                // For PUBLISHED status with additional criteria, start with published jobs and filter
                List<JobPosting> publishedJobs = jobPostingDao.findPublishedJobs();
                // First filter out expired jobs
                List<JobPosting> activeJobs = filterActiveJobs(publishedJobs);
                // Then apply other criteria filters
                List<JobPosting> filtered = applyAdditionalFilters(activeJobs, criteria, 1, Integer.MAX_VALUE);
                return filtered.size();
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
    
    /**
     * Filter out expired job postings (where application deadline has passed)
     * @param jobs List of job postings to filter
     * @return List of active (non-expired) job postings
     */
    private List<JobPosting> filterActiveJobs(List<JobPosting> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            return List.of();
        }
        
        LocalDate today = LocalDate.now();
        logger.debug("Filtering active jobs. Today: {}, Total jobs: {}", today, jobs.size());
        
        List<JobPosting> activeJobs = jobs.stream()
                .filter(job -> {
                    if (job.getApplicationDeadline() == null) {
                        logger.debug("Job {} has no deadline - keeping", job.getId());
                        return true;
                    }
                    // Use the isExpired() method from JobPosting for consistency
                    boolean isActive = !job.isExpired();
                    logger.debug("Job {} deadline: {}, expired: {}, active: {}", 
                               job.getId(), job.getApplicationDeadline(), job.isExpired(), isActive);
                    return isActive;
                })
                .collect(java.util.stream.Collectors.toList());
                
        logger.info("Filtered {} active jobs from {} total jobs", activeJobs.size(), jobs.size());
        return activeJobs;
    }
    
    /**
     * Apply additional filters (departmentId, jobType, searchQuery, etc.) to a list of jobs
     * @param jobs List of jobs to filter
     * @param criteria Filter criteria
     * @param page Page number
     * @param pageSize Page size
     * @return Filtered and paginated list
     */
    private List<JobPosting> applyAdditionalFilters(List<JobPosting> jobs, Map<String, Object> criteria, int page, int pageSize) {
        if (jobs == null || jobs.isEmpty()) {
            return List.of();
        }
        
        // Extract filter criteria (excluding status since it's already handled)
        Long departmentId = criteria.get("departmentId") instanceof Long ? (Long) criteria.get("departmentId") : null;
        String jobType = criteria.get("jobType") instanceof String ? ((String) criteria.get("jobType")).trim() : null;
        String jobLevel = criteria.get("jobLevel") instanceof String ? ((String) criteria.get("jobLevel")).trim() : null;
        String priority = criteria.get("priority") instanceof String ? ((String) criteria.get("priority")).trim() : null;
        String searchQuery = criteria.get("searchQuery") instanceof String ? ((String) criteria.get("searchQuery")).trim().toLowerCase() : null;
        
        List<JobPosting> filtered = new java.util.ArrayList<>();
        for (JobPosting jp : jobs) {
            // Apply filters (status and expired are already handled)
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
        
        logger.info("Applied additional filters: {} jobs remaining from {} jobs", filtered.size(), jobs.size());
        return paginate(filtered, page, pageSize);
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
        
        logger.info("🔴 SERVICE.reject() called with id={}, approverId={}, reason='{}'", id, approverId, reason);
        
        try {
            JobPosting jobPosting = jobPostingDao.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Job posting not found: " + id));
                    
            if (!jobPosting.isPending()) {
                throw new IllegalStateException("Job posting must be in PENDING state to reject");
            }
            
            logger.info("📝 Setting rejection fields...");
            jobPosting.setStatus("REJECTED");
            jobPosting.setRejectedReason(reason.trim());
            jobPosting.setApprovedByAccountId(approverId);
            jobPosting.setApprovedAt(LocalDateTime.now());
            jobPosting.setUpdatedAt(LocalDateTime.now());
            
            logger.info("💾 Before update - JobPosting fields:");
            logger.info("  - status: {}", jobPosting.getStatus());
            logger.info("  - rejectedReason: {}", jobPosting.getRejectedReason());
            logger.info("  - approvedByAccountId: {}", jobPosting.getApprovedByAccountId());
            logger.info("  - approvedAt: {}", jobPosting.getApprovedAt());
            
            jobPostingDao.update(jobPosting);
            logger.info("✅ DAO.update() completed!");
            
        } catch (SQLException e) {
            logger.error("❌ SQLException in reject(): {}", e.getMessage(), e);
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