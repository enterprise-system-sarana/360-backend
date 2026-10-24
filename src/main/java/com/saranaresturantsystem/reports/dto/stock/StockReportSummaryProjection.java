package com.saranaresturantsystem.reports.dto.stock;

import java.math.BigDecimal;

public interface StockReportSummaryProjection {
    BigDecimal getTotalQuantity();
    BigDecimal getTotalCostValue();
    BigDecimal getTotalSaleValue();
    BigDecimal getTotalPotentialProfit();
    Long getTotalItems();
    Long getInStockCount();
    Long getLowStockCount();
    Long getOutOfStockCount();
}
