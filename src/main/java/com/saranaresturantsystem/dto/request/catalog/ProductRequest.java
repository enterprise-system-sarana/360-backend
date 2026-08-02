package com.saranaresturantsystem.dto.request.catalog;

import jakarta.validation.constraints.NotNull;

public record ProductRequest(
        String code ,
        String noted,
        String imageUrl,
        String status ,
        @NotNull(message = "modelId is required")
        Long modelId
) {
}
