package com.yourwifi.voucher.repository;

import com.yourwifi.common.enums.VoucherStatus;
import com.yourwifi.voucher.entity.Voucher;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;

public interface VoucherRepository extends JpaRepository<Voucher, UUID> {
    Optional<Voucher> findByCode(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Voucher v where v.code = :code")
    Optional<Voucher> findByCodeForUpdate(String code);

    boolean existsByCode(String code);

    long countByStatus(VoucherStatus status);
}
