package com.ceres.blip.models.database;

import com.ceres.blip.models.enums.FileCategories;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "file_repository")
public class FileRepositoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type", length = 64)
    private String fileType;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_category", length = 64)
    private FileCategories fileCategory;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_path", length = Integer.MAX_VALUE)
    private String filePath;

    @Column(name = "uploaded_by")
    private String uploadedBy;

    @Column(name = "date_uploaded")
    private Timestamp dateUploaded;

}