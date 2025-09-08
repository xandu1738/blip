package com.ceres.blip.models.database;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "seats")
public class SeatModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "bus_id")
    private Long busId;

    @Size(max = 10)
    @Column(name = "seat_number", length = 10)
    private String seatNumber;

    @Size(max = 20)
    @Column(name = "seat_type", length = 20)
    private String seatType;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

}