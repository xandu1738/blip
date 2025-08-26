package com.ceres.blip.services;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.blip.models.database.PartnerModel;
import com.ceres.blip.models.database.RouteModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.models.jpa_helpers.enums.RouteStatus;
import com.ceres.blip.repositories.RouteRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.ceres.blip.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RouteService{
    private static final String DATA = "data";
    private static final String ROUTE_ID = "route_id";
    private static final String ORIGIN = "origin";
    private static final String DESTINATION = "destination";
    private static final String ESTIMATED_DISTANCE = "estimated_distance";
    private static final String ESTIMATED_DURATION = "estimated_duration";
    private static final String STATUS = "status";
    private static final String PARTNER_CODE = "partner_code";
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error: ";
    private final RouteRepository routeRepository;
    private final LocalUtilsService localUtilsService;
    //create a new route for a partner
    private OperationReturnObject createNewRoute(JSONObject object) {
        try {
            SystemUserModel authenticatedUser = localUtilsService.authenticatedUser();
            localUtilsService.requires(object, DATA);
            JSONObject data = object.getJSONObject(DATA);
            localUtilsService.requires(data, ORIGIN, DESTINATION, PARTNER_CODE, ESTIMATED_DISTANCE, ESTIMATED_DURATION);
            String origin = data.getString(ORIGIN);
            String destination = data.getString(DESTINATION);
            String partnerCode = data.getString(PARTNER_CODE);
            Double estimatedDistance = data.getDouble(ESTIMATED_DISTANCE);
            Double estimatedDuration = data.getDouble(ESTIMATED_DURATION);

            PartnerModel partner = localUtilsService.validatePartner(partnerCode);

            RouteModel routeModel = new RouteModel();
            routeModel.setOrigin(origin);
            routeModel.setDestination(destination);
            routeModel.setPartnerCode(partner.getPartnerCode());
            routeModel.setEstimatedDistance(BigDecimal.valueOf(estimatedDistance));
            routeModel.setEstimatedDuration(estimatedDuration);
            routeModel.setCreatedAt(localUtilsService.getCurrentTimestamp());
            routeModel.setCreatedBy(authenticatedUser.getId());
            routeModel.setStatus(RouteStatus.ACTIVE);

            RouteModel savedRoute = routeRepository.save(routeModel);
            return new OperationReturnObject(200, "Route Successfully created", savedRoute);
        } catch (IllegalArgumentException e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        } catch (Exception e) {
            return new OperationReturnObject(400, INTERNAL_SERVER_ERROR + e.getMessage(), null);
        }
    }

    //Edit route details
    private OperationReturnObject editRouteDetails(JSONObject object) {
        try {
            localUtilsService.requiresAuth();
            localUtilsService.requires(object, DATA);
            JSONObject data = object.getJSONObject(DATA);
            localUtilsService.requires(data, ROUTE_ID);
            Long routeId = data.getLong(ROUTE_ID);

            RouteModel existingRoute = routeRepository.findById(routeId)
                    .orElseThrow(() -> new IllegalArgumentException("Route with ID " + routeId + " not found"));

            if (data.containsKey(ORIGIN)) {
                existingRoute.setOrigin(data.getString(ORIGIN));
            }
            if (data.containsKey(DESTINATION)) {
                existingRoute.setDestination(data.getString(DESTINATION));
            }
            if (data.containsKey(ESTIMATED_DISTANCE)) {
                existingRoute.setEstimatedDistance(BigDecimal.valueOf(data.getDouble(ESTIMATED_DISTANCE)));
            }
            if (data.containsKey(ESTIMATED_DURATION)) {
                existingRoute.setEstimatedDuration(data.getDouble(ESTIMATED_DURATION));
            }
            if (data.containsKey(STATUS)) {
                String status = data.getString(STATUS);
                if (!EnumUtils.isValidEnum(RouteStatus.class, status)) {
                    throw new IllegalArgumentException("Invalid status value");
                }
                existingRoute.setStatus(RouteStatus.valueOf(status.toUpperCase()));
            }

            RouteModel updatedRoute = routeRepository.save(existingRoute);
            return new OperationReturnObject(200, "Route Successfully updated", updatedRoute);
        } catch (IllegalArgumentException e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        } catch (Exception e) {
            return new OperationReturnObject(400, INTERNAL_SERVER_ERROR + e.getMessage(), null);
        }
    }

    //List all routes for a partner
    private OperationReturnObject listRoutes(JSONObject object) {
        try {
            localUtilsService.authenticatedUser();
            JSONObject search = object.getJSONObject("search");
            String partnerCode = null;
            if (search != null && search.containsKey(PARTNER_CODE)) {
                partnerCode = search.getString(PARTNER_CODE);
                localUtilsService.validatePartner(partnerCode);
            }
            return new OperationReturnObject(200, "Routes fetched successfully", routeRepository.findAllByPartnerCode(partnerCode));
        } catch (IllegalArgumentException e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        } catch (Exception e) {
            return new OperationReturnObject(400, INTERNAL_SERVER_ERROR + e.getMessage(), null);
        }
    }

    //Get Single route details
    private OperationReturnObject getRouteDetails(JSONObject object) {
        try {
            localUtilsService.authenticatedUser();
            localUtilsService.requires(object, DATA);
            JSONObject data = object.getJSONObject(DATA);
            localUtilsService.requires(data, ROUTE_ID);
            Long routeId = data.getLong(ROUTE_ID);

            RouteModel existingRoute = routeRepository.findById(routeId)
                    .orElseThrow(() -> new IllegalArgumentException("Route with ID " + routeId + " not found"));

            return new OperationReturnObject(200, "Route fetched successfully", existingRoute);
        } catch (IllegalArgumentException e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        } catch (Exception e) {
            return new OperationReturnObject(400, INTERNAL_SERVER_ERROR + e.getMessage(), null);
        }
    }
}
