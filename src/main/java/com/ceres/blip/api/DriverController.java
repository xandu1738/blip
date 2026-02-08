package com.ceres.blip.api;

import com.ceres.blip.dtos.OperationReturnObject;
import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.services.DriverService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@Tag(name = "Drivers", description = "Drivers Management API")
public class DriverController {
    private final DriverService driverService;

    @PostMapping("/add-driver")
    public ResponseEntity<OperationReturnObject> addNewDriver(@RequestBody JsonNode request) {
        return ResponseEntity.ok(driverService.addNewDriver(request));
    }

    @GetMapping("/list/{pageNumber}/{pageSize}")
    public ResponseEntity<OperationReturnObject> listDrivers(@PathVariable int pageNumber, @PathVariable int pageSize)
            throws AuthorizationRequiredException {
        return ResponseEntity.ok(driverService.listDrivers(pageNumber, pageSize));
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<OperationReturnObject> fetchDriverDetails(@PathVariable Long driverId)
            throws AuthorizationRequiredException {
        return ResponseEntity.ok(driverService.fetchDriverDetails(driverId));
    }

    @PostMapping("/edit-driver-info")
    public ResponseEntity<OperationReturnObject> editDriverInformation(@RequestBody JsonNode request) {
        return ResponseEntity.ok(driverService.editDriver(request));
    }

    @DeleteMapping("/{driverId}")
    public ResponseEntity<OperationReturnObject> deleteDriver(@PathVariable Long driverId) {
        // Assuming deleteDriver returns OperationReturnObject
        return ResponseEntity.ok(driverService.deleteDriver(driverId));
    }
}
