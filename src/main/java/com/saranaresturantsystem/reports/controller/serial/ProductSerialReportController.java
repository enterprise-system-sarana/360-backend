package com.saranaresturantsystem.reports.controller.serial;

import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.reports.dto.serial.ProductSerialReportFilter;
import com.saranaresturantsystem.reports.service.serial.ProductSerialReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports/product-serials")
@Tag(name = "Product Serial Report", description = "Endpoints for generating product serials and inventory tracking reports")
public class ProductSerialReportController {

    private final ProductSerialReportService reportService;

    @GetMapping
    @Operation(summary = "Generate paginated product serials report with filters")
    public ResponseEntity<ApiResponse<PageDTO>> getProductSerialsReport(
            @ParameterObject ProductSerialReportFilter filter,
            @ParameterObject Pageable pageable
    ) {
        Page<?> reportPage = reportService.getProductSerialsReport(filter, pageable);
        return ResponseFactory.ok(reportPage, "Product Serial Report");
    }
}