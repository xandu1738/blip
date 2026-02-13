package com.ceres.blip.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public enum DefaultPermissions {
    /*
     * modules, partners, users, roles, domains, permissions
     * vehicles, parcels, trips, refunds, drivers
     * routes, trips, fares, charges, licenses
     * tracking, fleets, consignments, deliveries, payments
     * */
    MANAGE_MODULES("MANAGE_MODULES", "Can manage modules", List.of(AppDomains.BACK_OFFICE)),
    MANAGE_PARTNERS("MANAGE_PARTNERS", "Can manage partners", List.of(AppDomains.BACK_OFFICE)),
    MANAGE_USERS("MANAGE_USERS", "Can manage users", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    MANAGE_ROLES("MANAGE_ROLES", "Can manage roles", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    MANAGE_DOMAINS("MANAGE_DOMAINS", "Can manage domains", List.of(AppDomains.BACK_OFFICE)),
    MANAGE_VEHICLES("MANAGE_VEHICLES", "Can manage vehicles", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    MANAGE_PARCELS("MANAGE_PARCELS", "Can manage parcels", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    MANAGE_ROUTES("MANAGE_ROUTES", "Can manage routes", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    MANAGE_TRACKING("MANAGE_TRACKING", "Can manage tracking", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    MANAGE_PAYMENTS("MANAGE_PAYMENTS", "Can manage payments", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    MANAGE_DELIVERIES("MANAGE_DELIVERIES", "Can manage deliveries", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    MANAGE_REFUNDS("MANAGE_REFUNDS", "Can manage refunds", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    MANAGE_LICENSES("MANAGE_LICENSES", "Can manage licenses", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    MANAGE_FLEETS("MANAGE_FLEETS", "Can manage fleets", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    MANAGE_CHARGES("MANAGE_CHARGES", "Can manage charges", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    MANAGE_TRIPS("MANAGE_TRIPS", "Can manage trips", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    MANAGE_CONSIGNMENTS("MANAGE_CONSIGNMENTS", "Can manage consignments", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    MANAGE_DRIVERS("MANAGE_DRIVERS", "Can manage drivers", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    MANAGE_SCHEDULES("MANAGE_SCHEDULES", "Can manage schedules", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    MANAGE_FARES("MANAGE_FARES", "Can manage fare", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),

    //View Permissions
    VIEW_MODULES("VIEW_MODULES", "Can view modules", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_PARTNERS("VIEW_PARTNERS", "Can view partners", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_USERS("VIEW_USERS", "Can view users", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_ROLES("VIEW_ROLES", "Can view roles", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_DOMAINS("VIEW_DOMAINS", "Can view domains", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_VEHICLES("VIEW_VEHICLES", "Can view vehicles", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_PARCELS("VIEW_PARCELS", "Can view parcels", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_ROUTES("VIEW_ROUTES", "Can view routes", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_TRACKING("VIEW_TRACKING", "Can view tracking", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_PAYMENTS("VIEW_PAYMENTS", "Can view payments", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_DELIVERIES("VIEW_DELIVERIES", "Can view deliveries", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_REFUNDS("VIEW_REFUNDS", "Can view refunds", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_LICENSES("VIEW_LICENSES", "Can view licenses", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_FLEETS("VIEW_FLEETS", "Can view fleets", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_CHARGES("VIEW_CHARGES", "Can view charges", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_TRIPS("VIEW_TRIPS", "Can view trips", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_CONSIGNMENTS("VIEW_CONSIGNMENTS", "Can view consignments", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),
    VIEW_DRIVERS("VIEW_DRIVERS", "Can view drivers", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN, AppDomains.PUBLIC)),

    ASSIGNS_PERMISSIONS("ASSIGNS_PERMISSIONS", "Can Assign Permissions to roles", List.of(AppDomains.BACK_OFFICE, AppDomains.CLIENT_ADMIN)),
    ADMINISTRATOR("ADMINISTRATOR", "Can administer the system", List.of(AppDomains.BACK_OFFICE));
    final String permissionName;
    final String description;
    final List<AppDomains> domain;
}
