package com.saranaresturantsystem.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {

    private KpiData kpis;
    private RevenueChart revenueChart;
    private List<RecentTransaction> recentTransactions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KpiData {
        private KpiValue totalRevenue;
        private KpiValue netProfit;
        private KpiValue totalSales;
        private KpiValue activeCustomers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KpiValue {
        private Object value;
        private Double changePercentage;
        private Boolean isPositive;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueChart {
        private List<String> labels;
        private List<BigDecimal> data;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentTransaction {
        private Long id;
        private String name;
        private LocalDateTime date;
        private BigDecimal amount;
        private String status;
    }
}
