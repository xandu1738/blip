package com.ceres.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "vehicle")
public class VehicleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "registration_number", nullable = false, length = 20)
    private String registrationNumber;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @ColumnDefault("'ACTIVE'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

}