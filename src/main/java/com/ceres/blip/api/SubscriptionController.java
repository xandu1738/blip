package com.ceres.blip.api;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.blip.services.SubscriptionService;
import com.ceres.blip.utils.OperationReturnObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Subscriptions and Subscription plans API")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping("/plans/{pageNumber}/{pageSize}")
    public ResponseEntity<OperationReturnObject> getSubscriptionPlans(@PathVariable int pageNumber, @PathVariable int pageSize) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionPlans(pageNumber, pageSize));
    }

    @GetMapping("/list/{pageNumber}/{pageSize}")
    public ResponseEntity<OperationReturnObject> getSubscriptionsList(@PathVariable int pageNumber, @PathVariable int pageSize) {
        return ResponseEntity.ok(subscriptionService.getSubscriptions(pageNumber, pageSize));
    }

    @GetMapping("/plan/{planCode}")
    public ResponseEntity<OperationReturnObject> getSubscriptionPlanDetails(@PathVariable String planCode) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionPlanDetails(planCode));
    }

    @GetMapping("/{subscriptionId}")
    public ResponseEntity<OperationReturnObject> getSubscriptionDetails(@PathVariable long subscriptionId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionDetails(subscriptionId));
    }

    @PostMapping("/create-subscription")
    public ResponseEntity<OperationReturnObject> createSubscription(@RequestBody JSONObject request) {
        return ResponseEntity.ok(subscriptionService.saveSubscription(request));
    }

    @PostMapping("/create-plan")
    public ResponseEntity<OperationReturnObject> createSubscriptionPlan(@RequestBody JSONObject request) {
        return ResponseEntity.ok(subscriptionService.saveSubscriptionPlan(request));
    }
}
