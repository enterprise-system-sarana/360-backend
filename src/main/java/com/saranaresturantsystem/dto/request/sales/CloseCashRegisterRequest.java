package com.saranaresturantsystem.dto.request.sales;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CloseCashRegisterRequest(
        @NotNull @DecimalMin(value = "0.0") BigDecimal closingBalance,
        String closingNote
) {
}
