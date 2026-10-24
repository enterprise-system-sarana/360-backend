package com.saranaresturantsystem.reports.dto.sales;

import java.math.BigDecimal;

public interface SalesReportSummaryProjection {
    BigDecimal getTotalSalesAmount();
    BigDecimal getTotalDiscount();
    BigDecimal getTotalPaidAmount();
    BigDecimal getTotalCost();
    BigDecimal getTotalProfit();
    Long getTotalTransactions();
}
