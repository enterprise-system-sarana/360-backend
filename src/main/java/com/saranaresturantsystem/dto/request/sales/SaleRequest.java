package com.saranaresturantsystem.dto.request.sales;


import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record SaleRequest(
        String reference,
        @NotNull @Positive Long storeId,
        @Positive Long customerId,
        @DecimalMin(value = "0.0") Double discount,
        Double paidAmount,
        String noted,
        @NotEmpty List<@Valid SaleItemRequest> items
) {
}
