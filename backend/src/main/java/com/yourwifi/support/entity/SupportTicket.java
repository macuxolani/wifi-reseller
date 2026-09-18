package com.yourwifi.support.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "support_tickets")
@Getter
@Setter
public class SupportTicket {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "priority")
    private String priority;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
