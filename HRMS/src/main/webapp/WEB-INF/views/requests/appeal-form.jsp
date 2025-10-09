<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
    <head>
        <title>Submit Attendance Dispute</title>
    </head>
    <body>
        <h2>Submit Attendance Dispute</h2>

        <form action="${pageContext.request.contextPath}/requests/submit" method="post" enctype="multipart/form-data">
            <!-- Hidden field: request type -->
            <input type="hidden" name="request_type_id" value="${requestTypeId}"/>

            <!-- Select date -->
            <label for="attendanceDate">Select Date:</label>
            <input type="date" id="attendanceDate" name="attendance_date" required/>

            <!-- Button to open attendance logs popup -->
            <button type="button" onclick="window.open('${pageContext.request.contextPath}/attendance/view', 'AttendanceLogs', 'width=800,height=600');">
                View Attendance Logs
            </button>
            <br/><br/>

            <!-- Title -->
            <label for="title">Title:</label>
            <input type="text" id="title" name="title" required/>
            <br/><br/>

            <!-- Detail -->
            <label for="detail">Detail:</label><br/>
            <textarea id="detail" name="detail" rows="5" cols="50" required></textarea>
            <br/><br/>

            <!-- Attachment -->
            <label for="attachment">Attachment:</label>
            <input type="file" id="attachment" name="attachment"/>
            <br/><br/>

            <!-- Hidden input to store selected attendance log IDs -->
            <input type="hidden" id="selectedLogs" name="selected_log_ids"/>

            <button type="submit">Submit Dispute</button>
        </form>

        <script>
            // Giả sử trang popup sẽ gọi parent.setSelectedLogs(ids)
            function setSelectedLogs(ids) {
                document.getElementById('selectedLogs').value = ids.join(',');
            }
        </script>

    </body>
</html>
