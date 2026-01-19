package com.ceres.blip.dtos;

public record ValidateLicenseRequest(
        String licenseKey,
        String hardwareId,
        String ipAddress
) {
}
