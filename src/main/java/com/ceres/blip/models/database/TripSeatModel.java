package com.ceres.blip.models.database;

import com.ceres.blip.models.jpa_helpers.enums.TripSeatStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "trip_seats")
public class TripSeatModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "seat_id")
    private Long seatId;

    @Column(name = "trip_id")
    private Long tripId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private TripSeatStatus status;

    @Column(name = "booked_at")
    private Timestamp bookedAt;

    @Column(name = "booked_by")
    private Long bookedBy;

}