package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<PageResponse<ExpenseDto>> getAllExpenses(Pageable pageable) {
        Page<ExpenseDto> page = expenseService.getAllExpenses(pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<ExpenseDto> getExpense(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseService.getExpense(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<ExpenseDto> createExpense(@Valid @RequestBody CreateExpenseRequest request) {
        return ResponseEntity.ok(expenseService.createExpense(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<ExpenseDto> updateExpense(@PathVariable UUID id, @Valid @RequestBody UpdateExpenseRequest request) {
        return ResponseEntity.ok(expenseService.updateExpense(id, request));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<ExpenseDto> submitExpense(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseService.submitExpense(id));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ExpenseDto> approveExpense(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseService.approveExpense(id));
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<ExpenseDto> payExpense(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseService.payExpense(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<ExpenseDto> cancelExpense(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseService.cancelExpense(id));
    }
}
