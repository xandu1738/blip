package com.ceres.blip.dtos;

import java.sql.Timestamp;
import java.util.Objects;

public record GenerateLicenseRequest(
        String productId,
        int count,
        String partnerCode,
        Timestamp expiryDate,
        Integer maxActivations
) {
    public GenerateLicenseRequest {
        Objects.requireNonNull(productId,"Please provide product ID!");

        if (count < 1 || count > 1000) {
            throw new IllegalArgumentException("Invalid count value!");
        }

        if (maxActivations < 1 || maxActivations > 100) {
            throw new IllegalArgumentException("Invalid maxActivations value!");
        }

        if (expiryDate == null) {
            throw new IllegalArgumentException("Please provide expiry date!");
        }

        if (expiryDate.getTime() < System.currentTimeMillis()) {
            throw new IllegalArgumentException("Expiry date cannot be in the past!");
        }
    }
}
