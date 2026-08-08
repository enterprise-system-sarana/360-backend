package com.saranaresturantsystem.dto.request.sales;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SaleItemRequest(
         @NotNull Long productId,
         @NotNull @Positive BigDecimal quantity,
         @NotNull @DecimalMin(value = "0.0") Double price,
         @DecimalMin(value = "0.0") Double itemDiscount,
         List<Long> serialNumberIds
) {
}
