package com.ceres.blip.repositories;

import com.ceres.blip.models.database.DriverModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<DriverModel, Long> {
    Page<DriverModel> findAllByPartnerCode(String partnerCode, Pageable pageable);

    Optional<DriverModel> findByLicenseNumber(String licenseNumber);

    Optional<DriverModel> findByIdAndPartnerCode(Long id, String partnerCode);
}
