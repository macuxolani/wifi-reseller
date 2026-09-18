package com.yourwifi.billing.repository;

import com.yourwifi.billing.entity.CustomerEntitlement;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerEntitlementRepository extends JpaRepository<CustomerEntitlement, UUID> {
    List<CustomerEntitlement> findByCustomerIdAndStatusOrderByActivatedAtAsc(UUID customerId, String status);
}
