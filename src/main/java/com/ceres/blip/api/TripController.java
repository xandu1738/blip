package com.ceres.blip.api;

import com.ceres.blip.services.TripService;
import com.ceres.blip.utils.OperationReturnObject;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
@Tag(name = "Trips", description = "Trips API")
public class TripController {
    private final TripService tripService;

    @PostMapping("/add-trip")
    public ResponseEntity<OperationReturnObject> addNewTrip(@RequestBody JsonNode request) {
        return ResponseEntity.ok(tripService.addTrip(request));
    }

    @PostMapping("/edit-trip")
    public ResponseEntity<OperationReturnObject> editTrip(@RequestBody JsonNode request) {
        return ResponseEntity.ok(tripService.editTrip(request));
    }

    @GetMapping("/list/{pageNumber}/{pageSize}")
    public ResponseEntity<OperationReturnObject> tripsList(@PathVariable int pageNumber, @PathVariable int pageSize) {
        return ResponseEntity.ok(tripService.tripList(pageNumber, pageSize));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<OperationReturnObject> tripDetail(@PathVariable int id) {
        return ResponseEntity.ok(tripService.tripDetails(id));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<OperationReturnObject> removeTrip(@PathVariable int id) {
        return ResponseEntity.ok(tripService.removeTrip(id));
    }
}
