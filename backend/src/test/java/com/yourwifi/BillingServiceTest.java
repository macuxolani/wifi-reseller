package com.yourwifi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.yourwifi.billing.entity.CustomerEntitlement;
import com.yourwifi.billing.service.BillingService;
import com.yourwifi.common.enums.EntitlementSourceType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BillingServiceTest {

    @Test
    void createsEntitlementAndTracksRemainingBalance() {
        BillingService billingService = new BillingService();
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
