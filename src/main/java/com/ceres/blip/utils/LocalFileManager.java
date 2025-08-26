package com.ceres.blip.utils;

import com.ceres.blip.models.database.FileRepositoryModel;
import com.ceres.blip.models.jpa_helpers.enums.AllowedFileExtensions;
import com.ceres.blip.models.jpa_helpers.enums.FileCategories;
import com.ceres.blip.repositories.FilesRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
public class LocalFileManager {
    private final Path uploadDir;
    private final FilesRepository filesRepository;

    public LocalFileManager(@Value("${spring.servlet.multipart.location}") String uploadDirectory, FilesRepository filesRepository) {
        this.uploadDir = Paths.get(uploadDirectory);
        this.filesRepository = filesRepository;
    }

    public void storeFile(MultipartFile file, FileCategories fileCategory) throws IOException {
        FileRepositoryModel fileModel = new FileRepositoryModel();
        fileModel.setFileName(Objects.requireNonNull(file.getOriginalFilename()));
        fileModel.setFileSize(file.getSize());
        fileModel.setFileType(file.getContentType());
        fileModel.setFileCategory(fileCategory);
        fileModel.setDateUploaded(new Timestamp(Instant.now().getEpochSecond()));


        FileRepositoryModel saved = filesRepository.save(fileModel);
        //todo: link saved image to the affiliated entity entry
        Path filePath = uploadDir.resolve(Objects.requireNonNull(file.getOriginalFilename()));
        Files.copy(file.getInputStream(), filePath);
    }

    public void uploadFile(MultipartFile submissionFile, List<AllowedFileExtensions> allowedFileTypes, FileCategories fileCategory) {

        if (fileCategory == null) {
            throw new IllegalArgumentException("Please specify a valid file category");
        }

        try {
            if (submissionFile == null) {
                throw new IllegalStateException("Submission File is Null.");
            }
            String fileName = submissionFile.getOriginalFilename();
            if (fileName == null) {
                throw new IllegalStateException("File Name is null");
            }

            String[] parts = fileName.split("\\.");
            if (parts.length > 1) {
                String extension = parts[parts.length - 1].toLowerCase();

                List<String> whiteListed = allowedFileTypes.stream()
                        .map(t -> t.name().toLowerCase())
                        .toList();

                if (!whiteListed.contains(extension)) {
                    throw new IllegalStateException("Unaccepted file format, " + extension + ". Please upload compressed File only.");
                }

                storeFile(submissionFile, fileCategory);
            } else {
                throw new IllegalStateException("Invalid file. No file extension found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public String storeBase64File(String base64Data, String fileName, FileCategories fileCategory) throws IOException {
        // Decode the base64 data
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data);

        // Save the file to the upload directory
        Path filePath = uploadDir.resolve(fileName);
        Files.write(filePath, decodedBytes);

        // Create and save the file metadata
        FileRepositoryModel fileModel = new FileRepositoryModel();
        fileModel.setFileName(fileName);
        fileModel.setFileSize((long) decodedBytes.length);
        fileModel.setFileType(Files.probeContentType(filePath));
        fileModel.setFileCategory(fileCategory);
        fileModel.setDateUploaded(new Timestamp(Instant.now().toEpochMilli()));

        filesRepository.save(fileModel);
        return filePath.toString();
    }
}
