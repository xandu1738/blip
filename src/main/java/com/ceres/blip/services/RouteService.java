package com.ceres.blip.services;

import com.ceres.blip.models.database.PartnerModel;
import com.ceres.blip.models.database.RouteModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.models.enums.AppDomains;
import com.ceres.blip.models.enums.RouteStatus;
import com.ceres.blip.repositories.RouteRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.ceres.blip.dtos.OperationReturnObject;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RouteService extends LocalUtilsService{
    private static final String DATA = "data";
    private static final String ROUTE_ID = "route_id";
    private static final String ORIGIN = "origin";
    private static final String DESTINATION = "destination";
    private static final String ESTIMATED_DISTANCE = "estimated_distance";
    private static final String ESTIMATED_DURATION = "estimated_duration";
    private static final String STATUS = "status";
    private static final String PARTNER_CODE = "partner_code";
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error: ";
    private static final String ROUTES_FETCHED_SUCCESSFULLY = "Routes fetched successfully";
    private final RouteRepository routeRepository;
    //create a new route for a partner
    public OperationReturnObject createNewRoute(JsonNode object) {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();
            JsonNode data = getRequestData(object);
            requires(data, ORIGIN, DESTINATION, PARTNER_CODE, ESTIMATED_DISTANCE, ESTIMATED_DURATION);
            String origin = data.get(ORIGIN).asText();
            String destination = data.get(DESTINATION).asText();
            String partnerCode = data.get(PARTNER_CODE).asText();
            Double estimatedDistance = data.get(ESTIMATED_DISTANCE).asDouble();
            Double estimatedDuration = data.get(ESTIMATED_DURATION).asDouble();

            PartnerModel partner = validatePartner(partnerCode);

            RouteModel routeModel = new RouteModel();
            routeModel.setOrigin(origin);
            routeModel.setDestination(destination);
            routeModel.setPartnerCode(partner.getPartnerCode());
            routeModel.setEstimatedDistance(BigDecimal.valueOf(estimatedDistance));
            routeModel.setEstimatedDuration(estimatedDuration);
            routeModel.setCreatedAt(getCurrentTimestamp());
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
    @CachePut(value = "routes", key = "#object.data.id")
    public OperationReturnObject editRouteDetails(JsonNode object) {
        try {
            requiresAuth();
            requires(object, DATA);
            JsonNode data = getRequestData(object);
            requires(data, ROUTE_ID);
            Long routeId = data.get(ROUTE_ID).asLong();

            RouteModel existingRoute = routeRepository.findById(routeId)
                    .orElseThrow(() -> new IllegalArgumentException("Route with ID " + routeId + " not found"));

            if (data.has(ORIGIN)) {
                existingRoute.setOrigin(data.get(ORIGIN).asText());
            }
            if (data.has(DESTINATION)) {
                existingRoute.setDestination(data.get(DESTINATION).asText());
            }
            if (data.has(ESTIMATED_DISTANCE)) {
                existingRoute.setEstimatedDistance(BigDecimal.valueOf(data.get(ESTIMATED_DISTANCE).asDouble()));
            }
            if (data.has(ESTIMATED_DURATION)) {
                existingRoute.setEstimatedDuration(data.get(ESTIMATED_DURATION).asDouble());
            }
            if (data.has(STATUS)) {
                String status = data.get(STATUS).asText();
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
    @Cacheable(value = "routes", key = "#pageNumber + '-' + #pageSize")
    public OperationReturnObject listRoutes(String partnerCode,int pageNumber, int pageSize) {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();
            if (StringUtils.isNotBlank(authenticatedUser.getPartnerCode())) {
                validatePartner(partnerCode);
                return new OperationReturnObject(200, ROUTES_FETCHED_SUCCESSFULLY, routeRepository.findAllByPartnerCode(partnerCode, PageRequest.of(pageNumber, pageSize)));
            }

            belongsTo(AppDomains.BACK_OFFICE);
            if (getUserDomain().equals(AppDomains.BACK_OFFICE) && StringUtils.isNotBlank(partnerCode)){
                validatePartner(partnerCode);
                return new OperationReturnObject(200, ROUTES_FETCHED_SUCCESSFULLY, routeRepository.findAllByPartnerCode(partnerCode, PageRequest.of(pageNumber, pageSize)));
            }
            return new OperationReturnObject(200, ROUTES_FETCHED_SUCCESSFULLY, routeRepository.findAll(PageRequest.of(pageNumber, pageSize)));
        } catch (IllegalArgumentException e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        } catch (Exception e) {
            return new OperationReturnObject(400, INTERNAL_SERVER_ERROR + e.getMessage(), null);
        }
    }

    //Get Single route details
    @Cacheable(value = "route", key = "#routeId")
    public OperationReturnObject getRouteDetails(long routeId) {
        try {
            authenticatedUser();

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
