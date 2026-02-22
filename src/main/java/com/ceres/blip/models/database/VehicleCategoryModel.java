package com.ceres.blip.models.database;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "vehicle_category")
public class VehicleCategoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @Column(name = "name", length = 50)
    private String name;

    @Size(max = 50)
    @Column(name = "category_code", length = 50)
    private String categoryCode;

    @Column(name = "description")
    private String description;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "amenities",columnDefinition = "text[]")
    private List<String> amenities;

    @ColumnDefault("true")
    @Column(name = "active")
    private Boolean active;


}