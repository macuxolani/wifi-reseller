package com.yourwifi.voucher.entity;

import com.yourwifi.common.enums.VoucherStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
public class Voucher {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "package_id")
    private UUID packageId;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "speed_download_mbps")
    private Integer speedDownloadMbps;

    @Column(name = "speed_upload_mbps")
    private Integer speedUploadMbps;

    @Column(name = "price", precision = 19, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VoucherStatus status;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    @Column(name = "activated_at")
    private Instant activatedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "batch_id")
    private String batchId;
}
