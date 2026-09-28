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
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final ManagerAssignmentRepository managerAssignmentRepository;
    private final CurrentUser currentUser;

    public ExpenseService(ExpenseRepository expenseRepository, PropertyRepository propertyRepository,
                          UserRepository userRepository, ManagerAssignmentRepository managerAssignmentRepository,
                          CurrentUser currentUser) {
        this.expenseRepository = expenseRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.managerAssignmentRepository = managerAssignmentRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public Page<ExpenseDto> getAllExpenses(Pageable pageable) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRole() == Role.TENANT) {
            throw new AccessDeniedException("Tenants cannot view expenses");
        }
        if (user.getRole() == Role.OWNER) {
            return expenseRepository.findAll(pageable).map(this::toDto);
        }
        return expenseRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public ExpenseDto getExpense(UUID id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Expense not found"));
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRole() == Role.TENANT) {
            throw new AccessDeniedException("Tenants cannot view expenses");
        }
        return toDto(expense);
    }

    public ExpenseDto createExpense(CreateExpenseRequest request) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRole() == Role.TENANT) {
            throw new AccessDeniedException("Tenants cannot create expenses");
        }
        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new NotFoundException("Property not found"));
        String expenseNumber = "EXP-" + System.currentTimeMillis();
        Expense expense = Expense.builder()
                .expenseNumber(expenseNumber)
                .property(property)
                .building(request.buildingId() != null ? Building.builder().id(request.buildingId()).build() : null)
                .unit(request.unitId() != null ? Unit.builder().id(request.unitId()).build() : null)
                .category(request.category())
                .description(request.description())
                .amount(request.amount())
                .expenseDate(request.expenseDate())
                .vendor(request.vendor())
                .paymentMethod(request.paymentMethod())
                .supportingDocumentPath(request.supportingDocumentPath())
                .notes(request.notes())
                .status(ExpenseStatus.DRAFT)
                .createdBy(User.builder().id(userId).build())
                .build();
        expenseRepository.save(expense);
        return toDto(expense);
    }

    public ExpenseDto updateExpense(UUID id, UpdateExpenseRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Expense not found"));
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRole() == Role.TENANT) {
            throw new AccessDeniedException("Tenants cannot update expenses");
        }
        if (request.category() != null) expense.setCategory(request.category());
        if (request.description() != null) expense.setDescription(request.description());
        if (request.amount() != null) expense.setAmount(request.amount());
        if (request.expenseDate() != null) expense.setExpenseDate(request.expenseDate());
        if (request.vendor() != null) expense.setVendor(request.vendor());
        if (request.paymentMethod() != null) expense.setPaymentMethod(request.paymentMethod());
        if (request.supportingDocumentPath() != null) expense.setSupportingDocumentPath(request.supportingDocumentPath());
        if (request.notes() != null) expense.setNotes(request.notes());
        expenseRepository.save(expense);
        return toDto(expense);
    }

    public ExpenseDto submitExpense(UUID id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Expense not found"));
        if (expense.getStatus() != ExpenseStatus.DRAFT) {
            throw new BadRequestException("Expense is not in draft status");
        }
        expense.setStatus(ExpenseStatus.SUBMITTED);
        expenseRepository.save(expense);
        return toDto(expense);
    }

    public ExpenseDto approveExpense(UUID id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Expense not found"));
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRole() != Role.OWNER) {
            throw new AccessDeniedException("Only owner can approve expenses");
        }
        if (expense.getStatus() != ExpenseStatus.SUBMITTED) {
            throw new BadRequestException("Expense is not in submitted status");
        }
        expense.setStatus(ExpenseStatus.APPROVED);
        expense.setApprovedBy(User.builder().id(userId).build());
        expense.setApprovedAt(LocalDateTime.now());
        expenseRepository.save(expense);
        return toDto(expense);
    }

    public ExpenseDto payExpense(UUID id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Expense not found"));
        if (expense.getStatus() != ExpenseStatus.APPROVED) {
            throw new BadRequestException("Expense must be approved before payment");
        }
        expense.setStatus(ExpenseStatus.PAID);
        expenseRepository.save(expense);
        return toDto(expense);
    }

    public ExpenseDto cancelExpense(UUID id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Expense not found"));
        if (expense.getStatus() == ExpenseStatus.CANCELLED) {
            throw new BadRequestException("Expense is already cancelled");
        }
        if (expense.getStatus() == ExpenseStatus.PAID) {
            throw new BadRequestException("Cannot cancel paid expense");
        }
        expense.setStatus(ExpenseStatus.CANCELLED);
        expenseRepository.save(expense);
        return toDto(expense);
    }

    private ExpenseDto toDto(Expense expense) {
        User createdBy = expense.getCreatedBy();
        User approvedBy = expense.getApprovedBy();
        Property property = expense.getProperty();
        Building building = expense.getBuilding();
        Unit unit = expense.getUnit();
        return new ExpenseDto(
                expense.getId(),
                expense.getExpenseNumber(),
                property != null ? property.getId() : null,
                property != null ? property.getName() : null,
                building != null ? building.getId() : null,
                building != null ? building.getName() : null,
                unit != null ? unit.getId() : null,
                unit != null ? unit.getUnitNumber() : null,
                expense.getCategory(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getExpenseDate(),
                expense.getVendor(),
                expense.getPaymentMethod(),
                expense.getSupportingDocumentPath(),
                expense.getNotes(),
                expense.getStatus(),
                createdBy != null ? new UserSummary(createdBy.getId(), createdBy.getFirstName(), createdBy.getLastName(), createdBy.getEmail(), createdBy.getRole(), createdBy.getStatus()) : null,
                approvedBy != null ? new UserSummary(approvedBy.getId(), approvedBy.getFirstName(), approvedBy.getLastName(), approvedBy.getEmail(), approvedBy.getRole(), approvedBy.getStatus()) : null,
                expense.getApprovedAt(),
                expense.getCreatedAt()
        );
    }
}
