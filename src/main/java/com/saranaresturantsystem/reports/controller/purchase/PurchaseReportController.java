package com.saranaresturantsystem.reports.controller.purchase;

import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.reports.dto.purchases.PurchaseReportResponse;
import com.saranaresturantsystem.reports.service.purchase.PurchaseReportService;
import com.saranaresturantsystem.reports.specification.purchases.PurchaseReportFilter;
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
@RequestMapping("/api/v1/reports/purchases")
@Tag(name = "Purchase Report", description = "Endpoints for generating purchase reports")
public class PurchaseReportController {

    private final PurchaseReportService purchaseReportService;

    @GetMapping
//    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate paginated purchases report with optional filters")
    public ResponseEntity<ApiResponse<PageDTO>> getPurchasesReport(
            @ParameterObject @ModelAttribute PurchaseReportFilter filter,
            @Parameter(hidden = true) Pageable pageable
    ) {
        Page<PurchaseReportResponse> reportPage = purchaseReportService.getPurchasesReport(filter, pageable);
        return ResponseFactory.ok(reportPage, "Purchase Report retrieved successfully");
    }
}