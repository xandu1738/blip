package com.ceres.blip.models.database;

import com.ceres.blip.models.enums.TripStatus;
import jakarta.persistence.*;
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

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "bus_id", nullable = false)
    private Long busId;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "trip_date")
    private LocalDate tripDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private TripStatus status;

    @Column(name = "partner_code")
    private String partnerCode;

    @Column(name = "set_off_time")
    private Timestamp setOffTime;

    @Column(name = "estimated_arrival_time")
    private Timestamp estimatedArrivalTime;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "created_by")
    private Long createdBy;

}