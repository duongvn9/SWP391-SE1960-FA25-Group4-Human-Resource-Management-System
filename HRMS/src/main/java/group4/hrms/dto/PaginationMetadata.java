package group4.hrms.dto;

/**
 * Helper class for pagination metadata
 * Contains information about current page, total pages, and navigation
 */
public class PaginationMetadata {

    private int currentPage;
    private int pageSize;
    private long totalItems;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    // Constructors
    public PaginationMetadata() {
    }

    public PaginationMetadata(int currentPage, int pageSize, long totalItems) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = calculateTotalPages(totalItems, pageSize);
        this.hasNext = currentPage < totalPages;
        this.hasPrevious = currentPage > 1;
    }

    // Static factory method
    public static PaginationMetadata create(int currentPage, int pageSize, long totalItems) {
        return new PaginationMetadata(currentPage, pageSize, totalItems);
    }

    // Helper method to calculate total pages
    private int calculateTotalPages(long totalItems, int pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalItems / pageSize);
    }

    // Getters and Setters
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        this.hasNext = currentPage < totalPages;
        this.hasPrevious = currentPage > 1;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        this.totalPages = calculateTotalPages(totalItems, pageSize);
        this.hasNext = currentPage < totalPages;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
        this.totalPages = calculateTotalPages(totalItems, pageSize);
        this.hasNext = currentPage < totalPages;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    // Helper methods
    public int getStartItem() {
        if (totalItems == 0) {
            return 0;
        }
        return (currentPage - 1) * pageSize + 1;
    }

    public int getEndItem() {
        int end = currentPage * pageSize;
        return (int) Math.min(end, totalItems);
    }

    public int getNextPage() {
        return hasNext ? currentPage + 1 : currentPage;
    }

    public int getPreviousPage() {
        return hasPrevious ? currentPage - 1 : currentPage;
    }

    public boolean isEmpty() {
        return totalItems == 0;
    }

    @Override
    public String toString() {
        return "PaginationMetadata{" +
                "currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", totalItems=" + totalItems +
                ", totalPages=" + totalPages +
                ", hasNext=" + hasNext +
                ", hasPrevious=" + hasPrevious +
                '}';
    }
}
