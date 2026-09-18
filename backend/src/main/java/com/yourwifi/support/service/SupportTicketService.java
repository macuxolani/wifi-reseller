package com.yourwifi.support.service;

import com.yourwifi.support.dto.CreateTicketRequest;
import com.yourwifi.support.dto.SupportTicketResponse;
import com.yourwifi.support.entity.SupportTicket;
import com.yourwifi.support.repository.SupportTicketRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;

    public SupportTicketService(SupportTicketRepository supportTicketRepository) {
        this.supportTicketRepository = supportTicketRepository;
    }

    @Transactional
    public SupportTicketResponse createTicket(CreateTicketRequest request) {
        SupportTicket ticket = new SupportTicket();
        ticket.setId(UUID.randomUUID());
        ticket.setCustomerId(request.customerId());
        ticket.setSubject(request.subject());
        ticket.setMessage(request.message());
        ticket.setPriority(request.priority() == null ? "MEDIUM" : request.priority().toUpperCase());
        ticket.setStatus("OPEN");
        ticket.setCreatedAt(Instant.now());

        ticket = supportTicketRepository.save(ticket);

        return new SupportTicketResponse(
            ticket.getId(),
            ticket.getCustomerId(),
            ticket.getSubject(),
            ticket.getMessage(),
            ticket.getStatus(),
            ticket.getPriority(),
            ticket.getCreatedAt()
        );
    }

    public List<SupportTicketResponse> listOpenTickets() {
        return supportTicketRepository.findByStatus("OPEN").stream()
            .map(ticket -> new SupportTicketResponse(
                ticket.getId(),
                ticket.getCustomerId(),
                ticket.getSubject(),
                ticket.getMessage(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCreatedAt()
            ))
            .toList();
    }
}
