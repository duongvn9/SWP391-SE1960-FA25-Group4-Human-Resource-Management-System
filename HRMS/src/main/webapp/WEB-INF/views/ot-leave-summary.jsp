<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Attendance Summary</title>
        <script>
            // Simple JS tab switcher
            function showTab(tabName) {
                document.getElementById('otTab').style.display = (tabName === 'OT') ? 'block' : 'none';
                document.getElementById('leaveTab').style.display = (tabName === 'Leave') ? 'block' : 'none';
            }
        </script>
    </head>
    <body>
        <h2>Attendance Summary</h2>

        <!-- Header info about employee -->
    <c:set var="employeeName" value="Nguyễn Văn A" />
    <c:set var="employeeCode" value="E001" />
    <c:set var="department" value="IT" />
    <c:set var="position" value="Developer" />

    <p>
        <b>Employee:</b> ${employeeName} (${employeeCode})<br/>
        <b>Department:</b> ${department} | <b>Position:</b> ${position}<br/>
        <a href="employeeSelector.jsp">Back to Employee Selector</a>
    </p>

    <!-- Tabs -->
    <div>
        <button onclick="showTab('OT')">OT Summary</button>
        <button onclick="showTab('Leave')">Leave Summary</button>
    </div>

    <hr/>

    <!-- OT Summary Tab -->
    <div id="otTab">
        <h3>OT Summary</h3>
        <table border="1" cellpadding="5" cellspacing="0" width="100%">
            <thead>
                <tr>
                    <th>Date</th>
                    <th>OT Type</th>
                    <th>Hours</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>2025-09-01</td>
                    <td>Weekday OT</td>
                    <td>2</td>
                    <td>Approved</td>
                </tr>
                <tr>
                    <td>2025-09-05</td>
                    <td>Weekend OT</td>
                    <td>5</td>
                    <td>Pending</td>
                </tr>
                <tr>
                    <td>2025-09-10</td>
                    <td>Holiday OT</td>
                    <td>8</td>
                    <td>Approved</td>
                </tr>
            </tbody>
        </table>
        <p><b>Total OT Hours:</b> 15</p>
    </div>

    <!-- Leave Summary Tab -->
    <div id="leaveTab" style="display:none;">
        <h3>Leave Summary</h3>
        <table border="1" cellpadding="5" cellspacing="0" width="100%">
            <thead>
                <tr>
                    <th>Leave Type</th>
                    <th>Days Taken</th>
                    <th>Remaining</th>
                    <th>Last Used</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Annual Leave</td>
                    <td>10</td>
                    <td>5</td>
                    <td>2025-09-22</td>
                </tr>
                <tr>
                    <td>Sick Leave</td>
                    <td>3</td>
                    <td>7</td>
                    <td>2025-09-15</td>
                </tr>
                <tr>
                    <td>Unpaid Leave</td>
                    <td>1</td>
                    <td>NA</td>
                    <td>2025-09-12</td>
                </tr>
            </tbody>
        </table>
        <p><b>Total Leave Taken:</b> 14 days</p>
    </div>

</body>
</html>
