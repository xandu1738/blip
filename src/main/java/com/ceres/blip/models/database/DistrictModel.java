package com.ceres.blip.models.database;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "districts")
public class DistrictModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 30)
    @Column(name = "name", length = 30)
    private String name;

    @Size(max = 30)
    @Column(name = "district_code", length = 30)
    private String districtCode;

    @Size(max = 30)
    @Column(name = "district_id", length = 30)
    private String districtId;

}