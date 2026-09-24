package com.saranaresturantsystem.reports.controller.expense;

import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.reports.dto.expenses.ExpenseReportResponse;
import com.saranaresturantsystem.reports.service.expense.ExpenseReportService;
import com.saranaresturantsystem.reports.specification.expenses.ExpenseReportFilter;
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
@RequestMapping("/api/v1/reports/expenses")
@Tag(name = "Expense Report", description = "Endpoints for generating expense and financial transaction reports")
public class ExpenseReportController {

    private final ExpenseReportService expenseReportService;

    @GetMapping
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Generate paginated expenses report with optional filters")
    public ResponseEntity<ApiResponse<PageDTO>> getExpensesReport(
            @ParameterObject @ModelAttribute ExpenseReportFilter filter,
            @Parameter(hidden = true) Pageable pageable
    ) {
        Page<ExpenseReportResponse> reportPage = expenseReportService.getExpensesReport(filter, pageable);
        return ResponseFactory.ok(reportPage, "Expense Report retrieved successfully");
    }
}