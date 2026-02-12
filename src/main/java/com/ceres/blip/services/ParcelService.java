package com.ceres.blip.services;

import com.ceres.blip.dtos.OperationReturnObject;
import com.ceres.blip.models.database.ParcelModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.repositories.ParcelRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParcelService extends LocalUtilsService {

    private final ParcelRepository parcelRepository;

    public OperationReturnObject registerParcel(JsonNode object) {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();
            requires(object, "data");
            JsonNode data = getRequestData(object);
            requires(data, "receiverName", "receiverPhone", "pickupLocation", "dropOffLocation", "weight", "type",
                    "cost");

            ParcelModel parcel = new ParcelModel();
            parcel.setSenderId(authenticatedUser.getId());
            parcel.setReceiverName(data.get("receiverName").asText());
            parcel.setReceiverPhone(data.get("receiverPhone").asText());
            parcel.setPickupLocation(data.get("pickupLocation").asText());
            parcel.setDropOffLocation(data.get("dropOffLocation").asText());
            parcel.setWeight(new BigDecimal(data.get("weight").asText()));
            if (data.has("dimensions")) {
                parcel.setDimensions(data.get("dimensions").asText());
            }
            parcel.setType(data.get("type").asText());
            parcel.setCost(new BigDecimal(data.get("cost").asText()));
            parcel.setStatus("registered");
            parcel.setCreatedAt(getCurrentTimestamp());
            parcel.setCreatedBy(authenticatedUser.getId());

            ParcelModel savedParcel = parcelRepository.save(parcel);
            return new OperationReturnObject(200, "Parcel registered successfully", savedParcel);
        } catch (Exception e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        }
    }

    public OperationReturnObject listParcels() {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();
            List<ParcelModel> parcels = parcelRepository.findAllByCreatedBy(authenticatedUser.getId());
            return new OperationReturnObject(200, "Parcels fetched successfully", parcels);
        } catch (Exception e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        }
    }

    public OperationReturnObject getStats() {
        try {
            SystemUserModel authenticatedUser = authenticatedUser();
            List<ParcelModel> parcels = parcelRepository.findAllByCreatedBy(authenticatedUser.getId());

            long total = parcels.size();
            long registered = parcels.stream().filter(p -> "registered".equals(p.getStatus())).count();
            long assigned = parcels.stream().filter(p -> "assigned".equals(p.getStatus())).count();
            long delivered = parcels.stream().filter(p -> "delivered".equals(p.getStatus())).count();

            Map<String, Long> stats = new HashMap<>();
            stats.put("total", total);
            stats.put("registered", registered);
            stats.put("assigned", assigned);
            stats.put("delivered", delivered);

            return new OperationReturnObject(200, "Stats fetched successfully", stats);
        } catch (Exception e) {
            return new OperationReturnObject(400, e.getMessage(), null);
        }
    }
}
