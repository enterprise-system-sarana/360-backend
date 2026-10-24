package com.saranaresturantsystem.reports.dto.purchases;

import java.math.BigDecimal;

public interface PurchaseReportSummaryProjection {
    BigDecimal getTotalAmount();
    BigDecimal getTotalDiscount();
    BigDecimal getTotalPaidAmount();
    BigDecimal getTotalDueAmount();
    BigDecimal getTotalQuantity();
    Long getTotalTransactions();
}
