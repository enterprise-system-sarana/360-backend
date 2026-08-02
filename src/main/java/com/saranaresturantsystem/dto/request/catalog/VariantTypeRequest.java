package com.saranaresturantsystem.dto.request.catalog;

import com.saranaresturantsystem.enums.StatusType;
import jakarta.validation.constraints.NotNull;

public record VariantTypeRequest(

        String code,
        @NotNull(message = "Name is required")
        String name,
        StatusType status
) {
}
