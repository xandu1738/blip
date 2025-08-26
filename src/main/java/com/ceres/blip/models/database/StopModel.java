package com.ceres.blip.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "stop")
public class StopModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "route_id", nullable = false)
    private Long routeId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "crated_at", nullable = false)
    private Timestamp cratedAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

}