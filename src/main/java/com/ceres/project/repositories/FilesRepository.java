package com.ceres.project.repositories;

import com.ceres.project.models.database.FileRepositoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilesRepository extends JpaRepository<FileRepositoryModel, Long> {
}
