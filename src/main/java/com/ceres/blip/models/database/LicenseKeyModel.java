package com.ceres.blip.models.database;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "license_keys")
public class LicenseKeyModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "license_key", nullable = false, length = 100)
    private String licenseKey;

    @Size(max = 50)
    @NotNull
    @Column(name = "product_id", nullable = false, length = 50)
    private String productId;

    @Size(max = 20)
    @Column(name = "partner_code", length = 20)
    private String partnerCode;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @ColumnDefault("false")
    @Column(name = "is_used")
    private Boolean isUsed;

    @Size(max = 255)
    @Column(name = "hardware_id")
    private String hardwareId;

    @Size(max = 45)
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "activation_date")
    private Timestamp activationDate;

    @Column(name = "expiry_date")
    private Timestamp expiryDate;

    @ColumnDefault("1")
    @Column(name = "max_activations")
    private Integer maxActivations;

    @ColumnDefault("0")
    @Column(name = "current_activations")
    private Integer currentActivations;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Timestamp createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "licenseKey")
    private Set<LicenseActivationModel> licenseActivations = new LinkedHashSet<>();


}