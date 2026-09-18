package com.yourwifi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yourwifi.network.entity.NetworkEvent;
import com.yourwifi.network.repository.NetworkEventRepository;
import com.yourwifi.network.service.NetworkEventService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NetworkMonitoringTest {

    @Mock
    private NetworkEventRepository networkEventRepository;

    @InjectMocks
    private NetworkEventService networkEventService;

    @Test
    void recordEventCreatesLogAndStoresSeverity() {
        when(networkEventRepository.save(any(NetworkEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NetworkEvent event = networkEventService.recordEvent(
            UUID.randomUUID(),
            "HOTSPOT_HEARTBEAT",
            "INFO",
            "Hotspot connected"
        );

        assertNotNull(event);
        assertEquals("HOTSPOT_HEARTBEAT", event.getEventType());
        assertEquals("INFO", event.getSeverity());
    }

    @Test
    void getRecentEventsReturnsLatestEvents() {
        when(networkEventRepository.findTop10ByOrderByCreatedAtDesc()).thenReturn(List.of(new NetworkEvent()));

        List<NetworkEvent> events = networkEventService.getRecentEvents();
        assertEquals(1, events.size());
    }
}
