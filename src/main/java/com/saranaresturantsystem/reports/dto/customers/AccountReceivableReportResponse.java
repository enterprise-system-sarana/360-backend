package com.saranaresturantsystem.reports.dto.customers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountReceivableReportResponse {
    private Long id;
    private LocalDate date;
    private String customerName;
    private String phone;
    private String addressOrNote;
    private BigDecimal amount;
    private Long storeId;
    private String storeName;
}