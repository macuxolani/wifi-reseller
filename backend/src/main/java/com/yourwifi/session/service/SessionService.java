package com.yourwifi.session.service;

import com.yourwifi.session.entity.WifiSession;
import com.yourwifi.session.repository.SessionRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public WifiSession startSession(UUID customerId, UUID hotspotId, UUID packageId, String macAddress, String ipAddress, int downloadSpeed, int uploadSpeed) {
        WifiSession session = new WifiSession();
        session.setId(UUID.randomUUID());
        session.setSessionUuid(UUID.randomUUID());
        session.setCustomerId(customerId);
        session.setHotspotId(hotspotId);
        session.setPackageId(packageId);
        session.setMacAddress(macAddress);
        session.setIpAddress(ipAddress);
        session.setDownloadSpeed(downloadSpeed);
        session.setUploadSpeed(uploadSpeed);
        session.setStartedAt(Instant.now());
        session.setCreatedAt(Instant.now());
        session.setStatus("ACTIVE");
        return sessionRepository.save(session);
    }
}
