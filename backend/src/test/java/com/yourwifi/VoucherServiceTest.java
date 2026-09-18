package com.yourwifi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yourwifi.common.enums.UserStatus;
import com.yourwifi.common.enums.VoucherStatus;
import com.yourwifi.billing.service.BillingService;
import com.yourwifi.customer.entity.Customer;
import com.yourwifi.customer.repository.CustomerRepository;
import com.yourwifi.voucher.dto.VoucherRedeemRequest;
import com.yourwifi.voucher.entity.Voucher;
import com.yourwifi.voucher.repository.VoucherRepository;
import com.yourwifi.voucher.service.VoucherService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VoucherServiceTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BillingService billingService;

    @InjectMocks
    private VoucherService voucherService;

    @Test
    void redeemVoucherMarksVoucherUsedAndReturnsMinutesAdded() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setUsername("janedoe");
        customer.setEmail("jane@example.com");
        customer.setPhoneNumber("+27720000000");
        customer.setPasswordHash("hashed");
        customer.setStatus(UserStatus.ACTIVE);
        customer.setAccountCreatedAt(Instant.now());

        Voucher voucher = new Voucher();
        voucher.setId(UUID.randomUUID());
        voucher.setCode("ABC123XYZ456");
        voucher.setDurationMinutes(60);
        voucher.setStatus(VoucherStatus.UNUSED);
        voucher.setGeneratedAt(Instant.now());
        voucher.setExpiresAt(Instant.now().plusSeconds(3600));

        when(customerRepository.findByUsername("janedoe")).thenReturn(Optional.of(customer));
        when(voucherRepository.findByCodeForUpdate("ABC123XYZ456")).thenReturn(Optional.of(voucher));
        when(voucherRepository.save(any(Voucher.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = voucherService.redeemVoucher(new VoucherRedeemRequest("ABC123XYZ456", "janedoe"));

        assertNotNull(response);
        assertEquals("USED", response.status());
        assertEquals(60, response.minutesAdded());
    }
}
