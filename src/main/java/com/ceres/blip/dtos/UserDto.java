package com.ceres.blip.dtos;

import java.sql.Timestamp;

public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String roleCode,
        String domain,
        Timestamp createdAt,
        Timestamp lastLoggedInAt,
        Boolean isActive,
        String username

) {
}
