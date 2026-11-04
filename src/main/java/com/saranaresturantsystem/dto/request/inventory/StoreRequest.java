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
        String address1,
        String address2,
        String city,
        String state,
        String country,
        String postalCode,
        String currencyCode,
        String receiptHeader,
        String receiptFooter,
        String status
) {
}
