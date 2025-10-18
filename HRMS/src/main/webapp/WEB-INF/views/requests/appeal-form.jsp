<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
    <head>
        <title>Submit Attendance Dispute</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/appeal-form.css">
        <script src="${pageContext.request.contextPath}/assets/js/appeal-request.js"></script>
    </head>
    <body>
        <h2 class="form-title">Submit Attendance Dispute</h2>

        <form class="appeal-form" action="${pageContext.request.contextPath}/requests/appeal" method="post" enctype="multipart/form-data">
            <input type="hidden" name="request_type_id" value="${requestTypeId}"/>

            <div class="form-group">
                <label class="form-label">Select Attendance Date:</label><br/>
                <input class="form-input" type="date" id="attendanceDate" name="attendance_date"/>
                <button class="form-button" type="button" id="selectFromLogDates">Select from Attendance Logs</button>
                <input type="hidden" id="selectedLogDates" name="selected_log_dates"/>
            </div>

            <div class="form-group">
                <label class="form-label" for="title">Title:</label>
                <input class="form-input" type="text" id="title" name="title" required/>
            </div>

            <div class="form-group">
                <label class="form-label" for="detail">Detail:</label><br/>
                <textarea class="form-textarea" id="detail" name="detail" rows="5" cols="50" required></textarea>
            </div>

            <div class="form-group">
                <label class="form-label" for="attachment">Attachment:</label>
                <input class="form-input" type="file" id="attachment" name="attachment"/>
            </div>

            <input type="hidden" id="selectedLogs" name="selected_log_ids"/>

            <div class="form-group">
                <button class="form-submit" type="submit">Submit Dispute</button>
            </div>

            <c:if test="${not empty message}">
                <div class="form-message error-message">
                    ${message}
                </div>
            </c:if>
        </form>
    </body>
</html>
