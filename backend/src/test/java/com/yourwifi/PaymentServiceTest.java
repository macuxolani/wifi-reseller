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
import com.yourwifi.packageapp.entity.Package;
import com.yourwifi.packageapp.repository.PackageRepository;
import com.yourwifi.common.enums.PackageStatus;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PackageRepository packageRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPaymentStartsAsPendingUntilProviderVerification() {
        UUID packageId = UUID.randomUUID();
        CreatePaymentRequest request = new CreatePaymentRequest(
            UUID.randomUUID(),
            packageId,
            new BigDecimal("15.00"),
            "ZAR",
            "SIMULATED"
        );

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Package packageEntity = new Package();
        packageEntity.setId(packageId);
        packageEntity.setPrice(new BigDecimal("15.00"));
        packageEntity.setCurrency("ZAR");
        packageEntity.setStatus(PackageStatus.ACTIVE);
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(packageEntity));

        PaymentResponse response = paymentService.createPayment(request);

        assertNotNull(response);
        assertEquals("PENDING", response.status());
        assertNotNull(response.reference());
    }
}
