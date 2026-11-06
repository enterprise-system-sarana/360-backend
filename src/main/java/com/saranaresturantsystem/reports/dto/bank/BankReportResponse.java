package com.saranaresturantsystem.reports.dto.bank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankReportResponse {
    private Long transactionId;
    private Long bankId;
    private String bankName;
    private String accountName;
    private String accountNumber;
    private BigDecimal bankCurrentBalance;
    private BigDecimal amount;
    private String transactionType;
    private String transactionReference;
    private String transactionStatus;
    private LocalDateTime transactionDate;
    private String description;
    private Long saleId;
    private String saleNo;
    private String saleReference;
    private Long purchaseId;
    private String purchaseReferenceNo;
    private Long expenseId;
    private String expenseReference;
    private String flowType;
    private LocalDateTime createdAt;
}

