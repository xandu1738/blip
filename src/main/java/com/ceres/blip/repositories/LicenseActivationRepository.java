package com.ceres.blip.repositories;

import com.ceres.blip.models.database.LicenseActivationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LicenseActivationRepository extends JpaRepository<LicenseActivationModel,Long> {
    List<LicenseActivationModel> findByLicenseKeyId(Long licenseKeyId);

    Optional<LicenseActivationModel> findByLicenseKeyIdAndHardwareId(Long licenseKeyId, String hardwareId);

    @Query("SELECT COUNT(la) FROM LicenseActivationModel la WHERE la.licenseKey.id = :licenseKeyId AND la.isValid = true")
    int countActiveActivations(@Param("licenseKeyId") Long licenseKeyId);

    //Find Activations older than one year
}
