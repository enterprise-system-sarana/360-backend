package com.saranaresturantsystem.reports.dto.bank;

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
public class BankReportSummaryResponse {
    private BigDecimal totalInflow;
    private BigDecimal totalOutflow;
    private BigDecimal netFlow;
    private Long totalTransactions;
    private BigDecimal currentTotalBalance;
    private List<BankReportResponse> transactions;
}

