package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.PaymentAllocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentAllocationRepository extends JpaRepository<PaymentAllocation, UUID> {
    Page<PaymentAllocation> findByPaymentId(UUID paymentId, Pageable pageable);
    Page<PaymentAllocation> findByInvoiceId(UUID invoiceId, Pageable pageable);
    Page<PaymentAllocation> findByTenantId(UUID tenantId, Pageable pageable);
    Optional<PaymentAllocation> findByPaymentIdAndInvoiceId(UUID paymentId, UUID invoiceId);
}
