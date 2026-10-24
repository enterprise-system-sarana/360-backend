package com.saranaresturantsystem.dto.request.sales.payment;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLineRequestDTO {

    @NotBlank(message = "Payment method is required (e.g. CASH, ABA_PAYWAY, BANK_TRANSFER)")
    private String paymentMethod;

    @NotNull(message = "Line amount is required")
    @DecimalMin(value = "0.00", message = "Line amount must be greater than zero")
    private BigDecimal amount;

    private String transactionNo;
    private Long bankId;
    private Long currencyId;
}