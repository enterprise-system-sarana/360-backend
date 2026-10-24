package com.saranaresturantsystem.dto.response.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalePaymentSummaryResponse {
    private Long saleId;
    private String saleNumber;
    private BigDecimal grandTotal;
    private BigDecimal previouslyPaidAmount;
    private BigDecimal currentPaymentTotal;
    private BigDecimal totalPaid;
    private BigDecimal remainingBalance;
    private String paymentStatus;
    private String saleStatus;
    private List<PaymentResponse> payments;
}
