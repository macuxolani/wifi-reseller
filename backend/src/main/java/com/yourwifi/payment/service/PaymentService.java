package com.yourwifi.payment.service;

import com.yourwifi.payment.dto.CreatePaymentRequest;
import com.yourwifi.payment.dto.PaymentResponse;
import com.yourwifi.payment.entity.Payment;
import com.yourwifi.payment.repository.PaymentRepository;
import com.yourwifi.packageapp.entity.Package;
import com.yourwifi.packageapp.repository.PackageRepository;
import com.yourwifi.common.enums.PackageStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PackageRepository packageRepository;

    public PaymentService(PaymentRepository paymentRepository, PackageRepository packageRepository) {
        this.paymentRepository = paymentRepository;
        this.packageRepository = packageRepository;
    }

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        if (request.customerId() == null || request.packageId() == null || request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valid customer, package, and amount are required for payment.");
        }

        Package packageEntity = packageRepository.findById(request.packageId())
            .filter(packageOption -> packageOption.getStatus() == PackageStatus.ACTIVE)
            .orElseThrow(() -> new IllegalArgumentException("Selected package is not available."));
        if (request.amount().compareTo(packageEntity.getPrice()) != 0
            || (request.currency() != null && !request.currency().isBlank() && !request.currency().equalsIgnoreCase(packageEntity.getCurrency()))) {
            throw new IllegalArgumentException("Payment amount or currency does not match the selected package.");
        }

        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setReference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setCustomerId(request.customerId());
        payment.setPackageId(request.packageId());
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency() == null || request.currency().isBlank() ? "ZAR" : request.currency());
        payment.setProvider(request.provider() == null || request.provider().isBlank() ? "SIMULATED" : request.provider());
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
