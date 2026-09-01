package com.saranaresturantsystem.reports.controller.sales;

import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.reports.dto.sales.SaleReportFilter;
import com.saranaresturantsystem.reports.dto.sales.SaleReportResponse;
import com.saranaresturantsystem.reports.service.sales.ReportService;
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
@Tag(name = "Report", description = "Endpoints for generating various system reports (Sales, Purchase, etc.)")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales")
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate sales report with summary metrics and filters")
    public ResponseEntity<ApiResponse<SaleReportResponse>> getSaleReport(
            @ParameterObject SaleReportFilter filter
    ) {
        SaleReportResponse report = reportService.getSaleReport(filter);
        return ResponseFactory.ok(report, "Sale report generated successfully");
    }

    @GetMapping("/sales-items")
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate paginated sales report based on filters")
    public ResponseEntity<ApiResponse<PageDTO>> getSalesItemsReport(
            @ParameterObject SaleReportFilter filter,
            @ParameterObject Pageable pageable
    ) {
        Page<SaleReportResponse> reportPage = reportService.getSalesItemsReport(filter, pageable);
        return ResponseFactory.ok(reportPage, "Sale Report");
    }
}