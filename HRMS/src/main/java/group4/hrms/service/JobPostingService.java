package group4.hrms.service;

import group4.hrms.model.JobPosting;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing job postings
 */
public interface JobPostingService {
    
    /**
     * Get all available job types
     * @return List of job types (e.g. FULL_TIME, PART_TIME, CONTRACT, etc.)
     */
    List<String> getAllJobTypes();
    
    /**
     * Get all available job levels
     * @return List of job levels (e.g. INTERN, FRESHER, JUNIOR, SENIOR, etc.)
     */
    List<String> getAllJobLevels();
    /**
     * Create a new job posting
     * @param jobPosting The job posting to create (must have status = PENDING)
     * @return The ID of the created job posting
     */
    long create(JobPosting jobPosting);
    
    /**
     * Find job posting by ID
     * @param id The job posting ID
     * @return Optional containing the job posting if found
     */
    Optional<JobPosting> findById(long id);
    
    /**
     * Search job postings with criteria and pagination
     * @param criteria Map of search criteria
     * @param page Page number (1-based)
     * @param pageSize Number of items per page
     * @return List of job postings matching the criteria
     */
    List<JobPosting> findJobPostings(Map<String,Object> criteria, int page, int pageSize);
    
    /**
     * Count total job postings matching criteria
     * @param criteria Map of search criteria
     * @return Total count of matching job postings
     */
    int countJobPostings(Map<String,Object> criteria);
    
    /**
     * Approve a job posting (HRM only)
     * @param id The job posting ID
     * @param approverId The account ID of the approver
     * @throws IllegalStateException if job posting is not in PENDING state
     */
    void approve(long id, long approverId);
    
    /**
     * Reject a job posting (HRM only)
     * @param id The job posting ID
     * @param approverId The account ID of the approver
     * @param reason The reason for rejection
     * @throws IllegalStateException if job posting is not in PENDING state
     * @throws IllegalArgumentException if reason is null or empty
     */
    void reject(long id, long approverId, String reason);
    
    /**
     * Publish an approved job posting (HRM only)
     * @param id The job posting ID
     * @param publisherId The account ID of the publisher
     * @param publishDate Optional publication date, if null uses current date
     * @throws IllegalStateException if job posting is not in APPROVED state
     */
    void publish(long id, long publisherId, LocalDate publishDate);
}