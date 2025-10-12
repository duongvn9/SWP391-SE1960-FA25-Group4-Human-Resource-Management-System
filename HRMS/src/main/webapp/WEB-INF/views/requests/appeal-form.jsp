<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
    <head>
        <title>Submit Attendance Dispute</title>
        <script src="${pageContext.request.contextPath}/assets/js/appeal-request.js"></script>
    </head>
    <body>
        <h2>Submit Attendance Dispute</h2>

        <form action="${pageContext.request.contextPath}/requests/appeal" method="post" enctype="multipart/form-data">
            <!-- Hidden field: request type -->
            <input type="hidden" name="request_type_id" value="${requestTypeId}"/>

            <!-- Select date -->
            <label>Select Attendance Date:</label><br/>

            <!-- Chọn 1 ngày trực tiếp -->
            <input type="date" id="attendanceDate" name="attendance_date"/>

            <!-- Chọn nhiều ngày từ trang attendance log -->
            <button type="button" id="selectFromLogDates">Select from Attendance Logs</button>

            <!-- Hidden input lưu các ngày đã chọn từ log -->
            <input type="hidden" id="selectedLogDates" name="selected_log_dates"/>

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
            <c:if test="${not empty message}">
                <div style="color: red; margin-top: 10px;">
                    ${message}
                </div>
            </c:if>
        </form>
    </body>
</html>
