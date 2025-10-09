<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
            <button class="tab-btn" id="upload-btn" onclick="showTab('upload')" aria-selected="false">Upload File</button>
            <button class="tab-btn" id="google-btn" onclick="showTab('google')" aria-selected="false">Google Sheets</button>
            <button class="tab-btn active" id="manual-btn" onclick="showTab('manual')" aria-selected="true">Manual Entry</button>
        </div>

        <hr/>

        <div id="upload" class="tab-content" style="display:none;">
            <h3>Upload File (Excel / CSV)</h3>

            <!-- Form gửi file lên servlet -->
            <form action="${pageContext.request.contextPath}/attendance/import" method="post" enctype="multipart/form-data">
                <input type="file" id="fileInput" name="file" accept=".xlsx,.csv" required/>
                <p id="fileName"></p>
                <table border="1" cellpadding="6" id="filePreview"></table>
                <div style="margin-top: 10px;">
                    <!-- Nút Preview -->
                    <input type="submit" id="preview" name="action" value="Preview" />

                    <!-- Nút Import -->
                    <input type="submit" id="import" name="action" value="Import" />
                </div>
            </form>
        </div>


        <!-- Google Sheets Tab (unchanged) -->
        <div id="google" class="tab-content" style="display:none;">
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
            <button onclick="addRow(document.getElementById('manualTable').getElementsByTagName('tbody')[0])">Add Row</button>
            <button onclick="validateManual()">Validate</button>
            <button onclick="alert('Import success!')">Import</button>
            <br/><br/>
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