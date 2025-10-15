<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
    <head>
        <title>Submit Attendance Dispute</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/appeal-request.css">
        <script src="${pageContext.request.contextPath}/assets/js/appeal-request.js"></script>
    </head>
    <body>
        <h2>Submit Attendance Dispute</h2>

        <form action="${pageContext.request.contextPath}/requests/appeal" method="post" enctype="multipart/form-data">
            <input type="hidden" name="request_type_id" value="${requestTypeId}"/>

            <label>Select Attendance Date:</label><br/>
            <input type="date" id="attendanceDate" name="attendance_date"/>
            <button type="button" id="selectFromLogDates">Select from Attendance Logs</button>
            <input type="hidden" id="selectedLogDates" name="selected_log_dates"/>
            <br/><br/>

            <label for="title">Title:</label>
            <input type="text" id="title" name="title" required/>
            <br/><br/>

            <label for="detail">Detail:</label><br/>
            <textarea id="detail" name="detail" rows="5" cols="50" required></textarea>
            <br/><br/>

            <label for="attachment">Attachment:</label>
            <input type="file" id="attachment" name="attachment"/>
            <br/><br/>

            <!-- Hidden input to store selected attendance log IDs -->
            <input type="hidden" id="selectedLogs" name="selected_log_ids"/>

            <button type="submit">Submit Dispute</button>
            <c:if test="${not empty message}">
                <div style="color: red; margin-top: 10px;">
                    ${message}
                </div>
            </c:if>
        </form>
    </body>
</html>