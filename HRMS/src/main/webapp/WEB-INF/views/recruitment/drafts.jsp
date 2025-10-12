<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <jsp:include page="../layout/head.jsp">
      <jsp:param name="pageTitle" value="Draft Requests" />
    </jsp:include>
  </head>
  <body>
    <div class="dashboard-wrapper">
      <jsp:include page="../layout/sidebar.jsp">
        <jsp:param name="currentPage" value="drafts" />
      </jsp:include>

      <div class="main-content">
        <jsp:include page="../layout/dashboard-header.jsp">
          <jsp:param name="pageTitle" value="Saved Draft Requests" />
        </jsp:include>

        <div class="container-fluid px-4 py-3">
          <div class="card shadow-sm border-0 rounded-3">
            <div class="card-body">
              <h5 class="card-title mb-4">Your Draft Recruitment Requests</h5>

              <c:if test="${empty draftList}">
                <div class="alert alert-info">No drafts found.</div>
              </c:if>

              <c:if test="${not empty draftList}">
                <table class="table table-hover align-middle">
                  <thead class="table-light">
                    <tr>
                      <th>Title</th>
                      <th>Description</th>
                      <th>Created At</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <c:forEach var="draft" items="${draftList}">
                      <tr>
                        <td>${draft.title}</td>
                        <td>${draft.description}</td>
                        <td>${draft.createdAt}</td>
                        <td>
                          <a href="${pageContext.request.contextPath}/requests/edit?id=${draft.id}"
                             class="btn btn-sm btn-primary">Edit</a>
                        </td>
                      </tr>
                    </c:forEach>
                  </tbody>
                </table>
              </c:if>
            </div>
          </div>
        </div>

        <jsp:include page="../layout/dashboard-footer.jsp" />
      </div>
    </div>
  </body>
</html>
