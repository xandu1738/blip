package com.ceres.blip.dtos;

public record ActivateLicenseRequest(
        String licenseKey,
        String hardwareId,
        String ipAddress,
        String userAgent
) {
}
