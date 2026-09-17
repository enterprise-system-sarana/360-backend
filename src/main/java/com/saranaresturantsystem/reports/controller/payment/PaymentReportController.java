package com.saranaresturantsystem.reports.controller.payment;

import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.reports.dto.payments.PaymentReportResponse;
import com.saranaresturantsystem.reports.service.payment.PaymentReportService;
import com.saranaresturantsystem.reports.specification.payments.PaymentReportFilter;
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
@RequestMapping("/api/v1/reports/payments")
@Tag(name = "Payment Report", description = "Endpoints for generating payment reports")
public class PaymentReportController {

    private final PaymentReportService paymentReportService;

    @GetMapping
//    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate paginated payments report with filters for Sale No, Customer Name, Created By, Paid By, and Date Range")
    public ResponseEntity<ApiResponse<PageDTO>> getPaymentsReport(
            @ParameterObject @ModelAttribute PaymentReportFilter filter,
            @Parameter(hidden = true) Pageable pageable
    ) {
        Page<PaymentReportResponse> reportPage = paymentReportService.getPaymentsReport(filter, pageable);
        return ResponseFactory.ok(reportPage, "Payment Report retrieved successfully");
    }
}