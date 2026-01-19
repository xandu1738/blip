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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TripService extends LocalUtilsService {
    private final TripRepository repository;
    private final VehicleRepository vehicleRepository;
    private final SeatRepository seatRepository;
    private final TripSeatRepository tripSeatRepository;

    public OperationReturnObject addTrip(JsonNode object) {
        JsonNode data = getRequestData(object);
        requires(data, "route_id", "bus_id", "trip_date");
        Long routeId = data.get("route_id").asLong();
        Long busId = data.get("bus_id").asLong();
        String td = data.get("trip_date").asText();
        LocalDate tripDate = LocalDate.parse(td);

        TripStatus status = TripStatus.SCHEDULED;

        TripModel tripModel = new TripModel();
        tripModel.setBusId(busId);
        tripModel.setRouteId(routeId);
        tripModel.setStatus(status);
        tripModel.setTripDate(tripDate);
        tripModel.setCreatedAt(getCurrentTimestamp());
        tripModel.setCreatedBy(authenticatedUser().getId());
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

        TripModel tripModel = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Trip with ID " + id + " not found."));
        Long routeId = data.get("route_id").asLong();
        tripModel.setRouteId(routeId);
        Long busId = data.get("bus_id").asLong();
        //todo: reconcile trip seats to new bus seats. UNLESS we have some good number that has already booked seats then reject modification.
        tripModel.setBusId(busId);
        String td = data.get("trip_date").asText();
        if (StringUtils.isNotBlank(td)) {
            LocalDate tripDate = LocalDate.parse(td);
            tripModel.setTripDate(tripDate);
        }

        repository.save(tripModel);
        return new OperationReturnObject(200, "Trip successfully edited.", tripModel);
    }

    @Cacheable(value = "trips", key = "#pageNumber + '-' + #pageSize")
    public OperationReturnObject tripList(int pageNumber, int pageSize) {
        Page<TripModel> trips = repository.findAll(PageRequest.of(pageNumber, pageSize));
        return new OperationReturnObject(200, "Trips list successfully fetched.", trips);
    }

    @Cacheable(value = "trip", key = "#tripId")
    public OperationReturnObject tripDetails(long tripId) {
        return new OperationReturnObject(200, "Trip details successfully fetched.", repository.findById(tripId).orElse(null));
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
