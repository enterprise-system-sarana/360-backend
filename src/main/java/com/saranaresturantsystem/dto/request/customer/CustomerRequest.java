package com.saranaresturantsystem.dto.request.customer;

import jakarta.validation.constraints.NotNull;

public record CustomerRequest(
        @NotNull(message = "Code is required")
        String code,
        @NotNull(message = "Name is required")
        String name,
        String phone,
        String email,
        String note,
        String status
) {
}
