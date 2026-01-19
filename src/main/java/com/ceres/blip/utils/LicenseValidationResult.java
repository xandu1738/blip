package com.ceres.blip.utils;

import com.ceres.blip.models.database.LicenseKeyModel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicenseValidationResult {
    private boolean isValid;
    private String message;
    private LicenseKeyModel license;

    public static LicenseValidationResult valid(LicenseKeyModel license) {
        LicenseValidationResult result = new LicenseValidationResult();
        result.setValid(true);
        result.setMessage("License is valid");
        result.setLicense(license);
        return result;
    }

    public static LicenseValidationResult invalid(String message) {
        LicenseValidationResult result = new LicenseValidationResult();
        result.setValid(false);
        result.setMessage(message);
        return result;
    }
}
