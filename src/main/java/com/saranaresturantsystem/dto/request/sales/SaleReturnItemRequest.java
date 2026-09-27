package com.saranaresturantsystem.dto.request.sales;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record SaleReturnItemRequest(
        @NotNull Long saleItemId,
        @NotNull @Positive BigDecimal quantity,
        List<Long> serialNumberIds
) {
}
