package com.saranaresturantsystem.reports.controller.stock;

import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.reports.dto.stock.*;
import com.saranaresturantsystem.reports.service.stock.StockReportService;
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
@Tag(name = "Stock Report", description = "Endpoints for generating high-performance stock levels, inventory valuation and movement reports")
public class StockReportController {

    private final StockReportService stockReportService;

    @GetMapping({"/stocks", "/stock"})
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate stock report with summary metrics and filters")
    public ResponseEntity<ApiResponse<StockReportResponse>> getStockReport(
            @ParameterObject StockReportFilter filter
    ) {
        StockReportResponse report = stockReportService.getStockReport(filter);
        return ResponseFactory.ok(report, "Stock report generated successfully");
    }

    @GetMapping({"/stock-items", "/stocks-paged"})
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate paginated stock items report based on filters")
    public ResponseEntity<ApiResponse<PageDTO>> getStockItemsReport(
            @ParameterObject StockReportFilter filter,
            @ParameterObject Pageable pageable
    ) {
        Page<StockItemReportResponse> reportPage = stockReportService.getStockReportPaged(filter, pageable);
        return ResponseFactory.ok(reportPage, "Stock Items Report");
    }

    @GetMapping({"/stocks/summary", "/stock/summary"})
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Get aggregated stock summary KPI metrics")
    public ResponseEntity<ApiResponse<StockReportSummaryProjection>> getStockSummary(
            @ParameterObject StockReportFilter filter
    ) {
        StockReportSummaryProjection summary = stockReportService.getStockSummary(filter);
        return ResponseFactory.ok(summary, "Stock summary metrics retrieved successfully");
    }

    @GetMapping({"/stock-movements", "/stocks/movements"})
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate paginated stock movement and inventory audit transactions report")
    public ResponseEntity<ApiResponse<PageDTO>> getStockMovements(
            @ParameterObject StockMovementReportFilter filter,
            @ParameterObject Pageable pageable
    ) {
        Page<StockMovementItemResponse> movementPage = stockReportService.getStockMovements(filter, pageable);
        return ResponseFactory.ok(movementPage, "Stock Movements Report");
    }
}
