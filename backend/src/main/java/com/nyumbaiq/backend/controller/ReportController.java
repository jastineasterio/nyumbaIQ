package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/income/summary")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<IncomeSummaryDto> getIncomeSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getIncomeSummary(startDate, endDate));
    }

    @GetMapping("/expense/summary")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<ExpenseSummaryDto> getExpenseSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getExpenseSummary(startDate, endDate));
    }

    @GetMapping("/outstanding-rent")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<OutstandingRentDto> getOutstandingRent() {
        return ResponseEntity.ok(reportService.getOutstandingRent());
    }

    @GetMapping("/property/{propertyId}/revenue")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<FinancialReportDto> getPropertyRevenue(
            @PathVariable UUID propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getPropertyRevenueReport(propertyId, startDate, endDate));
    }
}
