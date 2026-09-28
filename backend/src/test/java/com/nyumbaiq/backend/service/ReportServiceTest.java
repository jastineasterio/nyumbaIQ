package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.*;
import com.nyumbaiq.backend.domain.enums.*;
import com.nyumbaiq.backend.domain.repository.*;
import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.exception.NotFoundException;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private LeaseRepository leaseRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private ReportService reportService;

    @Test
    void getIncomeSummary_ShouldReturnSummary() {
        when(paymentRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());
        when(invoiceRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());
        IncomeSummaryDto result = reportService.getIncomeSummary(LocalDate.now().minusDays(30), LocalDate.now());
        assertNotNull(result);
    }

    @Test
    void getExpenseSummary_ShouldReturnSummary() {
        when(expenseRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());
        ExpenseSummaryDto result = reportService.getExpenseSummary(LocalDate.now().minusDays(30), LocalDate.now());
        assertNotNull(result);
    }

    @Test
    void getOutstandingRent_ShouldReturnOutstanding() {
        when(invoiceRepository.findByStatus(eq(InvoiceStatus.OVERDUE), any(Pageable.class))).thenReturn(Page.empty());
        OutstandingRentDto result = reportService.getOutstandingRent();
        assertNotNull(result);
    }
}
