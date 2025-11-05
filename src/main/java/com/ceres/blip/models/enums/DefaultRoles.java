package com.ceres.blip.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public enum DefaultRoles {
    SUPER_ADMIN("SUPER_ADMIN", "Super Admin", AppDomains.BACK_OFFICE,
            List.of(DefaultPermissions.ASSIGNS_PERMISSIONS, DefaultPermissions.ADMINISTRATOR));

    final String code;
    final String roleName;
    final AppDomains domain;
    final List<DefaultPermissions> permissions;
}
