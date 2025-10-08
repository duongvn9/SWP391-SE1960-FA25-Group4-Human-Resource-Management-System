<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Attendance Management</title>
    </head>
    <body>
        <h2>Attendance Period Management</h2>

        <!-- ========== FILTER SECTION ========== -->
        <form id="filterForm" method="get" action="attendance.jsp">
            <label>From:</label>
            <input type="date" name="startDate" />
            <label>To:</label>
            <input type="date" name="endDate" />

            <label>Department:</label>
            <select name="department">
                <option value="">--All--</option>
                <option value="HR">HR</option>
                <option value="IT">IT</option>
                <option value="Finance">Finance</option>
            </select>

            <label>Employee:</label>
            <input type="text" name="employee" placeholder="Name or ID" />

            <label>Status:</label>
            <select name="status">
                <option value="">--All--</option>
                <option value="Late">Late</option>
                <option value="Missing">Missing</option>
                <option value="Approved">Approved</option>
                <option value="Pending">Pending</option>
                <option value="Corrected">Corrected</option>
            </select>

            <label>Source:</label>
            <select name="source">
                <option value="">--All--</option>
                <option value="App">App</option>
                <option value="Kiosk">Kiosk</option>
                <option value="Manual">Manual</option>
                <option value="Import">Import</option>
            </select>

            <label>Period:</label>
            <select name="period">
                <option value="">--All--</option>
                <option value="2025-10">2025-10</option>
                <option value="2025-09">2025-09</option>
            </select>

            <button type="submit">Search</button>
            <button type="button" onclick="resetFilters()">Reset</button>
        </form>

        <hr>

        <!-- ========== ACTION BUTTONS ========== -->
        <div id="actions">
            <button onclick="previewImport()">Preview Import</button>
            <button onclick="exportData('csv')">Export CSV</button>
            <button onclick="exportData('xls')">Export XLS</button>
            <button onclick="exportData('pdf')">Export PDF</button>

            <button id="editBtn" onclick="enableEdit()">Edit</button>
            <button id="submitBtn" style="display:none" onclick="submitChanges()">Submit</button>
            <button id="deleteBtn" onclick="toggleDeleteMode()">Delete</button>
            <button id="bulkDeleteBtn" style="display:none" onclick="bulkDelete()">Delete Selected</button>

            <button id="lockBtn" onclick="toggleLock()">Lock/Unlock Period</button>
        </div>

        <br>

        <!-- ========== MAIN TABLE ========== -->
        <table id="attendanceTable" border="1" cellspacing="0" cellpadding="6">
            <thead>
                <tr>
                    <th><input type="checkbox" onclick="selectAll(this)" /></th>
                    <th>Employee Name</th>
                    <th>Employee ID</th>
                    <th>Department / Team</th>
                    <th>Date</th>
                    <th>Check-in</th>
                    <th>Check-out</th>
                    <th>Status</th>
                    <th>Source</th>
                    <th>Period</th>
                    <th>Locked</th>
                    <th class="edit-col" style="display:none;">Actions</th>
                </tr>
            </thead>
            <tbody>
                <!-- DEMO DATA -->
                <tr onclick="openDetail({
                            id: 1,
                            employeeName: 'Nguyễn Văn A',
                            employeeId: 'EMP001',
                            department: 'IT',
                            date: '2025-10-08',
                            checkIn: '08:05',
                            checkOut: '17:00',
                            status: 'Approved',
                            source: 'App',
                            period: '2025-10',
                            locked: false,
                            notes: 'Quên chấm công buổi sáng, đã bổ sung.',
                            attachments: 'chamcong_1008.png',
                            lockedBy: 'Trần Thị HR',
                            lockedAt: '2025-10-09 09:00',
                            auditTrail: '08/10 chỉnh giờ check-in; 09/10 HR approve.'
                        })">
                    <td><input type="checkbox" /></td>
                    <td>Nguyễn Văn A</td>
                    <td>EMP001</td>
                    <td>IT</td>
                    <td>2025-10-08</td>
                    <td>08:05</td>
                    <td>17:00</td>
                    <td>Approved</td>
                    <td>App</td>
                    <td>2025-10</td>
                    <td>No</td>
                    <td class="edit-col" style="display:none;">
                        <button>Delete</button>
                    </td>
                </tr>
            </tbody>
        </table>

        <!-- ========== DETAIL POP-UP ========== -->
        <div id="overlay" style="display:none; position:fixed; top:0; left:0; right:0; bottom:0; background-color:rgba(0,0,0,0.3);"></div>
        <div id="popup" style="display:none; position:fixed; top:10%; left:50%; transform:translateX(-50%); background:#fff; padding:20px; border:1px solid #ccc; width:400px;">
            <h3>Attendance Detail</h3>
            <table border="0" cellspacing="4" cellpadding="4">
                <tr><td><b>Employee Name:</b></td><td id="dName"></td></tr>
                <tr><td><b>Employee ID:</b></td><td id="dId"></td></tr>
                <tr><td><b>Department:</b></td><td id="dDept"></td></tr>
                <tr><td><b>Date:</b></td><td id="dDate"></td></tr>
                <tr><td><b>Check-in:</b></td><td id="dIn"></td></tr>
                <tr><td><b>Check-out:</b></td><td id="dOut"></td></tr>
                <tr><td><b>Status:</b></td><td id="dStatus"></td></tr>
                <tr><td><b>Source:</b></td><td id="dSource"></td></tr>
                <tr><td><b>Period:</b></td><td id="dPeriod"></td></tr>
                <tr><td><b>Locked:</b></td><td id="dLocked"></td></tr>
                <tr><td><b>Notes / Reason:</b></td><td id="dNotes"></td></tr>
                <tr><td><b>Attachments:</b></td><td id="dAttach"></td></tr>
                <tr><td><b>Locked By:</b></td><td id="dLockedBy"></td></tr>
                <tr><td><b>Locked At:</b></td><td id="dLockedAt"></td></tr>
                <tr><td><b>Audit Trail:</b></td><td id="dAudit"></td></tr>
            </table>
            <br>
            <button onclick="closePopup()">Close</button>
        </div>

        <!-- ========== JAVASCRIPT ========== -->
        <script>
            let editMode = false;
            let deleteMode = false;

            function resetFilters() {
                document.getElementById("filterForm").reset();
            }

            function previewImport() {
                alert("Redirect to import module (demo)");
            }

            function exportData(fmt) {
                alert("Exporting as " + fmt.toUpperCase());
            }

            function enableEdit() {
                editMode = true;
                document.getElementById("editBtn").style.display = "none";
                document.getElementById("submitBtn").style.display = "inline-block";
                document.querySelectorAll(".edit-col").forEach(td => td.style.display = "table-cell");
            }

            function submitChanges() {
                editMode = false;
                document.getElementById("editBtn").style.display = "inline-block";
                document.getElementById("submitBtn").style.display = "none";
                document.querySelectorAll(".edit-col").forEach(td => td.style.display = "none");
                alert("Changes submitted (demo)");
            }

            function toggleDeleteMode() {
                deleteMode = !deleteMode;
                document.getElementById("bulkDeleteBtn").style.display = deleteMode ? "inline-block" : "none";
                alert(deleteMode ? "Delete mode ON" : "Delete mode OFF");
            }

            function selectAll(cb) {
                document.querySelectorAll("input[name='rowSelect']").forEach(ch => ch.checked = cb.checked);
            }

            function bulkDelete() {
                const selected = [...document.querySelectorAll("input[name='rowSelect']:checked")];
                if (selected.length === 0) {
                    alert("No rows selected!");
                    return;
                }
                alert("Deleted " + selected.length + " record(s) (demo)");
            }

            // OPEN POPUP DETAIL
            function openDetail(record) {
                if (editMode || deleteMode)
                    return;
                document.getElementById("overlay").style.display = "block";
                document.getElementById("popup").style.display = "block";

                document.getElementById("dName").innerText = record.employeeName;
                document.getElementById("dId").innerText = record.employeeId;
                document.getElementById("dDept").innerText = record.department;
                document.getElementById("dDate").innerText = record.date;
                document.getElementById("dIn").innerText = record.checkIn;
                document.getElementById("dOut").innerText = record.checkOut;
                document.getElementById("dStatus").innerText = record.status;
                document.getElementById("dSource").innerText = record.source;
                document.getElementById("dPeriod").innerText = record.period;
                document.getElementById("dLocked").innerText = record.locked ? "Yes" : "No";
                document.getElementById("dNotes").innerText = record.notes || "-";
                document.getElementById("dAttach").innerText = record.attachments || "-";
                document.getElementById("dLockedBy").innerText = record.lockedBy || "-";
                document.getElementById("dLockedAt").innerText = record.lockedAt || "-";
                document.getElementById("dAudit").innerText = record.auditTrail || "-";
            }

            function closePopup() {
                document.getElementById("popup").style.display = "none";
                document.getElementById("overlay").style.display = "none";
            }

            function toggleLock() {
                alert("Lock / Unlock period (demo)");
            }
        </script>
    </body>
</html>
