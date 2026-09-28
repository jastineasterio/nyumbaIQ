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
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentAllocationRepository allocationRepository;
    private final ReceiptRepository receiptRepository;
    private final LeaseRepository leaseRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final CurrentUser currentUser;

    public PaymentService(PaymentRepository paymentRepository, InvoiceRepository invoiceRepository,
                          PaymentAllocationRepository allocationRepository, ReceiptRepository receiptRepository,
                          LeaseRepository leaseRepository, UserRepository userRepository,
                          PropertyRepository propertyRepository, TenantRepository tenantRepository,
                          CurrentUser currentUser) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.allocationRepository = allocationRepository;
        this.receiptRepository = receiptRepository;
        this.leaseRepository = leaseRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public Page<PaymentDto> getAllPayments(Pageable pageable) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getRole() == Role.TENANT) {
            return paymentRepository.findByTenantId(userId, pageable).map(this::toDto);
        }

        return paymentRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public PaymentDto getPayment(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment not found"));
        return toDto(payment);
    }

    public PaymentDto createPayment(CreatePaymentRequest request) {
        if (paymentRepository.existsByPaymentReference(request.providerTransactionReference() != null ?
                request.providerTransactionReference() : "MANUAL-" + System.currentTimeMillis())) {
            throw new ConflictException("Payment reference already exists");
        }

        String paymentReference = request.providerTransactionReference() != null ?
                request.providerTransactionReference() : "PAY-" + System.currentTimeMillis();

        Tenant tenant = tenantRepository.findById(request.tenantId()).orElseThrow(() -> new NotFoundException("Tenant not found"));
        Lease lease = request.leaseId() != null ? leaseRepository.findById(request.leaseId()).orElse(null) : null;
        Property property = request.propertyId() != null ? propertyRepository.findById(request.propertyId()).orElse(null) : null;

        Payment payment = Payment.builder()
                .paymentReference(paymentReference)
                .tenant(tenant)
                .lease(lease)
                .property(property)
                .amount(request.amount())
                .currency(request.currency())
                .paymentMethod(request.paymentMethod())
                .provider(request.provider())
                .providerTransactionReference(request.providerTransactionReference())
                .internalTransactionReference(request.internalTransactionReference())
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);
        return toDto(payment);
    }

    public PaymentDto allocatePayment(UUID paymentId, CreatePaymentAllocationRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BadRequestException("Cannot allocate payment that is not successful");
        }

        Invoice invoice = invoiceRepository.findById(request.invoiceId())
                .orElseThrow(() -> new NotFoundException("Invoice not found"));

        BigDecimal totalAllocated = allocationRepository.findByPaymentId(paymentId, Pageable.unpaged()).getContent().stream()
                .map(PaymentAllocation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal newTotal = totalAllocated.add(request.amount());
        if (newTotal.compareTo(payment.getAmount()) > 0) {
            throw new BadRequestException("Allocation exceeds payment amount");
        }

        BigDecimal invoiceBalance = invoice.getAmount().subtract(
                allocationRepository.findByInvoiceId(invoice.getId(), Pageable.unpaged()).getContent().stream()
                        .map(PaymentAllocation::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        if (request.amount().compareTo(invoiceBalance) > 0) {
            throw new BadRequestException("Allocation exceeds invoice balance");
        }

        if (allocationRepository.findByPaymentIdAndInvoiceId(paymentId, invoice.getId()).isPresent()) {
            throw new ConflictException("Payment already allocated to this invoice");
        }

        PaymentAllocation allocation = PaymentAllocation.builder()
                .payment(payment)
                .invoice(invoice)
                .tenant(invoice.getTenant())
                .amount(request.amount())
                .build();

        allocationRepository.save(allocation);

        generateReceipt(payment, invoice, allocation);

        return toDto(payment);
    }

    public PaymentDto verifyPayment(String providerTransactionReference) {
        Payment payment = paymentRepository.findByProviderTransactionReference(providerTransactionReference)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setConfirmedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        return toDto(payment);
    }

    public PaymentDto getPaymentByReference(String reference) {
        Payment payment = paymentRepository.findByPaymentReference(reference)
                .orElseThrow(() -> new NotFoundException("Payment not found"));
        return toDto(payment);
    }

    private void generateReceipt(Payment payment, Invoice invoice, PaymentAllocation allocation) {
        BigDecimal previousBalance = invoice.getAmount();
        BigDecimal remainingBalance = previousBalance.subtract(allocation.getAmount());

        String receiptNumber = "RCPT-" + System.currentTimeMillis();

        Receipt receipt = Receipt.builder()
                .receiptNumber(receiptNumber)
                .tenant(invoice.getTenant())
                .payment(payment)
                .property(invoice.getProperty())
                .unit(invoice.getUnit())
                .amount(allocation.getAmount())
                .previousBalance(previousBalance)
                .remainingBalance(remainingBalance)
                .paymentMethod(payment.getPaymentMethod().name())
                .provider(payment.getProvider())
                .transactionReference(payment.getProviderTransactionReference())
                .build();

        receiptRepository.save(receipt);
    }

    private PaymentDto toDto(Payment payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getPaymentReference(),
                new UserSummary(payment.getTenant().getUser().getId(), payment.getTenant().getUser().getFirstName(),
                        payment.getTenant().getUser().getLastName(), payment.getTenant().getUser().getEmail(),
                        payment.getTenant().getUser().getRole(), payment.getTenant().getUser().getStatus()),
                payment.getProperty() != null ? new PropertySummary(payment.getProperty().getId(), payment.getProperty().getName(), payment.getProperty().getPropertyCode()) : null,
                payment.getLease() != null ? payment.getLease().getLeaseNumber() : null,
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaymentMethod(),
                payment.getProvider(),
                payment.getProviderTransactionReference(),
                payment.getInternalTransactionReference(),
                payment.getStatus(),
                payment.getInitiatedAt(),
                payment.getConfirmedAt(),
                payment.getCreatedAt()
        );
    }
}
