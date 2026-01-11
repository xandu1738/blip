package com.ceres.blip.dtos;

public record ActivateLicenseResponse(
        boolean success,
        String message,
        Long activationId
) {
}
