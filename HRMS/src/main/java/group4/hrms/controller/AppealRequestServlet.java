package group4.hrms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import group4.hrms.dao.RequestDao;
import group4.hrms.model.Request;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/requests/appeal/create")
@MultipartConfig
public class AppealRequestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String requestTypeId = req.getParameter("request_type_id");
        String title = req.getParameter("title");
        String detailText = req.getParameter("detail");
        String attendanceDate = req.getParameter("attendance_date");
        String selectedLogIds = req.getParameter("selected_log_ids");
        Part attachmentPart = req.getPart("attachment");

        // 2️⃣ Chuẩn bị JSON detail
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("detail_text", detailText);

        if (attendanceDate != null && !attendanceDate.isEmpty()) {
            detail.put("attendance_dates", List.of(attendanceDate));
        }

        if (selectedLogIds != null && !selectedLogIds.isEmpty()) {
            List<String> logIdList = Arrays.asList(selectedLogIds.split(","));
            detail.put("selected_log_ids", logIdList);
        }

        // 3️⃣ Xử lý file upload (nếu có)
        if (attachmentPart != null && attachmentPart.getSize() > 0) {
            String savedPath = saveFileToServer(req, attachmentPart);
            detail.put("attachment_path", savedPath);
        }

        // Convert sang JSON string
        ObjectMapper mapper = new ObjectMapper();
        String detailJson;
        try {
            detailJson = mapper.writeValueAsString(detail);
        } catch (JsonProcessingException e) {
            throw new ServletException("Failed to convert detail to JSON", e);
        }

        // 4️⃣ Lấy thông tin người tạo (demo, sau này lấy từ session)
        Long createdByAccountId = 34L;
        Long createdByUserId = 45L;
        Long departmentId = null;
        Long currentApproverAccountId = null;

        // 4️⃣ Lấy thông tin người tạo từ session
//        HttpSession session = req.getSession(false);
//        if (session == null || session.getAttribute("accountId") == null || session.getAttribute("userId") == null) {
//            throw new ServletException("User not logged in or session expired");
//        }
//
//        Long createdByAccountId = (Long) session.getAttribute("accountId");
//        Long createdByUserId = (Long) session.getAttribute("userId");
//
//        // Truy vấn departmentId dựa theo user
//        UserDao userDao = new UserDao();
//        Long departmentId = userDao.findDepartmentIdByUserId(createdByUserId);
//     
//        Long currentApproverAccountId = null;
        // 5️⃣ Tạo Request object
        Request request = new Request();
        request.setRequestTypeId(Long.valueOf(requestTypeId));
        request.setTitle(title);
        request.setDetailJson(detailJson);
        request.setCreatedByAccountId(createdByAccountId);
        request.setCreatedByUserId(createdByUserId);
        request.setDepartmentId(departmentId);
        request.setCurrentApproverAccountId(currentApproverAccountId);
        request.setStatus("DRAFT");

        RequestDao requestDAO = new RequestDao();
        requestDAO.save(request);

        req.setAttribute("message", "Request saved successfully");
        req.getRequestDispatcher("/WEB-INF/views/requests/appeal-form.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long requestTypeId = 3L;
        req.setAttribute("requestTypeId", requestTypeId);
        req.getRequestDispatcher("/WEB-INF/views/requests/appeal-form.jsp").forward(req, resp);
    }

    private String saveFileToServer(HttpServletRequest req, Part filePart) throws IOException {
        String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

        // Đường dẫn gốc trong webapp
        String uploadDir = req.getServletContext().getRealPath("/uploads/requests");
        Files.createDirectories(Paths.get(uploadDir));

        // Tạo tên file duy nhất
        String uniqueName = System.currentTimeMillis() + "_" + originalName;
        Path filePath = Paths.get(uploadDir, uniqueName);

        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Trả về đường dẫn tương đối để lưu vào JSON detail
        return "/uploads/requests/" + uniqueName;
    }
}
