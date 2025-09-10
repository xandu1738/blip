package com.ceres.blip.services;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.blip.models.database.TripModel;
import com.ceres.blip.models.database.TripSeatModel;
import com.ceres.blip.models.jpa_helpers.enums.TripSeatStatus;
import com.ceres.blip.models.jpa_helpers.enums.TripStatus;
import com.ceres.blip.repositories.SeatRepository;
import com.ceres.blip.repositories.TripRepository;
import com.ceres.blip.repositories.TripSeatRepository;
import com.ceres.blip.repositories.VehicleRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.ceres.blip.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

    public OperationReturnObject addTrip(JSONObject object) {
        requires(object, "data");
        JSONObject data = object.getJSONObject("data");
        requires(data, "route_id", "bus_id", "trip_date");
        Long routeId = data.getLong("route_id");
        Long busId = data.getLong("bus_id");
        String td = data.getString("trip_date");
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

    public OperationReturnObject editTrip(JSONObject object) {
        requires(object, "data");
        JSONObject data = object.getJSONObject("data");
        requires(data, "id");
        Long id = data.getLong("id");

        TripModel tripModel = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Trip with ID " + id + " not found."));
        Long routeId = data.getLong("route_id");
        if (routeId != null) {
            tripModel.setRouteId(routeId);
        }
        Long busId = data.getLong("bus_id");
        //todo: reconcile trip seats to new bus seats. UNLESS we have some good number that has already booked seats then reject modification.
        if (busId != null) {
            tripModel.setBusId(busId);
        }
        String td = data.getString("trip_date");
        if (StringUtils.isNotBlank(td)) {
            LocalDate tripDate = LocalDate.parse(td);
            tripModel.setTripDate(tripDate);
        }

        repository.save(tripModel);
        return new OperationReturnObject(200, "Trip successfully edited.", tripModel);
    }

    public OperationReturnObject tripList(int pageNumber, int pageSize) {
        Page<TripModel> trips = repository.findAll(PageRequest.of(pageNumber, pageSize));
        return new OperationReturnObject(200, "Trips list successfully fetched.", trips);
    }

    public OperationReturnObject tripDetails(long tripId) {
        return new OperationReturnObject(200, "Trip details successfully fetched.", repository.findById(tripId).orElse(null));
    }

    public OperationReturnObject removeTrip(long tripId) {
        if (tripId == 0) {
            throw new IllegalArgumentException("Trip ID cannot be null");
        }
        repository.deleteById(tripId);
        return new OperationReturnObject(200, "Trip successfully removed.", null);
    }
}
