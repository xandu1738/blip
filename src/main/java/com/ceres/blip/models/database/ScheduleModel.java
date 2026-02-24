package com.ceres.blip.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "schedule")
public class ScheduleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "route_id", nullable = false)
    private Long routeId;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "days_of_week", columnDefinition = "text[]")
    private String[] daysOfWeek; // e.g., "MON,TUE,WED,THU,FRI"

    @Column(name = "set_off_time", nullable = false)
    private LocalTime setOffTime;

    @Column(name = "expected_arrival_time", nullable = false)
    private LocalTime expectedArrivalTime;

    @Column(name = "partner_code", nullable = false)
    private String partnerCode;

    @ColumnDefault("'ACTIVE'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

}