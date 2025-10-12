<%-- 
    Document   : access-denied
    Created on : Oct 12, 2025, 10:05:32 PM
    Author     : ADMIN
--%>


<!DOCTYPE html>
<html>
<head>
  <title>Access Denied</title>
  <link href="${pageContext.request.contextPath}/assets/css/common.css" rel="stylesheet">
</head>
<body>
  <div class="container text-center py-5">
    <h2 class="text-danger">Access Denied</h2>
    <p>You do not have permission to perform this action.</p>
    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary mt-3">Back to Dashboard</a>
  </div>
</body>
</html>

