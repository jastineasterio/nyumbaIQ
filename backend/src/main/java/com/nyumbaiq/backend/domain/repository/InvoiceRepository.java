package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.Invoice;
import com.nyumbaiq.backend.domain.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Page<Invoice> findByTenantId(UUID tenantId, Pageable pageable);
    Page<Invoice> findByPropertyId(UUID propertyId, Pageable pageable);
    Page<Invoice> findByStatus(InvoiceStatus status, Pageable pageable);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    Optional<Invoice> findByBillingScheduleId(UUID billingScheduleId);
    boolean existsByInvoiceNumber(String invoiceNumber);
    boolean existsByBillingScheduleId(UUID billingScheduleId);
}
