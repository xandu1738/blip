package com.ceres.blip.api;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.blip.services.RouteService;
import com.ceres.blip.utils.OperationReturnObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@Tag(name = "Routes", description = "Routes API")
public class RoutesController {
    private final RouteService routeService;

    @PostMapping("/add-route")
    public ResponseEntity<OperationReturnObject> addNewRoute(@RequestBody JSONObject request) {
        return ResponseEntity.ok(routeService.createNewRoute(request));
    }

    @PostMapping("/edit-route-details")
    public ResponseEntity<OperationReturnObject> editRouteDetails(@RequestBody JSONObject request) {
        return ResponseEntity.ok(routeService.editRouteDetails(request));
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<OperationReturnObject> getRouteDetails(@PathVariable long routeId) {
        return ResponseEntity.ok(routeService.getRouteDetails(routeId));
    }

    @GetMapping("/list/{partnerCode}/{pageNumber}/{pageSize}")
    public ResponseEntity<OperationReturnObject> routesList(@PathVariable String partnerCode, @PathVariable int pageSize, @PathVariable int pageNumber) {
        return ResponseEntity.ok(routeService.listRoutes(partnerCode, pageNumber, pageSize));
    }
}
