package com.yourwifi.hotspot.controller;

import com.yourwifi.hotspot.dto.HotspotConnectionGuide;
import com.yourwifi.hotspot.dto.HotspotProvisionRequest;
import com.yourwifi.hotspot.entity.Hotspot;
import com.yourwifi.hotspot.repository.HotspotRepository;
import com.yourwifi.hotspot.service.HotspotProvisioningService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HotspotController {

    private final HotspotRepository hotspotRepository;
    private final HotspotProvisioningService hotspotProvisioningService;

    public HotspotController(HotspotRepository hotspotRepository, HotspotProvisioningService hotspotProvisioningService) {
        this.hotspotRepository = hotspotRepository;
        this.hotspotProvisioningService = hotspotProvisioningService;
    }

    @GetMapping("/hotspots/available")
    public ResponseEntity<List<Hotspot>> availableHotspots() {
        return ResponseEntity.ok(hotspotRepository.findAll());
    }

    @PostMapping("/hotspots")
    public ResponseEntity<HotspotConnectionGuide> provision(@Valid @RequestBody HotspotProvisionRequest request) {
        return ResponseEntity.ok(hotspotProvisioningService.provision(request));
    }

    @GetMapping("/hotspots/{hotspotId}/connection-guide")
    public ResponseEntity<HotspotConnectionGuide> connectionGuide(@PathVariable UUID hotspotId) {
        return ResponseEntity.ok(hotspotProvisioningService.connectionGuide(hotspotId));
    }
}
