package com.ceres.blip.models.database;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "license_activations")
public class LicenseActivationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ColumnDefault("nextval('license_activations_license_key_id_seq')")
    @JoinColumn(name = "license_key_id", nullable = false)
    private LicenseKeyModel licenseKey;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "activation_date")
    private Timestamp activationDate;

    @Size(max = 255)
    @Column(name = "hardware_id")
    private String hardwareId;

    @Size(max = 45)
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = Integer.MAX_VALUE)
    private String userAgent;

    @ColumnDefault("true")
    @Column(name = "is_valid")
    private Boolean isValid;

    @Column(name = "deactivation_date")
    private Timestamp deactivationDate;


}