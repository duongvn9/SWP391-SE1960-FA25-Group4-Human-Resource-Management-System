<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Employee Selector</title>
    </head>
    <body>
        <h2>Employee Selector</h2>

        <!-- Filter Form -->
        <form action="employeeSelector.jsp" method="get">
            <label>Search:</label>
            <input type="text" name="keyword" placeholder="Name or ID" />

            <label>Department:</label>
            <select name="department">
                <option value="">-- All Departments --</option>
                <option value="IT">IT</option>
                <option value="HR">HR</option>
                <option value="Finance">Finance</option>
            </select>

            <label>Position:</label>
            <select name="position">
                <option value="">-- All Positions --</option>
                <option value="Developer">Developer</option>
                <option value="Manager">Manager</option>
                <option value="Officer">Officer</option>
            </select>

            <button type="submit">Filter</button>
            <button type="submit">Reset</button>
        </form>

        <hr/>

        <!-- Employee List Table -->
        <table border="1" cellpadding="5" cellspacing="0" width="100%">
            <thead>
                <tr>
                    <th>Employee ID</th>
                    <th>Full Name</th>
                    <th>Department</th>
                    <th>Position</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>E001</td>
                    <td>Nguyễn Văn A</td>
                    <td>IT</td>
                    <td>Developer</td>
                    <td>Active</td>
                    <td><a href="attendanceSummary.jsp?empId=E001">View Summary</a></td>
                </tr>
                <tr>
                    <td>E002</td>
                    <td>Trần Thị B</td>
                    <td>HR</td>
                    <td>Officer</td>
                    <td>Active</td>
                    <td><a href="attendanceSummary.jsp?empId=E002">View Summary</a></td>
                </tr>
                <tr>
                    <td>E003</td>
                    <td>Lê Văn C</td>
                    <td>Finance</td>
                    <td>Manager</td>
                    <td>Inactive</td>
                    <td><a href="attendanceSummary.jsp?empId=E003">View Summary</a></td>
                </tr>
            </tbody>
        </table>

        <p><i>Showing 3 employees (demo data)</i></p>
    </body>
</html>
