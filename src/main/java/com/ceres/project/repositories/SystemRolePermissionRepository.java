package com.ceres.project.repositories;

import com.ceres.project.models.database.SystemRolePermissionAssignmentModel;
import com.ceres.project.models.jpa_helpers.repository.JetRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SystemRolePermissionRepository extends JetRepository<SystemRolePermissionAssignmentModel, Long> {
    Optional<SystemRolePermissionAssignmentModel> findByRoleCodeAndPermissionCode(String roleCode, String permissionCode);
    List<SystemRolePermissionAssignmentModel> findAllByRoleCode(String roleCode);
}
