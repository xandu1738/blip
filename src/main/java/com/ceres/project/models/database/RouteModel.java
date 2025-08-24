package com.ceres.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "route")
public class RouteModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "origin", nullable = false, length = 100)
    private String origin;

    @Column(name = "destination", nullable = false, length = 100)
    private String destination;

    @Column(name = "estimated_distance", nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedDistance;

    @ColumnDefault("'ACTIVE'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

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