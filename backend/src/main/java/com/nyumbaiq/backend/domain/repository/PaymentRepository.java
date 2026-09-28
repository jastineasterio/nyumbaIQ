package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.Payment;
import com.nyumbaiq.backend.domain.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Page<Payment> findByTenantId(UUID tenantId, Pageable pageable);
    Page<Payment> findByLeaseId(UUID leaseId, Pageable pageable);
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);
    Optional<Payment> findByPaymentReference(String paymentReference);
    Optional<Payment> findByProviderTransactionReference(String providerTransactionReference);
    boolean existsByPaymentReference(String paymentReference);
    boolean existsByProviderTransactionReference(String providerTransactionReference);
}
