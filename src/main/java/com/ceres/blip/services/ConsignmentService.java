package com.ceres.blip.services;

import com.ceres.blip.dtos.OperationReturnObject;
import com.ceres.blip.models.database.ConsignmentModel;
import com.ceres.blip.models.database.ConsignmentParcelModel;
import com.ceres.blip.models.database.ParcelModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.repositories.ConsignmentParcelRepository;
import com.ceres.blip.repositories.ConsignmentRepository;
import com.ceres.blip.repositories.ParcelRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsignmentService extends LocalUtilsService {

    private final ConsignmentRepository consignmentRepository;
    private final ParcelRepository parcelRepository;
    private final ConsignmentParcelRepository consignmentParcelRepository;

    @CacheEvict(value = { "consignments", "consignment" }, allEntries = true)
    public OperationReturnObject createConsignment(JsonNode object) {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();
            requires(object, "data");
            JsonNode data = getRequestData(object);
            requires(data, "scheduleId", "vehicleId", "origin", "destination");

            ConsignmentModel consignment = new ConsignmentModel();
            consignment.setConsignmentNumber("CON-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            consignment.setScheduleId(data.get("scheduleId").asLong());
            consignment.setVehicleId(data.get("vehicleId").asLong());
            consignment.setOrigin(data.get("origin").asText());
            consignment.setDestination(data.get("destination").asText());
            consignment.setTotalWeight(BigDecimal.ZERO);
            consignment.setTotalCost(BigDecimal.ZERO);
            consignment.setStatus("created");
            consignment.setCreatedBy(authenticatedUser.getId());
            consignment.setCreatedAt(getCurrentTimestamp());
            consignment.setUpdatedAt(getCurrentTimestamp());

            ConsignmentModel savedConsignment = consignmentRepository.save(consignment);
            return new OperationReturnObject(200, "Consignment created successfully", savedConsignment);
        } catch (Exception e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        }
    }

    @Transactional
    public OperationReturnObject assignParcel(JsonNode object) {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();
            requires(object, "data");
            JsonNode data = getRequestData(object);
            requires(data, "consignmentId", "parcelId");

            Long consignmentId = data.get("consignmentId").asLong();
            Long parcelId = data.get("parcelId").asLong();

            ConsignmentModel consignment = consignmentRepository.findById(consignmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Consignment not found"));
            ParcelModel parcel = parcelRepository.findById(parcelId)
                    .orElseThrow(() -> new IllegalArgumentException("Parcel not found"));

            if (!"registered".equals(parcel.getStatus())) {
                throw new IllegalArgumentException("Parcel is already assigned or delivered");
            }

            ConsignmentParcelModel assignment = new ConsignmentParcelModel();
            assignment.setConsignmentId(consignmentId);
            assignment.setParcelId(parcelId);
            if (data.has("remarks")) {
                assignment.setRemarks(data.get("remarks").asText());
            }
            assignment.setAddedAt(getCurrentTimestamp());
            assignment.setAddedBy(authenticatedUser.getId());

            consignmentParcelRepository.save(assignment);

            // Update parcel status
            parcel.setStatus("assigned");
            parcelRepository.save(parcel);

            // Update consignment totals
            consignment.setTotalWeight(consignment.getTotalWeight().add(parcel.getWeight()));
            consignment.setTotalCost(consignment.getTotalCost().add(parcel.getCost()));
            consignment.setUpdatedAt(getCurrentTimestamp());
            consignment.setLastUpdatedBy(authenticatedUser.getId());
            consignmentRepository.save(consignment);

            return new OperationReturnObject(200, "Parcel assigned to consignment successfully", null);
        } catch (Exception e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        }
    }

    public OperationReturnObject listConsignments() {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();
            List<ConsignmentModel> consignments = consignmentRepository.findAllByCreatedBy(authenticatedUser.getId());
            return new OperationReturnObject(200, "Consignments fetched successfully", consignments);
        } catch (Exception e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        }
    }

    public OperationReturnObject getStats() {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();
            List<ConsignmentModel> consignments = consignmentRepository.findAllByCreatedBy(authenticatedUser.getId());

            long total = consignments.size();
            long created = consignments.stream().filter(c -> "created".equals(c.getStatus())).count();
            long inTransit = consignments.stream().filter(c -> "in_transit".equals(c.getStatus())).count();
            long delivered = consignments.stream().filter(c -> "delivered".equals(c.getStatus())).count();

            Map<String, Long> stats = new HashMap<>();
            stats.put("total", total);
            stats.put("created", created);
            stats.put("inTransit", inTransit);
            stats.put("delivered", delivered);

            return new OperationReturnObject(200, "Stats fetched successfully", stats);
        } catch (Exception e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        }
    }
}
