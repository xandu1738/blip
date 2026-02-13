package com.ceres.blip.services;

import com.ceres.blip.dtos.OperationReturnObject;
import com.ceres.blip.models.database.SystemRoleModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.models.enums.AppDomains;
import com.ceres.blip.repositories.SystemRoleRepository;
import com.ceres.blip.utils.LocalUtilsService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ManagementService extends LocalUtilsService {
    private final SystemRoleRepository systemRoleRepository;
    private final com.ceres.blip.repositories.DistrictRepository districtRepository;

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
}
