package com.saranaresturantsystem.dto.request.purchases;

import jakarta.validation.constraints.NotNull;

public record SupplierRequest(
        @NotNull(message = "code is required")
        String code ,
        @NotNull(message = "name is required")
        String name,
        String phone ,
        String email ,String address ,
        String city ,
        String country ,
        String note ,
        String status
) {
}
