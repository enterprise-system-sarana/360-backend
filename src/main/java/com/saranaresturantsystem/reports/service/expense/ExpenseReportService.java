package com.saranaresturantsystem.reports.service.expense;


import com.saranaresturantsystem.reports.dto.expenses.ExpenseReportResponse;
import com.saranaresturantsystem.reports.specification.expenses.ExpenseReportFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpenseReportService {
    Page<ExpenseReportResponse> getExpensesReport(ExpenseReportFilter filter, Pageable pageable);
}