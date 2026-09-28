package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.*;
import com.nyumbaiq.backend.domain.enums.*;
import com.nyumbaiq.backend.domain.repository.*;
import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.exception.*;
import com.nyumbaiq.backend.security.CurrentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {
    @Mock
    private BillingScheduleRepository billingScheduleRepository;
    @Mock
    private LeaseRepository leaseRepository;
    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private BillingService billingService;

    @Test
    void getSchedule_ShouldReturnSchedule() {
        UUID scheduleId = UUID.randomUUID();
        Lease lease = Lease.builder()
                .id(UUID.randomUUID())
                .leaseNumber("LEASE-123")
                .build();
        BillingSchedule schedule = BillingSchedule.builder()
                .id(scheduleId)
                .lease(lease)
                .tenant(Tenant.builder().id(UUID.randomUUID()).user(User.builder().id(UUID.randomUUID()).firstName("Test").lastName("User").build()).build())
                .unit(Unit.builder().id(UUID.randomUUID()).unitNumber("101").build())
                .amount(new BigDecimal("1000"))
                .status(InvoiceStatus.UNPAID)
                .build();

        when(billingScheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        var result = billingService.getSchedule(scheduleId);
        assertNotNull(result);
    }

    @Test
    void getSchedule_ShouldThrow_WhenNotFound() {
        when(billingScheduleRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> billingService.getSchedule(UUID.randomUUID()));
    }
}
