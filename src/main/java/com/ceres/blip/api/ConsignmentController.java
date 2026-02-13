package com.ceres.blip.api;

import com.ceres.blip.dtos.OperationReturnObject;
import com.ceres.blip.services.ConsignmentService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/consignments")
@RequiredArgsConstructor
public class ConsignmentController {

    private final ConsignmentService consignmentService;

    @PostMapping("/create")
    public OperationReturnObject createConsignment(@RequestBody JsonNode object) {
        return consignmentService.createConsignment(object);
    }

    @PostMapping("/assign")
    public OperationReturnObject assignParcel(@RequestBody JsonNode object) {
        return consignmentService.assignParcel(object);
    }

    @GetMapping("/list")
    public OperationReturnObject listConsignments() {
        return consignmentService.listConsignments();
    }

    @GetMapping("/{id}/details")
    public OperationReturnObject getConsignmentDetails(@PathVariable Long id) {
        return consignmentService.getConsignmentDetails(id);
    }

    @GetMapping("/stats")
    public OperationReturnObject getStats() {
        return consignmentService.getStats();
    }
}
