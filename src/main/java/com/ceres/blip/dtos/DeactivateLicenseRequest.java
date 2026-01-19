package com.ceres.blip.dtos;

public record DeactivateLicenseRequest(
        String licenseKey,
        String hardwareId
) {
}
