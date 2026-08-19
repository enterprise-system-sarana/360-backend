package com.saranaresturantsystem.dto.request.sales;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRequest(
        String paymentNo,
        @NotNull(message = "Sale ID is required")
        Long saleId,
        String paymentMethod,
        Long bankId,
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,
        String transactionNo,
        String status,
        LocalDateTime paymentDate,
        Long userId
) {
}


