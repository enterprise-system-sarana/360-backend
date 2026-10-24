package com.saranaresturantsystem.reports.controller.purchases;

import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.reports.dto.purchases.PurchaseItemReportResponse;
import com.saranaresturantsystem.reports.dto.purchases.PurchaseReportFilter;
import com.saranaresturantsystem.reports.dto.purchases.PurchaseReportResponse;
import com.saranaresturantsystem.reports.service.purchases.PurchaseReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
@Tag(name = "Purchase Report", description = "Endpoints for generating purchase summary and itemized reports")
public class PurchaseReportController {

    private final PurchaseReportService purchaseReportService;

    @GetMapping("/purchases")
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate purchase report with summary metrics and filters")
    public ResponseEntity<ApiResponse<PurchaseReportResponse>> getPurchaseReport(
            @ParameterObject PurchaseReportFilter filter
    ) {
        PurchaseReportResponse report = purchaseReportService.getPurchaseReport(filter);
        return ResponseFactory.ok(report, "Purchase report generated successfully");
    }

    @GetMapping("/purchase-items")
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate paginated purchase items report based on filters")
    public ResponseEntity<ApiResponse<PageDTO>> getPurchaseItemsReport(
            @ParameterObject PurchaseReportFilter filter,
            @ParameterObject Pageable pageable
    ) {
        Page<PurchaseItemReportResponse> reportPage = purchaseReportService.getPurchaseItemsReport(filter, pageable);
        return ResponseFactory.ok(reportPage, "Purchase Items Report");
    }
}
