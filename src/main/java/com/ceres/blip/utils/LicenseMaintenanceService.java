package com.ceres.blip.utils;

import com.ceres.blip.models.database.LicenseKeyModel;
import com.ceres.blip.repositories.LicenseKeyRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class LicenseMaintenanceService {
    private final LicenseKeyRepository licenseKeyRepository;

    public LicenseMaintenanceService(LicenseKeyRepository licenseKeyRepository) {
        this.licenseKeyRepository = licenseKeyRepository;
    }

    /**
     * Deactivate expired licenses daily
     */
    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight daily
    public void deactivateExpiredLicenses() {
        log.info("Starting expired license deactivation");

        List<LicenseKeyModel> expiredLicenses = licenseKeyRepository.findExpiredLicenses();
        int count = 0;

        for (LicenseKeyModel license : expiredLicenses) {
            license.setIsActive(false);
            licenseKeyRepository.save(license);
            count++;
        }

        log.info("Deactivated {} expired licenses", count);
    }

    /**
     * Clean up old activation records weekly
     */
    @Scheduled(cron = "0 0 0 * * 0") // Runs at midnight every Sunday
    @Transactional
    public void cleanupOldActivations() {
        log.info("Cleaning up old activation records");

        // Delete activations older than 1 year
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        Date oneYearAgo = calendar.getTime();

        // This would require a custom query in the repository
//         activationRepository.deleteByActivationDateBefore(oneYearAgo);
    }
}
