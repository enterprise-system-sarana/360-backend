package com.saranaresturantsystem.dto.request.inventory;

import jakarta.validation.constraints.NotNull;

public record StoreRequest(
        @NotNull(message = "name is required")
        String name,
        @NotNull(message = "code is required")
        String code,
        String logo,
        String email,
        String phone,
        String city,
        String state,
        String country,
        String receiptHeader,
        String receiptFooter,
        String status
) {
}
