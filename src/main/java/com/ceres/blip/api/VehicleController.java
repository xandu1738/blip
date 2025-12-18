package com.ceres.blip.api;

import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.services.VehicleService;
import com.ceres.blip.utils.OperationReturnObject;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Vehicles API")
public class VehicleController {
    private final VehicleService vehicleService;

    @PostMapping("/add-vehicle")
    public ResponseEntity<OperationReturnObject> addNewVehicle(@RequestBody JsonNode request) {
        return ResponseEntity.ok(vehicleService.addNewVehicle(request));
    }

    @PostMapping("/edit-vehicle-info")
    public ResponseEntity<OperationReturnObject> editVehicleInformation(@RequestBody JsonNode request) {
        return ResponseEntity.ok(vehicleService.editVehicleInformation(request));
    }

    @GetMapping("/list/{pageNumber}/{pageSize}")
    public ResponseEntity<OperationReturnObject> vehiclesList(@PathVariable int pageSize, @PathVariable int pageNumber) throws AuthorizationRequiredException {
        return ResponseEntity.ok(vehicleService.vehiclesList(pageNumber, pageSize));
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<OperationReturnObject> vehicleProfile(@PathVariable long vehicleId) throws AuthorizationRequiredException {
        return ResponseEntity.ok(vehicleService.fetchVehicleDetails(vehicleId));
    }

    @GetMapping("/partner/{partnerCode}/{vehicleId}")
    public ResponseEntity<OperationReturnObject> assignVehicleToPartner(@PathVariable String partnerCode, @PathVariable Long vehicleId) {
        return ResponseEntity.ok(vehicleService.assignVehicleToPartner(partnerCode, vehicleId));
    }
}
