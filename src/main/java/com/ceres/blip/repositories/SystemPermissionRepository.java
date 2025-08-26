package com.ceres.blip.repositories;

import com.ceres.blip.models.database.SystemPermissionModel;
import com.ceres.blip.models.jpa_helpers.repository.JetRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemPermissionRepository extends JetRepository<SystemPermissionModel, Long> {
    Optional<SystemPermissionModel> findFirstByPermissionCode(String permission_code);
}
