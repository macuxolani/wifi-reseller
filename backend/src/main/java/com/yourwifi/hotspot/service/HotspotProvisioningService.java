package com.yourwifi.hotspot.service;

import com.yourwifi.common.enums.HotspotStatus;
import com.yourwifi.hotspot.dto.HotspotConnectionGuide;
import com.yourwifi.hotspot.dto.HotspotProvisionRequest;
import com.yourwifi.hotspot.entity.Hotspot;
import com.yourwifi.hotspot.repository.HotspotRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HotspotProvisioningService {

    private final HotspotRepository hotspotRepository;
    private final String radiusServer;
    private final int radiusAuthenticationPort;
    private final int radiusAccountingPort;
    private final String captivePortalUrl;

    public HotspotProvisioningService(
        HotspotRepository hotspotRepository,
        @Value("${app.network.radius-server}") String radiusServer,
        @Value("${app.network.radius-authentication-port}") int radiusAuthenticationPort,
        @Value("${app.network.radius-accounting-port}") int radiusAccountingPort,
        @Value("${app.network.captive-portal-url}") String captivePortalUrl
    ) {
        this.hotspotRepository = hotspotRepository;
        this.radiusServer = radiusServer;
        this.radiusAuthenticationPort = radiusAuthenticationPort;
        this.radiusAccountingPort = radiusAccountingPort;
        this.captivePortalUrl = captivePortalUrl;
    }

    @Transactional
    public HotspotConnectionGuide provision(HotspotProvisionRequest request) {
        Hotspot hotspot = new Hotspot();
        UUID id = UUID.randomUUID();
        hotspot.setId(id);
        hotspot.setUuid(UUID.randomUUID());
        hotspot.setName(request.name());
        hotspot.setCode(request.code());
        hotspot.setLocationName(request.locationName());
        hotspot.setAddress(request.address());
        hotspot.setRouterIp(request.routerIp());
        hotspot.setRouterApiPort(request.routerApiPort());
        hotspot.setMaxUsers(request.maxUsers());
        hotspot.setBandwidthCapacity(request.bandwidthCapacity());
        hotspot.setCurrentUsers(0);
        hotspot.setRadiusServer(radiusServer);
        hotspot.setStatus(HotspotStatus.MAINTENANCE);
        hotspot.setCreatedAt(Instant.now());
        hotspotRepository.save(hotspot);
        return guide(hotspot);
    }

    @Transactional(readOnly = true)
    public HotspotConnectionGuide connectionGuide(UUID hotspotId) {
        return guide(hotspotRepository.findById(hotspotId)
            .orElseThrow(() -> new IllegalArgumentException("Hotspot not found.")));
    }

    private HotspotConnectionGuide guide(Hotspot hotspot) {
        return new HotspotConnectionGuide(
            hotspot.getId(),
            hotspot.getCode(),
            radiusServer,
            radiusAuthenticationPort,
            radiusAccountingPort,
            "RADIUS_SHARED_SECRET",
            captivePortalUrl,
            hotspot.getStatus().name()
        );
    }
}