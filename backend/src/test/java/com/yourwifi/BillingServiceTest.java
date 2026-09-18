package com.yourwifi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.yourwifi.billing.entity.CustomerEntitlement;
import com.yourwifi.billing.repository.CustomerEntitlementRepository;
import com.yourwifi.billing.service.BillingService;
import com.yourwifi.common.enums.EntitlementSourceType;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

class BillingServiceTest {

    @Test
    void createsEntitlementAndTracksRemainingBalance() {
        CustomerEntitlementRepository repository = Mockito.mock(CustomerEntitlementRepository.class);
        List<CustomerEntitlement> storedEntitlements = new ArrayList<>();
        when(repository.save(any(CustomerEntitlement.class))).thenAnswer(invocation -> {
            CustomerEntitlement saved = invocation.getArgument(0);
            if (!storedEntitlements.contains(saved)) {
                storedEntitlements.add(saved);
            }
            return saved;
        });
        when(repository.findByCustomerIdAndStatusOrderByActivatedAtAsc(any(), any()))
            .thenAnswer(invocation -> storedEntitlements);
        BillingService billingService = new BillingService(repository);
        UUID customerId = UUID.randomUUID();

        CustomerEntitlement entitlement = billingService.createEntitlement(
            customerId,
            UUID.randomUUID(),
            60,
            EntitlementSourceType.VOUCHER,
            "VOUCHER-1"
        );

        billingService.applySessionUsage(customerId, 20);

        assertEquals(40, billingService.getRemainingMinutes(customerId));
        assertEquals(40, entitlement.getRemainingMinutes());
    }
}
