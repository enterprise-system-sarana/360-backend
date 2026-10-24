package com.saranaresturantsystem.dto.response.payment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLineResponseDTO {
    private Long id;
    private String paymentMethod;
    private BigDecimal amount;
    private String transactionNo;
    private Long bankId;
    private String bankName;
    private Long currencyId;
    private String currencyCode;
}