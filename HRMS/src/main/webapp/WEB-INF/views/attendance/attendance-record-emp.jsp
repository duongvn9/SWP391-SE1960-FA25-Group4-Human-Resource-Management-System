<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>My Attendance</title>
    </head>
    <body>
        <h2>My Attendance Records</h2>

        <!-- 🔍 Filter Section -->
        <form id="filterForm">
            <label for="startDate">From:</label>
            <input type="date" id="startDate" name="startDate">

            <label for="endDate">To:</label>
            <input type="date" id="endDate" name="endDate">

            <label for="status">Status:</label>
            <select id="status" name="status">
                <option value="">All</option>
                <option value="Approved">Approved</option>
                <option value="Pending">Pending</option>
                <option value="Late">Late</option>
                <option value="Missing">Missing</option>
            </select>

            <label for="source">Source:</label>
            <select id="source" name="source">
                <option value="">All</option>
                <option value="App">App</option>
                <option value="Kiosk">Kiosk</option>
                <option value="Manual">Manual</option>
                <option value="Import">Import</option>
            </select>

            <label for="periodSelect">Select Period:</label>
            <select id="periodSelect" name="periodSelect">
                <option value="">-- All Periods --</option>
                <c:forEach var="p" items="${periodList}">
                    <option value="${p.id}">${p.name}</option>
                </c:forEach>
            </select>

            <button type="button" id="filterBtn">Filter</button>
            <button type="button" id="resetBtn">Reset</button>
        </form>
        <br/><br/>

        <!-- 🔹 Export Buttons -->
        <button type="button" id="exportXLSBtn">Export XLS</button>
        <button type="button" id="exportCSVBtn">Export CSV</button>
        <button type="button" id="exportPDFBtn">Export PDF</button>
        <br/><br/>

        <table id="attendanceTable" border="1" cellpadding="6">
            <thead>
                <tr>
                    <th>Select</th>
                    <th>Date</th>
                    <th>Check-in</th>
                    <th>Check-out</th>
                    <th>Status</th>
                    <th>Source</th>
                    <th>Period</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="record" items="${attendanceList}">
                    <tr>
                        <td><button type="button" class="selectAttendanceBtn">Select</button></td>
                        <td>${record.dateStr}</td>
                        <td>${record.checkInStr}</td>
                        <td>${record.checkOutStr}</td>
                        <td>${record.status}</td>
                        <td>${record.source}</td>
                        <td>${record.period}</td>
                    </tr>
                </c:forEach>
            </tbody>

        </table>


        <br/>

        <!-- 📄 Pop-up Detail Modal -->
        <div id="attendancePopup" style="display:none; border:1px solid #000; padding:15px; background:#fff; width:400px; position:absolute; top:20%; left:35%;">
            <h3>Attendance Details</h3>
            <p><strong>Date:</strong> <span id="popupDate"></span></p>
            <p><strong>Check-in:</strong> <span id="popupIn"></span></p>
            <p><strong>Check-out:</strong> <span id="popupOut"></span></p>
            <p><strong>Status:</strong> <span id="popupStatus"></span></p>
            <p><strong>Source:</strong> <span id="popupSource"></span></p>
            <p><strong>Notes / Reason:</strong> <span id="popupNotes"></span></p>
            <p><strong>Attachments:</strong> <span id="popupAttachments"></span></p>
            <p><strong>Locked By:</strong> <span id="popupLockedBy"></span></p>
            <p><strong>Locked At:</strong> <span id="popupLockedAt"></span></p>
            <p><strong>Audit Trail:</strong> <span id="popupAudit"></span></p>
            <button id="closePopup">Close</button>
        </div>

        <script>
            const rows = document.querySelectorAll('.attendance-row');
            const popup = document.getElementById('attendancePopup');
            const closeBtn = document.getElementById('closePopup');

            rows.forEach(row => {
                // Show detail popup on row click
                row.addEventListener('click', () => {
                    document.getElementById('popupDate').textContent = row.cells[1].textContent;
                    document.getElementById('popupIn').textContent = row.cells[2].textContent;
                    document.getElementById('popupOut').textContent = row.cells[3].textContent;
                    document.getElementById('popupStatus').textContent = row.cells[4].textContent;
                    document.getElementById('popupSource').textContent = row.cells[5].textContent;
                    document.getElementById('popupNotes').textContent = row.getAttribute('data-notes');
                    document.getElementById('popupAttachments').textContent = row.getAttribute('data-attachments');
                    document.getElementById('popupLockedBy').textContent = row.getAttribute('data-lockedby');
                    document.getElementById('popupLockedAt').textContent = row.getAttribute('data-lockedat');
                    document.getElementById('popupAudit').textContent = row.getAttribute('data-audit');

                    popup.style.display = 'block';
                });

                // Select button for attendance record
                const selectBtn = row.querySelector('.selectAttendanceBtn');
                selectBtn.addEventListener('click', (event) => {
                    event.stopPropagation(); // tránh click row mở popup
                    const attendanceId = row.getAttribute('data-employee'); // hoặc ID riêng nếu có
                    alert('Selected attendance ID: ' + attendanceId);
                    // TODO: logic thêm record này vào form kháng nghị
                });
            });

            closeBtn.addEventListener('click', () => {
                popup.style.display = 'none';
            });

            // Filter reset button
            document.getElementById('resetBtn').addEventListener('click', () => {
                document.getElementById('filterForm').reset();
            });

            // Export buttons
            document.getElementById('exportXLSBtn').addEventListener('click', () => {
                alert('Export XLS');
            });
            document.getElementById('exportCSVBtn').addEventListener('click', () => {
                alert('Export CSV');
            });
            document.getElementById('exportPDFBtn').addEventListener('click', () => {
                alert('Export PDF');
            });
        </script>
    </body>
</html>
