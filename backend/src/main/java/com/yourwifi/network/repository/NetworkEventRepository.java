package com.yourwifi.network.repository;

import com.yourwifi.network.entity.NetworkEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NetworkEventRepository extends JpaRepository<NetworkEvent, UUID> {
    List<NetworkEvent> findTop10ByOrderByCreatedAtDesc();
}
