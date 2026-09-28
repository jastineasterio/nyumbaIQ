package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.*;
import com.nyumbaiq.backend.domain.enums.*;
import com.nyumbaiq.backend.domain.repository.*;
import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.exception.*;
import com.nyumbaiq.backend.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final BillingScheduleRepository billingScheduleRepository;
    private final PaymentAllocationRepository allocationRepository;
    private final LeaseRepository leaseRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public InvoiceService(InvoiceRepository invoiceRepository, BillingScheduleRepository billingScheduleRepository,
                          PaymentAllocationRepository allocationRepository, LeaseRepository leaseRepository,
                          UserRepository userRepository, CurrentUser currentUser) {
        this.invoiceRepository = invoiceRepository;
        this.billingScheduleRepository = billingScheduleRepository;
        this.allocationRepository = allocationRepository;
        this.leaseRepository = leaseRepository;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public Page<InvoiceDto> getAllInvoices(Pageable pageable) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getRole() == Role.TENANT) {
            return invoiceRepository.findByTenantId(userId, pageable).map(this::toDto);
        }

        return invoiceRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public InvoiceDto getInvoice(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Invoice not found"));
        return toDto(invoice);
    }

    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new NotFoundException("Invoice not found"));
        return toDto(invoice);
    }

    public InvoiceDto cancelInvoice(UUID id) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getRole() != Role.OWNER && user.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Only owner or manager can cancel invoices");
        }

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BadRequestException("Invoice is already cancelled");
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoiceRepository.save(invoice);
        return toDto(invoice);
    }

    private BigDecimal getTotalAllocated(Invoice invoice) {
        return allocationRepository.findByInvoiceId(invoice.getId(), Pageable.unpaged()).getContent().stream()
                .map(PaymentAllocation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private InvoiceDto toDto(Invoice invoice) {
        BigDecimal totalAllocated = getTotalAllocated(invoice);
        BigDecimal balance = invoice.getAmount().subtract(totalAllocated);

        return new InvoiceDto(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                new UserSummary(invoice.getTenant().getUser().getId(), invoice.getTenant().getUser().getFirstName(),
                        invoice.getTenant().getUser().getLastName(), invoice.getTenant().getUser().getEmail(),
                        invoice.getTenant().getUser().getRole(), invoice.getTenant().getUser().getStatus()),
                new PropertySummary(invoice.getProperty().getId(), invoice.getProperty().getName(), invoice.getProperty().getPropertyCode()),
                new UnitSummary(invoice.getUnit().getId(), invoice.getUnit().getUnitNumber()),
                invoice.getLease().getLeaseNumber(),
                invoice.getPeriodStart(),
                invoice.getPeriodEnd(),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                invoice.getAmount(),
                totalAllocated,
                balance,
                invoice.getStatus(),
                invoice.getNotes(),
                invoice.getCreatedAt()
        );
    }
}
