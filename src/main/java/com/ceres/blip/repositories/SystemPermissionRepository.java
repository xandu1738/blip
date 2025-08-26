package com.ceres.blip.repositories;

import com.ceres.blip.models.database.SystemPermissionModel;
import com.ceres.blip.models.jpa_helpers.repository.JetRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemPermissionRepository extends JpaRepository<SystemPermissionModel, Long> {
    Optional<SystemPermissionModel> findFirstByPermissionCode(String permissionCode);
}
