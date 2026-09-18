package com.yourwifi.payment.service;

import com.yourwifi.payment.dto.CreatePaymentRequest;
import com.yourwifi.payment.dto.PaymentResponse;
import com.yourwifi.payment.entity.Payment;
import com.yourwifi.payment.repository.PaymentRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setReference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setCustomerId(request.customerId());
        payment.setPackageId(request.packageId());
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency());
        payment.setProvider(request.provider());
        payment.setStatus("PENDING");
        payment.setInitiatedAt(Instant.now());

        payment = paymentRepository.save(payment);

        return new PaymentResponse(
            payment.getId(),
            payment.getReference(),
            payment.getCustomerId(),
            payment.getPackageId(),
            payment.getAmount(),
            payment.getCurrency(),
            payment.getProvider(),
            payment.getStatus(),
            payment.getInitiatedAt()
        );
    }
}
