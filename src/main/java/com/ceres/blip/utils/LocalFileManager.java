package com.ceres.blip.utils;

import com.ceres.blip.models.database.FileRepositoryModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.models.enums.AllowedFileExtensions;
import com.ceres.blip.models.enums.FileCategories;
import com.ceres.blip.repositories.FilesRepository;
import com.ceres.blip.services.UserManagementService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class LocalFileManager {
    private final FilesRepository filesRepository;
    private final UserManagementService userManagementService;

    public LocalFileManager(FilesRepository filesRepository, UserManagementService userManagementService) {
        this.filesRepository = filesRepository;
        this.userManagementService = userManagementService;
    }

    /**
     * Receives base64 string and saves it to a file in the upload directory under a specified ID
     * with reference to the associated entity. We also generate a web-accessible URL for the uploaded file.
     *
     * @param base64String is the base64 string of the file
     * @param entityId     is the ID of the associated entity
     * @param fileCategory is the category of the file
     * @param request      is the HttpServletRequest object
     * @return the generated web-accessible URL
     * @throws IOException
     */
    public String handleFileUpload(String base64String,
                                   Long entityId,
                                   FileCategories fileCategory,
                                   HttpServletRequest request) throws IOException {
        SystemUserModel authenticatedUser = userManagementService.authenticatedUser();
        // Create upload directory if it doesn't exist
        String projectRoot = System.getProperty("user.dir");
        File webDirectory = new File(projectRoot + File.separator + "web");
        File directory = new File(webDirectory, "upload");

        if (!directory.exists()) {
            directory.mkdirs();
        }
        String fileName = "";
        String fileUrl = "";

        if (StringUtils.isBlank(base64String)) {
            throw new IllegalArgumentException("No base64 string provided");
        }
        // Handle base64 file upload
        String[] parts = base64String.split(",");
        String base64Data = parts.length > 1 ? parts[1] : parts[0];

        // Extract file extension from base64 string if available
        String fileExtension = getFileExtension(parts);

        fileName = UUID.randomUUID() + fileExtension;
        Path filePath = Paths.get(directory.getAbsolutePath(), fileName);
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
        Files.write(filePath, decodedBytes);

        // Generate web-accessible URL
        String scheme = request.getScheme(); // http or https
        String serverName = request.getServerName(); // domain name
        int serverPort = request.getServerPort(); // port number
        String contextPath = request.getContextPath(); // application context path

        // Build the base URL
        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(scheme).append("://").append(serverName);

        // Only include port if it's not the default port
        if ((scheme.equals("http") && serverPort != 80) ||
            (scheme.equals("https") && serverPort != 443)) {
            baseUrl.append(":").append(serverPort);
        }

        baseUrl.append(contextPath);

        // Construct the file URL
        fileUrl = baseUrl + "/upload/" + fileName;

        FileRepositoryModel fileModel = new FileRepositoryModel();
        fileModel.setDescription(fileName);
        fileModel.setFileCategory(fileCategory);
        fileModel.setEntityId(entityId);
        fileModel.setCreatedAt(Instant.now());
        fileModel.setFileUrl(fileUrl);
        fileModel.setFilePath(filePath.toString());
        fileModel.setAddedBy(authenticatedUser.getId());
        filesRepository.save(fileModel);


        return fileUrl;
    }

    private static String getFileExtension(String[] parts) {
        String fileExtension = "";
        if (parts[0].contains("/")) {
            int colonIdx = parts[0].indexOf(":");
            int semiIdx = parts[0].indexOf(";");
            String mimeType = (colonIdx != -1 && semiIdx != -1)
                    ? parts[0].substring(colonIdx + 1, semiIdx).trim()
                    : null;
            if (mimeType == null) return "";
            fileExtension = switch (mimeType) {
                case "image/jpeg" -> ".jpg";
                case "image/png" -> ".png";
                case "image/gif" -> ".gif";
                case "application/pdf" -> ".pdf";
                case "application/msword" -> ".doc";
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> ".docx";
                case "application/vnd.ms-excel" -> ".xls";
                case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> ".xlsx";
                default -> "." + mimeType.substring(mimeType.indexOf("/") + 1);
            };
        }
        return fileExtension;
    }

    public boolean deleteFile(String filePath) {
        if (StringUtils.isBlank(filePath)) throw new IllegalArgumentException("File path cannot be blank");

        FileRepositoryModel fileRecord = filesRepository.findByFileUrl(filePath)
                .orElseThrow(() -> new IllegalStateException("Oops! File not found."));

        try {
            filesRepository.delete(fileRecord);
            Path path = Paths.get(fileRecord.getFilePath());
            Files.deleteIfExists(path);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
