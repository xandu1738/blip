package com.ceres.blip.repositories;

import com.ceres.blip.models.database.DistrictModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistrictRepository extends JpaRepository<DistrictModel, Long> {
}
