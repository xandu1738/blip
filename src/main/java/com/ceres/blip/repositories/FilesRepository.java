package com.ceres.blip.repositories;

import com.ceres.blip.models.database.FileRepositoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilesRepository extends JpaRepository<FileRepositoryModel, Long> {
}
