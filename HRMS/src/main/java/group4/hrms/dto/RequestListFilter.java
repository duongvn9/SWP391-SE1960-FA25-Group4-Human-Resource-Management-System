package group4.hrms.dto;

import java.time.LocalDate;

/**
 * DTO for filtering request list
 * Contains all filter criteria for the request list page
 */
public class RequestListFilter {

    private String scope;              // "my", "subordinate", "all"
    private Long requestTypeId;        // null for "all"
    private String status;             // "PENDING", "APPROVED", "REJECTED", "all"
    private boolean showCancelled;     // default false
    private LocalDate fromDate;
    private LocalDate toDate;
    private Long employeeId;           // for employee filter
    private String searchKeyword;
    private int page;                  // default 1
    private int pageSize;              // default 8

    // Constructors
    public RequestListFilter() {
        this.scope = "my";
        this.status = "all";
        this.showCancelled = false;
        this.page = 1;
        this.pageSize = 8;
    }

    // Getters and Setters
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Long getRequestTypeId() {
        return requestTypeId;
    }

    public void setRequestTypeId(Long requestTypeId) {
        this.requestTypeId = requestTypeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isShowCancelled() {
        return showCancelled;
    }

    public void setShowCancelled(boolean showCancelled) {
        this.showCancelled = showCancelled;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    // Helper methods
    public boolean hasDateRange() {
        return fromDate != null || toDate != null;
    }

    public boolean hasEmployeeFilter() {
        return employeeId != null;
    }

    public boolean hasSearch() {
        return searchKeyword != null && !searchKeyword.trim().isEmpty();
    }

    public boolean hasTypeFilter() {
        return requestTypeId != null;
    }

    public boolean hasStatusFilter() {
        return status != null && !"all".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "RequestListFilter{" +
                "scope='" + scope + '\'' +
                ", requestTypeId=" + requestTypeId +
                ", status='" + status + '\'' +
                ", showCancelled=" + showCancelled +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", employeeId=" + employeeId +
                ", searchKeyword='" + searchKeyword + '\'' +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
