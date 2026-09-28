package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.*;
import com.nyumbaiq.backend.domain.enums.*;
import com.nyumbaiq.backend.domain.repository.*;
import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportService {
    private final InvoiceRepository invoiceRepository;
    private final ExpenseRepository expenseRepository;
    private final PaymentRepository paymentRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final LeaseRepository leaseRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public ReportService(InvoiceRepository invoiceRepository, ExpenseRepository expenseRepository,
                         PaymentRepository paymentRepository, PropertyRepository propertyRepository,
                         TenantRepository tenantRepository, LeaseRepository leaseRepository,
                         UserRepository userRepository, CurrentUser currentUser) {
        this.invoiceRepository = invoiceRepository;
        this.expenseRepository = expenseRepository;
        this.paymentRepository = paymentRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
        this.leaseRepository = leaseRepository;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public IncomeSummaryDto getIncomeSummary(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        Page<Payment> payments = paymentRepository.findAll(Pageable.unpaged());
        BigDecimal totalIncome = BigDecimal.ZERO;
        Map<String, BigDecimal> incomeBySource = new HashMap<>();
        Map<String, BigDecimal> incomeByProperty = new HashMap<>();
        for (Payment payment : payments.getContent()) {
            if (payment.getConfirmedAt() != null && !payment.getConfirmedAt().isBefore(start) && !payment.getConfirmedAt().isAfter(end)) {
                totalIncome = totalIncome.add(payment.getAmount());
                String source = payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : "UNKNOWN";
                incomeBySource.merge(source, payment.getAmount(), BigDecimal::add);
                if (payment.getProperty() != null) {
                    incomeByProperty.merge(payment.getProperty().getName(), payment.getAmount(), BigDecimal::add);
                }
            }
        }
        Page<Invoice> invoices = invoiceRepository.findAll(Pageable.unpaged());
        BigDecimal expectedIncome = BigDecimal.ZERO;
        for (Invoice invoice : invoices.getContent()) {
            if (invoice.getIssueDate() != null && !invoice.getIssueDate().isBefore(start) && !invoice.getIssueDate().isAfter(end)) {
                expectedIncome = expectedIncome.add(invoice.getAmount());
            }
        }
        BigDecimal collectionRate = BigDecimal.ZERO;
        if (expectedIncome.compareTo(BigDecimal.ZERO) > 0) {
            collectionRate = totalIncome.divide(expectedIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return new IncomeSummaryDto(startDate, endDate, totalIncome, expectedIncome, collectionRate,
                incomeBySource, incomeByProperty);
    }

    @Transactional(readOnly = true)
    public ExpenseSummaryDto getExpenseSummary(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        Page<Expense> expenses = expenseRepository.findAll(Pageable.unpaged());
        BigDecimal totalExpenses = BigDecimal.ZERO;
        BigDecimal approvedExpenses = BigDecimal.ZERO;
        BigDecimal pendingExpenses = BigDecimal.ZERO;
        BigDecimal paidExpenses = BigDecimal.ZERO;
        Map<String, BigDecimal> expensesByCategory = new HashMap<>();
        Map<String, BigDecimal> expensesByProperty = new HashMap<>();
        for (Expense expense : expenses.getContent()) {
            if (expense.getExpenseDate() != null && !expense.getExpenseDate().isBefore(start) && !expense.getExpenseDate().isAfter(end)) {
                totalExpenses = totalExpenses.add(expense.getAmount());
                if (expense.getStatus() == ExpenseStatus.APPROVED) {
                    approvedExpenses = approvedExpenses.add(expense.getAmount());
                } else if (expense.getStatus() == ExpenseStatus.SUBMITTED) {
                    pendingExpenses = pendingExpenses.add(expense.getAmount());
                } else if (expense.getStatus() == ExpenseStatus.PAID) {
                    paidExpenses = paidExpenses.add(expense.getAmount());
                }
                expensesByCategory.merge(expense.getCategory(), expense.getAmount(), BigDecimal::add);
                if (expense.getProperty() != null) {
                    expensesByProperty.merge(expense.getProperty().getName(), expense.getAmount(), BigDecimal::add);
                }
            }
        }
        return new ExpenseSummaryDto(startDate, endDate, totalExpenses, approvedExpenses, pendingExpenses, paidExpenses,
                expensesByCategory, expensesByProperty);
    }

    @Transactional(readOnly = true)
    public OutstandingRentDto getOutstandingRent() {
        Page<Invoice> invoices = invoiceRepository.findByStatus(InvoiceStatus.OVERDUE, Pageable.unpaged());
        BigDecimal totalOutstanding = BigDecimal.ZERO;
        Map<UUID, OutstandingTenantDto> tenantMap = new HashMap<>();
        for (Invoice invoice : invoices.getContent()) {
            totalOutstanding = totalOutstanding.add(invoice.getBalance());
            Tenant tenant = invoice.getTenant();
            if (tenant != null) {
                tenantMap.merge(tenant.getId(), new OutstandingTenantDto(
                        tenant.getId(),
                        tenant.getFullName(),
                        invoice.getProperty() != null ? invoice.getProperty().getName() : null,
                        invoice.getUnit() != null ? invoice.getUnit().getUnitNumber() : null,
                        invoice.getBalance(),
                        calculateOverdueDays(invoice.getDueDate())
                ), (existing, newVal) -> new OutstandingTenantDto(
                        existing.tenantId(),
                        existing.tenantName(),
                        existing.propertyName(),
                        existing.unitNumber(),
                        existing.outstandingAmount().add(newVal.outstandingAmount()),
                        Math.max(existing.overdueDays(), newVal.overdueDays())
                ));
            }
        }
        return new OutstandingRentDto(totalOutstanding, tenantMap.size(), new ArrayList<>(tenantMap.values()));
    }

    @Transactional(readOnly = true)
    public FinancialReportDto getPropertyRevenueReport(UUID propertyId, LocalDate startDate, LocalDate endDate) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new NotFoundException("Property not found"));
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        Page<Invoice> invoices = invoiceRepository.findByPropertyId(propertyId, Pageable.unpaged());
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;
        BigDecimal outstandingRent = BigDecimal.ZERO;
        Map<String, BigDecimal> incomeByCategory = new HashMap<>();
        Map<String, BigDecimal> expensesByCategory = new HashMap<>();
        Map<String, BigDecimal> revenueByProperty = new HashMap<>();
        for (Invoice invoice : invoices.getContent()) {
            if (invoice.getIssueDate() != null && !invoice.getIssueDate().isBefore(start) && !invoice.getIssueDate().isAfter(end)) {
                totalIncome = totalIncome.add(invoice.getAmountPaid());
                incomeByCategory.merge("RENT", invoice.getAmountPaid(), BigDecimal::add);
                if (invoice.getBalance() != null && invoice.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                    outstandingRent = outstandingRent.add(invoice.getBalance());
                }
            }
        }
        Page<Expense> expenses = expenseRepository.findByPropertyId(propertyId, Pageable.unpaged());
        for (Expense expense : expenses.getContent()) {
            if (expense.getExpenseDate() != null && !expense.getExpenseDate().isBefore(start) && !expense.getExpenseDate().isAfter(end)) {
                totalExpenses = totalExpenses.add(expense.getAmount());
                expensesByCategory.merge(expense.getCategory(), expense.getAmount(), BigDecimal::add);
            }
        }
        revenueByProperty.put(property.getName(), totalIncome);
        BigDecimal netProfit = totalIncome.subtract(totalExpenses);
        return new FinancialReportDto(startDate, endDate, totalIncome, totalExpenses, netProfit, outstandingRent,
                incomeByCategory, expensesByCategory, revenueByProperty);
    }

    private int calculateOverdueDays(LocalDateTime dueDate) {
        if (dueDate == null) return 0;
        return (int) java.time.Duration.between(dueDate, LocalDateTime.now()).toDays();
    }
}
