package com.ceres.blip.services;

import com.ceres.blip.models.database.TripModel;
import com.ceres.blip.models.database.TripSeatModel;
import com.ceres.blip.models.enums.TripSeatStatus;
import com.ceres.blip.models.enums.TripStatus;
import com.ceres.blip.repositories.SeatRepository;
import com.ceres.blip.repositories.TripRepository;
import com.ceres.blip.repositories.TripSeatRepository;
import com.ceres.blip.repositories.VehicleRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.ceres.blip.dtos.OperationReturnObject;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService extends LocalUtilsService {
    private final TripRepository repository;
    private final VehicleRepository vehicleRepository;
    private final SeatRepository seatRepository;
    private final TripSeatRepository tripSeatRepository;

    public OperationReturnObject addTrip(JsonNode object) {
        JsonNode data = getRequestData(object);
        requires(data, "schedule_id", "bus_id", "trip_date");
        Long scheduleId = data.get("schedule_id").asLong();
        Long busId = data.get("bus_id").asLong();
        String td = data.get("trip_date").asText();
        LocalDate tripDate = LocalDate.parse(td);

        TripStatus status = TripStatus.SCHEDULED;

        TripModel tripModel = new TripModel();
        tripModel.setBusId(busId);
        tripModel.setScheduleId(scheduleId);
        tripModel.setStatus(status);
        tripModel.setTripDate(tripDate);

        if (data.has("driver_id")) {
            tripModel.setDriverId(data.get("driver_id").asLong());
        }

        if (data.has("set_off_time")) {
            tripModel.setSetOffTime(stringToTimestamp(data.get("set_off_time").asText()));
        }
        if (data.has("estimated_arrival_time")) {
            tripModel.setEstimatedArrivalTime(stringToTimestamp(data.get("estimated_arrival_time").asText()));
        }

        tripModel.setCreatedAt(getCurrentTimestamp());
        tripModel.setCreatedBy(authenticatedUser().getId());
        tripModel.setPartnerCode(authenticatedUser().getPartnerCode());
        TripModel savedTrip = repository.save(tripModel);

        executeAsync(() -> vehicleRepository.findById(busId).ifPresent(
                vehicleModel -> seatRepository.findAllByBusId(vehicleModel.getId()).forEach(seatModel -> {
                    TripSeatModel tripSeatModel = new TripSeatModel();
                    tripSeatModel.setTripId(savedTrip.getId());
                    tripSeatModel.setSeatId(seatModel.getId());
                    tripSeatModel.setStatus(TripSeatStatus.AVAILABLE);
                    tripSeatRepository.save(tripSeatModel);
                })));

        return new OperationReturnObject(200, "Trip successfully added.", tripModel);
    }

    @CachePut(value = "trip", key = "#object.data.id")
    public OperationReturnObject editTrip(JsonNode object) {
        JsonNode data = getRequestData(object);
        requires(data, "id");
        Long id = data.get("id").asLong();

        TripModel tripModel = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip with ID " + id + " not found."));

        if (data.has("schedule_id")) {
            Long scheduleId = data.get("schedule_id").asLong();
            tripModel.setScheduleId(scheduleId);
        }

        if (data.has("bus_id")) {
            Long busId = data.get("bus_id").asLong();
            // todo: reconcile trip seats to new bus seats. UNLESS we have some good number
            // that has already booked seats then reject modification.
            tripModel.setBusId(busId);
        }

        String td = data.get("trip_date") != null ? data.get("trip_date").asText() : null;
        if (StringUtils.isNotBlank(td)) {
            LocalDate tripDate = LocalDate.parse(td);
            tripModel.setTripDate(tripDate);
        }

        if (data.has("set_off_time")) {
            tripModel.setSetOffTime(stringToTimestamp(data.get("set_off_time").asText()));
        }
        if (data.has("estimated_arrival_time")) {
            tripModel.setEstimatedArrivalTime(stringToTimestamp(data.get("estimated_arrival_time").asText()));
        }

        if (data.has("driver_id")) {
            tripModel.setDriverId(data.get("driver_id").asLong());
        }

        repository.save(tripModel);
        return new OperationReturnObject(200, "Trip successfully edited.", tripModel);
    }

    @Cacheable(value = "trips", key = "#pageNumber + '-' + #pageSize")
    public OperationReturnObject tripList(int pageNumber, int pageSize) {
        List<TripModel> trips = repository.findAllByPartnerCode(authenticatedUser().getPartnerCode(),
                PageRequest.of(pageNumber, pageSize)).toList();
        return new OperationReturnObject(200, "Trips list successfully fetched.", trips);
    }

    @Cacheable(value = "trip", key = "#tripId")
    public OperationReturnObject tripDetails(long tripId) {
        return new OperationReturnObject(200, "Trip details successfully fetched.",
                repository.findById(tripId).orElse(null));
    }

    @CacheEvict(value = "trips", key = "#tripId")
    public OperationReturnObject removeTrip(long tripId) {
        if (tripId == 0) {
            throw new IllegalArgumentException("Trip ID cannot be null");
        }
        repository.deleteById(tripId);
        return new OperationReturnObject(200, "Trip successfully removed.", null);
    }
}
