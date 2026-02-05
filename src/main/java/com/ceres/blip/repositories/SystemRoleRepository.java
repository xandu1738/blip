package com.ceres.blip.repositories;

import com.ceres.blip.models.database.SystemRoleModel;
import com.ceres.blip.models.enums.AppDomains;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemRoleRepository extends JpaRepository<SystemRoleModel, Long> {
    Optional<SystemRoleModel> findByRoleCode(String code);
    List<SystemRoleModel> findAllByRoleDomain(AppDomains domain);
}
