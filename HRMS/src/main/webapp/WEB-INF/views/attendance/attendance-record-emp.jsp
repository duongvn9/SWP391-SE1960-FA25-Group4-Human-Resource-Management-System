<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

            <button type="button" id="filterBtn">Filter</button>
        </form>

        <br>

        <table id="attendanceTable" border="1" cellpadding="6">
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Check-in</th>
                    <th>Check-out</th>
                    <th>Status</th>
                    <th>Source</th>
                    <th>Period</th>
                    <th>Locked</th>
                </tr>
            </thead>
            <tbody>
                <tr class="attendance-row" data-employee="E001" data-notes="Came late due to traffic" 
                    data-attachments="none" data-lockedby="HR001" data-lockedat="2025-09-30" 
                    data-audit="Edited by HR 2025-09-29 09:30">
                    <td>2025-10-01</td>
                    <td>08:45</td>
                    <td>17:30</td>
                    <td>Late</td>
                    <td>App</td>
                    <td>Oct-2025</td>
                    <td>No</td>
                </tr>
                <tr class="attendance-row" data-employee="E001" data-notes="Normal day" 
                    data-attachments="screenshot.png" data-lockedby="HR002" data-lockedat="2025-09-30" 
                    data-audit="Imported automatically">
                    <td>2025-10-02</td>
                    <td>08:00</td>
                    <td>17:00</td>
                    <td>Approved</td>
                    <td>Import</td>
                    <td>Oct-2025</td>
                    <td>Yes</td>
                </tr>
            </tbody>
        </table>

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
                row.addEventListener('click', () => {
                    // Lấy dữ liệu từ dòng
                    document.getElementById('popupDate').textContent = row.cells[0].textContent;
                    document.getElementById('popupIn').textContent = row.cells[1].textContent;
                    document.getElementById('popupOut').textContent = row.cells[2].textContent;
                    document.getElementById('popupStatus').textContent = row.cells[3].textContent;
                    document.getElementById('popupSource').textContent = row.cells[4].textContent;
                    document.getElementById('popupNotes').textContent = row.getAttribute('data-notes');
                    document.getElementById('popupAttachments').textContent = row.getAttribute('data-attachments');
                    document.getElementById('popupLockedBy').textContent = row.getAttribute('data-lockedby');
                    document.getElementById('popupLockedAt').textContent = row.getAttribute('data-lockedat');
                    document.getElementById('popupAudit').textContent = row.getAttribute('data-audit');

                    popup.style.display = 'block';
                });
            });

            closeBtn.addEventListener('click', () => {
                popup.style.display = 'none';
            });
        </script>
    </body>
</html>
