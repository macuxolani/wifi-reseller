package com.yourwifi.network.service;

import com.yourwifi.network.entity.NetworkEvent;
import com.yourwifi.network.repository.NetworkEventRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NetworkEventService {

    private final NetworkEventRepository networkEventRepository;

    public NetworkEventService(NetworkEventRepository networkEventRepository) {
        this.networkEventRepository = networkEventRepository;
    }

    @Transactional
    public NetworkEvent recordEvent(UUID hotspotId, String eventType, String severity, String message) {
        NetworkEvent event = new NetworkEvent();
        event.setId(UUID.randomUUID());
        event.setHotspotId(hotspotId);
        event.setEventType(eventType);
        event.setSeverity(severity == null ? "INFO" : severity.toUpperCase());
        event.setMessage(message);
        event.setCreatedAt(Instant.now());
        return networkEventRepository.save(event);
    }

    public List<NetworkEvent> getRecentEvents() {
        return networkEventRepository.findTop10ByOrderByCreatedAtDesc();
    }
}
