<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>My Attendance</title>
        <style>
            .half-day-badge {
                background-color: #ffc107;
                padding: 4px 10px;
                border-radius: 4px;
                font-size: 0.85em;
                font-weight: 500;
                display: inline-block;
                color: #000;
            }

            .status-badge {
                padding: 3px 8px;
                border-radius: 3px;
                font-size: 0.9em;
                font-weight: 500;
            }

            .status-PRESENT {
                background-color: #28a745;
                color: white;
            }

            .status-ABSENT {
                background-color: #dc3545;
                color: white;
            }

            .status-LATE {
                background-color: #ffc107;
                color: #000;
            }

            .status-EARLY_LEAVE {
                background-color: #fd7e14;
                color: white;
            }

            .attendance-row {
                cursor: pointer;
            }

            .attendance-row:hover {
                background-color: #f5f5f5;
            }

            #attendancePopup {
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                border-radius: 8px;
            }
        </style>
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
                    <th>Working Hours</th>
                    <th>Status</th>
                    <th>Half-Day Leave</th>
                    <th>Notes</th>
                </tr>
            </thead>
            <tbody>
            <c:forEach var="record" items="${attendanceList}">
                <tr class="attendance-row"
                    data-userid="${record.userId}"
                    data-notes="${record.notes}"
                    data-halfdayinfo="${record.halfDayLeaveInfo}">

                    <td>
                        <button type="button" class="selectAttendanceBtn">Select</button>
                    </td>
                    <td>${record.workDateFormatted}</td>
                    <td>${record.checkInTimeFormatted}</td>
                    <td>${record.checkOutTimeFormatted}</td>
                    <td>${record.workingHoursFormatted}</td>
                    <td>
                        <span class="status-badge status-${record.status}">
                            ${record.statusDisplay}
                        </span>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${record.hasHalfDayLeave}">
                                <span class="half-day-badge" style="background-color: #ffc107; padding: 2px 8px; border-radius: 3px; font-size: 0.9em;">
                                    ${record.halfDayLeaveInfo}
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span style="color: #999;">-</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty record.notes}">
                                ${record.notes}
                            </c:when>
                            <c:otherwise>
                                <span style="color: #999;">-</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty attendanceList}">
                <tr>
                    <td colspan="8" style="text-align:center;">No records found</td>
                </tr>
            </c:if>
        </tbody>
    </table>

    <br/>



    <!-- 📄 Pop-up Detail Modal -->
    <div id="attendancePopup" style="display:none; border:1px solid #000; padding:15px; background:#fff; width:450px; position:absolute; top:20%; left:35%; z-index: 1000;">
        <h3>Attendance Details</h3>
        <p><strong>Date:</strong> <span id="popupDate"></span></p>
        <p><strong>Check-in:</strong> <span id="popupIn"></span></p>
        <p><strong>Check-out:</strong> <span id="popupOut"></span></p>
        <p><strong>Working Hours:</strong> <span id="popupWorkingHours"></span></p>
        <p><strong>Status:</strong> <span id="popupStatus"></span></p>
        <p><strong>Half-Day Leave:</strong> <span id="popupHalfDayInfo"></span></p>
        <p><strong>Notes / Reason:</strong> <span id="popupNotes"></span></p>
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
                document.getElementById('popupWorkingHours').textContent = row.cells[4].textContent;
                document.getElementById('popupStatus').textContent = row.cells[5].textContent.trim();

                const halfDayInfo = row.getAttribute('data-halfdayinfo');
                document.getElementById('popupHalfDayInfo').textContent = halfDayInfo || '-';

                const notes = row.getAttribute('data-notes');
                document.getElementById('popupNotes').textContent = notes || '-';

                popup.style.display = 'block';
            });

            // Select button for attendance record
            const selectBtn = row.querySelector('.selectAttendanceBtn');
            selectBtn.addEventListener('click', (event) => {
                event.stopPropagation(); // tránh click row mở popup
                const userId = row.getAttribute('data-userid');
                alert('Selected attendance for user ID: ' + userId);
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
