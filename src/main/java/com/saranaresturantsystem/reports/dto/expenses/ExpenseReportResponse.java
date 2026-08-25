package com.saranaresturantsystem.reports.dto.expenses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseReportResponse {
    private Long id;
    private String reference;
    private BigDecimal amount;
    private String note;
    private Long storeId;
    private  String storeName;
    private String description;
    private String status;
    private Long bankId;
    private String bankName;
    private Long expenseTypeId;
    private String expenseTypeName;
    private LocalDateTime createdAt;
}