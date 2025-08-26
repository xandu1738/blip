package com.ceres.blip.repositories;

import com.ceres.blip.models.database.SystemRolePermissionAssignmentModel;
import com.ceres.blip.models.jpa_helpers.repository.JetRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemRolePermissionRepository extends JpaRepository<SystemRolePermissionAssignmentModel, Long> {
    Optional<SystemRolePermissionAssignmentModel> findByRoleCodeAndPermissionCode(String roleCode, String permissionCode);
    List<SystemRolePermissionAssignmentModel> findAllByRoleCode(String roleCode);
}
