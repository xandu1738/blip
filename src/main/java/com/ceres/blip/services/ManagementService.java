package com.ceres.blip.services;

import com.ceres.blip.annotations.base.RequiresAuthentication;
import com.ceres.blip.dtos.OperationReturnObject;
import com.ceres.blip.models.database.AmenityModel;
import com.ceres.blip.models.database.SystemRoleModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.models.database.VehicleCategoryModel;
import com.ceres.blip.models.enums.AppDomains;
import com.ceres.blip.repositories.AmenitiesRepository;
import com.ceres.blip.repositories.SystemRoleRepository;
import com.ceres.blip.repositories.VehicleCategoryRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ManagementService extends LocalUtilsService {
    private final SystemRoleRepository systemRoleRepository;
    private final com.ceres.blip.repositories.DistrictRepository districtRepository;
    private final VehicleCategoryRepository vehicleCategoryRepository;
    private final AmenitiesRepository amenitiesRepository;

    public @NonNull Map<String, Object> fetchEnumValues() {
        List<Map<String, String>> domains = Arrays.stream(AppDomains.values())
                .map(o -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("optionLabel", o.getDomainName());
                    map.put("optionValue", o.name());
                    return map;
                }).toList();

        List<String> paymentStatuses = List.of("PENDING", "SUCCESS", "FAILED");
        Map<String, Object> mapper = new HashMap<>();
        mapper.put("domains", domains);
        mapper.put("paymentStatuses", paymentStatuses);
        return mapper;
    }

    public OperationReturnObject fetchSystemRoles() {
        SystemUserModel authenticatedUser = authenticatedUser();
        SystemRoleModel userRole = systemRoleRepository.findByRoleCode(authenticatedUser.getRoleCode())
                .orElseThrow(() -> new IllegalStateException("User has no role assigned"));

        List<SystemRoleModel> rolesICanManage = systemRoleRepository.findAllByRoleDomain(userRole.getRoleDomain());
        return new OperationReturnObject(200, "Roles fetched successfully", rolesICanManage);
    }

    public OperationReturnObject fetchDistricts() {
        return new OperationReturnObject(200, "Districts fetched successfully", districtRepository.findAll());
    }

    @CacheEvict(value = {"vehicleCategories"}, allEntries = true)
    public OperationReturnObject addVehicleCategory(JsonNode object) {
        JsonNode data = getRequestData(object);
        requires(data, "category_name", "description");
        String categoryName = data.get("category_name").asText();
        String description = data.get("description").asText();

        JsonNode amenities = data.get("amenities");
        List<String> amenitiesList = new ArrayList<>();
        if (amenities != null && amenities.isArray()) {
            for (JsonNode amenity : amenities) {
                amenitiesList.add(amenity.asText());
            }
        }

        String code = categoryName.toUpperCase().replace(" ", "_");
        Optional<VehicleCategoryModel> existing = vehicleCategoryRepository.findByCategoryCode(code);
        if (existing.isPresent()) {
            return new OperationReturnObject(400, "Vehicle Category with this code already exists", null);
        }

        VehicleCategoryModel categories = new VehicleCategoryModel();
        categories.setName(categoryName);
        categories.setDescription(description);
        categories.setCategoryCode(code);
        categories.setAmenities(amenitiesList);
        vehicleCategoryRepository.save(categories);

        return new OperationReturnObject(200, "Vehicle Category added successfully", null);
    }

    @Cacheable(value = "vehicleCategories")
    public OperationReturnObject fetchVehicleCategories() {
        return new OperationReturnObject(200, "Vehicle Categories fetched successfully", vehicleCategoryRepository.findAll());
    }

    @RequiresAuthentication
    @CacheEvict(value = {"amenities"}, allEntries = true)
    public OperationReturnObject addAmenity(JsonNode object) {
        JsonNode data = getRequestData(object);
        requires(data, "name", "description");

        String name = data.get("name").asText();
        String code = name.toUpperCase().replace(" ", "_");

        Optional<AmenityModel> existing = amenitiesRepository.findByCode(code);
        if (existing.isPresent()) {
            return new OperationReturnObject(400, "Amenity with this code already exists", null);
        }

        String description = data.get("description").asText();
        AmenityModel amenity = new AmenityModel();
        amenity.setName(name);
        amenity.setDescription(description);
        amenity.setCode(code);

        amenitiesRepository.save(amenity);
        return new OperationReturnObject(200, "Amenity added successfully", null);
    }

    @Cacheable(value = "amenities")
    public OperationReturnObject fetchAmenities() {
        return new OperationReturnObject(200, "Amenities fetched successfully", amenitiesRepository.findAll());
    }

    @CacheEvict(value = {"vehicleCategories"}, allEntries = true)
    public OperationReturnObject deactivateCategory(@NonNull String category) {
        VehicleCategoryModel categoryModel = vehicleCategoryRepository.findByCategoryCode(category)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        categoryModel.setActive(false);

        vehicleCategoryRepository.save(categoryModel);
        return new OperationReturnObject(200, "Category deactivated successfully", null);
    }
}
