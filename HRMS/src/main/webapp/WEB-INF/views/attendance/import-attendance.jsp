<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Import Attendance</title>
        <jsp:include page="../layout/head.jsp">
            <jsp:param name="pageTitle" value="attendance-record-emp" />
        </jsp:include>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/import-attendance.css">
        <script src="${pageContext.request.contextPath}/assets/js/import-attendance.js"></script>
    </head>
    <body class="import-attendance-page">
        <div class="page-wrapper">
            <jsp:include page="../layout/dashboard-header.jsp">
                <jsp:param name="pageTitle" value="attendance-record-emp" />
            </jsp:include>

            <div class="main-container">
                <jsp:include page="../layout/sidebar.jsp">
                    <jsp:param name="currentPage" value="attendance-record-emp" />
                </jsp:include>

                <h2 class="page-title">Import Attendance</h2>

                <!-- Tabs -->
                <div class="tab-header">
                    <button class="tab-btn" id="upload-btn">Upload File</button>
                    <button class="tab-btn" id="manual-btn">Manual Entry</button>
                </div>
                <hr class="tab-divider"/>

                <!-- Upload File Tab -->
                <div id="upload" class="tab-content upload-tab">
                    <h3 class="section-title">Upload File (Excel / CSV)</h3>

                    <form class="upload-form" action="${pageContext.request.contextPath}/attendance/import" 
                          method="post" enctype="multipart/form-data">

                        <div class="file-upload">
                            <input type="file" id="fileInput" name="file" accept=".xlsx,.csv" class="file-input"/>
                            <p id="fileName" class="file-name"></p>
                        </div>

                        <div class="form-actions" style="margin-top: 10px;">
                            <input type="submit" id="preview" name="action" value="Preview" class="btn btn-preview"/>
                            <input type="submit" id="import" name="action" value="Import" class="btn btn-import"/>
                        </div>

                        <!-- Hiển thị thông báo -->
                        <c:if test="${not empty error}">
                            <div class="alert alert-error">${error}</div>
                        </c:if>
                        <c:if test="${not empty success}">
                            <div class="alert alert-success">${success}</div>
                        </c:if>

                        <!-- Bảng preview / import với phân trang -->
                        <c:if test="${not empty previewLogs}">
                            <h4 class="preview-title" style="margin-top: 20px;">Attendance Data (Preview)</h4>

                            <table class="preview-data-table" style="width: 100%; border-collapse: collapse;" border="1">
                                <thead>
                                    <tr>
                                        <th class="col-employee-id">Employee ID</th>
                                        <th class="col-employee-name">Employee Name</th>
                                        <th class="col-department">Department</th>
                                        <th class="col-date">Date</th>
                                        <th class="col-checkin">Check-in</th>
                                        <th class="col-checkout">Check-out</th>
                                        <th class="col-status">Status</th>
                                        <th class="col-source">Source</th>
                                        <th class="col-period">Period</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="log" items="${previewLogs}">
                                        <tr class="data-row">
                                            <td class="col-employee-id">${log.userId}</td>
                                            <td class="col-employee-name">${log.employeeName}</td>
                                            <td class="col-department">${log.department}</td>
                                            <td class="col-date">${log.date}</td>
                                            <td class="col-checkin">${log.checkIn}</td>
                                            <td class="col-checkout">${log.checkOut}</td>
                                            <td class="col-status">${log.status}</td>
                                            <td class="col-source">${log.source}</td>
                                            <td class="col-period">${log.period}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>

                            <!-- Phân trang -->
                            <c:if test="${totalPages > 1}">
                                <div class="pagination preview-pagination">
                                    <!-- Previous -->
                                    <c:if test="${currentPage > 1}">
                                        <a href="?action=Preview&page=${currentPage - 1}" class="page-btn prev-btn">Previous</a>
                                    </c:if>

                                    <!-- Nút số trang -->
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

                                    <!-- Next -->
                                    <c:if test="${currentPage < totalPages}">
                                        <a href="?action=Preview&page=${currentPage + 1}" class="page-btn next-btn">Next</a>
                                    </c:if>
                                </div>
                            </c:if>

                        </c:if>
                    </form>
                </div>

                <!-- Manual Entry Tab -->
                <div id="manual" class="tab-content manual-tab">
                    <h3 class="section-title">Manual Entry</h3>
                    <table id="manualTable" class="excel-table manual-table">
                        <thead>
                            <tr>
                                <th class="col-employee-id">Employee ID</th>
                                <th class="col-date">Date</th>
                                <th class="col-checkin">Check-in</th>
                                <th class="col-checkout">Check-out</th>
                                <th class="col-status">Status</th>
                                <th class="col-source">Source</th>
                                <th class="col-note">Note</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>
