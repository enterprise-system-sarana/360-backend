package com.saranaresturantsystem.reports.controller.finance;

import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.reports.dto.bank.BankReportFilter;
import com.saranaresturantsystem.reports.dto.bank.BankReportResponse;
import com.saranaresturantsystem.reports.dto.bank.BankReportSummaryResponse;
import com.saranaresturantsystem.reports.service.finance.BankReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports/banks")
@Tag(name = "Bank Reports", description = "Endpoints for generating bank transaction tracking and balance reports")
public class BankReportController {

    private final BankReportService bankReportService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('report:read', 'bank:read', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN') or isAuthenticated()")
    @Operation(summary = "Generate bank report summary with totals and transaction flow breakdown")
    public ResponseEntity<ApiResponse<BankReportSummaryResponse>> getBankReportSummary(
            @ParameterObject @ModelAttribute BankReportFilter filter
    ) {
        BankReportSummaryResponse response = bankReportService.getBankReportSummary(filter);
        return ResponseFactory.ok(response, "Bank report generated successfully");
    }

    @GetMapping("/paged")
    @PreAuthorize("hasAnyAuthority('report:read', 'bank:read', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN') or isAuthenticated()")
    @Operation(summary = "Generate paginated bank transaction report with optional filters")
    public ResponseEntity<ApiResponse<PageDTO>> getBankReportPaged(
            @ParameterObject @ModelAttribute BankReportFilter filter,
            @Parameter(hidden = true) Pageable pageable
    ) {
        Page<BankReportResponse> reportPage = bankReportService.getBankReportPaged(filter, pageable);
        return ResponseFactory.ok(reportPage, "Bank Report retrieved successfully");
    }
}

