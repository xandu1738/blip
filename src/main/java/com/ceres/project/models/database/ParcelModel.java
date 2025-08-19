package com.ceres.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "parcel")
public class ParcelModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_name", nullable = false, length = 100)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 15)
    private String receiverPhone;

    @Column(name = "pickup_location", nullable = false, length = Integer.MAX_VALUE)
    private String pickupLocation;

    @Column(name = "drop_off_location", nullable = false, length = Integer.MAX_VALUE)
    private String dropOffLocation;

    @Column(name = "weight", nullable = false, precision = 10, scale = 2)
    private BigDecimal weight;

    @Column(name = "dimensions", length = 50)
    private String dimensions;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    @ColumnDefault("'registered'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

}