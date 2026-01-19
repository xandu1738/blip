package com.ceres.blip.dtos;

import com.ceres.blip.models.enums.SubscriptionRequestStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record SubscriptionRequestModelDto(
        Long id,
        String partnerCode,
        String subscriptionReference,
        SubscriptionRequestStatus status,
        Timestamp requestedOn,
        BigDecimal amountPaid,
        String planName,
        String requestedBy,
        String paymentConfirmedBy,
        BigDecimal paymentAmount,
        Boolean paymentConfirmed) {

}