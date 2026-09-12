package com.saranaresturantsystem.reports.controller.customer;

import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.reports.dto.customers.CustomerReportResponse;
import com.saranaresturantsystem.reports.service.customer.CustomerReportService;
import com.saranaresturantsystem.reports.specification.customers.CustomerReportFilter;
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
@RequestMapping("/api/v1/reports/customers")
@Tag(name = "Customer Report", description = "Endpoints for generating customer reports")
public class CustomerReportController {

    private final CustomerReportService customerReportService;

    @GetMapping
//    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate paginated customers report with optional filters")
    public ResponseEntity<ApiResponse<PageDTO>> getCustomersReport(
            @ParameterObject @ModelAttribute CustomerReportFilter filter,
            @Parameter(hidden = true) Pageable pageable
    ) {
        Page<CustomerReportResponse> reportPage = customerReportService.getCustomersReport(filter, pageable);
        return ResponseFactory.ok(reportPage, "Customer Report retrieved successfully");
    }
}