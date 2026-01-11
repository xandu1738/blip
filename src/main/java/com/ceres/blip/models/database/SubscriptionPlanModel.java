package com.ceres.blip.models.database;

import com.ceres.blip.dtos.ModulePrice;
import com.ceres.blip.models.enums.SubscriptionColors;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlanModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "icon", length = Integer.MAX_VALUE)
    private String icon;

    public String getColor() {
        return color.getColor();
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "color")
    private SubscriptionColors color;

    @Column(name = "popular")
    private Boolean popular;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "price",columnDefinition = "jsonb")
    private ModulePrice price;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "features",columnDefinition = "text[]")
    private List<String> features;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "created_by", length = Integer.MAX_VALUE)
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_on")
    private Timestamp createdOn;

}