package com.yourwifi.hotspot.dto;

import jakarta.validation.constraints.NotBlank;

public record HotspotProvisionRequest(
    @NotBlank String name,
    @NotBlank String code,
    String locationName,
    String address,
    String routerIp,
    Integer routerApiPort,
    Integer maxUsers,
    Long bandwidthCapacity
) {}