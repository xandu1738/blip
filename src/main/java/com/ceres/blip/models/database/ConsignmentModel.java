package com.ceres.blip.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "consignment")
public class ConsignmentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "consignment_number", nullable = false, length = 50)
    private String consignmentNumber;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "origin", nullable = false, length = 100)
    private String origin;

    @Column(name = "destination", nullable = false, length = 100)
    private String destination;

    @Column(name = "total_weight", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalWeight;

    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;

    @ColumnDefault("'created'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    @Column(name = "last_updated_by")
    private Long lastUpdatedBy;

}