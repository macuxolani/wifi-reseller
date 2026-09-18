package com.yourwifi.billing.service;

import com.yourwifi.billing.entity.CustomerEntitlement;
import com.yourwifi.common.enums.EntitlementSourceType;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BillingService {

    private final Map<UUID, Integer> centralBalanceMinutes = new ConcurrentHashMap<>();
    private final Map<UUID, CustomerEntitlement> activeEntitlements = new ConcurrentHashMap<>();

    public CustomerEntitlement createEntitlement(UUID customerId, UUID packageId, int minutes, EntitlementSourceType sourceType, String sourceReference) {
        int current = centralBalanceMinutes.getOrDefault(customerId, 0);
        int updated = current + minutes;
        centralBalanceMinutes.put(customerId, updated);

        CustomerEntitlement entitlement = new CustomerEntitlement();
        entitlement.setId(UUID.randomUUID());
        entitlement.setCustomerId(customerId);
        entitlement.setPackageId(packageId);
        entitlement.setOriginalDurationMinutes(minutes);
        entitlement.setRemainingMinutes(minutes);
        entitlement.setSourceType(sourceType);
        entitlement.setSourceReference(sourceReference);
        entitlement.setActivatedAt(Instant.now());
        entitlement.setStatus("ACTIVE");
        activeEntitlements.put(customerId, entitlement);
        return entitlement;
    }

    public void applySessionUsage(UUID customerId, int minutesUsed) {
        int current = centralBalanceMinutes.getOrDefault(customerId, 0);
        int updated = Math.max(0, current - minutesUsed);
        centralBalanceMinutes.put(customerId, updated);

        CustomerEntitlement entitlement = activeEntitlements.get(customerId);
        if (entitlement != null) {
            int newRemaining = Math.max(0, entitlement.getRemainingMinutes() - minutesUsed);
            entitlement.setRemainingMinutes(newRemaining);
            if (newRemaining == 0) {
                entitlement.setStatus("EXPIRED");
            }
        }
    }

    public int getRemainingMinutes(UUID customerId) {
        return centralBalanceMinutes.getOrDefault(customerId, 0);
    }
}
