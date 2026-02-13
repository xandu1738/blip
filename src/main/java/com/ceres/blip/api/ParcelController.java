package com.ceres.blip.api;

import com.ceres.blip.dtos.OperationReturnObject;
import com.ceres.blip.services.ParcelService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/parcels")
@RequiredArgsConstructor
public class ParcelController {

    private final ParcelService parcelService;

    @PostMapping("/register")
    public OperationReturnObject registerParcel(@RequestBody JsonNode object) {
        return parcelService.registerParcel(object);
    }

    @GetMapping("/list")
    public OperationReturnObject listParcels() {
        return parcelService.listParcels();
    }

    @GetMapping("/stats")
    public OperationReturnObject getStats() {
        return parcelService.getStats();
    }
}
