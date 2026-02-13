package com.ceres.blip.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum DefaultRoles {
    SUPER_ADMIN("SUPER_ADMIN", "Super Admin", AppDomains.BACK_OFFICE, Arrays.stream(DefaultPermissions.values()).toList()),
    PARTNER_ADMIN("PARTNER_ADMIN", "Partner Admin", AppDomains.BACK_OFFICE,
            List.of(
                    DefaultPermissions.ASSIGNS_PERMISSIONS,
                    DefaultPermissions.ADMINISTRATOR,
                    DefaultPermissions.MANAGE_DRIVERS,
                    DefaultPermissions.MANAGE_USERS,
                    DefaultPermissions.MANAGE_ROLES,
                    DefaultPermissions.MANAGE_CHARGES,
                    DefaultPermissions.MANAGE_VEHICLES,
                    DefaultPermissions.MANAGE_FLEETS,
                    DefaultPermissions.MANAGE_CONSIGNMENTS,
                    DefaultPermissions.MANAGE_DELIVERIES,
                    DefaultPermissions.MANAGE_PAYMENTS,
                    DefaultPermissions.MANAGE_ROUTES,
                    DefaultPermissions.MANAGE_CHARGES,
                    DefaultPermissions.MANAGE_PARCELS,
                    DefaultPermissions.MANAGE_FARES,
                    DefaultPermissions.MANAGE_TRACKING,
                    DefaultPermissions.MANAGE_SCHEDULES
            ));

    final String code;
    final String roleName;
    final AppDomains domain;
    final List<DefaultPermissions> permissions;
}
