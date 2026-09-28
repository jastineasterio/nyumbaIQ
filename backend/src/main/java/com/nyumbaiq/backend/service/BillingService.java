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
public class BillingService {
    private final BillingScheduleRepository billingScheduleRepository;
    private final LeaseRepository leaseRepository;
    private final InvoiceRepository invoiceRepository;
    private final CurrentUser currentUser;

    public BillingService(BillingScheduleRepository billingScheduleRepository, LeaseRepository leaseRepository,
                          InvoiceRepository invoiceRepository, CurrentUser currentUser) {
        this.billingScheduleRepository = billingScheduleRepository;
        this.leaseRepository = leaseRepository;
        this.invoiceRepository = invoiceRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public Page<BillingScheduleDto> getAllSchedules(Pageable pageable) {
        return billingScheduleRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public BillingScheduleDto getSchedule(UUID id) {
        BillingSchedule schedule = billingScheduleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Billing schedule not found"));
        return toDto(schedule);
    }

    public void generateBilling() {
        for (Lease lease : leaseRepository.findByStatus(LeaseStatus.ACTIVE, Pageable.unpaged()).getContent()) {
            generateScheduleForLease(lease);
        }
    }

    public void generateScheduleForLease(Lease lease) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime periodStart = getPeriodStart(lease, now);
        LocalDateTime periodEnd = getPeriodEnd(lease, periodStart);

        if (billingScheduleRepository.findByLeaseIdAndPeriodStartAndPeriodEnd(lease.getId(), periodStart, periodEnd).isPresent()) {
            return;
        }

        BillingSchedule schedule = BillingSchedule.builder()
                .lease(lease)
                .tenant(lease.getTenant())
                .unit(lease.getUnit())
                .property(lease.getProperty())
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .chargeDate(now)
                .dueDate(periodEnd)
                .amount(lease.getRentAmount())
                .status(com.nyumbaiq.backend.domain.enums.InvoiceStatus.UNPAID)
                .build();

        billingScheduleRepository.save(schedule);
    }

    private LocalDateTime getPeriodStart(Lease lease, LocalDateTime now) {
        return switch (lease.getBillingFrequency()) {
            case WEEKLY -> now.minusWeeks(1);
            case MONTHLY -> now.minusMonths(1);
            case QUARTERLY -> now.minusMonths(3);
            case SEMI_ANNUALLY -> now.minusMonths(6);
            default -> now.minusMonths(1);
        };
    }

    private LocalDateTime getPeriodEnd(Lease lease, LocalDateTime periodStart) {
        return switch (lease.getBillingFrequency()) {
            case WEEKLY -> periodStart.plusWeeks(1);
            case MONTHLY -> periodStart.plusMonths(1);
            case QUARTERLY -> periodStart.plusMonths(3);
            case SEMI_ANNUALLY -> periodStart.plusMonths(6);
            default -> periodStart.plusMonths(1);
        };
    }

    private BillingScheduleDto toDto(BillingSchedule schedule) {
        return new BillingScheduleDto(
                schedule.getId(),
                schedule.getLease().getLeaseNumber(),
                schedule.getTenant().getUser().getFirstName() + " " + schedule.getTenant().getUser().getLastName(),
                schedule.getUnit().getUnitNumber(),
                schedule.getPeriodStart(),
                schedule.getPeriodEnd(),
                schedule.getChargeDate(),
                schedule.getDueDate(),
                schedule.getAmount(),
                schedule.getStatus(),
                schedule.getCreatedAt()
        );
    }
}
