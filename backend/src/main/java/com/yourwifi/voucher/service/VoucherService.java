package com.yourwifi.voucher.service;

import com.yourwifi.billing.service.BillingService;
import com.yourwifi.common.enums.EntitlementSourceType;
import com.yourwifi.common.enums.VoucherStatus;
import com.yourwifi.common.exception.ApiException;
import com.yourwifi.customer.entity.Customer;
import com.yourwifi.customer.repository.CustomerRepository;
import com.yourwifi.voucher.dto.VoucherRedeemRequest;
import com.yourwifi.voucher.dto.VoucherRedeemResponse;
import com.yourwifi.voucher.dto.CreateVoucherRequest;
import com.yourwifi.voucher.entity.Voucher;
import com.yourwifi.voucher.repository.VoucherRepository;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final CustomerRepository customerRepository;
    private final BillingService billingService;
    private final SecureRandom secureRandom = new SecureRandom();

    public VoucherService(VoucherRepository voucherRepository, CustomerRepository customerRepository, BillingService billingService) {
        this.voucherRepository = voucherRepository;
        this.customerRepository = customerRepository;
        this.billingService = billingService;
    }

    @Transactional
    public VoucherRedeemResponse redeemVoucher(VoucherRedeemRequest request) {
        Customer customer = customerRepository.findByUsername(request.username())
            .orElseThrow(() -> new ApiException("CUSTOMER_NOT_FOUND", "Customer not found.", HttpStatus.NOT_FOUND.value()));

        Voucher voucher = voucherRepository.findByCodeForUpdate(request.code())
            .orElseThrow(() -> new ApiException("INVALID_VOUCHER", "Voucher is invalid or has already been used.", HttpStatus.BAD_REQUEST.value()));

        if (voucher.getStatus() != VoucherStatus.UNUSED && voucher.getStatus() != VoucherStatus.ACTIVE) {
            throw new ApiException("INVALID_VOUCHER", "Voucher is invalid or has already been used.", HttpStatus.BAD_REQUEST.value());
        }

        if (voucher.getExpiresAt() != null && voucher.getExpiresAt().isBefore(Instant.now())) {
            voucher.setStatus(VoucherStatus.EXPIRED);
            voucherRepository.save(voucher);
            throw new ApiException("VOUCHER_EXPIRED", "Voucher has expired.", HttpStatus.BAD_REQUEST.value());
        }

        voucher.setStatus(VoucherStatus.USED);
        voucher.setActivatedAt(Instant.now());
        voucher.setCustomerId(customer.getId());

        int minutesToAdd = voucher.getDurationMinutes() == null ? 0 : voucher.getDurationMinutes();
        addEntitlement(customer.getId(), voucher.getPackageId(), minutesToAdd, EntitlementSourceType.VOUCHER, voucher.getCode());

        voucherRepository.save(voucher);
        return new VoucherRedeemResponse(voucher.getCode(), voucher.getStatus().name(), minutesToAdd, "Voucher redeemed successfully.");
    }

    public void addEntitlement(UUID customerId, UUID packageId, int minutes, EntitlementSourceType sourceType, String sourceReference) {
        billingService.createEntitlement(customerId, packageId, minutes, sourceType, sourceReference);
    }

    public String generateSecureCode() {
        StringBuilder code = new StringBuilder();
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        for (int i = 0; i < 12; i++) {
            code.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }
        return code.toString();
    }

    @Transactional
    public Voucher createVoucher(CreateVoucherRequest request, String createdBy) {
        Voucher voucher = new Voucher();
        voucher.setId(UUID.randomUUID());
        voucher.setCode(generateSecureCode());
        voucher.setPackageId(request.packageId());
        voucher.setDurationMinutes(request.durationMinutes());
        voucher.setPrice(request.price());
        voucher.setStatus(VoucherStatus.UNUSED);
        voucher.setGeneratedAt(Instant.now());
        voucher.setCreatedBy(createdBy);
        return voucherRepository.save(voucher);
    }
}
