package com.ceres.blip.api;

import com.ceres.blip.services.SubscriptionService;
import com.ceres.blip.utils.OperationReturnObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "subscriptions")
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionsController {
    private final SubscriptionService subscriptionService;


    @GetMapping("/list/{pageNumber}/{pageSize}")
    public ResponseEntity<OperationReturnObject> modulesList(@PathVariable int pageSize, @PathVariable int pageNumber) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionPlans(pageNumber, pageSize));
    }
}
