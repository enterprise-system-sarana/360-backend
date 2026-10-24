package com.saranaresturantsystem.dto.request.sales.payment;


import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class PaymentRequestDTO {

    private String idempotencyKey;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.00", message = "Total amount must be greater than zero")
    private BigDecimal totalAmount;

    private Long saleId;
    private Long purchaseId;
    private Long userId;
    private String note;

    @NotEmpty(message = "At least one payment line is required")
    @Valid
    private List<PaymentLineRequestDTO> lines;
}