package com.yourwifi.network.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "network_events")
@Getter
@Setter
public class NetworkEvent {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "hotspot_id")
    private UUID hotspotId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "severity")
    private String severity;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
