package com.ceres.blip.repositories;

import com.ceres.blip.models.database.LicenseKeyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface LicenseKeyRepository extends JpaRepository<LicenseKeyModel, Long> {
    Optional<LicenseKeyModel> findByLicenseKey(String licenseKey);

    List<LicenseKeyModel> findByProductId(String productId);

    List<LicenseKeyModel> findByPartnerCode(String partnerCode);

    List<LicenseKeyModel> findByExpiryDateBefore(Timestamp date);

    boolean existsByLicenseKey(String licenseKey);

    @Query("SELECT lk FROM LicenseKeyModel lk WHERE lk.expiryDate < CURRENT_TIMESTAMP AND lk.isActive = true")
    List<LicenseKeyModel> findExpiredLicenses();
}
