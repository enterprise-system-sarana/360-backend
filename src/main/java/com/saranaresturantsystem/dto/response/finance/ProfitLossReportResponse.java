package com.saranaresturantsystem.dto.response.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfitLossReportResponse {
    private List<String> months;
    private IncomeData income;
    private ExpenseData expenses;
    private List<BigDecimal> netProfit;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IncomeData {
        private List<BigDecimal> sales;
        private List<BigDecimal> service;
        private List<BigDecimal> purchaseReturn;
        private List<BigDecimal> grossProfit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpenseData {
        private List<BigDecimal> sales;
        private List<BigDecimal> purchase;
        private List<BigDecimal> salesReturn;
        private List<BigDecimal> totalExpense;
    }
}
