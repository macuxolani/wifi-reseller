package com.yourwifi.hotspot.dto;

import java.util.UUID;

public record HotspotConnectionGuide(
    UUID hotspotId,
    String hotspotCode,
    String radiusServer,
    int radiusAuthenticationPort,
    int radiusAccountingPort,
    String sharedSecretEnvironmentVariable,
    String captivePortalUrl,
    String status
) {}