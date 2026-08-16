package com.saranaresturantsystem.dto.request.catalog;

import jakarta.validation.constraints.NotNull;

public record BrandRequest(
        @NotNull(message = "Name is required")
        String name,
        String imageUrl,
        String status
) {

}



