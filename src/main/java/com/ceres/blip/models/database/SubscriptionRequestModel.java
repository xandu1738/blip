package com.ceres.blip.models.database;

import com.ceres.blip.models.enums.SubscriptionRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "subscription_requests")
public class SubscriptionRequestModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "partner_code", length = Integer.MAX_VALUE)
    private String partnerCode;

    @Column(name = "subscription_reference", length = Integer.MAX_VALUE)
    private String subscriptionReference;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "subscription_plan")
    private Long subscriptionPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private SubscriptionRequestStatus status;

    @ColumnDefault("now()")
    @Column(name = "requested_on")
    private Timestamp requestedOn;


}