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
}

