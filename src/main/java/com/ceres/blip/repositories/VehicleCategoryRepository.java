package com.ceres.blip.repositories;

import com.ceres.blip.models.database.VehicleCategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleCategoryRepository extends JpaRepository<VehicleCategoryModel, Long>{
    Optional<VehicleCategoryModel> findByCategoryCode(String code);
}
