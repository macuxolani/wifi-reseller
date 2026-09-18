package com.yourwifi.billing.entity;

import com.yourwifi.common.enums.EntitlementSourceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customer_entitlements")
@Getter
@Setter
public class CustomerEntitlement {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "package_id")
    private UUID packageId;

    @Column(name = "original_duration_minutes", nullable = false)
    private Integer originalDurationMinutes;

    @Column(name = "remaining_duration_minutes", nullable = false)
    private Integer remainingMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private EntitlementSourceType sourceType;

    @Column(name = "source_reference")
    private String sourceReference;

    @Column(name = "activated_at", nullable = false)
    private Instant activatedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "status", nullable = false)
    private String status;
}
