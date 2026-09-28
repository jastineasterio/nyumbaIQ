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
class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private PaymentAllocationRepository allocationRepository;
    @Mock
    private ReceiptRepository receiptRepository;
    @Mock
    private LeaseRepository leaseRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPayment_ShouldCreate() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .firstName("Tenant")
                .lastName("Test")
                .email("tenant@test.com")
                .role(Role.TENANT)
                .status(UserStatus.ACTIVE)
                .build();

        Tenant tenant = Tenant.builder()
                .id(UUID.randomUUID())
                .user(user)
                .fullName("Tenant Test")
                .status(TenantStatus.ACTIVE)
                .build();

        when(paymentRepository.existsByPaymentReference(any())).thenReturn(false);
        when(tenantRepository.findById(tenant.getId())).thenReturn(Optional.of(tenant));
        when(paymentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var request = new CreatePaymentRequest(tenant.getId(), null, null, new BigDecimal("1000"), "TZS", PaymentMethod.MOBILE_MONEY, "M-Pesa", "TXN-123", "INT-123");

        var result = paymentService.createPayment(request);
        assertNotNull(result);
    }

    @Test
    void getPayment_ShouldReturnPayment() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .id(paymentId)
                .paymentReference("PAY-123")
                .amount(new BigDecimal("1000"))
                .status(PaymentStatus.SUCCESS)
                .build();

        Tenant tenant = Tenant.builder()
                .id(UUID.randomUUID())
                .user(User.builder().id(UUID.randomUUID()).firstName("Tenant").lastName("Test").email("tenant@test.com").role(Role.TENANT).build())
                .fullName("Tenant Test")
                .status(TenantStatus.ACTIVE)
                .build();

        payment.setTenant(tenant);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        var result = paymentService.getPayment(paymentId);
        assertNotNull(result);
    }

    @Test
    void getPayment_ShouldThrow_WhenNotFound() {
        when(paymentRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> paymentService.getPayment(UUID.randomUUID()));
    }
}
