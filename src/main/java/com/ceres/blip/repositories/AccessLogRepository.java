package com.ceres.blip.repositories;

import com.ceres.blip.models.database.AccessLogModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLogModel, Long> {
}
