package com.saranaresturantsystem.dto.request.customer;

public record CustomerRequest(
        Long id,
        String code,
        String name,
        String phone,
        String email,
        String note,
        String status
) {
}
