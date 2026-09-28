package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.Receipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {
    Page<Receipt> findByTenantId(UUID tenantId, Pageable pageable);
    Page<Receipt> findByPaymentId(UUID paymentId, Pageable pageable);
    Optional<Receipt> findByReceiptNumber(String receiptNumber);
    Optional<Receipt> findByPaymentId(UUID paymentId);
    boolean existsByReceiptNumber(String receiptNumber);
}
