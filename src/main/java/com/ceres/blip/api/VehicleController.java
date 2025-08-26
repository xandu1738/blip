package com.ceres.blip.api;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.services.VehicleService;
import com.ceres.blip.utils.OperationReturnObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Vehicles API")
public class VehicleController {
    private final VehicleService vehicleService;

    @PostMapping("/add-vehicle")
    public OperationReturnObject addNewVehicle(@RequestBody JSONObject request) {
        return vehicleService.addNewVehicle(request);
    }

    @PostMapping("/edit-vehicle-info")
    public OperationReturnObject editVehicleInformation(@RequestBody JSONObject request) {
        return vehicleService.editVehicleInformation(request);
    }

    @GetMapping("/list/{pageNumber}/{pageSize}")
    public OperationReturnObject vehiclesList(@PathVariable int pageSize, @PathVariable int pageNumber) throws AuthorizationRequiredException {
        return vehicleService.vehiclesList(pageNumber, pageSize);
    }

    @GetMapping("/{vehicleId}")
    public OperationReturnObject vehicleProfile(@PathVariable long vehicleId) throws AuthorizationRequiredException {
        return vehicleService.fetchVehicleDetails(vehicleId);
    }

    @GetMapping("/partner/{partnerCode}/{vehicleId}")
    public OperationReturnObject assignVehicleToPartner(@PathVariable String partnerCode, @PathVariable Long vehicleId) {
        return vehicleService.assignVehicleToPartner(partnerCode, vehicleId);
    }
}
