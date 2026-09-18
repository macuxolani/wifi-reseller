package com.yourwifi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yourwifi.payment.dto.CreatePaymentRequest;
import com.yourwifi.payment.dto.PaymentResponse;
import com.yourwifi.payment.entity.Payment;
import com.yourwifi.payment.repository.PaymentRepository;
import com.yourwifi.payment.service.PaymentService;
import com.yourwifi.reporting.dto.ReportSummaryDto;
import com.yourwifi.reporting.service.ReportingService;
import com.yourwifi.support.dto.CreateTicketRequest;
import com.yourwifi.support.entity.SupportTicket;
import com.yourwifi.support.repository.SupportTicketRepository;
import com.yourwifi.support.service.SupportTicketService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OperationalServicesTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SupportTicketRepository supportTicketRepository;

    @InjectMocks
    private PaymentService paymentService;

    @InjectMocks
    private SupportTicketService supportTicketService;

    @InjectMocks
    private ReportingService reportingService;

    @Test
    void paymentServiceCreatesPaymentRecord() {
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.createPayment(new CreatePaymentRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            new BigDecimal("120.00"),
            "ZAR",
            "MPESA"
        ));

        assertNotNull(response);
        assertNotNull(response.reference());
        assertEquals("PENDING", response.status());
    }

    @Test
    void supportTicketServiceCreatesAndListsTickets() {
        when(supportTicketRepository.save(any(SupportTicket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var created = supportTicketService.createTicket(new CreateTicketRequest(
            UUID.randomUUID(),
            "Connectivity issue",
            "Customer cannot connect after login.",
            "HIGH"
        ));

        assertNotNull(created);
        assertEquals("OPEN", created.status());

        var tickets = supportTicketService.listOpenTickets();
        assertNotNull(tickets);
        assertEquals(0, tickets.size());
    }

    @Test
    void reportingServiceReturnsExecutiveSummary() {
        ReportSummaryDto summary = reportingService.getExecutiveSummary();

        assertNotNull(summary);
        assertNotNull(summary.periodLabel());
        assertEquals(0, summary.totalActiveSessions());
    }
}
