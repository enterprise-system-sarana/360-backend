package com.saranaresturantsystem.reports.dto.sales;

import com.saranaresturantsystem.dto.response.sales.SaleResponse;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SaleReportResponse {
    private Double totalSalesAmount;
    private Double totalDiscount;
    private Double totalPaidAmount;
    private Long totalTransactions;
    private Long storeId;
    private String storeName;
    private Long customerId;
    private String customerName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<SaleResponse> sales;
}