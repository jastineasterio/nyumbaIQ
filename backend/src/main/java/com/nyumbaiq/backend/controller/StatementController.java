package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.service.StatementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/statements")
public class StatementController {
    private final StatementService statementService;

    public StatementController(StatementService statementService) {
        this.statementService = statementService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<StatementDto>> getAllStatements(Pageable pageable) {
        Page<StatementDto> page = statementService.getAllStatements(pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StatementDto> getStatement(@PathVariable UUID id) {
        return ResponseEntity.ok(statementService.getStatement(id));
    }

    @GetMapping("/number/{statementNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StatementDto> getStatementByNumber(@PathVariable String statementNumber) {
        return ResponseEntity.ok(statementService.getStatementByNumber(statementNumber));
    }

    @PostMapping("/generate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StatementDto> generateStatement(@Valid @RequestBody CreateStatementRequest request) {
        return ResponseEntity.ok(statementService.generateStatement(request));
    }
}
