package com.ceres.blip.models.database;

import com.ceres.blip.models.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "partner_subscriptions")
public class PartnerSubscriptionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "partner_id")
    private Long partnerId;

    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SubscriptionStatus status;

    @Column(name = "renewal_type", length = Integer.MAX_VALUE)
    private String renewalType;

    @Column(name = "renewal_date")
    private Timestamp renewalDate;

    @Column(name = "cancellation_date")
    private Timestamp cancellationDate;

}