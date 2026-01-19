package com.ceres.blip.dtos;

public record LicenseActivationRequest(
        String hardwareId,
        String ipAddress,
        String userAgent
) {
}
