package com.ceres.project.services;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ceres.project.exceptions.AuthorizationRequiredException;
import com.ceres.project.models.database.PartnerModel;
import com.ceres.project.models.database.SystemUserModel;
import com.ceres.project.models.database.VehicleModel;
import com.ceres.project.models.jpa_helpers.enums.VehicleTypes;
import com.ceres.project.repositories.VehicleRepository;
import com.ceres.project.services.base.BaseWebActionsService;
import com.ceres.project.utils.OperationReturnObject;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

@Service
public class VehicleService extends BaseWebActionsService {
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        super();
        this.vehicleRepository = vehicleRepository;
    }

    //Add new vehicle for partner
    private OperationReturnObject addNewVehicle(JSONObject object) throws AuthorizationRequiredException {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();

            requires(object, "data");
            JSONObject data = object.getJSONObject("data");
            requires(data, "registration_number", "type", "partner_code", "capacity");
            String registrationNumber = data.getString("registration_number");
            String partnerCode = data.getString("partner_code");
            String type = data.getString("type");
            Integer capacity = data.getInteger("capacity");

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

    //Vehicle Bulk Registration: Receives a list of vehicles to be registered for a partner as a json array
    private OperationReturnObject bulkVehicleRegistration(JSONObject object) throws AuthorizationRequiredException {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();

            requires(object, "data");
            JSONObject data = object.getJSONObject("data");
            requires(data, "vehicles");
            JSONArray vehiclesArray = data.getJSONArray("vehicles");

            String partnerCode = data.getString("partner_code");
            PartnerModel partner = validatePartner(partnerCode);

            // Process each vehicle in the array
            vehiclesArray.forEach(vehicle -> {
                JSONObject vehicleData = (JSONObject) vehicle;
                requires(vehicleData, "registration_number", "type", "partner_code", "capacity");
                String registrationNumber = vehicleData.getString("registration_number");
                String type = vehicleData.getString("type");
                Integer capacity = vehicleData.getInteger("capacity");

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
    private OperationReturnObject editVehicleInformation(JSONObject object) throws AuthorizationRequiredException {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();

            requires(object, "data");
            JSONObject data = object.getJSONObject("data");
            requires(data, "vehicle_id");
            Long vehicleId = data.getLong("vehicle_id");

            VehicleModel vehicleModel = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

            // Update fields if present
            if (data.containsKey("registration_number")) {
                vehicleModel.setRegistrationNumber(data.getString("registration_number"));
            }
            if (data.containsKey("type")) {
                String type = data.getString("type");
                if (!EnumUtils.isValidEnum(VehicleTypes.class, type)) {
                    throw new IllegalArgumentException("Invalid vehicle type");
                }
                vehicleModel.setType(VehicleTypes.valueOf(type));
            }
            if (data.containsKey("capacity")) {
                vehicleModel.setCapacity(data.getInteger("capacity"));
            }
            if (data.containsKey("status")) {
                vehicleModel.setStatus(data.getString("status"));
            }

            VehicleModel updatedVehicle = vehicleRepository.save(vehicleModel);
            return new OperationReturnObject(200, "Vehicle Information Updated Successfully", updatedVehicle);
        } catch (IllegalArgumentException e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        }
    }

    //Assign Vehicle to partner
    private OperationReturnObject assignVehicleToPartner(JSONObject object) throws AuthorizationRequiredException {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();

            requires(object, "data");
            JSONObject data = object.getJSONObject("data");
            requires(data, "vehicle_id", "partner_code");
            Long vehicleId = data.getLong("vehicle_id");
            String partnerCode = data.getString("partner_code");

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

    //TODO: Assign Vehicle to driver

    @Override
    public OperationReturnObject switchActions(String action, JSONObject request) throws AuthorizationRequiredException {
        return switch (action){
            case "AddNewVehicle" -> addNewVehicle(request);
            case "VehicleBulkRegistration" -> bulkVehicleRegistration(request);
            case "EditVehicleInformation" -> editVehicleInformation(request);
            case "AssignVehicleToPartner" -> assignVehicleToPartner(request);
            default -> new OperationReturnObject(400, "Invalid action: " + action, null);
        };
    }
}
