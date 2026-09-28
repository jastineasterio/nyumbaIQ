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
public class StatementService {
    private final StatementRepository statementRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentAllocationRepository allocationRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PropertyRepository propertyRepository;
    private final CurrentUser currentUser;

    public StatementService(StatementRepository statementRepository, InvoiceRepository invoiceRepository,
                            PaymentRepository paymentRepository, PaymentAllocationRepository allocationRepository,
                            UserRepository userRepository, TenantRepository tenantRepository,
                            PropertyRepository propertyRepository, CurrentUser currentUser) {
        this.statementRepository = statementRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.allocationRepository = allocationRepository;
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.propertyRepository = propertyRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public Page<StatementDto> getAllStatements(Pageable pageable) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getRole() == Role.TENANT) {
            return statementRepository.findByTenantId(userId, pageable).map(this::toDto);
        }

        return statementRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public StatementDto getStatement(UUID id) {
        Statement statement = statementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Statement not found"));
        return toDto(statement);
    }

    @Transactional(readOnly = true)
    public StatementDto getStatementByNumber(String statementNumber) {
        Statement statement = statementRepository.findByStatementNumber(statementNumber)
                .orElseThrow(() -> new NotFoundException("Statement not found"));
        return toDto(statement);
    }

    public StatementDto generateStatement(CreateStatementRequest request) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        Tenant tenant = tenantRepository.findById(request.tenantId()).orElseThrow(() -> new NotFoundException("Tenant not found"));
        Property property = propertyRepository.findById(request.propertyId()).orElseThrow(() -> new NotFoundException("Property not found"));

        BigDecimal openingBalance = BigDecimal.ZERO;
        BigDecimal totalCharges = invoiceRepository.findByTenantId(request.tenantId(), Pageable.unpaged()).getContent().stream()
                .filter(inv -> !inv.getPeriodStart().isBefore(request.periodStart()) && !inv.getPeriodEnd().isAfter(request.periodEnd()))
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPayments = paymentRepository.findByTenantId(request.tenantId(), Pageable.unpaged()).getContent().stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .filter(p -> !p.getInitiatedAt().isBefore(request.periodStart()) && !p.getInitiatedAt().isAfter(request.periodEnd()))
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal closingBalance = openingBalance.add(totalCharges).subtract(totalPayments);

        String statementNumber = "STMT-" + System.currentTimeMillis();

        Statement statement = Statement.builder()
                .tenant(tenant)
                .property(property)
                .statementNumber(statementNumber)
                .periodStart(request.periodStart())
                .periodEnd(request.periodEnd())
                .openingBalance(openingBalance)
                .closingBalance(closingBalance)
                .build();

        statementRepository.save(statement);
        return toDto(statement);
    }

    private StatementDto toDto(Statement statement) {
        return new StatementDto(
                statement.getId(),
                statement.getStatementNumber(),
                new UserSummary(statement.getTenant().getUser().getId(), statement.getTenant().getUser().getFirstName(),
                        statement.getTenant().getUser().getLastName(), statement.getTenant().getUser().getEmail(),
                        statement.getTenant().getUser().getRole(), statement.getTenant().getUser().getStatus()),
                new PropertySummary(statement.getProperty().getId(), statement.getProperty().getName(), statement.getProperty().getPropertyCode()),
                statement.getUnit() != null ? new UnitSummary(statement.getUnit().getId(), statement.getUnit().getUnitNumber()) : null,
                statement.getPeriodStart(),
                statement.getPeriodEnd(),
                statement.getOpeningBalance(),
                statement.getClosingBalance(),
                statement.getGeneratedAt(),
                statement.getCreatedAt()
        );
    }
}
