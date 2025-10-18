<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Import Attendance</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/import-attendance.css">
        <script src="${pageContext.request.contextPath}/assets/js/import-attendance.js"></script>
    </head>
    <body class="import-attendance-page">
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

            <form class="upload-form" 
                  action="${pageContext.request.contextPath}/attendance/import" 
                  method="post" enctype="multipart/form-data">

                <div class="file-upload">
                    <input type="file" id="fileInput" name="file" accept=".xlsx,.csv" class="file-input"/>
                    <p id="fileName" class="file-name"></p>
                </div>

                <table border="1" cellpadding="6" id="filePreview" class="preview-table"></table>

                <div class="form-actions" style="margin-top: 10px;">
                    <input type="submit" id="preview" name="action" value="Preview" class="btn btn-preview"/>
                    <input type="submit" id="import" name="action" value="Import" class="btn btn-import"/>
                </div>

                <!-- Hiển thị thông báo -->
                <c:if test="${not empty error}">
                    <div class="alert alert-error" style="color: red; margin-top: 10px;">
                        ${error}
                    </div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="alert alert-success" style="color: green; margin-top: 10px;">
                        ${success}
                    </div>
                </c:if>

                <!-- Khu vực hiển thị bảng preview dữ liệu -->
                <c:if test="${not empty previewLogs}">
                    <h4 class="preview-title" style="margin-top: 20px;">Preview Data</h4>

                    <table border="1" cellpadding="6"
                           class="preview-data-table"
                           style="border-collapse: collapse; margin-top: 10px; width: 100%;">
                        <thead>
                            <tr>
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
                                <tr>
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
                </c:if>
            </form>
        </div>

        <!-- Manual Entry Tab -->
        <div id="manual" class="tab-content manual-tab">
            <h3 class="section-title">Manual Entry (Excel-like Grid)</h3>
            <table id="manualTable" class="excel-table manual-table">
                <thead>
                    <tr>
                        <th>Employee ID</th>
                        <th>Date</th>
                        <th>Check-in</th>
                        <th>Check-out</th>
                        <th>Status</th>
                        <th>Source</th>
                        <th>Note</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>
    </body>
</html>
