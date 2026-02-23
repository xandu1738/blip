package com.ceres.blip.services;

import com.ceres.blip.dtos.ListResponseDto;
import com.ceres.blip.dtos.OperationReturn;
import com.ceres.blip.dtos.OperationReturnObject;
import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.models.database.DriverModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.models.enums.AppDomains;
import com.ceres.blip.repositories.DriverRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverService extends LocalUtilsService {

    private static final String NAME = "name";
    private static final String LICENSE_NUMBER = "license_number";
    private static final String CONTACT_NUMBER = "contact_number";
    private static final String PARTNER_CODE = "partner_code";
    private static final String DATA = "data";
    private static final String STATUS = "status";
    private static final String DRIVER_ID = "driver_id";

    private final DriverRepository driverRepository;
    private final NotificationService notificationService;

//    @CacheEvict(value = "drivers", allEntries = true)
    public OperationReturnObject addNewDriver(JsonNode object) {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();
            requires(object, DATA);
            JsonNode data = getRequestData(object);
            requires(data, NAME, LICENSE_NUMBER, CONTACT_NUMBER);

            String name = data.get(NAME).asText();
            String licenseNumber = data.get(LICENSE_NUMBER).asText();
            String contactNumber = data.get(CONTACT_NUMBER).asText();
            String partnerCode = authenticatedUser.getPartnerCode();

            OperationReturn operationReturn = correctMSISDN(contactNumber);
            if (operationReturn.returnCode() != 200) {
                throw new IllegalArgumentException(operationReturn.returnMessage());
            }
            contactNumber = operationReturn.returnMessage();

            if (StringUtils.isBlank(partnerCode)) {
                requires(data, PARTNER_CODE);
                partnerCode = data.get(PARTNER_CODE).asText();
            }

            if (StringUtils.isBlank(partnerCode)) {
                throw new IllegalStateException("Partner code cannot be blank");
            }

            Optional<DriverModel> existingDriver = driverRepository.findByLicenseNumber(licenseNumber);
            if (existingDriver.isPresent()) {
                throw new IllegalArgumentException("Driver with this license number already exists");
            }

            DriverModel driver = new DriverModel();
            driver.setName(name);
            driver.setLicenseNumber(licenseNumber);
            driver.setContactNumber(contactNumber);
            driver.setPartnerCode(partnerCode);
            driver.setStatus("ACTIVE");
            driver.setCreatedAt(getCurrentTimestamp());
            driver.setCreatedBy(authenticatedUser.getId());

            DriverModel savedDriver = driverRepository.save(driver);

            notificationService.sendNotification(
                    partnerCode,
                    "New Driver Registered",
                    "A new driver, " + name + ", has been added to the system.",
                    "DRIVER_ADDED");

            return new OperationReturnObject(200, "Driver successfully created", savedDriver);

        } catch (IllegalArgumentException e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        }
    }

//    @Cacheable(value = "drivers", key = "#pageNumber + '-' + #pageSize")
    public OperationReturnObject listDrivers(int pageNumber, int pageSize) throws AuthorizationRequiredException {
        requiresAuth();
        SystemUserModel authenticatedUser = authenticatedUser();
        List<DriverModel> drivers;

        if (getUserDomain().equals(AppDomains.BACK_OFFICE) && StringUtils.isBlank(authenticatedUser.getPartnerCode())) {
            drivers = driverRepository.findAll(PageRequest.of(pageNumber, pageSize)).toList();
        } else {
            drivers = driverRepository
                    .findAllByPartnerCode(authenticatedUser.getPartnerCode(), PageRequest.of(pageNumber, pageSize))
                    .toList();
        }

        return new OperationReturnObject(200, "Drivers list successfully fetched",
                new ListResponseDto((long) drivers.size(), drivers));
    }

//    @Cacheable(value = "driver", key = "#driverId")
    public OperationReturnObject fetchDriverDetails(Long driverId) throws AuthorizationRequiredException {
        requiresAuth();
        DriverModel driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
        return new OperationReturnObject(200, "Driver details successfully fetched", driver);
    }

//    @CacheEvict(value = {"drivers", "driver"}, allEntries = true)
    public OperationReturnObject editDriver(JsonNode object) {
        try {
            authenticatedUser();
            requires(object, DATA);
            JsonNode data = getRequestData(object);
            requires(data, DRIVER_ID);

            String partnerCode = authenticatedUser().getPartnerCode();
            if (StringUtils.isBlank(partnerCode)) {
                requires(data, PARTNER_CODE);
                partnerCode = data.get(PARTNER_CODE).asText();
            }

            if (StringUtils.isBlank(partnerCode)) {
                throw new IllegalStateException("Partner code cannot be blank");
            }

            Long driverId = data.get(DRIVER_ID).asLong();
            DriverModel driver = driverRepository.findByIdAndPartnerCode(driverId,partnerCode)
                    .orElseThrow(() -> new IllegalArgumentException("Driver not found"));

            if (data.has(NAME))
                driver.setName(data.get(NAME).asText());
            if (data.has(LICENSE_NUMBER)) {
                String newLicense = data.get(LICENSE_NUMBER).asText();
                if (!newLicense.equals(driver.getLicenseNumber())
                    && driverRepository.findByLicenseNumber(newLicense).isPresent()) {
                    throw new IllegalArgumentException("License number already in use by another driver");
                }
                driver.setLicenseNumber(newLicense);
            }
            if (data.has(CONTACT_NUMBER))
                driver.setContactNumber(data.get(CONTACT_NUMBER).asText());
            if (data.has(STATUS))
                driver.setStatus(data.get(STATUS).asText());

            DriverModel updatedDriver = driverRepository.save(driver);
            return new OperationReturnObject(200, "Driver details updated successfully", updatedDriver);

        } catch (IllegalArgumentException e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        }
    }

//    @CacheEvict(value = {"drivers", "driver"}, allEntries = true)
    public OperationReturnObject deleteDriver(Long driverId) {
        try {
            authenticatedUser();
            DriverModel driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new IllegalArgumentException("Driver not found"));

            // Soft delete
            driver.setStatus("DELETED");
            driverRepository.save(driver);

            return new OperationReturnObject(200, "Driver successfully deleted", null);
        } catch (IllegalArgumentException e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        }
    }
}
