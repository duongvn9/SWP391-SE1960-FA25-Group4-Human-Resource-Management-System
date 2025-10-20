/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */



package group4.hrms.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUploadUtil {

    public static String uploadFile(HttpServletRequest request, String fieldName, String folder) throws IOException, ServletException {
        Part filePart = request.getPart(fieldName);
        if (filePart == null || filePart.getSize() == 0) return null;

        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String ext = fileName.substring(fileName.lastIndexOf('.'));
        String newFileName = UUID.randomUUID() + ext;

        String uploadDir = request.getServletContext().getRealPath("/") + folder;
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, newFileName);
        filePart.write(file.getAbsolutePath());

        return folder + "/" + newFileName;
    }

    /**
     * Upload a single Part (used when multiple file inputs share same name)
     * Returns stored relative path or null
     */
    public static String uploadPart(Part part, String folder, HttpServletRequest request) throws IOException {
        if (part == null || part.getSize() == 0) return null;

        // Max size 5MB per file (enforced at UI level too)
        long maxBytes = 5L * 1024L * 1024L;
        if (part.getSize() > maxBytes) {
            throw new IOException("File too large: " + part.getSubmittedFileName());
        }

        String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";
        String newFileName = UUID.randomUUID() + ext;

        String uploadDir = request.getServletContext().getRealPath("/") + folder;
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, newFileName);
        part.write(file.getAbsolutePath());

        return folder + "/" + newFileName;
    }
}

