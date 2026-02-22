package com.ceres.blip.api;

import com.ceres.blip.dtos.OperationReturnObject;
import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.services.ManagementService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Management", description = "Management endpoints")
@RestController
@RequestMapping("/api/v1/management")
public class ManagementController {
    private final ManagementService managementService;

    public ManagementController(ManagementService managementService) {
        this.managementService = managementService;
    }

    @GetMapping
    public OperationReturnObject enumValues() {
        Map<String, Object> mapper = managementService.fetchEnumValues();

        return new OperationReturnObject(200, "OK", mapper);
    }

    @GetMapping("/our-roles")
    public OperationReturnObject systemRoles() {
        return managementService.fetchSystemRoles();
    }

    @GetMapping("/districts")
    public OperationReturnObject fetchDistricts() {
        return managementService.fetchDistricts();
    }

    @GetMapping("/vehicle-categories")
    public OperationReturnObject fetchVehicleCategories() {
        return managementService.fetchVehicleCategories();
    }
    @GetMapping("/amenities")
    public OperationReturnObject fetchAmenities() {
        return managementService.fetchAmenities();
    }

    @PostMapping("/add-vehicle-category")
    public OperationReturnObject addVehicleCategory(@RequestBody JsonNode object) throws AuthorizationRequiredException {
        return managementService.addVehicleCategory(object);
    }

    @PostMapping("/add-amenity")
    public OperationReturnObject addAmenity(@RequestBody JsonNode object) throws AuthorizationRequiredException {
        return managementService.addAmenity(object);
    }
}
