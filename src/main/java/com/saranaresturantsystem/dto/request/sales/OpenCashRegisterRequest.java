package com.saranaresturantsystem.dto.request.sales;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OpenCashRegisterRequest(
        @NotNull @Positive Long storeId,
        @NotNull @DecimalMin(value = "0.0") BigDecimal openingBalance
) {
}
