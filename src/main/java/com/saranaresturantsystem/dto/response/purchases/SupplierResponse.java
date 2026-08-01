package com.saranaresturantsystem.dto.response.purchases;

public record SupplierResponse(
        Long id,
        String code,
        String name,
        String phone,
        String email,
        String address,
        String city,
        String country,
        String note,
        String status
) {
}
