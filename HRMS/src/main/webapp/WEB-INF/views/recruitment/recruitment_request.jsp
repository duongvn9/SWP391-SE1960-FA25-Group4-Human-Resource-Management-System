<%-- 
    Document   : recruitment_request 
    Mục đích   : Form tạo Recruitment Request, gửi tất cả dữ liệu vào JSON.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <jsp:include page="../layout/head.jsp">
            <jsp:param name="pageTitle" value="Recruitment Request" />
        </jsp:include>
        <link href="${pageContext.request.contextPath}/assets/css/common.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/css/recruitment-request.css" rel="stylesheet">
    </head>
    <body>
        <div class="dashboard-wrapper">
            <jsp:include page="../layout/sidebar.jsp">
                <jsp:param name="currentPage" value="recruitment-create" />
            </jsp:include>

            <div class="main-content">
                <jsp:include page="../layout/dashboard-header.jsp">
                    <jsp:param name="pageTitle" value="Recruitment Request" />
                </jsp:include>


                <div class="container-fluid px-4 py-3">
                    <div class="card shadow-sm border-0 rounded-3">
                        <div class="card-body">
                            <h5 class="card-title mb-4">Create Recruitment Request</h5>

                            <form id="recruitmentRequestForm"
                                  action="${pageContext.request.contextPath}/requests/recruitment/submit"
                                  method="post" enctype="multipart/form-data">
                                
                                <input type="hidden" name="createdByAccountId" value="${sessionScope.userAccountId}" />
                                <input type="hidden" name="createdByUserId" value="${sessionScope.userId}" />
                                
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <label class="form-label">Job Title (Summary) <span class="text-danger">*</span></label>
                                        <input type="text" name="jobTitle" class="form-control" required />
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Position Name (Chức danh) <span class="text-danger">*</span></label>
                                        <input type="text" name="positionName" class="form-control" placeholder="Ví dụ: Senior Software Engineer" required/>
                                        <input type="hidden" name="positionCode" value="AUTO_GEN_CODE" />
                                    </div>
                                </div>
                                
                                <div class="row mb-3">
                                    <div class="col-md-4">
                                        <label class="form-label">Quantity <span class="text-danger">*</span></label>
                                        <input type="number" name="quantity" class="form-control" min="1" required />
                                    </div>
                                    <div class="col-md-4">
                                        <label class="form-label">Job Level</label>
                                        <input type="number" name="jobLevel" class="form-control" placeholder="Cấp độ (1-10)" />
                                    </div>
                                    <div class="col-md-4">
                                        <label class="form-label">Contract Type</label>
                                        <select name="type" class="form-select">
                                            <option value="FULL_TIME">Full-time</option>
                                            <option value="PART_TIME">Part-time</option>
                                            <option value="INTERNSHIP">Internship</option>
                                        </select>
                                    </div>
                                </div>
                                
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <label class="form-label">Recruitment Reason</label>
                                        <textarea name="recruitmentReason" class="form-control" rows="2" placeholder="Lý do tuyển dụng chính"></textarea>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Budget Salary Range</label>
                                        <input type="text" name="budgetSalaryRange" class="form-control" placeholder="VD: 15,000,000 - 20,000,000 VND Gross" />
                                    </div>
                                </div>
                                
                                <div class="mb-3">
                                    <label class="form-label">Job Summary (Tóm tắt nhiệm vụ chính) <span class="text-danger">*</span></label>
                                    <textarea name="jobSummary" class="form-control" rows="4" required></textarea>
                                </div>
                                
                                <div class="mb-3">
                                    <label class="form-label">Detailed JD / Attachment (optional)</label>
                                    <input type="file" name="attachment" class="form-control" />
                                </div>

                                <div class="d-flex gap-2">
                                    <button type="button" id="saveDraftBtn" class="btn btn-secondary" 
                                            onclick="document.getElementById('recruitmentRequestForm').action='${pageContext.request.contextPath}/requests/recruitment/save-draft'; document.getElementById('recruitmentRequestForm').submit();">
                                        Save as Draft
                                    </button>
                                    <button type="submit" class="btn btn-primary">Submit Request</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                
                <jsp:include page="../layout/dashboard-footer.jsp" />
            </div>
        </div> 
        <script src="${pageContext.request.contextPath}/assets/js/recruitment_request.js"></script>
    </body>
</html>