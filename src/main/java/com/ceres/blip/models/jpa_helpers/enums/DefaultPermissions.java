package com.ceres.blip.models.jpa_helpers.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DefaultPermissions {
    ASSIGNS_PERMISSIONS("ASSIGNS_PERMISSIONS", "Can Assign Permissions to roles", AppDomains.BACK_OFFICE),
    ADMINISTRATOR("ADMINISTRATOR", "Can administer the system", AppDomains.BACK_OFFICE);
    final String permissionName;
    final String description;
    final AppDomains domain;
}
