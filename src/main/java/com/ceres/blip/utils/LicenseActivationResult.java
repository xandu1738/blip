package com.ceres.blip.utils;

import com.ceres.blip.models.database.LicenseActivationModel;
import com.ceres.blip.models.database.LicenseKeyModel;
import com.ceres.blip.services.LicenseValidationService;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicenseActivationResult {
    private boolean success;
    private String message;
    private LicenseKeyModel license;
    private LicenseActivationModel activation;

    public static LicenseActivationResult success(LicenseKeyModel license, LicenseActivationModel activation) {
        LicenseActivationResult result = new LicenseActivationResult();
        result.setSuccess(true);
        result.setMessage("License activated successfully");
        result.setLicense(license);
        result.setActivation(activation);
        return result;
    }

    public static LicenseActivationResult failed(String message) {
        LicenseActivationResult result = new LicenseActivationResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}
