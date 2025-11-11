package com.ceres.blip.models.database;

import com.ceres.blip.models.enums.RouteStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "route")
public class RouteModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "partner_code", nullable = false, length = 100)
    private String partnerCode;

    @Column(name = "origin", nullable = false, length = 100)
    private String origin;

    @Column(name = "destination", nullable = false, length = 100)
    private String destination;

    @Column(name = "estimated_distance", nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedDistance;

    @Column(name = "estimated_duration_hrs", nullable = false, precision = 10)
    private Double estimatedDuration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RouteStatus status = RouteStatus.ACTIVE;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

/*
 TODO [Reverse Engineering] create field to map the 'estimated_time' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "estimated_time", columnDefinition = "interval not null")
    private Object estimatedTime;
*/
}