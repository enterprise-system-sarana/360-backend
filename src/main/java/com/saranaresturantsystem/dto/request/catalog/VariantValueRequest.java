package com.saranaresturantsystem.dto.request.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VariantValueRequest(
        @NotBlank(message = "Code is required")
        @Size(max = 50, message = "Code must not exceed 50 characters")
        String code,
        @NotBlank(message = "Name is required")
        @Size(max = 50, message = "Name must not exceed 50 characters")
        String name,
        @NotNull(message = "Variant type ID is required")
        Long variantTypeId,
        String status
) {
}