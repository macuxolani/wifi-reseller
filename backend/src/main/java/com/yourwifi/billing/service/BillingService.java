package com.yourwifi.billing.service;

import com.yourwifi.billing.entity.CustomerEntitlement;
import com.yourwifi.billing.repository.CustomerEntitlementRepository;
import com.yourwifi.common.enums.EntitlementSourceType;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillingService {

    private final CustomerEntitlementRepository entitlementRepository;

    public BillingService(CustomerEntitlementRepository entitlementRepository) {
        this.entitlementRepository = entitlementRepository;
    }

    @Transactional
    public CustomerEntitlement createEntitlement(UUID customerId, UUID packageId, int minutes, EntitlementSourceType sourceType, String sourceReference) {
        if (minutes <= 0) {
            throw new IllegalArgumentException("Entitlement duration must be positive.");
        }

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
        return entitlementRepository.save(entitlement);
    }

    @Transactional
    public void applySessionUsage(UUID customerId, int minutesUsed) {
        if (minutesUsed <= 0) {
            return;
        }

        int remainingUsage = minutesUsed;
        for (CustomerEntitlement entitlement : entitlementRepository
            .findByCustomerIdAndStatusOrderByActivatedAtAsc(customerId, "ACTIVE")) {
            int consumed = Math.min(remainingUsage, entitlement.getRemainingMinutes());
            entitlement.setRemainingMinutes(entitlement.getRemainingMinutes() - consumed);
            if (entitlement.getRemainingMinutes() == 0) {
                entitlement.setStatus("EXPIRED");
            }
            entitlementRepository.save(entitlement);
            remainingUsage -= consumed;
            if (remainingUsage == 0) {
                break;
            }
        }
    }

    public int getRemainingMinutes(UUID customerId) {
        return entitlementRepository.findByCustomerIdAndStatusOrderByActivatedAtAsc(customerId, "ACTIVE")
            .stream()
            .mapToInt(CustomerEntitlement::getRemainingMinutes)
            .sum();
    }
}
