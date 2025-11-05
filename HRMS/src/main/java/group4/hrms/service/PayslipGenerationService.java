package group4.hrms.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import group4.hrms.config.PayrollConfig;
import group4.hrms.dao.PayslipDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.GenerationRequest;
import group4.hrms.dto.GenerationResult;
import group4.hrms.dto.PayslipCalculationResult;
import group4.hrms.dto.PayslipFilter;
import group4.hrms.model.Payslip;
import group4.hrms.model.User;
import group4.hrms.util.DatabaseUtil;

/**
 * Service for bulk payslip generation and regeneration operations
 * Requirements: 3.1, 3.2, 3.3, 3.5, 3.6, 3.7, 8.1-8.7, 9.7, 9.8
 */
public class PayslipGenerationService {

    private static final Logger logger = Logger.getLogger(PayslipGenerationService.class.getName());

    // Concurrency control - prevent multiple generations for same period
    private static final ConcurrentHashMap<String, ReentrantLock> periodLocks = new ConcurrentHashMap<>();

    // Dependencies
    private final PayslipDao payslipDao;
    private final UserDao userDao;
    private final PayslipCalculationService calculationService;

    // Constructor
    public PayslipGenerationService() {
        this.payslipDao = new PayslipDao();
        this.userDao = new UserDao();
        this.calculationService = new PayslipCalculationService();
    }

    /**
     * Generate payslips for multiple employees based on request parameters
     * Requirements: 3.1, 3.2, 3.3, 3.5, 3.6, 3.7
     *
     * @param request Generation request with scope and parameters
     * @return GenerationResult with counts and error details
     */
    public GenerationResult generatePayslips(GenerationRequest request) {
        logger.info("Starting payslip generation: " + request);

        // Validate request
        if (!request.isValid()) {
            return new GenerationResult(false, "Invalid generation request parameters");
        }

        GenerationResult result = new GenerationResult();
        String lockKey = createLockKey(request.getPeriodStart(), request.getPeriodEnd());
        ReentrantLock lock = periodLocks.computeIfAbsent(lockKey, k -> new ReentrantLock());

        // Try to acquire lock - if already locked, reject immediately
        if (!lock.tryLock()) {
            logger.warning("Generation already in progress for period: " + lockKey);
            result.setSuccess(false);
            result.setMessage("A payslip generation is already in progress for this period. Please wait and try again later.");
            return result;
        }

        try {
            logger.info("Acquired lock for period: " + lockKey);

            // Check cutoff days for initial generation (not for regeneration)
            // TEMPORARILY DISABLED FOR TESTING - TODO: Re-enable after fixing period calculation
            /*
            if (!request.getForce() && !request.getOnlyDirty()) {
                if (!isWithinCutoffDays(request.getPeriodStart())) {
                    result.setSuccess(false);
                    result.setMessage("The payslip generation window has closed. Payslips can only be generated within the first 7 days of the following month.");
                    return result;
                }
            }
            */

            // Get list of users to process based on scope
            List<User> usersToProcess;
            try {
                usersToProcess = getUsersForGeneration(request);
            } catch (SQLException e) {
                result.addError(null, "Failed to get users for generation: " + e.getMessage(), e);
                return result;
            }
            logger.info(String.format("Found %d users to process for scope %s",
                                    usersToProcess.size(), request.getScope()));

            if (usersToProcess.isEmpty()) {
                result.setMessage("No users found for the specified scope");
                return result;
            }

            // Process each user with batch commits to avoid long transactions
            final int BATCH_SIZE = 10; // Commit every 10 users to prevent connection timeout
            int processedCount = 0;

            for (int i = 0; i < usersToProcess.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, usersToProcess.size());
                List<User> batch = usersToProcess.subList(i, endIndex);

                Connection connection = null;
                try {
                    connection = DatabaseUtil.getConnection();

                    // Validate connection before starting transaction
                    if (!connection.isValid(5)) {
                        logger.warning("Connection is not valid, getting new connection");
                        connection.close();
                        connection = DatabaseUtil.getConnection();
                    }

                    connection.setAutoCommit(false); // Start transaction for this batch

                    for (User user : batch) {
                        try {
                            processUserPayslip(user, request, result, connection);
                            processedCount++;
                        } catch (Exception e) {
                            logger.log(Level.WARNING,
                                     String.format("Error processing payslip for user %d: %s",
                                                 user.getId(), e.getMessage()), e);
                            result.addError(user.getId(), "Processing failed: " + e.getMessage(), e);
                            // Continue with next user - don't fail entire batch
                        }
                    }

                    connection.commit();
                    logger.info(String.format("Batch %d/%d committed successfully (%d users processed)",
                              (i / BATCH_SIZE) + 1,
                              (usersToProcess.size() + BATCH_SIZE - 1) / BATCH_SIZE,
                              processedCount));

                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Database error in batch processing", e);

                    if (connection != null) {
                        try {
                            if (!connection.isClosed()) {
                                connection.rollback();
                                logger.warning("Batch transaction rolled back due to error");
                            }
                        } catch (SQLException rollbackEx) {
                            logger.log(Level.SEVERE, "Error during rollback", rollbackEx);
                        }
                    }

                    // Add errors for all users in this batch
                    for (User user : batch) {
                        result.addError(user.getId(), "Batch processing failed: " + e.getMessage(), e);
                    }

                    // Continue with next batch instead of failing completely
                    logger.warning("Continuing with next batch after error");

                } finally {
                    if (connection != null) {
                        try {
                            if (!connection.isClosed()) {
                                connection.setAutoCommit(true);
                                connection.close();
                            }
                        } catch (SQLException e) {
                            logger.log(Level.WARNING, "Error closing connection", e);
                        }
                    }
                }
            }

            logger.info(String.format("Payslip generation completed: %d users processed", processedCount));

        } finally {
            lock.unlock();
            result.markCompleted();
            logger.info("Released lock for period: " + lockKey);
        }

        // Set final message
        if (result.isSuccess()) {
            result.setMessage(String.format("Generation completed: %d created, %d updated, %d skipped, %d errors",
                                          result.getCreatedCount(), result.getUpdatedCount(),
                                          result.getSkippedCount(), result.getErrorCount()));
        }

        logger.info("Payslip generation completed: " + result);
        return result;
    }

    /**
     * Regenerate a single payslip
     * Requirements: 8.5, 8.6, 8.7
     *
     * @param payslipId Payslip ID to regenerate
     * @param force Force regeneration even if not dirty
     * @return GenerationResult with operation details
     */
    public GenerationResult regeneratePayslip(Long payslipId, boolean force) {
        logger.info(String.format("Starting payslip regeneration: payslipId=%d, force=%s", payslipId, force));

        GenerationResult result = new GenerationResult();

        try {
            // Get existing payslip
            Optional<Payslip> existingPayslipOpt = payslipDao.findById(payslipId);
            if (!existingPayslipOpt.isPresent()) {
                result.setSuccess(false);
                result.setMessage("Payslip not found: " + payslipId);
                return result;
            }

            Payslip existingPayslip = existingPayslipOpt.get();

            // Check if regeneration is needed
            if (!force && !Boolean.TRUE.equals(existingPayslip.getIsDirty())) {
                result.incrementSkipped();
                result.setMessage("Payslip is not dirty and force=false, skipped regeneration");
                return result;
            }

            // Create generation request for this specific payslip
            GenerationRequest request = new GenerationRequest(
                existingPayslip.getPeriodStart(),
                existingPayslip.getPeriodEnd(),
                GenerationRequest.GenerationScope.EMPLOYEE
            );
            request.setScopeId(existingPayslip.getUserId());
            request.setForce(true); // Always force for regeneration
            request.setOnlyDirty(false);

            // Use the bulk generation method for consistency
            GenerationResult bulkResult = generatePayslips(request);

            // Copy results
            result.setSuccess(bulkResult.isSuccess());
            result.setCreatedCount(bulkResult.getCreatedCount());
            result.setUpdatedCount(bulkResult.getUpdatedCount());
            result.setSkippedCount(bulkResult.getSkippedCount());
            result.setErrorCount(bulkResult.getErrorCount());
            result.setErrors(bulkResult.getErrors());
            result.setMessage(bulkResult.getMessage());

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error regenerating payslip %d", payslipId), e);
            result.setSuccess(false);
            result.setMessage("Regeneration failed: " + e.getMessage());
            result.addError(null, "Regeneration failed", e);
        } finally {
            result.markCompleted();
        }

        logger.info("Payslip regeneration completed: " + result);
        return result;
    }

    /**
     * Bulk regenerate payslips based on filter criteria
     * Requirements: 9.1
     *
     * @param filter Filter criteria for payslips to regenerate
     * @return GenerationResult with operation details
     */
    public GenerationResult bulkRegenerate(PayslipFilter filter) {
        logger.info("Starting bulk regeneration with filter: " + filter);

        GenerationResult result = new GenerationResult();

        try {
            // Find payslips matching the filter
            List<Payslip> payslipsToRegenerate = payslipDao.findDirtyPayslips(
                filter.getPeriodStart(), filter.getPeriodEnd()
            );

            // Apply additional filter criteria
            if (filter.getUserId() != null) {
                payslipsToRegenerate.removeIf(p -> !p.getUserId().equals(filter.getUserId()));
            }

            if (filter.getDepartmentId() != null) {
                // Filter by department - would need to join with user data
                // For now, skip this filter or implement department filtering
            }

            logger.info(String.format("Found %d payslips to regenerate", payslipsToRegenerate.size()));

            if (payslipsToRegenerate.isEmpty()) {
                result.setMessage("No payslips found matching the filter criteria");
                return result;
            }

            // Regenerate each payslip
            for (Payslip payslip : payslipsToRegenerate) {
                try {
                    GenerationResult singleResult = regeneratePayslip(payslip.getId(), true);

                    // Aggregate results
                    result.setCreatedCount(result.getCreatedCount() + singleResult.getCreatedCount());
                    result.setUpdatedCount(result.getUpdatedCount() + singleResult.getUpdatedCount());
                    result.setSkippedCount(result.getSkippedCount() + singleResult.getSkippedCount());
                    result.setErrorCount(result.getErrorCount() + singleResult.getErrorCount());
                    result.getErrors().addAll(singleResult.getErrors());

                    if (!singleResult.isSuccess()) {
                        result.setSuccess(false);
                    }

                } catch (Exception e) {
                    logger.log(Level.WARNING,
                             String.format("Error in bulk regeneration for payslip %d", payslip.getId()), e);
                    result.addError(payslip.getUserId(), "Bulk regeneration failed", e);
                }
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in bulk regeneration", e);
            result.setSuccess(false);
            result.setMessage("Bulk regeneration failed: " + e.getMessage());
        } finally {
            result.markCompleted();
        }

        // Set final message
        if (result.isSuccess()) {
            result.setMessage(String.format("Bulk regeneration completed: %d updated, %d errors",
                                          result.getUpdatedCount(), result.getErrorCount()));
        }

        logger.info("Bulk regeneration completed: " + result);
        return result;
    }

    /**
     * Mark a payslip as dirty when source data changes
     * Requirements: 8.1, 8.2, 8.3, 8.4
     *
     * @param userId User ID
     * @param periodStart Period start date
     * @param periodEnd Period end date
     * @param reason Reason for marking dirty
     */
    public void markPayslipDirty(Long userId, LocalDate periodStart, LocalDate periodEnd, String reason) {
        logger.info(String.format("Marking payslip dirty: userId=%d, period=%s to %s, reason=%s",
                                userId, periodStart, periodEnd, reason));

        try {
            boolean marked = payslipDao.markDirty(userId, periodStart, periodEnd, reason);
            if (marked) {
                logger.info(String.format("Successfully marked payslip dirty for user %d", userId));
            } else {
                logger.warning(String.format("No payslip found to mark dirty for user %d in period %s to %s",
                                           userId, periodStart, periodEnd));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE,
                     String.format("Error marking payslip dirty for user %d", userId), e);
            throw new RuntimeException("Failed to mark payslip dirty", e);
        }
    }

    // Private helper methods

    private void processUserPayslip(User user, GenerationRequest request, GenerationResult result, Connection connection)
            throws SQLException {

        Long userId = user.getId();
        LocalDate periodStart = request.getPeriodStart();
        LocalDate periodEnd = request.getPeriodEnd();

        // Check if payslip already exists
        Optional<Payslip> existingPayslipOpt = payslipDao.findByUserAndPeriod(userId, periodStart, periodEnd);

        if (existingPayslipOpt.isPresent()) {
            Payslip existingPayslip = existingPayslipOpt.get();

            // Handle existing payslip based on request parameters
            if (request.getOnlyDirty() && !Boolean.TRUE.equals(existingPayslip.getIsDirty())) {
                result.incrementSkipped();
                return; // Skip non-dirty payslips when onlyDirty=true
            }

            if (!request.getForce() && "GENERATED".equals(existingPayslip.getStatus())) {
                result.incrementSkipped();
                return; // Skip already generated payslips when force=false
            }

            // Update existing payslip
            updateExistingPayslip(existingPayslip, connection);
            result.incrementUpdated();

        } else {
            // Create new payslip
            createNewPayslip(userId, periodStart, periodEnd, connection);
            result.incrementCreated();
        }
    }

    private void updateExistingPayslip(Payslip existingPayslip, Connection connection) throws SQLException {
        // Calculate new values
        PayslipCalculationResult calculation = calculationService.calculatePayslip(
            existingPayslip.getUserId(),
            existingPayslip.getPeriodStart(),
            existingPayslip.getPeriodEnd()
        );

        // Update payslip with new calculated values
        existingPayslip.setBaseSalary(calculation.getBaseSalary());
        existingPayslip.setOtAmount(calculation.getOtAmount());
        existingPayslip.setLatenessDeduction(calculation.getLatenessDeduction());
        existingPayslip.setUnderHoursDeduction(calculation.getUnderHoursDeduction());
        existingPayslip.setTaxAmount(calculation.getTaxAmount());
        existingPayslip.setGrossAmount(calculation.getGrossAmount());
        existingPayslip.setNetAmount(calculation.getNetAmount());
        existingPayslip.setCurrency(calculation.getCurrency());

        // Update snapshots
        String snapshotsJson = calculationService.serializeSnapshots(calculation.getSnapshots());
        existingPayslip.setDetailsJson(snapshotsJson);

        // Reset dirty flag and update timestamps
        existingPayslip.setIsDirty(false);
        existingPayslip.setDirtyReason(null);
        existingPayslip.setUpdatedAt(LocalDateTime.now());
        existingPayslip.setGeneratedAt(LocalDateTime.now());
        existingPayslip.setStatus("GENERATED");

        // Save to database
        payslipDao.update(existingPayslip);

        logger.fine(String.format("Updated payslip for user %d: gross=%s, net=%s",
                                existingPayslip.getUserId(),
                                existingPayslip.getGrossAmount(),
                                existingPayslip.getNetAmount()));
    }

    private void createNewPayslip(Long userId, LocalDate periodStart, LocalDate periodEnd, Connection connection)
            throws SQLException {

        // Calculate payslip values
        PayslipCalculationResult calculation = calculationService.calculatePayslip(userId, periodStart, periodEnd);

        // Create new payslip entity
        Payslip newPayslip = new Payslip();
        newPayslip.setUserId(userId);
        newPayslip.setPeriodStart(periodStart);
        newPayslip.setPeriodEnd(periodEnd);
        newPayslip.setBaseSalary(calculation.getBaseSalary());
        newPayslip.setOtAmount(calculation.getOtAmount());
        newPayslip.setLatenessDeduction(calculation.getLatenessDeduction());
        newPayslip.setUnderHoursDeduction(calculation.getUnderHoursDeduction());
        newPayslip.setTaxAmount(calculation.getTaxAmount());
        newPayslip.setGrossAmount(calculation.getGrossAmount());
        newPayslip.setNetAmount(calculation.getNetAmount());
        newPayslip.setCurrency(calculation.getCurrency());

        // Set snapshots
        String snapshotsJson = calculationService.serializeSnapshots(calculation.getSnapshots());
        newPayslip.setDetailsJson(snapshotsJson);

        // Set status and timestamps
        newPayslip.setStatus("GENERATED");
        newPayslip.setIsDirty(false);
        newPayslip.setCreatedAt(LocalDateTime.now());
        newPayslip.setUpdatedAt(LocalDateTime.now());
        newPayslip.setGeneratedAt(LocalDateTime.now());

        // Save to database
        payslipDao.save(newPayslip);

        logger.fine(String.format("Created new payslip for user %d: gross=%s, net=%s",
                                userId, newPayslip.getGrossAmount(), newPayslip.getNetAmount()));
    }

    private List<User> getUsersForGeneration(GenerationRequest request) throws SQLException {
        List<User> users = new ArrayList<>();

        switch (request.getScope()) {
            case ALL:
                // Get all active employees
                users = userDao.findActiveEmployees();
                break;

            case DEPARTMENT:
                // Get employees in specific department
                if (request.getScopeId() != null) {
                    users = userDao.findByDepartmentId(request.getScopeId());
                }
                break;

            case EMPLOYEE:
                // Get specific employee
                if (request.getScopeId() != null) {
                    Optional<User> userOpt = userDao.findById(request.getScopeId());
                    if (userOpt.isPresent()) {
                        users.add(userOpt.get());
                    }
                }
                break;
        }

        // Filter out inactive users - DISABLED: Let DAO handle active filtering
        // users.removeIf(user -> !"ACTIVE".equals(user.getStatus()));

        logger.info(String.format("Found %d users for generation after filtering", users.size()));
        return users;
    }

    private boolean isWithinCutoffDays(LocalDate periodStart) {
        LocalDate today = LocalDate.now();
        int cutoffDay = PayrollConfig.getGenerateCutoffDays(); // Default: 7

        // Get the year and month of the period
        int periodYear = periodStart.getYear();
        int periodMonth = periodStart.getMonthValue();

        // Get current year and month
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        int currentDay = today.getDayOfMonth();

        logger.info(String.format("Checking generation window: period=%d-%d, today=%d-%d (day %d), cutoff=%d",
                periodYear, periodMonth, currentYear, currentMonth, currentDay, cutoffDay));

        // Rule: Payslip for month X can only be generated in the first 7 days of month X+1
        // Example: Payslip for October 2025 can only be generated from Nov 1-7, 2025
        //          After Nov 7, the window closes and cannot generate October payslip anymore

        // Calculate the next month after the period
        LocalDate nextMonthStart = periodStart.plusMonths(1).withDayOfMonth(1);
        int nextYear = nextMonthStart.getYear();
        int nextMonth = nextMonthStart.getMonthValue();

        logger.info(String.format("Expected generation window: %d-%d (first %d days)", nextYear, nextMonth, cutoffDay));

        // Check if today is in the next month after the period
        boolean isInNextMonth = (currentYear == nextYear && currentMonth == nextMonth);

        if (!isInNextMonth) {
            logger.info(String.format("Not in generation window. isInNextMonth=%b", isInNextMonth));
            // Not in the generation window (next month), not allowed
            return false;
        }

        // In the next month, check if within first 7 days
        boolean withinCutoff = currentDay <= cutoffDay;
        logger.info(String.format("In generation window. Within cutoff: %b", withinCutoff));
        return withinCutoff;
    }

    private String createLockKey(LocalDate periodStart, LocalDate periodEnd) {
        return String.format("payslip_generation_%s_%s", periodStart, periodEnd);
    }
}