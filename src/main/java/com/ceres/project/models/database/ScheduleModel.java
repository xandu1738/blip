package com.ceres.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "schedule")
public class ScheduleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "route_id", nullable = false)
    private Long routeId;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "departure_time", nullable = false)
    private Instant departureTime;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "arrival_time", nullable = false)
    private Instant arrivalTime;

    @ColumnDefault("'scheduled'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

}