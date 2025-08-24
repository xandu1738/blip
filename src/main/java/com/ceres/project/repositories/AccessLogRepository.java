package com.ceres.project.repositories;

import com.ceres.project.models.database.AccessLogModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLogModel, Long> {
}
