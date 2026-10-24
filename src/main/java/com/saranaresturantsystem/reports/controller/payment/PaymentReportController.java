package com.saranaresturantsystem.reports.controller.payment;

import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.reports.dto.payment.*;
import com.saranaresturantsystem.reports.service.payment.PaymentReportService;
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
@RequestMapping("/api/v1/reports/payments")
@Tag(name = "Payment Reports", description = "Endpoints for generating customer sale payments and supplier purchase payments reports")
public class PaymentReportController {

    private final PaymentReportService paymentReportService;

    @GetMapping("/sales")
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate sale payments report with summary metrics and filters")
    public ResponseEntity<ApiResponse<SalePaymentSummaryResponse>> getSalePaymentReport(
            @ParameterObject PaymentReportFilter filter
    ) {
        SalePaymentSummaryResponse response = paymentReportService.getSalePaymentReport(filter);
        return ResponseFactory.ok(response, "Sale payments report generated successfully");
    }

    @GetMapping("/sales-paged")
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate paginated sale payments report based on filters")
    public ResponseEntity<ApiResponse<PageDTO>> getSalePaymentReportPaged(
            @ParameterObject PaymentReportFilter filter,
            @ParameterObject Pageable pageable
    ) {
        Page<SalePaymentReportResponse> page = paymentReportService.getSalePaymentReportPaged(filter, pageable);
        return ResponseFactory.ok(page, "Sale Payments Report");
    }

    @GetMapping("/purchases")
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate purchase payments report with summary metrics and filters")
    public ResponseEntity<ApiResponse<PurchasePaymentSummaryResponse>> getPurchasePaymentReport(
            @ParameterObject PaymentReportFilter filter
    ) {
        PurchasePaymentSummaryResponse response = paymentReportService.getPurchasePaymentReport(filter);
        return ResponseFactory.ok(response, "Purchase payments report generated successfully");
    }

    @GetMapping("/purchases-paged")
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate paginated purchase payments report based on filters")
    public ResponseEntity<ApiResponse<PageDTO>> getPurchasePaymentReportPaged(
            @ParameterObject PaymentReportFilter filter,
            @ParameterObject Pageable pageable
    ) {
        Page<PurchasePaymentReportResponse> page = paymentReportService.getPurchasePaymentReportPaged(filter, pageable);
        return ResponseFactory.ok(page, "Purchase Payments Report");
    }
}
