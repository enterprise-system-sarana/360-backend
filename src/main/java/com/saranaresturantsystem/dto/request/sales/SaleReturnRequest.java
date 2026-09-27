package com.saranaresturantsystem.dto.request.sales;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SaleReturnRequest(
        @NotEmpty List<@Valid SaleReturnItemRequest> items
) {
}
