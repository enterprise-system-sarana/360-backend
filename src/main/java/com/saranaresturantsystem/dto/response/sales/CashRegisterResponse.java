package com.saranaresturantsystem.dto.response.sales;

import com.saranaresturantsystem.dto.response.common.BaseEntityResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CashRegisterResponse extends BaseEntityResponse {
    private Long id;
    private String registerNo;
    private Long storeId;
    private String storeName;
    private Long userId;
    private String userName;
    private String userProfileImage;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private BigDecimal expectedBalance;
    private BigDecimal totalSales;
    private BigDecimal totalCashSales;
    private BigDecimal totalBankSales;
    private BigDecimal totalReturns;
    private BigDecimal totalCashIn;
    private BigDecimal totalCashOut;
    private BigDecimal difference;
    private Integer saleCount;
    private String status;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private String closingNote;
}
