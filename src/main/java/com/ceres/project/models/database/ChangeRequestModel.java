package com.ceres.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "change_request")
public class ChangeRequestModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "entity", nullable = false, length = 50)
    private String entity;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "action", nullable = false, length = 10)
    private String action;

    @ColumnDefault("'{}'")
    @Column(name = "old_value", nullable = false, length = Integer.MAX_VALUE)
    private String oldValue;

    @ColumnDefault("'{}'")
    @Column(name = "new_value", nullable = false, length = Integer.MAX_VALUE)
    private String newValue;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "\"timestamp\"", nullable = false)
    private Instant timestamp;

}