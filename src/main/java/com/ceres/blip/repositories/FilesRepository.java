package com.ceres.blip.repositories;

import com.ceres.blip.models.database.FileRepositoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilesRepository extends JpaRepository<FileRepositoryModel, Long> {
    Optional<FileRepositoryModel> findByFileUrl(String fileUrl);
}
