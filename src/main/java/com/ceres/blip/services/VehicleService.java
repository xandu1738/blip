package com.ceres.blip.services;

import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.models.database.PartnerModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.models.database.VehicleModel;
import com.ceres.blip.models.enums.AppDomains;
import com.ceres.blip.models.enums.VehicleTypes;
import com.ceres.blip.repositories.VehicleRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.ceres.blip.utils.OperationReturnObject;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleService extends LocalUtilsService{
    private static final String PARTNER_CODE = "partner_code";
    private static final String REGISTRATION_NUMBER = "registration_number";
    private static final String TYPE = "type";
    private static final String CAPACITY = "capacity";
    private static final String DATA = "data";
    private static final String VEHICLES = "vehicles";
    private static final String VEHICLE_ID = "vehicle_id";
    private static final String STATUS = "status";

    private final VehicleRepository vehicleRepository;

    //Add a new vehicle for partner
    public OperationReturnObject addNewVehicle(JsonNode object) {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();

            requires(object, DATA);
            JsonNode data = getRequestData(object);
            requires(data, REGISTRATION_NUMBER, TYPE, PARTNER_CODE, CAPACITY);
            String registrationNumber = data.get(REGISTRATION_NUMBER).asText();
            String partnerCode = data.get(PARTNER_CODE).asText();
            String type = data.get(TYPE).asText();
            Integer capacity = data.get(CAPACITY).asInt();

            if (!EnumUtils.isValidEnum(VehicleTypes.class, type)) {
                throw new IllegalArgumentException("Invalid vehicle type");
            }

            VehicleModel vehicleModel = new VehicleModel();
            vehicleModel.setRegistrationNumber(registrationNumber);
            vehicleModel.setPartnerCode(partnerCode);
            vehicleModel.setType(VehicleTypes.valueOf(type));
            vehicleModel.setCreatedAt(getCurrentTimestamp());
            vehicleModel.setCreatedBy(authenticatedUser.getId());
            vehicleModel.setCapacity(capacity);

            VehicleModel savedVehicle = vehicleRepository.save(vehicleModel);
            return new OperationReturnObject(200, "Vehicle Successfully registered", savedVehicle);
        } catch (IllegalArgumentException e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        }
    }

    //Vehicle Bulk Registration: Receives a list of vehicles to be registered for a partner as a JSON array
    public OperationReturnObject bulkVehicleRegistration(JsonNode object) {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();

            JsonNode data = getRequestData(object);
            requires(data, VEHICLES);
            JsonNode vehiclesArray = data.path(VEHICLES);

            if (!vehiclesArray.isArray()) {
                throw new IllegalArgumentException("Invalid vehicles array");
            }

            String partnerCode = data.get(PARTNER_CODE).asText();
            PartnerModel partner = validatePartner(partnerCode);

            // Process each vehicle in the array
            vehiclesArray.forEach(vehicleData -> {
                requires(vehicleData, REGISTRATION_NUMBER, TYPE, PARTNER_CODE, CAPACITY);
                String registrationNumber = vehicleData.get(REGISTRATION_NUMBER).asText();
                String type = vehicleData.get(TYPE).asText();
                Integer capacity = vehicleData.get(CAPACITY).asInt();

                if (!EnumUtils.isValidEnum(VehicleTypes.class, type)) {
                    throw new IllegalArgumentException("Invalid vehicle type for registration number: " + registrationNumber);
                }

                VehicleModel vehicleModel = new VehicleModel();
                vehicleModel.setRegistrationNumber(registrationNumber);
                vehicleModel.setPartnerCode(partner.getPartnerCode());
                vehicleModel.setType(VehicleTypes.valueOf(type));
                vehicleModel.setCreatedAt(getCurrentTimestamp());
                vehicleModel.setCreatedBy(authenticatedUser.getId());
                vehicleModel.setCapacity(capacity);

                vehicleRepository.save(vehicleModel);
            });

            return new OperationReturnObject(200, "Bulk Vehicle Registration Successful", null);
        } catch (IllegalArgumentException e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        }
    }

    //Edit Vehicle information
    public OperationReturnObject editVehicleInformation(JsonNode object) {
        try {
            authenticatedUser();

            requires(object, DATA);
            JsonNode data = getRequestData(object);
            requires(data, VEHICLE_ID);
            Long vehicleId = data.get(VEHICLE_ID).asLong();

            VehicleModel vehicleModel = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

            // Update fields if present
            if (data.has(REGISTRATION_NUMBER)) {
                vehicleModel.setRegistrationNumber(data.get(REGISTRATION_NUMBER).asText());
            }
            if (data.has(TYPE)) {
                String type = data.get(TYPE).asText();
                if (!EnumUtils.isValidEnum(VehicleTypes.class, type)) {
                    throw new IllegalArgumentException("Invalid vehicle type");
                }
                vehicleModel.setType(VehicleTypes.valueOf(type));
            }
            if (data.has(CAPACITY)) {
                vehicleModel.setCapacity(data.get(CAPACITY).asInt());
            }
            if (data.has(STATUS)) {
                vehicleModel.setStatus(data.get(STATUS).asText());
            }

            VehicleModel updatedVehicle = vehicleRepository.save(vehicleModel);
            return new OperationReturnObject(200, "Vehicle Information Updated Successfully", updatedVehicle);
        } catch (IllegalArgumentException e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        }
    }

    //Assign Vehicle to partner
    public OperationReturnObject assignVehicleToPartner(String partnerCode, Long vehicleId) {
        try {
            authenticatedUser();

            PartnerModel partner = validatePartner(partnerCode);

            VehicleModel vehicleModel = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

            vehicleModel.setPartnerCode(partner.getPartnerCode());
            VehicleModel updatedVehicle = vehicleRepository.save(vehicleModel);
            return new OperationReturnObject(200, "Vehicle Assigned to Partner Successfully", updatedVehicle);
        } catch (IllegalArgumentException e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        }
    }

    @Cacheable(value = "vehicles", key = "#pageNumber + '-' + #pageSize")
    public OperationReturnObject vehiclesList(int pageNumber, int pageSize) throws AuthorizationRequiredException {
        requiresAuth();
        SystemUserModel authenticatedUser = authenticatedUser();
        Page<VehicleModel> vehicles = null;

        if (getUserDomain().equals(AppDomains.BACK_OFFICE)){
            vehicles = vehicleRepository.findAll(PageRequest.of(pageNumber, pageSize));
            return new OperationReturnObject(200, "Vehicles list successfully fetched.", vehicles);
        }

        vehicles = vehicleRepository.findAllByPartnerCode(authenticatedUser.getPartnerCode(), PageRequest.of(pageNumber, pageSize));
        return new OperationReturnObject(200, "Vehicles list successfully fetched.", vehicles);
    }

    @Cacheable(value = "vehicle", key = "#vehicleId")
    public OperationReturnObject fetchVehicleDetails(Long vehicleId) throws AuthorizationRequiredException {
        requiresAuth();
        VehicleModel vehicleModel = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        return new OperationReturnObject(200, "Vehicle details successfully fetched.", vehicleModel);
    }

    //TODO: Assign Vehicle to driver
}
