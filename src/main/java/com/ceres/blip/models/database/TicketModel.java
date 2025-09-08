package com.ceres.blip.models.database;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "tickets")
public class TicketModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 30)
    @Column(name = "ticket_number", length = 30)
    private String ticketNumber;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "fare_amount", precision = 10, scale = 2)
    private BigDecimal fareAmount;

    @Size(max = 3)
    @Column(name = "currency", length = 3)
    private String currency;

    @Size(max = 30)
    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "check_in_time")
    private Timestamp checkInTime;

    @Column(name = "checked_in_by")
    private Long checkedInBy;

    @Size(max = 30)
    @Column(name = "qr_code", length = 30)
    private String qrCode;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Size(max = 30)
    @Column(name = "validation_status", length = 30)
    private String validationStatus;

}