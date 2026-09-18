package com.yourwifi.session.repository;

import com.yourwifi.session.entity.WifiSession;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<WifiSession, UUID> {
}
