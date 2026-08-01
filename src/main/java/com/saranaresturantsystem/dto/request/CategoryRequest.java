package com.saranaresturantsystem.dto.request;

import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
        @NotNull(message = "Name is required")
        String name ,
        @NotNull(message = "Code is required")
        String code,
        String imageUrl,
        String status
) {
}
