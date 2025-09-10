package com.ceres.blip.api;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.blip.services.TripService;
import com.ceres.blip.utils.OperationReturnObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
@Tag(name = "Trips", description = "Trips API")
public class TripController {
    private final TripService tripService;

    @PostMapping("/add-trip")
    public OperationReturnObject addNewTrip(@RequestBody JSONObject request) {
        return tripService.addTrip(request);
    }

    @PostMapping("/edit-trip")
    public OperationReturnObject editTrip(@RequestBody JSONObject request) {
        return tripService.editTrip(request);
    }

    @GetMapping("/list/{pageNumber}/{pageSize}")
    public OperationReturnObject tripsList(@PathVariable int pageNumber, @PathVariable int pageSize) {
        return tripService.tripList(pageNumber, pageSize);
    }

    @GetMapping("/detail/{id}")
    public OperationReturnObject tripDetail(@PathVariable int id) {
        return tripService.tripDetails(id);
    }

    @DeleteMapping("/remove/{id}")
    public OperationReturnObject removeTrip(@PathVariable int id) {
        return tripService.removeTrip(id);
    }
}
