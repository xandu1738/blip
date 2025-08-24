package com.ceres.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "access_logs")
public class AccessLogModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "method", nullable = false, length = 10)
    private String method;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "address")
    private String address;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "request", length = Integer.MAX_VALUE)
    private String request;

    @Column(name = "response", nullable = false, length = Integer.MAX_VALUE)
    private String response;

    @Column(name = "query_params", length = Integer.MAX_VALUE)
    private String queryParams;

    @Column(name = "user_agent", length = Integer.MAX_VALUE)
    private String userAgent;

    @ColumnDefault("false")
    @Column(name = "is_error", nullable = false)
    private Boolean isError = false;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "partner_code")
    private String partnerCode;

    @ColumnDefault("'{}'")
    @Column(name = "client_info", length = Integer.MAX_VALUE)
    private String clientInfo;

}