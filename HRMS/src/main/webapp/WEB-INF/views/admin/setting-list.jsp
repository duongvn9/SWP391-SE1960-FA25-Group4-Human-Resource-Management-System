<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <jsp:include page="../layout/head.jsp">
                <jsp:param name="pageTitle" value="Setting List - HRMS" />
                <jsp:param name="pageCss" value="dashboard.css" />
            </jsp:include>
            <style>
                .main-content {
                    margin-left: 260px;
                    padding: 2rem 2rem 0 2rem;
                    min-height: calc(100vh - 64px);
                    display: flex;
                    flex-direction: column;
                }

                .content-area {
                    flex: 1;
                    margin-bottom: 2rem;
                }

                .dashboard-footer {
                    margin-left: -2rem;
                    margin-right: -2rem;
                    margin-bottom: 0;
                }

                .card {
                    border: none;
                    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                    margin-bottom: 2rem;
                }

                .table th {
                    background-color: #f8f9fa;
                    font-weight: 600;
                }

                .btn-action {
                    padding: 0.25rem 0.5rem;
                    font-size: 0.875rem;
                }

                .section-title {
                    font-size: 1.25rem;
                    font-weight: 600;
                    color: #333;
                    margin-bottom: 1rem;
                    padding-bottom: 0.5rem;
                    border-bottom: 2px solid #007bff;
                }

                @media (max-width: 768px) {
                    .main-content {
                        margin-left: 0;
                    }
                }
            </style>
        </head>

        <body>
            <jsp:include page="../layout/sidebar.jsp">
                <jsp:param name="currentPage" value="settings" />
            </jsp:include>

            <div class="main-content" id="main-content">
                <!-- Header -->
                <jsp:include page="../layout/dashboard-header.jsp" />

                <!-- Content Area -->
                <div class="content-area">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h2><i class="fas fa-cog"></i> Settings Management</h2>
                        <a href="${pageContext.request.contextPath}/settings/new" class="btn btn-primary">
                            <i class="fas fa-plus"></i> New Department
                        </a>
                    </div>

                    <!-- Success/Error/Info Messages -->
                    <c:if test="${not empty param.success}">
                        <div class="alert alert-success alert-dismissible fade show">
                            <i class="fas fa-check-circle"></i> ${param.success}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <c:if test="${not empty param.info}">
                        <div class="alert alert-info alert-dismissible fade show">
                            <i class="fas fa-info-circle"></i> ${param.info}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <c:if test="${not empty param.error}">
                        <div class="alert alert-danger alert-dismissible fade show">
                            <i class="fas fa-exclamation-circle"></i> ${param.error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <!-- DEPARTMENT TABLE -->
                    <div class="card">
                        <div class="card-body">
                            <h5 class="section-title">
                                <i class="fas fa-building"></i> Departments
                            </h5>

                            <!-- Department Filter -->
                            <form method="get" action="${pageContext.request.contextPath}/settings"
                                class="row g-3 mb-3">
                                <input type="hidden" name="posSearch" value="${posSearch}">
                                <input type="hidden" name="posSort" value="${posSort}">
                                <input type="hidden" name="posPage" value="${posPage}">

                                <div class="col-md-4">
                                    <input type="text" name="deptSearch" class="form-control"
                                        placeholder="Search departments..." value="${deptSearch}">
                                </div>
                                <div class="col-md-2">
                                    <select name="deptSort" class="form-select">
                                        <option value="">Sort By</option>
                                        <option value="id" ${deptSort=='id' ? 'selected' : '' }>ID</option>
                                        <option value="name" ${deptSort=='name' ? 'selected' : '' }>Name</option>
                                    </select>
                                </div>
                                <div class="col-md-2">
                                    <button type="submit" class="btn btn-primary w-100">
                                        <i class="fas fa-search"></i> Search
                                    </button>
                                </div>
                                <div class="col-md-2">
                                    <a href="${pageContext.request.contextPath}/settings?posSearch=${posSearch}&posSort=${posSort}&posPage=${posPage}"
                                        class="btn btn-secondary w-100">
                                        <i class="fas fa-times"></i> Clear
                                    </a>
                                </div>
                                <div class="col-md-2 text-muted">
                                    Total: ${deptTotal} departments
                                </div>
                            </form>

                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Name</th>
                                            <th>Created At</th>
                                            <th>Updated At</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="dept" items="${departmentList}">
                                            <tr>
                                                <td>${dept.id}</td>
                                                <td>${dept.name}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty dept.createdAt}">
                                                            ${dept.createdAt.toString().replace('T', ' ')}
                                                        </c:when>
                                                        <c:otherwise>-</c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty dept.updatedAt}">
                                                            ${dept.updatedAt.toString().replace('T', ' ')}
                                                        </c:when>
                                                        <c:otherwise>-</c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${dept.name == 'Human Resource' || dept.name == 'IT Support'}">
                                                            <span class="text-muted">No action available</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <a href="${pageContext.request.contextPath}/settings/edit?id=${dept.id}&type=Department"
                                                                class="btn btn-sm btn-warning btn-action">
                                                                <i class="fas fa-edit"></i> Edit
                                                            </a>
                                                            <button type="button"
                                                                class="btn btn-sm btn-danger btn-action deleteBtn"
                                                                data-id="${dept.id}" data-name="${dept.name}"
                                                                data-type="Department" data-bs-toggle="modal"
                                                                data-bs-target="#deleteModal">
                                                                <i class="fas fa-trash"></i> Delete
                                                            </button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        <c:if test="${empty departmentList}">
                                            <tr>
                                                <td colspan="5" class="text-center">No departments found</td>
                                            </tr>
                                        </c:if>
                                    </tbody>
                                </table>
                            </div>

                            <!-- Department Pagination -->
                            <c:if test="${deptTotalPages > 1}">
                                <nav>
                                    <ul class="pagination justify-content-center">
                                        <li class="page-item ${deptPage == 1 ? 'disabled' : ''}">
                                            <a class="page-link"
                                                href="?deptSearch=${deptSearch}&deptSort=${deptSort}&deptPage=${deptPage - 1}&posSearch=${posSearch}&posSort=${posSort}&posPage=${posPage}">Previous</a>
                                        </li>
                                        <c:forEach begin="1" end="${deptTotalPages}" var="i">
                                            <li class="page-item ${deptPage == i ? 'active' : ''}">
                                                <a class="page-link"
                                                    href="?deptSearch=${deptSearch}&deptSort=${deptSort}&deptPage=${i}&posSearch=${posSearch}&posSort=${posSort}&posPage=${posPage}">${i}</a>
                                            </li>
                                        </c:forEach>
                                        <li class="page-item ${deptPage == deptTotalPages ? 'disabled' : ''}">
                                            <a class="page-link"
                                                href="?deptSearch=${deptSearch}&deptSort=${deptSort}&deptPage=${deptPage + 1}&posSearch=${posSearch}&posSort=${posSort}&posPage=${posPage}">Next</a>
                                        </li>
                                    </ul>
                                </nav>
                            </c:if>
                        </div>
                    </div>

                    <!-- POSITION TABLE -->
                    <div class="card">
                        <div class="card-body">
                            <h5 class="section-title">
                                <i class="fas fa-user-tie"></i> Positions
                            </h5>

                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Name</th>
                                            <th>Code</th>
                                            <th>Job Level</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="pos" items="${positionList}">
                                            <tr>
                                                <td>${pos.id}</td>
                                                <td>${pos.name}</td>
                                                <td>${pos.value}</td>
                                                <td>${pos.priority}</td>
                                            </tr>
                                        </c:forEach>
                                        <c:if test="${empty positionList}">
                                            <tr>
                                                <td colspan="4" class="text-center">No positions found</td>
                                            </tr>
                                        </c:if>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                </div>

                <!-- Footer -->
                <jsp:include page="../layout/dashboard-footer.jsp" />
            </div>

            <!-- Delete Confirmation Modal -->
            <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel"
                aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="deleteModalLabel">Confirm Delete</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <p>Are you sure you want to delete "<strong id="deleteItemName"></strong>"?</p>
                            <p class="text-danger mb-0">This action cannot be undone.</p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">No, Cancel</button>
                            <button type="button" class="btn btn-danger" id="confirmDeleteBtn">Yes, Delete</button>
                        </div>
                    </div>
                </div>
            </div>

            <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/settings/delete"
                style="display: none;">
                <input type="hidden" name="id" id="deleteId">
                <input type="hidden" name="type" id="deleteType">
            </form>

            <script>
                // Handle delete modal
                var deleteModal = document.getElementById('deleteModal');
                deleteModal.addEventListener('show.bs.modal', function (event) {
                    var button = event.relatedTarget;
                    var id = button.getAttribute('data-id');
                    var type = button.getAttribute('data-type');
                    var name = button.getAttribute('data-name');

                    document.getElementById('deleteItemName').textContent = name;
                    document.getElementById('deleteId').value = id;
                    document.getElementById('deleteType').value = type;
                });

                document.getElementById('confirmDeleteBtn').addEventListener('click', function () {
                    document.getElementById('deleteForm').submit();
                });

                // Auto-dismiss success and info messages after 3 seconds
                window.addEventListener('DOMContentLoaded', function () {
                    var successAlert = document.querySelector('.alert-success');
                    var infoAlert = document.querySelector('.alert-info');

                    function dismissAlert(alert) {
                        if (alert) {
                            setTimeout(function () {
                                alert.style.transition = 'opacity 0.5s';
                                alert.style.opacity = '0';
                                setTimeout(function () {
                                    alert.remove();
                                }, 500);
                            }, 3000);
                        }
                    }

                    dismissAlert(successAlert);
                    dismissAlert(infoAlert);
                });
            </script>
        </body>

        </html>