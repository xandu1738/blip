package com.ceres.blip.services;

import com.ceres.blip.dtos.LicenseActivationRequest;
import com.ceres.blip.dtos.LicenseValidationRequest;
import com.ceres.blip.models.database.LicenseActivationModel;
import com.ceres.blip.models.database.LicenseKeyModel;
import com.ceres.blip.repositories.LicenseActivationRepository;
import com.ceres.blip.repositories.LicenseKeyRepository;
import com.ceres.blip.utils.LicenseActivationResult;
import com.ceres.blip.utils.LicenseValidationResult;
import com.ceres.blip.utils.LocalUtilsService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LicenseValidationService extends LocalUtilsService {
    private final LicenseKeyRepository licenseKeyRepository;
    private final LicenseActivationRepository activationRepository;

    /**
     * Validate license key
     */
    public LicenseValidationResult validateLicense(String licenseKey, LicenseValidationRequest request) {
        try {
            // Find the license key
            Optional<LicenseKeyModel> optionalLicense = licenseKeyRepository.findByLicenseKey(licenseKey);
            if (optionalLicense.isEmpty()) {
                return LicenseValidationResult.invalid("License key not found");
            }

            LicenseKeyModel license = optionalLicense.get();

            // Check if the license is active
            if (!Boolean.TRUE.equals(license.getIsActive())) {
                return LicenseValidationResult.invalid("License is deactivated");
            }

            // Check if the license has expired
            if (license.getExpiryDate() != null &&
                license.getExpiryDate().before(new Date())) {
                return LicenseValidationResult.invalid("License has expired");
            }

            // Check activation limit
            if (license.getCurrentActivations() >= license.getMaxActivations()) {
                return LicenseValidationResult.invalid("Maximum activations reached");
            }

            // Check hardware binding if required
            if (request.hardwareId() != null && license.getHardwareId() != null &&
                !license.getHardwareId().equals(request.hardwareId())) {
                return LicenseValidationResult.invalid("License is bound to different hardware");
            }

            // All checks passed
            return LicenseValidationResult.valid(license);

        } catch (Exception e) {
            log.error("Error validating license: {}", licenseKey, e);
            return LicenseValidationResult.invalid("Validation error occurred");
        }
    }

    /**
     * Activate license
     */
    public LicenseActivationResult activateLicense(String licenseKey, LicenseActivationRequest request) {
        LicenseValidationRequest validationRequest = new LicenseValidationRequest(request.hardwareId(),request.ipAddress());

        LicenseValidationResult validation = validateLicense(licenseKey, validationRequest);

        if (!validation.isValid()) {
            return LicenseActivationResult.failed(validation.getMessage());
        }

        LicenseKeyModel license = validation.getLicense();

        try {
            // Create an activation record
            LicenseActivationModel activation = new LicenseActivationModel();
                    activation.setLicenseKey(license);
                    activation.setHardwareId(request.hardwareId());
                    activation.setIpAddress(request.ipAddress());
                    activation.setUserAgent(request.userAgent());
                    activation.setIsValid(true);

            activationRepository.save(activation);

            // Update license
            license.setIsUsed(true);
            license.setCurrentActivations(license.getCurrentActivations() + 1);
            if (license.getActivationDate() == null) {
                license.setActivationDate(getCurrentTimestamp());
            }
            if (StringUtils.isNotBlank(request.hardwareId())) {
                license.setHardwareId(request.hardwareId());
            }
            if (StringUtils.isNotBlank(request.ipAddress())) {
                license.setIpAddress(request.ipAddress());
            }

            licenseKeyRepository.save(license);

            return LicenseActivationResult.success(license, activation);

        } catch (Exception e) {
            log.error("Error activating license: {}", licenseKey, e);
            return LicenseActivationResult.failed("Activation failed");
        }
    }

    /**
     * Deactivate license
     */
    public boolean deactivateLicense(String licenseKey, String hardwareId) {
        Optional<LicenseKeyModel> optionalLicense = licenseKeyRepository.findByLicenseKey(licenseKey);
        if (optionalLicense.isEmpty()) {
            return false;
        }

        LicenseKeyModel license = optionalLicense.get();
        Optional<LicenseActivationModel> optionalActivation =
                activationRepository.findByLicenseKeyIdAndHardwareId(license.getId(), hardwareId);

        if (optionalActivation.isPresent()) {
            LicenseActivationModel activation = optionalActivation.get();
            activation.setIsValid(false);
            activation.setDeactivationDate(getCurrentTimestamp());
            activationRepository.save(activation);

            // Update license activation count
            int activeCount = activationRepository.countActiveActivations(license.getId());
            license.setCurrentActivations(activeCount);
            if (activeCount == 0) {
                license.setIsUsed(false);
            }
            licenseKeyRepository.save(license);

            return true;
        }

        return false;
    }

}
