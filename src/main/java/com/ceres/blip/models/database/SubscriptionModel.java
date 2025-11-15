package com.ceres.blip.models.database;

import com.ceres.blip.models.enums.SubscriptionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "subscriptions")
public class SubscriptionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "partner_code")
    private String partnerCode;

    @Size(max = 255)
    @Column(name = "module_code")
    private String moduleCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private SubscriptionStatus status;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "subscription_plan")
    private String subscriptionPlan;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

}