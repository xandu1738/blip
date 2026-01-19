package com.ceres.blip.dtos;

public record LicenseValidationRequest(
        String hardwareId,
        String ipAddress
) {
}
