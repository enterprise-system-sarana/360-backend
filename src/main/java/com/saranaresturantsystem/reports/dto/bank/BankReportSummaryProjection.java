package com.saranaresturantsystem.reports.dto.bank;

import java.math.BigDecimal;

public interface BankReportSummaryProjection {
    BigDecimal getTotalInflow();
    BigDecimal getTotalOutflow();
    Long getTotalTransactions();
}

