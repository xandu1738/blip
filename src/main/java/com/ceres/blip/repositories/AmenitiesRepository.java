package com.ceres.blip.repositories;

import com.ceres.blip.models.database.AmenityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AmenitiesRepository extends JpaRepository<AmenityModel,Long> {
    Optional<AmenityModel> findByCode(String code);
}
