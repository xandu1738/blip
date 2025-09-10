package com.ceres.blip.models.database;

import com.ceres.blip.models.jpa_helpers.enums.TripStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "trips")
public class TripModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "route_id")
    private Long routeId;

    @Column(name = "bus_id")
    private Long busId;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "trip_date")
    private LocalDate tripDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private TripStatus status;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "created_by")
    private Long createdBy;

}