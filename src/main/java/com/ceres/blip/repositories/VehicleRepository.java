package com.ceres.blip.repositories;

import com.ceres.blip.models.database.VehicleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleModel, Long> {
    Page<VehicleModel> findAllByPartnerCode(String partnerCode, Pageable pageable);
}
