package com.ceres.project.services;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
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
    private static final String PARTNER_CODE = "partner_code";
    private static final String REGISTRATION_NUMBER = "registration_number";
    private static final String TYPE = "type";
    private static final String CAPACITY = "capacity";
    private static final String DATA = "data";
    private static final String VEHICLES = "vehicles";
    private static final String VEHICLE_ID = "vehicle_id";
    private static final String STATUS = "status";

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        super();
        this.vehicleRepository = vehicleRepository;
    }

    //Add a new vehicle for partner
    private OperationReturnObject addNewVehicle(JSONObject object) {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();

            requires(object, DATA);
            JSONObject data = object.getJSONObject(DATA);
            requires(data, REGISTRATION_NUMBER, TYPE, PARTNER_CODE, CAPACITY);
            String registrationNumber = data.getString(REGISTRATION_NUMBER);
            String partnerCode = data.getString(PARTNER_CODE);
            String type = data.getString(TYPE);
            Integer capacity = data.getInteger(CAPACITY);

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
    private OperationReturnObject bulkVehicleRegistration(JSONObject object) {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();

            requires(object, DATA);
            JSONObject data = object.getJSONObject(DATA);
            requires(data, VEHICLES);
            JSONArray vehiclesArray = data.getJSONArray(VEHICLES);

            String partnerCode = data.getString(PARTNER_CODE);
            PartnerModel partner = validatePartner(partnerCode);

            // Process each vehicle in the array
            vehiclesArray.forEach(vehicle -> {
                JSONObject vehicleData = (JSONObject) vehicle;
                requires(vehicleData, REGISTRATION_NUMBER, TYPE, PARTNER_CODE, CAPACITY);
                String registrationNumber = vehicleData.getString(REGISTRATION_NUMBER);
                String type = vehicleData.getString(TYPE);
                Integer capacity = vehicleData.getInteger(CAPACITY);

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
    private OperationReturnObject editVehicleInformation(JSONObject object) {
        try {
            authenticatedUser();

            requires(object, DATA);
            JSONObject data = object.getJSONObject(DATA);
            requires(data, VEHICLE_ID);
            Long vehicleId = data.getLong(VEHICLE_ID);

            VehicleModel vehicleModel = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

            // Update fields if present
            if (data.containsKey(REGISTRATION_NUMBER)) {
                vehicleModel.setRegistrationNumber(data.getString(REGISTRATION_NUMBER));
            }
            if (data.containsKey(TYPE)) {
                String type = data.getString(TYPE);
                if (!EnumUtils.isValidEnum(VehicleTypes.class, type)) {
                    throw new IllegalArgumentException("Invalid vehicle type");
                }
                vehicleModel.setType(VehicleTypes.valueOf(type));
            }
            if (data.containsKey(CAPACITY)) {
                vehicleModel.setCapacity(data.getInteger(CAPACITY));
            }
            if (data.containsKey(STATUS)) {
                vehicleModel.setStatus(data.getString(STATUS));
            }

            VehicleModel updatedVehicle = vehicleRepository.save(vehicleModel);
            return new OperationReturnObject(200, "Vehicle Information Updated Successfully", updatedVehicle);
        } catch (IllegalArgumentException e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        }
    }

    //Assign Vehicle to partner
    private OperationReturnObject assignVehicleToPartner(JSONObject object) {
        try {
            authenticatedUser();

            requires(object, DATA);
            JSONObject data = object.getJSONObject(DATA);
            requires(data, VEHICLE_ID, PARTNER_CODE);
            Long vehicleId = data.getLong(VEHICLE_ID);
            String partnerCode = data.getString(PARTNER_CODE);

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
    public OperationReturnObject switchActions(String action, JSONObject request) {
        return switch (action){
            case "AddNewVehicle" -> addNewVehicle(request);
            case "VehicleBulkRegistration" -> bulkVehicleRegistration(request);
            case "EditVehicleInformation" -> editVehicleInformation(request);
            case "AssignVehicleToPartner" -> assignVehicleToPartner(request);
            default -> new OperationReturnObject(400, "Invalid action: " + action, null);
        };
    }
}
