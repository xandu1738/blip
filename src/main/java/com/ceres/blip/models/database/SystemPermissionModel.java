package com.ceres.blip.models.database;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "system_permission", schema = "public")
public class SystemPermissionModel {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;
    @Basic
    @Column(name = "permission_code", nullable = false)
    private String permissionCode;
    @Basic
    @Column(name = "permission_name")
    private String permissionName;
    @Basic
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "domains", columnDefinition = "text[]")
    private List<String> domains;
}
