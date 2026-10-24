package com.saranaresturantsystem.reports.controller.finance;

import com.saranaresturantsystem.dto.response.finance.ProfitLossReportResponse;
import com.saranaresturantsystem.services.interfaces.reports.ProfitLossReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Profit & Loss Report", description = "Endpoints for generating financial profit and loss reports")
public class ProfitLossReportController {

    private final ProfitLossReportService profitLossReportService;

    @GetMapping("/profit-loss")
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate profit and loss report for specified date range or months")
    public ResponseEntity<ProfitLossReportResponse> getProfitLossReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "6") Integer months) {
        
        return ResponseEntity.ok(profitLossReportService.generateProfitLossReport(startDate, endDate, months));
    }
}
