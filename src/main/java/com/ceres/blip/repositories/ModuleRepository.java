package com.ceres.blip.repositories;

import com.ceres.blip.models.database.ModuleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleModel, Long> {
    Optional<ModuleModel> findByCode(String code);
}
