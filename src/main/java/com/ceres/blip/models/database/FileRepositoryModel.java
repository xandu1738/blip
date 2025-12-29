package com.ceres.blip.models.database;

import com.ceres.blip.models.enums.FileCategories;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "file_repository")
public class FileRepositoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_repository_id_gen")
    @SequenceGenerator(name = "file_repository_id_gen", sequenceName = "file_repository_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "file_category", nullable = false, length = Integer.MAX_VALUE)
    @Enumerated(EnumType.STRING)
    private FileCategories fileCategory;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "added_by", nullable = false)
    private Long addedBy;

}