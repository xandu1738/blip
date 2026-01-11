package com.ceres.blip.dtos;

import java.util.List;

public record GenerateLicenseResponse(
        int count,
        List<String> keys
) {
}
