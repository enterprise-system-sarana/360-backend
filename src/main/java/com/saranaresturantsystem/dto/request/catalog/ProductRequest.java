package com.saranaresturantsystem.dto.request.catalog;

import jakarta.validation.constraints.NotNull;

public record ProductRequest(
        @NotNull(message = "Name is required")
        String name,
        @NotNull(message = "categoryId is required")
        Long categoryId,
        @NotNull(message = "brandId is required")
        Long brandId,
        String status
) {
}
