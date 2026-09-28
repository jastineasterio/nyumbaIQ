package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.Expense;
import com.nyumbaiq.backend.domain.enums.ExpenseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    Page<Expense> findByPropertyId(UUID propertyId, Pageable pageable);
    Page<Expense> findByStatus(ExpenseStatus status, Pageable pageable);
    Optional<Expense> findByExpenseNumber(String expenseNumber);
    boolean existsByExpenseNumber(String expenseNumber);
}
