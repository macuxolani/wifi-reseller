package com.yourwifi.hotspot.entity;

import com.yourwifi.common.enums.HotspotStatus;
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
@Table(name = "hotspot")
@Getter
@Setter
public class Hotspot {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "uuid", nullable = false, unique = true, columnDefinition = "uuid")
    private UUID uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "address")
    private String address;

    @Column(name = "router_ip")
    private String routerIp;

    @Column(name = "router_api_port")
    private Integer routerApiPort;

    @Column(name = "radius_server")
    private String radiusServer;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private HotspotStatus status;

    @Column(name = "last_heartbeat")
    private Instant lastHeartbeat;

    @Column(name = "max_users")
    private Integer maxUsers;

    @Column(name = "current_users")
    private Integer currentUsers;

    @Column(name = "bandwidth_capacity")
    private Long bandwidthCapacity;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
