package com.saranaresturantsystem.dto.response.inventory;

public record StoreResponse(
        Long id,
        String name,
        String code,
        String logo,
        String email,
        String phone,
        String address1,
        String address2,
        String city,
        String state,
        String postalCode,
        String country,
        String currencyCode,
        String receiptHeader,
        String receiptFooter,
        String status
) {
}
