<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Import Attendance</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/import-attendance.css">
        <script src="${pageContext.request.contextPath}/assets/js/import-attendance.js"></script>
    </head>
    <body>
        <h2>Import Attendance</h2>

        <!-- Tabs -->
        <div>
            <button class="tab-btn" id="upload-btn">Upload File</button>
            <button class="tab-btn" id="google-btn">Google Sheets</button>
            <button class="tab-btn" id="manual-btn">Manual Entry</button>
        </div>

        <hr/>

        <!-- Upload File Tab -->
        <div id="upload" class="tab-content">
            <h3>Upload File (Excel / CSV)</h3>
            <form action="${pageContext.request.contextPath}/attendance/import" method="post" enctype="multipart/form-data">
                <input type="file" id="fileInput" name="file" accept=".xlsx,.csv"/>
                <p id="fileName"></p>
                <table border="1" cellpadding="6" id="filePreview"></table>
                <div style="margin-top: 10px;">
                    <input type="submit" id="preview" name="action" value="Preview"/>
                    <input type="submit" id="import" name="action" value="Import"/>
                </div>

                <!-- Hiển thị thông báo -->
                <c:if test="${not empty error}">
                    <div style="color: red; margin-top: 10px;">
                        ${error}
                    </div>
                </c:if>
                <c:if test="${not empty success}">
                    <div style="color: green; margin-top: 10px;">
                        ${success}
                    </div>
                </c:if>

                <!-- Khu vực hiển thị bảng preview dữ liệu -->
                <c:if test="${not empty previewLogs}">
                    <h4 style="margin-top: 20px;">Preview Data</h4>
                    <table border="1" cellpadding="6" style="border-collapse: collapse; margin-top: 10px; width: 100%;">
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
                                    <td>${log.employeeId}</td>
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

        <!-- Google Sheets Tab -->
        <div id="google" class="tab-content">
            <h3>Import from Google Sheets</h3>
            <button onclick="alert('Google OAuth...')">Connect Google Account</button><br/><br/>
            <label>Sheet URL:</label>
            <input type="text" placeholder="Enter Google Sheet URL" size="50"/><br/><br/>
            <button onclick="alert('Loading sheet data...')">Load Sheet</button>
            <button onclick="alert('Import done')">Import</button>
        </div>

        <!-- Manual Entry Tab -->
        <div id="manual" class="tab-content">
            <h3>Manual Entry (Excel-like Grid)</h3>
            <table id="manualTable" class="excel-table">
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
