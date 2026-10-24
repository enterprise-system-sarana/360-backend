package com.saranaresturantsystem.reports.dto.payment;

import java.math.BigDecimal;

public interface PaymentReportSummaryProjection {
    BigDecimal getTotalAmount();
    BigDecimal getTotalReceivedAmount();
    BigDecimal getTotalReturnAmount();
    BigDecimal getTotalDueAmount();
    Long getTotalTransactions();
}
