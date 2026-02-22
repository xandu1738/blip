package com.ceres.blip.models.database;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "amenities")
public class AmenityModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "description")
    private String description;

    @Size(max = 50)
    @Column(name = "code", length = 50)
    private String code;

}