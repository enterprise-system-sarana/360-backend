package com.saranaresturantsystem.dto.request.catalog;

import jakarta.validation.constraints.NotNull;

public record VariantTypeRequest(

        String code,
        @NotNull(message = "Name is required")
        String name,
        String status
) {
}
