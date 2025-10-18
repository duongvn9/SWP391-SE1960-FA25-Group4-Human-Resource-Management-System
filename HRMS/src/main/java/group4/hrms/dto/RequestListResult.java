package group4.hrms.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO for request list results with pagination metadata
 * Contains filtered requests and pagination information
 */
public class RequestListResult {

    private List<RequestDto> requests;                              // For "my" and "subordinate" scope
    private Map<String, List<RequestDto>> requestsByDepartment;     // For "all" scope
    private PaginationMetadata pagination;
    private RequestListFilter appliedFilters;

    // Constructors
    public RequestListResult() {
        this.requests = new ArrayList<>();
        this.requestsByDepartment = new LinkedHashMap<>();
    }

    public RequestListResult(List<RequestDto> requests, PaginationMetadata pagination) {
        this.requests = requests;
        this.pagination = pagination;
        this.requestsByDepartment = new LinkedHashMap<>();
    }

    public RequestListResult(Map<String, List<RequestDto>> requestsByDepartment, PaginationMetadata pagination) {
        this.requestsByDepartment = requestsByDepartment;
        this.pagination = pagination;
        this.requests = new ArrayList<>();
    }

    // Static factory methods
    public static RequestListResult createFlatList(List<RequestDto> requests, PaginationMetadata pagination) {
        return new RequestListResult(requests, pagination);
    }

    public static RequestListResult createGroupedByDepartment(Map<String, List<RequestDto>> requestsByDepartment,
                                                               PaginationMetadata pagination) {
        return new RequestListResult(requestsByDepartment, pagination);
    }

    // Getters and Setters
    public List<RequestDto> getRequests() {
        return requests;
    }

    public void setRequests(List<RequestDto> requests) {
        this.requests = requests;
    }

    public Map<String, List<RequestDto>> getRequestsByDepartment() {
        return requestsByDepartment;
    }

    public void setRequestsByDepartment(Map<String, List<RequestDto>> requestsByDepartment) {
        this.requestsByDepartment = requestsByDepartment;
    }

    public PaginationMetadata getPagination() {
        return pagination;
    }

    public void setPagination(PaginationMetadata pagination) {
        this.pagination = pagination;
    }

    public RequestListFilter getAppliedFilters() {
        return appliedFilters;
    }

    public void setAppliedFilters(RequestListFilter appliedFilters) {
        this.appliedFilters = appliedFilters;
    }

    // Helper methods
    public boolean isGroupedByDepartment() {
        return requestsByDepartment != null && !requestsByDepartment.isEmpty();
    }

    public boolean isEmpty() {
        if (isGroupedByDepartment()) {
            return requestsByDepartment.isEmpty();
        }
        return requests == null || requests.isEmpty();
    }

    public int getTotalRequestCount() {
        if (isGroupedByDepartment()) {
            return requestsByDepartment.values().stream()
                    .mapToInt(List::size)
                    .sum();
        }
        return requests != null ? requests.size() : 0;
    }

    public int getDepartmentCount() {
        return requestsByDepartment != null ? requestsByDepartment.size() : 0;
    }

    @Override
    public String toString() {
        return "RequestListResult{" +
                "requestsCount=" + (requests != null ? requests.size() : 0) +
                ", departmentCount=" + getDepartmentCount() +
                ", pagination=" + pagination +
                ", appliedFilters=" + appliedFilters +
                '}';
    }
}
