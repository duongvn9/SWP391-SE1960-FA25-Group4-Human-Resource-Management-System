package group4.hrms.dto;

/**
 * DTO for payslip summary counters
 * Requirements: 2.3, 4.6
 */
public class PayslipSummaryCounters {

    private long generatedCount;
    private long dirtyCount;
    private long missingCount;
    private long totalInScope;

    // Constructors
    public PayslipSummaryCounters() {}

    public PayslipSummaryCounters(long generatedCount, long dirtyCount, long missingCount, long totalInScope) {
        this.generatedCount = generatedCount;
        this.dirtyCount = dirtyCount;
        this.missingCount = missingCount;
        this.totalInScope = totalInScope;
    }

    // Getters and Setters
    public long getGeneratedCount() {
        return generatedCount;
    }

    public void setGeneratedCount(long generatedCount) {
        this.generatedCount = generatedCount;
    }

    public long getDirtyCount() {
        return dirtyCount;
    }

    public void setDirtyCount(long dirtyCount) {
        this.dirtyCount = dirtyCount;
    }

    public long getMissingCount() {
        return missingCount;
    }

    public void setMissingCount(long missingCount) {
        this.missingCount = missingCount;
    }

    public long getTotalInScope() {
        return totalInScope;
    }

    public void setTotalInScope(long totalInScope) {
        this.totalInScope = totalInScope;
    }

    // Business methods
    public long getProcessedCount() {
        return generatedCount + dirtyCount;
    }

    public long getUnprocessedCount() {
        return missingCount;
    }

    public double getGeneratedPercentage() {
        if (totalInScope == 0) {
            return 0.0;
        }
        return (generatedCount * 100.0) / totalInScope;
    }

    public double getDirtyPercentage() {
        if (totalInScope == 0) {
            return 0.0;
        }
        return (dirtyCount * 100.0) / totalInScope;
    }

    public double getMissingPercentage() {
        if (totalInScope == 0) {
            return 0.0;
        }
        return (missingCount * 100.0) / totalInScope;
    }

    public double getCompletionPercentage() {
        if (totalInScope == 0) {
            return 100.0;
        }
        return (generatedCount * 100.0) / totalInScope;
    }

    public boolean hasIssues() {
        return dirtyCount > 0 || missingCount > 0;
    }

    public boolean isFullyProcessed() {
        return missingCount == 0 && dirtyCount == 0;
    }

    public long getTotalIssues() {
        return dirtyCount + missingCount;
    }

    // Display methods
    public String getGeneratedPercentageFormatted() {
        return String.format("%.1f%%", getGeneratedPercentage());
    }

    public String getDirtyPercentageFormatted() {
        return String.format("%.1f%%", getDirtyPercentage());
    }

    public String getMissingPercentageFormatted() {
        return String.format("%.1f%%", getMissingPercentage());
    }

    public String getCompletionPercentageFormatted() {
        return String.format("%.1f%%", getCompletionPercentage());
    }

    public String getStatusText() {
        if (isFullyProcessed()) {
            return "All payslips processed";
        } else if (hasIssues()) {
            return getTotalIssues() + " issues need attention";
        } else {
            return "Processing in progress";
        }
    }

    public String getStatusColor() {
        if (isFullyProcessed()) {
            return "success";
        } else if (dirtyCount > 0 || missingCount > 10) {
            return "warning";
        } else {
            return "info";
        }
    }

    @Override
    public String toString() {
        return String.format("PayslipSummaryCounters{generated=%d, dirty=%d, missing=%d, total=%d}",
                           generatedCount, dirtyCount, missingCount, totalInScope);
    }
}