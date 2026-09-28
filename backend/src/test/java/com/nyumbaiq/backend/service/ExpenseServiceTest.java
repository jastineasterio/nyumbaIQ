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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ManagerAssignmentRepository managerAssignmentRepository;
    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void getAllExpenses_ShouldReturnPage() {
        UUID userId = UUID.randomUUID();
        User owner = User.builder()
                .id(userId)
                .firstName("Test")
                .lastName("Owner")
                .email("owner@test.com")
                .role(Role.OWNER)
                .status(UserStatus.ACTIVE)
                .build();
        Property property = Property.builder()
                .id(UUID.randomUUID())
                .name("Test Property")
                .propertyCode("PROP-123")
                .owner(owner)
                .status(PropertyStatus.ACTIVE)
                .build();
        Expense expense = Expense.builder()
                .id(UUID.randomUUID())
                .expenseNumber("EXP-123")
                .property(property)
                .category("Maintenance")
                .description("Desc")
                .amount(BigDecimal.valueOf(100))
                .expenseDate(LocalDateTime.now())
                .status(ExpenseStatus.DRAFT)
                .createdBy(owner)
                .build();
        when(currentUser.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        Page<Expense> page = new PageImpl<>(List.of(expense));
        when(expenseRepository.findAll(any(Pageable.class))).thenReturn(page);
        var result = expenseService.getAllExpenses(Pageable.ofSize(20));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getExpense_ShouldReturnExpense() {
        UUID userId = UUID.randomUUID();
        User owner = User.builder()
                .id(userId)
                .firstName("Test")
                .lastName("Owner")
                .email("owner@test.com")
                .role(Role.OWNER)
                .status(UserStatus.ACTIVE)
                .build();
        Property property = Property.builder()
                .id(UUID.randomUUID())
                .name("Test Property")
                .propertyCode("PROP-123")
                .owner(owner)
                .status(PropertyStatus.ACTIVE)
                .build();
        Expense expense = Expense.builder()
                .id(UUID.randomUUID())
                .expenseNumber("EXP-123")
                .property(property)
                .category("Maintenance")
                .description("Desc")
                .amount(BigDecimal.valueOf(100))
                .expenseDate(LocalDateTime.now())
                .status(ExpenseStatus.DRAFT)
                .createdBy(owner)
                .build();
        when(currentUser.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(expenseRepository.findById(expense.getId())).thenReturn(Optional.of(expense));
        ExpenseDto dto = expenseService.getExpense(expense.getId());
        assertEquals(expense.getExpenseNumber(), dto.getExpenseNumber());
    }

    @Test
    void createExpense_ShouldCreateExpense() {
        UUID userId = UUID.randomUUID();
        User owner = User.builder()
                .id(userId)
                .firstName("Test")
                .lastName("Owner")
                .email("owner@test.com")
                .role(Role.OWNER)
                .status(UserStatus.ACTIVE)
                .build();
        Property property = Property.builder()
                .id(UUID.randomUUID())
                .name("Test Property")
                .propertyCode("PROP-123")
                .owner(owner)
                .status(PropertyStatus.ACTIVE)
                .build();
        when(currentUser.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(propertyRepository.findById(any())).thenReturn(Optional.of(property));
        Expense expense = Expense.builder()
                .id(UUID.randomUUID())
                .expenseNumber("EXP-123")
                .property(property)
                .category("Maintenance")
                .description("Desc")
                .amount(BigDecimal.valueOf(100))
                .expenseDate(LocalDateTime.now())
                .status(ExpenseStatus.DRAFT)
                .createdBy(owner)
                .build();
        when(expenseRepository.save(any())).thenReturn(expense);
        var result = expenseService.createExpense(new CreateExpenseRequest(
                property.getId(), null, null, "Maintenance", "Desc", BigDecimal.valueOf(100), LocalDateTime.now(), null, null, null, null
        ));
        assertNotNull(result);
    }
}
