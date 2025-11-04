<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

            <%-- Reusable Pagination Component Expected attributes: - pagination: PaginationMetadata object with: -
                currentPage (int) - totalPages (int) - totalItems (long) - hasNext (boolean) - hasPrevious (boolean) -
                pageSize (int) Usage: <jsp:include page="../layout/pagination.jsp" />

            Note: This component preserves all query parameters from the current request
            and only updates the 'page' parameter for navigation.
            --%>

            <c:if test="${not empty pagination and pagination.totalPages > 0}">
                <div class="pagination-container">
                    <nav aria-label="Page navigation">
                        <ul class="pagination justify-content-center">

                            <!-- Previous Button -->
                            <li class="page-item ${!pagination.hasPrevious ? 'disabled' : ''}">
                                <c:choose>
                                    <c:when test="${pagination.hasPrevious}">
                                        <a class="page-link" href="javascript:void(0);"
                                            data-page="${pagination.currentPage - 1}" aria-label="Previous">
                                            <span aria-hidden="true">&laquo; Previous</span>
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="page-link" aria-label="Previous">
                                            <span aria-hidden="true">&laquo; Previous</span>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </li>

                            <!-- Page Numbers -->
                            <c:choose>
                                <%-- Show all pages if total pages <=7 --%>
                                    <c:when test="${pagination.totalPages <= 7}">
                                        <c:forEach begin="1" end="${pagination.totalPages}" var="pageNum">
                                            <li class="page-item ${pagination.currentPage == pageNum ? 'active' : ''}">
                                                <c:choose>
                                                    <c:when test="${pagination.currentPage == pageNum}">
                                                        <span class="page-link">${pageNum}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a class="page-link" href="javascript:void(0);"
                                                            data-page="${pageNum}">
                                                            ${pageNum}
                                                        </a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </li>
                                        </c:forEach>
                                    </c:when>

                                    <%-- Show pagination with ellipsis for many pages --%>
                                        <c:otherwise>
                                            <%-- Always show first page --%>
                                                <li class="page-item ${pagination.currentPage == 1 ? 'active' : ''}">
                                                    <c:choose>
                                                        <c:when test="${pagination.currentPage == 1}">
                                                            <span class="page-link">1</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <a class="page-link" href="javascript:void(0);"
                                                                data-page="1">
                                                                1
                                                            </a>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </li>

                                                <%-- Show ellipsis if current page> 3 --%>
                                                    <c:if test="${pagination.currentPage > 3}">
                                                        <li class="page-item disabled">
                                                            <span class="page-link">...</span>
                                                        </li>
                                                    </c:if>

                                                    <%-- Show pages around current page --%>
                                                        <c:forEach begin="${pagination.currentPage - 1}"
                                                            end="${pagination.currentPage + 1}" var="pageNum">
                                                            <c:if
                                                                test="${pageNum > 1 and pageNum < pagination.totalPages}">
                                                                <li
                                                                    class="page-item ${pagination.currentPage == pageNum ? 'active' : ''}">
                                                                    <c:choose>
                                                                        <c:when
                                                                            test="${pagination.currentPage == pageNum}">
                                                                            <span class="page-link">${pageNum}</span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <a class="page-link"
                                                                                href="javascript:void(0);"
                                                                                data-page="${pageNum}">
                                                                                ${pageNum}
                                                                            </a>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </li>
                                                            </c:if>
                                                        </c:forEach>

                                                        <%-- Show ellipsis if current page < totalPages - 2 --%>
                                                            <c:if
                                                                test="${pagination.currentPage < pagination.totalPages - 2}">
                                                                <li class="page-item disabled">
                                                                    <span class="page-link">...</span>
                                                                </li>
                                                            </c:if>

                                                            <%-- Always show last page --%>
                                                                <li
                                                                    class="page-item ${pagination.currentPage == pagination.totalPages ? 'active' : ''}">
                                                                    <c:choose>
                                                                        <c:when
                                                                            test="${pagination.currentPage == pagination.totalPages}">
                                                                            <span
                                                                                class="page-link">${pagination.totalPages}</span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <a class="page-link"
                                                                                href="javascript:void(0);"
                                                                                data-page="${pagination.totalPages}">
                                                                                ${pagination.totalPages}
                                                                            </a>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </li>
                                        </c:otherwise>
                            </c:choose>

                            <!-- Next Button -->
                            <li class="page-item ${!pagination.hasNext ? 'disabled' : ''}">
                                <c:choose>
                                    <c:when test="${pagination.hasNext}">
                                        <a class="page-link" href="javascript:void(0);"
                                            data-page="${pagination.currentPage + 1}" aria-label="Next">
                                            <span aria-hidden="true">Next &raquo;</span>
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="page-link" aria-label="Next">
                                            <span aria-hidden="true">Next &raquo;</span>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </li>
                        </ul>
                    </nav>

                    <!-- Pagination Info -->
                    <div class="pagination-info text-center mt-2">
                        <small class="text-muted">
                            Page ${pagination.currentPage} of ${pagination.totalPages}
                            (${pagination.totalItems} total ${pagination.totalItems == 1 ? 'item' : 'items'})
                        </small>
                    </div>
                </div>

                <!-- JavaScript to handle page navigation while preserving filters -->
                <script>
                    document.addEventListener('DOMContentLoaded', function () {
                        // Add click event to all pagination links
                        document.querySelectorAll('.pagination .page-link[data-page]').forEach(function (link) {
                            link.addEventListener('click', function (e) {
                                e.preventDefault();
                                const pageNumber = this.getAttribute('data-page');

                                // Get current URL parameters
                                const urlParams = new URLSearchParams(window.location.search);

                                // Update or add the page parameter
                                urlParams.set('page', pageNumber);

                                // Navigate to the new URL with updated page parameter
                                window.location.href = window.location.pathname + '?' + urlParams.toString();
                            });
                        });
                    });
                </script>
            </c:if>

            <style>
                .pagination-container {
                    margin: 20px 0;
                }

                .pagination .page-link {
                    color: #007bff;
                    border: 1px solid #dee2e6;
                    padding: 0.5rem 0.75rem;
                    margin: 0 2px;
                    border-radius: 0.25rem;
                    transition: all 0.2s;
                    cursor: pointer;
                }

                .pagination .page-link:hover {
                    background-color: #e9ecef;
                    border-color: #dee2e6;
                }

                .pagination .page-item.active .page-link {
                    background-color: #007bff;
                    border-color: #007bff;
                    color: white;
                    font-weight: bold;
                    cursor: default;
                }

                .pagination .page-item.disabled .page-link,
                .pagination .page-item.disabled span {
                    color: #6c757d;
                    pointer-events: none;
                    background-color: #fff;
                    border-color: #dee2e6;
                    cursor: not-allowed;
                }

                .pagination-info {
                    color: #6c757d;
                    font-size: 0.875rem;
                }

                /* Responsive design for mobile */
                @media (max-width: 576px) {
                    .pagination .page-link {
                        padding: 0.375rem 0.5rem;
                        font-size: 0.875rem;
                        margin: 0 1px;
                    }

                    /* Hide middle page numbers on mobile, keep first, last, and current */
                    .pagination .page-item:not(.active):not(:first-child):not(:last-child):not(:nth-child(2)):not(:nth-last-child(2)) {
                        display: none;
                    }

                    .pagination-info {
                        font-size: 0.75rem;
                    }
                }
            </style>