package com.ceres.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "consignment_parcel")
public class ConsignmentParcelModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "consignment_id", nullable = false)
    private Long consignmentId;

    @Column(name = "parcel_id", nullable = false)
    private Long parcelId;

    @Column(name = "remarks", length = Integer.MAX_VALUE)
    private String remarks;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    @Column(name = "added_by", nullable = false)
    private Long addedBy;

}