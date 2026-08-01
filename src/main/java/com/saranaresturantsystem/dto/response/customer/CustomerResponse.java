package com.saranaresturantsystem.dto.response.customer;

public record CustomerResponse(
        Long id,
        String code,
        String name,
        String phone,
        String email,
        String note,
        String status

) {
}
