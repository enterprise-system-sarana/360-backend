package com.saranaresturantsystem.reports.controller.customer;

import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.reports.dto.customers.AccountReceivableReportResponse;
import com.saranaresturantsystem.reports.service.customer.AccountReceivableReportService;
import com.saranaresturantsystem.reports.specification.customers.AccountReceivableReportFilter;
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
@RequestMapping("/api/v1/reports/accounts-receivable")
@Tag(name = "Account Receivable Report", description = "Endpoints for generating customer accounts receivable reports")
public class AccountReceivableReportController {

    private final AccountReceivableReportService accountReceivableReportService;

    @GetMapping
//    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate paginated accounts receivable report with filters for store, dates, and customer name")
    public ResponseEntity<ApiResponse<PageDTO>> getAccountReceivableReport(
            @ParameterObject @ModelAttribute AccountReceivableReportFilter filter,
            @Parameter(hidden = true) Pageable pageable
    ) {
        Page<AccountReceivableReportResponse> reportPage = accountReceivableReportService.getAccountReceivableReport(filter, pageable);
        return ResponseFactory.ok(reportPage, "Account Receivable Report retrieved successfully");
    }
}