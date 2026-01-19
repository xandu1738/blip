package com.ceres.blip.api;

import com.ceres.blip.services.SubscriptionService;
import com.ceres.blip.dtos.OperationReturnObject;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "subscriptions")
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionsController {
    private final SubscriptionService subscriptionService;

    @GetMapping("/plans/{pageNumber}/{pageSize}")
    public ResponseEntity<OperationReturnObject> plansList(@PathVariable int pageSize, @PathVariable int pageNumber) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionPlans(pageNumber, pageSize));
    }

    @GetMapping("/requests/{pageNumber}/{pageSize}")
    public ResponseEntity<OperationReturnObject> subscriptionRequests(@PathVariable int pageSize, @PathVariable int pageNumber) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionRequests(pageNumber, pageSize));
    }

    @GetMapping("/list/{pageNumber}/{pageSize}")
    public ResponseEntity<OperationReturnObject> subscriptionList(@PathVariable int pageSize, @PathVariable int pageNumber) {
        return ResponseEntity.ok(subscriptionService.getSubscriptions(pageNumber, pageSize));
    }

    @PostMapping("/request")
    public ResponseEntity<OperationReturnObject> subscriptionRequest(@RequestBody JsonNode request) {
        return ResponseEntity.ok(subscriptionService.saveSubscriptionRequest(request));
    }

    @PostMapping("/request/confirm-payment")
    public ResponseEntity<OperationReturnObject> approveSubscriptionRequest(@RequestBody JsonNode request) {
        return ResponseEntity.ok(subscriptionService.approveSubscriptionRequest(request));
    }
}
