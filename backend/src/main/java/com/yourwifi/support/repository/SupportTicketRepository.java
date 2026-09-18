package com.yourwifi.support.repository;

import com.yourwifi.support.entity.SupportTicket;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, UUID> {
    List<SupportTicket> findByStatus(String status);
}
