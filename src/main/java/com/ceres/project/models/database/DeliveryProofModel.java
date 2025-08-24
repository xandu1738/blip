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
@Table(name = "delivery_proof")
public class DeliveryProofModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "parcel_id", nullable = false)
    private Long parcelId;

    @Column(name = "delivery_staff_id", nullable = false)
    private Long deliveryStaffId;

    @Column(name = "signature")
    private byte[] signature;

    @Column(name = "photo")
    private byte[] photo;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "delivered_at", nullable = false)
    private Timestamp deliveredAt;

    @Column(name = "delivered_by", nullable = false)
    private Long deliveredBy;

    @Column(name = "remarks", length = Integer.MAX_VALUE)
    private String remarks;

}