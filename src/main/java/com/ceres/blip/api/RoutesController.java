package com.ceres.blip.api;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.blip.services.RouteService;
import com.ceres.blip.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RoutesController {
    private final RouteService routeService;

    @PostMapping("/add-route")
    public OperationReturnObject addNewRoute(@RequestBody JSONObject request) {
        return routeService.createNewRoute(request);
    }

    @PostMapping("/edit-route-details")
    public OperationReturnObject editRouteDetails(@RequestBody JSONObject request) {
        return routeService.editRouteDetails(request);
    }

    @GetMapping("/{routeId}")
    public OperationReturnObject getRouteDetails(@PathVariable long routeId) {
        return routeService.getRouteDetails(routeId);
    }

    @GetMapping("/list/{partnerCode}/{pageNumber}/{pageSize}")
    public OperationReturnObject routesList(@PathVariable String partnerCode, @PathVariable int pageSize, @PathVariable int pageNumber) {
        return routeService.listRoutes(partnerCode, pageNumber, pageSize);
    }
}
