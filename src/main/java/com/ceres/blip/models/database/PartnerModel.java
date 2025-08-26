package com.ceres.blip.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "partners")
public class PartnerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "partner_name")
    private String partnerName;

    @Column(name = "partner_code", unique = true, nullable = false)
    private String partnerCode;

    @Column(name = "account_number", length = 64)
    private String accountNumber;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "business_reference", length = Integer.MAX_VALUE)
    private String businessReference;

    @Column(name = "active")
    private Boolean active = false;

    @Column(name = "logo", length = Integer.MAX_VALUE)
    private String logo;

    @ColumnDefault("'FULL'")
    @Column(name = "package", length = Integer.MAX_VALUE)
    private String packageField;

    @Column(name = "date_created")
    private Timestamp dateCreated;

    @Column(name = "created_by")
    private String createdBy;

}