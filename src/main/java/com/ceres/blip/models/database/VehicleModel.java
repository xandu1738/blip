package com.ceres.blip.models.database;

import com.ceres.blip.models.jpa_helpers.enums.VehicleTypes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;

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

//    @Enumerated(EnumType.STRING)
//    @Column(name = "category", nullable = false, length = 50)
//    private VehicleTypes category;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private VehicleTypes type;

    @ColumnDefault("'ACTIVE'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Basic
    @Column(name = "partner_code")
    private String partnerCode;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

}