package com.yourwifi.hotspot.repository;

import com.yourwifi.hotspot.entity.Hotspot;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotspotRepository extends JpaRepository<Hotspot, UUID> {
}
