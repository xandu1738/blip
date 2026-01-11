package com.ceres.blip.dtos;

public record BaseResponse(
        boolean success,
        String message
) {
}
