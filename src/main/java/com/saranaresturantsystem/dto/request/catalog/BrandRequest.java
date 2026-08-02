package com.saranaresturantsystem.dto.request.catalog;

import com.saranaresturantsystem.enums.StatusType;
import jakarta.validation.constraints.NotNull;

public record BrandRequest(
        @NotNull(message = "Name is required")
        String name,
        String imageUrl,
        StatusType status
) {

}



