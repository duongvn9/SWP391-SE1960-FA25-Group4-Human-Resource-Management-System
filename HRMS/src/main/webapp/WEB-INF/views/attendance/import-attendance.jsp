<%@ page contentType="text/html;charset=UTF-8" language="java" %> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Import Attendance</title>
        <jsp:include page="../layout/head.jsp">
            <jsp:param name="pageTitle" value="attendance-record-emp" />
        </jsp:include>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/import-attendance.css" />
        <script src="${pageContext.request.contextPath}/assets/js/import-attendance.js"></script>
    </head>
    <body class="import-attendance-page">
        <!-- Header -->
        <jsp:include page="../layout/dashboard-header.jsp">
            <jsp:param name="pageTitle" value="attendance-record-emp" />
        </jsp:include>

        <!-- Main wrapper: sidebar + content -->
        <div class="main-wrapper">
            <jsp:include page="../layout/sidebar.jsp">
                <jsp:param name="currentPage" value="attendance-record-emp" />
            </jsp:include>

            <div class="content-area main-content">
                <h2 class="page-title">Import Attendance</h2>

                <!-- Tabs -->
                <div class="tab-header">
                    <button class="tab-btn upload-tab-btn" id="upload-btn">Upload File</button>
                    <button class="tab-btn manual-tab-btn" id="manual-btn">Manual Entry</button>
                </div>
                <hr class="tab-divider" />

                <!-- Upload File Tab -->
                <div id="upload" class="tab-content upload-tab">
                    <h3 class="section-title">Upload File (Excel)</h3>

                    <form class="upload-form" action="${pageContext.request.contextPath}/attendance/import" method="post" enctype="multipart/form-data">
                        <div class="form-group">
                            <label class="form-label" for="fileInput">Select File to Import:</label>
                            <div class="file-upload">
                                <input type="file" id="fileInput" name="file" accept=".xlsx,.csv" class="form-input file-input" />
                                <p id="fileName" class="form-text">(Accepted formats: .xlsx, .csv)</p>
                            </div>
                        </div>

                        <div class="form-group btn-group">
                            <button type="submit" id="preview" name="action" value="Preview" class="form-button btn-primary">Preview</button>
                            <button type="submit" id="import" name="action" value="Import" class="form-button btn-secondary">Import</button>
                        </div>

                        <!-- Messages -->
                        <c:if test="${not empty error}">
                            <div class="form-message error-message">${error}</div>
                        </c:if>
                        <c:if test="${not empty success}">
                            <div class="form-message success-message">${success}</div>
                        </c:if>

                        <!-- Preview Table -->
                        <c:if test="${not empty previewLogs}">
                            <h4 class="preview-title">Attendance Data (Preview)</h4>

                            <table class="preview-data-table">
                                <thead>
                                    <tr class="table-header">
                                        <th>Employee ID</th>
                                        <th>Employee Name</th>
                                        <th>Department</th>
                                        <th>Date</th>
                                        <th>Check-in</th>
                                        <th>Check-out</th>
                                        <th>Status</th>
                                        <th>Source</th>
                                        <th>Period</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="log" items="${previewLogs}">
                                        <tr class="data-row">
                                            <td>${log.userId}</td>
                                            <td>${log.employeeName}</td>
                                            <td>${log.department}</td>
                                            <td>${log.date}</td>
                                            <td>${log.checkIn}</td>
                                            <td>${log.checkOut}</td>
                                            <td>${log.status}</td>
                                            <td>${log.source}</td>
                                            <td>${log.period}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>

                            <!-- Pagination -->
                            <c:if test="${totalPages > 1}">
                                <div class="pagination preview-pagination">
                                    <c:if test="${currentPage > 1}">
                                        <a href="?action=Preview&page=${currentPage - 1}" class="page-btn prev-btn">Previous</a>
                                    </c:if>

                                    <c:forEach var="i" begin="1" end="${totalPages}">
                                        <c:choose>
                                            <c:when test="${i == currentPage}">
                                                <a href="javascript:void(0)" class="page-btn current-page">${i}</a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="?action=Preview&page=${i}" class="page-btn">${i}</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>

                                    <c:if test="${currentPage < totalPages}">
                                        <a href="?action=Preview&page=${currentPage + 1}" class="page-btn next-btn">Next</a>
                                    </c:if>
                                </div>
                            </c:if>
                        </c:if>
                    </form>
                </div>

                <!-- Manual Tab -->
                <div id="manual" class="tab-content manual-tab">
                    <h3 class="section-title">Manual Entry</h3>

                    <div id="manualFeedback" class="feedback-message"></div>

                    <table id="manualTable" class="excel-table manual-table">
                        <thead>
                            <tr class="table-header">
                                <th>Employee ID</th>
                                <th>Date</th>
                                <th>Check-in</th>
                                <th>Check-out</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr class="manual-row">
                                <td contenteditable="true" class="manual-cell"></td>
                                <td contenteditable="true" class="manual-cell"></td>
                                <td contenteditable="true" class="manual-cell"></td>
                                <td contenteditable="true" class="manual-cell"></td>
                                <td contenteditable="true" class="manual-cell"></td>
                            </tr>
                        </tbody>
                    </table>

                    <div class="manual-btn-wrapper">
                        <form id="manualImportForm" action="${pageContext.request.contextPath}/attendance/import" method="post">
                            <input type="hidden" name="action" value="ManualImport" />
                            <input type="hidden" id="manualData" name="manualData" />
                            <button type="submit" class="btn btn-import">Import</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
