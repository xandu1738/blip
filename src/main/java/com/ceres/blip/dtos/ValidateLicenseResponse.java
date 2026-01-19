package com.ceres.blip.dtos;

import java.util.Date;

public record ValidateLicenseResponse(
        boolean valid,
        String message,
        String productId,
        Date expiryDate
) {
}
