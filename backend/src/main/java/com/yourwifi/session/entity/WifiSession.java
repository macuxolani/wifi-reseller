package com.yourwifi.session.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sessions")
@Getter
@Setter
public class WifiSession {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "session_uuid", nullable = false, unique = true, columnDefinition = "uuid")
    private UUID sessionUuid;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "hotspot_id")
    private UUID hotspotId;

    @Column(name = "radius_session_id")
    private String radiusSessionId;

    @Column(name = "mac_address")
    private String macAddress;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "package_id")
    private UUID packageId;

    @Column(name = "download_speed")
    private Integer downloadSpeed;

    @Column(name = "upload_speed")
    private Integer uploadSpeed;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "last_accounting_update_at")
    private Instant lastAccountingUpdateAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @Column(name = "bytes_uploaded")
    private Long bytesUploaded;

    @Column(name = "bytes_downloaded")
    private Long bytesDownloaded;

    @Column(name = "termination_reason")
    private String terminationReason;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
