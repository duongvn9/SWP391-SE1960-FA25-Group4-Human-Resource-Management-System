package group4.hrms.controller;

import group4.hrms.dao.AttachmentDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.UserDao;
import group4.hrms.model.Attachment;
import group4.hrms.model.Request;
import group4.hrms.model.User;
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
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/requests/appeal/create")
@MultipartConfig
public class AppealRequestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDao requestDao = new RequestDao();
        UserDao uDao = new UserDao();
        AttachmentDao attachmentDao = new AttachmentDao();

        try {
            Long accountId = (Long) req.getSession().getAttribute("accountId");
            Long userId = (Long) req.getSession().getAttribute("userId");

            String title = req.getParameter("title");
            String detailText = req.getParameter("detail");
            String selectedLogDatesStr = req.getParameter("selected_log_dates");
            Long requestTypeId = Long.valueOf(req.getParameter("request_type_id"));
            String userAttachmentLink = req.getParameter("attachmentLink");

            Request request = new Request();
            request.setRequestTypeId(requestTypeId);
            request.setTitle(title);
            request.setCreatedByAccountId(accountId);
            request.setCreatedByUserId(userId);

            User u = uDao.findById(userId).orElse(new User());
            request.setDepartmentId(u.getDepartmentId());
            request.setStatus("PENDING");
            request.setCreatedAt(LocalDateTime.now());

            String uploadedFileLink = null;
            Part filePart = req.getPart("attachment");
            if (filePart != null && filePart.getSize() > 0) {
                String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String uuid = java.util.UUID.randomUUID().toString();
                String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : "";
                String serverFileName = uuid + ext;

                Path uploadDir = Paths.get("C:/HRMS_uploads");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path filePath = uploadDir.resolve(serverFileName);
                try (InputStream in = filePart.getInputStream()) {
                    Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                uploadedFileLink = "/downloads/" + serverFileName;
            }

            StringBuilder detailJsonBuilder = new StringBuilder();
            detailJsonBuilder.append("{");
            detailJsonBuilder.append("\"attendance_dates\":\"").append(escapeJson(selectedLogDatesStr)).append("\"");
            if (detailText != null && !detailText.isEmpty()) {
                detailJsonBuilder.append(",\"detail_text\":\"").append(escapeJson(detailText)).append("\"");
            }
            if (uploadedFileLink != null) {
                detailJsonBuilder.append(",\"attachment_link\":\"").append(escapeJson(uploadedFileLink)).append("\"");
            }
            if (userAttachmentLink != null && !userAttachmentLink.isEmpty()) {
                detailJsonBuilder.append(",\"user_attachment_link\":\"").append(escapeJson(userAttachmentLink)).append("\"");
            }
            detailJsonBuilder.append("}");
            request.setDetailJson(detailJsonBuilder.toString());

            requestDao.save(request);

            if (userAttachmentLink != null && !userAttachmentLink.isEmpty()) {
                Attachment attachment = new Attachment();
                attachment.setOwnerType("REQUEST");                  // liên kết với request
                attachment.setOwnerId(request.getId());              // ID của request vừa lưu
                attachment.setOriginalName("Google drive link");      // tên hiển thị
                attachment.setAttachmentType("LINK");               // đánh dấu đây là link
                attachment.setExternalUrl(userAttachmentLink);      // lưu link thực tế
                attachment.setPath("");                            // không có file path
                attachment.setSizeBytes(0L);                         // không có file
                attachment.setUploadedByAccountId(accountId);       // ai thêm link
                attachment.setCreatedAt(LocalDateTime.now());       // thời gian tạo
                attachment.setContentType("External/link");
                attachment.setChecksumSha256(null);

                attachmentDao.save(attachment);
            }
            req.setAttribute("success", "Create appeal attendance request successfully!");
            req.getRequestDispatcher("/WEB-INF/views/requests/appeal-form.jsp").forward(req, resp);
        } catch (ServletException | IOException | NumberFormatException ex) {
            throw new ServletException(ex);
        } catch (SQLException ex) {
            Logger.getLogger(AppealRequestServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\"", "\\\"");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long requestTypeId = 8L;
        req.setAttribute("requestTypeId", requestTypeId);
        req.getRequestDispatcher("/WEB-INF/views/requests/appeal-form.jsp").forward(req, resp);
    }
}
